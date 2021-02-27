/*
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
package net.runelite.client.plugins.advancedmahoganyhomes;

import java.awt.*;
import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TextComponent;

@Slf4j
@Singleton
class advancedmahoganyhomesOverlayPanel extends OverlayPanel {

    private final Client client;
    private final advancedmahoganyhomesConfig config;
    private final advancedmahoganyhomesPlugin plugin;
    private final TextComponent textComponent = new TextComponent();

    @Inject
    private advancedmahoganyhomesOverlayPanel(final Client client, final advancedmahoganyhomesConfig config, final advancedmahoganyhomesPlugin plugin) {
        setPosition(OverlayPosition.TOP_LEFT);
        this.client = client;
        this.config = config;
        this.plugin = plugin;
    }


    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().add(LineComponent.builder()
                .left("NPC: ")
                .right("" + plugin.npc)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Location: ")
                .right("" + plugin.location)
                .build());

        showdata(panelComponent, "object1", null, client.getVarbitValue(plugin.var_object1));
        showdata(panelComponent, "object2", null, client.getVarbitValue(plugin.var_object2));
        showdata(panelComponent, "object3", null, client.getVarbitValue(plugin.var_object3));
        showdata(panelComponent, "object4", null, client.getVarbitValue(plugin.var_object4));
        showdata(panelComponent, "object5", null, client.getVarbitValue(plugin.var_object5));
        showdata(panelComponent, "object6", null, client.getVarbitValue(plugin.var_object6));
        showdata(panelComponent, "object7", null, client.getVarbitValue(plugin.var_object7));
        showdata(panelComponent, "object8", null, client.getVarbitValue(plugin.var_object8));
        //panelComponent.getChildren().add(panelComponent);

        return super.render(graphics);
    }

    public void showdata(PanelComponent t, String name, GameObject g, int varbit)
    {
        String objectname = "";
        if(g != null) {
            ObjectComposition od = client.getObjectDefinition(g.getId());
            if (od != null) {
                objectname = "(" + od.getName() + ")";
            }
        }

        panelComponent.getChildren().add(LineComponent.builder()
                .left(name + objectname)
                .right("" + varbit + "-" + plugin.get_furniture_state(varbit))
                .build());

    }

}
