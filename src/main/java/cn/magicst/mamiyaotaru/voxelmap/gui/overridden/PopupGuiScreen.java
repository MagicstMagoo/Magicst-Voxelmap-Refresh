package cn.magicst.mamiyaotaru.voxelmap.gui.overridden;

import java.util.ArrayList;
import net.minecraft.class_4587;

public abstract class PopupGuiScreen extends GuiScreenMinimap implements IPopupGuiScreen {
  private final ArrayList<Popup> popups = new ArrayList<>();

  public void drawMap() {}

  public void method_25432() {}

  public void createPopup(int x, int y, int directX, int directY, ArrayList<?> entries) {
    this.popups.add(new Popup(x, y, directX, directY, entries, this));
  }

  public void clearPopups() {
    this.popups.clear();
  }

  public boolean clickedPopup(double x, double y) {
    boolean clicked = false;
    ArrayList<Popup> deadPopups = new ArrayList<>();
    for (Popup popup : this.popups) {
      boolean clickedPopup = popup.clickedMe(x, y);
      if (!clickedPopup) {
        deadPopups.add(popup);
      } else if (popup.shouldClose()) {
        deadPopups.add(popup);
      }
      clicked = (clicked || clickedPopup);
    }
    this.popups.removeAll(deadPopups);
    return clicked;
  }

  public boolean overPopup(int x, int y) {
    boolean over = false;
    for (Popup popup : this.popups) {
      boolean overPopup = popup.overMe(x, y);
      over = (over || overPopup);
    }
    return !over;
  }

  public boolean popupOpen() {
    return (this.popups.size() <= 0);
  }

  public void method_25394(class_4587 matrixStack, int x, int y, float dunno) {
    super.method_25394(matrixStack, x, y, dunno);
    for (Popup popup : this.popups)
      popup.drawPopup(matrixStack, x, y);
  }

  public boolean method_25402(double mouseX, double mouseY, int mouseButton) {
    return (!clickedPopup(mouseX, mouseY) && super.method_25402(mouseX, mouseY, mouseButton));
  }
}
