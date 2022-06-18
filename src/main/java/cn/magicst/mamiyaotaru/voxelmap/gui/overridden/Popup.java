package cn.magicst.mamiyaotaru.voxelmap.gui.overridden;

import cn.magicst.mamiyaotaru.voxelmap.util.GLShim;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_293;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import net.minecraft.class_757;

public class Popup {
  class_310 client;

  class_327 fontRendererObj;

  int x;

  int y;

  PopupEntry[] entries;

  int w;

  int h;

  public int clickedX;

  public int clickedY;

  public int clickedDirectX;

  public int clickedDirectY;

  boolean shouldClose = false;

  PopupGuiScreen parentGui;

  int padding = 6;

  public Popup(int x, int y, int directX, int directY, ArrayList<?> entries, PopupGuiScreen parentGui) {
    this.client = class_310.method_1551();
    this.fontRendererObj = this.client.field_1772;
    this.parentGui = parentGui;
    this.clickedX = x;
    this.clickedY = y;
    this.clickedDirectX = directX;
    this.clickedDirectY = directY;
    this.x = x - 1;
    this.y = y - 1;
    this.entries = new PopupEntry[entries.size()];
    entries.toArray(this.entries);
    this.w = 0;
    this.h = this.entries.length * 20;
    for (PopupEntry entry : this.entries) {
      int entryWidth = this.fontRendererObj.method_1727(entry.name);
      if (entryWidth > this.w)
        this.w = entryWidth;
    }
    this.w += this.padding * 2;
    if (x + this.w > parentGui.field_22789)
      this.x = x - this.w + 2;
    if (y + this.h > parentGui.field_22790)
      this.y = y - this.h + 2;
  }

  public boolean clickedMe(double mouseX, double mouseY) {
    boolean clicked = (mouseX > this.x && mouseX < (this.x + this.w) && mouseY > this.y && mouseY < (this.y + this.h));
    if (clicked)
      for (int t = 0; t < this.entries.length; t++) {
        if ((this.entries[t]).enabled) {
          boolean entryClicked = (mouseX >= this.x && mouseX <= (this.x + this.w) && mouseY >= (this.y + t * 20) && mouseY <= (this.y + (t + 1) * 20));
          if (entryClicked) {
            this.shouldClose = (this.entries[t]).causesClose;
            this.parentGui.popupAction(this, (this.entries[t]).action);
          }
        }
      }
    return clicked;
  }

  public boolean overMe(int x, int y) {
    return (x > this.x && x < this.x + this.w && y > this.y && y < this.y + this.h);
  }

  public boolean shouldClose() {
    return this.shouldClose;
  }

