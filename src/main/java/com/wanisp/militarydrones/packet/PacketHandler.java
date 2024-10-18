package com.wanisp.militarydrones.packet;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("militarydrones", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        INSTANCE.messageBuilder(DroneModePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(DroneModePacket::toBytes)
                .decoder(DroneModePacket::new)
                .consumer(DroneModePacket::handle)
                .add();

        INSTANCE.messageBuilder(DroneOverlayPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(DroneOverlayPacket::toBytes)
                .decoder(DroneOverlayPacket::new)
                .consumer(DroneOverlayPacket::handle)
                .add();
    }
}
