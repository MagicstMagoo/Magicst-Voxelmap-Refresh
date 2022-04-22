 package cn.magicst.mamiyaotaru.voxelmap.util;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileReader;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.io.PrintWriter;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Random;
 import java.util.TreeMap;
 import net.minecraft.class_156;
 import net.minecraft.class_1959;
 import net.minecraft.class_1972;
 import net.minecraft.class_2378;
 import net.minecraft.class_2960;
 import net.minecraft.class_310;
 import net.minecraft.class_5458;
 
 public class BiomeRepository
 {
   public static class_1959 DEFAULT;
   public static class_1959 FOREST;
   public static class_1959 SWAMP;
   public static class_1959 SWAMP_HILLS;
   private static Random generator = new Random();
   private static HashMap IDtoColor = new HashMap<>(256);
   private static TreeMap<String, Integer> nameToColor = new TreeMap<>();
   private static boolean dirty = false;
   
   public static void getBiomes() {
     DEFAULT = (class_1959)class_5458.field_25933.method_29107(class_1972.field_9423);
     FOREST = (class_1959)class_5458.field_25933.method_29107(class_1972.field_9409);
     SWAMP = (class_1959)class_5458.field_25933.method_29107(class_1972.field_9471);
     SWAMP_HILLS = (class_1959)class_5458.field_25933.method_29107(class_1972.field_9471);
   }
   
   public static void loadBiomeColors() {
     File saveDir = new File((class_310.method_1551()).field_1697, "/voxelmap/");
     File settingsFile = new File(saveDir, "biomecolors.txt");
     if (settingsFile.exists()) {
       try {
         BufferedReader br = new BufferedReader(new FileReader(settingsFile));
         
         String sCurrentLine;
         while ((sCurrentLine = br.readLine()) != null) {
           String[] curLine = sCurrentLine.split("=");
           if (curLine.length == 2) {
             String name = curLine[0];
             int color = 0;
             
             try {
               color = Integer.decode(curLine[1]).intValue();
             } catch (NumberFormatException var10) {
               System.out.println("Error decoding integer string for biome colors; " + curLine[1]);
               color = 0;
             } 
             
             if (nameToColor.put(name, Integer.valueOf(color)) != null) {
               dirty = true;
             }
           } 
         } 
         
         br.close();
       } catch (Exception var12) {
         System.err.println("biome load error: " + var12.getLocalizedMessage());
         var12.printStackTrace();
       } 
     }
     
     try {
       InputStream is = class_310.method_1551().method_1478().method_14486(new class_2960("voxelmap", "conf/biomecolors.txt")).method_14482();
       BufferedReader br = new BufferedReader(new InputStreamReader(is));
       
       String sCurrentLine;
       while ((sCurrentLine = br.readLine()) != null) {
         String[] curLine = sCurrentLine.split("=");
         if (curLine.length == 2) {
           String name = curLine[0];
           int color = 0;
           
           try {
             color = Integer.decode(curLine[1]).intValue();
           } catch (NumberFormatException var9) {
             System.out.println("Error decoding integer string for biome colors; " + curLine[1]);
             color = 0;
           } 
           
           if (nameToColor.get(name) == null) {
             nameToColor.put(name, Integer.valueOf(color));
             dirty = true;
           } 
         } 
       } 
       
       br.close();
       is.close();
     } catch (IOException var11) {
       System.out.println("Error loading biome color config file from litemod!");
       var11.printStackTrace();
     } 
   }
 
   
   public static void saveBiomeColors() {
     if (dirty) {
       File saveDir = new File((class_310.method_1551()).field_1697, "/voxelmap/");
       if (!saveDir.exists()) {
         saveDir.mkdirs();
       }
       
       File settingsFile = new File(saveDir, "biomecolors.txt");
       
       try {
         PrintWriter out = new PrintWriter(new FileWriter(settingsFile));
         
         for (Map.Entry<String, Integer> entry : nameToColor.entrySet()) {
           String name = (String)entry.getKey();
           Integer color = (Integer)entry.getValue();
           String hexColor = Integer.toHexString(color.intValue());
           
           while (hexColor.length() < 6) {
             hexColor = "0" + hexColor;
           }
           
           hexColor = "0x" + hexColor;
           out.println(name + "=" + name);
         } 
         
         out.close();
       } catch (Exception var8) {
         System.err.println("biome save error: " + var8.getLocalizedMessage());
         var8.printStackTrace();
       } 
     } 
     
     dirty = false;
   }
   
   public static int getBiomeColor(int biomeID) {
     Integer color = (Integer)IDtoColor.get(Integer.valueOf(biomeID));
     if (color == null) {
       class_1959 biome = (class_1959)(class_310.method_1551()).field_1687.method_30349().method_30530(class_2378.field_25114).method_10200(biomeID);
       if (biome != null) {
         String identifier = (class_310.method_1551()).field_1687.method_30349().method_30530(class_2378.field_25114).method_10221(biome).toString();
         color = nameToColor.get(identifier);
         if (color == null) {
           String friendlyName = getName(biome);
           color = nameToColor.get(friendlyName);
           if (color != null) {
             nameToColor.remove(friendlyName);
             nameToColor.put(identifier, color);
             dirty = true;
           } 
         } 
         
         if (color == null) {
           int r = generator.nextInt(255);
           int g = generator.nextInt(255);
           int b = generator.nextInt(255);
           color = Integer.valueOf(r << 16 | g << 8 | b);
           nameToColor.put(identifier, color);
           dirty = true;
         } 
       } else {
         System.out.println("non biome");
         color = Integer.valueOf(0);
       } 
       
       IDtoColor.put(Integer.valueOf(biomeID), color);
     } 
     
     return color.intValue();
   }
   
   private static String getName(class_1959 biome) {
     class_2960 resourceLocation = (class_310.method_1551()).field_1687.method_30349().method_30530(class_2378.field_25114).method_10221(biome);
     String translationKey = class_156.method_646("biome", resourceLocation);
     String name = I18nUtils.getString(translationKey, new Object[0]);
     if (name.equals(translationKey)) {
       name = TextUtils.prettify(resourceLocation.method_12832().toString());
     }
     
     return name;
   }
   
   public static String getName(int biomeID) {
     String name = null;
     class_1959 biome = (class_1959)(class_310.method_1551()).field_1687.method_30349().method_30530(class_2378.field_25114).method_10200(biomeID);
     if (biome != null) {
       name = getName(biome);
     }
     
     if (name == null) {
       name = "Unknown";
     }
     
     return name;
   }
 }

