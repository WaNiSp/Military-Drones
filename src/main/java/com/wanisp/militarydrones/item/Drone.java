package com.wanisp.militarydrones.item;

import com.gluecode.fpvdrone.a.b;
import com.wanisp.militarydrones.event.SlotChangeHandler;
import com.wanisp.militarydrones.packet.droneMode.DroneModeOffPacket;
import com.wanisp.militarydrones.packet.PacketHandler;
import com.wanisp.militarydrones.packet.droneMode.DroneModeOnPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class Drone extends Item {
    private static final double MAX_DISTANCE = 150.0;

    public Drone(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    private void changeMode(boolean active) {
        b.q = !active;
        b.v = active;
        b.d();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {

        ItemStack itemStack = player.getHeldItem(hand);
        player.getCooldownTracker().setCooldown(this, 30);


        if(!world.isRemote) {
            if (itemStack.getTag() == null) {
                itemStack.setTag(new CompoundNBT());
                CompoundNBT tag = itemStack.getOrCreateTag();

                tag.putFloat("droneHealth", player.getMaxHealth());
                tag.putFloat("playerHealth", player.getHealth());

                tag.putFloat("pitch", player.rotationPitch);
                tag.putFloat("yaw", player.rotationYaw);

                tag.putDouble("x", player.getPosX());
                tag.putDouble("y", player.getPosY());
                tag.putDouble("z", player.getPosZ());
            }
        }
        else {
            if(b.q) {
                PacketHandler.INSTANCE.sendToServer(new DroneModeOffPacket(itemStack));
                SlotChangeHandler.setSlotLock(false, -1);
                changeMode(false);
            } else {
                PacketHandler.INSTANCE.sendToServer(new DroneModeOnPacket(itemStack));
                SlotChangeHandler.setSlotLock(true, player.inventory.currentItem);
                changeMode(true);
            }
        }


        return super.onItemRightClick(world, player, hand);
    }
}
