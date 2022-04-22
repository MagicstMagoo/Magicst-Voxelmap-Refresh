package cn.magicst.mamiyaotaru.voxelmap;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.AbstractVoxelMap;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.IChangeObserver;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.IColorManager;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.IDimensionManager;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.IMap;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.IPersistentMap;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.IRadar;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.ISettingsAndLightingChangeNotifier;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.ISubSettingsManager;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.IVoxelMap;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.IWaypointManager;
import cn.magicst.mamiyaotaru.voxelmap.persistent.PersistentMap;
import cn.magicst.mamiyaotaru.voxelmap.persistent.PersistentMapSettingsManager;
import cn.magicst.mamiyaotaru.voxelmap.util.BiomeRepository;
import cn.magicst.mamiyaotaru.voxelmap.util.DimensionManager;
import cn.magicst.mamiyaotaru.voxelmap.util.GLUtils;
import cn.magicst.mamiyaotaru.voxelmap.util.GameVariableAccessShim;
import cn.magicst.mamiyaotaru.voxelmap.util.MapUtils;
import cn.magicst.mamiyaotaru.voxelmap.util.ReflectionUtils;
import cn.magicst.mamiyaotaru.voxelmap.util.TextUtils;
import cn.magicst.mamiyaotaru.voxelmap.util.WorldUpdateListener;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.class_1937;
import net.minecraft.class_2540;
import net.minecraft.class_2561;
import net.minecraft.class_2585;
import net.minecraft.class_2596;
import net.minecraft.class_2817;
import net.minecraft.class_2960;
import net.minecraft.class_303;
import net.minecraft.class_310;
import net.minecraft.class_3300;
import net.minecraft.class_3302;
import net.minecraft.class_3304;
import net.minecraft.class_338;
import net.minecraft.class_3695;
import net.minecraft.class_3902;
import net.minecraft.class_4587;
import net.minecraft.class_638;

public class VoxelMap extends AbstractVoxelMap implements class_3302 {
  public static MapSettingsManager mapOptions = null;
  public static RadarSettingsManager radarOptions = null;
  private PersistentMapSettingsManager persistentMapOptions = null;
  private IMap map = null;
  private IRadar radar = null;
  private IRadar radarSimple = null;
  private PersistentMap persistentMap = null;
  private ISettingsAndLightingChangeNotifier settingsAndLightingChangeNotifier = null;
  private WorldUpdateListener worldUpdateListener = null;
  private IColorManager colorManager = null;
  private IWaypointManager waypointManager = null;
  private IDimensionManager dimensionManager = null;
  private class_638 world;
  private String worldName = "";
  private Long newServerTime = Long.valueOf(0L);
  private boolean checkMOTD = false;
  private class_303 mostRecentLine = null;
  private final UUID devUUID = UUID.fromString("9b37abb9-2487-4712-bb96-21a1e0b2023c");
  private String passMessage = null;
  
  public VoxelMap() {
    instance = this;
  }
  
  public void lateInit(boolean showUnderMenus, boolean isFair) {
    GLUtils.textureManager = class_310.method_1551().method_1531();
    mapOptions = new MapSettingsManager();
    mapOptions.showUnderMenus = showUnderMenus;
    radarOptions = new RadarSettingsManager();
    mapOptions.addSecondaryOptionsManager(radarOptions);
    this.persistentMapOptions = new PersistentMapSettingsManager();
    mapOptions.addSecondaryOptionsManager((ISubSettingsManager)this.persistentMapOptions);
    BiomeRepository.loadBiomeColors();
    this.colorManager = new ColorManager((IVoxelMap)this);
    this.waypointManager = new WaypointManager((IVoxelMap)this);
    this.dimensionManager = (IDimensionManager)new DimensionManager((IVoxelMap)this);
    this.persistentMap = new PersistentMap((IVoxelMap)this);
    mapOptions.loadAll();
    
    try {
      if (isFair) {
        radarOptions.radarAllowed = Boolean.valueOf(false);
        radarOptions.radarMobsAllowed = Boolean.valueOf(false);
        radarOptions.radarPlayersAllowed = Boolean.valueOf(false);
      } else {
        radarOptions.radarAllowed = Boolean.valueOf(true);
        radarOptions.radarMobsAllowed = Boolean.valueOf(true);
        radarOptions.radarPlayersAllowed = Boolean.valueOf(true);
        this.radar = new Radar((IVoxelMap)this);
        this.radarSimple = new RadarSimple((IVoxelMap)this);
      } 
    } catch (Exception var4) {
      System.err.println("Failed creating radar " + var4.getLocalizedMessage());
      var4.printStackTrace();
      radarOptions.radarAllowed = Boolean.valueOf(false);
      radarOptions.radarMobsAllowed = Boolean.valueOf(false);
      radarOptions.radarPlayersAllowed = Boolean.valueOf(false);
      this.radar = null;
      this.radarSimple = null;
    } 
    
    this.map = new Map((IVoxelMap)this);
    this.settingsAndLightingChangeNotifier = new SettingsAndLightingChangeNotifier();
    this.worldUpdateListener = new WorldUpdateListener();
    this.worldUpdateListener.addListener((IChangeObserver)this.map);
    this.worldUpdateListener.addListener((IChangeObserver)this.persistentMap);
    class_3304 resourceManager = (class_3304)class_310.method_1551().method_1478();
    resourceManager.method_14477(this);
    apply((class_3300)resourceManager);
  }
  
