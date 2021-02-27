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
package net.runelite.client.plugins.wintertodthelper;

import java.awt.*;
import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.client.game.ItemManager;
import com.openosrs.client.graphics.ModelOutlineRenderer;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

@Slf4j
@Singleton
class wintertodthelperOverlay extends Overlay {
    @Inject
    private ItemManager itemManager;

    private final Client client;
    private final wintertodthelperConfig config;
    private final wintertodthelperPlugin plugin;

    @Inject
    private wintertodthelperOverlay(final Client client, final wintertodthelperConfig config,
                                    final wintertodthelperPlugin plugin, final ModelOutlineRenderer modelOutlineRenderer) {
        super(plugin);
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.config = config;
        this.plugin = plugin;
    }

    public Point mouse() {
        return client.getMouseCanvasPosition();
    }

    public static void renderClickBox(Graphics2D graphics, Point mousePosition, Shape objectClickbox, Color configColor)
    {
        if (objectClickbox.contains(mousePosition.getX(), mousePosition.getY()))
        {
            graphics.setColor(configColor.darker());
        }
        else
        {
            graphics.setColor(configColor);
        }

        graphics.draw(objectClickbox);
        graphics.setColor(new Color(configColor.getRed(), configColor.getGreen(), configColor.getBlue(), 50));
        graphics.fill(objectClickbox);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if(config.ignorewait()) {
            if(plugin.timer > 0)
            {
                return null;
            }
        }
        render_brumaroot(graphics);
        render_brazier(graphics);
        return null;
    }

    public void render_brumaroot(Graphics2D graphics) {
        if (config.brumarootToggle()) {
            for (GameObject g : plugin.getBrumarootObjects()) {
                if (g.getPlane() == client.getPlane()) {
                    Shape clickbox = g.getClickbox();
                    if (clickbox != null) {
                        renderClickBox(graphics, mouse(), clickbox, bruma_root_color());
                        Color c;
                        if(plugin.isInventory_full())
                        {
                            c = config.taskdone();
                        }
                        else
                        {
                            if(plugin.isChopping())
                            {
                                c = config.chopping();
                            }
                            else
                            {
                                c = config.notChopping();
                            }
                        }
                        if(config.filled())
                        {
                            OverlayUtil.renderPolygon(graphics, clickbox, c);
                        }
                        else {
                            renderClickBox(graphics, mouse(), clickbox, c);
                        }
                    }
                }
            }
        }
    }

    public Color bruma_root_color() {
        Color c;
        if (plugin.isInventory_full()) {
            c = config.taskdone();
        } else {
            if (plugin.isChopping()) {
                c = config.chopping();
            } else {
                c = config.notChopping();
            }
        }
        return c;
    }

    public void render_brazier(Graphics2D graphics) {
        if (config.brazierToggle()) {
            for (GameObject g : plugin.getBrazierObjects()) {
                Shape clickbox = g.getClickbox();
                if (clickbox != null) {
                    if(config.filled())
                    {
                        OverlayUtil.renderPolygon(graphics, clickbox, brazier_color());
                    }
                    else {
                        renderClickBox(graphics, mouse(), clickbox, brazier_color());
                    }
                }
            }

            render_brazier_other_states(graphics);
        }
    }

    public Color brazier_color() {
        Color c;
        if (plugin.TotalLogs() == 0) {
            c = config.taskdone();
        } else {
            if (plugin.isBurning()) {
                c = config.burning();
            }
            else if(config.piiToggle() && plugin.TotalLogPoints() + plugin.score >= 500 && plugin.score < 500)
            {
                if(config.blinkPointInInv() && !plugin.flash) {
                    c = config.pointInInv();
                }
                else
                {
                    c = config.flashcolor();
                }
            }
            else {
                c = config.notBurning();
            }
        }
        return c;
    }

    public void render_brazier_other_states(Graphics2D graphics) {

        for (GameObject g : plugin.getBrokenBrazierObjects()) {
            Shape clickbox = g.getClickbox();
            if (clickbox != null) {
                if(config.filled())
                {
                    OverlayUtil.renderPolygon(graphics, clickbox, config.broken());
                }
                else {
                    renderClickBox(graphics, mouse(), clickbox, config.broken());
                }
            }
        }

        for (GameObject g : plugin.getUnlitBrazierObjects()) {
            Shape clickbox = g.getClickbox();
            if (clickbox != null) {
                if(config.filled())
                {
                    OverlayUtil.renderPolygon(graphics, clickbox, config.unlit());
                }
                else {
                    renderClickBox(graphics, mouse(), clickbox, config.unlit());
                }
            }
        }
    }
}