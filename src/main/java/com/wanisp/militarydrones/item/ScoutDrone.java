package com.wanisp.militarydrones.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import com.gluecode.fpvdrone.a.b;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScoutDrone extends Item {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final double MAX_DISTANCE = 150.0;

    public ScoutDrone(Properties properties) {
        super(properties);
    }


    public boolean checkDistance(Vector3d dronePosition, Vector3d playerPosition) {
        double x = Math.abs(dronePosition.x - playerPosition.x);
        double y = Math.abs(dronePosition.y - playerPosition.y);
        double z = Math.abs(dronePosition.z - playerPosition.z);

        return x + y + z < MAX_DISTANCE;
    }

    public void sendMessage(PlayerEntity player, String key) {
        player.sendStatusMessage(new StringTextComponent(I18n.format(key)), true);
    }


    public void savePositionAndRotation(CompoundNBT tag, Vector3d position, float pitch, float yaw) {
        // Saving position and rotation into tag
        tag.putDouble("x", position.x);
        tag.putDouble("y", position.y);
        tag.putDouble("z", position.z);

        tag.putFloat("pitch", pitch);
        tag.putFloat("yaw", yaw);
    }

    public Vector3d getPosition(CompoundNBT tag) {
        return new Vector3d(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
    }


    public void changeHealthOnDrone(CompoundNBT tag, PlayerEntity player) {
        // Save player health and get drone health
        tag.putFloat("playerHealth", player.getHealth());
        player.setHealth(tag.getFloat("droneHealth"));
    }

    public void changeHealthOnPlayer(CompoundNBT tag, PlayerEntity player) {
        // Save drone health and get player health
        tag.putFloat("droneHealth", player.getHealth());
        player.setHealth(tag.getFloat("playerHealth"));
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {

        ItemStack itemStack = p_77659_2_.getHeldItem(p_77659_3_);

        p_77659_2_.getCooldownTracker().setCooldown(this, 30);

        // If there's no tag
        if (!itemStack.hasTag()) {
            itemStack.setTag(new CompoundNBT());

            // Create own health for drone
            itemStack.getTag().putFloat("droneHealth", p_77659_2_.getMaxHealth());
            itemStack.getTag().putFloat("playerHealth", p_77659_2_.getHealth());
        }


        // Get tag
        CompoundNBT tag = itemStack.getTag();

        if (tag.getBoolean("flying")) {
            // Get position
            Vector3d pos = getPosition(tag);

            // Check how far drone from player
            if (!checkDistance(p_77659_2_.getPositionVec(), new Vector3d(pos.x, pos.y, pos.z))) {
                sendMessage(p_77659_2_, "message.militarydrones.drone_distance");
                return super.onItemRightClick(p_77659_1_, p_77659_2_, p_77659_3_);
            }

            tag.putBoolean("flying", false);

            // Get player health back and set drone health
            changeHealthOnPlayer(tag, p_77659_2_);

            // Give the player resistance so he doesn't die
            p_77659_2_.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 20, 100, false, false));

            // Get rotation
            float pitch = tag.getFloat("pitch");
            float yaw = tag.getFloat("yaw");


            // Get player back
            p_77659_2_.setPosition(pos.x + 0.5, pos.y, pos.z);
            p_77659_2_.rotationPitch = pitch;
            p_77659_2_.rotationYaw = yaw;

            // Disable drone mode
            b.v = false;
            b.d();

            // FPV mod has a bag with eye height and this is fix for it
            scheduler.schedule(() -> {
                p_77659_2_.setPose(Pose.STANDING);
                p_77659_2_.recalculateSize();
            }, 250, TimeUnit.MILLISECONDS);

        } else {
            tag.putBoolean("flying", true);

            // Save position and rotation
            savePositionAndRotation(tag, p_77659_2_.getPositionVec(), p_77659_2_.rotationPitch, p_77659_2_.rotationYaw);

            // Get drone health and set player health
            changeHealthOnDrone(tag, p_77659_2_);

            // Enable drone mode
            b.v = true;
            b.d();
        }

        return super.onItemRightClick(p_77659_1_, p_77659_2_, p_77659_3_);
    }
}