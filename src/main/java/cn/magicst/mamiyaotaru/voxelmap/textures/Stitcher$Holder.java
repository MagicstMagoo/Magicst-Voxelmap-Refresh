 package cn.magicst.mamiyaotaru.voxelmap.textures;
 
 import cn.magicst.mamiyaotaru.voxelmap.util.I18nUtils;
 import java.text.Collator;
 
 public class Holder
   implements Comparable<Stitcher.Holder>
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

