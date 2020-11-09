/*
 * Copyright (c) 2020, ImNoOSRS <https://github.com/ImNoOSRS>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *	list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *	this list of conditions and the following disclaimer in the documentation
 *	and/or other materials provided with the distribution.
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
package net.runelite.client.plugins.ZalcanoHelper;

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
import net.runelite.client.plugins.ZalcanoHelper.ZalcanoHelperConfig;
import net.runelite.client.plugins.ZalcanoHelper.ZalcanoHelperPlugin;
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
class ZalcanoHelperOverlay extends Overlay {
	@Inject
	private ItemManager itemManager;
	private final ModelOutlineRenderer modelOutlineRenderer;

	private final Client client;
	private final ZalcanoHelperConfig config;
	private final ZalcanoHelperPlugin plugin;
	private final TextComponent textComponent = new TextComponent();

	@Inject
	private ZalcanoHelperOverlay(final Client client, final ZalcanoHelperConfig config, final ZalcanoHelperPlugin plugin, final ModelOutlineRenderer modelOutlineRenderer) {
		super(plugin);
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.client = client;
		this.config = config;
		this.plugin = plugin;
		this.modelOutlineRenderer = modelOutlineRenderer;
	}


	@Override
	public Dimension render(Graphics2D graphics) {
		render_lightning(graphics);
		return null;
	}

	public void render_lightning(Graphics2D graphics)
	{
		if(!plugin.isAtZalcano())
		{
			return;
		}
		for(LocalPoint l : plugin.getFallingrocklocations())
		{
			WorldPoint w = WorldPoint.fromLocal(client, l);
			if(w.getPlane() != client.getPlane())
			{
				continue;
			}
			Color c = Color.GREEN;
			int countdown = Math.abs(plugin.getTickssincefallingrocks() - plugin.getMaxticks());
			if(countdown == 3)
			{
				c = Color.ORANGE;
			}
			else if(countdown < 3)
			{
				c = Color.RED;
			}
			render_object_server_tile(graphics, WorldPoint.fromLocal(client, l), c, 0 , 0);
			OverlayText(graphics, l, "" + countdown, c, 0, 0);
		}

		ZalcanoHelperPlugin.zalcanostate state = plugin.getzalcanostate();
		if(plugin.glowingrock != null)
		{
			Shape clickbox = plugin.glowingrock.getClickbox();
			Color normal = Color.GREEN;
			if(state == ZalcanoHelperPlugin.zalcanostate.mine)
			{
				normal = Color.CYAN;
			}
			if(clickbox != null) {
				OverlayUtil.renderClickBox(graphics, mouse(), clickbox, plugin.rock_danger ? Color.RED : normal);
				if(plugin.rock_danger) {
					OverlayText(graphics, plugin.glowingrock.getLocalLocation(), "" + plugin.rock_danger_counter, Color.WHITE, 0, 0);
				}
			}
		}
		if (state != ZalcanoHelperPlugin.zalcanostate.mine) {
			if (plugin.furnace != null) {
				if (plugin.has_raw_ore) {
					Shape clickbox = plugin.furnace.getClickbox();
					if (clickbox != null) {
						OverlayUtil.renderClickBox(graphics, mouse(), clickbox, Color.GREEN);
					}
				}
			}

			if (plugin.altar != null) {
				if(plugin.has_smithed_ore)
				{
					Shape clickbox = plugin.altar.getClickbox();
					if (clickbox != null) {
						OverlayUtil.renderClickBox(graphics, mouse(), clickbox, Color.GREEN);
					}
				}
			}

			if(plugin.has_imbued_ore || config.servertile())
			{
				final LocalPoint servertile = LocalPoint.fromWorld(client, client.getLocalPlayer().getWorldLocation());
				final Polygon serverpoly = Perspective.getCanvasTileAreaPoly(client, servertile, 1);
				if (servertile == null)
				{
					return;
				}
				if(config.showbluecircles()) {
					if (config.alwaysshowbluetiles() || plugin.has_imbued_ore) {
						for (GameObject g : plugin.getBlue_boost_circles()) {
							Shape clickbox = g.getClickbox();
							if (clickbox != null) {
								Color c = config.bluecirclecolor();
						/*if(clickbox.getBounds().contains(servertile.getX(), servertile.getY()))
						{
							c = Color.GREEN;
						}*/
								Polygon polygon = Perspective.getCanvasTileAreaPoly(client, g.getLocalLocation(), 3);
								if (polygon.contains(serverpoly.getBounds().getCenterX(), serverpoly.getBounds().getCenterY())) {
									c = config.activebluecirclecolor();
								}

								OverlayUtil.renderPolygon(graphics, polygon, c);

								if(config.bluecircleticks()) {
									int ticks_till_dissapear = Math.abs(plugin.ticks_since_circle - 25);
									Color dissapear = Color.GREEN;
									if (ticks_till_dissapear < 10) {
										dissapear = Color.YELLOW;
									} else if (ticks_till_dissapear < 4) {
										dissapear = Color.RED;
									}
									Point p = g.getCanvasTextLocation(graphics, "" + ticks_till_dissapear, -20);
									if (p != null) {
										OverlayUtil.renderTextLocation(graphics, g.getCanvasTextLocation(graphics, "" + ticks_till_dissapear, -20), "" + ticks_till_dissapear, dissapear);
									}
								}
								//OverlayUtil.renderPolygon(graphics, polygon2, Color.BLUE);

						/*
						OverlayUtil.renderClickBox(graphics, mouse(), clickbox, c);
						OverlayText(graphics, g.getLocalLocation(), "" + servertile.getX() + "->" + clickbox.getBounds().getX(), c, 0, 0);
						OverlayText(graphics, g.getLocalLocation(), "" + servertile.getY() + "->" + clickbox.getBounds().getY(), c, 2, 0);*/
							}
						}
					}
				}
				drawStrokeAndFill(graphics, config.serverTileOutlineColor(), config.serverTileFillColor(),
						config.serverTileOutlineWidth(), serverpoly);
			}
		}

		if(plugin.zalcano != null)
		{
			String text = state.toString();

			if (state == ZalcanoHelperPlugin.zalcanostate.mine || plugin.has_imbued_ore) {
				Shape clickbox = Perspective.getClickbox(client, plugin.zalcano.getModel(), plugin.zalcano.getOrientation(), plugin.zalcano.getLocalLocation());
				if (clickbox != null) {

					OverlayUtil.renderClickBox(graphics, mouse(), clickbox, Color.MAGENTA);
				}
			}
			else
			{
				text = state.toString();
				if(plugin.has_imbued_ore)
				{
					text = "Attack";
				}
			}

			Point p = plugin.zalcano.getCanvasTextLocation(graphics, plugin.zalcano.getName(), plugin.zalcano.getModelHeight() + 40);
			if (p != null) {
				OverlayUtil.renderTextLocation(graphics, plugin.zalcano.getCanvasTextLocation(graphics, text, plugin.zalcano.getLogicalHeight() + 40), text, Color.GREEN);
			}

			if(state == ZalcanoHelperPlugin.zalcanostate.normal)
			{
				if(plugin.ticks_since_anim > 10)
				{
					plugin.ticks_since_anim = 1;
				}
				int count = Math.abs(plugin.ticks_since_anim - 11);
				Color c = Color.GREEN;
				if(count == 3)
				{
					c = Color.YELLOW;
				}
				else if(count < 3)
				{
					c = Color.RED;
				}
				final Point canvasPoint = plugin.zalcano.getCanvasTextLocation(graphics, "" + count, plugin.zalcano.getLogicalHeight() - 400);
				OverlayUtil.renderTextLocation(graphics, "" + count, config.TickFontSize(),
						config.TickFontStyle().getFont(), c, canvasPoint, config.TickFontShadow(), 0);
			}
		}

	}

	public void render_object_server_tile(Graphics2D graphics, WorldPoint worldlocation, Color color, int offsetx, int offsety)
	{
		WorldPoint w = new WorldPoint(worldlocation.getX() + offsetx, worldlocation.getY() + offsety, worldlocation.getPlane());
		final LocalPoint localPoint = LocalPoint.fromWorld(client, w);

		if (localPoint == null)
		{
			return;
		}

		Polygon polygon = Perspective.getCanvasTilePoly(client, localPoint);

		if (polygon == null)
		{
			return;
		}
		drawStrokeAndFill(graphics, color, new Color(255, 0, 0, 20),
				1.0f, polygon);
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
		textComponent.setPosition(new java.awt.Point(textPoint.getX(), textPoint.getY()));
		textComponent.render(graphics);
	}

	public Point mouse()
	{
		return client.getMouseCanvasPosition();
	}
}
