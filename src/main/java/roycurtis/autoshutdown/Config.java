package roycurtis.autoshutdown;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Static container class for mod's configuration values. Handles saving and loading.
 */
class Config
{
    static Configuration config;

    static int hour   = 6;
    static int minute = 0;

    static boolean voteEnabled  = true;
    static int     voteInterval = 15;
    static Integer minVoters    = 2;
    static int     maxNoVotes   = 1;

    static String msgWarn = "Server is shutting down in %m minute(s).";
    static String msgKick = "Scheduled server shutdown";

    /**
     * Populates the config values with saved or defaults. Automatically creates the
     * config file with defaults, if missing.
     * @param configFile File to use for loading/saving
     */
    static void init(File configFile)
    {
        config = new Configuration(configFile);

        hour   = config.getInt("Hour", "Schedule", hour, 0, 23, "");
        minute = config.getInt("Minute", "Schedule", minute, 0, 59, "");

        voteEnabled  = config.getBoolean("VoteEnabled", "Voting", voteEnabled, "");
        voteInterval = config.getInt("VoteInterval", "Voting", voteInterval, 0, 99, "");
        minVoters    = config.getInt("MinVoters", "Voting", minVoters, 0, 99, "");
        maxNoVotes   = config.getInt("MaxNoVotes", "Voting", maxNoVotes, 1, 99, "");

        msgWarn = config.getString("Warn", "Messages", msgWarn, "");
        msgKick = config.getString("Kick", "Messages", msgKick, "");

        config.save();
    }

    private Config() { }
}
