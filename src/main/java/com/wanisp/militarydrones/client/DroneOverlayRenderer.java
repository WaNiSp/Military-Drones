package com.wanisp.militarydrones.client;

import com.gluecode.fpvdrone.Main;
import com.gluecode.fpvdrone.a.b;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.wanisp.militarydrones.item.drones.KamikazeDrone;
import com.wanisp.militarydrones.packet.PacketHandler;
import com.wanisp.militarydrones.packet.visual.DroneOverlayPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = "militarydrones")
public class DroneOverlayRenderer {

    private static final List<ResourceLocation> DRONE_OVERLAYS = Arrays.asList(
            new ResourceLocation("militarydrones", "textures/misc/video_frame_1.png"),
            new ResourceLocation("militarydrones", "textures/misc/video_frame_2.png"),
            new ResourceLocation("militarydrones", "textures/misc/video_frame_3.png"),
            new ResourceLocation("militarydrones", "textures/misc/video_frame_4.png"),
            new ResourceLocation("militarydrones", "textures/misc/video_frame_5.png"),
            new ResourceLocation("militarydrones", "textures/misc/video_frame_6.png")
    );

    private static final ResourceLocation DRONE_OVERLAY_TEXTURE =
            new ResourceLocation("militarydrones", "textures/misc/drone_overlay.png");

    private static final int ANIMATION_SPEED = 4;
    private static int frame = 0;
    private static int tickCounter = 0;

    private static boolean overlayVisible = false;
    private static int overlayTimer = 0;
    private static final int OVERLAY_DURATION = 40;

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HELMET) {
            if(b.q) {
                renderDroneViewOverlay(event.getMatrixStack());
            }

            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && overlayVisible) {
                renderInterferenceOverlay(event.getMatrixStack());
                overlayTimer--;

                if (overlayTimer <= 0) {
                    overlayVisible = false;
                }
            }
        }
    }

    private static void renderDroneViewOverlay(MatrixStack matrixStack) {
        Minecraft mc = Minecraft.getInstance();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        mc.getTextureManager().bindTexture(DRONE_OVERLAY_TEXTURE);

        int width = mc.getMainWindow().getScaledWidth();
        int height = mc.getMainWindow().getScaledHeight();

        AbstractGui.blit(matrixStack, 0, 0, 0, 0, width, height, width, height);

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    private static void renderInterferenceOverlay(MatrixStack matrixStack) {
        Minecraft mc = Minecraft.getInstance();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        if (++tickCounter >= ANIMATION_SPEED) {
            tickCounter = 0;
            frame = (frame + 1) % DRONE_OVERLAYS.size();
        }

        mc.getTextureManager().bindTexture(DRONE_OVERLAYS.get(frame));

        int width = mc.getMainWindow().getScaledWidth();
        int height = mc.getMainWindow().getScaledHeight();

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
        PlayerEntity player = event.player;
        ItemStack itemStack = player.getHeldItemMainhand();

        if (itemStack.isEmpty() || !(itemStack.getItem() instanceof KamikazeDrone)) {
            return;
        }

        if (!(Boolean) Main.entityArmStates.getOrDefault(player.getUniqueID(), false)) {
            return;
        }


        Vector3d motion = player.getMotion();
        World world = player.world;

        Vector3d startVec = player.getEyePosition(1.0F);
        Vector3d endVec = startVec.add(motion.scale(2));

        RayTraceResult rayTraceResult = world.rayTraceBlocks(new RayTraceContext(startVec, endVec,
                RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player
        ));

        if (rayTraceResult.getType() == RayTraceResult.Type.BLOCK) {
            if (player instanceof ServerPlayerEntity) {
                PacketHandler.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
                        new DroneOverlayPacket()
                );
            }
        }
    }
}
