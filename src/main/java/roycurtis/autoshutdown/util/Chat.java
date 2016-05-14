package roycurtis.autoshutdown.util;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

/**
 * Static utility class for chat functions (syntactic sugar)
 */
public class Chat
{

    /**
     * Broadcasts an auto. translated, formatted encapsulated message to all players
     * @param server Server instance to broadcast to
     * @param msg String or language key to broadcast
     * @param parts Optional objects to add to formattable message
     */
    public static void toAll(MinecraftServer server, TextComponentString msg)
    {
        server.addChatMessage( msg );
    }

    /**
     * Sends an automatically translated, formatted & encapsulated message to a player
     * @param sender Target to send message to
     * @param msg String or language key to broadcast
     * @param parts Optional objects to add to formattable message
     */
    public static void to(ICommandSender sender, TextComponentString msg)
    {
        sender.addChatMessage( msg );
    }

}
