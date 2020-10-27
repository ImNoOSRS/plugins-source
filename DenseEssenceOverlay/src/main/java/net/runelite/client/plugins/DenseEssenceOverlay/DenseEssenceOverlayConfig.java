/*
 * Copyright (c) 2018, Andrew EP | ElPinche256 <https://github.com/ElPinche256>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
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
package net.runelite.client.plugins.DenseEssenceOverlay;

import net.runelite.client.config.*;

import java.awt.*;


@ConfigGroup("DenseEssenceOverlayConfig")

//Sections
public interface DenseEssenceOverlayConfig extends Config {
    @ConfigSection(
            name = "Toggle Overlay",
            description = "",
            position = 0,
            keyName = "toggleSection"
    )
    default boolean toggleSection()
    {
        return false;
    }
    @ConfigSection(
            name = "Icon Indicators",
            description = "",
            position = 1,
            keyName = "indicatorSection"
    )
    default boolean indicatorSection()
    {
        return false;
    }
    @ConfigSection(
            name = "Essence Colors",
            description = "",
            position = 2,
            keyName = "essenceColors"
    )
    default boolean essenceColors()
    {
        return false;
    }

    //Overlay Toggles
    @ConfigItem(
            name = "Available Overlay",
            description = "Toggles Dense Overlay",
            position = 0,
            keyName = "denseToggle",
            section = "toggleSection"
    )
    default boolean denseToggle()
    {
        return true;
    }
    @ConfigItem(
            name = "Unavailable Overlay",
            description = "Toggles Depleted Overlay",
            position = 1,
            keyName = "depletedToggle",
            section = "toggleSection"
    )
    default boolean depletedToggle()
    {
        return true;
    }
    @ConfigItem(
            name = "Full Overlay",
            description = "Toggles Inventory is full Overlay",
            position = 2,
            keyName = "fullToggle",
            section = "toggleSection"
    )
    default boolean fullToggle()
    {
        return true;
    }
    @ConfigItem(
            name = "Decimals",
            description = "Toggles Decimals in the lap tracker",
            position = 3,
            keyName = "Decimals",
            section = "toggleSection"
    )
    default int Decimals()
    {
        return 0;
    }
    @ConfigItem(
            name = "Laps to Goal",
            description = "Toggles the lap tracker",
            position = 4,
            keyName = "lapstogoal",
            section = "toggleSection"
    )
    default boolean lapstogoal()
    {
        return true;
    }

    //Indicators
    @ConfigItem(
            keyName = "showDenseRunestoneIndicator",
            name = "Show dense runestone indicator",
            description = "Configures whether to display an indicator when dense runestone is ready to be mined",
            position = 0,
            section = "indicatorSection"
    )
    default boolean showDenseRunestoneIndicator()
    {
        return true;
    }
    @ConfigItem(
            keyName = "denseIndicator",
            name = "Available indicator Toggle",
            description = "Toggles dense indicator",
            position = 1,
            section = "indicatorSection",
            hidden = true,
            unhide = "showDenseRunestoneIndicator"
    )
    default boolean denseIndicator()
    {
        return true;
    }
    @ConfigItem(
            keyName = "depletedIndicator",
            name = "Unavailable indicator Toggle",
            description = "Toggles dense indicator",
            position = 2,
            section = "indicatorSection",
            hidden = true,
            unhide = "showDenseRunestoneIndicator"
    )
    default boolean depletedIndicator()
    {
        return true;
    }
    @ConfigItem(
            keyName = "fullIndicator",
            name = "Full indicator Toggle",
            description = "Toggles dense indicator",
            position = 3,
            section = "indicatorSection",
            hidden = true,
            unhide = "showDenseRunestoneIndicator"

    )
    default boolean fullIndicator()
    {
        return true;
    }


    //Essence Colors
    @Alpha
    @ConfigItem(
            name = "Available",
            description = "",
            position = 0,
            keyName = "dense",
            section = "essenceColors",
            hidden = true,
            unhide = "denseToggle"
    )
    default Color dense()
    {
        return Color.GREEN;
    }
    @Alpha
    @ConfigItem(
            name = "Unavailable",
            description = "",
            position = 1,
            keyName = "depleted",
            section = "essenceColors",
            hidden = true,
            unhide = "depletedToggle"
    )
    default Color depleted()
    {
        return Color.RED;
    }
    @Alpha
    @ConfigItem(
            name = "Inventory Full",
            description = "",
            position = 2,
            keyName = "full",
            section = "essenceColors",
            hidden = true,
            unhide = "fullToggle"
    )
    default Color full()
    {
        return Color.MAGENTA;
    }
}