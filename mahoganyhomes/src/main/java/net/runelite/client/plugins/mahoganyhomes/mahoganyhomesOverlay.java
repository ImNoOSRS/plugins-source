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
package net.runelite.client.plugins.mahoganyhomes;

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
import net.runelite.client.plugins.mahoganyhomes.mahoganyhomesConfig;
import net.runelite.client.plugins.mahoganyhomes.mahoganyhomesPlugin;
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
class mahoganyhomesOverlay extends Overlay {
    @Inject
    private ItemManager itemManager;
    private final ModelOutlineRenderer modelOutlineRenderer;

    private final Client client;
    private final mahoganyhomesConfig config;
    private final mahoganyhomesPlugin plugin;
    private final TextComponent textComponent = new TextComponent();

    @Inject
    private mahoganyhomesOverlay(final Client client, final mahoganyhomesConfig config, final mahoganyhomesPlugin plugin, final ModelOutlineRenderer modelOutlineRenderer) {
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
        if(plugin.amy != null)
        {
            Shape clickbox = Perspective.getClickbox(client, plugin.amy.getModel(), plugin.amy.getOrientation(), plugin.amy.getLocalLocation());
            if(clickbox != null) {

                OverlayUtil.renderClickBox(graphics, mouse(), clickbox, Color.CYAN);
            }
            else
            {
                OverlayUtil.renderNpcOverlay(graphics, plugin.amy, Color.CYAN, 1, 100, 80, client);
            }
        }

        if(plugin.overlaynpc != null)
        {
            Shape clickbox = Perspective.getClickbox(client, plugin.overlaynpc.getModel(), plugin.overlaynpc.getOrientation(), plugin.overlaynpc.getLocalLocation());
            if(clickbox != null) {

                OverlayUtil.renderClickBox(graphics, mouse(), clickbox, Color.CYAN);
            }
            else
            {
                OverlayUtil.renderNpcOverlay(graphics, plugin.overlaynpc, Color.MAGENTA, 1, 100, 80, client);
            }
        }

        displayobject(graphics, plugin.object1, plugin.var_object1);
        displayobject(graphics, plugin.object2, plugin.var_object2);
        displayobject(graphics, plugin.object3, plugin.var_object3);
        displayobject(graphics, plugin.object4, plugin.var_object4);
        displayobject(graphics, plugin.object5, plugin.var_object5);
        displayobject(graphics, plugin.object6, plugin.var_object6);
        displayobject(graphics, plugin.object7, plugin.var_object7);
        displayobject(graphics, plugin.object8, plugin.var_object8);
        return null;
    }

    public void displayobject(Graphics2D graphics, Set<GameObject> list, int varbitgrabber)
    {
        int varbit = client.getVarbitValue(varbitgrabber);
        for(GameObject current : list) {
            if(plugin.location.equals("FALADOR")) {
                if (current.getWorldLocation().distanceTo(client.getLocalPlayer().getWorldLocation()) > 10) {
                    continue;
                }
            }
            if (current != null) {
                Color c = Color.DARK_GRAY;
                if(current.getPlane() != client.getPlane())
                {
                    c = Color.ORANGE;
                    //Not on same height;
                }
                if (varbit != 8 && varbit != 2 && varbit != 0) {
                    if(c != Color.ORANGE) {
                        switch(varbit)
                        {
                            case 1://needs repair
                                c = config.RepairColor();
                                break;
                            case 3://Needs remove
                                c = config.RemoveColor();
                                break;
                            case 4://needs rebuild
                                c = config.RebuildColor();
                                break;
                        }
                    }
                    Shape clickbox = current.getClickbox();
                    if (clickbox != null) {
                        OverlayUtil.renderClickBox(graphics, mouse(), clickbox, c);
                    }
                }
            }
        }
    }
}
