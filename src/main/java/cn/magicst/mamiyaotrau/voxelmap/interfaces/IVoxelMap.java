package cn.magicst.mamiyaotaru.voxelmap.interfaces;

import cn.magicst.mamiyaotaru.voxelmap.MapSettingsManager;
import cn.magicst.mamiyaotaru.voxelmap.RadarSettingsManager;
import cn.magicst.mamiyaotaru.voxelmap.persistent.PersistentMapSettingsManager;
import cn.magicst.mamiyaotaru.voxelmap.util.WorldUpdateListener;

public interface IVoxelMap {
  MapSettingsManager getMapOptions();
  
  RadarSettingsManager getRadarOptions();
  
  PersistentMapSettingsManager getPersistentMapOptions();
  
  IMap getMap();
  
  IRadar getRadar();
  
  IColorManager getColorManager();
  
  IWaypointManager getWaypointManager();
  
  IDimensionManager getDimensionManager();
  
  IPersistentMap getPersistentMap();
  
  void setPermissions(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4);
  
  void newSubWorldName(String paramString, boolean paramBoolean);
  
  void newSubWorldHash(String paramString);
  
  ISettingsAndLightingChangeNotifier getSettingsAndLightingChangeNotifier();
  
  String getWorldSeed();
  
  void setWorldSeed(String paramString);
  
  void sendPlayerMessageOnMainThread(String paramString);
  
  WorldUpdateListener getWorldUpdateListener();
}

