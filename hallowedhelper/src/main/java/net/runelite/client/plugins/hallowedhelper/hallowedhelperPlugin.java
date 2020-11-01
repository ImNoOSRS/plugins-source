package net.runelite.client.plugins.hallowedhelper;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.queries.GroundObjectQuery;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.*;

@Extension
@PluginDescriptor(
        name = "<html>Hallowed <font size=\"\" color=\"red\"<b>BETA</font></b></html>",
        description = "Helps with hallowed.",
        type = PluginType.MISCELLANEOUS
)
@Slf4j
public class hallowedhelperPlugin extends Plugin {

    private static final String GAME_MESSAGE_ENTER_LOBBY1 = "You make your way back to the lobby of the Hallowed Sepulchre.";
    private static final String GAME_MESSAGE_ENTER_LOBBY2 = "The obelisk teleports you back to the lobby of the Hallowed Sepulchre.";
    private static final String GAME_MESSAGE_ENTER_SEPULCHRE = "You venture further down into the Hallowed Sepulchre.";
    private static final String GAME_MESSAGE_DOOR_CLOSES = "<col=ef1020>You hear a loud rumbling noise as the door to the next floor closes.";
    private static final String GAME_MESSAGE_DOOR_CLOSES2 = "<col=ef1020>You hear the sound of a magical barrier activating.";

    private int ticksleftvar = 10392;

    @Getter(AccessLevel.PACKAGE)
    public int currentfloor = -1;

    @Getter(AccessLevel.PACKAGE)
    public int subfloor = -1;

    @Getter(AccessLevel.PACKAGE)
    public int ticksleft = -1;

    public void getFloor()
    {
        //log.info("Getting floor...: " + client.getMapRegions()[0]);
        switch(client.getMapRegions()[0])
        {
            case 8796:
                currentfloor = 1;
                subfloor = 1;
                break;
            case 8797:
                currentfloor = 1;
                subfloor = 2;
                break;
            case 9052:
                currentfloor = 1;
                subfloor = 3;
                break;
            case 9820:
            case 9821://2nd sub maby?
                currentfloor = 2;
                subfloor = 1;
                break;
            case 9306:
                currentfloor = 3;
                subfloor = 1;
                get_third_floor_sub();
                break;
            case 9818:
                currentfloor = 4;
                subfloor = 1;
                get_fourth_floor();
                break;
            case 8794:
                currentfloor = 5;
                subfloor = 1;
                subfloor = 2;
                break;
            default:
                currentfloor = -1;
                subfloor = -1;
                break;
        }

        if(currentfloor == 1 && bridges.size() > 0)
        {
            GroundObject bridge = bridges.iterator().next();

            int xdistance = (bridge.getX() - client.getLocalPlayer().getLocalLocation().getX());
            if(xdistance > -1408) {// moeter lager voor de deur bij A1
                currentfloor = 1;
                subfloor = 3;
                return;
            }
        }
    }

    public void get_third_floor_sub()
    {
        if(floor_gates.size() == 0)
        {
            subfloor = 0;
            return;
        }
        GameObject oj = floor_gates.iterator().next();
        LocalPoint localPoint = oj.getLocalLocation();
        int offset = localPoint.getX() - client.getLocalPlayer().getLocalLocation().getX();

        if(offset > 640) {
            subfloor = 0;//LEFT
            return;
        }
        else if(offset < -640)
        {
            subfloor = 1;//MID
            return;
        }
        else
        {
            subfloor = 2;//MID
            return;
        }
    }

    public void get_fourth_floor()
    {
        if(client.getLocalPlayer().getLocalLocation().getY() > 6720)
        {
            subfloor = 0;//down
        }
        else if(client.getLocalPlayer().getLocalLocation().getY() < 1152)
        {
            subfloor = 1;//up
        }
        else
        {
            subfloor = 2;//MID
        }
    }


    @Inject
    private ConfigManager configManager;
    @Inject
    private Client client;
    @Inject
    private hallowedhelperConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private hallowedhelperOverlay hallowedhelperoverlay;
    @Inject
    private hallowedhelperInfoPanel infoPanel;
    @Inject
    private InfoBoxManager infoBoxManager;

    @Getter(AccessLevel.PACKAGE)
    private boolean doorOpen = true;

    @Getter(AccessLevel.PACKAGE)
    private final Set<HallowedSepulchreSword> swords = new HashSet<>();

    @Getter(AccessLevel.PACKAGE)
    private final Set<NPC> arrows = new HashSet<>();

