package com.wanisp.militarydrones.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.wanisp.militarydrones.item.KamikazeDrone;
import com.wanisp.militarydrones.packet.DroneOverlayPacket;
import com.wanisp.militarydrones.packet.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class DroneOverlayRenderer {

    private static final List<ResourceLocation> DRONE_OVERLAYS = Arrays.asList(
            new ResourceLocation("militarydrones", "textures/misc/video_frame_1.png"),
            new ResourceLocation("militarydrones", "textures/misc/video_frame_2.png"),
            new ResourceLocation("militarydrones", "textures/misc/video_frame_3.png"),
            new ResourceLocation("militarydrones", "textures/misc/video_frame_4.png"),
            new ResourceLocation("militarydrones", "textures/misc/video_frame_5.png"),
            new ResourceLocation("militarydrones", "textures/misc/video_frame_6.png")
    );

    private static final int ANIMATION_SPEED = 8;
    private static int frame = 0;
    private static int tickCounter = 0;

    private static boolean overlayVisible = false;
    private static int overlayTimer = 0;
    private static final int OVERLAY_DURATION = 60;

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HELMET) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && overlayVisible) {
                renderDroneOverlay(event.getMatrixStack());
                overlayTimer--;

                if (overlayTimer <= 0) {
                    overlayVisible = false;
                }
            }
        }
    }

    private static void renderDroneOverlay(MatrixStack matrixStack) {
        Minecraft mc = Minecraft.getInstance();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        // Change texture
        if (++tickCounter >= ANIMATION_SPEED) {
            tickCounter = 0;
            frame = (frame + 1) % DRONE_OVERLAYS.size();
        }

        // Get next texture
        mc.getTextureManager().bindTexture(DRONE_OVERLAYS.get(frame));

        // Get screen width and height
        int width = mc.getMainWindow().getScaledWidth();
        int height = mc.getMainWindow().getScaledHeight();

        // Render texture on all screen
        AbstractGui.blit(matrixStack, 0, 0, 0, 0, width, height, width, height);

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    public static void activateOverlay() {
        overlayTimer = OVERLAY_DURATION;
        overlayVisible = true;
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

        // Ray cast
        Vector3d startPos = player.getPositionVec();
        Vector3d endPos = startPos.add(motion.scale(2));

        World world = player.world;
        RayTraceResult result = world.rayTraceBlocks(new RayTraceContext(
                startPos, endPos, RayTraceContext.BlockMode.COLLIDER,
                RayTraceContext.FluidMode.NONE, player));

        if (result.getType() == RayTraceResult.Type.BLOCK) {
            // Send packet to player to off drone mode
            if (player instanceof ServerPlayerEntity) {
                PacketHandler.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
                        new DroneOverlayPacket()
                );
            }
        }
    }
}
