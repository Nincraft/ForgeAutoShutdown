package roycurtis.autoshutdown;

import roycurtis.autoshutdown.util.Chat;

/**
 * Alternative of the vanilla CommandException which translates messages server-side
 */
class CommandException extends net.minecraft.command.CommandException
{
    public CommandException(String msg, Object... parts)
    {
        super( Chat.translate(msg) , parts);
    }
}
