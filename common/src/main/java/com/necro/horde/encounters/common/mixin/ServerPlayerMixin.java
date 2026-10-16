package com.necro.horde.encounters.common.mixin;

import com.mojang.authlib.GameProfile;
import com.necro.horde.encounters.common.effect.HordeEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    public ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Inject(method = "completeUsingItem", at = @At("HEAD"))
    private void addSweetScent(CallbackInfo ci) {
        if (!this.useItem.isEmpty() && this.isUsingItem() && this.useItem.is(Items.HONEY_BOTTLE)) {
            this.addEffect(new MobEffectInstance(HordeEffects.SWEET_SCENT, 1200, 0, false, false, true));
        }
    }
}
