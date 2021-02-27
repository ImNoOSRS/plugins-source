/*
 * Copyright (c) 2020, Cyborger1
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.MLMUpperLevelMarkers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

class MLMUpperLevelMarkersOverlay extends Overlay
{
	private static final int MAX_DISTANCE = 2350;

	private final Client client;
	private final MLMUpperLevelMarkersPlugin plugin;
	private final MLMUpperLevelMarkersConfig config;

	@Inject
	MLMUpperLevelMarkersOverlay(Client client, MLMUpperLevelMarkersPlugin plugin, MLMUpperLevelMarkersConfig config)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		// TODO: Plug config here
		if (!plugin.isInMLM())
		{
			return null;
		}

		final Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null)
		{
			return null;
		}

		final LocalPoint playerLocalPoint = localPlayer.getLocalLocation();

		if (config.showOnlyWhenUpstairs() && !plugin.isUpstairs(playerLocalPoint))
		{
			return null;
		}

		final Duration firstTimeout = Duration.ofSeconds(config.getFirstTimeout());
		final Duration secondTimeout = Duration.ofSeconds(config.getSecondTimeout());

		for (Map.Entry<WorldPoint, StateTimePair> entry : plugin.getOreVeinStateMap().entrySet())
		{
			final OreVeinState state = entry.getValue().getState();
			final Instant time = entry.getValue().getTime();
			final LocalPoint localPoint = LocalPoint.fromWorld(client, entry.getKey());

			if (localPoint == null)
			{
				continue;
			}

			Color color;
			switch (state)
			{
				case MinedBySelf:
					color = config.getSelfMarkerColor();
					break;
				case MinedByOther:
					color = config.showOtherMarkers() ? config.getOtherMarkerColor() : null;
					break;
				default:
					color = null;
					break;
			}

			if (color != null && playerLocalPoint.distanceTo(localPoint) <= MAX_DISTANCE)
			{
				Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
				if (poly != null)
				{
					Duration sinceTime = Duration.between(time, Instant.now());
					if (firstTimeout.getSeconds() >= 0 && sinceTime.compareTo(firstTimeout) >= 0)
					{
						color = color.darker();
					}
					if (secondTimeout.getSeconds() >= 0 && sinceTime.compareTo(secondTimeout) >= 0)
					{
						color = color.darker();
					}

					OverlayUtil.renderPolygon(graphics, poly, color);
				}
			}
		}

		return null;
	}
}