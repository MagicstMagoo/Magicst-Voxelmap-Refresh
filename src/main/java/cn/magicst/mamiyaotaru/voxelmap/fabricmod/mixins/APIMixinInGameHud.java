 package cn.magicst.mamiyaotaru.voxelmap.fabricmod.mixins;
 
 import cn.magicst.mamiyaotaru.voxelmap.fabricmod.FabricModVoxelMap;
 import net.minecraft.class_329;
 import net.minecraft.class_4587;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 
 @Mixin({class_329.class})
 public class APIMixinInGameHud {
   @Inject(method = {"render(Lnet/minecraft/client/util/math/MatrixStack;F)V"}, at = {@At("RETURN")})
   private void onRenderGameOverlay(class_4587 matrixStack, float partialTicks, CallbackInfo ci) {
     FabricModVoxelMap.instance.renderOverlay(matrixStack);
   }
 }

