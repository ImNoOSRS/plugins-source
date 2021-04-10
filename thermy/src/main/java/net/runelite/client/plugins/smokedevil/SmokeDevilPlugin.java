package net.runelite.client.plugins.smokedevil;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.kit.KitType;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;

@Extension
@PluginDescriptor(
        name = "Smoke Devil",
        enabledByDefault = false,
        description = "Show freeze timer against Smoke Devil",
        tags = {"Smoke", "devil", "Lazy", "Dildo looking mofo", "imno"}
)


@Slf4j
public class SmokeDevilPlugin extends Plugin {
    // Injects our config
    @Inject
    private ConfigManager configManager;
    @Inject
    private SmokeDevilConfig config;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private SmokeDevilOverlay overlay;
    @Inject
    private OverlayManager overlayManager;

    @Provides
    SmokeDevilConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SmokeDevilConfig.class);
    }


    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("smokedevil"))
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

    @Override
    protected void startUp() {
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        for (final NPC npc : client.getNpcs())
        {
            if(npc.getId() == 499)
            {
                NPC_NAME = npc;
                if (!overlayManager.anyMatch(o -> o instanceof SmokeDevilOverlay))
                {
                    overlayManager.add(overlay);
                }
            }
        }

    }

    @Subscribe
    private void onGameStateChanged(final GameStateChanged event)
    {
        NPC_NAME = null;
    }

    @Getter(AccessLevel.PACKAGE)
    private NPC NPC_NAME;

    @Getter(AccessLevel.PACKAGE)
    private boolean is_barraged;

    @Getter(AccessLevel.PACKAGE)
    private int barrages_ticks = 0;

    @Getter(AccessLevel.PACKAGE)
    private int barrages_ticks_max = 32;

    @Subscribe
    private void onNpcSpawned(final NpcSpawned event)
    {
        NPC current = event.getNpc();
        if(current.getId() == 499)
        {
            NPC_NAME = current;
            if (!overlayManager.anyMatch(o -> o instanceof SmokeDevilOverlay))
            {
                overlayManager.add(overlay);
            }
        }
    }

    @Subscribe
    private void onNpcDespawned(final NpcDespawned event)
    {
        NPC current = event.getNpc();
        if(current.getId() == 499)
        {
            NPC_NAME = null;
        }
    }

    @Override
    protected void shutDown() {
        if (overlayManager.anyMatch(o -> o instanceof SmokeDevilOverlay))
        {
            overlayManager.remove(overlay);
        }
    }

    @Subscribe
    private void onBeforeRender(final BeforeRender event) {
        if (this.client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
    }

    @Subscribe
    private void onWidgetLoaded(WidgetLoaded event)
    {

    }

    private boolean need1reload = false;
    @Subscribe
    private void onAnimationChanged(final AnimationChanged event) {
        if (event.getActor().getAnimation() == 1979) {
            need1reload = true;
        }
    }

    private final int ice_barrage = 369;
    private WorldPoint lastduslocation;

    @Getter(AccessLevel.PACKAGE)
    private int barragedelay = 5;

    @Getter(AccessLevel.PACKAGE)
    private boolean delaying = false;

    @Subscribe
    public void onGameTick(GameTick event) {
        if(NPC_NAME == null)
        {
            return;
        }
        log.info("Counting up 1tick: ");
        barrages_ticks ++;
        if(barragedelay == 1)
        {
            delaying = false;
        }
        if(barragedelay > 1)
        {
            barragedelay--;
        }
        log.info("TOTAL:" + barrages_ticks);
        if(is_barraged && !need1reload) {
            if (!NPC_NAME.getWorldLocation().equals(lastduslocation)) {
                log.info("The thing moved: " + NPC_NAME.getWorldLocation().getX() + "!=" + lastduslocation.getX());
                is_barraged = false;
                delaying = true;
                barragedelay = 5;
                barrages_ticks = 0;
            }
        }
        else
        {
            if (barragedelay > 1)
            {
                //Dont set barrage when your too early.
                return;
            }
            //Also check this again on BARRAGE ANIMATIOn 1679 or some id idk
            //NPC_NAME.getSpotAnimationFrame() MIGHT BE WRONG.
            if (NPC_NAME.getSpotAnimFrame() == ice_barrage) {
                need1reload = false;
                log.info("Barraging...");
                is_barraged = true;
                barrages_ticks = 0;
                lastduslocation = NPC_NAME.getWorldLocation();
            }
        }

    }
}