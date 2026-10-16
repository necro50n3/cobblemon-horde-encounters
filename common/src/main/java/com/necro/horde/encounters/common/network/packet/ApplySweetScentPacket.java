package com.necro.horde.encounters.common.network.packet;

import com.cobblemon.mod.common.client.battle.*;
import com.necro.asymmetric.battles.common.network.ServerPacket;
import com.necro.horde.encounters.common.HordeEncounters;
import com.necro.horde.encounters.common.effect.HordeEffects;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.NotNull;

public record ApplySweetScentPacket(int duration) implements CustomPacketPayload, ServerPacket {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(HordeEncounters.MODID, "apply_sweet_scent");
    public static final Type<ApplySweetScentPacket> PACKET_TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, ApplySweetScentPacket> CODEC = StreamCodec.ofMember(ApplySweetScentPacket::write, ApplySweetScentPacket::read);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.duration);
    }

    public static ApplySweetScentPacket read(RegistryFriendlyByteBuf buf) {
        return new ApplySweetScentPacket(buf.readInt());
    }

    @Override
    public void handle(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(HordeEffects.SWEET_SCENT, this.duration, 0, false, false, true));
    }
}
