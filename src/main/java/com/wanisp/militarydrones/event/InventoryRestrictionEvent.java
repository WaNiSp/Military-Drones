package com.wanisp.militarydrones.event;

import com.gluecode.fpvdrone.a.b;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "militarydrones")
public class InventoryRestrictionEvent {

    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player != null && b.q) {
            if (event.getGui() instanceof net.minecraft.client.gui.screen.inventory.InventoryScreen) {
                event.setCanceled(true);
            }
        }
    }
}