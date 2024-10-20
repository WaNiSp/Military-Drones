package com.wanisp.militarydrones.packet.droneMode;

import com.gluecode.fpvdrone.a.b;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class DroneModeSetPacket {
    private final boolean active;

    public DroneModeSetPacket(boolean active) {
        this.active = active;
    }

    public DroneModeSetPacket(PacketBuffer buffer) {
        this.active = buffer.readBoolean();
    }

    public void toBytes(PacketBuffer buffer) {
        buffer.writeBoolean(active);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            b.q = !active;
            b.v = active;
            b.d();
        });
        context.setPacketHandled(true);
    }
}
