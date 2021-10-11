package net.runelite.client.plugins.DenseEssenceOverlay;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.AgilityShortcut;
import net.runelite.client.game.ItemManager;
import com.openosrs.client.game.WorldLocation;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.TextComponent;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;

@Slf4j
@Singleton
class LapCounterOverlay extends OverlayPanel {
    @Inject
    private ItemManager itemManager;

    private final Client client;
    private final DenseEssenceOverlayConfig config;
    private final DenseEssenceOverlayPlugin plugin;
    private final TextComponent textComponent = new TextComponent();

    @Inject
    private LapCounterOverlay(final Client client, final DenseEssenceOverlayConfig config, final DenseEssenceOverlayPlugin plugin) {
        setPosition(OverlayPosition.BOTTOM_RIGHT);
        this.client = client;
        this.config = config;
        this.plugin = plugin;
    }

    //int blood_runes = 6715;
    int soul_runes = 7228;
    //This shit right here makes toggable for decimals, which is neat
    public String format(float Counter)
    {
        if(config.Decimals() > 0)
        {
            String lmao = "#.";
            for(int i = 0; i < config.Decimals(); ++i)
            {
                lmao += "#";
            }
            DecimalFormat formatter = new DecimalFormat(lmao);
            return formatter.format(Counter);
        }
        else
        {
            DecimalFormat formatter = new DecimalFormat("#");
            return formatter.format(Counter);
        }
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if(!plugin.inarea) // So that it doesnt try to render shit when your not there anyway cool! :D Thanks! yw :P
        {
            return null;
        }
									panelComponent.getChildren().add(LineComponent.builder()
										.left("Runecrafting:")
										.right("")
										.build());

        int currentlvl = client.getRealSkillLevel(Skill.RUNECRAFT);
        float currentexp = client.getSkillExperience(Skill.RUNECRAFT);
        float startexp = plugin.start_exp_runecrafting;
        float exp_for_next_level = Experience.getXpForLevel(currentlvl + 1);
        int region = plugin.current_rc_region;
        int exp_devider = 4820;
        if (region == soul_runes) {
            exp_devider = 1233;
        }
        //Naming shit (tables) idk
									panelComponent.getChildren().add(LineComponent.builder()
										.left("Total Laps:")
										.right("" + Math.round(Math.abs(currentexp - startexp) / exp_devider))
										.build());

									panelComponent.getChildren().add(LineComponent.builder()
										.left("Laps until level:")
										.right("" + format(Math.abs(currentexp - exp_for_next_level / exp_devider)))
										.build());

        int goal_end = client.getVar(VarPlayer.RUNECRAFT_GOAL_END);
        if (goal_end != exp_for_next_level) ;
        if (config.lapstogoal()) {
									panelComponent.getChildren().add(LineComponent.builder()
										.left("Laps until goal:")
										.right("" + format(Math.abs(currentexp - client.getVar(VarPlayer.RUNECRAFT_GOAL_END) / exp_devider)))
										.build());

        }
        //This make shit do Size n' shit
        panelComponent.setPreferredSize(new Dimension(150, 0));
        return super.render(graphics);



    }
}
