package roycurtis.autoshutdown;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.TimerTask;

public class ShutdownTask extends TimerTask
{
    final ForgeAutoShutdown INSTANCE;
    final MinecraftServer   SERVER;

    boolean executeTick  = false;
    Byte    warningsLeft = 5;

    ShutdownTask()
    {
        INSTANCE = ForgeAutoShutdown.INSTANCE;
        SERVER   = MinecraftServer.getServer();
    }

    @Override
    public void run()
    {
        executeTick = true;
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
        if (!executeTick) return;

        if (warningsLeft == 0)
            performShutdown(Config.msgKick);
        else
            performWarning();

        executeTick = false;
    }

    void performWarning()
    {
        String warning = Config.msgWarn.replace( "%m", warningsLeft.toString() );

        Util.broadcast(SERVER, "*** " + warning);
        ForgeAutoShutdown.LOGGER.info(warning);
        warningsLeft--;
    }

    void performShutdown(String reason)
    {
        reason = Util.translate(reason);

        for ( Object value : SERVER.getConfigurationManager().playerEntityList.toArray() )
        {
            EntityPlayerMP player = (EntityPlayerMP) value;
            player.playerNetServerHandler.kickPlayerFromServer(reason);
        }

        SERVER.initiateShutdown();
    }
}
