package com.necro.horde.encounters.common.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "hordeencounters-common")
public class HordeEncountersConfig implements ConfigData {
    @Comment("Whether to enable wild herd horde encounters.")
    public boolean enable_herd_hordes = true;

    @Comment("Whether to enable random horde encounters. Encounters from wild herds are unaffected.")
    public boolean enable_random_hordes = true;

    @Comment("Default random horde encounter rate.")
    public double default_horde_rate = 0.05;

    @Comment("The default minimum and maximum level offset relative to the lead Pokémon")
    public HordeSettingsAdapter.LevelOffset default_level_offset = new HordeSettingsAdapter.LevelOffset(-5, 0);

    @Comment("A list of Pokémon labels to blacklist from random horde encounters.")
    public String[] label_blacklist = { "legendary", "mythical", "ultra_beast", "paradox" };

    @Comment("A list of Pokémon properties to blacklist from random horde encounters. Supports property strings such as \"rattata alolan\".")
    public String[] pokemon_blacklist = { "floette flower=eternal" };

    @Comment("A list of Pokémon aspects to blacklist from random horde encounters.")
    public String[] aspects_blacklist = { "aspect=raid" };

    @Comment(
        """
        A list of random horde spawn overriders from Pokémon properties. Supports property strings such as "rattata alolan".
        \s
        Format:
        properties:     The Pokémon properties. Required.
        horde_rate:     The base probability of a random horde encounter from the Pokémon. Default: Refer to the `default_horde_rate` config value.
        spawn_weights:  A weighted list of Pokémon properties that can spawn from the random horde encounter. Default: The base, non-baby Pokémon of this evolutionary line.
        level_offset:   The minimum and maximum level offset relative to the lead Pokémon. Default: Refer to the `default_level_offset` config value.
        \s
        Example:
        -   properties: charizard
            horde_rate: 0.03
            spawn_weights:
                charmander: 10.0
            level_offset:
                min: -5
                max: 0
        """
    )
    public HordeSettingsAdapter[] spawn_overrides = {};
}
