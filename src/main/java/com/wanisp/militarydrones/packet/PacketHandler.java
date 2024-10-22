package com.wanisp.militarydrones.packet;

import com.wanisp.militarydrones.packet.droneMode.DroneModeOffPacket;
import com.wanisp.militarydrones.packet.droneMode.DroneModeOnPacket;
import com.wanisp.militarydrones.packet.droneMode.DroneModeSetPacket;
import com.wanisp.militarydrones.packet.other.DropGrenadePacket;
import com.wanisp.militarydrones.packet.other.SlotLockPacket;
import com.wanisp.militarydrones.packet.visual.DroneOverlayPacket;
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

        INSTANCE.messageBuilder(DroneOverlayPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(DroneOverlayPacket::toBytes)
                .decoder(DroneOverlayPacket::new)
                .consumer(DroneOverlayPacket::handle)
                .add();

        INSTANCE.messageBuilder(SlotLockPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SlotLockPacket::toBytes)
                .decoder(SlotLockPacket::new)
                .consumer(SlotLockPacket::handle)
                .add();

        INSTANCE.messageBuilder(DroneModeSetPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(DroneModeSetPacket::toBytes)
                .decoder(DroneModeSetPacket::new)
                .consumer(DroneModeSetPacket::handle)
                .add();



        INSTANCE.messageBuilder(DroneModeOffPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(DroneModeOffPacket::toBytes)
                .decoder(DroneModeOffPacket::new)
                .consumer(DroneModeOffPacket::handle)
                .add();

        INSTANCE.messageBuilder(DroneModeOnPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(DroneModeOnPacket::toBytes)
                .decoder(DroneModeOnPacket::new)
                .consumer(DroneModeOnPacket::handle)
                .add();

        INSTANCE.messageBuilder(DropGrenadePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(DropGrenadePacket::toBytes)
                .decoder(DropGrenadePacket::new)
                .consumer(DropGrenadePacket::handle)
                .add();
    }
}
