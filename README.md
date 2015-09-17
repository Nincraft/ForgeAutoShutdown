ForgeAutoShutdown is a server-only mod that can:

* Schedules a specific time of the day for the server to shut down. This allows the server to be automatically restarted daily by a shell script, Windows batch file or service.
* Allow players to vote for a manual shutdown, so a lagged out server would not require admin intervention
* Shutdown or kill a server that is hung (stalled) or laggy

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

# Features

*Any of these features may be disabled in the config*

## Scheduled daily shutdown
ForgeAutoShutdown will log a message at the INFO level on server startup, with a date and time of the next scheduled shutdown. For example:

`[10:50:09] [Server thread/INFO] [ForgeAutoShutdown]: Next automatic shutdown: 08:30:00 19-June-2015`

If this message is missing, the mod has not been correctly installed or the schedule is disabled in config. If the mod is installed on a Minecraft client, it will log an ERROR to the console and not perform any function. It will not crash or disable the client.

Scheduled shutdown will always happen within the next 24 hours after server startup. This means that if the server starts and has missed the shutdown time even by a few minutes, it will schedule for the next day.

### Warnings
By default a scheduled shutdown will give a warning to all players, each minute for five minutes, after the scheduled time. This can be disabled by setting `Warnings` to `false`. This means the server will shutdown, without warning, by the scheduled time.

### Delay
If desired, the shutdown can be delayed by a configurable amount if players are still on the server. To enable this, set `Delay` to true and adjust `DelayBy` to the amount of minutes to delay.

The shutdown will be repeatedly delayed until the server is empty. When checking if the server for players, fake players (e.g. BuildCraft's quarry) are excluded. Note that shutdown warnings are ineffective with delays, and a pending shutdown will be cancelled if a player comes online during the countdown.


## Voting

If enabled, players may vote a manual shutdown. To do so, a player must execute `/shutdown`. Then, **all** players (including the vote initiator) must vote using `/shutdown yes` or `/shutdown no`.

If the amount of `no` votes reach a maximum threshold, the vote fails. If a vote is cast and too many players have disconnected in the meantime, the vote fails. If a vote fails, another one will not be able to start until a configured amount of minutes has passed.

If the vote succeeds, the server will instantly shutdown without warning. If an appropriate means of automatic restart is in place, it should be expected that the server will go up within a few minutes.

## Watchdog

If enabled, a watchdog thread can periodically watch the server for unresponsiveness. By default, it checks every 10 seconds:

* Whether the server is hanging (or "stalling") on a tick
* Whether the TPS stays below a certain amount for a certain length of time

If either problem is detected, the watchdog will try a soft kill (or a hard kill, if configured). This makes the server try to save all its data before shutting down. If a soft kill takes longer than ten seconds, the watchdog will do a hard kill.

# Building

## Requirements

* [Gradle installation with gradle binary in PATH](http://www.gradle.org/installation). Unlike the source package provided by Forge, this repository does not include a gradle wrapper or distribution.

## Usage
Simply execute `gradle setupCIWorkspace` in the root directory of this repository. Then execute `gradle build`. If subsequent builds cause problems, do `gradle clean`.

# Debugging

ForgeAutoShutdown makes use of `DEBUG` and `TRACE` logging levels for debugging. To enable these messages, append this line to the server's JVM arguments:

> `-Dlog4j.configurationFile=log4j.xml`

Then in the root directory of the server, create the file `log4j.xml` with these contents:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration monitorInterval="5">
  <Appenders>
    <Console name="Console" target="SYSTEM_OUT">
      <PatternLayout pattern="[%d{HH:mm:ss} %-4level] %logger{36}: %msg%n"/>
    </Console>
  </Appenders>
  <Loggers>
    <Root level="INFO">
      <AppenderRef ref="Console"/>
    </Root>
    <Logger name="ForgeAutoShutdown" level="ALL" additivity="false">
      <AppenderRef ref="Console"/>
    </Logger>
  </Loggers>
</Configuration>
```
