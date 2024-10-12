package com.wanisp.militarydrones.event;

import com.gluecode.fpvdrone.a.b;
import com.wanisp.militarydrones.item.ScoutDrone;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mod.EventBusSubscriber(modid = "militarydrones")
public class PlayerEventHandler {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            ItemStack itemStack = player.getHeldItemMainhand();

            if (itemStack.getItem() instanceof ScoutDrone) {
                CompoundNBT tag = itemStack.getTag();

                if (tag != null && tag.getBoolean("flying")) {
                    // Cancel death
                    event.setCanceled(true);

                    // Return player health
                    player.setHealth(tag.getFloat("playerHealth"));

                    // Give the player resistance so he doesn't die
                    player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 20, 100, false, false));

                    // Get saved position and rotation
                    float pitch = tag.getFloat("pitch");
                    float yaw = tag.getFloat("yaw");

                    double x = tag.getDouble("x");
                    double y = tag.getDouble("y");
                    double z = tag.getDouble("z");

                    // Teleport and rotate player
                    player.setPositionAndUpdate(x + 0.5, y, z);
                    player.rotationPitch = pitch;
                    player.rotationYaw = yaw;

                    // Delete drone from inventory
                    itemStack.shrink(1);

                    // Disable drone mode
                    b.v = false;
                    b.d();

                    // FPV mod has a bag with eye height and this is fix for it
                    scheduler.schedule(() -> {
                        player.setPose(Pose.STANDING);
                        player.recalculateSize();
                    }, 500, TimeUnit.MILLISECONDS);
                }
            }
        }
    }
}
