 package cn.magicst.mamiyaotaru.voxelmap.persistent;
 
 import cn.magicst.mamiyaotaru.voxelmap.MapSettingsManager;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.AbstractMapData;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IChangeObserver;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IColorManager;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IPersistentMap;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.ISettingsAndLightingChangeNotifier;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IVoxelMap;
 import cn.magicst.mamiyaotaru.voxelmap.util.BiomeRepository;
 import cn.magicst.mamiyaotaru.voxelmap.util.BlockRepository;
 import cn.magicst.mamiyaotaru.voxelmap.util.ColorUtils;
 import cn.magicst.mamiyaotaru.voxelmap.util.GameVariableAccessShim;
 import cn.magicst.mamiyaotaru.voxelmap.util.MapChunkCache;
 import cn.magicst.mamiyaotaru.voxelmap.util.MapUtils;
 import cn.magicst.mamiyaotaru.voxelmap.util.MutableBlockPos;
 import cn.magicst.mamiyaotaru.voxelmap.util.TextUtils;
 import cn.magicst.mamiyaotaru.voxelmap.util.TickCounter;
 import java.io.File;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.Iterator;
 import java.util.List;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ConcurrentLinkedQueue;
 import net.minecraft.class_1922;
 import net.minecraft.class_1937;
 import net.minecraft.class_1944;
 import net.minecraft.class_2246;
 import net.minecraft.class_2248;
 import net.minecraft.class_2338;
 import net.minecraft.class_2350;
 import net.minecraft.class_2378;
 import net.minecraft.class_259;
 import net.minecraft.class_265;
 import net.minecraft.class_2680;
 import net.minecraft.class_2818;
 import net.minecraft.class_2902;
 import net.minecraft.class_310;
 import net.minecraft.class_3610;
 import net.minecraft.class_3612;
 import net.minecraft.class_3614;
 import net.minecraft.class_638;
 
 public class PersistentMap
   implements IPersistentMap, IChangeObserver {
   IVoxelMap master;
   MutableBlockPos blockPos = new MutableBlockPos(0, 0, 0);
   IColorManager colorManager;
   MapSettingsManager mapOptions;
   PersistentMapSettingsManager options;
   WorldMatcher worldMatcher;
   int[] lightmapColors;
   class_638 world;
   String subworldName = "";
   protected final List<CachedRegion> cachedRegionsPool = Collections.synchronizedList(new ArrayList<>());
   protected ConcurrentHashMap cachedRegions = new ConcurrentHashMap<>(150, 0.9F, 2);
   int lastLeft = 0;
   int lastRight = 0;
   int lastTop = 0;
   int lastBottom = 0;
   CachedRegion[] lastRegionsArray = new CachedRegion[0]; Comparator ageThenDistanceSorter; Comparator distanceSorter; public PersistentMap(IVoxelMap master) {
     this.ageThenDistanceSorter = ((region1, region2) -> {
         long mostRecentAccess1 = region1.getMostRecentView();
         long mostRecentAccess2 = region2.getMostRecentView();
         if (mostRecentAccess1 < mostRecentAccess2) {
           return 1;
         }
         if (mostRecentAccess1 > mostRecentAccess2) {
           return -1;
         }
         double distance1sq = ((region1.getX() * 256 + region1.getWidth() / 2 - this.options.mapX) * (region1.getX() * 256 + region1.getWidth() / 2 - this.options.mapX) + (region1.getZ() * 256 + region1.getWidth() / 2 - this.options.mapZ) * (region1.getZ() * 256 + region1.getWidth() / 2 - this.options.mapZ));
         double distance2sq = ((region2.getX() * 256 + region2.getWidth() / 2 - this.options.mapX) * (region2.getX() * 256 + region2.getWidth() / 2 - this.options.mapX) + (region2.getZ() * 256 + region2.getWidth() / 2 - this.options.mapZ) * (region2.getZ() * 256 + region2.getWidth() / 2 - this.options.mapZ));
         return Double.compare(distance1sq, distance2sq);
       });
     this.distanceSorter = ((coordinates1, coordinates2) -> {
         double distance1sq = ((coordinates1.x * 256 + 128 - this.options.mapX) * (coordinates1.x * 256 + 128 - this.options.mapX) + (coordinates1.z * 256 + 128 - this.options.mapZ) * (coordinates1.z * 256 + 128 - this.options.mapZ));
         double distance2sq = ((coordinates2.x * 256 + 128 - this.options.mapX) * (coordinates2.x * 256 + 128 - this.options.mapX) + (coordinates2.z * 256 + 128 - this.options.mapZ) * (coordinates2.z * 256 + 128 - this.options.mapZ));
         return Double.compare(distance1sq, distance2sq);
       });
     this.queuedChangedChunks = false;
     
     this.chunkUpdateQueue = new ConcurrentLinkedQueue();
 
     
     this.master = master;
     this.colorManager = master.getColorManager();
     this.mapOptions = master.getMapOptions();
     this.options = master.getPersistentMapOptions();
     this.lightmapColors = new int[256];
     Arrays.fill(this.lightmapColors, -16777216);
   }
   private boolean queuedChangedChunks; private MapChunkCache chunkCache; private final ConcurrentLinkedQueue chunkUpdateQueue;
   
   public void newWorld(class_638 world) {
     this.subworldName = "";
     purgeCachedRegions();
     this.queuedChangedChunks = false;
     this.chunkUpdateQueue.clear();
     this.world = world;
     if (this.worldMatcher != null) {
       this.worldMatcher.cancel();
     }
     
     if (world != null) {
       newWorldStuff();
     } else {
       Thread pauseForSubworldNamesThread = new Thread(null, null, "VoxelMap Pause for Subworld Name Thread") {
           public void run() {
             try {
               Thread.sleep(2000L);
             } catch (InterruptedException var2) {
               var2.printStackTrace();
             } 
             
             if (PersistentMap.this.world != null) {
               PersistentMap.this.newWorldStuff();
             }
           }
         };
       
       pauseForSubworldNamesThread.start();
     } 
   }
 
   
   private void newWorldStuff() {
     String worldName = TextUtils.scrubNameFile(this.master.getWaypointManager().getCurrentWorldName());
     File oldCacheDir = new File((class_310.method_1551()).field_1697, "/mods/mamiyaotaru/voxelmap/cache/" + worldName + "/");
     if (oldCacheDir.exists() && oldCacheDir.isDirectory()) {
       File newCacheDir = new File((class_310.method_1551()).field_1697, "/voxelmap/cache/" + worldName + "/");
       newCacheDir.getParentFile().mkdirs();
       boolean success = oldCacheDir.renameTo(newCacheDir);
       if (!success) {
         System.out.println("Failed moving Voxelmap cache files.  Please move " + oldCacheDir.getPath() + " to " + newCacheDir.getPath());
       } else {
         System.out.println("Moved Voxelmap cache files from " + oldCacheDir.getPath() + " to " + newCacheDir.getPath());
       } 
     } 
     
     if (this.master.getWaypointManager().isMultiworld() && !class_310.method_1551().method_1542() && !this.master.getWaypointManager().receivedAutoSubworldName()) {
       this.worldMatcher = new WorldMatcher(this.master, this, this.world);
       this.worldMatcher.findMatch();
     } 
     
     this.chunkCache = new MapChunkCache(33, 33, this);
   }
 
   
   public void onTick(class_310 mc) {
     if (mc.field_1755 == null) {
       this.options.mapX = GameVariableAccessShim.xCoord();
       this.options.mapZ = GameVariableAccessShim.zCoord();
     } 
     
     if (!this.master.getWaypointManager().getCurrentSubworldDescriptor(false).equals(this.subworldName)) {
       this.subworldName = this.master.getWaypointManager().getCurrentSubworldDescriptor(false);
       if (this.worldMatcher != null && !this.subworldName.equals("")) {
         this.worldMatcher.cancel();
       }
       
       purgeCachedRegions();
     } 
     
     if (this.queuedChangedChunks) {
       this.queuedChangedChunks = false;
       prunePool();
     } 
     
     if (this.world != null) {
       this.chunkCache.centerChunks((class_2338)this.blockPos.withXYZ(GameVariableAccessShim.xCoord(), 0, GameVariableAccessShim.zCoord()));
       this.chunkCache.checkIfChunksBecameSurroundedByLoaded();
       
       while (!this.chunkUpdateQueue.isEmpty() && Math.abs(TickCounter.tickCounter - ((ChunkWithAge)this.chunkUpdateQueue.peek()).tick) >= 20) {
         doProcessChunk(((ChunkWithAge)this.chunkUpdateQueue.remove()).chunk);
       }
     } 
   }
 
 
   
   public PersistentMapSettingsManager getOptions() {
     return this.options;
   }
 
   
   public void purgeCachedRegions() {
     synchronized (this.cachedRegionsPool) {
       for (CachedRegion cachedRegion : this.cachedRegionsPool) {
         cachedRegion.cleanup();
       }
       
       this.cachedRegions.clear();
       this.cachedRegionsPool.clear();
       getRegions(0, -1, 0, -1);
     } 
   }
 
   
   public void renameSubworld(String oldName, String newName) {
     synchronized (this.cachedRegionsPool) {
       for (CachedRegion cachedRegion : this.cachedRegionsPool) {
         cachedRegion.renameSubworld(oldName, newName);
       }
     } 
   }
 
 
   
   public ISettingsAndLightingChangeNotifier getSettingsAndLightingChangeNotifier() {
     return this.master.getSettingsAndLightingChangeNotifier();
   }
 
   
   public void setLightMapArray(int[] lightmapColors) {
     boolean changed = false;
     int torchOffset = 0;
     int skylightMultiplier = 16;
     
     for (int t = 0; t < 16; t++) {
       if (lightmapColors[t * skylightMultiplier + torchOffset] != this.lightmapColors[t * skylightMultiplier + torchOffset]) {
         changed = true;
         
         break;
       } 
     } 
     System.arraycopy(lightmapColors, 0, this.lightmapColors, 0, 256);
     if (changed) {
       getSettingsAndLightingChangeNotifier().notifyOfChanges();
     }
   }
 
 
 
   
   public void getAndStoreData(AbstractMapData mapData, class_1937 world, class_2818 chunk, MutableBlockPos blockPos, boolean underground, int startX, int startZ, int imageX, int imageY) {
     int biomeID, seafloorHeight = 0;
     int transparentHeight = 0;
     int foliageHeight = 0;
     
     class_2680 transparentBlockState = BlockRepository.air.method_9564();
     class_2680 foliageBlockState = BlockRepository.air.method_9564();
     class_2680 seafloorBlockState = BlockRepository.air.method_9564();
     blockPos = blockPos.withXYZ(startX + imageX, 64, startZ + imageY);
     
     if (!chunk.method_12223()) {
       biomeID = world.method_30349().method_30530(class_2378.field_25114).method_10206(world.method_23753((class_2338)blockPos).comp_349());
     } else {
       biomeID = -1;
     } 
     
     mapData.setBiomeID(imageX, imageY, biomeID);
     if (biomeID != -1) {
       int surfaceHeight; class_2680 surfaceBlockState; boolean solid = false;
       if (underground) {
         surfaceHeight = getNetherHeight(chunk, startX + imageX, startZ + imageY);
         surfaceBlockState = chunk.method_8320((class_2338)blockPos.withXYZ(startX + imageX, surfaceHeight - 1, startZ + imageY));
         if (surfaceHeight != -1) {
           foliageHeight = surfaceHeight + 1;
           blockPos.setXYZ(startX + imageX, foliageHeight - 1, startZ + imageY);
           foliageBlockState = chunk.method_8320((class_2338)blockPos);
           class_3614 material = foliageBlockState.method_26207();
           if (material == class_3614.field_15948 || material == class_3614.field_15959 || material == class_3614.field_15922 || material == class_3614.field_15920) {
             foliageHeight = 0;
           }
         } 
       } else {
         transparentHeight = chunk.method_12005(class_2902.class_2903.field_13197, blockPos.method_10263() & 0xF, blockPos.method_10260() & 0xF) + 1;
         transparentBlockState = chunk.method_8320((class_2338)blockPos.withXYZ(startX + imageX, transparentHeight - 1, startZ + imageY));
         class_3610 fluidState = transparentBlockState.method_26227();
         if (fluidState != class_3612.field_15906.method_15785()) {
           transparentBlockState = fluidState.method_15759();
         }
         
         surfaceHeight = transparentHeight;
         surfaceBlockState = transparentBlockState;
         
         boolean hasOpacity = (transparentBlockState.method_26193((class_1922)world, (class_2338)blockPos) > 0);
         if (!hasOpacity && transparentBlockState.method_26225() && transparentBlockState.method_26211()) {
           class_265 voxelShape = transparentBlockState.method_26173((class_1922)world, (class_2338)blockPos, class_2350.field_11033);
           hasOpacity = class_259.method_20713(voxelShape, class_259.method_1073());
           voxelShape = transparentBlockState.method_26173((class_1922)world, (class_2338)blockPos, class_2350.field_11036);
           hasOpacity = (hasOpacity || class_259.method_20713(class_259.method_1073(), voxelShape));
         } 
         
         while (!hasOpacity && surfaceHeight > 0) {
           foliageBlockState = surfaceBlockState;
           surfaceHeight--;
           surfaceBlockState = chunk.method_8320((class_2338)blockPos.withXYZ(startX + imageX, surfaceHeight - 1, startZ + imageY));
           fluidState = surfaceBlockState.method_26227();
           if (fluidState != class_3612.field_15906.method_15785()) {
             surfaceBlockState = fluidState.method_15759();
           }
           
           hasOpacity = (surfaceBlockState.method_26193((class_1922)world, (class_2338)blockPos) > 0);
           if (!hasOpacity && surfaceBlockState.method_26225() && surfaceBlockState.method_26211()) {
             class_265 voxelShape = surfaceBlockState.method_26173((class_1922)world, (class_2338)blockPos, class_2350.field_11033);
             hasOpacity = class_259.method_20713(voxelShape, class_259.method_1073());
             voxelShape = surfaceBlockState.method_26173((class_1922)world, (class_2338)blockPos, class_2350.field_11036);
             hasOpacity = (hasOpacity || class_259.method_20713(class_259.method_1073(), voxelShape));
           } 
         } 
         
         if (surfaceHeight == transparentHeight) {
           transparentHeight = 0;
           transparentBlockState = BlockRepository.air.method_9564();
           foliageBlockState = chunk.method_8320((class_2338)blockPos.withXYZ(startX + imageX, surfaceHeight, startZ + imageY));
         } 
         
         if (foliageBlockState.method_26207() == class_3614.field_15948) {
           surfaceBlockState = foliageBlockState;
           foliageBlockState = BlockRepository.air.method_9564();
         } 
         
         if (foliageBlockState == transparentBlockState) {
           foliageBlockState = BlockRepository.air.method_9564();
         }
         
         if (foliageBlockState != null && foliageBlockState.method_26207() != class_3614.field_15959) {
           foliageHeight = surfaceHeight + 1;
         } else {
           foliageHeight = 0;
           foliageBlockState = BlockRepository.air.method_9564();
         } 
         
         class_3614 material = surfaceBlockState.method_26207();
         if (material == class_3614.field_15920 || material == class_3614.field_15958) {
           seafloorHeight = surfaceHeight;
           
           for (seafloorBlockState = chunk.method_8320((class_2338)blockPos.withXYZ(startX + imageX, surfaceHeight - 1, startZ + imageY)); seafloorBlockState.method_26193((class_1922)world, (class_2338)blockPos) < 5 && seafloorBlockState.method_26207() != class_3614.field_15923 && seafloorHeight > 1; seafloorBlockState = chunk.method_8320((class_2338)blockPos.withXYZ(startX + imageX, seafloorHeight - 1, startZ + imageY))) {
             material = seafloorBlockState.method_26207();
             if (transparentHeight == 0 && material != class_3614.field_15958 && material != class_3614.field_15920 && material.method_15801()) {
               transparentHeight = seafloorHeight;
               transparentBlockState = seafloorBlockState;
             } 
             
             if (foliageHeight == 0 && seafloorHeight != transparentHeight && transparentBlockState != seafloorBlockState && material != class_3614.field_15958 && material != class_3614.field_15920 && material != class_3614.field_15959 && material != class_3614.field_15915) {
               foliageHeight = seafloorHeight;
               foliageBlockState = seafloorBlockState;
             } 
             
             seafloorHeight--;
           } 
           
           if (seafloorBlockState.method_26207() == class_3614.field_15920) {
             seafloorBlockState = BlockRepository.air.method_9564();
           }
         } 
       } 
       
       mapData.setHeight(imageX, imageY, surfaceHeight);
       mapData.setBlockstate(imageX, imageY, surfaceBlockState);
       mapData.setTransparentHeight(imageX, imageY, transparentHeight);
       mapData.setTransparentBlockstate(imageX, imageY, transparentBlockState);
       mapData.setFoliageHeight(imageX, imageY, foliageHeight);
       mapData.setFoliageBlockstate(imageX, imageY, foliageBlockState);
       mapData.setOceanFloorHeight(imageX, imageY, seafloorHeight);
       mapData.setOceanFloorBlockstate(imageX, imageY, seafloorBlockState);
       if (surfaceHeight == -1) {
         surfaceHeight = 80;
         solid = true;
       } 
       
       if (surfaceBlockState.method_26207() == class_3614.field_15922) {
         solid = false;
       }
       
       int light = solid ? 0 : 255;
       if (!solid) {
         light = getLight(surfaceBlockState, world, blockPos, startX + imageX, startZ + imageY, surfaceHeight, solid);
       }
       
       mapData.setLight(imageX, imageY, light);
       int seafloorLight = 0;
       if (seafloorBlockState != null && seafloorBlockState != BlockRepository.air.method_9564()) {
         seafloorLight = getLight(seafloorBlockState, world, blockPos, startX + imageX, startZ + imageY, seafloorHeight, solid);
       }
       
       mapData.setOceanFloorLight(imageX, imageY, seafloorLight);
       int transparentLight = 0;
       if (transparentBlockState != null && transparentBlockState != BlockRepository.air.method_9564()) {
         transparentLight = getLight(transparentBlockState, world, blockPos, startX + imageX, startZ + imageY, transparentHeight, solid);
       }
       
       mapData.setTransparentLight(imageX, imageY, transparentLight);
       int foliageLight = 0;
       if (foliageBlockState != null && foliageBlockState != BlockRepository.air.method_9564()) {
         foliageLight = getLight(foliageBlockState, world, blockPos, startX + imageX, startZ + imageY, foliageHeight, solid);
       }
       
       mapData.setFoliageLight(imageX, imageY, foliageLight);
     } 
   }
   
   private int getNetherHeight(class_2818 chunk, int x, int z) {
     int y = 80;
     this.blockPos.setXYZ(x, y, z);
     class_2680 blockState = chunk.method_8320((class_2338)this.blockPos);
     if (blockState.method_26193((class_1922)this.world, (class_2338)this.blockPos) == 0 && blockState.method_26207() != class_3614.field_15922) {
       while (y > 0) {
         y--;
         this.blockPos.setXYZ(x, y, z);
         blockState = chunk.method_8320((class_2338)this.blockPos);
         if (blockState.method_26193((class_1922)this.world, (class_2338)this.blockPos) > 0 || blockState.method_26207() == class_3614.field_15922) {
           return y + 1;
         }
       } 
       
       return y;
     } 
     while (y <= 90) {
       y++;
       this.blockPos.setXYZ(x, y, z);
       blockState = chunk.method_8320((class_2338)this.blockPos);
       if (blockState.method_26193((class_1922)this.world, (class_2338)this.blockPos) == 0 && blockState.method_26207() != class_3614.field_15922) {
         return y;
       }
     } 
     
     return -1;
   }
 
   
   private int getLight(class_2680 blockState, class_1937 world, MutableBlockPos blockPos, int x, int z, int height, boolean solid) {
     int i3 = 255;
     if (solid) {
       i3 = 0;
     } else if (blockState != null && blockState.method_26207() != class_3614.field_15959) {
       blockPos.setXYZ(x, Math.max(Math.min(height, 255), 0), z);
       int blockLight = world.method_8314(class_1944.field_9282, (class_2338)blockPos) & 0xF;
       int skyLight = world.method_8314(class_1944.field_9284, (class_2338)blockPos);
       if (blockState.method_26207() == class_3614.field_15922 || blockState.method_26204() == class_2246.field_10092) {
         blockLight = 14;
       }
       
       i3 = blockLight + skyLight * 16;
     } 
     
     return i3;
   }
 
   
   public int getPixelColor(AbstractMapData mapData, class_638 world, MutableBlockPos blockPos, MutableBlockPos loopBlockPos, boolean underground, int multi, int startX, int startZ, int imageX, int imageY) {
     int mcX = startX + imageX;
     int mcZ = startZ + imageY;
     class_2680 surfaceBlockState = BlockRepository.air.method_9564();
     class_2680 transparentBlockState = BlockRepository.air.method_9564();
     class_2680 foliageBlockState = BlockRepository.air.method_9564();
     class_2680 seafloorBlockState = BlockRepository.air.method_9564();
     int surfaceHeight = 0;
     int seafloorHeight = 0;
     int transparentHeight = 0;
     int foliageHeight = 0;
     int surfaceColor = 0;
     int seafloorColor = 0;
     int transparentColor = 0;
     int foliageColor = 0;
     blockPos = blockPos.withXYZ(mcX, 0, mcZ);
     int color24 = 0;
     int biomeID = mapData.getBiomeID(imageX, imageY);
     surfaceBlockState = mapData.getBlockstate(imageX, imageY);
     if (surfaceBlockState != null && (surfaceBlockState.method_26204() != BlockRepository.air || mapData.getLight(imageX, imageY) != 0 || mapData.getHeight(imageX, imageY) != 0) && biomeID != -1 && biomeID != 255) {
       if (this.mapOptions.biomeOverlay == 1) {
         if (biomeID >= 0) {
           color24 = BiomeRepository.getBiomeColor(biomeID) | 0xFF000000;
         } else {
           color24 = 0;
         } 
         
         return MapUtils.doSlimeAndGrid(color24, mcX, mcZ);
       } 
       boolean solid = false;
       int blockStateID = 0;
       surfaceHeight = mapData.getHeight(imageX, imageY);
       blockStateID = BlockRepository.getStateId(surfaceBlockState);
       if (surfaceHeight == -1 || surfaceHeight == 255) {
         surfaceHeight = 80;
         solid = true;
       } 
       
       blockPos.setXYZ(mcX, surfaceHeight - 1, mcZ);
       if (surfaceBlockState.method_26207() == class_3614.field_15922) {
         solid = false;
       }
       
       if (this.mapOptions.biomes) {
         surfaceColor = this.colorManager.getBlockColor(blockPos, blockStateID, biomeID);
         int tint = -1;
         tint = this.colorManager.getBiomeTint(mapData, (class_1937)world, surfaceBlockState, blockStateID, blockPos, loopBlockPos, startX, startZ);
         if (tint != -1) {
           surfaceColor = ColorUtils.colorMultiplier(surfaceColor, tint);
         }
       } else {
         surfaceColor = this.colorManager.getBlockColorWithDefaultTint(blockPos, blockStateID);
       } 
       
       surfaceColor = applyHeight(mapData, surfaceColor, underground, multi, imageX, imageY, surfaceHeight, solid, 1);
       int light = mapData.getLight(imageX, imageY);
       if (solid) {
         surfaceColor = 0;
       } else if (this.mapOptions.lightmap) {
         int lightValue = getLight(light);
         surfaceColor = ColorUtils.colorMultiplier(surfaceColor, lightValue);
       } 
       
       if (this.mapOptions.waterTransparency && !solid) {
         seafloorHeight = mapData.getOceanFloorHeight(imageX, imageY);
         if (seafloorHeight > 0) {
           blockPos.setXYZ(mcX, seafloorHeight - 1, mcZ);
           seafloorBlockState = mapData.getOceanFloorBlockstate(imageX, imageY);
           if (seafloorBlockState != null && seafloorBlockState != BlockRepository.air.method_9564()) {
             blockStateID = BlockRepository.getStateId(seafloorBlockState);
             if (this.mapOptions.biomes) {
               seafloorColor = this.colorManager.getBlockColor(blockPos, blockStateID, biomeID);
               int tint = -1;
               tint = this.colorManager.getBiomeTint(mapData, (class_1937)world, seafloorBlockState, blockStateID, blockPos, loopBlockPos, startX, startZ);
               if (tint != -1) {
                 seafloorColor = ColorUtils.colorMultiplier(seafloorColor, tint);
               }
             } else {
               seafloorColor = this.colorManager.getBlockColorWithDefaultTint(blockPos, blockStateID);
             } 
             
             seafloorColor = applyHeight(mapData, seafloorColor, underground, multi, imageX, imageY, seafloorHeight, solid, 0);
             int seafloorLight = 255;
             seafloorLight = mapData.getOceanFloorLight(imageX, imageY);
             if (this.mapOptions.lightmap) {
               int lightValue = getLight(seafloorLight);
               seafloorColor = ColorUtils.colorMultiplier(seafloorColor, lightValue);
             } 
           } 
         } 
       } 
       
       if (this.mapOptions.blockTransparency && !solid) {
         transparentHeight = mapData.getTransparentHeight(imageX, imageY);
         if (transparentHeight > 0) {
           blockPos.setXYZ(mcX, transparentHeight - 1, mcZ);
           transparentBlockState = mapData.getTransparentBlockstate(imageX, imageY);
           if (transparentBlockState != null && transparentBlockState != BlockRepository.air.method_9564()) {
             blockStateID = BlockRepository.getStateId(transparentBlockState);
             if (this.mapOptions.biomes) {
               transparentColor = this.colorManager.getBlockColor(blockPos, blockStateID, biomeID);
               int tint = -1;
               tint = this.colorManager.getBiomeTint(mapData, (class_1937)world, transparentBlockState, blockStateID, blockPos, loopBlockPos, startX, startZ);
               if (tint != -1) {
                 transparentColor = ColorUtils.colorMultiplier(transparentColor, tint);
               }
             } else {
               transparentColor = this.colorManager.getBlockColorWithDefaultTint(blockPos, blockStateID);
             } 
             
             transparentColor = applyHeight(mapData, transparentColor, underground, multi, imageX, imageY, transparentHeight, solid, 3);
             int transparentLight = 255;
             transparentLight = mapData.getTransparentLight(imageX, imageY);
             if (this.mapOptions.lightmap) {
               int lightValue = getLight(transparentLight);
               transparentColor = ColorUtils.colorMultiplier(transparentColor, lightValue);
             } 
           } 
         } 
         
         foliageHeight = mapData.getFoliageHeight(imageX, imageY);
         if (foliageHeight > 0) {
           blockPos.setXYZ(mcX, foliageHeight - 1, mcZ);
           foliageBlockState = mapData.getFoliageBlockstate(imageX, imageY);
           if (foliageBlockState != null && foliageBlockState != BlockRepository.air.method_9564()) {
             blockStateID = BlockRepository.getStateId(foliageBlockState);
             if (this.mapOptions.biomes) {
               foliageColor = this.colorManager.getBlockColor(blockPos, blockStateID, biomeID);
               int tint = -1;
               tint = this.colorManager.getBiomeTint(mapData, (class_1937)world, foliageBlockState, blockStateID, blockPos, loopBlockPos, startX, startZ);
               if (tint != -1) {
                 foliageColor = ColorUtils.colorMultiplier(foliageColor, tint);
               }
             } else {
               foliageColor = this.colorManager.getBlockColorWithDefaultTint(blockPos, blockStateID);
             } 
             
             foliageColor = applyHeight(mapData, foliageColor, underground, multi, imageX, imageY, foliageHeight, solid, 2);
             int foliageLight = 255;
             foliageLight = mapData.getFoliageLight(imageX, imageY);
             if (this.mapOptions.lightmap) {
               int lightValue = getLight(foliageLight);
               foliageColor = ColorUtils.colorMultiplier(foliageColor, lightValue);
             } 
           } 
         } 
       } 
       
       if (this.mapOptions.waterTransparency && seafloorHeight > 0) {
         color24 = seafloorColor;
         if (foliageColor != 0 && foliageHeight <= surfaceHeight) {
           color24 = ColorUtils.colorAdder(foliageColor, seafloorColor);
         }
         
         if (transparentColor != 0 && transparentHeight <= surfaceHeight) {
           color24 = ColorUtils.colorAdder(transparentColor, color24);
         }
         
         color24 = ColorUtils.colorAdder(surfaceColor, color24);
       } else {
         color24 = surfaceColor;
       } 
       
       if (foliageColor != 0 && foliageHeight > surfaceHeight) {
         color24 = ColorUtils.colorAdder(foliageColor, color24);
       }
       
       if (transparentColor != 0 && transparentHeight > surfaceHeight) {
         color24 = ColorUtils.colorAdder(transparentColor, color24);
       }
       
       if (this.mapOptions.biomeOverlay == 2) {
         int bc = 0;
         if (biomeID >= 0) {
           bc = BiomeRepository.getBiomeColor(biomeID);
         }
         
         bc = 0x7F000000 | bc;
         color24 = ColorUtils.colorAdder(bc, color24);
       } 
       
       return MapUtils.doSlimeAndGrid(color24, mcX, mcZ);
     } 
     
     return 0;
   }
 
   
   private int applyHeight(AbstractMapData mapData, int color24, boolean underground, int multi, int imageX, int imageY, int height, boolean solid, int layer) {
     if (color24 != this.colorManager.getAirColor() && color24 != 0) {
       int heightComp = -1;
       if ((this.mapOptions.heightmap || this.mapOptions.slopemap) && !solid) {
         int diff = 0;
         double sc = 0.0D;
         boolean invert = false;
         if (!this.mapOptions.slopemap) {
           if (this.mapOptions.heightmap) {
             diff = height - 80;
             sc = Math.log10(Math.abs(diff) / 8.0D + 1.0D) / 1.8D;
             if (diff < 0) {
               sc = 0.0D - sc;
             }
           } 
         } else {
           if (imageX > 0 && imageY < 32 * multi - 1) {
             if (layer == 0) {
               heightComp = mapData.getOceanFloorHeight(imageX - 1, imageY + 1);
             }
             
             if (layer == 1) {
               heightComp = mapData.getHeight(imageX - 1, imageY + 1);
             }
             
             if (layer == 2) {
               heightComp = height;
             }
             
             if (layer == 3) {
               heightComp = mapData.getTransparentHeight(imageX - 1, imageY + 1);
               if (heightComp == -1) {
                 class_2680 transparentBlockState = mapData.getTransparentBlockstate(imageX, imageY);
                 if (transparentBlockState != null && transparentBlockState != BlockRepository.air.method_9564()) {
                   class_2248 block = transparentBlockState.method_26204();
                   if (block instanceof net.minecraft.class_2368 || block instanceof net.minecraft.class_2506) {
                     heightComp = mapData.getHeight(imageX - 1, imageY + 1);
                   }
                 } 
               } 
             } 
           } else if (imageX < 32 * multi - 1 && imageY > 0) {
             if (layer == 0) {
               heightComp = mapData.getOceanFloorHeight(imageX + 1, imageY - 1);
             }
             
             if (layer == 1) {
               heightComp = mapData.getHeight(imageX + 1, imageY - 1);
             }
             
             if (layer == 2) {
               heightComp = height;
             }
             
             if (layer == 3) {
               heightComp = mapData.getTransparentHeight(imageX + 1, imageY - 1);
               if (heightComp == -1) {
                 class_2680 transparentBlockState = mapData.getTransparentBlockstate(imageX, imageY);
                 if (transparentBlockState != null && transparentBlockState != BlockRepository.air.method_9564()) {
                   class_2248 block = transparentBlockState.method_26204();
                   if (block instanceof net.minecraft.class_2368 || block instanceof net.minecraft.class_2506) {
                     heightComp = mapData.getHeight(imageX + 1, imageY - 1);
                   }
                 } 
               } 
             } 
             
             invert = true;
           } else {
             heightComp = height;
           } 
           
           if (heightComp == -1) {
             heightComp = height;
           }
           
           if (!invert) {
             diff = heightComp - height;
           } else {
             diff = height - heightComp;
           } 
           
           if (diff != 0) {
             sc = (diff > 0) ? 1.0D : ((diff < 0) ? -1.0D : 0.0D);
             sc /= 8.0D;
           } 
           
           if (this.mapOptions.heightmap) {
             diff = height - 80;
             double heightsc = Math.log10(Math.abs(diff) / 8.0D + 1.0D) / 3.0D;
             sc = (diff > 0) ? (sc + heightsc) : (sc - heightsc);
           } 
         } 
         
         int alpha = color24 >> 24 & 0xFF;
         int r = color24 >> 16 & 0xFF;
         int g = color24 >> 8 & 0xFF;
         int b = color24 >> 0 & 0xFF;
         if (sc > 0.0D) {
           r += (int)(sc * (255 - r));
           g += (int)(sc * (255 - g));
           b += (int)(sc * (255 - b));
         } else if (sc < 0.0D) {
           sc = Math.abs(sc);
           r -= (int)(sc * r);
           g -= (int)(sc * g);
           b -= (int)(sc * b);
         } 
         
         color24 = alpha * 16777216 + r * 65536 + g * 256 + b;
       } 
     } 
     
     return color24;
   }
   
   private int getLight(int light) {
     return this.lightmapColors[light];
   }
 
   
   public CachedRegion[] getRegions(int left, int right, int top, int bottom) {
     if (left == this.lastLeft && right == this.lastRight && top == this.lastTop && bottom == this.lastBottom) {
       return this.lastRegionsArray;
     }
     ThreadManager.emptyQueue();
     CachedRegion[] visibleCachedRegionsArray = new CachedRegion[(right - left + 1) * (bottom - top + 1)];
     String worldName = this.master.getWaypointManager().getCurrentWorldName();
     String subWorldName = this.master.getWaypointManager().getCurrentSubworldDescriptor(false);
     List<RegionCoordinates> regionsToDisplay = new ArrayList<>();
     
     for (int t = left; t <= right; t++) {
       for (int s = top; s <= bottom; s++) {
         RegionCoordinates regionCoordinates = new RegionCoordinates(t, s);
         regionsToDisplay.add(regionCoordinates);
       } 
     } 
     
     Collections.sort(regionsToDisplay, this.distanceSorter);
     
     for (RegionCoordinates regionCoordinates : regionsToDisplay) {
       CachedRegion cachedRegion; int x = regionCoordinates.x;
       int z = regionCoordinates.z;
       StringBuilder keyBuilder = (new StringBuilder("")).append(x).append(",").append(z);
       String key = keyBuilder.toString();
       
       synchronized (this.cachedRegions) {
         cachedRegion = (CachedRegion)this.cachedRegions.get(key);
         if (cachedRegion == null) {
           cachedRegion = new CachedRegion(this, key, this.world, worldName, subWorldName, x, z);
           this.cachedRegions.put(key, cachedRegion);
           synchronized (this.cachedRegionsPool) {
             this.cachedRegionsPool.add(cachedRegion);
           } 
         } 
       } 
       
       cachedRegion.refresh(true);
       visibleCachedRegionsArray[(z - top) * (right - left + 1) + x - left] = cachedRegion;
     } 
     
     prunePool();
     synchronized (this.lastRegionsArray) {
       this.lastLeft = left;
       this.lastRight = right;
       this.lastTop = top;
       this.lastBottom = bottom;
       this.lastRegionsArray = visibleCachedRegionsArray;
       return visibleCachedRegionsArray;
     } 
   }
 
   
   private void prunePool() {
     synchronized (this.cachedRegionsPool) {
       Iterator<CachedRegion> iterator = this.cachedRegionsPool.iterator();
       
       while (iterator.hasNext()) {
         CachedRegion region = iterator.next();
         if (region.isLoaded() && region.isEmpty()) {
           this.cachedRegions.put(region.getKey(), CachedRegion.emptyRegion);
           region.cleanup();
           iterator.remove();
         } 
       } 
       
       if (this.cachedRegionsPool.size() > this.options.cacheSize) {
         Collections.sort(this.cachedRegionsPool, this.ageThenDistanceSorter);
         List<CachedRegion> toRemove = this.cachedRegionsPool.subList(this.options.cacheSize, this.cachedRegionsPool.size());
         
         for (CachedRegion cachedRegion : toRemove) {
           this.cachedRegions.remove(cachedRegion.getKey());
           cachedRegion.cleanup();
         } 
         
         toRemove.clear();
       } 
       
       compress();
     } 
   }
 
   
   public void compress() {
     synchronized (this.cachedRegionsPool) {
       for (CachedRegion cachedRegion : this.cachedRegionsPool) {
         if (System.currentTimeMillis() - cachedRegion.getMostRecentChange() > 5000L) {
           cachedRegion.compress();
         }
       } 
     } 
   }
 
 
   
   public void handleChangeInWorld(int chunkX, int chunkZ) {
     if (this.world != null) {
       class_2818 chunk = this.world.method_8497(chunkX, chunkZ);
       if (chunk != null && !chunk.method_12223() && 
         isChunkReady(this.world, chunk)) {
         processChunk(chunk);
       }
     } 
   }
 
 
 
   
   public void processChunk(class_2818 chunk) {
     this.chunkUpdateQueue.add(new ChunkWithAge(chunk, TickCounter.tickCounter));
   }
   
   private void doProcessChunk(class_2818 chunk) {
     this.queuedChangedChunks = true;
     try {
       CachedRegion cachedRegion;
       if (this.world == null) {
         return;
       }
       
       if (chunk == null || chunk.method_12223()) {
         return;
       }
       
       int chunkX = (chunk.method_12004()).field_9181;
       int chunkZ = (chunk.method_12004()).field_9180;
       int regionX = (int)Math.floor(chunkX / 16.0D);
       int regionZ = (int)Math.floor(chunkZ / 16.0D);
       StringBuilder keyBuilder = (new StringBuilder("")).append(regionX).append(",").append(regionZ);
       String key = keyBuilder.toString();
       
       synchronized (this.cachedRegions) {
         cachedRegion = (CachedRegion)this.cachedRegions.get(key);
         if (cachedRegion == null || cachedRegion == CachedRegion.emptyRegion) {
           String worldName = this.master.getWaypointManager().getCurrentWorldName();
           String subWorldName = this.master.getWaypointManager().getCurrentSubworldDescriptor(false);
           cachedRegion = new CachedRegion(this, key, this.world, worldName, subWorldName, regionX, regionZ);
           this.cachedRegions.put(key, cachedRegion);
           synchronized (this.cachedRegionsPool) {
             this.cachedRegionsPool.add(cachedRegion);
           } 
           
           synchronized (this.lastRegionsArray) {
             if (regionX >= this.lastLeft && regionX <= this.lastRight && regionZ >= this.lastTop && regionZ <= this.lastBottom) {
               this.lastRegionsArray[(regionZ - this.lastTop) * (this.lastRight - this.lastLeft + 1) + regionX - this.lastLeft] = cachedRegion;
             }
           } 
         } 
       } 
       
       if ((class_310.method_1551()).field_1755 != null && (class_310.method_1551()).field_1755 instanceof GuiPersistentMap) {
         cachedRegion.registerChangeAt(chunkX, chunkZ);
         cachedRegion.refresh(false);
       } else {
         cachedRegion.handleChangedChunk(chunk);
       } 
     } catch (Exception var19) {
       System.out.println(var19.getMessage());
       var19.printStackTrace();
     } 
   }
 
   
   private boolean isChunkReady(class_638 world, class_2818 chunk) {
     return this.chunkCache.isChunkSurroundedByLoaded((chunk.method_12004()).field_9181, (chunk.method_12004()).field_9180);
   }
 
   
   public boolean isRegionLoaded(int blockX, int blockZ) {
     int x = (int)Math.floor((blockX / 256.0F));
     int z = (int)Math.floor((blockZ / 256.0F));
     CachedRegion cachedRegion = (CachedRegion)this.cachedRegions.get("" + x + "," + x);
     return (cachedRegion == null) ? false : cachedRegion.isLoaded();
   }
 
   
   public boolean isGroundAt(int blockX, int blockZ) {
     int x = (int)Math.floor((blockX / 256.0F));
     int z = (int)Math.floor((blockZ / 256.0F));
     CachedRegion cachedRegion = (CachedRegion)this.cachedRegions.get("" + x + "," + x);
     return (cachedRegion == null) ? false : cachedRegion.isGroundAt(blockX, blockZ);
   }
 
   
   public int getHeightAt(int blockX, int blockZ) {
     int x = (int)Math.floor((blockX / 256.0F));
     int z = (int)Math.floor((blockZ / 256.0F));
     CachedRegion cachedRegion = (CachedRegion)this.cachedRegions.get("" + x + "," + x);
     return (cachedRegion == null) ? 64 : cachedRegion.getHeightAt(blockX, blockZ);
   }
   
   private class ChunkWithAge {
     class_2818 chunk;
     int tick;
     
     public ChunkWithAge(class_2818 chunk, int tick) {
       this.chunk = chunk;
       this.tick = tick;
     }
   }
   
   private class RegionCoordinates {
     int x;
     int z;
     
     public RegionCoordinates(int x, int z) {
       this.x = x;
       this.z = z;
     }
   }
 }


