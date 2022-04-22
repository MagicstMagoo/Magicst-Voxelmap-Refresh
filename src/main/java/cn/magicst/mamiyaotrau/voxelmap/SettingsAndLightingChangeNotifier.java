package cn.magicst.mamiyaotaru.voxelmap;

import cn.magicst.mamiyaotaru.voxelmap.interfaces.ISettingsAndLightingChangeListener;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.ISettingsAndLightingChangeNotifier;

public class SettingsAndLightingChangeNotifier
  implements ISettingsAndLightingChangeNotifier {
  public final void addObserver(ISettingsAndLightingChangeListener listener) {
    listeners.add(listener);
  }

  
  public final void removeObserver(ISettingsAndLightingChangeListener listener) {
    listeners.remove(listener);
  }

  
  public void notifyOfChanges() {
    for (ISettingsAndLightingChangeListener listener : listeners)
      listener.notifyOfActionableChange(this); 
  }
}