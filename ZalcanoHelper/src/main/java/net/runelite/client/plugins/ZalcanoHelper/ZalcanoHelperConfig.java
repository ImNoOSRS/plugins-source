/*
 * Copyright (c) 2020 ImNoOSRS <https://github.com/ImNoOSRS>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *	list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *	this list of conditions and the following disclaimer in the documentation
 *	and/or other materials provided with the distribution.
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
package net.runelite.client.plugins.ZalcanoHelper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("ZalcanoHelper")

public interface ZalcanoHelperConfig extends Config {

	@Getter
	@AllArgsConstructor
	enum FontStyle
	{
		BOLD("Bold", Font.BOLD),
		ITALIC("Italic", Font.ITALIC),
		PLAIN("Plain", Font.PLAIN);

		private final String name;
		private final int font;

		@Override
		public String toString()
		{
			return name;
		}
	}


	@ConfigSection(
			position = 0,
			name = "Zalcano Action Countdown",
			description = ""
	)
	String actioncountdown = "actioncountdown";

	@Range(
			min = 1,
			max = 100
	)
	@ConfigItem(
			position = 0,
			keyName = "ZalcanoTickFontSize",
			name = "Font size",
			description = "Adjust the font size of the wizard statue tick counter.",
			section = actioncountdown
	)
	@Units(Units.POINTS)
	default int TickFontSize()
	{
		return 50;
	}

	@ConfigItem(
			position = 1,
			keyName = "ZalcanoTickFontStyle",
			name = "Font style",
			description = "Bold/Italics/Plain",
			section = actioncountdown

	)
	default FontStyle TickFontStyle()
	{
		return FontStyle.PLAIN;
	}

	@ConfigItem(
			position = 2,
			keyName = "ZalcanoTickFontShadow",
			name = "Font shadow",
			description = "Toggle font shadow of the wizard statue tick counter.",
			section = actioncountdown
	)
	default boolean TickFontShadow()
	{
		return false;
	}

	@ConfigSection(
			position = 1,
			name = "Server Tile",
			description = ""
	)
	String servertile = "servertile";

	@ConfigItem(
			keyName = "ZalcanoServerTile",
			position = 1,
			name = "Show Server Tile",
			description = "",
			section = servertile
	)
	default boolean servertile()
	{
		return true;
	}


	@Alpha
	@ConfigItem(
			position = 2,
			keyName = "serverTileOutlineColor",
			name = "Server tile outline",
			description = "Change the overlay outline color of the player's server tile.",
			section = servertile
	)
	default Color serverTileOutlineColor()
	{
		return Color.CYAN;
	}

	@Alpha
	@ConfigItem(
			position = 3,
			keyName = "serverTileFillColor",
			name = "Server tile  fill",
			description = "Change the overlay fill color of the player's server tile.",
			section = servertile
	)
	default Color serverTileFillColor()
	{
		return new Color(0, 0, 0, 0);
	}

	@Range(
			min = 1,
			max = 4
	)
	@ConfigItem(
			name = "Outline width",
			description = "Change the width of the tile outline.",
			position = 4,
			keyName = "tileOutlineWidth",
			section = servertile
	)
	@Units(Units.POINTS)
	default int serverTileOutlineWidth()
	{
		return 1;
	}

	@ConfigSection(
			position = 2,
			name = "Boost Circles",
			description = ""
	)
	String circletiles = "circletiles";

	@ConfigItem(
			keyName = "ZalcanoBlueTiles",
			position = 1,
			name = "Show Blue Circles",
			description = "",
			section = circletiles
	)
	default boolean showbluecircles()
	{
		return true;
	}


	@ConfigItem(
			keyName = "ZalcanoRedTiles",
			position = 1,
			name = "Show Red Circles",
			description = "",
			section = circletiles
	)
	default boolean showredcircles()
	{
		return true;
	}

	@ConfigItem(
			keyName = "ZalcanoForceBlueTiles",
			position = 2,
			name = "Always show (blue)",
			description = "",
			section = circletiles
	)
	default boolean alwaysshowcircles()
	{
		return false;
	}

	@ConfigItem(
			position = 3,
			keyName = "BlueOverlayTickCounter",
			name = "Ticks till despawn",
			description = "Shows the ticks till blue tiles despawn.",
			section = circletiles
	)
	default boolean circleticks()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
			position = 4,
			keyName = "BlueOverlayColor",
			name = "Overlay color",
			description = "Change the overlay fill color of the circle overlay.",
			section = circletiles
	)
	default Color bluecirclecolor()
	{
		return Color.BLUE;
	}

	@Alpha
	@ConfigItem(
			position = 5,
			keyName = "BlueActiveOverlayColor",
			name = "Interacting Overlay",
			description = "Change the overlay fill color of the circle overlay.",
			section = circletiles
	)
	default Color activebluecirclecolor()
	{
		return Color.CYAN;
	}

	@ConfigSection(
			position = 3,
			name = "Other",
			description = ""
	)
	String other = "other";

	@ConfigItem(
			position = 1,
			keyName = "DontAttackWhenNoImbuedOres",
			name = "Walk trough",
			description = "Makes you able to walk trough zalcano when you have no imbued ores.",
			section = other
	)
	default boolean OnlyAttackWhenNoImbuedOres()
	{
		return true;
	}

	@ConfigItem(
			position = 1,
			keyName = "DontAttackWhenNoImbuedOres",
			name = "Prioritize Tele",
			description = "Makes the Zalcano entrance prioritized over the tree.",
			section = other
	)
	default boolean TeleportChannelPriority()
	{
		return true;
	}

}
