package cn.magicst.mamiyaotaru.voxelmap.gui;

import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.GuiScreenMinimap;
import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.IPopupGuiScreen;
import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.Popup;
import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.PopupGuiButton;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.IColorManager;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.IVoxelMap;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.IWaypointManager;
import cn.magicst.mamiyaotaru.voxelmap.textures.Sprite;
import cn.magicst.mamiyaotaru.voxelmap.textures.TextureAtlas;
import cn.magicst.mamiyaotaru.voxelmap.util.DimensionContainer;
import cn.magicst.mamiyaotaru.voxelmap.util.GLShim;
import cn.magicst.mamiyaotaru.voxelmap.util.GLUtils;
import cn.magicst.mamiyaotaru.voxelmap.util.I18nUtils;
import cn.magicst.mamiyaotaru.voxelmap.util.Waypoint;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.class_1937;
import net.minecraft.class_2561;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_293;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_342;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_4587;
import net.minecraft.class_757;

public class GuiAddWaypoint extends GuiScreenMinimap implements IPopupGuiScreen {
  IVoxelMap master;

  IWaypointManager waypointManager;

  IColorManager colorManager;

  private final IGuiWaypoints parentGui;

  private PopupGuiButton doneButton;

  private GuiSlotDimensions dimensionList;

  protected DimensionContainer selectedDimension = null;

  private class_2561 tooltip = null;

  private class_342 waypointName;

  private class_342 waypointX;

  private class_342 waypointZ;

  private class_342 waypointY;

  private PopupGuiButton buttonEnabled;

  protected Waypoint waypoint;

  private boolean choosingColor = false;

  private boolean choosingIcon = false;

  private final float red;

  private final float green;

  private final float blue;

  private final String suffix;

  private final boolean enabled;

  private boolean editing = false;

  private final class_2960 pickerResourceLocation = new class_2960("voxelmap", "images/colorpicker.png");

  private final class_2960 blank = new class_2960("textures/misc/white.png");

  public GuiAddWaypoint(IGuiWaypoints par1GuiScreen, IVoxelMap master, Waypoint par2Waypoint, boolean editing) {
    this.master = master;
    this.waypointManager = master.getWaypointManager();
    this.colorManager = master.getColorManager();
    this.parentGui = par1GuiScreen;
    this.waypoint = par2Waypoint;
    this.red = this.waypoint.red;
    this.green = this.waypoint.green;
    this.blue = this.waypoint.blue;
    this.suffix = this.waypoint.imageSuffix;
    this.enabled = this.waypoint.enabled;
    this.editing = editing;
  }

  public void method_25393() {
    this.waypointName.method_1865();
    this.waypointX.method_1865();
    this.waypointY.method_1865();
    this.waypointZ.method_1865();
  }

