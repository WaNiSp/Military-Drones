package com.wanisp.militarydrones.recipes;

import com.google.gson.JsonObject;
import com.wanisp.militarydrones.item.drones.DroneWithGrenades;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class GrenadeDroneRecipe extends SpecialRecipe {
    public GrenadeDroneRecipe(ResourceLocation p_i48169_1_) {
        super(p_i48169_1_);
    }

    @Override
    public boolean matches(CraftingInventory craftingInventory, World world) {
        int tntCount = 0;
        ItemStack customItemStack = ItemStack.EMPTY;

        for (int i = 0; i < craftingInventory.getSizeInventory(); i++) {
            ItemStack stack = craftingInventory.getStackInSlot(i);
            if (stack.getItem() == Items.TNT) {
                tntCount++;
            } else if (stack.getItem() instanceof DroneWithGrenades) {
                customItemStack = stack;
            }
        }
        return tntCount == 2 && !customItemStack.isEmpty();
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory craftingInventory) {
        ItemStack customItemStack = ItemStack.EMPTY;

        for (int i = 0; i < craftingInventory.getSizeInventory(); i++) {
            ItemStack stack = craftingInventory.getStackInSlot(i);
            if (stack.getItem() instanceof DroneWithGrenades) {
                customItemStack = stack.copy();
                break;
            }
        }

        DroneWithGrenades.addIntegerToItem(customItemStack, 5);
        return customItemStack;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 3;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.GRENADE_DRONE_RELOAD;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<GrenadeDroneRecipe> {
        @Override
        public GrenadeDroneRecipe read(ResourceLocation recipeId, JsonObject json) {
            return new GrenadeDroneRecipe(recipeId);
        }

        @Override
        public GrenadeDroneRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            return new GrenadeDroneRecipe(recipeId);
        }

        @Override
        public void write(PacketBuffer buffer, GrenadeDroneRecipe recipe) {}
    }
}
