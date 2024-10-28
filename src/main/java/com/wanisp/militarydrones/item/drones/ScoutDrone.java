package com.wanisp.militarydrones.item.drones;

import com.wanisp.militarydrones.item.Drone;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ScoutDrone extends Drone {
    public ScoutDrone(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    @Override
    public void addInformation(ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag iTooltipFlag) {
        tooltip.add(new TranslationTextComponent("tooltip.militarydrones.scout_drone"));
        super.addInformation(itemStack, world, tooltip, iTooltipFlag);
    }
}