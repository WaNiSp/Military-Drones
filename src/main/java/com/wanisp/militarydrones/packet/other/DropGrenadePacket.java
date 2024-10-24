package com.wanisp.militarydrones.packet.other;

import com.gluecode.fpvdrone.Main;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class DropGrenadePacket {
    private final ItemStack itemStack;

    public DropGrenadePacket(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public DropGrenadePacket(PacketBuffer buffer) {
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
                if (!(Boolean) Main.entityArmStates.getOrDefault(player.getUniqueID(), false)) {
                    return;
                }

                CompoundNBT tag = itemStack.getTag();
                assert tag != null;

                if(tag.getInt("ammunition") >= 1) {
                    tag.putInt("ammunition", tag.getInt("ammunition") - 1);
                    player.sendStatusMessage(new StringTextComponent(I18n.format("message.militarydrones.ammunition") + tag.getInt("ammunition")), true);

                    TNTEntity tnt = new TNTEntity(player.world, player.getPosX(), player.getPosY(), player.getPosZ(), player);
                    tnt.setFuse(60);
                    player.world.addEntity(tnt);

                    player.inventory.setInventorySlotContents(player.inventory.currentItem, itemStack);
                    player.inventory.markDirty();
                }
                else {
                    player.sendStatusMessage(new StringTextComponent(I18n.format("message.militarydrones.not_enough_ammunition")), true);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
