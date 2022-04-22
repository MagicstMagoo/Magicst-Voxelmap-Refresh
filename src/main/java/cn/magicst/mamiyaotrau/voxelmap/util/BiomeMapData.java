 package cn.magicst.mamiyaotaru.voxelmap.util;
 
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.AbstractMapData;
 import java.util.Arrays;
 import net.minecraft.class_2680;
 
 public class BiomeMapData
   extends AbstractMapData {
   public static final int DATABITS = 1;
   public static final int BYTESPERDATUM = 4;
   private static final int BIOMEIDPOS = 0;
   private int[] data;
   
   public BiomeMapData(int width, int height) {
     this.width = width;
     this.height = height;
     this.data = new int[width * height * 1];
     Arrays.fill(this.data, 0);
   }
 
   
   public int getHeight(int x, int z) {
     return 0;
   }
 
   
   public class_2680 getBlockstate(int x, int z) {
     return null;
   }
 
   
   public int getBiomeTint(int x, int z) {
     return 0;
   }
 
   
   public int getLight(int x, int z) {
     return 0;
   }
 
   
   public int getOceanFloorHeight(int x, int z) {
     return 0;
   }
 
   
   public class_2680 getOceanFloorBlockstate(int x, int z) {
     return null;
   }
 
   
   public int getOceanFloorBiomeTint(int x, int z) {
     return 0;
   }
 
   
   public int getOceanFloorLight(int x, int z) {
     return 0;
   }
 
   
   public int getTransparentHeight(int x, int z) {
     return 0;
   }
 
   
   public class_2680 getTransparentBlockstate(int x, int z) {
     return null;
   }
 
   
   public int getTransparentBiomeTint(int x, int z) {
     return 0;
   }
 
   
   public int getTransparentLight(int x, int z) {
     return 0;
   }
 
   
   public int getFoliageHeight(int x, int z) {
     return 0;
   }
 
   
   public class_2680 getFoliageBlockstate(int x, int z) {
     return null;
   }
 
   
   public int getFoliageBiomeTint(int x, int z) {
     return 0;
   }
 
   
   public int getFoliageLight(int x, int z) {
     return 0;
   }
 
   
   public int getBiomeID(int x, int z) {
     return getData(x, z, 0);
   }
   
   private int getData(int x, int z, int bit) {
     int index = (x + z * this.width) * 1 + bit;
     return this.data[index];
   }
 
 
   
   public void setHeight(int x, int z, int value) {}
 
 
   
   public void setBlockstate(int x, int z, class_2680 blockState) {}
 
 
   
   public void setBiomeTint(int x, int z, int value) {}
 
 
   
   public void setLight(int x, int z, int value) {}
 
 
   
   public void setOceanFloorHeight(int x, int z, int value) {}
 
 
   
   public void setOceanFloorBlockstate(int x, int z, class_2680 blockState) {}
 
 
   
   public void setOceanFloorBiomeTint(int x, int z, int value) {}
 
 
   
   public void setOceanFloorLight(int x, int z, int value) {}
 
 
   
   public void setTransparentHeight(int x, int z, int value) {}
 
 
   
   public void setTransparentBlockstate(int x, int z, class_2680 blockState) {}
 
 
   
   public void setTransparentBiomeTint(int x, int z, int value) {}
 
 
   
   public void setTransparentLight(int x, int z, int value) {}
 
 
   
   public void setFoliageHeight(int x, int z, int value) {}
 
 
   
   public void setFoliageBlockstate(int x, int z, class_2680 blockState) {}
 
 
   
   public void setFoliageBiomeTint(int x, int z, int value) {}
 
 
   
   public void setFoliageLight(int x, int z, int value) {}
 
   
   public void setBiomeID(int x, int z, int value) {
     setData(x, z, 0, value);
   }
   
   private void setData(int x, int z, int bit, int value) {
     int index = (x + z * this.width) * 1 + bit;
     this.data[index] = value;
   }
 
   
   public void moveX(int offset) {
     synchronized (this.dataLock) {
       if (offset > 0) {
         System.arraycopy(this.data, offset * 1, this.data, 0, this.data.length - offset * 1);
       } else if (offset < 0) {
         System.arraycopy(this.data, 0, this.data, -offset * 1, this.data.length + offset * 1);
       } 
     } 
   }
 
 
   
   public void moveZ(int offset) {
     synchronized (this.dataLock) {
       if (offset > 0) {
         System.arraycopy(this.data, offset * this.width * 1, this.data, 0, this.data.length - offset * this.width * 1);
       } else if (offset < 0) {
         System.arraycopy(this.data, 0, this.data, -offset * this.width * 1, this.data.length + offset * this.width * 1);
       } 
     } 
   }
 
   
   public void setData(int[] is) {
     this.data = is;
   }
   
   public int[] getData() {
     return this.data;
   }
 }

