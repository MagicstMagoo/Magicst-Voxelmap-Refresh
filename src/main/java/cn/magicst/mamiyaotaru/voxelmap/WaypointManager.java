package cn.magicst.mamiyaotaru.voxelmap;

import cn.magicst.mamiyaotaru.voxelmap.interfaces.AbstractVoxelMap;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.IVoxelMap;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.IWaypointManager;
import cn.magicst.mamiyaotaru.voxelmap.textures.IIconCreator;
import cn.magicst.mamiyaotaru.voxelmap.textures.Sprite;
import cn.magicst.mamiyaotaru.voxelmap.textures.TextureAtlas;
import cn.magicst.mamiyaotaru.voxelmap.util.BackgroundImageInfo;
import cn.magicst.mamiyaotaru.voxelmap.util.DimensionContainer;
import cn.magicst.mamiyaotaru.voxelmap.util.GLShim;
import cn.magicst.mamiyaotaru.voxelmap.util.GLUtils;
import cn.magicst.mamiyaotaru.voxelmap.util.GameVariableAccessShim;
import cn.magicst.mamiyaotaru.voxelmap.util.ImageUtils;
import cn.magicst.mamiyaotaru.voxelmap.util.MessageUtils;
import cn.magicst.mamiyaotaru.voxelmap.util.TextUtils;
import cn.magicst.mamiyaotaru.voxelmap.util.Waypoint;
import cn.magicst.mamiyaotaru.voxelmap.util.WaypointContainer;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.TreeSet;
import javax.imageio.ImageIO;
import net.minecraft.class_1937;
import net.minecraft.class_2535;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3298;
import net.minecraft.class_3300;
import net.minecraft.class_4587;
import net.minecraft.class_5218;
import net.minecraft.class_634;
import net.minecraft.class_642;
import net.minecraft.class_7134;
import net.minecraft.class_746;

public class WaypointManager implements IWaypointManager {
  IVoxelMap master;

  private class_310 game;

  public MapSettingsManager options;

  TextureAtlas textureAtlas;

  TextureAtlas textureAtlasChooser;

  private boolean loaded = false;

  private boolean needSave = false;

  private ArrayList<Waypoint> wayPts = new ArrayList<>();

  private Waypoint highlightedWaypoint = null;

  private String worldName = "";

  private String currentSubWorldName = "";

  private String currentSubworldDescriptor = "";

  private String currentSubworldDescriptorNoCodes = "";

  private boolean multiworld = false;

  private boolean gotAutoSubworldName = false;

  private DimensionContainer currentDimension = null;

  private final TreeSet<String> knownSubworldNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

  private final HashSet<String> oldNorthWorldNames = new HashSet<>();

  private final HashMap<String, String> worldSeeds = new HashMap<>();

  private BackgroundImageInfo backgroundImageInfo = null;

  private WaypointContainer waypointContainer = null;

  private File settingsFile;

  private Long lastNewWorldNameTime = Long.valueOf(0L);

  private final Object waypointLock = new Object();

  public WaypointManager(IVoxelMap master) {
    this.master = master;
    this.options = master.getMapOptions();
    this.textureAtlas = new TextureAtlas("waypoints");
    this.textureAtlas.method_4527(false, false);
    this.textureAtlasChooser = new TextureAtlas("chooser");
    this.textureAtlasChooser.method_4527(false, false);
    this.waypointContainer = new WaypointContainer(this.options);
  }

