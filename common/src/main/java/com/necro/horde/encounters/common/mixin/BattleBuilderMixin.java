package com.necro.horde.encounters.common.mixin;

import com.cobblemon.mod.common.api.storage.party.PartyStore;
import com.cobblemon.mod.common.battles.BattleBuilder;
import com.cobblemon.mod.common.battles.BattleFormat;
import com.cobblemon.mod.common.battles.BattleStartResult;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.necro.asymmetric.battles.common.api.AsymmetricBattleBuilder;
import com.necro.asymmetric.battles.common.api.BattleParticipant;
import com.necro.asymmetric.battles.common.api.actor.HordeBattleActor;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(value = BattleBuilder.class, priority = 900)
public class BattleBuilderMixin {
    @Inject(method = "pve*", at = @At("HEAD"), remap = false, cancellable = true)
    private void pveHordeBattle(ServerPlayer player, PokemonEntity pokemonEntity, UUID leadingPokemon, BattleFormat battleFormat, boolean cloneParties, boolean healFirst, float fleeDistance, PartyStore party, CallbackInfoReturnable<BattleStartResult> cir) {
        BattleParticipant<HordeBattleActor> horde = BattleParticipant.horde(pokemonEntity);
        if (horde == null) return;
        cir.setReturnValue(AsymmetricBattleBuilder.hordeBattle(BattleParticipant.player(player, leadingPokemon), horde));
    }
}
