 package cn.magicst.mamiyaotaru.voxelmap.persistent;
 
 import cn.magicst.mamiyaotaru.voxelmap.gui.GuiMinimapOptions;
 import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.IPopupGuiScreen;
 import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.PopupGuiButton;
 import net.minecraft.class_2561;
 import net.minecraft.class_4185;
 import net.minecraft.class_437;
 
 class null
   extends PopupGuiButton
 {
   null(int x, int y, int widthIn, int heightIn, class_2561 buttonText, class_4185.class_4241 pressAction, IPopupGuiScreen parentScreen) {
     super(x, y, widthIn, heightIn, buttonText, pressAction, parentScreen);
   } public void method_25306() {
     GuiPersistentMap.this.getMinecraft().method_1507((class_437)new GuiMinimapOptions((class_437)GuiPersistentMap.this, GuiPersistentMap.this.master));
   }
 }

