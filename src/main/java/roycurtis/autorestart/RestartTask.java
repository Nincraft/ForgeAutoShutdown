package roycurtis.autorestart;

import java.util.TimerTask;

public class RestartTask extends TimerTask
{
    @Override
    public void run()
    {
        ForgeAutoRestart.LOGGER.fatal("TIMER FIRED");
    }
}
