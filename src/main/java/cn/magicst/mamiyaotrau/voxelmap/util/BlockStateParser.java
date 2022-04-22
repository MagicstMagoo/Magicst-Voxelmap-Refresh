 package cn.magicst.mamiyaotaru.voxelmap.util;
 
 import com.google.common.collect.BiMap;
 import java.util.Optional;
 import net.minecraft.class_2246;
 import net.minecraft.class_2248;
 import net.minecraft.class_2378;
 import net.minecraft.class_2680;
 import net.minecraft.class_2769;
 import net.minecraft.class_2960;
 
 public class BlockStateParser
 {
   public static void parseLine(String line, BiMap map) {
     String[] lineParts = line.split(" ");
     int id = Integer.parseInt(lineParts[0]);
     class_2680 blockState = parseStateString(lineParts[1]);
     if (blockState != null) {
       map.forcePut(blockState, Integer.valueOf(id));
     }
   }
 
   
   private static class_2680 parseStateString(String stateString) {
     class_2680 blockState = null;
     int bracketIndex = stateString.indexOf("[");
     String resourceString = stateString.substring(0, (bracketIndex == -1) ? stateString.length() : bracketIndex);
     int curlyBracketOpenIndex = resourceString.indexOf("{");
     int curlyBracketCloseIndex = resourceString.indexOf("}");
     resourceString = resourceString.substring((curlyBracketOpenIndex == -1) ? 0 : (curlyBracketOpenIndex + 1), (curlyBracketCloseIndex == -1) ? resourceString.length() : curlyBracketCloseIndex);
     String[] resourceStringParts = resourceString.split(":");
     class_2960 resourceLocation = null;
     if (resourceStringParts.length == 1) {
       resourceLocation = new class_2960(resourceStringParts[0]);
     } else if (resourceStringParts.length == 2) {
       resourceLocation = new class_2960(resourceStringParts[0], resourceStringParts[1]);
     } 
     
     class_2248 block = (class_2248)class_2378.field_11146.method_10223(resourceLocation);
     if (block != class_2246.field_10124 || resourceString.equals("minecraft:air")) {
       blockState = block.method_9564();
       if (bracketIndex != -1) {
         String propertiesString = stateString.substring(stateString.indexOf("[") + 1, stateString.lastIndexOf("]"));
         String[] propertiesStringParts = propertiesString.split(",");
         
         for (int t = 0; t < propertiesStringParts.length; t++) {
           String[] propertyStringParts = propertiesStringParts[t].split("=");
           class_2769 property = block.method_9595().method_11663(propertyStringParts[0]);
           if (property != null) {
             blockState = withValue(blockState, property, propertyStringParts[1]);
           }
         } 
       } 
     } 
     
     return blockState;
   }
   
   private static class_2680 withValue(class_2680 blockState, class_2769 property, String valueString) {
     Optional<Comparable> value = property.method_11900(valueString);
     if (value.isPresent()) {
       blockState = (class_2680)blockState.method_11657(property, value.get());
     }
     
     return blockState;
   }
 }
