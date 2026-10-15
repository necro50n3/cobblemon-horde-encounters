package com.necro.horde.encounters.common.events;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class HordeBattleRollEvent {
    private final ServerPlayer player;
    private final PokemonEntity leader;
    private double hordeRate;

    public HordeBattleRollEvent(ServerPlayer player, PokemonEntity leader, double hordeRate) {
        this.player = player;
        this.leader = leader;
        this.hordeRate = hordeRate;
    }

    public ServerPlayer player() {
        return this.player;
    }

    public PokemonEntity leader() {
        return this.leader;
    }

    public double hordeRate() {
        return this.hordeRate;
    }

    public void set(double hordeRate) {
        this.hordeRate = hordeRate;
    }

    public void add(float mod) {
        this.hordeRate = Mth.clamp(this.hordeRate + mod, 0F, 1F);
    }

    public void multiply(float mod) {
        this.hordeRate = Mth.clamp(this.hordeRate * mod, 0F, 1F);
    }

    public boolean roll(RandomSource random) {
        return random.nextDouble() < this.hordeRate;
    }
}
