 package cn.magicst.mamiyaotaru.voxelmap.util;
 
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IDimensionManager;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IVoxelMap;
 import java.text.Collator;
 import java.util.ArrayList;
 import java.util.Collections;
 import net.minecraft.class_1937;
 import net.minecraft.class_2378;
 import net.minecraft.class_2874;
 import net.minecraft.class_2960;
 import net.minecraft.class_310;
 import net.minecraft.class_5321;
 
 public class DimensionManager
   implements IDimensionManager {
   IVoxelMap master;
   public ArrayList<DimensionContainer> dimensions;
   private ArrayList<class_5321> vanillaWorlds = new ArrayList<>();
   
   public DimensionManager(IVoxelMap master) {
     this.master = master;
     this.dimensions = new ArrayList<>();
     this.vanillaWorlds.add(class_1937.field_25179);
     this.vanillaWorlds.add(class_1937.field_25180);
     this.vanillaWorlds.add(class_1937.field_25181);
   }
 
   
   public ArrayList getDimensions() {
     return this.dimensions;
   }
 
   
   public void populateDimensions(class_1937 world) {
     this.dimensions.clear();
     class_2378 dimensionTypeRegistry = class_310.method_1551().method_1562().method_29091().method_30530(class_2378.field_25095);
     
     for (class_5321 vanillaWorldKey : this.vanillaWorlds) {
       class_5321 typeKey = class_5321.method_29179(class_2378.field_25095, vanillaWorldKey.method_29177());
       class_2874 dimensionType = (class_2874)dimensionTypeRegistry.method_29107(typeKey);
       DimensionContainer dimensionContainer = new DimensionContainer(dimensionType, vanillaWorldKey.method_29177().method_12832(), vanillaWorldKey.method_29177());
       this.dimensions.add(dimensionContainer);
     } 
     
     sort();
   }
 
   
   public void enteredWorld(class_1937 world) {
     class_2960 resourceLocation = world.method_27983().method_29177();
     DimensionContainer dim = getDimensionContainerByResourceLocation(resourceLocation);
     if (dim == null) {
       dim = new DimensionContainer(world.method_8597(), resourceLocation.method_12832(), resourceLocation);
       this.dimensions.add(dim);
       sort();
     } 
     
     if (dim.type == null) {
       try {
         dim.type = world.method_8597();
       } catch (Exception exception) {}
     }
   }
 
 
   
   private void sort() {
     Collator collator = I18nUtils.getLocaleAwareCollator();
     Collections.sort(this.dimensions, (dim1, dim2) -> dim1.resourceLocation.equals(class_1937.field_25179.method_29177()) ? -1 : (
 
         
         (dim1.resourceLocation.equals(class_1937.field_25180.method_29177()) && !dim2.resourceLocation.equals(class_1937.field_25179.method_29177())) ? -1 : (
 
         
         (dim1.resourceLocation.equals(class_1937.field_25181.method_29177()) && !dim2.resourceLocation.equals(class_1937.field_25179.method_29177()) && !dim2.resourceLocation.equals(class_1937.field_25180.method_29177())) ? -1 : collator.compare(dim1.name, dim2.name))));
   }
 
 
 
   
   public DimensionContainer getDimensionContainerByWorld(class_1937 world) {
     class_2960 resourceLocation = world.method_27983().method_29177();
     DimensionContainer dim = getDimensionContainerByResourceLocation(resourceLocation);
     if (dim == null) {
       dim = new DimensionContainer(world.method_8597(), resourceLocation.method_12832(), resourceLocation);
       this.dimensions.add(dim);
       sort();
     } 
     
     return dim;
   }
 
   
   public DimensionContainer getDimensionContainerByIdentifier(String ident) {
     DimensionContainer dim = null;
     class_2960 resourceLocation = new class_2960(ident);
     dim = getDimensionContainerByResourceLocation(resourceLocation);
     if (dim == null) {
       dim = new DimensionContainer((class_2874)null, resourceLocation.method_12832(), resourceLocation);
       this.dimensions.add(dim);
       sort();
     } 
     
     return dim;
   }
 
   
   public DimensionContainer getDimensionContainerByResourceLocation(class_2960 resourceLocation) {
     for (DimensionContainer dim : this.dimensions) {
       if (resourceLocation.equals(dim.resourceLocation)) {
         return dim;
       }
     } 
     
     return null;
   }
 }

