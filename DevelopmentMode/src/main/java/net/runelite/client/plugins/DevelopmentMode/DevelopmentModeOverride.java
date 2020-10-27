package net.runelite.client.plugins.DevelopmentMode;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.DefaultPluginManager;
import org.pf4j.RuntimeMode;

@Slf4j
public class DevelopmentModeOverride extends DefaultPluginManager {
    @Override
    public RuntimeMode getRuntimeMode()
    {
        return RuntimeMode.DEVELOPMENT;
    }
}