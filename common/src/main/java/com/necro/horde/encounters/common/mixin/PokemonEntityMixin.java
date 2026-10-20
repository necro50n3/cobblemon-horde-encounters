package com.necro.horde.encounters.common.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.necro.horde.encounters.common.util.IHordeSpawner;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PokemonEntity.class)
public class PokemonEntityMixin implements IHordeSpawner {
    @Unique
    private boolean horde_hasSpawnedHorde = false;

    @Override
    public boolean horde_hasSpawnedHorde() {
        return this.horde_hasSpawnedHorde;
    }

    @Override
    public void horde_setSpawnedHorde() {
        this.horde_hasSpawnedHorde = true;
    }

    @Inject(method = "saveWithoutId", at = @At("RETURN"))
    private void saveWithoutIdInject(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> cir) {
        if (this.horde_hasSpawnedHorde) nbt.putBoolean("has_spawned_horde", true);
    }

    @Inject(method = "load", at = @At("RETURN"))
    private void loadInject(CompoundTag nbt, CallbackInfo ci) {
        if (nbt.contains("has_spawned_horde")) this.horde_hasSpawnedHorde = true;
    }
}
