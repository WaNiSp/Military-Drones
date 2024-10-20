package com.wanisp.militarydrones.event;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "militarydrones")
public class SlotChangeHandler {

    private static boolean isSlotLocked = false;
    private static int lockedSlot = -1;

    public static void setSlotLock(boolean lock, int slot) {
        isSlotLocked = lock;
        lockedSlot = slot;
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;

        if (isSlotLocked && lockedSlot != -1) {
            if (player.inventory.currentItem != lockedSlot) {
                player.inventory.currentItem = lockedSlot;
            }
        }
    }
}
