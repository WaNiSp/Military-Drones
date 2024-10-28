package com.wanisp.militarydrones.recipes;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder("militarydrones")
public class ModRecipes {
    public static final IRecipeSerializer<GrenadeDroneRecipe> GRENADE_DRONE_RELOAD = null;

    public static void registerRecipes(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        event.getRegistry().registerAll(
                new GrenadeDroneRecipe.Serializer().setRegistryName(new ResourceLocation("militarydrones", "grenade_drone_reload"))
        );
    }
}
