 package cn.magicst.mamiyaotaru.voxelmap.util;
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.awt.Color;
 import java.awt.Font;
 import java.awt.FontMetrics;
 import java.awt.Graphics;
 import java.awt.Graphics2D;
 import java.awt.RenderingHints;
 import java.awt.geom.AffineTransform;
 import java.awt.image.AffineTransformOp;
 import java.awt.image.BufferedImage;
 import java.awt.image.ImageObserver;
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.nio.ByteBuffer;
 import java.nio.ByteOrder;
 import java.nio.IntBuffer;
 import javax.imageio.ImageIO;
 import net.minecraft.class_1159;
 import net.minecraft.class_2960;
 import net.minecraft.class_310;
 import net.minecraft.class_4587;
 import org.apache.logging.log4j.LogManager;
 import org.apache.logging.log4j.Logger;
 
 public class ImageUtils {
   public static void saveImage(String name, int glid, int maxMipmapLevel, int width, int height) {
     Logger logger = LogManager.getLogger();
     GLShim.glBindTexture(3553, glid);
     GLShim.glPixelStorei(3333, 1);
     GLShim.glPixelStorei(3317, 1);
     
     for (int mipmapLevel = 0; mipmapLevel <= maxMipmapLevel; mipmapLevel++) {
       File file = new File(name + "_" + name + ".png");
       int destWidth = width >> mipmapLevel;
       int destHeight = height >> mipmapLevel;
       int numPixels = destWidth * destHeight;
       IntBuffer pixelBuffer = BufferUtils.createIntBuffer(numPixels);
       int[] pixelArray = new int[numPixels];
       GLShim.glGetTexImage(3553, mipmapLevel, 32993, 33639, pixelBuffer);
       pixelBuffer.get(pixelArray);
       BufferedImage bufferedImage = new BufferedImage(destWidth, destHeight, 2);
       bufferedImage.setRGB(0, 0, destWidth, destHeight, pixelArray, 0, destWidth);
       
       try {
         ImageIO.write(bufferedImage, "png", file);
         logger.debug("Exported png to: {}", new Object[] { file.getAbsolutePath() });
       } catch (IOException var15) {
         logger.debug("Unable to write: ", var15);
       } 
     } 
   }
 
   
   public static BufferedImage validateImage(BufferedImage image) {
     if (image.getType() != 6) {
       BufferedImage temp = new BufferedImage(image.getWidth(), image.getHeight(), 6);
       Graphics2D g2 = temp.createGraphics();
       g2.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), (ImageObserver)null);
       g2.dispose();
       image = temp;
     } 
     
     return image;
   }
   
   public static BufferedImage createBufferedImageFromResourceLocation(class_2960 resourceLocation) {
     try {
       InputStream is = class_310.method_1551().method_1478().method_14486(resourceLocation).method_14482();
       BufferedImage image = ImageIO.read(is);
       is.close();
       if (image.getType() != 6) {
         BufferedImage temp = new BufferedImage(image.getWidth(), image.getHeight(), 6);
         Graphics2D g2 = temp.createGraphics();
         g2.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), (ImageObserver)null);
         g2.dispose();
         image = temp;
       } 
       
       return image;
     } catch (Exception var5) {
       return null;
     } 
   }
   
   public static BufferedImage createBufferedImageFromGLID(int id) {
     GLShim.glBindTexture(3553, id);
     GL11.glBindTexture(3553, id);
     return createBufferedImageFromCurrentGLImage();
   }
   public static BufferedImage createBufferedImageFromCurrentGLImage() {
     BufferedImage image;
     int imageWidth = GLShim.glGetTexLevelParameteri(3553, 0, 4096);
     int imageHeight = GLShim.glGetTexLevelParameteri(3553, 0, 4097);
     long size = imageWidth * imageHeight * 4L;
     
     if (size < 2147483647L) {
       image = new BufferedImage(imageWidth, imageHeight, 6);
       ByteBuffer byteBuffer = ByteBuffer.allocateDirect(imageWidth * imageHeight * 4).order(ByteOrder.nativeOrder());
       GLShim.glGetTexImage(3553, 0, 6408, 5121, byteBuffer);
       byteBuffer.position(0);
       byte[] bytes = new byte[byteBuffer.remaining()];
       byteBuffer.get(bytes);
       for (int x = 0; x < imageWidth; x++) {
         for (int y = 0; y < imageHeight; y++) {
           int index = y * imageWidth * 4 + x * 4;
           byte var8 = 0;
           int color24 = var8 | bytes[index + 2] & 0xFF;
           color24 |= (bytes[index + 1] & 0xFF) << 8;
           color24 |= (bytes[index] & 0xFF) << 16;
           color24 |= (bytes[index + 3] & 0xFF) << 24;
           image.setRGB(x, y, color24);
         } 
       } 
     } else {
       while (size > 2147483647L) {
         imageWidth /= 2;
         imageHeight /= 2;
         size = imageWidth * imageHeight * 4L;
       } 
       int glid = GLShim.glGetInteger(32873);
       image = new BufferedImage(imageWidth, imageHeight, 6);
       int fboWidth = 512;
       int fboHeight = 512;
       ByteBuffer byteBuffer = ByteBuffer.allocateDirect(fboWidth * fboHeight * 4).order(ByteOrder.nativeOrder());
       byte[] bytes = new byte[byteBuffer.remaining()];
       GLShim.glPushAttrib(4096);
       RenderSystem.backupProjectionMatrix();
       GLShim.glViewport(0, 0, fboWidth, fboHeight);
       class_1159 matrix4f = class_1159.method_4933(fboWidth, -fboHeight, 1000.0F, 3000.0F);
       RenderSystem.setProjectionMatrix(matrix4f);
       class_4587 matrixStack = RenderSystem.getModelViewStack();
       matrixStack.method_34426();
       matrixStack.method_22904(0.0D, 0.0D, -2000.0D);
       GLUtils.bindFrameBuffer(); int startX;
       for (startX = 0; startX + fboWidth < imageWidth; startX += fboWidth) {
         int startY; for (startY = 0; startY + fboWidth < imageHeight; startY += fboHeight) {
           GLUtils.disp(glid);
           GLShim.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
           GLShim.glClear(16640);
           GLUtils.drawPre();
           GLUtils.ldrawthree(0.0D, fboHeight, 1.0D, startX / imageWidth, startY / imageHeight);
           GLUtils.ldrawthree(fboWidth, fboHeight, 1.0D, (startX + fboWidth) / imageWidth, startY / imageHeight);
           GLUtils.ldrawthree(fboWidth, 0.0D, 1.0D, (startX + fboWidth) / imageWidth, (startY + fboHeight) / imageHeight);
           GLUtils.ldrawthree(0.0D, 0.0D, 1.0D, startX / imageWidth, (startY + fboHeight) / imageHeight);
           GLUtils.drawPost();
           GLUtils.disp(GLUtils.fboTextureID);
           byteBuffer.position(0);
           GLShim.glGetTexImage(3553, 0, 6408, 5121, byteBuffer);
           byteBuffer.position(0);
           byteBuffer.get(bytes);
           
           for (int x = 0; x < fboWidth && startX + x < imageWidth; x++) {
             for (int y = 0; y < fboHeight && startY + y < imageHeight; y++) {
               int index = y * fboWidth * 4 + x * 4;
               byte var8 = 0;
               int color24 = var8 | bytes[index + 2] & 0xFF;
               color24 |= (bytes[index + 1] & 0xFF) << 8;
               color24 |= (bytes[index] & 0xFF) << 16;
               color24 |= (bytes[index + 3] & 0xFF) << 24;
               image.setRGB(startX + x, startY + y, color24);
             } 
           } 
         } 
       } 
       
       GLUtils.unbindFrameBuffer();
       RenderSystem.restoreProjectionMatrix();
       GLShim.glPopAttrib();
       GLShim.glViewport(0, 0, class_310.method_1551().method_22683().method_4489(), class_310.method_1551().method_22683().method_4506());
     } 
     return image;
   }
   
   public static BufferedImage blankImage(class_2960 resourceLocation, int w, int h) {
     return blankImage(resourceLocation, w, h, 64, 32);
   }
   
   public static BufferedImage blankImage(class_2960 resourceLocation, int w, int h, int imageWidth, int imageHeight) {
     return blankImage(resourceLocation, w, h, imageWidth, imageHeight, 0, 0, 0, 0);
   }
   
   public static BufferedImage blankImage(class_2960 resourceLocation, int w, int h, int r, int g, int b, int a) {
     return blankImage(resourceLocation, w, h, 64, 32, r, g, b, a);
   }
   
   public static BufferedImage blankImage(class_2960 resourceLocation, int w, int h, int imageWidth, int imageHeight, int r, int g, int b, int a) {
     try {
       InputStream is = class_310.method_1551().method_1478().method_14486(resourceLocation).method_14482();
       BufferedImage mobSkin = ImageIO.read(is);
       is.close();
       BufferedImage temp = new BufferedImage(w * mobSkin.getWidth() / imageWidth, h * mobSkin.getWidth() / imageWidth, 6);
       Graphics2D g2 = temp.createGraphics();
       g2.setColor(new Color(r, g, b, a));
       g2.fillRect(0, 0, temp.getWidth(), temp.getHeight());
       g2.dispose();
       return temp;
     } catch (Exception var13) {
       System.err.println("Failed getting mob: " + resourceLocation.toString() + " - " + var13.getLocalizedMessage());
       var13.printStackTrace();
       return null;
     } 
   }
   
   public static BufferedImage blankImage(BufferedImage mobSkin, int w, int h) {
     return blankImage(mobSkin, w, h, 64, 32);
   }
   
   public static BufferedImage blankImage(BufferedImage mobSkin, int w, int h, int imageWidth, int imageHeight) {
     return blankImage(mobSkin, w, h, imageWidth, imageHeight, 0, 0, 0, 0);
   }
   
   public static BufferedImage blankImage(BufferedImage mobSkin, int w, int h, int r, int g, int b, int a) {
     return blankImage(mobSkin, w, h, 64, 32, r, g, b, a);
   }
   
   public static BufferedImage blankImage(BufferedImage mobSkin, int w, int h, int imageWidth, int imageHeight, int r, int g, int b, int a) {
     BufferedImage temp = new BufferedImage(w * mobSkin.getWidth() / imageWidth, h * mobSkin.getWidth() / imageWidth, 6);
     Graphics2D g2 = temp.createGraphics();
     g2.setColor(new Color(r, g, b, a));
     g2.fillRect(0, 0, temp.getWidth(), temp.getHeight());
     g2.dispose();
     return temp;
   }
   
   public static BufferedImage addCharacter(BufferedImage image, String character) {
     Graphics2D g2 = image.createGraphics();
     g2.setColor(new Color(0, 0, 0, 255));
     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
     g2.setFont(new Font("Arial", 0, image.getHeight()));
     FontMetrics fm = g2.getFontMetrics();
     int x = (image.getWidth() - fm.stringWidth("?")) / 2;
     int y = fm.getAscent() + (image.getHeight() - fm.getAscent() + fm.getDescent()) / 2;
     g2.drawString("?", x, y);
     g2.dispose();
     return image;
   }
   
   public static BufferedImage eraseArea(BufferedImage image, int x, int y, int w, int h, int imageWidth, int imageHeight) {
     float scaleX = (image.getWidth(null) / imageWidth);
     float scaleY = (image.getHeight(null) / imageHeight);
     x = (int)(x * scaleX);
     y = (int)(y * scaleY);
     w = (int)(w * scaleX);
     h = (int)(h * scaleY);
     int[] blankPixels = new int[w * h];
     Arrays.fill(blankPixels, 0);
     image.setRGB(x, y, w, h, blankPixels, 0, w);
     return image;
   }
   
   public static BufferedImage loadImage(class_2960 resourceLocation, int x, int y, int w, int h) {
     return loadImage(resourceLocation, x, y, w, h, 64, 32);
   }
   
   public static BufferedImage loadImage(class_2960 resourceLocation, int x, int y, int w, int h, int imageWidth, int imageHeight) {
     BufferedImage mobSkin = createBufferedImageFromResourceLocation(resourceLocation);
     if (mobSkin != null) {
       return loadImage(mobSkin, x, y, w, h, imageWidth, imageHeight);
     }
     System.err.println("Failed getting image: " + resourceLocation.toString());
     return null;
   }
 
   
   public static BufferedImage loadImage(BufferedImage mobSkin, int x, int y, int w, int h) {
     return loadImage(mobSkin, x, y, w, h, 64, 32);
   }
   
   public static BufferedImage loadImage(BufferedImage mobSkin, int x, int y, int w, int h, int imageWidth, int imageHeight) {
     float scale = (mobSkin.getWidth((ImageObserver)null) / imageWidth);
     x = (int)(x * scale);
     y = (int)(y * scale);
     w = (int)(w * scale);
     h = (int)(h * scale);
     w = Math.max(1, w);
     h = Math.max(1, h);
     x = Math.min(mobSkin.getWidth(null) - w, x);
     y = Math.min(mobSkin.getHeight(null) - h, y);
     return mobSkin.getSubimage(x, y, w, h);
   }
   
   public static BufferedImage addImages(BufferedImage base, BufferedImage overlay, float x, float y, int baseWidth, int baseHeight) {
     int scale = base.getWidth() / baseWidth;
     Graphics gfx = base.getGraphics();
     gfx.drawImage(overlay, (int)(x * scale), (int)(y * scale), (ImageObserver)null);
     gfx.dispose();
     return base;
   }
   
   public static BufferedImage scaleImage(BufferedImage image, float scaleBy) {
     if (scaleBy == 1.0F) {
       return image;
     }
     int type = image.getType();
     if (type == 13) {
       type = 6;
     }
     
     int newWidth = Math.max(1, (int)(image.getWidth() * scaleBy));
     int newHeight = Math.max(1, (int)(image.getHeight() * scaleBy));
     BufferedImage tmp = new BufferedImage(newWidth, newHeight, type);
     Graphics2D g2 = tmp.createGraphics();
     g2.drawImage(image, 0, 0, newWidth, newHeight, (ImageObserver)null);
     g2.dispose();
     return tmp;
   }
 
   
   public static BufferedImage scaleImage(BufferedImage image, float xScaleBy, float yScaleBy) {
     if (xScaleBy == 1.0F && yScaleBy == 1.0F) {
       return image;
     }
     int type = image.getType();
     if (type == 13) {
       type = 6;
     }
     
     int newWidth = Math.max(1, (int)(image.getWidth() * xScaleBy));
     int newHeight = Math.max(1, (int)(image.getHeight() * yScaleBy));
     BufferedImage tmp = new BufferedImage(newWidth, newHeight, type);
     Graphics2D g2 = tmp.createGraphics();
     g2.drawImage(image, 0, 0, newWidth, newHeight, (ImageObserver)null);
     g2.dispose();
     return tmp;
   }
 
   
   public static BufferedImage flipHorizontal(BufferedImage image) {
     AffineTransform tx = AffineTransform.getScaleInstance(-1.0D, 1.0D);
     tx.translate(-image.getWidth((ImageObserver)null), 0.0D);
     AffineTransformOp op = new AffineTransformOp(tx, 1);
     return op.filter(image, (BufferedImage)null);
   }
   
   public static BufferedImage into128(BufferedImage base) {
     BufferedImage frame = new BufferedImage(128, 128, base.getType());
     Graphics gfx = frame.getGraphics();
     gfx.drawImage(base, 64 - base.getWidth() / 2, 64 - base.getHeight() / 2, base.getWidth(), base.getHeight(), (ImageObserver)null);
     gfx.dispose();
     return frame;
   }
   
   public static BufferedImage intoSquare(BufferedImage base) {
     int dim = Math.max(base.getWidth(), base.getHeight());
     int t = 1;
     
     while (Math.pow(2.0D, (t - 1)) < dim) {
       t++;
     }
     
     int size = (int)Math.pow(2.0D, t);
     BufferedImage frame = new BufferedImage(size, size, base.getType());
     Graphics gfx = frame.getGraphics();
     gfx.drawImage(base, (size - base.getWidth()) / 2, (size - base.getHeight()) / 2, base.getWidth(), base.getHeight(), (ImageObserver)null);
     gfx.dispose();
     return frame;
   }
   
   public static BufferedImage pad(BufferedImage base) {
     int dim = Math.max(base.getWidth(), base.getHeight());
     int outlineWidth = 3;
     int size = dim + outlineWidth * 2;
     BufferedImage frame = new BufferedImage(size, size, base.getType());
     Graphics gfx = frame.getGraphics();
     gfx.drawImage(base, (size - base.getWidth()) / 2, (size - base.getHeight()) / 2, base.getWidth(), base.getHeight(), (ImageObserver)null);
     gfx.dispose();
     return frame;
   }
   
   public static BufferedImage fillOutline(BufferedImage image, boolean outline, int passes) {
     return fillOutline(image, outline, false, 0.0F, 0.0F, passes);
   }
   
   public static BufferedImage fillOutline(BufferedImage image, boolean outline, boolean armor, float intendedWidth, float intendedHeight, int passes) {
     if (outline) {
       for (int t = 0; t < passes; t++) {
         image = fillOutline(image, true, armor, intendedWidth, intendedHeight);
       }
     }
     
     return fillOutline(image, false, armor, intendedWidth, intendedHeight);
   }
   
   private static BufferedImage fillOutline(BufferedImage image, boolean solid, boolean armor, float intendedWidth, float intendedHeight) {
     float armorOutlineFractionHorizontal = intendedWidth / 2.0F - 1.0F;
     float armorOutlineFractionVertical = intendedHeight / 2.0F - 1.0F;
     BufferedImage temp = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
     Graphics gfx = temp.getGraphics();
     gfx.drawImage(image, 0, 0, (ImageObserver)null);
     gfx.dispose();
     int imageWidth = image.getWidth();
     int imageHeight = image.getHeight();
     
     for (int t = 0; t < image.getWidth(); t++) {
       for (int s = 0; s < image.getHeight(); s++) {
         int color = image.getRGB(t, s);
         if ((color >> 24 & 0xFF) == 0) {
           int newColor = sampleNonTransparentNeighborPixel(t, s, image);
           if (newColor != -420) {
             if (solid) {
               if (armor && t > (imageWidth / 2) - armorOutlineFractionHorizontal && t < (imageWidth / 2) + armorOutlineFractionHorizontal - 1.0F && s > (imageHeight / 2) - armorOutlineFractionVertical && s < (imageHeight / 2) + armorOutlineFractionVertical - 1.0F) {
                 newColor = 0;
               } else {
                 newColor = -16777216;
               } 
             } else {
               int red = newColor >> 16 & 0xFF;
               int green = newColor >> 8 & 0xFF;
               int blue = newColor >> 0 & 0xFF;
               newColor = 0x0 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
             } 
             
             temp.setRGB(t, s, newColor);
           } 
         } 
       } 
     } 
     
     return temp;
   }
   
   private static int sampleNonTransparentNeighborPixel(int x, int y, BufferedImage image) {
     if (x > 0) {
       int color = image.getRGB(x - 1, y);
       if ((color >> 24 & 0xFF) > 50) {
         return color;
       }
     } 
     
     if (x < image.getWidth() - 1) {
       int color = image.getRGB(x + 1, y);
       if ((color >> 24 & 0xFF) > 50) {
         return color;
       }
     } 
     
     if (y > 0) {
       int color = image.getRGB(x, y - 1);
       if ((color >> 24 & 0xFF) > 50) {
         return color;
       }
     } 
     
     if (y < image.getHeight() - 1) {
       int color = image.getRGB(x, y + 1);
       if ((color >> 24 & 0xFF) > 50) {
         return color;
       }
     } 
     
     if (x > 0 && y > 0) {
       int color = image.getRGB(x - 1, y - 1);
       if ((color >> 24 & 0xFF) > 50) {
         return color;
       }
     } 
     
     if (x > 0 && y < image.getHeight() - 1) {
       int color = image.getRGB(x - 1, y + 1);
       if ((color >> 24 & 0xFF) > 50) {
         return color;
       }
     } 
     
     if (x < image.getWidth() - 1 && y > 0) {
       int color = image.getRGB(x + 1, y - 1);
       if ((color >> 24 & 0xFF) > 50) {
         return color;
       }
     } 
     
     if (x < image.getWidth() - 1 && y < image.getHeight() - 1) {
       int color = image.getRGB(x + 1, y + 1);
       if ((color >> 24 & 0xFF) > 50) {
         return color;
       }
     } 
     
     return -420;
   }
   
   public static BufferedImage trim(BufferedImage image) {
     int left = -1;
     int right = image.getWidth();
     int top = -1;
     int bottom = image.getHeight();
     boolean foundColor = false;
     int color = 0;
     
     while (!foundColor && left < right - 1) {
       left++;
       
       for (int t = 0; t < image.getHeight(); t++) {
         color = image.getRGB(left, t);
         if (color >> 24 != 0) {
           foundColor = true;
         }
       } 
     } 
     
     foundColor = false;
     
     while (!foundColor && right > left + 1) {
       right--;
       
       for (int t = 0; t < image.getHeight(); t++) {
         color = image.getRGB(right, t);
         if (color >> 24 != 0) {
           foundColor = true;
         }
       } 
     } 
     
     foundColor = false;
     
     while (!foundColor && top < bottom - 1) {
       top++;
       
       for (int t = 0; t < image.getWidth(); t++) {
         color = image.getRGB(t, top);
         if (color >> 24 != 0) {
           foundColor = true;
         }
       } 
     } 
     
     foundColor = false;
     
     while (!foundColor && bottom > top + 1) {
       bottom--;
       
       for (int t = 0; t < image.getWidth(); t++) {
         color = image.getRGB(t, bottom);
         if (color >> 24 != 0) {
           foundColor = true;
         }
       } 
     } 
     
     return image.getSubimage(left, top, right - left + 1, bottom - top + 1);
   }
   
   public static BufferedImage trimCentered(BufferedImage image) {
     int height = image.getHeight();
     int width = image.getWidth();
     int left = -1;
     int right = width;
     int top = -1;
     int bottom = height;
     boolean foundColor = false;
     int color = 0;
     
     while (!foundColor && left < width / 2 - 1 && top < height / 2 - 1) {
       left++;
       right--;
       top++;
       bottom--;
       int y;
       for (y = top; y < bottom; y++) {
         color = image.getRGB(left, y);
         if (color >> 24 != 0) {
           foundColor = true;
         }
       } 
       
       for (y = top; y < bottom; y++) {
         color = image.getRGB(right, y);
         if (color >> 24 != 0) {
           foundColor = true;
         }
       } 
       int x;
       for (x = left; x < right; x++) {
         color = image.getRGB(x, top);
         if (color >> 24 != 0) {
           foundColor = true;
         }
       } 
       
       for (x = left; x < right; x++) {
         color = image.getRGB(x, bottom);
         if (color >> 24 != 0) {
           foundColor = true;
         }
       } 
     } 
     
     return image.getSubimage(left, top, right - left + 1, bottom - top + 1);
   }
   
   public static BufferedImage colorify(BufferedImage image, float r, float g, float b) {
     BufferedImage temp = new BufferedImage(image.getWidth(), image.getHeight(), 3);
     Graphics2D gfx = temp.createGraphics();
     gfx.drawImage(image, 0, 0, (ImageObserver)null);
     gfx.dispose();
     
     for (int x = 0; x < temp.getWidth(); x++) {
       for (int y = 0; y < temp.getHeight(); y++) {
         int ax = temp.getColorModel().getAlpha(temp.getRaster().getDataElements(x, y, null));
         int rx = temp.getColorModel().getRed(temp.getRaster().getDataElements(x, y, null));
         int gx = temp.getColorModel().getGreen(temp.getRaster().getDataElements(x, y, null));
         int bx = temp.getColorModel().getBlue(temp.getRaster().getDataElements(x, y, null));
         rx = (int)(rx * r);
         gx = (int)(gx * g);
         bx = (int)(bx * b);
         temp.setRGB(x, y, ax << 24 | rx << 16 | gx << 8 | bx << 0);
       } 
     } 
     
     return temp;
   }
   
   public static BufferedImage colorify(BufferedImage image, int r, int g, int b) {
     return colorify(image, r / 255.0F, g / 255.0F, b / 255.0F);
   }
   
   public static BufferedImage colorify(BufferedImage image, int rgb) {
     return colorify(image, rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF);
   }
   
   public static float percentageOfEdgePixelsThatAreSolid(BufferedImage image) {
     float edgePixels = (image.getWidth() * 2 + image.getHeight() * 2 - 2);
     float edgePixelsWithColor = 0.0F;
     int color = 0;
     int t;
     for (t = 0; t < image.getHeight(); t++) {
       color = image.getRGB(0, t);
       if (color >> 24 != 0) {
         edgePixelsWithColor++;
       }
       
       color = image.getRGB(image.getWidth() - 1, t);
       if (color >> 24 != 0) {
         edgePixelsWithColor++;
       }
     } 
     
     for (t = 1; t < image.getWidth() - 1; t++) {
       color = image.getRGB(t, 0);
       if (color >> 24 != 0) {
         edgePixelsWithColor++;
       }
       
       color = image.getRGB(t, image.getHeight() - 1);
       if (color >> 24 != 0) {
         edgePixelsWithColor++;
       }
     } 
     
     return edgePixelsWithColor / edgePixels;
   }
 }
 