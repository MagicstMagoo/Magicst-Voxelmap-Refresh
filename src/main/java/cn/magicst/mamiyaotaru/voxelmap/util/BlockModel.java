 package cn.magicst.mamiyaotaru.voxelmap.util;
 
 import java.awt.Color;
 import java.awt.Graphics2D;
 import java.awt.geom.AffineTransform;
 import java.awt.image.AffineTransformOp;
 import java.awt.image.BufferedImage;
 import java.awt.image.ImageObserver;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 import java.util.Objects;
 import net.minecraft.class_777;
 
 public class BlockModel {
   ArrayList<BlockFace> faces;
   BlockVertex[] longestSide;
   float failedToLoadX;
   float failedToLoadY;
   
   public BlockModel(List<class_777> quads, float failedToLoadX, float failedToLoadY) {
     this.failedToLoadX = failedToLoadX;
     this.failedToLoadY = failedToLoadY;
     this.faces = new ArrayList<>();
     class_777 quad = null;
     for (class_777 quad2 : quads) {
       BlockFace blockFace = new BlockFace(quad2.method_3357());
       if (!blockFace.isClockwise || blockFace.isVertical)
         continue; 
       this.faces.add(blockFace);
     } 
     Collections.sort(this.faces);
     this.longestSide = new BlockVertex[2];
     float greatestLength = 0.0F;
     BlockFace face = null;
     for (BlockFace face2 : this.faces) {
       float uDiff = (face2.longestSide[0]).u - (face2.longestSide[1]).u;
       float vDiff = (face2.longestSide[0]).v - (face2.longestSide[1]).v;
       float segmentLength = (float)Math.sqrt((uDiff * uDiff + vDiff * vDiff));
       if (segmentLength <= greatestLength)
         continue; 
       greatestLength = segmentLength;
       this.longestSide = face2.longestSide;
     } 
   }
   
   public int numberOfFaces() {
     return this.faces.size();
   }
   
   public ArrayList getFaces() {
     return this.faces;
   }
   
   public BufferedImage getImage(BufferedImage terrainImage) {
     float terrainImageAspectRatio = terrainImage.getWidth() / terrainImage.getHeight();
     float longestSideUV = Math.max(Math.abs((this.longestSide[0]).u - (this.longestSide[1]).u), Math.abs((this.longestSide[0]).v - (this.longestSide[1]).v) / terrainImageAspectRatio);
     float modelImageWidthUV = longestSideUV / Math.max(Math.abs((this.longestSide[0]).x - (this.longestSide[1]).x), Math.abs((this.longestSide[0]).z - (this.longestSide[1]).z));
     int modelImageWidth = Math.round(modelImageWidthUV * terrainImage.getWidth());
     BufferedImage modelImage = new BufferedImage(modelImageWidth, modelImageWidth, 6);
     Graphics2D g2 = modelImage.createGraphics();
     g2.setColor(new Color(0, 0, 0, 0));
     g2.fillRect(0, 0, modelImage.getWidth(), modelImage.getHeight());
     g2.dispose();
     BlockFace face = null;
     
     for (BlockFace var32 : this.faces) {
       float minU = var32.getMinU();
       float maxU = var32.getMaxU();
       float minV = var32.getMinV();
       float maxV = var32.getMaxV();
       float minX = var32.getMinX();
       float maxX = var32.getMaxX();
       float minZ = var32.getMinZ();
       float maxZ = var32.getMaxZ();
       if (similarEnough(minU, minV, this.failedToLoadX, this.failedToLoadY)) {
         return null;
       }
       
       int faceImageX = Math.round(minX * modelImage.getWidth());
       int faceImageY = Math.round(minZ * modelImage.getHeight());
       int faceImageWidth = Math.round(maxX * modelImage.getWidth()) - faceImageX;
       int faceImageHeight = Math.round(maxZ * modelImage.getHeight()) - faceImageY;
       if (faceImageWidth == 0) {
         if (faceImageX > modelImageWidth - 1) {
           faceImageX = modelImageWidth - 1;
         }
         
         faceImageWidth = 1;
       } 
       
       if (faceImageHeight == 0) {
         if (faceImageY > modelImageWidth - 1) {
           faceImageY = modelImageWidth - 1;
         }
         
         faceImageHeight = 1;
       } 
       
       int faceImageU = Math.round(minU * terrainImage.getWidth());
       int faceImageV = Math.round(minV * terrainImage.getHeight());
       int faceImageUVWidth = Math.round(maxU * terrainImage.getWidth()) - faceImageU;
       int faceImageUVHeight = Math.round(maxV * terrainImage.getHeight()) - faceImageV;
       if (faceImageUVWidth == 0) {
         faceImageUVWidth = 1;
       }
       
       if (faceImageUVHeight == 0) {
         faceImageUVHeight = 1;
       }
       
       BufferedImage faceImage = terrainImage.getSubimage(faceImageU, faceImageV, faceImageUVWidth, faceImageUVHeight);
       if (faceImageWidth != faceImageUVWidth || faceImageHeight != faceImageUVHeight) {
         if (faceImageWidth == faceImageUVHeight && faceImageHeight == faceImageUVWidth) {
           BufferedImage tmp = new BufferedImage(faceImageWidth, faceImageHeight, 6);
           AffineTransform transform = new AffineTransform();
           transform.translate((faceImage.getHeight() / 2), (faceImage.getWidth() / 2));
           transform.rotate(1.5707963267948966D);
           transform.translate((-faceImage.getWidth() / 2), (-faceImage.getHeight() / 2));
           AffineTransformOp op = new AffineTransformOp(transform, 1);
           faceImage = op.filter(faceImage, tmp);
         } else {
           BufferedImage tmp = new BufferedImage(faceImageWidth, faceImageHeight, 6);
           g2 = tmp.createGraphics();
           g2.drawImage(faceImage, 0, 0, faceImageWidth, faceImageHeight, (ImageObserver)null);
           g2.dispose();
           faceImage = tmp;
         } 
       }
       
       g2 = modelImage.createGraphics();
       g2.drawImage(faceImage, faceImageX, faceImageY, (ImageObserver)null);
       g2.dispose();
     } 
     
     return modelImage;
   }
   
   private boolean similarEnough(float a, float b, float one, float two) {
     boolean similar = (Math.abs(a - one) < 1.0E-4D);
     return (similar && Math.abs(b - two) < 1.0E-4D);
   }
   
   public class BlockFace implements Comparable<BlockFace> {
     BlockModel.BlockVertex[] vertices;
     boolean isHorizontal;
     boolean isVertical;
     boolean isClockwise;
     float yLevel;
     BlockModel.BlockVertex[] longestSide;
     
     BlockFace(int[] values) {
       int arraySize = values.length;
       int intsPerVertex = arraySize / 4;
       this.vertices = new BlockModel.BlockVertex[4];
       
       for (int t = 0; t < 4; t++) {
         float x = Float.intBitsToFloat(values[t * intsPerVertex + 0]);
         float y = Float.intBitsToFloat(values[t * intsPerVertex + 1]);
         float z = Float.intBitsToFloat(values[t * intsPerVertex + 2]);
         float u = Float.intBitsToFloat(values[t * intsPerVertex + 4]);
         float v = Float.intBitsToFloat(values[t * intsPerVertex + 5]);
         Objects.requireNonNull(BlockModel.this); this.vertices[t] = new BlockModel.BlockVertex(x, y, z, u, v);
       } 
       
       this.isHorizontal = checkIfHorizontal();
       this.isVertical = checkIfVertical();
       this.isClockwise = checkIfClockwise();
       this.yLevel = calculateY();
       this.longestSide = getLongestSide();
     }
     
     private boolean checkIfHorizontal() {
       boolean isHorizontal = true;
       float initialY = (this.vertices[0]).y;
       
       for (int t = 1; t < this.vertices.length; t++) {
         if ((this.vertices[t]).y != initialY) {
           isHorizontal = false;
         }
       } 
       
       return isHorizontal;
     }
     
     private boolean checkIfVertical() {
       boolean allSameX = true;
       boolean allSameZ = true;
       float initialX = (this.vertices[0]).x;
       float initialZ = (this.vertices[0]).z;
       
       for (int t = 1; t < this.vertices.length; t++) {
         if ((this.vertices[t]).x != initialX) {
           allSameX = false;
         }
         
         if ((this.vertices[t]).z != initialZ) {
           allSameZ = false;
         }
       } 
       
       return (allSameX || allSameZ);
     }
     
     private boolean checkIfClockwise() {
       float sum = 0.0F;
       
       for (int t = 0; t < this.vertices.length; t++) {
         sum += ((this.vertices[(t == this.vertices.length - 1) ? 0 : (t + 1)]).x - (this.vertices[t]).x) * ((this.vertices[(t == this.vertices.length - 1) ? 0 : (t + 1)]).z + (this.vertices[t]).z);
       }
       
       return (sum > 0.0F);
     }
     
     private float calculateY() {
       float sum = 0.0F;
       
       for (int t = 0; t < this.vertices.length; t++) {
         sum += (this.vertices[t]).y;
       }
       
       return sum / this.vertices.length;
     }
     
     private BlockModel.BlockVertex[] getLongestSide() {
       float greatestLength = -1.0F;
       BlockModel.BlockVertex[] longestSide = new BlockModel.BlockVertex[0];
       
       for (int t = 0; t < this.vertices.length; t++) {
         float uDiff = (this.vertices[t]).u - (this.vertices[(t == this.vertices.length - 1) ? 0 : (t + 1)]).u;
         float vDiff = (this.vertices[t]).v - (this.vertices[(t == this.vertices.length - 1) ? 0 : (t + 1)]).v;
         float segmentLength = (float)Math.sqrt((uDiff * uDiff + vDiff * vDiff));
         if (segmentLength > greatestLength) {
           greatestLength = segmentLength;
           longestSide = new BlockModel.BlockVertex[] { this.vertices[t], this.vertices[(t == this.vertices.length - 1) ? 0 : (t + 1)] };
         } 
       } 
       
       return longestSide;
     }
     
     public float getMinX() {
       float minX = 1.0F;
       
       for (int t = 0; t < this.vertices.length; t++) {
         if ((this.vertices[t]).x < minX) {
           minX = (this.vertices[t]).x;
         }
       } 
       
       return minX;
     }
     
     public float getMaxX() {
       float maxX = 0.0F;
       
       for (int t = 0; t < this.vertices.length; t++) {
         if ((this.vertices[t]).x > maxX) {
           maxX = (this.vertices[t]).x;
         }
       } 
       
       return maxX;
     }
     
     public float getMinZ() {
       float minZ = 1.0F;
       
       for (int t = 0; t < this.vertices.length; t++) {
         if ((this.vertices[t]).z < minZ) {
           minZ = (this.vertices[t]).z;
         }
       } 
       
       return minZ;
     }
     
     public float getMaxZ() {
       float maxZ = 0.0F;
       
       for (int t = 0; t < this.vertices.length; t++) {
         if ((this.vertices[t]).z > maxZ) {
           maxZ = (this.vertices[t]).z;
         }
       } 
       
       return maxZ;
     }
     
     public float getMinU() {
       float minU = 1.0F;
       
       for (int t = 0; t < this.vertices.length; t++) {
         if ((this.vertices[t]).u < minU) {
           minU = (this.vertices[t]).u;
         }
       } 
       
       return minU;
     }
     
     public float getMaxU() {
       float maxU = 0.0F;
       
       for (int t = 0; t < this.vertices.length; t++) {
         if ((this.vertices[t]).u > maxU) {
           maxU = (this.vertices[t]).u;
         }
       } 
       
       return maxU;
     }
     
     public float getMinV() {
       float minV = 1.0F;
       
       for (int t = 0; t < this.vertices.length; t++) {
         if ((this.vertices[t]).v < minV) {
           minV = (this.vertices[t]).v;
         }
       } 
       
       return minV;
     }
     
     public float getMaxV() {
       float maxV = 0.0F;
       
       for (int t = 0; t < this.vertices.length; t++) {
         if ((this.vertices[t]).v > maxV) {
           maxV = (this.vertices[t]).v;
         }
       } 
       
       return maxV;
     }
     
     public int compareTo(BlockFace compareTo) {
       if (this.yLevel > compareTo.yLevel) {
         return 1;
       }
       return (this.yLevel < compareTo.yLevel) ? -1 : 0;
     }
   }
   
   private class BlockVertex
   {
     float x;
     float y;
     float z;
     float u;
     float v;
     
     BlockVertex(float x, float y, float z, float u, float v) {
       this.x = x;
       this.y = y;
       this.z = z;
       this.u = u;
       this.v = v;
     }
   }
 }




