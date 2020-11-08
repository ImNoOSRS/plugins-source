/*
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
import net.runelite.client.game.WorldLocation;
import net.runelite.client.graphics.ModelOutlineRenderer;
import net.runelite.client.plugins.PlayerLogger.PlayerLoggerConfig;
import net.runelite.client.plugins.PlayerLogger.PlayerLoggerPlugin;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.TextComponent;
import net.runelite.client.ui.overlay.components.table.TableAlignment;
import net.runelite.client.ui.overlay.components.table.TableComponent;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;

@Slf4j
@Singleton
class PlayerLoggerOverlayPanel extends OverlayPanel {

	private final Client client;
	private final PlayerLoggerConfig config;
	private final PlayerLoggerPlugin plugin;
	private final TextComponent textComponent = new TextComponent();

	@Inject
	private PlayerLoggerOverlayPanel(final Client client, final PlayerLoggerConfig config, final PlayerLoggerPlugin plugin) {
		setPosition(OverlayPosition.TOP_LEFT);
		this.client = client;
		this.config = config;
		this.plugin = plugin;
	}


	@Override
	public Dimension render(Graphics2D graphics) {
		if(config.PlayerInfobox()) {
			TableComponent tableComponent = new TableComponent();
			tableComponent.setColumnAlignments(TableAlignment.LEFT, TableAlignment.RIGHT);
			int players = 0;
			int currentplayers = 0;
			int players_who_chatted = 0;
			int players_who_chatted_total = 0;
			int current_players_who_chatted = 0;
			int current_players_who_chatted_total = 0;
			if (!plugin.players.isEmpty()) {
				for (Map.Entry<String, PlayerData> entry : plugin.players.entrySet()) {
					players++;
					PlayerData pd = entry.getValue();
					if (pd.isactive) {
						currentplayers++;
					}
					if (pd.chats > 0) {
						players_who_chatted++;
						players_who_chatted_total += pd.chats;
						if (pd.isactive) {
							current_players_who_chatted++;
							current_players_who_chatted_total += pd.chats;
						}
					}
				}
				tableComponent.addRow("PlayerLogger 0.0.1");
				tableComponent.addRow("Players found: ", "" + players);
				tableComponent.addRow("Chatted: ", "" + players_who_chatted);
				tableComponent.addRow("Chatted total: ", "" + players_who_chatted_total);
				tableComponent.addRow("");
				tableComponent.addRow("Current players: ", "" + currentplayers);
				tableComponent.addRow("Current chatted: ", "" + current_players_who_chatted);
				tableComponent.addRow("Current chat total: ", "" + current_players_who_chatted_total);
				panelComponent.getChildren().add(tableComponent);
			}
		}


		return super.render(graphics);
	}

}
