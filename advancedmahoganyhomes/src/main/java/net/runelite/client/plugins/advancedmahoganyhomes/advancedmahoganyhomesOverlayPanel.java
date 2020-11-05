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
import net.runelite.client.ui.overlay.components.TextComponent;
import net.runelite.client.ui.overlay.components.table.TableAlignment;
import net.runelite.client.ui.overlay.components.table.TableComponent;

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
        TableComponent tableComponent = new TableComponent();
        tableComponent.setColumnAlignments(TableAlignment.LEFT, TableAlignment.RIGHT);
        tableComponent.addRow("NPC: ", plugin.npc);
        tableComponent.addRow("Location: ", plugin.location);
        showdata(tableComponent, "object1", null, client.getVarbitValue(plugin.var_object1));
        showdata(tableComponent, "object2", null, client.getVarbitValue(plugin.var_object2));
        showdata(tableComponent, "object3", null, client.getVarbitValue(plugin.var_object3));
        showdata(tableComponent, "object4", null, client.getVarbitValue(plugin.var_object4));
        showdata(tableComponent, "object5", null, client.getVarbitValue(plugin.var_object5));
        showdata(tableComponent, "object6", null, client.getVarbitValue(plugin.var_object6));
        showdata(tableComponent, "object7", null, client.getVarbitValue(plugin.var_object7));
        showdata(tableComponent, "object8", null, client.getVarbitValue(plugin.var_object8));
        panelComponent.getChildren().add(tableComponent);

        return super.render(graphics);
    }

    public void showdata(TableComponent t, String name, GameObject g, int varbit)
    {
        String objectname = "";
        if(g != null) {
            ObjectDefinition od = client.getObjectDefinition(g.getId());
            if (od != null) {
                objectname = "(" + od.getName() + ")";
            }
        }
        t.addRow(name + objectname, "" + varbit + "-" + plugin.get_furniture_state(varbit));
    }

}
