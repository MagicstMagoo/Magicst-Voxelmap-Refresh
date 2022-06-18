package cn.magicst.mamiyaotaru.voxelmap.gui;

import cn.magicst.mamiyaotaru.voxelmap.MapSettingsManager;
import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.GuiScreenMinimap;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.AbstractVoxelMap;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.IVoxelMap;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.IWaypointManager;
import cn.magicst.mamiyaotaru.voxelmap.util.CommandUtils;
import cn.magicst.mamiyaotaru.voxelmap.util.DimensionContainer;
import cn.magicst.mamiyaotaru.voxelmap.util.GameVariableAccessShim;
import cn.magicst.mamiyaotaru.voxelmap.util.I18nUtils;
import cn.magicst.mamiyaotaru.voxelmap.util.Waypoint;
import java.util.Random;
import java.util.TreeSet;
import net.minecraft.class_1937;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_342;
import net.minecraft.class_364;
import net.minecraft.class_410;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import net.minecraft.class_5250;

public class GuiWaypoints extends GuiScreenMinimap implements IGuiWaypoints {
  private final class_437 parentScreen;

  private final IVoxelMap master;

  protected final MapSettingsManager options;

  protected final IWaypointManager waypointManager;

  protected class_2561 screenTitle;

  private GuiSlotWaypoints waypointList;

  private class_4185 buttonEdit;

  private boolean editClicked = false;

  private class_4185 buttonDelete;

  private boolean deleteClicked = false;

  private class_4185 buttonHighlight;

  private class_4185 buttonShare;

  private class_4185 buttonTeleport;

  private class_4185 buttonSortName;

  private class_4185 buttonSortCreated;

  private class_4185 buttonSortDistance;

  private class_4185 buttonSortColor;

  protected class_342 filter;

  private boolean addClicked = false;

  private class_2561 tooltip = null;

  protected Waypoint selectedWaypoint = null;

  protected Waypoint highlightedWaypoint;

  protected Waypoint newWaypoint = null;

  private final Random generator = new Random();

  private boolean changedSort = false;

  public GuiWaypoints(class_437 parentScreen, IVoxelMap master) {
    this.master = master;
    this.parentScreen = parentScreen;
    this.options = master.getMapOptions();
    this.waypointManager = master.getWaypointManager();
    this.highlightedWaypoint = this.waypointManager.getHighlightedWaypoint();
  }

  public void method_25393() {
    this.filter.method_1865();
  }

