//Created by PluginCreator by ImNo: https://github.com/ImNoOSRS 
package net.runelite.client.plugins.ZalcanoHelper;

import net.runelite.api.coords.LocalPoint;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.queries.InventoryItemQuery;
import net.runelite.client.ui.overlay.OverlayManager;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
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
import net.runelite.client.plugins.PluginType;
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
		name = "Zalcano Helper",
		description = "Helps you with Zalcano",
		type = PluginType.SYSTEM
)
@Slf4j
public class ZalcanoHelperPlugin extends Plugin {
	// Injects our config
	@Inject
	private ConfigManager configManager;
	@Inject
	private ZalcanoHelperConfig config;
	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private ZalcanoHelperOverlay overlay;

	public GameObject glowingrock;

	@Getter(AccessLevel.PACKAGE)
	private final Set<LocalPoint> fallingrocklocations = new HashSet<>();

	@Getter(AccessLevel.PACKAGE)
	private int tickssincefallingrocks = 0;

	@Getter(AccessLevel.PACKAGE)
	private int maxticks = 10;

	private int falling_rock_id = 1727;

	public boolean rock_danger = false;
	public int rock_danger_counter = 5;

	@Getter(AccessLevel.PACKAGE)
	private final Set<GameObject> blue_boost_circles = new HashSet<>();

	public NPC zalcano;
	public GameObject furnace;
	public GameObject altar;

	public boolean has_raw_ore = false;
	public boolean has_smithed_ore = false;
	public boolean has_imbued_ore = false;

	private static final Set<Integer> REGION_IDS = Set.of(
			11869, 11870
	);

	@Provides
	ZalcanoHelperConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(ZalcanoHelperConfig.class);
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event) {
		if (event.getGroup().equals("ZalcanoHelper"))
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
		overlayManager.add(overlay);
		if (client.getGameState() != GameState.LOGGED_IN || !isAtZalcano())
		{
			return;
		}
		load();
	}

	public boolean isAtZalcano()
	{
		return REGION_IDS.contains(client.getMapRegions()[0]);
	}

	public enum zalcanostate
	{
		normal,
		mine,
		dead,
		unknown
	}
	public zalcanostate getzalcanostate()
	{
		int animation = zalcano.getAnimation();
		if(animation == 8433)
		{
			log.info("Zalcano spawns portals.");
		}
		else if(animation == 8435)
		{
			log.info("Zalcano throws rocks.");
		}
		else if(animation == 8440)
		{
			log.info("Zalcano died.");
			glowingrock = null;
			return zalcanostate.dead;
		}
		switch(zalcano.getPoseAnimation())
		{
			case 8429:
			case 8430:
				return zalcanostate.normal;
			case 8438:
				return zalcanostate.mine;
			default:
				return zalcanostate.unknown;
		}
	}

	@Override
	protected void shutDown() {
		overlayManager.remove(overlay);
		clear();
	}

	public void clear()
	{
		blue_boost_circles.clear();
	}


	@Subscribe
	private void onGameStateChanged(final GameStateChanged event) {
		if(event.getGameState() == GameState.LOGGED_IN)
		{
			clear();
		}
	}

	@Subscribe
	private void onGameObjectSpawned(GameObjectSpawned event)
	{
		handleGameObject(event.getGameObject());
	}

	public void handleGameObject(GameObject gameobject)
	{
		int id = gameobject.getId();
		switch(id)
		{
			case ObjectID.ROCK_FORMATION_GLOWING:
				glowingrock = gameobject;
				rock_danger = false;
				break;
			case ObjectID.DEMONIC_SYMBOL_36200:
				blue_boost_circles.add(gameobject);
				break;
			case ObjectID.FURNACE_36195:
				furnace = gameobject;
				break;
			case ObjectID.ALTAR_36196:
				altar = gameobject;
				break;
		}
	}

	@Subscribe
	private void onGameObjectDespawned(GameObjectDespawned event)
	{
		GameObject gameobject = event.getGameObject();
		if(gameobject.getId() == ObjectID.DEMONIC_SYMBOL_36200)
		{
			blue_boost_circles.remove(gameobject);
		}
	}

	@Subscribe
	private void onNpcSpawned(NpcSpawned event)
	{
		handleNpc(event.getNpc());
	}

	public void handleNpc(NPC npc)
	{
		if(npc.getId() == NpcID.ZALCANO)
		{
			zalcano = npc;
		}
	}

	private void load() {
		final LocatableQueryResults<GameObject> locatableQueryResults = new GameObjectQuery().result(client);

		for (final GameObject gameObject : locatableQueryResults) {
			handleGameObject(gameObject);
		}

		for (final NPC npc : client.getNpcs()) {
			handleNpc(npc);
		}
	}

	@Subscribe
	private void onGraphicsObjectCreated(GraphicsObjectCreated g)
	{
		if(g.getGraphicsObject().getId() == falling_rock_id)
		{
			GraphicsObject graphicsobject = g.getGraphicsObject();
			tickssincefallingrocks = 0;
			LocalPoint current = graphicsobject.getLocation();
			if(!fallingrocklocations.contains(current))
			{
				fallingrocklocations.add(current);
			}
		}
	}

	@Subscribe
	private void onProjectileSpawned(ProjectileSpawned event)
	{
		Projectile projectile = event.getProjectile();
		if(projectile.getId() == 1728)//spit fire
		{
			rock_danger = true;
			rock_danger_counter = 5;
		}
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		has_raw_ore = inventoryContains(23905);
		has_smithed_ore = inventoryContains(23906);
		has_imbued_ore = inventoryContains(23907);
		if(fallingrocklocations.size() > 0)
		{
			tickssincefallingrocks++;
			if(tickssincefallingrocks == maxticks)
			{
				fallingrocklocations.clear();
			}
		}
		if(rock_danger)
		{
			rock_danger_counter--;
		}
	}

	public boolean inventoryContains(int itemID)
	{
		if (client.getItemContainer(InventoryID.INVENTORY) == null)
		{
			return false;
		}

		return new InventoryItemQuery(InventoryID.INVENTORY)
				.idEquals(itemID)
				.result(client)
				.size() >= 1;
	}
}