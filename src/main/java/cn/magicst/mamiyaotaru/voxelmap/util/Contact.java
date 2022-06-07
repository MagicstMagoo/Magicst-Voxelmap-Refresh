 package cn.magicst.mamiyaotaru.voxelmap.util;
 
 import cn.magicst.mamiyaotaru.voxelmap.textures.Sprite;
 import java.util.UUID;
 import net.minecraft.class_1297;
 import net.minecraft.class_310;
 
 public class Contact
 {
   public double x;
   public double z;
   public int y;
   public int yFudge = 0;
   public float angle;
   public double distance;
   public float brightness;
   public EnumMobs type;
   public boolean vanillaType;
   public boolean custom = false;
   public UUID uuid = null;
   public String name = "_";
   public int rotationFactor = 0;
   public String skinURL = "";
   public class_1297 entity = null;
   public Sprite icon = null;
   public Sprite armorIcon = null;
   public int armorColor = -1;
   
   public Contact(class_1297 entity, EnumMobs type) {
     this.entity = entity;
     this.type = type;
     this.vanillaType = (type != EnumMobs.GENERICNEUTRAL && type != EnumMobs.GENERICHOSTILE && type != EnumMobs.GENERICTAME && type != EnumMobs.UNKNOWN);
   }
   
   public void setUUID(UUID uuid) {
     this.uuid = uuid;
   }
   
   public void setName(String name) {
     this.name = name;
   }
   
   public void setRotationFactor(int rotationFactor) {
     this.rotationFactor = rotationFactor;
   }
   
   public void setArmorColor(int armorColor) {
     this.armorColor = armorColor;
   }
   
   public void updateLocation() {
     this.x = this.entity.field_6014 + (this.entity.method_23317() - this.entity.field_6014) * class_310.method_1551().method_1488();
     this.y = (int)this.entity.method_23318() + this.yFudge;
     this.z = this.entity.field_5969 + (this.entity.method_23321() - this.entity.field_5969) * class_310.method_1551().method_1488();
   }
 }