    @Getter(AccessLevel.PACKAGE)
    private final Set<GameObject> chests = new HashSet<>();

    @Getter(AccessLevel.PACKAGE)
    private final Set<GroundObject> bridges = new HashSet<>();

    @Getter(AccessLevel.PACKAGE)
    private final Set<GameObject> portals = new HashSet<>();

    @Getter(AccessLevel.PACKAGE)
    private final Set<GameObject> stairs = new HashSet<>();

    @Getter(AccessLevel.PACKAGE)
    private final Set<GameObject> floor_gates = new HashSet<>();

    @Getter(AccessLevel.PACKAGE)
    private final Set<GameObject> crossbowStatues = new HashSet<>();

    @Getter(AccessLevel.PACKAGE)
    private final Set<GameObject> swordStatues = new HashSet<>();

    @Getter(AccessLevel.PACKAGE)
    private final Set<LocalPoint> lightningboltlocations = new HashSet<>();

    @Getter(AccessLevel.PACKAGE)
    private int tickssincelightning = 0;

    @Getter(AccessLevel.PACKAGE)
    private final Set<HallowedSepulchreWizardStatue> wizardStatues = new HashSet<>();

    public GameObject Floor4WizardLeft;
    public GameObject Floor4WizardRight;
    public GameObject Floor4WizardBottom1;
    public GameObject Floor4WizardBottom2;
    public GameObject Floor4WizardBottom3;

    @Getter(AccessLevel.PACKAGE)
    private boolean playerInSepulchre = false;


    private static final int ANIM_TICK_SPEED_2 = 2;
    private static final int ANIM_TICK_SPEED_3 = 3;

    private static final int WIZARD_STATUE_ANIM_FIRE = 8658;

    private static final int LIGHTNING_ID = 1796;

    private static final Set<Integer> SWORD_STATUES = Set.of(
            38428,//Floor2
            ObjectID.KNIGHT_STATUE_38429,
            ObjectID.KNIGHT_STATUE_38430,
            ObjectID.KNIGHT_STATUE_38431,
            ObjectID.KNIGHT_STATUE_38432,
            ObjectID.KNIGHT_STATUE_38435,//RUNE Floor4 maby 5
            ObjectID.KNIGHT_STATUE_38437,
            ObjectID.KNIGHT_STATUE_38438,
            ObjectID.KNIGHT_STATUE_38439,
            ObjectID.KNIGHT_STATUE_38436,//Floor2?
            ObjectID.KNIGHT_STATUE_38440,
            ObjectID.KNIGHT_STATUE_38441,//Floor 4 maby 5 too.
            ObjectID.KNIGHT_STATUE_38443//Floor 4 maby 5 too.
            // hierboven breekt de sowrd ofzo
    );

    private static final Set<Integer> CROSSBOWMAN_STATUE_IDS = Set.of(
            ObjectID.CROSSBOWMAN_STATUE,
            ObjectID.CROSSBOWMAN_STATUE_38445,
            ObjectID.CROSSBOWMAN_STATUE_38446
    );

    private static final Set<Integer> WIZARD_STATUE_2TICK_IDS = Set.of(
            ObjectID.WIZARD_STATUE_38421,
            ObjectID.WIZARD_STATUE_38422,
            ObjectID.WIZARD_STATUE_38423,
            ObjectID.WIZARD_STATUE_38424,
            ObjectID.WIZARD_STATUE_38425
    );

    private static final Set<Integer> WIZARD_STATUE_3TICK_IDS = Set.of(
            ObjectID.WIZARD_STATUE,
            ObjectID.WIZARD_STATUE_38410,
            ObjectID.WIZARD_STATUE_38411,
            ObjectID.WIZARD_STATUE_38412,
            ObjectID.WIZARD_STATUE_38416,
            ObjectID.WIZARD_STATUE_38417,
            ObjectID.WIZARD_STATUE_38418,
            ObjectID.WIZARD_STATUE_38419,
            ObjectID.WIZARD_STATUE_38420
    );

    private static final Set<Integer> SWORD_IDS = Set.of(
            NullNpcID.NULL_9669,
            NullNpcID.NULL_9670,
            NullNpcID.NULL_9671
    );


    private static final Set<Integer> ARROW_IDS = Set.of(
            NullNpcID.NULL_9672,
            NullNpcID.NULL_9673,
            NullNpcID.NULL_9674
    );

    private final int bridge_id = 39527;
    private final int portal_id = 39533;
    private static final Set<Integer> STAIRS_IDS = Set.of(
            38462,
            38464,
            38465,
            38466,
            38467,
            38468,
            38469,
            38471,
            38472
    );

