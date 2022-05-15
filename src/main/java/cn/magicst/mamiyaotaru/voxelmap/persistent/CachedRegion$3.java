 package cn.magicst.mamiyaotaru.voxelmap.persistent;
 
 class null
   implements Runnable
 {
   public void run() {
     if (CachedRegion.this.threadLock.tryLock()) {
       
       try { CachedRegion.this.compressData(); }
       catch (Exception exception) {  }
       finally
       { CachedRegion.this.threadLock.unlock(); }
     
     }
     
     CachedRegion.this.queuedToCompress = false;
   }
 }

