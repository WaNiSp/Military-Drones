package com.wanisp.militarydrones.item.drones;

import com.wanisp.militarydrones.item.Drone;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

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

    @Override
    public void addInformation(ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag iTooltipFlag) {
        tooltip.add(new TranslationTextComponent("tooltip.militarydrones.drone_with_grenades"));
        if(itemStack.getTag() != null && itemStack.getTag().contains("ammunition")) {
            tooltip.add(new StringTextComponent(I18n.format("message.militarydrones.ammunition") + itemStack.getTag().getInt("ammunition")));
        }
        super.addInformation(itemStack, world, tooltip, iTooltipFlag);
    }
}
