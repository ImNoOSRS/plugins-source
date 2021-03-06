//Created by PluginCreator by ImNo: https://github.com/ImNoOSRS 
package net.runelite.client.plugins.ConfigSearch;

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
		name = "Config Search",
		description = "Allows you to search in config sections of plugins.",
)
@Slf4j
public class ConfigSearchPlugin extends Plugin {
	// Injects our config
	@Inject
	private ConfigManager configManager;
	@Inject
	private ConfigSearchConfig config;
	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;

	@Provides
	ConfigSearchConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(ConfigSearchConfig.class);
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event) {
		if (event.getGroup().equals("ConfigSearch"))
		{
			switch(event.getKey())
			{
				case "example":
					break;
				default:
					break;
			}
		}
	}

	@Override
	protected void startUp() {

	}

	@Override
	protected void shutDown() {

	}

	@Subscribe
	private void onBeforeRender(final BeforeRender event) {
		if (this.client.getGameState() != GameState.LOGGED_IN) {
			return;
		}
	}

	@Subscribe
	private void onWidgetLoaded(WidgetLoaded event)
	{

	}

	@Subscribe
	public void onGameTick(GameTick event) {

	}
}