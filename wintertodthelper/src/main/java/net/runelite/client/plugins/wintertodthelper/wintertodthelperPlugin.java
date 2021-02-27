package net.runelite.client.plugins.wintertodthelper;

import com.google.common.base.Splitter;
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
import net.runelite.client.ui.overlay.infobox.Counter;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.QuantityFormatter;
import org.apache.commons.lang3.ObjectUtils;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.*;

@Extension
@PluginDescriptor(
        name = "Wintertoder Helper",
        description = "Helps with wintertodt, duh",
        tags = {"imno"}
)
@Slf4j
public class wintertodthelperPlugin extends Plugin
{
    // Injects our config
    @Inject
    private wintertodthelperConfig config;
    @Inject
    private ConfigManager configManager;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private ItemManager itemManager;

    @Inject
    private OverlayManager overlayManager;


    @Inject
    private wintertodthelperOverlay overlay;

    @Inject
    private CountOverlay count;

    @Getter(AccessLevel.PACKAGE)
    private final Set<GameObject> brumarootObjects = new HashSet<>();

    @Getter(AccessLevel.PACKAGE)
    private final Set<GameObject> brazierObjects = new HashSet<>();

    @Getter(AccessLevel.PACKAGE)
    private final Set<GameObject> brokenBrazierObjects = new HashSet<>();
    @Getter(AccessLevel.PACKAGE)
    private final Set<GameObject> unlitBrazierObjects = new HashSet<>();

    @Getter(AccessLevel.PACKAGE)
    private int NormalLogsCount = -1;

    @Getter(AccessLevel.PACKAGE)
    private int FletchedLogsCount = -1;

    @Getter(AccessLevel.PACKAGE)
    private int OtherItems = -1;

    @Getter(AccessLevel.PACKAGE)
    private int EmptySpots = -1;

