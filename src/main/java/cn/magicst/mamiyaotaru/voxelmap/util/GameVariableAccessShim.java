 package cn.magicst.mamiyaotaru.voxelmap.util;
 
 import java.io.File;
 import net.minecraft.class_310;
 import net.minecraft.class_638;
 
 public class GameVariableAccessShim
 {
   private static class_310 minecraft = class_310.method_1551();
   
   public static class_310 getMinecraft() {
     return minecraft;
   }
   
   public static class_638 getWorld() {
     return minecraft.field_1687;
   }
   
   public static File getDataDir() {
     return minecraft.field_1697;
   }
   
   public static int xCoord() {
     return (int)((minecraft.method_1560().method_23317() < 0.0D) ? (minecraft.method_1560().method_23317() - 1.0D) : minecraft.method_1560().method_23317());
   }
   
   public static int zCoord() {
     return (int)((minecraft.method_1560().method_23321() < 0.0D) ? (minecraft.method_1560().method_23321() - 1.0D) : minecraft.method_1560().method_23321());
   }
   
   public static int yCoord() {
     return (int)Math.ceil(minecraft.method_1560().method_23318());
   }
   
   public static double xCoordDouble() {
     return (minecraft.field_1755 != null && minecraft.field_1755.method_25421()) ? minecraft.method_1560().method_23317() : ((minecraft.method_1560()).field_6014 + (minecraft.method_1560().method_23317() - (minecraft.method_1560()).field_6014) * minecraft.method_1488());
   }
   
   public static double zCoordDouble() {
     return (minecraft.field_1755 != null && minecraft.field_1755.method_25421()) ? minecraft.method_1560().method_23321() : ((minecraft.method_1560()).field_5969 + (minecraft.method_1560().method_23321() - (minecraft.method_1560()).field_5969) * minecraft.method_1488());
   }
   
   public static double yCoordDouble() {
     return (minecraft.field_1755 != null && minecraft.field_1755.method_25421()) ? minecraft.method_1560().method_23318() : ((minecraft.method_1560()).field_6036 + (minecraft.method_1560().method_23318() - (minecraft.method_1560()).field_6036) * minecraft.method_1488());
   }
   
   public static float rotationYaw() {
     return (minecraft.method_1560()).field_5982 + (minecraft.method_1560().method_36454() - (minecraft.method_1560()).field_5982) * minecraft.method_1488();
   }
 }

