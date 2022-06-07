 package cn.magicst.mamiyaotaru.voxelmap.util;
 
 import cn.magicst.mamiyaotaru.voxelmap.textures.Sprite;
 import com.mojang.blaze3d.platform.GlStateManager;
 import com.mojang.blaze3d.platform.TextureUtil;
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.awt.image.BufferedImage;
 import java.nio.ByteBuffer;
 import java.nio.IntBuffer;
 import net.minecraft.class_1011;
 import net.minecraft.class_1044;
 import net.minecraft.class_1060;
 import net.minecraft.class_1159;
 import net.minecraft.class_276;
 import net.minecraft.class_287;
 import net.minecraft.class_289;
 import net.minecraft.class_290;
 import net.minecraft.class_293;
 import net.minecraft.class_2960;
 import net.minecraft.class_310;
 import net.minecraft.class_311;
 import net.minecraft.class_6367;
 import org.lwjgl.BufferUtils;
 import org.lwjgl.opengl.GL11;
 import org.lwjgl.opengl.GL30;
 
 public class GLUtils
 {
   private static class_289 tessellator = class_289.method_1348();
   private static class_287 vertexBuffer = tessellator.method_1349();
   public static class_1060 textureManager;
   public static class_276 frameBuffer;
   public static int fboID = 0;
   public static int rboID = 0;
   public static int fboTextureID = 0;
   public static int depthTextureID = 0;
   private static int previousFBOID = 0;
   private static int previousFBOIDREAD = 0;
   private static int previousFBOIDDRAW = 0;
   private static int previousProgram = 0;
   public static boolean hasAlphaBits = (GL30.glGetFramebufferAttachmentParameteri(36008, 1026, 33301) > 0);
   public static final int fboSize = 512;
   public static final int fboRad = 256;
   private static final IntBuffer dataBuffer = class_311.method_1596(16777216).asIntBuffer();
   
   public static void setupFrameBuffer() {
     previousFBOID = GL11.glGetInteger(36006);
     fboID = GL30.glGenFramebuffers();
     fboTextureID = GL11.glGenTextures();
     int width = 512;
     int height = 512;
     GL30.glBindFramebuffer(36160, fboID);
     ByteBuffer byteBuffer = BufferUtils.createByteBuffer(4 * width * height);
     GL11.glBindTexture(3553, fboTextureID);
     GL11.glTexParameteri(3553, 10242, 10496);
     GL11.glTexParameteri(3553, 10243, 10496);
     GL11.glTexParameteri(3553, 10241, 9729);
     GL11.glTexParameteri(3553, 10240, 9729);
     GL11.glTexImage2D(3553, 0, 6408, width, height, 0, 6408, 5120, byteBuffer);
     GL30.glFramebufferTexture2D(36160, 36064, 3553, fboTextureID, 0);
     rboID = GL30.glGenRenderbuffers();
     GL30.glBindRenderbuffer(36161, rboID);
     GL30.glRenderbufferStorage(36161, 33190, width, height);
     GL30.glFramebufferRenderbuffer(36160, 36096, 36161, rboID);
     GL30.glBindRenderbuffer(36161, 0);
     checkFramebufferStatus();
     GL30.glBindFramebuffer(36160, previousFBOID);
     GlStateManager._bindTexture(0);
   }
   
   public static void setupFrameBufferUsingMinecraft() {
     frameBuffer = (class_276)new class_6367(512, 512, true, class_310.field_1703);
     fboID = frameBuffer.field_1476;
     fboTextureID = frameBuffer.method_30277();
   }
   
   public static void setupFrameBufferUsingMinecraftUnrolled() {
     RenderSystem.assertOnRenderThreadOrInit();
     fboID = GL30.glGenFramebuffers();
     fboTextureID = GL11.glGenTextures();
     depthTextureID = GL11.glGenTextures();
     GL11.glBindTexture(3553, depthTextureID);
     GL11.glTexParameteri(3553, 10241, 9728);
     GL11.glTexParameteri(3553, 10240, 9728);
     GL11.glTexParameteri(3553, 34892, 0);
     GL11.glTexImage2D(3553, 0, 6402, 512, 512, 0, 6402, 5126, (IntBuffer)null);
     GL11.glBindTexture(3553, fboTextureID);
     GL11.glTexParameteri(3553, 10241, 9729);
     GL11.glTexParameteri(3553, 10240, 9729);
     GL11.glTexImage2D(3553, 0, 32856, 512, 512, 0, 6408, 5121, (IntBuffer)null);
     GL30.glBindFramebuffer(36160, fboID);
     GL30.glFramebufferTexture2D(36160, 36064, 3553, fboTextureID, 0);
     GL30.glFramebufferTexture2D(36160, 36096, 3553, depthTextureID, 0);
     checkFramebufferStatus();
     GlStateManager._clearColor(1.0F, 1.0F, 1.0F, 0.0F);
     int i = 16384;
     GlStateManager._clearDepth(1.0D);
     i |= 0x100;
     GlStateManager._clear(i, class_310.field_1703);
     GlStateManager._glBindFramebuffer(36160, 0);
     GlStateManager._bindTexture(0);
   }
   
   public static void checkFramebufferStatus() {
     int i = GL30.glCheckFramebufferStatus(36160);
     if (i != 36053) {
       if (i == 36054)
         throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT"); 
       if (i == 36055)
         throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT"); 
       if (i == 36059)
         throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER"); 
       if (i == 36060) {
         throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
       }
       throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + i);
     } 
   }
 
   
   public static void bindFrameBuffer() {
     previousFBOID = GL11.glGetInteger(36006);
     previousFBOIDREAD = GL11.glGetInteger(36010);
     previousFBOIDDRAW = GL11.glGetInteger(36006);
     GL30.glBindFramebuffer(36160, fboID);
     GL30.glBindFramebuffer(36008, fboID);
     GL30.glBindFramebuffer(36009, fboID);
   }
   
   public static void unbindFrameBuffer() {
     GL30.glBindFramebuffer(36160, previousFBOID);
     GL30.glBindFramebuffer(36008, previousFBOIDREAD);
     GL30.glBindFramebuffer(36009, previousFBOIDDRAW);
   }
   
   public static void setMap(int x, int y) {
     setMap(x, y, 128);
   }
   
   public static void setMapWithScale(int x, int y, float scale) {
     setMap(x, y, (int)(128.0F * scale));
   }
   
   public static void setMap(float x, float y, int imageSize) {
     float scale = imageSize / 4.0F;
     ldrawthree((x - scale), (y + scale), 1.0D, 0.0F, 1.0F);
     ldrawthree((x + scale), (y + scale), 1.0D, 1.0F, 1.0F);
     ldrawthree((x + scale), (y - scale), 1.0D, 1.0F, 0.0F);
     ldrawthree((x - scale), (y - scale), 1.0D, 0.0F, 0.0F);
   }
   
   public static void setMap(Sprite icon, float x, float y, float imageSize) {
     float halfWidth = imageSize / 4.0F;
     ldrawthree((x - halfWidth), (y + halfWidth), 1.0D, icon.getMinU(), icon.getMaxV());
     ldrawthree((x + halfWidth), (y + halfWidth), 1.0D, icon.getMaxU(), icon.getMaxV());
     ldrawthree((x + halfWidth), (y - halfWidth), 1.0D, icon.getMaxU(), icon.getMinV());
     ldrawthree((x - halfWidth), (y - halfWidth), 1.0D, icon.getMinU(), icon.getMinV());
   }
   
   public static int tex(BufferedImage paramImg) {
     int glid = TextureUtil.generateTextureId();
     int width = paramImg.getWidth();
     int height = paramImg.getHeight();
     int[] imageData = new int[width * height];
     paramImg.getRGB(0, 0, width, height, imageData, 0, width);
     GLShim.glBindTexture(3553, glid);
     dataBuffer.clear();
     dataBuffer.put(imageData, 0, width * height);
     dataBuffer.position(0).limit(width * height);
     GLShim.glTexParameteri(3553, 10241, 9729);
     GLShim.glTexParameteri(3553, 10240, 9729);
     GLShim.glPixelStorei(3314, 0);
     GLShim.glPixelStorei(3316, 0);
     GLShim.glPixelStorei(3315, 0);
     GLShim.glTexImage2D(3553, 0, 6408, width, height, 0, 32993, 33639, dataBuffer);
     return glid;
   }
   
   public static void img(String paramStr) {
     textureManager.method_22813(new class_2960(paramStr));
   }
   
   public static void img2(String paramStr) {
     RenderSystem.setShaderTexture(0, new class_2960(paramStr));
   }
   
   public static void img(class_2960 paramResourceLocation) {
     textureManager.method_22813(paramResourceLocation);
   }
   
   public static void img2(class_2960 paramResourceLocation) {
     RenderSystem.setShaderTexture(0, paramResourceLocation);
   }
   
   public static void disp(int paramInt) {
     GLShim.glBindTexture(3553, paramInt);
   }
   
   public static void disp2(int paramInt) {
     RenderSystem.setShaderTexture(0, paramInt);
   }
   
   public static void register(class_2960 resourceLocation, class_1044 image) {
     textureManager.method_4616(resourceLocation, image);
   }
   
   public static class_1011 nativeImageFromBufferedImage(BufferedImage base) {
     int glid = tex(base);
     class_1011 nativeImage = new class_1011(base.getWidth(), base.getHeight(), false);
     RenderSystem.bindTexture(glid);
     nativeImage.method_4327(0, false);
     return nativeImage;
   }
   
   public static void drawPre() {
     vertexBuffer.method_1328(class_293.class_5596.field_27382, class_290.field_1585);
   }
   
   public static void drawPre(class_293 vertexFormat) {
     vertexBuffer.method_1328(class_293.class_5596.field_27382, vertexFormat);
   }
   
   public static void drawPost() {
     tessellator.method_1350();
   }
   
   public static void glah(int g) {
     GLShim.glDeleteTextures(g);
   }
   
   public static void ldrawone(int x, int y, double z, float u, float v) {
     vertexBuffer.method_22912(x, y, z).method_22913(u, v).method_1344();
   }
   
   public static void ldrawtwo(double x, double y, double z) {
     vertexBuffer.method_22912(x, y, z).method_1344();
   }
   
   public static void ldrawthree(double x, double y, double z, float u, float v) {
     vertexBuffer.method_22912(x, y, z).method_22913(u, v).method_1344();
   }
   
   public static void ldrawthree(class_1159 matrix4f, double x, double y, double z, float u, float v) {
     vertexBuffer.method_22918(matrix4f, (float)x, (float)y, (float)z).method_22913(u, v).method_1344();
   }
 }
