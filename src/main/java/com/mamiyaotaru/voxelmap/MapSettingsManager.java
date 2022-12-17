package com.mamiyaotaru.voxelmap;

import com.mamiyaotaru.voxelmap.gui.overridden.EnumOptionsMinimap;
import com.mamiyaotaru.voxelmap.interfaces.ISettingsManager;
import com.mamiyaotaru.voxelmap.interfaces.ISubSettingsManager;
import com.mamiyaotaru.voxelmap.util.I18nUtils;
import com.mamiyaotaru.voxelmap.util.MessageUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_2561;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3675;

public class MapSettingsManager implements ISettingsManager {
    private File settingsFile;
    public boolean showUnderMenus;
    private final int availableProcessors = Runtime.getRuntime().availableProcessors();
    public boolean multicore;
    public boolean hide;
    public boolean coords;
    protected boolean showCaves;
    public boolean lightmap;
    public boolean heightmap;
    public boolean slopemap;
    public boolean filtering;
    public boolean waterTransparency;
    public boolean blockTransparency;
    public boolean biomes;
    public int biomeOverlay;
    public boolean chunkGrid;
    public boolean slimeChunks;
    public boolean squareMap;
    public boolean rotates;
    public boolean oldNorth;
    public boolean showBeacons;
    public boolean showWaypoints;
    private boolean preToggleBeacons;
    private boolean preToggleSigns;
    public int deathpoints;
    public int maxWaypointDisplayDistance;
    protected boolean welcome;
    public int zoom;
    public int sizeModifier;
    public int mapCorner;
    public Boolean cavesAllowed;
    public int sort;
    protected boolean realTimeTorches;
    public class_304 keyBindZoom;
    public class_304 keyBindFullscreen;
    public class_304 keyBindMenu;
    public class_304 keyBindWaypointMenu;
    public class_304 keyBindWaypoint;
    public class_304 keyBindMobToggle;
    public class_304 keyBindWaypointToggle;
    public class_304[] keyBindings;
    public class_310 game;
    private boolean somethingChanged;
    public static MapSettingsManager instance;
    private final List<ISubSettingsManager> subSettingsManagers;

    public MapSettingsManager() {
        this.multicore = this.availableProcessors > 1;
        this.hide = false;
        this.coords = true;
        this.showCaves = true;
        this.lightmap = true;
        this.heightmap = this.multicore;
        this.slopemap = true;
        this.filtering = false;
        this.waterTransparency = this.multicore;
        this.blockTransparency = this.multicore;
        this.biomes = this.multicore;
        this.biomeOverlay = 0;
        this.chunkGrid = false;
        this.slimeChunks = false;
        this.squareMap = true;
        this.rotates = true;
        this.oldNorth = false;
        this.showBeacons = false;
        this.showWaypoints = true;
        this.preToggleBeacons = false;
        this.preToggleSigns = true;
        this.deathpoints = 1;
        this.maxWaypointDisplayDistance = 1000;
        this.welcome = true;
        this.zoom = 2;
        this.sizeModifier = 1;
        this.mapCorner = 1;
        this.cavesAllowed = true;
        this.sort = 1;
        this.realTimeTorches = false;
        this.keyBindZoom = new class_304("key.minimap.zoom", class_3675.method_15981("key.keyboard.z").method_1444(), "controls.minimap.title");
        this.keyBindFullscreen = new class_304("key.minimap.togglefullscreen", class_3675.method_15981("key.keyboard.x").method_1444(), "controls.minimap.title");
        this.keyBindMenu = new class_304("key.minimap.voxelmapmenu", class_3675.method_15981("key.keyboard.m").method_1444(), "controls.minimap.title");
        this.keyBindWaypointMenu = new class_304("key.minimap.waypointmenu", -1, "controls.minimap.title");
        this.keyBindWaypoint = new class_304("key.minimap.waypointhotkey", class_3675.method_15981("key.keyboard.n").method_1444(), "controls.minimap.title");
        this.keyBindMobToggle = new class_304("key.minimap.togglemobs", -1, "controls.minimap.title");
        this.keyBindWaypointToggle = new class_304("key.minimap.toggleingamewaypoints", -1, "controls.minimap.title");
        this.subSettingsManagers = new ArrayList();
        instance = this;
        this.game = class_310.method_1551();
        this.keyBindings = new class_304[]{this.keyBindMenu, this.keyBindWaypointMenu, this.keyBindZoom, this.keyBindFullscreen, this.keyBindWaypoint, this.keyBindMobToggle, this.keyBindWaypointToggle};
    }

