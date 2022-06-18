package cn.magicst.mamiyaotaru.voxelmap.gui.overridden;

import cn.magicst.mamiyaotaru.voxelmap.util.GLShim;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_293;
import net.minecraft.class_310;
import net.minecraft.class_350;
import net.minecraft.class_3532;
import net.minecraft.class_364;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import net.minecraft.class_6382;
import net.minecraft.class_757;
import org.jetbrains.annotations.Nullable;

public abstract class GuiSlotMinimap extends class_350 {
  protected int slotWidth = 220;

  protected boolean centerListVertically = true;

  private boolean showTopBottomBG = true;

  private boolean showSlotBG = true;

  private boolean hasListHeader;

  protected int headerPadding;

  protected long lastClicked = 0L;

  public boolean doubleclick = false;

  public GuiSlotMinimap(class_310 par1Minecraft, int width, int height, int y1, int y2, int slotHeight) {
    super(par1Minecraft, width, height, y1, y2, slotHeight);
    method_25304(0);
  }

  public void setShowTopBottomBG(boolean showTopBottomBG) {
    this.showTopBottomBG = showTopBottomBG;
  }

  public void setShowSlotBG(boolean showSlotBG) {
    this.showSlotBG = showSlotBG;
  }

  public void method_25394(class_4587 matrixStack, int mouseX, int mouseY, float partialTicks) {
    method_25325(matrixStack);
    int scrollBarLeft = method_25329();
    int scrollBarRight = scrollBarLeft + 6;
    method_25307(method_25341());
    GLShim.glDisable(2896);
    class_289 tessellator = class_289.method_1348();
    class_287 vertexBuffer = tessellator.method_1349();
    if (this.showSlotBG) {
      RenderSystem.setShader(class_757::method_34543);
      RenderSystem.setShaderTexture(0, class_437.field_22735);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      float f = 32.0F;
      vertexBuffer.method_1328(class_293.class_5596.field_27382, class_290.field_1575);
      vertexBuffer.method_22912(this.field_19088, this.field_19086, 0.0D).method_22913(this.field_19088 / f, (this.field_19086 + (int)method_25341()) / f).method_1336(32, 32, 32, 255).method_1344();
      vertexBuffer.method_22912(this.field_19087, this.field_19086, 0.0D).method_22913(this.field_19087 / f, (this.field_19086 + (int)method_25341()) / f).method_1336(32, 32, 32, 255).method_1344();
      vertexBuffer.method_22912(this.field_19087, this.field_19085, 0.0D).method_22913(this.field_19087 / f, (this.field_19085 + (int)method_25341()) / f).method_1336(32, 32, 32, 255).method_1344();
      vertexBuffer.method_22912(this.field_19088, this.field_19085, 0.0D).method_22913(this.field_19088 / f, (this.field_19085 + (int)method_25341()) / f).method_1336(32, 32, 32, 255).method_1344();
      tessellator.method_1350();
    }
    int leftEdge = this.field_19088 + this.field_22742 / 2 - method_25322() / 2 + 2;
    int topOfListYPos = this.field_19085 + 4 - (int)method_25341();
    if (this.hasListHeader)
      method_25312(matrixStack, leftEdge, topOfListYPos, tessellator);
    method_25311(matrixStack, leftEdge, topOfListYPos, mouseX, mouseY, partialTicks);
    GLShim.glDisable(2929);
    byte topBottomFadeHeight = 4;
    if (this.showTopBottomBG) {
      RenderSystem.setShader(class_757::method_34543);
      RenderSystem.setShaderTexture(0, class_437.field_22735);
      RenderSystem.enableDepthTest();
      RenderSystem.depthFunc(519);
      vertexBuffer.method_1328(class_293.class_5596.field_27382, class_290.field_1575);
      vertexBuffer.method_22912(this.field_19088, this.field_19085, -100.0D).method_22913(0.0F, this.field_19085 / 32.0F).method_1336(64, 64, 64, 255).method_1344();
      vertexBuffer.method_22912((this.field_19088 + this.field_22742), this.field_19085, -100.0D).method_22913(this.field_22742 / 32.0F, this.field_19085 / 32.0F).method_1336(64, 64, 64, 255).method_1344();
      vertexBuffer.method_22912((this.field_19088 + this.field_22742), 0.0D, -100.0D).method_22913(this.field_22742 / 32.0F, 0.0F).method_1336(64, 64, 64, 255).method_1344();
      vertexBuffer.method_22912(this.field_19088, 0.0D, -100.0D).method_22913(0.0F, 0.0F).method_1336(64, 64, 64, 255).method_1344();
      vertexBuffer.method_22912(this.field_19088, this.field_22743, -100.0D).method_22913(0.0F, this.field_22743 / 32.0F).method_1336(64, 64, 64, 255).method_1344();
      vertexBuffer.method_22912((this.field_19088 + this.field_22742), this.field_22743, -100.0D).method_22913(this.field_22742 / 32.0F, this.field_22743 / 32.0F).method_1336(64, 64, 64, 255).method_1344();
      vertexBuffer.method_22912((this.field_19088 + this.field_22742), this.field_19086, -100.0D).method_22913(this.field_22742 / 32.0F, this.field_19086 / 32.0F).method_1336(64, 64, 64, 255).method_1344();
      vertexBuffer.method_22912(this.field_19088, this.field_19086, -100.0D).method_22913(0.0F, this.field_19086 / 32.0F).method_1336(64, 64, 64, 255).method_1344();
      tessellator.method_1350();
      RenderSystem.depthFunc(515);
      RenderSystem.disableDepthTest();
      GLShim.glEnable(3042);
      RenderSystem.blendFuncSeparate(770, 771, 0, 1);
      GLShim.glDisable(3553);
      RenderSystem.setShader(class_757::method_34543);
      RenderSystem.setShaderTexture(0, field_22735);
      vertexBuffer.method_1328(class_293.class_5596.field_27382, class_290.field_1575);
      vertexBuffer.method_22912(this.field_19088, (this.field_19085 + topBottomFadeHeight), 0.0D).method_22913(0.0F, 1.0F).method_1336(0, 0, 0, 0).method_1344();
      vertexBuffer.method_22912(this.field_19087, (this.field_19085 + topBottomFadeHeight), 0.0D).method_22913(1.0F, 1.0F).method_1336(0, 0, 0, 0).method_1344();
      vertexBuffer.method_22912(this.field_19087, this.field_19085, 0.0D).method_22913(1.0F, 0.0F).method_1336(0, 0, 0, 255).method_1344();
      vertexBuffer.method_22912(this.field_19088, this.field_19085, 0.0D).method_22913(0.0F, 0.0F).method_1336(0, 0, 0, 255).method_1344();
      vertexBuffer.method_22912(this.field_19088, this.field_19086, 0.0D).method_22913(0.0F, 1.0F).method_1336(0, 0, 0, 255).method_1344();
      vertexBuffer.method_22912(this.field_19087, this.field_19086, 0.0D).method_22913(1.0F, 1.0F).method_1336(0, 0, 0, 255).method_1344();
      vertexBuffer.method_22912(this.field_19087, (this.field_19086 - topBottomFadeHeight), 0.0D).method_22913(1.0F, 0.0F).method_1336(0, 0, 0, 0).method_1344();
      vertexBuffer.method_22912(this.field_19088, (this.field_19086 - topBottomFadeHeight), 0.0D).method_22913(0.0F, 0.0F).method_1336(0, 0, 0, 0).method_1344();
      tessellator.method_1350();
    }
    int maxScroll = method_25331();
    if (maxScroll > 0) {
      GLShim.glDisable(3553);
      RenderSystem.disableTexture();
      RenderSystem.setShader(class_757::method_34540);
      int k1 = (this.field_19086 - this.field_19085) * (this.field_19086 - this.field_19085) / method_25317();
      k1 = class_3532.method_15340(k1, 32, this.field_19086 - this.field_19085 - 8);
      int l1 = (int)method_25341() * (this.field_19086 - this.field_19085 - k1) / maxScroll + this.field_19085;
      if (l1 < this.field_19085)
        l1 = this.field_19085;
      vertexBuffer.method_1328(class_293.class_5596.field_27382, class_290.field_1576);
      vertexBuffer.method_22912(scrollBarLeft, this.field_19086, 0.0D).method_1336(0, 0, 0, 255).method_1344();
      vertexBuffer.method_22912(scrollBarRight, this.field_19086, 0.0D).method_1336(0, 0, 0, 255).method_1344();
      vertexBuffer.method_22912(scrollBarRight, this.field_19085, 0.0D).method_1336(0, 0, 0, 255).method_1344();
      vertexBuffer.method_22912(scrollBarLeft, this.field_19085, 0.0D).method_1336(0, 0, 0, 255).method_1344();
      vertexBuffer.method_22912(scrollBarLeft, (l1 + k1), 0.0D).method_1336(128, 128, 128, 255).method_1344();
      vertexBuffer.method_22912(scrollBarRight, (l1 + k1), 0.0D).method_1336(128, 128, 128, 255).method_1344();
      vertexBuffer.method_22912(scrollBarRight, l1, 0.0D).method_1336(128, 128, 128, 255).method_1344();
      vertexBuffer.method_22912(scrollBarLeft, l1, 0.0D).method_1336(128, 128, 128, 255).method_1344();
      vertexBuffer.method_22912(scrollBarLeft, (l1 + k1 - 1), 0.0D).method_1336(192, 192, 192, 255).method_1344();
      vertexBuffer.method_22912((scrollBarRight - 1), (l1 + k1 - 1), 0.0D).method_1336(192, 192, 192, 255).method_1344();
      vertexBuffer.method_22912((scrollBarRight - 1), l1, 0.0D).method_1336(192, 192, 192, 255).method_1344();
      vertexBuffer.method_22912(scrollBarLeft, l1, 0.0D).method_1336(192, 192, 192, 255).method_1344();
      tessellator.method_1350();
    }
    method_25320(matrixStack, mouseX, mouseY);
    GLShim.glEnable(3553);
    GLShim.glDisable(3042);
  }

  public int method_25322() {
    return this.slotWidth;
  }

  public void setSlotWidth(int slotWidth) {
    this.slotWidth = slotWidth;
  }

  protected int method_25329() {
    return (this.slotWidth >= 220) ? (this.field_22742 / 2 + 124) : (this.field_19087 - 6);
  }

  public void method_25333(int x1) {
    this.field_19088 = x1;
    this.field_19087 = x1 + this.field_22742;
  }

  public boolean method_25402(double mouseX, double mouseY, int mouseButton) {
    this.doubleclick = (System.currentTimeMillis() - this.lastClicked < 250L);
    this.lastClicked = System.currentTimeMillis();
    return super.method_25402(mouseX, mouseY, mouseButton);
  }

  public void method_37020(class_6382 builder) {}
}
