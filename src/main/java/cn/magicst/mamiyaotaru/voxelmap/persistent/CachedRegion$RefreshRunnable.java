 package cn.magicst.mamiyaotaru.voxelmap.persistent;
 
 class RefreshRunnable
   extends AbstractNotifyingRunnable
 {
   private boolean forceCompress = false;
   
   public RefreshRunnable(boolean forceCompress) {
     this.forceCompress = forceCompress;
   }
 
   
   public void doRun() {
     CachedRegion.this.threadLock.lock();
     CachedRegion.this.mostRecentChange = System.currentTimeMillis();
     
     try {
       if (!CachedRegion.this.loaded) {
         CachedRegion.this.load();
       }
       
       if (CachedRegion.this.dataUpdateQueued) {
         CachedRegion.this.loadModifiedData();
         CachedRegion.this.dataUpdateQueued = false;
       } 
       
       for (; CachedRegion.this.dataUpdated || CachedRegion.this.displayOptionsChanged; CachedRegion.this.refreshingImage = false) {
         CachedRegion.this.dataUpdated = false;
         CachedRegion.this.displayOptionsChanged = false;
         CachedRegion.this.refreshingImage = true;
         synchronized (CachedRegion.this.image) {
           CachedRegion.this.fillImage();
           CachedRegion.this.imageChanged = true;
         } 
       } 
       
       if (this.forceCompress) {
         CachedRegion.this.compressData();
       }
     } catch (Exception var8) {
       System.out.println("Exception loading region: " + var8.getLocalizedMessage());
       var8.printStackTrace();
     } finally {
       CachedRegion.this.threadLock.unlock();
       CachedRegion.this.refreshQueued = false;
     } 
   }
 }

