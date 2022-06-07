 package cn.magicst.mamiyaotaru.voxelmap.persistent;
 
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.ISettingsAndLightingChangeNotifier;
 import net.minecraft.class_2818;
 public class EmptyCachedRegion
   extends CachedRegion
 {
   public void notifyOfActionableChange(ISettingsAndLightingChangeNotifier notifier) {}
   
   public void refresh(boolean forceCompress) {}
   
   public void handleChangedChunk(class_2818 chunk) {}
   
   public void notifyOfThreadComplete(AbstractNotifyingRunnable runnable) {}
   
   public long getMostRecentView() {
     return 0L;
   }
 
   
   public String getKey() {
     return "";
   }
 
   
   public int getX() {
     return 0;
   }
 
   
   public int getZ() {
     return 0;
   }
 
   
   public int getWidth() {
     return 256;
   }
 
   
   public int getGLID() {
     return 0;
   }
 
   
   public CompressibleMapData getMapData() {
     return null;
   }
 
   
   public boolean isLoaded() {
     return true;
   }
 
   
   public boolean isEmpty() {
     return true;
   }
 
   
   public boolean isGroundAt(int blockX, int blockZ) {
     return false;
   }
 
   
   public int getHeightAt(int blockX, int blockZ) {
     return 0;
   }
   
   public void cleanup() {}
   
   public void compress() {}
 }

