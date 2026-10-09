package com.necro.horde.encounters.neoforge.events;

import com.necro.asymmetric.battles.common.api.spawning.BattleSpawnPool;
import com.necro.asymmetric.battles.common.registry.SpawnPoolTypeRegistry;
import com.necro.asymmetric.battles.neoforge.reloader.BattleSpawnReloadListener;
import com.necro.horde.encounters.common.HordeEncounters;
import com.necro.horde.encounters.common.config.ConfigCache;
import com.necro.horde.encounters.common.spawning.HordeBattleSpawnPool;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@EventBusSubscriber(modid = HordeEncounters.MODID)
public class HordeEncountersEvents {
    @SubscribeEvent
    private static void onReloadDataPack(AddReloadListenerEvent event) {
        event.addListener(new BattleSpawnReloadListener("horde", BattleSpawnPool.GSON, HordeBattleSpawnPool.class));
    }

    @SubscribeEvent
    private static void onServerStarting(ServerStartingEvent event) {
        ConfigCache.init();
    }

    @SubscribeEvent
    private static void onServerStarted(ServerStartedEvent event) {
        ConfigCache.onServerStarted();
        SpawnPoolTypeRegistry.sort("horde");
    }
}
