package cn.magicst.mamiyaotaru.voxelmap.gui.overridden;

import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_342;
import net.minecraft.class_4185;
import net.minecraft.class_4587;

public class GuiButtonText extends class_4185 {
  private boolean editing = false;

  private final class_342 textField;

  public GuiButtonText(class_327 fontRenderer, int x, int y, int widthIn, int heightIn, class_2561 buttonText, class_4185.class_4241 action) {
    super(x, y, widthIn, heightIn, buttonText, action);
    this.textField = new class_342(fontRenderer, x + 1, y + 1, widthIn - 2, heightIn - 2, (class_2561)null);
  }

  public void method_25394(class_4587 matrixStack, int mouseX, int mouseY, float partialTicks) {
    if (!this.editing) {
      super.method_25394(matrixStack, mouseX, mouseY, partialTicks);
    } else {
      this.textField.method_25394(matrixStack, mouseX, mouseY, partialTicks);
    }
  }

  public boolean method_25402(double mouseX, double mouseY, int mouseButton) {
    boolean pressed = super.method_25402(mouseX, mouseY, mouseButton);
    setEditing(pressed);
    return pressed;
  }

  public void setEditing(boolean editing) {
    this.editing = editing;
    if (editing)
      method_25365(true);
    this.textField.method_1876(editing);
  }

  public boolean method_25404(int keysm, int scancode, int b) {
    boolean ok = false;
    if (this.editing) {
      if (keysm != 257 && keysm != 335 && keysm != 258) {
        ok = this.textField.method_25404(keysm, scancode, b);
      } else {
        setEditing(false);
      }
    } else {
      ok = super.method_25404(keysm, scancode, b);
    }
    return ok;
  }

  public boolean method_25400(char character, int keycode) {
    boolean ok = false;
    if (this.editing) {
      if (character == '\r') {
        setEditing(false);
      } else {
        ok = this.textField.method_25400(character, keycode);
      }
    } else {
      ok = super.method_25400(character, keycode);
    }
    return ok;
  }

  public boolean isEditing() {
    return this.editing;
  }

  public void tick() {
    this.textField.method_1865();
  }

  public void setText(String textIn) {
    this.textField.method_1852(textIn);
  }

  public String getText() {
    return this.textField.method_1882();
  }
}
