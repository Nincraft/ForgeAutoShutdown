package roycurtis.autoshutdown;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.time.Instant;
import java.util.*;

public class ShutdownCommand implements ICommand
{
    static final List ALIASES = Collections.singletonList("shutdown");
    static final List OPTIONS = Arrays.asList("yes", "no");

    final ForgeAutoShutdown INSTANCE;
    final MinecraftServer   SERVER;

    HashMap<String, Boolean> votes = new HashMap<>();

    Date    lastVote = Date.from(Instant.EPOCH);
    boolean voting   = false;

    ShutdownCommand()
    {
        INSTANCE = ForgeAutoShutdown.INSTANCE;
        SERVER   = MinecraftServer.getServer();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (sender == SERVER)
            throw new CommandException("FAS.error.playersonly");

        if (voting)
            processVote(sender, args);
        else
            initiateVote(sender, args);
    }

    private void initiateVote(ICommandSender sender, String[] args)
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

        Util.broadcast(SERVER, "FAS.msg.votebegun");
        voting = true;
    }

    private void processVote(ICommandSender sender, String[] args)
    {
        if (args.length < 1)
            throw new CommandException("FAS.error.voteinprogress");
        else if ( !OPTIONS.contains( args[0].toLowerCase() ) )
            throw new CommandException("FAS.error.incorrectsyntax");

        String  name = sender.getCommandSenderName();
        Boolean vote = args[0].equalsIgnoreCase("yes");

        if ( votes.containsKey(name) )
            Util.chat(sender, "FAS.msg.votecleared");

        votes.put(name, vote);
        Util.chat(sender, "FAS.msg.voterecorded");
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
        ForgeAutoShutdown.LOGGER.info("Server shutdown initiated by vote");
        INSTANCE.task.performShutdown("FAS.msg.usershutdown");
    }

    private void voteFailure(String reason)
    {
        Util.broadcast(SERVER, reason);
        votes.clear();

        lastVote = new Date();
        voting   = false;
    }

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
}
