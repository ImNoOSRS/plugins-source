package com.profittracker;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;

import net.runelite.api.events.*;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

@Extension
@Slf4j
@PluginDescriptor(
        name = "Profit Tracker"
)
public class ProfitTrackerPlugin extends Plugin
{
    ProfitTrackerGoldDrops goldDropsObject;
    ProfitTrackerInventoryValue inventoryValueObject;

    // the profit will be calculated against this value
    private long prevInventoryValue;
    private long totalProfit;

    private long startTickMillis;

    private boolean skipTickForProfitCalculation;
    private boolean inventoryValueChanged;


    @Inject
    private Client client;

    @Inject
    private ProfitTrackerConfig config;

    @Inject
    private ItemManager itemManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ProfitTrackerOverlay overlay;

    @Override
    protected void startUp() throws Exception
    {
        // Add the inventory overlay
        overlayManager.add(overlay);

        goldDropsObject = new ProfitTrackerGoldDrops(client, itemManager);

        inventoryValueObject = new ProfitTrackerInventoryValue(client, itemManager);

        ResetCalculations();

    }

    // start profit calculation from this point
    private void ResetCalculations()
    {
        // value here doesn't matter, will be overwritten
        prevInventoryValue = -1;

        // profit begins at 0 of course
        totalProfit = 0;

        // initialize timer
        startTickMillis = System.currentTimeMillis();

        // skip profit calculation for first tick, to initialize first inventory value
        skipTickForProfitCalculation = true;


        inventoryValueChanged = false;
    }

    @Override
    protected void shutDown() throws Exception
    {
        // Remove the inventory overlay
        overlayManager.remove(overlay);

    }

    @Subscribe
    public void onGameTick(GameTick gameTick)
    {
        /*
        Main plugin logic here

        1. If inventory changed,
            - calculate profit (inventory value difference)
            - generate gold drop (nice animation for showing gold earn or loss)

        2. Calculate profit rate and update in overlay

        */

        long profitPerHour;
        long tickProfit;

        if (inventoryValueChanged)
        {
            tickProfit = calculateTickProfit();

            // accumulate profit
            totalProfit += tickProfit;

            overlay.updateProfitValue(totalProfit);

            // generate gold drop
            if (config.goldDrops() && tickProfit != 0)
            {
                goldDropsObject.requestGoldDrop(Math.toIntExact(tickProfit));
            }

            inventoryValueChanged = false;
        }

        profitPerHour = calculateProfitHourly(startTickMillis, totalProfit);

        overlay.updateProfitRate(profitPerHour);

    }

    private long calculateTickProfit()
    {
        /*
        Calculate and return the profit for this tick
        if skipTickForProfitCalculation is set, meaning this tick was bank / deposit
        so return 0

         */
        long newInventoryValue;
        long newProfit;

        // calculate current inventory value
        newInventoryValue = inventoryValueObject.calculateInventoryValue();

        if (!skipTickForProfitCalculation)
        {
            // calculate new profit
            newProfit = newInventoryValue - prevInventoryValue;

        }
        else
        {
            /* first time calculation / banking / equipping */
            log.info("Skipping profit calculation!");

            skipTickForProfitCalculation = false;

            // no profit this tick
            newProfit = 0;
        }

        // update prevInventoryValue for future calculations anyway!
        prevInventoryValue = newInventoryValue;

        return newProfit;
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event)
    {
        /*
        this event tells us when inventory has changed
        and when banking/equipment event occured this tick
         */
        log.info("onItemContainerChanged container id: " + event.getContainerId());

        int containerId = event.getContainerId();

        if(containerId == InventoryID.INVENTORY.getId()) {
            // inventory has changed - need calculate profit in onGameTick
            inventoryValueChanged = true;

        }

        // in these events, inventory WILL be changed but we DON'T want to calculate profit!
        if(     containerId == InventoryID.BANK.getId() ||
                containerId == InventoryID.EQUIPMENT.getId()) {
            // this is a bank or equipment interaction.
            // Don't take this into account
            skipTickForProfitCalculation = true;

        }

    }

    static long calculateProfitHourly(long startTimeMillies, long profit)
    {
        long averageProfitThousandForHour;
        long averageProfitForSecond;
        long secondsElapsed;
        long timeDeltaMillis;
        long currentTimeMillis;

        // calculate time
        currentTimeMillis = System.currentTimeMillis();

        timeDeltaMillis = currentTimeMillis - startTimeMillies;

        secondsElapsed = timeDeltaMillis / 1000;
        if (secondsElapsed > 0)
        {
            averageProfitForSecond = (profit) / secondsElapsed;
        }
        else
        {
            // can't divide by zero, not enough time has passed
            averageProfitForSecond = 0;
        }

        averageProfitThousandForHour = averageProfitForSecond * 3600 / 1000;

        return averageProfitThousandForHour;
    }


    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        /* for ignoring deposit in deposit box */
        log.info(String.format("Click! ID: %d, actionParam: %d ,menuOption: %s, menuTarget: %s, widgetId: %d",
                event.getId(), event.getParam0(), event.getMenuOption(), event.getTarget(), event.getWidgetId()));

        if (event.getId() == ObjectID.BANK_DEPOSIT_BOX) {
            // we've interacted with a deposit box. Don't take this tick into account for profit calculation
            skipTickForProfitCalculation = true;
        }


    }

    @Provides
    ProfitTrackerConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(ProfitTrackerConfig.class);
    }


    @Subscribe
    public void onScriptPreFired(ScriptPreFired scriptPreFired)
    {
        goldDropsObject.onScriptPreFired(scriptPreFired);
    }
}