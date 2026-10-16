package com.necro.horde.encounters.fabric;

import com.necro.horde.encounters.common.HordeEncounters;
import com.necro.horde.encounters.fabric.effect.HordeEffectsFabric;
import com.necro.horde.encounters.fabric.events.HordeEncountersEvents;
import com.necro.horde.encounters.fabric.network.HordeNetworkMessagesFabric;
import net.fabricmc.api.ModInitializer;

public class HordeEncountersFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        HordeEncounters.init();
        HordeEncountersEvents.init();

        HordeEffectsFabric.registerEffects();
        HordeNetworkMessagesFabric.registerPayload();
    }
}
