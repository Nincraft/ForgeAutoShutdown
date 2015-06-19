package roycurtis.autoshutdown;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.TimerTask;

public class ShutdownTask extends TimerTask
{
    final ForgeAutoShutdown INSTANCE;
    final MinecraftServer   SERVER;

    boolean executeTick  = false;
    Byte    warningsLeft = 5;

    public ShutdownTask()
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
            performShutdown();
        else
            performWarning();

        executeTick = false;
    }

    void performWarning()
    {
        String warning = INSTANCE.msgWarn.replace( "%m", warningsLeft.toString() );

        IChatComponent warningChat = new ChatComponentText("*** " + warning);

        for (Object player : SERVER.getConfigurationManager().playerEntityList)
            ( (EntityPlayerMP) player ).addChatMessage(warningChat);

        ForgeAutoShutdown.LOGGER.info(warning);
        warningsLeft--;
    }

    void performShutdown()
    {
        for (Object value : SERVER.getConfigurationManager().playerEntityList)
        {
            EntityPlayerMP player = (EntityPlayerMP) value;
            player.playerNetServerHandler.kickPlayerFromServer(INSTANCE.msgKick);
        }

        SERVER.initiateShutdown();
    }
}