  public void drawPopup(class_4587 matrixStack, int mouseX, int mouseY) {
    class_289 tessellator = class_289.method_1348();
    class_287 vertexBuffer = tessellator.method_1349();
    GLShim.glDisable(2929);
    RenderSystem.setShader(class_757::method_34543);
    RenderSystem.setShaderTexture(0, class_437.field_22735);
    GLShim.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    float var6 = 32.0F;
    vertexBuffer.method_1328(class_293.class_5596.field_27382, class_290.field_1575);
    vertexBuffer.method_22912(this.x, (this.y + this.h), 0.0D).method_22913(this.x / var6, this.y / var6).method_1336(64, 64, 64, 255).method_1344();
    vertexBuffer.method_22912((this.x + this.w), (this.y + this.h), 0.0D).method_22913((this.x + this.w) / var6, this.y / var6).method_1336(64, 64, 64, 255).method_1344();
    vertexBuffer.method_22912((this.x + this.w), this.y, 0.0D).method_22913((this.x + this.w) / var6, (this.y + this.h) / var6).method_1336(64, 64, 64, 255).method_1344();
    vertexBuffer.method_22912(this.x, this.y, 0.0D).method_22913(this.x / var6, (this.y + this.h) / var6).method_1336(64, 64, 64, 255).method_1344();
    tessellator.method_1350();
    GLShim.glEnable(3042);
    GLShim.glBlendFunc(770, 771);
    RenderSystem.setShader(class_757::method_34540);
    GLShim.glDisable(3553);
    byte fadeWidth = 4;
    vertexBuffer.method_1328(class_293.class_5596.field_27382, class_290.field_1576);
    vertexBuffer.method_22912(this.x, (this.y + 4), 0.0D).method_1336(0, 0, 0, 0).method_1344();
    vertexBuffer.method_22912((this.x + this.w), (this.y + 4), 0.0D).method_1336(0, 0, 0, 0).method_1344();
    vertexBuffer.method_22912((this.x + this.w), this.y, 0.0D).method_1336(0, 0, 0, 255).method_1344();
    vertexBuffer.method_22912(this.x, this.y, 0.0D).method_1336(0, 0, 0, 255).method_1344();
    vertexBuffer.method_22912(this.x, (this.y + this.h), 0.0D).method_1336(0, 0, 0, 255).method_1344();
    vertexBuffer.method_22912((this.x + this.w), (this.y + this.h), 0.0D).method_1336(0, 0, 0, 255).method_1344();
    vertexBuffer.method_22912((this.x + this.w), (this.y + this.h - 4), 0.0D).method_1336(0, 0, 0, 0).method_1344();
    vertexBuffer.method_22912(this.x, (this.y + this.h - 4), 0.0D).method_1336(0, 0, 0, 0).method_1344();
    vertexBuffer.method_22912(this.x, this.y, 0.0D).method_1336(0, 0, 0, 255).method_1344();
    vertexBuffer.method_22912(this.x, (this.y + this.h), 0.0D).method_1336(0, 0, 0, 255).method_1344();
    vertexBuffer.method_22912((this.x + 4), (this.y + this.h), 0.0D).method_1336(0, 0, 0, 0).method_1344();
    vertexBuffer.method_22912((this.x + 4), this.y, 0.0D).method_1336(0, 0, 0, 0).method_1344();
    vertexBuffer.method_22912((this.x + this.w - 4), this.y, 0.0D).method_1336(0, 0, 0, 0).method_1344();
    vertexBuffer.method_22912((this.x + this.w - 4), (this.y + this.h), 0.0D).method_1336(0, 0, 0, 0).method_1344();
    vertexBuffer.method_22912((this.x + this.w), (this.y + this.h), 0.0D).method_1336(0, 0, 0, 255).method_1344();
    vertexBuffer.method_22912((this.x + this.w), this.y, 0.0D).method_1336(0, 0, 0, 255).method_1344();
    tessellator.method_1350();
    vertexBuffer.method_1328(class_293.class_5596.field_27382, class_290.field_1576);
    vertexBuffer.method_22912((this.x + this.w - 4), this.y, 0.0D).method_1336(0, 0, 0, 0).method_1344();
    vertexBuffer.method_22912((this.x + this.w - 4), (this.y + this.h), 0.0D).method_1336(0, 0, 0, 0).method_1344();
    vertexBuffer.method_22912((this.x + this.w), (this.y + this.h), 0.0D).method_1336(0, 0, 0, 255).method_1344();
    vertexBuffer.method_22912((this.x + this.w), this.y, 0.0D).method_1336(0, 0, 0, 255).method_1344();
    tessellator.method_1350();
    GLShim.glEnable(3553);
    RenderSystem.setShader(class_757::method_34542);
    GLShim.glDisable(3042);
    for (int t = 0; t < this.entries.length; t++) {
      int color = !(this.entries[t]).enabled ? 10526880 : ((mouseX >= this.x && mouseX <= this.x + this.w && mouseY >= this.y + t * 20 && mouseY <= this.y + (t + 1) * 20) ? 16777120 : 14737632);
      this.fontRendererObj.method_1720(matrixStack, (this.entries[t]).name, (this.x + this.padding), (this.y + this.padding + t * 20), color);
    }
  }

  public static class PopupEntry {
    public String name;

    public int action;

    boolean causesClose;

    boolean enabled;

    public PopupEntry(String name, int action, boolean causesClose, boolean enabled) {
      this.name = name;
      this.action = action;
      this.causesClose = causesClose;
      this.enabled = enabled;
    }
  }
}
