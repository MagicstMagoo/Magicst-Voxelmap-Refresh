 package cn.magicst.mamiyaotaru.voxelmap.util;
 
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IChangeObserver;
 import net.minecraft.class_2818;
 import net.minecraft.class_310;
 
 public class MapChunk {
   private int x = 0;
   private int z = 0;
   private class_2818 chunk;
   private boolean isChanged = false;
   private boolean isLoaded = false;
   private boolean isSurroundedByLoaded = false;
   
   public MapChunk(int x, int z) {
     this.x = x;
     this.z = z;
     this.chunk = (class_310.method_1551()).field_1687.method_8497(x, z);
     this.isLoaded = (this.chunk != null && !this.chunk.method_12223() && (class_310.method_1551()).field_1687.method_8393(x, z));
     this.isSurroundedByLoaded = false;
     this.isChanged = true;
   }
   
   public void checkIfChunkChanged(IChangeObserver changeObserver) {
     if (hasChunkLoadedOrUnloaded() || this.isChanged) {
       changeObserver.processChunk(this.chunk);
       this.isChanged = false;
     } 
   }
 
   
   private boolean hasChunkLoadedOrUnloaded() {
     boolean hasChanged = false;
     if (!this.isLoaded) {
       this.chunk = (class_310.method_1551()).field_1687.method_8497(this.x, this.z);
       if (this.chunk != null && !this.chunk.method_12223() && (class_310.method_1551()).field_1687.method_8393(this.x, this.z)) {
         this.isLoaded = true;
         hasChanged = true;
       } 
     } else if (this.isLoaded && (this.chunk == null || this.chunk.method_12223() || !(class_310.method_1551()).field_1687.method_8393(this.x, this.z))) {
       this.isLoaded = false;
       hasChanged = true;
     } 
     
     return hasChanged;
   }
   
   public void checkIfChunkBecameSurroundedByLoaded(IChangeObserver changeObserver) {
     this.chunk = (class_310.method_1551()).field_1687.method_8497(this.x, this.z);
     this.isLoaded = (this.chunk != null && !this.chunk.method_12223() && (class_310.method_1551()).field_1687.method_8393(this.x, this.z));
     if (this.isLoaded) {
       boolean formerSurroundedByLoaded = this.isSurroundedByLoaded;
       this.isSurroundedByLoaded = isSurroundedByLoaded();
       if (!formerSurroundedByLoaded && this.isSurroundedByLoaded) {
         changeObserver.processChunk(this.chunk);
       }
     } else {
       this.isSurroundedByLoaded = false;
     } 
   }
 
   
   public boolean isSurroundedByLoaded() {
     this.chunk = (class_310.method_1551()).field_1687.method_8497(this.x, this.z);
     this.isLoaded = (this.chunk != null && !this.chunk.method_12223() && (class_310.method_1551()).field_1687.method_8393(this.x, this.z));
     boolean neighborsLoaded = this.isLoaded;
     
     for (int t = this.x - 1; t <= this.x + 1 && neighborsLoaded; t++) {
       for (int s = this.z - 1; s <= this.z + 1 && neighborsLoaded; s++) {
         class_2818 neighborChunk = (class_310.method_1551()).field_1687.method_8497(t, s);
         neighborsLoaded = (neighborsLoaded && neighborChunk != null && !neighborChunk.method_12223() && (class_310.method_1551()).field_1687.method_8393(t, s));
       } 
     } 
     
     return neighborsLoaded;
   }
   
   public int getX() {
     return this.x;
   }
   
   public int getZ() {
     return this.z;
   }
   
   public void setModified(boolean isModified) {
     this.isChanged = isModified;
   }
 }
 