  public void onResourceManagerReload(class_3300 resourceManager) {
    List<class_2960> images = new ArrayList<>();
    IIconCreator iconCreator = textureAtlas -> {
      class_310 mc = class_310.method_1551();
      Map<class_2960, class_3298> resourceMap = mc.method_1478().method_14488("images", ());
      for (class_2960 candidate : resourceMap.keySet()) {
        if (candidate.method_12836().equals("voxelmap") && candidate.method_12832().contains("images/waypoints"))
          images.add(candidate);
      }
      Sprite markerIcon = textureAtlas.registerIconForResource(new class_2960("voxelmap", "images/waypoints/marker.png"), class_310.method_1551().method_1478());
      Sprite markerIconSmall = textureAtlas.registerIconForResource(new class_2960("voxelmap", "images/waypoints/markersmall.png"), class_310.method_1551().method_1478());
      for (class_2960 resourceLocation : images) {
        Sprite icon = textureAtlas.registerIconForResource(resourceLocation, class_310.method_1551().method_1478());
        String name = resourceLocation.toString();
        if (name.toLowerCase().contains("waypoints/waypoint") && !name.toLowerCase().contains("small")) {
          textureAtlas.registerMaskedIcon(name.replace(".png", "Small.png"), icon);
          textureAtlas.registerMaskedIcon(name.replace("waypoints/waypoint", "waypoints/marker"), markerIcon);
          textureAtlas.registerMaskedIcon(name.replace("waypoints/waypoint", "waypoints/marker").replace(".png", "Small.png"), markerIconSmall);
          continue;
        }
        if (name.toLowerCase().contains("waypoints/marker") && !name.toLowerCase().contains("small"))
          textureAtlas.registerMaskedIcon(name.replace(".png", "Small.png"), icon);
      }
    };
    this.textureAtlas.loadTextureAtlas(iconCreator);
    GLShim.glTexParameteri(3553, 10241, 9729);
    GLShim.glTexParameteri(3553, 10240, 9729);
    this.textureAtlasChooser.reset();
    int expectedSize = 32;
    for (class_2960 resourceLocation : images) {
      String name = resourceLocation.toString();
      if (name.toLowerCase().contains("waypoints/waypoint") && !name.toLowerCase().contains("small"))
        try {
          Optional<class_3298> imageResource = resourceManager.method_14486(resourceLocation);
          BufferedImage bufferedImage = ImageIO.read(((class_3298)imageResource.get()).method_14482());
          ((class_3298)imageResource.get()).method_43039().close();
          float scale = expectedSize / bufferedImage.getWidth();
          bufferedImage = ImageUtils.scaleImage(bufferedImage, scale);
          this.textureAtlasChooser.registerIconForBufferedImage(name, bufferedImage);
        } catch (IOException var11) {
          this.textureAtlasChooser.registerIconForResource(resourceLocation, class_310.method_1551().method_1478());
        }
    }
    this.textureAtlasChooser.stitch();
  }

  public TextureAtlas getTextureAtlas() {
    return this.textureAtlas;
  }

  public TextureAtlas getTextureAtlasChooser() {
    return this.textureAtlasChooser;
  }

  public ArrayList<Waypoint> getWaypoints() {
    return this.wayPts;
  }

  public void newWorld(class_1937 world) {
    if (world == null) {
      this.currentDimension = null;
    } else {
      String mapName;
      this.game = class_310.method_1551();
      if (this.game.method_1496()) {
        mapName = getMapName();
      } else {
        mapName = getServerName();
        if (mapName != null)
          mapName = mapName.toLowerCase();
      }
      if (!this.worldName.equals(mapName) && mapName != null && !mapName.equals("")) {
        this.currentDimension = null;
        this.worldName = mapName;
        this.master.getDimensionManager().populateDimensions(world);
        loadWaypoints();
      }
      this.master.getDimensionManager().enteredWorld(world);
      DimensionContainer dim = this.master.getDimensionManager().getDimensionContainerByWorld(world);
      enteredDimension(dim);
      setSubWorldDescriptor("");
    }
  }

  public String getMapName() {
    return this.game.method_1576().method_27050(class_5218.field_24188).normalize().toFile().getName();
  }

  public String getServerName() {
    String serverName = "";
    try {
      class_642 serverData = this.game.method_1558();
      if (serverData != null) {
        boolean isOnLAN = serverData.method_2994();
        if (isOnLAN) {
          System.out.println("LAN server detected!");
          serverName = serverData.field_3752;
        } else {
          serverName = serverData.field_3761;
        }
      } else if (this.game.method_1589()) {
        System.out.println("REALMS server detected!");
        serverName = "Realms";
      } else {
        class_634 netHandler = this.game.method_1562();
        class_2535 networkManager = netHandler.method_2872();
        InetSocketAddress socketAddress = (InetSocketAddress)networkManager.method_10755();
        serverName = socketAddress.getHostString() + ":" + socketAddress.getHostString();
      }
    } catch (Exception var6) {
      System.err.println("error getting ServerData");
      var6.printStackTrace();
    }
    return serverName;
  }

  public String getCurrentWorldName() {
    return this.worldName;
  }

