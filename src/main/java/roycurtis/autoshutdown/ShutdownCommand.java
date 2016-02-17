package roycurtis.autoshutdown;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;
import roycurtis.autoshutdown.util.Chat;
import roycurtis.autoshutdown.util.Server;

import java.util.*;

/**
 * Singleton that handles the `/shutdown` voting command
 */
public class ShutdownCommand implements ICommand
{
    static final List ALIASES = Collections.singletonList("shutdown");
    static final List OPTIONS = Arrays.asList("yes", "no");

    private static ShutdownCommand INSTANCE;
    private static MinecraftServer SERVER;
    private static Logger          LOGGER;

    HashMap<String, Boolean> votes = new HashMap<>();

    Date    lastVote = new Date(0);
    boolean voting   = false;

    /** Creates and registers the `/shutdown` command for use */
    public static void create(FMLServerStartingEvent event)
    {
        if (INSTANCE != null)
            throw new RuntimeException("ShutdownCommand can only be created once");

        INSTANCE = new ShutdownCommand();
        SERVER   = MinecraftServer.getServer();
        LOGGER   = ForgeAutoShutdown.LOGGER;

        event.registerServerCommand(INSTANCE);
        LOGGER.debug("`/shutdown` command registered");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (sender == SERVER)
            throw new CommandException("FAS.error.playersonly");

        if (voting)
            processVote(sender, args);
        else
            initiateVote(args);
    }

    private ShutdownCommand() { }

    private void initiateVote(String[] args) throws CommandException
    {
        if (args.length >= 1)
            throw new CommandException("FAS.error.novoteinprogress");

        Date now        = new Date();
        long interval   = Config.voteInterval * 60 * 1000;
        long difference = now.getTime() - lastVote.getTime();

        if (difference < interval)
            throw new CommandException("FAS.error.toosoon", (interval - difference) / 1000);

        List players = SERVER.getConfigurationManager().playerEntityList;

        if (players.size() < Config.minVoters)
            throw new CommandException("FAS.error.notenoughplayers", Config.minVoters);

        Chat.toAll(SERVER, "FAS.msg.votebegun");
        voting = true;
    }

    private void processVote(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
            throw new CommandException("FAS.error.voteinprogress");
        else if ( !OPTIONS.contains( args[0].toLowerCase() ) )
            throw new CommandException("FAS.error.votebadsyntax");

        String  name = sender.getName();
        Boolean vote = args[0].equalsIgnoreCase("yes");

        if ( votes.containsKey(name) )
            Chat.to(sender, "FAS.msg.votecleared");

        votes.put(name, vote);
        Chat.to(sender, "FAS.msg.voterecorded");
        checkVotes();
    }

    private void checkVotes()
    {
        int players = SERVER.getConfigurationManager().playerEntityList.size();

        if (players < Config.minVoters)
        {
            voteFailure("FAS.fail.notenoughplayers");
            return;
        }

        int yes = Collections.frequency(votes.values(), true);
        int no  = Collections.frequency(votes.values(), false);

        if (no >= Config.maxNoVotes)
        {
            voteFailure("FAS.fail.maxnovotes");
            return;
        }

        if (yes + no == players)
            voteSuccess();
    }

    private void voteSuccess()
    {
        LOGGER.info("Server shutdown initiated by vote");
        Server.shutdown("FAS.msg.usershutdown");
    }

    private void voteFailure(String reason)
    {
        Chat.toAll(SERVER, reason);
        votes.clear();

        lastVote = new Date();
        voting   = false;
    }

    // <editor-fold desc="ICommand">
    @Override
    public String getCommandName()
    {
        return "shutdown";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/shutdown <yes|no>";
    }

    @Override
    public List getCommandAliases()
    {
        return ALIASES;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return Config.voteEnabled;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] strings, BlockPos blockPos)
    {
        return OPTIONS;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int idx)
    {
        return false;
    }

    @Override
    public int compareTo(ICommand command)
    {
        return command.getCommandName().compareTo( getCommandName() );
    }

}
