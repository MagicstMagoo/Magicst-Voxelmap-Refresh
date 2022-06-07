 package cn.magicst.mamiyaotaru.voxelmap.persistent;
 
 import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.EnumOptionsMinimap;
 import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.GuiOptionButtonMinimap;
 import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.GuiScreenMinimap;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IVoxelMap;
 import net.minecraft.class_2561;
 import net.minecraft.class_4185;
 import net.minecraft.class_437;
 import net.minecraft.class_4587;
 
 public class GuiPersistentMapOptions
   extends GuiScreenMinimap {
   private final class_437 parent;
   private static EnumOptionsMinimap[] relevantOptions;
   private final PersistentMapSettingsManager options;
   private final class_2561 screenTitle = (class_2561)class_2561.method_43471("options.worldmap.title");
   private final class_2561 cacheSettings = (class_2561)class_2561.method_43471("options.worldmap.cachesettings");
   private final class_2561 warning = (class_2561)class_2561.method_43471("options.worldmap.warning");
   private static EnumOptionsMinimap[] relevantOptions2;
   
   public GuiPersistentMapOptions(class_437 parent, IVoxelMap master) {
     this.parent = parent;
     this.options = master.getPersistentMapOptions();
   }
   public void method_25426() {
   }

   protected void optionClicked(class_4185 par1GuiButton) {
     EnumOptionsMinimap option = ((GuiOptionButtonMinimap)par1GuiButton).returnEnumOptions();
     this.options.setOptionValue(option);
     par1GuiButton.method_25355((class_2561)class_2561.method_43470(this.options.getKeyText(option)));
     
     for (Object buttonObj : getButtonList()) {
       if (buttonObj instanceof GuiOptionButtonMinimap) {
         GuiOptionButtonMinimap button = (GuiOptionButtonMinimap)buttonObj;
         if (button.returnEnumOptions().equals(EnumOptionsMinimap.SHOWWAYPOINTNAMES))
           button.field_22763 = this.options.showWaypoints; 
       } 
     } 
   }
   
   public void method_25394(class_4587 matrixStack, int par1, int par2, float par3) {
   }
 }

