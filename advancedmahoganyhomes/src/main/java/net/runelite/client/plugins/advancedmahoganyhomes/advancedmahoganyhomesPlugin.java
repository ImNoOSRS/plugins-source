//Created by PluginCreated by ImNo: https://github.com/ImNoOSRS 
package net.runelite.client.plugins.advancedmahoganyhomes;

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
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Clipboard;
import net.runelite.client.util.QuantityFormatter;
import org.apache.commons.lang3.ObjectUtils;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

@Extension
@PluginDescriptor(
        name = "Advanced Mahogany Homes",
        description = "Mahogany Homes Assistant.",
        type = PluginType.SYSTEM
)
@Slf4j
public class advancedmahoganyhomesPlugin extends Plugin {
    // Injects our config
    @Inject
    private ConfigManager configManager;
    @Inject
    private advancedmahoganyhomesConfig config;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private advancedmahoganyhomesOverlay overlay;
    @Inject
    private advancedmahoganyhomesOverlayPanel overlaypanel;

    public int var_object1 = 10554;
    public int var_object2 = 10555;
    public int var_object3 = 10556;
    public int var_object4 = 10557;
    public int var_object5 = 10558;
    public int var_object6 = 10559;
    public int var_object7 = 10560;
    public int var_object8 = 10561;

    private final int npc_amy = 7417;
    private final int amy_region = 11828;
    public NPC amy;
    public NPC overlaynpc;

    //SEE PATTERN \/ same order per id hmmm
    private static final Set<Integer> object1_ids = Set.of(
            39981, 39989, 39997, 40002, 40007, 40011, 40083, 40156, 40164, 40171, 40296, 40297
    );

    private static final Set<Integer> object2_ids = Set.of(
            39982, 39990, 39998, 40008, 40084, 40089, 40095, 40157, 40172, 40165, 40287, 40293
    );

    private static final Set<Integer> object3_ids = Set.of(
            39983, 39991, 39999, 40003, 40012, 40085, 40090, 40096, 40158, 40173, 40166, 40290
    );

    private static final Set<Integer> object4_ids = Set.of(
            39984, 39992, 40000, 40086, 40091, 40097, 40159, 40167, 40174, 40288, 40291, 40294
    );

    private static final Set<Integer> object5_ids = Set.of(
            39985, 39993, 40009, 40013, 40087, 40092, 40175, 40160, 40168, 40286, 40298, 40004
    );

    private static final Set<Integer> object6_ids = Set.of(
            39986, 39994, 40001, 40005, 40010, 40014, 40088, 40093, 40098, 40161, 40169, 40176
    );

    private static final Set<Integer> object7_ids = Set.of(
            39987, 39995, 40006, 40015, 40094, 40099, 40162, 40170, 40177, 40292, 40295
    );

    private static final Set<Integer> object8_ids = Set.of(
            39996, 39988, 40163, 40289, 40299
    );


    public final Set<GameObject> object1 = new HashSet<>();
    public final Set<GameObject> object2 = new HashSet<>();
    public final Set<GameObject> object3 = new HashSet<>();
    public final Set<GameObject> object4 = new HashSet<>();
    public final Set<GameObject> object5 = new HashSet<>();
    public final Set<GameObject> object6 = new HashSet<>();
    public final Set<GameObject> object7 = new HashSet<>();
    public final Set<GameObject> object8 = new HashSet<>();


    public String get_furniture_state(int number)
    {
        switch(number)
        {
            case 1:
                return "NEEDS_REPAIR";
            case 3:
                return "NEEDS_REMOVE";
            case 2:
            case 8:
                return "FINISHED";
            case 4:
                return "NEEDS_REBUILD";

        }
        return "UNKNOWN";
    }

