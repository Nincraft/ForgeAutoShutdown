package roycurtis.autoshutdown;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;

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
        if (voting)
            processVote(sender, args);
        else
            initiateVote(sender, args);
    }

    private void initiateVote(ICommandSender sender, String[] args)
    {
        if (args.length >= 1)
            throw new WrongUsageException("FAS.error.novoteinprogress");

        Date now        = new Date();
        long interval   = Config.voteInterval * 60 * 1000;
        long difference = now.getTime() - lastVote.getTime();

        if (difference < interval)
            throw new CommandException("FAS.error.toosoon", (interval - difference) / 1000);

        List players = SERVER.getConfigurationManager().playerEntityList;

        if (players.size() < Config.minVoters)
            throw new CommandException("FAS.error.notenoughplayers", Config.minVoters);

        voting = true;
    }

    private void processVote(ICommandSender sender, String[] args)
    {
        if (args.length < 1)
            throw new CommandException("FAS.error.voteinprogress");
        else if ( !OPTIONS.contains( args[0].toLowerCase() ) )
            throw new WrongUsageException("FAS.error.incorrectsyntax");
    }

    private void voteSuccess()
    {
        ForgeAutoShutdown.LOGGER.info("Server shutdown initiated by vote");
        INSTANCE.task.performShutdown();
    }

    private void voteFailure()
    {
        String msg = StatCollector.translateToFallback("FAS.msg.votefailed");

        SERVER.getConfigurationManager()
            .sendChatMsg( new ChatComponentText(msg) );

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
