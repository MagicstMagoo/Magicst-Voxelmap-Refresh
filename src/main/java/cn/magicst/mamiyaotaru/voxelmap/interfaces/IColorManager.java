package cn.magicst.mamiyaotaru.voxelmap.interfaces;

import cn.magicst.mamiyaotaru.voxelmap.util.MutableBlockPos;
import java.awt.image.BufferedImage;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2680;
import net.minecraft.class_3300;

public interface IColorManager {
  void onResourceManagerReload(class_3300 paramclass_3300);
  
  BufferedImage getColorPicker();
  
  BufferedImage getBlockImage(class_2680 paramclass_2680, class_1799 paramclass_1799, class_1937 paramclass_1937, float paramFloat1, float paramFloat2);
  
  boolean checkForChanges();
  
  int getBlockColorWithDefaultTint(MutableBlockPos paramMutableBlockPos, int paramInt);
  
  int getBlockColor(MutableBlockPos paramMutableBlockPos, int paramInt1, int paramInt2);
  
  void setSkyColor(int paramInt);
  
  int getAirColor();
  
  int getBiomeTint(AbstractMapData paramAbstractMapData, class_1937 paramclass_1937, class_2680 paramclass_2680, int paramInt1, MutableBlockPos paramMutableBlockPos1, MutableBlockPos paramMutableBlockPos2, int paramInt2, int paramInt3);
}


