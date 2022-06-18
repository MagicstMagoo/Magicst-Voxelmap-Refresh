package cn.magicst.mamiyaotaru.voxelmap.gui;

import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.GuiSlotMinimap;
import cn.magicst.mamiyaotaru.voxelmap.textures.Sprite;
import cn.magicst.mamiyaotaru.voxelmap.textures.TextureAtlas;
import cn.magicst.mamiyaotaru.voxelmap.util.GLShim;
import cn.magicst.mamiyaotaru.voxelmap.util.GLUtils;
import cn.magicst.mamiyaotaru.voxelmap.util.I18nUtils;
import cn.magicst.mamiyaotaru.voxelmap.util.TextUtils;
import cn.magicst.mamiyaotaru.voxelmap.util.Waypoint;
import java.awt.Color;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.class_2561;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_293;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_333;
import net.minecraft.class_350;
import net.minecraft.class_4587;
import net.minecraft.class_5250;

class GuiSlotWaypoints extends GuiSlotMinimap {
  private final ArrayList<WaypointItem> waypoints;

  private ArrayList<?> waypointsFiltered;

  final GuiWaypoints parentGui;

  private String filterString = "";

  final class_2561 ENABLE = (class_2561)class_2561.method_43471("minimap.waypoints.enable");

  final class_2561 DISABLE = (class_2561)class_2561.method_43471("minimap.waypoints.disable");

  final class_2960 visibleIconIdentifier = new class_2960("textures/mob_effect/night_vision.png");

  final class_2960 invisibleIconIdentifier = new class_2960("textures/mob_effect/blindness.png");

  public GuiSlotWaypoints(GuiWaypoints par1GuiWaypoints) {
    super(par1GuiWaypoints.options.game, par1GuiWaypoints.getWidth(), par1GuiWaypoints.getHeight(), 54, par1GuiWaypoints.getHeight() - 90 + 4, 18);
    this.parentGui = par1GuiWaypoints;
    this.waypoints = new ArrayList<>();
    for (Waypoint pt : this.parentGui.waypointManager.getWaypoints()) {
      if (pt.inWorld && pt.inDimension)
        this.waypoints.add(new WaypointItem(this.parentGui, pt));
    }
    this.waypointsFiltered = new ArrayList(this.waypoints);
    this.waypointsFiltered.forEach(x$0 -> method_25321((class_350.class_351)x$0));
  }

  public void setSelected(WaypointItem item) {
    method_25313(item);
    if (method_25334() instanceof WaypointItem)
      class_333.field_2054.method_19788(class_2561.method_43469("narrator.select", new Object[] { ((WaypointItem)method_25334()).waypoint.name }).getString());
    this.parentGui.setSelectedWaypoint(item.waypoint);
  }

  public boolean method_25402(double mouseX, double mouseY, int mouseButton) {
    return super.method_25402(mouseX, mouseY, mouseButton);
  }

  protected boolean method_25332(int par1) {
    return ((WaypointItem)this.waypointsFiltered.get(par1)).waypoint.equals(this.parentGui.selectedWaypoint);
  }

  protected int method_25317() {
    return method_25340() * this.field_22741;
  }

  public void method_25325(class_4587 matrixStack) {
    this.parentGui.method_25420(matrixStack);
  }

  public void drawTexturedModalRect(int xCoord, int yCoord, Sprite textureSprite, int widthIn, int heightIn) {
    class_289 tessellator = class_289.method_1348();
    class_287 vertexbuffer = tessellator.method_1349();
    vertexbuffer.method_1328(class_293.class_5596.field_27382, class_290.field_1585);
    vertexbuffer.method_22912(xCoord, (yCoord + heightIn), 1.0D).method_22913(textureSprite.getMinU(), textureSprite.getMaxV()).method_1344();
    vertexbuffer.method_22912((xCoord + widthIn), (yCoord + heightIn), 1.0D).method_22913(textureSprite.getMaxU(), textureSprite.getMaxV()).method_1344();
    vertexbuffer.method_22912((xCoord + widthIn), yCoord, 1.0D).method_22913(textureSprite.getMaxU(), textureSprite.getMinV()).method_1344();
    vertexbuffer.method_22912(xCoord, yCoord, 1.0D).method_22913(textureSprite.getMinU(), textureSprite.getMinV()).method_1344();
    tessellator.method_1350();
  }

  protected void sortBy(int sortKey, boolean ascending) {
    int order = ascending ? 1 : -1;
    if (sortKey == 1) {
      ArrayList<?> masterWaypointsList = this.parentGui.waypointManager.getWaypoints();
      this.waypoints.sort((waypointEntry1, waypointEntry2) -> Double.compare(masterWaypointsList.indexOf(waypointEntry1.waypoint), masterWaypointsList.indexOf(waypointEntry2.waypoint)) * order);
    } else if (sortKey == 3) {
      if (ascending) {
        Collections.sort(this.waypoints);
      } else {
        this.waypoints.sort(Collections.reverseOrder());
      }
    } else if (sortKey == 2) {
      Collator collator = I18nUtils.getLocaleAwareCollator();
      this.waypoints.sort((waypointEntry1, waypointEntry2) -> collator.compare(waypointEntry1.waypoint.name, waypointEntry2.waypoint.name) * order);
    } else if (sortKey == 4) {
      this.waypoints.sort((waypointEntry1, waypointEntry2) -> {
        Waypoint waypoint1 = waypointEntry1.waypoint;
        Waypoint waypoint2 = waypointEntry2.waypoint;
        float hue1 = Color.RGBtoHSB((int)(waypoint1.red * 255.0F), (int)(waypoint1.green * 255.0F), (int)(waypoint1.blue * 255.0F), null)[0];
        float hue2 = Color.RGBtoHSB((int)(waypoint2.red * 255.0F), (int)(waypoint2.green * 255.0F), (int)(waypoint2.blue * 255.0F), null)[0];
        return Double.compare(hue1, hue2) * order;
      });
    }
    updateFilter(this.filterString);
  }

  protected void updateFilter(String filterString) {
    method_25339();
    this.filterString = filterString;
    this.waypointsFiltered = new ArrayList(this.waypoints);
    Iterator<?> iterator = this.waypointsFiltered.iterator();
    while (iterator.hasNext()) {
      Waypoint waypoint = ((WaypointItem)iterator.next()).waypoint;
      if (!TextUtils.scrubCodes(waypoint.name).toLowerCase().contains(filterString)) {
        if (waypoint == this.parentGui.selectedWaypoint)
          this.parentGui.setSelectedWaypoint((Waypoint)null);
        iterator.remove();
      }
    }
    this.waypointsFiltered.forEach(x$0 -> method_25321((class_350.class_351)x$0));
  }

  public class WaypointItem extends class_350.class_351<WaypointItem> implements Comparable<WaypointItem> {
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
        if (mouseX >= GuiSlotWaypoints.this.field_19088 && mouseX <= GuiSlotWaypoints.this.field_19087 && mouseY >= GuiSlotWaypoints.this.field_19085 && mouseY <= GuiSlotWaypoints.this.field_19086)
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
}
