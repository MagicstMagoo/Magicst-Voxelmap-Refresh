package cn.magicst.mamiyaotaru.voxelmap.gui;

import cn.magicst.mamiyaotaru.voxelmap.textures.Sprite;
import cn.magicst.mamiyaotaru.voxelmap.textures.TextureAtlas;
import cn.magicst.mamiyaotaru.voxelmap.util.GLShim;
import cn.magicst.mamiyaotaru.voxelmap.util.GLUtils;
import cn.magicst.mamiyaotaru.voxelmap.util.Waypoint;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_350;
import net.minecraft.class_4587;
import net.minecraft.class_5250;

public class WaypointItem extends class_350.class_351<GuiSlotWaypoints.WaypointItem> implements Comparable<GuiSlotWaypoints.WaypointItem> {
  private final GuiWaypoints parentGui;

  private final Waypoint waypoint;

  protected WaypointItem(GuiWaypoints waypointScreen, Waypoint waypoint) {
    this.parentGui = waypointScreen;
    this.waypoint = waypoint;
  }

  public void method_25343(class_4587 matrixStack, int slotIndex, int slotYPos, int leftEdge, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean mouseOver, float partialTicks) {
    class_332.method_25300(matrixStack, this.parentGui.getFontRenderer(), this.waypoint.name, this.parentGui.getWidth() / 2, slotYPos + 3, this.waypoint.getUnifiedColor());
    byte padding = 3;
    if (mouseX >= leftEdge - padding && mouseY >= slotYPos && mouseX <= leftEdge + 215 + padding && mouseY <= slotYPos + entryHeight) {
      class_5250 class_5250;
      if (mouseX >= leftEdge + 215 - 16 - padding && mouseX <= leftEdge + 215 + padding) {
        class_2561 tooltip = this.waypoint.enabled ? GuiSlotWaypoints.this.DISABLE : GuiSlotWaypoints.this.ENABLE;
      } else {
        String tooltipText = "X: " + this.waypoint.getX() + " Z: " + this.waypoint.getZ();
        if (this.waypoint.getY() > 0)
          tooltipText = tooltipText + " Y: " + tooltipText;
        class_5250 = class_2561.method_43470(tooltipText);
      }
      if (mouseX >= GuiSlotWaypoints.access$000(GuiSlotWaypoints.this) && mouseX <= GuiSlotWaypoints.access$100(GuiSlotWaypoints.this) && mouseY >= GuiSlotWaypoints.access$200(GuiSlotWaypoints.this) && mouseY <= GuiSlotWaypoints.access$300(GuiSlotWaypoints.this))
        GuiWaypoints.setTooltip(GuiSlotWaypoints.this.parentGui, (class_2561)class_5250);
    }
    GLShim.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GLUtils.img2(this.waypoint.enabled ? GuiSlotWaypoints.this.visibleIconIdentifier : GuiSlotWaypoints.this.invisibleIconIdentifier);
    class_332.method_25291(matrixStack, leftEdge + 198, slotYPos - 2, GuiSlotWaypoints.this.method_25305(), 0.0F, 0.0F, 18, 18, 18, 18);
    if (this.waypoint == this.parentGui.highlightedWaypoint) {
      int x = leftEdge + 199;
      int y = slotYPos - 1;
      GLShim.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);
      TextureAtlas textureAtlas = this.parentGui.waypointManager.getTextureAtlas();
      GLUtils.disp(textureAtlas.method_4624());
      Sprite icon = textureAtlas.getAtlasSprite("voxelmap:images/waypoints/target.png");
      GuiSlotWaypoints.this.drawTexturedModalRect(x, y, icon, 16, 16);
    }
  }

  public boolean method_25402(double mouseX, double mouseY, int mouseButton) {
    GuiSlotWaypoints.this.setSelected(this);
    int leftEdge = this.parentGui.getWidth() / 2 - 92 - 16;
    byte padding = 3;
    int width = 215;
    if (mouseX >= (leftEdge + width - 16 - padding) && mouseX <= (leftEdge + width + padding)) {
      if (GuiSlotWaypoints.this.doubleclick)
        this.parentGui.setHighlightedWaypoint();
      this.parentGui.toggleWaypointVisibility();
    } else if (GuiSlotWaypoints.this.doubleclick) {
      this.parentGui.editWaypoint(this.parentGui.selectedWaypoint);
    }
    return true;
  }

  public int compareTo(WaypointItem arg0) {
    return this.waypoint.compareTo(arg0.waypoint);
  }
}
