//Created by PluginCreator by ImNo: https://github.com/ImNoOSRS 
package net.runelite.client.plugins.ConfigSearch;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.PluginPanel;
import javax.inject.Inject;

@Slf4j
public class ConfigPanelOverride extends PluginPanel {

    @Inject
    protected void rebuild() {
        log.info("Rebuilding");
    }
}