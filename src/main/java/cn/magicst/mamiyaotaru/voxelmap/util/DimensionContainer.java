 package cn.magicst.mamiyaotaru.voxelmap.util;
 
 import java.text.Collator;
 import net.minecraft.class_2874;
 import net.minecraft.class_2960;
 
 public class DimensionContainer
   implements Comparable<DimensionContainer> {
   public class_2874 type;
   public String name = "notLoaded";
   public class_2960 resourceLocation;
   private static final Collator collator = I18nUtils.getLocaleAwareCollator();
   
   public DimensionContainer(class_2874 type, String name, class_2960 resourceLocation) {
     this.type = type;
     this.name = name;
     this.resourceLocation = resourceLocation;
   }
   
   public String getStorageName() {
     String storageName = null;
     if (this.resourceLocation != null) {
       if (this.resourceLocation.method_12836().equals("minecraft")) {
         storageName = this.resourceLocation.method_12832();
       } else {
         storageName = this.resourceLocation.toString();
       } 
     } else {
       storageName = "UNKNOWN";
     } 
     
     return storageName;
   }
   
   public String getDisplayName() {
     return TextUtils.prettify(this.name);
   }
   
   public int compareTo(DimensionContainer other) {
     return collator.compare(this.name, other.name);
   }
 }
