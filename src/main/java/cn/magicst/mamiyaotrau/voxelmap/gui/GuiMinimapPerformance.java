 package cn.magicst.mamiyaotaru.voxelmap.gui;

 import cn.magicst.mamiyaotaru.voxelmap.MapSettingsManager;
 import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.EnumOptionsMinimap;
 import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.GuiButtonText;
 import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.GuiOptionButtonMinimap;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IVoxelMap;
 import cn.magicst.mamiyaotaru.voxelmap.util.I18nUtils;
 import net.minecraft.class_2561;
 import net.minecraft.class_2585;
 import net.minecraft.class_2588;
 import net.minecraft.class_364;
 import net.minecraft.class_4185;
 import net.minecraft.class_437;
 import net.minecraft.class_4587;
 
 public class GuiMinimapPerformance extends GuiScreenMinimap {
   private static final EnumOptionsMinimap[] relevantOptions = new EnumOptionsMinimap[] { EnumOptionsMinimap.LIGHTING, EnumOptionsMinimap.TERRAIN, EnumOptionsMinimap.WATERTRANSPARENCY, EnumOptionsMinimap.BLOCKTRANSPARENCY, EnumOptionsMinimap.BIOMES, EnumOptionsMinimap.FILTERING, EnumOptionsMinimap.CHUNKGRID, EnumOptionsMinimap.BIOMEOVERLAY, EnumOptionsMinimap.SLIMECHUNKS };
   private GuiButtonText worldSeedButton;
   private GuiOptionButtonMinimap slimeChunksButton;
   private final class_437 parentScreen;
   protected String screenTitle = "Details / Performance";
   private final MapSettingsManager options;
   IVoxelMap master;
   
   public GuiMinimapPerformance(class_437 par1GuiScreen, IVoxelMap master) {
     this.parentScreen = par1GuiScreen;
     this.options = master.getMapOptions();
     this.master = master;
   }
   
   private int getLeftBorder() {
     return getWidth() / 2 - 155;
   }
   
   public void method_25426() {
     this.screenTitle = I18nUtils.getString("options.minimap.detailsperformance", new Object[0]);
     (getMinecraft()).field_1774.method_1462(true);
     int leftBorder = getLeftBorder();
     int var2 = 0;
     
     for (EnumOptionsMinimap option : relevantOptions) {
       String text = this.options.getKeyText(option);
       if ((option == EnumOptionsMinimap.WATERTRANSPARENCY || option == EnumOptionsMinimap.BLOCKTRANSPARENCY || option == EnumOptionsMinimap.BIOMES) && !this.options.multicore && this.options.getOptionBooleanValue(option)) {
         text = "§c" + text;
       }
       
       GuiOptionButtonMinimap optionButton = new GuiOptionButtonMinimap(leftBorder + var2 % 2 * 160, getHeight() / 6 + 24 * (var2 >> 1), option, (class_2561)new class_2585(text), this::optionClicked);
       method_37063((class_364)optionButton);
       var2++;
       if (optionButton.returnEnumOptions().equals(EnumOptionsMinimap.SLIMECHUNKS)) {
         this.slimeChunksButton = optionButton;
         this.slimeChunksButton.field_22763 = (getMinecraft().method_1496() || !this.master.getWorldSeed().equals(""));
       } 
     } 
     
     String worldSeedDisplay = this.master.getWorldSeed();
     if (worldSeedDisplay.equals("")) {
       worldSeedDisplay = I18nUtils.getString("selectWorld.versionUnknown", new Object[0]);
     }
     
     String buttonText = I18nUtils.getString("options.minimap.worldseed", new Object[0]) + ": " + I18nUtils.getString("options.minimap.worldseed", new Object[0]);
     this.worldSeedButton = new GuiButtonText(getFontRenderer(), leftBorder + var2 % 2 * 160, getHeight() / 6 + 24 * (var2 >> 1), 150, 20, (class_2561)new class_2585(buttonText), button -> this.worldSeedButton.setEditing(true));
     this.worldSeedButton.setText(this.master.getWorldSeed());
     this.worldSeedButton.field_22763 = !getMinecraft().method_1496();
     method_37063((class_364)this.worldSeedButton);
     var2++;
     method_37063((class_364)new class_4185(getWidth() / 2 - 100, getHeight() / 6 + 168, 200, 20, (class_2561)new class_2588("gui.done"), button -> getMinecraft().method_1507(this.parentScreen)));
   }
 
   
   public void method_25432() {
     (getMinecraft()).field_1774.method_1462(false);
   }
   
   protected void optionClicked(class_4185 par1GuiButton) {
     EnumOptionsMinimap option = ((GuiOptionButtonMinimap)par1GuiButton).returnEnumOptions();
     this.options.setOptionValue(option);
     String perfBomb = "";
     if ((option == EnumOptionsMinimap.WATERTRANSPARENCY || option == EnumOptionsMinimap.BLOCKTRANSPARENCY || option == EnumOptionsMinimap.BIOMES) && !this.options.multicore && this.options.getOptionBooleanValue(option)) {
       perfBomb = "§c";
     }
     
     par1GuiButton.method_25355((class_2561)new class_2585(perfBomb + perfBomb));
   }
   
   public boolean method_25404(int keysm, int scancode, int b) {
     if (keysm == 258) {
       this.worldSeedButton.method_25404(keysm, scancode, b);
     }
     
     if ((keysm == 257 || keysm == 335) && this.worldSeedButton.isEditing()) {
       newSeed();
     }
     
     return super.method_25404(keysm, scancode, b);
   }
   
   public boolean method_25400(char character, int keycode) {
     boolean OK = super.method_25400(character, keycode);
     if (character == '\r' && this.worldSeedButton.isEditing()) {
       newSeed();
     }
     
     return OK;
   }
   
   private void newSeed() {
     String newSeed = this.worldSeedButton.getText();
     this.master.setWorldSeed(newSeed);
     String worldSeedDisplay = this.master.getWorldSeed();
     if (worldSeedDisplay.equals("")) {
       worldSeedDisplay = I18nUtils.getString("selectWorld.versionUnknown", new Object[0]);
     }
     
     String buttonText = I18nUtils.getString("options.minimap.worldseed", new Object[0]) + ": " + I18nUtils.getString("options.minimap.worldseed", new Object[0]);
     this.worldSeedButton.method_25355((class_2561)new class_2585(buttonText));
     this.worldSeedButton.setText(this.master.getWorldSeed());
     this.master.getMap().forceFullRender(true);
     this.slimeChunksButton.field_22763 = (getMinecraft().method_1496() || !this.master.getWorldSeed().equals(""));
   }
   
   public void method_25394(class_4587 matrixStack, int mouseX, int mouseY, float partialTicks) {
     drawMap(matrixStack);
     method_25420(matrixStack);
     method_25300(matrixStack, getFontRenderer(), this.screenTitle, getWidth() / 2, 20, 16777215);
     super.method_25394(matrixStack, mouseX, mouseY, partialTicks);
   }
   
   public void method_25393() {
     this.worldSeedButton.tick();
   }
 }