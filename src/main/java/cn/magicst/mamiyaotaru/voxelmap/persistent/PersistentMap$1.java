 package cn.magicst.mamiyaotaru.voxelmap.persistent;
 
 class null
   extends Thread
 {
   null(ThreadGroup group, Runnable target, String name) {
     super(group, target, name);
   } public void run() {
     try {
       Thread.sleep(2000L);
     } catch (InterruptedException var2) {
       var2.printStackTrace();
     } 
     
     if (PersistentMap.this.world != null)
       PersistentMap.this.newWorldStuff(); 
   }
 }

