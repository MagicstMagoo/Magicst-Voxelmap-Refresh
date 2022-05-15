 package cn.magicst.mamiyaotaru.voxelmap.util;
 
 import net.minecraft.class_1011;
 import net.minecraft.class_1043;
 import org.lwjgl.system.MemoryUtil;
 
 public class MutableNativeImageBackedTexture extends class_1043 {
   private Object bufferLock = new Object();
   private class_1011 image;
   private long pointer;
   
   public MutableNativeImageBackedTexture(class_1011 image) {
     super(image);
     this.image = image;
     String info = image.toString();
     String pointerString = info.substring(info.indexOf("(") + 1, info.indexOf("]") - 1);
     this.pointer = Long.parseLong(pointerString);
   }
   
   public MutableNativeImageBackedTexture(int width, int height, boolean b) {
     super(width, height, b);
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
     synchronized (this.bufferLock) {
       int size = this.image.method_4323() * this.image.method_4307() * 4;
       if (offset > 0) {
         MemoryUtil.memCopy(this.pointer + (offset * 4), this.pointer, (size - offset * 4));
       } else if (offset < 0) {
         MemoryUtil.memCopy(this.pointer, this.pointer - (offset * 4), (size + offset * 4));
       } 
     } 
   }
 
   
   public void moveY(int offset) {
     synchronized (this.bufferLock) {
       int size = this.image.method_4323() * this.image.method_4307() * 4;
       int width = this.image.method_4307();
       if (offset > 0) {
         MemoryUtil.memCopy(this.pointer + (offset * width * 4), this.pointer, (size - offset * width * 4));
       } else if (offset < 0) {
         MemoryUtil.memCopy(this.pointer, this.pointer - (offset * width * 4), (size + offset * width * 4));
       } 
     } 
   }
 
   
   public void setRGB(int x, int y, int color24) {
     int alpha = color24 >> 24 & 0xFF;
     byte a = -1;
     byte r = (byte)((color24 >> 0 & 0xFF) * alpha / 255);
     byte g = (byte)((color24 >> 8 & 0xFF) * alpha / 255);
     byte b = (byte)((color24 >> 16 & 0xFF) * alpha / 255);
     int color = (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
     this.image.method_4305(x, y, color);
   }
 }
 