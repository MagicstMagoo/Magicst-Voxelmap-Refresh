 package cn.magicst.mamiyaotaru.voxelmap.persistent;
 
 import java.awt.image.BufferedImage;
 import java.awt.image.DataBufferByte;
 import java.io.File;
 import java.io.IOException;
 import javax.imageio.ImageIO;
 
 class null
   implements Runnable
 {
   public void run() {
     CachedRegion.this.threadLock.lock();
     
     try {
       BufferedImage realBufferedImage = new BufferedImage(CachedRegion.this.width, CachedRegion.this.width, 6);
       byte[] dstArray = ((DataBufferByte)realBufferedImage.getRaster().getDataBuffer()).getData();
       System.arraycopy(CachedRegion.this.image.getData(), 0, dstArray, 0, (CachedRegion.this.image.getData()).length);
       ImageIO.write(realBufferedImage, "png", imageFile);
     } catch (IOException var6) {
       var6.printStackTrace();
     } finally {
       CachedRegion.this.threadLock.unlock();
     } 
   }
 }


