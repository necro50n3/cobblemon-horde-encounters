package com.necro.horde.encounters.neoforge.network;

import com.necro.asymmetric.battles.common.network.ClientPacket;
import com.necro.asymmetric.battles.common.network.ServerPacket;
import com.necro.horde.encounters.common.HordeEncounters;
import com.necro.horde.encounters.common.network.HordeNetworkMessages;
import com.necro.horde.encounters.common.network.packet.ApplySweetScentPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = HordeEncounters.MODID)
public class HordeNetworkMessagesNeoForge {
    @SubscribeEvent
    private static void registerPayload(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar payloadRegistrar = event.registrar(HordeEncounters.MODID).versioned("1.0.0").optional();

        payloadRegistrar.playToServer(ApplySweetScentPacket.PACKET_TYPE, ApplySweetScentPacket.CODEC, HordeNetworkMessagesNeoForge::handle);

        init();
    }

    private static void init() {
        HordeNetworkMessages.APPLY_SWEET_SCENT = duration ->
            HordeNetworkMessagesNeoForge.sendPacketToServer(new ApplySweetScentPacket(duration));
    }

    public static void sendPacketToServer(CustomPacketPayload packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendPacketToAll(CustomPacketPayload packet) {
        PacketDistributor.sendToAllPlayers(packet);
    }

    public static void sendPacketToPlayer(ServerPlayer player, CustomPacketPayload packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    private static void handle(ClientPacket packet, IPayloadContext context) {
        context.enqueueWork(packet::handle);
    }

    private static void handle(ServerPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> packet.handle((ServerPlayer) context.player()));
    }
}
