package com.necro.horde.encounters.neoforge;

import com.necro.horde.encounters.common.HordeEncounters;
import com.necro.horde.encounters.common.HordeEncountersClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = HordeEncounters.MODID, dist = Dist.CLIENT)
public class HordeEncountersNeoForgeClient {
    public HordeEncountersNeoForgeClient(IEventBus modBus, ModContainer container) {
        HordeEncountersClient.init();
    }
}
