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
package net.runelite.client.plugins.wintertodthelper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.base.Strings;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.BackgroundComponent;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.InfoBoxComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TextComponent;
import net.runelite.client.util.ImageUtil;


@Slf4j
@Singleton
class CountOverlay extends Overlay {
    @Inject
    private ItemManager itemManager;

    private final Client client;
    private final wintertodthelperConfig config;
    private final wintertodthelperPlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();

    private final int brumalogs = 20695;

    private Color specialred = new Color(156, 0, 0, 156);
    private Color specialgreen =  new Color(0, 156, 0, 156);
    private Color specialyellow =  new Color(200, 156, 0, 156);

    @Inject
    private CountOverlay(final Client client, final wintertodthelperConfig config, final wintertodthelperPlugin plugin) {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        this.setPosition(OverlayPosition.BOTTOM_RIGHT);
        this.panelComponent.setOrientation(net.runelite.client.ui.overlay.components.ComponentOrientation.VERTICAL);
    }

    public Point mouse() {
        return client.getMouseCanvasPosition();
    }

    public BufferedImage log_image = ImageUtil.getResourceStreamFromClass(getClass(), "bruma_logs.png");
    public BufferedImage kindling_image = ImageUtil.getResourceStreamFromClass(getClass(), "kindling.png");
    private void addLogsInfoBox() {
        if (plugin.getNormalLogsCount() == -1) {
            return;
        }
        InfoBoxComponent logsComponent = new InfoBoxComponent();

        int logsininv = plugin.getNormalLogsCount();
        int emptyinvspots = plugin.getEmptySpots();
        if(config.piiToggle() && plugin.TotalLogPoints() + plugin.score >= 500 && plugin.score < 500)
        {
            logsComponent.setBackgroundColor(config.pointInInv());
        }
        else {
            if (plugin.isBurning() || plugin.isChopping()) {
                logsComponent.setBackgroundColor(specialyellow);
            } else {
                if (emptyinvspots == 0) {
                    logsComponent.setBackgroundColor(specialgreen);
                } else {
                    logsComponent.setBackgroundColor(specialred);
                }
            }
        }


        //logsComponent.setImage(itemManager.getImage(brumalogs));
        //ImageUtil.resizeImage(itemManager.getImage(brumalogs), 36, 36)
        logsComponent.setImage(log_image);
        logsComponent.setText(logsininv + "/" + (logsininv + emptyinvspots));
        logsComponent.setColor(Color.white);
        //logsComponent.setPreferredLocation(new java.awt.Point(g.getCanvasLocation().getX(), g.getCanvasLocation().getY()));
        //panelComponent.setPreferredLocation(new java.awt.Point(g.getCanvasLocation().getX(), g.getCanvasLocation().getY()));
        logsComponent.setPreferredSize(new Dimension(40, 40));

        panelComponent.getChildren().add(logsComponent);
    }

    private void addFletchedLogsInfoBox() {
        if (plugin.getFletchedLogsCount() < 1) {
            return;
        }
        InfoBoxComponent logsComponent = new InfoBoxComponent();
        int fletchedlogsininv = plugin.getFletchedLogsCount();
        int normallogs = plugin.getNormalLogsCount();
        if(config.piiToggle() && plugin.TotalLogPoints() + plugin.score >= 500 && plugin.score < 500)
        {
            logsComponent.setBackgroundColor(config.pointInInv());
        }
        else {
            if (plugin.isBurning() || plugin.isFletching()) {
                logsComponent.setBackgroundColor(specialyellow);
            } else {
                if (normallogs == 0) {
                    logsComponent.setBackgroundColor(specialgreen);
                } else {
                    logsComponent.setBackgroundColor(specialred);
                }
            }
        }


        //logsComponent.setImage(itemManager.getImage(brumalogs));
        //ImageUtil.resizeImage(itemManager.getImage(brumalogs), 36, 36)
        logsComponent.setImage(kindling_image);
        logsComponent.setText(fletchedlogsininv + "/" + (fletchedlogsininv + normallogs));
        logsComponent.setColor(Color.white);
        //logsComponent.setPreferredLocation(new java.awt.Point(g.getCanvasLocation().getX(), g.getCanvasLocation().getY()));
        //panelComponent.setPreferredLocation(new java.awt.Point(g.getCanvasLocation().getX(), g.getCanvasLocation().getY()));
        logsComponent.setPreferredSize(new Dimension(40, 40));

        panelComponent.getChildren().add(logsComponent);
    }

    java.awt.Point FixedPoint = new java.awt.Point((int)panelComponent.getBounds().getLocation().getX() - 40, (int)panelComponent.getBounds().getLocation().getY() - 100);

    @Override
    public Dimension render(Graphics2D graphics) {
        if(!config.infoboxToggle())
        {
            return null;
        }
        if(!plugin.isInWintertodt())
        {
            return null;
        }

        panelComponent.getChildren().clear();
        addFletchedLogsInfoBox();
        addLogsInfoBox();
        panelComponent.setPreferredLocation(FixedPoint);
        panelComponent.setPreferredSize(new Dimension(40, 0));
        panelComponent.setBorder(new Rectangle(0, 0, 0, 0));
        panelComponent.render(graphics);
        return null;
    }

}