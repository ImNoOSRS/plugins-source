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
package net.runelite.client.plugins.DaeyaltEssence;

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
import net.runelite.client.plugins.DaeyaltEssence.DaeyaltEssenceConfig;
import net.runelite.client.plugins.DaeyaltEssence.DaeyaltEssencePlugin;
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
class DaeyaltEssenceOverlay extends Overlay {
	@Inject
	private ItemManager itemManager;
	private final ModelOutlineRenderer modelOutlineRenderer;

	private final Client client;
	private final DaeyaltEssenceConfig config;
	private final DaeyaltEssencePlugin plugin;
	private final TextComponent textComponent = new TextComponent();

	@Inject
	private DaeyaltEssenceOverlay(final Client client, final DaeyaltEssenceConfig config, final DaeyaltEssencePlugin plugin, final ModelOutlineRenderer modelOutlineRenderer) {
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
		if(plugin.current_deayalt_essence != null)
		{
			Shape clickbox = plugin.current_deayalt_essence.getClickbox();
			if(clickbox != null) {
				OverlayUtil.renderClickBox(graphics, mouse(), clickbox, plugin.animating ? config.DaeyaltEssenceColorInteracting() : config.DaeyaltEssenceColor());
				if(config.DaeyaltEssenceTimeTillChange()) {
					if (plugin.count != -1) {
						String count = "~" + Math.abs(plugin.count - 101);
						if (plugin.count == 102) {
							count = "SOON";
						}
						final Point canvasPoint = plugin.current_deayalt_essence.getCanvasTextLocation(graphics, count, plugin.current_deayalt_essence.getModel().getCenterZ());
						OverlayUtil.renderTextLocation(graphics, count, 20,
								Font.TRUETYPE_FONT, Color.WHITE, canvasPoint, false, 0);
					}
				}
			}
		}
		return null;
	}
}
