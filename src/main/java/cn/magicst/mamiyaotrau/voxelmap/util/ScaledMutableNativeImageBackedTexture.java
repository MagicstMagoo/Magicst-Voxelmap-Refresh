 package cn.magicst.mamiyaotaru.voxelmap.util;
 
 import net.minecraft.class_1011;
 
 public class ScaledMutableNativeImageBackedTexture extends MutableNativeImageBackedTexture {
   private Object bufferLock = new Object();
   private class_1011 image;
   private long pointer;
   private int scale;
   
   public ScaledMutableNativeImageBackedTexture(int width, int height, boolean b) {
     super(512, 512, b);
     this.scale = 512 / width;
     this.image = method_4525();
     String info = this.image.toString();
     String pointerString = info.substring(info.indexOf("@") + 1, info.indexOf("]") - 1);
     this.pointer = Long.parseLong(pointerString);
   }
 
 
   
   public void blank() {}
 
   
   public void write() {
     method_4524();
   }
 
   
   public int getWidth() {
     return this.image.method_4323();
   }
 
   
   public int getHeight() {
     return this.image.method_4323();
   }
 
   
   public int getIndex() {
     return method_4624();
   }
 
   
   public void moveX(int offset) {
     super.moveX(offset * this.scale);
   }
 
   
   public void moveY(int offset) {
     super.moveY(offset * this.scale);
   }
 
   
   public void setRGB(int x, int y, int color24) {
     int alpha = color24 >> 24 & 0xFF;
     byte a = -1;
     byte r = (byte)((color24 >> 0 & 0xFF) * alpha / 255);
     byte g = (byte)((color24 >> 8 & 0xFF) * alpha / 255);
     byte b = (byte)((color24 >> 16 & 0xFF) * alpha / 255);
     int color = (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
     
     for (int t = 0; t < this.scale; t++) {
       for (int s = 0; s < this.scale; s++)
         this.image.method_4305(x * this.scale + t, y * this.scale + s, color); 
     } 
   }
 }


