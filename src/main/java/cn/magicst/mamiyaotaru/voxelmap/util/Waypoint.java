 package cn.magicst.mamiyaotaru.voxelmap.util;
 
 import java.io.Serializable;
 import java.util.Locale;
 import java.util.TreeSet;
 import net.minecraft.class_1297;
 import net.minecraft.class_310;
 
 public class Waypoint
   implements Serializable, Comparable<Waypoint> {
   private static final long serialVersionUID = 8136790917447997951L;
   public String name;
   public String imageSuffix = "";
   public String world = "";
   public TreeSet<DimensionContainer> dimensions = new TreeSet<>();
   public int x;
   public int z;
   public int y;
   public boolean enabled;
   public boolean inWorld = true;
   public boolean inDimension = true;
   public float red = 0.0F;
   public float green = 1.0F;
   public float blue = 0.0F;
   
   public Waypoint(String name, int x, int z, int y, boolean enabled, float red, float green, float blue, String suffix, String world, TreeSet<DimensionContainer> dimensions) {
     this.name = name;
     this.x = x;
     this.z = z;
     this.y = y;
     this.enabled = enabled;
     this.red = red;
     this.green = green;
     this.blue = blue;
     this.imageSuffix = suffix.toLowerCase(Locale.ROOT);
     this.world = world;
     this.dimensions = dimensions;
   }
   
   public int getUnifiedColor() {
     return -16777216 + ((int)(this.red * 255.0F) << 16) + ((int)(this.green * 255.0F) << 8) + (int)(this.blue * 255.0F);
   }
   
   public boolean isActive() {
     return (this.enabled && this.inWorld && this.inDimension);
   }
   
   public int getX() {
     return (int)(this.x / (class_310.method_1551()).field_1724.field_6002.method_8597().comp_646());
   }
   
   public int getZ() {
     return (int)(this.z / (class_310.method_1551()).field_1724.field_6002.method_8597().comp_646());
   }
   
   public int getY() {
     return this.y;
   }
   
   public void setX(int x) {
     this.x = (int)(x * (class_310.method_1551()).field_1724.field_6002.method_8597().comp_646());
   }
   
   public void setZ(int z) {
     this.z = (int)(z * (class_310.method_1551()).field_1724.field_6002.method_8597().comp_646());
   }
   
   public void setY(int y) {
     this.y = y;
   }
   
   public int compareTo(Waypoint arg0) {
     double myDistance = getDistanceSqToEntity((class_1297)(class_310.method_1551()).field_1724);
     double comparedDistance = arg0.getDistanceSqToEntity((class_1297)(class_310.method_1551()).field_1724);
     return Double.compare(myDistance, comparedDistance);
   }
   
   public double getDistanceSqToEntity(class_1297 par1Entity) {
     double var2 = getX() + 0.5D - par1Entity.method_23317();
     double var4 = getY() + 0.5D - par1Entity.method_23318();
     double var6 = getZ() + 0.5D - par1Entity.method_23321();
     return var2 * var2 + var4 * var4 + var6 * var6;
   }
   
   public boolean equals(Object otherObject) {
     if (this == otherObject)
       return true; 
     if (!(otherObject instanceof Waypoint)) {
       return false;
     }
     Waypoint otherWaypoint = (Waypoint)otherObject;
     return (this.name.equals(otherWaypoint.name) && this.imageSuffix.equals(otherWaypoint.imageSuffix) && this.world.equals(otherWaypoint.world) && this.x == otherWaypoint.x && this.y == otherWaypoint.y && this.z == otherWaypoint.z && this.red == otherWaypoint.red && this.green == otherWaypoint.green && this.blue == otherWaypoint.blue && this.dimensions.equals(otherWaypoint.dimensions));
   }
 }

