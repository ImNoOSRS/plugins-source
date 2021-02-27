//Created by PluginCreated by ImNo: https://github.com/ImNoOSRS 
package net.runelite.client.plugins.StealingArtifactsAddon;

import net.runelite.client.ui.overlay.OverlayManager;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.*;

@Extension
@PluginDescriptor(
        name = "Stealing Artifacts Addon",
        description = "Stealing Artifacts Addon",
        tags = {"imno"}
)
@Slf4j
public class StealingArtifactsAddonPlugin extends Plugin {
    // Injects our config
    @Inject
    private ConfigManager configManager;
    @Inject
    private StealingArtifactsAddonConfig config;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
        private OverlayManager overlayManager;
@Inject
        private StealingArtifactsAddonOverlay overlay;
    @Provides
    StealingArtifactsAddonConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(StealingArtifactsAddonConfig.class);
    }

    public final Set<NPC> Guards = new HashSet<>();

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("StealingArtifactsAddon"))
        {
            switch(event.getKey())
            {
                case "example":
                    break;
                default:
                    break;
            }
        }
    }

    private static final Set<Integer> guard_ids = Set.of(
            6973, 6974, 6975, 6976, 6978, 6979, 6980
    );

    @Override
    protected void startUp() {
        overlayManager.add(overlay);
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }
        for(NPC current : client.getNpcs())
        {
            if(guard_ids.contains(current.getId()))
            {
                Guards.add(current);
            }
        }
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(overlay);
        Guards.clear();
    }

    @Subscribe
    private void onGameStateChanged(final GameStateChanged event)
    {
        Guards.clear();
        final GameState gameState = event.getGameState();
        switch (gameState)
        {
            case LOGGED_IN:
                for(NPC current : client.getNpcs())
                {
                    if(guard_ids.contains(current.getId()))
                    {
                        Guards.add(current);
                    }
                }
                break;
            case LOGIN_SCREEN:
                break;
            default:
                break;
        }
    }

    @Subscribe
    private void onBeforeRender(final BeforeRender event) {
        if (this.client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
    }

    @Subscribe
    private void onNpcSpawned(final NpcSpawned event)
    {
        NPC current = event.getNpc();
        if(guard_ids.contains(current.getId()))
        {
            Guards.add(current);
        }
    }

    @Subscribe
    private void onNpcDespawned(final NpcDespawned event)
    {
        NPC current = event.getNpc();
        if(guard_ids.contains(current.getId()))
        {
            Guards.remove(event.getNpc());
        }
    }

    @Subscribe
    private void onWidgetLoaded(WidgetLoaded event)
    {

    }

    @Subscribe
    public void onGameTick(GameTick event) {

    }
}