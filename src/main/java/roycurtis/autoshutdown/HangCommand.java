package roycurtis.autoshutdown;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * Singleton that handles the `/hang` debugging command
 */
public class HangCommand implements ICommand
{
    static final List ALIASES = Collections.singletonList("hang");

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
        LOGGER.warn("Initiating a server hang by Thread sleep");
        while (true)
            try
            {
                Thread.sleep(1000);
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
        return "/hang <sleep|wait>";
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
        return Collections.emptyList();
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