  public void method_25426() {
    if (getMinecraft() == null)
      return;
    (getMinecraft()).field_1774.method_1462(true);
    method_37067();
    this.waypointName = new class_342(getFontRenderer(), getWidth() / 2 - 100, getHeight() / 6 + 13, 200, 20, null);
    this.waypointName.method_1852(this.waypoint.name);
    this.waypointX = new class_342(getFontRenderer(), getWidth() / 2 - 100, getHeight() / 6 + 41 + 13, 56, 20, null);
    this.waypointX.method_1880(128);
    this.waypointX.method_1852("" + this.waypoint.getX());
    this.waypointZ = new class_342(getFontRenderer(), getWidth() / 2 - 28, getHeight() / 6 + 41 + 13, 56, 20, null);
    this.waypointZ.method_1880(128);
    this.waypointZ.method_1852("" + this.waypoint.getZ());
    this.waypointY = new class_342(getFontRenderer(), getWidth() / 2 + 44, getHeight() / 6 + 41 + 13, 56, 20, null);
    this.waypointY.method_1880(128);
    this.waypointY.method_1852("" + this.waypoint.getY());
    method_37063((class_364)this.waypointName);
    method_37063((class_364)this.waypointX);
    method_37063((class_364)this.waypointZ);
    method_37063((class_364)this.waypointY);
    int buttonListY = getHeight() / 6 + 82 + 6;
    method_37063((class_364)(this.buttonEnabled = new PopupGuiButton(getWidth() / 2 - 101, buttonListY, 100, 20, (class_2561)class_2561.method_43470("Enabled: " + (this.waypoint.enabled ? "On" : "Off")), button -> this.waypoint.enabled = !this.waypoint.enabled, this)));
    method_37063((class_364)new PopupGuiButton(getWidth() / 2 - 101, buttonListY + 24, 100, 20, (class_2561)class_2561.method_43470(I18nUtils.getString("minimap.waypoints.sortbycolor", new Object[0]) + ":     "), button -> this.choosingColor = true, this));
    method_37063((class_364)new PopupGuiButton(getWidth() / 2 - 101, buttonListY + 48, 100, 20, (class_2561)class_2561.method_43470(I18nUtils.getString("minimap.waypoints.sortbyicon", new Object[0]) + ":     "), button -> this.choosingIcon = true, this));
    this.doneButton = new PopupGuiButton(getWidth() / 2 - 155, getHeight() / 6 + 168, 150, 20, (class_2561)class_2561.method_43471("addServer.add"), button -> acceptWaypoint(), this);
    method_37063((class_364)this.doneButton);
    method_37063((class_364)new PopupGuiButton(getWidth() / 2 + 5, getHeight() / 6 + 168, 150, 20, (class_2561)class_2561.method_43471("gui.cancel"), button -> cancelWaypoint(), this));
    this.doneButton.field_22763 = (this.waypointName.method_1882().length() > 0);
    method_25395((class_364)this.waypointName);
    this.waypointName.method_1876(true);
    this.dimensionList = new GuiSlotDimensions(this);
  }

  public void method_25432() {
    if (getMinecraft() == null)
      return;
    (getMinecraft()).field_1774.method_1462(false);
  }

  protected void cancelWaypoint() {
    this.waypoint.red = this.red;
    this.waypoint.green = this.green;
    this.waypoint.blue = this.blue;
    this.waypoint.imageSuffix = this.suffix;
    this.waypoint.enabled = this.enabled;
    if (this.parentGui != null) {
      this.parentGui.accept(false);
    } else {
      if (getMinecraft() == null)
        return;
      getMinecraft().method_1507(null);
    }
  }

  protected void acceptWaypoint() {
    this.waypoint.name = this.waypointName.method_1882();
    this.waypoint.setX(Integer.parseInt(this.waypointX.method_1882()));
    this.waypoint.setZ(Integer.parseInt(this.waypointZ.method_1882()));
    this.waypoint.setY(Integer.parseInt(this.waypointY.method_1882()));
    if (this.parentGui != null) {
      this.parentGui.accept(true);
    } else {
      if (this.editing) {
        this.waypointManager.saveWaypoints();
      } else {
        this.waypointManager.addWaypoint(this.waypoint);
      }
      if (getMinecraft() == null)
        return;
      getMinecraft().method_1507(null);
    }
  }

  public boolean method_25404(int keysm, int scancode, int b) {
    boolean OK = false;
    if (popupOpen()) {
      OK = super.method_25404(keysm, scancode, b);
      boolean acceptable = (this.waypointName.method_1882().length() > 0);
      try {
        Integer.parseInt(this.waypointX.method_1882());
        Integer.parseInt(this.waypointZ.method_1882());
        Integer.parseInt(this.waypointY.method_1882());
      } catch (NumberFormatException var7) {
        acceptable = false;
      }
      this.doneButton.field_22763 = acceptable;
      if ((keysm == 257 || keysm == 335) && acceptable)
        acceptWaypoint();
    }
    return OK;
  }

  public boolean method_25400(char character, int keycode) {
    boolean OK = false;
    if (popupOpen()) {
      OK = super.method_25400(character, keycode);
      boolean acceptable = (this.waypointName.method_1882().length() > 0);
      try {
        Integer.parseInt(this.waypointX.method_1882());
        Integer.parseInt(this.waypointZ.method_1882());
        Integer.parseInt(this.waypointY.method_1882());
      } catch (NumberFormatException var6) {
        acceptable = false;
      }
      this.doneButton.field_22763 = acceptable;
    }
    return OK;
  }

