package net.runelite.client.plugins.pathfinder;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("pathfinder")
public interface PathHighlightConfig extends Config
{
	@Alpha
	@ConfigItem(
			keyName = "highlightPathColor",
			name = "Color of current path highlighting",
			description = "Configures the color of the path"
	)
	default Color highlightPathColor()
	{
		return new Color(0, 127, 0, 127);
	}
}
