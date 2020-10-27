package net.runelite.client.plugins.decimalprices;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;

@Slf4j
@Extension
@PluginDescriptor(
        name = "Decimal Prices",
        description = "Allows the use of decimals when entering a custom price",
        tags = {"decimal", "GE", "price"},
        type = PluginType.UTILITY
)
public class DecimalPrices extends Plugin {
    @Inject
    private Client client;

    @Inject
    private KeyManager keyManager;

    @Inject
    private DecimalPricesKeyListener inputListener;

    @Override
    protected void startUp() throws Exception {
        keyManager.registerKeyListener(inputListener);
        log.info("Decimal prices started!");
    }

    @Override
    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(inputListener);
        log.info("Decimal prices stopped!");
    }

}
