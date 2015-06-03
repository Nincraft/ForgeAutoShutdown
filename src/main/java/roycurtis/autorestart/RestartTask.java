package roycurtis.autorestart;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.TimerTask;

public class RestartTask extends TimerTask
{
    @Override
    public void run()
    {
        ForgeAutoRestart instance = ForgeAutoRestart.INSTANCE;
        MinecraftServer  server   = MinecraftServer.getServer();

        for (Object value : server.getConfigurationManager().playerEntityList)
        {
            EntityPlayerMP player = (EntityPlayerMP) value;
            player.playerNetServerHandler.kickPlayerFromServer(instance.cfgMessage);
        }

        server.initiateShutdown();
    }
}
