package com.wanisp.militarydrones.item.drones;

import com.wanisp.militarydrones.item.Drone;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class DroneWithGrenades extends Drone {
    public DroneWithGrenades(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    private void initializeDroneNBT(ItemStack itemStack, PlayerEntity player) {
        itemStack.setTag(new CompoundNBT());
        CompoundNBT tag = itemStack.getOrCreateTag();
        tag.putFloat("droneHealth", player.getMaxHealth());
        tag.putFloat("playerHealth", player.getHealth());
        tag.putFloat("pitch", player.rotationPitch);
        tag.putFloat("yaw", player.rotationYaw);
        tag.putInt("ammunition", 5);
        tag.putDouble("x", player.getPosX());
        tag.putDouble("y", player.getPosY());
        tag.putDouble("z", player.getPosZ());
    }



    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);

        if(!world.isRemote) {
            if (itemStack.getTag() == null) {
                initializeDroneNBT(itemStack, player);
            }
        }

        return super.onItemRightClick(world, player, hand);
    }
}
