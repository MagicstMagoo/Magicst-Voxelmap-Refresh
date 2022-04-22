 package cn.magicst.mamiyaotaru.voxelmap.persistent;
 import com.google.common.collect.BiMap;
 import com.google.common.collect.HashBiMap;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.AbstractVoxelMap;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IPersistentMap;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.ISettingsAndLightingChangeListener;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.ISettingsAndLightingChangeNotifier;
 import cn.magicst.mamiyaotaru.voxelmap.util.BlockStateParser;
 import cn.magicst.mamiyaotaru.voxelmap.util.CommandUtils;
 import cn.magicst.mamiyaotaru.voxelmap.util.GameVariableAccessShim;
 import cn.magicst.mamiyaotaru.voxelmap.util.MutableBlockPos;
 import cn.magicst.mamiyaotaru.voxelmap.util.ReflectionUtils;
 import cn.magicst.mamiyaotaru.voxelmap.util.TextUtils;
 import java.awt.image.BufferedImage;
 import java.awt.image.DataBufferByte;
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.Arrays;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Optional;
 import java.util.Properties;
 import java.util.Scanner;
 import java.util.concurrent.CompletableFuture;
 import java.util.concurrent.Executor;
 import java.util.concurrent.Future;
 import java.util.concurrent.locks.ReadWriteLock;
 import java.util.concurrent.locks.ReentrantLock;
 import java.util.concurrent.locks.ReentrantReadWriteLock;
 import java.util.zip.ZipEntry;
 import java.util.zip.ZipFile;
 import java.util.zip.ZipOutputStream;
 import javax.imageio.ImageIO;
 import net.minecraft.class_1255;
 import net.minecraft.class_1923;
 import net.minecraft.class_1937;
 import net.minecraft.class_1944;
 import net.minecraft.class_2338;
 import net.minecraft.class_2487;
 import net.minecraft.class_2499;
 import net.minecraft.class_26;
 import net.minecraft.class_2791;
 import net.minecraft.class_2806;
 import net.minecraft.class_2818;
 import net.minecraft.class_2874;
 import net.minecraft.class_2902;
 import net.minecraft.class_310;
 import net.minecraft.class_3215;
 import net.minecraft.class_3218;
 import net.minecraft.class_3898;
 import net.minecraft.class_5218;
 import net.minecraft.class_638;
 
 public class CachedRegion implements IThreadCompleteListener, ISettingsAndLightingChangeListener {
   public static EmptyCachedRegion emptyRegion = new EmptyCachedRegion();
   private long mostRecentView = 0L;
   private long mostRecentChange = 0L;
   private IPersistentMap persistentMap;
   private String key;
   private class_638 world;
   private class_3218 worldServer;
   private class_3215 chunkProvider;
   Class executorClass;
   private class_1255 executor;
   private class_3898 chunkLoader;
   private String worldName;
   private String subworldName;
   private String worldNamePathPart;
   private String subworldNamePathPart = "";
   private String dimensionNamePathPart;
   private boolean underground = false;
   private int x;
   private int z;
   private int width = 256;
   private boolean empty = true;
   private boolean liveChunksUpdated = false;
   boolean remoteWorld;
   private boolean[] liveChunkUpdateQueued = new boolean[256];
   private boolean[] chunkUpdateQueued = new boolean[256];
   private CompressibleGLBufferedImage image;
   private CompressibleMapData data;
   MutableBlockPos blockPos = new MutableBlockPos(0, 0, 0);
   MutableBlockPos loopBlockPos = new MutableBlockPos(0, 0, 0);
   Future future = null;
   private ReentrantLock threadLock = new ReentrantLock();
   boolean displayOptionsChanged = false;
   boolean imageChanged = false;
   boolean refreshQueued = false;
   boolean refreshingImage = false;
   boolean dataUpdated = false;
   boolean dataUpdateQueued = false;
   boolean loaded = false;
   boolean closed = false;
   private static final Object anvilLock = new Object();
   private static final ReadWriteLock tickLock = new ReentrantReadWriteLock();
   private static int loadedChunkCount = 0;
   
   private static boolean debug = false;
   
   private boolean queuedToCompress = false;
 
   
   public CachedRegion(IPersistentMap persistentMap, String key, class_638 world, String worldName, String subworldName, int x, int z) {
     this.persistentMap = persistentMap;
     this.key = key;
     this.world = world;
     this.worldName = worldName;
     this.subworldName = subworldName;
     this.worldNamePathPart = TextUtils.scrubNameFile(worldName);
     if (subworldName != "") {
       this.subworldNamePathPart = TextUtils.scrubNameFile(subworldName) + "/";
     }
     
     String dimensionName = AbstractVoxelMap.getInstance().getDimensionManager().getDimensionContainerByWorld((class_1937)world).getStorageName();
     this.dimensionNamePathPart = TextUtils.scrubNameFile(dimensionName);
     boolean knownUnderground = false;
     knownUnderground = (knownUnderground || dimensionName.toLowerCase().contains("erebus"));
     this.underground = ((!world.method_28103().method_28114() && !world.method_8597().method_12491()) || world.method_8597().method_27998() || knownUnderground);
     this.remoteWorld = !class_310.method_1551().method_1496();
     persistentMap.getSettingsAndLightingChangeNotifier().addObserver(this);
     this.x = x;
     this.z = z;
     if (!this.remoteWorld) {
       this.worldServer = class_310.method_1551().method_1576().method_3847(world.method_27983());
       this.chunkProvider = this.worldServer.method_14178();
       this.executorClass = this.chunkProvider.getClass().getDeclaredClasses()[0];
       this.executor = (class_1255)ReflectionUtils.getPrivateFieldValueByType(this.chunkProvider, class_3215.class, this.executorClass);
       this.chunkLoader = this.chunkProvider.field_17254;
     } 
     
     Arrays.fill(this.liveChunkUpdateQueued, false);
     Arrays.fill(this.chunkUpdateQueued, false);
   }
   
   public void renameSubworld(String oldName, String newName) {
     if (oldName.equals(this.subworldName)) {
       this.closed = true;
       this.threadLock.lock();
 
       
       try { this.subworldName = newName;
         if (this.subworldName != "") {
           this.subworldNamePathPart = TextUtils.scrubNameFile(this.subworldName) + "/";
         } }
       catch (Exception exception) {  }
       finally
       { this.threadLock.unlock();
         this.closed = false; }
     
     } 
   }
 
   
   public void registerChangeAt(int chunkX, int chunkZ) {
     chunkX -= this.x * 16;
     chunkZ -= this.z * 16;
     this.dataUpdateQueued = true;
     int index = chunkZ * 16 + chunkX;
     this.liveChunkUpdateQueued[index] = true;
   }
 
   
   public void notifyOfActionableChange(ISettingsAndLightingChangeNotifier notifier) {
     this.displayOptionsChanged = true;
   }
   
   public void refresh(boolean forceCompress) {
     this.mostRecentView = System.currentTimeMillis();
     if (this.future != null && (this.future.isDone() || this.future.isCancelled())) {
       this.refreshQueued = false;
     }
     
     if (!this.refreshQueued) {
       this.refreshQueued = true;
       if (this.loaded && !this.dataUpdated && !this.dataUpdateQueued && !this.displayOptionsChanged) {
         this.refreshQueued = false;
       } else {
         RefreshRunnable regionProcessingRunnable = new RefreshRunnable(forceCompress);
         this.future = ThreadManager.executorService.submit(regionProcessingRunnable);
       } 
     } 
   }
 
   
   public void handleChangedChunk(class_2818 chunk) {
     int chunkX = (chunk.method_12004()).field_9181 - this.x * 16;
     int chunkZ = (chunk.method_12004()).field_9180 - this.z * 16;
     int index = chunkZ * 16 + chunkX;
     if (!this.chunkUpdateQueued[index]) {
       this.chunkUpdateQueued[index] = true;
       this.mostRecentView = System.currentTimeMillis();
       this.mostRecentChange = this.mostRecentView;
       FillChunkRunnable fillChunkRunnable = new FillChunkRunnable(chunk);
       ThreadManager.executorService.execute(fillChunkRunnable);
     } 
   }
 
   
   public void notifyOfThreadComplete(AbstractNotifyingRunnable runnable) {}
 
   
   private void load() {
     this.data = new CompressibleMapData(256, 256);
     this.image = new CompressibleGLBufferedImage(256, 256, 6);
     loadCachedData();
     loadCurrentData(this.world);
     if (!this.remoteWorld) {
       loadAnvilData((class_1937)this.world);
     }
     
     this.loaded = true;
   }
   
   private void loadCurrentData(class_638 world) {
     for (int chunkX = 0; chunkX < 16; chunkX++) {
       for (int chunkZ = 0; chunkZ < 16; chunkZ++) {
         class_2818 chunk = world.method_8497(this.x * 16 + chunkX, this.z * 16 + chunkZ);
         if (chunk != null && !chunk.method_12223() && world.method_8393(this.x * 16 + chunkX, this.z * 16 + chunkZ) && isSurroundedByLoaded(chunk)) {
           loadChunkData(chunk, chunkX, chunkZ);
         }
       } 
     } 
   }
 
   
   private void loadModifiedData() {
     for (int chunkX = 0; chunkX < 16; chunkX++) {
       for (int chunkZ = 0; chunkZ < 16; chunkZ++) {
         if (this.liveChunkUpdateQueued[chunkZ * 16 + chunkX]) {
           this.liveChunkUpdateQueued[chunkZ * 16 + chunkX] = false;
           class_2818 chunk = this.world.method_8497(this.x * 16 + chunkX, this.z * 16 + chunkZ);
           if (chunk != null && !chunk.method_12223() && this.world.method_8393(this.x * 16 + chunkX, this.z * 16 + chunkZ)) {
             loadChunkData(chunk, chunkX, chunkZ);
           }
         } 
       } 
     } 
   }
 
   
   private void loadChunkData(class_2818 chunk, int chunkX, int chunkZ) {
     boolean isEmpty = isChunkEmptyOrUnlit(chunk);
     boolean isSurroundedByLoaded = isSurroundedByLoaded(chunk);
     if (!this.closed && this.world == GameVariableAccessShim.getWorld() && !isEmpty && isSurroundedByLoaded) {
       doLoadChunkData(chunk, chunkX, chunkZ);
     }
   }
 
   
   private void loadChunkDataSkipLightCheck(class_2818 chunk, int chunkX, int chunkZ) {
     if (!this.closed && this.world == GameVariableAccessShim.getWorld() && !isChunkEmpty(chunk)) {
       doLoadChunkData(chunk, chunkX, chunkZ);
     }
   }
 
   
   private void doLoadChunkData(class_2818 chunk, int chunkX, int chunkZ) {
     for (int t = 0; t < 16; t++) {
       for (int s = 0; s < 16; s++) {
         this.persistentMap.getAndStoreData(this.data, chunk.method_12200(), chunk, this.blockPos, this.underground, this.x * 256, this.z * 256, chunkX * 16 + t, chunkZ * 16 + s);
       }
     } 
     
     this.empty = false;
     this.liveChunksUpdated = true;
     this.dataUpdated = true;
   }
   
   private boolean isChunkEmptyOrUnlit(class_2818 chunk) {
     if (!this.closed && chunk.method_12009().method_12165(class_2806.field_12803)) {
       boolean overworld = this.world.method_8597().method_12491();
       
       for (int t = 0; t < 16; t++) {
         for (int s = 0; s < 16; s++) {
           if (overworld) {
             if (chunk.method_12200().method_8314(class_1944.field_9284, (class_2338)this.blockPos.withXYZ((chunk.method_12004()).field_9181 * 16 + t, chunk.method_12031() + 15, (chunk.method_12004()).field_9180 * 16 + s)) != 0) {
               return false;
             }
           } else if (chunk.method_12005(class_2902.class_2903.field_13197, t, s) != 0) {
             return false;
           } 
         } 
       } 
       
       return true;
     } 
     return true;
   }
 
   
   private boolean isChunkEmpty(class_2818 chunk) {
     if (!this.closed && !chunk.method_12223() && chunk.method_12009().method_12165(class_2806.field_12803)) {
       for (int t = 0; t < 16; t++) {
         for (int s = 0; s < 16; s++) {
           if (chunk.method_12005(class_2902.class_2903.field_13197, t, s) != 0) {
             return false;
           }
           
           if (chunk.method_12200().method_8314(class_1944.field_9284, (class_2338)this.blockPos.withXYZ((chunk.method_12004()).field_9181 * 16 + t, chunk.method_12031() + 15, (chunk.method_12004()).field_9180 * 16 + s)) != 0) {
             return false;
           }
         } 
       } 
       
       return true;
     } 
     return true;
   }
 
   
   public boolean isSurroundedByLoaded(class_2818 chunk) {
     int chunkX = (chunk.method_12004()).field_9181;
     int chunkZ = (chunk.method_12004()).field_9180;
     boolean neighborsLoaded = (chunk != null && !chunk.method_12223() && (class_310.method_1551()).field_1687.method_8393(chunkX, chunkZ));
     
     for (int t = chunkX - 1; t <= chunkX + 1 && neighborsLoaded; t++) {
       for (int s = chunkZ - 1; s <= chunkZ + 1 && neighborsLoaded; s++) {
         class_2818 neighborChunk = (class_310.method_1551()).field_1687.method_8497(t, s);
         neighborsLoaded = (neighborsLoaded && neighborChunk != null && !neighborChunk.method_12223() && (class_310.method_1551()).field_1687.method_8393(t, s));
       } 
     } 
     
     return neighborsLoaded;
   }
   
   private void loadAnvilData(class_1937 world) {
     if (!this.remoteWorld) {
       boolean full = true;
       
       for (int t = 0; t < 16; t++) {
         for (int s = 0; s < 16; s++) {
           if (!this.closed && this.data.getHeight(t * 16, s * 16) == 0 && this.data.getLight(t * 16, s * 16) == 0) {
             full = false;
           }
         } 
       } 
       
       if (!this.closed && !full) {
         File directory = new File(class_2874.method_12488(this.worldServer.method_27983(), this.worldServer.method_8503().method_27050(class_5218.field_24188).normalize()).toString(), "region");
         File regionFile = new File(directory, "r." + (int)Math.floor((this.x / 2)) + "." + (int)Math.floor((this.z / 2)) + ".mca");
         if (regionFile.exists()) {
           boolean dataChanged = false;
           boolean loadedChunks = false;
           class_2791[] chunks = new class_2791[256];
           boolean[] chunkChanged = new boolean[256];
           Arrays.fill((Object[])chunks, (Object)null);
           Arrays.fill(chunkChanged, false);
           tickLock.readLock().lock();
           
           try {
             synchronized (anvilLock) {
               if (debug) {
                 System.out.println(Thread.currentThread().getName() + " starting load");
               }
               
               long loadTime = System.currentTimeMillis();
               CompletableFuture<?> loadFuture = CompletableFuture.runAsync(() -> {
                     for (int tx = 0; tx < 16; tx++) {
                       for (int sx = 0; sx < 16; sx++) {
                         if (!this.closed && this.data.getHeight(tx * 16, sx * 16) == 0 && this.data.getLight(tx * 16, sx * 16) == 0) {
                           try {
                             int index = tx + sx * 16;
                             
                             class_1923 chunkPos = new class_1923(this.x * 16 + tx, this.z * 16 + sx);
                             
                             class_2487 rawNbt = this.chunkLoader.method_23696(chunkPos);
                             
                             if (rawNbt != null) {
                               class_2487 nbt = this.chunkLoader.method_17907(this.worldServer.method_27983(), (), rawNbt, Optional.empty());
                               if (!this.closed && nbt.method_10573("Level", 10)) {
                                 class_2487 level = nbt.method_10562("Level");
                                 int chunkX = level.method_10550("xPos");
                                 int chunkZ = level.method_10550("zPos");
                                 if (chunkPos.field_9181 == chunkX && chunkPos.field_9180 == chunkZ && level.method_10573("Status", 8) && class_2806.method_12168(level.method_10558("Status")).method_12165(class_2806.field_12786) && level.method_10545("Sections")) {
                                   class_2499 sections = level.method_10554("Sections", 10);
                                   if (!sections.isEmpty() && sections.size() != 0) {
                                     boolean hasInfo = false;
                                     int i = 0;
                                     while (i < sections.size() && !hasInfo && !this.closed) {
                                       class_2487 section = sections.method_10602(i);
                                       if (section.method_10573("Palette", 9) && section.method_10573("BlockStates", 12)) {
                                         hasInfo = true;
                                       }
                                       i++;
                                     } 
                                     if (hasInfo) {
                                       boolean hasLight = true;
                                       if (!level.method_10545("isLightOn") || !level.method_10577("isLightOn")) {
                                         hasLight = false;
                                       }
                                       if (level.method_10545("LightPopulated")) {
                                         hasLight = false;
                                       }
                                       if (!nbt.method_10545("DataVersion") || nbt.method_10550("DataVersion") < 1900) {
                                         hasLight = false;
                                       }
                                       chunks[index] = (class_2791)this.worldServer.method_8497(chunkPos.field_9181, chunkPos.field_9180);
                                     } 
                                   } 
                                 } 
                               } 
                             } 
                           } catch (IOException var15x) {
                             System.out.println("failed checking NBT while loading from anvil: " + var15x.getMessage());
                             
                             var15x.printStackTrace();
                           } 
                         }
                       } 
                     } 
                   }(Executor)this.executor);
               
               while (!this.closed && !loadFuture.isDone()) {
                 try {
                   Thread.sleep(3L);
                 } catch (InterruptedException interruptedException) {}
               } 
 
               
               loadFuture.cancel(false);
               if (debug) {
                 System.out.println(Thread.currentThread().getName() + " finished load after " + Thread.currentThread().getName() + " milliseconds");
               }
             } 
             
             if (debug) {
               System.out.println(Thread.currentThread().getName() + " starting calculation");
             }
             
             long calcTime = System.currentTimeMillis();
             
             for (int i = 0; i < 16; i++) {
               for (int s = 0; s < 16; s++) {
                 int index = i + s * 16;
                 if (!this.closed && chunks[index] != null) {
                   loadedChunks = true;
                   loadedChunkCount++;
                   class_2818 loadedChunk = null;
                   if (chunks[index] instanceof class_2818) {
                     loadedChunk = (class_2818)chunks[index];
                   } else {
                     System.out.println("non world chunk at " + (chunks[index].method_12004()).field_9181 + "," + (chunks[index].method_12004()).field_9180);
                   } 
                   
                   if (!this.closed && loadedChunk != null && loadedChunk.method_12009().method_12165(class_2806.field_12803)) {
                     CompletableFuture<class_2818> lightFuture = this.chunkProvider.method_17293().method_17310((class_2791)loadedChunk, false);
                     
                     while (!this.closed && !lightFuture.isDone()) {
                       try {
                         Thread.sleep(3L);
                       } catch (InterruptedException interruptedException) {}
                     } 
 
                     
                     loadedChunk = lightFuture.getNow(loadedChunk);
                     lightFuture.cancel(false);
                   } 
                   
                   if (!this.closed && loadedChunk != null && loadedChunk.method_12009().method_12165(class_2806.field_12803)) {
                     loadChunkDataSkipLightCheck(loadedChunk, i, s);
                     dataChanged = true;
                   } 
                 } 
               } 
             } 
             
             if (debug) {
               System.out.println(Thread.currentThread().getName() + " finished calculating after " + Thread.currentThread().getName() + " milliseconds");
             }
           } catch (Exception var41) {
             System.out.println("error in anvil loading");
           } finally {
             tickLock.readLock().unlock();
           } 
           
           if (!this.closed && dataChanged) {
             saveData(false);
           }
           
           if (!this.closed && loadedChunks && loadedChunkCount > 4096) {
             loadedChunkCount = 0;
             tickLock.writeLock().lock();
             
             try {
               CompletableFuture<Void> tickFuture = CompletableFuture.runAsync(() -> this.chunkProvider.method_12127((), this.executor.method_18854()));
               long tickTime = System.currentTimeMillis();
               if (debug) {
                 System.out.println(Thread.currentThread().getName() + " starting chunk GC tick");
               }
               
               while (!this.closed && !tickFuture.isDone()) {
                 try {
                   Thread.sleep(3L);
                 } catch (InterruptedException interruptedException) {}
               } 
 
               
               if (debug) {
                 System.out.println(Thread.currentThread().getName() + " finished chunk GC tick after " + Thread.currentThread().getName() + " milliseconds");
               }
             } catch (Exception var38) {
               System.out.println("error ticking from anvil loading");
             } finally {
               tickLock.writeLock().unlock();
             } 
           } 
         } 
       } 
     } 
   }
 
   
   private void loadCachedData() {
     try {
       File cachedRegionFileDir = new File((class_310.method_1551()).field_1697, "/voxelmap/cache/" + this.worldNamePathPart + "/" + this.subworldNamePathPart + this.dimensionNamePathPart);
       cachedRegionFileDir.mkdirs();
       File cachedRegionFile = new File(cachedRegionFileDir, "/" + this.key + ".zip");
       if (cachedRegionFile.exists()) {
         ZipFile zFile = new ZipFile(cachedRegionFile);
         BiMap stateToInt = null;
         int total = 0;
         byte[] decompressedByteData = new byte[this.data.getWidth() * this.data.getHeight() * 17 * 4];
         ZipEntry ze = zFile.getEntry("data");
         InputStream is = zFile.getInputStream(ze);
         
         int count;
         for (byte[] byteData = new byte[2048]; (count = is.read(byteData, 0, 2048)) != -1 && count + total <= this.data.getWidth() * this.data.getHeight() * 17 * 4; total += count) {
           System.arraycopy(byteData, 0, decompressedByteData, total, count);
         }
         
         is.close();
         ze = zFile.getEntry("key");
         is = zFile.getInputStream(ze);
         HashBiMap hashBiMap = HashBiMap.create();
         Scanner sc = new Scanner(is);
         
         while (sc.hasNextLine()) {
           BlockStateParser.parseLine(sc.nextLine(), (BiMap)hashBiMap);
         }
         
         sc.close();
         is.close();
         int version = 1;
         ze = zFile.getEntry("control");
         if (ze != null) {
           is = zFile.getInputStream(ze);
           if (is != null) {
             Properties properties = new Properties();
             properties.load(is);
             String versionString = properties.getProperty("version", "1");
             
             try {
               version = Integer.parseInt(versionString);
             } catch (NumberFormatException var16) {
               version = 1;
             } 
             
             is.close();
           } 
         } 
         
         zFile.close();
         if (total == this.data.getWidth() * this.data.getHeight() * 18 && hashBiMap != null) {
           byte[] var23 = new byte[this.data.getWidth() * this.data.getHeight() * 18];
           System.arraycopy(decompressedByteData, 0, var23, 0, var23.length);
           this.data.setData(var23, (BiMap)hashBiMap, version);
           this.empty = false;
           this.dataUpdated = true;
         } else {
           System.out.println("failed to load data from " + cachedRegionFile.getPath());
         } 
         
         if (hashBiMap == null || version < 2) {
           this.liveChunksUpdated = true;
         }
       } 
     } catch (Exception var17) {
       System.err.println("Failed to load region file for " + this.x + "," + this.z + " in " + this.worldNamePathPart + "/" + this.subworldNamePathPart + this.dimensionNamePathPart);
       var17.printStackTrace();
     } 
   }
 
   
   private void saveData(boolean newThread) {
     if (this.liveChunksUpdated && !this.worldNamePathPart.equals("")) {
       if (newThread) {
         ThreadManager.executorService.execute(new Runnable() {
               public void run() {
                 CachedRegion.this.threadLock.lock();
                 
                 try {
                   CachedRegion.this.doSave();
                 } catch (IOException var5) {
                   System.err.println("Failed to save region file for " + CachedRegion.this.x + "," + CachedRegion.this.z + " in " + CachedRegion.this.worldNamePathPart + "/" + CachedRegion.this.subworldNamePathPart + CachedRegion.this.dimensionNamePathPart);
                   var5.printStackTrace();
                 } finally {
                   CachedRegion.this.threadLock.unlock();
                 } 
               }
             });
       } else {
         
         try {
           doSave();
         } catch (IOException var3) {
           var3.printStackTrace();
         } 
       } 
       
       this.liveChunksUpdated = false;
     } 
   }
 
   
   private void doSave() throws IOException {
     BiMap stateToInt = this.data.getStateToInt();
     byte[] byteArray = this.data.getData();
     int var10000 = byteArray.length;
     int var10001 = this.data.getWidth() * this.data.getHeight();
     CompressibleMapData var10002 = this.data;
     if (var10000 == var10001 * 18) {
       File cachedRegionFileDir = new File((class_310.method_1551()).field_1697, "/voxelmap/cache/" + this.worldNamePathPart + "/" + this.subworldNamePathPart + this.dimensionNamePathPart);
       cachedRegionFileDir.mkdirs();
       File cachedRegionFile = new File(cachedRegionFileDir, "/" + this.key + ".zip");
       FileOutputStream fos = new FileOutputStream(cachedRegionFile);
       ZipOutputStream zos = new ZipOutputStream(fos);
       ZipEntry ze = new ZipEntry("data");
       ze.setSize(byteArray.length);
       zos.putNextEntry(ze);
       zos.write(byteArray);
       zos.closeEntry();
       if (stateToInt != null) {
         Iterator<Map.Entry> iterator = stateToInt.entrySet().iterator();
         StringBuffer stringBuffer1 = new StringBuffer();
         
         while (iterator.hasNext()) {
           Map.Entry entry = iterator.next();
           String str = "" + entry.getValue() + " " + entry.getValue() + "\r\n";
           stringBuffer1.append(str);
         } 
         
         byte[] arrayOfByte = String.valueOf(stringBuffer1).getBytes();
         ze = new ZipEntry("key");
         ze.setSize(arrayOfByte.length);
         zos.putNextEntry(ze);
         zos.write(arrayOfByte);
         zos.closeEntry();
       } 
       
       StringBuffer stringBuffer = new StringBuffer();
       String nextLine = "version:2\r\n";
       stringBuffer.append(nextLine);
       byte[] keyByteArray = String.valueOf(stringBuffer).getBytes();
       ze = new ZipEntry("control");
       ze.setSize(keyByteArray.length);
       zos.putNextEntry(ze);
       zos.write(keyByteArray);
       zos.closeEntry();
       zos.close();
       fos.close();
     } else {
       System.err.println("Data array wrong size: " + byteArray.length + "for " + this.x + "," + this.z + " in " + this.worldNamePathPart + "/" + this.subworldNamePathPart + this.dimensionNamePathPart);
     } 
   }
 
   
   private void fillImage() {
     int color24 = 0;
     
     for (int t = 0; t < 256; t++) {
       for (int s = 0; s < 256; s++) {
         color24 = this.persistentMap.getPixelColor(this.data, this.world, this.blockPos, this.loopBlockPos, this.underground, 8, this.x * 256, this.z * 256, t, s);
         this.image.setRGB(t, s, color24);
       } 
     } 
   }
 
   
   private void saveImage() {
     if (!this.empty) {
       File imageFileDir = new File((class_310.method_1551()).field_1697, "/voxelmap/cache/" + this.worldNamePathPart + "/" + this.subworldNamePathPart + this.dimensionNamePathPart + "/images/z1");
       imageFileDir.mkdirs();
       final File imageFile = new File(imageFileDir, this.key + ".png");
       if (this.liveChunksUpdated || !imageFile.exists()) {
         ThreadManager.executorService.execute(new Runnable() {
               public void run() {
                 CachedRegion.this.threadLock.lock();
                 
                 try {
                   BufferedImage realBufferedImage = new BufferedImage(CachedRegion.this.width, CachedRegion.this.width, 6);
                   byte[] dstArray = ((DataBufferByte)realBufferedImage.getRaster().getDataBuffer()).getData();
                   System.arraycopy(CachedRegion.this.image.getData(), 0, dstArray, 0, (CachedRegion.this.image.getData()).length);
                   ImageIO.write(realBufferedImage, "png", imageFile);
                 } catch (IOException var6) {
                   var6.printStackTrace();
                 } finally {
                   CachedRegion.this.threadLock.unlock();
                 } 
               }
             });
       }
     } 
   }
 
 
   
   public long getMostRecentView() {
     return this.mostRecentView;
   }
   
   public long getMostRecentChange() {
     return this.mostRecentChange;
   }
   
   public String getKey() {
     return this.key;
   }
   
   public int getX() {
     return this.x;
   }
   
   public int getZ() {
     return this.z;
   }
   
   public int getWidth() {
     return this.width;
   }
   
   public int getGLID() {
     if (this.image != null) {
       if (!this.refreshingImage) {
         synchronized (this.image) {
           if (this.imageChanged) {
             this.imageChanged = false;
             this.image.write();
           } 
         } 
       }
       
       return this.image.getIndex();
     } 
     return 0;
   }
 
   
   public CompressibleMapData getMapData() {
     return this.data;
   }
   
   public boolean isLoaded() {
     return this.loaded;
   }
   
   public boolean isEmpty() {
     return this.empty;
   }
   
   public boolean isGroundAt(int blockX, int blockZ) {
     return (isLoaded() && getHeightAt(blockX, blockZ) > 0);
   }
   
   public int getHeightAt(int blockX, int blockZ) {
     int x = blockX - this.x * 256;
     int z = blockZ - this.z * 256;
     int y = (this.data == null) ? 0 : this.data.getHeight(x, z);
     if (this.underground && y == 255) {
       y = CommandUtils.getSafeHeight(blockX, 64, blockZ, (class_1937)this.world);
     }
     
     return y;
   }
   
   public void compress() {
     if (this.data != null && !isCompressed() && !this.queuedToCompress) {
       this.queuedToCompress = true;
       ThreadManager.executorService.execute(new Runnable() {
             public void run() {
               if (CachedRegion.this.threadLock.tryLock()) {
                 
                 try { CachedRegion.this.compressData(); }
                 catch (Exception exception) {  }
                 finally
                 { CachedRegion.this.threadLock.unlock(); }
               
               }
               
               CachedRegion.this.queuedToCompress = false;
             }
           });
     } 
   }
 
   
   private void compressData() {
     this.data.compress();
   }
   
   private boolean isCompressed() {
     return this.data.isCompressed();
   }
   
   public void cleanup() {
     this.closed = true;
     this.queuedToCompress = true;
     if (this.future != null) {
       this.future.cancel(false);
     }
     
     this.persistentMap.getSettingsAndLightingChangeNotifier().removeObserver(this);
     if (this.image != null) {
       this.image.baleet();
     }
     
     saveData(true);
     if ((this.persistentMap.getOptions()).outputImages)
       saveImage(); 
   }
   
   public CachedRegion() {}
   
   private class FillChunkRunnable implements Runnable {
     private class_2818 chunk;
     private int index;
     
     public FillChunkRunnable(class_2818 chunk) {
       this.chunk = chunk;
       int chunkX = (chunk.method_12004()).field_9181 - CachedRegion.this.x * 16;
       int chunkZ = (chunk.method_12004()).field_9180 - CachedRegion.this.z * 16;
       this.index = chunkZ * 16 + chunkX;
     }
     
     public void run() {
       CachedRegion.this.threadLock.lock();
 
       
       try { if (!CachedRegion.this.loaded) {
           CachedRegion.this.load();
         }
         
         int chunkX = (this.chunk.method_12004()).field_9181 - CachedRegion.this.x * 16;
         int chunkZ = (this.chunk.method_12004()).field_9180 - CachedRegion.this.z * 16;
         CachedRegion.this.loadChunkData(this.chunk, chunkX, chunkZ); }
       catch (Exception exception) {  }
       finally
       { CachedRegion.this.threadLock.unlock();
         CachedRegion.this.chunkUpdateQueued[this.index] = false; }
     
     }
   }
   
   private class RefreshRunnable
     extends AbstractNotifyingRunnable {
     private boolean forceCompress = false;
     
     public RefreshRunnable(boolean forceCompress) {
       this.forceCompress = forceCompress;
     }
 
     
     public void doRun() {
       CachedRegion.this.threadLock.lock();
       CachedRegion.this.mostRecentChange = System.currentTimeMillis();
       
       try {
         if (!CachedRegion.this.loaded) {
           CachedRegion.this.load();
         }
         
         if (CachedRegion.this.dataUpdateQueued) {
           CachedRegion.this.loadModifiedData();
           CachedRegion.this.dataUpdateQueued = false;
         } 
         
         for (; CachedRegion.this.dataUpdated || CachedRegion.this.displayOptionsChanged; CachedRegion.this.refreshingImage = false) {
           CachedRegion.this.dataUpdated = false;
           CachedRegion.this.displayOptionsChanged = false;
           CachedRegion.this.refreshingImage = true;
           synchronized (CachedRegion.this.image) {
             CachedRegion.this.fillImage();
             CachedRegion.this.imageChanged = true;
           } 
         } 
         
         if (this.forceCompress) {
           CachedRegion.this.compressData();
         }
       } catch (Exception var8) {
         System.out.println("Exception loading region: " + var8.getLocalizedMessage());
         var8.printStackTrace();
       } finally {
         CachedRegion.this.threadLock.unlock();
         CachedRegion.this.refreshQueued = false;
       } 
     }
   }
 }

