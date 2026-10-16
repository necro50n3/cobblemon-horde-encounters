package com.necro.horde.encounters.fabric.network;

import com.necro.asymmetric.battles.common.network.ClientPacket;
import com.necro.asymmetric.battles.common.network.ServerPacket;
import com.necro.horde.encounters.common.network.HordeNetworkMessages;
import com.necro.horde.encounters.common.network.packet.ApplySweetScentPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class HordeNetworkMessagesFabric {
    public static void registerPayload() {
        PayloadTypeRegistry.playC2S().register(ApplySweetScentPacket.PACKET_TYPE, ApplySweetScentPacket.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ApplySweetScentPacket.PACKET_TYPE, HordeNetworkMessagesFabric::handle);

        init();
    }

    private static void init() {
        HordeNetworkMessages.APPLY_SWEET_SCENT = duration ->
            HordeNetworkMessagesFabric.sendPacketToServer(new ApplySweetScentPacket(duration));
    }

    public static void sendPacketToServer(CustomPacketPayload packet) {
        ClientPlayNetworking.send(packet);
    }

    public static void sendPacketToAll(MinecraftServer server, CustomPacketPayload packet) {
        server.getPlayerList().getPlayers().forEach(player -> ServerPlayNetworking.send(player, packet));
    }

    public static void sendPacketToPlayer(ServerPlayer player, CustomPacketPayload packet) {
        ServerPlayNetworking.send(player, packet);
    }

    private static void handle(ClientPacket packet, ClientPlayNetworking.Context context) {
        packet.handle();
    }

    private static void handle(ServerPacket packet, ServerPlayNetworking.Context context) {
        context.server().execute(() -> packet.handle(context.player()));
    }
}
