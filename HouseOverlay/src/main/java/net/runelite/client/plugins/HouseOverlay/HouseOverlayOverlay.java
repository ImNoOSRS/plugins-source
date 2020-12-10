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
package net.runelite.client.plugins.HouseOverlay;

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
import net.runelite.api.kit.KitType;
import net.runelite.api.queries.DecorativeObjectQuery;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.client.game.AgilityShortcut;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.WorldLocation;
import net.runelite.client.graphics.ModelOutlineRenderer;
import net.runelite.client.plugins.HouseOverlay.HouseOverlayConfig;
import net.runelite.client.plugins.HouseOverlay.HouseOverlayPlugin;
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
class HouseOverlayOverlay extends Overlay {
    @Inject
    private ItemManager itemManager;
    private final ModelOutlineRenderer modelOutlineRenderer;

    private final Client client;
    private final HouseOverlayConfig config;
    private final HouseOverlayPlugin plugin;
    private final TextComponent textComponent = new TextComponent();

    @Inject
    private HouseOverlayOverlay(final Client client, final HouseOverlayConfig config, final HouseOverlayPlugin plugin, final ModelOutlineRenderer modelOutlineRenderer) {
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
        if(plugin.inhouse)
        {
            final LocatableQueryResults<GameObject> locatableQueryResults = new GameObjectQuery().result(client);

            for (final GameObject gameObject : locatableQueryResults)
            {
                lastaction = "";
                extrainfo = "";
                int id = gameObject.getId();
                String name = "";
                name = hotfixednames(id);
                if(name.isEmpty()) {
                    name = getname(id);
                    if(name == "skip")
                    {
                        continue;
                    }
                }


                int modelheight = gameObject.getModel().getModelHeight();
                switch(id)
                {
                    case 29241://Rejuvenate Pool
                    case 40848://Frozen reju Pool
                    case 33375://Portal Nexus
                    case 4525://Exit Portal
                        modelheight = 65;
                        break;
                }

                ProcessObject(graphics, id, name, gameObject.getClickbox(), modelheight, gameObject.getCanvasTextLocation(graphics, name, modelheight), Color.GREEN);
            }

            final LocatableQueryResults<DecorativeObject> DecorativeQueryResults = new DecorativeObjectQuery().result(client);

            for (final DecorativeObject dob : DecorativeQueryResults)
            {
                lastaction = "";
                extrainfo = "";
                int id = dob.getId();
                String name = "";
                name = hotfixednames(id);
                if(name.isEmpty()) {
                    name = getname(id);
                    if(name == "skip")
                    {
                        continue;
                    }
                }
                ProcessObject(graphics, id, name, dob.getClickbox(), dob.getModel1().getModelHeight(), dob.getCanvasTextLocation(graphics, name, dob.getModel1().getModelHeight()), Color.ORANGE);
            }
        }
        return null;
    }

    String lastaction = "";
    String extrainfo = "";
    public String getname(int id)
    {
        ObjectDefinition def = client.getObjectDefinition(id);
        if (def != null) {
            if (def.getImpostorIds() != null) {
                def = def.getImpostor();
            }

            switch(id)
            {
                case 29156://Jewelery Box
                    lastaction = def.getActions()[2];
                    break;
                case 33412://Xerics
                case 33416://DigSite
                case 13523://Glory
                case 33375://PortalNexus
                    lastaction = def.getActions()[0];
                    break;
                case 29228://Fairy Ring
                case 29229://Fairy Ring Tree
                    for(String getter : def.getActions())
                    {
                        if(getter.toLowerCase().contains("last"))
                        {
                            lastaction = getter.replace(")", "").replace("(", "!");
                            lastaction = lastaction = "Last: " + plugin.get_fairy_ring_name(lastaction.split("!")[1]);
                            break;
                        }
                    }
                    break;
            }

            String[] actions = def.getActions();
            if(actions == null)
            {
                return "skip";
            }
            if(actions[0] != null && actions[0].equals("Enter")) {
                //WhiteList Portals
            }
            else
            {
                if (actions[1] == null) {
                    return "skip";
                }
                if (actions[1].equals("Jars")) {
                        return "skip";
                }
            }

            return def.getName().replace(" Portal", "");
        }
        return "";
    }

    public void ProcessObject(Graphics2D graphics, int id, String name, Shape clickbox, int ModelHeight, Point getCanvasTextLocation, Color c)
    {
        if(id < 1300)
        {
            //Ignore dumb shit like your player.
            return;
        }
        if(id == 13640)
        {
            //Ignore portal focus.
            return;
        }
        c = getcolor(id, c);
        if(clickbox != null) {
            OverlayUtil.renderClickBox(graphics, mouse(), clickbox, c);
        }

        if(getCanvasTextLocation == null)
        {
            return;
        }
        OverlayUtil.renderTextLocation(graphics, getCanvasTextLocation, name, Color.CYAN);
        if(!lastaction.isEmpty())
            OverlayUtil.renderTextLocation(graphics, new Point(getCanvasTextLocation.getX(), getCanvasTextLocation.getY() - 15), "(" + lastaction + ")", Color.RED);

        if(!extrainfo.isEmpty())
            OverlayUtil.renderTextLocation(graphics, new Point(getCanvasTextLocation.getX(), getCanvasTextLocation.getY() + 15), extrainfo, Color.MAGENTA);
    }

    public String hotfixednames(int id)
    {
        switch(id)
        {
            case 4525:
                return "Exit";
            case 29241:
            case 40848://Frozen
                return "Rejuvenation Pool";
        }

        return "";
    }

    public Color getcolor(int id, Color defaultcolor)
    {
        if(id == 29241 || id == 40848)//Ornate rej.
        {
            if(plugin.currentanimation == 7305)
            {
                return Color.ORANGE;
            }
            else {
                return Color.MAGENTA;
            }
        }

        if(id == 4525)//Exit
        {
            return Color.RED;
        }

        if(id == 29228)//Fairy Ring
        {
            if(config.fairyStaff())
            if(!plugin.fairy_ring_has_staff)
            {
                extrainfo = "WIELD STAFF";
                return Color.PINK;
            }
        }

        return defaultcolor;
    }
}
