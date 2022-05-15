 package cn.magicst.mamiyaotaru.voxelmap.util;
 
 import java.util.Objects;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class BlockFace
   implements Comparable<BlockModel.BlockFace>
 {
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
       Objects.requireNonNull(BlockModel.this); this.vertices[t] = new BlockModel.BlockVertex(BlockModel.this, x, y, z, u, v);
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

