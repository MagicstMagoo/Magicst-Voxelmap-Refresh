 package cn.magicst.mamiyaotaru.voxelmap.persistent;
 
 import java.util.Set;
 import java.util.concurrent.CopyOnWriteArraySet;
 
 public abstract class AbstractNotifyingRunnable implements Runnable {
   private final Set<IThreadCompleteListener> listeners = new CopyOnWriteArraySet<>();
   
   public final void addListener(IThreadCompleteListener listener) {
     this.listeners.add(listener);
   }
   
   public final void removeListener(IThreadCompleteListener listener) {
     this.listeners.remove(listener);
   }
   
   private void notifyListeners() {
     for (IThreadCompleteListener listener : this.listeners) {
       listener.notifyOfThreadComplete(this);
     }
   }
 
   
   public final void run() {
     try {
       doRun();
     } finally {
       notifyListeners();
     } 
   }
   
   public abstract void doRun();
 }