  public void handleDeath() {
    HashSet<Waypoint> toDel = new HashSet<>();
    for (Waypoint pt : this.wayPts) {
      if (pt.name.equals("Latest Death"))
        pt.name = "Previous Death";
      if (pt.name.startsWith("Previous Death")) {
        if (this.options.deathpoints == 2) {
          int num = 0;
          try {
            if (pt.name.length() > 15)
              num = Integer.parseInt(pt.name.substring(15));
          } catch (Exception var6) {
            num = 0;
          }
          pt.red -= (pt.red - 0.5F) / 8.0F;
          pt.green -= (pt.green - 0.5F) / 8.0F;
          pt.blue -= (pt.blue - 0.5F) / 8.0F;
          pt.name = "Previous Death " + num + 1;
          continue;
        }
        toDel.add(pt);
      }
    }
    if (this.options.deathpoints != 2 && toDel.size() > 0)
      for (Waypoint pt : toDel)
        deleteWaypoint(pt);
    if (this.options.deathpoints != 0) {
      class_746 thePlayer = (class_310.method_1551()).field_1724;
      TreeSet<DimensionContainer> dimensions = new TreeSet();
      dimensions.add(AbstractVoxelMap.getInstance().getDimensionManager().getDimensionContainerByWorld((class_1937)(class_310.method_1551()).field_1687));
      double dimensionScale = thePlayer.field_6002.method_8597().comp_646();
      addWaypoint(new Waypoint("Latest Death", (int)(GameVariableAccessShim.xCoord() * dimensionScale), (int)(GameVariableAccessShim.zCoord() * dimensionScale), GameVariableAccessShim.yCoord() - 1, true, 1.0F, 1.0F, 1.0F, "Skull", getCurrentSubworldDescriptor(false), dimensions));
    }
  }

  private void enteredDimension(DimensionContainer dimension) {
    this.highlightedWaypoint = null;
    if (dimension == this.currentDimension)
      this.multiworld = true;
    this.currentDimension = dimension;
    synchronized (this.waypointLock) {
      this.waypointContainer = new WaypointContainer(this.options);
      for (Waypoint pt : this.wayPts) {
        if (pt.dimensions.size() != 0 && !pt.dimensions.contains(dimension)) {
          pt.inDimension = false;
        } else {
          pt.inDimension = true;
        }
        this.waypointContainer.addWaypoint(pt);
      }
      this.waypointContainer.setHighlightedWaypoint(this.highlightedWaypoint);
    }
    loadBackgroundMapImage();
  }

  public void setOldNorth(boolean oldNorth) {
    String oldNorthWorldName = "";
    if (this.knownSubworldNames.size() == 0) {
      oldNorthWorldName = "all";
    } else {
      oldNorthWorldName = getCurrentSubworldDescriptor(false);
    }
    if (oldNorth) {
      this.oldNorthWorldNames.add(oldNorthWorldName);
    } else {
      this.oldNorthWorldNames.remove(oldNorthWorldName);
    }
    saveWaypoints();
  }

  public TreeSet getKnownSubworldNames() {
    return this.knownSubworldNames;
  }

  public boolean receivedAutoSubworldName() {
    return this.gotAutoSubworldName;
  }

  public boolean isMultiworld() {
    return (this.multiworld || this.game.method_1589());
  }

  public synchronized void setSubworldName(String name, boolean fromServer) {
    boolean notNull = !name.equals("");
    if (notNull || System.currentTimeMillis() - this.lastNewWorldNameTime.longValue() > 2000L) {
      if (notNull) {
        if (fromServer)
          this.gotAutoSubworldName = true;
        if (!name.equals(this.currentSubWorldName))
          System.out.println("New world name: " + TextUtils.scrubCodes(name));
        this.lastNewWorldNameTime = Long.valueOf(System.currentTimeMillis());
      }
      this.currentSubWorldName = name;
      setSubWorldDescriptor(this.currentSubWorldName);
    }
  }

  public synchronized void setSubworldHash(String hash) {
    if (this.currentSubWorldName.equals(""))
      setSubWorldDescriptor(hash);
  }