  public boolean method_25402(double mouseX, double mouseY, int mouseButton) {
    if (popupOpen()) {
      super.method_25402(mouseX, mouseY, mouseButton);
      this.waypointName.method_25402(mouseX, mouseY, mouseButton);
      this.waypointX.method_25402(mouseX, mouseY, mouseButton);
      this.waypointZ.method_25402(mouseX, mouseY, mouseButton);
      this.waypointY.method_25402(mouseX, mouseY, mouseButton);
    } else if (this.choosingColor) {
      if (mouseX >= (getWidth() / 2 - 128) && mouseX < (getWidth() / 2 + 128) && mouseY >= (getHeight() / 2 - 128) && mouseY < (getHeight() / 2 + 128)) {
        int color = this.colorManager.getColorPicker().getRGB((int)mouseX - getWidth() / 2 - 128, (int)mouseY - getHeight() / 2 - 128);
        this.waypoint.red = (color >> 16 & 0xFF) / 255.0F;
        this.waypoint.green = (color >> 8 & 0xFF) / 255.0F;
        this.waypoint.blue = (color & 0xFF) / 255.0F;
        this.choosingColor = false;
      }
    } else if (this.choosingIcon &&
            getMinecraft() != null) {
      float scScale = (float)getMinecraft().method_22683().method_4495();
      TextureAtlas chooser = this.waypointManager.getTextureAtlasChooser();
      float scale = scScale / 2.0F;
      float displayWidthFloat = chooser.getWidth() / scale;
      float displayHeightFloat = chooser.getHeight() / scale;
      if (displayWidthFloat > getMinecraft().method_22683().method_4489()) {
        float adj = displayWidthFloat / getMinecraft().method_22683().method_4489();
        scale *= adj;
        displayWidthFloat /= adj;
        displayHeightFloat /= adj;
      }
      if (displayHeightFloat > getMinecraft().method_22683().method_4506()) {
        float adj = displayHeightFloat / getMinecraft().method_22683().method_4506();
        scale *= adj;
        displayWidthFloat /= adj;
        displayHeightFloat /= adj;
      }
      int displayWidth = (int)displayWidthFloat;
      int displayHeight = (int)displayHeightFloat;
      if (mouseX >= (getWidth() / 2 - displayWidth / 2) && mouseX < (getWidth() / 2 + displayWidth / 2) && mouseY >= (getHeight() / 2 - displayHeight / 2) && mouseY < (getHeight() / 2 + displayHeight / 2)) {
        float x = ((float)mouseX - (getWidth() / 2 - displayWidth / 2)) * scale;
        float y = ((float)mouseY - (getHeight() / 2 - displayHeight / 2)) * scale;
        Sprite icon = chooser.getIconAt(x, y);
        if (icon != chooser.getMissingImage()) {
          this.waypoint.imageSuffix = icon.getIconName().replace("voxelmap:images/waypoints/waypoint", "").replace(".png", "");
          this.choosingIcon = false;
        }
      }
    }
    if (popupOpen() && this.dimensionList != null)
      this.dimensionList.method_25402(mouseX, mouseY, mouseButton);
    return true;
  }

  public boolean method_25406(double mouseX, double mouseY, int mouseButton) {
    if (popupOpen() && this.dimensionList != null)
      this.dimensionList.method_25406(mouseX, mouseY, mouseButton);
    return true;
  }

  public boolean method_25403(double mouseX, double mouseY, int mouseEvent, double deltaX, double deltaY) {
    return (!popupOpen() || this.dimensionList == null || this.dimensionList.method_25403(mouseX, mouseY, mouseEvent, deltaX, deltaY));
  }

  public boolean method_25401(double mouseX, double mouseY, double amount) {
    return (!popupOpen() || this.dimensionList == null || this.dimensionList.method_25401(mouseX, mouseY, amount));
  }

  public boolean overPopup(int x, int y) {
    return (!this.choosingColor && !this.choosingIcon);
  }

  public boolean popupOpen() {
    return (!this.choosingColor && !this.choosingIcon);
  }

  public void popupAction(Popup popup, int action) {}

