package com.mamiyaotaru.voxelmap;

import com.mamiyaotaru.voxelmap.gui.GuiAddWaypoint;
import com.mamiyaotaru.voxelmap.gui.GuiWaypoints;
import com.mamiyaotaru.voxelmap.gui.IGuiWaypoints;
import com.mamiyaotaru.voxelmap.gui.overridden.EnumOptionsMinimap;
import com.mamiyaotaru.voxelmap.interfaces.AbstractMapData;
import com.mamiyaotaru.voxelmap.interfaces.AbstractVoxelMap;
import com.mamiyaotaru.voxelmap.interfaces.IColorManager;
import com.mamiyaotaru.voxelmap.interfaces.IMap;
import com.mamiyaotaru.voxelmap.interfaces.IVoxelMap;
import com.mamiyaotaru.voxelmap.interfaces.IWaypointManager;
import com.mamiyaotaru.voxelmap.persistent.GuiPersistentMap;
import com.mamiyaotaru.voxelmap.textures.Sprite;
import com.mamiyaotaru.voxelmap.textures.TextureAtlas;
import com.mamiyaotaru.voxelmap.util.BiomeRepository;
import com.mamiyaotaru.voxelmap.util.BlockRepository;
import com.mamiyaotaru.voxelmap.util.ColorUtils;
import com.mamiyaotaru.voxelmap.util.FullMapData;
import com.mamiyaotaru.voxelmap.util.GLShim;
import com.mamiyaotaru.voxelmap.util.GLUtils;
import com.mamiyaotaru.voxelmap.util.GameVariableAccessShim;
import com.mamiyaotaru.voxelmap.util.I18nUtils;
import com.mamiyaotaru.voxelmap.util.LayoutVariables;
import com.mamiyaotaru.voxelmap.util.LiveScaledGLBufferedImage;
import com.mamiyaotaru.voxelmap.util.MapChunkCache;
import com.mamiyaotaru.voxelmap.util.MapUtils;
import com.mamiyaotaru.voxelmap.util.MutableBlockPos;
import com.mamiyaotaru.voxelmap.util.MutableNativeImageBackedTexture;
import com.mamiyaotaru.voxelmap.util.ReflectionUtils;
import com.mamiyaotaru.voxelmap.util.ScaledMutableNativeImageBackedTexture;
import com.mamiyaotaru.voxelmap.util.TickCounter;
import com.mamiyaotaru.voxelmap.util.Waypoint;
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
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;
import javax.imageio.ImageIO;
import net.minecraft.class_1043;
import net.minecraft.class_1159;
import net.minecraft.class_1160;
import net.minecraft.class_124;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1937;
import net.minecraft.class_1944;
import net.minecraft.class_1959;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2350;
import net.minecraft.class_2368;
import net.minecraft.class_2378;
import net.minecraft.class_2506;
import net.minecraft.class_2561;
import net.minecraft.class_259;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_286;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_304;
import net.minecraft.class_308;
import net.minecraft.class_310;
import net.minecraft.class_315;
import net.minecraft.class_327;
import net.minecraft.class_3298;
import net.minecraft.class_3532;
import net.minecraft.class_3610;
import net.minecraft.class_3612;
import net.minecraft.class_3614;
import net.minecraft.class_4060;
import net.minecraft.class_418;
import net.minecraft.class_428;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import net.minecraft.class_638;
import net.minecraft.class_757;
import net.minecraft.class_758;
import net.minecraft.class_765;
import net.minecraft.class_2902.class_2903;
import org.lwjgl.BufferUtils;

public class Map implements Runnable, IMap {
    private final float[] lastLightBrightnessTable = new float[16];
    private final Object coordinateLock = new Object();
    private final class_2960 arrowResourceLocation = new class_2960("voxelmap", "images/mmarrow.png");
    private final class_2960 roundmapResourceLocation = new class_2960("voxelmap", "images/roundmap.png");
    private final class_2960 squareStencil = new class_2960("voxelmap", "images/square.png");
    private final class_2960 circleStencil = new class_2960("voxelmap", "images/circle.png");
    LiveScaledGLBufferedImage roundImage = new LiveScaledGLBufferedImage(128, 128, 6);
    private final IVoxelMap master;
    private class_310 game;
    private class_638 world = null;
    private final MapSettingsManager options;
    private final LayoutVariables layoutVariables;
    private final IColorManager colorManager;
    private final IWaypointManager waypointManager;
    private final int availableProcessors = Runtime.getRuntime().availableProcessors();
    private final boolean multicore;
    private final int heightMapResetHeight;
    private final int heightMapResetTime;
    private final boolean threading;
    private final FullMapData[] mapData;
    private final MapChunkCache[] chunkCache;
    private MutableNativeImageBackedTexture[] mapImages;
    private final MutableNativeImageBackedTexture[] mapImagesFiltered;
    private final MutableNativeImageBackedTexture[] mapImagesUnfiltered;
    private MutableBlockPos blockPos;
    private final MutableBlockPos tempBlockPos;
    private class_2680 transparentBlockState;
    private class_2680 surfaceBlockState;
    private class_2680 seafloorBlockState;
    private class_2680 foliageBlockState;
    private boolean imageChanged;
    private class_1043 lightmapTexture;
    private boolean needLightmapRefresh;
    private int tickWithLightChange;
    private boolean lastPaused;
    private double lastGamma;
    private float lastSunBrightness;
    private float lastLightning;
    private float lastPotion;
    private final int[] lastLightmapValues;
    private boolean lastBeneathRendering;
    private boolean needSkyColor;
    private boolean lastAboveHorizon;
    private int lastBiome;
    private int lastSkyColor;
    private final Random generator;
    private boolean showWelcomeScreen;
    private class_437 lastGuiScreen;
    private boolean enabled;
    private boolean fullscreenMap;
    private boolean active;
    private int zoom;
    private int mapX;
    private int mapY;
    private int scWidth;
    private int scHeight;
    private String error;
    private final class_2561[] welcomeText;
    private int ztimer;
    private int heightMapFudge;
    private int timer;
    private boolean doFullRender;
    private boolean zoomChanged;
    private int lastX;
    private int lastZ;
    private int lastY;
    private int lastImageX;
    private int lastImageZ;
    private boolean lastFullscreen;
    private float direction;
    private float percentX;
    private float percentY;
    private String subworldName;
    private int northRotate;
    private Thread zCalc;
    private int zCalcTicker;
    private final class_327 fontRenderer;
    private final int[] lightmapColors;
    private double zoomScale;
    private double zoomScaleAdjusted;
    private int mapImageInt;

    public Map(IVoxelMap master) {
        this.multicore = this.availableProcessors > 1;
        this.heightMapResetHeight = this.multicore ? 2 : 5;
        this.heightMapResetTime = this.multicore ? 300 : 3000;
        this.threading = this.multicore;
        this.mapData = new FullMapData[5];
        this.chunkCache = new MapChunkCache[5];
        this.mapImagesFiltered = new MutableNativeImageBackedTexture[5];
        this.mapImagesUnfiltered = new MutableNativeImageBackedTexture[5];
        this.blockPos = new MutableBlockPos(0, 0, 0);
        this.tempBlockPos = new MutableBlockPos(0, 0, 0);
        this.imageChanged = true;
        this.lightmapTexture = null;
        this.needLightmapRefresh = true;
        this.tickWithLightChange = 0;
        this.lastPaused = true;
        this.lastGamma = 0.0;
        this.lastSunBrightness = 0.0F;
        this.lastLightning = 0.0F;
        this.lastPotion = 0.0F;
        this.lastLightmapValues = new int[]{-16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216};
        this.lastBeneathRendering = false;
        this.needSkyColor = false;
        this.lastAboveHorizon = true;
        this.lastBiome = 0;
        this.lastSkyColor = 0;
        this.generator = new Random();
        this.lastGuiScreen = null;
        this.enabled = true;
        this.fullscreenMap = false;
        this.active = false;
        this.mapX = 37;
        this.mapY = 37;
        this.error = "";
        this.welcomeText = new class_2561[8];
        this.ztimer = 0;
        this.heightMapFudge = 0;
        this.timer = 0;
        this.doFullRender = true;
        this.lastX = 0;
        this.lastZ = 0;
        this.lastY = 0;
        this.lastImageX = 0;
        this.lastImageZ = 0;
        this.lastFullscreen = false;
        this.direction = 0.0F;
        this.subworldName = "";
        this.northRotate = 0;
        this.zCalc = new Thread(this, "Voxelmap LiveMap Calculation Thread");
        this.zCalcTicker = 0;
        this.lightmapColors = new int[256];
        this.zoomScale = 1.0;
        this.zoomScaleAdjusted = 1.0;
        this.mapImageInt = -1;
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
            f.set(this.game.field_1690, tempBindings.toArray(new class_304[tempBindings.size()]));
        } catch (IllegalAccessException | IllegalArgumentException var7) {
            var7.printStackTrace();
        }

        java.util.Map categoryOrder = (java.util.Map)ReflectionUtils.getPrivateFieldValueByType((Object)null, class_304.class, java.util.Map.class, 2);
        System.out.println("CATEGORY ORDER IS " + categoryOrder.size());
        Integer categoryPlace = (Integer)categoryOrder.get("controls.minimap.title");
        if (categoryPlace == null) {
            int currentSize = categoryOrder.size();
            categoryOrder.put("controls.minimap.title", currentSize + 1);
        }

