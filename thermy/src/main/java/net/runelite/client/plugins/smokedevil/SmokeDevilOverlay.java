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
package net.runelite.client.plugins.smokedevil;

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
class SmokeDevilOverlay extends Overlay
{
    @Inject
    private ItemManager itemManager;
    private final ModelOutlineRenderer modelOutlineRenderer;

    private final Client client;
    private final SmokeDevilConfig config;
    private final SmokeDevilPlugin plugin;
    private final TextComponent textComponent = new TextComponent();

    private Player player;

    @Inject
    private SmokeDevilOverlay(final Client client, final SmokeDevilConfig config, final SmokeDevilPlugin plugin, final ModelOutlineRenderer modelOutlineRenderer)
    {
        super(plugin);
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        this.modelOutlineRenderer = modelOutlineRenderer;
    }



    @Override
    public Dimension render(Graphics2D graphics)
    {
        Color c = Color.GREEN;
        //The default color is green /\
        Color c2 = Color.GREEN;
        NPC npc = plugin.getNPC_NAME();
        if(npc == null)
        {
            return null;
        }

        String text = "";
        Color bordercolor = config.unsafeColor();
        if(plugin.is_barraged())
        {
            text += " Barraged:" + (plugin.getBarrages_ticks_max() - plugin.getBarrages_ticks());
            /*Not Sure About this line?
            c = config.barragecolor();

             */
        }

        LocalPoint realnpclocation = movey(movex(npc.getLocalLocation(), 0), 0);
        int distancex = (client.getLocalPlayer().getLocalLocation().getX() / 128) - realnpclocation.getX()/ 128;
        int distancey = (client.getLocalPlayer().getLocalLocation().getY() / 128) - realnpclocation.getY() / 128;

        if(config.showdebugInfo())
        {
            text += " DISTANCE: " + distancex + " AND " + distancey;
            render_tile(graphics, realnpclocation, Color.PINK, Color.PINK);
        }

        if(plugin.isDelaying())
        {
            text = "Barrage in: " + plugin.getBarragedelay();
            c = config.barrageIn();
        }
        if(distancey == -11 || distancex == 10 || distancex == -11)
        {
            bordercolor = config.safeColor();
        }

        if(plugin.is_barraged())
        {
            c = config.barragedColor();
        }

        Color bordercolortransparent = new Color(bordercolor.getRed(), bordercolor.getGreen(), bordercolor.getBlue(), 80);
        //And here below it uses c and c2 \/ How would you make that a configable thing though?
        //OOOHXD
        //render_tile(graphics, npc.getLocalLocation(), c, 12);
        render_safe_squares(graphics, npc, bordercolor, bordercolortransparent);
        OverlayText(graphics, npc.getLocalLocation(), text, c, 0, -150);


        render_npc_shit(graphics, npc, c);
        return null;
    }

    private static final Color TRANSPARENT = new Color(0, 0, 0, 0);

