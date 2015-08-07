package roycurtis.autoshutdown;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Singleton that handles the `/hang` debugging command
 */
public class HangCommand implements ICommand
{
    static final List ALIASES = Collections.singletonList("hang");
    static final List OPTIONS = Arrays.asList("1000", "2500", "5000", "10000", "25000", "60000");

    private static HangCommand INSTANCE;
    private static Logger      LOGGER;

    /** Creates and registers the `/hang` command for use */
    public static void create(FMLServerStartingEvent event)
    {
        if (INSTANCE != null)
            throw new RuntimeException("HangCommand can only be created once");

        INSTANCE = new HangCommand();
        LOGGER   = ForgeAutoShutdown.LOGGER;

        event.registerServerCommand(INSTANCE);
        LOGGER.debug("`/hang` command registered");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {

        int sleep = Integer.MAX_VALUE;
        if (args.length >= 1)
            sleep = CommandBase.parseInt(sender, args[0]);

        LOGGER.warn("Sleeping main server thread by %s ms", sleep);

        try
        {
            Thread.sleep(sleep);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private HangCommand() { }

    // <editor-fold desc="ICommand">
    @Override
    public String getCommandName()
    {
        return "hang";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/hang [milliseconds]";
    }

    @Override
    public List getCommandAliases()
    {
        return ALIASES;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return sender instanceof MinecraftServer;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return OPTIONS;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int idx)
    {
        return false;
    }

    @Override
    public int compareTo(Object o)
    {
        ICommand command = (ICommand) o;
        return command.getCommandName().compareTo( getCommandName() );
    }
    // </editor-fold>
}
