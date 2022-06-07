 package cn.magicst.mamiyaotaru.voxelmap.util;
 
 import cn.magicst.mamiyaotaru.voxelmap.gui.GuiAddWaypoint;
 import cn.magicst.mamiyaotaru.voxelmap.gui.GuiSelectPlayer;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.AbstractVoxelMap;
 import cn.magicst.mamiyaotaru.voxelmap.interfaces.IVoxelMap;
 import java.util.ArrayList;
 import java.util.Random;
 import java.util.TreeSet;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import net.minecraft.class_124;
 import net.minecraft.class_1922;
 import net.minecraft.class_1937;
 import net.minecraft.class_2248;
 import net.minecraft.class_2338;
 import net.minecraft.class_2558;
 import net.minecraft.class_2561;
 import net.minecraft.class_2568;
 import net.minecraft.class_2583;
 import net.minecraft.class_2680;
 import net.minecraft.class_2806;
 import net.minecraft.class_2902;
 import net.minecraft.class_2960;
 import net.minecraft.class_310;
 import net.minecraft.class_3675;
 import net.minecraft.class_437;
 import net.minecraft.class_5250;
 
 public class CommandUtils {
   private static final String NEW_WAYPOINT_COMMAND = "/newWaypoint ";
   private static final int NEW_WAYPOINT_COMMAND_LENGTH = "/newWaypoint ".length();
   private static final String TELEPORT_COMMAND = "/ztp ";
   private static final int TELEPORT_COMMAND_LENGTH = "/ztp ".length();
   private static Random generator = new Random();
   public static Pattern pattern = Pattern.compile("\\[(\\w+\\s*:\\s*[-#]?[^\\[\\]]+)(,\\s*\\w+\\s*:\\s*[-#]?[^\\[\\]]+)+\\]", 2);
   
   public static boolean checkForWaypoints(class_2561 chat) {
     String message = chat.getString();
     ArrayList<String> waypointStrings = getWaypointStrings(message);
     if (waypointStrings.size() <= 0) {
       return true;
     }
     ArrayList<class_2561> textComponents = new ArrayList<>();
     int count = 0;
     
     for (String waypointString : waypointStrings) {
       int waypointStringLocation = message.indexOf(waypointString);
       if (waypointStringLocation > count) {
         textComponents.add(class_2561.method_43470(message.substring(count, waypointStringLocation)));
       }
       
       class_5250 clickableWaypoint = class_2561.method_43470(waypointString);
       class_2583 chatStyle = clickableWaypoint.method_10866();
       chatStyle = chatStyle.method_10958(new class_2558(class_2558.class_2559.field_11750, "/newWaypoint " + waypointString.substring(1, waypointString.length() - 1)));
       chatStyle = chatStyle.method_10977(class_124.field_1075);
       class_5250 class_52501 = class_2561.method_43470(I18nUtils.getString("minimap.waypointshare.tooltip1", new Object[0]) + "\n" + I18nUtils.getString("minimap.waypointshare.tooltip1", new Object[0]));
       chatStyle = chatStyle.method_10949(new class_2568(class_2568.class_5247.field_24342, class_52501));
       clickableWaypoint.method_10862(chatStyle);
       textComponents.add(clickableWaypoint);
       count = waypointStringLocation + waypointString.length();
     } 
     
     if (count < message.length() - 1) {
       textComponents.add(class_2561.method_43470(message.substring(count, message.length())));
     }
     
     class_5250 finalTextComponent = class_2561.method_43470("");
     
     for (class_2561 textComponent : textComponents) {
       finalTextComponent.method_10852(textComponent);
     }
     
     (class_310.method_1551()).field_1705.method_1743().method_1812((class_2561)finalTextComponent);
     return false;
   }
 
   
   public static ArrayList<String> getWaypointStrings(String message) {
     ArrayList<String> list = new ArrayList<>();
     if (message.contains("[") && message.contains("]")) {
       Matcher matcher = pattern.matcher(message);
       
       while (matcher.find()) {
         String match = matcher.group();
         if (createWaypointFromChat(match.substring(1, match.length() - 1)) != null) {
           list.add(match);
         }
       } 
     } 
     
     return list;
   }
   
   private static Waypoint createWaypointFromChat(String details) {
     Waypoint waypoint = null;
     String[] pairs = details.split(",");
     
     try {
       String name = "";
       Integer x = null;
       Integer z = null;
       int y = 64;
       boolean enabled = true;
       float red = generator.nextFloat();
       float green = generator.nextFloat();
       float blue = generator.nextFloat();
       String suffix = "";
       String world = "";
       TreeSet<DimensionContainer> dimensions = new TreeSet<>();
       
       for (String pair : pairs) {
         int splitIndex = pair.indexOf(":");
         if (splitIndex != -1) {
           String key = pair.substring(0, splitIndex).toLowerCase().trim();
           String value = pair.substring(splitIndex + 1).trim();
           if (key.equals("name")) {
             name = TextUtils.descrubName(value);
           } else if (key.equals("x")) {
             x = Integer.valueOf(Integer.parseInt(value));
           } else if (key.equals("z")) {
             z = Integer.valueOf(Integer.parseInt(value));
           } else if (key.equals("y")) {
             y = Integer.parseInt(value);
           } else if (key.equals("enabled")) {
             enabled = Boolean.parseBoolean(value);
           } else if (key.equals("red")) {
             red = Float.parseFloat(value);
           } else if (key.equals("green")) {
             green = Float.parseFloat(value);
           } else if (key.equals("blue")) {
             blue = Float.parseFloat(value);
           } else if (key.equals("color")) {
             int color = Integer.decode(value).intValue();
             red = (color >> 16 & 0xFF) / 255.0F;
             green = (color >> 8 & 0xFF) / 255.0F;
             blue = (color & 0xFF) / 255.0F;
           } else if (!key.equals("suffix") && !key.equals("icon")) {
             String[] dimensionStrings; switch (key) { case "world":
                 world = TextUtils.descrubName(value); break;
               case "dimensions":
                 dimensionStrings = value.split("#");
                 for (String dimensionString : dimensionStrings)
                   dimensions.add(AbstractVoxelMap.getInstance().getDimensionManager().getDimensionContainerByIdentifier(dimensionString)); 
                 break;
               case "dimension":
               case "dim":
                 dimensions.add(AbstractVoxelMap.getInstance().getDimensionManager().getDimensionContainerByIdentifier(value)); break; }
           
           } else {
             suffix = value;
           } 
         } 
       } 
       
       if (world.equals("")) {
         world = AbstractVoxelMap.getInstance().getWaypointManager().getCurrentSubworldDescriptor(false);
       }
       
       if (dimensions.size() == 0) {
         dimensions.add(AbstractVoxelMap.getInstance().getDimensionManager().getDimensionContainerByWorld((class_1937)(class_310.method_1551()).field_1687));
       }
       
       if (x != null && z != null) {
         if (dimensions.size() == 1 && ((DimensionContainer)dimensions.first()).type.comp_646() != 1.0D) {
           double dimensionScale = ((DimensionContainer)dimensions.first()).type.comp_646();
           x = Integer.valueOf((int)(x.intValue() * dimensionScale));
           z = Integer.valueOf((int)(z.intValue() * dimensionScale));
         } 
         
         waypoint = new Waypoint(name, x.intValue(), z.intValue(), y, enabled, red, green, blue, suffix, world, dimensions);
       } 
     } catch (NumberFormatException var20) {
       waypoint = null;
     } 
     
     return waypoint;
   }
   
   public static void waypointClicked(String command) {
     boolean control = (class_3675.method_15987(class_310.method_1551().method_22683().method_4490(), class_3675.method_15981("key.keyboard.left.control").method_1444()) || class_3675.method_15987(class_310.method_1551().method_22683().method_4490(), class_3675.method_15981("key.keyboard.right.control").method_1444()));
     String details = command.substring(NEW_WAYPOINT_COMMAND_LENGTH);
     Waypoint newWaypoint = createWaypointFromChat(details);
     if (newWaypoint != null) {
       for (Waypoint existingWaypoint : AbstractVoxelMap.getInstance().getWaypointManager().getWaypoints()) {
         if (newWaypoint.getX() == existingWaypoint.getX() && newWaypoint.getZ() == existingWaypoint.getZ()) {
           if (control) {
             class_310.method_1551().method_1507((class_437)new GuiAddWaypoint(null, (IVoxelMap)AbstractVoxelMap.getInstance(), existingWaypoint, true));
           } else {
             AbstractVoxelMap.getInstance().getWaypointManager().setHighlightedWaypoint(existingWaypoint, false);
           } 
           
           return;
         } 
       } 
       
       if (control) {
         class_310.method_1551().method_1507((class_437)new GuiAddWaypoint(null, (IVoxelMap)AbstractVoxelMap.getInstance(), newWaypoint, false));
       } else {
         AbstractVoxelMap.getInstance().getWaypointManager().setHighlightedWaypoint(newWaypoint, false);
       } 
     } 
   }
 
   
   public static void sendWaypoint(Waypoint waypoint) {
     class_2960 resourceLocation = (AbstractVoxelMap.getInstance().getDimensionManager().getDimensionContainerByWorld((class_1937)(class_310.method_1551()).field_1687)).resourceLocation;
     int color = ((int)(waypoint.red * 255.0F) & 0xFF) << 16 | ((int)(waypoint.green * 255.0F) & 0xFF) << 8 | (int)(waypoint.blue * 255.0F) & 0xFF;
     StringBuilder hexColor = new StringBuilder(Integer.toHexString(color));
     
     while (hexColor.length() < 6) {
       hexColor.insert(0, "0");
     }
     
     hexColor.insert(0, "#");
     String world = AbstractVoxelMap.getInstance().getWaypointManager().getCurrentSubworldDescriptor(false);
     if (waypoint.world != null && !waypoint.world.equals("")) {
       world = waypoint.world;
     }
     
     String suffix = waypoint.imageSuffix;
     Object[] args = { TextUtils.scrubNameRegex(waypoint.name), Integer.valueOf(waypoint.getX()), Integer.valueOf(waypoint.getY()), Integer.valueOf(waypoint.getZ()), resourceLocation.toString() };
     String message = String.format("[name:%s, x:%s, y:%s, z:%s, dim:%s", args);
     if (world != null && !world.equals("")) {
       message = message + ", world:" + message;
     }
     
     if (suffix != null && !suffix.equals("")) {
       message = message + ", icon:" + message;
     }
     
     message = message + "]";
     class_310.method_1551().method_1507((class_437)new GuiSelectPlayer((class_437)null, (IVoxelMap)AbstractVoxelMap.getInstance(), message, true));
   }
   
   public static void sendCoordinate(int x, int y, int z) {
     String message = String.format("[x:%s, y:%s, z:%s]", new Object[] { Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(z) });
     class_310.method_1551().method_1507((class_437)new GuiSelectPlayer((class_437)null, (IVoxelMap)AbstractVoxelMap.getInstance(), message, false));
   }
   
   public static void teleport(String command) {
     String details = command.substring(TELEPORT_COMMAND_LENGTH);
     
     for (Waypoint wp : AbstractVoxelMap.getInstance().getWaypointManager().getWaypoints()) {
       if (wp.name.equalsIgnoreCase(details) && wp.inDimension && wp.inWorld) {
         boolean mp = !class_310.method_1551().method_1496();
         int y = (wp.getY() > (class_310.method_1551()).field_1687.method_31607()) ? wp.getY() : (!(class_310.method_1551()).field_1724.field_6002.method_8597().comp_643() ? (class_310.method_1551()).field_1687.method_31600() : 64);
         (class_310.method_1551()).field_1724.method_3142("/tp " + (class_310.method_1551()).field_1724.method_5477().getString() + " " + wp.getX() + " " + y + " " + wp.getZ());
         if (mp) {
           (class_310.method_1551()).field_1724.method_3142("/tppos " + wp.getX() + " " + y + " " + wp.getZ());
         }
         return;
       } 
     } 
   }
 
 
   
   public static int getSafeHeight(int x, int y, int z, class_1937 worldObj) {
     boolean inNetherDimension = worldObj.method_8597().comp_643();
     class_2338 blockPos = new class_2338(x, y, z);
     worldObj.method_22350(blockPos);
     worldObj.method_8398().method_12121(blockPos.method_10263() >> 4, blockPos.method_10260() >> 4, class_2806.field_12803, true);
     if (inNetherDimension) {
       int safeY = -1;
       
       for (int t = 0; t < 127; t++) {
         if (y + t < 127 && isBlockStandable(worldObj, x, y + t, z) && isBlockOpen(worldObj, x, y + t + 1, z) && isBlockOpen(worldObj, x, y + t + 2, z)) {
           safeY = y + t + 1;
           t = 128;
         } 
         
         if (y - t > 0 && isBlockStandable(worldObj, x, y - t, z) && isBlockOpen(worldObj, x, y - t + 1, z) && isBlockOpen(worldObj, x, y - t + 2, z)) {
           safeY = y - t + 1;
           t = 128;
         } 
       } 
       
       y = safeY;
     } else if (y <= 0) {
       y = worldObj.method_8624(class_2902.class_2903.field_13203, x, z) + 1;
     } 
     
     return y;
   }
   
   private static boolean isBlockStandable(class_1937 worldObj, int par1, int par2, int par3) {
     class_2338 blockPos = new class_2338(par1, par2, par3);
     class_2680 blockState = worldObj.method_8320(blockPos);
     class_2248 block = blockState.method_26204();
     return (block != null && blockState.method_26207().method_15804());
   }
   
   private static boolean isBlockOpen(class_1937 worldObj, int par1, int par2, int par3) {
     class_2338 blockPos = new class_2338(par1, par2, par3);
     class_2680 blockState = worldObj.method_8320(blockPos);
     class_2248 block = blockState.method_26204();
     return (block == null || !blockState.method_26228((class_1922)worldObj, blockPos));
   }
 }

