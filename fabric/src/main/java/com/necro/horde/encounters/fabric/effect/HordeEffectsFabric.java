package com.necro.horde.encounters.fabric.effect;

import com.necro.horde.encounters.common.HordeEncounters;
import com.necro.horde.encounters.common.effect.HordeEffects;
import com.necro.horde.encounters.common.effect.SweetScentEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

public class HordeEffectsFabric {
    public static void registerEffects() {
        HordeEffects.SWEET_SCENT = register("sweet_scent", new SweetScentEffect());
    }

    private static Holder<MobEffect> register(String name, MobEffect effect) {
        return Registry.registerForHolder(
            BuiltInRegistries.MOB_EFFECT,
            ResourceLocation.fromNamespaceAndPath(HordeEncounters.MODID, name),
            effect
        );
    }
}
