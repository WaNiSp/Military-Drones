package com.wanisp.militarydrones.item;

import com.gluecode.fpvdrone.a.b;
import com.wanisp.militarydrones.event.SlotChangeHandler;
import com.wanisp.militarydrones.packet.droneMode.DroneModeOffPacket;
import com.wanisp.militarydrones.packet.PacketHandler;
import com.wanisp.militarydrones.packet.droneMode.DroneModeOnPacket;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class Drone extends Item {
    private static final double MAX_DISTANCE = 150.0;

    public Drone(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    public boolean isWithinMaxDistance(Vector3d dronePosition, PlayerEntity player, ItemStack itemStack) {
        CompoundNBT tag = itemStack.getTag();
        if(tag == null) {
            return false;
        }

        Vector3d playerPosition = new Vector3d(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
        if(dronePosition.distanceTo(playerPosition) < MAX_DISTANCE){
            return true;
        }

        player.sendStatusMessage(new StringTextComponent(I18n.format("message.militarydrones.drone_distance")), true);
        return false;
    }


    private void initializeDroneNBT(ItemStack itemStack, PlayerEntity player) {
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

    private void toggleDroneMode(boolean active) {
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
                initializeDroneNBT(itemStack, player);
            }
        }
        else {
            if(b.q) {
                if(!isWithinMaxDistance(player.getPositionVec(), player, itemStack)){
                    return super.onItemRightClick(world, player, hand);
                }

                PacketHandler.INSTANCE.sendToServer(new DroneModeOffPacket(itemStack));
                SlotChangeHandler.setSlotLock(false, -1);
                toggleDroneMode(false);
            } else {
                PacketHandler.INSTANCE.sendToServer(new DroneModeOnPacket(itemStack));
                SlotChangeHandler.setSlotLock(true, player.inventory.currentItem);
                toggleDroneMode(true);
            }
        }


        return super.onItemRightClick(world, player, hand);
    }
}