package com.necro.horde.encounters.common.events;

import com.cobblemon.mod.common.api.events.Cancelable;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HordeBattleStartEvent {
    public static class Pre extends Cancelable {
        private final PokemonEntity source;

        public Pre(PokemonEntity source) {
            this.source = source;
        }

        public PokemonEntity source() {
            return this.source;
        }
    }

    public record Post(@NotNull List<PokemonEntity> horde) {
        public PokemonEntity leader() {
            return this.horde.getFirst();
        }
    }
}
