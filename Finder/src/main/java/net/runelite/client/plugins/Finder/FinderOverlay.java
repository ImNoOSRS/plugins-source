/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * Copyright (c) 2018, Cas <https://github.com/casvandongen>
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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.AgilityShortcut;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.WorldLocation;
import net.runelite.client.graphics.ModelOutlineRenderer;
import net.runelite.client.plugins.Finder.FinderConfig;
import net.runelite.client.plugins.Finder.FinderPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.TextComponent;
import net.runelite.client.ui.overlay.components.table.TableComponent;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;

@Slf4j
@Singleton
class FinderOverlay extends Overlay
{ 
    @Inject
    private ItemManager itemManager;
    private final ModelOutlineRenderer modelOutlineRenderer;

    private final Client client;
    private final FinderConfig config;
    private final FinderPlugin plugin;
    private final TextComponent textComponent = new TextComponent();

    @Inject
    private FinderOverlay(final Client client, final FinderConfig config, final FinderPlugin plugin, final ModelOutlineRenderer modelOutlineRenderer)
    {
        super(plugin);
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        this.modelOutlineRenderer = modelOutlineRenderer;
    }

    public Point mouse()
    {
        return client.getMouseCanvasPosition();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        for (Map.Entry<GameObject, String> entry : plugin.getGameobjects().entrySet()) {
            GameObject g = entry.getKey();
            String name = entry.getValue();
            Shape clickbox = g.getClickbox();
            if(clickbox == null)
            {
                continue;
            }
            OverlayUtil.renderClickBox(graphics, mouse(), clickbox, config.color());
            Entity e = g.getEntity();
            if(e == null)
            {
                continue;
            }

            if(config.shownames()) {
                Point p = g.getCanvasTextLocation(graphics, name, e.getModelHeight() - 40);
                if (p == null) {
                    continue;
                }
                OverlayUtil.renderTextLocation(graphics, g.getCanvasTextLocation(graphics, name, e.getModelHeight() - 40), name, Color.GREEN);
            }
        }

        for (Map.Entry<GroundObject, String> entry : plugin.getGroundobjects().entrySet()) {
            GroundObject g = entry.getKey();
            String name = entry.getValue();
            Shape clickbox = g.getClickbox();
            if(clickbox == null)
            {
                continue;
            }
            OverlayUtil.renderClickBox(graphics, mouse(), clickbox, config.color());
            Entity e = g.getEntity();
            if(e == null)
            {
                continue;
            }

            if(config.shownames()) {
                Point p = g.getCanvasTextLocation(graphics, name, e.getModelHeight() - 40);
                if (p == null) {
                    continue;
                }
                OverlayUtil.renderTextLocation(graphics, g.getCanvasTextLocation(graphics, name, e.getModelHeight() - 40), name, Color.GREEN);
            }
        }

        for (Map.Entry<WallObject, String> entry : plugin.getWallobjects().entrySet()) {
            WallObject g = entry.getKey();
            String name = entry.getValue();
            Shape clickbox = g.getClickbox();
            if(clickbox == null)
            {
                continue;
            }
            OverlayUtil.renderClickBox(graphics, mouse(), clickbox, config.color());
            Entity e = g.getEntity1();
            if(e == null)
            {
                continue;
            }

            if(config.shownames()) {
                Point p = g.getCanvasTextLocation(graphics, name, e.getModelHeight() - 40);
                if (p == null) {
                    continue;
                }
                OverlayUtil.renderTextLocation(graphics, g.getCanvasTextLocation(graphics, name, e.getModelHeight() - 40), name, Color.GREEN);
            }
        }

        for(NPC g : plugin.getNpcs())
        {
            Shape clickbox = Perspective.getClickbox(client, g.getModel(), g.getOrientation(), g.getLocalLocation());
            if(clickbox != null) {

                OverlayUtil.renderClickBox(graphics, mouse(), clickbox, Color.CYAN);
                if(config.shownames()) {
                    Point p = g.getCanvasTextLocation(graphics, g.getName(), g.getModelHeight() + 40);
                    if (p == null) {
                        continue;
                    }
                    OverlayUtil.renderTextLocation(graphics, g.getCanvasTextLocation(graphics, g.getName(), g.getLogicalHeight() + 40), g.getName(), Color.GREEN);
                }
            }
            /*else
            {
                //OverlayUtil.renderNpcOverlay(graphics, g, Color.CYAN, 1, 100, 80, client);
                OverlayUtil.renderActorOverlay(graphics, g, config.shownames() ? g.getName() : "", config.color());
            }*/

            //OverlayUtil.renderA(graphics, g, config.color(), 1, 1, 2, client);
            //OverlayUtil.renderTextLocation(graphics, g.getCanvasTextLocation(graphics, g.getName(), g.getLogicalHeight() - 40), g.getName(), Color.GREEN);
        }

        for(Player g : plugin.getPlayers())
        {
            Shape clickbox = Perspective.getClickbox(client, g.getModel(), g.getOrientation(), g.getLocalLocation());
            if(clickbox != null) {

                OverlayUtil.renderClickBox(graphics, mouse(), clickbox, Color.CYAN);
                if(config.shownames()) {
                    Point p = g.getCanvasTextLocation(graphics, g.getName(), g.getModelHeight() + 40);
                    if (p == null) {
                        continue;
                    }
                    OverlayUtil.renderTextLocation(graphics, g.getCanvasTextLocation(graphics, g.getName(), g.getLogicalHeight() + 40), g.getName(), Color.GREEN);
                }
            }
            /*else
            {
                //OverlayUtil.renderNpcOverlay(graphics, g, Color.CYAN, 1, 100, 80, client);
                OverlayUtil.renderActorOverlay(graphics, g, config.shownames() ? g.getName() : "", config.color());
            }*/
        }
        return null;
    }
}
