package com.necro.horde.encounters.neoforge;

import com.necro.horde.encounters.common.HordeEncounters;
import com.necro.horde.encounters.neoforge.effect.HordeEffectsNeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(HordeEncounters.MODID)
public class HordeEncountersNeoForge {
    public HordeEncountersNeoForge(IEventBus modBus, ModContainer container) {
        HordeEncounters.init();

        HordeEffectsNeoForge.registerEffects(modBus);
    }
}
