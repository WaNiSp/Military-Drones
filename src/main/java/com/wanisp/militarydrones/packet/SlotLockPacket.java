package com.wanisp.militarydrones.packet;

import com.wanisp.militarydrones.event.SlotChangeHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SlotLockPacket {

    private final boolean isSlotLocked;
    private final int lockedSlot;

    // Constructor for creating packet
    public SlotLockPacket(boolean lock, int slot) {
        this.isSlotLocked = lock;
        this.lockedSlot = slot;
    }

    // Constructor for reading data from packet
    public SlotLockPacket(PacketBuffer buffer) {
        this.isSlotLocked = buffer.readBoolean();
        this.lockedSlot = buffer.readInt();
    }

    // Method to write data into packet
    public void toBytes(PacketBuffer buffer) {
        buffer.writeBoolean(isSlotLocked);
        buffer.writeInt(lockedSlot);
    }

    // Processing packet on client
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // Disable drone mode
            SlotChangeHandler.setSlotLock(isSlotLocked, lockedSlot);
        });
        context.setPacketHandled(true);
    }
}
