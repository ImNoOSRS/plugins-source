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
package net.runelite.client.plugins.wintertodthelper;

import net.runelite.client.config.*;

import java.awt.*;


@ConfigGroup("wintertodthelper")

    //------------------------------------------------------------//
    //Sections
    //------------------------------------------------------------//

public interface wintertodthelperConfig extends Config {

    @ConfigSection(
            name = "Brumaroot Color",
            description = "",
            position = 1,
            keyName = "brumarootColors"
    )
    default boolean borderSection()
    {
        return false;
    }

    @ConfigSection(
            name = "Brazier Colors",
            description = "",
            position = 2,
            keyName = "brazierColors"
    )
    default boolean brazierColors()
    {
        return false;
    }

    @ConfigSection(
            name = "Enough Points Alerter",
            description = "",
            position = 3,
            keyName = "piiSection"
    )
    default boolean piiSection()
    {
        return false;
    }

    @ConfigSection(
            name = "Miscellaneous",
            description = "",
            position = 4,
            keyName = "miscSection"
    )
    default boolean misc()
    {
        return false;
    }

    //------------------------------------------------------------//
    //Brumaroot Colors
    //------------------------------------------------------------//

    @ConfigItem(
            name = "Brumaroot Overlay",
            description = "Toggles Brumaroot overlay",
            position = 0,
            keyName = "brumarootToggle",
            section = "brumarootColors"
    )
    default boolean brumarootToggle()
    {
        return true;
    }


    @Alpha
    @ConfigItem(
            name = "Not Chopping",
            description = "",
            position = 1,
            keyName = "notChopping",
            section = "brumarootColors",
            hidden = true,
            unhide = "brumarootToggle"
    )
    default Color notChopping()
    {
        return Color.RED;
    }

    @Alpha
    @ConfigItem(
            name = "Chopping",
            description = "",
            position = 2,
            keyName = "chopping",
            section = "brumarootColors",
            hidden = true,
            unhide = "brumarootToggle"
    )
    default Color chopping()
    {
        return Color.GREEN;
    }

    //------------------------------------------------------------//
    //Brazier Colors
    //------------------------------------------------------------//

    @ConfigItem(
            name = "Brazier Overlay",
            description = "Toggles Brazier overlay",
            position = 0,
            keyName = "brazierToggle",
            section = "brazierColors"
    )
    default boolean brazierToggle()
    {
        return true;
    }

    @Alpha
    @ConfigItem(
            name = "Not Burning",
            description = "",
            position = 1,
            keyName = "notBurning",
            section = "brazierColors",
            hidden = true,
            unhide = "brazierToggle"
    )
    default Color notBurning()
    {
        return Color.RED;
    }

    @Alpha
    @ConfigItem(
            name = "Burning",
            description = "",
            position = 2,
            keyName = "burning",
            section = "brazierColors",
            hidden = true,
            unhide = "brazierToggle"
    )
    default Color burning()
    {
        return Color.GREEN;
    }

    @Alpha
    @ConfigItem(
            name = "Unlit",
            description = "",
            position = 3,
            keyName = "unlit",
            section = "brazierColors",
            hidden = true,
            unhide = "brazierToggle"
    )
    default Color unlit()
    {
        return Color.CYAN;
    }

    @Alpha
    @ConfigItem(
            name = "Broken",
            description = "",
            position = 4,
            keyName = "broken",
            section = "brazierColors",
            hidden = true,
            unhide = "brazierToggle"
    )
    default Color broken()
    {
        return Color.ORANGE;
    }

    //------------------------------------------------------------//
    //Points in Inventory Overlay
    //------------------------------------------------------------//

    @ConfigItem(
            name = "PointInInv Toggle",
            description = "Toggles toggles all below",
            position = 0,
            keyName = "piiToggle",
            section = "piiSection"
    )
    default boolean piiToggle()
    {
        return true;
    }

    @Alpha
    @ConfigItem(
            name = "Points in Inventory",
            description = "Highlights brazier, when you have enough points in your inventory",
            position = 1,
            keyName = "pointInInv",
            section = "piiSection"
    )
    default Color pointInInv()
    {
        return Color.MAGENTA;
    }

    @ConfigItem(
            name = "Blink for PointInInv",
            description = "Toggles blinking brazier when you have enough logs in inventory",
            position = 2,
            keyName = "blinkPointInInv",
            section = "piiSection"
    )
    default boolean blinkPointInInv()
    {
        return true;
    }

    @Alpha
    @ConfigItem(
            name = "Blink color",
            description = "Choose the secondary color for when the brazier blinks",
            position = 3,
            keyName = "flashcolor",
            section = "piiSection",
            hidden = true,
            unhide = "blinkPointInInv"
    )
    default Color flashcolor()
    {
        return Color.RED;
    }

    //------------------------------------------------------------//
    //Miscellaneous
    //------------------------------------------------------------//

    @ConfigItem(
            name = "Infobox Toggle",
            description = "Toggles logs in inventory overlay",
            position = 0,
            keyName = "infoboxToggle",
            section = "miscSection"
    )
    default boolean infoboxToggle()
    {
        return true;
    }

    @Alpha
    @ConfigItem(
            name = "Task Done",
            description = "",
            position = 1,
            keyName = "taskdone",
            section = "miscSection"
    )
    default Color taskdone()
    {
        return Color.BLACK;
    }


}