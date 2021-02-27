/*
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.examineosb;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Provides;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Pattern;
import javax.inject.Inject;

import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemID;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import static net.runelite.api.widgets.WidgetInfo.SEED_VAULT_ITEM_CONTAINER;
import static net.runelite.api.widgets.WidgetInfo.TO_CHILD;
import static net.runelite.api.widgets.WidgetInfo.TO_GROUP;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.*;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.QuantityFormatter;
import net.runelite.http.api.examine.ExamineClient;
import net.runelite.http.api.osbuddy.OSBGrandExchangeClient;
import net.runelite.http.api.osbuddy.OSBGrandExchangeResult;
import okhttp3.OkHttpClient;
import org.pf4j.Extension;

/**
 * Submits examine info to the api
 *
 * @author Adam
 */
@Extension
@PluginDescriptor(
	name = "Examine with Osbuddy",
	description = "Send examine information to the API",
	tags = {"npcs", "items", "inventory", "objects"}
)
@Slf4j
public class ExamineOSBPlugin extends Plugin
{
	private static final Pattern X_PATTERN = Pattern.compile("^\\d+ x ");

	private final Deque<ExamineOSBPending> pending = new ArrayDeque<>();
	private final Cache<ExamineOSBCacheKey, Boolean> cache = CacheBuilder.newBuilder()
		.maximumSize(128L)
		.build();

	@Inject
	private ExamineClient examineClient;

	@Inject
	private Client client;

	@Inject
	private ItemManager itemManager;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private OSBGrandExchangeClient osbGrandExchangeClient;

	@Inject
	private ScheduledExecutorService executorService;

	@Inject
	private PluginManager pluginManager;

	@Provides
	ExamineClient provideExamineClient(OkHttpClient okHttpClient)
	{
		return new ExamineClient(okHttpClient);
	}

	@Provides
	OSBGrandExchangeClient provideOsbGrandExchangeClient(OkHttpClient okHttpClient)
	{
		return new OSBGrandExchangeClient(okHttpClient);
	}