        this.showWelcomeScreen = this.options.welcome;
        this.zCalc.start();
        this.zCalc.setPriority(5);
        this.mapData[0] = new FullMapData(32, 32);
        this.mapData[1] = new FullMapData(64, 64);
        this.mapData[2] = new FullMapData(128, 128);
        this.mapData[3] = new FullMapData(256, 256);
        this.mapData[4] = new FullMapData(512, 512);
        this.chunkCache[0] = new MapChunkCache(3, 3, this);
        this.chunkCache[1] = new MapChunkCache(5, 5, this);
        this.chunkCache[2] = new MapChunkCache(9, 9, this);
        this.chunkCache[3] = new MapChunkCache(17, 17, this);
        this.chunkCache[4] = new MapChunkCache(33, 33, this);
        this.mapImagesFiltered[0] = new MutableNativeImageBackedTexture(32, 32, true);
        this.mapImagesFiltered[1] = new MutableNativeImageBackedTexture(64, 64, true);
        this.mapImagesFiltered[2] = new MutableNativeImageBackedTexture(128, 128, true);
        this.mapImagesFiltered[3] = new MutableNativeImageBackedTexture(256, 256, true);
        this.mapImagesFiltered[4] = new MutableNativeImageBackedTexture(512, 512, true);
        this.mapImagesUnfiltered[0] = new ScaledMutableNativeImageBackedTexture(32, 32, true);
        this.mapImagesUnfiltered[1] = new ScaledMutableNativeImageBackedTexture(64, 64, true);
        this.mapImagesUnfiltered[2] = new ScaledMutableNativeImageBackedTexture(128, 128, true);
        this.mapImagesUnfiltered[3] = new ScaledMutableNativeImageBackedTexture(256, 256, true);
        this.mapImagesUnfiltered[4] = new ScaledMutableNativeImageBackedTexture(512, 512, true);
        if (this.options.filtering) {
            this.mapImages = this.mapImagesFiltered;
        } else {
            this.mapImages = this.mapImagesUnfiltered;
        }

