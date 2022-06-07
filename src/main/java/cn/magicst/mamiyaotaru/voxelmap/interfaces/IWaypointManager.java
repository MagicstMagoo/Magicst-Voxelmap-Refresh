package cn.magicst.mamiyaotaru.voxelmap.interfaces;

import cn.magicst.mamiyaotaru.voxelmap.textures.TextureAtlas;
import cn.magicst.mamiyaotaru.voxelmap.util.BackgroundImageInfo;
import cn.magicst.mamiyaotaru.voxelmap.util.Waypoint;
import java.util.ArrayList;
import java.util.TreeSet;
import net.minecraft.class_1937;
import net.minecraft.class_3300;
import net.minecraft.class_4587;

public interface IWaypointManager {
  ArrayList<Waypoint> getWaypoints();
  
  void deleteWaypoint(Waypoint paramWaypoint);
  
  void saveWaypoints();
  
  void addWaypoint(Waypoint paramWaypoint);
  
  void handleDeath();
  
  void newWorld(class_1937 paramclass_1937);
  
  String getCurrentWorldName();
  
  TreeSet getKnownSubworldNames();
  
  boolean receivedAutoSubworldName();
  
  boolean isMultiworld();
  
  void setSubworldName(String paramString, boolean paramBoolean);
  
  void setSubworldHash(String paramString);
  
  void changeSubworldName(String paramString1, String paramString2);
  
  void deleteSubworld(String paramString);
  
  void setOldNorth(boolean paramBoolean);
  
  String getCurrentSubworldDescriptor(boolean paramBoolean);
  
  void renderWaypoints(float paramFloat, class_4587 paramclass_4587, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4);
  
  void onResourceManagerReload(class_3300 paramclass_3300);
  
  TextureAtlas getTextureAtlas();
  
  TextureAtlas getTextureAtlasChooser();
  
  void setHighlightedWaypoint(Waypoint paramWaypoint, boolean paramBoolean);
  
  Waypoint getHighlightedWaypoint();
  
  String getWorldSeed();
  
  void setWorldSeed(String paramString);
  
  BackgroundImageInfo getBackgroundImageInfo();
}

