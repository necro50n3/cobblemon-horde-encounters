package com.necro.horde.encounters.common.api;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.drop.DropTable;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.feature.IntSpeciesFeature;
import com.cobblemon.mod.common.api.spawning.SpawnCause;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.util.PlayerExtensionsKt;
import com.necro.asymmetric.battles.common.api.AsymmetricAPI;
import com.necro.asymmetric.battles.common.api.spawning.BattleSpawnDetail;
import com.necro.asymmetric.battles.common.api.spawning.BattleSpawnPool;
import com.necro.asymmetric.battles.common.api.spawning.BattleSpawnablePosition;
import com.necro.asymmetric.battles.common.registry.SpawnPoolTypeRegistry;
import com.necro.asymmetric.battles.common.util.IBattleSpawn;
import com.necro.asymmetric.battles.common.util.PropertyExtractors;
import com.necro.horde.encounters.common.config.ConfigCache;
import com.necro.horde.encounters.common.events.HordeBattleRollEvent;
import com.necro.horde.encounters.common.events.HordeEvents;
import com.necro.horde.encounters.common.spawning.HordeBattleSpawnPool;
import kotlin.Unit;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class HordeManager {
    private final Pokemon pokemon;
    private final PokemonProperties properties;
    private final HordeBattleSpawnPool pool;
    private final RandomSource random;

    public HordeManager(PokemonEntity pokemonEntity) {
        this.pokemon = pokemonEntity.getPokemon();
        this.properties = this.pokemon.createPokemonProperties(PropertyExtractors.LONG_EXTRACTOR);
        this.properties.setAspects(new HashSet<>(this.pokemon.getAspects()));
        HordeBattleSpawnPool pool = (HordeBattleSpawnPool) SpawnPoolTypeRegistry.get("horde", pokemonEntity);
        this.pool = pool != null ? pool : HordeBattleSpawnPool.create(this.pokemon);
        this.random = pokemonEntity.getRandom();
    }

    public boolean shouldHorde(ServerPlayer player, PokemonEntity pokemonEntity) {
        HordeBattleRollEvent event = new HordeBattleRollEvent(player, pokemonEntity, this.pool.hordeRate());
        HordeEvents.RANDOM_HORDE_BATTLE_ROLL.emit(event);
        return event.roll(this.random);
    }

    public List<PokemonEntity> spawnHorde(ServerPlayer player, PokemonEntity pokemonEntity, UUID leadingPokemon, @Nullable IntSpeciesFeature hordeSize) {
        Vec3 playerPos = this.playerPosition(player, PlayerExtensionsKt.party(player).get(leadingPokemon), pokemonEntity);

        List<PokemonEntity> horde = new ArrayList<>();
        horde.add(pokemonEntity);
        int size = hordeSize != null ? hordeSize.getValue() - 1 : this.pool.size(this.random);
        List<Pokemon> spawns = this.getRandomSpawns(
            player,
            (ServerLevel) pokemonEntity.level(),
            pokemonEntity.blockPosition(),
            null,
            this.pokemon.getLevel(),
            () -> {
                BattleSpawnDetail detail = BattleSpawnPool.defaultSpawn(pokemonEntity.getPokemon(), null);
                ConfigCache.setLevelOffset(detail);
                return detail.create(pokemonEntity.getPokemon().getLevel(), player);
            },
            size
        );
        for (int i = 0; i < spawns.size(); i++) {
            Pokemon pokemon = spawns.get(i);
            Vec3 sendOutPosition = this.getSendOutPosition(playerPos, pokemonEntity, pokemon, i);
            if (sendOutPosition == null) continue;
            ((IBattleSpawn) pokemon).aba_setBattleSpawn();
            PokemonEntity newEntity = pokemon.sendOut((ServerLevel) pokemonEntity.level(), sendOutPosition, null, p -> Unit.INSTANCE);
            if (newEntity == null) continue;

            newEntity.lookAt(EntityAnchorArgument.Anchor.EYES, playerPos);
            ((ServerLevel) pokemonEntity.level()).sendParticles(ParticleTypes.GUST_EMITTER_SMALL, sendOutPosition.x(), sendOutPosition.y(), sendOutPosition.z(), 1, 1.0, 0.0, 0.0, 0.0);
            newEntity.setDrops(new DropTable());
            horde.add(newEntity);
        }
        return horde;
    }

    private Vec3 playerPosition(ServerPlayer player, Pokemon playerPokemon, PokemonEntity wildEntity) {
        Vec3 playerPos = player.position();
        Vec3 actorOffset = wildEntity.position().subtract(playerPos);
        double actorDistance = actorOffset.length();
        if (actorDistance == 0.0) return playerPos;

        float pokemonWidth = playerPokemon.getForm().getHitbox().width() * playerPokemon.getForm().getBaseScale();

        double minDistance = 8.0;
        Vec3 basePos = playerPos;
        if (actorDistance < minDistance) {
            Vec3 scaledOffset = actorOffset.scale(minDistance / actorDistance);
            basePos = playerPos.subtract(scaledOffset.subtract(actorOffset));
            actorOffset = scaledOffset;
        }

        Vec3 candidatePos = basePos.add(actorOffset.scale(0.3));
        double eyeY = player.getEyeHeight();
        BlockHitResult hit = player.level().clip(new ClipContext(
            playerPos.add(0.0, eyeY, 0.0),
            new Vec3(candidatePos.x(), player.getY() + eyeY, candidatePos.z()),
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            player
        ));

        if (hit.getType() == HitResult.Type.BLOCK) {
            if (hit.getLocation().distanceTo(playerPos) < pokemonWidth) return basePos;
            return new Vec3(hit.getLocation().x(), candidatePos.y(), hit.getLocation().z());
        }

        return candidatePos;
    }

    private Vec3 getSendOutPosition(Vec3 playerPos, PokemonEntity pokemonEntity, Pokemon newPokemon, int index) {
        Vec3 wildPos = pokemonEntity.position();
        Vec3 axis = new Vec3(playerPos.x() - wildPos.x(), 0.0, playerPos.z() - wildPos.z());
        if (axis.lengthSqr() == 0.0) return null;
        Vec3 parallel = axis.normalize();
        Vec3 perpendicular = axis.normalize().cross(new Vec3(0.0, 1.0, 0.0));

        float pokemonWidth = newPokemon.getForm().getHitbox().width() * newPokemon.getForm().getBaseScale();
        double spacing = Math.max(pokemonWidth, 3.5);

        double direction = (index % 2 == 0) ? 1.0 : -1.0;
        double perpendicularOffset = direction * (1 + index / 2) * spacing;
        double parallelOffset = -((index / 2 == 0) ? 2.0 : 0.5);

        return wildPos.add(perpendicular.scale(perpendicularOffset)).add(parallel.scale(parallelOffset));
    }

    public List<Pokemon> getRandomSpawns(ServerPlayer player, ServerLevel level, BlockPos blockPos, PokemonBattle battle, int baseLevel, Supplier<Pokemon> defaultSpawn, int count) {
        SpawnCause cause = new SpawnCause(AsymmetricAPI.SPAWNER, player);
        BattleSpawnablePosition spawnablePosition = new BattleSpawnablePosition(cause, level, blockPos, List.of(), battle, this.properties, baseLevel);
        return this.pool.getRandomSpawns(spawnablePosition, player, defaultSpawn, count);
    }
}
