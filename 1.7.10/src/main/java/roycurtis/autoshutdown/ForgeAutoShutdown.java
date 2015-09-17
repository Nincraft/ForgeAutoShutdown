package roycurtis.autoshutdown;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
    modid   = ForgeAutoShutdown.MODID,
    name    = ForgeAutoShutdown.MODID,
    version = ForgeAutoShutdown.VERSION,

    acceptableRemoteVersions = "*",
    acceptableSaveVersions   = ""
)
public class ForgeAutoShutdown
{
    // Frozen at 1.0.1, to prevent misleading world save errors
    public static final String VERSION = "1.0.1";
    public static final String MODID   = "ForgeAutoShutdown";
    public static final Logger LOGGER  = LogManager.getFormatterLogger(MODID);

    @EventHandler
    @SideOnly(Side.CLIENT)
    public void clientPreInit(FMLPreInitializationEvent event)
    {
        LOGGER.error("This mod is intended only for use on servers.");
        LOGGER.error("Please consider removing this mod from your installation.");
    }

    @EventHandler
    @SideOnly(Side.SERVER)
    public void serverPreInit(FMLPreInitializationEvent event)
    {
        Config.init( event.getSuggestedConfigurationFile() );
    }

    @EventHandler
    @SideOnly(Side.SERVER)
    public void serverStart(FMLServerStartingEvent event)
    {
        if ( Config.isNothingEnabled() )
        {
            LOGGER.warn("It appears no ForgeAutoShutdown features are enabled.");
            LOGGER.warn("Please check the config at `config/forgeautoshutdown.cfg`.");
            return;
        }

        if (Config.scheduleEnabled)
            ShutdownTask.create();

        if (Config.voteEnabled)
            ShutdownCommand.create(event);

        if (Config.watchdogEnabled)
            WatchdogTask.create();
    }
}
