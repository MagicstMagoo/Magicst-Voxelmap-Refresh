package cn.magicst.mamiyaotaru.voxelmap.interfaces;

import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.EnumOptionsMinimap;

public interface ISettingsManager {
  String getKeyText(EnumOptionsMinimap paramEnumOptionsMinimap);
  
  void setOptionFloatValue(EnumOptionsMinimap paramEnumOptionsMinimap, float paramFloat);
  
  float getOptionFloatValue(EnumOptionsMinimap paramEnumOptionsMinimap);
}

