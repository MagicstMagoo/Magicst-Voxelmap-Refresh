 package cn.magicst.mamiyaotaru.voxelmap.interfaces;
 
 import java.util.concurrent.CopyOnWriteArraySet;
 
 public interface ISettingsAndLightingChangeNotifier {
   public static final CopyOnWriteArraySet<ISettingsAndLightingChangeListener> listeners = new CopyOnWriteArraySet<>();
   
   void addObserver(ISettingsAndLightingChangeListener paramISettingsAndLightingChangeListener);
   
   void removeObserver(ISettingsAndLightingChangeListener paramISettingsAndLightingChangeListener);
   
   void notifyOfChanges();
 }

