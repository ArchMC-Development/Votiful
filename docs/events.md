# Format

```yaml
events:
  <event-name>:
    key-type: <key-type>
    value: <value>
    repeat: <true|false>
    service-rule:
    - <service>
    player-rule:
    - <player>
    server-rule:
    - <server>
    commands:
    - <command>
```

* `key-type`
    - The type of key to use for the event.
    - Available types:
        - `group`: The event will be triggered for each group.
        - `player`: The event will be triggered for each player.
        - `server`: The event will be triggered once for the server.
        - `service`: The event will be triggered once for each service.
* `service-rule`
    - The list of services to trigger the event for.
    - If not specified, the event will be triggered for all services.
* `player-rule`
    - The list of players to trigger the event for.
    - If not specified, the event will be triggered for all players.
* `server-rule`
    - The list of servers to trigger the event for.
    - If not specified, the event will be triggered for all servers.
    - use `@current` to refer to the current server.
* `commands`
    - The list of commands to execute when the event is triggered.
    - The following placeholders can be used in the commands:
        - `{key}`: The calculated key of the event.
        - `{value}`: The calculated total value for the key.
* `value`
    - The minimum votes required to trigger the event.
* `repeat`
    - Whether the event should be triggered multiple times.
    - If `true`, the event will be triggered if the votes are a multiple of the `value`.
    - If `false`, the event will be triggered if the votes are equal to or greater than the `value`.

# Example

```yaml
events:
  # A group event that triggers when the total votes for the group is a multiple of 10.
  test-group:
    key-type: group
    repeat: true
    value: 10
    commands:
    - 'say {key} group : {value}'
  # A player event that triggers when the total votes for the player is a multiple of 10.
  test-player:
    key-type: player
    repeat: true
    value: 10
    service-rule:
      - 'PlanetMinecraft.com' # Only consider votes from the PlanetMinecraft service.
    commands:
    - 'say {key} player : {value}'
```