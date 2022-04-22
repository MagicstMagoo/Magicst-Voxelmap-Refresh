 package cn.magicst.mamiyaotaru.voxelmap.textures;
 
 import com.google.common.collect.Lists;
 import cn.magicst.mamiyaotaru.voxelmap.util.GLShim;
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.awt.Graphics2D;
 import java.awt.image.BufferedImage;
 import java.awt.image.ImageObserver;
 import java.io.IOException;
 import java.util.Random;
 import java.util.concurrent.CompletableFuture;
 import java.util.concurrent.Executor;
 import net.minecraft.class_1060;
 import net.minecraft.class_156;
 import net.minecraft.class_286;
 import net.minecraft.class_287;
 import net.minecraft.class_289;
 import net.minecraft.class_290;
 import net.minecraft.class_293;
 import net.minecraft.class_2960;
 import net.minecraft.class_310;
 import net.minecraft.class_327;
 import net.minecraft.class_3300;
 import net.minecraft.class_3302;
 import net.minecraft.class_3695;
 import net.minecraft.class_376;
 import net.minecraft.class_377;
 import net.minecraft.class_390;
 import net.minecraft.class_757;
 
 public class FontRendererWithAtlas
   extends class_327 implements class_3302 {
   private int[] charWidthArray = new int[256];
   public int FONT_HEIGHT = 9;
   public Random fontRandom = new Random();
   private int[] colorCode = new int[32];
   private final class_2960 locationFontTexture;
   private Sprite fontIcon = null;
   private Sprite blankIcon = null;
   private int ref = 0;
   private final class_1060 renderEngine;
   private float posX;
   private float posY;
   private float red;
   private float blue;
   private float green;
   private float alpha;
   private int textColor;
   private boolean randomStyle;
   private boolean boldStyle;
   private boolean italicStyle;
   private boolean underlineStyle;
   private boolean strikethroughStyle;
   private class_287 vertexBuffer;
   
   public FontRendererWithAtlas(class_1060 renderEngine, class_2960 locationFontTexture) {
     super(identifierx -> (class_377)class_156.method_654(new class_377(renderEngine, locationFontTexture), ()));
     this.locationFontTexture = locationFontTexture;
     this.renderEngine = renderEngine;
     renderEngine.method_22813(this.locationFontTexture);
     
     for (int colorCodeIndex = 0; colorCodeIndex < 32; colorCodeIndex++) {
       int var6 = (colorCodeIndex >> 3 & 0x1) * 85;
       int red = (colorCodeIndex >> 2 & 0x1) * 170 + var6;
       int green = (colorCodeIndex >> 1 & 0x1) * 170 + var6;
       int blue = (colorCodeIndex >> 0 & 0x1) * 170 + var6;
       if (colorCodeIndex == 6) {
         red += 85;
       }
       
       if (colorCodeIndex >= 16) {
         red /= 4;
         green /= 4;
         blue /= 4;
       } 
       
       this.colorCode[colorCodeIndex] = (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
     } 
     
     this.vertexBuffer = class_289.method_1348().method_1349();
   }
   
   public void onResourceManagerReload(class_3300 resourceManager) {
     readFontTexture();
   }
   
   private void readFontTexture() {
     BufferedImage fontImage;
     try {
       fontImage = TextureUtilLegacy.readBufferedImage(class_310.method_1551().method_1478().method_14486(this.locationFontTexture).method_14482());
     } catch (IOException var17) {
       throw new RuntimeException(var17);
     } 
     
     if (fontImage.getWidth() > 512 || fontImage.getHeight() > 512) {
       int maxDim = Math.max(fontImage.getWidth(), fontImage.getHeight());
       float scaleBy = 512.0F / maxDim;
       int type = fontImage.getType();
       if (type == 13) {
         type = 6;
       }
       
       int newWidth = Math.max(1, (int)(fontImage.getWidth() * scaleBy));
       int newHeight = Math.max(1, (int)(fontImage.getHeight() * scaleBy));
       BufferedImage tmp = new BufferedImage(newWidth, newHeight, type);
       Graphics2D g2 = tmp.createGraphics();
       g2.drawImage(fontImage, 0, 0, newWidth, newHeight, (ImageObserver)null);
       g2.dispose();
       fontImage = tmp;
     } 
     
     int sheetWidth = fontImage.getWidth();
     int sheetHeight = fontImage.getHeight();
     int[] sheetImageData = new int[sheetWidth * sheetHeight];
     fontImage.getRGB(0, 0, sheetWidth, sheetHeight, sheetImageData, 0, sheetWidth);
     int characterHeight = sheetHeight / 16;
     int characterWidth = sheetWidth / 16;
     byte padding = 1;
     float scale = 8.0F / characterWidth;
     
     for (int characterIndex = 0; characterIndex < 256; characterIndex++) {
       int characterX = characterIndex % 16;
       int characterY = characterIndex / 16;
       if (characterIndex == 32) {
         this.charWidthArray[characterIndex] = 3 + padding;
       }
       
       int thisCharacterWidth = characterWidth - 1;
       boolean onlyBlankPixels = true;
       
       while (thisCharacterWidth >= 0 && onlyBlankPixels) {
         int pixelX = characterX * characterWidth + thisCharacterWidth;
         
         for (int characterPixelYPos = 0; characterPixelYPos < characterHeight && onlyBlankPixels; characterPixelYPos++) {
           int pixelY = (characterY * characterWidth + characterPixelYPos) * sheetWidth;
           if ((sheetImageData[pixelX + pixelY] >> 24 & 0xFF) != 0) {
             onlyBlankPixels = false;
           }
         } 
         
         if (onlyBlankPixels) {
           thisCharacterWidth--;
         }
       } 
       
       thisCharacterWidth++;
       this.charWidthArray[characterIndex] = (int)(0.5D + (thisCharacterWidth * scale)) + padding;
     } 
   }
 
   
   public void setSprites(Sprite text, Sprite blank) {
     this.fontIcon = text;
     this.blankIcon = blank;
   }
   
   public void setFontRef(int ref) {
     this.ref = ref;
   }
   
   private float renderCharAtPos(int charIndex, char character, boolean shadow) {
     return (character == ' ') ? 4.0F : renderDefaultChar(charIndex, shadow);
   }
   
   private float renderDefaultChar(int charIndex, boolean shadow) {
     float sheetWidth = (this.fontIcon.originX + this.fontIcon.width) / this.fontIcon.getMaxU();
     float sheetHeight = (this.fontIcon.originY + this.fontIcon.height) / this.fontIcon.getMaxV();
     float fontScaleX = (this.fontIcon.width - 2) / 128.0F;
     float fontScaleY = (this.fontIcon.height - 2) / 128.0F;
     float charXPosInSheet = (charIndex % 16 * 8) * fontScaleX + this.fontIcon.originX + 1.0F;
     float charYPosInSheet = (charIndex / 16 * 8) * fontScaleY + this.fontIcon.originY + 1.0F;
     float shadowOffset = shadow ? 1.0F : 0.0F;
     float charWidth = this.charWidthArray[charIndex] - 0.01F;
     this.vertexBuffer.method_22912((this.posX + shadowOffset), this.posY, 0.0D).method_22913(charXPosInSheet / sheetWidth, charYPosInSheet / sheetHeight).method_22915(this.red, this.blue, this.green, this.alpha).method_1344();
     this.vertexBuffer.method_22912((this.posX - shadowOffset), (this.posY + 7.99F), 0.0D).method_22913(charXPosInSheet / sheetWidth, (charYPosInSheet + 7.99F * fontScaleY) / sheetHeight).method_22915(this.red, this.blue, this.green, this.alpha).method_1344();
     this.vertexBuffer.method_22912((this.posX + charWidth - 1.0F - shadowOffset), (this.posY + 7.99F), 0.0D).method_22913((charXPosInSheet + (charWidth - 1.0F) * fontScaleX) / sheetWidth, (charYPosInSheet + 7.99F * fontScaleY) / sheetHeight).method_22915(this.red, this.blue, this.green, this.alpha).method_1344();
     this.vertexBuffer.method_22912((this.posX + charWidth - 1.0F + shadowOffset), this.posY, 0.0D).method_22913((charXPosInSheet + (charWidth - 1.0F) * fontScaleX) / sheetWidth, charYPosInSheet / sheetHeight).method_22915(this.red, this.blue, this.green, this.alpha).method_1344();
     return this.charWidthArray[charIndex];
   }
   
   public int drawStringWithShadow(String text, float x, float y, int color) {
     return drawString(text, x, y, color, true);
   }
   
   public int drawString(String text, int x, int y, int color) {
     return drawString(text, x, y, color, false);
   }
   public int drawString(String text, float x, float y, int color, boolean shadow) {
     int var6;
     resetStyles();
     RenderSystem.setShader(class_757::method_34543);
     this.vertexBuffer.method_23477();
     this.vertexBuffer.method_1328(class_293.class_5596.field_27382, class_290.field_1575);
     
     if (shadow) {
       var6 = renderString(text, x + 1.0F, y + 1.0F, color, true);
       var6 = Math.max(var6, renderString(text, x, y, color, false));
     } else {
       var6 = renderString(text, x, y, color, false);
     } 
     
     this.vertexBuffer.method_1326();
     class_286.method_1309(this.vertexBuffer);
     return var6;
   }
   
   private void resetStyles() {
     this.randomStyle = false;
     this.boldStyle = false;
     this.italicStyle = false;
     this.underlineStyle = false;
     this.strikethroughStyle = false;
   }
   
   private void renderStringAtPos(String text, boolean shadow) {
     for (int textIndex = 0; textIndex < text.length(); textIndex++) {
       char character = text.charAt(textIndex);
       if (character == '§' && textIndex + 1 < text.length()) {
         int formatCode = "0123456789abcdefklmnor".indexOf(text.toLowerCase().charAt(textIndex + 1));
         if (formatCode < 16) {
           this.randomStyle = false;
           this.boldStyle = false;
           this.strikethroughStyle = false;
           this.underlineStyle = false;
           this.italicStyle = false;
           if (formatCode < 0 || formatCode > 15) {
             formatCode = 15;
           }
           
           if (shadow) {
             formatCode += 16;
           }
           
           int color = this.colorCode[formatCode];
           this.textColor = color;
         } else if (formatCode == 16) {
           this.randomStyle = true;
         } else if (formatCode == 17) {
           this.boldStyle = true;
         } else if (formatCode == 18) {
           this.strikethroughStyle = true;
         } else if (formatCode == 19) {
           this.underlineStyle = true;
         } else if (formatCode == 20) {
           this.italicStyle = true;
         } else if (formatCode == 21) {
           this.randomStyle = false;
           this.boldStyle = false;
           this.strikethroughStyle = false;
           this.underlineStyle = false;
           this.italicStyle = false;
           GLShim.glColor4f(this.red, this.blue, this.green, this.alpha);
         } 
         
         textIndex++;
       } else {
         int charIndex = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\000\000\000\000\000\000\000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\000".indexOf(character);
         if (charIndex != -1) {
           float sheetWidth = (this.blankIcon.originX + this.blankIcon.width) / this.blankIcon.getMaxU();
           float sheetHeight = (this.blankIcon.originY + this.blankIcon.height) / this.blankIcon.getMaxV();
           float u = (this.blankIcon.originX + 4) / sheetWidth;
           float v = (this.blankIcon.originY + 4) / sheetHeight;
           if (this.randomStyle) {
             int randomCharIndex;
             do {
               randomCharIndex = this.fontRandom.nextInt(this.charWidthArray.length);
             } while (this.charWidthArray[charIndex] != this.charWidthArray[randomCharIndex]);
             
             charIndex = randomCharIndex;
           } 
           
           float offset = 1.0F;
           float widthOfRenderedChar = renderCharAtPos(charIndex, character, this.italicStyle);
           if (this.boldStyle) {
             this.posX += offset;
             renderCharAtPos(charIndex, character, this.italicStyle);
             this.posX -= offset;
             widthOfRenderedChar++;
           } 
           
           if (this.strikethroughStyle) {
             this.vertexBuffer.method_22912(this.posX, (this.posY + (this.FONT_HEIGHT / 2)), 0.0D).method_22913(u, v).method_22915(this.red, this.blue, this.green, this.alpha).method_1344();
             this.vertexBuffer.method_22912((this.posX + widthOfRenderedChar), (this.posY + (this.FONT_HEIGHT / 2)), 0.0D).method_22913(u, v).method_22915(this.red, this.blue, this.green, this.alpha).method_1344();
             this.vertexBuffer.method_22912((this.posX + widthOfRenderedChar), (this.posY + (this.FONT_HEIGHT / 2) - 1.0F), 0.0D).method_22913(u, v).method_22915(this.red, this.blue, this.green, this.alpha).method_1344();
             this.vertexBuffer.method_22912(this.posX, (this.posY + (this.FONT_HEIGHT / 2) - 1.0F), 0.0D).method_22913(u, v).method_22915(this.red, this.blue, this.green, this.alpha).method_1344();
           } 
           
           if (this.underlineStyle) {
             int l = this.underlineStyle ? -1 : 0;
             this.vertexBuffer.method_22912((this.posX + l), (this.posY + this.FONT_HEIGHT), 0.0D).method_22913(u, v).method_22915(this.red, this.blue, this.green, this.alpha).method_1344();
             this.vertexBuffer.method_22912((this.posX + widthOfRenderedChar), (this.posY + this.FONT_HEIGHT), 0.0D).method_22913(u, v).method_22915(this.red, this.blue, this.green, this.alpha).method_1344();
             this.vertexBuffer.method_22912((this.posX + widthOfRenderedChar), (this.posY + this.FONT_HEIGHT - 1.0F), 0.0D).method_22913(u, v).method_22915(this.red, this.blue, this.green, this.alpha).method_1344();
             this.vertexBuffer.method_22912((this.posX + l), (this.posY + this.FONT_HEIGHT - 1.0F), 0.0D).method_22913(u, v).method_22915(this.red, this.blue, this.green, this.alpha).method_1344();
           } 
           
           this.posX += (int)widthOfRenderedChar;
         } 
       } 
     } 
   }
 
   
   private int renderString(String text, float x, float y, int color, boolean shadow) {
     if (text == null) {
       return 0;
     }
     if ((color & 0xFC000000) == 0) {
       color |= 0xFF000000;
     }
     
     if (shadow) {
       color = (color & 0xFCFCFC) >> 2 | color & 0xFF000000;
     }
     
     this.red = (color >> 16 & 0xFF) / 255.0F;
     this.blue = (color >> 8 & 0xFF) / 255.0F;
     this.green = (color & 0xFF) / 255.0F;
     this.alpha = (color >> 24 & 0xFF) / 255.0F;
     this.posX = x;
     this.posY = y;
     renderStringAtPos(text, shadow);
     return (int)this.posX;
   }
 
   
   public int getStringWidth(String string) {
     if (string == null) {
       return 0;
     }
     int totalWidth = 0;
     boolean includeSpace = false;
     
     for (int charIndex = 0; charIndex < string.length(); charIndex++) {
       char character = string.charAt(charIndex);
       float characterWidth = getCharWidth(character);
       if (characterWidth < 0.0F && charIndex < string.length() - 1) {
         charIndex++;
         character = string.charAt(charIndex);
         if (character != 'l' && character != 'L') {
           if (character == 'r' || character == 'R') {
             includeSpace = false;
           }
         } else {
           includeSpace = true;
         } 
         
         characterWidth = 0.0F;
       } 
       
       totalWidth = (int)(totalWidth + characterWidth);
       if (includeSpace && characterWidth > 0.0F) {
         totalWidth++;
       }
     } 
     
     return totalWidth;
   }
 
   
   public float getCharWidth(char character) {
     if (character == '§')
       return -1.0F; 
     if (character == ' ') {
       return 4.0F;
     }
     int indexInDefaultSheet = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\000\000\000\000\000\000\000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\000".indexOf(character);
     return (character > '\000' && indexInDefaultSheet != -1) ? this.charWidthArray[indexInDefaultSheet] : 0.0F;
   }
 
   
   public CompletableFuture method_25931(class_3302.class_4045 var1, class_3300 var2, class_3695 var3, class_3695 var4, Executor var5, Executor var6) {
     return null;
   }
 }

