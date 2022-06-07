package cn.magicst.mamiyaotaru.voxelmap.interfaces;

import net.minecraft.class_2680;

public interface IMapData {
  public static final int DATABITS = 17;
  
  public static final int BYTESPERDATUM = 4;
  
  int getWidth();
  
  int getHeight();
  
  int getHeight(int paramInt1, int paramInt2);
  
  class_2680 getBlockstate(int paramInt1, int paramInt2);
  
  int getBiomeTint(int paramInt1, int paramInt2);
  
  int getLight(int paramInt1, int paramInt2);
  
  int getOceanFloorHeight(int paramInt1, int paramInt2);
  
  class_2680 getOceanFloorBlockstate(int paramInt1, int paramInt2);
  
  int getOceanFloorBiomeTint(int paramInt1, int paramInt2);
  
  int getOceanFloorLight(int paramInt1, int paramInt2);
  
  int getTransparentHeight(int paramInt1, int paramInt2);
  
  class_2680 getTransparentBlockstate(int paramInt1, int paramInt2);
  
  int getTransparentBiomeTint(int paramInt1, int paramInt2);
  
  int getTransparentLight(int paramInt1, int paramInt2);
  
  int getFoliageHeight(int paramInt1, int paramInt2);
  
  class_2680 getFoliageBlockstate(int paramInt1, int paramInt2);
  
  int getFoliageBiomeTint(int paramInt1, int paramInt2);
  
  int getFoliageLight(int paramInt1, int paramInt2);
  
  int getBiomeID(int paramInt1, int paramInt2);
  
  void setHeight(int paramInt1, int paramInt2, int paramInt3);
  
  void setBlockstate(int paramInt1, int paramInt2, class_2680 paramclass_2680);
  
  void setBiomeTint(int paramInt1, int paramInt2, int paramInt3);
  
  void setLight(int paramInt1, int paramInt2, int paramInt3);
  
  void setOceanFloorHeight(int paramInt1, int paramInt2, int paramInt3);
  
  void setOceanFloorBlockstate(int paramInt1, int paramInt2, class_2680 paramclass_2680);
  
  void setOceanFloorBiomeTint(int paramInt1, int paramInt2, int paramInt3);
  
  void setOceanFloorLight(int paramInt1, int paramInt2, int paramInt3);
  
  void setTransparentHeight(int paramInt1, int paramInt2, int paramInt3);
  
  void setTransparentBlockstate(int paramInt1, int paramInt2, class_2680 paramclass_2680);
  
  void setTransparentBiomeTint(int paramInt1, int paramInt2, int paramInt3);
  
  void setTransparentLight(int paramInt1, int paramInt2, int paramInt3);
  
  void setFoliageHeight(int paramInt1, int paramInt2, int paramInt3);
  
  void setFoliageBlockstate(int paramInt1, int paramInt2, class_2680 paramclass_2680);
  
  void setFoliageBiomeTint(int paramInt1, int paramInt2, int paramInt3);
  
  void setFoliageLight(int paramInt1, int paramInt2, int paramInt3);
  
  void setBiomeID(int paramInt1, int paramInt2, int paramInt3);
  
  void moveX(int paramInt);
  
  void moveZ(int paramInt);
}

