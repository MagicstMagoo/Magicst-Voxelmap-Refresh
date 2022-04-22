 package cn.magicst.mamiyaotaru.voxelmap.fabricmod.mixins;
 
 import cn.magicst.mamiyaotaru.voxelmap.fabricmod.FabricModVoxelMap;
 import net.minecraft.class_2658;
 import net.minecraft.class_634;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 
 @Mixin({class_634.class})
 public class APIMixinNetHandlerPlayClient {
   @Inject(method = {"onCustomPayload(Lnet/minecraft/network/packet/s2c/play/CustomPayloadS2CPacket;)V"}, at = {@At("HEAD")}, cancellable = true)
   private void onHandleCustomPayload(class_2658 packet, CallbackInfo ci) {
     if (FabricModVoxelMap.instance.handleCustomPayload(packet))
       ci.cancel(); 
   }
 }

