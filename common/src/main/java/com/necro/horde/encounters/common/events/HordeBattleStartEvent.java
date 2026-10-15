package com.necro.horde.encounters.common.events;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.events.Cancelable;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HordeBattleStartEvent {
    public static class Pre extends Cancelable {
        private final ServerPlayer player;
        private final PokemonEntity source;

        public Pre(ServerPlayer player, PokemonEntity source) {
            this.player = player;
            this.source = source;
        }

        public ServerPlayer player() {
            return this.player;
        }

        public PokemonEntity source() {
            return this.source;
        }
    }

    public record Post(ServerPlayer player, @NotNull List<PokemonEntity> horde, PokemonBattle battle) {
        public PokemonEntity leader() {
            return this.horde.getFirst();
        }
    }
}
