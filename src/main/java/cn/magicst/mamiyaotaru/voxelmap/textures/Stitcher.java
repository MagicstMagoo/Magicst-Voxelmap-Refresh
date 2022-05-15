 package cn.magicst.mamiyaotaru.voxelmap.textures;
 
 import com.google.common.collect.Lists;
 import com.google.common.collect.Sets;
 import cn.magicst.mamiyaotaru.voxelmap.util.I18nUtils;
 import java.text.Collator;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.List;
 import java.util.Objects;
 import java.util.Set;
 import net.minecraft.class_3532;
 
 public class Stitcher {
   private final Set setStitchHolders = Sets.newHashSetWithExpectedSize(256);
   private final List<Slot> stitchSlots = Lists.newArrayListWithCapacity(256);
   private int currentWidth = 0;
   private int currentHeight = 0;
   private int currentWidthToPowerOfTwo = 0;
   private int currentHeightToPowerOfTwo = 0;
   private final int maxWidth;
   private final int maxHeight;
   private final int maxTileDimension;
   
   public Stitcher(int maxWidth, int maxHeight, int maxTileDimension) {
     this.maxWidth = maxWidth;
     this.maxHeight = maxHeight;
     this.maxTileDimension = maxTileDimension;
   }
   
   public int getCurrentImageWidth() {
     return this.currentWidthToPowerOfTwo;
   }
   
   public int getCurrentImageHeight() {
     return this.currentHeightToPowerOfTwo;
   }
   
   public int getCurrentWidth() {
     return this.currentWidth;
   }
   
   public int getCurrentHeight() {
     return this.currentHeight;
   }
   
   public void addSprite(Sprite icon) {
     Holder holder = new Holder(icon);
     if (this.maxTileDimension > 0) {
       holder.setNewDimension(this.maxTileDimension);
     }
     
     this.setStitchHolders.add(holder);
   }
   
   public void doStitch() {
     Holder[] stitchHoldersArray = (Holder[])this.setStitchHolders.toArray((Object[])new Holder[this.setStitchHolders.size()]);
     Arrays.sort((Object[])stitchHoldersArray);
     Holder[] tempStitchHoldersArray = stitchHoldersArray;
     int stitcherHoldersArrayLength = stitchHoldersArray.length;
     if (stitcherHoldersArrayLength > 0) {
       Holder holder = stitchHoldersArray[0];
       int iconWidth = holder.width;
       int iconHeight = holder.height;
       boolean allSameSize = true;
       
       for (int i = 1; i < stitcherHoldersArrayLength && allSameSize; i++) {
         holder = tempStitchHoldersArray[i];
         allSameSize = (allSameSize && holder.width == iconWidth && holder.height == iconHeight);
       } 
       
       if (allSameSize) {
         int nextPowerOfTwo = class_3532.method_15339(stitcherHoldersArrayLength);
         int power = Integer.numberOfTrailingZeros(nextPowerOfTwo);
         int width = (int)Math.pow(2.0D, Math.ceil(power / 2.0D)) * iconWidth;
         int height = (int)Math.pow(2.0D, Math.floor(power / 2.0D)) * iconHeight;
         this.currentWidth = width;
         this.currentHeight = height;
         this.currentWidthToPowerOfTwo = width;
         this.currentHeightToPowerOfTwo = height;
         Slot slot = new Slot(0, 0, this.currentWidth, this.currentHeight);
         this.stitchSlots.add(slot);
       } 
     } 
     
     for (int stitcherHolderIndex = 0; stitcherHolderIndex < stitcherHoldersArrayLength; stitcherHolderIndex++) {
       Holder holder = tempStitchHoldersArray[stitcherHolderIndex];
       if (!allocateSlot(holder)) {
         String errorString = String.format("Unable to fit: %s - size: %dx%d - Maybe try a lower resolution resourcepack?", new Object[] { holder.getAtlasSprite().getIconName(), Integer.valueOf(holder.getAtlasSprite().getIconWidth()), Integer.valueOf(holder.getAtlasSprite().getIconHeight()) });
         throw new StitcherException(holder, errorString);
       } 
     } 
     
     this.currentWidthToPowerOfTwo = class_3532.method_15339(this.currentWidth);
     this.currentHeightToPowerOfTwo = class_3532.method_15339(this.currentHeight);
     this.setStitchHolders.clear();
   }
   
   public void doStitchNew() {
     Holder[] stitchHoldersArray = (Holder[])this.setStitchHolders.toArray((Object[])new Holder[this.setStitchHolders.size()]);
     Arrays.sort((Object[])stitchHoldersArray);
     
     for (Holder holder : stitchHoldersArray) {
       if (!allocateSlot(holder)) {
         String errorString = String.format("Unable to fit: %s - size: %dx%d - Maybe try a lower resolution resourcepack?", new Object[] { holder.getAtlasSprite().getIconName(), Integer.valueOf(holder.getAtlasSprite().getIconWidth()), Integer.valueOf(holder.getAtlasSprite().getIconHeight()) });
         throw new StitcherException(holder, errorString);
       } 
     } 
     
     this.currentWidthToPowerOfTwo = class_3532.method_15339(this.currentWidth);
     this.currentHeightToPowerOfTwo = class_3532.method_15339(this.currentHeight);
     this.setStitchHolders.clear();
   }
   
   public List<Sprite> getStitchSlots() {
     ArrayList<Slot> listOfStitchSlots = Lists.newArrayList();
     
     for (Slot slot : this.stitchSlots) {
       slot.getAllStitchSlots(listOfStitchSlots);
     }
     
     ArrayList<Sprite> spritesList = Lists.newArrayList();
     
     for (Slot stitcherSlot : listOfStitchSlots) {
       Holder stitcherHolder = stitcherSlot.getStitchHolder();
       Sprite icon = stitcherHolder.getAtlasSprite();
       icon.initSprite(this.currentWidthToPowerOfTwo, this.currentHeightToPowerOfTwo, stitcherSlot.getOriginX(), stitcherSlot.getOriginY());
       spritesList.add(icon);
     } 
     
     return spritesList;
   }
   
   private boolean allocateSlot(Holder holder) {
     for (int stitcherSlotsIndex = 0; stitcherSlotsIndex < this.stitchSlots.size(); stitcherSlotsIndex++) {
       if (((Slot)this.stitchSlots.get(stitcherSlotsIndex)).addSlot(holder)) {
         return true;
       }
     } 
     
     return expandAndAllocateSlot(holder);
   } private boolean expandAndAllocateSlot(Holder holder) {
     boolean shouldExpandRight;
     Slot slot;
     int expandBy = holder.getWidth();
     int currentWidthToPowerOfTwo = class_3532.method_15339(this.currentWidth);
     int currentHeightToPowerOfTwo = class_3532.method_15339(this.currentHeight);
     int possibleNewWidthToPowerOfTwo = class_3532.method_15339(this.currentWidth + expandBy);
     int possibleNewHeightToPowerOfTwo = class_3532.method_15339(this.currentHeight + expandBy);
     boolean isRoomToExpandRight = (possibleNewWidthToPowerOfTwo <= this.maxWidth);
     boolean isRoomToExpandDown = (possibleNewHeightToPowerOfTwo <= this.maxHeight);
     if (!isRoomToExpandRight && !isRoomToExpandDown) {
       return false;
     }
     boolean widthWouldChange = (currentWidthToPowerOfTwo != possibleNewWidthToPowerOfTwo);
     boolean heightWouldChange = (currentHeightToPowerOfTwo != possibleNewHeightToPowerOfTwo);
     
     if (widthWouldChange ^ heightWouldChange) {
       shouldExpandRight = !widthWouldChange;
     } else {
       shouldExpandRight = (isRoomToExpandRight && currentWidthToPowerOfTwo <= currentHeightToPowerOfTwo);
     } 
     
     if (class_3532.method_15339((shouldExpandRight ? this.currentWidth : this.currentHeight) + expandBy) > (shouldExpandRight ? this.maxWidth : this.maxHeight)) {
       return false;
     }
     
     if (shouldExpandRight) {
       if (this.currentHeight == 0) {
         this.currentHeight = holder.getHeight();
       }
       
       slot = new Slot(this.currentWidth, 0, holder.getWidth(), this.currentHeight);
       this.currentWidth += holder.getWidth();
     } else {
       slot = new Slot(0, this.currentHeight, this.currentWidth, holder.getHeight());
       this.currentHeight += holder.getHeight();
     } 
     
     if (!slot.addSlot(holder)) {
       String errorString = String.format("Unable to fit: %s - size: %dx%d - Maybe try a lower resolution resourcepack?", new Object[] { holder.getAtlasSprite().getIconName(), Integer.valueOf(holder.getAtlasSprite().getIconWidth()), Integer.valueOf(holder.getAtlasSprite().getIconHeight()) });
       System.err.println(errorString);
     } 
     
     this.stitchSlots.add(slot);
     return true;
   }
   
   public class Holder
     implements Comparable<Holder>
   {
     private final Sprite icon;
     private final int width;
     private final int height;
     private float scaleFactor = 1.0F;
     
     public Holder(Sprite icon) {
       this.icon = icon;
       this.width = icon.getIconWidth();
       this.height = icon.getIconHeight();
     }
     
     public Sprite getAtlasSprite() {
       return this.icon;
     }
     
     public int getWidth() {
       return (int)(this.width * this.scaleFactor);
     }
     
     public int getHeight() {
       return (int)(this.height * this.scaleFactor);
     }
     
     public void setNewDimension(int newDimension) {
       if (this.width > newDimension && this.height > newDimension) {
         this.scaleFactor = newDimension / Math.min(this.width, this.height);
       }
     }
 
     
     public int compareTo(Holder compareTo) {
       int var2;
       if (getHeight() == compareTo.getHeight()) {
         if (getWidth() == compareTo.getWidth()) {
           if (this.icon.getIconName() == null) {
             return (compareTo.icon.getIconName() == null) ? 0 : -1;
           }
           
           Collator collator = I18nUtils.getLocaleAwareCollator();
           return collator.compare(this.icon.getIconName(), compareTo.icon.getIconName());
         } 
         
         var2 = (getWidth() < compareTo.getWidth()) ? 1 : -1;
       } else {
         var2 = (getHeight() < compareTo.getHeight()) ? 1 : -1;
       } 
       
       return var2;
     }
   }
   
   public class Slot {
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
 }

