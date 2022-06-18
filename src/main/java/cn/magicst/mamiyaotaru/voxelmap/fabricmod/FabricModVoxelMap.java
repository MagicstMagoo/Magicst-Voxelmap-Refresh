package cn.magicst.mamiyaotaru.voxelmap.fabricmod;

import cn.magicst.mamiyaotaru.voxelmap.VoxelMap;
import cn.magicst.mamiyaotaru.voxelmap.persistent.ThreadManager;
import cn.magicst.mamiyaotaru.voxelmap.util.BiomeRepository;
import cn.magicst.mamiyaotaru.voxelmap.util.CommandUtils;
import java.nio.charset.StandardCharsets;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.class_2540;
import net.minecraft.class_2561;
import net.minecraft.class_2658;
import net.minecraft.class_310;
import net.minecraft.class_4587;

public class FabricModVoxelMap implements ClientModInitializer {
  public static FabricModVoxelMap instance;

  private boolean initialized = false;

  private VoxelMap master = null;

  public void onInitializeClient() {
    instance = this;
    this.master = new VoxelMap();
  }

  public void lateInit() {
    this.initialized = true;
    this.master.lateInit(true, false);
    Runtime.getRuntime().addShutdownHook(new Thread(this::onShutDown));
  }

  public void clientTick(class_310 client) {
    if (!this.initialized) {
      boolean OK = (class_310.method_1551() != null && client.method_1478() != null && client.method_1531() != null);
      if (OK)
        lateInit();
    }
    if (this.initialized)
      this.master.onTick(client);
  }

  public void renderOverlay(class_4587 matrixStack) {
    if (!this.initialized)
      lateInit();
    try {
      this.master.onTickInGame(matrixStack, class_310.method_1551());
    } catch (Exception exception) {}
  }

  public boolean onChat(class_2561 chat) {
    return CommandUtils.checkForWaypoints(chat);
  }

  public boolean onSendChatMessage(String message) {
    if (message.startsWith("/newWaypoint")) {
      CommandUtils.waypointClicked(message);
      return false;
    }
    if (message.startsWith("/ztp")) {
      CommandUtils.teleport(message);
      return false;
    }
    return true;
  }

  public static void onRenderHand(float partialTicks, long timeSlice, class_4587 matrixStack, boolean beacons, boolean signs, boolean withDepth, boolean withoutDepth) {
    try {
      instance.master.getWaypointManager().renderWaypoints(partialTicks, matrixStack, beacons, signs, withDepth, withoutDepth);
    } catch (Exception exception) {}
  }

  public void onShutDown() {
    System.out.print("Saving all world maps");
    instance.master.getPersistentMap().purgeCachedRegions();
    instance.master.getMapOptions().saveAll();
    BiomeRepository.saveBiomeColors();
    long shutdownTime = System.currentTimeMillis();
    while (ThreadManager.executorService.getQueue().size() + ThreadManager.executorService.getActiveCount() > 0 && System.currentTimeMillis() - shutdownTime < 10000L) {
      System.out.print(".");
      try {
        Thread.sleep(200L);
      } catch (InterruptedException interruptedException) {}
    }
    System.out.println();
  }

  public boolean handleCustomPayload(class_2658 packet) {
    if (packet != null && packet.method_11456() != null) {
      String channel = packet.method_11456().method_12832();
      class_2540 buffer = packet.method_11458();
      if (channel.equals("world_info") || channel.equals("world_id")) {
        buffer.readByte();
        byte length = buffer.readByte();
        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        String subWorldName = new String(bytes, StandardCharsets.UTF_8);
        this.master.newSubWorldName(subWorldName, true);
        return true;
      }
    }
    return false;
  }
}