    @Provides
    advancedmahoganyhomesConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(advancedmahoganyhomesConfig.class);
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("mahoganyhomes"))
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

    private static final Set<Integer> REGION_IDS = Set.of(
            11828, 69698
    );

    private boolean isHomeArea()
    {
        return REGION_IDS.contains(client.getMapRegions()[0]);
    }

    public void load_data()
    {
        for (final NPC npc : client.getNpcs())
        {
            addNpc(npc);
        }

        final LocatableQueryResults<GameObject> locatableQueryResults = new GameObjectQuery().result(client);

        for (final GameObject gameObject : locatableQueryResults)
        {
            addGameObject(gameObject);
        }
    }

    @Override
    protected void startUp() {
        overlayManager.add(overlay);
        overlayManager.add(overlaypanel);

        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        load_data();
    }

    private void onGameObjectSpawned(GameObjectSpawned event)
    {
        addGameObject(event.getGameObject());
    }

    public void addGameObject(GameObject g)
    {
        int id = g.getId();
        if(object1_ids.contains(id))
        {
            if(!object1.contains(g))
            {
                object1.add(g);
            }
        }
        else if(object2_ids.contains(id))
        {
            if(!object2.contains(g))
            {
                object2.add(g);
            }
        }
        else if(object3_ids.contains(id))
        {
            if(!object3.contains(g))
            {
                object3.add(g);
            }
        }
        else if(object4_ids.contains(id))
        {
            if(!object4.contains(g))
            {
                object4.add(g);
            }
        }
        else if(object5_ids.contains(id))
        {
            if(!object5.contains(g))
            {
                object5.add(g);
            }
        }
        else if(object6_ids.contains(id))
        {
            if(!object6.contains(g))
            {
                object6.add(g);
            }
        }
        else if(object7_ids.contains(id))
        {
            if(!object7.contains(g))
            {
                object7.add(g);
            }
        }
        else if(object8_ids.contains(id))
        {
            if(!object8.contains(g))
            {
                object8.add(g);
            }
        }
    }

    private void onGameObjectDespawned(GameObjectDespawned event)
    {
        GameObject g = event.getGameObject();
        int id = g.getId();
        if(object1_ids.contains(id))
        {
            if(object1.contains(g))
            {
                object1.remove(g);
            }
        }
        else if(object2_ids.contains(id))
        {
            if(object2.contains(g))
            {
                object2.remove(g);
            }
        }
        else if(object3_ids.contains(id))
        {
            if(object3.contains(g))
            {
                object3.remove(g);
            }
        }
        else if(object4_ids.contains(id))
        {
            if(object4.contains(g))
            {
                object4.remove(g);
            }
        }
        else if(object5_ids.contains(id))
        {
            if(object5.contains(g))
            {
                object5.remove(g);
            }
        }
        else if(object6_ids.contains(id))
        {
            if(object6.contains(g))
            {
                object6.remove(g);
            }
        }
        else if(object7_ids.contains(id))
        {
            if(object7.contains(g))
            {
                object7.remove(g);
            }
        }
        else if(object8_ids.contains(id))
        {
            if(object8.contains(g))
            {
                object8.remove(g);
            }
        }
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(overlay);
        overlayManager.remove(overlaypanel);
        clear_data();
    }

    public void clear_data()
    {
        amy = null;
        overlaynpc = null;
        object1.clear();
        object2.clear();
        object3.clear();
        object4.clear();
        object5.clear();
        object6.clear();
        object7.clear();
        object8.clear();
    }

    @Subscribe
    private void onGameStateChanged(final GameStateChanged event)
    {
        clear_data();
        final GameState gameState = event.getGameState();
        switch (gameState)
        {
            case LOGGED_IN:
                break;
            case LOGIN_SCREEN:
                break;
            default:
                break;
        }
    }

    @Subscribe
    private void onNpcSpawned(final NpcSpawned event)
    {
        addNpc(event.getNpc());
    }

    private boolean gottask = false;

    public void addNpc(NPC npc)
    {
        switch(npc.getId())
        {
            case npc_amy:
                amy = npc;
                if(!gottask) {
                    client.setHintArrow(amy);
                }
                break;
        }


        if(!npc_to_overlay.isEmpty()) {
            if(npc.getName() != null) {
                if (npc.getName().equals(npc_to_overlay)) {
                    overlaynpc = npc;
                }
            }
        }
    }

    @Subscribe
    private void onNpcDespawned(final NpcDespawned event)
    {
        NPC current = event.getNpc();
        switch(current.getId())
        {
            case npc_amy:
                amy = null;
                break;
        }

        if(!npc_to_overlay.isEmpty()) {
            if(current.getName() != null) {
                if (current.getName().equals(npc_to_overlay)) {
                    overlaynpc = null;
                }
            }
        }
    }

    @Subscribe
    private void onWidgetLoaded(WidgetLoaded widgetLoaded)
    {
        if (widgetLoaded.getGroupId() == WidgetID.DIALOG_NPC_GROUP_ID)
        {
            clientThread.invokeLater(this::checkNpcDialog);
        }
    }

    public void checkNpcDialog()
    {
        Widget widgetDialogNpcText = client.getWidget(WidgetInfo.DIALOG_NPC_TEXT);

        if (widgetDialogNpcText == null)
        {
            return;
        }

        String text = widgetDialogNpcText.getText();
        process_task(text);
        //ADD CHECK FOR LOCATIONS HERE
    }

    @Subscribe
    private void onBeforeRender(final BeforeRender event) {
        if (this.client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
    }

    private int lastvar7 = 0;
    private boolean reload_next_tick = false;
    @Subscribe
    public void onGameTick(GameTick event) {
        if (this.client.getGameState() != GameState.LOGGED_IN) {
            clear_data();
            return;
        }

        if(!npc_to_find.equals(""))
        {
            for (final NPC npc : client.getNpcs())
            {
                if(npc != null) {
                    if (npc.getName() != null) {
                        if (npc.getName().equals(npc_to_find)) {
                            npc_to_find = "";
                            client.setHintArrow(npc);
                            break;
                        }
                    }
                }
            }
        }

        reload_next_tick = true;
        //Just always reload when shit spawns 0l0l
        if(reload_next_tick)
        {
            clear_data();
            load_data();
        }

        /*
        int newvar7 = client.getVarbitValue(var_object7);
        if(newvar7 != lastvar7)
        {
            clear_data();
            if(newvar7 != 0)
            {
                reload_next_tick = true;
                load_data();
            }
            lastvar7 = newvar7;
        }*/
    }

    String npc_to_find = "";
    String npc_to_overlay = "";
    public String location = "";
    public String npc = "NO CONTRACT";

    public boolean process_task(String message)
    {
        if(message.startsWith("Go see")) {
            npc = message.split("</col>")[0].split("<col=ff0000>")[1];
            npc_to_overlay = npc;
            location = message.split("</col>")[1].split("<col=ff0000>")[1];
            client.clearHintArrow();
            gottask = true;
            return true;
        }
        return false;
    }

    @Subscribe
    void onChatMessage(ChatMessage event) {
        if (event.getType() == ChatMessageType.GAMEMESSAGE || event.getType() == ChatMessageType.SPAM) {
            String message = event.getMessage();
            //log.info("MSGx:" + message);
            if(process_task(message))
            {
                return;
            }
            if(message.contains("contracts with a total of"))
            {
                client.clearHintArrow();
                npc = "NO CONTRACT";
                location = "FALADOR";
                npc_to_find = "";
                overlaynpc = null;
                gottask = false;
            }
            else if(message.contains("seems happy with your work"))
            {
                //log.info("MSG: " + message);
                String npcname = message.replace("<col=229628>", "").split(" ")[0].replace(",", "");
                log.info("Checking for name: " + npcname);
                npc_to_find = npcname;
                //Make it check for this NPC till found. on gameticks.
            }
        }
    }

}