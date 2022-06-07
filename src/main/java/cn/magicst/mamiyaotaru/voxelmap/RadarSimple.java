 package cn.magicst.mamiyaotaru.voxelmap;
 
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IRadar;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IVoxelMap;
 import cn.magicst.mamiyaotaru.voxelmap.textures.Sprite;
 import cn.magicst.mamiyaotaru.voxelmap.textures.TextureAtlas;
 import cn.magicst.mamiyaotaru.voxelmap.util.Contact;
 import cn.magicst.mamiyaotaru.voxelmap.util.EnumMobs;
 import cn.magicst.mamiyaotaru.voxelmap.util.GLShim;
 import cn.magicst.mamiyaotaru.voxelmap.util.GLUtils;
 import cn.magicst.mamiyaotaru.voxelmap.util.GameVariableAccessShim;
 import cn.magicst.mamiyaotaru.voxelmap.util.ImageUtils;
 import cn.magicst.mamiyaotaru.voxelmap.util.LayoutVariables;
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.awt.image.BufferedImage;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.UUID;
 import net.minecraft.class_1160;
 import net.minecraft.class_1297;
 import net.minecraft.class_1321;
 import net.minecraft.class_1456;
 import net.minecraft.class_1463;
 import net.minecraft.class_1493;
 import net.minecraft.class_1590;
 import net.minecraft.class_1657;
 import net.minecraft.class_2960;
 import net.minecraft.class_310;
 import net.minecraft.class_3300;
 import net.minecraft.class_4466;
 import net.minecraft.class_4587;
 import org.apache.logging.log4j.Level;
 import org.apache.logging.log4j.LogManager;
 import org.apache.logging.log4j.Logger;
 
 
 public class RadarSimple
   implements IRadar
 {
   private class_310 game;
   private LayoutVariables layoutVariables = null;
   public MapSettingsManager minimapOptions = null;
   public RadarSettingsManager options = null;
   private TextureAtlas textureAtlas;
   private boolean enabled = true;
   private boolean completedLoading = false;
   private int timer = 500;
   private float direction = 0.0F;
   private ArrayList<Contact> contacts = new ArrayList<>(40);
   UUID devUUID = UUID.fromString("9b37abb9-2487-4712-bb96-21a1e0b2023c");
   
   private final Logger logger = LogManager.getLogger();
   
   public RadarSimple(IVoxelMap master) {
     this.minimapOptions = master.getMapOptions();
     this.options = master.getRadarOptions();
     this.game = class_310.method_1551();
     this.textureAtlas = new TextureAtlas("pings");
     this.textureAtlas.method_4527(false, false);
   }
 
   
   public void onResourceManagerReload(class_3300 resourceManager) {
     loadTexturePackIcons();
   }
   
   private void loadTexturePackIcons() {
     this.completedLoading = false;
     
     try {
       this.textureAtlas.reset();
       BufferedImage contact = ImageUtils.loadImage(new class_2960("voxelmap", "images/radar/contact.png"), 0, 0, 32, 32, 32, 32);
       contact = ImageUtils.fillOutline(contact, false, true, 32.0F, 32.0F, 0);
       this.textureAtlas.registerIconForBufferedImage("contact", contact);
       BufferedImage facing = ImageUtils.loadImage(new class_2960("voxelmap", "images/radar/contact_facing.png"), 0, 0, 32, 32, 32, 32);
       facing = ImageUtils.fillOutline(facing, false, true, 32.0F, 32.0F, 0);
       this.textureAtlas.registerIconForBufferedImage("facing", facing);
       BufferedImage glow = ImageUtils.loadImage(new class_2960("voxelmap", "images/radar/glow.png"), 0, 0, 16, 16, 16, 16);
       glow = ImageUtils.fillOutline(glow, false, true, 16.0F, 16.0F, 0);
       this.textureAtlas.registerIconForBufferedImage("glow", glow);
       this.textureAtlas.stitch();
       this.completedLoading = true;
     } catch (Exception var4) {
       System.err.println("Failed getting mobs " + var4.getLocalizedMessage());
       var4.printStackTrace();
     } 
   }
 
 
   
   public void onTickInGame(class_4587 matrixStack, class_310 mc, LayoutVariables layoutVariables) {
     if (this.options.radarAllowed.booleanValue() || this.options.radarMobsAllowed.booleanValue() || this.options.radarPlayersAllowed.booleanValue()) {
       if (this.game == null) {
         this.game = mc;
       }
       
       this.layoutVariables = layoutVariables;
       if (this.options.isChanged()) {
         this.timer = 500;
       }
       
       this.direction = GameVariableAccessShim.rotationYaw() + 180.0F;
       
       while (this.direction >= 360.0F) {
         this.direction -= 360.0F;
       }
       
       while (this.direction < 0.0F) {
         this.direction += 360.0F;
       }
       
       if (this.enabled) {
         if (this.completedLoading && this.timer > 95) {
           calculateMobs();
           this.timer = 0;
         } 
         
         this.timer++;
         if (this.completedLoading) {
           renderMapMobs(matrixStack, this.layoutVariables.mapX, this.layoutVariables.mapY);
         }
         
         GLShim.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
       } 
     } 
   }
 
   
   public void calculateMobs() {
     this.contacts.clear();
     
     for (class_1297 entity : this.game.field_1687.method_18112()) {
       try {
         if (entity != null && !entity.method_5756((class_1657)this.game.field_1724) && ((this.options.showHostiles && (this.options.radarAllowed.booleanValue() || this.options.radarMobsAllowed.booleanValue()) && isHostile(entity)) || (this.options.showPlayers && (this.options.radarAllowed.booleanValue() || this.options.radarPlayersAllowed.booleanValue()) && isPlayer(entity)) || (this.options.showNeutrals && this.options.radarMobsAllowed.booleanValue() && isNeutral(entity)))) {
           int wayX = GameVariableAccessShim.xCoord() - (int)entity.method_19538().method_10216();
           int wayZ = GameVariableAccessShim.zCoord() - (int)entity.method_19538().method_10215();
           int wayY = GameVariableAccessShim.yCoord() - (int)entity.method_19538().method_10214();
           double hypot = (wayX * wayX + wayZ * wayZ + wayY * wayY);
           hypot /= this.layoutVariables.zoomScaleAdjusted * this.layoutVariables.zoomScaleAdjusted;
           if (hypot < 961.0D) {
             Contact contact = new Contact(entity, getUnknownMobNeutrality(entity));
             String unscrubbedName = contact.entity.method_5476().getString();
             contact.setName(unscrubbedName);
             contact.updateLocation();
             this.contacts.add(contact);
           } 
         } 
       } catch (Exception var11) {
         System.err.println(var11.getLocalizedMessage());
         var11.printStackTrace();
       } 
     } 
     
     Collections.sort(this.contacts, new Comparator<Contact>() {
           public int compare(Contact contact1, Contact contact2) {
             return contact1.y - contact2.y;
           }
         });
   }
   
   private EnumMobs getUnknownMobNeutrality(class_1297 entity) {
     if (isHostile(entity)) {
       return EnumMobs.GENERICHOSTILE;
     }
     return (!(entity instanceof class_1321) || !((class_1321)entity).method_6181() || (!this.game.method_1496() && !((class_1321)entity).method_6177().equals(this.game.field_1724))) ? EnumMobs.GENERICNEUTRAL : EnumMobs.GENERICTAME;
   }
 
   
   private boolean isHostile(class_1297 entity) {
     if (entity instanceof class_1590) {
       class_1590 zombifiedPiglinEntity = (class_1590)entity;
       return zombifiedPiglinEntity.method_7076((class_1657)this.game.field_1724);
     }  if (entity instanceof net.minecraft.class_1569)
       return true; 
     if (entity instanceof class_4466) {
       class_4466 beeEntity = (class_4466)entity;
       return beeEntity.method_29511();
     } 
     if (entity instanceof class_1456) {
       class_1456 polarBearEntity = (class_1456)entity;
       
       for (Object object : polarBearEntity.field_6002.method_18467(class_1456.class, polarBearEntity.method_5829().method_1009(8.0D, 4.0D, 8.0D))) {
         if (((class_1456)object).method_6109()) {
           return true;
         }
       } 
     } 
     
     if (entity instanceof class_1463) {
       class_1463 rabbitEntity = (class_1463)entity;
       return (rabbitEntity.method_6610() == 99);
     }  if (entity instanceof class_1493) {
       class_1493 wolfEntity = (class_1493)entity;
       return wolfEntity.method_29511();
     } 
     return false;
   }
 
 
   
   private boolean isPlayer(class_1297 entity) {
     return entity instanceof net.minecraft.class_745;
   }
   
   private boolean isNeutral(class_1297 entity) {
     if (!(entity instanceof net.minecraft.class_1309)) {
       return false;
     }
     return (!(entity instanceof class_1657) && !isHostile(entity));
   }
 
   
   public void renderMapMobs(class_4587 matrixStack, int x, int y) {
     double max = this.layoutVariables.zoomScaleAdjusted * 32.0D;
     GLUtils.disp2(this.textureAtlas.method_4624());
     
     for (Contact contact : this.contacts) {
       contact.updateLocation();
       double contactX = contact.x;
       double contactZ = contact.z;
       int contactY = contact.y;
       double wayX = GameVariableAccessShim.xCoordDouble() - contactX;
       double wayZ = GameVariableAccessShim.zCoordDouble() - contactZ;
       int wayY = GameVariableAccessShim.yCoord() - contactY;
       double adjustedDiff = max - Math.max(Math.abs(wayY) - 0, 0);
       contact.brightness = (float)Math.max(adjustedDiff / max, 0.0D);
       contact.brightness *= contact.brightness;
       contact.angle = (float)Math.toDegrees(Math.atan2(wayX, wayZ));
       contact.distance = Math.sqrt(wayX * wayX + wayZ * wayZ) / this.layoutVariables.zoomScaleAdjusted;
       GLShim.glBlendFunc(770, 771);
       if (wayY < 0) {
         GLShim.glColor4f(1.0F, 1.0F, 1.0F, contact.brightness);
       } else {
         GLShim.glColor3f(1.0F * contact.brightness, 1.0F * contact.brightness, 1.0F * contact.brightness);
       } 
       
       if (this.minimapOptions.rotates) {
         contact.angle += this.direction;
       } else if (this.minimapOptions.oldNorth) {
         contact.angle -= 90.0F;
       } 
       
       boolean inRange = false;
       if (!this.minimapOptions.squareMap) {
         inRange = (contact.distance < 31.0D);
       } else {
         double radLocate = Math.toRadians(contact.angle);
         double dispX = contact.distance * Math.cos(radLocate);
         double dispY = contact.distance * Math.sin(radLocate);
         inRange = (Math.abs(dispX) <= 28.5D && Math.abs(dispY) <= 28.5D);
       } 
       
       if (inRange) {
         try {
           matrixStack.method_22903();
           float contactFacing = contact.entity.method_5791();
           if (this.minimapOptions.rotates) {
             contactFacing -= this.direction;
           } else if (this.minimapOptions.oldNorth) {
             contactFacing += 90.0F;
           } 
           
           matrixStack.method_22904(x, y, 0.0D);
           matrixStack.method_22907(class_1160.field_20707.method_23214(-contact.angle));
           matrixStack.method_22904(0.0D, -contact.distance, 0.0D);
           matrixStack.method_22907(class_1160.field_20707.method_23214(contact.angle + contactFacing));
           matrixStack.method_22904(-x, -y, 0.0D);
           RenderSystem.applyModelViewMatrix();
           if (contact.uuid != null && contact.uuid.equals(this.devUUID)) {
             Sprite icon = this.textureAtlas.getAtlasSprite("glow");
             applyFilteringParameters();
             GLUtils.drawPre();
             GLUtils.setMap(icon, x, y, (int)(icon.getIconWidth() / 2.0F));
             GLUtils.drawPost();
           } 
           
           applyFilteringParameters();
           GLUtils.drawPre();
           GLUtils.setMap(this.textureAtlas.getAtlasSprite("contact"), x, y, 16.0F);
           GLUtils.drawPost();
           if (this.options.showFacing) {
             applyFilteringParameters();
             GLUtils.drawPre();
             GLUtils.setMap(this.textureAtlas.getAtlasSprite("facing"), x, y, 16.0F);
             GLUtils.drawPost();
           } 
         } catch (Exception e) {
           System.err.println("Error rendering mob icon! " + e.getLocalizedMessage() + " contact type " + contact.type);
           this.logger.log(Level.ERROR, e);
         } finally {
           matrixStack.method_22909();
           RenderSystem.applyModelViewMatrix();
         } 
       }
     } 
   }
 
   
   private void applyFilteringParameters() {
     if (this.options.filtering) {
       GLShim.glTexParameteri(3553, 10241, 9729);
       GLShim.glTexParameteri(3553, 10240, 9729);
       GLShim.glTexParameteri(3553, 10242, 10496);
       GLShim.glTexParameteri(3553, 10243, 10496);
     } else {
       GLShim.glTexParameteri(3553, 10241, 9728);
       GLShim.glTexParameteri(3553, 10240, 9728);
     } 
   }
 }