  public void method_25426() {
    this.screenTitle = (class_2561)class_2561.method_43471("minimap.waypoints.title");
    (getMinecraft()).field_1774.method_1462(true);
    this.waypointList = new GuiSlotWaypoints(this);
    method_37063((class_364)(this.buttonSortName = new class_4185(getWidth() / 2 - 154, 34, 77, 20, (class_2561)class_2561.method_43471("minimap.waypoints.sortbyname"), button -> sortClicked(2))));
    method_37063((class_364)(this.buttonSortDistance = new class_4185(getWidth() / 2 - 77, 34, 77, 20, (class_2561)class_2561.method_43471("minimap.waypoints.sortbydistance"), button -> sortClicked(3))));
    method_37063((class_364)(this.buttonSortCreated = new class_4185(getWidth() / 2, 34, 77, 20, (class_2561)class_2561.method_43471("minimap.waypoints.sortbycreated"), button -> sortClicked(1))));
    method_37063((class_364)(this.buttonSortColor = new class_4185(getWidth() / 2 + 77, 34, 77, 20, (class_2561)class_2561.method_43471("minimap.waypoints.sortbycolor"), button -> sortClicked(4))));
    int filterStringWidth = getFontRenderer().method_1727(I18nUtils.getString("minimap.waypoints.filter", new Object[0]) + ":");
    this.filter = new class_342(getFontRenderer(), getWidth() / 2 - 153 + filterStringWidth + 5, getHeight() - 80, 305 - filterStringWidth - 5, 20, (class_2561)null);
    this.filter.method_1880(35);
    method_37063((class_364)this.filter);
    method_37063((class_364)(this.buttonEdit = new class_4185(getWidth() / 2 - 154, getHeight() - 52, 74, 20, (class_2561)class_2561.method_43471("selectServer.edit"), button -> editWaypoint(this.selectedWaypoint))));
    method_37063((class_364)(this.buttonDelete = new class_4185(getWidth() / 2 - 76, getHeight() - 52, 74, 20, (class_2561)class_2561.method_43471("selectServer.delete"), button -> deleteClicked())));
    method_37063((class_364)(this.buttonHighlight = new class_4185(getWidth() / 2 + 2, getHeight() - 52, 74, 20, (class_2561)class_2561.method_43471("minimap.waypoints.highlight"), button -> setHighlightedWaypoint())));
    method_37063((class_364)(this.buttonTeleport = new class_4185(getWidth() / 2 + 80, getHeight() - 52, 74, 20, (class_2561)class_2561.method_43471("minimap.waypoints.teleportto"), button -> teleportClicked())));
    method_37063((class_364)(this.buttonShare = new class_4185(getWidth() / 2 - 154, getHeight() - 28, 74, 20, (class_2561)class_2561.method_43471("minimap.waypoints.share"), button -> CommandUtils.sendWaypoint(this.selectedWaypoint))));
    method_37063((class_364)new class_4185(getWidth() / 2 - 76, getHeight() - 28, 74, 20, (class_2561)class_2561.method_43471("minimap.waypoints.newwaypoint"), button -> addWaypoint()));
    method_37063((class_364)new class_4185(getWidth() / 2 + 2, getHeight() - 28, 74, 20, (class_2561)class_2561.method_43471("menu.options"), button -> getMinecraft().method_1507((class_437)new GuiWaypointsOptions((class_437)this, this.options))));
    method_37063((class_364)new class_4185(getWidth() / 2 + 80, getHeight() - 28, 74, 20, (class_2561)class_2561.method_43471("gui.done"), button -> getMinecraft().method_1507(this.parentScreen)));
    method_25395((class_364)this.filter);
    this.filter.method_1876(true);
    boolean isSomethingSelected = (this.selectedWaypoint != null);
    this.buttonEdit.field_22763 = isSomethingSelected;
    this.buttonDelete.field_22763 = isSomethingSelected;
    this.buttonHighlight.field_22763 = isSomethingSelected;
    this.buttonShare.field_22763 = isSomethingSelected;
    this.buttonTeleport.field_22763 = (isSomethingSelected && canTeleport());
    sort();
  }

  private void sort() {
    int sortKey = Math.abs(this.options.sort);
    boolean ascending = (this.options.sort > 0);
    this.waypointList.sortBy(sortKey, ascending);
    String arrow = ascending ? ": ";
    if (sortKey == 2) {
      this.buttonSortName.method_25355((class_2561)class_2561.method_43470(arrow + " " + arrow + " " + I18nUtils.getString("minimap.waypoints.sortbyname", new Object[0])));
    } else {
      this.buttonSortName.method_25355((class_2561)class_2561.method_43471("minimap.waypoints.sortbyname"));
    }
    if (sortKey == 3) {
      this.buttonSortDistance.method_25355((class_2561)class_2561.method_43470(arrow + " " + arrow + " " + I18nUtils.getString("minimap.waypoints.sortbydistance", new Object[0])));
    } else {
      this.buttonSortDistance.method_25355((class_2561)class_2561.method_43471("minimap.waypoints.sortbydistance"));
    }
    if (sortKey == 1) {
      this.buttonSortCreated.method_25355((class_2561)class_2561.method_43470(arrow + " " + arrow + " " + I18nUtils.getString("minimap.waypoints.sortbycreated", new Object[0])));
    } else {
      this.buttonSortCreated.method_25355((class_2561)class_2561.method_43471("minimap.waypoints.sortbycreated"));
    }
    if (sortKey == 4) {
      this.buttonSortColor.method_25355((class_2561)class_2561.method_43470(arrow + " " + arrow + " " + I18nUtils.getString("minimap.waypoints.sortbycolor", new Object[0])));
    } else {
      this.buttonSortColor.method_25355((class_2561)class_2561.method_43471("minimap.waypoints.sortbycolor"));
    }
  }