    public void addSecondaryOptionsManager(ISubSettingsManager secondarySettingsManager) {
        this.subSettingsManagers.add(secondarySettingsManager);
    }

    public void loadAll() {
        this.settingsFile = new File(this.game.field_1697, "config/voxelmap.properties");

        try {
            if (this.settingsFile.exists()) {
                BufferedReader in;
                String sCurrentLine;
                for(in = new BufferedReader(new InputStreamReader(new FileInputStream(this.settingsFile), StandardCharsets.UTF_8.newDecoder())); (sCurrentLine = in.readLine()) != null; class_304.method_1426()) {
                    String[] curLine = sCurrentLine.split(":");
                    switch (curLine[0]) {
                        case "Zoom Level":
                            this.zoom = Math.max(0, Math.min(4, Integer.parseInt(curLine[1])));
                            break;
                        case "Hide Minimap":
                            this.hide = Boolean.parseBoolean(curLine[1]);
                            break;
                        case "Show Coordinates":
                            this.coords = Boolean.parseBoolean(curLine[1]);
                            break;
                        case "Enable Cave Mode":
                            this.showCaves = Boolean.parseBoolean(curLine[1]);
                            break;
                        case "Dynamic Lighting":
                            this.lightmap = Boolean.parseBoolean(curLine[1]);
                            break;
                        case "Height Map":
                            this.heightmap = Boolean.parseBoolean(curLine[1]);
                            break;
                        case "Slope Map":
                            this.slopemap = Boolean.parseBoolean(curLine[1]);
                            break;
                        case "Blur":
                            this.filtering = Boolean.parseBoolean(curLine[1]);
                            break;
                        case "Water Transparency":
                            this.waterTransparency = Boolean.parseBoolean(curLine[1]);
                            break;
                        case "Block Transparency":
                            this.blockTransparency = Boolean.parseBoolean(curLine[1]);
                            break;
                        case "Biomes":
                            this.biomes = Boolean.parseBoolean(curLine[1]);
                            break;
                        case "Biome Overlay":
                            this.biomeOverlay = Math.max(0, Math.min(2, Integer.parseInt(curLine[1])));
                            break;
                        case "Chunk Grid":
                            this.chunkGrid = Boolean.parseBoolean(curLine[1]);
                            break;
                        case "Slime Chunks":
                            this.slimeChunks = Boolean.parseBoolean(curLine[1]);
                            break;
                        case "Square Map":
                            this.squareMap = Boolean.parseBoolean(curLine[1]);
                            break;
                        case "Rotation":
                            this.rotates = Boolean.parseBoolean(curLine[1]);
                            break;
                        case "Old North":
                            this.oldNorth = Boolean.parseBoolean(curLine[1]);
                            break;
                        case "Waypoint Beacons":
                            this.showBeacons = Boolean.parseBoolean(curLine[1]);
                            break;
                        case "Waypoint Signs":
                            this.showWaypoints = Boolean.parseBoolean(curLine[1]);
                            break;
                        case "Deathpoints":
                            this.deathpoints = Math.max(0, Math.min(2, Integer.parseInt(curLine[1])));
                            break;
                        case "Waypoint Max Distance":
                            this.maxWaypointDisplayDistance = Math.max(-1, Math.min(10000, Integer.parseInt(curLine[1])));
                            break;
                        case "Waypoint Sort By":
                            this.sort = Math.max(1, Math.min(4, Integer.parseInt(curLine[1])));
                            break;
                        case "Welcome Message":
                            this.welcome = Boolean.parseBoolean(curLine[1]);
                            break;
                        case "Real Time Torch Flicker":
                            this.realTimeTorches = Boolean.parseBoolean(curLine[1]);
                            break;
                        case "Map Corner":
                            this.mapCorner = Math.max(0, Math.min(3, Integer.parseInt(curLine[1])));
                            break;
                        case "Map Size":
                            this.sizeModifier = Math.max(-1, Math.min(4, Integer.parseInt(curLine[1])));
                            break;
                        case "Zoom Key":
                            this.bindKey(this.keyBindZoom, curLine[1]);
                            break;
                        case "Fullscreen Key":
                            this.bindKey(this.keyBindFullscreen, curLine[1]);
                            break;
                        case "Menu Key":
                            this.bindKey(this.keyBindMenu, curLine[1]);
                            break;
                        case "Waypoint Menu Key":
                            this.bindKey(this.keyBindWaypointMenu, curLine[1]);
                            break;
                        case "Waypoint Key":
                            this.bindKey(this.keyBindWaypoint, curLine[1]);
                            break;
                        case "Mob Key":
                            this.bindKey(this.keyBindMobToggle, curLine[1]);
                            break;
                        case "In-game Waypoint Key":
                            this.bindKey(this.keyBindWaypointToggle, curLine[1]);
                    }
                }

                Iterator var7 = this.subSettingsManagers.iterator();

                while(var7.hasNext()) {
                    ISubSettingsManager subSettingsManager = (ISubSettingsManager)var7.next();
                    subSettingsManager.loadSettings(this.settingsFile);
                }

                in.close();
            }

            this.saveAll();
        } catch (Exception var6) {
        }

    }

