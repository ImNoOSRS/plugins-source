//Created by PluginCreator by ImNo: https://github.com/ImNoOSRS 
package net.runelite.client.plugins.PlayerLogger;

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
		name = "Player Logger",
		description = "Logs players for you.",
		type = PluginType.SYSTEM
)
@Slf4j
public class PlayerLoggerPlugin extends Plugin {
	// Injects our config
	@Inject
	private ConfigManager configManager;
	@Inject
	private PlayerLoggerConfig config;
	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;
 	@Inject
	private OverlayManager overlayManager;
 	@Inject
	private PlayerLoggerOverlay overlay;
 	@Inject
	private PlayerLoggerOverlayPanel overlaypanel;
	@Provides
	PlayerLoggerConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(PlayerLoggerConfig.class);
	}

	@Getter(AccessLevel.PACKAGE)
	public Map<Player, PlayerData> players = new HashMap();

	@Override
	protected void startUp() {
		overlayManager.add(overlay);
		overlayManager.add(overlaypanel);
		if (client.getGameState() != GameState.LOGGED_IN) {
			return;
		}
		load();
	}

	public void load()
	{
		for(Map.Entry<Player, PlayerData> entry : players.entrySet())
		{
			PlayerData pd = entry.getValue();
			pd.isactive = false;
			players.put(entry.getKey(), pd);
		}
		if (client.getGameState() == GameState.LOGGED_IN) {
			for(Player player : client.getPlayers())
			{
				if(players.containsKey(player))
				{
					PlayerData pd = players.get(player);
					pd.isactive = true;
					players.put(player, pd);
				}
				else {
					PlayerData pd = new PlayerData();
					pd.isactive = true;
					pd.chats = 0;
					players.put(player, pd);
				}
			}
		}
	}

	@Override
	protected void shutDown() {
		overlayManager.remove(overlay);
		overlayManager.remove(overlaypanel);
		players.clear();
	}

	@Subscribe
	private void onGameStateChanged(final GameStateChanged event)
	{
		final GameState gameState = event.getGameState();;
		if(gameState == GameState.LOGGED_IN)
		{
			load();
		}
	}

	@Subscribe
	private void onPlayerSpawned(PlayerSpawned event)
	{
		Player player = event.getPlayer();

		handleplayer(player, true);
	}

	public void handleplayer(Player player, boolean active)
	{
		if(players.containsKey(player))
		{
			PlayerData pd = players.get(player);
			pd.isactive = active;
			players.put(player, pd);
		}
		else {
			PlayerData pd = new PlayerData();
			pd.isactive = active;
			pd.chats = 0;
			players.put(player, pd);
		}
	}

	public void handlechat(Player player)
	{
		PlayerData pd = players.get(player);
		pd.chats++;
		players.put(player, pd);
	}

	@Subscribe
	private void onPlayerDespawned(PlayerDespawned event)
	{
		Player player = event.getPlayer();
		handleplayer(player, false);
	}

	@Subscribe
	private void onChatMessage(final ChatMessage message)
	{
		for(Map.Entry<Player, PlayerData> entry : players.entrySet())
		{
			if(entry.getKey().getName() == message.getName())
			{
				handlechat(entry.getKey());
				return;
			}
		}
	}

	@Subscribe
	public void onConfigButtonClicked(ConfigButtonClicked event) {
		if (!event.getGroup().equals("PlayerLogger"))
			return;
		if (event.getKey().equals("ResetDataPlayerLogger")) {
			log.info("Resetting player data.");
			players.clear();
			if (client.getGameState() != GameState.LOGGED_IN) {
				return;
			}
			else {
				load();
			}
		}
	}
}