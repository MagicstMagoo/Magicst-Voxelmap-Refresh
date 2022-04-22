 package cn.magicst.mamiyaotaru.voxelmap.util;
 
 import cn.magicst.mamiyaotaru.voxelmap.MapSettingsManager;
 import cn.magicst.mamiyaotaru.voxelmap.VoxelMap;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IVoxelMap;
 import java.util.Random;
 
 public class MapUtils
 {
   private static MapSettingsManager options = null;
   private static IVoxelMap master = null;
   private static Random slimeRandom = null;
   private static int lastSlimeX = 0;
   private static int lastSlimeZ = 0;
   private static boolean isSlimeChunk = false;
   
   public static void reset() {
     master = (IVoxelMap)VoxelMap.getInstance();
     options = master.getMapOptions();
     slimeRandom = null;
     lastSlimeX = -120000;
     lastSlimeZ = 10000;
     isSlimeChunk = false;
   }
   
   public static int doSlimeAndGrid(int color24, int mcX, int mcZ) {
     if (options.slimeChunks && isSlimeChunk(mcX, mcZ)) {
       color24 = ColorUtils.colorAdder(2097217280, color24);
     }
     
     if (options.chunkGrid) {
       if (mcX % 512 != 0 && mcZ % 512 != 0) {
         if (mcX % 16 == 0 || mcZ % 16 == 0) {
           color24 = ColorUtils.colorAdder(2097152000, color24);
         }
       } else {
         color24 = ColorUtils.colorAdder(2113863680, color24);
       } 
     }
     
     return color24;
   }
   
   public static boolean isSlimeChunk(int mcX, int mcZ) {
     int xPosition = mcX >> 4;
     int zPosition = mcZ >> 4;
     String seedString = VoxelMap.getInstance().getWorldSeed();
     if (!seedString.equals("")) {
       long seed = 0L;
       
       try {
         seed = Long.parseLong(seedString);
       } catch (NumberFormatException var8) {
         seed = seedString.hashCode();
       } 
       
       if (xPosition != lastSlimeX || zPosition != lastSlimeZ || slimeRandom == null) {
         lastSlimeX = xPosition;
         lastSlimeZ = zPosition;
         slimeRandom = new Random(seed + (xPosition * xPosition * 4987142) + (xPosition * 5947611) + (zPosition * zPosition) * 4392871L + (zPosition * 389711) ^ 0x3AD8025FL);
         isSlimeChunk = (slimeRandom.nextInt(10) == 0);
       } 
     } 
     
     return isSlimeChunk;
   }
 }

 