    private void bindKey(class_304 keyBinding, String id) {
        try {
            keyBinding.method_1422(class_3675.method_15981(id));
        } catch (Exception var4) {
            System.err.println(id + " is not a valid keybinding");
        }

    }

    public void saveAll() {
        File settingsFileDir = new File(this.game.field_1697, "/config/");
        if (!settingsFileDir.exists()) {
            settingsFileDir.mkdirs();
        }

        this.settingsFile = new File(settingsFileDir, "voxelmap.properties");

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.settingsFile), Charset.forName("UTF-8").newEncoder())));
            out.println("Zoom Level:" + Integer.toString(this.zoom));
            out.println("Hide Minimap:" + Boolean.toString(this.hide));
            out.println("Show Coordinates:" + Boolean.toString(this.coords));
            out.println("Enable Cave Mode:" + Boolean.toString(this.showCaves));
            out.println("Dynamic Lighting:" + Boolean.toString(this.lightmap));
            out.println("Height Map:" + Boolean.toString(this.heightmap));
            out.println("Slope Map:" + Boolean.toString(this.slopemap));
            out.println("Blur:" + Boolean.toString(this.filtering));
            out.println("Water Transparency:" + Boolean.toString(this.waterTransparency));
            out.println("Block Transparency:" + Boolean.toString(this.blockTransparency));
            out.println("Biomes:" + Boolean.toString(this.biomes));
            out.println("Biome Overlay:" + Integer.toString(this.biomeOverlay));
            out.println("Chunk Grid:" + Boolean.toString(this.chunkGrid));
            out.println("Slime Chunks:" + Boolean.toString(this.slimeChunks));
            out.println("Square Map:" + Boolean.toString(this.squareMap));
            out.println("Rotation:" + Boolean.toString(this.rotates));
            out.println("Old North:" + Boolean.toString(this.oldNorth));
            out.println("Waypoint Beacons:" + Boolean.toString(this.showBeacons));
            out.println("Waypoint Signs:" + Boolean.toString(this.showWaypoints));
            out.println("Deathpoints:" + Integer.toString(this.deathpoints));
            out.println("Waypoint Max Distance:" + Integer.toString(this.maxWaypointDisplayDistance));
            out.println("Waypoint Sort By:" + Integer.toString(this.sort));
            out.println("Welcome Message:" + Boolean.toString(this.welcome));
            out.println("Map Corner:" + Integer.toString(this.mapCorner));
            out.println("Map Size:" + Integer.toString(this.sizeModifier));
            out.println("Zoom Key:" + this.keyBindZoom.method_1428());
            out.println("Fullscreen Key:" + this.keyBindFullscreen.method_1428());
            out.println("Menu Key:" + this.keyBindMenu.method_1428());
            out.println("Waypoint Menu Key:" + this.keyBindWaypointMenu.method_1428());
            out.println("Waypoint Key:" + this.keyBindWaypoint.method_1428());
            out.println("Mob Key:" + this.keyBindMobToggle.method_1428());
            out.println("In-game Waypoint Key:" + this.keyBindWaypointToggle.method_1428());
            Iterator var3 = this.subSettingsManagers.iterator();

            while(var3.hasNext()) {
                ISubSettingsManager subSettingsManager = (ISubSettingsManager)var3.next();
                subSettingsManager.saveAll(out);
            }

            out.close();
        } catch (Exception var5) {
            MessageUtils.chatInfo("§EError Saving Settings " + var5.getLocalizedMessage());
        }

    }

    public String getKeyText(EnumOptionsMinimap par1EnumOptions) {
        String var10000 = par1EnumOptions.getName();
        String s = I18nUtils.getString(var10000, new Object[0]) + ": ";
        if (par1EnumOptions.isFloat()) {
            float f = this.getOptionFloatValue(par1EnumOptions);
            if (par1EnumOptions == EnumOptionsMinimap.ZOOM) {
                return s + (int)f;
            } else if (par1EnumOptions == EnumOptionsMinimap.WAYPOINTDISTANCE) {
                return f < 0.0F ? s + I18nUtils.getString("options.minimap.waypoints.infinite", new Object[0]) : s + (int)f;
            } else {
                return f == 0.0F ? s + I18nUtils.getString("options.off", new Object[0]) : s + (int)f + "%";
            }
        } else if (par1EnumOptions.isBoolean()) {
            boolean flag = this.getOptionBooleanValue(par1EnumOptions);
            return flag ? s + I18nUtils.getString("options.on", new Object[0]) : s + I18nUtils.getString("options.off", new Object[0]);
        } else if (par1EnumOptions.isList()) {
            String state = this.getOptionListValue(par1EnumOptions);
            return s + state;
        } else {
            return s;
        }
    }

    public float getOptionFloatValue(EnumOptionsMinimap par1EnumOptions) {
        if (par1EnumOptions == EnumOptionsMinimap.ZOOM) {
            return (float)this.zoom;
        } else {
            return par1EnumOptions == EnumOptionsMinimap.WAYPOINTDISTANCE ? (float)this.maxWaypointDisplayDistance : 0.0F;
        }
    }

    public boolean getOptionBooleanValue(EnumOptionsMinimap par1EnumOptions) {
        boolean var10000;
        switch (par1EnumOptions) {
            case COORDS:
                var10000 = this.coords;
                break;
            case HIDE:
                var10000 = this.hide;
                break;
            case CAVEMODE:
                var10000 = this.cavesAllowed && this.showCaves;
                break;
            case LIGHTING:
                var10000 = this.lightmap;
                break;
            case SQUARE:
                var10000 = this.squareMap;
                break;
            case ROTATES:
                var10000 = this.rotates;
                break;
            case OLDNORTH:
                var10000 = this.oldNorth;
                break;
            case WELCOME:
                var10000 = this.welcome;
                break;
            case FILTERING:
                var10000 = this.filtering;
                break;
            case WATERTRANSPARENCY:
                var10000 = this.waterTransparency;
                break;
            case BLOCKTRANSPARENCY:
                var10000 = this.blockTransparency;
                break;
            case BIOMES:
                var10000 = this.biomes;
                break;
            case CHUNKGRID:
                var10000 = this.chunkGrid;
                break;
            case SLIMECHUNKS:
                var10000 = this.slimeChunks;
                break;
            default:
                throw new IllegalArgumentException("Add code to handle EnumOptionMinimap: " + par1EnumOptions.getName() + ". (possibly not a boolean applicable to minimap)");
        }

        return var10000;
    }

    public String getOptionListValue(EnumOptionsMinimap par1EnumOptions) {
        switch (par1EnumOptions) {
            case TERRAIN:
                if (this.slopemap && this.heightmap) {
                    return I18nUtils.getString("options.minimap.terrain.both", new Object[0]);
                } else if (this.heightmap) {
                    return I18nUtils.getString("options.minimap.terrain.height", new Object[0]);
                } else {
                    if (this.slopemap) {
                        return I18nUtils.getString("options.minimap.terrain.slope", new Object[0]);
                    }

                    return I18nUtils.getString("options.off", new Object[0]);
                }
            case BEACONS:
                if (this.showBeacons && this.showWaypoints) {
                    return I18nUtils.getString("options.minimap.ingamewaypoints.both", new Object[0]);
                } else if (this.showBeacons) {
                    return I18nUtils.getString("options.minimap.ingamewaypoints.beacons", new Object[0]);
                } else {
                    if (this.showWaypoints) {
                        return I18nUtils.getString("options.minimap.ingamewaypoints.signs", new Object[0]);
                    }

                    return I18nUtils.getString("options.off", new Object[0]);
                }
            case LOCATION:
                if (this.mapCorner == 0) {
                    return I18nUtils.getString("options.minimap.location.topleft", new Object[0]);
                } else if (this.mapCorner == 1) {
                    return I18nUtils.getString("options.minimap.location.topright", new Object[0]);
                } else if (this.mapCorner == 2) {
                    return I18nUtils.getString("options.minimap.location.bottomright", new Object[0]);
                } else {
                    if (this.mapCorner == 3) {
                        return I18nUtils.getString("options.minimap.location.bottomleft", new Object[0]);
                    }

                    return "Error";
                }
            case SIZE:
                if (this.sizeModifier == -1) {
                    return I18nUtils.getString("options.minimap.size.small", new Object[0]);
                } else if (this.sizeModifier == 0) {
                    return I18nUtils.getString("options.minimap.size.medium", new Object[0]);
                } else if (this.sizeModifier == 1) {
                    return I18nUtils.getString("options.minimap.size.large", new Object[0]);
                } else if (this.sizeModifier == 2) {
                    return I18nUtils.getString("options.minimap.size.xl", new Object[0]);
                } else if (this.sizeModifier == 3) {
                    return I18nUtils.getString("options.minimap.size.xxl", new Object[0]);
                } else {
                    if (this.sizeModifier == 4) {
                        return I18nUtils.getString("options.minimap.size.xxxl", new Object[0]);
                    }

                    return "error";
                }
            case BIOMEOVERLAY:
                if (this.biomeOverlay == 0) {
                    return I18nUtils.getString("options.off", new Object[0]);
                } else if (this.biomeOverlay == 1) {
                    return I18nUtils.getString("options.minimap.biomeoverlay.solid", new Object[0]);
                } else {
                    if (this.biomeOverlay == 2) {
                        return I18nUtils.getString("options.minimap.biomeoverlay.transparent", new Object[0]);
                    }

                    return "error";
                }
            case DEATHPOINTS:
                if (this.deathpoints == 0) {
                    return I18nUtils.getString("options.off", new Object[0]);
                } else if (this.deathpoints == 1) {
                    return I18nUtils.getString("options.minimap.waypoints.deathpoints.mostrecent", new Object[0]);
                } else {
                    if (this.deathpoints == 2) {
                        return I18nUtils.getString("options.minimap.waypoints.deathpoints.all", new Object[0]);
                    }

                    return "error";
                }
            default:
                throw new IllegalArgumentException("Add code to handle EnumOptionMinimap: " + par1EnumOptions.getName() + ". (possibly not a list value applicable to minimap)");
        }
    }

    public void setOptionFloatValue(EnumOptionsMinimap par1EnumOptions, float par2) {
        if (par1EnumOptions == EnumOptionsMinimap.WAYPOINTDISTANCE) {
            float distance = par2 * 9951.0F + 50.0F;
            if (distance > 10000.0F) {
                distance = -1.0F;
            }

            this.maxWaypointDisplayDistance = (int)distance;
        }

        this.somethingChanged = true;
    }

    public void setOptionValue(EnumOptionsMinimap par1EnumOptions) {
        switch (par1EnumOptions) {
            case COORDS:
                this.coords = !this.coords;
                break;
            case HIDE:
                this.hide = !this.hide;
                break;
            case CAVEMODE:
                this.showCaves = !this.showCaves;
                break;
            case LIGHTING:
                this.lightmap = !this.lightmap;
                break;
            case SQUARE:
                this.squareMap = !this.squareMap;
                break;
            case ROTATES:
                this.rotates = !this.rotates;
                break;
            case OLDNORTH:
                this.oldNorth = !this.oldNorth;
                break;
            case WELCOME:
                this.welcome = !this.welcome;
                break;
            case FILTERING:
                this.filtering = !this.filtering;
                break;
            case WATERTRANSPARENCY:
                this.waterTransparency = !this.waterTransparency;
                break;
            case BLOCKTRANSPARENCY:
                this.blockTransparency = !this.blockTransparency;
                break;
            case BIOMES:
                this.biomes = !this.biomes;
                break;
            case CHUNKGRID:
                this.chunkGrid = !this.chunkGrid;
                break;
            case SLIMECHUNKS:
                this.slimeChunks = !this.slimeChunks;
                break;
            case TERRAIN:
                if (this.slopemap && this.heightmap) {
                    this.slopemap = false;
                    this.heightmap = false;
                } else if (this.slopemap) {
                    this.slopemap = false;
                    this.heightmap = true;
                } else if (this.heightmap) {
                    this.slopemap = true;
                    this.heightmap = true;
                } else {
                    this.slopemap = true;
                    this.heightmap = false;
                }
                break;
            case BEACONS:
                if (this.showBeacons && this.showWaypoints) {
                    this.showBeacons = false;
                    this.showWaypoints = false;
                } else if (this.showBeacons) {
                    this.showBeacons = false;
                    this.showWaypoints = true;
                } else if (this.showWaypoints) {
                    this.showWaypoints = true;
                    this.showBeacons = true;
                } else {
                    this.showBeacons = true;
                    this.showWaypoints = false;
                }
                break;
            case LOCATION:
                this.mapCorner = this.mapCorner >= 3 ? 0 : this.mapCorner + 1;
                break;
            case SIZE:
                this.sizeModifier = this.sizeModifier >= 4 ? -1 : this.sizeModifier + 1;
                break;
            case BIOMEOVERLAY:
                ++this.biomeOverlay;
                if (this.biomeOverlay > 2) {
                    this.biomeOverlay = 0;
                }
                break;
            case DEATHPOINTS:
                ++this.deathpoints;
                if (this.deathpoints > 2) {
                    this.deathpoints = 0;
                }
                break;
            default:
                throw new IllegalArgumentException("Add code to handle EnumOptionMinimap: " + par1EnumOptions.getName());
        }

        this.somethingChanged = true;
    }

    public void toggleIngameWaypoints() {
        if (!this.showBeacons && !this.showWaypoints) {
            this.showBeacons = this.preToggleBeacons;
            this.showWaypoints = this.preToggleSigns;
        } else {
            this.preToggleBeacons = this.showBeacons;
            this.preToggleSigns = this.showWaypoints;
            this.showBeacons = false;
            this.showWaypoints = false;
        }

    }

    public String getKeyBindingDescription(int keybindIndex) {
        return this.keyBindings[keybindIndex].method_1431().equals("key.minimap.voxelmapmenu") ? I18nUtils.getString("key.minimap.menu", new Object[0]) : I18nUtils.getString(this.keyBindings[keybindIndex].method_1431(), new Object[0]);
    }

    public class_2561 getKeybindDisplayString(int keybindIndex) {
        class_304 keyBinding = this.keyBindings[keybindIndex];
        return this.getKeybindDisplayString(keyBinding);
    }

    public class_2561 getKeybindDisplayString(class_304 keyBinding) {
        return keyBinding.method_16007();
    }

    public void setKeyBinding(class_304 keyBinding, class_3675.class_306 input) {
        keyBinding.method_1422(input);
        this.saveAll();
    }

    public void setSort(int sort) {
        if (sort != this.sort && sort != -this.sort) {
            this.sort = sort;
        } else {
            this.sort = -this.sort;
        }

    }

    public boolean isChanged() {
        if (this.somethingChanged) {
            this.somethingChanged = false;
            return true;
        } else {
            return false;
        }
    }
}
