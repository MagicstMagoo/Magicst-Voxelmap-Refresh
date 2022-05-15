 package cn.magicst.mamiyaotaru.voxelmap.gui.overridden;
 
 import cn.magicst.mamiyaotaru.voxelmap.MapSettingsManager;
 import cn.magicst.mamiyaotaru.voxelmap.VoxelMap;
 import cn.magicst.mamiyaotaru.voxelmap.util.GLShim;
 import java.util.List;
 import net.minecraft.class_2561;
 import net.minecraft.class_2585;
 import net.minecraft.class_310;
 import net.minecraft.class_327;
 import net.minecraft.class_437;
 import net.minecraft.class_4587;
 
 public class GuiScreenMinimap
   extends class_437 {
   protected GuiScreenMinimap() {
     this(new class_2585(""));
   }
   
   protected GuiScreenMinimap(class_2585 textComponent_1) {
     super((class_2561)textComponent_1);
     method_25304(0);
   }
   
   public void drawMap(class_4587 matrixStack) {
     if (!(VoxelMap.instance.getMapOptions()).showUnderMenus) {
       VoxelMap.instance.getMap().drawMinimap(matrixStack, this.field_22787);
       GLShim.glClear(256);
     } 
   }
 
   
   public void method_25432() {
     MapSettingsManager.instance.saveAll();
   }
   
   public void method_25424(class_4587 matrixStack, class_2561 text, int x, int y) {
     if (text != null && text.getString() != null && !text.getString().equals("")) {
       super.method_25424(matrixStack, text, x, y);
     }
   }
 
   
   public class_310 getMinecraft() {
     return this.field_22787;
   }
   
   public int getWidth() {
     return this.field_22789;
   }
   
   public int getHeight() {
     return this.field_22790;
   }
   
   public List<?> getButtonList() {
     return method_25396();
   }
   
   public class_327 getFontRenderer() {
     return this.field_22793;
   }
 }

