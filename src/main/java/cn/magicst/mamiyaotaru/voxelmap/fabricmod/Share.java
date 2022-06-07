 package cn.magicst.mamiyaotaru.voxelmap.fabricmod;
 
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.AbstractVoxelMap;
 
 public class Share {
   public static boolean isOldNorth() {
     return (AbstractVoxelMap.getInstance().getMapOptions()).oldNorth;
   }
 }
