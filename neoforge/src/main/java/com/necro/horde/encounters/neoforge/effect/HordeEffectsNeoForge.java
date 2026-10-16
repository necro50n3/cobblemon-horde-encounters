package com.necro.horde.encounters.neoforge.effect;

import com.necro.horde.encounters.common.HordeEncounters;
import com.necro.horde.encounters.common.effect.HordeEffects;
import com.necro.horde.encounters.common.effect.SweetScentEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class HordeEffectsNeoForge {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, HordeEncounters.MODID);

    public static void registerEffects(IEventBus modBus) {
        HordeEffects.SWEET_SCENT = register("sweet_scent", SweetScentEffect::new);

        MOB_EFFECTS.register(modBus);
    }

    private static Holder<MobEffect> register(String name, Supplier<MobEffect> effect) {
        return MOB_EFFECTS.register(name, effect);
    }
}
