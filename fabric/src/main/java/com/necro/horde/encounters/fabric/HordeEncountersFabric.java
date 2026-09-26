package com.necro.horde.encounters.fabric;

import com.necro.horde.encounters.common.HordeEncounters;
import net.fabricmc.api.ModInitializer;

public class HordeEncountersFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        HordeEncounters.init();
    }

}