    private static final Set<Integer> FLOOR_GATE_IDS = Set.of(
            39622,
            39623,
            39624
    );

    private static final Set<Integer> CHEST_IDS = Set.of(
            39544,
            39545
    );


    @Subscribe
    private void onNpcSpawned(final NpcSpawned event)
    {
        if (!playerInSepulchre)
        {
            return;
        }

        addNpc(event.getNpc());
    }

    @Subscribe
    private void onNpcDespawned(final NpcDespawned event)
    {
        if (!playerInSepulchre)
        {
            return;
        }

        removeNpc(event.getNpc());
    }


    private void addNpc(final NPC npc)
    {
        final int id = npc.getId();

        if (ARROW_IDS.contains(id))
        {
            arrows.add(npc);
        }
        else if (SWORD_IDS.contains(id))
        {
            swords.add(new HallowedSepulchreSword(npc));
        }
    }

    private void removeNpc(final NPC npc)
    {
        final int id = npc.getId();

        if (ARROW_IDS.contains(id))
        {
            arrows.remove(npc);
        }
        else if (SWORD_IDS.contains(id))
        {
            //log.info("REMOVE SWORD");
            for(HallowedSepulchreSword sword : swords) {
                if(sword.getNpc() == npc) {
                    swords.remove(sword);
                    break;
                }
            }
        }
    }

    private void addGroundObject(final GroundObject groundObject)
    {
        final int id = groundObject.getId();
        if(id == bridge_id)
        {
            bridges.add(groundObject);
        }
    }

    @Getter(AccessLevel.PACKAGE)
    private int animation = -1;

    @Getter(AccessLevel.PACKAGE)
    private int graphic = -1;

    boolean waitforspot = false;
    @Subscribe
    private void onAnimationChanged(final AnimationChanged event)
    {
        if(!playerInSepulchre)
        {
            return;
        }
        Player local = client.getLocalPlayer();

        if (event.getActor() != local)
        {
            return;
        }

        //EFFECT == 267 = pOINSON VAN CHEST
        animation = local.getAnimation();
        if(config.GlitchyHit()) {
            //3422 = DIVE DOWN COOL
            if (animation == 1816) {
                event.getActor().setAnimation(653);//666 hoofd buggy hh//653 expload//
                waitforspot = true;
            }
            else if(animation == 836)
            {
                event.getActor().setAnimation(653);
                client.getLocalPlayer().setSpotAnimation(137);
            }
            //439 = RONDJE DRAAIE
        }
        if(config.GlitchyGrapple())
        {
            if(animation == 6067)
            {
                event.getActor().setAnimation(6106);//of 6070//6106=auto of 6098
            }
        }
        //log.info("Anim changed: " + animation);
    }


    private static final Set<Integer> REGION_IDS = Set.of(
            8794, 8795, 8796, 8797, 8798,
            9050, 9051, 9052, 9053, 9054,
            9306, 9307, 9308, 9309, 9310,
            9562, 9563, 9564, 9565, 9566,
            9818, 9819, 9820, 9821, 9822,
            10074, 10075, 10076, 10077, 10078,
            10330, 10331, 10332, 10333, 10334
    );


    private boolean isInSepulchreRegion()
    {
        if(expired)
        {
            log.info("Beta Expired.");
            return false;
        }
        return REGION_IDS.contains(client.getMapRegions()[0]);
    }


    private void locateSepulchreGameObjects()
    {
        final LocatableQueryResults<GameObject> locatableQueryResults = new GameObjectQuery().result(client);

        for (final GameObject gameObject : locatableQueryResults)
        {
            addGameObject(gameObject);
        }

        final LocatableQueryResults<GroundObject> groundObjectResults = new GroundObjectQuery().result(client);

        for (final GroundObject groundObject : groundObjectResults)
        {
            addGroundObject(groundObject);
        }

        for (final GraphicsObject graphicsObject : client.getGraphicsObjects())
        {
            addgraphicsobject(graphicsObject);
        }


        for (final NPC npc : client.getNpcs())
        {
            addNpc(npc);
        }
        //get_all_objects_test();
    }

    private void addgraphicsobject(GraphicsObject object)
    {
        if(object.getId() == LIGHTNING_ID) {
            tickssincelightning = 0;
            LocalPoint current = object.getLocation();
            if(!lightningboltlocations.contains(current))
            {
                lightningboltlocations.add(current);
            }
        }
    }

