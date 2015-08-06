package roycurtis.autoshutdown;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

enum WatchdogState
{
    /** Monitoring server for unresponsiveness */
    MONITOR,
    /** Monitoring a possibly hung server */
    HUNG,
    /** Monitoring TPS going below threshold */
    SLOWED,
    /** Trying to shutdown the server cleanly */
    SOFT_EXIT,
    /** Trying to shutdown the server hard */
    HARD_EXIT
}

/**
 * Singleton that acts as a timer task for monitoring server stalls
 */
public class WatchdogTask extends TimerTask
{
    private static WatchdogTask    INSTANCE;
    private static MinecraftServer SERVER;
    private static Logger          LOGGER;
    private static Timer           TIMER;

    public static void create()
    {
        if (INSTANCE != null)
            throw new RuntimeException("WatchdogTask can only be created once");

        INSTANCE = new WatchdogTask();
        SERVER   = MinecraftServer.getServer();
        LOGGER   = ForgeAutoShutdown.LOGGER;
        TIMER    = new Timer("ForgeAutoShutdown watchdog");

        int intervalMs = Config.watchdogInterval * 1000;
        TIMER.schedule(INSTANCE, intervalMs, intervalMs);
        LOGGER.debug("Watchdog timer running");
    }

    private WatchdogState state = WatchdogState.MONITOR;

    private int lastServerTick = 0;
    private int hungTicks      = 0;

    @Override
    public void run()
    {
        LOGGER.trace("Watchdog timer called");

        switch (state)
        {
            case MONITOR:
                onMonitor();  break;
            case HUNG:
                onHung();     break;
            case SLOWED:
                onSlowed();   break;
            case SOFT_EXIT:
                onSoftExit(); break;
            case HARD_EXIT:
                onHardExit(); break;
        }
    }

    /**
     * Checks if server is hung on a particular tick, then if TPS is too low for too
     * long
     */
    private void onMonitor()
    {
        LOGGER.trace("Handling MONITOR state on watchdog");

        double avgLatency = MathHelper.average(SERVER.tickTimeArray) * 0.000001;
        double avgTPS     = Math.min(1000 / avgLatency, 20);

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace("Watchdog: 100 tick avg. latency: %.2f / 50 ms", avgLatency);
            LOGGER.trace("Watchdog: 100 tick avg. TPS: %.2f / 20", avgTPS);
        }

        // First, check if server is hung on a tick
        int serverTick = SERVER.getTickCounter();
        if (serverTick == lastServerTick)
        {
            LOGGER.debug("No advance in server ticks; switching to HUNG state");
            state     = WatchdogState.HUNG;
            hungTicks = 0;
            onHung();
            return;
        }
        else lastServerTick = serverTick;

        // Finally, check if TPS has been too low for too long
        // TODO
    }

    /** Monitors an unresponsive server, then proceeds to kill it if confirmed */
    private void onHung()
    {
        LOGGER.trace("Handling HUNG state on watchdog");

        // Recover to MONITOR state if no longer hanging
        int serverTick = SERVER.getTickCounter();
        if (serverTick != lastServerTick)
        {
            LOGGER.debug("Server no longer hung; switching to MONITOR state");
            state = WatchdogState.MONITOR;
            return;
        }

        hungTicks++;
        int hangSec = hungTicks * Config.watchdogInterval;
        LOGGER.trace("Server hanging: %d seconds", hangSec);

        if (hangSec > Config.maxTickTimeout)
        {
            state = Config.attemptSafeKill
                ? WatchdogState.SOFT_EXIT
                : WatchdogState.HARD_EXIT;

            LOGGER.debug("Server confirmed hung; switching to %s state", state);
        }
    }

    private void onSlowed()
    {
        // TODO: implement
    }

    /**
     * Attempts a "soft exit" of the server by trying to manually save all worlds.
     * Watchdog will never recover back to MONITOR state from here.
     */
    private void onSoftExit()
    {
        LOGGER.trace("Handling SOFT_EXIT state on watchdog");
        // Use exitJava because MinecraftServer registers a shutdown hook
        FMLCommonHandler.instance().exitJava(1, false);

        // TODO: timeout to hard exit
    }

    /**
     * Attempts a "hard exit" of the server. Watchdog will never recover back to MONITOR
     * state from here.
     */
    private void onHardExit()
    {
        LOGGER.fatal("Attempting a hard kill of the server - data may be lost!");
        FMLCommonHandler.instance().exitJava(1, true);
    }

    private WatchdogTask() { }
}
