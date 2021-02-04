package net.runelite.client.plugins.hallowedhelper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("hallowedhelper")

public interface hallowedhelperConfig extends Config {

    @Getter
    @AllArgsConstructor
    enum RenderDistance
    {
        SHORT("Short", 2350),
        MEDIUM("Medium", 3525),
        FAR("Far", 4700),
        UNCAPPED("Uncapped", 0);

        private final String name;
        private final int distance;

        @Override
        public String toString()
        {
            return name;
        }
    }


    @ConfigSection(
            name = "Main",
            description = "",
            position = 1,
            keyName = "MainSection"
    )
    default boolean MainSection()
    {
        return false;
    }

    @ConfigTitleSection(
            keyName = "copy",
            name = "<html><center>Hallowed Helper [BETA]." +
                    "<br>Created by ImNo" +
                    "<br>https://discord.gg/GQx4grf" +
                    "<br></center></html>",
            description = "",
            position = 1,
            section = "MainSection"
    )
    default Title copy()
    {
        return new Title();
    }

    @ConfigItem(
            position = 2,
            keyName = "HelperRenderDistance",
            name = "Render distance",
            description = "How far to render overlays from your player's position.",
            section = "MainSection"
    )
    default RenderDistance renderDistance()
    {
        return RenderDistance.MEDIUM;
    }

    @ConfigSection(
            name = "Floor Gate's",
            description = "",
            position = 2,
            keyName = "floorgateSection"
    )
    default boolean floorgateSection()
    {
        return false;
    }

    @Alpha
    @ConfigItem(
            position = 1,
            keyName = "ShowFloorGate",
            name = "Render floor gates [Little buggy]",
            description = "Shows the floor gates at the end.",
            section = "floorgateSection"
    )
    default boolean ShowFloorGate()
    {
        return true;
    }

    @Alpha
    @ConfigItem(
            position = 2,
            keyName = "floorGateColor",
            name = "Floor Stairs Color",
            description = "Change the overlay fill color of floor stairs.",
            section = "floorgateSection"
    )
    default Color floorgateColor()
    {
        return Color.CYAN;
    }

    @ConfigSection(
            name = "Stairs",
            description = "",
            position = 3,
            keyName = "stairsSection"
    )
    default boolean stairsSection()
    {
        return false;
    }

    @Alpha
    @ConfigItem(
            position = 1,
            keyName = "ShowStairs",
            name = "Render Stairs",
            description = "Shows stairs down/up.",
            section = "stairsSection"
    )
    default boolean ShowStairs()
    {
        return true;
    }

    @Alpha
    @ConfigItem(
            position = 2,
            keyName = "StairsColor",
            name = "Stairs Color",
            description = "Change the overlay fill color of stairs.",
            section = "stairsSection"
    )
    default Color stairsColor()
    {
        return Color.CYAN;
    }


    @ConfigSection(
            name = "Chests",
            description = "",
            position = 4,
            keyName = "chestsSection"
    )
    default boolean chestsSection()
    {
        return false;
    }

    @Alpha
    @ConfigItem(
            position = 1,
            keyName = "ShowChests",
            name = "Render Chests",
            description = "Shows chest overlay.",
            section = "chestsSection"
    )
    default boolean ShowChests()
    {
        return true;
    }

    @Alpha
    @ConfigItem(
            position = 2,
            keyName = "chestColor",
            name = "Chest Color",
            description = "Change the overlay fill color of chests.",
            section = "chestsSection"
    )
    default Color chestColor()
    {
        return Color.MAGENTA;
    }


    @Alpha
    @ConfigItem(
            position = 3,
            keyName = "USEchestColor",
            name = "Chest Animation: 1",
            description = "Change the overlay fill color of opening chests.",
            section = "chestsSection"
    )
    default Color chestOpeningColor()
    {
        return Color.CYAN;
    }

    @Alpha
    @ConfigItem(
            position = 4,
            keyName = "USEchestColor2",
            name = "Chest Animation: 2",
            description = "Change the overlay fill color of opening chests [1].",
            section = "chestsSection"
    )
    default Color chestOpeningColor2()
    {
        return Color.GREEN;
    }

