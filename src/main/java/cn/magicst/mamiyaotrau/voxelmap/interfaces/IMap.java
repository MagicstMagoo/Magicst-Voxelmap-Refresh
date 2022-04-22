package cn.magicst.mamiyaotaru.voxelmap.interfaces;

import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_638;

public interface IMap extends IChangeObserver {
  void forceFullRender(boolean paramBoolean);
  
  void drawMinimap(class_4587 paramclass_4587, class_310 paramclass_310);
  
  float getPercentX();
  
  float getPercentY();
  
  void newWorld(class_638 paramclass_638);
  
  void onTickInGame(class_4587 paramclass_4587, class_310 paramclass_310);
  
  int[] getLightmapArray();
  
  void newWorldName();
}

