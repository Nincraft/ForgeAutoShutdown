package roycurtis.autorestart;

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
    modid   = ForgeAutoRestart.MODID,
    name    = ForgeAutoRestart.MODID,
    version = ForgeAutoRestart.VERSION,

    acceptableRemoteVersions = "*",
    acceptableSaveVersions   = ""
)
public class ForgeAutoRestart
{
    static final String  MODID   = "ForgeAutoRestart";
    static final String  VERSION = "1.0.0";
    static final Logger  LOGGER  = LogManager.getFormatterLogger(MODID);

    @Mod.Instance(MODID)
    static ForgeAutoRestart INSTANCE;

    Configuration config;
    Timer         timer;

    int    cfgHour    = 6;
    int    cfgMinute  = 0;
    String cfgMessage = "Scheduled server shutdown";

    @EventHandler
    @SideOnly(Side.CLIENT)
    public void clientPreInit(FMLPreInitializationEvent event)
    {
        LOGGER.error("This mod is useful only on servers; it will do nothing on this client.");
        LOGGER.error("Please consider removing this mod from your installation.");
    }

    @EventHandler
    @SideOnly(Side.SERVER)
    public void serverPreInit(FMLPreInitializationEvent event)
    {
        config = new Configuration( event.getSuggestedConfigurationFile() );
        timer  = new Timer("Forge AutoRestart timer");

        cfgHour    = config.getInt("Hour", "Schedule", cfgHour, 0, 23, "");
        cfgMinute  = config.getInt("Minute", "Schedule", cfgMinute, 0, 59, "");
        cfgMessage = config.getString("Message", "Shutdown", cfgMessage, "");

        config.save();
    }

    @EventHandler
    @SideOnly(Side.SERVER)
    public void init(FMLInitializationEvent event)
    {
        DateFormat  dateFormat = new SimpleDateFormat("HH:mm:ss dd-MMMM-yyyy");
        RestartTask task       = new RestartTask();
        Calendar    restartAt  = Calendar.getInstance();
        restartAt.set(Calendar.HOUR_OF_DAY, cfgHour);
        restartAt.set(Calendar.MINUTE, cfgMinute);

        // Adjust for when current time surpasses restart schedule
        // (e.g. if restart time is 07:00 and current time is 13:21)
        if ( restartAt.before( Calendar.getInstance() ) )
            restartAt.roll(Calendar.DATE, true);

        Date restartAtDate = restartAt.getTime();

        timer.schedule(task, restartAtDate);
        LOGGER.info( "Next automatic restart: %s", dateFormat.format(restartAtDate) );
    }
}
