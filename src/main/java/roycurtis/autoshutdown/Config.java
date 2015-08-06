package roycurtis.autoshutdown;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Static container class for mod's configuration values. Handles saving and loading.
 */
class Config
{
    private static final String SCHEDULE = "Schedule";
    private static final String VOTING   = "Voting";
    private static final String MESSAGES = "Messages";

    static Configuration config;

    static boolean scheduleEnabled = true;
    static int     scheduleHour    = 6;
    static int     scheduleMinute  = 0;

    static boolean voteEnabled  = true;
    static int     voteInterval = 15;
    static int     minVoters    = 2;
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

        config.setCategoryComment(SCHEDULE,
            "All times are 24 hour (military) format, relative to machine's local time");

        scheduleEnabled = config.getBoolean("Enabled", SCHEDULE, scheduleEnabled,
            "If true, server will automatically shutdown at given time of day");
        scheduleHour    = config.getInt("Hour", SCHEDULE, scheduleHour, 0, 23,
            "Hour of the shutdown process (e.g. 8 for 8 AM)");
        scheduleMinute  = config.getInt("Minute", SCHEDULE, scheduleMinute, 0, 59,
            "Minute of the shutdown process (e.g. 30 for half-past)");

        config.setCategoryComment(VOTING,
            "Allows players to shut down the server without admin intervention");

        voteEnabled  = config.getBoolean("VoteEnabled", VOTING, voteEnabled,
            "If true, players may vote to shut server down using '/shutdown'");
        voteInterval = config.getInt("VoteInterval", VOTING, voteInterval, 0, 1440,
            "Min. minutes after a failed vote before new one can begin");
        minVoters    = config.getInt("MinVoters", VOTING, minVoters, 1, 999,
            "Min. players online required to begin a vote");
        maxNoVotes   = config.getInt("MaxNoVotes", VOTING, maxNoVotes, 1, 999,
            "Max. 'No' votes to cancel a shutdown");

        config.setCategoryComment(MESSAGES,
            "Customizable messages for the shutdown process");

        msgWarn = config.getString("Warn", MESSAGES, msgWarn,
            "Pre-shutdown warning message. Use %m for minutes remaining");
        msgKick = config.getString("Kick", MESSAGES, msgKick,
            "Message shown to player on disconnect during shutdown");

        config.save();
    }

    static boolean isNothingEnabled()
    {
        return !scheduleEnabled && !voteEnabled && !watchdogEnabled;
    }

    private Config() { }
}
