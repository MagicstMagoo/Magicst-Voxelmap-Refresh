 package cn.magicst.mamiyaotaru.voxelmap.gui;
 
 import cn.magicst.mamiyaotaru.voxelmap.util.CustomMob;
 import cn.magicst.mamiyaotaru.voxelmap.util.CustomMobsManager;
 import cn.magicst.mamiyaotaru.voxelmap.util.EnumMobs;
 import cn.magicst.mamiyaotaru.voxelmap.util.GLShim;
 import cn.magicst.mamiyaotaru.voxelmap.util.GLUtils;
 import net.minecraft.class_2561;
 import net.minecraft.class_332;
 import net.minecraft.class_350;
 import net.minecraft.class_4587;
  
 public class MobItem
   extends class_350.class_351<GuiSlotMobs.MobItem>
 {
   private final GuiMobs parentGui;
   private final String id;
   private final String name;
   
   protected MobItem(GuiMobs mobsScreen, String id) {
     this.parentGui = mobsScreen;
     this.id = id;
     this.name = GuiSlotMobs.getTranslatedName(id);
   }
   
   public void method_25343(class_4587 matrixStack, int slotIndex, int slotYPos, int leftEdge, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean mouseOver, float partialTicks) {
     boolean isHostile = false;
     boolean isNeutral = false;
     boolean isEnabled = true;
     EnumMobs mob = EnumMobs.getMobByName(this.id);
     if (mob != null) {
       isHostile = mob.isHostile;
       isNeutral = mob.isNeutral;
       isEnabled = mob.enabled;
     } else {
       CustomMob customMob = CustomMobsManager.getCustomMobByType(this.id);
       if (customMob != null) {
         isHostile = customMob.isHostile;
         isNeutral = customMob.isNeutral;
         isEnabled = customMob.enabled;
       } 
     } 
     
     int red = isHostile ? 255 : 0;
     int green = isNeutral ? 255 : 0;
     int color = -16777216 + (red << 16) + (green << 8);
     class_332.method_25300(matrixStack, this.parentGui.getFontRenderer(), this.name, this.parentGui.getWidth() / 2, slotYPos + 3, color);
     byte padding = 3;
     if (mouseX >= leftEdge - padding && mouseY >= slotYPos && mouseX <= leftEdge + 215 + padding && mouseY <= slotYPos + GuiSlotMobs.access$000(GuiSlotMobs.this)) {
       class_2561 tooltip;
       if (mouseX >= leftEdge + 215 - 16 - padding && mouseX <= leftEdge + 215 + padding) {
         tooltip = isEnabled ? GuiSlotMobs.this.DISABLE : GuiSlotMobs.this.ENABLE;
       } else {
         tooltip = isEnabled ? GuiSlotMobs.this.ENABLED : GuiSlotMobs.this.DISABLED;
       } 
       
       GuiMobs.setTooltip(this.parentGui, tooltip);
     } 
     
     GLShim.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
     GLUtils.img2(isEnabled ? GuiSlotMobs.this.visibleIconIdentifier : GuiSlotMobs.this.invisibleIconIdentifier);
     class_332.method_25291(matrixStack, leftEdge + 198, slotYPos - 2, GuiSlotMobs.this.method_25305(), 0.0F, 0.0F, 18, 18, 18, 18);
   }
   
   public boolean method_25402(double mouseX, double mouseY, int mouseEvent) {
     GuiSlotMobs.this.setSelected(this);
     int leftEdge = this.parentGui.getWidth() / 2 - 92 - 16;
     byte padding = 3;
     int width = 215;
     if (mouseX >= (leftEdge + width - 16 - padding) && mouseX <= (leftEdge + width + padding)) {
       this.parentGui.toggleMobVisibility();
     } else if (GuiSlotMobs.this.doubleclick) {
       this.parentGui.toggleMobVisibility();
     } 
     
     return true;
   }
 }

