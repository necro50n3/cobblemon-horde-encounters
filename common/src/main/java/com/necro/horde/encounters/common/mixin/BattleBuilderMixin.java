package com.necro.horde.encounters.common.mixin;

import com.cobblemon.mod.common.api.pokemon.feature.IntSpeciesFeature;
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
import com.necro.horde.encounters.common.events.HordeBattleStartEvent;
import com.necro.horde.encounters.common.events.HordeEvents;
import com.necro.horde.encounters.common.util.IHordeSpawner;
import kotlin.Unit;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Mixin(value = BattleBuilder.class, priority = 900)
public class BattleBuilderMixin {
    @Inject(method = "pve*", at = @At("HEAD"), remap = false, cancellable = true)
    private void pveHordeBattle(ServerPlayer player, PokemonEntity pokemonEntity, UUID leadingPokemon, BattleFormat battleFormat, boolean cloneParties, boolean healFirst, float fleeDistance, PartyStore party, CallbackInfoReturnable<BattleStartResult> cir) {
        BattleStartResult start = this.horde_startFeatureHorde(player, pokemonEntity, leadingPokemon, cir);
        if (start == null) start = this.horde_startHerdHorde(player, pokemonEntity, leadingPokemon, cir);
        if (start == null) start = this.horde_startRandomHorde(player, pokemonEntity, leadingPokemon, cir);

        ((IHordeSpawner) pokemonEntity).horde_setSpawnedHorde();
        if (start != null) cir.setReturnValue(start);
    }

    @Unique
    private @Nullable BattleStartResult horde_startFeatureHorde(ServerPlayer player, PokemonEntity pokemonEntity, UUID leadingPokemon, CallbackInfoReturnable<BattleStartResult> cir) {
        if (this.horde_hasTriedSpawnHorde(pokemonEntity)) return null;
        IntSpeciesFeature feature = pokemonEntity.getPokemon().getFeature("horde_size");
        if (feature == null || feature.getValue() == 0) return null;
        else if (feature.getValue() == 1) return cir.getReturnValue();
        HordeManager manager = new HordeManager(pokemonEntity);
        return this.horde_actuallyStartRandomHorde(player, pokemonEntity, leadingPokemon, manager, feature, cir);
    }

    @Unique
    private @Nullable BattleStartResult horde_startHerdHorde(ServerPlayer player, PokemonEntity pokemonEntity, UUID leadingPokemon, CallbackInfoReturnable<BattleStartResult> cir) {
        if (!HordeEncounters.CONFIG.enable_herd_hordes) return null;
        BattleParticipant<HordeBattleActor> horde = BattleParticipant.horde(pokemonEntity);
        if (horde == null) return null;

        HordeBattleStartEvent.Pre preEvent = new HordeBattleStartEvent.Pre(player, pokemonEntity);
        HordeEvents.HERD_HORDE_BATTLE_START_PRE.emit(preEvent);
        if (preEvent.isCanceled()) return cir.getReturnValue();

        return AsymmetricBattleBuilder.hordeBattle(BattleParticipant.player(player, leadingPokemon), horde)
            .ifSuccessful(battle -> {
                List<PokemonEntity> entities = battle.getSide2().getActivePokemon().stream()
                    .filter(pokemon -> pokemon.getBattlePokemon() != null)
                    .map(pokemon -> pokemon.getBattlePokemon().getEntity())
                    .toList();
                HordeBattleStartEvent.Post postEvent = new HordeBattleStartEvent.Post(player, entities, battle);
                HordeEvents.HERD_HORDE_BATTLE_START_POST.emit(postEvent);
                return Unit.INSTANCE;
            });
    }

    @Unique
    private boolean horde_hasTriedSpawnHorde(PokemonEntity pokemonEntity) {
        return ((IHordeSpawner) pokemonEntity).horde_hasSpawnedHorde() && !HordeEncounters.CONFIG.can_retry_horde;
    }

    @Unique
    private @Nullable BattleStartResult horde_startRandomHorde(ServerPlayer player, PokemonEntity pokemonEntity, UUID leadingPokemon, CallbackInfoReturnable<BattleStartResult> cir) {
        if (!HordeEncounters.CONFIG.enable_random_hordes) return null;
        else if (this.horde_hasTriedSpawnHorde(pokemonEntity)) return null;
        else if (!ConfigCache.canHorde(pokemonEntity)) return null;

        HordeManager manager = new HordeManager(pokemonEntity);
        if (!manager.shouldHorde(player, pokemonEntity)) return null;

        return this.horde_actuallyStartRandomHorde(player, pokemonEntity, leadingPokemon, manager, null, cir);
    }

    @Unique
    private @Nullable BattleStartResult horde_actuallyStartRandomHorde(ServerPlayer player, PokemonEntity pokemonEntity, UUID leadingPokemon, HordeManager manager, @Nullable IntSpeciesFeature hordeSize, CallbackInfoReturnable<BattleStartResult> cir) {
        HordeBattleStartEvent.Pre preEvent = new HordeBattleStartEvent.Pre(player, pokemonEntity);
        HordeEvents.RANDOM_HORDE_BATTLE_START_PRE.emit(preEvent);
        if (preEvent.isCanceled()) return cir.getReturnValue();

        List<PokemonEntity> horde = manager.spawnHorde(player, pokemonEntity, leadingPokemon, hordeSize);
        return AsymmetricBattleBuilder.hordeBattle(BattleParticipant.player(player, leadingPokemon), BattleParticipant.horde(horde))
            .ifSuccessful(battle -> {
                HordeBattleStartEvent.Post postEvent = new HordeBattleStartEvent.Post(player, Collections.unmodifiableList(horde), battle);
                HordeEvents.RANDOM_HORDE_BATTLE_START_POST.emit(postEvent);
                return Unit.INSTANCE;
            })
            .ifErrored(error -> {
                    horde.forEach(entity -> {
                        if (!entity.isRemoved()) entity.discard();
                    });
                    return Unit.INSTANCE;
                }
            );
    }
}
