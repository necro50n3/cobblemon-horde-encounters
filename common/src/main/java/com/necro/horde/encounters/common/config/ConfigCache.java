package com.necro.horde.encounters.common.config;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.properties.CustomPokemonProperty;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.properties.AspectPropertyType;
import com.necro.asymmetric.battles.common.util.PropertyExtractors;
import com.necro.horde.encounters.common.HordeEncounters;

import java.util.*;

public class ConfigCache {
    private static final Map<String, List<PokemonProperties>> POKEMON_BLACKLIST = new HashMap<>();
    private static final Set<CustomPokemonProperty> ASPECT_BLACKLIST = new HashSet<>();

    public static void init() {
        Arrays.stream(HordeEncounters.CONFIG.pokemon_blacklist)
            .map(properties -> {
                PokemonProperties props = PokemonProperties.Companion.parse(properties, " ", "=");
                if (props.getSpecies() == null) {
                    HordeEncounters.LOGGER.warn("Invalid species in Pokémon blacklist: {}", properties);
                    return null;
                }
                return props;
            })
            .forEach(properties -> {
                if (properties == null) return;
                List<PokemonProperties> map = POKEMON_BLACKLIST.computeIfAbsent(properties.getSpecies().toLowerCase(Locale.ROOT), species -> new ArrayList<>());
                map.add(properties);
            });
        ASPECT_BLACKLIST.addAll(Arrays.stream(HordeEncounters.CONFIG.aspects_blacklist)
            .map(AspectPropertyType.INSTANCE::fromString)
            .toList()
        );
    }

    public static void onServerStarted() {
        Arrays.stream(HordeEncounters.CONFIG.spawn_overrides).forEach(HordeSettingsAdapter::registerSpawnDetails);
    }

    public static boolean canHorde(PokemonEntity pokemonEntity) {
        if (isLabelBlacklisted(pokemonEntity)) return false;
        else if (isPropertyBlacklisted(pokemonEntity)) return false;
        else return !isAspectBlacklisted(pokemonEntity);
    }

    private static boolean isLabelBlacklisted(PokemonEntity pokemonEntity) {
        return Arrays.stream(HordeEncounters.CONFIG.label_blacklist).anyMatch(label -> pokemonEntity.getPokemon().hasLabels(label));
    }

    private static boolean isPropertyBlacklisted(PokemonEntity pokemonEntity) {
        List<PokemonProperties> blacklist = POKEMON_BLACKLIST.get(pokemonEntity.getPokemon().getSpecies().getResourceIdentifier().getPath());
        if (blacklist == null) return false;
        PokemonProperties check = pokemonEntity.getPokemon().createPokemonProperties(PropertyExtractors.LONG_EXTRACTOR);
        check.setAspects(pokemonEntity.getAspects());
        return blacklist.stream().anyMatch(properties -> properties.isSubSetOf(check) && check.getAspects().containsAll(properties.getAspects()));
    }

    private static boolean isAspectBlacklisted(PokemonEntity pokemonEntity) {
        return ASPECT_BLACKLIST.stream().anyMatch(aspect -> aspect.matches(pokemonEntity));
    }
}
