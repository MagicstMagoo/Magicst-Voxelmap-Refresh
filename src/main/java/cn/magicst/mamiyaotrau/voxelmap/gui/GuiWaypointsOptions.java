 package cn.magicst.mamiyaotaru.voxelmap.gui;

 import cn.magicst.mamiyaotaru.voxelmap.MapSettingsManager;
 import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.EnumOptionsMinimap;
 import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.GuiOptionButtonMinimap;
 import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.GuiOptionSliderMinimap;
 import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.GuiScreenMinimap;
 import net.minecraft.class_2561;
 import net.minecraft.class_2585;
 import net.minecraft.class_2588;
 import net.minecraft.class_364;
 import net.minecraft.class_4185;
 import net.minecraft.class_437;
 import net.minecraft.class_4587;
 
 public class GuiWaypointsOptions extends GuiScreenMinimap {
   private static final EnumOptionsMinimap[] relevantOptions = new EnumOptionsMinimap[] { EnumOptionsMinimap.WAYPOINTDISTANCE, EnumOptionsMinimap.DEATHPOINTS };
   private final class_437 parent;
   private final MapSettingsManager options;
   protected class_2561 screenTitle;
   
   public GuiWaypointsOptions(class_437 parent, MapSettingsManager options) {
     this.parent = parent;
     this.options = options;
   }
   
   public void method_25426() {
     int var2 = 0;
     this.screenTitle = (class_2561)new class_2588("options.minimap.waypoints.title");
     
     for (EnumOptionsMinimap option : relevantOptions) {
       if (option.isFloat()) {
         float distance = this.options.getOptionFloatValue(option);
         if (distance < 0.0F) {
           distance = 10001.0F;
         }
         
         distance = (distance - 50.0F) / 9951.0F;
         method_37063((class_364)new GuiOptionSliderMinimap(getWidth() / 2 - 155 + var2 % 2 * 160, getHeight() / 6 + 24 * (var2 >> 1), option, distance, (ISettingsManager)this.options));
       } else {
         GuiOptionButtonMinimap optionButton = new GuiOptionButtonMinimap(getWidth() / 2 - 155 + var2 % 2 * 160, getHeight() / 6 + 24 * (var2 >> 1), option, (class_2561)new class_2585(this.options.getKeyText(option)), this::optionClicked);
         method_37063((class_364)optionButton);
       } 
       
       var2++;
     } 
     
     method_37063((class_364)new class_4185(getWidth() / 2 - 100, getHeight() / 6 + 168, 200, 20, (class_2561)new class_2588("gui.done"), button -> getMinecraft().method_1507(this.parent)));
   }
   
   protected void optionClicked(class_4185 par1GuiButton) {
     EnumOptionsMinimap option = ((GuiOptionButtonMinimap)par1GuiButton).returnEnumOptions();
     this.options.setOptionValue(option);
     par1GuiButton.method_25355((class_2561)new class_2585(this.options.getKeyText(option)));
   }
   
   public void method_25394(class_4587 matrixStack, int mouseX, int mouseY, float partialTicks) {
     drawMap(matrixStack);
     method_25420(matrixStack);
     method_27534(matrixStack, this.field_22793, this.screenTitle, getWidth() / 2, 20, 16777215);
     super.method_25394(matrixStack, mouseX, mouseY, partialTicks);
   }
 }

