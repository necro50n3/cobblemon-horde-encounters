package com.necro.horde.encounters.common.config;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.necro.asymmetric.battles.common.api.spawning.BattleSpawnDetail;
import com.necro.asymmetric.battles.common.config.serializer.Indent;
import com.necro.asymmetric.battles.common.config.serializer.YamlKey;
import com.necro.asymmetric.battles.common.registry.SpawnPoolTypeRegistry;
import com.necro.horde.encounters.common.HordeEncounters;
import com.necro.horde.encounters.common.spawning.HordeBattleSpawnPool;
import kotlin.ranges.IntRange;

import java.util.Map;

@Indent(false)
public class HordeSettingsAdapter {
    private String properties;
    @YamlKey("horde_rate") private Double hordeRate;
    @YamlKey("spawn_weights") private Map<String, Double> spawnWeights = null;
    @YamlKey("level_offset") private LevelOffset levelOffset = null;

    public void registerSpawnDetails() {
        String error = null;
        if (this.properties == null) error = "Failed to parse Spawn Overrides entry: Missing required key \"properties\".";
        if (this.spawnWeights == null) this.spawnWeights = Map.of();
        if (this.levelOffset == null) this.levelOffset = new LevelOffset(-5, 0);
        if (this.levelOffset.min() == null) error = String.format("Failed to parse %s: Missing required key \"min\"", this.properties);
        if (this.levelOffset.max() == null) error = String.format("Failed to parse %s: Missing required key \"max\"", this.properties);
        if (error != null) {
            HordeEncounters.LOGGER.error(error);
            return;
        }

        PokemonProperties spawnProperties = PokemonProperties.Companion.parse(this.properties);
        IntRange levelRangeOffset = new IntRange(this.levelOffset.min(), this.levelOffset.max());
        this.spawnWeights.forEach((properties, weight) -> {
            if (properties == null) {
                HordeEncounters.LOGGER.error("Failed to parse spawn for {}: Missing required key \"properties\"", this.properties);
                return;
            }

            BattleSpawnDetail detail = BattleSpawnDetail.basic(PokemonProperties.Companion.parse(properties), levelRangeOffset, weight);
            SpawnPoolTypeRegistry.register("horde", spawnProperties, detail, HordeBattleSpawnPool.class);
        });

        HordeBattleSpawnPool pool = (HordeBattleSpawnPool) SpawnPoolTypeRegistry.get("horde", spawnProperties);
        if (pool != null) pool.hordeRate = this.hordeRate;
    }

    public record LevelOffset(Integer min, Integer max) {}
}