	@Override
	protected void startUp() {
		// Stop the GPU plugin before starting up
		for (Plugin plugin : pluginManager.getPlugins()) {
			if (plugin.getName().equals("Examine") && pluginManager.isPluginEnabled(plugin)) {
				try {
					System.out.println("Stopping Examine plugin");
					pluginManager.stopPlugin(plugin);
				} catch (PluginInstantiationException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event)
	{
		pending.clear();
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		osbuddymsg();
	}

	@Subscribe
	void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (!event.getMenuOption().equals("Examine"))
		{
			return;
		}

		ExamineOSBType type;
		int id, quantity = -1;
		switch (event.getMenuAction())
		{
			case EXAMINE_ITEM:
			{
				type = ExamineOSBType.ITEM;
				id = event.getId();

				int widgetId = event.getWidgetId();
				int widgetGroup = TO_GROUP(widgetId);
				int widgetChild = TO_CHILD(widgetId);
				Widget widget = client.getWidget(widgetGroup, widgetChild);
				WidgetItem widgetItem = widget.getWidgetItem(event.getActionParam());
				quantity = widgetItem != null && widgetItem.getId() >= 0 ? widgetItem.getQuantity() : 1;
				break;
			}
			case EXAMINE_ITEM_GROUND:
				type = ExamineOSBType.ITEM;
				id = event.getId();
				break;
			case CC_OP_LOW_PRIORITY:
			{
				type = ExamineOSBType.ITEM_BANK_EQ;
				int[] qi = findItemFromWidget(event.getWidgetId(), event.getActionParam());
				if (qi == null)
				{
					log.debug("Examine for item with unknown widget: {}", event);
					return;
				}
				quantity = qi[0];
				id = qi[1];
				break;
			}
			case EXAMINE_OBJECT:
				type = ExamineOSBType.OBJECT;
				id = event.getId();
				break;
			case EXAMINE_NPC:
				type = ExamineOSBType.NPC;
				id = event.getId();
				break;
			default:
				return;
		}

		ExamineOSBPending pendingExamine = new ExamineOSBPending();
		pendingExamine.setType(type);
		pendingExamine.setId(id);
		pendingExamine.setQuantity(quantity);
		pendingExamine.setCreated(Instant.now());
		pending.push(pendingExamine);
	}

	@Subscribe
	void onChatMessage(ChatMessage event)
	{
		/*for(Plugin p : pluginManager.getPlugins())
		{

		}*/
		ExamineOSBType type;
		switch (event.getType())
		{
			case ITEM_EXAMINE:
				type = ExamineOSBType.ITEM;
				break;
			case OBJECT_EXAMINE:
				type = ExamineOSBType.OBJECT;
				break;
			case NPC_EXAMINE:
				type = ExamineOSBType.NPC;
				break;
			case GAMEMESSAGE:
				type = ExamineOSBType.ITEM_BANK_EQ;
				break;
			default:
				return;
		}

		if (pending.isEmpty())
		{
			log.debug("Got examine without a pending examine?");
			return;
		}

		ExamineOSBPending pendingExamine = pending.pop();

		if (pendingExamine.getType() != type)
		{
			log.debug("Type mismatch for pending examine: {} != {}", pendingExamine.getType(), type);
			pending.clear(); // eh
			return;
		}

		log.debug("Got examine for {} {}: {}", pendingExamine.getType(), pendingExamine.getId(), event.getMessage());

		// If it is an item, show the price of it
		final ItemComposition itemDefinition;
		if (pendingExamine.getType() == ExamineOSBType.ITEM || pendingExamine.getType() == ExamineOSBType.ITEM_BANK_EQ)
		{
			final int itemId = pendingExamine.getId();
			final int itemQuantity = pendingExamine.getQuantity();

			if (itemId == ItemID.COINS_995)
			{
				return;
			}

			itemDefinition = itemManager.getItemComposition(itemId);
			getItemPrice(itemDefinition.getId(), itemDefinition, itemQuantity);
		}
		else
		{
			itemDefinition = null;
		}

		// Don't submit examine info for tradeable items, which we already have from the RS item api
		if (itemDefinition != null && itemDefinition.isTradeable())
		{
			return;
		}

		// Large quantities of items show eg. 100000 x Coins
		if (type == ExamineOSBType.ITEM && X_PATTERN.matcher(event.getMessage()).lookingAt())
		{
			return;
		}

		ExamineOSBCacheKey key = new ExamineOSBCacheKey(type, pendingExamine.getId());
		Boolean cached = cache.getIfPresent(key);
		if (cached != null)
		{
			return;
		}

		cache.put(key, Boolean.TRUE);
		submitExamine(pendingExamine, event.getMessage());
	}

	private int[] findItemFromWidget(int widgetId, int actionParam)
	{
		int widgetGroup = TO_GROUP(widgetId);
		int widgetChild = TO_CHILD(widgetId);
		Widget widget = client.getWidget(widgetGroup, widgetChild);

		if (widget == null)
		{
			return null;
		}

		if (WidgetInfo.EQUIPMENT.getGroupId() == widgetGroup)
		{
			Widget widgetItem = widget.getChild(1);
			if (widgetItem != null)
			{
				return new int[]{widgetItem.getItemQuantity(), widgetItem.getItemId()};
			}
		}
		else if (WidgetInfo.SMITHING_INVENTORY_ITEMS_CONTAINER.getGroupId() == widgetGroup)
		{
			Widget widgetItem = widget.getChild(2);
			if (widgetItem != null)
			{
				return new int[]{widgetItem.getItemQuantity(), widgetItem.getItemId()};
			}
		}
		else if (WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getGroupId() == widgetGroup
			|| WidgetInfo.RUNE_POUCH_ITEM_CONTAINER.getGroupId() == widgetGroup)
		{
			Widget widgetItem = widget.getChild(actionParam);
			if (widgetItem != null)
			{
				return new int[]{widgetItem.getItemQuantity(), widgetItem.getItemId()};
			}
		}
		else if (WidgetInfo.BANK_ITEM_CONTAINER.getGroupId() == widgetGroup
			|| WidgetInfo.CLUE_SCROLL_REWARD_ITEM_CONTAINER.getGroupId() == widgetGroup
			|| WidgetInfo.LOOTING_BAG_CONTAINER.getGroupId() == widgetGroup
			|| WidgetID.SEED_VAULT_INVENTORY_GROUP_ID == widgetGroup
			|| WidgetID.SEED_BOX_GROUP_ID == widgetGroup
			|| WidgetID.PLAYER_TRADE_SCREEN_GROUP_ID == widgetGroup
			|| WidgetID.PLAYER_TRADE_INVENTORY_GROUP_ID == widgetGroup)
		{
			Widget widgetItem = widget.getChild(actionParam);
			if (widgetItem != null)
			{
				return new int[]{widgetItem.getItemQuantity(), widgetItem.getItemId()};
			}
		}
		else if (WidgetInfo.SHOP_ITEMS_CONTAINER.getGroupId() == widgetGroup)
		{
			Widget widgetItem = widget.getChild(actionParam);
			if (widgetItem != null)
			{
				return new int[]{1, widgetItem.getItemId()};
			}
		}
		else if (WidgetID.SEED_VAULT_GROUP_ID == widgetGroup)
		{
			Widget widgetItem = client.getWidget(SEED_VAULT_ITEM_CONTAINER).getChild(actionParam);
			if (widgetItem != null)
			{
				return new int[]{widgetItem.getItemQuantity(), widgetItem.getItemId()};
			}
		}

		return null;
	}

	private OSBGrandExchangeResult osbGrandExchangeResult;

	@Inject
	private ClientThread clientThread;

	String pooledmsg = "";
	public void gamemessage(String msg) {
		pooledmsg = msg;
	}

	public void osbuddymsg()
	{
		if(pooledmsg != "") {
			client.addChatMessage(
					ChatMessageType.GAMEMESSAGE,
					"",
					ColorUtil.prependColorTag("[OSBUDDY] ", Color.RED) + ColorUtil.prependColorTag("", Color.WHITE) + pooledmsg,
					null
			);
			pooledmsg = "";
		}
	}

	public static boolean methodExists(Class clazz, String methodName) {
		boolean result = false;
		for (Method method : clazz.getDeclaredMethods()) {
			if (method.getName().equals(methodName)) {
				result = true;
				break;
			}
		}
		return result;
	}

	@VisibleForTesting
	void getItemPrice(int id, ItemComposition itemComposition, int quantity)
	{

		//REMOVED FOR NOW BROKEN!!!!
	}

	private void submitExamine(ExamineOSBPending examine, String text)
	{
		int id = examine.getId();

		switch (examine.getType())
		{
			case ITEM:
				examineClient.submitItem(id, text);
				break;
			case OBJECT:
				examineClient.submitObject(id, text);
				break;
			case NPC:
				examineClient.submitNpc(id, text);
				break;
		}
	}

}