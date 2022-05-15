 package cn.magicst.mamiyaotaru.voxelmap.gui.overridden;
 
 import net.minecraft.class_2561;
 import net.minecraft.class_4185;
 import net.minecraft.class_4587;
 
 public class PopupGuiButton extends class_4185 {
   IPopupGuiScreen parentScreen;
   
   public PopupGuiButton(int x, int y, int widthIn, int heightIn, class_2561 buttonText, class_4185.class_4241 pressAction, IPopupGuiScreen parentScreen) {
     super(x, y, widthIn, heightIn, buttonText, pressAction);
     this.parentScreen = parentScreen;
   }
   
   public void method_25394(class_4587 matrixStack, int mouseX, int mouseY, float partialTicks) {
     boolean canHover = this.parentScreen.overPopup(mouseX, mouseY);
     if (!canHover) {
       mouseX = 0;
       mouseY = 0;
     } 
     
     super.method_25394(matrixStack, mouseX, mouseY, partialTicks);
   }
 }