    @Alpha
    @ConfigItem(
            position = 5,
            keyName = "USEchestColor3",
            name = "Chest Animation: 3",
            description = "Change the overlay fill color of opening chests [2].",
            section = "chestsSection"
    )
    default Color chestOpeningColor3()
    {
        return Color.GREEN;
    }

    @Alpha
    @ConfigItem(
            position = 6,
            keyName = "USEchestColor4",
            name = "Chest Failed (Poison)",
            description = "Change the overlay fill color of failing the opening of a chest.",
            section = "chestsSection"
    )
    default Color chestOpeningFail()
    {
        return Color.RED;
    }

    @ConfigSection(
            name = "Portal (Magic)",
            description = "",
            position = 5,
            keyName = "magicportalSection"
    )
    default boolean magicportalSection()
    {
        return false;
    }

    @Alpha
    @ConfigItem(
            position = 1,
            keyName = "ShowPortals",
            name = "Render Portals",
            description = "Shows portal overlay.",
            section = "magicportalSection"
    )
    default boolean ShowPortals()
    {
        return true;
    }

    @Alpha
    @ConfigItem(
            position = 2,
            keyName = "portalColor",
            name = "Portal Color",
            description = "Change the overlay fill color of portals.",
            section = "magicportalSection"
    )
    default Color portalColor()
    {
        return Color.YELLOW;
    }


    @Alpha
    @ConfigItem(
            position = 3,
            keyName = "OPENportalColor",
            name = "Portal Color (Opening)",
            description = "Change the overlay fill color of opening portals.",
            section = "magicportalSection"
    )
    default Color portalOpenColor()
    {
        return Color.GREEN;
    }

    @ConfigSection(
            name = "Fire Statues",
            description = "",
            position = 6,
            keyName = "FireStatueSection"
    )
    default boolean FireStatueSection()
    {
        return false;
    }

    @Alpha
    @ConfigItem(
            position = 1,
            keyName = "HHShowSafeTiles",
            name = "Show SAFE tiles",
            description = "Safe Tiles",
            section = "FireStatueSection"
    )
    default boolean ShowSafeTiles()
    {
        return true;
    }

    @Alpha
    @ConfigItem(
            position = 2,
            keyName = "HHSafeTileColor",
            name = "SAFE tile color",
            description = "Change the overlay fill color of safe tiles.",
            section = "FireStatueSection"
    )
    default Color SafeTileColor()
    {
        return Color.GREEN;
    }

    @Alpha
    @ConfigItem(
            position = 3,
            keyName = "HHShowRiskyTiles",
            name = "Show Risky tiles",
            description = "Risky Tiles",
            section = "FireStatueSection"
    )
    default boolean ShowRiskyTiles()
    {
        return true;
    }

    @Alpha
    @ConfigItem(
            position = 4,
            keyName = "HHRiskyTileColor",
            name = "RISKY tile color",
            description = "Change the overlay fill color of risky tiles.",
            section = "FireStatueSection"
    )
    default Color RiskyTileColor()
    {
        return Color.YELLOW;
    }

    @Alpha
    @ConfigItem(
            position = 5,
            keyName = "HHShowUnsafeTiles",
            name = "Show Unsafe tiles",
            description = "Unsafe Tiles",
            section = "FireStatueSection"
    )
    default boolean ShowUnsafeTiles()
    {
        return true;
    }

    @Alpha
    @ConfigItem(
            position = 6,
            keyName = "HHUnsafeTileColor",
            name = "UNSAFE tile color",
            description = "Change the overlay fill color of safe tiles.",
            section = "FireStatueSection"
    )
    default Color UnsafeTileColor()
    {
        return Color.RED;
    }

    @Alpha
    @ConfigItem(
            position = 7,
            keyName = "HHShowFireUnreachable",
            name = "Check Unreachable Tiles",
            description = "Check Unreachable Tiles",
            section = "FireStatueSection"
    )
    default boolean ShowFireUnreachable()
    {
        return true;
    }

    @Alpha
    @ConfigItem(
            position = 8,
            keyName = "HHShowFireTickCounter",
            name = "Show Tick Counter (From Fire)",
            description = "Show Tick Counter (From Fire)",
            section = "FireStatueSection"
    )
    default boolean ShowFireTickCounter()
    {
        return true;
    }

