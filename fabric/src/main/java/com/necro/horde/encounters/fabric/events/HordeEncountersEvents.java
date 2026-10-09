package com.necro.horde.encounters.fabric.events;

import com.necro.asymmetric.battles.common.api.spawning.BattleSpawnPool;
import com.necro.asymmetric.battles.common.registry.SpawnPoolTypeRegistry;
import com.necro.asymmetric.battles.fabric.reloader.BattleSpawnReloadListener;
import com.necro.horde.encounters.common.HordeEncounters;
import com.necro.horde.encounters.common.config.ConfigCache;
import com.necro.horde.encounters.common.spawning.HordeBattleSpawnPool;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public class HordeEncountersEvents {
    public static void init() {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(
            new BattleSpawnReloadListener(ResourceLocation.fromNamespaceAndPath(HordeEncounters.MODID, "sos"),
                "horde",
                BattleSpawnPool.GSON,
                HordeBattleSpawnPool.class
            ));

        ServerLifecycleEvents.SERVER_STARTING.register(server -> ConfigCache.init());

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ConfigCache.onServerStarted();
            SpawnPoolTypeRegistry.sort("horde");
        });
    }
}
