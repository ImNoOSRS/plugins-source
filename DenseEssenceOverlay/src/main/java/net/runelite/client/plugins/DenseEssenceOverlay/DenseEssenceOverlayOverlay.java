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
package net.runelite.client.plugins.DenseEssenceOverlay;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.game.SpriteManager;
import com.openosrs.client.graphics.ModelOutlineRenderer;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.TextComponent;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;

@Slf4j
@Singleton
class DenseEssenceOverlayOverlay extends Overlay
{
    @Inject
    private static final int Z_OFFSET = 200;
    private ItemManager itemManager;
    private SpriteManager spriteManager;
    private final ModelOutlineRenderer modelOutlineRenderer;

    private final Client client;
    private final DenseEssenceOverlayConfig config;
    private final DenseEssenceOverlayPlugin plugin;
    private final TextComponent textComponent = new TextComponent();
    private final SkillIconManager skillIconManager;

    @Inject
    private DenseEssenceOverlayOverlay(final Client client, final DenseEssenceOverlayConfig config, final DenseEssenceOverlayPlugin plugin, final ModelOutlineRenderer modelOutlineRenderer, final SkillIconManager skillIconManager, final ItemManager itemManager, final SpriteManager spriteManager)
    {
        super(plugin);
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        this.modelOutlineRenderer = modelOutlineRenderer;
        this.skillIconManager = skillIconManager;
        this.itemManager = itemManager;
        this.spriteManager = spriteManager;
    }

    public Point mouse()
    {
        return client.getMouseCanvasPosition();
    }

    public BufferedImage getBankFiller()
    {
        return itemManager.getImage(ItemID.BANK_FILLER);
    }

    public BufferedImage checkmark_image = ImageUtil.getResourceStreamFromClass(getClass(), "checkmark.png");

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
        if(!config.showLapOverlay())
        {
            return null;
        }
        if (!plugin.inarea) {
            return null;
        }
        for (GameObject g: plugin.getDenseObjects()) {
            ObjectComposition definition = client.getObjectDefinition(g.getId());
            if (definition != null) {
                if (definition.getImpostorIds() != null) {
                    definition = definition.getImpostor();
                }

                boolean dense = definition.getId() == 8975;
                Color c;

                if (plugin.isInventory_full() && config.fullToggle()) {
                    c = config.full();
                } else if (dense) {
                    if(plugin.mining && config.miningToggle())
                    {
                        c = config.mining();
                    }
                    else {
                        if (!config.denseToggle()) {
                            c = Color.BLACK;
                        } else {
                            c = config.dense();
                        }
                    }
                } else {
                    if (!config.depletedToggle()) {
                        c = Color.BLACK;
                    } else {
                        c = config.depleted();
                    }
                }

                Shape clickbox = g.getClickbox();
                if (clickbox != null)
                {
                    if (c != Color.BLACK)
                    {
                        renderClickBox(graphics, mouse(), clickbox, c);
                    }
                }
                if (config.showDenseRunestoneIndicator() && config.denseIndicator() && dense && !plugin.isInventory_full()) {
                    OverlayUtil.renderImageLocation(client, graphics, g.getLocalLocation(), skillIconManager.getSkillImage(Skill.MINING, false), Z_OFFSET);
                } else if (config.showDenseRunestoneIndicator() && config.depletedIndicator()) {
                    if (!dense) {
                        OverlayUtil.renderImageLocation(client, graphics, g.getLocalLocation(), getBankFiller(), Z_OFFSET);
                    }
                    else if (config.fullIndicator() && plugin.isInventory_full() && config.fullToggle())
                    {
                        OverlayUtil.renderImageLocation(client, graphics, g.getLocalLocation(), checkmark_image, Z_OFFSET);
                    }
                }
            }
        }
        return null;
    }
}