    @ConfigItem(
            position = 9,
            keyName = "HHShowReversedFireTickCounter",
            name = "Show Tick Counter (Untill fire)",
            description = "Show Tick Counter (Untill fire)",
            section = "FireStatueSection"
    )
    default boolean ShowReversedFireTickCounter()
    {
        return true;
    }

    @ConfigItem(
            position = 10,
            keyName = "HHShowArrowTiles",
            name = "Show Arrow Danger [BETA]",
            description = "Show Arrow Danger",
            section = "FireStatueSection"
    )
    default boolean ShowArrowDanger()
    {
        return true;
    }


    @Alpha
    @ConfigItem(
            position = 11,
            keyName = "HHShowArrowDangerColor",
            name = "Arrow Danger Color",
            description = "ShowArrowDangerColor",
            section = "FireStatueSection"
    )
    default Color ShowArrowDangerColor()
    {
        return Color.BLUE;
    }


    @ConfigItem(
            position = 12,
            keyName = "HHFillTileOverlay",
            name = "Fill Tile Overlay",
            description = "Fill in the tile overlay.",
            section = "FireStatueSection"
    )
    default boolean FillTileOverlay()
    {
        return false;
    }

    @ConfigSection(
            name = "Lightning Spawns",
            description = "",
            position = 7,
            keyName = "Lightningsection"
    )
    default boolean Lightningsection()
    {
        return false;
    }

    @ConfigItem(
            position = 1,
            keyName = "HHLightningTiles",
            name = "Lightning tiles",
            description = "",
            section = "Lightningsection"
    )
    default boolean ShowLightningTiles()
    {
        return true;
    }

    @ConfigItem(
            position = 2,
            keyName = "HHLightningCountdown",
            name = "Lightning countdown",
            description = "",
            section = "Lightningsection"
    )
    default boolean ShowLightningCountdown()
    {
        return true;
    }

    @ConfigSection(
            name = "Server Tile",
            description = "",
            position = 8,
            keyName = "ServerTileSection"
    )
    default boolean ServerTileSection()
    {
        return false;
    }

    @Alpha
    @ConfigItem(
            position = 1,
            keyName = "HHShowServerTile",
            name = "Show Server Tile",
            description = "Show Server Tile",
            section = "ServerTileSection"
    )
    default boolean ShowServerTile()
    {
        return true;
    }

    @Alpha
    @ConfigItem(
            position = 2,
            keyName = "HHServerTileOutLine",
            name = "Outline",
            description = "Server Tile Outline",
            section = "ServerTileSection"
    )
    default Color ServerTileOutline()
    {
        return Color.CYAN;
    }

    @Alpha
    @ConfigItem(
            position = 3,
            keyName = "HHServerTileFill",
            name = "Fill",
            description = "Server Tile Fill",
            section = "ServerTileSection"
    )
    default Color ServerTileFill()
    {
        return new Color(0, 0, 0, 0);
    }

    @ConfigSection(
            name = "Other",
            description = "",
            position = 9,
            keyName = "otherSection"
    )
    default boolean otherSection()
    {
        return false;
    }


    @ConfigItem(
            position = 3,
            keyName = "GlitchyHit",
            name = "Explode on hit (fun)",
            description = "Only works with the ring equipped.",
            section = "otherSection"
    )
    default boolean GlitchyHit()
    {
        return false;
    }

    @ConfigItem(
            position = 4,
            keyName = "GlitchyGrapple",
            name = "Glitchy Grapple",
            description = "Only works with the ring equipped.",
            section = "otherSection"
    )
    default boolean GlitchyGrapple()
    {
        return false;
    }

    @ConfigItem(
            position = 4,
            keyName = "ShowDebugInfoHH",
            name = "ShowDebugInfo [LAGGY]",
            description = "Dont use this...",
            section = "otherSection"
    )
    default boolean ShowDebugInfo()
    {
        return false;
    }


    @ConfigItem(
            position = 5,
            keyName = "HHShowValues",
            name = "Show Values (alot)",
            description = "Show Values (alot)",
            section = "otherSection"
    )
    default boolean ShowValues()
    {
        return false;
    }


    @ConfigItem(
            position = 6,
            keyName = "IgnoreFloor5Implentation",
            name = "Disable the floor 5 implentation.",
            description = "Disables the special implentation.",
            section = "otherSection"
    )
    default boolean DisableFloor5Implentation()
    {
        return false;
    }

}