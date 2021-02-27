package net.runelite.client.plugins.DenseEssenceOverlay;

import com.google.common.base.Splitter;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.kit.KitType;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseListener;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.QuantityFormatter;
import org.apache.commons.lang3.ObjectUtils;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Extension
@PluginDescriptor(
        name = "Dense Essence",
        description = "makes Runecrafting a little bit easier"
)
@Slf4j
public class DenseEssenceOverlayPlugin extends Plugin
{
    // Injects our config
    @Inject
    private DenseEssenceOverlayConfig config;
    @Inject
    private ConfigManager configManager;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private LapCounterOverlay lapCounterOverlay;
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private DenseEssenceOverlayOverlay overlay;

    @Getter(AccessLevel.PACKAGE)
    private int denseStone = -1;
    @Getter(AccessLevel.PACKAGE)
    private int EmptySpots = -1;
    @Getter(AccessLevel.PACKAGE)
    private int OtherItems = -1;
    @Getter(AccessLevel.PACKAGE)
    public boolean inventory_full = false;

    @Getter(AccessLevel.PACKAGE)
    private final Set<GameObject> denseObjects = new HashSet<>();

    private static final int RUNECRAFTING_ANIMATION = 791;
    int Dense_runestone = 8975;
    @Provides
    DenseEssenceOverlayConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(DenseEssenceOverlayConfig.class);
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("DenseEssenceOverlay"))
        {
            switch(event.getKey())
            {
                case "test":
                    break;
                default:
                    break;
            }
        }
    }


    @Getter(AccessLevel.PACKAGE)
    public boolean mining = false;


    public int start_exp_runecrafting = 0;

    @Subscribe
    public void onGameTick(GameTick event) {

        inarea = isInArea();
        if(!inarea)
        {
            denseObjects.clear();
            return;
        }
        if(start_exp_runecrafting == 0)
        {
            start_exp_runecrafting =client.getSkillExperience(Skill.RUNECRAFT);
        }
        //Checks full inventory
        CountItems();
    }

    
    private static final Set<Integer> REGION_IDS = Set.of(
            6715, 6459
    );

    private boolean isInArea()
    {
        return REGION_IDS.contains(client.getMapRegions()[0]);
    }

    public boolean inarea = false;

    public int current_rc_region = -1;


    @Subscribe
    private void onAnimationChanged(final AnimationChanged event)
    {
        Player local = client.getLocalPlayer();

        if (event.getActor() != local)
        {
            return;
        }

        int animId = local.getAnimation();
        if(animId == 8347 ||animId == 7139 || animId == 4482 || animId == 7201)
        {
            mining = true;
        }
        else {
            mining = false;
        }

        if(animId == 791)
        current_rc_region = local.getAnimation();
    }


    @Override
    protected void startUp() {
        final LocatableQueryResults<GameObject> locatableQueryResults = new GameObjectQuery().result(client);

        for (final GameObject gameObject : locatableQueryResults)
        {
            addGameObject(gameObject);
        }

        overlayManager.add(overlay);
        overlayManager.add(lapCounterOverlay);
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(overlay);
        overlayManager.remove(lapCounterOverlay);
    }

    @Subscribe
    private void onGameObjectSpawned(GameObjectSpawned event)
    {
        GameObject GO = event.getGameObject();
        addGameObject(GO);
    }

    @Subscribe
    private void onGameObjectDespawned(GameObjectDespawned event)
    {
        GameObject g = event.getGameObject();
        final int id = g.getId();
        if(id == Dense_runestone) {
            if(denseObjects.contains(g))
            {
                denseObjects.remove(g);
            }
        }

    }

    @Subscribe
    private void onGameStateChanged(final GameStateChanged event)
    {
        denseObjects.clear();

        if(event.getGameState() == GameState.LOGGED_IN)
        {
            current_rc_region = 0;
            final LocatableQueryResults<GameObject> locatableQueryResults = new GameObjectQuery().result(client);

            for (final GameObject gameObject : locatableQueryResults)
            {
                addGameObject(gameObject);
            }
        }
    }


    private final int denseessence = 13445;

    private void CountItems()
    {
        denseStone = -1;
        EmptySpots = -1;
        OtherItems = -1;
        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
        Item[] inventoryItems;

        if (inventory != null)
        {
            denseStone = 0;
            EmptySpots = 0;
            OtherItems = 0;
            inventoryItems = inventory.getItems();
            for (Item item : inventoryItems) {
                if (item.getId() == denseessence) {
                    denseStone++;
                } else {
                    if (item.getId() == -1) {
                        EmptySpots++;
                    } else {
                        OtherItems++;
                    }
                }
            }
        }

        inventory_full = EmptySpots == 0;
    }


    int leftstone = 8981;
    int rightstone = 10796;private static final int DENSE_RUNESTONE_SOUTH_ID = NullObjectID.NULL_10796;
    private void addGameObject(final GameObject gameObject) {
        final int id = gameObject.getId();
        if(id == leftstone || id == rightstone)
        {
            denseObjects.add(gameObject);
        }
    }
}