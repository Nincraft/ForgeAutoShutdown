ForgeAutoShutdown is a server-only mod that schedules a specific time of the day for the server to shut down. This allows the server to be automatically restarted daily by a shell script or Windows service.

# Building

## Requirements

* [Gradle installation with gradle binary in PATH](http://www.gradle.org/installation)

	Unlike the source package provided by Forge, this repository does not include a gradle wrapper or distribution.

## Usage
Simply execute `gradle setupDevWorkspace` in the root directory of this repository. Then execute `gradle build`. If subsequent builds cause problems, do `gradle clean`.