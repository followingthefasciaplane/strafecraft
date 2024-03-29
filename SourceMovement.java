import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

@Mod("sourcemovement")
public class SourceMovement {
    private static final Logger LOGGER = LogManager.getLogger();

    private PlayerMovementManager playerMovementManager;
    private CommandHandler commandHandler;

    public SourceMovement() {
        // register the configuration
        Path configPath = FMLPaths.CONFIGDIR.get().resolve("sourcemovement.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SourceMovementConfig.SPEC, configPath.toString());

        // register event listeners
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onServerStarting);
        // register the config change listener
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConfigChanged);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Setting up SourceMovement mod...");

        // create instances of PlayerMovementManager and CommandHandler
        playerMovementManager = new PlayerMovementManager();
        commandHandler = new CommandHandler(playerMovementManager);

        // register the PlayerMovementManager with the Forge event bus
        MinecraftForge.EVENT_BUS.register(playerMovementManager);

        // perform initial validation and loading of config values
        SourceMovementConfig.validateConfig();

        LOGGER.info("SourceMovement mod setup complete.");
    }

    private void onServerStarting(final FMLServerStartingEvent event) {
        LOGGER.info("Registering SourceMovement commands...");

        // register the command handler with the server's command dispatcher
        commandHandler.registerCommands(event.getServer().getCommandManager().getDispatcher());

        LOGGER.info("SourceMovement commands registered.");
    }

    @SubscribeEvent
    public void onConfigChanged(final ModConfigEvent.Reloading event) {
        // ensure we are responding to our mod's configuration changes
        if (event.getConfig().getModId().equals("sourcemovement")) {
            SourceMovementConfig.refreshConfig();
            LOGGER.info("Configuration reloaded, values updated.");
        }
    }
}
