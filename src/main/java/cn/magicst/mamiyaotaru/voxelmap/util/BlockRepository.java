 package cn.magicst.mamiyaotaru.voxelmap.util;
 
 import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
 import java.util.Arrays;
 import java.util.HashSet;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.locks.ReadWriteLock;
 import java.util.concurrent.locks.ReentrantReadWriteLock;
 import net.minecraft.class_2246;
 import net.minecraft.class_2248;
 import net.minecraft.class_2378;
 import net.minecraft.class_2667;
 import net.minecraft.class_2680;
 import net.minecraft.class_3610;
 import net.minecraft.class_3612;
 
 
 
 public class BlockRepository
 {
   public static class_2248 air = class_2246.field_10124;
   public static class_2248 voidAir;
   public static class_2248 caveAir;
   public static int airID = 0;
   public static int voidAirID = 0;
   public static int caveAirID = 0;
   public static class_2667 pistonTechBlock;
   public static class_2248 water;
   public static class_2248 lava;
   public static class_2248 ice;
   public static class_2248 grassBlock;
   public static class_2248 oakLeaves;
   public static class_2248 spruceLeaves;
   public static class_2248 birchLeaves;
   public static class_2248 jungleLeaves;
   public static class_2248 acaciaLeaves;
   public static class_2248 darkOakLeaves;
   public static class_2248 grass;
   public static class_2248 fern;
   public static class_2248 tallGrass;
   public static class_2248 largeFern;
   public static class_2248 reeds;
   public static class_2248 vine;
   public static class_2248 lilypad;
   public static class_2248 tallFlower;
   public static class_2248 cobweb;
   public static class_2248 stickyPiston;
   public static class_2248 piston;
   public static class_2248 redstone;
   public static class_2248 ladder;
   public static class_2248 barrier;
   public static class_2248 chorusPlant;
   public static class_2248 chorusFlower;
   public static class_3610 dry = class_3612.field_15906.method_15785();
   public static HashSet<class_2248> biomeBlocks;
   public static class_2248[] biomeBlocksArray = new class_2248[] { grassBlock, oakLeaves, spruceLeaves, birchLeaves, jungleLeaves, acaciaLeaves, darkOakLeaves, grass, fern, tallGrass, largeFern, reeds, vine, lilypad, tallFlower, water };
   public static HashSet<class_2248> shapedBlocks;
   public static class_2248[] shapedBlocksArray = new class_2248[] { ladder, vine };
   private static final ConcurrentHashMap<class_2680, Integer> stateToInt = new ConcurrentHashMap<>(1024);
   private static final ReferenceArrayList<class_2680> blockStates = new ReferenceArrayList(16384);
   private static int count = 1;
   private static final ReadWriteLock incrementLock = new ReentrantReadWriteLock();
   
   public static void getBlocks() {
     air = class_2246.field_10124;
     airID = getStateId(air.method_9564());
     voidAir = class_2246.field_10243;
     voidAirID = getStateId(voidAir.method_9564());
     caveAir = class_2246.field_10543;
     caveAirID = getStateId(caveAir.method_9564());
     pistonTechBlock = (class_2667)class_2246.field_10008;
     water = class_2246.field_10382;
     lava = class_2246.field_10164;
     ice = class_2246.field_10295;
     grassBlock = class_2246.field_10219;
     oakLeaves = class_2246.field_10503;
     spruceLeaves = class_2246.field_9988;
     birchLeaves = class_2246.field_10539;
     jungleLeaves = class_2246.field_10335;
     acaciaLeaves = class_2246.field_10098;
     darkOakLeaves = class_2246.field_10035;
     grass = class_2246.field_10479;
     fern = class_2246.field_10112;
     tallGrass = class_2246.field_10214;
     largeFern = class_2246.field_10313;
     reeds = class_2246.field_10424;
     vine = class_2246.field_10597;
     lilypad = class_2246.field_10588;
     cobweb = class_2246.field_10343;
     stickyPiston = class_2246.field_10615;
     piston = class_2246.field_10560;
     redstone = class_2246.field_10091;
     ladder = class_2246.field_9983;
     barrier = class_2246.field_10499;
     chorusPlant = class_2246.field_10021;
     chorusFlower = class_2246.field_10528;
     biomeBlocksArray = new class_2248[] { grassBlock, oakLeaves, spruceLeaves, birchLeaves, jungleLeaves, acaciaLeaves, darkOakLeaves, grass, fern, tallGrass, largeFern, reeds, vine, lilypad, tallFlower, water };
     biomeBlocks = new HashSet<>(Arrays.asList(biomeBlocksArray));
     shapedBlocksArray = new class_2248[] { ladder, vine };
     shapedBlocks = new HashSet<>(Arrays.asList(shapedBlocksArray));
     
     for (class_2248 block : class_2378.field_11146) {
       if (block instanceof net.minecraft.class_2323 || block instanceof net.minecraft.class_2478) {
         shapedBlocks.add(block);
       }
     } 
   }
 
   
   public static int getStateId(class_2680 blockState) {
     Integer id = stateToInt.get(blockState);
     if (id == null) {
       synchronized (incrementLock) {
         id = stateToInt.get(blockState);
         if (id == null) {
           id = Integer.valueOf(count);
           blockStates.add(blockState);
           stateToInt.put(blockState, id);
           count++;
         } 
       } 
     }
     
     return id.intValue();
   }
   
   public static class_2680 getStateById(int id) {
     return (class_2680)blockStates.get(id);
   }
   
   static {
     class_2680 airBlockState = class_2246.field_10124.method_9564();
     stateToInt.put(airBlockState, Integer.valueOf(0));
     blockStates.add(airBlockState);
   }
 }
