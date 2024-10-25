package com.wanisp.militarydrones.event;

import com.wanisp.militarydrones.item.drones.DroneWithGrenades;
import com.wanisp.militarydrones.packet.PacketHandler;
import com.wanisp.militarydrones.packet.other.DropGrenadePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = "militarydrones")
public class KeyInputEvent {
    public static KeyBinding dropGrenadeKey;

    public static void setupKey() {
        dropGrenadeKey = new KeyBinding("key.militarydrones.drop_grenade", GLFW.GLFW_KEY_G, "key.categories.militarydrones");
        ClientRegistry.registerKeyBinding(dropGrenadeKey);
    }

    @SubscribeEvent
    public static void onKeyInput(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;

        if (mc.world == null || player == null || mc.currentScreen != null) {
            return;
        }

        if(dropGrenadeKey.isPressed() && mc.currentScreen == null) {
            ItemStack itemStack = player.getHeldItemMainhand();

            if(!itemStack.isEmpty() && itemStack.getItem() instanceof DroneWithGrenades) {
                PacketHandler.INSTANCE.sendToServer(new DropGrenadePacket(mc.player.getHeldItemMainhand()));
            }
        }
    }
}
