package cn.magicst.mamiyaotaru.voxelmap.gui;

import cn.magicst.mamiyaotaru.voxelmap.RadarSettingsManager;
import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.GuiScreenMinimap;
import cn.magicst.mamiyaotaru.voxelmap.util.CustomMob;
import cn.magicst.mamiyaotaru.voxelmap.util.CustomMobsManager;
import cn.magicst.mamiyaotaru.voxelmap.util.EnumMobs;
import cn.magicst.mamiyaotaru.voxelmap.util.I18nUtils;
import net.minecraft.class_2561;
import net.minecraft.class_342;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_4587;

public class GuiMobs extends GuiScreenMinimap {
  private final class_437 parentScreen;

  protected final RadarSettingsManager options;

  protected class_2561 screenTitle;

  private GuiSlotMobs mobsList;

  private class_4185 buttonEnable;

  private class_4185 buttonDisable;

  protected class_342 filter;

  private class_2561 tooltip = null;

  protected String selectedMobId = null;

  public GuiMobs(class_437 parentScreen, RadarSettingsManager options) {
    this.parentScreen = parentScreen;
    this.options = options;
  }

  public void method_25393() {
    this.filter.method_1865();
  }

  public void method_25426() {
    this.screenTitle = (class_2561)class_2561.method_43471("options.minimap.mobs.title");
    (getMinecraft()).field_1774.method_1462(true);
    this.mobsList = new GuiSlotMobs(this);
    int filterStringWidth = getFontRenderer().method_1727(I18nUtils.getString("minimap.waypoints.filter", new Object[0]) + ":");
    this.filter = new class_342(getFontRenderer(), getWidth() / 2 - 153 + filterStringWidth + 5, getHeight() - 56, 305 - filterStringWidth - 5, 20, (class_2561)null);
    this.filter.method_1880(35);
    method_37063((class_364)this.filter);
    method_37063((class_364)(this.buttonEnable = new class_4185(getWidth() / 2 - 154, getHeight() - 28, 100, 20, (class_2561)class_2561.method_43471("options.minimap.mobs.enable"), button -> setMobEnabled(this.selectedMobId, true))));
    method_37063((class_364)(this.buttonDisable = new class_4185(getWidth() / 2 - 50, getHeight() - 28, 100, 20, (class_2561)class_2561.method_43471("options.minimap.mobs.disable"), button -> setMobEnabled(this.selectedMobId, false))));
    method_37063((class_364)new class_4185(getWidth() / 2 + 4 + 50, getHeight() - 28, 100, 20, (class_2561)class_2561.method_43471("gui.done"), button -> getMinecraft().method_1507(this.parentScreen)));
    method_25395((class_364)this.filter);
    this.filter.method_1876(true);
    boolean isSomethingSelected = (this.selectedMobId != null);
    this.buttonEnable.field_22763 = isSomethingSelected;
    this.buttonDisable.field_22763 = isSomethingSelected;
  }

  public boolean method_25404(int keysm, int scancode, int b) {
    boolean OK = super.method_25404(keysm, scancode, b);
    if (this.filter.method_25370())
      this.mobsList.updateFilter(this.filter.method_1882().toLowerCase());
    return OK;
  }

  public boolean method_25400(char character, int keycode) {
    boolean OK = super.method_25400(character, keycode);
    if (this.filter.method_25370())
      this.mobsList.updateFilter(this.filter.method_1882().toLowerCase());
    return OK;
  }

  public boolean method_25402(double mouseX, double mouseY, int mouseButton) {
    this.mobsList.method_25402(mouseX, mouseY, mouseButton);
    return super.method_25402(mouseX, mouseY, mouseButton);
  }

  public boolean method_25406(double mouseX, double mouseY, int mouseButton) {
    this.mobsList.method_25406(mouseX, mouseY, mouseButton);
    return super.method_25406(mouseX, mouseY, mouseButton);
  }

  public boolean method_25403(double mouseX, double mouseY, int mouseEvent, double deltaX, double deltaY) {
    return this.mobsList.method_25403(mouseX, mouseY, mouseEvent, deltaX, deltaY);
  }

  public boolean method_25401(double mouseX, double mouseY, double amount) {
    return this.mobsList.method_25401(mouseX, mouseY, amount);
  }

  protected void setSelectedMob(String id) {
    this.selectedMobId = id;
  }

  private boolean isMobEnabled(String mobId) {
    EnumMobs mob = EnumMobs.getMobByName(mobId);
    if (mob != null)
      return mob.enabled;
    CustomMob customMob = CustomMobsManager.getCustomMobByType(mobId);
    return (customMob != null && customMob.enabled);
  }

  private void setMobEnabled(String mobId, boolean enabled) {
    for (EnumMobs mob : EnumMobs.values()) {
      if (mob.id.equals(mobId))
        mob.enabled = enabled;
    }
    for (CustomMob mob : CustomMobsManager.mobs) {
      if (mob.id.equals(mobId))
        mob.enabled = enabled;
    }
  }

  protected void toggleMobVisibility() {
    EnumMobs mob = EnumMobs.getMobByName(this.selectedMobId);
    if (mob != null) {
      setMobEnabled(this.selectedMobId, !mob.enabled);
    } else {
      CustomMob customMob = CustomMobsManager.getCustomMobByType(this.selectedMobId);
      if (customMob != null)
        setMobEnabled(this.selectedMobId, !customMob.enabled);
    }
  }

  public void method_25394(class_4587 matrixStack, int mouseX, int mouseY, float partialticks) {
    drawMap(matrixStack);
    this.tooltip = null;
    this.mobsList.method_25394(matrixStack, mouseX, mouseY, partialticks);
    method_27534(matrixStack, getFontRenderer(), this.screenTitle, getWidth() / 2, 20, 16777215);
    boolean isSomethingSelected = (this.selectedMobId != null);
    this.buttonEnable.field_22763 = (isSomethingSelected && !isMobEnabled(this.selectedMobId));
    this.buttonDisable.field_22763 = (isSomethingSelected && isMobEnabled(this.selectedMobId));
    super.method_25394(matrixStack, mouseX, mouseY, partialticks);
    method_25303(matrixStack, getFontRenderer(), I18nUtils.getString("minimap.waypoints.filter", new Object[0]) + ":", getWidth() / 2 - 153, getHeight() - 51, 10526880);
    this.filter.method_25394(matrixStack, mouseX, mouseY, partialticks);
    if (this.tooltip != null)
      method_25424(matrixStack, this.tooltip, mouseX, mouseY);
  }

  static void setTooltip(GuiMobs par0GuiWaypoints, class_2561 par1Str) {
    par0GuiWaypoints.tooltip = par1Str;
  }

  public void method_25432() {
    this.field_22787.field_1774.method_1462(false);
    super.method_25432();
  }
}
