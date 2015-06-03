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

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    static final Boolean DEBUG   = Boolean.parseBoolean( System.getProperty("debug", "false") );

    Configuration config;

    Timer timer = new Timer("Forge AutoRestart timer");

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
    }

    @EventHandler
    @SideOnly(Side.SERVER)
    public void init(FMLInitializationEvent event)
    {
        Calendar restartAt = Calendar.getInstance();
        restartAt.set(Calendar.HOUR_OF_DAY, 6);
        restartAt.set(Calendar.MINUTE,      30);
        restartAt.set(Calendar.SECOND,      0);

        if ( restartAt.before( Calendar.getInstance() ) )
        {
            restartAt.roll(Calendar.DATE, true);
            LOGGER.debug("Rolling restart date forward");
        }

        timer.schedule(new RestartTask(), restartAt.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd-MMMM-yyyy");
        LOGGER.info("Automatic restart at %s (%d)", formatter.format(restartAt.getTime()), restartAt.getTimeInMillis());
    }
}
