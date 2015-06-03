package roycurtis.autoshutdown;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.config.Configuration;
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
    static final String MODID   = "ForgeAutoShutdown";
    static final String VERSION = "1.0.0";
    static final Logger LOGGER  = LogManager.getFormatterLogger(MODID);

    @Mod.Instance(MODID)
    static ForgeAutoShutdown INSTANCE;

    Configuration config;
    Timer         timer;

    int    cfgHour   = 6;
    int    cfgMinute = 0;
    String msgWarn   = "Server is shutting down in %m minute(s).";
    String msgKick   = "Scheduled server shutdown";

    @EventHandler
    @SideOnly(Side.CLIENT)
    public void clientPreInit(FMLPreInitializationEvent event)
    {
        LOGGER.error("This mod is useful only on servers; it will do nothing on this client.");
        LOGGER.error("Please consider removing this mod from your installation.");
    }

    @EventHandler
    @SideOnly(Side.SERVER)
    /**
     * Sets up timer thread and the configuration file. Automatically creates the config
     * file and populates it with defaults, if missing.
     */
    public void serverPreInit(FMLPreInitializationEvent event)
    {
        config = new Configuration( event.getSuggestedConfigurationFile() );
        timer  = new Timer("Forge Auto-Shutdown timer");

        cfgHour   = config.getInt("Hour", "Schedule", cfgHour, 0, 23, "");
        cfgMinute = config.getInt("Minute", "Schedule", cfgMinute, 0, 59, "");
        msgWarn   = config.getString("Warn", "Messages", msgWarn, "");
        msgKick   = config.getString("Kick", "Messages", msgKick, "");

        config.save();
    }

    @EventHandler
    @SideOnly(Side.SERVER)
    /**
     * Creates a scheduled task on the mod's timer for the next expected automatic
     * shutdown time, then every 60 seconds after. This allows the ShutdownTask to warn
     * all players of an impending shutdown.
     */
    public void init(FMLInitializationEvent event)
    {
        DateFormat   dateFormat = new SimpleDateFormat("HH:mm:ss dd-MMMM-yyyy");
        ShutdownTask task       = new ShutdownTask();
        Calendar     shutdownAt = Calendar.getInstance();
        shutdownAt.set(Calendar.HOUR_OF_DAY, cfgHour);
        shutdownAt.set(Calendar.MINUTE, cfgMinute);
        shutdownAt.set(Calendar.SECOND, 0);

        // Adjust for when current time surpasses shutdown schedule
        // (e.g. if shutdown time is 07:00 and current time is 13:21)
        if ( shutdownAt.before( Calendar.getInstance() ) )
            shutdownAt.add(Calendar.DAY_OF_MONTH, 1);

        Date shutdownAtDate = shutdownAt.getTime();

        timer.schedule(task, shutdownAtDate, 60 * 1000);
        LOGGER.info( "Next automatic shutdown: %s", dateFormat.format(shutdownAtDate) );
    }
}
