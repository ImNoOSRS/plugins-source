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
package net.runelite.client.plugins.StealingArtifactsAddon;

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
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.AgilityShortcut;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.WorldLocation;
import net.runelite.client.graphics.ModelOutlineRenderer;
import net.runelite.client.plugins.StealingArtifactsAddon.StealingArtifactsAddonConfig;
import net.runelite.client.plugins.StealingArtifactsAddon.StealingArtifactsAddonPlugin;
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
class StealingArtifactsAddonOverlay extends Overlay {
    @Inject
    private ItemManager itemManager;
    private final ModelOutlineRenderer modelOutlineRenderer;

    private final Client client;
    private final StealingArtifactsAddonConfig config;
    private final StealingArtifactsAddonPlugin plugin;
    private final TextComponent textComponent = new TextComponent();

    @Inject
    private StealingArtifactsAddonOverlay(final Client client, final StealingArtifactsAddonConfig config, final StealingArtifactsAddonPlugin plugin, final ModelOutlineRenderer modelOutlineRenderer) {
        super(plugin);
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        this.modelOutlineRenderer = modelOutlineRenderer;
    }

	private static final int CULL_LINE_OF_SIGHT_RANGE = 3;
	private static final Color LINE_OF_SIGHT_COLOR = new Color(204, 42, 219);

    @Override
    public Dimension render(Graphics2D graphics) {
    	for(NPC current : plugin.Guards) {
			WorldArea area = current.getWorldArea();
			OverlayUtil.renderActorOverlay(graphics, current, "" + current.getOrientation() / 512, Color.RED);
			for (int x = area.getX() - CULL_LINE_OF_SIGHT_RANGE; x <= area.getX() + CULL_LINE_OF_SIGHT_RANGE; x++) {
				for (int y = area.getY() - CULL_LINE_OF_SIGHT_RANGE; y <= area.getY() + CULL_LINE_OF_SIGHT_RANGE; y++) {
					if (x == area.getX() && y == area.getY()) {
						continue;
					}
					renderTileIfHasLineOfSight(graphics, area, x, y, current.getOrientation() / 512, current);
				}
			}
		}
        return null;
    }
	
	private void renderTileIfHasLineOfSight(Graphics2D graphics, WorldArea start, int targetX, int targetY, int or, NPC npc)
	{
		WorldPoint targetLocation = new WorldPoint(targetX, targetY, start.getPlane());

		/*if(targetLocation.distanceTo(npc.getWorldLocation()) > 3)
		{
			return;
		}*/
		if(or == 3)
		{
			if(targetLocation.getX() < npc.getWorldLocation().getX())
			{
				return;
			}
		}

		if(or == 1)
		{
			if(targetLocation.getX() > npc.getWorldLocation().getX())
			{
				return;
			}
		}

		if(or == 0)
		{
			if(targetLocation.getY() > npc.getWorldLocation().getY())
			{
				return;
			}
		}

		if(or == 2)
		{
			if(targetLocation.getY() < npc.getWorldLocation().getY())
			{
				return;
			}
		}
		// Running the line of sight algorithm 100 times per frame doesn't
		// seem to use much CPU time, however rendering 100 tiles does
		if (start.hasLineOfSightTo(client, targetLocation))
		{
			LocalPoint lp = LocalPoint.fromWorld(client, targetLocation);
			if (lp == null)
			{
				return;
			}

			Polygon poly = Perspective.getCanvasTilePoly(client, lp);
			if (poly == null)
			{
				return;
			}

			OverlayUtil.renderPolygon(graphics, poly, LINE_OF_SIGHT_COLOR);
			OverlayText(graphics, lp, "" + targetLocation.distanceTo(npc.getWorldLocation()), Color.MAGENTA, 0 ,0);
		}
	}

	public void OverlayText(Graphics2D graphics, LocalPoint lp, String text, Color color, int offsetx, int offsety)
	{
		final Point textPoint = Perspective.getCanvasTextLocation(client,
				graphics,
				new LocalPoint(lp.getX() + (offsetx * Perspective.LOCAL_TILE_SIZE), lp.getY() + (offsety * Perspective.LOCAL_TILE_SIZE)),
				text,
				0);

		if(textPoint == null)//sometimes fails?
		{
			return;
		}

		textComponent.setText(text);
		textComponent.setColor(color);
		textComponent.setPosition(new java.awt.Point(textPoint.getX() + offsetx, textPoint.getY() + offsety));
		textComponent.render(graphics);
	}
}
