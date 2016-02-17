package roycurtis.autoshutdown.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.FakePlayer;
import org.apache.logging.log4j.Logger;
import roycurtis.autoshutdown.ForgeAutoShutdown;

/**
 * Static utility class for server functions
 */
public class Server
{
    // Safe to declare here. This class should only ever be loaded if any of its methods
    // are called, which by then these should be available.
    static final MinecraftServer SERVER = MinecraftServer.getServer();
    static final Logger          LOGGER = ForgeAutoShutdown.LOGGER;

    /** Kicks all players from the server with given reason, then shuts server down */
    public static void shutdown(String reason)
    {
        reason = Chat.translate(reason);

        for ( Object value : SERVER.getConfigurationManager().playerEntityList.toArray() )
        {
            EntityPlayerMP player = (EntityPlayerMP) value;
            player.playerNetServerHandler.kickPlayerFromServer(reason);
        }

        LOGGER.debug("Shutdown initiated because: %s", reason);
        SERVER.initiateShutdown();
    }

    /** Checks if any non-fake player is present on the server */
    public static boolean hasRealPlayers()
    {
        for ( Object value : SERVER.getConfigurationManager().playerEntityList.toArray() )
            if (value instanceof EntityPlayerMP)
            if ( !(value instanceof FakePlayer) )
                return true;

        return false;
    }
}