    public void render_npc_shit(Graphics2D graphics, NPC actor, Color color)
    {
        		switch (config.renderStyle())
		{
			case SOUTH_WEST_TILE:
			{
				int size = 1;
				NPCDefinition composition = actor.getTransformedDefinition();
				if (composition != null)
				{
					size = composition.getSize();
				}

				LocalPoint localPoint = actor.getLocalLocation();

				int x = localPoint.getX() - ((size - 1) * Perspective.LOCAL_TILE_SIZE / 2);
				int y = localPoint.getY() - ((size - 1) * Perspective.LOCAL_TILE_SIZE / 2);

				Polygon tilePoly = Perspective.getCanvasTilePoly(client, new LocalPoint(x, y));

				renderPoly(graphics, color, tilePoly);
				break;
			}
			case TILE:
			{
				int size = 1;
				NPCDefinition composition = actor.getTransformedDefinition();
				if (composition != null)
				{
					size = composition.getSize();
				}
				final LocalPoint lp = actor.getLocalLocation();
				final Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, lp, size);
				renderPoly(graphics, color, tilePoly);
				break;
			}
			case THIN_TILE:
			{
				int size = 1;
				NPCDefinition composition = actor.getTransformedDefinition();
				if (composition != null)
				{
					size = composition.getSize();
				}
				final LocalPoint lp = actor.getLocalLocation();
				final Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, lp, size);
				renderPoly(graphics, color, tilePoly, 1);
				break;
			}
			case HULL:
				final Shape objectClickbox = actor.getConvexHull();
				if (objectClickbox != null)
				{
					graphics.setColor(color);
					graphics.draw(objectClickbox);
				}
				break;
			case THIN_OUTLINE:
				modelOutlineRenderer.drawOutline(actor, 1, color);
				break;
			case OUTLINE:
				modelOutlineRenderer.drawOutline(actor, 2, color);
				break;
			case THIN_GLOW:
				modelOutlineRenderer.drawOutline(actor, 4, color, TRANSPARENT);
				break;
			case GLOW:
				modelOutlineRenderer.drawOutline(actor, 8, color, TRANSPARENT);
				break;
			case TRUE_LOCATIONS:
			{
				int size = 1;
				NPCDefinition composition = actor.getTransformedDefinition();
				if (composition != null)
				{
					size = composition.getSize();
				}
				LocalPoint lp = LocalPoint.fromWorld(client, actor.getWorldLocation());
				if (lp != null)
				{
					lp = new LocalPoint(lp.getX() + size * 128 / 2 - 64, lp.getY() + size * 128 / 2 - 64);
					final Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, lp, size);
					renderPoly(graphics, color, tilePoly);
				}
				break;
			}
			case THIN_TRUE_LOCATIONS:
			{
				int size = 1;
				NPCDefinition composition = actor.getTransformedDefinition();
				if (composition != null)
				{
					size = composition.getSize();
				}
				LocalPoint lp = LocalPoint.fromWorld(client, actor.getWorldLocation());
				if (lp != null)
				{
					lp = new LocalPoint(lp.getX() + size * 128 / 2 - 64, lp.getY() + size * 128 / 2 - 64);
					final Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, lp, size);
					renderPoly(graphics, color, tilePoly, 1);
				}
				break;
			}
		}
    }

    private void renderPoly(Graphics2D graphics, Color color, Polygon polygon)
	{
		if (polygon != null)
		{
			graphics.setColor(color);
			graphics.setStroke(new BasicStroke(2));
			graphics.draw(polygon);
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
			graphics.fill(polygon);
		}
	}

    private void renderPoly(Graphics2D graphics, Color color, Polygon polygon, int width)
	{
		if (polygon != null)
		{
			graphics.setColor(color);
			graphics.setStroke(new BasicStroke(width));
			graphics.draw(polygon);
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
			graphics.fill(polygon);
		}
	}

    public void render_safe_squares(Graphics2D graphics, NPC npc, Color bordercolor, Color bordercolortransparent)
    {
        WorldPoint npcpoint = npc.getWorldLocation();
        WorldPoint lefttop = movey(movex(npcpoint, -9), -9);
        render_tile(graphics, lefttop, bordercolor, bordercolortransparent);
        int tileoffset = 21;
        for(int i=1;i<=tileoffset;i++){
           lefttop = movex(lefttop, 1);
           render_tile(graphics, lefttop, bordercolor, bordercolortransparent);
        };
        for(int i=1;i<=tileoffset;i++){
           lefttop = movey(lefttop, 1);
           render_tile(graphics, lefttop, bordercolor, bordercolortransparent);
        };
        for(int i=1;i<=tileoffset;i++){
           lefttop = movex(lefttop, -1);
           render_tile(graphics, lefttop, bordercolor, bordercolortransparent);
        };
        tileoffset -= 1;
        for(int i=1;i<=tileoffset;i++){
           lefttop = movey(lefttop, -1);
           render_tile(graphics, lefttop, bordercolor, bordercolortransparent);
        };
    }

    public Point mouse()
    {
        return client.getMouseCanvasPosition();
    }

    public WorldPoint movex(WorldPoint p, double amount)
    {
        double offset = amount;
        return new WorldPoint(p.getX() + (int)offset, p.getY(), p.getPlane());
    }

    public WorldPoint movey(WorldPoint p, double amount)
    {
        double offset = amount;
        return new WorldPoint(p.getX(), p.getY() + (int)offset, p.getPlane());
    }

    public LocalPoint movex(LocalPoint p, double amount)
    {
        double offset = amount * 128;
        return new LocalPoint(p.getX() + (int)offset, p.getY());
    }

    public LocalPoint movey(LocalPoint p, double amount)
    {
        double offset = amount * 128;
        return new LocalPoint(p.getX(), p.getY() + (int)offset);
    }

    public void render_tile(Graphics2D graphics, LocalPoint localpoint, Color color, Color color2)
    {
        Polygon polygon = Perspective.getCanvasTilePoly(client, localpoint);

        if (polygon == null)
        {
            return;
        }
        drawStrokeAndFill(graphics, color, color2,
                1.0f, polygon);
    }

    public void render_tile(Graphics2D graphics, WorldPoint worldlocation, Color color, Color color2)
    {
        final LocalPoint localPoint = LocalPoint.fromWorld(client, worldlocation);

        if (localPoint == null)
        {
            return;
        }


        Polygon polygon = Perspective.getCanvasTilePoly(client, localPoint);

        if (polygon == null)
        {
            return;
        }
        drawStrokeAndFill(graphics, color, color2,
                1.0f, polygon);
    }

    public void OverlayText(Graphics2D graphics, LocalPoint lp, String text, Color color, int offsetx, int offsety)
    {
        final Point textPoint = Perspective.getCanvasTextLocation(client,
                graphics,
                new LocalPoint(lp.getX() + offsetx, lp.getY() + offsety),
                text,
                0);

        if(textPoint == null)//sometimes fails?e

        {
            return;
        }

        textComponent.setText(text);
        textComponent.setColor(color);
        textComponent.setPosition(new java.awt.Point(textPoint.getX(), textPoint.getY()));
        textComponent.render(graphics);
    }

    private static void drawStrokeAndFill(final Graphics2D graphics2D, final Color outlineColor, final Color fillColor,
                                          final float strokeWidth, final Shape shape)
    {
        graphics2D.setColor(outlineColor);
        final Stroke originalStroke = graphics2D.getStroke();
        graphics2D.setStroke(new BasicStroke(strokeWidth));
        graphics2D.draw(shape);
        graphics2D.setColor(fillColor);
        graphics2D.fill(shape);
        graphics2D.setStroke(originalStroke);
    }

    public void render_tile(Graphics2D graphics, LocalPoint lp, Color color, int size)
    {
        Color color2 = new Color(color.getRed(), color.getGreen(), color.getBlue(), 60);
        Polygon polygon = Perspective.getCanvasTileAreaPoly(client, lp, size);

        if (polygon == null)
        {
            return;
        }
        drawStrokeAndFill(graphics, color, color2,
                1.0f, polygon);
    }


}