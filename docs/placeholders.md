# Placeholders

| Placeholder                                             | Description                                                            |
|---------------------------------------------------------|------------------------------------------------------------------------|
| `votiful_group[;<filter1>;<filter2>;...]`               | Returns the total votes of the group. Filters are optional.            |
| `votiful_server;<serverName>[;<filter1>;<filter2>;...]` | Returns the total votes of the specified server. Filters are optional. |
| `votiful_server;@current[;<filter1>;<filter2>;...]`     | Returns the total votes of the current server. Filters are optional.   |
| `votiful_player[;<playerName>;<filter1>;<filter2>;...]` | Returns the total votes of the specified player. Filters are optional. |

# Filters

Filters are in the format of `filterName=filterValue`. Filters are separated by a semicolon (
`filterName=filterValue;filterName=filterValue`).

| Filter Name | Description               |
|-------------|---------------------------|
| `server`    | Matches the server name.  |
| `player`    | Matches the player name.  |
| `service`   | Matches the service name. |

# Examples

- `votiful_group` - Returns the total votes of the group.
- `votiful_group;service=PlanetMinecraft` - Returns the total votes of the group from the service `PlanetMinecraft`.
- `votiful_server;@current` - Returns the total votes of the current server.
- `votiful_server;@current;service=PlanetMinecraft;player=Notch` - Returns the total votes of the current server from
  the service `PlanetMinecraft` and the player `Notch`.
- `votiful_player` - Returns the total votes of the player.
- `votiful_player;server=lobby` - Returns the total votes of the player from the server `lobby`.