  private void setSubWorldDescriptor(String descriptor) {
    boolean serverSaysOldNorth = false;
    if (descriptor.endsWith(")) {
            descriptor = descriptor.substring(0, descriptor.length() - 4);
    serverSaysOldNorth = true;
  }
    this.currentSubworldDescriptor = descriptor;
    this.currentSubworldDescriptorNoCodes = TextUtils.scrubCodes(this.currentSubworldDescriptor);
  newSubworldName(this.currentSubworldDescriptorNoCodes);
  String currentSubWorldDescriptorScrubbed = TextUtils.scrubName(this.currentSubworldDescriptorNoCodes);
  synchronized (this.waypointLock) {
    for (Waypoint pt : this.wayPts) {
      if (currentSubWorldDescriptorScrubbed != "" && pt.world != "" && !currentSubWorldDescriptorScrubbed.equals(pt.world)) {
        pt.inWorld = false;
        continue;
      }
      pt.inWorld = true;
    }
  }
    if (serverSaysOldNorth)
          if (this.currentSubworldDescriptorNoCodes.equals("")) {
    this.oldNorthWorldNames.add("all");
  } else {
    this.oldNorthWorldNames.add(this.currentSubworldDescriptorNoCodes);
  }
    (this.master.getMapOptions()).oldNorth = this.oldNorthWorldNames.contains(this.currentSubworldDescriptorNoCodes);
}

  private void newSubworldName(String name) {
    if (name != null && !name.equals("")) {
      this.multiworld = true;
      if (this.knownSubworldNames.add(name))
        if (this.loaded) {
          saveWaypoints();
        } else {
          this.needSave = true;
        }
    }
    loadBackgroundMapImage();
  }

  public void changeSubworldName(String oldName, String newName) {
    if (!newName.equals(oldName) && this.knownSubworldNames.remove(oldName)) {
      this.knownSubworldNames.add(newName);
      synchronized (this.waypointLock) {
        for (Waypoint pt : this.wayPts) {
          if (pt.world.equals(oldName))
            pt.world = newName;
        }
      }
      this.master.getPersistentMap().renameSubworld(oldName, newName);
      String worldName = getCurrentWorldName();
      String worldNamePathPart = TextUtils.scrubNameFile(worldName);
      String subWorldNamePathPart = TextUtils.scrubNameFile(oldName) + "/";
      File oldCachedRegionFileDir = new File((class_310.method_1551()).field_1697, "/mods/mamiyaotaru/voxelmap/cache/" + worldNamePathPart + "/" + subWorldNamePathPart);
      if (oldCachedRegionFileDir.exists() && oldCachedRegionFileDir.isDirectory()) {
        subWorldNamePathPart = TextUtils.scrubNameFile(newName) + "/";
        File newCachedRegionFileDir = new File((class_310.method_1551()).field_1697, "/mods/mamiyaotaru/voxelmap/cache/" + worldNamePathPart + "/" + subWorldNamePathPart);
        boolean success = oldCachedRegionFileDir.renameTo(newCachedRegionFileDir);
        if (!success)
          System.out.println("Failed renaming " + oldCachedRegionFileDir.getPath() + " to " + newCachedRegionFileDir.getPath());
      }
      if (oldName.equals(getCurrentSubworldDescriptor(false)))
        setSubworldName(newName, false);
      saveWaypoints();
    }
  }

  public void deleteSubworld(String name) {
    if (this.knownSubworldNames.remove(name)) {
      synchronized (this.waypointLock) {
        for (Waypoint pt : this.wayPts) {
          if (pt.world.equals(name)) {
            pt.world = "";
            pt.inWorld = true;
          }
        }
      }
      saveWaypoints();
      this.lastNewWorldNameTime = Long.valueOf(0L);
      setSubworldName("", false);
    }
    if (this.knownSubworldNames.size() == 0)
      this.multiworld = false;
  }

  public String getCurrentSubworldDescriptor(boolean withCodes) {
    return withCodes ? this.currentSubworldDescriptor : this.currentSubworldDescriptorNoCodes;
  }

  public String getWorldSeed() {
    String key = "all";
    if (this.knownSubworldNames.size() > 0)
      key = getCurrentSubworldDescriptor(false);
    String seed = this.worldSeeds.get(key);
    if (seed == null)
      seed = "";
    return seed;
  }

  public void setWorldSeed(String newSeed) {
    String worldName = "all";
    if (this.knownSubworldNames.size() > 0)
      worldName = getCurrentSubworldDescriptor(false);
    this.worldSeeds.put(worldName, newSeed);
    saveWaypoints();
  }

  public void saveWaypoints() {
    String worldNameSave = getCurrentWorldName();
    if (worldNameSave.endsWith(":25565")) {
      int portSepLoc = worldNameSave.lastIndexOf(":");
      if (portSepLoc != -1)
        worldNameSave = worldNameSave.substring(0, portSepLoc);
    }
    worldNameSave = TextUtils.scrubNameFile(worldNameSave);
    File saveDir = new File((class_310.method_1551()).field_1697, "/voxelmap/");
    if (!saveDir.exists())
      saveDir.mkdirs();
    this.settingsFile = new File(saveDir, worldNameSave + ".points");
    try {
      PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.settingsFile), StandardCharsets.UTF_8));
      String knownSubworldsString = "";
      for (String subworldName : this.knownSubworldNames)
        knownSubworldsString = knownSubworldsString + knownSubworldsString + ",";
      out.println("subworlds:" + knownSubworldsString);
      String oldNorthWorldsString = "";
      for (String oldNorthWorldName : this.oldNorthWorldNames)
        oldNorthWorldsString = oldNorthWorldsString + oldNorthWorldsString + ",";
      out.println("oldNorthWorlds:" + oldNorthWorldsString);
      String seedsString = "";
      for (Map.Entry<String, String> entry : this.worldSeeds.entrySet())
        seedsString = seedsString + seedsString + "#" + TextUtils.scrubName((String)entry.getKey()) + ",";
      out.println("seeds:" + seedsString);
      for (Waypoint pt : this.wayPts) {
        if (!pt.name.startsWith("^")) {
          String dimensionsString = "";
          for (DimensionContainer dimension : pt.dimensions)
            dimensionsString = dimensionsString + dimensionsString + "#";
          if (dimensionsString.equals(""))
            dimensionsString = dimensionsString + dimensionsString;
          out.println("name:" + TextUtils.scrubName(pt.name) + ",x:" + pt.x + ",z:" + pt.z + ",y:" + pt.y + ",enabled:" + Boolean.toString(pt.enabled) + ",red:" + pt.red + ",green:" + pt.green + ",blue:" + pt.blue + ",suffix:" + pt.imageSuffix + ",world:" + TextUtils.scrubName(pt.world) + ",dimensions:" + dimensionsString);
        }
      }
      out.close();
    } catch (Exception var12) {
      MessageUtils.chatInfo("Saving Waypoints");
      var12.printStackTrace();
    }
  }

  private void loadWaypoints() {
    this.loaded = false;
    this.multiworld = false;
    this.gotAutoSubworldName = false;
    this.currentDimension = null;
    setSubWorldDescriptor("");
    this.knownSubworldNames.clear();
    this.oldNorthWorldNames.clear();
    this.worldSeeds.clear();
    synchronized (this.waypointLock) {
      boolean loaded = false;
      this.wayPts = new ArrayList<>();
      String worldNameStandard = getCurrentWorldName();
      if (worldNameStandard.endsWith(":25565")) {
        int portSepLoc = worldNameStandard.lastIndexOf(":");
        if (portSepLoc != -1)
          worldNameStandard = worldNameStandard.substring(0, portSepLoc);
      }
      worldNameStandard = TextUtils.scrubNameFile(worldNameStandard);
      loaded = loadWaypointsExtensible(worldNameStandard);
      if (!loaded)
        MessageUtils.chatInfo("waypoints exist for this world/server.");
    }
    this.loaded = true;
    if (this.needSave) {
      this.needSave = false;
      saveWaypoints();
    }
    this.multiworld = (this.multiworld || this.knownSubworldNames.size() > 0);
  }

  private boolean loadWaypointsExtensible(String worldNameStandard) {
    File settingsFileNew = new File((class_310.method_1551()).field_1697, "/voxelmap/" + worldNameStandard + ".points");
    File settingsFileOld = new File((class_310.method_1551()).field_1697, "/mods/mamiyaotaru/voxelmap/" + worldNameStandard + ".points");
    if (!settingsFileOld.exists() && !settingsFileNew.exists())
      return false;
    if (!settingsFileOld.exists()) {
      this.settingsFile = settingsFileNew;
    } else if (!settingsFileNew.exists()) {
      this.settingsFile = settingsFileOld;
    } else {
      this.settingsFile = settingsFileNew;
    }
    if (this.settingsFile.exists()) {
      try {
        Properties properties = new Properties();
        FileReader fr = new FileReader(this.settingsFile);
        properties.load(fr);
        String subWorldsS = properties.getProperty("subworlds", "");
        String[] subWorlds = subWorldsS.split(",");
        for (String subWorld : subWorlds) {
          if (!subWorld.equals(""))
            this.knownSubworldNames.add(TextUtils.descrubName(subWorld));
        }
        String oldNorthWorldsS = properties.getProperty("oldNorthWorlds", "");
        String[] oldNorthWorlds = oldNorthWorldsS.split(",");
        for (String oldNorthWorld : oldNorthWorlds) {
          if (!oldNorthWorld.equals(""))
            this.oldNorthWorldNames.add(TextUtils.descrubName(oldNorthWorld));
        }
        String worldSeedsS = properties.getProperty("seeds", "");
        String[] worldSeedPairs = worldSeedsS.split(",");
        for (String pair : worldSeedPairs) {
          String[] worldSeedPair = pair.split("#");
          if (worldSeedPair.length == 2)
            this.worldSeeds.put(worldSeedPair[0], worldSeedPair[1]);
        }
        fr.close();
      } catch (IOException iOException) {}
      try {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(this.settingsFile), StandardCharsets.UTF_8));
        String sCurrentLine;
        while ((sCurrentLine = in.readLine()) != null) {
          try {
            String[] pairs = sCurrentLine.split(",");
            if (pairs.length > 1) {
              String name = "";
              int x = 0;
              int z = 0;
              int y = -1;
              boolean enabled = false;
              float red = 0.5F;
              float green = 0.0F;
              float blue = 0.0F;
              String suffix = "";
              String world = "";
              TreeSet<DimensionContainer> dimensions = new TreeSet();
              for (String pair : pairs) {
                int splitIndex = pair.indexOf(":");
                if (splitIndex != -1) {
                  String key = pair.substring(0, splitIndex).toLowerCase().trim();
                  String value = pair.substring(splitIndex + 1).trim();
                  if (key.equals("name")) {
                    name = TextUtils.descrubName(value);
                  } else if (key.equals("x")) {
                    x = Integer.parseInt(value);
                  } else if (key.equals("z")) {
                    z = Integer.parseInt(value);
                  } else if (key.equals("y")) {
                    y = Integer.parseInt(value);
                  } else if (key.equals("enabled")) {
                    enabled = Boolean.parseBoolean(value);
                  } else if (key.equals("red")) {
                    red = Float.parseFloat(value);
                  } else if (key.equals("green")) {
                    green = Float.parseFloat(value);
                  } else if (key.equals("blue")) {
                    blue = Float.parseFloat(value);
                  } else if (key.equals("suffix")) {
                    suffix = value;
                  } else if (key.equals("world")) {
                    world = TextUtils.descrubName(value);
                  } else if (key.equals("dimensions")) {
                    String[] dimensionStrings = value.split("#");
                    for (String dimensionString : dimensionStrings)
                      dimensions.add(AbstractVoxelMap.getInstance().getDimensionManager().getDimensionContainerByIdentifier(dimensionString));
                    if (dimensions.size() == 0)
                      dimensions.add(AbstractVoxelMap.getInstance().getDimensionManager().getDimensionContainerByResourceLocation(class_7134.field_37666.method_29177()));
                  }
                }
              }
              if (!name.equals("")) {
                loadWaypoint(name, x, z, y, enabled, red, green, blue, suffix, world, dimensions);
                if (!world.equals(""))
                  this.knownSubworldNames.add(TextUtils.descrubName(world));
              }
            }
          } catch (Exception exception) {}
        }
        in.close();
        return true;
      } catch (Exception var25) {
        MessageUtils.chatInfo("Loading Waypoints");
        System.err.println("waypoint load error: " + var25.getLocalizedMessage());
        var25.printStackTrace();
        return false;
      }
    }
    return false;
  }

  private void loadWaypoint(String name, int x, int z, int y, boolean enabled, float red, float green, float blue, String suffix, String world, TreeSet dimensions) {
    Waypoint newWaypoint = new Waypoint(name, x, z, y, enabled, red, green, blue, suffix, world, dimensions);
    if (!this.wayPts.contains(newWaypoint))
      this.wayPts.add(newWaypoint);
  }

  public void deleteWaypoint(Waypoint point) {
    this.waypointContainer.removeWaypoint(point);
    this.wayPts.remove(point);
    saveWaypoints();
    if (point == this.highlightedWaypoint)
      setHighlightedWaypoint(null, false);
  }

  public void addWaypoint(Waypoint newWaypoint) {
    this.wayPts.add(newWaypoint);
    this.waypointContainer.addWaypoint(newWaypoint);
    saveWaypoints();
    if (this.highlightedWaypoint != null && this.highlightedWaypoint.getX() == newWaypoint.getX() && this.highlightedWaypoint.getZ() == newWaypoint.getZ())
      setHighlightedWaypoint(newWaypoint, false);
  }

  public void setHighlightedWaypoint(Waypoint waypoint, boolean toggle) {
    if (toggle && waypoint == this.highlightedWaypoint) {
      this.highlightedWaypoint = null;
    } else {
      if (waypoint != null && !this.wayPts.contains(waypoint)) {
        waypoint.red = 2.0F;
        waypoint.blue = 0.0F;
        waypoint.green = 0.0F;
      }
      this.highlightedWaypoint = waypoint;
    }
    this.waypointContainer.setHighlightedWaypoint(this.highlightedWaypoint);
  }

  public Waypoint getHighlightedWaypoint() {
    return this.highlightedWaypoint;
  }

  public void renderWaypoints(float partialTicks, class_4587 matrixStack, boolean beacons, boolean signs, boolean withDepth, boolean withoutDepth) {
    if (this.waypointContainer != null)
      this.waypointContainer.renderWaypoints(partialTicks, matrixStack, beacons, signs, withDepth, withoutDepth);
  }

  private void loadBackgroundMapImage() {
    if (this.backgroundImageInfo != null) {
      GLUtils.glah(this.backgroundImageInfo.glid);
      this.backgroundImageInfo = null;
    }
    try {
      String path = getCurrentWorldName();
      String subworldDescriptor = getCurrentSubworldDescriptor(false);
      if (subworldDescriptor != null && !subworldDescriptor.equals(""))
        path = path + "/" + path;
      path = path + "/" + path;
      InputStream is = ((class_3298)this.game.method_1478().method_14486(new class_2960("voxelmap", "images/backgroundmaps/" + path + "/map.png")).get()).method_14482();
      Image image = ImageIO.read(is);
      is.close();
      BufferedImage mapImage = new BufferedImage(image.getWidth(null), image.getHeight(null), 2);
      Graphics gfx = mapImage.createGraphics();
      gfx.drawImage(image, 0, 0, null);
      gfx.dispose();
      is = ((class_3298)this.game.method_1478().method_14486(new class_2960("voxelmap", "images/backgroundmaps/" + path + "/map.txt")).get()).method_14482();
      InputStreamReader isr = new InputStreamReader(is);
      Properties mapProperties = new Properties();
      mapProperties.load(isr);
      String left = mapProperties.getProperty("left");
      String right = mapProperties.getProperty("right");
      String top = mapProperties.getProperty("top");
      String bottom = mapProperties.getProperty("bottom");
      String width = mapProperties.getProperty("width");
      String height = mapProperties.getProperty("height");
      String scale = mapProperties.getProperty("scale");
      if (left != null && top != null && width != null && height != null) {
        this.backgroundImageInfo = new BackgroundImageInfo(mapImage, Integer.parseInt(left), Integer.parseInt(top), Integer.parseInt(width), Integer.parseInt(height));
      } else if (left != null && top != null && scale != null) {
        this.backgroundImageInfo = new BackgroundImageInfo(mapImage, Integer.parseInt(left), Integer.parseInt(top), Float.parseFloat(scale));
      } else if (left != null && top != null && right != null && bottom != null) {
        int widthInt = Integer.parseInt(right) - Integer.parseInt(left);
        int heightInt = Integer.parseInt(right) - Integer.parseInt(left);
        this.backgroundImageInfo = new BackgroundImageInfo(mapImage, Integer.parseInt(left), Integer.parseInt(top), widthInt, heightInt);
      }
      isr.close();
    } catch (Exception exception) {}
  }

  public BackgroundImageInfo getBackgroundImageInfo() {
    return this.backgroundImageInfo;
  }
}
