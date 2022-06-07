 package cn.magicst.mamiyaotaru.voxelmap.persistent;
 
 import com.google.common.collect.BiMap;
 import com.google.common.collect.HashBiMap;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.AbstractMapData;
 import cn.magicst.mamiyaotaru.voxelmap.util.CompressionUtils;
 import java.io.IOException;
 import java.util.Arrays;
 import java.util.zip.DataFormatException;
 import net.minecraft.class_2680;
 
 public class CompressibleMapData
   extends AbstractMapData {
   public static final int DATABITS = 18;
   public static final int BYTESPERDATUM = 1;
   private static final int HEIGHTPOS = 0;
   private static final int BLOCKSTATEPOS = 1;
   private static final int LIGHTPOS = 3;
   private static final int OCEANFLOORHEIGHTPOS = 4;
   private static final int OCEANFLOORBLOCKSTATEPOS = 5;
   private static final int OCEANFLOORLIGHTPOS = 7;
   private static final int TRANSPARENTHEIGHTPOS = 8;
   private static final int TRANSPARENTBLOCKSTATEPOS = 9;
   private static final int TRANSPARENTLIGHTPOS = 11;
   private static final int FOLIAGEHEIGHTPOS = 12;
   private static final int FOLIAGEBLOCKSTATEPOS = 13;
   private static final int FOLIAGELIGHTPOS = 15;
   private static final int BIOMEIDPOS = 16;
   private byte[] data;
   private boolean isCompressed = false;
   private BiMap stateToInt = null;
   int count = 1;
   private static byte[] compressedEmptyData = new byte[1179648];
   
   public CompressibleMapData(int width, int height) {
     this.width = width;
     this.height = height;
     this.data = compressedEmptyData;
     this.isCompressed = true;
   }
 
   
   public int getHeight(int x, int z) {
     return getData(x, z, 0) & 0xFF;
   }
 
   
   public class_2680 getBlockstate(int x, int z) {
     int id = (getData(x, z, 1) & 0xFF) << 8 | getData(x, z, 2) & 0xFF;
     return getStateFromID(id);
   }
 
   
   public int getBiomeTint(int x, int z) {
     return 0;
   }
 
   
   public int getLight(int x, int z) {
     return getData(x, z, 3) & 0xFF;
   }
 
   
   public int getOceanFloorHeight(int x, int z) {
     return getData(x, z, 4) & 0xFF;
   }
 
   
   public class_2680 getOceanFloorBlockstate(int x, int z) {
     int id = (getData(x, z, 5) & 0xFF) << 8 | getData(x, z, 6) & 0xFF;
     return getStateFromID(id);
   }
 
   
   public int getOceanFloorBiomeTint(int x, int z) {
     return 0;
   }
 
   
   public int getOceanFloorLight(int x, int z) {
     return getData(x, z, 7) & 0xFF;
   }
 
   
   public int getTransparentHeight(int x, int z) {
     return getData(x, z, 8) & 0xFF;
   }
 
   
   public class_2680 getTransparentBlockstate(int x, int z) {
     int id = (getData(x, z, 9) & 0xFF) << 8 | getData(x, z, 10) & 0xFF;
     return getStateFromID(id);
   }
 
   
   public int getTransparentBiomeTint(int x, int z) {
     return 0;
   }
 
   
   public int getTransparentLight(int x, int z) {
     return getData(x, z, 11) & 0xFF;
   }
 
   
   public int getFoliageHeight(int x, int z) {
     return getData(x, z, 12) & 0xFF;
   }
 
   
   public class_2680 getFoliageBlockstate(int x, int z) {
     int id = (getData(x, z, 13) & 0xFF) << 8 | getData(x, z, 14) & 0xFF;
     return getStateFromID(id);
   }
 
   
   public int getFoliageBiomeTint(int x, int z) {
     return 0;
   }
 
   
   public int getFoliageLight(int x, int z) {
     return getData(x, z, 15) & 0xFF;
   }
 
   
   public int getBiomeID(int x, int z) {
     return (getData(x, z, 16) & 0xFF) << 8 | getData(x, z, 17) & 0xFF;
   }
   
   private synchronized byte getData(int x, int z, int bit) {
     if (this.isCompressed) {
       decompress();
     }
     
     int index = x + z * this.width + this.width * this.height * bit;
     return this.data[index];
   }
 
   
   public void setHeight(int x, int z, int value) {
     setData(x, z, 0, (byte)value);
   }
 
   
   public void setBlockstate(int x, int z, class_2680 blockState) {
     int id = getIDFromState(blockState);
     setData(x, z, 1, (byte)(id >> 8));
     setData(x, z, 2, (byte)id);
   }
 
 
   
   public void setBiomeTint(int x, int z, int value) {}
 
   
   public void setLight(int x, int z, int value) {
     setData(x, z, 3, (byte)value);
   }
 
   
   public void setOceanFloorHeight(int x, int z, int value) {
     setData(x, z, 4, (byte)value);
   }
 
   
   public void setOceanFloorBlockstate(int x, int z, class_2680 blockState) {
     int id = getIDFromState(blockState);
     setData(x, z, 5, (byte)(id >> 8));
     setData(x, z, 6, (byte)id);
   }
 
 
   
   public void setOceanFloorBiomeTint(int x, int z, int value) {}
 
   
   public void setOceanFloorLight(int x, int z, int value) {
     setData(x, z, 7, (byte)value);
   }
 
   
   public void setTransparentHeight(int x, int z, int value) {
     setData(x, z, 8, (byte)value);
   }
 
   
   public void setTransparentBlockstate(int x, int z, class_2680 blockState) {
     int id = getIDFromState(blockState);
     setData(x, z, 9, (byte)(id >> 8));
     setData(x, z, 10, (byte)id);
   }
 
 
   
   public void setTransparentBiomeTint(int x, int z, int value) {}
 
   
   public void setTransparentLight(int x, int z, int value) {
     setData(x, z, 11, (byte)value);
   }
 
   
   public void setFoliageHeight(int x, int z, int value) {
     setData(x, z, 12, (byte)value);
   }
 
   
   public void setFoliageBlockstate(int x, int z, class_2680 blockState) {
     int id = getIDFromState(blockState);
     setData(x, z, 13, (byte)(id >> 8));
     setData(x, z, 14, (byte)id);
   }
 
 
   
   public void setFoliageBiomeTint(int x, int z, int value) {}
 
   
   public void setFoliageLight(int x, int z, int value) {
     setData(x, z, 15, (byte)value);
   }
 
   
   public void setBiomeID(int x, int z, int value) {
     setData(x, z, 16, (byte)(value >> 8));
     setData(x, z, 17, (byte)value);
   }
   
   private synchronized void setData(int x, int z, int bit, byte value) {
     if (this.isCompressed) {
       decompress();
     }
     
     int index = x + z * this.width + this.width * this.height * bit;
     this.data[index] = value;
   }
 
   
   public void moveX(int offset) {
     synchronized (this.dataLock) {
       if (offset > 0) {
         System.arraycopy(this.data, offset * 18, this.data, 0, this.data.length - offset * 18);
       } else if (offset < 0) {
         System.arraycopy(this.data, 0, this.data, -offset * 18, this.data.length + offset * 18);
       } 
     } 
   }
 
 
   
   public void moveZ(int offset) {
     synchronized (this.dataLock) {
       if (offset > 0) {
         System.arraycopy(this.data, offset * this.width * 18, this.data, 0, this.data.length - offset * this.width * 18);
       } else if (offset < 0) {
         System.arraycopy(this.data, 0, this.data, -offset * this.width * 18, this.data.length + offset * this.width * 18);
       } 
     } 
   }
 
   
   public synchronized void setData(byte[] is, BiMap newStateToInt, int version) {
     this.data = is;
     this.isCompressed = false;
     if (version < 2) {
       convertData();
     }
     
     this.stateToInt = newStateToInt;
     this.count = this.stateToInt.size();
   }
   
   private synchronized void convertData() {
     if (this.isCompressed) {
       decompress();
     }
     
     byte[] newData = new byte[this.data.length];
     
     for (int x = 0; x < this.width; x++) {
       for (int z = 0; z < this.height; z++) {
         for (int bit = 0; bit < 18; bit++) {
           int oldIndex = (x + z * this.width) * 18 + bit;
           int newIndex = x + z * this.width + this.width * this.height * bit;
           newData[newIndex] = this.data[oldIndex];
         } 
       } 
     } 
     
     this.data = newData;
   }
   
   public synchronized byte[] getData() {
     if (this.isCompressed) {
       decompress();
     }
     
     return this.data;
   }
   
   public synchronized void compress() {
     if (!this.isCompressed) {
       try {
         this.isCompressed = true;
         this.data = CompressionUtils.compress(this.data);
       } catch (IOException iOException) {}
     }
   }
 
 
   
   private synchronized void decompress() {
     if (this.stateToInt == null) {
       this.stateToInt = (BiMap)HashBiMap.create();
     }
     
     if (this.isCompressed) {
       
       try { this.data = CompressionUtils.decompress(this.data);
         this.isCompressed = false; }
       catch (IOException iOException) {  }
       catch (DataFormatException dataFormatException) {}
     }
   }
 
 
   
   public synchronized boolean isCompressed() {
     return this.isCompressed;
   }
   
   private synchronized int getIDFromState(class_2680 blockState) {
     Integer id = (Integer)this.stateToInt.get(blockState);
     if (id == null && blockState != null) {
       while (this.stateToInt.inverse().containsKey(Integer.valueOf(this.count))) {
         this.count++;
       }
       
       id = Integer.valueOf(this.count);
       this.stateToInt.put(blockState, id);
     } 
     
     return id.intValue();
   }
   
   private class_2680 getStateFromID(int id) {
     return (class_2680)this.stateToInt.inverse().get(Integer.valueOf(id));
   }
   
   public BiMap getStateToInt() {
     this.stateToInt = createKeyFromCurrentBlocks(this.stateToInt);
     return this.stateToInt;
   }
   
   private BiMap createKeyFromCurrentBlocks(BiMap oldMap) {
     this.count = 1;
     HashBiMap hashBiMap = HashBiMap.create();
     
     for (int x = 0; x < this.width; x++) {
       for (int z = 0; z < this.height; z++) {
         int oldID = (getData(x, z, 1) & 0xFF) << 8 | getData(x, z, 2) & 0xFF;
         if (oldID != 0) {
           class_2680 blockState = (class_2680)oldMap.inverse().get(Integer.valueOf(oldID));
           Integer id = (Integer)hashBiMap.get(blockState);
           if (id == null && blockState != null) {
             while (hashBiMap.inverse().containsKey(Integer.valueOf(this.count))) {
               this.count++;
             }
             
             id = Integer.valueOf(this.count);
             hashBiMap.put(blockState, id);
           } 
           
           setData(x, z, 1, (byte)(id.intValue() >> 8));
           setData(x, z, 2, (byte)id.intValue());
         } 
         
         oldID = (getData(x, z, 5) & 0xFF) << 8 | getData(x, z, 6) & 0xFF;
         if (oldID != 0) {
           class_2680 blockState = (class_2680)oldMap.inverse().get(Integer.valueOf(oldID));
           Integer id = (Integer)hashBiMap.get(blockState);
           if (id == null && blockState != null) {
             while (hashBiMap.inverse().containsKey(Integer.valueOf(this.count))) {
               this.count++;
             }
             
             id = Integer.valueOf(this.count);
             hashBiMap.put(blockState, id);
           } 
           
           setData(x, z, 5, (byte)(id.intValue() >> 8));
           setData(x, z, 6, (byte)id.intValue());
         } 
         
         oldID = (getData(x, z, 9) & 0xFF) << 8 | getData(x, z, 10) & 0xFF;
         if (oldID != 0) {
           class_2680 blockState = (class_2680)oldMap.inverse().get(Integer.valueOf(oldID));
           Integer id = (Integer)hashBiMap.get(blockState);
           if (id == null && blockState != null) {
             while (hashBiMap.inverse().containsKey(Integer.valueOf(this.count))) {
               this.count++;
             }
             
             id = Integer.valueOf(this.count);
             hashBiMap.put(blockState, id);
           } 
           
           setData(x, z, 9, (byte)(id.intValue() >> 8));
           setData(x, z, 10, (byte)id.intValue());
         } 
         
         oldID = (getData(x, z, 13) & 0xFF) << 8 | getData(x, z, 14) & 0xFF;
         if (oldID != 0) {
           class_2680 blockState = (class_2680)oldMap.inverse().get(Integer.valueOf(oldID));
           Integer id = (Integer)hashBiMap.get(blockState);
           if (id == null && blockState != null) {
             while (hashBiMap.inverse().containsKey(Integer.valueOf(this.count))) {
               this.count++;
             }
             
             id = Integer.valueOf(this.count);
             hashBiMap.put(blockState, id);
           } 
           
           setData(x, z, 13, (byte)(id.intValue() >> 8));
           setData(x, z, 14, (byte)id.intValue());
         } 
       } 
     } 
     
     return (BiMap)hashBiMap;
   }
   
   static {
     Arrays.fill(compressedEmptyData, (byte)0);
     
     try {
       compressedEmptyData = CompressionUtils.compress(compressedEmptyData);
     } catch (IOException var1) {
       var1.printStackTrace();
     } 
   }
 }


