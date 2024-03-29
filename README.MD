# WORK IN PROGRESS

attempt at source engine bhopping and strafing in minecraft forge. still needs a lot of work its far from tested

## features

- **air strafing**: gain speed while airborne by strafing
- **bhopping**: chain together jumps to maintain horizontal speed
- **air acceleration**: gain speed by strafing left and right
- **ground speed**: desired responsiveness when moving on the ground
- **maxvelocity**: preventing players from reaching crazy speeds
- **friction**: control the rate at which players decelerate when not actively accelerating

## commands

- `/sourcemovement bhop`: enables or disables the plugin for self
- `/sourcemovement bhoptarget <targets> <true/false>`: enables or disables the plugin for the specified player(s)
- `/sourcemovement permissions`: displays the current permissions
- `/sourcemovement setPermission <command> <level>`: sets the permission level required to use a specific command (hardcapped max limits have to be changed in config file)
- `/sourcemovement airAcceleration <value>`: sets the air acceleration value
- `/sourcemovement airStrafeSpeed <value>`: sets the maximum air strafe speed, acts kinda like wishvel
- `/sourcemovement groundSpeed <value>`: how fast players move on the ground
- `/sourcemovement bunnyHopMultiplier <value>`: higher values result in greater speed increases when bunny hopping
- `/sourcemovement maxAirSpeed <value>`: sets the maxvelocity
- `/sourcemovement friction <value>`: how quickly players decelerate when not actively accelerating
- `/sourcemovement config`: displays the current config
- `/sourcemovement help`: displays a list of commands

## permissions and hard caps

permissions and configs stored and saved in `sourcemovement.toml`

hard cap max limits to physics features can only be changed this way

- `maxAirAcceleration`
- `maxAirStrafeSpeed`
- `maxGroundSpeed`
- `maxBunnyHopMultiplier`
- `maxMaxAirSpeed`
- `maxFriction`

permission levels:

- `PLAYER`
- `ADMIN`
- `OWNER`

## todo

- .mods file
- create paths
- improve physics
- begin testing