package cn.magicst.mamiyaotaru.voxelmap.fabricmod.mixins;

import cn.magicst.mamiyaotaru.voxelmap.VoxelMap;
import cn.magicst.mamiyaotaru.voxelmap.fabricmod.FabricModVoxelMap;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.class_1159;
import net.minecraft.class_1921;
import net.minecraft.class_276;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4599;
import net.minecraft.class_757;
import net.minecraft.class_761;
import net.minecraft.class_765;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_761.class})
public class MixinWorldRenderer {
    @Shadow
    private class_276 field_25274;

    @Shadow
    private class_4599 field_20951;

    @Inject(method = {"render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/util/math/Matrix4f;)V"}, at = {@At("RETURN")})
    private void postRender(class_4587 matrixStack, float partialTicks, long timeSlice, boolean lookingAtBlock, class_4184 camera, class_757 gameRenderer, class_765 lightmapTextureManager, class_1159 matrix4f, CallbackInfo ci) {
        if ((VoxelMap.getInstance().getMapOptions()).showBeacons || (VoxelMap.getInstance().getMapOptions()).showWaypoints) {
            if (class_310.method_29611()) {
                class_276 framebuffer = class_310.method_1551().method_1522();
                GlStateManager._glBindFramebuffer(36008, this.field_25274.field_1476);
                GlStateManager._glBindFramebuffer(36009, framebuffer.field_1476);
                GlStateManager._glBlitFrameBuffer(0, 0, this.field_25274.field_1482, this.field_25274.field_1481, 0, 0, framebuffer.field_1482, framebuffer.field_1481, 256, 9728);
            }
            boolean drawSignForeground = !class_310.method_29611();
            FabricModVoxelMap.onRenderHand(partialTicks, timeSlice, matrixStack, (VoxelMap.getInstance().getMapOptions()).showBeacons, (VoxelMap.getInstance().getMapOptions()).showWaypoints, drawSignForeground, true);
        }
    }

    @Inject(method = {"renderLayer(Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/util/math/MatrixStack;DDDLnet/minecraft/util/math/Matrix4f;)V"}, at = {@At("RETURN")})
    private void postRenderLayer(class_1921 renderLayer, class_4587 matrixStack, double x, double y, double z, class_1159 matrix4f, CallbackInfo ci) {
        if (class_310.method_29611() && (VoxelMap.getInstance().getMapOptions()).showWaypoints && renderLayer == class_1921.method_23583() && (class_310.method_1551()).field_1769.method_29360() != null) {
            (class_310.method_1551()).field_1769.method_29360().method_1235(false);
            FabricModVoxelMap.onRenderHand(class_310.method_1551().method_1488(), 0L, matrixStack, false, true, true, false);
            class_310.method_1551().method_1522().method_1235(false);
        }
    }

    @Inject(method = {"scheduleChunkRender(IIIZ)V"}, at = {@At("RETURN")})
    public void postScheduleChunkRender(int x, int y, int z, boolean dunno, CallbackInfo ci) {
        VoxelMap.instance.getWorldUpdateListener().notifyObservers(x, z);
    }
}
