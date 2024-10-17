package com.wanisp.militarydrones.event;

import com.gluecode.fpvdrone.a.b;
import com.wanisp.militarydrones.item.Drone;
import com.wanisp.militarydrones.item.KamikazeDrone;
import com.wanisp.militarydrones.packet.DroneModePacket;
import com.wanisp.militarydrones.packet.PacketHandler;
import net.minecraft.entity.Pose;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mod.EventBusSubscriber(modid = "militarydrones")
public class PlayerEventHandler {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static void getPlayerBack(PlayerEntity player, CompoundNBT tag, ItemStack itemStack, int delay, boolean isKamikaze){
        if(!player.world.isRemote){

            // Send packet to player to off drone mode
            if (player instanceof ServerPlayerEntity) {
                PacketHandler.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
                        new DroneModePacket(false)
                );
            }

            if(isKamikaze){
                // Create tnt on collision position
                TNTEntity tnt = new TNTEntity(player.world, player.getPosX(), player.getPosY(), player.getPosZ(), player);
                tnt.setNoGravity(true);
                tnt.setFuse(7);
                player.world.addEntity(tnt);
            }

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

            // FPV mod has a bag with eye height and this is fix for it
            scheduler.schedule(() -> {
                player.setPose(Pose.STANDING);
                player.recalculateSize();
            }, delay, TimeUnit.MILLISECONDS);
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            ItemStack itemStack = player.getHeldItemMainhand();

            if (itemStack.getItem() instanceof Drone) {
                CompoundNBT tag = itemStack.getTag();

                if (tag != null && tag.getBoolean("flying")) {
                    // Return to player everything
                    getPlayerBack(player, tag, itemStack, 500, false);

                    // Cancel death
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Get player and world
        PlayerEntity player = event.player;

        ItemStack itemStack = player.getHeldItemMainhand();

        // Get item and check if it's our kamikaze drone in fly mode
        if (itemStack.getItem() instanceof KamikazeDrone) {
            CompoundNBT tag = itemStack.getTag();
            if (tag != null && tag.getBoolean("flying")) {
                // Get hit box and grow him
                AxisAlignedBB boundingBox = player.getBoundingBox().grow(0.5D, 0.5D, 0.5D);
                World world = player.world;

                // Check if player collision with something
                if (!world.hasNoCollisions(boundingBox)) {
                    // If we're not collision very slow
                    Vector3d motion = player.getMotion();
                    if (motion.length() > 0.5D) {
                        // Return to player everything
                        getPlayerBack(player, tag, itemStack, 500, true);
                    }
                }
            }
        }
    }
}
