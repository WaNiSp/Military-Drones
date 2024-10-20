package com.wanisp.militarydrones.packet.other;

import com.wanisp.militarydrones.event.SlotChangeHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SlotLockPacket {

    private final boolean isSlotLocked;
    private final int lockedSlot;

    public SlotLockPacket(boolean lock, int slot) {
        this.isSlotLocked = lock;
        this.lockedSlot = slot;
    }

    public SlotLockPacket(PacketBuffer buffer) {
        this.isSlotLocked = buffer.readBoolean();
        this.lockedSlot = buffer.readInt();
    }

    public void toBytes(PacketBuffer buffer) {
        buffer.writeBoolean(isSlotLocked);
        buffer.writeInt(lockedSlot);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            SlotChangeHandler.setSlotLock(isSlotLocked, lockedSlot);
        });
        context.setPacketHandled(true);
    }
}
