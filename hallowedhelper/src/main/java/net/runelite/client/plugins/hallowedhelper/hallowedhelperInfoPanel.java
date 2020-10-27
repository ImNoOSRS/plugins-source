package net.runelite.client.plugins.hallowedhelper;

import java.awt.*;
import java.util.Map;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.table.TableAlignment;
import net.runelite.client.ui.overlay.components.table.TableComponent;
import net.runelite.client.util.ColorUtil;

@Slf4j
public class hallowedhelperInfoPanel extends OverlayPanel
{
        private final Client client;
        private final hallowedhelperConfig config;
        private final hallowedhelperPlugin plugin;

        @Inject
        hallowedhelperInfoPanel(Client client, final hallowedhelperConfig config, hallowedhelperPlugin plugin)
        {
            this.client = client;
            this.plugin = plugin;
            this.config = config;
            setPosition(OverlayPosition.TOP_LEFT);
        }

        private int maxfloor = 1;
        private int userX = 0;

        @Override
        public Dimension render(Graphics2D graphics)
        {
            Player local = client.getLocalPlayer();
            WorldPoint WorldPoint = local.getWorldLocation();
            LocalPoint LocalLocation = local.getLocalLocation();
            maxfloor = getMaxFloor();
            TableComponent tableComponent = new TableComponent();
            tableComponent.setColumnAlignments(TableAlignment.LEFT, TableAlignment.RIGHT);

            tableComponent.addRow("Hallowed-Info", "");
            tableComponent.addRow("FLOOR4_ROTATION", "" + plugin.floor4_fire_rotation);
            tableComponent.addRow("TICKS_SINCE:", "" + plugin.floor_4_ticks_since_statue);
            tableComponent.addRow("FLOOR5_ROTATION", "" + plugin.floor5_fire_rotation);
            tableComponent.addRow("TICKS5_SINCE:", "" + plugin.floor_5_ticks_since_statue);
            tableComponent.addRow("FLOOR5[2]_ROTATION", "" + plugin.floor5_2A_fire_rotation);
            tableComponent.addRow("TICKS5[2]_SINCE:", "" + plugin.floor5_2A_ticks_since_statue);
            tableComponent.addRow("Location: ", getLocation());
            //tableComponent.addRow("Region ID: ", "" + client.getLocalPlayer().getWorldLocation().getRegionID());
            //its shit /\
            tableComponent.addRow("TicksLeft: ", "" + (plugin.isDoorOpen() ? plugin.getTicksleft() : 0));
            tableComponent.addRow("DoorOpen: ", "" + (plugin.isDoorOpen() ? ColorUtil.prependColorTag("Yes", Color.GREEN) : ColorUtil.prependColorTag("No", Color.RED)));
            tableComponent.addRow("Level: ", "" + client.getRealSkillLevel(Skill.AGILITY));
            if(config.ShowValues()) {
                tableComponent.addRow("Plane: ", "" + client.getPlane());
                tableComponent.addRow("WorldX: ", "" + WorldPoint.getX());
                tableComponent.addRow("WorldX (Reg): ", "" + WorldPoint.getRegionX());
                tableComponent.addRow("WorldY (Reg): ", "" + WorldPoint.getRegionY());
                tableComponent.addRow("WorldY: ", "" + WorldPoint.getX());
                tableComponent.addRow("LocalX: ", "" + LocalLocation.getX());
                tableComponent.addRow("LocalY: ", "" + LocalLocation.getY());
                tableComponent.addRow("SceneX: ", "" + LocalLocation.getSceneX());
                tableComponent.addRow("SceneY: ", "" + LocalLocation.getSceneY());
                //tableComponent.addRow("LH: ", "" + client.getLocalPlayer().getLogicalHeight());
                tableComponent.addRow("Region", "" + client.getMapRegions()[0]);
                Integer swordcount = plugin.getSwords().size();
                Integer arrowcount = plugin.getArrows().size();
                Integer chestcount = plugin.getChests().size();
                tableComponent.addRow("Swords", swordcount.toString());
                tableComponent.addRow("Swordsman", "" + plugin.getSwordStatues().size());
                tableComponent.addRow("Arrows", arrowcount.toString());
                tableComponent.addRow("Floor-Stairs", "" + plugin.getFloor_gates().size());
                tableComponent.addRow("Stairs", "" + plugin.getStairs().size());
                tableComponent.addRow("Chests", "" + chestcount);
                tableComponent.addRow("Lightningbolts: ", ""  + plugin.getLightningboltlocations().size());
            }
            //client.getVarbitValue(24717); Hallowed Floor? -42 : 10 = FLOOR?
            //Varbit 10392 is de Afteller tijd

            /*
            for(GameObject chest : plugin.getChests())
            {
                ObjectDefinition definition = client.getObjectDefinition(chest.getId());
                if (definition != null) {
                    if (definition.getImpostorIds() != null) {
                        definition = definition.getImpostor();
                    }
                    int varbit = definition.getId();
                    tableComponent.addRow("Chest " + chest.getId() + ": ", "" + varbit);
                }
            }*/

            panelComponent.getChildren().add(tableComponent);

            return super.render(graphics);
        }

