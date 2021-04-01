//Created by PluginCreated by ImNo: https://discord.gg/dhfRTRE
package net.runelite.client.plugins.DeveloperHelper;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.events.*;
import net.runelite.api.kit.KitType;
import net.runelite.api.util.Text;
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
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.QuantityFormatter;
import org.apache.commons.lang3.ObjectUtils;
import org.pf4j.Extension;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

@Extension
@PluginDescriptor(
        name = "Developer Helper",
        description = "Developer Helper by ImNo https://github.com/ImNoOSRS "
)
@Slf4j
public class DeveloperHelperPlugin extends Plugin {

    public boolean copy_widgets_on_next_tick = false;
    // Injects our config
    @Inject
    private ConfigManager configManager;
    @Inject
    private DeveloperHelperConfig config;
    @Inject
    private Client client;
    @Inject
    public ClientThread clientThread;
    @Inject
    private ClientToolbar clientToolbar;
    
    @Provides
    DeveloperHelperConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(DeveloperHelperConfig.class);
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("DeveloperHelper"))
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

    private NavigationButton navButton;

    @Override
    protected void startUp() {
        sidepanel(true);
    }

    @Override
    protected void shutDown() {
        sidepanel(false);
    }

    DeveloperHelperPanel panel;
    public void sidepanel(Boolean show)
    {
        if(show) {
            panel = injector.getInstance(DeveloperHelperPanel.class);
            final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "developer_helper_icon.png");

            navButton = NavigationButton.builder()
                    .tooltip("Developer Helper")
                    .icon(icon)
                    .priority(2)
                    .panel(panel)
                    .build();

            clientToolbar.addNavigation(navButton);
        }
        else
        {
            clientToolbar.removeNavigation(navButton);
        }
    }


    public boolean widgethotfix = false;
    @Subscribe
    private void onGameTick(GameTick gameTick)
    {
        if(widgethotfix)
        {
            Widget w = client.getWidget(161, 16);
            if(w != null)
                w.setHidden(true);

            Widget w2 = client.getWidget(84, 21);
            if(w2 != null)
                w2.setHidden(true);

            Widget w3 = client.getWidget(281, 189);
            if(w3 != null)
                w3.setHidden(true);
        }
        if(copy_widgets_on_next_tick)
        {
            copy_widgets_on_next_tick = false;
            if (client.getGameState() != GameState.LOGGED_IN) {
                return;
            }
            CopyWidgets();
        }
    }

    String widgetdata;
    public void CopyWidgets()
    {
        widgetdata = "DUMP_WIDGETS_START\n";
        for (final Widget widgetRoot : client.getWidgetRoots()) {
            this.processWidget(widgetRoot);
        }
        widgetdata += "END";
        Clipboard.store(widgetdata);
    }

    private void processWidget(final Widget widget) {
        if (widget == null) {
            return;
        }

        String line =  "Widget{" + widget.getName() + "}:" + widget.getText() + "(" + widget.getId() + ") - Type:" + widget.getType() + ", CT:" + widget.getContentType();
        String group = "__" + WidgetInfo.TO_GROUP(widget.getId()) + "." + WidgetInfo.TO_CHILD(widget.getId()) + "[" + widget.getIndex() + "]";
        widgetdata += line + group + "\n";

        for (final Widget child : widget.getStaticChildren()) {
            this.processWidget(child);
        }
        for (final Widget dynamicChild : widget.getDynamicChildren()) {
            this.processWidget(dynamicChild);
        }
        for (final Widget nestedChild : widget.getNestedChildren()) {
            this.processWidget(nestedChild);
        }
    }

    @Subscribe
    private void onBeforeRender(BeforeRender event)
    {

    }


    private static final String copyid = "Copy Object ID";
    private static final String copyworldpoint = "Copy Object WorldPoint";
    private static final String copylocalpoint = "Copy Object LocalPoint";
    private static final String copytileworldpoint = "Copy Tile WorldPoint";
    private static final String copytilelocalpoint = "Copy Tile LocalPoint";
    private static final String WALK_HERE = "Walk here";
    private static final String copychat = "Copy chat TEXT";

    public static class IsKeyPressed {
        private static volatile boolean wPressed = false;
        public static boolean isShiftPressed() {
            synchronized (IsKeyPressed.class) {
                return wPressed;
            }
        }

        public static void main(String[] args) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

                @Override
                public boolean dispatchKeyEvent(KeyEvent ke) {
                    synchronized (IsKeyPressed.class) {
                        switch (ke.getID()) {
                            case KeyEvent.KEY_PRESSED:
                                if (ke.getKeyCode() == KeyEvent.VK_SHIFT) {
                                    wPressed = true;
                                }
                                break;

                            case KeyEvent.KEY_RELEASED:
                                if (ke.getKeyCode() == KeyEvent.VK_SHIFT) {
                                    wPressed = false;
                                }
                                break;
                        }
                        return false;
                    }
                }
            });
        }
    }

    int counter = 0;
    @Subscribe
    private void onMenuOpened(MenuOpened event)
    {

    }

    public MenuEntry base_entry(MenuEntry current, String newname)
    {
        MenuEntry base = new MenuEntry();
        base.setOption(newname);
        base.setTarget(current.getTarget());
        base.setOpcode(MenuAction.RUNELITE.getId());
        counter++;
        base.setParam0(counter);
        base.setParam1(current.getParam1());
        base.setIdentifier(current.getId());
        return base;
    }

    public MenuEntry base_entry(MenuEntryAdded current, String newname)
    {
        MenuEntry base = new MenuEntry();
        base.setOption(newname);
        base.setTarget(current.getTarget());
        base.setOpcode(MenuAction.RUNELITE.getId());
        counter++;
        base.setParam0(counter);
        base.setParam1(current.getParam1());
        base.setIdentifier(current.getId());
        return base;
    }

    public void insert_menu_entry(MenuEntry entry)
    {
        client.insertMenuItem(entry.getOption(), entry.getTarget(), entry.getMenuAction().getId(), entry.getId(), entry.getParam0(), entry.getParam1(), false);
    }

    public String selected_chat_text = "";
    @Subscribe
    private void onMenuEntryAdded(MenuEntryAdded event)
    {
        if(panel.LogMenuEntryAdded.isSelected())
        {
            String storing = "MenuEntryAdded: " + event.getOption() + " > [";
            storing += "Identifier:" + event.getId();
            storing += " Target:" + event.getTarget();
            storing += " MenuAction:" + event.getMenuAction();
            storing += " Param0:" + event.getParam0();
            storing += " Param1:" + event.getParam1();
            storing += "]";
            log(storing);
        }
        if(config.shift() && !client.isKeyPressed(KeyCode.KC_SHIFT))
        {
            return;
        }

        if(config.copyChat()) {
            final Widget chatbox = client.getWidget(WidgetInfo.CHATBOX);

            if (chatbox != null) {
                Point mouse = client.getMouseCanvasPosition();
                if (chatbox.getBounds().contains(mouse.getX(), mouse.getY())) {
                    log.info(event.getOption());
                    if (!event.getOption().equals(copychat) && event.getOption().equals("Walk here")) {
                        final Widget transparent = client.getWidget(WidgetInfo.CHATBOX_MESSAGE_LINES);
                        for (Widget w : transparent.getDynamicChildren()) {
                            if (!w.getText().equals("")) {
                                //log.info("Processing: " + w.getText());
                                if (w.getBounds().contains(mouse.getX(), mouse.getY())) {
                                    if (w.getText().replace("</col>", "").endsWith(":")) {
                                        //log.info("Ignored becouse it has [:]");
                                        continue;
                                    }
                                    MenuEntry copy_chat_text = base_entry(event, copychat);
                                    insert_menu_entry(copy_chat_text);
                                    //log.info("Added copy option: " + w.getText());
                                    selected_chat_text = w.getText();
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }

        //int op = event.getMenuAction();
        String option = event.getOption();
        switch(option)
        {
            case "Withdraw-All-but-1":
                MenuEntry copy_bank_name = base_entry(event, "Copy item NAME");
                insert_menu_entry(copy_bank_name);
                break;
            case "Drop":
            case "Destroy":
                MenuEntry copy_id = base_entry(event, "Copy item ID");
                insert_menu_entry(copy_id);
                ItemComposition def = client.getItemDefinition(event.getId());
                if(def.getNote() != -1)
                {
                    MenuEntry copy_id_unnoted = base_entry(event, "Copy item ID (Unnoted)");
                    copy_id_unnoted.setIdentifier(def.getLinkedNoteId());
                    insert_menu_entry(copy_id_unnoted);
                }
                MenuEntry copy_name = base_entry(event, "Copy item NAME");
                insert_menu_entry(copy_name);
                break;
        }
        if (event.getMenuAction() != MenuAction.EXAMINE_OBJECT)
        {
            if(!config.copyTileData())
            {
                return;
            }
            if(event.getOption().equals(WALK_HERE))
            {
                MenuEntry menuEntry = new MenuEntry();

                menuEntry.setOption(copytileworldpoint);
                menuEntry.setTarget(event.getTarget());
                menuEntry.setParam0(menuEntry.getParam0());
                menuEntry.setParam1(event.getParam1());
                menuEntry.setIdentifier(event.getId());
                menuEntry.setOpcode(MenuAction.RUNELITE.getId());

                insert_menu_entry(menuEntry);
                MenuEntry menuEntry3 = new MenuEntry();

                menuEntry3.setOption(copytilelocalpoint);
                menuEntry3.setTarget(event.getTarget());
                menuEntry3.setParam0(menuEntry3.getParam0());
                menuEntry3.setParam1(event.getParam1());
                menuEntry3.setIdentifier(event.getId());
                menuEntry3.setOpcode(MenuAction.RUNELITE.getId());
                insert_menu_entry(menuEntry3);
            }
            return;
        }

        if(!config.copyObjectData())
        {
            return;
        }

        final Tile tile = client.getScene().getTiles()[client.getPlane()][event.getParam0()][event.getParam1()];
        final TileObject tileObject = findTileObject(tile, event.getId());

        if (tileObject == null)
        {
            return;
        }

        MenuEntry[] menuEntries = client.getMenuEntries();
        menuEntries = Arrays.copyOf(menuEntries, menuEntries.length + 2);
        MenuEntry menuEntry = menuEntries[menuEntries.length - 1] = new MenuEntry();

        menuEntry.setOption(copyid);
        menuEntry.setTarget(event.getTarget());
        menuEntry.setParam0(event.getParam0());
        menuEntry.setParam1(event.getParam1());
        menuEntry.setIdentifier(event.getId());
        menuEntry.setOpcode(MenuAction.RUNELITE.getId());

        MenuEntry menuEntry2 = menuEntries[menuEntries.length - 2] = new MenuEntry();

        menuEntry2.setOption(copyworldpoint);
        menuEntry2.setTarget(event.getTarget());
        menuEntry2.setParam0(event.getParam0() + 1);
        menuEntry2.setParam1(event.getParam1());
        menuEntry2.setIdentifier(event.getId());
        menuEntry2.setOpcode(MenuAction.RUNELITE.getId());

        MenuEntry menuEntry3 = menuEntries[menuEntries.length - 3] = new MenuEntry();

        menuEntry3.setOption(copylocalpoint);
        menuEntry3.setTarget(event.getTarget());
        menuEntry3.setParam0(event.getParam0() + 2);
        menuEntry3.setParam1(event.getParam1());
        menuEntry3.setIdentifier(event.getId());
        menuEntry3.setOpcode(MenuAction.RUNELITE.getId());
        client.setMenuEntries(menuEntries);
    }

    public void gamelog(String message) {
        client.addChatMessage(
                ChatMessageType.GAMEMESSAGE,
                "",
                message,
                null
        );
    }

    @Subscribe
    void onChatMessage(ChatMessage event) {
        if(!panel.LogChatbox.isSelected())
        {
            return;
        }
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        String message = event.getMessage();
        log.info("GameMessage: " + message);
    }

    @Subscribe
    private void onMenuOptionClicked(MenuOptionClicked event)
    {
        if(event.getMenuOption().equals(copychat))
        {
            if(config.IgnoreChatColor())
            {
                selected_chat_text = Text.toJagexName(Text.removeTags(selected_chat_text));
            }
            Clipboard.store(selected_chat_text);
            return;
        }
        if(event.getMenuOption() == "Copy item ID" || event.getMenuOption() == "Copy item ID (Unnoted)") {
            Integer idf = event.getId();
            String itemname = "Failed grabbing name.";
            ItemComposition i = client.getItemDefinition(event.getId());
            if(i != null)
            {
                itemname = i.getName();
            }
            Clipboard.store(idf.toString());
        }
        if(event.getMenuOption() == "Copy item NAME") {
            /*
            String itemname = "Failed grabbing name.";
            ItemComposition i = client.getItemComposition(event.getId());
            if(i != null)
            {
                itemname = i.getName();
            }*/
            Clipboard.store(event.getMenuTarget().replace("<", ">").split(">")[2]);
        }

        if (event.getMenuAction() != MenuAction.RUNELITE || !(event.getMenuOption().equals(copyid) || event.getMenuOption().equals(copyworldpoint)|| event.getMenuOption().equals(copytileworldpoint) || event.getMenuOption().equals(copylocalpoint)|| event.getMenuOption().equals(copytilelocalpoint)))
        {
            String storing = "" + event.getMenuOption() + " > [";
            storing += "Identifier:" + event.getId();
            storing += " Target:" + event.getMenuTarget();
            storing += " MenuAction:" + event.getMenuAction();
            storing += " Param0:" + event.getActionParam();
            storing += " Param1:" + event.getWidgetId();
            storing += "]";
            if(panel.LogMenuActions.isSelected())
            {
                switch((String)panel.ActionHandleType.getSelectedItem()) {
                    case "Copy":
                        Clipboard.store(storing);
                        break;
                    case "Log in console":
                        log.info(storing);
                        break;
                    case "Log in chat":
                        gamelog(storing);
                        break;
                }
            }
            return;
        }


        if(event.getMenuOption().equals(copytileworldpoint))
        {
            Tile t = client.getSelectedSceneTile();
            if(t != null) {
                Clipboard.store("new WorldPoint(" + t.getWorldLocation().getX() + ", " + t.getWorldLocation().getY() + ", " + t.getWorldLocation().getPlane() + ")");
            }
            else
            {
                Clipboard.store("Could not locate Tile");
            }
            return;
        }
        else if(event.getMenuOption().equals(copytilelocalpoint))
        {
            Tile t = client.getSelectedSceneTile();
            if(t != null) {
                Clipboard.store("new LocalPoint(" + t.getLocalLocation().getX() + ", " + t.getLocalLocation().getY() + ")");
            }
            else
            {
                Clipboard.store("Could not locate Tile");
            }
            return;
        }

        Scene scene = client.getScene();
        Tile[][][] tiles = scene.getTiles();
        final int x = event.getActionParam();
        final int y = event.getWidgetId();
        final int z = client.getPlane();
        final Tile tile = tiles[z][x][y];

        TileObject object = findTileObject(tile, event.getId());
        if (object == null)
        {
            return;
        }

        if(event.getMenuOption().equals(copyworldpoint)) {
            Clipboard.store("new WorldPoint(" + object.getWorldLocation().getX() + ", " + object.getWorldLocation().getY() + ", " + object.getWorldLocation().getPlane() +")");
        }
        else if(event.getMenuOption().equals(copylocalpoint)) {
            Clipboard.store("new LocalPoint(" + object.getLocalLocation().getX() + ", " + object.getLocalLocation().getY() + ")");
        }

        // object.getId() is always the base object id, getObjectComposition transforms it to
        // the correct object we see
        ObjectComposition objectDefinition = getObjectComposition(object.getId());
        String name = objectDefinition.getName();
        // Name is probably never "null" - however prevent adding it if it is, as it will
        // become ambiguous as objects with no name are assigned name "null"
        if (Strings.isNullOrEmpty(name) || name.equals("null"))
        {
            return;
        }

        if(event.getMenuOption().equals(copyid)) {
            Clipboard.store("" + object.getId());
        }
    }

    public void LogWidgets(Widget w)
    {
        String widget = "Widget loaded: " + w.getId() + "[" + w.getIndex() + "]" + " > " + w.getText();
        log(widget);
        /*for(Widget child : w.getChildren())
        {
            widget = "Child: " + child.getId() + "[" + child.getIndex() + "]" + " > " + w.getText();
            log(widget);
        }*/
    }

    public void log(String text)
    {
        switch((String)panel.ActionHandleType.getSelectedItem()) {
            case "Log in chat":
                gamelog(text);
                break;
            default:
                log.info(text);
                break;
        }
    }

    @Subscribe
    private void onWidgetLoaded(WidgetLoaded event)
    {
        if(panel.LogOnWidgetLoaded.isSelected())
        {
            //LogWidgets(w);
            String widget = "Widget loaded: " + event.getGroupId();
            log(widget);
        }
    }

    @Subscribe
    private void onGameObjectSpawned(GameObjectSpawned event)
    {
        if(panel.LogGameObjectSpawned.isSelected())
        {
            LogGameObject("GameObjectSpawned", event.getGameObject());
        }
    }

    @Subscribe
    private void onGameObjectDepawned(GameObjectDespawned event)
    {
        if(panel.LogGameObjectDespawned.isSelected())
        {
            LogGameObject("GameObjectDespawned", event.getGameObject());
        }
    }

    @Subscribe
    private void onGameObjectChanged(GameObjectChanged event)
    {
        if(panel.LogGameObjectChanged.isSelected())
        {
            LogGameObject("GameObjectChanged", event.getGameObject());
        }
    }

    public void LogGameObject(String text, GameObject g)
    {
        ObjectComposition def = client.getObjectDefinition(g.getId());
        if (def.getImpostorIds() != null) {
            def = def.getImpostor();
        }
        log(text + ": {" + g.getId() + "(" + def.getName() + ")");
    }

    @Nullable
    private ObjectComposition getObjectComposition(int id)
    {
        ObjectComposition objectComposition = client.getObjectDefinition(id);
        return objectComposition.getImpostorIds() == null ? objectComposition : objectComposition.getImpostor();
    }

    private TileObject findTileObject(Tile tile, int id)
    {
        if (tile == null)
        {
            return null;
        }

        final GameObject[] tileGameObjects = tile.getGameObjects();
        final DecorativeObject tileDecorativeObject = tile.getDecorativeObject();
        final WallObject tileWallObject = tile.getWallObject();
        final GroundObject groundObject = tile.getGroundObject();

        if (objectIdEquals(tileWallObject, id))
        {
            return tileWallObject;
        }

        if (objectIdEquals(tileDecorativeObject, id))
        {
            return tileDecorativeObject;
        }

        if (objectIdEquals(groundObject, id))
        {
            return groundObject;
        }

        for (GameObject object : tileGameObjects)
        {
            if (objectIdEquals(object, id))
            {
                return object;
            }
        }
        return null;
    }

    private boolean objectIdEquals(TileObject tileObject, int id)
    {
        if (tileObject == null)
        {
            return false;
        }

        if (tileObject.getId() == id)
        {
            return true;
        }

        // Menu action EXAMINE_OBJECT sends the transformed object id, not the base id, unlike
        // all of the GAME_OBJECT_OPTION actions, so check the id against the impostor ids
        final ObjectComposition comp = client.getObjectDefinition(tileObject.getId());

        if (comp.getImpostorIds() != null)
        {
            for (int impostorId : comp.getImpostorIds())
            {
                if (impostorId == id)
                {
                    return true;
                }
            }
        }

        return false;
    }

}