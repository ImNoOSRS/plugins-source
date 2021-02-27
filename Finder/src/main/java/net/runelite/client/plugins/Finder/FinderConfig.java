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
package net.runelite.client.plugins.Finder;

import net.runelite.client.config.*;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

@ConfigGroup("Finder")

public interface FinderConfig extends Config {


    @ConfigItem(
            position = 0,
            keyName = "FinderHotkey",
            name = "Hotkey",
            description = "Hotkey to use Finder"
    )
    default Keybind hotkey()
    {
        return new Keybind(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK);
    }

    @ConfigItem(
            position = 1,
            keyName = "FinderShowNames",
            name = "Show Names",
            description = "Display names on objects and players."
    )
    default boolean shownames()
    {
        return true;
    }

    @ConfigItem(
            position = 2,
            keyName = "autoresearch",
            name = "Automaticly re-search",
            description = "Automaticly research every X ticks."
    )
    default boolean autoresearch()
    {
        return false;
    }

    @ConfigItem(
            position = 3,
            keyName = "autoresearchdelay",
            name = "Delay",
            description = "Delay for auto research",
            hidden = true,
            unhide = "autoresearch"
    )
    default int researchdelay()
    {
        return 5;
    }

    @ConfigItem(
            position = 4,
            keyName = "FinderColor",
            name = "Overlay color",
            description = "Which color should objects/players be hilighted with."
    )
    default Color color()
    {
        return Color.CYAN;
    }

    @ConfigItem(
            position = 5,
            keyName = "FinderRest",
            name = "Reset others on search",
            description = "Reset other searches on new search (If disabled you can reset by doing an empty search)"
    )
    default boolean reset()
    {
        return false;
    }

    @ConfigItem(
            position = 6,
            keyName = "FinderResearch",
            name = "Research on GameState",
            description = "Auto re-search last when gamestate changes (Login, Loading, etc)"
    )
    default boolean research()
    {
        return false;
    }

    @ConfigItem(
            position = 7,
            keyName = "FinderGameObjects",
            name = "Search Game Objects",
            description = "Adds game objects to the search"
    )
    default boolean usegameobjects()
    {
        return true;
    }

    @ConfigItem(
            position = 7,
            keyName = "FinderWallObjects",
            name = "Search Wall Objects",
            description = "Adds wall objects to the search"
    )
    default boolean usewallobjects()
    {
        return true;
    }

    @ConfigItem(
            position = 8,
            keyName = "FinderGroundObjects",
            name = "Search Ground Objects",
            description = "Adds ground objects to the search"
    )
    default boolean usegroundobjects()
    {
        return true;
    }

    @ConfigItem(
            position = 9,
            keyName = "FinderNpcs",
            name = "Search NPCs",
            description = "Adds npcs to the search"
    )
    default boolean usenpcs()
    {
        return true;
    }

    @ConfigItem(
            position = 10,
            keyName = "FinderPlayers",
            name = "Search Players",
            description = "Adds players to the search"
    )
    default boolean useplayers()
    {
        return true;
    }

}
