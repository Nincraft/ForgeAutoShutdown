ForgeAutoShutdown is a server-only mod that schedules a specific time of the day for the server to shut down. This allows the server to be automatically restarted daily by a shell script or Windows service.

# Requirements

* Minecraft Forge server for 1.7.10
  * At least [10.13.2.1291](http://files.minecraftforge.net/maven/net/minecraftforge/forge/1.7.10-10.13.2.1291/forge-1.7.10-10.13.2.1291-installer.jar)
  * Tested to work on [10.13.4.1448](http://files.minecraftforge.net/maven/net/minecraftforge/forge/1.7.10-10.13.4.1448-1.7.10/forge-1.7.10-10.13.4.1448-1.7.10-installer.jar)
* A [wrapper script](https://github.com/Gamealition/Minecraft-Scripts), if server intends to restart after shutdown

# Installation

1. Download the [latest release JAR](https://github.com/Gamealition/ForgeAutoShutdown/releases) or clone this repository & build a JAR file
2. Place the JAR file in the `mods/` directory of the server
3. Run/restart the server
4. Open `config/ForgeAutoShutdown.cfg` and modify configuration to desired values
5. Restart the server for changes to take effect

## Example config

This example configures a server to automatically shutdown at 9:30AM (09:30) server time. Warnings will begin at 9:25AM (09:25). The messages are intended for servers that use ForgeAutoShutdown for automatic daily restarts.

```
# Configuration file

messages {
    #  [default: Scheduled server shutdown]
    S:Kick=Daily restart; please return in 5 minutes

    #  [default: Server is shutting down in %m minute(s).]
    S:Warn=Server will perform its daily restart in %m minute(s).
}


schedule {
    #  [range: 0 ~ 23, default: 6]
    I:Hour=9

    #  [range: 0 ~ 59, default: 0]
    I:Minute=25
}
```

## Verification
ForgeAutoShutdown will log a message at the INFO level on server startup, with a date and time of the next scheduled shutdown. For example:

```[10:50:09] [Server thread/INFO] [forgeautoshutdown]: Next automatic shutdown: 08:30:00 19-June-2015```

If this message is missing, the mod has not been correctly installed. If the mod is installed on a Minecraft client, it will log an ERROR to the console and not perform any function. It will not crash or disable the client.

Scheduled shutdown will always happen within the next 24 hours after server startup. This means that if the server starts and has missed the shutdown time even by a few minutes, it will schedule for the next day.

# Building

## Requirements

* [Gradle installation with gradle binary in PATH](http://www.gradle.org/installation). Unlike the source package provided by Forge, this repository does not include a gradle wrapper or distribution.

## Usage
Simply execute `gradle setupDevWorkspace` in the root directory of this repository. Then execute `gradle build`. If subsequent builds cause problems, do `gradle clean`.
