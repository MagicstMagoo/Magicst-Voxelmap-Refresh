package cn.magicst.mamiyaotaru.voxelmap.interfaces;

import cn.magicst.mamiyaotaru.voxelmap.persistent.CachedRegion;
import cn.magicst.mamiyaotaru.voxelmap.persistent.PersistentMapSettingsManager;
import cn.magicst.mamiyaotaru.voxelmap.util.MutableBlockPos;
import net.minecraft.class_1937;
import net.minecraft.class_2818;
import net.minecraft.class_310;
import net.minecraft.class_638;

public interface IPersistentMap extends IChangeObserver {
  void newWorld(class_638 paramclass_638);
  
  void onTick(class_310 paramclass_310);
  
  ISettingsAndLightingChangeNotifier getSettingsAndLightingChangeNotifier();
  
  void setLightMapArray(int[] paramArrayOfint);
  
  void getAndStoreData(AbstractMapData paramAbstractMapData, class_1937 paramclass_1937, class_2818 paramclass_2818, MutableBlockPos paramMutableBlockPos, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  int getPixelColor(AbstractMapData paramAbstractMapData, class_638 paramclass_638, MutableBlockPos paramMutableBlockPos1, MutableBlockPos paramMutableBlockPos2, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  CachedRegion[] getRegions(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  boolean isRegionLoaded(int paramInt1, int paramInt2);
  
  boolean isGroundAt(int paramInt1, int paramInt2);
  
  int getHeightAt(int paramInt1, int paramInt2);
  
  void purgeCachedRegions();
  
  void renameSubworld(String paramString1, String paramString2);
  
  PersistentMapSettingsManager getOptions();
  
  void compress();
}