  public CompletableFuture<Void> method_25931(class_3302.class_4045 synchronizer, class_3300 resourceManager, class_3695 loadProfiler, class_3695 applyProfiler, Executor loadExecutor, Executor applyExecutor) {
    return synchronizer.method_18352(class_3902.field_17274).thenRunAsync(() -> apply(resourceManager), applyExecutor);
  }
  
  private void apply(class_3300 resourceManager) {
    this.waypointManager.onResourceManagerReload(resourceManager);
    if (this.radar != null) {
      this.radar.onResourceManagerReload(resourceManager);
    }
    
    if (this.radarSimple != null) {
      this.radarSimple.onResourceManagerReload(resourceManager);
    }
    
    this.colorManager.onResourceManagerReload(resourceManager);
  }
  
  public void onTickInGame(class_4587 matrixStack, class_310 mc) {
    this.map.onTickInGame(matrixStack, mc);
    if (this.passMessage != null) {
      mc.field_1705.method_1743().method_1812((class_2561)new class_2585(this.passMessage));
      this.passMessage = null;
    } 
  }

  
  public void onTick(class_310 mc) {
    if (this.checkMOTD) {
      checkPermissionMessages(mc);
    }
    
    if ((GameVariableAccessShim.getWorld() != null && !GameVariableAccessShim.getWorld().equals(this.world)) || (this.world != null && !this.world.equals(GameVariableAccessShim.getWorld()))) {
      this.world = GameVariableAccessShim.getWorld();
      this.waypointManager.newWorld((class_1937)this.world);
      this.persistentMap.newWorld(this.world);
      if (this.world != null) {
        MapUtils.reset();
        StringBuilder channelList = new StringBuilder();
        channelList.append("worldinfo:world_id");
        class_2540 buffer = new class_2540(Unpooled.buffer());
        buffer.writeBytes(channelList.toString().getBytes(Charsets.UTF_8));
        mc.method_1562().method_2883((class_2596)new class_2817(new class_2960("minecraft:register"), buffer));
        ByteBuf data1 = Unpooled.buffer(4);
        data1.writeInt(42);
        class_2540 packetBuffer1 = new class_2540(data1);
        class_2817 packet1 = new class_2817(new class_2960("worldinfo:world_id"), packetBuffer1);
        mc.field_1724.field_3944.method_2883((class_2596)packet1);
        ByteBuf data2 = Unpooled.buffer(4);
        data2.writeInt(43);
        class_2540 packetBuffer2 = new class_2540(data2);
        new class_2817(new class_2960("journeymap:world_info"), packetBuffer2);
        mc.field_1724.method_3117();
        Map skinMap = mc.method_1582().method_4654(mc.field_1724.method_7334());
        if (skinMap.containsKey(MinecraftProfileTexture.Type.SKIN)) {
          mc.method_1582().method_4656((MinecraftProfileTexture)skinMap.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
        }
        
        if (!this.worldName.equals(this.waypointManager.getCurrentWorldName())) {
          this.worldName = this.waypointManager.getCurrentWorldName();
          radarOptions.radarAllowed = Boolean.valueOf(true);
          radarOptions.radarPlayersAllowed = radarOptions.radarAllowed;
          radarOptions.radarMobsAllowed = radarOptions.radarAllowed;
          mapOptions.cavesAllowed = Boolean.valueOf(true);
          if (!mc.method_1496()) {
            this.newServerTime = Long.valueOf(System.currentTimeMillis());
            this.checkMOTD = true;
          } 
        } 
        
        this.map.newWorld(this.world);
      } 
    } 
    
    TickCounter.onTick();
    this.persistentMap.onTick(mc);
  }

  
  private void checkPermissionMessages(class_310 mc) {
    if (GameVariableAccessShim.getWorld() != null && mc.field_1724 != null && mc.field_1705 != null && System.currentTimeMillis() - this.newServerTime.longValue() < 20000L) {
      UUID playerUUID = mc.field_1724.method_5667();
      Object guiNewChat = mc.field_1705.method_1743();
      if (guiNewChat == null) {
        System.out.println("failed to get guiNewChat");
      } else {
        Object chatListObj = ReflectionUtils.getPrivateFieldValueByType(guiNewChat, class_338.class, List.class, 1);
        if (chatListObj == null) {
          System.out.println("could not get chatlist");
        } else {
          List<?> chatList = (List)chatListObj;
          boolean killRadar = false;
          boolean killCaves = false;

          
          for (int t = 0; t < chatList.size(); t++) {
            class_303 checkMe = (class_303)chatList.get(t);
            if (checkMe.equals(this.mostRecentLine)) {
              break;
            }
            
            class_2561 rawText = (class_2561)checkMe.method_1412();
            String msg = TextUtils.asFormattedString(rawText);
            String error = "";
            msg = msg.replaceAll("§r", "");
            if (msg.contains("§3 §6 §3 §6 §3 §6 §d")) {
              killCaves = true;
              error = error + "Server disabled cavemapping.  ";
            } 
            
            if (msg.contains("§3 §6 §3 §6 §3 §6 §e")) {
              killRadar = true;
              error = error + "Server disabled radar.  ";
            } 
            
            if (!error.equals("")) {
              this.passMessage = error;
            }
          } 
          
          radarOptions.radarAllowed = Boolean.valueOf((radarOptions.radarAllowed.booleanValue() && (!killRadar || this.devUUID.equals(playerUUID))));
          radarOptions.radarPlayersAllowed = radarOptions.radarAllowed;
          radarOptions.radarMobsAllowed = radarOptions.radarAllowed;
          mapOptions.cavesAllowed = Boolean.valueOf((mapOptions.cavesAllowed.booleanValue() && (!killCaves || this.devUUID.equals(playerUUID))));
          this.mostRecentLine = (chatList.size() > 0) ? (class_303)chatList.get(0) : null;
        } 
      } 
    } else if (System.currentTimeMillis() - this.newServerTime.longValue() >= 20000L) {
      this.checkMOTD = false;
    } 
  }


  
  public MapSettingsManager getMapOptions() {
    return mapOptions;
  }

  
  public RadarSettingsManager getRadarOptions() {
    return radarOptions;
  }

  
  public PersistentMapSettingsManager getPersistentMapOptions() {
    return this.persistentMapOptions;
  }

  
  public IMap getMap() {
    return this.map;
  }

  
  public ISettingsAndLightingChangeNotifier getSettingsAndLightingChangeNotifier() {
    return this.settingsAndLightingChangeNotifier;
  }

  
  public IRadar getRadar() {
    if (radarOptions.showRadar) {
      if (radarOptions.radarMode == 1) {
        return this.radarSimple;
      }
      
      if (radarOptions.radarMode == 2) {
        return this.radar;
      }
    } 
    
    return null;
  }

  
  public IColorManager getColorManager() {
    return this.colorManager;
  }

  
  public IWaypointManager getWaypointManager() {
    return this.waypointManager;
  }

  
  public IDimensionManager getDimensionManager() {
    return this.dimensionManager;
  }

  
  public IPersistentMap getPersistentMap() {
    return (IPersistentMap)this.persistentMap;
  }

  
  public void setPermissions(boolean hasFullRadarPermission, boolean hasPlayersOnRadarPermission, boolean hasMobsOnRadarPermission, boolean hasCavemodePermission) {
    boolean override = false;
    
    try {
      UUID devUUID = UUID.fromString("9b37abb9-2487-4712-bb96-21a1e0b2023c");
      UUID playerUUID = (class_310.method_1551()).field_1724.method_5667();
      override = playerUUID.equals(devUUID);
    } catch (Exception exception) {}

    
    radarOptions.radarAllowed = Boolean.valueOf((hasFullRadarPermission || override));
    radarOptions.radarPlayersAllowed = Boolean.valueOf((hasPlayersOnRadarPermission || override));
    radarOptions.radarMobsAllowed = Boolean.valueOf((hasMobsOnRadarPermission || override));
    mapOptions.cavesAllowed = Boolean.valueOf((hasCavemodePermission || override));
  }

  
  public synchronized void newSubWorldName(String name, boolean fromServer) {
    this.waypointManager.setSubworldName(name, fromServer);
    this.map.newWorldName();
  }

  
  public synchronized void newSubWorldHash(String hash) {
    this.waypointManager.setSubworldHash(hash);
  }

  
  public String getWorldSeed() {
    if (class_310.method_1551().method_1496()) {
      String seed = "";
      
      try {
        seed = Long.toString(class_310.method_1551().method_1576().method_3847(class_1937.field_25179).method_8412());
      } catch (Exception exception) {}

      
      return seed;
    } 
    return this.waypointManager.getWorldSeed();
  }


  
  public void setWorldSeed(String newSeed) {
    if (!class_310.method_1551().method_1496()) {
      this.waypointManager.setWorldSeed(newSeed);
    }
  }


  
  public void sendPlayerMessageOnMainThread(String s) {
    this.passMessage = s;
  }

  
  public WorldUpdateListener getWorldUpdateListener() {
    return this.worldUpdateListener;
  }
}

