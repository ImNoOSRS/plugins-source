package net.runelite.client.plugins.Finder;

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
import net.runelite.api.queries.GroundObjectQuery;
import net.runelite.api.queries.WallObjectQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Keybind;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Clipboard;
import net.runelite.client.util.LinkBrowser;
import net.runelite.client.util.QuantityFormatter;
import org.apache.commons.lang3.ObjectUtils;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

@Extension
@PluginDescriptor(
        name = "Finder",
        description = "Find players, objects or npc's",
        type = PluginType.UTILITY
)
@Slf4j
public class FinderPlugin extends Plugin implements KeyListener {
    // Injects our config
    @Inject
    private ConfigManager configManager;
    @Inject
    private FinderConfig config;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private ChatboxPanelManager chatboxPanelManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private FinderOverlay overlay;
    @Inject
    private KeyManager keyManager;

    @Provides
    FinderConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(FinderConfig.class);
    }

    @Override
    protected void startUp() {
        keyManager.registerKeyListener(this);
    }

    @Override
    protected void shutDown() {
        keyManager.unregisterKeyListener(this);
        clearall();
    }


    @Subscribe
    private void onGameStateChanged(final GameStateChanged event) {
        if(event.getGameState() == GameState.LOGGED_IN) {
            clearnexttick = true;
        }
    }

    @Getter(AccessLevel.PACKAGE)
    public Map<GameObject, String> gameobjects = new HashMap<GameObject, String>();

    @Getter(AccessLevel.PACKAGE)
    public Map<WallObject, String> wallobjects = new HashMap<WallObject, String>();

    @Getter(AccessLevel.PACKAGE)
    public Map<GroundObject, String> groundobjects = new HashMap<GroundObject, String>();

    @Getter(AccessLevel.PACKAGE)
    private final Set<NPC> npcs = new HashSet<>();

    @Getter(AccessLevel.PACKAGE)
    private final Set<Player> players = new HashSet<>();

    public void clearall()
    {
        if (overlayManager.anyMatch(o -> o instanceof FinderOverlay)) {
            overlayManager.remove(overlay);
        }
        clearobjects();
    }

    public void clearobjects()
    {
        gameobjects.clear();
        wallobjects.clear();
        npcs.clear();
        groundobjects.clear();
        players.clear();
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("Finder"))
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


    private int autosereachticks = 0;
    @Subscribe
    public void onGameTick(GameTick event) {
        if(config.autoresearch())
        {
            autosereachticks++;
            if(autosereachticks == config.researchdelay())
            {
                autosereachticks = 0;
                needsload = true;
            }
        }
        if(clearnexttick)
        {
            clearnexttick = false;
            if(config.research())
            {
                clearobjects();
                needsload = true;
            }
            else
            {
                clearall();
            }
        }
        if(needsload)
        {
            search();
        }
    }

    public void search()
    {
        if(to_find.equals(""))
        {
            clearall();
        }
        else {
            needsload = false;
            if(config.reset())
            {
                clearobjects();
            }

            if(to_find.equals("help"))
            {
                log.info("Show help box");
                String helpinfo = "You can search objects, players or NPCs" +
                        "\nExample" +
                        "\ntree" +
                        "\nSyntax: Search all for 'tree'." +
                        "\nExample" +
                        "\nobject:tree" +
                        "\nSyntax: Search all objects for 'tree'." +
                        "\nExample" +
                        "\ntree,bank" +
                        "\nSyntax: Search all for 'tree' AND 'bank'." +
                        "\nOther commands:" +
                        "\nplayer:" +
                        "\nnpc:" +
                        "\nweb:" +
                        "\nwiki:" +
                        "\ngoogle:" +
                        "\nyoutube: or yt:";
                        to_find = "";
                infoBox(helpinfo, "Finder Info");
                return;
            }
            if(to_find.startsWith("google:"))
            {
                LinkBrowser.browse(to_find.replace("google:", "https://google.com/search?q="));
                to_find = "";
            }
            else if(to_find.startsWith("wiki:"))
            {
                LinkBrowser.browse(to_find.replace("wiki:", "https://oldschool.runescape.wiki/?search=") + "&utm_source=runelite");
                to_find = "";
            }
            else if(to_find.startsWith("youtube:"))
            {
                LinkBrowser.browse(to_find.replace("youtube:", "https://www.youtube.com/results?search_query="));
                to_find = "";
            }
            else if(to_find.startsWith("yt:"))
            {
                LinkBrowser.browse(to_find.replace("yt:", "https://www.youtube.com/results?search_query="));
                to_find = "";
            }
            else if(to_find.startsWith("link:") || to_find.startsWith("l:") || to_find.startsWith("web:") || to_find.startsWith("url:") || to_find.startsWith("www.") || to_find.startsWith("http"))
            {
                if(to_find.startsWith("http://"))
                {
                    to_find = to_find.replace("http://", "https://");
                }
                else if(!to_find.startsWith("https://")) {
                    to_find = "https://" + to_find;
                }
                LinkBrowser.browse(to_find.replace("link:", "").replace("l:", "").replace("web:", "").replace("url:", ""));
                to_find = "";
            }
            FindObjects();
            if (gameobjects.size() > 0 | npcs.size() > 0 | groundobjects.size() > 0 | players.size() > 0) {
                if (!overlayManager.anyMatch(o -> o instanceof FinderOverlay)) {
                    overlayManager.add(overlay);
                }
            }
        }
    }

    public static void infoBox(String infoMessage, String titleBar)
    {
        SwingUtilities.invokeLater(() ->
        {
            JOptionPane.showMessageDialog(ClientUI.getFrame(), infoMessage, titleBar, JOptionPane.INFORMATION_MESSAGE);
        });
    }

    public void FindObjects()
    {
        boolean searchgameobjects = config.usegameobjects();
        boolean searchgroundobjects = config.usegroundobjects();
        boolean searchwallobjects = config.usewallobjects();
        boolean searchnpcs = config.usenpcs();
        boolean searchplayers = config.useplayers();

        if(to_find.startsWith("p:") || to_find.startsWith("player:"))
        {
            searchnpcs = false;
            searchgroundobjects = false;
            searchgameobjects = false;
            searchwallobjects = false;
        }
        else if(to_find.startsWith("o:") || to_find.startsWith("object:"))
        {
            searchnpcs = false;
            searchplayers = false;
        }
        else if(to_find.startsWith("npc:"))
        {
            searchplayers = false;
            searchgameobjects = false;
            searchgroundobjects = false;
            searchwallobjects = false;
        }

        //BELANGERIJK
        //BELANGRIJK
        //wsS KAN IK HIER OOK ALLES IN EEN LIJST ZETTEN EN DAN .containsAny dan kan ik ze ook parsen bij onGameObjectSpawned etc.
        for(String find : to_find.split(","))
        {
            find = find.split(":")[0];
            if(find.equals(""))
            {
                continue;
            }
            if(find.equals("*") || find.equals("all"))
            {
                find = "";
            }
            if(searchgameobjects) {
                final LocatableQueryResults<GameObject> locatableQueryResults = new GameObjectQuery().result(client);

                for (final GameObject gameObject : locatableQueryResults) {

                    //for(Field i : ObjectID.class.getFields())
                    ObjectDefinition od = client.getObjectDefinition(gameObject.getId());
                    if (od == null) {
                        continue;
                    }
                    if (od.getImpostorIds() != null) {
                        if(od.getImpostor() != null)//Hotfix
                        {
                            od = od.getImpostor();
                        }
                    }
                    if (od.getName().toLowerCase().contains(find)) {
                        gameobjects.put(gameObject, od.getName());
                    }
                }
            }

            if(searchgroundobjects) {
                final LocatableQueryResults<GroundObject> groundObjectResults = new GroundObjectQuery().result(client);

                for (final GroundObject groundObject : groundObjectResults) {
                    ObjectDefinition od = client.getObjectDefinition(groundObject.getId());
                    if (od == null) {
                        continue;
                    }
                    if (od.getImpostorIds() != null) {
                        if(od.getImpostor() != null)//Hotfix
                        {
                            od = od.getImpostor();
                        }
                    }
                    if (od.getName().toLowerCase().contains(find)) {
                        groundobjects.put(groundObject, od.getName());
                    }
                }
            }

            if(searchwallobjects) {
                final LocatableQueryResults<WallObject> wallQueryResults = new WallObjectQuery().result(client);

                for (final WallObject wallObject : wallQueryResults) {
                    //for(Field i : ObjectID.class.getFields())
                    ObjectDefinition od = client.getObjectDefinition(wallObject.getId());
                    if (od == null) {
                        continue;
                    }
                    if (od.getImpostorIds() != null) {
                        if(od.getImpostor() != null)//Hotfix
                        {
                            od = od.getImpostor();
                        }
                    }
                    if (od.getName().toLowerCase().contains(find)) {
                        wallobjects.put(wallObject, od.getName());
                    }
                }
            }

            if(searchnpcs) {
                for (final NPC npc : client.getNpcs()) {
                    if (npc == null) {
                        continue;
                    }
                    if (npc.getName() != null) {
                        String name = npc.getName();
                        if (name.toLowerCase().contains(find)) {
                            npcs.add(npc);
                        }
                    }
                }
            }

            if(searchplayers) {
                for (final Player player : client.getPlayers()) {
                    if (player == null) {
                        continue;
                    }
                    if (player.getName() != null) {
                        String name = player.getName();
                        if (name.toLowerCase().contains(find)) {
                            players.add(player);
                        }
                    }
                }
            }
        }
    }

    @Subscribe
    private void onGameObjectDespawned(GameObjectDespawned event)
    {
        for (Map.Entry<GameObject, String> entry : gameobjects.entrySet()) {
            if(entry.getKey() == event.getGameObject())
            {
                gameobjects.remove(entry);
            }
        }
    }

    @Subscribe
    private void onGroundObjectDespawned(GroundObjectDespawned event)
    {
        for (Map.Entry<GroundObject, String> entry : groundobjects.entrySet()) {
            if(entry.getKey() == event.getGroundObject())
            {
                groundobjects.remove(entry);
            }
        }
    }

    @Subscribe
    private void onNpcDespawned(final NpcDespawned event)
    {
        if(npcs.contains(event.getNpc()))
        {
            npcs.remove(event.getNpc());
        }
    }

    @Subscribe
    private void onPlayerDespawned(PlayerDespawned event)
    {
        if(players.contains(event.getPlayer()))
        {
            players.remove(event.getPlayer());
        }
    }

    private String to_find = "";
    private List<String> searches = new ArrayList<String>();
    private boolean needsload = false;
    private boolean clearnexttick = false;

    public void ask_box()
    {
        String searchtext = "Search ";
        boolean first = false;
        boolean last = true;
        if(config.usegroundobjects() | config.usegameobjects())
        {
            first = true;
            searchtext += "Object";
        }
        if(config.useplayers())
        {
            last = false;
        }
        if(config.usenpcs())
        {
            if(first)
            {
                if(last) {
                    searchtext += " or";
                }
                else
                {
                    searchtext += ", ";
                }
            }
            searchtext += "NPC";
        }
        if(config.useplayers())
        {
            searchtext += " or Players";
        }

        searchtext += "<br>For multiple searches split them between commas (search HELP for help)";

        chatboxPanelManager.openTextInput(searchtext)
                .onDone((content) ->
                {
                    if (content == null)
                    {
                        return;
                    }

                    to_find = content.toLowerCase().trim();
                    searches.add(to_find);
                    needsload = true;
                }).build();
    }

    private static final int HOTKEY = KeyEvent.VK_SHIFT;

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    Keybind ctrlv = new Keybind(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK);

    @Override
    public void keyPressed(KeyEvent e)
    {
        if (config.hotkey().matches(e))
        {
            ask_box();
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        if (e.getKeyCode() == HOTKEY)
        {

        }
    }
}