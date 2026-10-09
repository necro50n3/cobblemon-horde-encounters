package com.necro.horde.encounters.common.mixin;

import com.cobblemon.mod.common.api.storage.party.PartyStore;
import com.cobblemon.mod.common.battles.BattleBuilder;
import com.cobblemon.mod.common.battles.BattleFormat;
import com.cobblemon.mod.common.battles.BattleStartResult;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.necro.asymmetric.battles.common.api.AsymmetricBattleBuilder;
import com.necro.asymmetric.battles.common.api.BattleParticipant;
import com.necro.asymmetric.battles.common.api.actor.HordeBattleActor;
import com.necro.horde.encounters.common.HordeEncounters;
import com.necro.horde.encounters.common.api.HordeManager;
import com.necro.horde.encounters.common.config.ConfigCache;
import kotlin.Unit;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.UUID;

@Mixin(value = BattleBuilder.class, priority = 900)
public class BattleBuilderMixin {
    @Inject(method = "pve*", at = @At("HEAD"), remap = false, cancellable = true)
    private void pveHordeBattle(ServerPlayer player, PokemonEntity pokemonEntity, UUID leadingPokemon, BattleFormat battleFormat, boolean cloneParties, boolean healFirst, float fleeDistance, PartyStore party, CallbackInfoReturnable<BattleStartResult> cir) {
        BattleStartResult herdStart = this.horde_startHerdHorde(player, pokemonEntity, leadingPokemon);
        if (herdStart != null) {
            cir.setReturnValue(herdStart);
            return;
        }
        BattleStartResult randomStart = this.horde_startRandomHorde(player, pokemonEntity, leadingPokemon);
        if (randomStart != null) cir.setReturnValue(randomStart);
    }

    @Unique
    private @Nullable BattleStartResult horde_startHerdHorde(ServerPlayer player, PokemonEntity pokemonEntity, UUID leadingPokemon) {
        if (!HordeEncounters.CONFIG.enable_herd_hordes) return null;
        BattleParticipant<HordeBattleActor> horde = BattleParticipant.horde(pokemonEntity);
        return horde != null ? AsymmetricBattleBuilder.hordeBattle(BattleParticipant.player(player, leadingPokemon), horde) : null;
    }

    @Unique
    private @Nullable BattleStartResult horde_startRandomHorde(ServerPlayer player, PokemonEntity pokemonEntity, UUID leadingPokemon) {
        if (!HordeEncounters.CONFIG.enable_random_hordes) return null;
        else if (!ConfigCache.canHorde(pokemonEntity)) return null;

        HordeManager manager = new HordeManager(pokemonEntity);
        if (!manager.shouldHorde()) return null;

        List<PokemonEntity> horde = manager.spawnHorde(player, pokemonEntity, leadingPokemon);
        return AsymmetricBattleBuilder.hordeBattle(BattleParticipant.player(player, leadingPokemon), BattleParticipant.horde(horde)).ifErrored(error -> {
                horde.forEach(entity -> {
                    if (!entity.isRemoved()) entity.discard();
                });
                return Unit.INSTANCE;
            }
        );
    }
}
