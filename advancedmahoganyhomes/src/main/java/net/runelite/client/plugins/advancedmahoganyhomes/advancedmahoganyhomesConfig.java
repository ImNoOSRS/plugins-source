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
package net.runelite.client.plugins.advancedmahoganyhomes;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("advancedmahoganyhomes")

public interface advancedmahoganyhomesConfig extends Config {
    @ConfigItem(
            position = 0,
            keyName = "MahoganyNeedsRepair",
            name = "Repair Color",
            description = "Which color should repairable objects be disaplyed with."
    )
    default Color RepairColor()
    {
        return new Color(248,187,208);
    }

    @ConfigItem(
            position = 2,
            keyName = "MahoganyNeedsRemove",
            name = "Remove Color",
            description = "Which color should removeable objects be disaplyed with."
    )
    default Color RemoveColor()
    {
        return Color.green;
    }

    @ConfigItem(
            position = 3,
            keyName = "MahoganyNeedsRebuild",
            name = "Rebuild Color",
            description = "Which color should rebuildable objects be disaplyed with."
    )
    default Color RebuildColor()
    {
        return Color.CYAN;
    }
}