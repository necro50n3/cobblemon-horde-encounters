package com.necro.horde.encounters.fabric;

import com.necro.horde.encounters.common.HordeEncountersClient;
import net.fabricmc.api.ClientModInitializer;

public class HordeEncountersFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HordeEncountersClient.init();
    }
}
