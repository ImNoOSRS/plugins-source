//Created by PluginCreator by ImNo: https://github.com/ImNoOSRS 
package net.runelite.client.plugins.FastCam;

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
		name = "FastCam",
		description = "Makes your camera movement fast.",
		tags = {"Render", "GPU"},
		type = PluginType.SYSTEM
)
@Slf4j
public class FastCamPlugin extends Plugin {
	// Injects our config
	@Inject
	private ConfigManager configManager;
	@Inject
	private FastCamConfig config;
	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;

	@Provides
	FastCamConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(FastCamConfig.class);
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event) {
		if (event.getGroup().equals("FastCam"))
		{
			client.setOculusOrbNormalSpeed(config.camspeed());
		}
	}

	@Override
	protected void startUp() {
			client.setOculusOrbNormalSpeed(config.camspeed());
	}

	@Override
	protected void shutDown() {
			client.setOculusOrbNormalSpeed(16);
	}
}