 package cn.magicst.mamiyaotaru.voxelmap.gui.overridden;
 
 import net.minecraft.class_2561;
 import net.minecraft.class_4185;
 
 public class GuiOptionButtonMinimap extends class_4185 {
   private final EnumOptionsMinimap enumOptions;
   
   public GuiOptionButtonMinimap(int x, int y, EnumOptionsMinimap par4EnumOptions, class_2561 buttonText, class_4185.class_4241 press) {
     super(x, y, 150, 20, buttonText, press);
     this.enumOptions = par4EnumOptions;
   }
   
   public EnumOptionsMinimap returnEnumOptions() {
     return this.enumOptions;
   }
 }
