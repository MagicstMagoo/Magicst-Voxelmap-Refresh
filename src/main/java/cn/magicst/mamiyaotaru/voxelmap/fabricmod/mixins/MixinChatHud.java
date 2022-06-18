package cn.magicst.mamiyaotaru.voxelmap.fabricmod.mixins;

import cn.magicst.mamiyaotaru.voxelmap.VoxelMap;
import net.minecraft.class_2561;
import net.minecraft.class_338;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_338.class})
public abstract class MixinChatHud {
    @Inject(method = {"addMessage(Lnet/minecraft/text/Text;)V"}, at = {@At("HEAD")})
    private void addMessage(class_2561 message, CallbackInfo ci) {
        VoxelMap.checkPermissionMessages(message);
    }
}
