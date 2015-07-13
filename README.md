ForgeAutoShutdown is a server-only mod that schedules a specific time of the day for the server to shut down. This allows the server to be automatically restarted daily by a shell script or Windows service. Players can also be allowed to vote in a manual shutdown, so a lagged out server would not require admin intervention.

# License
The codebase is licensed under the MIT license. Therefore, you **are free** to redistribute this mod or any of its derivatives as part of a modpack. You **are free** to use this mod for commercial intents. You **are free** to use any or all of this code as part of another code base, as a derivative or as a clone. You have **zero obligation** to notify me or request permission to use this codebase in its raw or compiled forms.

For the full legal code, see `LICENSE.md`.

# Requirements

* Minecraft Forge server for...
  * 1.7.10, at least [10.13.4.1448](http://files.minecraftforge.net/maven/net/minecraftforge/forge/1.7.10-10.13.4.1448-1.7.10/forge-1.7.10-10.13.4.1448-1.7.10-installer.jar)
  * 1.8, at least [11.14.3.1450](http://files.minecraftforge.net/maven/net/minecraftforge/forge/1.8-11.14.3.1450/forge-1.8-11.14.3.1450-installer.jar)
* A [wrapper script](https://github.com/Gamealition/Minecraft-Scripts), if server intends to restart after shutdown

# Installation

1. Download the [latest release JAR](https://github.com/Gamealition/ForgeAutoShutdown/releases) or clone this repository & build a JAR file
2. Place the JAR file in the `mods/` directory of the server
3. Run/restart the server
4. Open `config/ForgeAutoShutdown.cfg` and modify configuration to desired values
5. Restart the server for changes to take effect

## Example config

This example configures a server to automatically shutdown at 9:30AM (09:30) server time. Warnings will begin at 9:25AM (09:25). Voting is enabled, where two players are required to be online to start a vote. The messages are intended for servers that use ForgeAutoShutdown for automatic daily restarts.

```
messages {
    #  [default: Scheduled server shutdown]
    S:Kick=Daily restart; please return in 5 minutes

    #  [default: Server is shutting down in %m minute(s).]
    S:Warn=Server will perform its daily restart in %m minute(s).
}

schedule {
    # Hour of the shutdown process (e.g. 8 for 8 AM) [range: 0 ~ 23, default: 6]
    I:Hour=9

    # Minute of the shutdown process (e.g. 30 for half-past) [range: 0 ~ 59, default: 0]
    I:Minute=25
}

voting {
    # If true, players may vote to shut server down using '/shutdown' [default: true]
    B:VoteEnabled=true
    
    # Max. 'No' votes to cancel a shutdown [range: 1 ~ 999, default: 1]
    I:MaxNoVotes=1

    # Min. players online required to begin a vote [range: 1 ~ 999, default: 2]
    I:MinVoters=2

    # Min. minutes after a failed vote before new one can begin [range: 0 ~ 1440, default: 15]
    I:VoteInterval=15
}
```

## Verification
ForgeAutoShutdown will log a message at the INFO level on server startup, with a date and time of the next scheduled shutdown. For example:

```[10:50:09] [Server thread/INFO] [forgeautoshutdown]: Next automatic shutdown: 08:30:00 19-June-2015```

If this message is missing, the mod has not been correctly installed. If the mod is installed on a Minecraft client, it will log an ERROR to the console and not perform any function. It will not crash or disable the client.

Scheduled shutdown will always happen within the next 24 hours after server startup. This means that if the server starts and has missed the shutdown time even by a few minutes, it will schedule for the next day.

## Voting

If enabled, players may vote a manual shutdown. To do so, a player must execute `/shutdown`. Then, **all** players (including the vote initiator) must vote using `/shutdown yes` or `/shutdown no`.

If the amount of `no` votes reach a maximum threshold, the vote fails. If a vote is cast and too many players have disconnected in the meantime, the vote fails. If a vote fails, another one will not be able to start until a configured amount of minutes has passed.

If the vote succeeds, the server will instantly shutdown without warning. If an appropriate means of automatic restart is in place, it should be expected that the server will go up within a few minutes.

# Building

## Requirements

* [Gradle installation with gradle binary in PATH](http://www.gradle.org/installation). Unlike the source package provided by Forge, this repository does not include a gradle wrapper or distribution.

## Usage
Simply execute `gradle setupDevWorkspace` in the root directory of this repository. Then execute `gradle build`. If subsequent builds cause problems, do `gradle clean`.
