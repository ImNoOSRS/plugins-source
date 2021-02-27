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
package net.runelite.client.plugins.DeveloperHelper;

import net.runelite.client.config.*;

@ConfigGroup("DeveloperHelper")

public interface DeveloperHelperConfig extends Config {

    @ConfigItem(
            name = "Copy Item Data",
            description = "",
            position = 1,
            keyName = "dhCopyItemData"
    )
    default boolean copyItemData()
    {
        return true;
    }

    @ConfigItem(
            name = "Copy Object Data",
            description = "Copy Object Data",
            position = 2,
            keyName = "dhCopyObjectData"
    )
    default boolean copyObjectData()
    {
        return true;
    }

    @ConfigItem(
            name = "Copy Tile Data",
            description = "Copy Tile Data",
            position = 3,
            keyName = "dhCopyTileData"
    )
    default boolean copyTileData()
    {
        return true;
    }

    @ConfigItem(
            name = "Copy Chat Text",
            description = "Copies chat text for you.",
            position = 4,
            keyName = "dhCopyChatText"
    )
    default boolean copyChat()
    {
        return true;
    }

    @ConfigItem(
            name = "Ignore chat colordata",
            description = "Ignores the colordata for you.",
            position = 5,
            keyName = "dhCopyChatIgnoreColor"
    )
    default boolean IgnoreChatColor()
    {
        return false;
    }

    @ConfigItem(
            name = "Require Shift For Copying",
            description = "Make it so you need shift to copy data.",
            position = 6,
            keyName = "dhShiftRequired"
    )
    default boolean shift()
    {
        return true;
    }

}
