//Created by PluginCreator by ImNo: https://github.com/ImNoOSRS 
package net.runelite.client.plugins.DaeyaltEssence;

import net.runelite.client.ui.overlay.OverlayManager;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.kit.KitType;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.util.Clipboard;
import net.runelite.client.util.QuantityFormatter;
import org.apache.commons.lang3.ObjectUtils;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

@Extension
@PluginDescriptor(
		name = "DaeyaltEssenceHelper",
		description = "Overlays where to mine Daeyalt.",
		type = PluginType.SYSTEM
)
@Slf4j
public class DaeyaltEssencePlugin extends Plugin {
	// Injects our config
	@Inject
	private ConfigManager configManager;
	@Inject
	private DaeyaltEssenceConfig config;
	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;
 	@Inject
	private OverlayManager overlayManager;
 	@Inject
	private DaeyaltEssenceOverlay overlay;
	@Provides
	DaeyaltEssenceConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(DaeyaltEssenceConfig.class);
	}

	public GameObject current_deayalt_essence;
	@Override
	protected void startUp() {
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() {
		overlayManager.remove(overlay);
		current_deayalt_essence = null;
	}

	@Subscribe
	private void onGameStateChanged(final GameStateChanged event) {
		final GameState gameState = event.getGameState();
		switch (gameState) {
			case LOGGED_IN:
				current_deayalt_essence = null;
				count = -1;
				firstoffset = 3;
				first = true;
				break;
		}
	}

	public boolean animating = false;
	public Integer count = -1;
	@Subscribe
	private void onGameTick(final GameTick event) {
		if(client.getLocalPlayer().getWorldLocation().getRegionID() == 14744)
		{
			animating = client.getLocalPlayer().getAnimation() != -1;
			if(count < 102 && count != -1) {
				count++;
			}
			if(first)
			{
				firstoffset--;
				if(firstoffset == 0)
				{
					first = false;
				}
			}
		}
	}

	public int firstoffset = 3;
	public boolean first = true;
	@Subscribe
	private void onGameObjectSpawned(GameObjectSpawned event)
	{
		if(event.getGameObject().getId() == 39095)
		{
			current_deayalt_essence = event.getGameObject();
			if(!first) {
				count = 0;
			}
		}
	}
}