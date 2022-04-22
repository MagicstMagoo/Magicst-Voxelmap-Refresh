 package cn.magicst.mamiyaotaru.voxelmap.gui;

 import cn.magicst.mamiyaotaru.voxelmap.MapSettingsManager;
 import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.GuiScreenMinimap;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IVoxelMap;
 import cn.magicst.mamiyaotaru.voxelmap.util.I18nUtils;
 import net.minecraft.class_124;
 import net.minecraft.class_2561;
 import net.minecraft.class_2585;
 import net.minecraft.class_2588;
 import net.minecraft.class_304;
 import net.minecraft.class_364;
 import net.minecraft.class_3675;
 import net.minecraft.class_4185;
 import net.minecraft.class_437;
 import net.minecraft.class_4587;
 
 public class GuiMinimapControls extends GuiScreenMinimap {
   protected String screenTitle = "Controls"; private final class_437 parentScreen;
   private final MapSettingsManager options;
   public class_304 buttonId = null;
   
   public GuiMinimapControls(class_437 par1GuiScreen, IVoxelMap master) {
     this.parentScreen = par1GuiScreen;
     this.options = master.getMapOptions();
   }
   
   private int getLeftBorder() {
     return getWidth() / 2 - 155;
   }
   
   public void method_25426() {
     int left = getLeftBorder();
     
     for (int t = 0; t < this.options.keyBindings.length; t++) {
       int id = t;
       method_37063((class_364)new class_4185(left + t % 2 * 160, getHeight() / 6 + 24 * (t >> 1), 70, 20, this.options.getKeybindDisplayString(t), button -> controlButtonClicked(id)));
     } 
     
     method_37063((class_364)new class_4185(getWidth() / 2 - 100, getHeight() / 6 + 168, 200, 20, (class_2561)new class_2588("gui.done"), button -> getMinecraft().method_1507(this.parentScreen)));
     this.screenTitle = I18nUtils.getString("controls.minimap.title", new Object[0]);
   }
   
   protected void controlButtonClicked(int id) {
     for (int buttonListIndex = 0; buttonListIndex < this.options.keyBindings.length; buttonListIndex++) {
       ((class_4185)getButtonList().get(buttonListIndex)).method_25355(this.options.getKeybindDisplayString(buttonListIndex));
     }
     
     this.buttonId = this.options.keyBindings[id];
   }
   
   public boolean method_25402(double mouseX, double mouseY, int mouseButton) {
     if (this.buttonId != null) {
       this.options.setKeyBinding(this.buttonId, class_3675.class_307.field_1672.method_1447(mouseButton));
       this.buttonId = null;
       class_304.method_1426();
       return true;
     } 
     return super.method_25402(mouseX, mouseY, mouseButton);
   }
 
   
   public boolean method_25404(int keysm, int scancode, int b) {
     if (this.buttonId != null) {
       if (keysm == 256) {
         this.options.setKeyBinding(this.buttonId, class_3675.field_16237);
       } else {
         this.options.setKeyBinding(this.buttonId, class_3675.method_15985(keysm, scancode));
       } 
       
       this.buttonId = null;
       class_304.method_1426();
       return true;
     } 
     return super.method_25404(keysm, scancode, b);
   }
 
   
   public void method_25394(class_4587 matrixStack, int mouseX, int mouseY, float partialTicks) {
     drawMap(matrixStack);
     method_25420(matrixStack);
     method_25300(matrixStack, getFontRenderer(), this.screenTitle, getWidth() / 2, 20, 16777215);
     int leftBorder = getLeftBorder();
     
     for (int keyCounter = 0; keyCounter < this.options.keyBindings.length; keyCounter++) {
       boolean keycodeCollision = false;
       class_304 keyBinding = this.options.keyBindings[keyCounter];
       
       for (int compareKeyCounter = 0; compareKeyCounter < this.options.game.field_1690.field_1839.length; compareKeyCounter++) {
         if (compareKeyCounter < this.options.keyBindings.length) {
           class_304 compareBinding = this.options.keyBindings[compareKeyCounter];
           if (keyBinding != compareBinding && keyBinding.method_1435(compareBinding)) {
             keycodeCollision = true;
             
             break;
           } 
         } 
         if (compareKeyCounter < this.options.game.field_1690.field_1839.length) {
           class_304 compareBinding = this.options.game.field_1690.field_1839[compareKeyCounter];
           if (keyBinding != compareBinding && keyBinding.method_1435(compareBinding)) {
             keycodeCollision = true;
             
             break;
           } 
         } 
       } 
       if (this.buttonId == this.options.keyBindings[keyCounter]) {
         ((class_4185)getButtonList().get(keyCounter)).method_25355((class_2561)(new class_2585("> ")).method_10852((class_2561)(new class_2585("???")).method_27661().method_27692(class_124.field_1054)).method_27693(" <").method_27692(class_124.field_1054));
       } else if (keycodeCollision) {
         ((class_4185)getButtonList().get(keyCounter)).method_25355((class_2561)this.options.getKeybindDisplayString(keyCounter).method_27661().method_27692(class_124.field_1061));
       } else {
         ((class_4185)getButtonList().get(keyCounter)).method_25355(this.options.getKeybindDisplayString(keyCounter));
       } 
       
       method_25303(matrixStack, getFontRenderer(), this.options.getKeyBindingDescription(keyCounter), leftBorder + keyCounter % 2 * 160 + 70 + 6, getHeight() / 6 + 24 * (keyCounter >> 1) + 7, -1);
     } 
     
     method_25300(matrixStack, getFontRenderer(), I18nUtils.getString("controls.minimap.unbind1", new Object[0]), getWidth() / 2, getHeight() / 6 + 115, 16777215);
     method_25300(matrixStack, getFontRenderer(), I18nUtils.getString("controls.minimap.unbind2", new Object[0]), getWidth() / 2, getHeight() / 6 + 129, 16777215);
     super.method_25394(matrixStack, mouseX, mouseY, partialTicks);
   }
 }