package cn.magicst.mamiyaotaru.voxelmap.gui;

import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.GuiScreenMinimap;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.IVoxelMap;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.IWaypointManager;
import cn.magicst.mamiyaotaru.voxelmap.util.I18nUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.ArrayList;
import net.minecraft.class_2561;
import net.minecraft.class_342;
import net.minecraft.class_364;
import net.minecraft.class_410;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import net.minecraft.class_5250;

public class GuiSubworldEdit extends GuiScreenMinimap implements BooleanConsumer {
  private final class_437 parent;

  private final IWaypointManager waypointManager;

  private final ArrayList<?> knownSubworldNames;

  private String originalSubworldName = "";

  private String currentSubworldName = "";

  private class_342 subworldNameField;

  private class_4185 doneButton;

  private class_4185 deleteButton;

  private boolean deleteClicked = false;

  public GuiSubworldEdit(class_437 parent, IVoxelMap master, String subworldName) {
    this.parent = parent;
    this.waypointManager = master.getWaypointManager();
    this.originalSubworldName = subworldName;
    this.knownSubworldNames = new ArrayList(this.waypointManager.getKnownSubworldNames());
  }

  public void method_25393() {
    this.subworldNameField.method_1865();
  }

  public void method_25426() {
    (getMinecraft()).field_1774.method_1462(true);
    method_37067();
    this.subworldNameField = new class_342(getFontRenderer(), getWidth() / 2 - 100, getHeight() / 6 + 13, 200, 20, null);
    method_25395((class_364)this.subworldNameField);
    this.subworldNameField.method_1876(true);
    this.subworldNameField.method_1852(this.originalSubworldName);
    method_37063((class_364)this.subworldNameField);
    method_37063((class_364)(this.doneButton = new class_4185(getWidth() / 2 - 155, getHeight() / 6 + 168, 150, 20, (class_2561)class_2561.method_43471("gui.done"), button -> changeNameClicked())));
    method_37063((class_364)new class_4185(getWidth() / 2 + 5, getHeight() / 6 + 168, 150, 20, (class_2561)class_2561.method_43471("gui.cancel"), button -> getMinecraft().method_1507(this.parent)));
    int buttonListY = getHeight() / 6 + 82 + 6;
    method_37063((class_364)(this.deleteButton = new class_4185(getWidth() / 2 - 50, buttonListY + 24, 100, 20, (class_2561)class_2561.method_43471("selectServer.delete"), button -> deleteClicked())));
    this.doneButton.field_22763 = isNameAcceptable();
    this.deleteButton.field_22763 = this.originalSubworldName.equals(this.subworldNameField.method_1882());
  }

  public void method_25432() {
    (getMinecraft()).field_1774.method_1462(false);
  }

  private void changeNameClicked() {
    if (!this.currentSubworldName.equals(this.originalSubworldName))
      this.waypointManager.changeSubworldName(this.originalSubworldName, this.currentSubworldName);
    getMinecraft().method_1507(this.parent);
  }

  private void deleteClicked() {
    this.deleteClicked = true;
    class_5250 class_52501 = class_2561.method_43471("worldmap.subworld.deleteconfirm");
    class_5250 class_52502 = class_2561.method_43469("selectServer.deleteWarning", new Object[] { this.originalSubworldName });
    class_5250 class_52503 = class_2561.method_43471("selectServer.deleteButton");
    class_5250 class_52504 = class_2561.method_43471("gui.cancel");
    class_410 confirmScreen = new class_410(this, (class_2561)class_52501, (class_2561)class_52502, (class_2561)class_52503, (class_2561)class_52504);
    getMinecraft().method_1507((class_437)confirmScreen);
  }

  public void accept(boolean par1) {
    if (this.deleteClicked) {
      this.deleteClicked = false;
      if (par1)
        this.waypointManager.deleteSubworld(this.originalSubworldName);
      getMinecraft().method_1507(this.parent);
    }
  }

  public boolean method_25404(int keysm, int scancode, int b) {
    boolean OK = super.method_25404(keysm, scancode, b);
    boolean acceptable = isNameAcceptable();
    this.doneButton.field_22763 = isNameAcceptable();
    this.deleteButton.field_22763 = this.originalSubworldName.equals(this.subworldNameField.method_1882());
    if ((keysm == 257 || keysm == 335) && acceptable)
      changeNameClicked();
    return OK;
  }

  public boolean method_25400(char character, int keycode) {
    boolean OK = super.method_25400(character, keycode);
    boolean acceptable = isNameAcceptable();
    this.doneButton.field_22763 = isNameAcceptable();
    this.deleteButton.field_22763 = this.originalSubworldName.equals(this.subworldNameField.method_1882());
    if (character == '\r' && acceptable)
      changeNameClicked();
    return OK;
  }

  public boolean method_25402(double mouseX, double mouseY, int par3) {
    this.subworldNameField.method_25402(mouseX, mouseY, par3);
    return super.method_25402(mouseX, mouseY, par3);
  }

  public void method_25394(class_4587 matrixStack, int mouseX, int mouseY, float partialTicks) {
    drawMap(matrixStack);
    method_25420(matrixStack);
    method_25300(matrixStack, getFontRenderer(), I18nUtils.getString("worldmap.subworld.edit", new Object[0]), getWidth() / 2, 20, 16777215);
    method_25303(matrixStack, getFontRenderer(), I18nUtils.getString("worldmap.subworld.name", new Object[0]), getWidth() / 2 - 100, getHeight() / 6, 10526880);
    this.subworldNameField.method_25394(matrixStack, mouseX, mouseY, partialTicks);
    super.method_25394(matrixStack, mouseX, mouseY, partialTicks);
  }

  private boolean isNameAcceptable() {
    boolean acceptable = true;
    this.currentSubworldName = this.subworldNameField.method_1882();
    acceptable = (this.currentSubworldName.length() > 0);
    return (acceptable && (this.currentSubworldName.equals(this.originalSubworldName) || !this.knownSubworldNames.contains(this.currentSubworldName)));
  }
}
