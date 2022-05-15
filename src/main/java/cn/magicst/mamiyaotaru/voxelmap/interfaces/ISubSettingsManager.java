package cn.magicst.mamiyaotaru.voxelmap.interfaces;

import java.io.File;
import java.io.PrintWriter;

public interface ISubSettingsManager extends ISettingsManager {
  void loadSettings(File paramFile);
  
  void saveAll(PrintWriter paramPrintWriter);
}

