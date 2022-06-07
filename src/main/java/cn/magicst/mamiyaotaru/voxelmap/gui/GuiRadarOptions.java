 package cn.magicst.mamiyaotaru.voxelmap.gui;
 import cn.magicst.mamiyaotaru.voxelmap.RadarSettingsManager;
 import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.EnumOptionsMinimap;
 import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.GuiOptionButtonMinimap;
 import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.GuiScreenMinimap;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IVoxelMap;
 import net.minecraft.class_2561;
 import net.minecraft.class_364;
 import net.minecraft.class_4185;
 import net.minecraft.class_437;
 import net.minecraft.class_4587;
 
 public class GuiRadarOptions extends GuiScreenMinimap {
   private static final EnumOptionsMinimap[] relevantOptionsFull = new EnumOptionsMinimap[] { EnumOptionsMinimap.SHOWRADAR, EnumOptionsMinimap.RADARMODE, EnumOptionsMinimap.SHOWHOSTILES, EnumOptionsMinimap.SHOWNEUTRALS, EnumOptionsMinimap.SHOWPLAYERS, EnumOptionsMinimap.SHOWPLAYERNAMES, EnumOptionsMinimap.SHOWPLAYERHELMETS, EnumOptionsMinimap.SHOWMOBHELMETS, EnumOptionsMinimap.RADARFILTERING, EnumOptionsMinimap.RADAROUTLINES };
   private static final EnumOptionsMinimap[] relevantOptionsSimple = new EnumOptionsMinimap[] { EnumOptionsMinimap.SHOWRADAR, EnumOptionsMinimap.RADARMODE, EnumOptionsMinimap.SHOWHOSTILES, EnumOptionsMinimap.SHOWNEUTRALS, EnumOptionsMinimap.SHOWPLAYERS, EnumOptionsMinimap.SHOWFACING };
   private final class_437 parent;
   private final RadarSettingsManager options;
   protected class_2561 screenTitle;
   
   public GuiRadarOptions(class_437 parent, IVoxelMap master) {
     this.parent = parent;
     this.options = master.getRadarOptions();
   }
   public void method_25426() {
     EnumOptionsMinimap[] relevantOptions;
     getButtonList().clear();
     method_25396().clear();
     int var2 = 0;
     this.screenTitle = (class_2561)class_2561.method_43471("options.minimap.radar.title");
     
     if (this.options.radarMode == 2) {
       relevantOptions = relevantOptionsFull;
     } else {
       relevantOptions = relevantOptionsSimple;
     } 
     
     for (EnumOptionsMinimap option : relevantOptions) {
       GuiOptionButtonMinimap optionButton = new GuiOptionButtonMinimap(getWidth() / 2 - 155 + var2 % 2 * 160, getHeight() / 6 + 24 * (var2 >> 1), option, (class_2561)class_2561.method_43470(this.options.getKeyText(option)), this::optionClicked);
       method_37063((class_364)optionButton);
       var2++;
     } 
     
     for (Object buttonObj : getButtonList()) {
       if (buttonObj instanceof GuiOptionButtonMinimap) { GuiOptionButtonMinimap button = (GuiOptionButtonMinimap)buttonObj;
         if (!button.returnEnumOptions().equals(EnumOptionsMinimap.SHOWRADAR)) {
           button.field_22763 = this.options.showRadar;
         }
         
         if (button.returnEnumOptions().equals(EnumOptionsMinimap.SHOWPLAYERS)) {
           button.field_22763 = (button.field_22763 && (this.options.radarAllowed.booleanValue() || this.options.radarPlayersAllowed.booleanValue())); continue;
         }  if (!button.returnEnumOptions().equals(EnumOptionsMinimap.SHOWNEUTRALS) && !button.returnEnumOptions().equals(EnumOptionsMinimap.SHOWHOSTILES)) {
           if (!button.returnEnumOptions().equals(EnumOptionsMinimap.SHOWPLAYERHELMETS) && !button.returnEnumOptions().equals(EnumOptionsMinimap.SHOWPLAYERNAMES)) {
             if (button.returnEnumOptions().equals(EnumOptionsMinimap.SHOWMOBHELMETS))
               button.field_22763 = (button.field_22763 && (this.options.showNeutrals || this.options.showHostiles) && (this.options.radarAllowed.booleanValue() || this.options.radarMobsAllowed.booleanValue())); 
             continue;
           } 
           button.field_22763 = (button.field_22763 && this.options.showPlayers && (this.options.radarAllowed.booleanValue() || this.options.radarPlayersAllowed.booleanValue()));
           continue;
         } 
         button.field_22763 = (button.field_22763 && (this.options.radarAllowed.booleanValue() || this.options.radarMobsAllowed.booleanValue())); }
     
     } 
 
     
     if (this.options.radarMode == 2) {
       method_37063((class_364)new class_4185(getWidth() / 2 - 155, getHeight() / 6 + 144 - 6, 150, 20, (class_2561)class_2561.method_43471("options.minimap.radar.selectmobs"), buttonx -> getMinecraft().method_1507((class_437)new GuiMobs((class_437)this, this.options))));
     }
     
     method_37063((class_364)new class_4185(getWidth() / 2 - 100, getHeight() / 6 + 168, 200, 20, (class_2561)class_2561.method_43471("gui.done"), buttonx -> getMinecraft().method_1507(this.parent)));
   }
   
   protected void optionClicked(class_4185 buttonClicked) {
     EnumOptionsMinimap option = ((GuiOptionButtonMinimap)buttonClicked).returnEnumOptions();
     this.options.setOptionValue(option);
     if (((GuiOptionButtonMinimap)buttonClicked).returnEnumOptions().equals(EnumOptionsMinimap.RADARMODE)) {
       method_25426();
     } else {
       buttonClicked.method_25355((class_2561)class_2561.method_43470(this.options.getKeyText(option)));
       
       for (Object buttonObj : getButtonList()) {
         if (buttonObj instanceof GuiOptionButtonMinimap) { GuiOptionButtonMinimap button = (GuiOptionButtonMinimap)buttonObj;
           if (!button.returnEnumOptions().equals(EnumOptionsMinimap.SHOWRADAR)) {
             button.field_22763 = this.options.showRadar;
           }
           
           if (button.returnEnumOptions().equals(EnumOptionsMinimap.SHOWPLAYERS)) {
             button.field_22763 = (button.field_22763 && (this.options.radarAllowed.booleanValue() || this.options.radarPlayersAllowed.booleanValue())); continue;
           }  if (!button.returnEnumOptions().equals(EnumOptionsMinimap.SHOWNEUTRALS) && !button.returnEnumOptions().equals(EnumOptionsMinimap.SHOWHOSTILES)) {
             if (!button.returnEnumOptions().equals(EnumOptionsMinimap.SHOWPLAYERHELMETS) && !button.returnEnumOptions().equals(EnumOptionsMinimap.SHOWPLAYERNAMES)) {
               if (button.returnEnumOptions().equals(EnumOptionsMinimap.SHOWMOBHELMETS))
                 button.field_22763 = (button.field_22763 && (this.options.showNeutrals || this.options.showHostiles) && (this.options.radarAllowed.booleanValue() || this.options.radarMobsAllowed.booleanValue())); 
               continue;
             } 
             button.field_22763 = (button.field_22763 && this.options.showPlayers && (this.options.radarAllowed.booleanValue() || this.options.radarPlayersAllowed.booleanValue()));
             continue;
           } 
           button.field_22763 = (button.field_22763 && (this.options.radarAllowed.booleanValue() || this.options.radarMobsAllowed.booleanValue())); }
       
       } 
     } 
   }
 
 
   
   public void method_25394(class_4587 matrixStack, int mouseX, int mouseY, float partialTicks) {
     drawMap(matrixStack);
     method_25420(matrixStack);
     method_27534(matrixStack, getFontRenderer(), this.screenTitle, getWidth() / 2, 20, 16777215);
     super.method_25394(matrixStack, mouseX, mouseY, partialTicks);
   }
 }

