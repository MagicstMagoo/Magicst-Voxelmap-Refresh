 package cn.magicst.mamiyaotaru.voxelmap.persistent;
 
 import net.minecraft.class_2818;
 
 class FillChunkRunnable
   implements Runnable
 {
   private class_2818 chunk;
   private int index;
   
   public FillChunkRunnable(class_2818 chunk) {
     this.chunk = chunk;
     int chunkX = (chunk.method_12004()).field_9181 - paramCachedRegion.x * 16;
     int chunkZ = (chunk.method_12004()).field_9180 - paramCachedRegion.z * 16;
     this.index = chunkZ * 16 + chunkX;
   }
   
   public void run() {
     CachedRegion.this.threadLock.lock();
 
     
     try { if (!CachedRegion.this.loaded) {
         CachedRegion.this.load();
       }
       
       int chunkX = (this.chunk.method_12004()).field_9181 - CachedRegion.this.x * 16;
       int chunkZ = (this.chunk.method_12004()).field_9180 - CachedRegion.this.z * 16;
       CachedRegion.this.loadChunkData(this.chunk, chunkX, chunkZ); }
     catch (Exception exception) {  }
     finally
     { CachedRegion.this.threadLock.unlock();
       CachedRegion.this.chunkUpdateQueued[this.index] = false; }
   
   }
 }