        GLUtils.setupFrameBuffer();
        this.fontRenderer = this.game.field_1772;
        this.zoom = this.options.zoom;
        this.setZoomScale();
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
            while(true) {
                while(this.threading) {
                    for(this.active = true; this.game.field_1724 != null && this.world != null && this.active; this.active = false) {
                        if (!this.options.hide) {
                            try {
                                this.mapCalc(this.doFullRender);
                                if (!this.doFullRender) {
                                    this.chunkCache[this.zoom].centerChunks(this.blockPos.withXYZ(this.lastX, 0, this.lastZ));
                                    this.chunkCache[this.zoom].checkIfChunksChanged();
                                }
                            } catch (Exception var9) {
                            }
                        }

                        this.doFullRender = this.zoomChanged;
                        this.zoomChanged = false;
                    }

                    this.zCalcTicker = 0;
                    synchronized(this.zCalc) {
                        try {
                            this.zCalc.wait(0L);
                        } catch (InterruptedException var7) {
                        }
                    }
                }

                synchronized(this.zCalc) {
                    try {
                        this.zCalc.wait(0L);
                    } catch (InterruptedException var5) {
                    }
                }
            }
        }

    }

    public void newWorld(class_638 world) {
        this.world = world;
        this.lightmapTexture = this.getLightmapTexture();
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
            this.lightmapTexture = this.getLightmapTexture();
        }

        if (this.game.field_1755 == null && this.options.keyBindMenu.method_1436()) {
            this.showWelcomeScreen = false;
            if (this.options.welcome) {
                this.options.welcome = false;
                this.options.saveAll();
            }

            this.game.method_1507(new GuiPersistentMap((class_437)null, this.master));
        }

        if (this.game.field_1755 == null && this.options.keyBindWaypointMenu.method_1436()) {
            this.showWelcomeScreen = false;
            if (this.options.welcome) {
                this.options.welcome = false;
                this.options.saveAll();
            }

            this.game.method_1507(new GuiWaypoints((class_437)null, this.master));
        }

        if (this.game.field_1755 == null && this.options.keyBindWaypoint.method_1436()) {
            this.showWelcomeScreen = false;
            if (this.options.welcome) {
                this.options.welcome = false;
                this.options.saveAll();
            }

            float r;
            float g;
            float b;
            if (this.waypointManager.getWaypoints().size() == 0) {
                r = 0.0F;
                g = 1.0F;
                b = 0.0F;
            } else {
                r = this.generator.nextFloat();
                g = this.generator.nextFloat();
                b = this.generator.nextFloat();
            }

            TreeSet dimensions = new TreeSet();
            dimensions.add(AbstractVoxelMap.getInstance().getDimensionManager().getDimensionContainerByWorld(this.game.field_1687));
            double dimensionScale = this.game.field_1724.field_6002.method_8597().comp_646();
            Waypoint newWaypoint = new Waypoint("", (int)((double)GameVariableAccessShim.xCoord() * dimensionScale), (int)((double)GameVariableAccessShim.zCoord() * dimensionScale), GameVariableAccessShim.yCoord(), true, r, g, b, "", this.master.getWaypointManager().getCurrentSubworldDescriptor(false), dimensions);
            this.game.method_1507(new GuiAddWaypoint((IGuiWaypoints)null, this.master, newWaypoint, false));
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
                this.cycleZoomLevel();
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

        this.checkForChanges();
        if (this.game.field_1755 instanceof class_418 && !(this.lastGuiScreen instanceof class_418)) {
            this.waypointManager.handleDeath();
        }

        this.lastGuiScreen = this.game.field_1755;
        this.calculateCurrentLightAndSkyColor();
        if (this.threading) {
            if (!this.zCalc.isAlive() && this.threading) {
                this.zCalc = new Thread(this, "Voxelmap LiveMap Calculation Thread");
                this.zCalc.setPriority(5);
                this.zCalc.start();
            }

            if (!(this.game.field_1755 instanceof class_418) && !(this.game.field_1755 instanceof class_428)) {
                ++this.zCalcTicker;
                if (this.zCalcTicker > 2000) {
                    this.zCalcTicker = 0;
                    this.zCalc.stop();
                } else {
                    synchronized(this.zCalc) {
                        this.zCalc.notify();
                    }
                }
            }
        } else if (!this.threading) {
            if (!this.options.hide && this.world != null) {
                this.mapCalc(this.doFullRender);
                if (!this.doFullRender) {
                    this.chunkCache[this.zoom].centerChunks(this.blockPos.withXYZ(this.lastX, 0, this.lastZ));
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

        for(this.direction = GameVariableAccessShim.rotationYaw() + 180.0F; this.direction >= 360.0F; this.direction -= 360.0F) {
        }

        while(this.direction < 0.0F) {
            this.direction += 360.0F;
        }

        if (!this.error.equals("") && this.ztimer == 0) {
            this.ztimer = 500;
        }

        if (this.ztimer > 0) {
            --this.ztimer;
        }

        if (this.ztimer == 0 && !this.error.equals("")) {
            this.error = "";
        }

        if (this.enabled) {
            this.drawMinimap(matrixStack, mc);
        }

        this.timer = this.timer > 5000 ? 0 : this.timer + 1;
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
            if (this.multicore && (Integer)this.game.field_1690.method_42510().method_41753() > 8) {
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
        this.setZoomScale();
        this.mapImages[this.zoom].blank();
        this.doFullRender = true;
    }

    private void setZoomScale() {
        this.zoomScale = Math.pow(2.0, (double)this.zoom) / 2.0;
        if (this.options.squareMap && this.options.rotates) {
            this.zoomScaleAdjusted = this.zoomScale / 1.414199948310852;
        } else {
            this.zoomScaleAdjusted = this.zoomScale;
        }

    }

    private class_1043 getLightmapTexture() {
        class_765 lightTextureManager = this.game.field_1773.method_22974();
        Object lightmapTextureObj = ReflectionUtils.getPrivateFieldValueByType(lightTextureManager, class_765.class, class_1043.class);
        return lightmapTextureObj == null ? null : (class_1043)lightmapTextureObj;
    }

    public void calculateCurrentLightAndSkyColor() {
        try {
            if (this.world != null) {
                int t;
                if (this.needLightmapRefresh && TickCounter.tickCounter != this.tickWithLightChange && !this.game.method_1493() || this.options.realTimeTorches) {
                    GLUtils.disp(this.lightmapTexture.method_4624());
                    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024).order(ByteOrder.nativeOrder());
                    GLShim.glGetTexImage(3553, 0, 6408, 5121, byteBuffer);

                    for(int i = 0; i < this.lightmapColors.length; ++i) {
                        t = i * 4;
                        this.lightmapColors[i] = (byteBuffer.get(t + 3) << 24) + (byteBuffer.get(t) << 16) + (byteBuffer.get(t + 1) << 8) + (byteBuffer.get(t + 2) << 0);
                    }

                    if (this.lightmapColors[255] != 0) {
                        this.needLightmapRefresh = false;
                    }
                }

                boolean lightChanged = false;
                if ((Double)this.game.field_1690.method_42473().method_41753() != this.lastGamma) {
                    lightChanged = true;
                    this.lastGamma = (Double)this.game.field_1690.method_42473().method_41753();
                }

                float[] providerLightBrightnessTable = new float[16];

                for(t = 0; t < 16; ++t) {
                    providerLightBrightnessTable[t] = this.world.method_8597().method_28528((long)t);
                }

                for(t = 0; t < 16; ++t) {
                    if (providerLightBrightnessTable[t] != this.lastLightBrightnessTable[t]) {
                        lightChanged = true;
                        this.lastLightBrightnessTable[t] = providerLightBrightnessTable[t];
                    }
                }

                float sunBrightness = this.world.method_23783(1.0F);
                if ((double)Math.abs(this.lastSunBrightness - sunBrightness) > 0.01 || (double)sunBrightness == 1.0 && sunBrightness != this.lastSunBrightness || (double)sunBrightness == 0.0 && sunBrightness != this.lastSunBrightness) {
                    lightChanged = true;
                    this.needSkyColor = true;
                    this.lastSunBrightness = sunBrightness;
                }

                float potionEffect = 0.0F;
                int lastLightningBolt;
                if (this.game.field_1724.method_6059(class_1294.field_5925)) {
                    lastLightningBolt = this.game.field_1724.method_6112(class_1294.field_5925).method_5584();
                    potionEffect = lastLightningBolt > 200 ? 1.0F : 0.7F + class_3532.method_15374(((float)lastLightningBolt - 1.0F) * 3.1415927F * 0.2F) * 0.3F;
                }

                if (this.lastPotion != potionEffect) {
                    this.lastPotion = potionEffect;
                    lightChanged = true;
                }

                lastLightningBolt = this.world.method_23789();
                if (this.lastLightning != (float)lastLightningBolt) {
                    this.lastLightning = (float)lastLightningBolt;
                    lightChanged = true;
                }

                if (this.lastPaused != this.game.method_1493()) {
                    this.lastPaused = !this.lastPaused;
                    lightChanged = true;
                }

                boolean scheduledUpdate = (this.timer - 50) % (this.lastLightBrightnessTable[0] == 0.0F ? 250 : 2000) == 0;
                if (lightChanged || scheduledUpdate) {
                    this.tickWithLightChange = TickCounter.tickCounter;
                    lightChanged = false;
                    this.needLightmapRefresh = true;
                }

                boolean aboveHorizon = this.game.field_1724.method_5836(0.0F).field_1351 >= this.world.method_28104().method_28105(this.world);
                if (this.world.method_27983().method_29177().toString().toLowerCase().contains("ether")) {
                    aboveHorizon = true;
                }

                if (aboveHorizon != this.lastAboveHorizon) {
                    this.needSkyColor = true;
                    this.lastAboveHorizon = aboveHorizon;
                }

                int biomeID = this.world.method_30349().method_30530(class_2378.field_25114).method_10206((class_1959)this.world.method_23753(this.blockPos.withXYZ(GameVariableAccessShim.xCoord(), GameVariableAccessShim.yCoord(), GameVariableAccessShim.zCoord())).comp_349());
                if (biomeID != this.lastBiome) {
                    this.needSkyColor = true;
                    this.lastBiome = biomeID;
                }

                if (this.needSkyColor || scheduledUpdate) {
                    this.colorManager.setSkyColor(this.getSkyColor());
                }
            }
        } catch (NullPointerException var9) {
        }

    }

    private int getSkyColor() {
        this.needSkyColor = false;
        boolean aboveHorizon = this.lastAboveHorizon;
        float[] fogColors = new float[4];
        FloatBuffer temp = BufferUtils.createFloatBuffer(4);
        class_758.method_3210(this.game.field_1773.method_19418(), 0.0F, this.world, (Integer)this.game.field_1690.method_42503().method_41753(), this.game.field_1773.method_3195(0.0F));
        GLShim.glGetFloatv(3106, temp);
        temp.get(fogColors);
        float r = fogColors[0];
        float g = fogColors[1];
        float b = fogColors[2];
        if (!aboveHorizon && (Integer)this.game.field_1690.method_42503().method_41753() >= 4) {
            return 167772160 + (int)(r * 255.0F) * 65536 + (int)(g * 255.0F) * 256 + (int)(b * 255.0F);
        } else {
            int backgroundColor = -16777216 + (int)(r * 255.0F) * 65536 + (int)(g * 255.0F) * 256 + (int)(b * 255.0F);
            float[] sunsetColors = this.world.method_28103().method_28109(this.world.method_30274(0.0F), 0.0F);
            if (sunsetColors != null && (Integer)this.game.field_1690.method_42503().method_41753() >= 4) {
                int sunsetColor = (int)(sunsetColors[3] * 128.0F) * 16777216 + (int)(sunsetColors[0] * 255.0F) * 65536 + (int)(sunsetColors[1] * 255.0F) * 256 + (int)(sunsetColors[2] * 255.0F);
                return ColorUtils.colorAdder(sunsetColor, backgroundColor);
            } else {
                return backgroundColor;
            }
        }
    }

    public int[] getLightmapArray() {
        return this.lightmapColors;
    }

    public void drawMinimap(class_4587 matrixStack, class_310 mc) {
        int scScaleOrig;
        for(scScaleOrig = 1; this.game.method_22683().method_4489() / (scScaleOrig + 1) >= 320 && this.game.method_22683().method_4506() / (scScaleOrig + 1) >= 240; ++scScaleOrig) {
        }

        int scScale = scScaleOrig + (this.fullscreenMap ? 0 : this.options.sizeModifier);
        double scaledWidthD = (double)this.game.method_22683().method_4489() / (double)scScale;
        double scaledHeightD = (double)this.game.method_22683().method_4506() / (double)scScale;
        this.scWidth = class_3532.method_15384(scaledWidthD);
        this.scHeight = class_3532.method_15384(scaledHeightD);
        RenderSystem.backupProjectionMatrix();
        class_1159 matrix4f = class_1159.method_34239(0.0F, (float)scaledWidthD, 0.0F, (float)scaledHeightD, 1000.0F, 3000.0F);
        RenderSystem.setProjectionMatrix(matrix4f);
        class_4587 modelViewMatrixStack = RenderSystem.getModelViewStack();
        modelViewMatrixStack.method_34426();
        modelViewMatrixStack.method_22904(0.0, 0.0, -2000.0);
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
            Iterator var12 = this.game.field_1724.method_6026().iterator();

            while(var12.hasNext()) {
                class_1293 statusEffectInstance = (class_1293)var12.next();
                if (statusEffectInstance.method_5592()) {
                    if (statusEffectInstance.method_5579().method_5573()) {
                        statusIconOffset = Math.max(statusIconOffset, 24.0F);
                    } else {
                        statusIconOffset = Math.max(statusIconOffset, 50.0F);
                    }
                }
            }

            int scHeight = this.game.method_22683().method_4502();
            float resFactor = (float)this.scHeight / (float)scHeight;
            this.mapY += (int)(statusIconOffset * resFactor);
        }

        GLShim.glEnable(3042);
        GLShim.glEnable(3553);
        GLShim.glBlendFunc(770, 0);
        GLShim.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (!this.options.hide) {
            if (this.fullscreenMap) {
                this.renderMapFull(modelViewMatrixStack, this.scWidth, this.scHeight);
            } else {
                this.renderMap(modelViewMatrixStack, this.mapX, this.mapY, scScale);
            }

            GLShim.glDisable(2929);
            if (this.master.getRadar() != null && !this.fullscreenMap) {
                this.layoutVariables.updateVars(scScale, this.mapX, this.mapY, this.zoomScale, this.zoomScaleAdjusted);
                this.master.getRadar().onTickInGame(modelViewMatrixStack, mc, this.layoutVariables);
            }

            if (!this.fullscreenMap) {
                this.drawDirections(matrixStack, this.mapX, this.mapY);
            }

            GLShim.glEnable(3042);
            if (this.fullscreenMap) {
                this.drawArrow(modelViewMatrixStack, this.scWidth / 2, this.scHeight / 2);
            } else {
                this.drawArrow(modelViewMatrixStack, this.mapX, this.mapY);
            }
        }

        if (this.options.coords) {
            this.showCoords(matrixStack, this.mapX, this.mapY);
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
        this.game.field_1772.method_30881(modelViewMatrixStack, class_2561.method_43470("******sdkfjhsdkjfhsdkjfh"), 100.0F, 100.0F, -1);
        if (this.showWelcomeScreen) {
            GLShim.glEnable(3042);
            this.drawWelcomeScreen(matrixStack, this.game.method_22683().method_4486(), this.game.method_22683().method_4502());
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
            this.loadMapImage();
            changed = true;
        }

        if (this.options.isChanged()) {
            if (this.options.filtering) {
                this.mapImages = this.mapImagesFiltered;
            } else {
                this.mapImages = this.mapImagesUnfiltered;
            }

            changed = true;
            this.setZoomScale();
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
        int multi = (int)Math.pow(2.0, (double)this.zoom);
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

            for(int t = 0; t < 16; ++t) {
                if (this.lastLightmapValues[t] != this.lightmapColors[t * skylightMultiplier + torchOffset]) {
                    needLight = true;
                    this.lastLightmapValues[t] = this.lightmapColors[t * skylightMultiplier + torchOffset];
                }
            }
        }

        if (offsetY != 0) {
            ++this.heightMapFudge;
        } else if (this.heightMapFudge != 0) {
            ++this.heightMapFudge;
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
        boolean beneathRendering;
        if (this.game.field_1724.field_6002.method_8597().comp_643()) {
            boolean netherPlayerInOpen = this.world.method_22350(this.blockPos).method_12005(class_2903.field_13197, this.blockPos.method_10263() & 15, this.blockPos.method_10260() & 15) <= currentY;
            nether = currentY < 126;
            if (this.options.cavesAllowed && this.options.showCaves && currentY >= 126 && !netherPlayerInOpen) {
                caves = true;
            }
        } else if (this.game.field_1724.field_17892.method_28103().method_28114() && !this.game.field_1724.field_17892.method_8597().comp_642()) {
            beneathRendering = this.world.method_22350(this.blockPos).method_12005(class_2903.field_13197, this.blockPos.method_10263() & 15, this.blockPos.method_10260() & 15) <= currentY;
            if (this.options.cavesAllowed && this.options.showCaves && !beneathRendering) {
                caves = true;
            }
        } else if (this.options.cavesAllowed && this.options.showCaves && this.world.method_8314(class_1944.field_9284, this.blockPos) <= 0) {
            caves = true;
        }

        beneathRendering = caves || nether;
        if (this.lastBeneathRendering != beneathRendering) {
            full = true;
        }

        this.lastBeneathRendering = beneathRendering;
        boolean needHeightAndID = needHeightMap && (nether || caves);
        int color24 = true;
        synchronized(this.coordinateLock) {
            if (!full) {
                this.mapImages[this.zoom].moveY(offsetZ);
                this.mapImages[this.zoom].moveX(offsetX);
            }

            this.lastX = currentX;
            this.lastZ = currentZ;
        }

        int startX = currentX - 16 * multi;
        int startZ = currentZ - 16 * multi;
        int imageY;
        int imageX;
        int color24;
        if (!full) {
            this.mapData[this.zoom].moveZ(offsetZ);
            this.mapData[this.zoom].moveX(offsetX);

            for(imageY = offsetZ > 0 ? 32 * multi - 1 : -offsetZ - 1; imageY >= (offsetZ > 0 ? 32 * multi - offsetZ : 0); --imageY) {
                for(imageX = 0; imageX < 32 * multi; ++imageX) {
                    color24 = this.getPixelColor(true, true, true, true, nether, caves, this.world, multi, startX, startZ, imageX, imageY);
                    this.mapImages[this.zoom].setRGB(imageX, imageY, color24);
                }
            }

            for(imageY = 32 * multi - 1; imageY >= 0; --imageY) {
                for(imageX = offsetX > 0 ? 32 * multi - offsetX : 0; imageX < (offsetX > 0 ? 32 * multi : -offsetX); ++imageX) {
                    color24 = this.getPixelColor(true, true, true, true, nether, caves, this.world, multi, startX, startZ, imageX, imageY);
                    this.mapImages[this.zoom].setRGB(imageX, imageY, color24);
                }
            }
        }

        if (full || this.options.heightmap && needHeightMap || needHeightAndID || this.options.lightmap && needLight || skyColorChanged) {
            for(imageY = 32 * multi - 1; imageY >= 0; --imageY) {
                for(imageX = 0; imageX < 32 * multi; ++imageX) {
                    color24 = this.getPixelColor(full, full || needHeightAndID, full, full || needLight || needHeightAndID, nether, caves, this.world, multi, startX, startZ, imageX, imageY);
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
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public void processChunk(class_2818 chunk) {
        this.rectangleCalc(chunk.method_12004().field_9181 * 16, chunk.method_12004().field_9180 * 16, chunk.method_12004().field_9181 * 16 + 15, chunk.method_12004().field_9180 * 16 + 15);
    }

    private void rectangleCalc(int left, int top, int right, int bottom) {
        boolean nether = false;
        boolean caves = false;
        boolean netherPlayerInOpen = false;
        this.blockPos.setXYZ(this.lastX, Math.max(Math.min(GameVariableAccessShim.yCoord(), 255), 0), this.lastZ);
        int currentY = GameVariableAccessShim.yCoord();
        if (this.game.field_1724.field_6002.method_8597().comp_643()) {
            netherPlayerInOpen = this.world.method_22350(this.blockPos).method_12005(class_2903.field_13197, this.blockPos.method_10263() & 15, this.blockPos.method_10260() & 15) <= currentY;
            nether = currentY < 126;
            if (this.options.cavesAllowed && this.options.showCaves && currentY >= 126 && !netherPlayerInOpen) {
                caves = true;
            }
        } else if (this.game.field_1724.field_17892.method_28103().method_28114() && !this.game.field_1724.field_17892.method_8597().comp_642()) {
            boolean endPlayerInOpen = this.world.method_22350(this.blockPos).method_12005(class_2903.field_13197, this.blockPos.method_10263() & 15, this.blockPos.method_10260() & 15) <= currentY;
            if (this.options.cavesAllowed && this.options.showCaves && !endPlayerInOpen) {
                caves = true;
            }
        } else if (this.options.cavesAllowed && this.options.showCaves && this.world.method_8314(class_1944.field_9284, this.blockPos) <= 0) {
            caves = true;
        }

        int startX = this.lastX;
        int startZ = this.lastZ;
        int multi = (int)Math.pow(2.0, (double)this.zoom);
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
        int color24 = false;

        for(int imageY = bottom; imageY >= top; --imageY) {
            for(int imageX = left; imageX <= right; ++imageX) {
                int color24 = this.getPixelColor(true, true, true, true, nether, caves, this.world, multi, startX, startZ, imageX, imageY);
                this.mapImages[this.zoom].setRGB(imageX, imageY, color24);
            }
        }

        this.imageChanged = true;
    }

    private int getPixelColor(boolean needBiome, boolean needHeightAndID, boolean needTint, boolean needLight, boolean nether, boolean caves, class_1937 world, int multi, int startX, int startZ, int imageX, int imageY) {
        int surfaceHeight = false;
        int seafloorHeight = -1;
        int transparentHeight = -1;
        int foliageHeight = true;
        int surfaceColor = false;
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
        int surfaceBlockStateID = false;
        int transparentBlockStateID = false;
        int foliageBlockStateID = false;
        int seafloorBlockStateID = false;
        this.blockPos = this.blockPos.withXYZ(startX + imageX, 0, startZ + imageY);
        int color24 = false;
        int biomeID = false;
        int biomeID;
        if (needBiome) {
            if (world.method_22340(this.blockPos)) {
                biomeID = world.method_30349().method_30530(class_2378.field_25114).method_10206((class_1959)world.method_23753(this.blockPos).comp_349());
            } else {
                biomeID = -1;
            }

            this.mapData[this.zoom].setBiomeID(imageX, imageY, biomeID);
        } else {
            biomeID = this.mapData[this.zoom].getBiomeID(imageX, imageY);
        }

        int color24;
        if (this.options.biomeOverlay == 1) {
            if (biomeID >= 0) {
                color24 = BiomeRepository.getBiomeColor(biomeID) | -16777216;
            } else {
                color24 = 0;
            }

            return MapUtils.doSlimeAndGrid(color24, startX + imageX, startZ + imageY);
        } else {
            boolean solid = false;
            int surfaceHeight;
            int foliageHeight;
            int surfaceBlockStateID;
            int transparentBlockStateID;
            int foliageBlockStateID;
            int seafloorBlockStateID;
            if (needHeightAndID) {
                if (!nether && !caves) {
                    class_2818 chunk = world.method_8500(this.blockPos);
                    transparentHeight = chunk.method_12005(class_2903.field_13197, this.blockPos.method_10263() & 15, this.blockPos.method_10260() & 15) + 1;
                    this.transparentBlockState = world.method_8320(this.blockPos.withXYZ(startX + imageX, transparentHeight - 1, startZ + imageY));
                    class_3610 fluidState = this.transparentBlockState.method_26227();
                    if (fluidState != class_3612.field_15906.method_15785()) {
                        this.transparentBlockState = fluidState.method_15759();
                    }

                    surfaceHeight = transparentHeight;
                    this.surfaceBlockState = this.transparentBlockState;
                    class_265 voxelShape = null;
                    boolean hasOpacity = this.surfaceBlockState.method_26193(world, this.blockPos) > 0;
                    if (!hasOpacity && this.surfaceBlockState.method_26225() && this.surfaceBlockState.method_26211()) {
                        voxelShape = this.surfaceBlockState.method_26173(world, this.blockPos, class_2350.field_11033);
                        hasOpacity = class_259.method_20713(voxelShape, class_259.method_1073());
                        voxelShape = this.surfaceBlockState.method_26173(world, this.blockPos, class_2350.field_11036);
                        hasOpacity = hasOpacity || class_259.method_20713(class_259.method_1073(), voxelShape);
                    }

                    while(!hasOpacity && surfaceHeight > 0) {
                        this.foliageBlockState = this.surfaceBlockState;
                        --surfaceHeight;
                        this.surfaceBlockState = world.method_8320(this.blockPos.withXYZ(startX + imageX, surfaceHeight - 1, startZ + imageY));
                        fluidState = this.surfaceBlockState.method_26227();
                        if (fluidState != class_3612.field_15906.method_15785()) {
                            this.surfaceBlockState = fluidState.method_15759();
                        }

                        hasOpacity = this.surfaceBlockState.method_26193(world, this.blockPos) > 0;
                        if (!hasOpacity && this.surfaceBlockState.method_26225() && this.surfaceBlockState.method_26211()) {
                            voxelShape = this.surfaceBlockState.method_26173(world, this.blockPos, class_2350.field_11033);
                            hasOpacity = class_259.method_20713(voxelShape, class_259.method_1073());
                            voxelShape = this.surfaceBlockState.method_26173(world, this.blockPos, class_2350.field_11036);
                            hasOpacity = hasOpacity || class_259.method_20713(class_259.method_1073(), voxelShape);
                        }
                    }

                    if (surfaceHeight == transparentHeight) {
                        transparentHeight = -1;
                        this.transparentBlockState = BlockRepository.air.method_9564();
                        this.foliageBlockState = world.method_8320(this.blockPos.withXYZ(startX + imageX, surfaceHeight, startZ + imageY));
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

                        for(this.seafloorBlockState = world.method_8320(this.blockPos.withXYZ(startX + imageX, surfaceHeight - 1, startZ + imageY)); this.seafloorBlockState.method_26193(world, this.blockPos) < 5 && this.seafloorBlockState.method_26207() != class_3614.field_15923 && seafloorHeight > 1; this.seafloorBlockState = world.method_8320(this.blockPos.withXYZ(startX + imageX, seafloorHeight - 1, startZ + imageY))) {
                            material = this.seafloorBlockState.method_26207();
                            if (transparentHeight == -1 && material != class_3614.field_15958 && material != class_3614.field_15920 && material.method_15801()) {
                                transparentHeight = seafloorHeight;
                                this.transparentBlockState = this.seafloorBlockState;
                            }

                            if (foliageHeight == -1 && seafloorHeight != transparentHeight && this.transparentBlockState != this.seafloorBlockState && material != class_3614.field_15958 && material != class_3614.field_15920 && material != class_3614.field_15959 && material != class_3614.field_15915) {
                                foliageHeight = seafloorHeight;
                                this.foliageBlockState = this.seafloorBlockState;
                            }

                            --seafloorHeight;
                        }

                        if (this.seafloorBlockState.method_26207() == class_3614.field_15920) {
                            this.seafloorBlockState = BlockRepository.air.method_9564();
                        }
                    }
                } else {
                    surfaceHeight = this.getNetherHeight(startX + imageX, startZ + imageY);
                    this.surfaceBlockState = world.method_8320(this.blockPos.withXYZ(startX + imageX, surfaceHeight - 1, startZ + imageY));
                    surfaceBlockStateID = BlockRepository.getStateId(this.surfaceBlockState);
                    foliageHeight = surfaceHeight + 1;
                    this.blockPos.setXYZ(startX + imageX, foliageHeight - 1, startZ + imageY);
                    this.foliageBlockState = world.method_8320(this.blockPos);
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

            int surfaceColor;
            boolean light;
            int light;
            if (this.options.biomes) {
                surfaceColor = this.colorManager.getBlockColor(this.blockPos, surfaceBlockStateID, biomeID);
                light = true;
                if (!needTint && !surfaceBlockChangeForcedTint) {
                    light = this.mapData[this.zoom].getBiomeTint(imageX, imageY);
                } else {
                    light = this.colorManager.getBiomeTint(this.mapData[this.zoom], world, this.surfaceBlockState, surfaceBlockStateID, this.blockPos.withXYZ(startX + imageX, surfaceHeight - 1, startZ + imageY), this.tempBlockPos, startX, startZ);
                    this.mapData[this.zoom].setBiomeTint(imageX, imageY, light);
                }

                if (light != -1) {
                    surfaceColor = ColorUtils.colorMultiplier(surfaceColor, light);
                }
            } else {
                surfaceColor = this.colorManager.getBlockColorWithDefaultTint(this.blockPos, surfaceBlockStateID);
            }

            surfaceColor = this.applyHeight(surfaceColor, nether, caves, world, multi, startX, startZ, imageX, imageY, surfaceHeight, solid, 1);
            light = !solid;
            if (needLight) {
                light = this.getLight(surfaceColor, this.surfaceBlockState, world, startX + imageX, startZ + imageY, surfaceHeight, solid);
                this.mapData[this.zoom].setLight(imageX, imageY, light);
            } else {
                light = this.mapData[this.zoom].getLight(imageX, imageY);
            }

            if (light == 0) {
                surfaceColor = 0;
            } else if (light != 255) {
                surfaceColor = ColorUtils.colorMultiplier(surfaceColor, light);
            }

            boolean foliageLight;
            int foliageLight;
            if (this.options.waterTransparency && seafloorHeight != -1) {
                if (!this.options.biomes) {
                    seafloorColor = this.colorManager.getBlockColorWithDefaultTint(this.blockPos, seafloorBlockStateID);
                } else {
                    seafloorColor = this.colorManager.getBlockColor(this.blockPos, seafloorBlockStateID, biomeID);
                    foliageLight = true;
                    if (!needTint && !seafloorBlockChangeForcedTint) {
                        foliageLight = this.mapData[this.zoom].getOceanFloorBiomeTint(imageX, imageY);
                    } else {
                        foliageLight = this.colorManager.getBiomeTint(this.mapData[this.zoom], world, this.seafloorBlockState, seafloorBlockStateID, this.blockPos.withXYZ(startX + imageX, seafloorHeight - 1, startZ + imageY), this.tempBlockPos, startX, startZ);
                        this.mapData[this.zoom].setOceanFloorBiomeTint(imageX, imageY, foliageLight);
                    }

                    if (foliageLight != -1) {
                        seafloorColor = ColorUtils.colorMultiplier(seafloorColor, foliageLight);
                    }
                }

                seafloorColor = this.applyHeight(seafloorColor, nether, caves, world, multi, startX, startZ, imageX, imageY, seafloorHeight, solid, 0);
                foliageLight = true;
                if (needLight) {
                    foliageLight = this.getLight(seafloorColor, this.seafloorBlockState, world, startX + imageX, startZ + imageY, seafloorHeight, solid);
                    this.blockPos.setXYZ(startX + imageX, seafloorHeight, startZ + imageY);
                    class_2680 blockStateAbove = world.method_8320(this.blockPos);
                    class_3614 materialAbove = blockStateAbove.method_26207();
                    if (this.options.lightmap && materialAbove == class_3614.field_15958) {
                        int multiplier = 255;
                        if (this.game.field_1690.method_41792().method_41753() == class_4060.field_18145) {
                            multiplier = 200;
                        } else if (this.game.field_1690.method_41792().method_41753() == class_4060.field_18146) {
                            multiplier = 120;
                        }

                        foliageLight = ColorUtils.colorMultiplier(foliageLight, -16777216 | multiplier << 16 | multiplier << 8 | multiplier);
                    }

                    this.mapData[this.zoom].setOceanFloorLight(imageX, imageY, foliageLight);
                } else {
                    foliageLight = this.mapData[this.zoom].getOceanFloorLight(imageX, imageY);
                }

                if (foliageLight == 0) {
                    seafloorColor = 0;
                } else if (foliageLight != 255) {
                    seafloorColor = ColorUtils.colorMultiplier(seafloorColor, foliageLight);
                }
            }

            if (this.options.blockTransparency) {
                if (transparentHeight != -1 && this.transparentBlockState != null && this.transparentBlockState != BlockRepository.air.method_9564()) {
                    if (this.options.biomes) {
                        transparentColor = this.colorManager.getBlockColor(this.blockPos, transparentBlockStateID, biomeID);
                        foliageLight = true;
                        if (!needTint && !transparentBlockChangeForcedTint) {
                            foliageLight = this.mapData[this.zoom].getTransparentBiomeTint(imageX, imageY);
                        } else {
                            foliageLight = this.colorManager.getBiomeTint(this.mapData[this.zoom], world, this.transparentBlockState, transparentBlockStateID, this.blockPos.withXYZ(startX + imageX, transparentHeight - 1, startZ + imageY), this.tempBlockPos, startX, startZ);
                            this.mapData[this.zoom].setTransparentBiomeTint(imageX, imageY, foliageLight);
                        }

                        if (foliageLight != -1) {
                            transparentColor = ColorUtils.colorMultiplier(transparentColor, foliageLight);
                        }
                    } else {
                        transparentColor = this.colorManager.getBlockColorWithDefaultTint(this.blockPos, transparentBlockStateID);
                    }

                    transparentColor = this.applyHeight(transparentColor, nether, caves, world, multi, startX, startZ, imageX, imageY, transparentHeight, solid, 3);
                    foliageLight = true;
                    if (needLight) {
                        foliageLight = this.getLight(transparentColor, this.transparentBlockState, world, startX + imageX, startZ + imageY, transparentHeight, solid);
                        this.mapData[this.zoom].setTransparentLight(imageX, imageY, foliageLight);
                    } else {
                        foliageLight = this.mapData[this.zoom].getTransparentLight(imageX, imageY);
                    }

                    if (foliageLight == 0) {
                        transparentColor = 0;
                    } else if (foliageLight != 255) {
                        transparentColor = ColorUtils.colorMultiplier(transparentColor, foliageLight);
                    }
                }

                if (foliageHeight != -1 && this.foliageBlockState != null && this.foliageBlockState != BlockRepository.air.method_9564()) {
                    if (!this.options.biomes) {
                        foliageColor = this.colorManager.getBlockColorWithDefaultTint(this.blockPos, foliageBlockStateID);
                    } else {
                        foliageColor = this.colorManager.getBlockColor(this.blockPos, foliageBlockStateID, biomeID);
                        foliageLight = true;
                        if (!needTint && !foliageBlockChangeForcedTint) {
                            foliageLight = this.mapData[this.zoom].getFoliageBiomeTint(imageX, imageY);
                        } else {
                            foliageLight = this.colorManager.getBiomeTint(this.mapData[this.zoom], world, this.foliageBlockState, foliageBlockStateID, this.blockPos.withXYZ(startX + imageX, foliageHeight - 1, startZ + imageY), this.tempBlockPos, startX, startZ);
                            this.mapData[this.zoom].setFoliageBiomeTint(imageX, imageY, foliageLight);
                        }

                        if (foliageLight != -1) {
                            foliageColor = ColorUtils.colorMultiplier(foliageColor, foliageLight);
                        }
                    }

                    foliageColor = this.applyHeight(foliageColor, nether, caves, world, multi, startX, startZ, imageX, imageY, foliageHeight, solid, 2);
                    foliageLight = true;
                    if (needLight) {
                        foliageLight = this.getLight(foliageColor, this.foliageBlockState, world, startX + imageX, startZ + imageY, foliageHeight, solid);
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
                foliageLight = 0;
                if (biomeID >= 0) {
                    foliageLight = BiomeRepository.getBiomeColor(biomeID);
                }

                foliageLight |= 2130706432;
                color24 = ColorUtils.colorAdder(foliageLight, color24);
            }

            return MapUtils.doSlimeAndGrid(color24, startX + imageX, startZ + imageY);
        }
    }

    private int getBlockHeight(boolean nether, boolean caves, class_1937 world, int x, int z) {
        int playerHeight = GameVariableAccessShim.yCoord();
        this.blockPos.setXYZ(x, playerHeight, z);
        class_2818 chunk = (class_2818)world.method_22350(this.blockPos);
        int height = chunk.method_12005(class_2903.field_13197, this.blockPos.method_10263() & 15, this.blockPos.method_10260() & 15) + 1;
        class_2680 blockState = world.method_8320(this.blockPos.withXYZ(x, height - 1, z));
        class_3610 fluidState = this.transparentBlockState.method_26227();
        if (fluidState != class_3612.field_15906.method_15785()) {
            blockState = fluidState.method_15759();
        }

        while(blockState.method_26193(world, this.blockPos) == 0 && height > 0) {
            --height;
            blockState = world.method_8320(this.blockPos.withXYZ(x, height - 1, z));
            fluidState = this.surfaceBlockState.method_26227();
            if (fluidState != class_3612.field_15906.method_15785()) {
                blockState = fluidState.method_15759();
            }
        }

        return (nether || caves) && height > playerHeight ? this.getNetherHeight(x, z) : height;
    }

    private int getNetherHeight(int x, int z) {
        int y = this.lastY;
        this.blockPos.setXYZ(x, y, z);
        class_2680 blockState = this.world.method_8320(this.blockPos);
        if (blockState.method_26193(this.world, this.blockPos) == 0 && blockState.method_26207() != class_3614.field_15922) {
            do {
                if (y <= this.world.method_31607()) {
                    return y;
                }

                --y;
                this.blockPos.setXYZ(x, y, z);
                blockState = this.world.method_8320(this.blockPos);
            } while(blockState.method_26193(this.world, this.blockPos) <= 0 && blockState.method_26207() != class_3614.field_15922);

            return y + 1;
        } else {
            while(y <= this.lastY + 10 && y < this.world.method_31600()) {
                ++y;
                this.blockPos.setXYZ(x, y, z);
                blockState = this.world.method_8320(this.blockPos);
                if (blockState.method_26193(this.world, this.blockPos) == 0 && blockState.method_26207() != class_3614.field_15922) {
                    return y;
                }
            }

            return -1;
        }
    }

    private final int getSeafloorHeight(class_1937 world, int x, int z, int height) {
        for(class_2680 blockState = world.method_8320(this.blockPos.withXYZ(x, height - 1, z)); blockState.method_26193(world, this.blockPos) < 5 && blockState.method_26207() != class_3614.field_15923 && height > 1; blockState = world.method_8320(this.blockPos.withXYZ(x, height - 1, z))) {
            --height;
        }

        return height;
    }

    private final int getTransparentHeight(boolean nether, boolean caves, class_1937 world, int x, int z, int height) {
        int transHeight = true;
        int transHeight;
        if (!caves && !nether) {
            transHeight = world.method_8598(class_2903.field_13197, this.blockPos.withXYZ(x, height, z)).method_10264();
            if (transHeight <= height) {
                transHeight = -1;
            }
        } else {
            transHeight = -1;
        }

        class_2680 blockState = world.method_8320(this.blockPos.withXYZ(x, transHeight - 1, z));
        class_3614 material = blockState.method_26207();
        if (transHeight == height + 1 && material == class_3614.field_15948) {
            transHeight = -1;
        }

        if (material == class_3614.field_15952) {
            ++transHeight;
            blockState = world.method_8320(this.blockPos.withXYZ(x, transHeight - 1, z));
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
            int diff = false;
            double sc = 0.0;
            int baseHeight;
            int diff;
            if (!this.options.slopemap) {
                if (this.options.heightmap) {
                    diff = height - this.lastY;
                    sc = Math.log10((double)Math.abs(diff) / 8.0 + 1.0) / 1.8;
                    if (diff < 0) {
                        sc = 0.0 - sc;
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
                            if (block instanceof class_2368 || block instanceof class_2506) {
                                heightComp = this.mapData[this.zoom].getHeight(imageX - 1, imageY + 1);
                            }
                        }
                    }
                } else {
                    if (layer == 0) {
                        baseHeight = this.getBlockHeight(nether, caves, world, startX + imageX - 1, startZ + imageY + 1);
                        heightComp = this.getSeafloorHeight(world, startX + imageX - 1, startZ + imageY + 1, baseHeight);
                    }

                    if (layer == 1) {
                        heightComp = this.getBlockHeight(nether, caves, world, startX + imageX - 1, startZ + imageY + 1);
                    }

                    if (layer == 2) {
                        heightComp = height;
                    }

                    if (layer == 3) {
                        baseHeight = this.getBlockHeight(nether, caves, world, startX + imageX - 1, startZ + imageY + 1);
                        heightComp = this.getTransparentHeight(nether, caves, world, startX + imageX - 1, startZ + imageY + 1, baseHeight);
                        if (heightComp == -1) {
                            class_2680 blockState = world.method_8320(this.blockPos.withXYZ(startX + imageX, height - 1, startZ + imageY));
                            class_2248 block = blockState.method_26204();
                            if (block instanceof class_2368 || block instanceof class_2506) {
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
                    sc = diff > 0 ? 1.0 : (diff < 0 ? -1.0 : 0.0);
                    sc /= 8.0;
                }

                if (this.options.heightmap) {
                    diff = height - this.lastY;
                    double heightsc = Math.log10((double)Math.abs(diff) / 8.0 + 1.0) / 3.0;
                    sc = diff > 0 ? sc + heightsc : sc - heightsc;
                }
            }

            baseHeight = color24 >> 24 & 255;
            int r = color24 >> 16 & 255;
            int g = color24 >> 8 & 255;
            int b = color24 >> 0 & 255;
            if (sc > 0.0) {
                r += (int)(sc * (double)(255 - r));
                g += (int)(sc * (double)(255 - g));
                b += (int)(sc * (double)(255 - b));
            } else if (sc < 0.0) {
                sc = Math.abs(sc);
                r -= (int)(sc * (double)r);
                g -= (int)(sc * (double)g);
                b -= (int)(sc * (double)b);
            }

            color24 = baseHeight * 16777216 + r * 65536 + g * 256 + b;
        }

        return color24;
    }

    private int getLight(int color24, class_2680 blockState, class_1937 world, int x, int z, int height, boolean solid) {
        int i3 = 255;
        if (solid) {
            i3 = 0;
        } else if (color24 != this.colorManager.getAirColor() && color24 != 0 && this.options.lightmap) {
            this.blockPos.setXYZ(x, Math.max(Math.min(height, 255), 0), z);
            int blockLight = world.method_8314(class_1944.field_9282, this.blockPos);
            int skyLight = world.method_8314(class_1944.field_9284, this.blockPos);
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
            GLUtils.setMap((float)x, (float)y, 128);
            GLUtils.drawPost();
            GLShim.glBlendFunc(772, 773);
            synchronized(this.coordinateLock) {
                if (this.imageChanged) {
                    this.imageChanged = false;
                    this.mapImages[this.zoom].write();
                    this.lastImageX = this.lastX;
                    this.lastImageZ = this.lastZ;
                }
            }

            float multi = (float)(1.0 / this.zoomScaleAdjusted);
            this.percentX = (float)(GameVariableAccessShim.xCoordDouble() - (double)this.lastImageX);
            this.percentY = (float)(GameVariableAccessShim.zCoordDouble() - (double)this.lastImageZ);
            this.percentX *= multi;
            this.percentY *= multi;
            GLUtils.disp2(this.mapImages[this.zoom].getIndex());
            matrixStack.method_22903();
            matrixStack.method_22904((double)x, (double)y, 0.0);
            matrixStack.method_22907(class_1160.field_20707.method_23214(!this.options.rotates ? (float)this.northRotate : -this.direction));
            matrixStack.method_22904((double)(-x), (double)(-y), 0.0);
            matrixStack.method_22904((double)(-this.percentX), (double)(-this.percentY), 0.0);
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
            matrixStack.method_22904(0.0, 0.0, -2000.0);
            RenderSystem.applyModelViewMatrix();
            GLShim.glDepthMask(false);
            GLShim.glDisable(2929);
            GLShim.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            GLShim.glClear(16384);
            GLShim.glBlendFunc(770, 0);
            GLUtils.img2(this.options.squareMap ? this.squareStencil : this.circleStencil);
            GLUtils.drawPre();
            GLUtils.ldrawthree((double)(256.0F - 256.0F / scale), (double)(256.0F + 256.0F / scale), 1.0, 0.0F, 0.0F);
            GLUtils.ldrawthree((double)(256.0F + 256.0F / scale), (double)(256.0F + 256.0F / scale), 1.0, 1.0F, 0.0F);
            GLUtils.ldrawthree((double)(256.0F + 256.0F / scale), (double)(256.0F - 256.0F / scale), 1.0, 1.0F, 1.0F);
            GLUtils.ldrawthree((double)(256.0F - 256.0F / scale), (double)(256.0F - 256.0F / scale), 1.0, 0.0F, 1.0F);
            class_287 bb = class_289.method_1348().method_1349();
            class_286.method_43433(bb.method_1326());
            GLShim.glBlendFuncSeparate(1, 0, 774, 0);
            synchronized(this.coordinateLock) {
                if (this.imageChanged) {
                    this.imageChanged = false;
                    this.mapImages[this.zoom].write();
                    this.lastImageX = this.lastX;
                    this.lastImageZ = this.lastZ;
                }
            }

            float multi = (float)(1.0 / this.zoomScale);
            this.percentX = (float)(GameVariableAccessShim.xCoordDouble() - (double)this.lastImageX);
            this.percentY = (float)(GameVariableAccessShim.zCoordDouble() - (double)this.lastImageZ);
            this.percentX *= multi;
            this.percentY *= multi;
            GLUtils.disp2(this.mapImages[this.zoom].getIndex());
            GLShim.glTexParameteri(3553, 10241, 9987);
            GLShim.glTexParameteri(3553, 10240, 9729);
            matrixStack.method_22903();
            matrixStack.method_22904(256.0, 256.0, 0.0);
            if (!this.options.rotates) {
                matrixStack.method_22907(class_1160.field_20707.method_23214((float)(-this.northRotate)));
            } else {
                matrixStack.method_22907(class_1160.field_20707.method_23214(this.direction));
            }

            matrixStack.method_22904(-256.0, -256.0, 0.0);
            matrixStack.method_22904((double)(-this.percentX * 512.0F / 64.0F), (double)(this.percentY * 512.0F / 64.0F), 0.0);
            RenderSystem.applyModelViewMatrix();
            GLUtils.drawPre();
            GLUtils.ldrawthree(0.0, 512.0, 1.0, 0.0F, 0.0F);
            GLUtils.ldrawthree(512.0, 512.0, 1.0, 1.0F, 0.0F);
            GLUtils.ldrawthree(512.0, 0.0, 1.0, 1.0F, 1.0F);
            GLUtils.ldrawthree(0.0, 0.0, 1.0, 0.0F, 1.0F);
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

        double guiScale = (double)this.game.method_22683().method_4489() / (double)this.scWidth;
        GLShim.glEnable(3089);
        GLShim.glScissor((int)(guiScale * (double)(x - 32)), (int)(guiScale * ((double)(this.scHeight - y) - 32.0)), (int)(guiScale * 64.0), (int)(guiScale * 63.0));
        GLUtils.drawPre();
        GLUtils.setMapWithScale(x, y, scale);
        GLUtils.drawPost();
        GLShim.glDisable(3089);
        matrixStack.method_22909();
        RenderSystem.applyModelViewMatrix();
        GLShim.glBlendFunc(770, 771);
        GLShim.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.options.squareMap) {
            this.drawSquareMapFrame(x, y);
        } else {
            this.drawRoundMapFrame(x, y);
        }

        double lastXDouble = GameVariableAccessShim.xCoordDouble();
        double lastZDouble = GameVariableAccessShim.zCoordDouble();
        TextureAtlas textureAtlas = this.master.getWaypointManager().getTextureAtlas();
        GLUtils.disp2(textureAtlas.method_4624());
        GLShim.glEnable(3042);
        GLShim.glBlendFunc(770, 771);
        GLShim.glDisable(2929);
        Waypoint highlightedPoint = this.waypointManager.getHighlightedWaypoint();
        Iterator var14 = this.waypointManager.getWaypoints().iterator();

        while(true) {
            Waypoint pt;
            double distanceSq;
            do {
                do {
                    if (!var14.hasNext()) {
                        if (highlightedPoint != null) {
                            this.drawWaypoint(matrixStack, highlightedPoint, textureAtlas, x, y, scScale, lastXDouble, lastZDouble, textureAtlas.getAtlasSprite("voxelmap:images/waypoints/target.png"), 1.0F, 0.0F, 0.0F);
                        }

                        GLShim.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                        return;
                    }

                    pt = (Waypoint)var14.next();
                } while(!pt.isActive() && pt != highlightedPoint);

                distanceSq = pt.getDistanceSqToEntity(this.game.method_1560());
            } while(!(distanceSq < (double)(this.options.maxWaypointDisplayDistance * this.options.maxWaypointDisplayDistance)) && this.options.maxWaypointDisplayDistance >= 0 && pt != highlightedPoint);

            this.drawWaypoint(matrixStack, pt, textureAtlas, x, y, scScale, lastXDouble, lastZDouble, (Sprite)null, (Float)null, (Float)null, (Float)null);
        }
    }

    private void drawWaypoint(class_4587 matrixStack, Waypoint pt, TextureAtlas textureAtlas, int x, int y, int scScale, double lastXDouble, double lastZDouble, Sprite icon, Float r, Float g, Float b) {
        boolean uprightIcon = icon != null;
        if (r == null) {
            r = pt.red;
        }

        if (g == null) {
            g = pt.green;
        }

        if (b == null) {
            b = pt.blue;
        }

        double wayX = lastXDouble - (double)pt.getX() - 0.5;
        double wayY = lastZDouble - (double)pt.getZ() - 0.5;
        float locate = (float)Math.toDegrees(Math.atan2(wayX, wayY));
        double hypot = Math.sqrt(wayX * wayX + wayY * wayY);
        boolean far = false;
        if (this.options.rotates) {
            locate += this.direction;
        } else {
            locate -= (float)this.northRotate;
        }

        hypot /= this.zoomScaleAdjusted;
        if (this.options.squareMap) {
            double radLocate = Math.toRadians((double)locate);
            double dispX = hypot * Math.cos(radLocate);
            double dispY = hypot * Math.sin(radLocate);
            far = Math.abs(dispX) > 28.5 || Math.abs(dispY) > 28.5;
            if (far) {
                hypot = hypot / Math.max(Math.abs(dispX), Math.abs(dispY)) * 30.0;
            }
        } else {
            far = hypot >= 31.0;
            if (far) {
                hypot = 34.0;
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
                GLShim.glColor4f(r, g, b, !pt.enabled && !target ? 0.3F : 1.0F);
                matrixStack.method_22904((double)x, (double)y, 0.0);
                matrixStack.method_22907(class_1160.field_20707.method_23214(-locate));
                if (uprightIcon) {
                    matrixStack.method_22904(0.0, -hypot, 0.0);
                    matrixStack.method_22907(class_1160.field_20707.method_23214(locate));
                    matrixStack.method_22904((double)(-x), (double)(-y), 0.0);
                } else {
                    matrixStack.method_22904((double)(-x), (double)(-y), 0.0);
                    matrixStack.method_22904(0.0, -hypot, 0.0);
                }

                RenderSystem.applyModelViewMatrix();
                GLShim.glTexParameteri(3553, 10241, 9729);
                GLShim.glTexParameteri(3553, 10240, 9729);
                GLUtils.drawPre();
                GLUtils.setMap(icon, (float)x, (float)y, 16.0F);
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
                GLShim.glColor4f(r, g, b, !pt.enabled && !target ? 0.3F : 1.0F);
                matrixStack.method_22907(class_1160.field_20707.method_23214(-locate));
                matrixStack.method_22904(0.0, -hypot, 0.0);
                matrixStack.method_22907(class_1160.field_20707.method_23214(-(-locate)));
                RenderSystem.applyModelViewMatrix();
                GLShim.glTexParameteri(3553, 10241, 9729);
                GLShim.glTexParameteri(3553, 10240, 9729);
                GLUtils.drawPre();
                GLUtils.setMap(icon, (float)x, (float)y, 16.0F);
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
            matrixStack.method_22904((double)x, (double)y, 0.0);
            matrixStack.method_22907(class_1160.field_20707.method_23214(this.options.rotates && !this.fullscreenMap ? 0.0F : this.direction + (float)this.northRotate));
            matrixStack.method_22904((double)(-x), (double)(-y), 0.0);
            RenderSystem.applyModelViewMatrix();
            GLUtils.drawPre();
            GLUtils.setMap((float)x, (float)y, 16);
            GLUtils.drawPost();
        } catch (Exception var8) {
            this.error = "Error: minimap arrow not found!";
        } finally {
            matrixStack.method_22909();
            RenderSystem.applyModelViewMatrix();
        }

    }

    private void renderMapFull(class_4587 matrixStack, int scWidth, int scHeight) {
        synchronized(this.coordinateLock) {
            if (this.imageChanged) {
                this.imageChanged = false;
                this.mapImages[this.zoom].write();
                this.lastImageX = this.lastX;
                this.lastImageZ = this.lastZ;
            }
        }

        RenderSystem.setShader(class_757::method_34542);
        GLUtils.disp2(this.mapImages[this.zoom].getIndex());
        GLShim.glTexParameteri(3553, 10241, 9987);
        GLShim.glTexParameteri(3553, 10240, 9729);
        matrixStack.method_22903();
        matrixStack.method_22904((double)((float)scWidth / 2.0F), (double)((float)scHeight / 2.0F), -0.0);
        matrixStack.method_22907(class_1160.field_20707.method_23214((float)this.northRotate));
        matrixStack.method_22904((double)(-((float)scWidth / 2.0F)), (double)(-((float)scHeight / 2.0F)), -0.0);
        RenderSystem.applyModelViewMatrix();
        GLShim.glDisable(2929);
        GLUtils.drawPre();
        int left = scWidth / 2 - 128;
        int top = scHeight / 2 - 128;
        GLUtils.ldrawone(left, top + 256, 160.0, 0.0F, 1.0F);
        GLUtils.ldrawone(left + 256, top + 256, 160.0, 1.0F, 1.0F);
        GLUtils.ldrawone(left + 256, top, 160.0, 1.0F, 0.0F);
        GLUtils.ldrawone(left, top, 160.0, 0.0F, 0.0F);
        GLUtils.drawPost();
        matrixStack.method_22909();
        RenderSystem.applyModelViewMatrix();
        if (this.options.biomeOverlay != 0) {
            double factor = Math.pow(2.0, (double)(3 - this.zoom));
            int minimumSize = (int)Math.pow(2.0, (double)this.zoom);
            minimumSize *= minimumSize;
            ArrayList labels = this.mapData[this.zoom].getBiomeLabels();
            GLShim.glDisable(2929);
            matrixStack.method_22903();
            matrixStack.method_22904(0.0, 0.0, 1160.0);
            RenderSystem.applyModelViewMatrix();
            Iterator var10 = labels.iterator();

            while(var10.hasNext()) {
                Object o = var10.next();
                AbstractMapData.BiomeLabel label = (AbstractMapData.BiomeLabel)o;
                if (label.segmentSize > minimumSize) {
                    String name = label.name;
                    int nameWidth = this.chkLen(name);
                    float x = (float)((double)label.x * factor);
                    float z = (float)((double)label.z * factor);
                    if (this.options.oldNorth) {
                        this.write(matrixStack, name, (float)(left + 256) - z - (float)(nameWidth / 2), (float)top + x - 3.0F, 16777215);
                    } else {
                        this.write(matrixStack, name, (float)left + x - (float)(nameWidth / 2), (float)top + z - 3.0F, 16777215);
                    }
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
            GLUtils.setMap((float)x, (float)y, 128);
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
            GLUtils.setMap((float)x, (float)y, 128);
            GLUtils.drawPost();
        } catch (Exception var4) {
            this.error = "Error: minimap overlay not found!";
        }

    }

    private void drawDirections(class_4587 matrixStack, int x, int y) {
        boolean unicode = (Boolean)this.game.field_1690.method_42437().method_41753();
        float scale = unicode ? 0.65F : 0.5F;
        float rotate;
        if (this.options.rotates) {
            rotate = -this.direction - 90.0F - (float)this.northRotate;
        } else {
            rotate = -90.0F;
        }

        float distance;
        if (this.options.squareMap) {
            if (this.options.rotates) {
                float tempdir = this.direction % 90.0F;
                tempdir = 45.0F - Math.abs(45.0F - tempdir);
                distance = (float)(33.5 / (double)scale / Math.cos(Math.toRadians((double)tempdir)));
            } else {
                distance = 33.5F / scale;
            }
        } else {
            distance = 32.0F / scale;
        }

        matrixStack.method_22903();
        matrixStack.method_22905(scale, scale, 1.0F);
        matrixStack.method_22904((double)distance * Math.sin(Math.toRadians(-((double)rotate - 90.0))), (double)distance * Math.cos(Math.toRadians(-((double)rotate - 90.0))), 100.0);
        this.write(matrixStack, "N", (float)x / scale - 2.0F, (float)y / scale - 4.0F, 16777215);
        matrixStack.method_22909();
        matrixStack.method_22903();
        matrixStack.method_22905(scale, scale, 1.0F);
        matrixStack.method_22904((double)distance * Math.sin(Math.toRadians((double)(-rotate))), (double)distance * Math.cos(Math.toRadians((double)(-rotate))), 10.0);
        this.write(matrixStack, "E", (float)x / scale - 2.0F, (float)y / scale - 4.0F, 16777215);
        matrixStack.method_22909();
        matrixStack.method_22903();
        matrixStack.method_22905(scale, scale, 1.0F);
        matrixStack.method_22904((double)distance * Math.sin(Math.toRadians(-((double)rotate + 90.0))), (double)distance * Math.cos(Math.toRadians(-((double)rotate + 90.0))), 10.0);
        this.write(matrixStack, "S", (float)x / scale - 2.0F, (float)y / scale - 4.0F, 16777215);
        matrixStack.method_22909();
        matrixStack.method_22903();
        matrixStack.method_22905(scale, scale, 1.0F);
        matrixStack.method_22904((double)distance * Math.sin(Math.toRadians(-((double)rotate + 180.0))), (double)distance * Math.cos(Math.toRadians(-((double)rotate + 180.0))), 10.0);
        this.write(matrixStack, "W", (float)x / scale - 2.0F, (float)y / scale - 4.0F, 16777215);
        matrixStack.method_22909();
    }

    private void showCoords(class_4587 matrixStack, int x, int y) {
        int textStart;
        if (y > this.scHeight - 37 - 32 - 4 - 15) {
            textStart = y - 32 - 4 - 9;
        } else {
            textStart = y + 32 + 4;
        }

        String var10000;
        if (!this.options.hide && !this.fullscreenMap) {
            boolean unicode = (Boolean)this.game.field_1690.method_42437().method_41753();
            float scale = unicode ? 0.65F : 0.5F;
            matrixStack.method_22903();
            matrixStack.method_22905(scale, scale, 1.0F);
            var10000 = this.dCoord(GameVariableAccessShim.xCoord());
            String xy = var10000 + ", " + this.dCoord(GameVariableAccessShim.zCoord());
            int m = this.chkLen(xy) / 2;
            this.write(matrixStack, xy, (float)x / scale - (float)m, (float)textStart / scale, 16777215);
            xy = Integer.toString(GameVariableAccessShim.yCoord());
            m = this.chkLen(xy) / 2;
            this.write(matrixStack, xy, (float)x / scale - (float)m, (float)textStart / scale + 10.0F, 16777215);
            if (this.ztimer > 0) {
                m = this.chkLen(this.error) / 2;
                this.write(matrixStack, this.error, (float)x / scale - (float)m, (float)textStart / scale + 19.0F, 16777215);
            }

            matrixStack.method_22909();
        } else {
            int heading = (int)(this.direction + (float)this.northRotate);
            if (heading > 360) {
                heading -= 360;
            }

            var10000 = this.dCoord(GameVariableAccessShim.xCoord());
            String stats = "(" + var10000 + ", " + GameVariableAccessShim.yCoord() + ", " + this.dCoord(GameVariableAccessShim.zCoord()) + ") " + heading + "'";
            int m = this.chkLen(stats) / 2;
            this.write(matrixStack, stats, (float)(this.scWidth / 2 - m), 5.0F, 16777215);
            if (this.ztimer > 0) {
                m = this.chkLen(this.error) / 2;
                this.write(matrixStack, this.error, (float)(this.scWidth / 2 - m), 15.0F, 16777215);
            }
        }

    }

    private String dCoord(int paramInt1) {
        if (paramInt1 < 0) {
            return "-" + Math.abs(paramInt1);
        } else {
            return paramInt1 > 0 ? "+" + paramInt1 : " " + paramInt1;
        }
    }

    private int chkLen(String string) {
        return this.fontRenderer.method_1727(string);
    }

    private void write(class_4587 matrixStack, String text, float x, float y, int color) {
        this.fontRenderer.method_1720(matrixStack, text, x, y, color);
    }

    private int chkLen(class_2561 text) {
        return this.fontRenderer.method_27525(text);
    }

    private void write(class_4587 matrixStack, class_2561 text, float x, float y, int color) {
        this.fontRenderer.method_30881(matrixStack, text, x, y, color);
    }

    private void drawWelcomeScreen(class_4587 matrixStack, int scWidth, int scHeight) {
        if (this.welcomeText[1] == null || this.welcomeText[1].getString().equals("minimap.ui.welcome2")) {
            String zmodver = "v1.11.10";
            this.welcomeText[0] = class_2561.method_43470("").method_10852(class_2561.method_43470("VoxelMap! ").method_27692(class_124.field_1061)).method_27693(zmodver + " ").method_10852(class_2561.method_43471("minimap.ui.welcome1"));
            this.welcomeText[1] = class_2561.method_43471("minimap.ui.welcome2");
            this.welcomeText[2] = class_2561.method_43471("minimap.ui.welcome3");
            this.welcomeText[3] = class_2561.method_43471("minimap.ui.welcome4");
            this.welcomeText[4] = class_2561.method_43470("").method_10852(class_2561.method_43472(this.options.keyBindZoom.method_1431()).method_27692(class_124.field_1075)).method_27693(": ").method_10852(class_2561.method_43471("minimap.ui.welcome5a")).method_27693(", ").method_10852(class_2561.method_43472(this.options.keyBindMenu.method_1431()).method_27692(class_124.field_1075)).method_27693(": ").method_10852(class_2561.method_43471("minimap.ui.welcome5b"));
            this.welcomeText[5] = class_2561.method_43470("").method_10852(class_2561.method_43472(this.options.keyBindFullscreen.method_1431()).method_27692(class_124.field_1075)).method_27693(": ").method_10852(class_2561.method_43471("minimap.ui.welcome6"));
            this.welcomeText[6] = class_2561.method_43470("").method_10852(class_2561.method_43472(this.options.keyBindWaypoint.method_1431()).method_27692(class_124.field_1075)).method_27693(": ").method_10852(class_2561.method_43471("minimap.ui.welcome7"));
            this.welcomeText[7] = this.options.keyBindZoom.method_16007().method_27661().method_27693(": ").method_10852(class_2561.method_43471("minimap.ui.welcome8").method_27692(class_124.field_1080));
        }

        GLShim.glBlendFunc(770, 771);
        int maxSize = 0;
        int border = 2;
        class_2561 head = this.welcomeText[0];

        int height;
        for(height = 1; height < this.welcomeText.length - 1; ++height) {
            if (this.chkLen(this.welcomeText[height]) > maxSize) {
                maxSize = this.chkLen(this.welcomeText[height]);
            }
        }

        int title = this.chkLen(head);
        int centerX = (int)((double)(scWidth + 5) / 2.0);
        int centerY = (int)((double)(scHeight + 5) / 2.0);
        class_2561 hide = this.welcomeText[this.welcomeText.length - 1];
        int footer = this.chkLen(hide);
        GLShim.glDisable(3553);
        GLShim.glColor4f(0.0F, 0.0F, 0.0F, 0.7F);
        double leftX = (double)centerX - (double)title / 2.0 - (double)border;
        double rightX = (double)centerX + (double)title / 2.0 + (double)border;
        double topY = (double)centerY - (double)(height - 1) / 2.0 * 10.0 - (double)border - 20.0;
        double botY = (double)centerY - (double)(height - 1) / 2.0 * 10.0 + (double)border - 10.0;
        this.drawBox(leftX, rightX, topY, botY);
        leftX = (double)centerX - (double)maxSize / 2.0 - (double)border;
        rightX = (double)centerX + (double)maxSize / 2.0 + (double)border;
        topY = (double)centerY - (double)(height - 1) / 2.0 * 10.0 - (double)border;
        botY = (double)centerY + (double)(height - 1) / 2.0 * 10.0 + (double)border;
        this.drawBox(leftX, rightX, topY, botY);
        leftX = (double)centerX - (double)footer / 2.0 - (double)border;
        rightX = (double)centerX + (double)footer / 2.0 + (double)border;
        topY = (double)centerY + (double)(height - 1) / 2.0 * 10.0 - (double)border + 10.0;
        botY = (double)centerY + (double)(height - 1) / 2.0 * 10.0 + (double)border + 20.0;
        this.drawBox(leftX, rightX, topY, botY);
        GLShim.glEnable(3553);
        this.write(matrixStack, head, (float)(centerX - title / 2), (float)(centerY - (height - 1) * 10 / 2 - 19), 16777215);

        for(int n = 1; n < height; ++n) {
            this.write(matrixStack, this.welcomeText[n], (float)(centerX - maxSize / 2), (float)(centerY - (height - 1) * 10 / 2 + n * 10 - 9), 16777215);
        }

        this.write(matrixStack, hide, (float)(centerX - footer / 2), (float)((scHeight + 5) / 2 + (height - 1) * 10 / 2 + 11), 16777215);
    }

    private void drawBox(double leftX, double rightX, double topY, double botY) {
        GLUtils.drawPre(class_290.field_1592);
        GLUtils.ldrawtwo(leftX, botY, 0.0);
        GLUtils.ldrawtwo(rightX, botY, 0.0);
        GLUtils.ldrawtwo(rightX, topY, 0.0);
        GLUtils.ldrawtwo(leftX, topY, 0.0);
        GLUtils.drawPost();
    }
}
