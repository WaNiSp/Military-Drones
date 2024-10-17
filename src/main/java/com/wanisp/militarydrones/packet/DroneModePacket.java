package com.wanisp.militarydrones.packet;

import com.gluecode.fpvdrone.a.b;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class DroneModePacket {

    private final boolean activateDrone;

    // Constructor for creating packet
    public DroneModePacket(boolean deactivateDrone) {
        this.activateDrone = deactivateDrone;
    }

    // Constructor for reading data from packet
    public DroneModePacket(PacketBuffer buffer) {
        this.activateDrone = buffer.readBoolean();
    }

    // Method to write data into packet
    public void toBytes(PacketBuffer buffer) {
        buffer.writeBoolean(activateDrone);
    }

    // Processing packet on client
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // Only client code

            // Disable drone mode
            b.v = activateDrone;
            b.d();
        });
        context.setPacketHandled(true);
    }
}
