package cn.magicst.mamiyaotaru.voxelmap.gui;

import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.GuiScreenMinimap;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.IVoxelMap;
import cn.magicst.mamiyaotaru.voxelmap.util.I18nUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.class_2561;
import net.minecraft.class_342;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import net.minecraft.class_5250;

public class GuiSelectPlayer extends GuiScreenMinimap implements BooleanConsumer {
  private final class_437 parentScreen;

  protected class_2561 screenTitle = (class_2561)class_2561.method_43470("players");

  private boolean sharingWaypoint;

  private GuiButtonRowListPlayers playerList;

  protected boolean allClicked = false;

  protected class_342 message;

  protected class_342 filter;

  private class_2561 tooltip = null;

  private final String locInfo;

  final class_5250 SHARE_MESSAGE = class_2561.method_43471("minimap.waypointshare.sharemessage").method_27693(":");

  final class_2561 SHARE_WITH = (class_2561)class_2561.method_43471("minimap.waypointshare.sharewith");

  final class_2561 SHARE_WAYPOINT = (class_2561)class_2561.method_43471("minimap.waypointshare.title");

  final class_2561 SHARE_COORDINATES = (class_2561)class_2561.method_43471("minimap.waypointshare.titlecoordinate");

  public GuiSelectPlayer(class_437 parentScreen, IVoxelMap master, String locInfo, boolean sharingWaypoint) {
    this.parentScreen = parentScreen;
    this.locInfo = locInfo;
    this.sharingWaypoint = sharingWaypoint;
  }

  public void method_25393() {
    this.message.method_1865();
    this.filter.method_1865();
  }

  public void method_25426() {
    this.screenTitle = this.sharingWaypoint ? this.SHARE_WAYPOINT : this.SHARE_COORDINATES;
    (getMinecraft()).field_1774.method_1462(true);
    this.playerList = new GuiButtonRowListPlayers(this);
    int messageStringWidth = getFontRenderer().method_1727(I18nUtils.getString("minimap.waypointshare.sharemessage", new Object[0]) + ":");
    this.message = new class_342(getFontRenderer(), getWidth() / 2 - 153 + messageStringWidth + 5, 34, 305 - messageStringWidth - 5, 20, (class_2561)null);
    this.message.method_1880(78);
    method_37063((class_364)this.message);
    int filterStringWidth = getFontRenderer().method_1727(I18nUtils.getString("minimap.waypoints.filter", new Object[0]) + ":");
    this.filter = new class_342(getFontRenderer(), getWidth() / 2 - 153 + filterStringWidth + 5, getHeight() - 55, 305 - filterStringWidth - 5, 20, (class_2561)null);
    this.filter.method_1880(35);
    method_37063((class_364)this.filter);
    method_37063((class_364)new class_4185(this.field_22789 / 2 - 100, this.field_22790 - 27, 150, 20, (class_2561)class_2561.method_43471("gui.cancel"), button -> getMinecraft().method_1507(this.parentScreen)));
    method_25395((class_364)this.filter);
    this.filter.method_1876(true);
  }

  public boolean method_25404(int keysm, int scancode, int b) {
    boolean OK = super.method_25404(keysm, scancode, b);
    if (this.filter.method_25370())
      this.playerList.updateFilter(this.filter.method_1882().toLowerCase());
    return OK;
  }

  public boolean method_25400(char character, int keycode) {
    boolean OK = super.method_25400(character, keycode);
    if (this.filter.method_25370())
      this.playerList.updateFilter(this.filter.method_1882().toLowerCase());
    return OK;
  }

  public boolean method_25402(double mouseX, double mouseY, int mouseButton) {
    this.playerList.method_25402(mouseX, mouseY, mouseButton);
    return super.method_25402(mouseX, mouseY, mouseButton);
  }

  public boolean method_25406(double mouseX, double mouseY, int mouseButton) {
    this.playerList.method_25406(mouseX, mouseY, mouseButton);
    return super.method_25406(mouseX, mouseY, mouseButton);
  }

  public boolean method_25403(double mouseX, double mouseY, int mouseEvent, double deltaX, double deltaY) {
    return this.playerList.method_25403(mouseX, mouseY, mouseEvent, deltaX, deltaY);
  }

  public boolean method_25401(double mouseX, double mouseY, double amount) {
    return this.playerList.method_25401(mouseX, mouseY, amount);
  }

  public void accept(boolean par1) {
    if (this.allClicked) {
      this.allClicked = false;
      if (par1) {
        String combined = this.message.method_1882() + " " + this.message.method_1882();
        if (combined.length() > 100) {
          this.field_22787.field_1724.method_3142(this.message.method_1882());
          this.field_22787.field_1724.method_3142(this.locInfo);
        } else {
          this.field_22787.field_1724.method_3142(combined);
        }
        getMinecraft().method_1507(this.parentScreen);
      } else {
        getMinecraft().method_1507((class_437)this);
      }
    }
  }

  protected void sendMessageToPlayer(String name) {
    String combined = "msg " + name + " " + this.message.method_1882() + " " + this.locInfo;
    if (combined.length() > 100) {
      (getMinecraft()).field_1724.method_44099("msg " + name + " " + this.message.method_1882());
      (getMinecraft()).field_1724.method_44099("msg " + name + " " + this.locInfo);
    } else {
      (getMinecraft()).field_1724.method_44099(combined);
    }
    getMinecraft().method_1507(this.parentScreen);
  }

  public void method_25394(class_4587 matrixStack, int mouseX, int mouseY, float partialTicks) {
    drawMap(matrixStack);
    this.tooltip = null;
    this.playerList.method_25394(matrixStack, mouseX, mouseY, partialTicks);
    method_27534(matrixStack, getFontRenderer(), this.screenTitle, getWidth() / 2, 20, 16777215);
    super.method_25394(matrixStack, mouseX, mouseY, partialTicks);
    method_27535(matrixStack, getFontRenderer(), (class_2561)this.SHARE_MESSAGE, getWidth() / 2 - 153, 39, 10526880);
    this.message.method_25394(matrixStack, mouseX, mouseY, partialTicks);
    method_27534(matrixStack, getFontRenderer(), this.SHARE_WITH, getWidth() / 2, 75, 16777215);
    method_25303(matrixStack, getFontRenderer(), I18nUtils.getString("minimap.waypoints.filter", new Object[0]) + ":", getWidth() / 2 - 153, getHeight() - 50, 10526880);
    this.filter.method_25394(matrixStack, mouseX, mouseY, partialTicks);
    if (this.tooltip != null)
      method_25424(matrixStack, this.tooltip, mouseX, mouseY);
  }

  static void setTooltip(GuiSelectPlayer par0GuiWaypoints, class_2561 par1Str) {
    par0GuiWaypoints.tooltip = par1Str;
  }

  public void method_25432() {
    (getMinecraft()).field_1774.method_1462(false);
  }
}