  private void deleteClicked() {
    String var2 = this.selectedWaypoint.name;
    if (var2 != null) {
      this.deleteClicked = true;
      class_5250 class_52501 = class_2561.method_43471("minimap.waypoints.deleteconfirm");
      class_5250 class_52502 = class_2561.method_43469("selectServer.deleteWarning", new Object[] { var2 });
      class_5250 class_52503 = class_2561.method_43471("selectServer.deleteButton");
      class_5250 class_52504 = class_2561.method_43471("gui.cancel");
      class_410 confirmScreen = new class_410(this, (class_2561)class_52501, (class_2561)class_52502, (class_2561)class_52503, (class_2561)class_52504);
      getMinecraft().method_1507((class_437)confirmScreen);
    }
  }

  private void teleportClicked() {
    boolean mp = !this.field_22787.method_1496();
    int y = (this.selectedWaypoint.getY() > (class_310.method_1551()).field_1687.method_31607()) ? this.selectedWaypoint.getY() : (!this.options.game.field_1724.field_6002.method_8597().comp_643() ? (class_310.method_1551()).field_1687.method_31600() : 64);
    this.options.game.field_1724.method_44099("tp " + this.options.game.field_1724.method_5477().getString() + " " + this.selectedWaypoint.getX() + " " + y + " " + this.selectedWaypoint.getZ());
    if (mp)
      this.options.game.field_1724.method_44099("tppos " + this.selectedWaypoint.getX() + " " + y + " " + this.selectedWaypoint.getZ());
    getMinecraft().method_1507(null);
  }

  protected void sortClicked(int id) {
    this.options.setSort(id);
    this.changedSort = true;
    sort();
  }

  public boolean method_25404(int keysm, int scancode, int b) {
    boolean OK = super.method_25404(keysm, scancode, b);
    if (this.filter.method_25370())
      this.waypointList.updateFilter(this.filter.method_1882().toLowerCase());
    return OK;
  }

  public boolean method_25400(char character, int keycode) {
    boolean OK = super.method_25400(character, keycode);
    if (this.filter.method_25370())
      this.waypointList.updateFilter(this.filter.method_1882().toLowerCase());
    return OK;
  }

  public boolean method_25402(double mouseX, double mouseY, int mouseButton) {
    this.waypointList.method_25402(mouseX, mouseY, mouseButton);
    return super.method_25402(mouseX, mouseY, mouseButton);
  }

  public boolean method_25406(double mouseX, double mouseY, int mouseButton) {
    this.waypointList.method_25406(mouseX, mouseY, mouseButton);
    return super.method_25406(mouseX, mouseY, mouseButton);
  }

  public boolean method_25403(double mouseX, double mouseY, int mouseEvent, double deltaX, double deltaY) {
    return this.waypointList.method_25403(mouseX, mouseY, mouseEvent, deltaX, deltaY);
  }

  public boolean method_25401(double mouseX, double mouseY, double amount) {
    return this.waypointList.method_25401(mouseX, mouseY, amount);
  }

  public boolean isEditing() {
    return this.editClicked;
  }

  public void accept(boolean par1) {
    if (this.deleteClicked) {
      this.deleteClicked = false;
      if (par1) {
        this.waypointManager.deleteWaypoint(this.selectedWaypoint);
        this.selectedWaypoint = null;
      }
      getMinecraft().method_1507((class_437)this);
    }
    if (this.editClicked) {
      this.editClicked = false;
      if (par1)
        this.waypointManager.saveWaypoints();
      getMinecraft().method_1507((class_437)this);
    }
    if (this.addClicked) {
      this.addClicked = false;
      if (par1) {
        this.waypointManager.addWaypoint(this.newWaypoint);
        setSelectedWaypoint(this.newWaypoint);
      }
      getMinecraft().method_1507((class_437)this);
    }
  }

