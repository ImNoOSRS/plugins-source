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
package net.runelite.client.plugins.smokedevil;

import net.runelite.client.config.*;

import java.awt.*;


@ConfigGroup("smokedevil")
public interface SmokeDevilConfig extends Config
{


    //Sections

    @ConfigSection(
            name = "General",
            description = "",
            position = 0
    )
	String generalSection = "generalSection";

    @ConfigSection(
            name = "Safe Border Settings",
            description = "",
            position = 1
    )
	String borderSection = "borderSection";

    @ConfigSection(
            name = "NPC Overlay",
            description = "",
            position = 2
    )
	String npcSection = "npcSection";

    @ConfigSection(
            name = "Debug Settings",
            description = "",
            position = 3
    )
	String debugSection = "debugSection";

    //General Section
    @ConfigItem(
            name = "(FILLER)",
            description = "Does Nothing",
            position = 0,
            keyName = "AttackRangeSection",
            section = generalSection
    )
    default boolean AttackRangeSection()
    {
        return false;
    }

    //BorderSection
    /*@Alpha
    @ConfigItem(
            name = "SAFE COLOR (NOT WORKING)",
            description = "Shows the tiles just out of range of the Smoke Devils Attacks",
            position = 1,
            keyName = "barragecolor",
            section = borderSection
    )
    default Color barragecolor()
    {
        return Color.GREEN;
    }

     */

    @Alpha
    @ConfigItem(
            name = "Safe Color",
            description = "Shows the tiles just out of range of the Smoke Devils Attacks",
            position = 0,
            keyName = "safeColor",
            section = borderSection
    )
    default Color safeColor()
    {
        return Color.GREEN;
    }

    @Alpha
    @ConfigItem(
            name = "Unsafe Color",
            description = "Shows the tiles just out of range of the Smoke Devils Attacks",
            position = 1,
            keyName = "unsafeColor",
            section = borderSection
    )
    default Color unsafeColor()
    {
        return Color.RED;
    }

    //NPC Overlay Section
    @ConfigItem(
            position = 0,
            keyName = "highlightStyle",
            name = "Highlight Style",
            description = "Highlight setting",
            section = npcSection
    )
    default RenderStyle renderStyle()
    {
        return RenderStyle.THIN_OUTLINE;
    }

    @Alpha
    @ConfigItem(
            name = "Barraged Color",
            description = "Color that shows when NPC is Barraged",
            position = 1,
            keyName = "barragedColor",
            section = npcSection
    )
    default Color barragedColor()
    {
        return Color.CYAN;
    }
    @Alpha
    @ConfigItem(
            name = "Barrage In Color",
            description = "Color that shows when NPC should be Barraged",
            position = 2,
            keyName = "barrageIn",
            section = npcSection
    )
    default Color barrageIn()
    {
        return Color.ORANGE;
    }

    //Debug Section
    @ConfigItem(
            name = "Debug Info",
            description = "Shows the tiles just out of range of the Smoke Devils Attacks",
            position = 1,
            keyName = "debugInfo",
            section = debugSection
    )
    default boolean showdebugInfo()
    {
        return false;
    }
}
