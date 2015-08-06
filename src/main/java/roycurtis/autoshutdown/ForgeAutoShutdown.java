package roycurtis.autoshutdown;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

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
    static final String VERSION = "1.0.1";
    static final String MODID   = "ForgeAutoShutdown";
    static final Logger LOGGER  = LogManager.getFormatterLogger(MODID);

    @Mod.Instance(MODID)
    static ForgeAutoShutdown INSTANCE;

    // Instances are not created here, as not to waste memory if this mod is loaded as a
    // client mod by user error.
    ShutdownTask task;
    Timer        timer;

    @EventHandler
    @SideOnly(Side.CLIENT)
    public void clientPreInit(FMLPreInitializationEvent event)
    {
        LOGGER.error("This mod is intended only for use on servers.");
        LOGGER.error("Please consider removing this mod from your installation.");
    }

    @EventHandler
    @SideOnly(Side.SERVER)
    /**
     * Sets up timer thread and loads/creates the configuration file
     */
    public void serverPreInit(FMLPreInitializationEvent event)
    {
        timer = new Timer("Forge Auto-Shutdown timer");

        Config.init( event.getSuggestedConfigurationFile() );
    }

    @EventHandler
    @SideOnly(Side.SERVER)
    /**
     * This does two main things:
     * * Register the ShutdownTask as a server tick handler
     * * Schedules ShutdownTask to run at the scheduled time, then every minute after
     *
     * The use of a tick handler ensures the shutdown process is run in the main thread,
     * to prevent issues with cross-thread contamination. As the handler run 40 times a
     * second, the event is just a boolean check. This means the scheduled task's role is
     * to unlock the tick handler.
     */
    public void serverStart(FMLServerStartingEvent event)
    {
        DateFormat      dateFormat = new SimpleDateFormat("HH:mm:ss dd-MMMM-yyyy");
        ShutdownCommand command    = new ShutdownCommand();
        Calendar        shutdownAt = Calendar.getInstance();
        shutdownAt.set(Calendar.HOUR_OF_DAY, Config.hour);
        shutdownAt.set(Calendar.MINUTE, Config.minute);
        shutdownAt.set(Calendar.SECOND, 0);

        task = new ShutdownTask();

        // Adjust for when current time surpasses shutdown schedule
        // (e.g. if shutdown time is 07:00 and current time is 13:21)
        if ( shutdownAt.before( Calendar.getInstance() ) )
            shutdownAt.add(Calendar.DAY_OF_MONTH, 1);

        Date shutdownAtDate = shutdownAt.getTime();

        timer.schedule(task, shutdownAtDate, 60 * 1000);
        event.registerServerCommand(command);
        LOGGER.info( "Next automatic shutdown: %s", dateFormat.format(shutdownAtDate) );
    }
}
