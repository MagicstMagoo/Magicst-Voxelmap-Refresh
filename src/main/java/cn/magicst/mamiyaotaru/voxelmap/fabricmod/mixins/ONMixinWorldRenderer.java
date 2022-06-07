 package cn.magicst.mamiyaotaru.voxelmap.fabricmod.mixins;
 
 import cn.magicst.mamiyaotaru.voxelmap.fabricmod.Share;
 import net.minecraft.class_1158;
 import net.minecraft.class_1160;
 import net.minecraft.class_4587;
 import net.minecraft.class_761;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Redirect;
 
 @Mixin({class_761.class})
 public class ONMixinWorldRenderer {
   @Redirect(method = {"renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lnet/minecraft/util/math/Quaternion;)V"))
   private void onRotate(class_4587 matrixStack, class_1158 quat) {
     if (Share.isOldNorth()) {
       if (quat.equals(class_1160.field_20703.method_23214(90.0F))) {
         return;
       }
       
       if (quat.equals(class_1160.field_20705.method_23214(-90.0F))) {
         return;
       }
     } 
     
     matrixStack.method_22907(quat);
   }
 }
