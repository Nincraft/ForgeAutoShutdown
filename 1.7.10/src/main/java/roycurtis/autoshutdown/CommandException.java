package roycurtis.autoshutdown;

/**
 * Alternative of the vanilla CommandException which translates messages server-side
 */
class CommandException extends net.minecraft.command.CommandException
{
    public CommandException(String msg, Object... parts)
    {
        super( Util.translate(msg) , parts);
    }
}
