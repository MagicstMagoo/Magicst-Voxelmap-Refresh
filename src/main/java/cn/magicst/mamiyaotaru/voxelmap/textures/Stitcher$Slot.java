 package cn.magicst.mamiyaotaru.voxelmap.textures;
 
 import com.google.common.collect.Lists;
 import java.util.List;
 import java.util.Objects;
 
 public class Slot
 {
   private final int originX;
   private final int originY;
   private final int width;
   private final int height;
   private int failsAt = Stitcher.this.maxWidth;
   private List<Slot> subSlots;
   private Stitcher.Holder holder;
   
   public Slot(int originX, int originY, int width, int height) {
     this.originX = originX;
     this.originY = originY;
     this.width = width;
     this.height = height;
   }
   
   public Stitcher.Holder getStitchHolder() {
     return this.holder;
   }
   
   public int getOriginX() {
     return this.originX;
   }
   
   public int getOriginY() {
     return this.originY;
   }
   
   public boolean addSlot(Stitcher.Holder holder) {
     if (holder.width >= this.failsAt)
       return false; 
     if (this.holder != null) {
       this.failsAt = 0;
       return false;
     } 
     int holderWidth = holder.getWidth();
     int holderHeight = holder.getHeight();
     if (holderWidth <= this.width && holderHeight <= this.height) {
       if (holderWidth == this.width && holderHeight == this.height) {
         this.holder = holder;
         return true;
       } 
       if (this.subSlots == null) {
         this.subSlots = Lists.newArrayListWithCapacity(1);
         Objects.requireNonNull(Stitcher.this); this.subSlots.add(new Slot(this.originX, this.originY, holderWidth, holderHeight));
         int excessWidth = this.width - holderWidth;
         int excessHeight = this.height - holderHeight;
         if (excessHeight > 0 && excessWidth > 0) {
           int var6 = Math.max(this.height, excessWidth);
           int var7 = Math.max(this.width, excessHeight);
           if (var6 > var7) {
             Objects.requireNonNull(Stitcher.this); this.subSlots.add(new Slot(this.originX, this.originY + holderHeight, holderWidth, excessHeight));
             Objects.requireNonNull(Stitcher.this); this.subSlots.add(new Slot(this.originX + holderWidth, this.originY, excessWidth, this.height));
           } else {
             Objects.requireNonNull(Stitcher.this); this.subSlots.add(new Slot(this.originX + holderWidth, this.originY, excessWidth, holderHeight));
             Objects.requireNonNull(Stitcher.this); this.subSlots.add(new Slot(this.originX, this.originY + holderHeight, this.width, excessHeight));
           } 
         } else if (excessWidth == 0) {
           Objects.requireNonNull(Stitcher.this); this.subSlots.add(new Slot(this.originX, this.originY + holderHeight, holderWidth, excessHeight));
         } else if (excessHeight == 0) {
           Objects.requireNonNull(Stitcher.this); this.subSlots.add(new Slot(this.originX + holderWidth, this.originY, excessWidth, holderHeight));
         } 
       } 
       
       for (Slot slot : this.subSlots) {
         if (slot.addSlot(holder)) {
           return true;
         }
       } 
       
       this.failsAt = holder.width;
       return false;
     } 
     
     this.failsAt = holder.width;
     return false;
   }
    
   public void getAllStitchSlots(List<Slot> listOfStitchSlots) {
     if (this.holder != null) {
       listOfStitchSlots.add(this);
     } else if (this.subSlots != null) {
       for (Slot slot : this.subSlots)
         slot.getAllStitchSlots(listOfStitchSlots); 
     } 
   }
 }

