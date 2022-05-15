 package cn.magicst.mamiyaotaru.voxelmap.fabricmod.mixins;
 
 import cn.magicst.mamiyaotaru.voxelmap.fabricmod.FabricModVoxelMap;
 import net.minecraft.class_310;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 
 @Mixin({class_310.class})
 public class APIMixinMinecraftClient
 {
   @Inject(method = {"tick()V"}, at = {@At("RETURN")})
   private void onTick(CallbackInfo ci) {
     FabricModVoxelMap.instance.clientTick(class_310.method_1551());
   }
 }
