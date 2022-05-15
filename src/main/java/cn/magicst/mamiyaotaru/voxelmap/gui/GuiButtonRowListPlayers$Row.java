 package cn.magicst.mamiyaotaru.voxelmap.gui;
 
 import com.mojang.authlib.GameProfile;
 import com.mojang.blaze3d.systems.RenderSystem;
 import net.minecraft.class_1657;
 import net.minecraft.class_1664;
 import net.minecraft.class_2561;
 import net.minecraft.class_2588;
 import net.minecraft.class_310;
 import net.minecraft.class_350;
 import net.minecraft.class_4185;
 import net.minecraft.class_437;
 import net.minecraft.class_4587;
 import net.minecraft.class_640;
 import net.minecraft.class_757;
 
 public class Row
   extends class_350.class_351<GuiButtonRowListPlayers.Row>
 {
   private final class_310 client = class_310.method_1551();
   private class_4185 button = null;
   private class_4185 button1 = null;
   private class_4185 button2 = null;
   private int id = 0;
   private int id1 = 0;
   private int id2 = 0;
   
   public Row(class_4185 button, int id) {
     this.button = button;
     this.id = id;
   }
   
   public Row(class_4185 button1, int id1, class_4185 button2, int id2) {
     this.button1 = button1;
     this.id1 = id1;
     this.button2 = button2;
     this.id2 = id2;
   }
   
   public void method_25343(class_4587 matrixStack, int slotIndex, int y, int x, int listWidth, int itemHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
     drawButton(matrixStack, this.button, this.id, slotIndex, x, y, listWidth, itemHeight, mouseX, mouseY, isSelected, partialTicks);
     drawButton(matrixStack, this.button1, this.id1, slotIndex, x, y, listWidth, itemHeight, mouseX, mouseY, isSelected, partialTicks);
     drawButton(matrixStack, this.button2, this.id2, slotIndex, x, y, listWidth, itemHeight, mouseX, mouseY, isSelected, partialTicks);
   }
   
   private void drawButton(class_4587 matrixStack, class_4185 button, int id, int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
     if (button != null) {
       button.field_22761 = y;
       button.method_25394(matrixStack, mouseX, mouseY, partialTicks);
       if (id != -1) {
         drawIconForButton(matrixStack, button, id);
       }
       
       if (button.method_25367() && mouseY >= GuiButtonRowListPlayers.access$000(GuiButtonRowListPlayers.this) && mouseY <= GuiButtonRowListPlayers.access$100(GuiButtonRowListPlayers.this)) {
         class_2588 class_2588 = new class_2588("minimap.waypointshare.sharewithname", new Object[] { button.method_25369() });
         GuiSelectPlayer.setTooltip(GuiButtonRowListPlayers.this.parentGui, (class_2561)class_2588);
       } 
     } 
   }
 
   
   private void drawIconForButton(class_4587 matrixStack, class_4185 button, int id) {
     class_640 networkPlayerInfo = (class_640)GuiButtonRowListPlayers.this.playersFiltered.get(id);
     GameProfile gameProfile = networkPlayerInfo.method_2966();
     class_1657 entityPlayer = this.client.field_1687.method_18470(gameProfile.getId());
     RenderSystem.setShader(class_757::method_34542);
     RenderSystem.setShaderTexture(0, networkPlayerInfo.method_2968());
     class_437.method_25293(matrixStack, button.field_22760 + 6, button.field_22761 + 6, 8, 8, 8.0F, 8.0F, 8, 8, 64, 64);
     if (entityPlayer != null && entityPlayer.method_7348(class_1664.field_7563)) {
       class_437.method_25293(matrixStack, button.field_22760 + 6, button.field_22761 + 6, 8, 8, 40.0F, 8.0F, 8, 8, 64, 64);
     }
   }
 
   
   public boolean method_25402(double mouseX, double mouseY, int mouseEvent) {
     if (this.button != null && this.button.method_25402(mouseX, mouseY, mouseEvent)) {
       GuiButtonRowListPlayers.this.buttonClicked(this.id);
       return true;
     }  if (this.button1 != null && this.button1.method_25402(mouseX, mouseY, mouseEvent)) {
       GuiButtonRowListPlayers.this.buttonClicked(this.id1);
       return true;
     }  if (this.button2 != null && this.button2.method_25402(mouseX, mouseY, mouseEvent)) {
       GuiButtonRowListPlayers.this.buttonClicked(this.id2);
       return true;
     } 
     return false;
   }
 
   
   public boolean method_25406(double mouseX, double mouseY, int mouseEvent) {
     if (this.button != null) {
       this.button.method_25406(mouseX, mouseY, mouseEvent);
       return true;
     }  if (this.button1 != null) {
       this.button1.method_25406(mouseX, mouseY, mouseEvent);
       return true;
     }  if (this.button2 != null) {
       this.button2.method_25406(mouseX, mouseY, mouseEvent);
       return true;
     } 
     return false;
   }
 }
