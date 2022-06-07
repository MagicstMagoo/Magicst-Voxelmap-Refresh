 package cn.magicst.mamiyaotaru.voxelmap.gui;
 
 import cn.magicst.mamiyaotaru.voxelmap.MapSettingsManager;
 import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.EnumOptionsMinimap;
 import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.GuiOptionButtonMinimap;
 import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.GuiScreenMinimap;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IVoxelMap;
 import cn.magicst.mamiyaotaru.voxelmap.persistent.GuiPersistentMapOptions;
 import cn.magicst.mamiyaotaru.voxelmap.util.I18nUtils;
 import net.minecraft.class_2561;
 import net.minecraft.class_364;
 import net.minecraft.class_4185;
 import net.minecraft.class_437;
 import net.minecraft.class_4587;
 
 public class GuiMinimapOptions
   extends GuiScreenMinimap {
   private final class_437 parent;
   protected String screenTitle = "Minimap Options"; private final IVoxelMap master; private final MapSettingsManager options;
   
   public GuiMinimapOptions(class_437 parent, IVoxelMap master) {
     this.parent = parent;
     this.master = master;
     this.options = master.getMapOptions();
   }
   
   public void method_25426() {
     EnumOptionsMinimap[] relevantOptions = { EnumOptionsMinimap.COORDS, EnumOptionsMinimap.HIDE, EnumOptionsMinimap.LOCATION, EnumOptionsMinimap.SIZE, EnumOptionsMinimap.SQUARE, EnumOptionsMinimap.ROTATES, EnumOptionsMinimap.BEACONS, EnumOptionsMinimap.CAVEMODE };
     int var2 = 0;
     this.screenTitle = I18nUtils.getString("options.minimap.title", new Object[0]);
     
     for (EnumOptionsMinimap option : relevantOptions) {
       GuiOptionButtonMinimap optionButton = new GuiOptionButtonMinimap(getWidth() / 2 - 155 + var2 % 2 * 160, getHeight() / 6 + 24 * (var2 >> 1), option, (class_2561)class_2561.method_43470(this.options.getKeyText(option)), this::optionClicked);
       method_37063((class_364)optionButton);
       if (option.equals(EnumOptionsMinimap.CAVEMODE)) {
         optionButton.field_22763 = this.options.cavesAllowed.booleanValue();
       }
       
       var2++;
     } 
     
     class_4185 radarOptionsButton = new class_4185(getWidth() / 2 - 155, getHeight() / 6 + 120 - 6, 150, 20, (class_2561)class_2561.method_43471("options.minimap.radar"), button -> getMinecraft().method_1507((class_437)new GuiRadarOptions((class_437)this, this.master)));
     radarOptionsButton.field_22763 = ((this.master.getRadarOptions()).radarAllowed.booleanValue() || (this.master.getRadarOptions()).radarMobsAllowed.booleanValue() || (this.master.getRadarOptions()).radarPlayersAllowed.booleanValue());
     method_37063((class_364)radarOptionsButton);
     method_37063((class_364)new class_4185(getWidth() / 2 + 5, getHeight() / 6 + 120 - 6, 150, 20, (class_2561)class_2561.method_43471("options.minimap.detailsperformance"), button -> getMinecraft().method_1507((class_437)new GuiMinimapPerformance((class_437)this, this.master))));
     method_37063((class_364)new class_4185(getWidth() / 2 - 155, getHeight() / 6 + 144 - 6, 150, 20, (class_2561)class_2561.method_43471("options.controls"), button -> getMinecraft().method_1507((class_437)new GuiMinimapControls((class_437)this, this.master))));
     method_37063((class_364)new class_4185(getWidth() / 2 + 5, getHeight() / 6 + 144 - 6, 150, 20, (class_2561)class_2561.method_43471("options.minimap.worldmap"), button -> getMinecraft().method_1507((class_437)new GuiPersistentMapOptions((class_437)this, this.master))));
     method_37063((class_364)new class_4185(getWidth() / 2 - 100, getHeight() / 6 + 168, 200, 20, (class_2561)class_2561.method_43471("gui.done"), button -> getMinecraft().method_1507(this.parent)));
   }
   
   protected void optionClicked(class_4185 par1GuiButton) {
     EnumOptionsMinimap option = ((GuiOptionButtonMinimap)par1GuiButton).returnEnumOptions();
     this.options.setOptionValue(option);
     par1GuiButton.method_25355((class_2561)class_2561.method_43470(this.options.getKeyText(option)));
     if (option == EnumOptionsMinimap.OLDNORTH) {
       this.master.getWaypointManager().setOldNorth(this.options.oldNorth);
     }
   }
 
   
   public void method_25394(class_4587 matrixStack, int mouseX, int mouseY, float partialTicks) {
     drawMap(matrixStack);
     method_25420(matrixStack);
     method_25300(matrixStack, getFontRenderer(), this.screenTitle, getWidth() / 2, 20, 16777215);
     super.method_25394(matrixStack, mouseX, mouseY, partialTicks);
   }
 }

