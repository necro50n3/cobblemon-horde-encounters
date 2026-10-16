package com.necro.horde.encounters.common.events;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.reactive.CancelableObservable;
import com.cobblemon.mod.common.api.reactive.SimpleObservable;
import com.necro.horde.encounters.common.HordeEncounters;
import com.necro.horde.encounters.common.effect.HordeEffects;

public class HordeEvents {
    public static CancelableObservable<HordeBattleStartEvent.Pre> HERD_HORDE_BATTLE_START_PRE = new CancelableObservable<>();
    public static SimpleObservable<HordeBattleStartEvent.Post> HERD_HORDE_BATTLE_START_POST = new SimpleObservable<>();
    public static SimpleObservable<HordeBattleRollEvent> RANDOM_HORDE_BATTLE_ROLL = new SimpleObservable<>();
    public static CancelableObservable<HordeBattleStartEvent.Pre> RANDOM_HORDE_BATTLE_START_PRE = new CancelableObservable<>();
    public static SimpleObservable<HordeBattleStartEvent.Post> RANDOM_HORDE_BATTLE_START_POST = new SimpleObservable<>();

    public static void init() {
        RANDOM_HORDE_BATTLE_ROLL.subscribe(Priority.NORMAL, event -> {
            if (!HordeEncounters.CONFIG.enable_sweet_scent) return;
            if (!event.player().hasEffect(HordeEffects.SWEET_SCENT)) return;
            else if (event.player().level().isRaining()) return;
            event.set(1.0);
            event.player().removeEffect(HordeEffects.SWEET_SCENT);
        });
    }
}
