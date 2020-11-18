/*
 * Copyright (c) 2020, Cyborger1
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.MLMUpperLevelMarkers;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup("mlmupperlevelmarkers")
public interface MLMUpperLevelMarkersConfig extends Config
{
	@ConfigItem(
		keyName = "selfMarkerColor",
		name = "Self Marker Color",
		description = "Color of markers on veins you've mined",
		position = 1
	)
	default Color getSelfMarkerColor()
	{
		return Color.GREEN;
	}

	@ConfigItem(
		keyName = "otherMarkerColor",
		name = "Other Marker Color",
		description = "Color of markers on veins other players have mined",
		position = 2
	)
	default Color getOtherMarkerColor()
	{
		return Color.YELLOW;
	}

	@ConfigItem(
		keyName = "showOtherMarkers",
		name = "Show Other Players' Markers",
		description = "Add markers to veins other players have mined",
		position = 3
	)
	default boolean showOtherMarkers()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showOnlyWhenUpstairs",
		name = "Show Only When Upstairs",
		description = "Only show markers if you are upstairs",
		position = 4
	)
	default boolean showOnlyWhenUpstairs()
	{
		return true;
	}

	// 15 and 27 are values from the osrs wiki on approximate lifetime of upper level MLM veins
	@ConfigItem(
		keyName = "firstTimeout",
		name = "First Timeout",
		description = "Darkens the marker after a vein has been first mined for this long (-1 to disable)",
		position = 5
	)
	@Units(Units.SECONDS)
	@Range(min = -1)
	default int getFirstTimeout()
	{
		return 15;
	}

	@ConfigItem(
		keyName = "secondTimeout",
		name = "Second Timeout",
		description = "Darkens the marker again after a vein has been first mined for this long (-1 to disable)",
		position = 6
	)
	@Units(Units.SECONDS)
	@Range(min = -1)
	default int getSecondTimeout()
	{
		return 27;
	}
}
