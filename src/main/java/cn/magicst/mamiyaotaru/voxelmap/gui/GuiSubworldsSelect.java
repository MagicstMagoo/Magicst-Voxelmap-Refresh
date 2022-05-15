 package cn.magicst.mamiyaotaru.voxelmap.gui;
 
 import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.GuiScreenMinimap;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IVoxelMap;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IWaypointManager;
 import cn.magicst.mamiyaotaru.voxelmap.util.I18nUtils;
 import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
 import java.text.Collator;
 import java.util.ArrayList;
 import net.minecraft.class_1297;
 import net.minecraft.class_2561;
 import net.minecraft.class_2585;
 import net.minecraft.class_2588;
 import net.minecraft.class_299;
 import net.minecraft.class_310;
 import net.minecraft.class_342;
 import net.minecraft.class_364;
 import net.minecraft.class_410;
 import net.minecraft.class_4185;
 import net.minecraft.class_437;
 import net.minecraft.class_4587;
 import net.minecraft.class_5348;
 import net.minecraft.class_5498;
 import net.minecraft.class_743;
 import net.minecraft.class_744;
 import net.minecraft.class_746;
 
 
 
 public class GuiSubworldsSelect
   extends GuiScreenMinimap
   implements BooleanConsumer
 {
   private class_2561 title;
   private class_2561 select;
   private boolean multiworld = false;
   private class_342 newNameField;
   private boolean newWorld = false;
   private float yaw;
   
   public GuiSubworldsSelect(class_437 parent, IVoxelMap master) {
     this.field_22787 = class_310.method_1551();
     this.parent = parent;
     this.thePlayer = (getMinecraft()).field_1724;
     this.camera = new class_746(getMinecraft(), (getMinecraft()).field_1687, getMinecraft().method_1562(), this.thePlayer.method_3143(), new class_299(), false, false);
     this.camera.field_3913 = (class_744)new class_743((getMinecraft()).field_1690);
     this.camera.method_5808(this.thePlayer.method_23317(), this.thePlayer.method_23318() - this.thePlayer.method_5678(), this.thePlayer.method_23321(), this.thePlayer.method_36454(), 0.0F);
     this.yaw = this.thePlayer.method_36454();
     this.thirdPersonViewOrig = (getMinecraft()).field_1690.method_31044();
     this.master = master;
     this.waypointManager = master.getWaypointManager();
   }
   private final class_5498 thirdPersonViewOrig; private String[] worlds; private final class_437 parent; class_746 thePlayer; class_746 camera; private final IVoxelMap master; private final IWaypointManager waypointManager;
   public void method_25426() {
     ArrayList<String> knownSubworldNames = new ArrayList<>(this.waypointManager.getKnownSubworldNames());
     if (!this.multiworld && !this.waypointManager.isMultiworld() && !getMinecraft().method_1589()) {
       class_410 confirmScreen = new class_410(this, (class_2561)new class_2588("worldmap.multiworld.isthismultiworld"), (class_2561)new class_2588("worldmap.multiworld.explanation"), (class_2561)new class_2588("gui.yes"), (class_2561)new class_2588("gui.no"));
       getMinecraft().method_1507((class_437)confirmScreen);
     } else {
       (getMinecraft()).field_1690.method_31043(class_5498.field_26664);
       getMinecraft().method_1504((class_1297)this.camera);
     } 
     
     this.title = (class_2561)new class_2588("worldmap.multiworld.title");
     this.select = (class_2561)new class_2588("worldmap.multiworld.select");
     method_37067();
     int centerX = this.field_22789 / 2;
     int buttonsPerRow = this.field_22789 / 150;
     if (buttonsPerRow == 0) {
       buttonsPerRow = 1;
     }
     
     int buttonWidth = this.field_22789 / buttonsPerRow - 5;
     int xSpacing = (this.field_22789 - buttonsPerRow * buttonWidth) / 2;
     class_4185 cancelBtn = new class_4185(centerX - 100, this.field_22790 - 30, 200, 20, (class_2561)new class_2588("gui.cancel"), button -> getMinecraft().method_1507(null));
     method_37063((class_364)cancelBtn);
     Collator collator = I18nUtils.getLocaleAwareCollator();
     knownSubworldNames.sort((name1, name2) -> -collator.compare(name1, name2));
     int numKnownSubworlds = knownSubworldNames.size();
     int completeRows = (int)Math.floor(((numKnownSubworlds + 1) / buttonsPerRow));
     int lastRowShiftBy = (int)(Math.ceil(((numKnownSubworlds + 1) / buttonsPerRow)) * buttonsPerRow - (numKnownSubworlds + 1));
     this.worlds = new String[numKnownSubworlds];
     class_4185[] selectButtons = new class_4185[numKnownSubworlds + 1];
     class_4185[] editButtons = new class_4185[numKnownSubworlds + 1];
     
     for (int t = 0; t < numKnownSubworlds; t++) {
       int shiftBy = 1;
       if (t / buttonsPerRow >= completeRows) {
         shiftBy = lastRowShiftBy + 1;
       }
       
       this.worlds[t] = knownSubworldNames.get(t);
       int tt = t;
       selectButtons[t] = new class_4185((buttonsPerRow - shiftBy - t % buttonsPerRow) * buttonWidth + xSpacing, this.field_22790 - 60 - t / buttonsPerRow * 21, buttonWidth - 32, 20, (class_2561)new class_2585(this.worlds[t]), button -> worldSelected(this.worlds[tt]));
       editButtons[t] = new class_4185((buttonsPerRow - shiftBy - t % buttonsPerRow) * buttonWidth + xSpacing + buttonWidth - 32, this.field_22790 - 60 - t / buttonsPerRow * 21, 30, 20, (class_2561)new class_2585("⚒"), button -> editWorld(this.worlds[tt]));
       method_37063((class_364)selectButtons[t]);
       method_37063((class_364)editButtons[t]);
     } 
     
     int numButtons = selectButtons.length - 1;
     if (!this.newWorld) {
       selectButtons[numButtons] = new class_4185((buttonsPerRow - 1 - lastRowShiftBy - numButtons % buttonsPerRow) * buttonWidth + xSpacing, this.field_22790 - 60 - numButtons / buttonsPerRow * 21, buttonWidth - 2, 20, (class_2561)new class_2585("< " + I18nUtils.getString("worldmap.multiworld.newname", new Object[0]) + " >"), button -> {
             this.newWorld = true;
             this.newNameField.method_1876(true);
           });
       method_37063((class_364)selectButtons[numButtons]);
     } 
     
     this.newNameField = new class_342(getFontRenderer(), (buttonsPerRow - 1 - lastRowShiftBy - numButtons % buttonsPerRow) * buttonWidth + xSpacing + 1, this.field_22790 - 60 - numButtons / buttonsPerRow * 21 + 1, buttonWidth - 4, 18, null);
   }
   
   public void accept(boolean par1) {
     if (!par1) {
       getMinecraft().method_1507(this.parent);
     } else {
       this.multiworld = true;
       getMinecraft().method_1507((class_437)this);
     } 
   }
 
   
   public boolean method_25402(double mouseX, double mouseY, int mouseButton) {
     if (this.newWorld) {
       this.newNameField.method_25402(mouseX, mouseY, mouseButton);
     }
     
     return super.method_25402(mouseX, mouseY, mouseButton);
   }
   
   public boolean method_25404(int keysm, int scancode, int b) {
     if (this.newNameField.method_25370()) {
       this.newNameField.method_25404(keysm, scancode, b);
       if ((keysm == 257 || keysm == 335) && this.newNameField.method_25370()) {
         String newName = this.newNameField.method_1882();
         if (newName != null && !newName.isEmpty()) {
           worldSelected(newName);
         }
       } 
     } 
     
     return super.method_25404(keysm, scancode, b);
   }
   
   public boolean method_25400(char typedChar, int keyCode) {
     if (this.newNameField.method_25370()) {
       this.newNameField.method_25400(typedChar, keyCode);
       if (keyCode == 28) {
         String newName = this.newNameField.method_1882();
         if (newName != null && !newName.isEmpty()) {
           worldSelected(newName);
         }
       } 
     } 
     
     return super.method_25400(typedChar, keyCode);
   }
   
   public void method_25393() {
     this.newNameField.method_1865();
     super.method_25393();
   }
   
   public void method_25394(class_4587 matrixStack, int mouseX, int mouseY, float partialTicks) {
     int titleStringWidth = getFontRenderer().method_27525((class_5348)this.title);
     titleStringWidth = Math.max(titleStringWidth, getFontRenderer().method_27525((class_5348)this.select));
     method_25294(matrixStack, this.field_22789 / 2 - titleStringWidth / 2 - 5, 0, this.field_22789 / 2 + titleStringWidth / 2 + 5, 27, -1073741824);
     method_27534(matrixStack, getFontRenderer(), this.title, this.field_22789 / 2, 5, 16777215);
     method_27534(matrixStack, getFontRenderer(), this.select, this.field_22789 / 2, 15, 16711680);
     this.camera.field_6004 = 0.0F;
     this.camera.method_36457(0.0F);
     this.camera.field_5982 = this.yaw;
     this.camera.method_36456(this.yaw);
     float var4 = 0.475F;
     this.camera.field_5971 = this.camera.field_6036 = this.thePlayer.method_23318();
     this.camera.field_6038 = this.camera.field_6014 = this.thePlayer.method_23317() - var4 * Math.sin(this.yaw / 180.0D * Math.PI);
     this.camera.field_5989 = this.camera.field_5969 = this.thePlayer.method_23321() + var4 * Math.cos(this.yaw / 180.0D * Math.PI);
     this.camera.method_23327(this.camera.field_6014, this.camera.field_6036, this.camera.field_5969);
     float var5 = 1.0F;
     this.yaw = (float)(this.yaw + var5 * (1.0D + 0.699999988079071D * Math.cos((this.yaw + 45.0F) / 45.0D * Math.PI)));
     super.method_25394(matrixStack, mouseX, mouseY, partialTicks);
     if (this.newWorld) {
       this.newNameField.method_25394(matrixStack, mouseX, mouseY, partialTicks);
     }
   }
 
 
   
   public void method_25432() {
     super.method_25432();
     (getMinecraft()).field_1690.method_31043(this.thirdPersonViewOrig);
     getMinecraft().method_1504((class_1297)this.thePlayer);
   }
   
   private void worldSelected(String selectedSubworldName) {
     this.waypointManager.setSubworldName(selectedSubworldName, false);
     getMinecraft().method_1507(this.parent);
   }
   
   private void editWorld(String subworldNameToEdit) {
     getMinecraft().method_1507((class_437)new GuiSubworldEdit((class_437)this, this.master, subworldNameToEdit));
   }
 }
