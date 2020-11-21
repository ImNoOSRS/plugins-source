//Created by PluginCreator by ImNo: https://github.com/ImNoOSRS 
package net.runelite.client.plugins.NoAreaEffects;

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
		name = "No Area Effects",
		description = "Disable area effects such as tears of guthix dark layers.",
		type = PluginType.SYSTEM
)
@Slf4j
public class NoAreaEffectsPlugin extends Plugin {
	@Inject
	private ConfigManager configManager;
	@Inject
	private NoAreaEffectsConfig config;
	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;

	@Provides
	NoAreaEffectsConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(NoAreaEffectsConfig.class);
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event) {
		if (client.getGameState() != GameState.LOGGED_IN) {
			return;
		}
		if (event.getGroup().equals("NoAreaEffects"))
		{
			switch(event.getKey())
			{
				case "TearsOfGuthixBright":
					if(client.getLocalPlayer().getWorldLocation().getRegionID() == TearsOfGuthix_region)
					{
						hidewidget(client.getWidget(TearsOfGuthix_group, 0), config.TearsOfGuthix());
					}
					break;
				case "RaidsLobbyBright":
					if(client.getLocalPlayer().getWorldLocation().getRegionID() == Raids_Lobby_region)
					{
						hidewidget(Raids_Widget(), config.RaidsLobby());
					}
					break;
				default:
					break;
			}
		}
	}

	@Override
	protected void startUp() {
		if (client.getGameState() != GameState.LOGGED_IN) {
			return;
		}
		swap_states(true);
	}

	@Override
	protected void shutDown() {
		if (client.getGameState() != GameState.LOGGED_IN) {
			return;
		}
		swap_states(false);
	}

	public void swap_states(boolean state)
	{
		if(client.getLocalPlayer().getWorldLocation().getRegionID() == TearsOfGuthix_region)
		{
			if(config.TearsOfGuthix())
			{
				hidewidget(client.getWidget(TearsOfGuthix_group, 0), state);
			}
		}
		else if(client.getLocalPlayer().getWorldLocation().getRegionID() == Raids_Lobby_region)
		{
			if(config.RaidsLobby()) {
				hidewidget(Raids_Widget(), state);
			}
		}
	}

	public final int TearsOfGuthix_region = 12948;
	public final int TearsOfGuthix_group = 98;
	public final int Raids_Lobby_region = 14642;
	public final int Raids_Lobby_group = 28;
	public final int Raids_Lobby_child = 1;
	public final int Raids_Lobby_second_child = 1;

	public Widget Raids_Widget()
	{
		Widget w = client.getWidget(Raids_Lobby_group, Raids_Lobby_child);
		Widget[] widgets = w.getChildren();
		if(widgets != null)
		{
			return widgets[Raids_Lobby_second_child];
		}
		return null;
	}
	@Subscribe
	private void onWidgetLoaded(WidgetLoaded event)
	{
		int group = event.getGroupId();
		switch(group)
		{
			case TearsOfGuthix_group:
				if(config.TearsOfGuthix()) {
					if(client.getLocalPlayer().getWorldLocation().getRegionID() == TearsOfGuthix_region) {
						hidewidget(client.getWidget(TearsOfGuthix_group, 0), true);
					}
				}
				break;
			case Raids_Lobby_group:
				if(config.RaidsLobby()) {
					if(client.getLocalPlayer().getWorldLocation().getRegionID() == Raids_Lobby_region) {
						hidewidget(Raids_Widget(), true);
					}
				}
				break;
		}
	}

	public void hidewidget(Widget w, boolean hide)
	{
		w.setHidden(hide);
	}
}