    int brumaroot = 29311;
    int brazier = 29314;
    int brokenBrazier = 29313;
    int unlitBrazier = 29312;
    @Provides
    wintertodthelperConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(wintertodthelperConfig.class);
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("wintertodthelper"))
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

    final int burning_animation = 832;
    final int fletching_animation = 1248;

    @Getter(AccessLevel.PACKAGE)
    public boolean inventory_full = false;

    @Getter(AccessLevel.PACKAGE)
    public boolean chopping = false;

    @Getter(AccessLevel.PACKAGE)
    public boolean burning = false;

    @Getter(AccessLevel.PACKAGE)
    public boolean fletching = false;

    int burningoffset = 0;

    int wintertodtregion = 6205;

    @Getter(AccessLevel.PACKAGE)
    public int score = 0;

    @Getter(AccessLevel.PACKAGE)
    public boolean flash = false;

    public boolean isInWintertodt()
    {
        return client.getMapRegions()[0] == wintertodtregion;
    }

    public int timer = 0;

    @Subscribe
    public void onGameTick(GameTick event) {
        if(!isInWintertodt())
        {
            cleanup();
        }

        if(config.ignorewait())
        {
            timer = client.getVar(Varbits.WINTERTODT_TIMER);
        }

        if(config.blinkPointInInv()) {
            flash = !flash;
        }
        else
        {
            flash = false;
        }

        if(burning)
        {
            if(burningoffset == 0)
            {
                burning = false;
            }
            else {
                burningoffset--;
            }
        }
        CountItems();
        Widget scorewidget = client.getWidget(396, 7);
        if(scorewidget != null)
        {
            score = Integer.parseInt(scorewidget.getText().replace("<br>", "X").split("X")[1]);
        }
    }

    @Subscribe
    private void onAnimationChanged(final AnimationChanged event)
    {
        Player local = client.getLocalPlayer();

        if (event.getActor() != local)
        {
            return;
        }

        fletching = false;

        int animId = local.getAnimation();
        switch(animId)
        {
            case burning_animation:
                if(burningoffset == 0) {
                    burningoffset = 5;
                }
                else
                {
                    burningoffset = 3;
                }
                burning = true;
                break;
            case fletching_animation:
                fletching = true;
                break;
        }
        Axe axe = Axe.findAxeByAnimId(animId);
        if (axe != null)
        {
            chopping = true;
        }
        else
        {
            chopping = false;
        }
    }

    public final String BURNING_STOPPED_1 = "You have run out of bruma roots";
    public final String BURNING_STOPPED_2 = "The brazier is broken and shrapnel";
    public final String BURNING_STOPPED_3 = "You fix the brazier";
    public final String BURNING_STOPPED_4 = "The brazier has gone out.";
    public final String BURNING_STOPPED_5 = "You light the brazier";

    public final String CHOPPING_STOPPED_1 = "Your inventory is too full";

    @Subscribe
    private void onChatMessage(final ChatMessage message)
    {
        if(!isInWintertodt())
        {
            return;
        }
        ChatMessageType chatMessageType = message.getType();

        if (chatMessageType != ChatMessageType.GAMEMESSAGE && chatMessageType != ChatMessageType.SPAM)
        {
            return;
        }

        final String msg = message.getMessage().replace("<col=00ffff>", "");
        //log.info("MSG: " + msg);
        if(msg.startsWith("The cold of"))
        {
            burning = false;
            return;

        }
        switch (msg)
        {
            case BURNING_STOPPED_1:
            case BURNING_STOPPED_2:
            case BURNING_STOPPED_3:
            case BURNING_STOPPED_4:
            case BURNING_STOPPED_5:
                burning = false;
                burningoffset = 0;
                break;
            case CHOPPING_STOPPED_1:
                chopping = false;
                break;
            default:
                break;
        }
    }

    private final int brumalogs = 20695;
    private final int brumalogs_fletched = 20696;

    private void CountItems()
    {
        NormalLogsCount = -1;
        FletchedLogsCount = -1;
        EmptySpots = -1;
        OtherItems = -1;
        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
        Item[] inventoryItems;

        if (inventory != null)
        {
            NormalLogsCount = 0;
            FletchedLogsCount = 0;
            EmptySpots = 0;
            OtherItems = 0;
            inventoryItems = inventory.getItems();
            for (Item item : inventoryItems) {
                if (item.getId() == brumalogs) {
                    NormalLogsCount++;
                } else if (item.getId() == brumalogs_fletched) {
                    FletchedLogsCount++;
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

    @Override
    protected void startUp() {
        if (client.getGameState() == GameState.LOGGED_IN) {
            if (isInWintertodt()) {
                final LocatableQueryResults<GameObject> locatableQueryResults = new GameObjectQuery().result(client);

                for (final GameObject gameObject : locatableQueryResults) {
                    addGameObject(gameObject);
                }
            }
        }

        overlayManager.add(overlay);
        overlayManager.add(count);
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(overlay);
        overlayManager.remove(count);
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
        if(id == brumaroot) {
            if(brumarootObjects.contains(g))
            {
                brumarootObjects.remove(g);
            }
        }
        else if(id == brazier)
        {
            brazierObjects.remove(g);
        }
        else if(id == brokenBrazier)
        {
            brokenBrazierObjects.remove(g);
        }
        else if(id == unlitBrazier)
        {
            unlitBrazierObjects.remove(g);
        }
    }

    public void cleanup()
    {
        brumarootObjects.clear();
        brazierObjects.clear();
        brokenBrazierObjects.clear();
        unlitBrazierObjects.clear();
        NormalLogsCount = -1;
        FletchedLogsCount = -1;
        OtherItems = -1;
        EmptySpots = -1;

        inventory_full = false;
        chopping = false;
        burning = false;
        fletching = false;
        burningoffset = 0;
        score = 0;
    }

    public int TotalLogs()
    {
        return NormalLogsCount + FletchedLogsCount;
    }

    public int TotalLogPoints()
    {
        return (NormalLogsCount * 10) + (FletchedLogsCount * 25);
    }

    @Subscribe
    private void onGameStateChanged(final GameStateChanged event)
    {
        cleanup();

        if(event.getGameState() == GameState.LOGGED_IN)
        {
            final LocatableQueryResults<GameObject> locatableQueryResults = new GameObjectQuery().result(client);

            for (final GameObject gameObject : locatableQueryResults)
            {
                addGameObject(gameObject);
            }
        }
    }

    private void addGameObject(final GameObject gameObject) {
        final int id = gameObject.getId();
        if(id == brumaroot)
        {
            brumarootObjects.add(gameObject);
        }
        else if(id == brazier)
        {
            brazierObjects.add(gameObject);
        }
        else if(id == brokenBrazier)
        {
            brokenBrazierObjects.add(gameObject);
        }
        else if(id == unlitBrazier)
        {
            unlitBrazierObjects.add(gameObject);
        }
    }
}