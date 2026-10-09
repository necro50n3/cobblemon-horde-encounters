package com.necro.horde.encounters.common.spawning;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.necro.asymmetric.battles.common.api.spawning.BattleSpawnPool;
import com.necro.asymmetric.battles.common.util.PropertyExtractors;
import com.necro.horde.encounters.common.HordeEncounters;
import kotlin.ranges.IntRange;

import java.util.HashSet;

public class HordeBattleSpawnPool extends BattleSpawnPool {
    public Double hordeRate = null;

    public double hordeRate() {
        return this.hordeRate != null ? this.hordeRate : HordeEncounters.CONFIG.default_horde_rate;
    }

    public static HordeBattleSpawnPool create(Pokemon pokemon) {
        HordeBattleSpawnPool pool = new HordeBattleSpawnPool();
        pool.properties = pokemon.createPokemonProperties(PropertyExtractors.LONG_EXTRACTOR);
        pool.properties.setAspects(new HashSet<>(pokemon.getAspects()));
        pool.pokemon = pool.properties.asString(" ");
        pool.properties.setOriginalString(pool.pokemon);
        pool.spawns.add(BattleSpawnPool.defaultSpawn(pokemon, new IntRange(HordeEncounters.CONFIG.default_level_offset.min(), HordeEncounters.CONFIG.default_level_offset.max())));
        return pool;
    }
}
