package cn.magicst.mamiyaotaru.voxelmap.interfaces;

import cn.magicst.mamiyaotaru.voxelmap.util.LayoutVariables;
import net.minecraft.class_310;
import net.minecraft.class_3300;
import net.minecraft.class_4587;

public interface IRadar {
  void onResourceManagerReload(class_3300 paramclass_3300);
  
  void onTickInGame(class_4587 paramclass_4587, class_310 paramclass_310, LayoutVariables paramLayoutVariables);
}

