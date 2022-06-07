 package cn.magicst.mamiyaotaru.voxelmap.persistent;
 
 import java.io.IOException;
 
 class null
   implements Runnable
 {
   public void run() {
     CachedRegion.this.threadLock.lock();
     
     try {
       CachedRegion.this.doSave();
     } catch (IOException var5) {
       System.err.println("Failed to save region file for " + CachedRegion.this.x + "," + CachedRegion.this.z + " in " + CachedRegion.this.worldNamePathPart + "/" + CachedRegion.this.subworldNamePathPart + CachedRegion.this.dimensionNamePathPart);
       var5.printStackTrace();
     } finally {
       CachedRegion.this.threadLock.unlock();
     } 
   }
 }




