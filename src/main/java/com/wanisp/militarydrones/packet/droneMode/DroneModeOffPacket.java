package com.wanisp.militarydrones.packet.droneMode;

import net.minecraft.entity.Pose;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class DroneModeOffPacket {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final ItemStack itemStack;

    public DroneModeOffPacket(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public DroneModeOffPacket(PacketBuffer buffer) {
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

                player.removePotionEffect(Effects.INVISIBILITY);
                player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 20, 100, false, false));

                tag.putFloat("droneHealth", player.getHealth());
                player.setHealth(tag.getFloat("playerHealth"));

                Vector3d pos = new Vector3d(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
                player.setPosition(pos.x + 0.5, pos.y, pos.z);
                player.rotationPitch = tag.getFloat("pitch");
                player.rotationYaw = tag.getFloat("yaw");

                player.inventory.setInventorySlotContents(player.inventory.currentItem, itemStack);
                player.inventory.markDirty();

                scheduler.schedule(() -> {
                    player.setPose(Pose.STANDING);
                    player.recalculateSize();
                }, 300, TimeUnit.MILLISECONDS);
            }

        });
        context.setPacketHandled(true);
    }
}