        public String getLocation()
        {
            switch(plugin.getCurrentfloor())
            {
                case -1:
                    return "Lobby";
                case 1:
                    return "Floor " + ColorUtil.prependColorTag("1" + getSubFloor(), Color.GREEN) + ColorUtil.prependColorTag("/" + maxfloor, Color.WHITE);
                case 2:
                    return "Floor " + ColorUtil.prependColorTag("2", Color.GREEN) + ColorUtil.prependColorTag("/" + maxfloor, Color.WHITE);
                case 3:
                    return "Floor\r\n" + ColorUtil.prependColorTag("3", Color.GREEN) + ColorUtil.prependColorTag("/" + maxfloor, Color.WHITE) + "\r\n" + ThirdrdFloorLocation();
                case 4:
                    return "Floor\r\n" + ColorUtil.prependColorTag("4", Color.GREEN) + ColorUtil.prependColorTag("/" + maxfloor, Color.WHITE) + "\r\n" + FourthFloorLocation();
                case 5:
                    return "Floor\r\n" + ColorUtil.prependColorTag("5", Color.GREEN) + ColorUtil.prependColorTag("/" + maxfloor, Color.WHITE);
                default:
                    return "?";
            }
        }

    private String ThirdrdFloorLocation()
    {
        if(plugin.getSubfloor() == 0)
        {
            return "Left";
        }
        else if(plugin.getSubfloor() == 1)
        {
            return "Mid";
        }
        else
        {
            return "Right";
        }
    }

    private String FourthFloorLocation()
    {
        if(plugin.getSubfloor() == 0)
        {
            return "Up";
        }
        else if(plugin.getSubfloor() == 1)
        {
            return "Mid";
        }
        else
        {
            return "Down";
        }
    }

    public String getSubFloor()
    {
        if(plugin.getBridges().size() > 0)
        {
            GroundObject bridge = plugin.getBridges().iterator().next();
            int xdistance = (bridge.getX() - client.getLocalPlayer().getLocalLocation().getX());
            if(xdistance > -1408) {
                return "C";
            }
        }
        switch(client.getMapRegions()[0])
        {
            case 8796:
                return "A";
            case 8797:
                return "B";
            case 9052:
                return "C";
        }
        return "";
    }

        public int getMaxFloor()
        {
            int lv = client.getRealSkillLevel(Skill.AGILITY);
            if(lv > 91)
            {
                return 5;
            }
            else if(lv > 81)
            {
                return 4;
            }
            else if(lv > 71)
            {
                return 3;
            }
            else if(lv > 61)
            {
                return 2;
            }
            else if(lv > 51)
            {
                return 1;
            }
            return 0;
        }
    }
