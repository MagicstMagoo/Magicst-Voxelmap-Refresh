 package cn.magicst.mamiyaotaru.voxelmap.util;
 
 import cn.magicst.mamiyaotaru.voxelmap.MapSettingsManager;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.AbstractVoxelMap;
 import cn.magicst.mamiyaotaru.voxelmap.textures.Sprite;
 import cn.magicst.mamiyaotaru.voxelmap.textures.TextureAtlas;
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 import java.util.Optional;
 import net.minecraft.class_1159;
 import net.minecraft.class_1160;
 import net.minecraft.class_1297;
 import net.minecraft.class_238;
 import net.minecraft.class_243;
 import net.minecraft.class_2561;
 import net.minecraft.class_2585;
 import net.minecraft.class_2818;
 import net.minecraft.class_287;
 import net.minecraft.class_289;
 import net.minecraft.class_290;
 import net.minecraft.class_293;
 import net.minecraft.class_310;
 import net.minecraft.class_316;
 import net.minecraft.class_327;
 import net.minecraft.class_4587;
 import net.minecraft.class_4597;
 import net.minecraft.class_757;
 
 public class WaypointContainer {
   private List<Waypoint> wayPts = new ArrayList<>();
   private Waypoint highlightedWaypoint = null;
   private class_310 mc;
   public MapSettingsManager options = null;
   private final String TARGETFLAG = "*&^TARget%$^";
   
   public WaypointContainer(MapSettingsManager options) {
     this.mc = class_310.method_1551();
     this.options = options;
   }
   
   public void addWaypoint(Waypoint newWaypoint) {
     this.wayPts.add(newWaypoint);
   }
   
   public void removeWaypoint(Waypoint waypoint) {
     this.wayPts.remove(waypoint);
   }
   
   public void setHighlightedWaypoint(Waypoint highlightedWaypoint) {
     this.highlightedWaypoint = highlightedWaypoint;
   }
   
   private void sortWaypoints() {
     Collections.sort(this.wayPts, Collections.reverseOrder());
   }
   
   public void renderWaypoints(float partialTicks, class_4587 matrixStack, boolean beacons, boolean signs, boolean withDepth, boolean withoutDepth) {
     sortWaypoints();
     class_1297 cameraEntity = this.options.game.method_1560();
     double renderPosX = GameVariableAccessShim.xCoordDouble();
     double renderPosY = GameVariableAccessShim.yCoordDouble();
     double renderPosZ = GameVariableAccessShim.zCoordDouble();
     GLShim.glEnable(2884);
     if (this.options.showBeacons && beacons) {
       GLShim.glDisable(3553);
       GLShim.glDisable(2896);
       GLShim.glEnable(2929);
       GLShim.glDepthMask(false);
       GLShim.glEnable(3042);
       GLShim.glBlendFunc(770, 1);
       RenderSystem.setShader(class_757::method_34540);
       class_1159 matrix4f = matrixStack.method_23760().method_23761();
       
       for (Waypoint pt : this.wayPts) {
         if (pt.isActive() || pt == this.highlightedWaypoint) {
           int x = pt.getX();
           int z = pt.getZ();
           class_2818 chunk = this.mc.field_1687.method_8497(x >> 4, z >> 4);
           if (chunk != null && !chunk.method_12223() && this.mc.field_1687.method_8393(x >> 4, z >> 4)) {
             double bottomOfWorld = (class_310.method_1551()).field_1687.method_31607() - renderPosY;
             renderBeam(pt, x - renderPosX, bottomOfWorld, z - renderPosZ, 64.0F, matrix4f);
           } 
         } 
       } 
       
       GLShim.glDisable(3042);
       GLShim.glEnable(2896);
       GLShim.glEnable(3553);
       GLShim.glDepthMask(true);
     } 
     
     if (this.options.showWaypoints && signs) {
       GLShim.glDisable(2896);
       GLShim.glEnable(3042);
       GLShim.glBlendFuncSeparate(770, 771, 1, 771);
       
       for (Waypoint pt : this.wayPts) {
         if (pt.isActive() || pt == this.highlightedWaypoint) {
           int x = pt.getX();
           int z = pt.getZ();
           int y = pt.getY();
           double distance = Math.sqrt(pt.getDistanceSqToEntity(cameraEntity));
           if ((distance < this.options.maxWaypointDisplayDistance || this.options.maxWaypointDisplayDistance < 0 || pt == this.highlightedWaypoint) && !this.options.game.field_1690.field_1842) {
             boolean isPointedAt = isPointedAt(pt, distance, cameraEntity, Float.valueOf(partialTicks));
             String label = pt.name;
             renderLabel(matrixStack, pt, distance, isPointedAt, label, x - renderPosX, y - renderPosY - 0.5D, z - renderPosZ, 64, withDepth, withoutDepth);
           } 
         } 
       } 
       
       if (this.highlightedWaypoint != null && !this.options.game.field_1690.field_1842) {
         int x = this.highlightedWaypoint.getX();
         int z = this.highlightedWaypoint.getZ();
         int y = this.highlightedWaypoint.getY();
         double distance = Math.sqrt(this.highlightedWaypoint.getDistanceSqToEntity(cameraEntity));
         boolean isPointedAt = isPointedAt(this.highlightedWaypoint, distance, cameraEntity, Float.valueOf(partialTicks));
         renderLabel(matrixStack, this.highlightedWaypoint, distance, isPointedAt, "*&^TARget%$^", x - renderPosX, y - renderPosY - 0.5D, z - renderPosZ, 64, withDepth, withoutDepth);
       } 
       
       GLShim.glEnable(2929);
       GLShim.glDepthMask(true);
       GLShim.glDisable(3042);
     } 
   }
 
   
   private boolean isPointedAt(Waypoint waypoint, double distance, class_1297 cameraEntity, Float partialTicks) {
     class_243 cameraPos = cameraEntity.method_5836(partialTicks.floatValue());
     double degrees = 5.0D + Math.min(5.0D / distance, 5.0D);
     double angle = degrees * 0.0174533D;
     double size = Math.sin(angle) * distance;
     class_243 cameraPosPlusDirection = cameraEntity.method_5828(partialTicks.floatValue());
     class_243 cameraPosPlusDirectionTimesDistance = cameraPos.method_1031(cameraPosPlusDirection.field_1352 * distance, cameraPosPlusDirection.field_1351 * distance, cameraPosPlusDirection.field_1350 * distance);
     class_238 axisalignedbb = new class_238((waypoint.getX() + 0.5F) - size, (waypoint.getY() + 1.5F) - size, (waypoint.getZ() + 0.5F) - size, (waypoint.getX() + 0.5F) + size, (waypoint.getY() + 1.5F) + size, (waypoint.getZ() + 0.5F) + size);
     Optional<class_243> raytraceresult = axisalignedbb.method_992(cameraPos, cameraPosPlusDirectionTimesDistance);
     if (axisalignedbb.method_1006(cameraPos)) {
       return (distance >= 1.0D);
     }
     return raytraceresult.isPresent();
   }
   
   private void renderBeam(Waypoint par1EntityWaypoint, double baseX, double baseY, double baseZ, float par8, class_1159 matrix4f) {
     class_289 tessellator = class_289.method_1348();
     class_287 vertexBuffer = tessellator.method_1349();
     int height = (class_310.method_1551()).field_1687.method_31605();
     float brightness = 0.06F;
     double topWidthFactor = 1.05D;
     double bottomWidthFactor = 1.05D;
     float r = par1EntityWaypoint.red;
     float b = par1EntityWaypoint.blue;
     float g = par1EntityWaypoint.green;
     
     for (int width = 0; width < 4; width++) {
       vertexBuffer.method_1328(class_293.class_5596.field_27380, class_290.field_1576);
       double d6 = 0.1D + width * 0.2D;
       d6 *= topWidthFactor;
       double d7 = 0.1D + width * 0.2D;
       d7 *= bottomWidthFactor;
       
       for (int side = 0; side < 5; side++) {
         float vertX2 = (float)(baseX + 0.5D - d6);
         float vertZ2 = (float)(baseZ + 0.5D - d6);
         if (side == 1 || side == 2) {
           vertX2 = (float)(vertX2 + d6 * 2.0D);
         }
         
         if (side == 2 || side == 3) {
           vertZ2 = (float)(vertZ2 + d6 * 2.0D);
         }
         
         float vertX1 = (float)(baseX + 0.5D - d7);
         float vertZ1 = (float)(baseZ + 0.5D - d7);
         if (side == 1 || side == 2) {
           vertX1 = (float)(vertX1 + d7 * 2.0D);
         }
         
         if (side == 2 || side == 3) {
           vertZ1 = (float)(vertZ1 + d7 * 2.0D);
         }
         
         vertexBuffer.method_22918(matrix4f, vertX1, (float)baseY + 0.0F, vertZ1).method_22915(r * brightness, g * brightness, b * brightness, 0.8F).method_1344();
         vertexBuffer.method_22918(matrix4f, vertX2, (float)baseY + height, vertZ2).method_22915(r * brightness, g * brightness, b * brightness, 0.8F).method_1344();
       } 
       
       tessellator.method_1350();
     } 
   }
 
   
   private void renderLabel(class_4587 matrixStack, Waypoint pt, double distance, boolean isPointedAt, String name, double baseX, double baseY, double baseZ, int par9, boolean withDepth, boolean withoutDepth) {
     boolean target = (name == "*&^TARget%$^");
     if (target) {
       if (pt.red == 2.0F && pt.green == 0.0F && pt.blue == 0.0F) {
         name = "X:" + pt.getX() + ", Y:" + pt.getY() + ", Z:" + pt.getZ();
       } else {
         isPointedAt = false;
       } 
     }
     
     name = name + " (" + name + "m)";
     double maxDistance = class_316.field_1933.method_18613(this.options.game.field_1690) * 16.0D * 0.99D;
     double adjustedDistance = distance;
     if (distance > maxDistance) {
       baseX = baseX / distance * maxDistance;
       baseY = baseY / distance * maxDistance;
       baseZ = baseZ / distance * maxDistance;
       adjustedDistance = maxDistance;
     } 
     
     float var14 = ((float)adjustedDistance * 0.1F + 1.0F) * 0.0266F;
     matrixStack.method_22903();
     matrixStack.method_22904(((float)baseX + 0.5F), ((float)baseY + 0.5F), ((float)baseZ + 0.5F));
     matrixStack.method_22907(class_1160.field_20705.method_23214(-(this.mc.method_1561()).field_4686.method_19330()));
     matrixStack.method_22907(class_1160.field_20703.method_23214((this.mc.method_1561()).field_4686.method_19329()));
     matrixStack.method_22905(-var14, -var14, -var14);
     class_1159 matrix4f = matrixStack.method_23760().method_23761();
     class_289 tessellator = class_289.method_1348();
     class_287 vertexBuffer = tessellator.method_1349();
     float fade = (distance > 5.0D) ? 1.0F : ((float)distance / 5.0F);
     fade = Math.min(fade, (!pt.enabled && !target) ? 0.3F : 1.0F);
     float width = 10.0F;
     float r = target ? 1.0F : pt.red;
     float g = target ? 0.0F : pt.green;
     float b = target ? 0.0F : pt.blue;
     TextureAtlas textureAtlas = AbstractVoxelMap.getInstance().getWaypointManager().getTextureAtlas();
     Sprite icon = target ? textureAtlas.getAtlasSprite("voxelmap:images/waypoints/target.png") : textureAtlas.getAtlasSprite("voxelmap:images/waypoints/waypoint" + pt.imageSuffix + ".png");
     if (icon == textureAtlas.getMissingImage()) {
       icon = textureAtlas.getAtlasSprite("voxelmap:images/waypoints/waypoint.png");
     }
     
     RenderSystem.setShader(class_757::method_34543);
     GLUtils.disp2(textureAtlas.method_4624());
     GLShim.glEnable(3553);
     if (withDepth) {
       GLShim.glDepthMask((distance < maxDistance));
       GLShim.glEnable(2929);
       vertexBuffer.method_1328(class_293.class_5596.field_27382, class_290.field_1575);
       vertexBuffer.method_22918(matrix4f, -width, -width, 0.0F).method_22913(icon.getMinU(), icon.getMinV()).method_22915(r, g, b, 1.0F * fade).method_1344();
       vertexBuffer.method_22918(matrix4f, -width, width, 0.0F).method_22913(icon.getMinU(), icon.getMaxV()).method_22915(r, g, b, 1.0F * fade).method_1344();
       vertexBuffer.method_22918(matrix4f, width, width, 0.0F).method_22913(icon.getMaxU(), icon.getMaxV()).method_22915(r, g, b, 1.0F * fade).method_1344();
       vertexBuffer.method_22918(matrix4f, width, -width, 0.0F).method_22913(icon.getMaxU(), icon.getMinV()).method_22915(r, g, b, 1.0F * fade).method_1344();
       tessellator.method_1350();
     } 
     
     if (withoutDepth) {
       GLShim.glDisable(2929);
       GLShim.glDepthMask(false);
       vertexBuffer.method_1328(class_293.class_5596.field_27382, class_290.field_1575);
       vertexBuffer.method_22918(matrix4f, -width, -width, 0.0F).method_22913(icon.getMinU(), icon.getMinV()).method_22915(r, g, b, 0.3F * fade).method_1344();
       vertexBuffer.method_22918(matrix4f, -width, width, 0.0F).method_22913(icon.getMinU(), icon.getMaxV()).method_22915(r, g, b, 0.3F * fade).method_1344();
       vertexBuffer.method_22918(matrix4f, width, width, 0.0F).method_22913(icon.getMaxU(), icon.getMaxV()).method_22915(r, g, b, 0.3F * fade).method_1344();
       vertexBuffer.method_22918(matrix4f, width, -width, 0.0F).method_22913(icon.getMaxU(), icon.getMinV()).method_22915(r, g, b, 0.3F * fade).method_1344();
       tessellator.method_1350();
     } 
     
     class_327 fontRenderer = this.mc.field_1772;
     if (isPointedAt && fontRenderer != null) {
       byte elevateBy = -19;
       GLShim.glDisable(3553);
       GLShim.glEnable(32823);
       int halfStringWidth = fontRenderer.method_1727(name) / 2;
       RenderSystem.setShader(class_757::method_34540);
       if (withDepth) {
         GLShim.glEnable(2929);
         GLShim.glDepthMask((distance < maxDistance));
         GLShim.glPolygonOffset(1.0F, 7.0F);
         vertexBuffer.method_1328(class_293.class_5596.field_27382, class_290.field_1576);
         vertexBuffer.method_22918(matrix4f, (-halfStringWidth - 2), (-2 + elevateBy), 0.0F).method_22915(pt.red, pt.green, pt.blue, 0.6F * fade).method_1344();
         vertexBuffer.method_22918(matrix4f, (-halfStringWidth - 2), (9 + elevateBy), 0.0F).method_22915(pt.red, pt.green, pt.blue, 0.6F * fade).method_1344();
         vertexBuffer.method_22918(matrix4f, (halfStringWidth + 2), (9 + elevateBy), 0.0F).method_22915(pt.red, pt.green, pt.blue, 0.6F * fade).method_1344();
         vertexBuffer.method_22918(matrix4f, (halfStringWidth + 2), (-2 + elevateBy), 0.0F).method_22915(pt.red, pt.green, pt.blue, 0.6F * fade).method_1344();
         tessellator.method_1350();
         GLShim.glPolygonOffset(1.0F, 5.0F);
         vertexBuffer.method_1328(class_293.class_5596.field_27382, class_290.field_1576);
         vertexBuffer.method_22918(matrix4f, (-halfStringWidth - 1), (-1 + elevateBy), 0.0F).method_22915(0.0F, 0.0F, 0.0F, 0.15F * fade).method_1344();
         vertexBuffer.method_22918(matrix4f, (-halfStringWidth - 1), (8 + elevateBy), 0.0F).method_22915(0.0F, 0.0F, 0.0F, 0.15F * fade).method_1344();
         vertexBuffer.method_22918(matrix4f, (halfStringWidth + 1), (8 + elevateBy), 0.0F).method_22915(0.0F, 0.0F, 0.0F, 0.15F * fade).method_1344();
         vertexBuffer.method_22918(matrix4f, (halfStringWidth + 1), (-1 + elevateBy), 0.0F).method_22915(0.0F, 0.0F, 0.0F, 0.15F * fade).method_1344();
         tessellator.method_1350();
       } 
       
       if (withoutDepth) {
         GLShim.glDisable(2929);
         GLShim.glDepthMask(false);
         GLShim.glPolygonOffset(1.0F, 11.0F);
         vertexBuffer.method_1328(class_293.class_5596.field_27382, class_290.field_1576);
         vertexBuffer.method_22918(matrix4f, (-halfStringWidth - 2), (-2 + elevateBy), 0.0F).method_22915(pt.red, pt.green, pt.blue, 0.15F * fade).method_1344();
         vertexBuffer.method_22918(matrix4f, (-halfStringWidth - 2), (9 + elevateBy), 0.0F).method_22915(pt.red, pt.green, pt.blue, 0.15F * fade).method_1344();
         vertexBuffer.method_22918(matrix4f, (halfStringWidth + 2), (9 + elevateBy), 0.0F).method_22915(pt.red, pt.green, pt.blue, 0.15F * fade).method_1344();
         vertexBuffer.method_22918(matrix4f, (halfStringWidth + 2), (-2 + elevateBy), 0.0F).method_22915(pt.red, pt.green, pt.blue, 0.15F * fade).method_1344();
         tessellator.method_1350();
         GLShim.glPolygonOffset(1.0F, 9.0F);
         vertexBuffer.method_1328(class_293.class_5596.field_27382, class_290.field_1576);
         vertexBuffer.method_22918(matrix4f, (-halfStringWidth - 1), (-1 + elevateBy), 0.0F).method_22915(0.0F, 0.0F, 0.0F, 0.15F * fade).method_1344();
         vertexBuffer.method_22918(matrix4f, (-halfStringWidth - 1), (8 + elevateBy), 0.0F).method_22915(0.0F, 0.0F, 0.0F, 0.15F * fade).method_1344();
         vertexBuffer.method_22918(matrix4f, (halfStringWidth + 1), (8 + elevateBy), 0.0F).method_22915(0.0F, 0.0F, 0.0F, 0.15F * fade).method_1344();
         vertexBuffer.method_22918(matrix4f, (halfStringWidth + 1), (-1 + elevateBy), 0.0F).method_22915(0.0F, 0.0F, 0.0F, 0.15F * fade).method_1344();
         tessellator.method_1350();
       } 
       
       GLShim.glDisable(32823);
       GLShim.glDepthMask(false);
       GLShim.glEnable(3553);
       class_4597.class_4598 vertexConsumerProvider = this.mc.method_22940().method_23000();
       if (withoutDepth) {
         int textColor = (int)(255.0F * fade) << 24 | 0xCCCCCC;
         GLShim.glDisable(2929);
         fontRenderer.method_30882((class_2561)new class_2585(name), (-fontRenderer.method_1727(name) / 2), elevateBy, textColor, false, matrix4f, (class_4597)vertexConsumerProvider, true, 0, 15728880);
         vertexConsumerProvider.method_22993();
         GLShim.glEnable(2929);
         textColor = (int)(255.0F * fade) << 24 | 0xFFFFFF;
         fontRenderer.method_1729(matrixStack, name, (-fontRenderer.method_1727(name) / 2), elevateBy, textColor);
       } 
       
       GLShim.glEnable(3042);
     } 
     
     GLShim.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
     matrixStack.method_22909();
   }
 }
