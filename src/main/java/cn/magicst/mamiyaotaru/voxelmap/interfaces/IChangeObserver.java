package cn.magicst.mamiyaotaru.voxelmap.interfaces;

import net.minecraft.class_2818;

public interface IChangeObserver {
  void handleChangeInWorld(int paramInt1, int paramInt2);
  
  void processChunk(class_2818 paramclass_2818);
}