  public void method_25394(class_4587 matrixStack, int mouseX, int mouseY, float partialTicks) {
    if (getMinecraft() == null)
      return;
    drawMap(matrixStack);
    float scScale = (float)getMinecraft().method_22683().method_4495();
    this.tooltip = null;
    this.buttonEnabled.method_25355((class_2561)class_2561.method_43470(I18nUtils.getString("minimap.waypoints.enabled", new Object[0]) + " " + I18nUtils.getString("minimap.waypoints.enabled", new Object[0])));
    if (!this.choosingColor && !this.choosingIcon)
      method_25420(matrixStack);
    this.dimensionList.method_25394(matrixStack, mouseX, mouseY, partialTicks);
    method_25300(matrixStack, getFontRenderer(), ((this.parentGui == null || !this.parentGui.isEditing()) && !this.editing) ? I18nUtils.getString("minimap.waypoints.new", new Object[0]) : I18nUtils.getString("minimap.waypoints.edit", new Object[0]), getWidth() / 2, 20, 16777215);
    method_25303(matrixStack, getFontRenderer(), I18nUtils.getString("minimap.waypoints.name", new Object[0]), getWidth() / 2 - 100, getHeight() / 6, 10526880);
    method_25303(matrixStack, getFontRenderer(), I18nUtils.getString("X", new Object[0]), getWidth() / 2 - 100, getHeight() / 6 + 41, 10526880);
    method_25303(matrixStack, getFontRenderer(), I18nUtils.getString("Z", new Object[0]), getWidth() / 2 - 28, getHeight() / 6 + 41, 10526880);
    method_25303(matrixStack, getFontRenderer(), I18nUtils.getString("Y", new Object[0]), getWidth() / 2 + 44, getHeight() / 6 + 41, 10526880);
    this.waypointName.method_25394(matrixStack, mouseX, mouseY, partialTicks);
    this.waypointX.method_25394(matrixStack, mouseX, mouseY, partialTicks);
    this.waypointZ.method_25394(matrixStack, mouseX, mouseY, partialTicks);
    this.waypointY.method_25394(matrixStack, mouseX, mouseY, partialTicks);
    int buttonListY = getHeight() / 6 + 82 + 6;
    super.method_25394(matrixStack, mouseX, mouseY, partialTicks);
    GLShim.glColor4f(this.waypoint.red, this.waypoint.green, this.waypoint.blue, 1.0F);
    GLShim.glDisable(3553);
    RenderSystem.setShader(class_757::method_34543);
    RenderSystem.setShaderTexture(0, this.blank);
    method_25302(matrixStack, getWidth() / 2 - 25, buttonListY + 24 + 5, 0, 0, 16, 10);
    TextureAtlas chooser = this.waypointManager.getTextureAtlasChooser();
    RenderSystem.setShader(class_757::method_34542);
    GLUtils.disp2(chooser.method_4624());
    GLShim.glTexParameteri(3553, 10241, 9729);
    Sprite icon = chooser.getAtlasSprite("voxelmap:images/waypoints/waypoint" + this.waypoint.imageSuffix + ".png");
    drawTexturedModalRect((getWidth() / 2 - 25), (buttonListY + 48 + 2), icon, 16.0F, 16.0F);
    if (this.choosingColor || this.choosingIcon)
      method_25420(matrixStack);
    if (this.choosingColor) {
      GLShim.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GLUtils.img2(this.pickerResourceLocation);
      GLShim.glTexParameteri(3553, 10241, 9728);
      method_25302(matrixStack, getWidth() / 2 - 128, getHeight() / 2 - 128, 0, 0, 256, 256);
    }
    if (this.choosingIcon) {
      float scale = scScale / 2.0F;
      float displayWidthFloat = chooser.getWidth() / scale;
      float displayHeightFloat = chooser.getHeight() / scale;
      if (displayWidthFloat > getMinecraft().method_22683().method_4489()) {
        float adj = displayWidthFloat / getMinecraft().method_22683().method_4489();
        displayWidthFloat /= adj;
        displayHeightFloat /= adj;
      }
      if (displayHeightFloat > getMinecraft().method_22683().method_4506()) {
        float adj = displayHeightFloat / getMinecraft().method_22683().method_4506();
        displayWidthFloat /= adj;
        displayHeightFloat /= adj;
      }
      int displayWidth = (int)displayWidthFloat;
      int displayHeight = (int)displayHeightFloat;
      RenderSystem.setShader(class_757::method_34543);
      RenderSystem.setShaderTexture(0, this.blank);
      GLShim.glTexParameteri(3553, 10241, 9728);
      GLShim.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
      method_25302(matrixStack, getWidth() / 2 - displayWidth / 2 - 1, getHeight() / 2 - displayHeight / 2 - 1, 0, 0, displayWidth + 2, displayHeight + 2);
      GLShim.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      method_25302(matrixStack, getWidth() / 2 - displayWidth / 2, getHeight() / 2 - displayHeight / 2, 0, 0, displayWidth, displayHeight);
      GLShim.glColor4f(this.waypoint.red, this.waypoint.green, this.waypoint.blue, 1.0F);
      GLShim.glEnable(3042);
      RenderSystem.setShader(class_757::method_34542);
      GLUtils.disp2(chooser.method_4624());
      GLShim.glTexParameteri(3553, 10241, 9729);
      method_25293(matrixStack, getWidth() / 2 - displayWidth / 2, getHeight() / 2 - displayHeight / 2, displayWidth, displayHeight, 0.0F, 0.0F, chooser.getWidth(), chooser.getHeight(), chooser.getImageWidth(), chooser.getImageHeight());
      if (mouseX >= getWidth() / 2 - displayWidth / 2 && mouseX <= getWidth() / 2 + displayWidth / 2 && mouseY >= getHeight() / 2 - displayHeight / 2 && mouseY <= getHeight() / 2 + displayHeight / 2) {
        float x = (mouseX - getWidth() / 2 - displayWidth / 2) * scale;
        float y = (mouseY - getHeight() / 2 - displayHeight / 2) * scale;
        icon = chooser.getIconAt(x, y);
        if (icon != chooser.getMissingImage())
          this.tooltip = (class_2561)class_2561.method_43470(icon.getIconName().replace("voxelmap:images/waypoints/waypoint", "").replace(".png", ""));
      }
      GLShim.glDisable(3042);
      GLShim.glTexParameteri(3553, 10241, 9728);
    }
    if (this.tooltip != null)
      method_25424(matrixStack, this.tooltip, mouseX, mouseY);
  }

