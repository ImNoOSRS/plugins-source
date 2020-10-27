package com.profittracker;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;

import java.text.DecimalFormat;
/**
 * The ProfitTrackerOverlay class is used to display profit values for the user
 */
public class ProfitTrackerOverlay extends Overlay {
    private long profitValue;
    private long profitRateValue;
    private final ProfitTrackerConfig ptConfig;
    private final PanelComponent panelComponent = new PanelComponent();

    public static String FormatIntegerWithCommas(long value) {
        DecimalFormat df = new DecimalFormat("###,###,###");
        return df.format(value);
    }
    @Inject
    private ProfitTrackerOverlay(ProfitTrackerConfig config)
    {
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        profitValue = 0L;
        ptConfig = config;
    }

    /**
     * Render the item value overlay.
     * @param graphics the 2D graphics
     * @return the value of {@link PanelComponent#render(Graphics2D)} from this panel implementation.
     */
    @Override
    public Dimension render(Graphics2D graphics) {
        String titleText = "Profit Tracker:";

        // Not sure how this can occur, but it was recommended to do so
        panelComponent.getChildren().clear();

        // Build overlay title
        panelComponent.getChildren().add(TitleComponent.builder()
                .text(titleText)
                .color(Color.GREEN)
                .build());

        // Set the size of the overlay (width)
        panelComponent.setPreferredSize(new Dimension(
                graphics.getFontMetrics().stringWidth(titleText) + 30,
                0));

        // Profit
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Profit:")
                .right(FormatIntegerWithCommas(profitValue))
                .build());

        // Profit Rate
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Rate:")
                .right(profitRateValue + "K/H")
                .build());

        return panelComponent.render(graphics);
    }

    /**
     * Updates profit value display
     * @param newValue the value to update the profitValue's {{@link #panelComponent}} with.
     */
    public void updateProfitValue(final long newValue) {
        SwingUtilities.invokeLater(() ->
            profitValue = newValue
        );
    }

    /**
     * Updates profit rate value display
     * @param newValue the value to update the profitRateValue's {{@link #panelComponent}} with.
     */
    public void updateProfitRate(final long newValue) {
        SwingUtilities.invokeLater(() ->
            profitRateValue = newValue
        );
    }


}
