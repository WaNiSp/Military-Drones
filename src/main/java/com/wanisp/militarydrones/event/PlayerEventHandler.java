package com.wanisp.militarydrones.event;

import com.wanisp.militarydrones.item.Drone;
import com.wanisp.militarydrones.item.KamikazeDrone;
import com.wanisp.militarydrones.packet.DroneModePacket;
import com.wanisp.militarydrones.packet.PacketHandler;
import com.wanisp.militarydrones.packet.SlotLockPacket;
import net.minecraft.entity.Pose;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
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


            // Send packet to player to off drone mode and unlock slot
            if (player instanceof ServerPlayerEntity) {
                PacketHandler.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
                        new DroneModePacket(false)
                );

                PacketHandler.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
                        new SlotLockPacket(false, -1)
                );
            }

            if(isKamikaze){
                // Create tnt on collision position
                TNTEntity tnt = new TNTEntity(player.world, player.getPosX(), player.getPosY(), player.getPosZ(), player);
                tnt.setNoGravity(true);
                tnt.setFuse(7);
                player.world.addEntity(tnt);
            }


            // Give the player resistance so he doesn't die
            player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 20, 100, false, false));

            // Return player health
            player.setHealth(tag.getFloat("playerHealth"));

            // Teleport and rotate player
            player.setPositionAndUpdate(tag.getDouble("x") + 0.5, tag.getDouble("y"), tag.getDouble("z"));
            player.rotationPitch = tag.getFloat("pitch");
            player.rotationYaw = tag.getFloat("yaw");

            // FPV mod has a bag with eye height and this is fix for it
            scheduler.schedule(() -> {
                player.setPose(Pose.STANDING);
                player.recalculateSize();
            }, delay, TimeUnit.MILLISECONDS);

            // Delete drone from inventory
            itemStack.shrink(1);
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
        // Get player and item stack
        PlayerEntity player = event.player;
        ItemStack itemStack = player.getHeldItemMainhand();

        if (itemStack.isEmpty() || !(itemStack.getItem() instanceof KamikazeDrone)) {
            return;
        }

        // Check tag and are we flying
        CompoundNBT tag = itemStack.getTag();
        if (tag == null || !tag.getBoolean("flying")) {
            return;
        }

        // Get motion and check him
        Vector3d motion = player.getMotion();

        if (motion.lengthSquared() <= 0.1D) {
            return;
        }

        // Ray cast
        Vector3d startPos = player.getPositionVec();
        Vector3d endPos = startPos.add(motion);

        World world = player.world;
        RayTraceResult result = world.rayTraceBlocks(new RayTraceContext(
                startPos, endPos, RayTraceContext.BlockMode.COLLIDER,
                RayTraceContext.FluidMode.NONE, player));

        if (result.getType() == RayTraceResult.Type.BLOCK) {
            //DroneOverlayRenderer.activateOverlay();
            getPlayerBack(player, tag, itemStack, 250, true);
        }
    }
}