package com.wanisp.militarydrones.item;

import com.wanisp.militarydrones.MilitaryDronesMod;
import com.wanisp.militarydrones.item.drones.KamikazeDrone;
import com.wanisp.militarydrones.item.drones.ScoutDrone;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MilitaryDronesMod.MOD_ID);

    public static final RegistryObject<Item> SCOUT_DRONE = ITEMS.register("scout_drone",
            () -> new ScoutDrone(new Item.Properties().group(ItemGroup.COMBAT).maxStackSize(1)));

    public static final RegistryObject<Item> KAMIKAZE_DRONE = ITEMS.register("kamikaze_drone",
            () -> new KamikazeDrone(new Item.Properties().group(ItemGroup.COMBAT).maxStackSize(1)));

    public static final RegistryObject<Item> CHIP = ITEMS.register("chip",
            () -> new Item(new Item.Properties().group(ItemGroup.COMBAT).maxStackSize(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