    @Subscribe
    private void onGraphicsObjectCreated(GraphicsObjectCreated g)
    {
        //log.info("Graphics object created: " + g.getGraphicsObject().getId());
        if(g.getGraphicsObject().getId() == LIGHTNING_ID)
        {
            addgraphicsobject(g.getGraphicsObject());
        }
    }

    @Subscribe
    private void onChatMessage(final ChatMessage message)
    {
        if (!playerInSepulchre || message.getType() != ChatMessageType.GAMEMESSAGE)
        {
            return;
        }

        switch (message.getMessage())
        {
            case GAME_MESSAGE_DOOR_CLOSES:
            case GAME_MESSAGE_DOOR_CLOSES2:
                doorOpen = false;
                break;
            case GAME_MESSAGE_ENTER_LOBBY1:
            case GAME_MESSAGE_ENTER_LOBBY2:
                clearSepulchreGameObjects();
                break;
            case GAME_MESSAGE_ENTER_SEPULCHRE:
                doorOpen = true;
                if (!overlayManager.anyMatch(o -> o instanceof hallowedhelperOverlay))
                {
                    overlayManager.add(hallowedhelperoverlay);
                }
                if (!overlayManager.anyMatch(o -> o instanceof hallowedhelperInfoPanel))
                {
                    overlayManager.add(infoPanel);
                }
                break;
            default:
                break;
        }
    }

    public void clearSepulchreGameObjects()
    {
        Floor4WizardLeft = null;
        Floor4WizardRight = null;
        Floor4WizardBottom1 = null;
        Floor4WizardBottom2 = null;
        Floor4WizardBottom3 = null;
        floor_4_first_fire_detected = false;
        floor_5_first_fire_detected = false;
        floor5_2A_first_fire_detected = false;
        floor_5_4_first_fire_detected = false;
        Floor5TopLeft = null;
        Floor5BottomLeft = null;
        Floor5_2ATopLeft = null;
        Floor5_2AOneUnderTopLeft = null;
        Floor5_4BottomLeft = null;
        Floor5_4BottomRight = null;
        swords.clear();
        arrows.clear();
        chests.clear();
        bridges.clear();
        portals.clear();
        floor_gates.clear();
        wizardStatues.clear();
        crossbowStatues.clear();
        swordStatues.clear();
        stairs.clear();
        lightningboltlocations.clear();
        lastplane = 2;
        reload_next_tick = false;
        delayed_reload_wait = 3;
    }