  public void setSelectedDimension(DimensionContainer dimension) {
    this.selectedDimension = dimension;
  }

  public void toggleDimensionSelected() {
    if (this.waypoint.dimensions.size() > 1 && this.waypoint.dimensions.contains(this.selectedDimension) && this.selectedDimension != this.master.getDimensionManager().getDimensionContainerByWorld((class_1937)(class_310.method_1551()).field_1687)) {
      this.waypoint.dimensions.remove(this.selectedDimension);
    } else {
      this.waypoint.dimensions.add(this.selectedDimension);
    }
  }

  static void setTooltip(GuiAddWaypoint par0GuiWaypoint, class_2561 par1Str) {
    par0GuiWaypoint.tooltip = par1Str;
  }

  public void drawTexturedModalRect(float xCoord, float yCoord, Sprite icon, float widthIn, float heightIn) {
    class_289 tessellator = class_289.method_1348();
    class_287 vertexbuffer = tessellator.method_1349();
    vertexbuffer.method_1328(class_293.class_5596.field_27382, class_290.field_1585);
    vertexbuffer.method_22912((xCoord + 0.0F), (yCoord + heightIn), method_25305()).method_22913(icon.getMinU(), icon.getMaxV()).method_1344();
    vertexbuffer.method_22912((xCoord + widthIn), (yCoord + heightIn), method_25305()).method_22913(icon.getMaxU(), icon.getMaxV()).method_1344();
    vertexbuffer.method_22912((xCoord + widthIn), (yCoord + 0.0F), method_25305()).method_22913(icon.getMaxU(), icon.getMinV()).method_1344();
    vertexbuffer.method_22912((xCoord + 0.0F), (yCoord + 0.0F), method_25305()).method_22913(icon.getMinU(), icon.getMinV()).method_1344();
    tessellator.method_1350();
  }
}
