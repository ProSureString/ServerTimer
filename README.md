# ServerTimer

A Minecraft Bukkit/Spigot plugin that provides ab SMP start sequence with countdown timers, world border management, and grace period functionality.

## Features

- **Customizable World Border**: Set the initial and target world border size
- **Game Start Countdown**: Visual countdown with boss bar and chat notifications
- **Grace Period**: Configurable PvP-free grace period after game start
- **Visual Feedback**: Boss bar timers, title announcements, and sound effects
- **Multi-World Support**: Works across all server worlds

## Commands

### `/startserver <BorderSize> <StartDuration> <GraceDuration>`

Initiates the server start sequence with the specified parameters:

- `BorderSize`: The target size (in blocks) that the world border will expand to
- `StartDuration`: The countdown duration (in minutes) before the game starts
- `GraceDuration`: The grace period duration (in minutes) during which PvP is disabled

Example: `/startserver 3000 5 10` - Sets a 5-minute start countdown, expands the border to 3000 blocks whne the start countdown ends, and sets a 10-minute grace period after.

## Sequence Overview

1. **Initialization**:
   - PvP is disabled across all worlds
   - World border is set to a small initial size (20 blocks by default)
   - A countdown boss bar appears

2. **Start Countdown**:
   - The boss bar displays the remaining time until game start
   - Changes color from green to yellow at 1 minute remaining
   - Changes to red for the final 15 seconds
   - Final countdown (3-2-1) appears as title messages

3. **Game Start**:
   - "GO!!!" title appears
   - World border begins expanding to the target size
   - Grace period timer begins

4. **Grace Period**:
   - A pink boss bar displays the remaining grace period time
   - Changes to red for the final minute
   - PvP remains disabled during this period

5. **Full Game Start**:
   - PvP is enabled across all worlds
   - A notification appears to all players
   - Thunder sound plays to signify the end of the grace period (for dramatics)

## Installation

1. Download the ServerTimer.jar file
2. Place it in your server's `plugins` folder
3. Restart your server or use `/reload confirm`

## Configuration

~~The plugin uses a configuration file (`config.yml`) which is generated on first run.~~
No config yet. The current configuration system doesn't work.

## Permissions

No special permissions are currently required to use the plugin commands.

## Requirements

- Bukkit/Spigot/Paper server
- Minecraft version compatibility: [1.21.1+]

## Building from Source

1. Clone the repository
2. Open in Intellij IDEA [Other IDEs untested but should work]
3. Build using Maven: `mvn install`
4. The compiled jar will be in the `target` folder


## Support

For issues, feature requests, or questions, please [Contact ProSureString](https://discord.com/users/678287437583351808) on discord..

---

Created by ProSureString. All Rights Reserved