    @Provides
    hallowedhelperConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(hallowedhelperConfig.class);
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("hallowedhelper"))
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

    public boolean expired = false;
    @Override
    protected void startUp() {
        Rotation.init(config.SafeTileColor(), config.UnsafeTileColor(), Color.BLUE, new Color(255, 102, 0));
        LocalDateTime l = LocalDateTime.now();
        if(l.getYear() > 2021)
        {
            expired = true;
        }
        else
        {
            if(l.getYear() == 2021 && l.getMonth().getValue() > 3)
            {
                expired = true;
            }
        }
        /*
        for (Map.Entry<Integer, Object> entry : client.getVarcMap().entrySet()) {
            log.info("INT:" + entry.getKey() + ",VAL:" + entry.getValue());
        }*/
        if (client.getGameState() != GameState.LOGGED_IN || !isInSepulchreRegion())
        {
            return;
        }
        getFloor();
        overlayManager.add(hallowedhelperoverlay);
        log.info("Adding panel...");
        overlayManager.add(infoPanel);
        playerInSepulchre = true;
        locateSepulchreGameObjects();
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(hallowedhelperoverlay);
        overlayManager.remove(infoPanel);
        clearSepulchreGameObjects();
        playerInSepulchre = false;
    }

    @Subscribe
    private void onGameStateChanged(final GameStateChanged event)
    {
        final GameState gameState = event.getGameState();;
        switch (gameState)
        {
            case LOGGED_IN:
                if (isInSepulchreRegion())
                {
                    playerInSepulchre = true;
                    getFloor();
                    lastplane = 2;
                }
                else if (playerInSepulchre)
                {
                    shutDown();
                }
                break;
            case LOGIN_SCREEN:
                if (playerInSepulchre)
                {
                    shutDown();
                }
                break;
            default:
                if (playerInSepulchre)
                {
                    clearSepulchreGameObjects();
                    getFloor();
                    doorOpen = true;
                }
                break;
        }
    }

    public int floor4_fire_rotation = 4;
    public int floor5_fire_rotation = 1;
    public int floor5_2A_fire_rotation = 1;
    public int floor5_4_fire_rotation = 1;

    private int lastplane = 2;

    private boolean reload_next_tick = false;
    private int delayed_reload_wait = 3;

    @Subscribe
    public void onGameTick(GameTick event) {
        if(!playerInSepulchre)
        {
            return;
        }

        if(reload_next_tick)
        {
            delayed_reload_wait--;
            log.info("Reloading in : " + delayed_reload_wait);
            if(delayed_reload_wait == 0) {
                log.info("Reloading");
                lightningboltlocations.clear();
                //HOTFIX FLOOR 5 STATUES.
                clearSepulchreGameObjects();
                lastplane = client.getPlane();
                locateSepulchreGameObjects();
                reload_next_tick = false;
            }
            else
            {
                return;
            }
        }

        if(currentfloor == 3)
        {
            get_third_floor_sub();
        }
        else if(currentfloor == 4) {
            if(client.getPlane() != lastplane)
            {
                lightningboltlocations.clear();
                lastplane = client.getPlane();
            }
            update_floor4_statues();
        }
        else if(currentfloor == 5){
            if(client.getPlane() != lastplane)
            {
                log.info("Reloading next tick");
                reload_next_tick = true;
                delayed_reload_wait = 3;
            }

            if(client.getPlane() == 2) {
                update_floor5_statues();
            }
            else if(client.getPlane() == 1) {
                update_floor5_2A_statues();
            }
            else if(client.getPlane() == 0)
            {
                update_floor5_4_statues();
            }
        }
        tickssincelightning++;
        ticksleft = client.getVarbitValue(ticksleftvar);
        doorOpen = (ticksleft != 1);

        graphic = client.getLocalPlayer().getSpotAnimation();
        if(waitforspot)
        {
            if(client.getLocalPlayer().getSpotAnimation() == 1805) {
                client.getLocalPlayer().setSpotAnimation(137);
                waitforspot = false;
            }
        }
        updateStatues();
    }

    //GETTING ROTATION
    //FLOOR 4
    public void update_floor4_statues()
    {
        if(Floor4WizardLeft == null) {
            return;
        }
        if(!floor_4_first_fire_detected) {
            if (!isfiring(Floor4WizardBottom1) && !isfiring(Floor4WizardBottom2)) {
                return;
            }
        }
        boolean leftfire = false;
        boolean rightfire = false;
        boolean last_first_fire = floor_4_first_fire_detected;
        if(Floor4WizardLeft != null) {
            if(isfiring(Floor4WizardLeft))
            {
                floor_4_first_fire_detected = true;
                leftfire = true;
            }
        }

        if(Floor4WizardRight != null) {
            if(isfiring(Floor4WizardRight))
            {
                floor_4_first_fire_detected = true;
                rightfire = true;
            }
        }

        floor_4_ticks_since_statue++;
        if(floor_4_ticks_since_statue == 8)
        {
            floor4_fire_rotation++;
            floor_4_ticks_since_statue = 0;
        }

        if(floor4_fire_rotation == 7)
        {
            floor4_fire_rotation = 1;
        }

        if(!last_first_fire)
        {
            if(isfiring(Floor4WizardBottom1) && isfiring(Floor4WizardBottom2))
            {
                if(isfiring(Floor4WizardBottom3)) {
                    floor4_fire_rotation = 2;
                    floor4_fire_rotation = 1;
                    floor_4_ticks_since_statue = 4;
                }
                else
                {
                    floor4_fire_rotation = 1;
                    floor4_fire_rotation = 6;
                    floor_4_ticks_since_statue = 4;
                }
            }
            else {

                if (leftfire) {
                    floor4_fire_rotation = 3;
                    floor_4_ticks_since_statue = 0;
                    //HOTFIX
                    floor4_fire_rotation = 2;
                    floor_4_ticks_since_statue = 4;
                } else if (rightfire) {
                    floor4_fire_rotation = 4;
                    floor_4_ticks_since_statue = 4;
                    //HOTFIX
                    floor4_fire_rotation = 3;
                    floor_4_ticks_since_statue = 4;
                }
            }
        }
            if(leftfire && rightfire) {
                if(floor4_fire_rotation != 6) {

                    floor4_fire_rotation = 6;
                    floor_4_ticks_since_statue = 4;
            }
        }
    }
    //FLOOR 5 - PLANE 2
    public void update_floor5_statues()
    {
        if(Floor5TopLeft == null) {
            return;
        }
        if(Floor5BottomLeft == null) {
            return;
        }
        if(!floor_5_first_fire_detected) {
            if(isfiring(Floor5BottomLeft))
            {
                floor5_fire_rotation = 1;
                floor_5_ticks_since_statue = -4;
                floor_5_first_fire_detected = true;
            }
            else if(isfiring(Floor5TopLeft))
            {
                floor5_fire_rotation = 2;
                floor_5_ticks_since_statue = -4;
                floor_5_first_fire_detected = true;
            }
        }
        floor_5_ticks_since_statue++;
        if(floor_5_ticks_since_statue == 7)
        {
            floor5_fire_rotation++;
            floor_5_ticks_since_statue = 0;
        }

        if(floor5_fire_rotation == 3)
        {
            floor5_fire_rotation = 1;
        }
    }
    //FLOOR 5 - PLANE 1
    public void update_floor5_2A_statues()
    {
        if(Floor5_2ATopLeft == null) {
            return;
        }
        if(Floor5_2AOneUnderTopLeft == null)
        {
            return;
        }
        if(!floor5_2A_first_fire_detected) {
            if(isfiring(Floor5_2ATopLeft) || isfiring(Floor5_2AOneUnderTopLeft))
            {
                if(isfiring(Floor5_2ATopLeft) && isfiring(Floor5_2AOneUnderTopLeft)) {
                    floor5_2A_fire_rotation = 1;
                }
                else
                {
                    floor5_2A_fire_rotation = 2;
                }
                floor5_2A_ticks_since_statue = 3;
                floor5_2A_first_fire_detected = true;
            }
        }


        floor5_2A_ticks_since_statue++;
        if(floor5_2A_ticks_since_statue == 7)
        {
            floor5_2A_fire_rotation++;
            floor5_2A_ticks_since_statue = 0;
        }

        if(floor5_2A_fire_rotation == 3)
        {
            floor5_2A_fire_rotation = 1;
        }
    }


    //FLOOR 5 - PLANE 0
    public void update_floor5_4_statues()
    {
        if(floor5_4BottomLeft == null) {
            return;
        }
        if(floor5_4Bottomright == null) {
            return;
        }
        if(!floor_5_4_first_fire_detected) {
            if(isfiring(floor5_4BottomLeft))
            {
                floor5_4_fire_rotation = 1;
                floor_5_4_ticks_since_statue = -3;
                floor_5_4_first_fire_detected = true;
            }
            else if(isfiring(floor5_4Bottomright))
            {
                floor5_4_fire_rotation = 2;
                floor_5_4_ticks_since_statue = -3;
                floor_5_4_first_fire_detected = true;
            }
        }
        floor_5_4_ticks_since_statue++;
        if(floor_5_4_ticks_since_statue == 7)
        {
            floor5_4_fire_rotation++;
            floor_5_4_ticks_since_statue = 0;
        }

        if(floor5_4_fire_rotation == 3)
        {
            floor5_4_fire_rotation = 1;
        }
    }


    public boolean isfiring(GameObject g)
    {
        final DynamicObject dynamicObject = (DynamicObject) g.getEntity();
        return dynamicObject.getAnimationID() == WIZARD_STATUE_ANIM_FIRE;
    }


    public int identifygameojectbylocalocation(GameObject sword)
    {
        return sword.getLocalLocation().getX() + sword.getLocalLocation().getY();
    }

    private void updateStatues()
    {
        if (!wizardStatues.isEmpty())
        {
            for (final HallowedSepulchreWizardStatue wizardStatue : wizardStatues)
            {
                wizardStatue.updateTicksUntilNextAnimation();
            }
        }

        if (!swords.isEmpty())
        {
            for (final HallowedSepulchreSword sword : swords)
            {
                if(sword.getThrower() == null)
                {
                    GameObject thrower = get_sword_thrower(sword.getNpc());
                    if(thrower == null)
                    {
                        continue;
                    }
                    sword.setThrower(thrower);
                }
                sword.updateState();
            }
        }
    }

    public GameObject get_sword_thrower(NPC sword)
    {
        int orientation = (sword.getOrientation() / 512);
        for (final GameObject gameObject  : swordStatues) {
            if(gameObject.getPlane() != sword.getWorldLocation().getPlane())
            {
                continue;
            }
            switch(orientation)
            {
                case 0:
                case 2:
                    if(Math.abs(gameObject.getWorldLocation().getX() - sword.getWorldLocation().getX()) < 4)
                    {
                        if(Math.abs(gameObject.getWorldLocation().getY() - sword.getWorldLocation().getY()) < 20) {
                            return gameObject;
                        }
                    }
                    break;
                case 1:
                case 3:
                    if(Math.abs(gameObject.getWorldLocation().getY() - sword.getWorldLocation().getY()) < 4)
                    {
                        if(Math.abs(gameObject.getWorldLocation().getX() - sword.getWorldLocation().getX()) < 20) {
                            return gameObject;
                        }
                    }
                    break;
            }
        }
        return null;
    }

    @Subscribe
    private void onGameObjectSpawned(GameObjectSpawned event)
    {
        GameObject GO = event.getGameObject();
        addGameObject(GO);
    }

    private void addGameObject(final GameObject gameObject)
    {
        final int id = gameObject.getId();
        //Entity entity = gameObject.getEntity();

        if (CROSSBOWMAN_STATUE_IDS.contains(id))
        {
            crossbowStatues.add(gameObject);
        }
        else if (SWORD_STATUES.contains(id))
        {
            swordStatues.add(gameObject);
        }
        else if (WIZARD_STATUE_2TICK_IDS.contains(id))
        {
            if(currentfloor == 5)
            {
                LocalPoint twp = gameObject.getLocalLocation();
                if(client.getPlane() == 2)
                {
                    if(FloorParts.FLOOR5_1.isinarea(twp))
                    {
                        Process_Floor_5_WizardSpot(gameObject);
                        return;
                    }
                }
                else if(client.getPlane() == 1) {
                    if (FloorParts.FLOOR5_2.isinarea(twp)) {
                        Process_Floor5_2A_WizardSpot(gameObject);
                        return;
                    }
                    if (FloorParts.FLOOR5_3.isinarea(twp)) {
                        //Dont do anything.
                        return;
                    }
                }
                else if(client.getPlane() == 0) {
                    if (FloorParts.FLOOR5_4.isinarea(twp)) {
                        Process_Floor_5_4_WizardSpot(gameObject);
                        return;
                    }
                    if (FloorParts.FLOOR5_5.isinarea(twp)) {
                        //Dont do anything.
                        return;
                    }
                }
            }
            wizardStatues.add(new HallowedSepulchreWizardStatue(gameObject, WIZARD_STATUE_ANIM_FIRE, ANIM_TICK_SPEED_2));
        }
        else if (WIZARD_STATUE_3TICK_IDS.contains(id))
        {
            if(currentfloor == 4)
            {
                LocalPoint twp = gameObject.getLocalLocation();
                if(FloorParts.FLOOR4_1.isinarea(twp))
                {
                    Process_Floor_4_WizardSpot(gameObject);
                    return;
                }
            }
            wizardStatues.add(new HallowedSepulchreWizardStatue(gameObject, WIZARD_STATUE_ANIM_FIRE, ANIM_TICK_SPEED_3));
        }
        else if(CHEST_IDS.contains(id))
        {
            if(!chests.contains(gameObject)) {
                chests.add(gameObject);
            }
        }
        else if(FLOOR_GATE_IDS.contains(id))
        {
            if(!floor_gates.contains(gameObject)) {
                floor_gates.add(gameObject);
            }
        }
        else if(STAIRS_IDS.contains(id))
        {
            if(!stairs.contains(gameObject)) {
                stairs.add(gameObject);
            }
        }

        switch(id)
        {
            case portal_id:
                portals.add(gameObject);
                break;
        }

    }

    @Subscribe
    private void onGameObjectDespawned(GameObjectDespawned event)
    {
        GameObject gameobject = event.getGameObject();
        int id = gameobject.getId();
        if (CROSSBOWMAN_STATUE_IDS.contains(id))
        {
            crossbowStatues.remove(gameobject);
        }
        else if (SWORD_STATUES.contains(id))
        {
            swordStatues.remove(gameobject);
        }
        else if (WIZARD_STATUE_2TICK_IDS.contains(id))
        {
            for(HallowedSepulchreWizardStatue statue : wizardStatues) {
                if(statue.getGameObject() == gameobject) {
                    wizardStatues.remove(statue);
                    break;
                }
            }
        }
        else if (WIZARD_STATUE_3TICK_IDS.contains(id))
        {
            for(HallowedSepulchreWizardStatue statue : wizardStatues) {
                if(statue.getGameObject() == gameobject) {
                    wizardStatues.remove(statue);
                    break;
                }
            }
        }
        else if(CHEST_IDS.contains(id))
        {
            chests.remove(gameobject);
        }
        else if(FLOOR_GATE_IDS.contains(id))
        {
            floor_gates.remove(gameobject);
        }
        else if(STAIRS_IDS.contains(id))
        {
            stairs.remove(gameobject);
        }

        switch(id)
        {
            case portal_id:
                portals.remove(gameobject);
                break;
        }
        //objects.removeIf(o -> o.getTileObject() == event.getGameObject());
    }

    LocalPoint floor4LeftLocation = new LocalPoint(6016, 8704);
    LocalPoint floor4RightLocation = new LocalPoint(6016, 9344);
    LocalPoint floor4BottomLocation1 = new LocalPoint(7296, 8704);
    LocalPoint floor4BottomLocation2 = new LocalPoint(7296, 9344);
    LocalPoint floor4BottomLocation3 = new LocalPoint(7040, 8704);
    public int floor_4_ticks_since_statue = 0;
    private boolean floor_4_first_fire_detected = false;
    public void Process_Floor_4_WizardSpot(GameObject g)
    {
        LocalPoint check = g.getLocalLocation();
        if(check.equals(floor4LeftLocation))
        {
            Floor4WizardLeft = g;
            floor_4_ticks_since_statue = 0;
            return;
        }
        if(check.equals(floor4RightLocation))
        {
            Floor4WizardRight = g;
            return;
        }
        if(check.equals(floor4BottomLocation1))
        {
            Floor4WizardBottom1 = g;
            return;
        }
        if(check.equals(floor4BottomLocation2))
        {
            Floor4WizardBottom2 = g;
            return;
        }
        if(check.equals(floor4BottomLocation3))
        {
            Floor4WizardBottom3 = g;
            return;
        }
    }

    public GameObject Floor5TopLeft;
    public GameObject Floor5BottomLeft;
    LocalPoint floor5TopLeft = new LocalPoint(4096, 9088);
    LocalPoint floor5BottomLeft = new LocalPoint(5632, 9088);

    public int floor_5_ticks_since_statue = 0;
    public boolean floor_5_first_fire_detected = false;

    public void Process_Floor_5_WizardSpot(GameObject g)
    {
        LocalPoint check = g.getLocalLocation();
        if(check.equals(floor5TopLeft))
        {
            Floor5TopLeft = g;
            floor_5_ticks_since_statue = 0;
            return;
        }
        if(check.equals(floor5BottomLeft))
        {
            Floor5BottomLeft = g;
            return;
        }
    }
    public GameObject Floor5_2ATopLeft;
    public GameObject Floor5_2AOneUnderTopLeft;
    LocalPoint floor5_2ATopLeft = new LocalPoint(3840, 9088);
    LocalPoint floor5_2A1OneUnderTopLeft = new LocalPoint(4096, 9088);

    public int floor5_2A_ticks_since_statue = 0;
    public boolean floor5_2A_first_fire_detected = false;

    public void Process_Floor5_2A_WizardSpot(GameObject g)
    {
        LocalPoint check = g.getLocalLocation();
        if(check.equals(floor5_2ATopLeft))
        {
            Floor5_2ATopLeft = g;
            floor5_2A_ticks_since_statue = 0;
            return;
        }
        if(check.equals(floor5_2A1OneUnderTopLeft))
        {
            Floor5_2AOneUnderTopLeft = g;
            return;
        }
    }

    public GameObject floor5_4BottomLeft;
    public GameObject floor5_4Bottomright;
    LocalPoint Floor5_4BottomLeft = new LocalPoint(8960, 8448);
    LocalPoint Floor5_4BottomRight = new LocalPoint(8960, 9088);

    public int floor_5_4_ticks_since_statue = 0;
    public boolean floor_5_4_first_fire_detected = false;

    public void Process_Floor_5_4_WizardSpot(GameObject g)
    {
        LocalPoint check = g.getLocalLocation();
        log.info("Process_Floor_5_4_WizardSpot: " + check.getX() + "-" + check.getY());
        if(check.equals(Floor5_4BottomLeft))
        {
            log.info("Found Left");
            floor5_4BottomLeft = g;
            floor_5_4_ticks_since_statue = 0;
            return;
        }
        if(check.equals(Floor5_4BottomRight))
        {
            floor5_4Bottomright = g;
            return;
        }
    }

    @Subscribe
    private void onProjectileMoved(ProjectileMoved event)
    {
        //Projectile p = event.getProjectile();
        //log.info(p.getId() + " <P> Moved");
    }

    @Subscribe
    private void onGroundObjectSpawned(GroundObjectSpawned event)
    {
        GroundObject GO = event.getGroundObject();
        addGroundObject(GO);
    }


    @Subscribe
    private void onGroundObjectDespawned(GroundObjectDespawned event)
    {
        GroundObject GO = event.getGroundObject();
        if(bridge_id == GO.getId())
        {
            bridges.remove(GO);
        }
    }
}
