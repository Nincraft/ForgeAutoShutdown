package roycurtis.autoshutdown;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ShutdownCommand implements ICommand
{
    static final List ALIASES = Collections.singletonList("shutdown");
    static final List OPTIONS = Arrays.asList("yes", "no");

    final ForgeAutoShutdown INSTANCE;
    final MinecraftServer   SERVER;

    Date lastVote = new Date();

    ShutdownCommand()
    {
        INSTANCE = ForgeAutoShutdown.INSTANCE;
        SERVER   = MinecraftServer.getServer();
    }

    @Override
    public String getCommandName()
    {
        return "shutdown";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/shutdown <yes|no>";
//        return "§l/shutdown§r - Initiates vote for server shutdown" + " \f" +
//               "§l/shutdown yes§r - Casts yes vote for shutdown"  + " \f" +
//               "§l/shutdown no§r  - Casts no vote for shutdown";
    }

    @Override
    public List getCommandAliases()
    {
        return ALIASES;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        Date now        = new Date();
        long interval   = INSTANCE.cfgVoteInterval * 60 * 1000;
        long difference = now.getTime() - lastVote.getTime();

        if (difference < interval)
        {
            Long   timeLeft = (difference - interval) / 1000;
            String message  = "*** It is too soon since the last vote to initiate" +
                "another one. Try again in %d seconds."
                .replace( "%d", timeLeft.toString() );

            sender.addChatMessage( new ChatComponentText(message) );
            return;
        }

        List players = SERVER.getConfigurationManager().playerEntityList;

        if (players.size() < INSTANCE.cfgMinVoters)
        {
            String message  = "*** Need at least %d players to initiate a vote."
                .replace( "%d", INSTANCE.cfgMinVoters.toString() );

            return;
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_)
    {
        return INSTANCE.cfgVoteEnabled;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_)
    {
        return OPTIONS;
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_)
    {
        return false;
    }

    @Override
    public int compareTo(Object o)
    {
        ICommand command = (ICommand) o;
        return command.getCommandName().compareTo( getCommandName() );
    }
}
