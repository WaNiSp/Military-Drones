package com.wanisp.militarydrones.event;

import com.gluecode.fpvdrone.Main;
import com.gluecode.fpvdrone.a.b;
import com.wanisp.militarydrones.entity.PowerfulTNTEntity;
import com.wanisp.militarydrones.item.Drone;
import com.wanisp.militarydrones.item.drones.KamikazeDrone;
import com.wanisp.militarydrones.packet.PacketHandler;
import com.wanisp.militarydrones.packet.droneMode.DroneModeSetPacket;
import com.wanisp.militarydrones.packet.other.SlotLockPacket;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = "militarydrones")
public class PlayerEventHandler {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static void getPlayerBack(PlayerEntity player, CompoundNBT tag, ItemStack itemStack, boolean isKamikaze){
        if(!player.world.isRemote){

            if (player instanceof ServerPlayerEntity) {
                PacketHandler.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
                        new SlotLockPacket(false, -1)
                );

                PacketHandler.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
                        new DroneModeSetPacket(false)
                );
            }

            if(isKamikaze){
                PowerfulTNTEntity tnt = new PowerfulTNTEntity(player.world, player.getPosX(), player.getPosY(), player.getPosZ(), player);
                tnt.setNoGravity(true);
                tnt.setFuse(7);
                player.world.addEntity(tnt);
            }

            player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 20, 100, false, false));

            player.setHealth(tag.getFloat("playerHealth"));

            Vector3d pos = new Vector3d(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
            player.setPositionAndUpdate(pos.x + 0.5, pos.y, pos.z);
            player.rotationPitch = tag.getFloat("pitch");
            player.rotationYaw = tag.getFloat("yaw");

            scheduler.schedule(() -> {
                player.setPose(Pose.STANDING);
                player.recalculateSize();
            }, 325, TimeUnit.MILLISECONDS);

            itemStack.shrink(1);
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            ItemStack itemStack = player.getHeldItemMainhand();

            if (itemStack.getItem() instanceof Drone) {
                if ((Boolean) Main.entityArmStates.getOrDefault(player.getUniqueID(), false)) {
                    getPlayerBack(player, itemStack.getTag(), itemStack,false);

                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;
        ItemStack itemStack = player.getHeldItemMainhand();

        if (itemStack.isEmpty() || !(itemStack.getItem() instanceof KamikazeDrone)) {
            return;
        }

        if (!(Boolean) Main.entityArmStates.getOrDefault(player.getUniqueID(), false)) {
            return;
        }

        Vector3d motion = player.getMotion();

        if (motion.lengthSquared() <= 0.25D) {
            return;
        }


        World world = player.world;

        AxisAlignedBB boundingBox = player.getBoundingBox().expand(motion.scale(0.5)).grow(0.25D);
        List<VoxelShape> collisions = world.getBlockCollisionShapes(player, boundingBox).collect(Collectors.toList());

        if (!collisions.isEmpty()) {
            getPlayerBack(player, itemStack.getTag(), itemStack, true);
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getPlayer() != null && event.getPlayer().world.isRemote) {
            if (b.q) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getPlayer() != null && event.getPlayer().world.isRemote) {
            if (b.q) {
                event.setCanceled(true);
            }
        }
    }
}