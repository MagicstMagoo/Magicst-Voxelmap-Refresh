 package cn.magicst.mamiyaotaru.voxelmap.util;
 
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.AbstractMapData;
 import java.util.Arrays;
 import net.minecraft.class_2680;
 
 public class FullMapData
   extends AbstractMapData {
   public static final int DATABITS = 17;
   public static final int BYTESPERDATUM = 4;
   private static final int HEIGHTPOS = 0;
   private static final int BLOCKSTATEPOS = 1;
   private static final int TINTPOS = 2;
   private static final int LIGHTPOS = 3;
   private static final int OCEANFLOORHEIGHTPOS = 4;
   private static final int OCEANFLOORBLOCKSTATEPOS = 5;
   private static final int OCEANFLOORTINTPOS = 6;
   private static final int OCEANFLOORLIGHTPOS = 7;
   private static final int TRANSPARENTHEIGHTPOS = 8;
   private static final int TRANSPARENTBLOCKSTATEPOS = 9;
   private static final int TRANSPARENTTINTPOS = 10;
   private static final int TRANSPARENTLIGHTPOS = 11;
   private static final int FOLIAGEHEIGHTPOS = 12;
   private static final int FOLIAGEBLOCKSTATEPOS = 13;
   private static final int FOLIAGETINTPOS = 14;
   private static final int FOLIAGELIGHTPOS = 15;
   private static final int BIOMEIDPOS = 16;
   private int[] data;
   
   public FullMapData(int width, int height) {
     this.width = width;
     this.height = height;
     this.data = new int[width * height * 17];
     Arrays.fill(this.data, 0);
   }
   
   public void blank() {
     Arrays.fill(this.data, 0);
   }
 
   
   public int getHeight(int x, int z) {
     return getData(x, z, 0);
   }
   
   public int getBlockstateID(int x, int z) {
     return getData(x, z, 1);
   }
 
   
   public class_2680 getBlockstate(int x, int z) {
     return getStateFromID(getData(x, z, 1));
   }
 
   
   public int getBiomeTint(int x, int z) {
     return getData(x, z, 2);
   }
 
   
   public int getLight(int x, int z) {
     return getData(x, z, 3);
   }
 
   
   public int getOceanFloorHeight(int x, int z) {
     return getData(x, z, 4);
   }
   
   public int getOceanFloorBlockstateID(int x, int z) {
     return getData(x, z, 5);
   }
 
   
   public class_2680 getOceanFloorBlockstate(int x, int z) {
     return getStateFromID(getData(x, z, 5));
   }
 
   
   public int getOceanFloorBiomeTint(int x, int z) {
     return getData(x, z, 6);
   }
 
   
   public int getOceanFloorLight(int x, int z) {
     return getData(x, z, 7);
   }
 
   
   public int getTransparentHeight(int x, int z) {
     return getData(x, z, 8);
   }
   
   public int getTransparentBlockstateID(int x, int z) {
     return getData(x, z, 9);
   }
 
   
   public class_2680 getTransparentBlockstate(int x, int z) {
     return getStateFromID(getData(x, z, 9));
   }
 
   
   public int getTransparentBiomeTint(int x, int z) {
     return getData(x, z, 10);
   }
 
   
   public int getTransparentLight(int x, int z) {
     return getData(x, z, 11);
   }
 
   
   public int getFoliageHeight(int x, int z) {
     return getData(x, z, 12);
   }
   
   public int getFoliageBlockstateID(int x, int z) {
     return getData(x, z, 13);
   }
 
   
   public class_2680 getFoliageBlockstate(int x, int z) {
     return getStateFromID(getData(x, z, 13));
   }
 
   
   public int getFoliageBiomeTint(int x, int z) {
     return getData(x, z, 14);
   }
 
   
   public int getFoliageLight(int x, int z) {
     return getData(x, z, 15);
   }
 
   
   public int getBiomeID(int x, int z) {
     return getData(x, z, 16);
   }
   
   private int getData(int x, int z, int bit) {
     int index = (x + z * this.width) * 17 + bit;
     return this.data[index];
   }
 
   
   public void setHeight(int x, int z, int value) {
     setData(x, z, 0, value);
   }
   
   public void setBlockstateID(int x, int z, int id) {
     setData(x, z, 1, id);
   }
 
   
   public void setBlockstate(int x, int z, class_2680 blockState) {
     setData(x, z, 1, getIDFromState(blockState));
   }
 
   
   public void setBiomeTint(int x, int z, int value) {
     setData(x, z, 2, value);
   }
 
   
   public void setLight(int x, int z, int value) {
     setData(x, z, 3, value);
   }
 
   
   public void setOceanFloorHeight(int x, int z, int value) {
     setData(x, z, 4, value);
   }
   
   public void setOceanFloorBlockstateID(int x, int z, int id) {
     setData(x, z, 5, id);
   }
 
   
   public void setOceanFloorBlockstate(int x, int z, class_2680 blockState) {
     setData(x, z, 5, getIDFromState(blockState));
   }
 
   
   public void setOceanFloorBiomeTint(int x, int z, int value) {
     setData(x, z, 6, value);
   }
 
   
   public void setOceanFloorLight(int x, int z, int value) {
     setData(x, z, 7, value);
   }
 
   
   public void setTransparentHeight(int x, int z, int value) {
     setData(x, z, 8, value);
   }
   
   public void setTransparentBlockstateID(int x, int z, int id) {
     setData(x, z, 9, id);
   }
 
   
   public void setTransparentBlockstate(int x, int z, class_2680 blockState) {
     setData(x, z, 9, getIDFromState(blockState));
   }
 
   
   public void setTransparentBiomeTint(int x, int z, int value) {
     setData(x, z, 10, value);
   }
 
   
   public void setTransparentLight(int x, int z, int value) {
     setData(x, z, 11, value);
   }
 
   
   public void setFoliageHeight(int x, int z, int value) {
     setData(x, z, 12, value);
   }
   
   public void setFoliageBlockstateID(int x, int z, int id) {
     setData(x, z, 13, id);
   }
 
   
   public void setFoliageBlockstate(int x, int z, class_2680 blockState) {
     setData(x, z, 13, getIDFromState(blockState));
   }
 
   
   public void setFoliageBiomeTint(int x, int z, int value) {
     setData(x, z, 14, value);
   }
 
   
   public void setFoliageLight(int x, int z, int value) {
     setData(x, z, 15, value);
   }
 
   
   public void setBiomeID(int x, int z, int value) {
     setData(x, z, 16, value);
   }
   
   private void setData(int x, int z, int bit, int value) {
     int index = (x + z * this.width) * 17 + bit;
     this.data[index] = value;
   }
 
   
   public void moveX(int offset) {
     synchronized (this.dataLock) {
       if (offset > 0) {
         System.arraycopy(this.data, offset * 17, this.data, 0, this.data.length - offset * 17);
       } else if (offset < 0) {
         System.arraycopy(this.data, 0, this.data, -offset * 17, this.data.length + offset * 17);
       } 
     } 
   }
 
 
   
   public void moveZ(int offset) {
     synchronized (this.dataLock) {
       if (offset > 0) {
         System.arraycopy(this.data, offset * this.width * 17, this.data, 0, this.data.length - offset * this.width * 17);
       } else if (offset < 0) {
         System.arraycopy(this.data, 0, this.data, -offset * this.width * 17, this.data.length + offset * this.width * 17);
       } 
     } 
   }
 
   
   public void setData(int[] is) {
     this.data = is;
   }
   
   public int[] getData() {
     return this.data;
   }
   
   private int getIDFromState(class_2680 blockState) {
     return BlockRepository.getStateId(blockState);
   }
   
   private class_2680 getStateFromID(int id) {
     return BlockRepository.getStateById(id);
   }
 }

