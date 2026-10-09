package com.necro.horde.encounters.common;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleFledEvent;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.platform.events.PlatformEvents;
import com.mojang.logging.LogUtils;
import com.necro.asymmetric.battles.common.config.serializer.PrimitiveYamlConfigSerializer;
import com.necro.asymmetric.battles.common.util.IBattleSpawn;
import com.necro.horde.encounters.common.config.HordeEncountersConfig;
import me.shedaniel.autoconfig.AutoConfig;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class HordeEncounters {
    public static final String MODID = "hordeencounters";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static HordeEncountersConfig CONFIG;

    public static void init() {
        LOGGER.info("Initiating {}", MODID);

        AutoConfig.register(HordeEncountersConfig .class, PrimitiveYamlConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(HordeEncountersConfig.class).getConfig();

        CobblemonEvents.BATTLE_FLED.subscribe(Priority.LOWEST, (Consumer<BattleFledEvent>) event -> despawnHordeOnFlee(event.getBattle()));
        PlatformEvents.SERVER_PLAYER_LOGOUT.subscribe(Priority.HIGHEST, event -> {
            PokemonBattle battle = BattleRegistry.getBattleByParticipatingPlayer(event.getPlayer());
            if (battle != null) despawnHordeOnFlee(battle);
        });
    }

    private static void despawnHordeOnFlee(PokemonBattle battle) {
        if (!battle.getFormat().getBattleType().getName().equals("horde")) return;
        List<PokemonEntity> entities = battle.getSide2().getActivePokemon().stream()
            .filter(pokemon -> pokemon.getBattlePokemon() != null && pokemon.isAlive())
            .map(pokemon -> pokemon.getBattlePokemon().getEntity())
            .filter(Objects::nonNull)
            .toList();
        entities.forEach(entity -> {
            if (((IBattleSpawn) entity.getPokemon()).aba_isBattleSpawn()) entity.discard();
        });
    }
}
