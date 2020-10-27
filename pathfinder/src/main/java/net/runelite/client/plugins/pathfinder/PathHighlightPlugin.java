package net.runelite.client.plugins.pathfinder;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

@Slf4j
@Extension
@PluginDescriptor(
		name = "Pathfinder",
		description = "Highlight the path your character will take to the hovered tile",
		tags = {"highlight", "overlay", "path", "tile", "tiles", "gauntlet", "zalcano"},
		enabledByDefault = false
)
public class PathHighlightPlugin extends Plugin
{
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PathHighlightOverlay overlay;

	@Provides
	PathHighlightConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PathHighlightConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
	}
}
