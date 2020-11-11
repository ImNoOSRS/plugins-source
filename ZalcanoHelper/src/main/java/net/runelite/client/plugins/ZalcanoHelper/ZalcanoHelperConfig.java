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


	@ConfigTitleSection(
			keyName = "actioncountdown",
			position = 0,
			name = "Zalcano Action Countdown",
			description = ""
	)
	default Title overlay()
	{
		return new Title();
	}

	@Range(
			min = 1,
			max = 100
	)
	@ConfigItem(
			position = 0,
			keyName = "ZalcanoTickFontSize",
			name = "Font size",
			description = "Adjust the font size of the wizard statue tick counter.",
			titleSection = "actioncountdown"
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
			titleSection = "actioncountdown"

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
			titleSection = "actioncountdown"
	)
	default boolean TickFontShadow()
	{
		return false;
	}

	@ConfigTitleSection(
			keyName = "servertile",
			position = 1,
			name = "Server Tile",
			description = ""
	)
	default Title servertiletitle()
	{
		return new Title();
	}

	@ConfigItem(
			keyName = "ZalcanoServerTile",
			position = 1,
			name = "Show Server Tile",
			description = "",
			titleSection = "servertile"
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
			titleSection = "servertile"
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
			titleSection = "servertile"
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
			titleSection = "servertile"
	)
	@Units(Units.POINTS)
	default int serverTileOutlineWidth()
	{
		return 1;
	}

	@ConfigTitleSection(
			keyName = "circletiles",
			position = 2,
			name = "Boost Circles",
			description = ""
	)
	default Title circile()
	{
		return new Title();
	}

	@ConfigItem(
			keyName = "ZalcanoBlueTiles",
			position = 1,
			name = "Show Blue Circles",
			description = "",
			titleSection = "circletiles"
	)
	default boolean showbluecircles()
	{
		return true;
	}

	@ConfigItem(
			keyName = "ZalcanoForceBlueTiles",
			position = 2,
			name = "Show when not needed",
			description = "",
			titleSection = "circletiles"
	)
	default boolean alwaysshowbluetiles()
	{
		return false;
	}

	@ConfigItem(
			position = 3,
			keyName = "BlueOverlayTickCounter",
			name = "Ticks till despawn",
			description = "Shows the ticks till blue tiles despawn.",
			titleSection = "circletiles"
	)
	default boolean bluecircleticks()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
			position = 4,
			keyName = "BlueOverlayColor",
			name = "Overlay color",
			description = "Change the overlay fill color of the circle overlay.",
			titleSection = "circletiles"
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
			titleSection = "circletiles"
	)
	default Color activebluecirclecolor()
	{
		return Color.RED;
	}

}