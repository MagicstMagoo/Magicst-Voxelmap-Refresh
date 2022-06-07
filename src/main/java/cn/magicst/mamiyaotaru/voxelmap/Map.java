 package cn.magicst.mamiyaotaru.voxelmap;
 
 import cn.magicst.mamiyaotaru.voxelmap.gui.GuiAddWaypoint;
 import cn.magicst.mamiyaotaru.voxelmap.gui.GuiWaypoints;
 import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.EnumOptionsMinimap;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.AbstractMapData;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IChangeObserver;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IColorManager;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IMap;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IVoxelMap;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IWaypointManager;
 import cn.magicst.mamiyaotaru.voxelmap.persistent.GuiPersistentMap;
 import cn.magicst.mamiyaotaru.voxelmap.textures.Sprite;
 import cn.magicst.mamiyaotaru.voxelmap.textures.TextureAtlas;
 import cn.magicst.mamiyaotaru.voxelmap.util.BiomeRepository;
 import cn.magicst.mamiyaotaru.voxelmap.util.BlockRepository;
 import cn.magicst.mamiyaotaru.voxelmap.util.ColorUtils;
 import cn.magicst.mamiyaotaru.voxelmap.util.DimensionContainer;
 import cn.magicst.mamiyaotaru.voxelmap.util.FullMapData;
 import cn.magicst.mamiyaotaru.voxelmap.util.GLShim;
 import cn.magicst.mamiyaotaru.voxelmap.util.GLUtils;
 import cn.magicst.mamiyaotaru.voxelmap.util.GameVariableAccessShim;
 import cn.magicst.mamiyaotaru.voxelmap.util.I18nUtils;
 import cn.magicst.mamiyaotaru.voxelmap.util.LayoutVariables;
 import cn.magicst.mamiyaotaru.voxelmap.util.LiveScaledGLBufferedImage;
 import cn.magicst.mamiyaotaru.voxelmap.util.MapChunkCache;
 import cn.magicst.mamiyaotaru.voxelmap.util.MapUtils;
 import cn.magicst.mamiyaotaru.voxelmap.util.MutableBlockPos;
 import cn.magicst.mamiyaotaru.voxelmap.util.MutableNativeImageBackedTexture;
 import cn.magicst.mamiyaotaru.voxelmap.util.ReflectionUtils;
 import cn.magicst.mamiyaotaru.voxelmap.util.ScaledMutableNativeImageBackedTexture;
 import cn.magicst.mamiyaotaru.voxelmap.util.TickCounter;
 import cn.magicst.mamiyaotaru.voxelmap.util.Waypoint;
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.awt.AlphaComposite;
 import java.awt.Graphics2D;
 import java.awt.Image;
 import java.awt.image.BufferedImage;
 import java.awt.image.ImageObserver;
 import java.io.InputStream;
 import java.lang.reflect.Field;
 import java.nio.ByteBuffer;
 import java.nio.ByteOrder;
 import java.nio.FloatBuffer;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Random;
 import java.util.TreeSet;
 import javax.imageio.ImageIO;
 import net.minecraft.class_1043;
 import net.minecraft.class_1159;
 import net.minecraft.class_1160;
 import net.minecraft.class_124;
 import net.minecraft.class_1293;
 import net.minecraft.class_1294;
 import net.minecraft.class_1922;
 import net.minecraft.class_1937;
 import net.minecraft.class_1944;
 import net.minecraft.class_2246;
 import net.minecraft.class_2248;
 import net.minecraft.class_2338;
 import net.minecraft.class_2350;
 import net.minecraft.class_2378;
 import net.minecraft.class_2561;
 import net.minecraft.class_259;
 import net.minecraft.class_265;
 import net.minecraft.class_2680;
 import net.minecraft.class_2818;
 import net.minecraft.class_286;
 import net.minecraft.class_287;
 import net.minecraft.class_289;
 import net.minecraft.class_290;
 import net.minecraft.class_2902;
 import net.minecraft.class_2960;
 import net.minecraft.class_304;
 import net.minecraft.class_310;
 import net.minecraft.class_315;
 import net.minecraft.class_327;
 import net.minecraft.class_3298;
 import net.minecraft.class_3532;
 import net.minecraft.class_3610;
 import net.minecraft.class_3612;
 import net.minecraft.class_3614;
 import net.minecraft.class_4060;
 import net.minecraft.class_437;
 import net.minecraft.class_4587;
 import net.minecraft.class_5348;
 import net.minecraft.class_5539;
 import net.minecraft.class_638;
 import net.minecraft.class_757;
 import net.minecraft.class_758;
 import net.minecraft.class_765;
 import org.lwjgl.BufferUtils;
 
 public class Map implements Runnable, IMap {
   private final float[] lastLightBrightnessTable = new float[16];
   private final Object coordinateLock = new Object();
   private final int SEAFLOORLAYER = 0;
   private final int GROUNDLAYER = 1;
   private final int FOLIAGELAYER = 2;
   private final int TRANSPARENTLAYER = 3;
   private final float SQRT2 = 1.4142F;
   private final class_2960 arrowResourceLocation = new class_2960("voxelmap", "images/mmarrow.png");
   private final class_2960 roundmapResourceLocation = new class_2960("voxelmap", "images/roundmap.png");
   private final class_2960 squareStencil = new class_2960("voxelmap", "images/square.png");
   private final class_2960 circleStencil = new class_2960("voxelmap", "images/circle.png");
   LiveScaledGLBufferedImage roundImage = new LiveScaledGLBufferedImage(128, 128, 6);
   private IVoxelMap master;
   private class_310 game;
   private String zmodver = "v1.10.18";
   private class_638 world = null;
   private MapSettingsManager options = null;
   private LayoutVariables layoutVariables = null;
   private IColorManager colorManager = null;
   private IWaypointManager waypointManager = null;
   private int availableProcessors = Runtime.getRuntime().availableProcessors();
   private boolean multicore = (this.availableProcessors > 1);
   private int heightMapResetHeight = this.multicore ? 2 : 5;
   private int heightMapResetTime = this.multicore ? 300 : 3000;
   private boolean threading = this.multicore;
   private FullMapData[] mapData = new FullMapData[5];
   private MapChunkCache[] chunkCache = new MapChunkCache[5];
   private MutableNativeImageBackedTexture[] mapImages;
   private MutableNativeImageBackedTexture[] mapImagesFiltered = new MutableNativeImageBackedTexture[5];
   private MutableNativeImageBackedTexture[] mapImagesUnfiltered = new MutableNativeImageBackedTexture[5];
   private MutableBlockPos blockPos = new MutableBlockPos(0, 0, 0);
   private MutableBlockPos tempBlockPos = new MutableBlockPos(0, 0, 0);
   private class_2680 transparentBlockState;
   private class_2680 surfaceBlockState;
   private class_2680 seafloorBlockState;
   private class_2680 foliageBlockState;
   private boolean imageChanged = true;
   private class_1043 lightmapTexture = null;
   private boolean needLightmapRefresh = true;
   private int tickWithLightChange = 0;
   private boolean lastPaused = true;
   private double lastGamma = 0.0D;
   private float lastSunBrightness = 0.0F;
   private float lastLightning = 0.0F;
   private float lastPotion = 0.0F;
   private int[] lastLightmapValues = new int[] { -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216 };
   private boolean lastBeneathRendering = false;
   private boolean needSkyColor = false;
   private boolean lastAboveHorizon = true;
   private int lastBiome = 0;
   private int lastSkyColor = 0;
   private Random generator = new Random();
   private boolean showWelcomeScreen = true;
   private class_437 lastGuiScreen = null;
   private boolean enabled = true;
   private boolean fullscreenMap = false;
   private boolean active = false;
   private int zoom = 2;
   private int mapX = 37;
   private int mapY = 37;
   private int scWidth;
   private int scHeight;
   private String error = "";
   private class_2561[] welcomeText = new class_2561[8];
   private int ztimer = 0;
   private int heightMapFudge = 0;
   private int timer = 0;
   private boolean doFullRender = true;
   private boolean zoomChanged;
   private int lastX = 0;
   private int lastZ = 0;
   private int lastY = 0;
   private int lastImageX = 0;
   private int lastImageZ = 0;
   private boolean lastFullscreen = false;
   private float direction = 0.0F;
   private float percentX;
   private float percentY;
   private String subworldName = "";
   private int northRotate = 0;
   private Thread zCalc = new Thread(this, "Voxelmap LiveMap Calculation Thread");
   private int zCalcTicker = 0;
   private class_327 fontRenderer;
   private int[] lightmapColors = new int[256];
   private double zoomScale = 1.0D;
   private double zoomScaleAdjusted = 1.0D;
   private int count = 0;
   private int mapImageInt = -1;
   
   public Map(IVoxelMap master) {
     this.master = master;
     this.game = GameVariableAccessShim.getMinecraft();
     this.options = master.getMapOptions();
     this.colorManager = master.getColorManager();
     this.waypointManager = master.getWaypointManager();
     this.layoutVariables = new LayoutVariables();
     ArrayList tempBindings = new ArrayList();
     tempBindings.addAll(Arrays.asList(this.game.field_1690.field_1839));
     tempBindings.addAll(Arrays.asList(this.options.keyBindings));
     Field f = ReflectionUtils.getFieldByType(this.game.field_1690, class_315.class, class_304[].class, 1);
     
     try {
       f.set(this.game.field_1690, tempBindings.toArray((Object[])new class_304[tempBindings.size()]));
     } catch (IllegalArgumentException var7) {
       var7.printStackTrace();
     } catch (IllegalAccessException var8) {
       var8.printStackTrace();
     } 
     
     java.util.Map<String, Integer> categoryOrder = (java.util.Map)ReflectionUtils.getPrivateFieldValueByType(null, class_304.class, java.util.Map.class, 2);
     System.out.println("CATEGORY ORDER IS " + categoryOrder.size());
     Integer categoryPlace = (Integer)categoryOrder.get("controls.minimap.title");
     if (categoryPlace == null) {
       int currentSize = categoryOrder.size();
       categoryOrder.put("controls.minimap.title", Integer.valueOf(currentSize + 1));
     } 
     
     this.showWelcomeScreen = this.options.welcome;
     this.zCalc.start();
     this.zCalc.setPriority(5);
     this.mapData[0] = new FullMapData(32, 32);
     this.mapData[1] = new FullMapData(64, 64);
     this.mapData[2] = new FullMapData(128, 128);
     this.mapData[3] = new FullMapData(256, 256);
     this.mapData[4] = new FullMapData(512, 512);
     this.chunkCache[0] = new MapChunkCache(3, 3, (IChangeObserver)this);
     this.chunkCache[1] = new MapChunkCache(5, 5, (IChangeObserver)this);
     this.chunkCache[2] = new MapChunkCache(9, 9, (IChangeObserver)this);
     this.chunkCache[3] = new MapChunkCache(17, 17, (IChangeObserver)this);
     this.chunkCache[4] = new MapChunkCache(33, 33, (IChangeObserver)this);
     this.mapImagesFiltered[0] = new MutableNativeImageBackedTexture(32, 32, true);
     this.mapImagesFiltered[1] = new MutableNativeImageBackedTexture(64, 64, true);
     this.mapImagesFiltered[2] = new MutableNativeImageBackedTexture(128, 128, true);
     this.mapImagesFiltered[3] = new MutableNativeImageBackedTexture(256, 256, true);
     this.mapImagesFiltered[4] = new MutableNativeImageBackedTexture(512, 512, true);
     this.mapImagesUnfiltered[0] = (MutableNativeImageBackedTexture)new ScaledMutableNativeImageBackedTexture(32, 32, true);
     this.mapImagesUnfiltered[1] = (MutableNativeImageBackedTexture)new ScaledMutableNativeImageBackedTexture(64, 64, true);
     this.mapImagesUnfiltered[2] = (MutableNativeImageBackedTexture)new ScaledMutableNativeImageBackedTexture(128, 128, true);
     this.mapImagesUnfiltered[3] = (MutableNativeImageBackedTexture)new ScaledMutableNativeImageBackedTexture(256, 256, true);
     this.mapImagesUnfiltered[4] = (MutableNativeImageBackedTexture)new ScaledMutableNativeImageBackedTexture(512, 512, true);
     if (this.options.filtering) {
       this.mapImages = this.mapImagesFiltered;
     } else {
       this.mapImages = this.mapImagesUnfiltered;
     } 
     
     GLUtils.setupFrameBuffer();
     this.fontRenderer = this.game.field_1772;
     this.zoom = this.options.zoom;
     setZoomScale();
   }
 
   
   public void forceFullRender(boolean forceFullRender) {
     this.doFullRender = forceFullRender;
     this.master.getSettingsAndLightingChangeNotifier().notifyOfChanges();
   }
 
   
   public float getPercentX() {
     return this.percentX;
   }
 
   
   public float getPercentY() {
     return this.percentY;
   }
   
   public void run() {
     if (this.game != null) {
       while (true) {
         while (!this.threading) {
           synchronized (this.zCalc) {
             try {
               this.zCalc.wait(0L);
             } catch (InterruptedException interruptedException) {}
           } 
         } 
 
         
         for (this.active = true; this.game.field_1724 != null && this.world != null && this.active; this.active = false) {
           if (!this.options.hide) {
             try {
               mapCalc(this.doFullRender);
               if (!this.doFullRender) {
                 this.chunkCache[this.zoom].centerChunks((class_2338)this.blockPos.withXYZ(this.lastX, 0, this.lastZ));
                 this.chunkCache[this.zoom].checkIfChunksChanged();
               } 
             } catch (Exception exception) {}
           }
 
           
           this.doFullRender = this.zoomChanged;
           this.zoomChanged = false;
         } 
         
         this.zCalcTicker = 0;
         synchronized (this.zCalc) {
           try {
             this.zCalc.wait(0L);
           } catch (InterruptedException interruptedException) {}
         } 
       } 
     }
   }
 
 
 
   
   public void newWorld(class_638 world) {
     this.world = world;
     this.lightmapTexture = getLightmapTexture();
     this.mapData[this.zoom].blank();
     this.mapImages[this.zoom].blank();
     this.doFullRender = true;
     this.master.getSettingsAndLightingChangeNotifier().notifyOfChanges();
   }
 
   
   public void newWorldName() {
     this.subworldName = this.waypointManager.getCurrentSubworldDescriptor(true);
     StringBuilder subworldNameBuilder = (new StringBuilder("§r")).append(I18nUtils.getString("worldmap.multiworld.newworld", new Object[0])).append(":").append(" ");
     if (this.subworldName.equals("") && this.waypointManager.isMultiworld()) {
       subworldNameBuilder.append("???");
     } else if (!this.subworldName.equals("")) {
       subworldNameBuilder.append(this.subworldName);
     } 
     
     this.error = subworldNameBuilder.toString();
   }
 
   
   public void onTickInGame(class_4587 matrixStack, class_310 mc) {
     this.northRotate = this.options.oldNorth ? 90 : 0;
     if (this.game == null) {
       this.game = mc;
     }
     
     if (this.lightmapTexture == null) {
       this.lightmapTexture = getLightmapTexture();
     }
     
     if (this.game.field_1755 == null && this.options.keyBindMenu.method_1436()) {
       this.showWelcomeScreen = false;
       if (this.options.welcome) {
         this.options.welcome = false;
         this.options.saveAll();
       } 
       
       this.game.method_1507((class_437)new GuiPersistentMap((class_437)null, this.master));
     } 
     
     if (this.game.field_1755 == null && this.options.keyBindWaypointMenu.method_1436()) {
       this.showWelcomeScreen = false;
       if (this.options.welcome) {
         this.options.welcome = false;
         this.options.saveAll();
       } 
       
       this.game.method_1507((class_437)new GuiWaypoints((class_437)null, this.master));
     } 
     
     if (this.game.field_1755 == null && this.options.keyBindWaypoint.method_1436()) {
       float r, g, b; this.showWelcomeScreen = false;
       if (this.options.welcome) {
         this.options.welcome = false;
         this.options.saveAll();
       } 
 
 
 
       
       if (this.waypointManager.getWaypoints().size() == 0) {
         r = 0.0F;
         g = 1.0F;
         b = 0.0F;
       } else {
         r = this.generator.nextFloat();
         g = this.generator.nextFloat();
         b = this.generator.nextFloat();
       } 
       
       TreeSet<DimensionContainer> dimensions = new TreeSet();
       dimensions.add(AbstractVoxelMap.getInstance().getDimensionManager().getDimensionContainerByWorld((class_1937)this.game.field_1687));
       double dimensionScale = this.game.field_1724.field_6002.method_8597().comp_646();
       Waypoint newWaypoint = new Waypoint("", (int)(GameVariableAccessShim.xCoord() * dimensionScale), (int)(GameVariableAccessShim.zCoord() * dimensionScale), GameVariableAccessShim.yCoord(), true, r, g, b, "", this.master.getWaypointManager().getCurrentSubworldDescriptor(false), dimensions);
       this.game.method_1507((class_437)new GuiAddWaypoint((IGuiWaypoints)null, this.master, newWaypoint, false));
     } 
     
     if (this.game.field_1755 == null && this.options.keyBindMobToggle.method_1436()) {
       this.master.getRadarOptions().setOptionValue(EnumOptionsMinimap.SHOWRADAR);
       this.options.saveAll();
     } 
     
     if (this.game.field_1755 == null && this.options.keyBindWaypointToggle.method_1436()) {
       this.options.toggleIngameWaypoints();
     }
     
     if (this.game.field_1755 == null && this.options.keyBindZoom.method_1436()) {
       this.showWelcomeScreen = false;
       if (this.options.welcome) {
         this.options.welcome = false;
         this.options.saveAll();
       } else {
         cycleZoomLevel();
       } 
     } 
     
     if (this.game.field_1755 == null && this.options.keyBindFullscreen.method_1436()) {
       this.fullscreenMap = !this.fullscreenMap;
       if (this.zoom == 4) {
         this.error = I18nUtils.getString("minimap.ui.zoomlevel", new Object[0]) + " (0.25x)";
       } else if (this.zoom == 3) {
         this.error = I18nUtils.getString("minimap.ui.zoomlevel", new Object[0]) + " (0.5x)";
       } else if (this.zoom == 2) {
         this.error = I18nUtils.getString("minimap.ui.zoomlevel", new Object[0]) + " (1.0x)";
       } else if (this.zoom == 1) {
         this.error = I18nUtils.getString("minimap.ui.zoomlevel", new Object[0]) + " (2.0x)";
       } else {
         this.error = I18nUtils.getString("minimap.ui.zoomlevel", new Object[0]) + " (4.0x)";
       } 
     } 
     
     checkForChanges();
     if (this.game.field_1755 instanceof net.minecraft.class_418 && !(this.lastGuiScreen instanceof net.minecraft.class_418)) {
       this.waypointManager.handleDeath();
     }
     
     this.lastGuiScreen = this.game.field_1755;
     calculateCurrentLightAndSkyColor();
     if (this.threading) {
       if (!this.zCalc.isAlive() && this.threading) {
         this.zCalc = new Thread(this, "Voxelmap LiveMap Calculation Thread");
         this.zCalc.setPriority(5);
         this.zCalc.start();
       } 
       
       if (!(this.game.field_1755 instanceof net.minecraft.class_418) && !(this.game.field_1755 instanceof net.minecraft.class_428)) {
         this.zCalcTicker++;
         if (this.zCalcTicker > 2000) {
           this.zCalcTicker = 0;
           this.zCalc.stop();
         } else {
           synchronized (this.zCalc) {
             this.zCalc.notify();
           } 
         } 
       } 
     } else if (!this.threading) {
       if (!this.options.hide && this.world != null) {
         mapCalc(this.doFullRender);
         if (!this.doFullRender) {
           this.chunkCache[this.zoom].centerChunks((class_2338)this.blockPos.withXYZ(this.lastX, 0, this.lastZ));
           this.chunkCache[this.zoom].checkIfChunksChanged();
         } 
       } 
       
       this.doFullRender = false;
     } 
     
     if (!mc.field_1690.field_1842 && (this.options.showUnderMenus || this.game.field_1755 == null) && !this.game.field_1690.field_1866) {
       this.enabled = true;
     } else {
       this.enabled = false;
     } 
     
     this.direction = GameVariableAccessShim.rotationYaw() + 180.0F;
     
     while (this.direction >= 360.0F) {
       this.direction -= 360.0F;
     }
     
     while (this.direction < 0.0F) {
       this.direction += 360.0F;
     }
     
     if (!this.error.equals("") && this.ztimer == 0) {
       this.ztimer = 500;
     }
     
     if (this.ztimer > 0) {
       this.ztimer--;
     }
     
     if (this.ztimer == 0 && !this.error.equals("")) {
       this.error = "";
     }
     
     if (this.enabled) {
       drawMinimap(matrixStack, mc);
     }
     
     this.timer = (this.timer > 5000) ? 0 : (this.timer + 1);
   }
   
   private void cycleZoomLevel() {
     if (this.options.zoom == 4) {
       this.options.zoom = 3;
       this.error = I18nUtils.getString("minimap.ui.zoomlevel", new Object[0]) + " (0.5x)";
     } else if (this.options.zoom == 3) {
       this.options.zoom = 2;
       this.error = I18nUtils.getString("minimap.ui.zoomlevel", new Object[0]) + " (1.0x)";
     } else if (this.options.zoom == 2) {
       this.options.zoom = 1;
       this.error = I18nUtils.getString("minimap.ui.zoomlevel", new Object[0]) + " (2.0x)";
     } else if (this.options.zoom == 1) {
       this.options.zoom = 0;
       this.error = I18nUtils.getString("minimap.ui.zoomlevel", new Object[0]) + " (4.0x)";
     } else if (this.options.zoom == 0) {
       if (this.multicore && ((Integer)this.game.field_1690.method_42510().method_41753()).intValue() > 8) {
         this.options.zoom = 4;
         this.error = I18nUtils.getString("minimap.ui.zoomlevel", new Object[0]) + " (0.25x)";
       } else {
         this.options.zoom = 3;
         this.error = I18nUtils.getString("minimap.ui.zoomlevel", new Object[0]) + " (0.5x)";
       } 
     } 
     
     this.options.saveAll();
     this.zoomChanged = true;
     this.zoom = this.options.zoom;
     setZoomScale();
     this.mapImages[this.zoom].blank();
     this.doFullRender = true;
   }
   
   private void setZoomScale() {
     this.zoomScale = Math.pow(2.0D, this.zoom) / 2.0D;
     if (this.options.squareMap && this.options.rotates) {
       this.zoomScaleAdjusted = this.zoomScale / 1.414199948310852D;
     } else {
       this.zoomScaleAdjusted = this.zoomScale;
     } 
   }
 
   
   private class_1043 getLightmapTexture() {
     class_765 lightTextureManager = this.game.field_1773.method_22974();
     Object lightmapTextureObj = ReflectionUtils.getPrivateFieldValueByType(lightTextureManager, class_765.class, class_1043.class);
     return (lightmapTextureObj == null) ? null : (class_1043)lightmapTextureObj;
   }
   
   public void calculateCurrentLightAndSkyColor() {
     try {
       if (this.world != null) {
         if ((this.needLightmapRefresh && TickCounter.tickCounter != this.tickWithLightChange && !this.game.method_1493()) || this.options.realTimeTorches) {
           GLUtils.disp(this.lightmapTexture.method_4624());
           ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024).order(ByteOrder.nativeOrder());
           GLShim.glGetTexImage(3553, 0, 6408, 5121, byteBuffer);
           
           for (int i = 0; i < this.lightmapColors.length; i++) {
             int index = i * 4;
             this.lightmapColors[i] = (byteBuffer.get(index + 3) << 24) + (byteBuffer.get(index) << 16) + (byteBuffer.get(index + 1) << 8) + (byteBuffer.get(index + 2) << 0);
           } 
           
           if (this.lightmapColors[255] != 0) {
             this.needLightmapRefresh = false;
           }
         } 
         
         boolean lightChanged = false;
         if (((Double)this.game.field_1690.method_42473().method_41753()).doubleValue() != this.lastGamma) {
           lightChanged = true;
           this.lastGamma = ((Double)this.game.field_1690.method_42473().method_41753()).doubleValue();
         } 
         
         float[] providerLightBrightnessTable = new float[16];
         int t;
         for (t = 0; t < 16; t++) {
           providerLightBrightnessTable[t] = this.world.method_8597().method_28528(t);
         }
         
         for (t = 0; t < 16; t++) {
           if (providerLightBrightnessTable[t] != this.lastLightBrightnessTable[t]) {
             lightChanged = true;
             this.lastLightBrightnessTable[t] = providerLightBrightnessTable[t];
           } 
         } 
         
         float sunBrightness = this.world.method_23783(1.0F);
         if (Math.abs(this.lastSunBrightness - sunBrightness) > 0.01D || (sunBrightness == 1.0D && sunBrightness != this.lastSunBrightness) || (sunBrightness == 0.0D && sunBrightness != this.lastSunBrightness)) {
           lightChanged = true;
           this.needSkyColor = true;
           this.lastSunBrightness = sunBrightness;
         } 
         
         float potionEffect = 0.0F;
         if (this.game.field_1724.method_6059(class_1294.field_5925)) {
           int duration = this.game.field_1724.method_6112(class_1294.field_5925).method_5584();
           potionEffect = (duration > 200) ? 1.0F : (0.7F + class_3532.method_15374((duration - 1.0F) * 3.1415927F * 0.2F) * 0.3F);
         } 
         
         if (this.lastPotion != potionEffect) {
           this.lastPotion = potionEffect;
           lightChanged = true;
         } 
         
         int lastLightningBolt = this.world.method_23789();
         if (this.lastLightning != lastLightningBolt) {
           this.lastLightning = lastLightningBolt;
           lightChanged = true;
         } 
         
         if (this.lastPaused != this.game.method_1493()) {
           this.lastPaused = !this.lastPaused;
           lightChanged = true;
         } 
         
         boolean scheduledUpdate = ((this.timer - 50) % ((this.lastLightBrightnessTable[0] == 0.0F) ? 250 : 2000) == 0);
         if (lightChanged || scheduledUpdate) {
           this.tickWithLightChange = TickCounter.tickCounter;
           lightChanged = false;
           this.needLightmapRefresh = true;
         } 
         
         boolean aboveHorizon = ((this.game.field_1724.method_5836(0.0F)).field_1351 >= this.world.method_28104().method_28105((class_5539)this.world));
         if (this.world.method_27983().method_29177().toString().toLowerCase().contains("ether")) {
           aboveHorizon = true;
         }
         
         if (aboveHorizon != this.lastAboveHorizon) {
           this.needSkyColor = true;
           this.lastAboveHorizon = aboveHorizon;
         } 
         
         int biomeID = this.world.method_30349().method_30530(class_2378.field_25114).method_10206(this.world.method_23753((class_2338)this.blockPos.withXYZ(GameVariableAccessShim.xCoord(), GameVariableAccessShim.yCoord(), GameVariableAccessShim.zCoord())).comp_349());
         if (biomeID != this.lastBiome) {
           this.needSkyColor = true;
           this.lastBiome = biomeID;
         } 
         
         if (this.needSkyColor || scheduledUpdate) {
           this.colorManager.setSkyColor(getSkyColor());
         }
       } 
     } catch (NullPointerException nullPointerException) {}
   }
 
 
   
   private int getSkyColor() {
     this.needSkyColor = false;
     boolean aboveHorizon = this.lastAboveHorizon;
     float[] fogColors = new float[4];
     FloatBuffer temp = BufferUtils.createFloatBuffer(4);
     class_758.method_3210(this.game.field_1773.method_19418(), 0.0F, this.world, ((Integer)this.game.field_1690.method_42503().method_41753()).intValue(), this.game.field_1773.method_3195(0.0F));
     GLShim.glGetFloatv(3106, temp);
     temp.get(fogColors);
     float r = fogColors[0];
     float g = fogColors[1];
     float b = fogColors[2];
     if (!aboveHorizon && ((Integer)this.game.field_1690.method_42503().method_41753()).intValue() >= 4) {
       return 167772160 + (int)(r * 255.0F) * 65536 + (int)(g * 255.0F) * 256 + (int)(b * 255.0F);
     }
     int backgroundColor = -16777216 + (int)(r * 255.0F) * 65536 + (int)(g * 255.0F) * 256 + (int)(b * 255.0F);
     float[] sunsetColors = this.world.method_28103().method_28109(this.world.method_30274(0.0F), 0.0F);
     if (sunsetColors != null && ((Integer)this.game.field_1690.method_42503().method_41753()).intValue() >= 4) {
       int sunsetColor = (int)(sunsetColors[3] * 128.0F) * 16777216 + (int)(sunsetColors[0] * 255.0F) * 65536 + (int)(sunsetColors[1] * 255.0F) * 256 + (int)(sunsetColors[2] * 255.0F);
       return ColorUtils.colorAdder(sunsetColor, backgroundColor);
     } 
     return backgroundColor;
   }
 
 
 
   
   public int[] getLightmapArray() {
     return this.lightmapColors;
   }
 
   
   public void drawMinimap(class_4587 matrixStack, class_310 mc) {
     int scScaleOrig = 1;
     
     while (this.game.method_22683().method_4489() / (scScaleOrig + 1) >= 320 && this.game.method_22683().method_4506() / (scScaleOrig + 1) >= 240) {
       scScaleOrig++;
     }
     
     int scScale = scScaleOrig + (this.fullscreenMap ? 0 : this.options.sizeModifier);
     double scaledWidthD = this.game.method_22683().method_4489() / scScale;
     double scaledHeightD = this.game.method_22683().method_4506() / scScale;
     this.scWidth = class_3532.method_15384(scaledWidthD);
     this.scHeight = class_3532.method_15384(scaledHeightD);
     RenderSystem.backupProjectionMatrix();
     class_1159 matrix4f = class_1159.method_34239(0.0F, (float)scaledWidthD, 0.0F, (float)scaledHeightD, 1000.0F, 3000.0F);
     RenderSystem.setProjectionMatrix(matrix4f);
     class_4587 modelViewMatrixStack = RenderSystem.getModelViewStack();
     modelViewMatrixStack.method_34426();
     modelViewMatrixStack.method_22904(0.0D, 0.0D, -2000.0D);
     RenderSystem.applyModelViewMatrix();
     class_308.method_24211();
     if (this.options.mapCorner != 0 && this.options.mapCorner != 3) {
       this.mapX = this.scWidth - 37;
     } else {
       this.mapX = 37;
     } 
     
     if (this.options.mapCorner != 0 && this.options.mapCorner != 1) {
       this.mapY = this.scHeight - 37;
     } else {
       this.mapY = 37;
     } 
     
     if (this.options.mapCorner == 1 && this.game.field_1724.method_6026().size() > 0) {
       float statusIconOffset = 0.0F;
       
       for (class_1293 statusEffectInstance : this.game.field_1724.method_6026()) {
         if (statusEffectInstance.method_5592()) {
           if (statusEffectInstance.method_5579().method_5573()) {
             statusIconOffset = Math.max(statusIconOffset, 24.0F); continue;
           } 
           statusIconOffset = Math.max(statusIconOffset, 50.0F);
         } 
       } 
 
       
       int scHeight = this.game.method_22683().method_4502();
       float resFactor = this.scHeight / scHeight;
       this.mapY += (int)(statusIconOffset * resFactor);
     } 
     
     GLShim.glEnable(3042);
     GLShim.glEnable(3553);
     GLShim.glBlendFunc(770, 0);
     GLShim.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
     if (!this.options.hide) {
       if (this.fullscreenMap) {
         renderMapFull(modelViewMatrixStack, this.scWidth, this.scHeight);
       } else {
         renderMap(modelViewMatrixStack, this.mapX, this.mapY, scScale);
       } 
       
       GLShim.glDisable(2929);
       if (this.master.getRadar() != null && !this.fullscreenMap) {
         this.layoutVariables.updateVars(scScale, this.mapX, this.mapY, this.zoomScale, this.zoomScaleAdjusted);
         this.master.getRadar().onTickInGame(modelViewMatrixStack, mc, this.layoutVariables);
       } 
       
       if (!this.fullscreenMap) {
         drawDirections(matrixStack, this.mapX, this.mapY);
       }
       
       GLShim.glEnable(3042);
       if (this.fullscreenMap) {
         drawArrow(modelViewMatrixStack, this.scWidth / 2, this.scHeight / 2);
       } else {
         drawArrow(modelViewMatrixStack, this.mapX, this.mapY);
       } 
     } 
     
     if (this.options.coords) {
       showCoords(matrixStack, this.mapX, this.mapY);
     }
     
     GLShim.glDepthMask(true);
     GLShim.glEnable(2929);
     GLShim.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
     RenderSystem.restoreProjectionMatrix();
     RenderSystem.applyModelViewMatrix();
     GLShim.glDisable(2929);
     GLShim.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     this.game.field_1772.getClass();
     this.game.field_1772.method_30881(modelViewMatrixStack, (class_2561)class_2561.method_43470("******sdkfjhsdkjfhsdkjfh"), 100.0F, 100.0F, -1);
     if (this.showWelcomeScreen) {
       GLShim.glEnable(3042);
       drawWelcomeScreen(matrixStack, this.game.method_22683().method_4486(), this.game.method_22683().method_4502());
     } 
     
     GLShim.glDepthMask(true);
     GLShim.glEnable(2929);
     GLShim.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
     GLShim.glTexParameteri(3553, 10241, 9728);
     GLShim.glTexParameteri(3553, 10240, 9728);
   }
   
   private void checkForChanges() {
     boolean changed = false;
     if (this.colorManager.checkForChanges()) {
       loadMapImage();
       changed = true;
     } 
     
     if (this.options.isChanged()) {
       if (this.options.filtering) {
         this.mapImages = this.mapImagesFiltered;
       } else {
         this.mapImages = this.mapImagesUnfiltered;
       } 
       
       changed = true;
       setZoomScale();
     } 
     
     if (changed) {
       this.doFullRender = true;
       this.master.getSettingsAndLightingChangeNotifier().notifyOfChanges();
     } 
   }
 
   
   private void mapCalc(boolean full) {
     int currentX = GameVariableAccessShim.xCoord();
     int currentZ = GameVariableAccessShim.zCoord();
     int currentY = GameVariableAccessShim.yCoord();
     int offsetX = currentX - this.lastX;
     int offsetZ = currentZ - this.lastZ;
     int offsetY = currentY - this.lastY;
     int multi = (int)Math.pow(2.0D, this.zoom);
     
     boolean needHeightMap = false;
     boolean needLight = false;
     boolean skyColorChanged = false;
     int skyColor = this.colorManager.getAirColor();
     if (this.lastSkyColor != skyColor) {
       skyColorChanged = true;
       this.lastSkyColor = skyColor;
     } 
     
     if (this.options.lightmap) {
       int torchOffset = this.options.realTimeTorches ? 8 : 0;
       int skylightMultiplier = 16;
       
       for (int t = 0; t < 16; t++) {
         if (this.lastLightmapValues[t] != this.lightmapColors[t * skylightMultiplier + torchOffset]) {
           needLight = true;
           this.lastLightmapValues[t] = this.lightmapColors[t * skylightMultiplier + torchOffset];
         } 
       } 
     } 
     
     if (offsetY != 0) {
       this.heightMapFudge++;
     } else if (this.heightMapFudge != 0) {
       this.heightMapFudge++;
     } 
     
     if (full || Math.abs(offsetY) >= this.heightMapResetHeight || this.heightMapFudge > this.heightMapResetTime) {
       if (this.lastY != currentY) {
         needHeightMap = true;
       }
       
       this.lastY = currentY;
       this.heightMapFudge = 0;
     } 
     
     if (Math.abs(offsetX) > 32 * multi || Math.abs(offsetZ) > 32 * multi) {
       full = true;
     }
     
     boolean nether = false;
     boolean caves = false;
     
     this.blockPos.setXYZ(this.lastX, Math.max(Math.min(GameVariableAccessShim.yCoord(), 255), 0), this.lastZ);
     if (this.game.field_1724.field_6002.method_8597().comp_643()) {
       
       boolean netherPlayerInOpen = (this.world.method_22350((class_2338)this.blockPos).method_12005(class_2902.class_2903.field_13197, this.blockPos.method_10263() & 0xF, this.blockPos.method_10260() & 0xF) <= currentY);
       nether = (currentY < 126);
       if (this.options.cavesAllowed.booleanValue() && this.options.showCaves && currentY >= 126 && !netherPlayerInOpen) {
         caves = true;
       }
     } else if (this.game.field_1724.field_17892.method_28103().method_28114() && !this.game.field_1724.field_17892.method_8597().comp_642()) {
       boolean endPlayerInOpen = (this.world.method_22350((class_2338)this.blockPos).method_12005(class_2902.class_2903.field_13197, this.blockPos.method_10263() & 0xF, this.blockPos.method_10260() & 0xF) <= currentY);
       if (this.options.cavesAllowed.booleanValue() && this.options.showCaves && !endPlayerInOpen) {
         caves = true;
       }
     } else if (this.options.cavesAllowed.booleanValue() && this.options.showCaves && this.world.method_8314(class_1944.field_9284, (class_2338)this.blockPos) <= 0) {
       caves = true;
     } 
     
     boolean beneathRendering = (caves || nether);
     if (this.lastBeneathRendering != beneathRendering) {
       full = true;
     }
     
     this.lastBeneathRendering = beneathRendering;
     boolean needHeightAndID = (needHeightMap && (nether || caves));
     int color24 = -1;
     synchronized (this.coordinateLock) {
       if (!full) {
         this.mapImages[this.zoom].moveY(offsetZ);
         this.mapImages[this.zoom].moveX(offsetX);
       } 
       
       this.lastX = currentX;
       this.lastZ = currentZ;
     } 
     
     int startX = currentX - 16 * multi;
     int startZ = currentZ - 16 * multi;
     if (!full) {
       this.mapData[this.zoom].moveZ(offsetZ);
       this.mapData[this.zoom].moveX(offsetX);
       int imageY;
       for (imageY = (offsetZ > 0) ? (32 * multi - 1) : (-offsetZ - 1); imageY >= ((offsetZ > 0) ? (32 * multi - offsetZ) : 0); imageY--) {
         for (int imageX = 0; imageX < 32 * multi; imageX++) {
           color24 = getPixelColor(true, true, true, true, nether, caves, (class_1937)this.world, multi, startX, startZ, imageX, imageY);
           this.mapImages[this.zoom].setRGB(imageX, imageY, color24);
         } 
       } 
       
       for (imageY = 32 * multi - 1; imageY >= 0; ) {
         int imageX = (offsetX > 0) ? (32 * multi - offsetX) : 0; for (;; imageY--) { if (imageX < ((offsetX > 0) ? (32 * multi) : -offsetX)) {
             color24 = getPixelColor(true, true, true, true, nether, caves, (class_1937)this.world, multi, startX, startZ, imageX, imageY);
             this.mapImages[this.zoom].setRGB(imageX, imageY, color24); imageX++; continue;
           }  }
       
       } 
     } 
     if (full || (this.options.heightmap && needHeightMap) || needHeightAndID || (this.options.lightmap && needLight) || skyColorChanged) {
       for (int imageY = 32 * multi - 1; imageY >= 0; imageY--) {
         for (int imageX = 0; imageX < 32 * multi; imageX++) {
           color24 = getPixelColor(full, (full || needHeightAndID), full, (full || needLight || needHeightAndID), nether, caves, (class_1937)this.world, multi, startX, startZ, imageX, imageY);
           this.mapImages[this.zoom].setRGB(imageX, imageY, color24);
         } 
       } 
     }
     
     if ((full || offsetX != 0 || offsetZ != 0 || !this.lastFullscreen) && this.fullscreenMap && this.options.biomeOverlay != 0) {
       this.mapData[this.zoom].segmentBiomes();
       this.mapData[this.zoom].findCenterOfSegments(!this.options.oldNorth);
     } 
     
     this.lastFullscreen = this.fullscreenMap;
     if (full || offsetX != 0 || offsetZ != 0 || needHeightMap || needLight || skyColorChanged) {
       this.imageChanged = true;
     }
     
     if (needLight || skyColorChanged) {
       this.master.getSettingsAndLightingChangeNotifier().notifyOfChanges();
     }
   }
 
 
   
   public void handleChangeInWorld(int chunkX, int chunkZ) {
     try {
       this.chunkCache[this.zoom].registerChangeAt(chunkX, chunkZ);
     } catch (Exception e) {
       e.printStackTrace();
     } 
   }
 
   
   public void processChunk(class_2818 chunk) {
     rectangleCalc((chunk.method_12004()).field_9181 * 16, (chunk.method_12004()).field_9180 * 16, (chunk.method_12004()).field_9181 * 16 + 15, (chunk.method_12004()).field_9180 * 16 + 15);
   }
   
   private void rectangleCalc(int left, int top, int right, int bottom) {
     boolean nether = false;
     boolean caves = false;
     boolean netherPlayerInOpen = false;
     this.blockPos.setXYZ(this.lastX, Math.max(Math.min(GameVariableAccessShim.yCoord(), 255), 0), this.lastZ);
     int currentY = GameVariableAccessShim.yCoord();
     if (this.game.field_1724.field_6002.method_8597().comp_643()) {
       netherPlayerInOpen = (this.world.method_22350((class_2338)this.blockPos).method_12005(class_2902.class_2903.field_13197, this.blockPos.method_10263() & 0xF, this.blockPos.method_10260() & 0xF) <= currentY);
       nether = (currentY < 126);
       if (this.options.cavesAllowed.booleanValue() && this.options.showCaves && currentY >= 126 && !netherPlayerInOpen) {
         caves = true;
       }
     } else if (this.game.field_1724.field_17892.method_28103().method_28114() && !this.game.field_1724.field_17892.method_8597().comp_642()) {
       boolean endPlayerInOpen = (this.world.method_22350((class_2338)this.blockPos).method_12005(class_2902.class_2903.field_13197, this.blockPos.method_10263() & 0xF, this.blockPos.method_10260() & 0xF) <= currentY);
       if (this.options.cavesAllowed.booleanValue() && this.options.showCaves && !endPlayerInOpen) {
         caves = true;
       }
     } else if (this.options.cavesAllowed.booleanValue() && this.options.showCaves && this.world.method_8314(class_1944.field_9284, (class_2338)this.blockPos) <= 0) {
       caves = true;
     } 
     
     int startX = this.lastX;
     int startZ = this.lastZ;
     int multi = (int)Math.pow(2.0D, this.zoom);
     startX -= 16 * multi;
     startZ -= 16 * multi;
     left = left - startX - 1;
     right = right - startX + 1;
     top = top - startZ - 1;
     bottom = bottom - startZ + 1;
     left = Math.max(0, left);
     right = Math.min(32 * multi - 1, right);
     top = Math.max(0, top);
     bottom = Math.min(32 * multi - 1, bottom);
     int color24 = 0;
     
     for (int imageY = bottom; imageY >= top; imageY--) {
       for (int imageX = left; imageX <= right; imageX++) {
         color24 = getPixelColor(true, true, true, true, nether, caves, (class_1937)this.world, multi, startX, startZ, imageX, imageY);
         this.mapImages[this.zoom].setRGB(imageX, imageY, color24);
       } 
     } 
     
     this.imageChanged = true;
   }
   
   private int getPixelColor(boolean needBiome, boolean needHeightAndID, boolean needTint, boolean needLight, boolean nether, boolean caves, class_1937 world, int multi, int startX, int startZ, int imageX, int imageY) {
     int surfaceHeight = 0;
     int seafloorHeight = -1;
     int transparentHeight = -1;
     int foliageHeight = -1;
     int surfaceColor = 0;
     int seafloorColor = 0;
     int transparentColor = 0;
     int foliageColor = 0;
     this.surfaceBlockState = null;
     this.transparentBlockState = BlockRepository.air.method_9564();
     this.foliageBlockState = BlockRepository.air.method_9564();
     this.seafloorBlockState = BlockRepository.air.method_9564();
     boolean surfaceBlockChangeForcedTint = false;
     boolean transparentBlockChangeForcedTint = false;
     boolean foliageBlockChangeForcedTint = false;
     boolean seafloorBlockChangeForcedTint = false;
     int surfaceBlockStateID = 0;
     int transparentBlockStateID = 0;
     int foliageBlockStateID = 0;
     int seafloorBlockStateID = 0;
     this.blockPos = this.blockPos.withXYZ(startX + imageX, 0, startZ + imageY);
     int color24 = 0;
     int biomeID = 0;
     if (needBiome) {
       if (world.method_22340((class_2338)this.blockPos)) {
         biomeID = world.method_30349().method_30530(class_2378.field_25114).method_10206(world.method_23753((class_2338)this.blockPos).comp_349());
       } else {
         biomeID = -1;
       } 
       
       this.mapData[this.zoom].setBiomeID(imageX, imageY, biomeID);
     } else {
       biomeID = this.mapData[this.zoom].getBiomeID(imageX, imageY);
     } 
     
     if (this.options.biomeOverlay == 1) {
       if (biomeID >= 0) {
         color24 = BiomeRepository.getBiomeColor(biomeID) | 0xFF000000;
       } else {
         color24 = 0;
       } 
       
       return MapUtils.doSlimeAndGrid(color24, startX + imageX, startZ + imageY);
     } 
     boolean solid = false;
     if (needHeightAndID) {
       if (!nether && !caves) {
         class_2818 chunk = world.method_8500((class_2338)this.blockPos);
         transparentHeight = chunk.method_12005(class_2902.class_2903.field_13197, this.blockPos.method_10263() & 0xF, this.blockPos.method_10260() & 0xF) + 1;
         this.transparentBlockState = world.method_8320((class_2338)this.blockPos.withXYZ(startX + imageX, transparentHeight - 1, startZ + imageY));
         class_3610 fluidState = this.transparentBlockState.method_26227();
         if (fluidState != class_3612.field_15906.method_15785()) {
           this.transparentBlockState = fluidState.method_15759();
         }
         
         surfaceHeight = transparentHeight;
         this.surfaceBlockState = this.transparentBlockState;
         class_265 voxelShape = null;
         boolean hasOpacity = (this.surfaceBlockState.method_26193((class_1922)world, (class_2338)this.blockPos) > 0);
         if (!hasOpacity && this.surfaceBlockState.method_26225() && this.surfaceBlockState.method_26211()) {
           voxelShape = this.surfaceBlockState.method_26173((class_1922)world, (class_2338)this.blockPos, class_2350.field_11033);
           hasOpacity = class_259.method_20713(voxelShape, class_259.method_1073());
           voxelShape = this.surfaceBlockState.method_26173((class_1922)world, (class_2338)this.blockPos, class_2350.field_11036);
           hasOpacity = (hasOpacity || class_259.method_20713(class_259.method_1073(), voxelShape));
         } 
         
         while (!hasOpacity && surfaceHeight > 0) {
           this.foliageBlockState = this.surfaceBlockState;
           surfaceHeight--;
           this.surfaceBlockState = world.method_8320((class_2338)this.blockPos.withXYZ(startX + imageX, surfaceHeight - 1, startZ + imageY));
           fluidState = this.surfaceBlockState.method_26227();
           if (fluidState != class_3612.field_15906.method_15785()) {
             this.surfaceBlockState = fluidState.method_15759();
           }
           
           hasOpacity = (this.surfaceBlockState.method_26193((class_1922)world, (class_2338)this.blockPos) > 0);
           if (!hasOpacity && this.surfaceBlockState.method_26225() && this.surfaceBlockState.method_26211()) {
             voxelShape = this.surfaceBlockState.method_26173((class_1922)world, (class_2338)this.blockPos, class_2350.field_11033);
             hasOpacity = class_259.method_20713(voxelShape, class_259.method_1073());
             voxelShape = this.surfaceBlockState.method_26173((class_1922)world, (class_2338)this.blockPos, class_2350.field_11036);
             hasOpacity = (hasOpacity || class_259.method_20713(class_259.method_1073(), voxelShape));
           } 
         } 
         
         if (surfaceHeight == transparentHeight) {
           transparentHeight = -1;
           this.transparentBlockState = BlockRepository.air.method_9564();
           this.foliageBlockState = world.method_8320((class_2338)this.blockPos.withXYZ(startX + imageX, surfaceHeight, startZ + imageY));
         } 
         
         if (this.foliageBlockState.method_26207() == class_3614.field_15948) {
           this.surfaceBlockState = this.foliageBlockState;
           this.foliageBlockState = BlockRepository.air.method_9564();
         } 
         
         if (this.foliageBlockState == this.transparentBlockState) {
           this.foliageBlockState = BlockRepository.air.method_9564();
         }
         
         if (this.foliageBlockState != null && this.foliageBlockState.method_26207() != class_3614.field_15959) {
           foliageHeight = surfaceHeight + 1;
         } else {
           foliageHeight = -1;
         } 
         
         class_3614 material = this.surfaceBlockState.method_26207();
         if (material == class_3614.field_15920 || material == class_3614.field_15958) {
           seafloorHeight = surfaceHeight;
           
           for (this.seafloorBlockState = world.method_8320((class_2338)this.blockPos.withXYZ(startX + imageX, surfaceHeight - 1, startZ + imageY)); this.seafloorBlockState.method_26193((class_1922)world, (class_2338)this.blockPos) < 5 && this.seafloorBlockState.method_26207() != class_3614.field_15923 && seafloorHeight > 1; this.seafloorBlockState = world.method_8320((class_2338)this.blockPos.withXYZ(startX + imageX, seafloorHeight - 1, startZ + imageY))) {
             material = this.seafloorBlockState.method_26207();
             if (transparentHeight == -1 && material != class_3614.field_15958 && material != class_3614.field_15920 && material.method_15801()) {
               transparentHeight = seafloorHeight;
               this.transparentBlockState = this.seafloorBlockState;
             } 
             
             if (foliageHeight == -1 && seafloorHeight != transparentHeight && this.transparentBlockState != this.seafloorBlockState && material != class_3614.field_15958 && material != class_3614.field_15920 && material != class_3614.field_15959 && material != class_3614.field_15915) {
               foliageHeight = seafloorHeight;
               this.foliageBlockState = this.seafloorBlockState;
             } 
             
             seafloorHeight--;
           } 
           
           if (this.seafloorBlockState.method_26207() == class_3614.field_15920) {
             this.seafloorBlockState = BlockRepository.air.method_9564();
           }
         } 
       } else {
         surfaceHeight = getNetherHeight(nether, startX + imageX, startZ + imageY);
         this.surfaceBlockState = world.method_8320((class_2338)this.blockPos.withXYZ(startX + imageX, surfaceHeight - 1, startZ + imageY));
         surfaceBlockStateID = BlockRepository.getStateId(this.surfaceBlockState);
         foliageHeight = surfaceHeight + 1;
         this.blockPos.setXYZ(startX + imageX, foliageHeight - 1, startZ + imageY);
         this.foliageBlockState = world.method_8320((class_2338)this.blockPos);
         class_3614 material = this.foliageBlockState.method_26207();
         if (material != class_3614.field_15948 && material != class_3614.field_15959 && material != class_3614.field_15922 && material != class_3614.field_15920) {
           foliageBlockStateID = BlockRepository.getStateId(this.foliageBlockState);
         } else {
           foliageHeight = -1;
         } 
       } 
       
       surfaceBlockStateID = BlockRepository.getStateId(this.surfaceBlockState);
       if (this.options.biomes && this.surfaceBlockState != this.mapData[this.zoom].getBlockstate(imageX, imageY)) {
         surfaceBlockChangeForcedTint = true;
       }
       
       this.mapData[this.zoom].setHeight(imageX, imageY, surfaceHeight);
       this.mapData[this.zoom].setBlockstateID(imageX, imageY, surfaceBlockStateID);
       if (this.options.biomes && this.transparentBlockState != this.mapData[this.zoom].getTransparentBlockstate(imageX, imageY)) {
         transparentBlockChangeForcedTint = true;
       }
       
       this.mapData[this.zoom].setTransparentHeight(imageX, imageY, transparentHeight);
       transparentBlockStateID = BlockRepository.getStateId(this.transparentBlockState);
       this.mapData[this.zoom].setTransparentBlockstateID(imageX, imageY, transparentBlockStateID);
       if (this.options.biomes && this.foliageBlockState != this.mapData[this.zoom].getFoliageBlockstate(imageX, imageY)) {
         foliageBlockChangeForcedTint = true;
       }
       
       this.mapData[this.zoom].setFoliageHeight(imageX, imageY, foliageHeight);
       foliageBlockStateID = BlockRepository.getStateId(this.foliageBlockState);
       this.mapData[this.zoom].setFoliageBlockstateID(imageX, imageY, foliageBlockStateID);
       if (this.options.biomes && this.seafloorBlockState != this.mapData[this.zoom].getOceanFloorBlockstate(imageX, imageY)) {
         seafloorBlockChangeForcedTint = true;
       }
       
       this.mapData[this.zoom].setOceanFloorHeight(imageX, imageY, seafloorHeight);
       seafloorBlockStateID = BlockRepository.getStateId(this.seafloorBlockState);
       this.mapData[this.zoom].setOceanFloorBlockstateID(imageX, imageY, seafloorBlockStateID);
     } else {
       surfaceHeight = this.mapData[this.zoom].getHeight(imageX, imageY);
       surfaceBlockStateID = this.mapData[this.zoom].getBlockstateID(imageX, imageY);
       this.surfaceBlockState = BlockRepository.getStateById(surfaceBlockStateID);
       transparentHeight = this.mapData[this.zoom].getTransparentHeight(imageX, imageY);
       transparentBlockStateID = this.mapData[this.zoom].getTransparentBlockstateID(imageX, imageY);
       this.transparentBlockState = BlockRepository.getStateById(transparentBlockStateID);
       foliageHeight = this.mapData[this.zoom].getFoliageHeight(imageX, imageY);
       foliageBlockStateID = this.mapData[this.zoom].getFoliageBlockstateID(imageX, imageY);
       this.foliageBlockState = BlockRepository.getStateById(foliageBlockStateID);
       seafloorHeight = this.mapData[this.zoom].getOceanFloorHeight(imageX, imageY);
       seafloorBlockStateID = this.mapData[this.zoom].getOceanFloorBlockstateID(imageX, imageY);
       this.seafloorBlockState = BlockRepository.getStateById(seafloorBlockStateID);
     } 
     
     if (surfaceHeight == -1) {
       surfaceHeight = this.lastY + 1;
       solid = true;
     } 
     
     if (this.surfaceBlockState.method_26207() == class_3614.field_15922) {
       solid = false;
     }
     
     if (this.options.biomes) {
       surfaceColor = this.colorManager.getBlockColor(this.blockPos, surfaceBlockStateID, biomeID);
       int tint = -1;
       if (!needTint && !surfaceBlockChangeForcedTint) {
         tint = this.mapData[this.zoom].getBiomeTint(imageX, imageY);
       } else {
         tint = this.colorManager.getBiomeTint((AbstractMapData)this.mapData[this.zoom], world, this.surfaceBlockState, surfaceBlockStateID, this.blockPos.withXYZ(startX + imageX, surfaceHeight - 1, startZ + imageY), this.tempBlockPos, startX, startZ);
         this.mapData[this.zoom].setBiomeTint(imageX, imageY, tint);
       } 
       
       if (tint != -1) {
         surfaceColor = ColorUtils.colorMultiplier(surfaceColor, tint);
       }
     } else {
       surfaceColor = this.colorManager.getBlockColorWithDefaultTint(this.blockPos, surfaceBlockStateID);
     } 
     
     surfaceColor = applyHeight(surfaceColor, nether, caves, world, multi, startX, startZ, imageX, imageY, surfaceHeight, solid, 1);
     int light = solid ? 0 : 255;
     if (needLight) {
       light = getLight(surfaceColor, this.surfaceBlockState, world, startX + imageX, startZ + imageY, surfaceHeight, solid);
       this.mapData[this.zoom].setLight(imageX, imageY, light);
     } else {
       light = this.mapData[this.zoom].getLight(imageX, imageY);
     } 
     
     if (light == 0) {
       surfaceColor = 0;
     } else if (light != 255) {
       surfaceColor = ColorUtils.colorMultiplier(surfaceColor, light);
     } 
     
     if (this.options.waterTransparency && seafloorHeight != -1) {
       if (!this.options.biomes) {
         seafloorColor = this.colorManager.getBlockColorWithDefaultTint(this.blockPos, seafloorBlockStateID);
       } else {
         seafloorColor = this.colorManager.getBlockColor(this.blockPos, seafloorBlockStateID, biomeID);
         int tint = -1;
         if (!needTint && !seafloorBlockChangeForcedTint) {
           tint = this.mapData[this.zoom].getOceanFloorBiomeTint(imageX, imageY);
         } else {
           tint = this.colorManager.getBiomeTint((AbstractMapData)this.mapData[this.zoom], world, this.seafloorBlockState, seafloorBlockStateID, this.blockPos.withXYZ(startX + imageX, seafloorHeight - 1, startZ + imageY), this.tempBlockPos, startX, startZ);
           this.mapData[this.zoom].setOceanFloorBiomeTint(imageX, imageY, tint);
         } 
         
         if (tint != -1) {
           seafloorColor = ColorUtils.colorMultiplier(seafloorColor, tint);
         }
       } 
       
       seafloorColor = applyHeight(seafloorColor, nether, caves, world, multi, startX, startZ, imageX, imageY, seafloorHeight, solid, 0);
       int seafloorLight = 255;
       if (needLight) {
         seafloorLight = getLight(seafloorColor, this.seafloorBlockState, world, startX + imageX, startZ + imageY, seafloorHeight, solid);
         this.blockPos.setXYZ(startX + imageX, seafloorHeight, startZ + imageY);
         class_2680 blockStateAbove = world.method_8320((class_2338)this.blockPos);
         class_3614 materialAbove = blockStateAbove.method_26207();
         if (this.options.lightmap && materialAbove == class_3614.field_15958) {
           int multiplier = 255;
           if (this.game.field_1690.method_41792().method_41753() == class_4060.field_18145) {
             multiplier = 200;
           } else if (this.game.field_1690.method_41792().method_41753() == class_4060.field_18146) {
             multiplier = 120;
           } 
           
           seafloorLight = ColorUtils.colorMultiplier(seafloorLight, 0xFF000000 | multiplier << 16 | multiplier << 8 | multiplier);
         } 
         
         this.mapData[this.zoom].setOceanFloorLight(imageX, imageY, seafloorLight);
       } else {
         seafloorLight = this.mapData[this.zoom].getOceanFloorLight(imageX, imageY);
       } 
       
       if (seafloorLight == 0) {
         seafloorColor = 0;
       } else if (seafloorLight != 255) {
         seafloorColor = ColorUtils.colorMultiplier(seafloorColor, seafloorLight);
       } 
     } 
     
     if (this.options.blockTransparency) {
       if (transparentHeight != -1 && this.transparentBlockState != null && this.transparentBlockState != BlockRepository.air.method_9564()) {
         if (this.options.biomes) {
           transparentColor = this.colorManager.getBlockColor(this.blockPos, transparentBlockStateID, biomeID);
           int tint = -1;
           if (!needTint && !transparentBlockChangeForcedTint) {
             tint = this.mapData[this.zoom].getTransparentBiomeTint(imageX, imageY);
           } else {
             tint = this.colorManager.getBiomeTint((AbstractMapData)this.mapData[this.zoom], world, this.transparentBlockState, transparentBlockStateID, this.blockPos.withXYZ(startX + imageX, transparentHeight - 1, startZ + imageY), this.tempBlockPos, startX, startZ);
             this.mapData[this.zoom].setTransparentBiomeTint(imageX, imageY, tint);
           } 
           
           if (tint != -1) {
             transparentColor = ColorUtils.colorMultiplier(transparentColor, tint);
           }
         } else {
           transparentColor = this.colorManager.getBlockColorWithDefaultTint(this.blockPos, transparentBlockStateID);
         } 
         
         transparentColor = applyHeight(transparentColor, nether, caves, world, multi, startX, startZ, imageX, imageY, transparentHeight, solid, 3);
         int transparentLight = 255;
         if (needLight) {
           transparentLight = getLight(transparentColor, this.transparentBlockState, world, startX + imageX, startZ + imageY, transparentHeight, solid);
           this.mapData[this.zoom].setTransparentLight(imageX, imageY, transparentLight);
         } else {
           transparentLight = this.mapData[this.zoom].getTransparentLight(imageX, imageY);
         } 
         
         if (transparentLight == 0) {
           transparentColor = 0;
         } else if (transparentLight != 255) {
           transparentColor = ColorUtils.colorMultiplier(transparentColor, transparentLight);
         } 
       } 
       
       if (foliageHeight != -1 && this.foliageBlockState != null && this.foliageBlockState != BlockRepository.air.method_9564()) {
         if (!this.options.biomes) {
           foliageColor = this.colorManager.getBlockColorWithDefaultTint(this.blockPos, foliageBlockStateID);
         } else {
           foliageColor = this.colorManager.getBlockColor(this.blockPos, foliageBlockStateID, biomeID);
           int tint = -1;
           if (!needTint && !foliageBlockChangeForcedTint) {
             tint = this.mapData[this.zoom].getFoliageBiomeTint(imageX, imageY);
           } else {
             tint = this.colorManager.getBiomeTint((AbstractMapData)this.mapData[this.zoom], world, this.foliageBlockState, foliageBlockStateID, this.blockPos.withXYZ(startX + imageX, foliageHeight - 1, startZ + imageY), this.tempBlockPos, startX, startZ);
             this.mapData[this.zoom].setFoliageBiomeTint(imageX, imageY, tint);
           } 
           
           if (tint != -1) {
             foliageColor = ColorUtils.colorMultiplier(foliageColor, tint);
           }
         } 
         
         foliageColor = applyHeight(foliageColor, nether, caves, world, multi, startX, startZ, imageX, imageY, foliageHeight, solid, 2);
         int foliageLight = 255;
         if (needLight) {
           foliageLight = getLight(foliageColor, this.foliageBlockState, world, startX + imageX, startZ + imageY, foliageHeight, solid);
           this.mapData[this.zoom].setFoliageLight(imageX, imageY, foliageLight);
         } else {
           foliageLight = this.mapData[this.zoom].getFoliageLight(imageX, imageY);
         } 
         
         if (foliageLight == 0) {
           foliageColor = 0;
         } else if (foliageLight != 255) {
           foliageColor = ColorUtils.colorMultiplier(foliageColor, foliageLight);
         } 
       } 
     } 
     
     if (seafloorColor != 0 && seafloorHeight > 0) {
       color24 = seafloorColor;
       if (foliageColor != 0 && foliageHeight <= surfaceHeight) {
         color24 = ColorUtils.colorAdder(foliageColor, seafloorColor);
       }
       
       if (transparentColor != 0 && transparentHeight <= surfaceHeight) {
         color24 = ColorUtils.colorAdder(transparentColor, color24);
       }
       
       color24 = ColorUtils.colorAdder(surfaceColor, color24);
     } else {
       color24 = surfaceColor;
     } 
     
     if (foliageColor != 0 && foliageHeight > surfaceHeight) {
       color24 = ColorUtils.colorAdder(foliageColor, color24);
     }
     
     if (transparentColor != 0 && transparentHeight > surfaceHeight) {
       color24 = ColorUtils.colorAdder(transparentColor, color24);
     }
     
     if (this.options.biomeOverlay == 2) {
       int bc = 0;
       if (biomeID >= 0) {
         bc = BiomeRepository.getBiomeColor(biomeID);
       }
       
       bc = 0x7F000000 | bc;
       color24 = ColorUtils.colorAdder(bc, color24);
     } 
     
     return MapUtils.doSlimeAndGrid(color24, startX + imageX, startZ + imageY);
   }
 
   
   private final int getBlockHeight(boolean nether, boolean caves, class_1937 world, int x, int z) {
     int playerHeight = GameVariableAccessShim.yCoord();
     this.blockPos.setXYZ(x, playerHeight, z);
     class_2818 chunk = (class_2818)world.method_22350((class_2338)this.blockPos);
     int height = chunk.method_12005(class_2902.class_2903.field_13197, this.blockPos.method_10263() & 0xF, this.blockPos.method_10260() & 0xF) + 1;
     class_2680 blockState = world.method_8320((class_2338)this.blockPos.withXYZ(x, height - 1, z));
     class_3610 fluidState = this.transparentBlockState.method_26227();
     if (fluidState != class_3612.field_15906.method_15785()) {
       blockState = fluidState.method_15759();
     }
     
     while (blockState.method_26193((class_1922)world, (class_2338)this.blockPos) == 0 && height > 0) {
       height--;
       blockState = world.method_8320((class_2338)this.blockPos.withXYZ(x, height - 1, z));
       fluidState = this.surfaceBlockState.method_26227();
       if (fluidState != class_3612.field_15906.method_15785()) {
         blockState = fluidState.method_15759();
       }
     } 
     
     return ((nether || caves) && height > playerHeight) ? getNetherHeight(nether, x, z) : height;
   }
   
   private int getNetherHeight(boolean nether, int x, int z) {
     int y = this.lastY;
     this.blockPos.setXYZ(x, y, z);
     class_2680 blockState = this.world.method_8320((class_2338)this.blockPos);
     if (blockState.method_26193((class_1922)this.world, (class_2338)this.blockPos) == 0 && blockState.method_26207() != class_3614.field_15922) {
       while (y > 0) {
         y--;
         this.blockPos.setXYZ(x, y, z);
         blockState = this.world.method_8320((class_2338)this.blockPos);
         if (blockState.method_26193((class_1922)this.world, (class_2338)this.blockPos) > 0 || blockState.method_26207() == class_3614.field_15922) {
           return y + 1;
         }
       } 
       
       return y;
     } 
     while (y <= this.lastY + 10 && y < (nether ? 127 : 256)) {
       y++;
       this.blockPos.setXYZ(x, y, z);
       blockState = this.world.method_8320((class_2338)this.blockPos);
       if (blockState.method_26193((class_1922)this.world, (class_2338)this.blockPos) == 0 && blockState.method_26207() != class_3614.field_15922) {
         return y;
       }
     } 
     
     return -1;
   }
 
   
   private final int getSeafloorHeight(class_1937 world, int x, int z, int height) {
     for (class_2680 blockState = world.method_8320((class_2338)this.blockPos.withXYZ(x, height - 1, z)); blockState.method_26193((class_1922)world, (class_2338)this.blockPos) < 5 && blockState.method_26207() != class_3614.field_15923 && height > 1; blockState = world.method_8320((class_2338)this.blockPos.withXYZ(x, height - 1, z))) {
       height--;
     }
     
     return height;
   }
   
   private final int getTransparentHeight(boolean nether, boolean caves, class_1937 world, int x, int z, int height) {
     int transHeight = -1;
     if (!caves && !nether) {
       transHeight = world.method_8598(class_2902.class_2903.field_13197, (class_2338)this.blockPos.withXYZ(x, height, z)).method_10264();
       if (transHeight <= height) {
         transHeight = -1;
       }
     } else {
       transHeight = -1;
     } 
     
     class_2680 blockState = world.method_8320((class_2338)this.blockPos.withXYZ(x, transHeight - 1, z));
     class_3614 material = blockState.method_26207();
     if (transHeight == height + 1 && material == class_3614.field_15948) {
       transHeight = -1;
     }
     
     if (material == class_3614.field_15952) {
       transHeight++;
       blockState = world.method_8320((class_2338)this.blockPos.withXYZ(x, transHeight - 1, z));
       material = blockState.method_26207();
       if (material == class_3614.field_15959) {
         transHeight = -1;
       }
     } 
     
     return transHeight;
   }
   
   private int applyHeight(int color24, boolean nether, boolean caves, class_1937 world, int multi, int startX, int startZ, int imageX, int imageY, int height, boolean solid, int layer) {
     if (color24 != this.colorManager.getAirColor() && color24 != 0 && (this.options.heightmap || this.options.slopemap) && !solid) {
       int heightComp = -1;
       int diff = 0;
       double sc = 0.0D;
       if (!this.options.slopemap) {
         if (this.options.heightmap) {
           diff = height - this.lastY;
           sc = Math.log10(Math.abs(diff) / 8.0D + 1.0D) / 1.8D;
           if (diff < 0) {
             sc = 0.0D - sc;
           }
         } 
       } else {
         if (imageX > 0 && imageY < 32 * multi - 1) {
           if (layer == 0) {
             heightComp = this.mapData[this.zoom].getOceanFloorHeight(imageX - 1, imageY + 1);
           }
           
           if (layer == 1) {
             heightComp = this.mapData[this.zoom].getHeight(imageX - 1, imageY + 1);
           }
           
           if (layer == 2) {
             heightComp = height;
           }
           
           if (layer == 3) {
             heightComp = this.mapData[this.zoom].getTransparentHeight(imageX - 1, imageY + 1);
             if (heightComp == -1) {
               class_2248 block = BlockRepository.getStateById(this.mapData[this.zoom].getTransparentBlockstateID(imageX, imageY)).method_26204();
               if (block instanceof net.minecraft.class_2368 || block instanceof net.minecraft.class_2506) {
                 heightComp = this.mapData[this.zoom].getHeight(imageX - 1, imageY + 1);
               }
             } 
           } 
         } else {
           if (layer == 0) {
             int baseHeight = getBlockHeight(nether, caves, world, startX + imageX - 1, startZ + imageY + 1);
             heightComp = getSeafloorHeight(world, startX + imageX - 1, startZ + imageY + 1, baseHeight);
           } 
           
           if (layer == 1) {
             heightComp = getBlockHeight(nether, caves, world, startX + imageX - 1, startZ + imageY + 1);
           }
           
           if (layer == 2) {
             heightComp = height;
           }
           
           if (layer == 3) {
             int baseHeight = getBlockHeight(nether, caves, world, startX + imageX - 1, startZ + imageY + 1);
             heightComp = getTransparentHeight(nether, caves, world, startX + imageX - 1, startZ + imageY + 1, baseHeight);
             if (heightComp == -1) {
               class_2680 blockState = world.method_8320((class_2338)this.blockPos.withXYZ(startX + imageX, height - 1, startZ + imageY));
               class_2248 block = blockState.method_26204();
               if (block instanceof net.minecraft.class_2368 || block instanceof net.minecraft.class_2506) {
                 heightComp = baseHeight;
               }
             } 
           } 
         } 
         
         if (heightComp == -1) {
           heightComp = height;
         }
         
         diff = heightComp - height;
         if (diff != 0) {
           sc = (diff > 0) ? 1.0D : ((diff < 0) ? -1.0D : 0.0D);
           sc /= 8.0D;
         } 
         
         if (this.options.heightmap) {
           diff = height - this.lastY;
           double heightsc = Math.log10(Math.abs(diff) / 8.0D + 1.0D) / 3.0D;
           sc = (diff > 0) ? (sc + heightsc) : (sc - heightsc);
         } 
       } 
       
       int alpha = color24 >> 24 & 0xFF;
       int r = color24 >> 16 & 0xFF;
       int g = color24 >> 8 & 0xFF;
       int b = color24 >> 0 & 0xFF;
       if (sc > 0.0D) {
         r += (int)(sc * (255 - r));
         g += (int)(sc * (255 - g));
         b += (int)(sc * (255 - b));
       } else if (sc < 0.0D) {
         sc = Math.abs(sc);
         r -= (int)(sc * r);
         g -= (int)(sc * g);
         b -= (int)(sc * b);
       } 
       
       color24 = alpha * 16777216 + r * 65536 + g * 256 + b;
     } 
     
     return color24;
   }
   
   private int getLight(int color24, class_2680 blockState, class_1937 world, int x, int z, int height, boolean solid) {
     int i3 = 255;
     if (solid) {
       i3 = 0;
     } else if (color24 != this.colorManager.getAirColor() && color24 != 0 && this.options.lightmap) {
       this.blockPos.setXYZ(x, Math.max(Math.min(height, 255), 0), z);
       int blockLight = world.method_8314(class_1944.field_9282, (class_2338)this.blockPos);
       int skyLight = world.method_8314(class_1944.field_9284, (class_2338)this.blockPos);
       if (blockState.method_26207() == class_3614.field_15922 || blockState.method_26204() == class_2246.field_10092) {
         blockLight = 14;
       }
       
       i3 = this.lightmapColors[blockLight + skyLight * 16];
     } 
     
     return i3;
   }
   
   private void renderMap(class_4587 matrixStack, int x, int y, int scScale) {
     float scale = 1.0F;
     if (this.options.squareMap && this.options.rotates) {
       scale = 1.4142F;
     }
     
     if (GLUtils.hasAlphaBits) {
       RenderSystem.setShader(class_757::method_34542);
       GLShim.glColorMask(false, false, false, true);
       GLShim.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
       GLShim.glClear(16384);
       GLShim.glBlendFunc(770, 771);
       GLShim.glColorMask(true, true, true, true);
       GLUtils.img2(this.options.squareMap ? this.squareStencil : this.circleStencil);
       GLUtils.drawPre();
       GLUtils.setMap(x, y, 128);
       GLUtils.drawPost();
       GLShim.glBlendFunc(772, 773);
       synchronized (this.coordinateLock) {
         if (this.imageChanged) {
           this.imageChanged = false;
           this.mapImages[this.zoom].write();
           this.lastImageX = this.lastX;
           this.lastImageZ = this.lastZ;
         } 
       } 
       
       float multi = (float)(1.0D / this.zoomScaleAdjusted);
       this.percentX = (float)(GameVariableAccessShim.xCoordDouble() - this.lastImageX);
       this.percentY = (float)(GameVariableAccessShim.zCoordDouble() - this.lastImageZ);
       this.percentX *= multi;
       this.percentY *= multi;
       GLUtils.disp2(this.mapImages[this.zoom].getIndex());
       matrixStack.method_22903();
       matrixStack.method_22904(x, y, 0.0D);
       matrixStack.method_22907(class_1160.field_20707.method_23214(!this.options.rotates ? this.northRotate : -this.direction));
       matrixStack.method_22904(-x, -y, 0.0D);
       matrixStack.method_22904(-this.percentX, -this.percentY, 0.0D);
       RenderSystem.applyModelViewMatrix();
       GLShim.glTexParameteri(3553, 10241, 9987);
       GLShim.glTexParameteri(3553, 10240, 9729);
     } else {
       GLShim.glBindTexture(3553, 0);
       class_1159 minimapProjectionMatrix = RenderSystem.getProjectionMatrix();
       RenderSystem.setShader(class_757::method_34542);
       class_1159 matrix4f = class_1159.method_34239(0.0F, 512.0F, 0.0F, 512.0F, 1000.0F, 3000.0F);
       RenderSystem.setProjectionMatrix(matrix4f);
       GLUtils.bindFrameBuffer();
       GLShim.glViewport(0, 0, 512, 512);
       matrixStack.method_22903();
       matrixStack.method_34426();
       matrixStack.method_22904(0.0D, 0.0D, -2000.0D);
       RenderSystem.applyModelViewMatrix();
       GLShim.glDepthMask(false);
       GLShim.glDisable(2929);
       GLShim.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
       GLShim.glClear(16384);
       GLShim.glBlendFunc(770, 0);
       GLUtils.img2(this.options.squareMap ? this.squareStencil : this.circleStencil);
       GLUtils.drawPre();
       GLUtils.ldrawthree((256.0F - 256.0F / scale), (256.0F + 256.0F / scale), 1.0D, 0.0F, 0.0F);
       GLUtils.ldrawthree((256.0F + 256.0F / scale), (256.0F + 256.0F / scale), 1.0D, 1.0F, 0.0F);
       GLUtils.ldrawthree((256.0F + 256.0F / scale), (256.0F - 256.0F / scale), 1.0D, 1.0F, 1.0F);
       GLUtils.ldrawthree((256.0F - 256.0F / scale), (256.0F - 256.0F / scale), 1.0D, 0.0F, 1.0F);
       class_287 bb = class_289.method_1348().method_1349();
       bb.method_1326();
       class_286.method_43437(bb.method_1326());
       GLShim.glBlendFuncSeparate(1, 0, 774, 0);
       synchronized (this.coordinateLock) {
         if (this.imageChanged) {
           this.imageChanged = false;
           this.mapImages[this.zoom].write();
           this.lastImageX = this.lastX;
           this.lastImageZ = this.lastZ;
         } 
       } 
       
       float multi = (float)(1.0D / this.zoomScale);
       this.percentX = (float)(GameVariableAccessShim.xCoordDouble() - this.lastImageX);
       this.percentY = (float)(GameVariableAccessShim.zCoordDouble() - this.lastImageZ);
       this.percentX *= multi;
       this.percentY *= multi;
       GLUtils.disp2(this.mapImages[this.zoom].getIndex());
       GLShim.glTexParameteri(3553, 10241, 9987);
       GLShim.glTexParameteri(3553, 10240, 9729);
       matrixStack.method_22903();
       matrixStack.method_22904(256.0D, 256.0D, 0.0D);
       if (!this.options.rotates) {
         matrixStack.method_22907(class_1160.field_20707.method_23214(-this.northRotate));
       } else {
         matrixStack.method_22907(class_1160.field_20707.method_23214(this.direction));
       } 
       
       matrixStack.method_22904(-256.0D, -256.0D, 0.0D);
       matrixStack.method_22904((-this.percentX * 512.0F / 64.0F), (this.percentY * 512.0F / 64.0F), 0.0D);
       RenderSystem.applyModelViewMatrix();
       GLUtils.drawPre();
       GLUtils.ldrawthree(0.0D, 512.0D, 1.0D, 0.0F, 0.0F);
       GLUtils.ldrawthree(512.0D, 512.0D, 1.0D, 1.0F, 0.0F);
       GLUtils.ldrawthree(512.0D, 0.0D, 1.0D, 1.0F, 1.0F);
       GLUtils.ldrawthree(0.0D, 0.0D, 1.0D, 0.0F, 1.0F);
       GLUtils.drawPost();
       matrixStack.method_22909();
       RenderSystem.applyModelViewMatrix();
       GLShim.glDepthMask(true);
       GLShim.glEnable(2929);
       GLUtils.unbindFrameBuffer();
       GLShim.glViewport(0, 0, this.game.method_22683().method_4489(), this.game.method_22683().method_4506());
       matrixStack.method_22909();
       RenderSystem.setProjectionMatrix(minimapProjectionMatrix);
       matrixStack.method_22903();
       GLShim.glBlendFunc(770, 0);
       GLUtils.disp2(GLUtils.fboTextureID);
     } 
     
     double guiScale = this.game.method_22683().method_4489() / this.scWidth;
     GLShim.glEnable(3089);
     GLShim.glScissor((int)(guiScale * (x - 32)), (int)(guiScale * ((this.scHeight - y) - 32.0D)), (int)(guiScale * 64.0D), (int)(guiScale * 63.0D));
     GLUtils.drawPre();
     GLUtils.setMapWithScale(x, y, scale);
     GLUtils.drawPost();
     GLShim.glDisable(3089);
     matrixStack.method_22909();
     RenderSystem.applyModelViewMatrix();
     GLShim.glBlendFunc(770, 771);
     GLShim.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
     if (this.options.squareMap) {
       drawSquareMapFrame(x, y);
     } else {
       drawRoundMapFrame(x, y);
     } 
     
     double lastXDouble = GameVariableAccessShim.xCoordDouble();
     double lastZDouble = GameVariableAccessShim.zCoordDouble();
     TextureAtlas textureAtlas = this.master.getWaypointManager().getTextureAtlas();
     GLUtils.disp2(textureAtlas.method_4624());
     GLShim.glEnable(3042);
     GLShim.glBlendFunc(770, 771);
     GLShim.glDisable(2929);
     Waypoint highlightedPoint = this.waypointManager.getHighlightedWaypoint();
     
     for (Waypoint pt : this.waypointManager.getWaypoints()) {
       if (pt.isActive() || pt == highlightedPoint) {
         double distanceSq = pt.getDistanceSqToEntity(this.game.method_1560());
         if (distanceSq < (this.options.maxWaypointDisplayDistance * this.options.maxWaypointDisplayDistance) || this.options.maxWaypointDisplayDistance < 0 || pt == highlightedPoint) {
           drawWaypoint(matrixStack, pt, textureAtlas, x, y, scScale, lastXDouble, lastZDouble, (Sprite)null, (Float)null, (Float)null, (Float)null);
         }
       } 
     } 
     
     if (highlightedPoint != null) {
       drawWaypoint(matrixStack, highlightedPoint, textureAtlas, x, y, scScale, lastXDouble, lastZDouble, textureAtlas.getAtlasSprite("voxelmap:images/waypoints/target.png"), Float.valueOf(1.0F), Float.valueOf(0.0F), Float.valueOf(0.0F));
     }
     
     GLShim.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }
   
   private void drawWaypoint(class_4587 matrixStack, Waypoint pt, TextureAtlas textureAtlas, int x, int y, int scScale, double lastXDouble, double lastZDouble, Sprite icon, Float r, Float g, Float b) {
     boolean uprightIcon = (icon != null);
     if (r == null) {
       r = Float.valueOf(pt.red);
     }
     
     if (g == null) {
       g = Float.valueOf(pt.green);
     }
     
     if (b == null) {
       b = Float.valueOf(pt.blue);
     }
     
     double wayX = lastXDouble - pt.getX() - 0.5D;
     double wayY = lastZDouble - pt.getZ() - 0.5D;
     float locate = (float)Math.toDegrees(Math.atan2(wayX, wayY));
     double hypot = Math.sqrt(wayX * wayX + wayY * wayY);
     boolean far = false;
     if (this.options.rotates) {
       locate += this.direction;
     } else {
       locate -= this.northRotate;
     } 
     
     hypot /= this.zoomScaleAdjusted;
     if (this.options.squareMap) {
       double radLocate = Math.toRadians(locate);
       double dispX = hypot * Math.cos(radLocate);
       double dispY = hypot * Math.sin(radLocate);
       far = (Math.abs(dispX) > 28.5D || Math.abs(dispY) > 28.5D);
       if (far) {
         hypot = hypot / Math.max(Math.abs(dispX), Math.abs(dispY)) * 30.0D;
       }
     } else {
       far = (hypot >= 31.0D);
       if (far) {
         hypot = 34.0D;
       }
     } 
     
     boolean target = false;
     if (far) {
       try {
         if (icon == null) {
           if (scScale >= 3) {
             icon = textureAtlas.getAtlasSprite("voxelmap:images/waypoints/marker" + pt.imageSuffix + ".png");
           } else {
             icon = textureAtlas.getAtlasSprite("voxelmap:images/waypoints/marker" + pt.imageSuffix + "Small.png");
           } 
           
           if (icon == textureAtlas.getMissingImage()) {
             if (scScale >= 3) {
               icon = textureAtlas.getAtlasSprite("voxelmap:images/waypoints/marker.png");
             } else {
               icon = textureAtlas.getAtlasSprite("voxelmap:images/waypoints/markerSmall.png");
             } 
           }
         } else {
           target = true;
         } 
         
         matrixStack.method_22903();
         GLShim.glColor4f(r.floatValue(), g.floatValue(), b.floatValue(), (!pt.enabled && !target) ? 0.3F : 1.0F);
         matrixStack.method_22904(x, y, 0.0D);
         matrixStack.method_22907(class_1160.field_20707.method_23214(-locate));
         if (uprightIcon) {
           matrixStack.method_22904(0.0D, -hypot, 0.0D);
           matrixStack.method_22907(class_1160.field_20707.method_23214(locate));
           matrixStack.method_22904(-x, -y, 0.0D);
         } else {
           matrixStack.method_22904(-x, -y, 0.0D);
           matrixStack.method_22904(0.0D, -hypot, 0.0D);
         } 
         
         RenderSystem.applyModelViewMatrix();
         GLShim.glTexParameteri(3553, 10241, 9729);
         GLShim.glTexParameteri(3553, 10240, 9729);
         GLUtils.drawPre();
         GLUtils.setMap(icon, x, y, 16.0F);
         GLUtils.drawPost();
       } catch (Exception var40) {
         this.error = "Error: marker overlay not found!";
       } finally {
         matrixStack.method_22909();
         RenderSystem.applyModelViewMatrix();
       } 
     } else {
       try {
         if (icon == null) {
           if (scScale >= 3) {
             icon = textureAtlas.getAtlasSprite("voxelmap:images/waypoints/waypoint" + pt.imageSuffix + ".png");
           } else {
             icon = textureAtlas.getAtlasSprite("voxelmap:images/waypoints/waypoint" + pt.imageSuffix + "Small.png");
           } 
           
           if (icon == textureAtlas.getMissingImage()) {
             if (scScale >= 3) {
               icon = textureAtlas.getAtlasSprite("voxelmap:images/waypoints/waypoint.png");
             } else {
               icon = textureAtlas.getAtlasSprite("voxelmap:images/waypoints/waypointSmall.png");
             } 
           }
         } else {
           target = true;
         } 
         
         matrixStack.method_22903();
         GLShim.glColor4f(r.floatValue(), g.floatValue(), b.floatValue(), (!pt.enabled && !target) ? 0.3F : 1.0F);
         matrixStack.method_22907(class_1160.field_20707.method_23214(-locate));
         matrixStack.method_22904(0.0D, -hypot, 0.0D);
         matrixStack.method_22907(class_1160.field_20707.method_23214(--locate));
         RenderSystem.applyModelViewMatrix();
         GLShim.glTexParameteri(3553, 10241, 9729);
         GLShim.glTexParameteri(3553, 10240, 9729);
         GLUtils.drawPre();
         GLUtils.setMap(icon, x, y, 16.0F);
         GLUtils.drawPost();
       } catch (Exception var42) {
         this.error = "Error: waypoint overlay not found!";
       } finally {
         matrixStack.method_22909();
         RenderSystem.applyModelViewMatrix();
       } 
     } 
   }
 
   
   private void drawArrow(class_4587 matrixStack, int x, int y) {
     try {
       RenderSystem.setShader(class_757::method_34542);
       matrixStack.method_22903();
       GLShim.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
       GLShim.glBlendFunc(770, 771);
       GLUtils.img2(this.arrowResourceLocation);
       GLShim.glTexParameteri(3553, 10241, 9729);
       GLShim.glTexParameteri(3553, 10240, 9729);
       matrixStack.method_22904(x, y, 0.0D);
       matrixStack.method_22907(class_1160.field_20707.method_23214((this.options.rotates && !this.fullscreenMap) ? 0.0F : (this.direction + this.northRotate)));
       matrixStack.method_22904(-x, -y, 0.0D);
       RenderSystem.applyModelViewMatrix();
       GLUtils.drawPre();
       GLUtils.setMap(x, y, 16);
       GLUtils.drawPost();
     } catch (Exception var8) {
       this.error = "Error: minimap arrow not found!";
     } finally {
       matrixStack.method_22909();
       RenderSystem.applyModelViewMatrix();
     } 
   }
 
   
   private void renderMapFull(class_4587 matrixStack, int scWidth, int scHeight) {
     synchronized (this.coordinateLock) {
       if (this.imageChanged) {
         this.imageChanged = false;
         this.mapImages[this.zoom].write();
         this.lastImageX = this.lastX;
         this.lastImageZ = this.lastZ;
       } 
     } 
     
     this.count++;
     RenderSystem.setShader(class_757::method_34542);
     GLUtils.disp2(this.mapImages[this.zoom].getIndex());
     GLShim.glTexParameteri(3553, 10241, 9987);
     GLShim.glTexParameteri(3553, 10240, 9729);
     matrixStack.method_22903();
     matrixStack.method_22904((scWidth / 2.0F), (scHeight / 2.0F), -0.0D);
     matrixStack.method_22907(class_1160.field_20707.method_23214(this.northRotate));
     matrixStack.method_22904(-(scWidth / 2.0F), -(scHeight / 2.0F), -0.0D);
     RenderSystem.applyModelViewMatrix();
     GLShim.glDisable(2929);
     GLUtils.drawPre();
     int left = scWidth / 2 - 128;
     int top = scHeight / 2 - 128;
     GLUtils.ldrawone(left, top + 256, 160.0D, 0.0F, 1.0F);
     GLUtils.ldrawone(left + 256, top + 256, 160.0D, 1.0F, 1.0F);
     GLUtils.ldrawone(left + 256, top, 160.0D, 1.0F, 0.0F);
     GLUtils.ldrawone(left, top, 160.0D, 0.0F, 0.0F);
     GLUtils.drawPost();
     matrixStack.method_22909();
     RenderSystem.applyModelViewMatrix();
     if (this.options.biomeOverlay != 0) {
       double factor = Math.pow(2.0D, (3 - this.zoom));
       int minimumSize = (int)Math.pow(2.0D, this.zoom);
       minimumSize *= minimumSize;
       ArrayList labels = this.mapData[this.zoom].getBiomeLabels();
       GLShim.glDisable(2929);
       matrixStack.method_22903();
       matrixStack.method_22904(0.0D, 0.0D, 1160.0D);
       RenderSystem.applyModelViewMatrix();
       
       for (Object o : labels) {
         AbstractMapData.BiomeLabel label = (AbstractMapData.BiomeLabel)o;
         if (label.segmentSize > minimumSize) {
           String name = label.name;
           int nameWidth = chkLen(name);
           float x = (float)(label.x * factor);
           float z = (float)(label.z * factor);
           if (this.options.oldNorth) {
             write(matrixStack, name, (left + 256) - z - (nameWidth / 2), top + x - 3.0F, 16777215); continue;
           } 
           write(matrixStack, name, left + x - (nameWidth / 2), top + z - 3.0F, 16777215);
         } 
       } 
 
       
       matrixStack.method_22909();
       RenderSystem.applyModelViewMatrix();
       GLShim.glEnable(2929);
     } 
   }
 
   
   private void drawSquareMapFrame(int x, int y) {
     try {
       GLUtils.disp2(this.mapImageInt);
       GLShim.glTexParameteri(3553, 10241, 9729);
       GLShim.glTexParameteri(3553, 10240, 9729);
       GLShim.glTexParameteri(3553, 10242, 10496);
       GLShim.glTexParameteri(3553, 10243, 10496);
       GLUtils.drawPre();
       GLUtils.setMap(x, y, 128);
       GLUtils.drawPost();
     } catch (Exception var4) {
       this.error = "error: minimap overlay not found!";
     } 
   }
 
   
   private void loadMapImage() {
     if (this.mapImageInt != -1) {
       GLUtils.glah(this.mapImageInt);
     }
     
     try {
       InputStream is = ((class_3298)this.game.method_1478().method_14486(new class_2960("voxelmap", "images/squaremap.png")).get()).method_14482();
       BufferedImage mapImage = ImageIO.read(is);
       is.close();
       this.mapImageInt = GLUtils.tex(mapImage);
     } catch (Exception var8) {
       try {
         InputStream is = ((class_3298)this.game.method_1478().method_14486(new class_2960("textures/map/map_background.png")).get()).method_14482();
         Image tpMap = ImageIO.read(is);
         is.close();
         BufferedImage mapImage = new BufferedImage(tpMap.getWidth((ImageObserver)null), tpMap.getHeight((ImageObserver)null), 2);
         Graphics2D gfx = mapImage.createGraphics();
         gfx.drawImage(tpMap, 0, 0, (ImageObserver)null);
         int border = mapImage.getWidth() * 8 / 128;
         gfx.setComposite(AlphaComposite.Clear);
         gfx.fillRect(border, border, mapImage.getWidth() - border * 2, mapImage.getHeight() - border * 2);
         gfx.dispose();
         this.mapImageInt = GLUtils.tex(mapImage);
       } catch (Exception var7) {
         System.err.println("Error loading texture pack's map image: " + var7.getLocalizedMessage());
       } 
     } 
   }
 
   
   private void drawRoundMapFrame(int x, int y) {
     try {
       GLUtils.img2(this.roundmapResourceLocation);
       GLShim.glTexParameteri(3553, 10241, 9729);
       GLShim.glTexParameteri(3553, 10240, 9729);
       GLUtils.drawPre();
       GLUtils.setMap(x, y, 128);
       GLUtils.drawPost();
     } catch (Exception var4) {
       this.error = "Error: minimap overlay not found!";
     } 
   }
   
   private void drawDirections(class_4587 matrixStack, int x, int y) {
     float rotate, distance;
     boolean unicode = ((Boolean)this.game.field_1690.method_42437().method_41753()).booleanValue();
     float scale = unicode ? 0.65F : 0.5F;
     
     if (this.options.rotates) {
       rotate = -this.direction - 90.0F - this.northRotate;
     } else {
       rotate = -90.0F;
     } 
 
     
     if (this.options.squareMap) {
       if (this.options.rotates) {
         float tempdir = this.direction % 90.0F;
         tempdir = 45.0F - Math.abs(45.0F - tempdir);
         distance = (float)(33.5D / scale / Math.cos(Math.toRadians(tempdir)));
       } else {
         distance = 33.5F / scale;
       } 
     } else {
       distance = 32.0F / scale;
     } 
     
     matrixStack.method_22903();
     matrixStack.method_22905(scale, scale, 1.0F);
     matrixStack.method_22904(distance * Math.sin(Math.toRadians(-(rotate - 90.0D))), distance * Math.cos(Math.toRadians(-(rotate - 90.0D))), 100.0D);
     write(matrixStack, "N", x / scale - 2.0F, y / scale - 4.0F, 16777215);
     matrixStack.method_22909();
     matrixStack.method_22903();
     matrixStack.method_22905(scale, scale, 1.0F);
     matrixStack.method_22904(distance * Math.sin(Math.toRadians(-rotate)), distance * Math.cos(Math.toRadians(-rotate)), 10.0D);
     write(matrixStack, "E", x / scale - 2.0F, y / scale - 4.0F, 16777215);
     matrixStack.method_22909();
     matrixStack.method_22903();
     matrixStack.method_22905(scale, scale, 1.0F);
     matrixStack.method_22904(distance * Math.sin(Math.toRadians(-(rotate + 90.0D))), distance * Math.cos(Math.toRadians(-(rotate + 90.0D))), 10.0D);
     write(matrixStack, "S", x / scale - 2.0F, y / scale - 4.0F, 16777215);
     matrixStack.method_22909();
     matrixStack.method_22903();
     matrixStack.method_22905(scale, scale, 1.0F);
     matrixStack.method_22904(distance * Math.sin(Math.toRadians(-(rotate + 180.0D))), distance * Math.cos(Math.toRadians(-(rotate + 180.0D))), 10.0D);
     write(matrixStack, "W", x / scale - 2.0F, y / scale - 4.0F, 16777215);
     matrixStack.method_22909();
   }
   
   private void showCoords(class_4587 matrixStack, int x, int y) {
     int textStart;
     if (y > this.scHeight - 37 - 32 - 4 - 15) {
       textStart = y - 32 - 4 - 9;
     } else {
       textStart = y + 32 + 4;
     } 
     
     if (!this.options.hide && !this.fullscreenMap) {
       boolean unicode = ((Boolean)this.game.field_1690.method_42437().method_41753()).booleanValue();
       float scale = unicode ? 0.65F : 0.5F;
       matrixStack.method_22903();
       matrixStack.method_22905(scale, scale, 1.0F);
       String xy = dCoord(GameVariableAccessShim.xCoord()) + ", " + dCoord(GameVariableAccessShim.xCoord());
       int m = chkLen(xy) / 2;
       write(matrixStack, xy, x / scale - m, textStart / scale, 16777215);
       xy = Integer.toString(GameVariableAccessShim.yCoord());
       m = chkLen(xy) / 2;
       write(matrixStack, xy, x / scale - m, textStart / scale + 10.0F, 16777215);
       if (this.ztimer > 0) {
         m = chkLen(this.error) / 2;
         write(matrixStack, this.error, x / scale - m, textStart / scale + 19.0F, 16777215);
       } 
       
       matrixStack.method_22909();
     } else {
       int heading = (int)(this.direction + this.northRotate);
       if (heading > 360) {
         heading -= 360;
       }
       
       String stats = "(" + dCoord(GameVariableAccessShim.xCoord()) + ", " + GameVariableAccessShim.yCoord() + ", " + dCoord(GameVariableAccessShim.zCoord()) + ") " + heading + "'";
       int m = chkLen(stats) / 2;
       write(matrixStack, stats, (this.scWidth / 2 - m), 5.0F, 16777215);
       if (this.ztimer > 0) {
         m = chkLen(this.error) / 2;
         write(matrixStack, this.error, (this.scWidth / 2 - m), 15.0F, 16777215);
       } 
     } 
   }
 
   
   private String dCoord(int paramInt1) {
     if (paramInt1 < 0) {
       return "-" + Math.abs(paramInt1);
     }
     return (paramInt1 > 0) ? ("+" + paramInt1) : (" " + paramInt1);
   }
 
   
   private int chkLen(String string) {
     return this.fontRenderer.method_1727(string);
   }
   
   private void write(class_4587 matrixStack, String text, float x, float y, int color) {
     this.fontRenderer.method_1720(matrixStack, text, x, y, color);
   }
   
   private int chkLen(class_2561 text) {
     return this.fontRenderer.method_27525((class_5348)text);
   }
   
   private void write(class_4587 matrixStack, class_2561 text, float x, float y, int color) {
     this.fontRenderer.method_30881(matrixStack, text, x, y, color);
   }
   
   private void drawWelcomeScreen(class_4587 matrixStack, int scWidth, int scHeight) {
     if (this.welcomeText[1] == null || this.welcomeText[1].getString().equals("minimap.ui.welcome2")) {
       this.welcomeText[0] = (class_2561)class_2561.method_43470("").method_10852((class_2561)class_2561.method_43470("VoxelMap! ").method_27692(class_124.field_1061)).method_27693(this.zmodver + " ").method_10852((class_2561)class_2561.method_43471("minimap.ui.welcome1"));
       this.welcomeText[1] = (class_2561)class_2561.method_43471("minimap.ui.welcome2");
       this.welcomeText[2] = (class_2561)class_2561.method_43471("minimap.ui.welcome3");
       this.welcomeText[3] = (class_2561)class_2561.method_43471("minimap.ui.welcome4");
       this.welcomeText[4] = (class_2561)class_2561.method_43470("").method_10852((class_2561)class_2561.method_43472(this.options.keyBindZoom.method_1431()).method_27692(class_124.field_1075)).method_27693(": ").method_10852((class_2561)class_2561.method_43471("minimap.ui.welcome5a")).method_27693(", ").method_10852((class_2561)class_2561.method_43472(this.options.keyBindMenu.method_1431()).method_27692(class_124.field_1075)).method_27693(": ").method_10852((class_2561)class_2561.method_43471("minimap.ui.welcome5b"));
       this.welcomeText[5] = (class_2561)class_2561.method_43470("").method_10852((class_2561)class_2561.method_43472(this.options.keyBindFullscreen.method_1431()).method_27692(class_124.field_1075)).method_27693(": ").method_10852((class_2561)class_2561.method_43471("minimap.ui.welcome6"));
       this.welcomeText[6] = (class_2561)class_2561.method_43470("").method_10852((class_2561)class_2561.method_43472(this.options.keyBindWaypoint.method_1431()).method_27692(class_124.field_1075)).method_27693(": ").method_10852((class_2561)class_2561.method_43471("minimap.ui.welcome7"));
       this.welcomeText[7] = (class_2561)this.options.keyBindZoom.method_16007().method_27661().method_27693(": ").method_10852((class_2561)class_2561.method_43471("minimap.ui.welcome8").method_27692(class_124.field_1080));
     } 
     
     GLShim.glBlendFunc(770, 771);
     int maxSize = 0;
     int border = 2;
     class_2561 head = this.welcomeText[0];
     
     int height;
     for (height = 1; height < this.welcomeText.length - 1; height++) {
       if (chkLen(this.welcomeText[height]) > maxSize) {
         maxSize = chkLen(this.welcomeText[height]);
       }
     } 
     
     int title = chkLen(head);
     int centerX = (int)((scWidth + 5) / 2.0D);
     int centerY = (int)((scHeight + 5) / 2.0D);
     class_2561 hide = this.welcomeText[this.welcomeText.length - 1];
     int footer = chkLen(hide);
     GLShim.glDisable(3553);
     GLShim.glColor4f(0.0F, 0.0F, 0.0F, 0.7F);
     double leftX = centerX - title / 2.0D - border;
     double rightX = centerX + title / 2.0D + border;
     double topY = centerY - (height - 1) / 2.0D * 10.0D - border - 20.0D;
     double botY = centerY - (height - 1) / 2.0D * 10.0D + border - 10.0D;
     drawBox(leftX, rightX, topY, botY);
     leftX = centerX - maxSize / 2.0D - border;
     rightX = centerX + maxSize / 2.0D + border;
     topY = centerY - (height - 1) / 2.0D * 10.0D - border;
     botY = centerY + (height - 1) / 2.0D * 10.0D + border;
     drawBox(leftX, rightX, topY, botY);
     leftX = centerX - footer / 2.0D - border;
     rightX = centerX + footer / 2.0D + border;
     topY = centerY + (height - 1) / 2.0D * 10.0D - border + 10.0D;
     botY = centerY + (height - 1) / 2.0D * 10.0D + border + 20.0D;
     drawBox(leftX, rightX, topY, botY);
     GLShim.glEnable(3553);
     write(matrixStack, head, (centerX - title / 2), (centerY - (height - 1) * 10 / 2 - 19), 16777215);
     
     for (int n = 1; n < height; n++) {
       write(matrixStack, this.welcomeText[n], (centerX - maxSize / 2), (centerY - (height - 1) * 10 / 2 + n * 10 - 9), 16777215);
     }
     
     write(matrixStack, hide, (centerX - footer / 2), ((scHeight + 5) / 2 + (height - 1) * 10 / 2 + 11), 16777215);
   }
   
   private void drawBox(double leftX, double rightX, double topY, double botY) {
     GLUtils.drawPre(class_290.field_1592);
     GLUtils.ldrawtwo(leftX, botY, 0.0D);
     GLUtils.ldrawtwo(rightX, botY, 0.0D);
     GLUtils.ldrawtwo(rightX, topY, 0.0D);
     GLUtils.ldrawtwo(leftX, topY, 0.0D);
     GLUtils.drawPost();
   }
 }


