 package cn.magicst.mamiyaotaru.voxelmap.util;
 
 import net.minecraft.class_2338;
 import net.minecraft.class_2350;
 import net.minecraft.class_2382;
 
 public class MutableBlockPos extends class_2338 {
   public int x;
   
   public MutableBlockPos(int x, int y, int z) {
     super(0, 0, 0);
     this.x = x;
     this.y = y;
     this.z = z;
   }
   public int y; public int z;
   public MutableBlockPos withXYZ(int x, int y, int z) {
     this.x = x;
     this.y = y;
     this.z = z;
     return this;
   }
   
   public void setXYZ(int x, int y, int z) {
     this.x = x;
     this.y = y;
     this.z = z;
   }
   
   public int method_10263() {
     return this.x;
   }
   
   public int method_10264() {
     return this.y;
   }
   
   public int method_10260() {
     return this.z;
   }
 }
