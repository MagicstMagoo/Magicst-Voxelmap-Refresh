 package cn.magicst.mamiyaotaru.voxelmap.gui;
 
 import cn.magicst.mamiyaotaru.voxelmap.RadarSettingsManager;
 import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.GuiSlotMinimap;
 import cn.magicst.mamiyaotaru.voxelmap.util.CustomMob;
 import cn.magicst.mamiyaotaru.voxelmap.util.CustomMobsManager;
 import cn.magicst.mamiyaotaru.voxelmap.util.EnumMobs;
 import cn.magicst.mamiyaotaru.voxelmap.util.GLShim;
 import cn.magicst.mamiyaotaru.voxelmap.util.GLUtils;
 import cn.magicst.mamiyaotaru.voxelmap.util.I18nUtils;
 import cn.magicst.mamiyaotaru.voxelmap.util.TextUtils;
 import java.text.Collator;
 import java.util.ArrayList;
 import java.util.Iterator;
 import net.minecraft.class_2561;
 import net.minecraft.class_2588;
 import net.minecraft.class_2960;
 import net.minecraft.class_332;
 import net.minecraft.class_333;
 import net.minecraft.class_350;
 import net.minecraft.class_4587;
 
 class GuiSlotMobs
   extends GuiSlotMinimap
 {
   private final ArrayList<MobItem> mobs;
   private ArrayList<?> mobsFiltered;
   final GuiMobs parentGui;
   final class_2588 ENABLE = new class_2588("options.minimap.mobs.enable");
   final class_2588 DISABLE = new class_2588("options.minimap.mobs.disable");
   final class_2588 ENABLED = new class_2588("options.minimap.mobs.enabled");
   final class_2588 DISABLED = new class_2588("options.minimap.mobs.disabled");
   final class_2960 visibleIconIdentifier = new class_2960("textures/mob_effect/night_vision.png");
   final class_2960 invisibleIconIdentifier = new class_2960("textures/mob_effect/blindness.png");
   
   public GuiSlotMobs(GuiMobs par1GuiMobs) {
     super(par1GuiMobs.options.game, par1GuiMobs.getWidth(), par1GuiMobs.getHeight(), 32, par1GuiMobs.getHeight() - 65 + 4, 18);
     this.parentGui = par1GuiMobs;
     RadarSettingsManager options = this.parentGui.options;
     this.mobs = new ArrayList<>();
     
     for (EnumMobs mob : EnumMobs.values()) {
       if (mob.isTopLevelUnit && ((mob.isHostile && options.showHostiles) || (mob.isNeutral && options.showNeutrals))) {
         this.mobs.add(new MobItem(this.parentGui, mob.id));
       }
     } 
     
     for (CustomMob mob : CustomMobsManager.mobs) {
       if ((mob.isHostile && options.showHostiles) || (mob.isNeutral && options.showNeutrals)) {
         this.mobs.add(new MobItem(this.parentGui, mob.id));
       }
     } 
     
     Collator collator = I18nUtils.getLocaleAwareCollator();
     this.mobs.sort((mob1, mob2) -> collator.compare(mob1.name, mob2.name));
     this.mobsFiltered = new ArrayList(this.mobs);
     this.mobsFiltered.forEach(x$0 -> method_25321((class_350.class_351)x$0));
   }
   
   private static String getTranslatedName(String name) {
     if (!name.contains(".")) {
       name = "entity.minecraft." + name.toLowerCase();
     }
     
     name = I18nUtils.getString(name, new Object[0]);
     name = name.replaceAll("^entity.minecraft.", "");
     name = name.replace("_", " ");
     name = name.substring(0, 1).toUpperCase() + name.substring(0, 1).toUpperCase();
     return TextUtils.scrubCodes(name);
   }
   
   public void setSelected(MobItem item) {
     method_25313(item);
     if (method_25334() instanceof MobItem) {
       class_333.field_2054.method_19788((new class_2588("narrator.select", new Object[] { ((MobItem)method_25334()).name })).getString());
     }
     
     this.parentGui.setSelectedMob(item.id);
   }
   
   protected boolean method_25332(int par1) {
     return ((MobItem)this.mobsFiltered.get(par1)).id.equals(this.parentGui.selectedMobId);
   }
   
   protected int method_25317() {
     return method_25340() * this.field_22741;
   }
   
   public void method_25325(class_4587 matrixStack) {
     this.parentGui.method_25420(matrixStack);
   }
   
   protected void updateFilter(String filterString) {
     method_25339();
     this.mobsFiltered = new ArrayList(this.mobs);
     Iterator<?> iterator = this.mobsFiltered.iterator();
     
     while (iterator.hasNext()) {
       String mobName = ((MobItem)iterator.next()).name;
       if (!mobName.toLowerCase().contains(filterString)) {
         if (mobName.equals(this.parentGui.selectedMobId)) {
           this.parentGui.setSelectedMob((String)null);
         }
         
         iterator.remove();
       } 
     } 
     
     this.mobsFiltered.forEach(x$0 -> method_25321((class_350.class_351)x$0));
   }
   
   public class MobItem extends class_350.class_351<MobItem> {
     private final GuiMobs parentGui;
     private final String id;
     private final String name;
     
     protected MobItem(GuiMobs mobsScreen, String id) {
       this.parentGui = mobsScreen;
       this.id = id;
       this.name = GuiSlotMobs.getTranslatedName(id);
     }
     
     public void method_25343(class_4587 matrixStack, int slotIndex, int slotYPos, int leftEdge, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean mouseOver, float partialTicks) {
       boolean isHostile = false;
       boolean isNeutral = false;
       boolean isEnabled = true;
       EnumMobs mob = EnumMobs.getMobByName(this.id);
       if (mob != null) {
         isHostile = mob.isHostile;
         isNeutral = mob.isNeutral;
         isEnabled = mob.enabled;
       } else {
         CustomMob customMob = CustomMobsManager.getCustomMobByType(this.id);
         if (customMob != null) {
           isHostile = customMob.isHostile;
           isNeutral = customMob.isNeutral;
           isEnabled = customMob.enabled;
         } 
       } 
       
       int red = isHostile ? 255 : 0;
       int green = isNeutral ? 255 : 0;
       int color = -16777216 + (red << 16) + (green << 8);
       class_332.method_25300(matrixStack, this.parentGui.getFontRenderer(), this.name, this.parentGui.getWidth() / 2, slotYPos + 3, color);
       byte padding = 3;
       if (mouseX >= leftEdge - padding && mouseY >= slotYPos && mouseX <= leftEdge + 215 + padding && mouseY <= slotYPos + GuiSlotMobs.this.field_22741) {
         class_2588 tooltip;
         if (mouseX >= leftEdge + 215 - 16 - padding && mouseX <= leftEdge + 215 + padding) {
           tooltip = isEnabled ? GuiSlotMobs.this.DISABLE : GuiSlotMobs.this.ENABLE;
         } else {
           tooltip = isEnabled ? GuiSlotMobs.this.ENABLED : GuiSlotMobs.this.DISABLED;
         } 
         
         GuiMobs.setTooltip(this.parentGui, (class_2561)tooltip);
       } 
       
       GLShim.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
       GLUtils.img2(isEnabled ? GuiSlotMobs.this.visibleIconIdentifier : GuiSlotMobs.this.invisibleIconIdentifier);
       class_332.method_25291(matrixStack, leftEdge + 198, slotYPos - 2, GuiSlotMobs.this.method_25305(), 0.0F, 0.0F, 18, 18, 18, 18);
     }
     
     public boolean method_25402(double mouseX, double mouseY, int mouseEvent) {
       GuiSlotMobs.this.setSelected(this);
       int leftEdge = this.parentGui.getWidth() / 2 - 92 - 16;
       byte padding = 3;
       int width = 215;
       if (mouseX >= (leftEdge + width - 16 - padding) && mouseX <= (leftEdge + width + padding)) {
         this.parentGui.toggleMobVisibility();
       } else if (GuiSlotMobs.this.doubleclick) {
         this.parentGui.toggleMobVisibility();
       } 
       
       return true;
     }
   }
 }