  protected void setSelectedWaypoint(Waypoint waypoint) {
    this.selectedWaypoint = waypoint;
    boolean isSomethingSelected = (this.selectedWaypoint != null);
    this.buttonEdit.field_22763 = isSomethingSelected;
    this.buttonDelete.field_22763 = isSomethingSelected;
    this.buttonHighlight.field_22763 = isSomethingSelected;
    this.buttonHighlight.method_25355((class_2561)class_2561.method_43471((isSomethingSelected && this.selectedWaypoint == this.highlightedWaypoint) ? "minimap.waypoints.removehighlight" : "minimap.waypoints.highlight"));
    this.buttonShare.field_22763 = isSomethingSelected;
    this.buttonTeleport.field_22763 = (isSomethingSelected && canTeleport());
  }

  protected void setHighlightedWaypoint() {
    this.waypointManager.setHighlightedWaypoint(this.selectedWaypoint, true);
    this.highlightedWaypoint = this.waypointManager.getHighlightedWaypoint();
    boolean isSomethingSelected = (this.selectedWaypoint != null);
    this.buttonHighlight.method_25355((class_2561)class_2561.method_43471((isSomethingSelected && this.selectedWaypoint == this.highlightedWaypoint) ? "minimap.waypoints.removehighlight" : "minimap.waypoints.highlight"));
  }

  protected void editWaypoint(Waypoint waypoint) {
    this.editClicked = true;
    getMinecraft().method_1507((class_437)new GuiAddWaypoint(this, this.master, waypoint, true));
  }

  protected void addWaypoint() {
    float r, g, b;
    this.addClicked = true;
    if (this.waypointManager.getWaypoints().size() == 0) {
      r = 0.0F;
      g = 1.0F;
      b = 0.0F;
    } else {
      r = this.generator.nextFloat();
      g = this.generator.nextFloat();
      b = this.generator.nextFloat();
    }
    TreeSet<DimensionContainer> dimensions = new TreeSet<>();
    dimensions.add(AbstractVoxelMap.getInstance().getDimensionManager().getDimensionContainerByWorld((class_1937)(getMinecraft()).field_1687));
    double dimensionScale = this.options.game.field_1724.field_6002.method_8597().comp_646();
    this.newWaypoint = new Waypoint("", (int)(GameVariableAccessShim.xCoord() * dimensionScale), (int)(GameVariableAccessShim.zCoord() * dimensionScale), GameVariableAccessShim.yCoord(), true, r, g, b, "", this.master.getWaypointManager().getCurrentSubworldDescriptor(false), dimensions);
    getMinecraft().method_1507((class_437)new GuiAddWaypoint(this, this.master, this.newWaypoint, false));
  }

  protected void toggleWaypointVisibility() {
    this.selectedWaypoint.enabled = !this.selectedWaypoint.enabled;
    this.waypointManager.saveWaypoints();
  }

  public void method_25394(class_4587 matrixStack, int mouseX, int mouseY, float partialTicks) {
    drawMap(matrixStack);
    this.tooltip = null;
    this.waypointList.method_25394(matrixStack, mouseX, mouseY, partialTicks);
    method_27534(matrixStack, getFontRenderer(), this.screenTitle, getWidth() / 2, 20, 16777215);
    super.method_25394(matrixStack, mouseX, mouseY, partialTicks);
    method_25303(matrixStack, getFontRenderer(), I18nUtils.getString("minimap.waypoints.filter", new Object[0]) + ":", getWidth() / 2 - 153, getHeight() - 75, 10526880);
    this.filter.method_25394(matrixStack, mouseX, mouseY, partialTicks);
    if (this.tooltip != null)
      method_25424(matrixStack, this.tooltip, mouseX, mouseY);
  }

  static void setTooltip(GuiWaypoints par0GuiWaypoints, class_2561 par1Str) {
    par0GuiWaypoints.tooltip = par1Str;
  }

  public boolean canTeleport() {
    boolean allowed, singlePlayer = this.options.game.method_1496();
    if (singlePlayer) {
      try {
        allowed = getMinecraft().method_1576().method_3760().method_14569((getMinecraft()).field_1724.method_7334());
      } catch (Exception var4) {
        allowed = getMinecraft().method_1576().method_27728().method_194();
      }
    } else {
      allowed = true;
    }
    return allowed;
  }

  public void method_25432() {
    this.field_22787.field_1774.method_1462(false);
    if (this.changedSort)
      super.method_25432();
  }
}
