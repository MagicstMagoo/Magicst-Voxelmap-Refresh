 package cn.magicst.mamiyaotaru.voxelmap.util;
 
 import net.minecraft.class_1297;
 import net.minecraft.class_1420;
 import net.minecraft.class_1428;
 import net.minecraft.class_1430;
 import net.minecraft.class_1431;
 import net.minecraft.class_1433;
 import net.minecraft.class_1438;
 import net.minecraft.class_1439;
 import net.minecraft.class_1440;
 import net.minecraft.class_1451;
 import net.minecraft.class_1452;
 import net.minecraft.class_1453;
 import net.minecraft.class_1454;
 import net.minecraft.class_1456;
 import net.minecraft.class_1462;
 import net.minecraft.class_1463;
 import net.minecraft.class_1472;
 import net.minecraft.class_1473;
 import net.minecraft.class_1474;
 import net.minecraft.class_1477;
 import net.minecraft.class_1481;
 import net.minecraft.class_1493;
 import net.minecraft.class_1495;
 import net.minecraft.class_1498;
 import net.minecraft.class_1500;
 import net.minecraft.class_1501;
 import net.minecraft.class_1506;
 import net.minecraft.class_1507;
 import net.minecraft.class_1510;
 import net.minecraft.class_1528;
 import net.minecraft.class_1545;
 import net.minecraft.class_1548;
 import net.minecraft.class_1549;
 import net.minecraft.class_1550;
 import net.minecraft.class_1551;
 import net.minecraft.class_1559;
 import net.minecraft.class_1560;
 import net.minecraft.class_1564;
 import net.minecraft.class_1571;
 import net.minecraft.class_1576;
 import net.minecraft.class_1577;
 import net.minecraft.class_1581;
 import net.minecraft.class_1584;
 import net.minecraft.class_1589;
 import net.minecraft.class_1590;
 import net.minecraft.class_1593;
 import net.minecraft.class_1604;
 import net.minecraft.class_1606;
 import net.minecraft.class_1613;
 import net.minecraft.class_1614;
 import net.minecraft.class_1621;
 import net.minecraft.class_1627;
 import net.minecraft.class_1628;
 import net.minecraft.class_1632;
 import net.minecraft.class_1634;
 import net.minecraft.class_1639;
 import net.minecraft.class_1640;
 import net.minecraft.class_1641;
 import net.minecraft.class_1642;
 import net.minecraft.class_1646;
 import net.minecraft.class_2960;
 import net.minecraft.class_3701;
 import net.minecraft.class_3986;
 import net.minecraft.class_3989;
 import net.minecraft.class_4019;
 import net.minecraft.class_4466;
 import net.minecraft.class_4760;
 import net.minecraft.class_4836;
 import net.minecraft.class_4985;
 import net.minecraft.class_5136;
 import net.minecraft.class_5762;
 import net.minecraft.class_5776;
 import net.minecraft.class_6053;
 import net.minecraft.class_745;
 
 public enum EnumMobs {
   GENERICHOSTILE((Class)null, "Monster", false, 8.0F, "textures/entity/zombie/zombie.png", "", true, false), GENERICNEUTRAL((Class)null, "Mob", false, 8.0F, "textures/entity/pig/pig.png", "", false, true), GENERICTAME((Class)null, "Unknown_Tame", false, 8.0F, "textures/entity/wolf/wolf.png", "", false, true), AXOLOTL(class_5762.class, "Axolotl", true, 0.0F, "textures/entity/axolotl/axolotl_blue.png", "", false, true), BAT(class_1420.class, "Bat", true, 4.0F, "textures/entity/bat.png", "", false, true), BEE(class_4466.class, "Bee", true, 0.0F, "textures/entity/bee/bee.png", "", true, true), BLAZE(class_1545.class, "Blaze", true, 0.0F, "textures/entity/blaze.png", "", true, false), CAT(class_1451.class, "Cat", true, 0.0F, "textures/entity/cat/siamese.png", "", false, true), CAVESPIDER(class_1549.class, "Cave_Spider", true, 0.0F, "textures/entity/spider/cave_spider.png", "", true, false), CHICKEN(class_1428.class, "Chicken", true, 6.0F, "textures/entity/chicken.png", "", false, true), COD(class_1431.class, "Cod", true, 8.0F, "textures/entity/fish/cod.png", "", false, true), COW(class_1430.class, "Cow", true, 0.0F, "textures/entity/cow/cow.png", "", false, true), CREEPER(class_1548.class, "Creeper", true, 0.0F, "textures/entity/creeper/creeper.png", "", true, false), DOLPHIN(class_1433.class, "Dolphin", true, 0.0F, "textures/entity/dolphin.png", "", false, true), DROWNED(class_1551.class, "Drowned", true, 0.0F, "textures/entity/zombie/drowned.png", "textures/entity/zombie/drowned_outer_layer.png", true, false), ENDERDRAGON(class_1510.class, "Ender_Dragon", true, 16.0F, "textures/entity/enderdragon/dragon.png", "", true, false), ENDERMAN(class_1560.class, "Enderman", true, 0.0F, "textures/entity/enderman/enderman.png", "textures/entity/enderman/enderman_eyes.png", true, false), ENDERMITE(class_1559.class, "Endermite", true, 0.0F, "textures/entity/endermite.png", "", true, false), EVOKER(class_1564.class, "Evoker", true, 0.0F, "textures/entity/illager/evoker.png", "", true, false), FOX(class_4019.class, "Fox", true, 0.0F, "textures/entity/fox/fox.png", "", false, true), GHAST(class_1571.class, "Ghast", true, 16.0F, "textures/entity/ghast/ghast.png", "", true, false), GHASTATTACKING((Class)null, "Ghast", false, 16.0F, "textures/entity/ghast/ghast_shooting.png", "", true, false), GLOWSQUID(class_5776.class, "Glow_Squid", true, 0.0F, "textures/entity/squid/glow_squid.png", "", false, true), GOAT(class_6053.class, "Goat", true, 0.0F, "textures/entity/goat/goat.png", "", false, true), GUARDIAN(class_1577.class, "Guardian", true, 6.0F, "textures/entity/guardian.png", "", true, false), GUARDIANELDER(class_1550.class, "Elder_Guardian", true, 12.0F, "textures/entity/guardian_elder.png", "", true, false), HOGLIN(class_4760.class, "Hoglin", true, 0.0F, "textures/entity/hoglin/hoglin.png", "", true, false), HORSE(class_1498.class, "Horse", true, 8.0F, "textures/entity/horse/horse_creamy.png", "textures/entity/horse/horse_markings_white.png", false, true), HUSK(class_1576.class, "Husk", true, 0.0F, "textures/entity/zombie/husk.png", "", true, false), ILLUSIONER(class_1581.class, "Illusioner", true, 0.0F, "textures/entity/illager/illusioner.png", "", true, false), IRONGOLEM(class_1439.class, "Iron_Golem", true, 8.0F, "textures/entity/iron_golem/iron_golem.png", "", false, true), LLAMA(class_1501.class, "Llama", true, 8.0F, "textures/entity/llama/brown.png", "", false, true), LLAMATRADER(class_3986.class, "Trader_Llama", true, 8.0F, "textures/entity/llama/brown.png", "", false, true), MAGMA(class_1589.class, "Magma_Cube", true, 8.0F, "textures/entity/slime/magmacube.png", "", true, false), MOOSHROOM(class_1438.class, "Mooshroom", true, 40.0F, "textures/entity/cow/red_mooshroom.png", "", false, true), OCELOT(class_3701.class, "Ocelot", true, 0.0F, "textures/entity/cat/ocelot.png", "", false, true), PANDA(class_1440.class, "Panda", true, 0.0F, "textures/entity/panda/panda.png", "", true, true), PARROT(class_1453.class, "Parrot", true, 8.0F, "textures/entity/parrot/parrot_red_blue.png", "", false, true), PHANTOM(class_1593.class, "Phantom", true, 10.0F, "textures/entity/phantom.png", "textures/entity/phantom_eyes.png", true, false), PIG(class_1452.class, "Pig", true, 0.0F, "textures/entity/pig/pig.png", "", false, true), PIGLIN(class_4836.class, "Piglin", true, 0.0F, "textures/entity/piglin/piglin.png", "", true, false), PIGLINZOMBIE(class_1590.class, "Zombie_Piglin", true, 0.0F, "textures/entity/piglin/zombified_piglin.png", "", true, true), PILLAGER(class_1604.class, "Pillager", true, 0.0F, "textures/entity/illager/pillager.png", "", true, false), PLAYER(class_745.class, "Player", false, 8.0F, "textures/entity/steve.png", "", false, false), POLARBEAR(class_1456.class, "Polar_Bear", true, 0.0F, "textures/entity/bear/polarbear.png", "", true, true), PUFFERFISH(class_1454.class, "Pufferfish", true, 3.0F, "textures/entity/fish/pufferfish.png", "", false, true), PUFFERFISHHALF((Class)null, "Pufferfish_Half", false, 5.0F, "textures/entity/fish/pufferfish.png", "", false, true), PUFFERFISHFULL((Class)null, "Pufferfish_Full", false, 8.0F, "textures/entity/fish/pufferfish.png", "", false, true), RABBIT(class_1463.class, "Rabbit", true, 0.0F, "textures/entity/rabbit/salt.png", "", false, true), RAVAGER(class_1584.class, "Ravager", true, 0.0F, "textures/entity/illager/ravager.png", "", true, false), SALMON(class_1462.class, "Salmon", true, 13.0F, "textures/entity/fish/salmon.png", "", false, true), SHEEP(class_1472.class, "Sheep", true, 0.0F, "textures/entity/sheep/sheep.png", "", false, true), SHULKER(class_1606.class, "Shulker", true, 0.0F, "textures/entity/shulker/shulker_purple.png", "", true, false), SILVERFISH(class_1614.class, "Silverfish", true, 0.0F, "textures/entity/silverfish.png", "", true, false), SKELETON(class_1613.class, "Skeleton", true, 0.0F, "textures/entity/skeleton/skeleton.png", "", true, false), SKELETONWITHER(class_1639.class, "Wither_Skeleton", true, 0.0F, "textures/entity/skeleton/wither_skeleton.png", "", true, false), SLIME(class_1621.class, "Slime", true, 8.0F, "textures/entity/slime/slime.png", "", true, false), SNOWGOLEM(class_1473.class, "Snow_Golem", true, 0.0F, "textures/entity/snow_golem.png", "", false, true), SPIDER(class_1628.class, "Spider", true, 0.0F, "textures/entity/spider/spider.png", "", true, false), SQUID(class_1477.class, "Squid", true, 0.0F, "textures/entity/squid/squid.png", "", false, true), STRAY(class_1627.class, "Stray", true, 0.0F, "textures/entity/skeleton/stray.png", "textures/entity/skeleton/stray_overlay.png", true, false), STRIDER(class_4985.class, "Strider", true, 0.0F, "textures/entity/strider/strider.png", "", false, true), TROPICALFISHA(class_1474.class, "Tropical_Fish", true, 5.0F, "textures/entity/fish/tropical_a.png", "textures/entity/fish/tropical_a_pattern_1.png", false, true), TROPICALFISHB((Class)null, "Tropical_Fish", false, 6.0F, "textures/entity/fish/tropical_b.png", "textures/entity/fish/tropical_b_pattern_4.png", false, true), TURTLE(class_1481.class, "Turtle", true, 0.0F, "textures/entity/turtle/big_sea_turtle.png", "", false, true), VEX(class_1634.class, "Vex", true, 0.0F, "textures/entity/illager/vex.png", "", true, false), VEXCHARGING((Class)null, "Vex", false, 0.0F, "textures/entity/illager/vex_charging.png", "", true, false), VILLAGER(class_1646.class, "Villager", true, 0.0F, "textures/entity/villager/villager.png", "textures/entity/villager/profession/farmer.png", false, true), VINDICATOR(class_1632.class, "Vindicator", true, 0.0F, "textures/entity/illager/vindicator.png", "", true, false), WANDERINGTRADER(class_3989.class, "Wandering_Trader", true, 0.0F, "textures/entity/wandering_trader.png", "", false, true), WITCH(class_1640.class, "Witch", true, 0.0F, "textures/entity/witch.png", "", true, false), WITHER(class_1528.class, "Wither", true, 24.0F, "textures/entity/wither/wither.png", "", true, false), WITHERINVULNERABLE((Class)null, "Wither", false, 24.0F, "textures/entity/wither/wither_invulnerable.png", "", true, false), WOLF(class_1493.class, "Wolf", true, 0.0F, "textures/entity/wolf/wolf.png", "", true, true), ZOGLIN(class_5136.class, "Zoglin", true, 0.0F, "textures/entity/hoglin/zoglin.png", "", true, false), ZOMBIE(class_1642.class, "Zombie", true, 0.0F, "textures/entity/zombie/zombie.png", "", true, false), ZOMBIEVILLAGER(class_1641.class, "Zombie_villager", true, 0.0F, "textures/entity/zombie_villager/zombie_villager.png", "textures/entity/zombie_villager/profession/farmer.png", true, false), UNKNOWN((Class)null, "Unknown", false, 8.0F, "/mob/uknown.png", "", true, true);
   
   public boolean enabled;
   public final boolean isNeutral;
   public final boolean isHostile;
   public class_2960 secondaryResourceLocation;
   public final class_2960 resourceLocation;
   public final float expectedWidth;
   public final boolean isTopLevelUnit;
   public final String id;
   public final Class clazz;
   
   public static EnumMobs getMobByName(String par0) {
     for (EnumMobs enumMob : values()) {
       if (enumMob.id.equals(par0)) {
         return enumMob;
       }
     } 
     
     return null;
   }
   
   public static EnumMobs getMobTypeByEntity(class_1297 entity) {
     Class<?> clazz = entity.getClass();
     if (clazz.equals(class_1474.class)) {
       return (((class_1474)entity).method_6654() == 0) ? TROPICALFISHA : TROPICALFISHB;
     }
     return getMobTypeByClass(clazz);
   }
 
   
   private static EnumMobs getMobTypeByClass(Class<?> clazz) {
     if (class_745.class.isAssignableFrom(clazz))
       return PLAYER; 
     if (!clazz.equals(class_1498.class) && !clazz.equals(class_1495.class) && !clazz.equals(class_1500.class) && !clazz.equals(class_1506.class) && !clazz.equals(class_1507.class)) {
       for (EnumMobs enumMob : values()) {
         if (clazz.equals(enumMob.clazz)) {
           return enumMob;
         }
       } 
       
       return UNKNOWN;
     } 
     return HORSE;
   }
 
   
   EnumMobs(Class clazz, String name, boolean topLevelUnit, float expectedWidth, String path, String secondaryPath, boolean isHostile, boolean isNeutral) {
     this.clazz = clazz;
     this.id = name;
     this.isTopLevelUnit = topLevelUnit;
     this.expectedWidth = expectedWidth;
     this.resourceLocation = new class_2960(path.toLowerCase());
     this.secondaryResourceLocation = secondaryPath.equals("") ? null : new class_2960(secondaryPath.toLowerCase());
     this.isHostile = isHostile;
     this.isNeutral = isNeutral;
     this.enabled = true;
   }
   
   public int returnEnumOrdinal() {
     return ordinal();
   }
 
   
   private static EnumMobs[] $values() {
     return new EnumMobs[] { GENERICHOSTILE, GENERICNEUTRAL, GENERICTAME, AXOLOTL, BAT, BEE, BLAZE, CAT, CAVESPIDER, CHICKEN, COD, COW, CREEPER, DOLPHIN, DROWNED, ENDERDRAGON, ENDERMAN, ENDERMITE, EVOKER, FOX, GHAST, GHASTATTACKING, GLOWSQUID, GOAT, GUARDIAN, GUARDIANELDER, HOGLIN, HORSE, HUSK, ILLUSIONER, IRONGOLEM, LLAMA, LLAMATRADER, MAGMA, MOOSHROOM, OCELOT, PANDA, PARROT, PHANTOM, PIG, PIGLIN, PIGLINZOMBIE, PILLAGER, PLAYER, POLARBEAR, PUFFERFISH, PUFFERFISHHALF, PUFFERFISHFULL, RABBIT, RAVAGER, SALMON, SHEEP, SHULKER, SILVERFISH, SKELETON, SKELETONWITHER, SLIME, SNOWGOLEM, SPIDER, SQUID, STRAY, STRIDER, TROPICALFISHA, TROPICALFISHB, TURTLE, VEX, VEXCHARGING, VILLAGER, VINDICATOR, WANDERINGTRADER, WITCH, WITHER, WITHERINVULNERABLE, WOLF, ZOGLIN, ZOMBIE, ZOMBIEVILLAGER, UNKNOWN };
   }
 }
 