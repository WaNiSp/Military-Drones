package com.wanisp.militarydrones.packet.droneMode;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class DroneModeOnPacket {
    private final ItemStack itemStack;

    public DroneModeOnPacket(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public DroneModeOnPacket(PacketBuffer buffer) {
        this.itemStack = buffer.readItemStack();
    }

    public void toBytes(PacketBuffer buffer) {
        buffer.writeItemStack(itemStack);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();

            if(player != null && itemStack != null) {
                CompoundNBT tag = itemStack.getTag();
                assert tag != null;

                tag.putFloat("playerHealth", player.getHealth());
                player.setHealth(tag.getFloat("droneHealth"));

                tag.putFloat("pitch", player.rotationPitch);
                tag.putFloat("yaw", player.rotationYaw);

                Vector3d position = player.getPositionVec();
                tag.putDouble("x", position.x);
                tag.putDouble("y", position.y);
                tag.putDouble("z", position.z);

                player.inventory.setInventorySlotContents(player.inventory.currentItem, itemStack);
                player.inventory.markDirty();
            }

        });
        context.setPacketHandled(true);
    }
}
