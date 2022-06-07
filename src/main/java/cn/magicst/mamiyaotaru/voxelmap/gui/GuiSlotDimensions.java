 package cn.magicst.mamiyaotaru.voxelmap.gui;
 
 import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.GuiSlotMinimap;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IDimensionManager;
 import cn.magicst.mamiyaotaru.voxelmap.util.DimensionContainer;
 import cn.magicst.mamiyaotaru.voxelmap.util.GLShim;
 import cn.magicst.mamiyaotaru.voxelmap.util.GLUtils;
 import java.util.ArrayList;
 import net.minecraft.class_2561;
 import net.minecraft.class_310;
 import net.minecraft.class_332;
 import net.minecraft.class_333;
 import net.minecraft.class_350;
 import net.minecraft.class_4587;
 
 class GuiSlotDimensions
   extends GuiSlotMinimap {
   final GuiAddWaypoint parentGui;
   private final ArrayList<DimensionItem> dimensions;
   final class_2561 APPLIES = (class_2561)class_2561.method_43471("minimap.waypoints.dimension.applies");
   final class_2561 NOT_APPLIES = (class_2561)class_2561.method_43471("minimap.waypoints.dimension.notapplies");
   
   public GuiSlotDimensions(GuiAddWaypoint par1GuiWaypoints) {
     super(class_310.method_1551(), 101, par1GuiWaypoints.getHeight(), par1GuiWaypoints.getHeight() / 6 + 82 + 6, par1GuiWaypoints.getHeight() / 6 + 164 + 3, 18);
     this.parentGui = par1GuiWaypoints;
     setSlotWidth(88);
     method_25333(this.parentGui.getWidth() / 2);
     method_29344(false);
     setShowTopBottomBG(false);
     setShowSlotBG(false);
     IDimensionManager dimensionManager = this.parentGui.master.getDimensionManager();
     this.dimensions = new ArrayList<>();
     DimensionItem first = null;
     
     for (DimensionContainer dim : dimensionManager.getDimensions()) {
       DimensionItem item = new DimensionItem(this.parentGui, dim);
       this.dimensions.add(item);
       if (dim.equals(this.parentGui.waypoint.dimensions.first())) {
         first = item;
       }
     } 
     
     this.dimensions.forEach(x$0 -> rec$.method_25321(x$0));
     if (first != null) {
       method_25328(first);
     }
   }
 
   
   public void setSelected(DimensionItem item) {
     method_25313(item);
     if (method_25334() instanceof DimensionItem) {
       class_333.field_2054.method_19788(class_2561.method_43469("narrator.select", new Object[] { ((DimensionItem)method_25334()).dim.name }).getString());
     }
     
     this.parentGui.setSelectedDimension(item.dim);
   }
   
   protected boolean method_25332(int par1) {
     return ((DimensionItem)this.dimensions.get(par1)).dim.equals(this.parentGui.selectedDimension);
   }
   
   public void method_25325(class_4587 matrixStack) {}
   
   public class DimensionItem
     extends class_350.class_351<DimensionItem> {
     private final GuiAddWaypoint parentGui;
     private final DimensionContainer dim;
     
     protected DimensionItem(GuiAddWaypoint waypointScreen, DimensionContainer dim) {
       this.parentGui = waypointScreen;
       this.dim = dim;
     }
     
     public void method_25343(class_4587 matrixStack, int slotIndex, int slotYPos, int leftEdge, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean mouseOver, float partialTicks) {
       class_332.method_25300(matrixStack, this.parentGui.getFontRenderer(), this.dim.getDisplayName(), this.parentGui.getWidth() / 2 + GuiSlotDimensions.this.slotWidth / 2, slotYPos + 3, 16777215);
       byte padding = 4;
       byte iconWidth = 16;
       leftEdge = this.parentGui.getWidth() / 2;
       int width = GuiSlotDimensions.this.slotWidth;
       if (mouseX >= leftEdge + padding && mouseY >= slotYPos && mouseX <= leftEdge + width + padding && mouseY <= slotYPos + GuiSlotDimensions.this.field_22741) {
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
       int width = GuiSlotDimensions.this.slotWidth;
       if (mouseX >= (leftEdge + width - iconWidth - padding) && mouseX <= (leftEdge + width)) {
         this.parentGui.toggleDimensionSelected();
       } else if (GuiSlotDimensions.this.doubleclick) {
         this.parentGui.toggleDimensionSelected();
       } 
       
       return true;
     }
   }
 }

