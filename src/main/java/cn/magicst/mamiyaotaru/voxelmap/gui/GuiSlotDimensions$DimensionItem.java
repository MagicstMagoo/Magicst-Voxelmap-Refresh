 package cn.magicst.mamiyaotaru.voxelmap.gui;
 
 import cn.magicst.mamiyaotaru.voxelmap.util.DimensionContainer;
 import cn.magicst.mamiyaotaru.voxelmap.util.GLShim;
 import cn.magicst.mamiyaotaru.voxelmap.util.GLUtils;
 import net.minecraft.class_2561;
 import net.minecraft.class_332;
 import net.minecraft.class_350;
 import net.minecraft.class_4587; 
 
 public class DimensionItem
   extends class_350.class_351<GuiSlotDimensions.DimensionItem>
 {
   private final GuiAddWaypoint parentGui;
   private final DimensionContainer dim;
   
   protected DimensionItem(GuiAddWaypoint waypointScreen, DimensionContainer dim) {
     this.parentGui = waypointScreen;
     this.dim = dim;
   }
   
   public void method_25343(class_4587 matrixStack, int slotIndex, int slotYPos, int leftEdge, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean mouseOver, float partialTicks) {
     class_332.method_25300(matrixStack, this.parentGui.getFontRenderer(), this.dim.getDisplayName(), this.parentGui.getWidth() / 2 + GuiSlotDimensions.access$000(GuiSlotDimensions.this) / 2, slotYPos + 3, 16777215);
     byte padding = 4;
     byte iconWidth = 16;
     leftEdge = this.parentGui.getWidth() / 2;
     int width = GuiSlotDimensions.access$100(GuiSlotDimensions.this);
     if (mouseX >= leftEdge + padding && mouseY >= slotYPos && mouseX <= leftEdge + width + padding && mouseY <= slotYPos + GuiSlotDimensions.access$200(GuiSlotDimensions.this)) {
       class_2561 tooltip;
       if (this.parentGui.popupOpen() && mouseX >= leftEdge + width - iconWidth - padding && mouseX <= leftEdge + width) {
         tooltip = this.parentGui.waypoint.dimensions.contains(this.dim) ? GuiSlotDimensions.this.APPLIES : GuiSlotDimensions.this.NOT_APPLIES;
       } else {
         tooltip = null;
       } 
       
       GuiAddWaypoint.setTooltip(this.parentGui, tooltip);
     } 
     
     GLShim.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
     GLUtils.img2("textures/gui/container/beacon.png");
     int xOffset = this.parentGui.waypoint.dimensions.contains(this.dim) ? 91 : 113;
     int yOffset = 222;
     this.parentGui.method_25302(matrixStack, leftEdge + width - iconWidth, slotYPos - 2, xOffset, yOffset, 16, 16);
   }
   
   public boolean method_25402(double mouseX, double mouseY, int mouseButton) {
     GuiSlotDimensions.this.setSelected(this);
     int leftEdge = this.parentGui.getWidth() / 2;
     byte padding = 4;
     byte iconWidth = 16;
     int width = GuiSlotDimensions.access$300(GuiSlotDimensions.this);
     if (mouseX >= (leftEdge + width - iconWidth - padding) && mouseX <= (leftEdge + width)) {
       this.parentGui.toggleDimensionSelected();
     } else if (GuiSlotDimensions.this.doubleclick) {
       this.parentGui.toggleDimensionSelected();
     } 
     
     return true;
   }
 }


/* Location:              D:\系统文件夹\桌面\voxelmap-1.19-1.11.3.jar!\cn.magicst.mamiyaotaru.voxelmap\gui\GuiSlotDimensions$DimensionItem.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */