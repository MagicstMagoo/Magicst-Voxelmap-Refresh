package cn.magicst.mamiyaotaru.voxelmap.interfaces;

import cn.magicst.mamiyaotaru.voxelmap.util.DimensionContainer;
import java.util.ArrayList;
import net.minecraft.class_1937;
import net.minecraft.class_2960;

public interface IDimensionManager {
  ArrayList<DimensionContainer> getDimensions();
  
  DimensionContainer getDimensionContainerByWorld(class_1937 paramclass_1937);
  
  DimensionContainer getDimensionContainerByIdentifier(String paramString);
  
  void enteredWorld(class_1937 paramclass_1937);
  
  void populateDimensions(class_1937 paramclass_1937);
  
  DimensionContainer getDimensionContainerByResourceLocation(class_2960 paramclass_2960);
}

