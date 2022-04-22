 package cn.magicst.mamiyaotaru.voxelmap.fabricmod.mixins;
 
 import cn.magicst.mamiyaotaru.voxelmap.fabricmod.FabricModVoxelMap;
 import com.mojang.authlib.GameProfile;
 import net.minecraft.class_638;
 import net.minecraft.class_742;
 import net.minecraft.class_746;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 
 @Mixin({class_746.class})
 public abstract class APIMixinClientPlayerEntity extends class_742 {
   public APIMixinClientPlayerEntity() {
     super((class_638)null, (GameProfile)null);
   }
   
   @Inject(method = {"sendChatMessage(Ljava/lang/String;)V"}, at = {@At("HEAD")}, cancellable = true)
   public void onSendChatMessage(String message, CallbackInfo ci) {
     if (!FabricModVoxelMap.instance.onSendChatMessage(message))
       ci.cancel(); 
   }
 }

