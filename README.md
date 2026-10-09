[![Modrinth](https://img.shields.io/modrinth/dt/BYo4eT2j?style=for-the-badge&logo=modrinth&label=Modrinth)](https://modrinth.com/mod/BYo4eT2j)
[![CurseForge](https://img.shields.io/curseforge/dt/1717500?style=for-the-badge&logo=curseforge&label=Curseforge)](https://www.curseforge.com/minecraft/mc-mods/cobblemon-horde-encounters)
[![Discord](https://img.shields.io/discord/1355016729679499284?style=for-the-badge&logo=discord&label=Discord)](https://discord.gg/6jBar3y6nt)
[![GitHub](https://img.shields.io/badge/GitHub-565656?style=for-the-badge&logo=github)](https://github.com/necro50n3/cobblemon-horde-encounters)

# Cobblemon Horde Battles
![Horde Battles](https://i.imgur.com/NJjBGZp.png)

## Features
Adds Gen 6's Horde Battles to Cobblemon!

Pokémon that spawn in herds (up to 6) now all join the battle at the same time.

## Dependencies
- [Asymmetric Battles API](https://github.com/necro50n3/asymmetric-battles-api)

## Custom Spawning
### Basic Spawning (Config)
You can set up basic spawn settings with the `spawn_overrides` config value.
This allows for customising call rates, level ranges and Pokémon spawns.

The only required field is `properties`.
```
-   properties: charizard alpha
    horde_rate: 0.03
    spawn_weights:
        charmander: 10.0
    level_offset:
        min: -5
        max: 0
```

### Advanced Spawning (Datapack)
Advanced spawn settings allow for additional conditional spawning, conditional weights, and level ranges or level range offsets per-spawn.
Advanced spawning entries must be in the `data/<namespace>/battle_spawns/horde` directory.
Supports `conditions`, `anticonditions`, and `weightMultipliers`. Additionally supports registration conditions `neededInstalledMods` and `neededUninstalledMods`.
Refer to Cobblemon's [Spawn Detail Presets](https://wiki.cobblemon.com/index.php/Spawn_Detail_Presets).


The only required field is `pokemon`.
```
{
    "pokemon": "wooper paldean",
    "hordeRate": 0.03,
    "spawns": [
        {
            "pokemon": "wooper paldean",
            "weight": 10.0,
            "level": "20-25"
        },
        {
            "pokemon": "clodsire",
            "levelOffset": "-5-10",
            "conditions": {
                "isRaining": true
            }
        }
    ]
}
```