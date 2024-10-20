package com.wanisp.militarydrones.packet.visual;

import com.wanisp.militarydrones.client.DroneOverlayRenderer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class DroneOverlayPacket {

    public DroneOverlayPacket() {}

    public DroneOverlayPacket(PacketBuffer buf) {
    }

    public void toBytes(PacketBuffer buf) {}

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(DroneOverlayRenderer::activateOverlay);
        ctx.get().setPacketHandled(true);
    }
}
