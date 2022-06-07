 package cn.magicst.mamiyaotaru.voxelmap.persistent;
 
 import cn.magicst.mamiyaotaru.voxelmap.MapSettingsManager;
 import cn.magicst.mamiyaotaru.voxelmap.gui.GuiAddWaypoint;
 import cn.magicst.mamiyaotaru.voxelmap.gui.GuiMinimapOptions;
 import cn.magicst.mamiyaotaru.voxelmap.gui.GuiSubworldsSelect;
 import cn.magicst.mamiyaotaru.voxelmap.gui.GuiWaypoints;
 import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.IPopupGuiScreen;
 import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.Popup;
 import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.PopupGuiButton;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.AbstractMapData;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.AbstractVoxelMap;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IVoxelMap;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IWaypointManager;
 import cn.magicst.mamiyaotaru.voxelmap.textures.Sprite;
 import cn.magicst.mamiyaotaru.voxelmap.textures.TextureAtlas;
 import cn.magicst.mamiyaotaru.voxelmap.util.BackgroundImageInfo;
 import cn.magicst.mamiyaotaru.voxelmap.util.BiomeMapData;
 import cn.magicst.mamiyaotaru.voxelmap.util.CommandUtils;
 import cn.magicst.mamiyaotaru.voxelmap.util.DimensionContainer;
 import cn.magicst.mamiyaotaru.voxelmap.util.GLShim;
 import cn.magicst.mamiyaotaru.voxelmap.util.GLUtils;
 import cn.magicst.mamiyaotaru.voxelmap.util.GameVariableAccessShim;
 import cn.magicst.mamiyaotaru.voxelmap.util.I18nUtils;
 import cn.magicst.mamiyaotaru.voxelmap.util.ImageUtils;
 import cn.magicst.mamiyaotaru.voxelmap.util.Waypoint;
 import com.mojang.blaze3d.systems.RenderSystem;
 import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
 import java.awt.image.BufferedImage;
 import java.util.ArrayList;
 import java.util.Random;
 import java.util.TreeSet;
 import net.minecraft.class_1046;
 import net.minecraft.class_1068;
 import net.minecraft.class_1160;
 import net.minecraft.class_124;
 import net.minecraft.class_1664;
 import net.minecraft.class_1937;
 import net.minecraft.class_2561;
 import net.minecraft.class_287;
 import net.minecraft.class_289;
 import net.minecraft.class_290;
 import net.minecraft.class_293;
 import net.minecraft.class_2960;
 import net.minecraft.class_304;
 import net.minecraft.class_310;
 import net.minecraft.class_342;
 import net.minecraft.class_364;
 import net.minecraft.class_3675;
 import net.minecraft.class_410;
 import net.minecraft.class_4185;
 import net.minecraft.class_437;
 import net.minecraft.class_4587;
 import net.minecraft.class_5250;
 import net.minecraft.class_642;
 import net.minecraft.class_742;
 import net.minecraft.class_757;
 import org.lwjgl.glfw.GLFW;
 
 public class GuiPersistentMap extends PopupGuiScreen implements IGuiWaypoints {
   private final Random generator = new Random(); private final class_310 mc;
   private final IVoxelMap master;
   private final IPersistentMap persistentMap;
   private final IWaypointManager waypointManager;
   private final class_437 parent;
   private final MapSettingsManager mapOptions;
   private final PersistentMapSettingsManager options;
   protected String screenTitle = "World Map";
   protected String worldNameDisplay = "";
   protected int worldNameDisplayLength = 0;
   protected int maxWorldNameDisplayLength = 0;
   private String subworldName = "";
   private PopupGuiButton buttonMultiworld;
   private int top;
   private int bottom;
   private boolean oldNorth = false;
   private boolean lastStill = false;
   private boolean editingCoordinates = false;
   private boolean lastEditingCoordinates = false;
   private class_342 coordinates;
   int centerX = 0;
   int centerY = 0;
   float mapCenterX = 0.0F;
   float mapCenterZ = 0.0F;
   float deltaX = 0.0F;
   float deltaY = 0.0F;
   float deltaXonRelease = 0.0F;
   float deltaYonRelease = 0.0F;
   long timeOfRelease = 0L;
   boolean mouseCursorShown = true;
   long timeAtLastTick = 0L;
   long timeOfLastKBInput = 0L;
   long timeOfLastMouseInput = 0L;
   final float TIME_CONSTANT = 350.0F;
   float lastMouseX = 0.0F;
   float lastMouseY = 0.0F;
   protected int mouseX;
   protected int mouseY;
   boolean leftMouseButtonDown = false;
   float zoom = 4.0F;
   float zoomStart = 4.0F;
   float zoomGoal = 4.0F;
   long timeOfZoom = 0L;
   float zoomDirectX = 0.0F;
   float zoomDirectY = 0.0F;
   private float scScale = 1.0F;
   private float guiToMap = 2.0F;
   private float mapToGui = 0.5F;
   private float mouseDirectToMap = 1.0F;
   private float guiToDirectMouse = 2.0F;
   private static int playerGLID = 0;
   private static boolean gotSkin = false;
   private static int skinTries = 0;
   private boolean closed = false;
   private CachedRegion[] regions = new CachedRegion[0];
   BackgroundImageInfo backGroundImageInfo = null;
   private final BiomeMapData biomeMapData = new BiomeMapData(760, 360);
   private float mapPixelsX = 0.0F;
   private float mapPixelsY = 0.0F;
   private final Object closedLock = new Object();
   private final class_304 keyBindForward = new class_304("key.forward.fake", 17, "key.categories.movement");
   private final class_304 keyBindLeft = new class_304("key.left.fake", 30, "key.categories.movement");
   private final class_304 keyBindBack = new class_304("key.back.fake", 31, "key.categories.movement");
   private final class_304 keyBindRight = new class_304("key.right.fake", 32, "key.categories.movement");
   private final class_304 keyBindSprint = new class_304("key.sprint.fake", 29, "key.categories.movement");
   private final class_3675.class_306 forwardCode;
   private final class_3675.class_306 leftCode;
   private final class_3675.class_306 backCode;
   private final class_3675.class_306 rightCode;
   private final class_3675.class_306 sprintCode;
   class_3675.class_306 nullInput = class_3675.method_15981("key.keyboard.unknown");
   private class_2561 multiworldButtonName = null;
   private class_5250 multiworldButtonNameRed = null;
   int sideMargin = 10;
   int buttonCount = 5;
   int buttonSeparation = 4;
   int buttonWidth = 66;
   public boolean editClicked = false;
   public boolean deleteClicked = false;
   public boolean addClicked = false;
   Waypoint newWaypoint;
   Waypoint selectedWaypoint;
   
   public GuiPersistentMap(class_437 parent, IVoxelMap master) {
     this.mc = class_310.method_1551();
     this.parent = parent;
     this.master = master;
     this.waypointManager = master.getWaypointManager();
     this.mapOptions = master.getMapOptions();
     this.persistentMap = master.getPersistentMap();
     this.options = master.getPersistentMapOptions();
     this.zoom = this.options.zoom;
     this.zoomStart = this.options.zoom;
     this.zoomGoal = this.options.zoom;
     this.persistentMap.setLightMapArray(master.getMap().getLightmapArray());
     if (!gotSkin && skinTries < 5) {
       getSkin();
     }
     
     this.forwardCode = class_3675.method_15981(this.mc.field_1690.field_1894.method_1428());
     this.leftCode = class_3675.method_15981(this.mc.field_1690.field_1913.method_1428());
     this.backCode = class_3675.method_15981(this.mc.field_1690.field_1881.method_1428());
     this.rightCode = class_3675.method_15981(this.mc.field_1690.field_1849.method_1428());
     this.sprintCode = class_3675.method_15981(this.mc.field_1690.field_1867.method_1428());
   }
   
   private void getSkin() {
     class_2960 skinLocation = this.mc.field_1724.method_3117();
     class_1046 imageData = null;
     
     try {
       if (skinLocation != class_1068.method_4648(this.mc.field_1724.method_5667())) {
         class_742.method_3120(skinLocation, this.mc.field_1724.method_5477().getString());
         imageData = (class_1046)class_310.method_1551().method_1531().method_4619(skinLocation);
       } 
     } catch (Exception exception) {}
 
     
     if (imageData != null) {
       gotSkin = true;
       GLUtils.disp(imageData.method_4624());
     } else {
       skinTries++;
       GLUtils.img(skinLocation);
     } 
     
     BufferedImage skinImage = ImageUtils.createBufferedImageFromCurrentGLImage();
     boolean showHat = this.mc.field_1724.method_7348(class_1664.field_7563);
     if (showHat) {
       skinImage = ImageUtils.addImages(ImageUtils.loadImage(skinImage, 8, 8, 8, 8), ImageUtils.loadImage(skinImage, 40, 8, 8, 8), 0.0F, 0.0F, 8, 8);
     } else {
       skinImage = ImageUtils.loadImage(skinImage, 8, 8, 8, 8);
     } 
     
     float scale = skinImage.getWidth() / 8.0F;
     skinImage = ImageUtils.fillOutline(ImageUtils.pad(ImageUtils.scaleImage(skinImage, 2.0F / scale)), true, 1);
     if (playerGLID != 0) {
       GLUtils.glah(playerGLID);
     }
     
     playerGLID = GLUtils.tex(skinImage);
   }
   
   public void method_25426() {
     this.field_22792 = true;
     this.oldNorth = this.mapOptions.oldNorth;
     centerAt(this.options.mapX, this.options.mapZ);
     this.mc.field_1774.method_1462(true);
     if ((getMinecraft()).field_1755 == this) {
       this.closed = false;
     }
     
     this.screenTitle = I18nUtils.getString("worldmap.title", new Object[0]);
     buildWorldName();
     this.leftMouseButtonDown = false;
     this.sideMargin = 10;
     this.buttonCount = 5;
     this.buttonSeparation = 4;
     this.buttonWidth = (this.field_22789 - this.sideMargin * 2 - this.buttonSeparation * (this.buttonCount - 1)) / this.buttonCount;
     method_37063((class_364)new PopupGuiButton(this.sideMargin + 0 * (this.buttonWidth + this.buttonSeparation), getHeight() - 28, this.buttonWidth, 20, (class_2561)class_2561.method_43471("options.minimap.waypoints"), buttonWidget_1 -> getMinecraft().method_1507((class_437)new GuiWaypoints((class_437)this, this.master)), (IPopupGuiScreen)this));
     this.multiworldButtonName = (class_2561)class_2561.method_43471(getMinecraft().method_1589() ? "menu.online" : "options.worldmap.multiworld");
     this.multiworldButtonNameRed = class_2561.method_43471(getMinecraft().method_1589() ? "menu.online" : "options.worldmap.multiworld").method_27692(class_124.field_1061);
     if (!getMinecraft().method_1496() && !this.master.getWaypointManager().receivedAutoSubworldName()) {
       method_37063((class_364)(this.buttonMultiworld = new PopupGuiButton(this.sideMargin + 1 * (this.buttonWidth + this.buttonSeparation), getHeight() - 28, this.buttonWidth, 20, this.multiworldButtonName, buttonWidget_1 -> getMinecraft().method_1507((class_437)new GuiSubworldsSelect((class_437)this, this.master)), (IPopupGuiScreen)this)));
     }
     
     method_37063((class_364)new PopupGuiButton(this.sideMargin + 3 * (this.buttonWidth + this.buttonSeparation), getHeight() - 28, this.buttonWidth, 20, (class_2561)class_2561.method_43471("menu.options"), null, (IPopupGuiScreen)this) {
           public void method_25306() {
             GuiPersistentMap.this.getMinecraft().method_1507((class_437)new GuiMinimapOptions((class_437)GuiPersistentMap.this, GuiPersistentMap.this.master));
           }
         });
     method_37063((class_364)new PopupGuiButton(this.sideMargin + 4 * (this.buttonWidth + this.buttonSeparation), getHeight() - 28, this.buttonWidth, 20, (class_2561)class_2561.method_43471("gui.done"), null, (IPopupGuiScreen)this) {
           public void method_25306() {
             GuiPersistentMap.this.getMinecraft().method_1507(GuiPersistentMap.this.parent);
           }
         });
     this.coordinates = new class_342(getFontRenderer(), this.sideMargin, 10, 140, 20, (class_2561)null);
     this.top = 32;
     this.bottom = getHeight() - 32;
     this.centerX = getWidth() / 2;
     this.centerY = (this.bottom - this.top) / 2;
     this.scScale = (float)this.mc.method_22683().method_4495();
     this.mapPixelsX = this.mc.method_22683().method_4489();
     this.mapPixelsY = (this.mc.method_22683().method_4506() - (int)(64.0F * this.scScale));
     this.lastStill = false;
     this.timeAtLastTick = System.currentTimeMillis();
     this.keyBindForward.method_1422(this.forwardCode);
     this.keyBindLeft.method_1422(this.leftCode);
     this.keyBindBack.method_1422(this.backCode);
     this.keyBindRight.method_1422(this.rightCode);
     this.keyBindSprint.method_1422(this.sprintCode);
     this.mc.field_1690.field_1894.method_1422(this.nullInput);
     this.mc.field_1690.field_1913.method_1422(this.nullInput);
     this.mc.field_1690.field_1881.method_1422(this.nullInput);
     this.mc.field_1690.field_1849.method_1422(this.nullInput);
     this.mc.field_1690.field_1867.method_1422(this.nullInput);
     class_304.method_1426();
   }
   
   private void centerAt(int x, int z) {
     if (this.oldNorth) {
       this.mapCenterX = -z;
       this.mapCenterZ = x;
     } else {
       this.mapCenterX = x;
       this.mapCenterZ = z;
     } 
   }
 
   
   private void buildWorldName() {
     String worldName = "";
     if (this.mc.method_1496()) {
       worldName = this.mc.method_1576().method_27728().method_150();
       if (worldName == null || worldName.equals("")) {
         worldName = "Singleplayer World";
       }
     } else {
       class_642 serverData = this.mc.method_1558();
       if (serverData != null) {
         worldName = serverData.field_3752;
       }
       
       if (worldName == null || worldName.equals("")) {
         worldName = "Multiplayer Server";
       }
       
       if (this.field_22787.method_1589()) {
         worldName = "Realms";
       }
     } 
     
     StringBuilder worldNameBuilder = (new StringBuilder("§r")).append(worldName);
     String subworldName = this.master.getWaypointManager().getCurrentSubworldDescriptor(true);
     this.subworldName = subworldName;
     if ((subworldName == null || subworldName.equals("")) && this.master.getWaypointManager().isMultiworld()) {
       subworldName = "???";
     }
     
     if (subworldName != null && !subworldName.equals("")) {
       worldNameBuilder.append(" - ").append(subworldName);
     }
     
     this.worldNameDisplay = worldNameBuilder.toString();
     this.worldNameDisplayLength = getFontRenderer().method_1727(this.worldNameDisplay);
     
     for (this.maxWorldNameDisplayLength = getWidth() / 2 - getFontRenderer().method_1727(this.screenTitle) / 2 - this.sideMargin * 2; this.worldNameDisplayLength > this.maxWorldNameDisplayLength && worldName.length() > 5; this.worldNameDisplayLength = getFontRenderer().method_1727(this.worldNameDisplay)) {
       worldName = worldName.substring(0, worldName.length() - 1);
       worldNameBuilder = new StringBuilder(worldName);
       worldNameBuilder.append("...");
       if (subworldName != null && !subworldName.equals("")) {
         worldNameBuilder.append(" - ").append(subworldName);
       }
       
       this.worldNameDisplay = worldNameBuilder.toString();
     } 
     
     if (subworldName != null && !subworldName.equals("")) {
       while (this.worldNameDisplayLength > this.maxWorldNameDisplayLength && subworldName.length() > 5) {
         worldNameBuilder = new StringBuilder(worldName);
         worldNameBuilder.append("...");
         subworldName = subworldName.substring(0, subworldName.length() - 1);
         worldNameBuilder.append(" - ").append(subworldName);
         this.worldNameDisplay = worldNameBuilder.toString();
         this.worldNameDisplayLength = getFontRenderer().method_1727(this.worldNameDisplay);
       } 
     }
   }
 
   
   private float bindZoom(float zoom) {
     zoom = Math.max(this.options.minZoom, zoom);
     return Math.min(this.options.maxZoom, zoom);
   }
   
   private float easeOut(float elapsedTime, float startValue, float finalDelta, float totalTime) {
     float value;
     if (elapsedTime == totalTime) {
       value = startValue + finalDelta;
     } else {
       value = finalDelta * (-((float)Math.pow(2.0D, (-10.0F * elapsedTime / totalTime))) + 1.0F) + startValue;
     } 
     
     return value;
   }
   
   public boolean method_25401(double mouseX, double mouseY, double mouseRoll) {
     this.timeOfLastMouseInput = System.currentTimeMillis();
     switchToMouseInput();
     float mouseDirectX = (float)this.mc.field_1729.method_1603();
     float mouseDirectY = (float)this.mc.field_1729.method_1604();
     if (mouseRoll != 0.0D) {
       if (mouseRoll > 0.0D) {
         this.zoomGoal *= 1.26F;
       } else if (mouseRoll < 0.0D) {
         this.zoomGoal /= 1.26F;
       } 
       
       this.zoomStart = this.zoom;
       this.zoomGoal = bindZoom(this.zoomGoal);
       this.timeOfZoom = System.currentTimeMillis();
       this.zoomDirectX = mouseDirectX;
       this.zoomDirectY = mouseDirectY;
     } 
     
     return true;
   }
   
   public boolean method_25406(double mouseX, double mouseY, int mouseButton) {
     if (mouseY > this.top && mouseY < this.bottom && mouseButton == 1) {
       this.timeOfLastKBInput = 0L;
       int mouseDirectX = (int)this.mc.field_1729.method_1603();
       int mouseDirectY = (int)this.mc.field_1729.method_1604();
       createPopup((int)mouseX, (int)mouseY, mouseDirectX, mouseDirectY);
     } 
     
     return super.method_25406(mouseX, mouseY, mouseButton);
   }
 
   
   public boolean method_25402(double mouseX, double mouseY, int mouseButton) {
     if (popupOpen()) {
       this.coordinates.method_25402(mouseX, mouseY, mouseButton);
       this.editingCoordinates = this.coordinates.method_25370();
       if (this.editingCoordinates && !this.lastEditingCoordinates) {
         int x = 0;
         int z = 0;
         if (this.oldNorth) {
           x = (int)Math.floor(this.mapCenterZ);
           z = -((int)Math.floor(this.mapCenterX));
         } else {
           x = (int)Math.floor(this.mapCenterX);
           z = (int)Math.floor(this.mapCenterZ);
         } 
         
         this.coordinates.method_1852("" + x + ", " + x);
         this.coordinates.method_1868(16777215);
       } 
       
       this.lastEditingCoordinates = this.editingCoordinates;
     } 
     
     return (super.method_25402(mouseX, mouseY, mouseButton) || mouseButton == 1);
   }
   
   public boolean method_25404(int keysm, int scancode, int b) {
     if (!this.editingCoordinates && (this.mc.field_1690.field_1903.method_1417(keysm, scancode) || this.mc.field_1690.field_1832.method_1417(keysm, scancode))) {
       if (this.mc.field_1690.field_1903.method_1417(keysm, scancode)) {
         this.zoomGoal /= 1.26F;
       }
       
       if (this.mc.field_1690.field_1832.method_1417(keysm, scancode)) {
         this.zoomGoal *= 1.26F;
       }
       
       this.zoomStart = this.zoom;
       this.zoomGoal = bindZoom(this.zoomGoal);
       this.timeOfZoom = System.currentTimeMillis();
       this.zoomDirectX = (this.mc.method_22683().method_4489() / 2);
       this.zoomDirectY = (this.mc.method_22683().method_4506() - this.mc.method_22683().method_4506() / 2);
       switchToKeyboardInput();
     } 
     
     clearPopups();
     if (this.editingCoordinates) {
       this.coordinates.method_25404(keysm, scancode, b);
       boolean isGood = isAcceptable(this.coordinates.method_1882());
       this.coordinates.method_1868(isGood ? 16777215 : 16711680);
       if ((keysm == 257 || keysm == 335) && this.coordinates.method_25370() && isGood) {
         String[] xz = this.coordinates.method_1882().split(",");
         centerAt(Integer.valueOf(xz[0].trim()).intValue(), Integer.valueOf(xz[1].trim()).intValue());
         this.editingCoordinates = false;
         this.lastEditingCoordinates = false;
         switchToKeyboardInput();
       } 
       
       if (keysm == 258 && this.coordinates.method_25370()) {
         this.editingCoordinates = false;
         this.lastEditingCoordinates = false;
         switchToKeyboardInput();
       } 
     } 
     
     if ((this.master.getMapOptions()).keyBindMenu.method_1417(keysm, scancode)) {
       keysm = 256;
       scancode = -1;
       b = -1;
     } 
     
     return super.method_25404(keysm, scancode, b);
   }
   
   public boolean method_25400(char typedChar, int keyCode) {
     clearPopups();
     if (this.editingCoordinates) {
       this.coordinates.method_25400(typedChar, keyCode);
       boolean isGood = isAcceptable(this.coordinates.method_1882());
       this.coordinates.method_1868(isGood ? 16777215 : 16711680);
       if (typedChar == '\r' && this.coordinates.method_25370() && isGood) {
         String[] xz = this.coordinates.method_1882().split(",");
         centerAt(Integer.valueOf(xz[0].trim()).intValue(), Integer.valueOf(xz[1].trim()).intValue());
         this.editingCoordinates = false;
         this.lastEditingCoordinates = false;
         switchToKeyboardInput();
       } 
     } 
     
     if ((this.master.getMapOptions()).keyBindMenu.method_1417(keyCode, -1)) {
       super.method_25404(256, -1, -1);
     }
     
     return super.method_25400(typedChar, keyCode);
   }
   
   private boolean isAcceptable(String input) {
     try {
       String[] xz = this.coordinates.method_1882().split(",");
       Integer.valueOf(xz[0].trim());
       Integer.valueOf(xz[1].trim());
       return true;
     } catch (NumberFormatException var3) {
       return false;
     } catch (ArrayIndexOutOfBoundsException var4) {
       return false;
     } 
   }
   
   private void switchToMouseInput() {
     this.timeOfLastKBInput = 0L;
     if (!this.mouseCursorShown) {
       GLFW.glfwSetInputMode(this.mc.method_22683().method_4490(), 208897, 212993);
     }
     
     this.mouseCursorShown = true;
   }
   
   private void switchToKeyboardInput() {
     this.timeOfLastKBInput = System.currentTimeMillis();
     this.mouseCursorShown = false;
     GLFW.glfwSetInputMode(this.mc.method_22683().method_4490(), 208897, 212995);
   }
   
   public void method_25394(class_4587 matrixStack, int mouseX, int mouseY, float partialTicks) {
     float cursorX, cursorY, cursorCoordZ, cursorCoordX;
     this.zoomGoal = bindZoom(this.zoomGoal);
     if (this.mouseX != mouseX || this.mouseY != mouseY) {
       this.timeOfLastMouseInput = System.currentTimeMillis();
       switchToMouseInput();
     } 
     
     this.mouseX = mouseX;
     this.mouseY = mouseY;
     float mouseDirectX = (float)this.mc.field_1729.method_1603();
     float mouseDirectY = (float)this.mc.field_1729.method_1604();
     if (this.zoom != this.zoomGoal) {
       float previousZoom = this.zoom;
       long timeSinceZoom = System.currentTimeMillis() - this.timeOfZoom;
       if ((float)timeSinceZoom < 700.0F) {
         this.zoom = easeOut((float)timeSinceZoom, this.zoomStart, this.zoomGoal - this.zoomStart, 700.0F);
       } else {
         this.zoom = this.zoomGoal;
       } 
       
       float f1 = this.zoom;
       if (this.mc.method_22683().method_4489() > 1600) {
         f1 = this.zoom * this.mc.method_22683().method_4489() / 1600.0F;
       }
       
       float zoomDelta = this.zoom / previousZoom;
       float zoomOffsetX = this.centerX * this.guiToDirectMouse - this.zoomDirectX;
       float zoomOffsetY = (this.top + this.centerY) * this.guiToDirectMouse - this.zoomDirectY;
       float zoomDeltaX = zoomOffsetX - zoomOffsetX * zoomDelta;
       float zoomDeltaY = zoomOffsetY - zoomOffsetY * zoomDelta;
       this.mapCenterX += zoomDeltaX / f1;
       this.mapCenterZ += zoomDeltaY / f1;
     } 
     
     this.options.zoom = this.zoomGoal;
     float scaledZoom = this.zoom;
     if (this.mc.method_22683().method_4480() > 1600) {
       scaledZoom = this.zoom * this.mc.method_22683().method_4480() / 1600.0F;
     }
     
     this.guiToMap = this.scScale / scaledZoom;
     this.mapToGui = 1.0F / this.scScale * scaledZoom;
     this.mouseDirectToMap = 1.0F / scaledZoom;
     this.guiToDirectMouse = this.scScale;
     method_25420(matrixStack);
     if (this.mc.field_1729.method_1608()) {
       if (!this.leftMouseButtonDown && overPopup(mouseX, mouseY)) {
         this.deltaX = 0.0F;
         this.deltaY = 0.0F;
         this.lastMouseX = mouseDirectX;
         this.lastMouseY = mouseDirectY;
         this.leftMouseButtonDown = true;
       } else if (this.leftMouseButtonDown) {
         this.deltaX = (this.lastMouseX - mouseDirectX) * this.mouseDirectToMap;
         this.deltaY = (this.lastMouseY - mouseDirectY) * this.mouseDirectToMap;
         this.lastMouseX = mouseDirectX;
         this.lastMouseY = mouseDirectY;
         this.deltaXonRelease = this.deltaX;
         this.deltaYonRelease = this.deltaY;
         this.timeOfRelease = System.currentTimeMillis();
       } 
     } else {
       long timeSinceRelease = System.currentTimeMillis() - this.timeOfRelease;
       if ((float)timeSinceRelease < 700.0F) {
         this.deltaX = this.deltaXonRelease * (float)Math.exp(((float)-timeSinceRelease / 350.0F));
         this.deltaY = this.deltaYonRelease * (float)Math.exp(((float)-timeSinceRelease / 350.0F));
       } else {
         this.deltaX = 0.0F;
         this.deltaY = 0.0F;
         this.deltaXonRelease = 0.0F;
         this.deltaYonRelease = 0.0F;
       } 
       
       this.leftMouseButtonDown = false;
     } 
     
     long timeSinceLastTick = System.currentTimeMillis() - this.timeAtLastTick;
     this.timeAtLastTick = System.currentTimeMillis();
     if (!this.editingCoordinates) {
       int kbDelta = 5;
       if (this.keyBindSprint.method_1434()) {
         kbDelta = 10;
       }
       
       if (this.keyBindForward.method_1434()) {
         this.deltaY -= kbDelta / scaledZoom * (float)timeSinceLastTick / 12.0F;
         switchToKeyboardInput();
       } 
       
       if (this.keyBindBack.method_1434()) {
         this.deltaY += kbDelta / scaledZoom * (float)timeSinceLastTick / 12.0F;
         switchToKeyboardInput();
       } 
       
       if (this.keyBindLeft.method_1434()) {
         this.deltaX -= kbDelta / scaledZoom * (float)timeSinceLastTick / 12.0F;
         switchToKeyboardInput();
       } 
       
       if (this.keyBindRight.method_1434()) {
         this.deltaX += kbDelta / scaledZoom * (float)timeSinceLastTick / 12.0F;
         switchToKeyboardInput();
       } 
     } 
     
     this.mapCenterX += this.deltaX;
     this.mapCenterZ += this.deltaY;
     if (this.oldNorth) {
       this.options.mapX = (int)this.mapCenterZ;
       this.options.mapZ = -((int)this.mapCenterX);
     } else {
       this.options.mapX = (int)this.mapCenterX;
       this.options.mapZ = (int)this.mapCenterZ;
     } 
     
     this.centerX = getWidth() / 2;
     this.centerY = (this.bottom - this.top) / 2;
     int left = 0;
     int right = 0;
     int top = 0;
     int bottom = 0;
     if (this.oldNorth) {
       left = (int)Math.floor(((this.mapCenterZ - this.centerY * this.guiToMap) / 256.0F));
       right = (int)Math.floor(((this.mapCenterZ + this.centerY * this.guiToMap) / 256.0F));
       top = (int)Math.floor(((-this.mapCenterX - this.centerX * this.guiToMap) / 256.0F));
       bottom = (int)Math.floor(((-this.mapCenterX + this.centerX * this.guiToMap) / 256.0F));
     } else {
       left = (int)Math.floor(((this.mapCenterX - this.centerX * this.guiToMap) / 256.0F));
       right = (int)Math.floor(((this.mapCenterX + this.centerX * this.guiToMap) / 256.0F));
       top = (int)Math.floor(((this.mapCenterZ - this.centerY * this.guiToMap) / 256.0F));
       bottom = (int)Math.floor(((this.mapCenterZ + this.centerY * this.guiToMap) / 256.0F));
     } 
     
     synchronized (this.closedLock) {
       if (this.closed) {
         return;
       }
       
       this.regions = this.persistentMap.getRegions(left - 1, right + 1, top - 1, bottom + 1);
     } 
     
     class_4587 modelViewMatrixStack = RenderSystem.getModelViewStack();
     modelViewMatrixStack.method_22903();
     GLShim.glColor3f(1.0F, 1.0F, 1.0F);
     modelViewMatrixStack.method_22904((this.centerX - this.mapCenterX * this.mapToGui), ((this.top + this.centerY) - this.mapCenterZ * this.mapToGui), 0.0D);
     if (this.oldNorth) {
       modelViewMatrixStack.method_22907(class_1160.field_20707.method_23214(90.0F));
     }
     
     RenderSystem.applyModelViewMatrix();
     RenderSystem.setShader(class_757::method_34542);
     this.backGroundImageInfo = this.waypointManager.getBackgroundImageInfo();
     if (this.backGroundImageInfo != null) {
       GLUtils.disp2(this.backGroundImageInfo.glid);
       drawTexturedModalRect(this.backGroundImageInfo.left * this.mapToGui, this.backGroundImageInfo.top * this.mapToGui, this.backGroundImageInfo.width * this.mapToGui, this.backGroundImageInfo.height * this.mapToGui);
     } 
     
     for (int t = 0; t < this.regions.length; t++) {
       CachedRegion region = this.regions[t];
       int glid = region.getGLID();
       if (glid != 0) {
         GLUtils.disp2(glid);
         if (this.mapOptions.filtering) {
           GLShim.glTexParameteri(3553, 10241, 9987);
           GLShim.glTexParameteri(3553, 10240, 9729);
         } else {
           GLShim.glTexParameteri(3553, 10241, 9987);
           GLShim.glTexParameteri(3553, 10240, 9728);
         } 
         
         drawTexturedModalRect((region.getX() * 256) * this.mapToGui, (region.getZ() * 256) * this.mapToGui, region.getWidth() * this.mapToGui, region.getWidth() * this.mapToGui);
       } 
     } 
 
 
     
     if (this.mouseCursorShown) {
       cursorX = mouseDirectX;
       cursorY = mouseDirectY - this.top * this.guiToDirectMouse;
     } else {
       cursorX = (this.mc.method_22683().method_4489() / 2);
       cursorY = (this.mc.method_22683().method_4506() - this.mc.method_22683().method_4506() / 2) - this.top * this.guiToDirectMouse;
     } 
 
 
     
     if (this.oldNorth) {
       cursorCoordX = cursorY * this.mouseDirectToMap + this.mapCenterZ - this.centerY * this.guiToMap;
       cursorCoordZ = -(cursorX * this.mouseDirectToMap + this.mapCenterX - this.centerX * this.guiToMap);
     } else {
       cursorCoordX = cursorX * this.mouseDirectToMap + this.mapCenterX - this.centerX * this.guiToMap;
       cursorCoordZ = cursorY * this.mouseDirectToMap + this.mapCenterZ - this.centerY * this.guiToMap;
     } 
     
     RenderSystem.setShader(class_757::method_34542);
     if (this.options.showWaypoints) {
       for (Waypoint pt : this.waypointManager.getWaypoints()) {
         drawWaypoint(matrixStack, pt, cursorCoordX, cursorCoordZ, (Sprite)null, (Float)null, (Float)null, (Float)null);
       }
       
       if (this.waypointManager.getHighlightedWaypoint() != null) {
         drawWaypoint(matrixStack, this.waypointManager.getHighlightedWaypoint(), cursorCoordX, cursorCoordZ, this.master.getWaypointManager().getTextureAtlas().getAtlasSprite("voxelmap:images/waypoints/target.png"), Float.valueOf(1.0F), Float.valueOf(0.0F), Float.valueOf(0.0F));
       }
     } 
     
     GLShim.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
     RenderSystem.setShader(class_757::method_34542);
     GLUtils.disp2(playerGLID);
     GLShim.glTexParameteri(3553, 10241, 9729);
     GLShim.glTexParameteri(3553, 10240, 9729);
     float playerX = (float)GameVariableAccessShim.xCoordDouble();
     float playerZ = (float)GameVariableAccessShim.zCoordDouble();
     if (this.oldNorth) {
       modelViewMatrixStack.method_22903();
       modelViewMatrixStack.method_22904((playerX * this.mapToGui), (playerZ * this.mapToGui), 0.0D);
       modelViewMatrixStack.method_22907(class_1160.field_20707.method_23214(-90.0F));
       modelViewMatrixStack.method_22904(-(playerX * this.mapToGui), -(playerZ * this.mapToGui), 0.0D);
       RenderSystem.applyModelViewMatrix();
     } 
     
     drawTexturedModalRect(-10.0F / this.scScale + playerX * this.mapToGui, -10.0F / this.scScale + playerZ * this.mapToGui, 20.0F / this.scScale, 20.0F / this.scScale);
     if (this.oldNorth) {
       modelViewMatrixStack.method_22909();
     }
     
     if (this.oldNorth) {
       modelViewMatrixStack.method_22907(class_1160.field_20707.method_23214(-90.0F));
     }
     
     modelViewMatrixStack.method_22904(-(this.centerX - this.mapCenterX * this.mapToGui), -((this.top + this.centerY) - this.mapCenterZ * this.mapToGui), 0.0D);
     RenderSystem.applyModelViewMatrix();
     if (this.mapOptions.biomeOverlay != 0) {
       float biomeScaleX = this.mapPixelsX / 760.0F;
       float biomeScaleY = this.mapPixelsY / 360.0F;
       boolean still = !this.leftMouseButtonDown;
       still = (still && this.zoom == this.zoomGoal);
       still = (still && this.deltaX == 0.0F && this.deltaY == 0.0F);
       still = (still && ThreadManager.executorService.getActiveCount() == 0);
       if (still && !this.lastStill) {
         int column = 0;
         if (this.oldNorth) {
           column = (int)Math.floor(Math.floor((this.mapCenterZ - this.centerY * this.guiToMap)) / 256.0D) - left - 1;
         } else {
           column = (int)Math.floor(Math.floor((this.mapCenterX - this.centerX * this.guiToMap)) / 256.0D) - left - 1;
         } 
         
         for (int i = 0; i < this.biomeMapData.getWidth(); i++) {
           for (int j = 0; j < this.biomeMapData.getHeight(); j++) {
             float floatMapX, floatMapZ;
             
             if (this.oldNorth) {
               floatMapX = j * biomeScaleY * this.mouseDirectToMap + this.mapCenterZ - this.centerY * this.guiToMap;
               floatMapZ = -(i * biomeScaleX * this.mouseDirectToMap + this.mapCenterX - this.centerX * this.guiToMap);
             } else {
               floatMapX = i * biomeScaleX * this.mouseDirectToMap + this.mapCenterX - this.centerX * this.guiToMap;
               floatMapZ = j * biomeScaleY * this.mouseDirectToMap + this.mapCenterZ - this.centerY * this.guiToMap;
             } 
             
             int mapX = (int)Math.floor(floatMapX);
             int mapZ = (int)Math.floor(floatMapZ);
             int regionX = (int)Math.floor((mapX / 256.0F)) - left - 1;
             int regionZ = (int)Math.floor((mapZ / 256.0F)) - top - 1;
             if ((!this.oldNorth && regionX != column) || (this.oldNorth && regionZ != column)) {
               this.persistentMap.compress();
             }
             
             column = !this.oldNorth ? regionX : regionZ;
             CachedRegion region = this.regions[regionZ * (right + 1 - left - 1 + 1) + regionX];
             int id = -1;
             if (region.getMapData() != null && region.isLoaded() && !region.isEmpty()) {
               int inRegionX = mapX - region.getX() * region.getWidth();
               int inRegionZ = mapZ - region.getZ() * region.getWidth();
               int height = region.getMapData().getHeight(inRegionX, inRegionZ);
               int light = region.getMapData().getLight(inRegionX, inRegionZ);
               if (height != 0 || light != 0) {
                 id = region.getMapData().getBiomeID(inRegionX, inRegionZ);
               }
             } 
             
             this.biomeMapData.setBiomeID(i, j, id);
           } 
         } 
         
         this.persistentMap.compress();
         this.biomeMapData.segmentBiomes();
         this.biomeMapData.findCenterOfSegments(true);
       } 
       
       this.lastStill = still;
       boolean displayStill = !this.leftMouseButtonDown;
       displayStill = (displayStill && this.zoom == this.zoomGoal);
       displayStill = (displayStill && this.deltaX == 0.0F && this.deltaY == 0.0F);
       if (displayStill) {
         int minimumSize = (int)(20.0F * this.scScale / biomeScaleX);
         minimumSize *= minimumSize;
         ArrayList<AbstractMapData.BiomeLabel> labels = this.biomeMapData.getBiomeLabels();
         GLShim.glDisable(2929);
         
         for (int i = 0; i < labels.size(); i++) {
           AbstractMapData.BiomeLabel label = labels.get(i);
           if (label.segmentSize > minimumSize) {
             int nameWidth = chkLen(label.name);
             float f1 = label.x * biomeScaleX / this.scScale;
             float f2 = label.z * biomeScaleY / this.scScale;
             write(matrixStack, label.name, f1 - (nameWidth / 2), this.top + f2 - 3.0F, 16777215);
           } 
         } 
         
         GLShim.glEnable(2929);
       } 
     } 
     
     modelViewMatrixStack.method_22909();
     RenderSystem.applyModelViewMatrix();
     if (System.currentTimeMillis() - this.timeOfLastKBInput < 2000L) {
       int scWidth = this.mc.method_22683().method_4486();
       int scHeight = this.mc.method_22683().method_4502();
       RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
       RenderSystem.setShader(class_757::method_34542);
       RenderSystem.setShaderTexture(0, field_22737);
       RenderSystem.enableBlend();
       RenderSystem.blendFuncSeparate(775, 769, 1, 0);
       method_25302(matrixStack, scWidth / 2 - 7, scHeight / 2 - 7, 0, 0, 16, 16);
       RenderSystem.blendFuncSeparate(770, 771, 1, 0);
     } else {
       switchToMouseInput();
     } 
     
     overlayBackground(0, this.top, 255, 255);
     overlayBackground(this.bottom, getHeight(), 255, 255);
     method_25300(matrixStack, getFontRenderer(), this.screenTitle, getWidth() / 2, 16, 16777215);
     int x = (int)Math.floor(cursorCoordX);
     int z = (int)Math.floor(cursorCoordZ);
     if ((this.master.getMapOptions()).coords) {
       if (!this.editingCoordinates) {
         method_25303(matrixStack, getFontRenderer(), "X: " + x, this.sideMargin, 16, 16777215);
         method_25303(matrixStack, getFontRenderer(), "Z: " + z, this.sideMargin + 64, 16, 16777215);
       } else {
         this.coordinates.method_25394(matrixStack, mouseX, mouseY, partialTicks);
       } 
     }
     
     if ((this.subworldName != null && !this.subworldName.equals(this.master.getWaypointManager().getCurrentSubworldDescriptor(true))) || (this.master.getWaypointManager().getCurrentSubworldDescriptor(true) != null && !this.master.getWaypointManager().getCurrentSubworldDescriptor(true).equals(this.subworldName))) {
       buildWorldName();
     }
     
     method_25303(matrixStack, getFontRenderer(), this.worldNameDisplay, getWidth() - this.sideMargin - this.worldNameDisplayLength, 16, 16777215);
     if (this.buttonMultiworld != null) {
       if ((this.subworldName == null || this.subworldName.equals("")) && this.master.getWaypointManager().isMultiworld()) {
         if ((int)(System.currentTimeMillis() / 1000L % 2L) == 0) {
           this.buttonMultiworld.method_25355((class_2561)this.multiworldButtonNameRed);
         } else {
           this.buttonMultiworld.method_25355(this.multiworldButtonName);
         } 
       } else {
         this.buttonMultiworld.method_25355(this.multiworldButtonName);
       } 
     }
     
     super.method_25394(matrixStack, mouseX, mouseY, partialTicks);
   }
   
   private void drawWaypoint(class_4587 matrixStack, Waypoint pt, float cursorCoordX, float cursorCoordZ, Sprite icon, Float r, Float g, Float b) {
     if (pt.inWorld && pt.inDimension && isOnScreen(pt.getX(), pt.getZ())) {
       String name = pt.name;
       if (r == null) {
         r = Float.valueOf(pt.red);
       }
       
       if (g == null) {
         g = Float.valueOf(pt.green);
       }
       
       if (b == null) {
         b = Float.valueOf(pt.blue);
       }
       
       float ptX = pt.getX();
       float ptZ = pt.getZ();
       if ((this.backGroundImageInfo != null && this.backGroundImageInfo.isInRange((int)ptX, (int)ptZ)) || this.persistentMap.isRegionLoaded((int)ptX, (int)ptZ)) {
         ptX += 0.5F;
         ptZ += 0.5F;
         boolean hover = (cursorCoordX > ptX - 18.0F * this.guiToMap / this.guiToDirectMouse && cursorCoordX < ptX + 18.0F * this.guiToMap / this.guiToDirectMouse && cursorCoordZ > ptZ - 18.0F * this.guiToMap / this.guiToDirectMouse && cursorCoordZ < ptZ + 18.0F * this.guiToMap / this.guiToDirectMouse);
         boolean target = false;
         RenderSystem.setShader(class_757::method_34542);
         TextureAtlas atlas = this.master.getWaypointManager().getTextureAtlas();
         GLUtils.disp2(atlas.method_4624());
         if (icon == null) {
           icon = atlas.getAtlasSprite("voxelmap:images/waypoints/waypoint" + pt.imageSuffix + ".png");
           if (icon == atlas.getMissingImage()) {
             icon = atlas.getAtlasSprite("voxelmap:images/waypoints/waypoint.png");
           }
         } else {
           name = "";
           target = true;
         } 
         
         GLShim.glColor4f(r.floatValue(), g.floatValue(), b.floatValue(), (!pt.enabled && !target && !hover) ? 0.3F : 1.0F);
         GLShim.glTexParameteri(3553, 10241, 9729);
         GLShim.glTexParameteri(3553, 10240, 9729);
         if (this.oldNorth) {
           matrixStack.method_22903();
           matrixStack.method_22904((ptX * this.mapToGui), (ptZ * this.mapToGui), 0.0D);
           matrixStack.method_22907(class_1160.field_20707.method_23214(-90.0F));
           matrixStack.method_22904(-(ptX * this.mapToGui), -(ptZ * this.mapToGui), 0.0D);
           RenderSystem.applyModelViewMatrix();
         } 
         
         drawTexturedModalRect(-16.0F / this.scScale + ptX * this.mapToGui, -16.0F / this.scScale + ptZ * this.mapToGui, icon, 32.0F / this.scScale, 32.0F / this.scScale);
         if (this.oldNorth) {
           matrixStack.method_22909();
           RenderSystem.applyModelViewMatrix();
         } 
         
         if ((this.mapOptions.biomeOverlay == 0 && this.options.showWaypointNames) || target || hover) {
           float fontScale = 2.0F / this.scScale;
           int m = chkLen(name) / 2;
           matrixStack.method_22903();
           matrixStack.method_22905(fontScale, fontScale, 1.0F);
           if (this.oldNorth) {
             matrixStack.method_22904((ptX * this.mapToGui / fontScale), (ptZ * this.mapToGui / fontScale), 0.0D);
             matrixStack.method_22907(class_1160.field_20707.method_23214(-90.0F));
             matrixStack.method_22904(-(ptX * this.mapToGui / fontScale), -(ptZ * this.mapToGui / fontScale), 0.0D);
             RenderSystem.applyModelViewMatrix();
           } 
           
           write(matrixStack, name, ptX * this.mapToGui / fontScale - m, ptZ * this.mapToGui / fontScale + 16.0F / this.scScale / fontScale, (!pt.enabled && !target && !hover) ? 1442840575 : 16777215);
           matrixStack.method_22909();
           RenderSystem.applyModelViewMatrix();
           GLShim.glEnable(3042);
         } 
       } 
     } 
   }
 
   
   private boolean isOnScreen(int x, int z) {
     int left;
     int right;
     int top;
     int bottom;
     if (this.oldNorth) {
       left = (int)Math.floor(this.mapCenterZ - (this.centerY * this.guiToMap) * 1.1D);
       right = (int)Math.floor(this.mapCenterZ + (this.centerY * this.guiToMap) * 1.1D);
       top = (int)Math.floor(-this.mapCenterX - (this.centerX * this.guiToMap) * 1.1D);
       bottom = (int)Math.floor(-this.mapCenterX + (this.centerX * this.guiToMap) * 1.1D);
     } else {
       left = (int)Math.floor(this.mapCenterX - (this.centerX * this.guiToMap) * 1.1D);
       right = (int)Math.floor(this.mapCenterX + (this.centerX * this.guiToMap) * 1.1D);
       top = (int)Math.floor(this.mapCenterZ - (this.centerY * this.guiToMap) * 1.1D);
       bottom = (int)Math.floor(this.mapCenterZ + (this.centerY * this.guiToMap) * 1.1D);
     } 
     
     return (x > left && x < right && z > top && z < bottom);
   }
   
   public void method_25420(class_4587 matrixStack) {
     method_25294(matrixStack, 0, 0, getWidth(), getHeight(), -16777216);
   }
   
   protected void overlayBackground(int startY, int endY, int startAlpha, int endAlpha) {
     class_289 tessellator = class_289.method_1348();
     class_287 vertexBuffer = tessellator.method_1349();
     RenderSystem.setShader(class_757::method_34543);
     RenderSystem.setShaderTexture(0, class_437.field_22735);
     RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
     vertexBuffer.method_1328(class_293.class_5596.field_27382, class_290.field_1575);
     vertexBuffer.method_22912(0.0D, endY, 0.0D).method_22913(0.0F, endY / 32.0F).method_1336(64, 64, 64, endAlpha).method_1344();
     vertexBuffer.method_22912((0 + getWidth()), endY, 0.0D).method_22913(this.field_22789 / 32.0F, endY / 32.0F).method_1336(64, 64, 64, endAlpha).method_1344();
     vertexBuffer.method_22912((0 + getWidth()), startY, 0.0D).method_22913(this.field_22789 / 32.0F, startY / 32.0F).method_1336(64, 64, 64, startAlpha).method_1344();
     vertexBuffer.method_22912(0.0D, startY, 0.0D).method_22913(0.0F, startY / 32.0F).method_1336(64, 64, 64, startAlpha).method_1344();
     tessellator.method_1350();
   }
   
   public void method_25393() {
     this.coordinates.method_1865();
   }
 
   
   public void method_25432() {
     this.mc.field_1690.field_1894.method_1422(this.forwardCode);
     this.mc.field_1690.field_1913.method_1422(this.leftCode);
     this.mc.field_1690.field_1881.method_1422(this.backCode);
     this.mc.field_1690.field_1849.method_1422(this.rightCode);
     this.mc.field_1690.field_1867.method_1422(this.sprintCode);
     this.keyBindForward.method_1422(this.nullInput);
     this.keyBindLeft.method_1422(this.nullInput);
     this.keyBindBack.method_1422(this.nullInput);
     this.keyBindRight.method_1422(this.nullInput);
     this.keyBindSprint.method_1422(this.nullInput);
     class_304.method_1426();
     class_304.method_1437();
     this.mc.field_1774.method_1462(false);
     synchronized (this.closedLock) {
       this.closed = true;
       this.persistentMap.getRegions(0, -1, 0, -1);
       this.regions = new CachedRegion[0];
     } 
   }
   
   public void drawTexturedModalRect(float x, float y, float width, float height) {
     class_289 tessellator = class_289.method_1348();
     class_287 vertexBuffer = tessellator.method_1349();
     vertexBuffer.method_1328(class_293.class_5596.field_27382, class_290.field_1585);
     vertexBuffer.method_22912((x + 0.0F), (y + height), method_25305()).method_22913(0.0F, 1.0F).method_1344();
     vertexBuffer.method_22912((x + width), (y + height), method_25305()).method_22913(1.0F, 1.0F).method_1344();
     vertexBuffer.method_22912((x + width), (y + 0.0F), method_25305()).method_22913(1.0F, 0.0F).method_1344();
     vertexBuffer.method_22912((x + 0.0F), (y + 0.0F), method_25305()).method_22913(0.0F, 0.0F).method_1344();
     tessellator.method_1350();
   }
   
   public void drawTexturedModalRect(Sprite icon, float x, float y) {
     float width = icon.getIconWidth() / this.scScale;
     float height = icon.getIconHeight() / this.scScale;
     drawTexturedModalRect(x, y, icon, width, height);
   }
   
   public void drawTexturedModalRect(float xCoord, float yCoord, Sprite icon, float widthIn, float heightIn) {
     class_289 tessellator = class_289.method_1348();
     class_287 vertexBuffer = tessellator.method_1349();
     vertexBuffer.method_1328(class_293.class_5596.field_27382, class_290.field_1585);
     vertexBuffer.method_22912((xCoord + 0.0F), (yCoord + heightIn), method_25305()).method_22913(icon.getMinU(), icon.getMaxV()).method_1344();
     vertexBuffer.method_22912((xCoord + widthIn), (yCoord + heightIn), method_25305()).method_22913(icon.getMaxU(), icon.getMaxV()).method_1344();
     vertexBuffer.method_22912((xCoord + widthIn), (yCoord + 0.0F), method_25305()).method_22913(icon.getMaxU(), icon.getMinV()).method_1344();
     vertexBuffer.method_22912((xCoord + 0.0F), (yCoord + 0.0F), method_25305()).method_22913(icon.getMinU(), icon.getMinV()).method_1344();
     tessellator.method_1350();
   }
   private void createPopup(int mouseX, int mouseY, int mouseDirectX, int mouseDirectY) {
     float cursorCoordX, cursorCoordZ;
     ArrayList<Popup.PopupEntry> entries = new ArrayList();
     float cursorX = mouseDirectX;
     float cursorY = mouseDirectY - this.top * this.guiToDirectMouse;
 
     
     if (this.oldNorth) {
       cursorCoordX = cursorY * this.mouseDirectToMap + this.mapCenterZ - this.centerY * this.guiToMap;
       cursorCoordZ = -(cursorX * this.mouseDirectToMap + this.mapCenterX - this.centerX * this.guiToMap);
     } else {
       cursorCoordX = cursorX * this.mouseDirectToMap + this.mapCenterX - this.centerX * this.guiToMap;
       cursorCoordZ = cursorY * this.mouseDirectToMap + this.mapCenterZ - this.centerY * this.guiToMap;
     } 
     
     int x = (int)Math.floor(cursorCoordX);
     int z = (int)Math.floor(cursorCoordZ);
     boolean canTeleport = canTeleport();
     canTeleport = (canTeleport && (this.persistentMap.isGroundAt(x, z) || (this.backGroundImageInfo != null && this.backGroundImageInfo.isGroundAt(x, z))));
     Waypoint hovered = getHovered(cursorCoordX, cursorCoordZ);
     if (hovered != null && this.waypointManager.getWaypoints().contains(hovered)) {
       Popup.PopupEntry entry = new Popup.PopupEntry(I18nUtils.getString("selectServer.edit", new Object[0]), 4, true, true);
       entries.add(entry);
       entry = new Popup.PopupEntry(I18nUtils.getString("selectServer.delete", new Object[0]), 5, true, true);
       entries.add(entry);
       entry = new Popup.PopupEntry(I18nUtils.getString((hovered != this.waypointManager.getHighlightedWaypoint()) ? "minimap.waypoints.highlight" : "minimap.waypoints.removehighlight", new Object[0]), 1, true, true);
       entries.add(entry);
       entry = new Popup.PopupEntry(I18nUtils.getString("minimap.waypoints.teleportto", new Object[0]), 3, true, canTeleport);
       entries.add(entry);
       entry = new Popup.PopupEntry(I18nUtils.getString("minimap.waypoints.share", new Object[0]), 2, true, true);
       entries.add(entry);
     } else {
       Popup.PopupEntry entry = new Popup.PopupEntry(I18nUtils.getString("minimap.waypoints.newwaypoint", new Object[0]), 0, true, true);
       entries.add(entry);
       entry = new Popup.PopupEntry(I18nUtils.getString((hovered == null) ? "minimap.waypoints.highlight" : "minimap.waypoints.removehighlight", new Object[0]), 1, true, true);
       entries.add(entry);
       entry = new Popup.PopupEntry(I18nUtils.getString("minimap.waypoints.teleportto", new Object[0]), 3, true, canTeleport);
       entries.add(entry);
       entry = new Popup.PopupEntry(I18nUtils.getString("minimap.waypoints.share", new Object[0]), 2, true, true);
       entries.add(entry);
     } 
     
     createPopup(mouseX, mouseY, mouseDirectX, mouseDirectY, entries);
   }
   
   private Waypoint getHovered(float cursorCoordX, float cursorCoordZ) {
     Waypoint waypoint = null;
     
     for (Waypoint pt : this.waypointManager.getWaypoints()) {
       float ptX = pt.getX() + 0.5F;
       float ptZ = pt.getZ() + 0.5F;
       boolean hover = (pt.inDimension && pt.inWorld && cursorCoordX > ptX - 18.0F * this.guiToMap / this.guiToDirectMouse && cursorCoordX < ptX + 18.0F * this.guiToMap / this.guiToDirectMouse && cursorCoordZ > ptZ - 18.0F * this.guiToMap / this.guiToDirectMouse && cursorCoordZ < ptZ + 18.0F * this.guiToMap / this.guiToDirectMouse);
       if (hover) {
         waypoint = pt;
       }
     } 
     
     if (waypoint == null) {
       Waypoint pt = this.waypointManager.getHighlightedWaypoint();
       if (pt != null) {
         float ptX = pt.getX() + 0.5F;
         float ptZ = pt.getZ() + 0.5F;
         boolean hover = (pt.inDimension && pt.inWorld && cursorCoordX > ptX - 18.0F * this.guiToMap / this.guiToDirectMouse && cursorCoordX < ptX + 18.0F * this.guiToMap / this.guiToDirectMouse && cursorCoordZ > ptZ - 18.0F * this.guiToMap / this.guiToDirectMouse && cursorCoordZ < ptZ + 18.0F * this.guiToMap / this.guiToDirectMouse);
         if (hover) {
           waypoint = pt;
         }
       } 
     } 
     
     return waypoint;
   }
   public void popupAction(Popup popup, int action) {
     float cursorCoordX, cursorCoordZ, r, g, b;
     TreeSet<DimensionContainer> dimensions;
     int mouseDirectX = popup.clickedDirectX;
     int mouseDirectY = popup.clickedDirectY;
     float cursorX = mouseDirectX;
     float cursorY = mouseDirectY - this.top * this.guiToDirectMouse;
 
     
     if (this.oldNorth) {
       cursorCoordX = cursorY * this.mouseDirectToMap + this.mapCenterZ - this.centerY * this.guiToMap;
       cursorCoordZ = -(cursorX * this.mouseDirectToMap + this.mapCenterX - this.centerX * this.guiToMap);
     } else {
       cursorCoordX = cursorX * this.mouseDirectToMap + this.mapCenterX - this.centerX * this.guiToMap;
       cursorCoordZ = cursorY * this.mouseDirectToMap + this.mapCenterZ - this.centerY * this.guiToMap;
     } 
     
     int x = (int)Math.floor(cursorCoordX);
     int z = (int)Math.floor(cursorCoordZ);
     int y = this.persistentMap.getHeightAt(x, z);
     Waypoint hovered = getHovered(cursorCoordX, cursorCoordZ);
     this.editClicked = false;
     this.addClicked = false;
     this.deleteClicked = false;
     double dimensionScale = this.mc.field_1724.field_6002.method_8597().comp_646();
     switch (action) {
       case 0:
         if (hovered != null) {
           x = hovered.getX();
           z = hovered.getZ();
         } 
         
         this.addClicked = true;
 
 
         
         if (this.waypointManager.getWaypoints().size() == 0) {
           r = 0.0F;
           g = 1.0F;
           b = 0.0F;
         } else {
           r = this.generator.nextFloat();
           g = this.generator.nextFloat();
           b = this.generator.nextFloat();
         } 
         
         dimensions = new TreeSet();
         dimensions.add(AbstractVoxelMap.getInstance().getDimensionManager().getDimensionContainerByWorld((class_1937)this.mc.field_1687));
         this.newWaypoint = new Waypoint("", (int)(x * dimensionScale), (int)(z * dimensionScale), y, true, r, g, b, "", this.master.getWaypointManager().getCurrentSubworldDescriptor(false), dimensions);
         this.mc.method_1507((class_437)new GuiAddWaypoint(this, this.master, this.newWaypoint, false));
         return;
       case 1:
         if (hovered != null) {
           this.waypointManager.setHighlightedWaypoint(hovered, true);
         } else {
           y = (y > 0) ? y : 64;
           TreeSet<DimensionContainer> dimensions2 = new TreeSet();
           dimensions2.add(AbstractVoxelMap.getInstance().getDimensionManager().getDimensionContainerByWorld((class_1937)this.mc.field_1687));
           Waypoint fakePoint = new Waypoint("", (int)(x * dimensionScale), (int)(z * dimensionScale), y, true, 1.0F, 0.0F, 0.0F, "", this.master.getWaypointManager().getCurrentSubworldDescriptor(false), dimensions2);
           this.waypointManager.setHighlightedWaypoint(fakePoint, true);
         } 
         return;
       case 2:
         if (hovered != null) {
           CommandUtils.sendWaypoint(hovered);
         } else {
           y = (y > 0) ? y : 64;
           CommandUtils.sendCoordinate(x, y, z);
         } 
         return;
       case 3:
         if (hovered != null) {
           this.selectedWaypoint = hovered;
           boolean mp = !this.mc.method_1542();
           y = (this.selectedWaypoint.getY() > (class_310.method_1551()).field_1687.method_31607()) ? this.selectedWaypoint.getY() : (!this.mc.field_1724.field_6002.method_8597().comp_643() ? (class_310.method_1551()).field_1687.method_31600() : 64);
           this.mc.field_1724.method_3142("/tp " + this.mc.field_1724.method_5477().getString() + " " + this.selectedWaypoint.getX() + " " + y + " " + this.selectedWaypoint.getZ());
           if (mp) {
             this.mc.field_1724.method_3142("/tppos " + this.selectedWaypoint.getX() + " " + y + " " + this.selectedWaypoint.getZ());
           } else {
             getMinecraft().method_1507((class_437)null);
           } 
         } else {
           if (y == 0) {
             y = !this.mc.field_1724.field_6002.method_8597().comp_643() ? (class_310.method_1551()).field_1687.method_31600() : 64;
           }
           
           this.mc.field_1724.method_3142("/tp " + this.mc.field_1724.method_5477().getString() + " " + x + " " + y + " " + z);
           if (!this.mc.method_1542()) {
             this.mc.field_1724.method_3142("/tppos " + x + " " + y + " " + z);
           }
         } 
         return;
       case 4:
         if (hovered != null) {
           this.editClicked = true;
           this.selectedWaypoint = hovered;
           this.mc.method_1507((class_437)new GuiAddWaypoint(this, this.master, hovered, true));
         } 
         return;
       case 5:
         if (hovered != null) {
           this.deleteClicked = true;
           this.selectedWaypoint = hovered;
           class_5250 class_52501 = class_2561.method_43471("minimap.waypoints.deleteconfirm");
           class_5250 class_52502 = class_2561.method_43469("selectServer.deleteWarning", new Object[] { this.selectedWaypoint.name });
           class_5250 class_52503 = class_2561.method_43471("selectServer.deleteButton");
           class_5250 class_52504 = class_2561.method_43471("gui.cancel");
           class_410 confirmScreen = new class_410((BooleanConsumer)this, (class_2561)class_52501, (class_2561)class_52502, (class_2561)class_52503, (class_2561)class_52504);
           getMinecraft().method_1507((class_437)confirmScreen);
         } 
         return;
     } 
     System.out.println("unimplemented command");
   }
 
 
 
   
   public boolean isEditing() {
     return this.editClicked;
   }
   
   public void accept(boolean confirm) {
     if (this.deleteClicked) {
       this.deleteClicked = false;
       if (confirm) {
         this.waypointManager.deleteWaypoint(this.selectedWaypoint);
         this.selectedWaypoint = null;
       } 
     } 
     
     if (this.editClicked) {
       this.editClicked = false;
       if (confirm) {
         this.waypointManager.saveWaypoints();
       }
     } 
     
     if (this.addClicked) {
       this.addClicked = false;
       if (confirm) {
         this.waypointManager.addWaypoint(this.newWaypoint);
       }
     } 
     
     getMinecraft().method_1507((class_437)this);
   }
   
   public boolean canTeleport() {
     boolean allowed = false;
     boolean singlePlayer = this.mc.method_1542();
     if (singlePlayer) {
       try {
         allowed = this.mc.method_1576().method_3760().method_14569(this.mc.field_1724.method_7334());
       } catch (Exception var4) {
         allowed = this.mc.method_1576().method_27728().method_194();
       } 
     } else {
       allowed = true;
     } 
     
     return allowed;
   }
   
   private int chkLen(String string) {
     return getFontRenderer().method_1727(string);
   }
   
   private void write(class_4587 matrixStack, String string, float x, float y, int color) {
     getFontRenderer().method_1720(matrixStack, string, x, y, color);
   }
 }


