 package cn.magicst.mamiyaotaru.voxelmap.textures;
 
 import com.google.common.collect.Maps;
 import cn.magicst.mamiyaotaru.voxelmap.util.GLShim;
 import cn.magicst.mamiyaotaru.voxelmap.util.ImageUtils;
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.awt.image.BufferedImage;
 import java.io.IOException;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import net.minecraft.class_1044;
 import net.minecraft.class_128;
 import net.minecraft.class_129;
 import net.minecraft.class_148;
 import net.minecraft.class_2960;
 import net.minecraft.class_3298;
 import net.minecraft.class_3300;
 import org.apache.logging.log4j.LogManager;
 import org.apache.logging.log4j.Logger;
 
 public class TextureAtlas
   extends class_1044 {
   private static final Logger logger = LogManager.getLogger();
   private final HashMap<String, Sprite> mapRegisteredSprites;
   private final HashMap<String, Sprite> mapUploadedSprites;
   private final String basePath;
   private final IIconCreator iconCreator;
   private final int mipmapLevels = 0;
   private final Sprite missingImage;
   private final Sprite failedImage;
   private Stitcher stitcher;
   
   public TextureAtlas(String basePath) {
     this(basePath, (IIconCreator)null);
   }
   
   public TextureAtlas(String basePath, IIconCreator iconCreator) {
     this.mapRegisteredSprites = Maps.newHashMap();
     this.mapUploadedSprites = Maps.newHashMap();
     this.missingImage = new Sprite("missingno");
     this.failedImage = new Sprite("notfound");
     this.basePath = basePath;
     this.iconCreator = iconCreator;
   }
   
   private void initMissingImage() {
     int[] missingTextureData = new int[1];
     Arrays.fill(missingTextureData, 0);
     this.missingImage.setIconWidth(1);
     this.missingImage.setIconHeight(1);
     this.missingImage.setTextureData(missingTextureData);
     this.failedImage.copyFrom(this.missingImage);
     this.failedImage.setTextureData(missingTextureData);
   }
   
   public void method_4625(class_3300 resourceManager) throws IOException {
     if (this.iconCreator != null) {
       loadTextureAtlas(this.iconCreator);
     }
   }
 
   
   public void reset() {
     this.mapRegisteredSprites.clear();
     this.mapUploadedSprites.clear();
     initMissingImage();
     int glMaxTextureSize = RenderSystem.maxSupportedTextureSize();
     this.stitcher = new Stitcher(glMaxTextureSize, glMaxTextureSize, 0);
   }
   
   public void loadTextureAtlas(IIconCreator iconCreator) {
     reset();
     iconCreator.addIcons(this);
     stitch();
   }
   
   public void stitch() {
     for (Map.Entry<String, Sprite> entry : this.mapRegisteredSprites.entrySet()) {
       Sprite icon = (Sprite)entry.getValue();
       this.stitcher.addSprite(icon);
     } 
     
     try {
       this.stitcher.doStitch();
     } catch (StitcherException var11) {
       throw var11;
     } 
     
     logger.info("Created: {}x{} {}-atlas", new Object[] { Integer.valueOf(this.stitcher.getCurrentImageWidth()), Integer.valueOf(this.stitcher.getCurrentImageHeight()), this.basePath });
     TextureUtilLegacy.allocateTextureImpl(method_4624(), 0, this.stitcher.getCurrentImageWidth(), this.stitcher.getCurrentImageHeight());
     int[] zeros = new int[this.stitcher.getCurrentImageWidth() * this.stitcher.getCurrentImageHeight()];
     Arrays.fill(zeros, 0);
     TextureUtilLegacy.uploadTexture(method_4624(), zeros, this.stitcher.getCurrentImageWidth(), this.stitcher.getCurrentImageHeight());
     HashMap<String, Sprite> tempMapRegisteredSprites = Maps.newHashMap(this.mapRegisteredSprites);
     
     for (Sprite icon : this.stitcher.getStitchSlots()) {
       String iconName = icon.getIconName();
       tempMapRegisteredSprites.remove(iconName);
       this.mapUploadedSprites.put(iconName, icon);
       
       try {
         TextureUtilLegacy.uploadTextureMipmap(new int[][] { icon.getTextureData() }, icon.getIconWidth(), icon.getIconHeight(), icon.getOriginX(), icon.getOriginY(), false, false);
       } catch (Throwable var10) {
         class_128 crashReport = class_128.method_560(var10, "Stitching texture atlas");
         class_129 crashReportCategory = crashReport.method_562("Texture being stitched together");
         crashReportCategory.method_578("Atlas path", this.basePath);
         crashReportCategory.method_578("Sprite", icon);
         throw new class_148(crashReport);
       } 
     } 
     
     for (Sprite icon : tempMapRegisteredSprites.values()) {
       icon.copyFrom(this.missingImage);
     }
     
     this.mapRegisteredSprites.clear();
     this.missingImage.initSprite(getHeight(), getWidth(), 0, 0);
     this.failedImage.initSprite(getHeight(), getWidth(), 0, 0);
     ImageUtils.saveImage(this.basePath.replaceAll("/", "_"), method_4624(), 0, this.stitcher.getCurrentImageWidth(), this.stitcher.getCurrentImageHeight());
   }
   
   public void stitchNew() {
     for (Map.Entry<String, Sprite> entry : this.mapRegisteredSprites.entrySet()) {
       Sprite icon = (Sprite)entry.getValue();
       this.stitcher.addSprite(icon);
     } 
     
     int oldWidth = this.stitcher.getCurrentImageWidth();
     int oldHeight = this.stitcher.getCurrentImageHeight();
     
     try {
       this.stitcher.doStitchNew();
     } catch (StitcherException var12) {
       throw var12;
     } 
     
     if (oldWidth == this.stitcher.getCurrentImageWidth() && oldHeight == this.stitcher.getCurrentImageHeight()) {
       GLShim.glBindTexture(3553, this.field_5204);
     } else {
       logger.info("Resized to: {}x{} {}-atlas", new Object[] { Integer.valueOf(this.stitcher.getCurrentImageWidth()), Integer.valueOf(this.stitcher.getCurrentImageHeight()), this.basePath });
       TextureUtilLegacy.allocateTextureImpl(method_4624(), 0, this.stitcher.getCurrentImageWidth(), this.stitcher.getCurrentImageHeight());
       int[] zeros = new int[this.stitcher.getCurrentImageWidth() * this.stitcher.getCurrentImageHeight()];
       Arrays.fill(zeros, 0);
       TextureUtilLegacy.uploadTexture(method_4624(), zeros, this.stitcher.getCurrentImageWidth(), this.stitcher.getCurrentImageHeight());
     } 
     
     HashMap<String, Sprite> tempMapRegisteredSprites = Maps.newHashMap(this.mapRegisteredSprites);
     
     for (Sprite icon : this.stitcher.getStitchSlots()) {
       String iconName = icon.getIconName();
       tempMapRegisteredSprites.remove(iconName);
       this.mapUploadedSprites.put(iconName, icon);
       
       try {
         TextureUtilLegacy.uploadTextureMipmap(new int[][] { icon.getTextureData() }, icon.getIconWidth(), icon.getIconHeight(), icon.getOriginX(), icon.getOriginY(), false, false);
       } catch (Throwable var11) {
         class_128 crashReport = class_128.method_560(var11, "Stitching texture atlas");
         class_129 crashReportCategory = crashReport.method_562("Texture being stitched together");
         crashReportCategory.method_578("Atlas path", this.basePath);
         crashReportCategory.method_578("Sprite", icon);
         throw new class_148(crashReport);
       } 
     } 
     
     for (Sprite icon : tempMapRegisteredSprites.values()) {
       icon.copyFrom(this.missingImage);
     }
     
     this.mapRegisteredSprites.clear();
     this.missingImage.initSprite(getHeight(), getWidth(), 0, 0);
     this.failedImage.initSprite(getHeight(), getWidth(), 0, 0);
     if (oldWidth != this.stitcher.getCurrentImageWidth() || oldHeight != this.stitcher.getCurrentImageHeight()) {
       ImageUtils.saveImage(this.basePath.replaceAll("/", "_"), method_4624(), 0, this.stitcher.getCurrentImageWidth(), this.stitcher.getCurrentImageHeight());
     }
   }
 
   
   public Sprite getIconAt(float x, float y) {
     Iterator<Map.Entry> uploadedSpritesEntriesIterator = this.mapUploadedSprites.entrySet().iterator();
     
     while (uploadedSpritesEntriesIterator.hasNext()) {
       Sprite icon = (Sprite)((Map.Entry)uploadedSpritesEntriesIterator.next()).getValue();
       if (x >= icon.originX && x < (icon.originX + icon.width) && y >= icon.originY && y < (icon.originY + icon.height)) {
         return icon;
       }
     } 
     
     return this.missingImage;
   }
   
   public Sprite getAtlasSprite(String name) {
     Sprite icon = this.mapUploadedSprites.get(name);
     if (icon == null) {
       icon = this.missingImage;
     }
     
     return icon;
   }
   
   public Sprite getAtlasSpriteIncludingYetToBeStitched(String name) {
     Sprite icon = this.mapUploadedSprites.get(name);
     if (icon == null) {
       icon = this.mapRegisteredSprites.get(name);
     }
     
     if (icon == null) {
       icon = this.missingImage;
     }
     
     return icon;
   }
   
   public Sprite registerIconForResource(class_2960 resourceLocation, class_3300 resourceManager) {
     if (resourceLocation == null) {
       throw new IllegalArgumentException("Location cannot be null!");
     }
     Sprite icon = this.mapRegisteredSprites.get(resourceLocation.toString());
     if (icon == null) {
       icon = Sprite.spriteFromResourceLocation(resourceLocation);
       
       try {
         class_3298 entryResource = resourceManager.method_14486(resourceLocation);
         BufferedImage entryBufferedImage = TextureUtilLegacy.readBufferedImage(entryResource.method_14482());
         icon.bufferedImageToIntData(entryBufferedImage);
         entryBufferedImage.flush();
       } catch (RuntimeException var6) {
         logger.error("Unable to parse metadata from " + resourceLocation, var6);
       } catch (IOException var7) {
         logger.error("Using missing texture, unable to load " + resourceLocation, var7);
       } 
       
       this.mapRegisteredSprites.put(resourceLocation.toString(), icon);
     } 
     
     return icon;
   }
 
   
   public Sprite registerIconForBufferedImage(String name, BufferedImage bufferedImage) {
     if (name != null && !name.equals("")) {
       Sprite icon = this.mapRegisteredSprites.get(name);
       if (icon == null) {
         icon = Sprite.spriteFromString(name);
         icon.bufferedImageToIntData(bufferedImage);
         bufferedImage.flush();
         
         for (Sprite existing : this.mapUploadedSprites.values()) {
           if (Arrays.equals(existing.imageData, icon.imageData)) {
             registerMaskedIcon(name, existing);
             return existing;
           } 
         } 
         
         for (Sprite existing : this.mapRegisteredSprites.values()) {
           if (Arrays.equals(existing.imageData, icon.imageData)) {
             registerMaskedIcon(name, existing);
             return existing;
           } 
         } 
         
         this.mapRegisteredSprites.put(name, icon);
       } 
       
       return icon;
     } 
     throw new IllegalArgumentException("Name cannot be null!");
   }
 
   
   public void registerOrOverwriteSprite(String name, BufferedImage bufferedImage) {
     if (name != null && !name.equals("")) {
       Sprite icon = this.mapRegisteredSprites.get(name);
       if (icon != null) {
         icon.bufferedImageToIntData(bufferedImage);
       } else {
         icon = getAtlasSprite(name);
         if (icon != null) {
           icon.bufferedImageToIntData(bufferedImage);
           
           try {
             GLShim.glBindTexture(3553, this.field_5204);
             TextureUtilLegacy.uploadTextureMipmap(new int[][] { icon.getTextureData() }, icon.getIconWidth(), icon.getIconHeight(), icon.getOriginX(), icon.getOriginY(), false, false);
           } catch (Throwable var7) {
             class_128 crashReport = class_128.method_560(var7, "Stitching texture atlas");
             class_129 crashReportCategory = crashReport.method_562("Texture being stitched together");
             crashReportCategory.method_578("Atlas path", this.basePath);
             crashReportCategory.method_578("Sprite", icon);
             throw new class_148(crashReport);
           } 
         } 
       } 
       
       bufferedImage.flush();
     } else {
       throw new IllegalArgumentException("Name cannot be null!");
     } 
   }
   
   public Sprite getMissingImage() {
     return this.missingImage;
   }
   
   public Sprite getFailedImage() {
     return this.failedImage;
   }
   
   public void registerFailedIcon(String name) {
     this.mapUploadedSprites.put(name, this.failedImage);
   }
   
   public void registerMaskedIcon(String name, Sprite originalIcon) {
     Sprite existingIcon = this.mapUploadedSprites.get(name);
     if (existingIcon == null) {
       existingIcon = this.mapRegisteredSprites.get(name);
     }
     
     if (existingIcon == null) {
       this.mapUploadedSprites.put(name, originalIcon);
     }
   }
 
   
   public int getWidth() {
     return this.stitcher.getCurrentWidth();
   }
   
   public int getHeight() {
     return this.stitcher.getCurrentHeight();
   }
   
   public int getImageWidth() {
     return this.stitcher.getCurrentImageWidth();
   }
   
   public int getImageHeight() {
     return this.stitcher.getCurrentImageHeight();
   }
 }
