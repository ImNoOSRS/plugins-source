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
package net.runelite.client.plugins.PlayerLogger;

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
import com.openosrs.client.game.WorldLocation;
import net.runelite.client.plugins.PlayerLogger.PlayerLoggerConfig;
import net.runelite.client.plugins.PlayerLogger.PlayerLoggerPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.TextComponent;

@Slf4j
@Singleton
class PlayerLoggerOverlay extends Overlay {
	@Inject
	private ItemManager itemManager;

	private final Client client;
	private final PlayerLoggerConfig config;
	private final PlayerLoggerPlugin plugin;
	private final TextComponent textComponent = new TextComponent();

	@Inject
	private PlayerLoggerOverlay(final Client client, final PlayerLoggerConfig config, final PlayerLoggerPlugin plugin) {
		super(plugin);
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.client = client;
		this.config = config;
		this.plugin = plugin;
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
		if(config.PlayerClickboxes()) {
			if (!plugin.players.isEmpty()) {
				for (Map.Entry<String, PlayerData> entry : plugin.players.entrySet()) {
					PlayerData pd = entry.getValue();
					if (pd.isactive) {
						RenderPlayer(graphics, entry.getValue().player);
					}
				}
			}
		}
		return null;
	}

	public void RenderPlayer(Graphics2D graphics, Player player)
	{
		Shape clickbox = Perspective.getClickbox(client, player.getModel(), player.getOrientation(), player.getLocalLocation());
		if(clickbox != null) {

			renderClickBox(graphics, mouse(), clickbox, Color.CYAN);
			Point p = player.getCanvasTextLocation(graphics, player.getName(), player.getModelHeight() + 40);
			if (p == null) {
				return;
			}
			OverlayUtil.renderTextLocation(graphics, player.getCanvasTextLocation(graphics, player.getName(), player.getLogicalHeight() + 40), player.getName(), Color.GREEN);
		}
	}

	public Point mouse()
	{
		return client.getMouseCanvasPosition();
	}
}