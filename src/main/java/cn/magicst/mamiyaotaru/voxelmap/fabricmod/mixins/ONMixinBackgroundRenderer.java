package cn.magicst.mamiyaotaru.voxelmap.fabricmod.mixins;

import cn.magicst.mamiyaotaru.voxelmap.fabricmod.Share;
import net.minecraft.class_1160;
import net.minecraft.class_758;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({class_758.class})
public class ONMixinBackgroundRenderer {
    @Redirect(method = {"render(Lnet/minecraft/client/render/Camera;FLnet/minecraft/client/world/ClientWorld;IF)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3f;dot(Lnet/minecraft/util/math/Vec3f;)F"))
    private static float onDotProduct(class_1160 vec3d, class_1160 arg) {
        if (Share.isOldNorth())
            arg = new class_1160(0.0F, 0.0F, -arg.method_4943());
        return vec3d.method_4950(arg);
    }
}
