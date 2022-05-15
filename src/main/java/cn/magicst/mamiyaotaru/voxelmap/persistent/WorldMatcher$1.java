 package cn.magicst.mamiyaotaru.voxelmap.persistent;
 
 import cn.magicst.mamiyaotaru.voxelmap.util.I18nUtils;
 import cn.magicst.mamiyaotaru.voxelmap.util.MessageUtils;
 import cn.magicst.mamiyaotaru.voxelmap.util.TextUtils;
 import java.io.File;
 import java.util.ArrayList;
 import java.util.Iterator;
 import net.minecraft.class_1937;
 import net.minecraft.class_310;
 import net.minecraft.class_746;
 
 class null
   implements Runnable
 {
   int x;
   int z;
   ArrayList candidateRegions = new ArrayList();
   ComparisonCachedRegion region;
   String worldName = WorldMatcher.this.master.getWaypointManager().getCurrentWorldName();
   String worldNamePathPart = TextUtils.scrubNameFile(this.worldName);
   String dimensionName = WorldMatcher.this.master.getDimensionManager().getDimensionContainerByWorld((class_1937)WorldMatcher.this.world).getStorageName();
   String dimensionNamePathPart = TextUtils.scrubNameFile(this.dimensionName);
   File cachedRegionFileDir = new File((class_310.method_1551()).field_1697, "/voxelmap/cache/" + this.worldNamePathPart + "/");
   
   public void run() {
     try {
       Thread.sleep(500L);
     } catch (InterruptedException var8) {
       var8.printStackTrace();
     } 
     
     this.cachedRegionFileDir.mkdirs();
     ArrayList knownSubworldNames = new ArrayList(WorldMatcher.this.master.getWaypointManager().getKnownSubworldNames());
     String[] subworldNamesArray = new String[knownSubworldNames.size()];
     knownSubworldNames.toArray((Object[])subworldNamesArray);
     class_746 player = (class_310.method_1551()).field_1724;
     MessageUtils.printDebug("player coords " + player.method_23317() + " " + player.method_23321() + " in world " + WorldMatcher.this.master.getWaypointManager().getCurrentWorldName());
     this.x = (int)Math.floor(player.method_23317() / 256.0D);
     this.z = (int)Math.floor(player.method_23321() / 256.0D);
     loadRegions(subworldNamesArray);
     int attempts = 0;
     
     while (!WorldMatcher.this.cancelled && (this.candidateRegions.size() == 0 || this.region.getLoadedChunks() < 5) && attempts < 5) {
       attempts++;
       
       try {
         Thread.sleep(1000L);
       } catch (InterruptedException var7) {
         var7.printStackTrace();
       } 
       
       if (this.x == (int)Math.floor(player.method_23317() / 256.0D) && this.z == (int)Math.floor(player.method_23321() / 256.0D)) {
         if (this.candidateRegions.size() > 0) {
           MessageUtils.printDebug("going to load current region");
           this.region.loadCurrent();
           MessageUtils.printDebug("loaded chunks in local region: " + this.region.getLoadedChunks());
         } 
       } else {
         this.x = (int)Math.floor(player.method_23317() / 256.0D);
         this.z = (int)Math.floor(player.method_23321() / 256.0D);
         MessageUtils.printDebug("player coords changed to " + player.method_23317() + " " + player.method_23321() + " in world " + WorldMatcher.this.master.getWaypointManager().getCurrentWorldName());
         loadRegions(subworldNamesArray);
       } 
       
       if (attempts >= 5) {
         if (this.candidateRegions.size() == 0) {
           MessageUtils.printDebug("no candidate regions at current coordinates, bailing"); continue;
         } 
         MessageUtils.printDebug("took too long to load local region, bailing");
       } 
     } 
 
     
     Iterator<ComparisonCachedRegion> iterator = this.candidateRegions.iterator();
     
     while (!WorldMatcher.this.cancelled && iterator.hasNext()) {
       ComparisonCachedRegion candidateRegion = iterator.next();
       MessageUtils.printDebug("testing region " + candidateRegion.getSubworldName() + ": " + candidateRegion.getKey());
       if (this.region.getSimilarityTo(candidateRegion) < 95) {
         MessageUtils.printDebug("region failed");
         iterator.remove(); continue;
       } 
       MessageUtils.printDebug("region succeeded");
     } 
 
     
     MessageUtils.printDebug("remaining regions: " + this.candidateRegions.size());
     if (!WorldMatcher.this.cancelled && this.candidateRegions.size() == 1 && !WorldMatcher.this.master.getWaypointManager().receivedAutoSubworldName()) {
       WorldMatcher.this.master.newSubWorldName(((ComparisonCachedRegion)this.candidateRegions.get(0)).getSubworldName(), false);
       StringBuilder successBuilder = (new StringBuilder(I18nUtils.getString("worldmap.multiworld.foundworld1", new Object[0]))).append(":").append(" §a").append(((ComparisonCachedRegion)this.candidateRegions.get(0)).getSubworldName()).append(".§r").append(" ").append(I18nUtils.getString("worldmap.multiworld.foundworld2", new Object[0]));
       MessageUtils.chatInfo(successBuilder.toString());
     } else if (!WorldMatcher.this.cancelled && !WorldMatcher.this.master.getWaypointManager().receivedAutoSubworldName()) {
       MessageUtils.printDebug("remaining regions: " + this.candidateRegions.size());
       StringBuilder failureBuilder = (new StringBuilder("§4VoxelMap§r")).append(":").append(" ").append(I18nUtils.getString("worldmap.multiworld.unknownsubworld", new Object[0]));
       MessageUtils.chatInfo(failureBuilder.toString());
     } 
   }
 
   
   private void loadRegions(String[] subworldNamesArray) {
     for (String subworldName : subworldNamesArray) {
       if (!WorldMatcher.this.cancelled) {
         File subworldDir = new File(this.cachedRegionFileDir, subworldName + "/" + subworldName);
         if (subworldDir != null && subworldDir.isDirectory()) {
           ComparisonCachedRegion candidateRegion = new ComparisonCachedRegion(WorldMatcher.this.map, "" + this.x + "," + this.x, WorldMatcher.this.world, this.worldName, subworldName, this.x, this.z);
           candidateRegion.loadStored();
           this.candidateRegions.add(candidateRegion);
           MessageUtils.printDebug("added candidate region " + candidateRegion.getSubworldName() + ": " + candidateRegion.getKey());
         } else {
           MessageUtils.printDebug(subworldName + " not found as a candidate region");
         } 
       } 
     } 
     
     this.region = new ComparisonCachedRegion(WorldMatcher.this.map, "" + this.x + "," + this.x, (class_310.method_1551()).field_1687, this.worldName, "", this.x, this.z);
     MessageUtils.printDebug("going to load current region");
     this.region.loadCurrent();
     MessageUtils.printDebug("loaded chunks in local region: " + this.region.getLoadedChunks());
   }
 }

