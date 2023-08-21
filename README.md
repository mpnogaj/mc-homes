# mc-homes
mc-homes is a Minecraft mod that provides /home and /waypoint commands and dedicated key bindings.

# Homes and waypoints
Both homes and waypoints can be placed by players to later teleport to them. The main difference, between those two, is that homes are private to each player while waypoints are public.

# Available commands

### Home commands
- `/home [<name>]` - teleports to home named `<name>`, or to a default one when `<name>` is not provided
- `/setHome <name>` - creates a home named `<name>`
- `/removeHome <name>` - removes home named `<name>`
- `/setDefaultHome <name>` - sets home named `<name>` as a default home

### Waypoint commands
- `/waypoint [<name>]` - teleports to waypoint named `<name>`, or to a default one when `<name>` is not provided
- `/setWaypoint <name>` - creates a waypoint named `<name>`
- `/removeWaypoint <name>` - removes waypoint named `<name>`
- `/setDefaultWaypoint <name>` - sets waypoint named `<name>` as a default waypoint

# Key bindings
This mod also provides 2 key bindings with execute `/home` and `/waypoint` commands (without `<name>` parameter). By default, they are set to F23 and F24 keys respectively.
