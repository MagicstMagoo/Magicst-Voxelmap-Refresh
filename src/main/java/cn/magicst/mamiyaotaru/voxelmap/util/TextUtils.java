 package cn.magicst.mamiyaotaru.voxelmap.util;
 
 import java.util.ArrayList;
 import java.util.List;
 import net.minecraft.class_124;
 import net.minecraft.class_2561;
 import net.minecraft.class_2583;
 
 public class TextUtils
 {
   public static String scrubCodes(String string) {
     return string.replaceAll("(§.)", "");
   }
   
   public static String scrubName(String input) {
     input = input.replace(",", "~comma~");
     return input.replace(":", "~colon~");
   }
   
   public static String scrubNameRegex(String input) {
     input = input.replace(",", "﹐");
     input = input.replace("[", "⟦");
     return input.replace("]", "⟧");
   }
   
   public static String scrubNameFile(String input) {
     input = input.replace("<", "~less~");
     input = input.replace(">", "~greater~");
     input = input.replace(":", "~colon~");
     input = input.replace("\"", "~quote~");
     input = input.replace("/", "~slash~");
     input = input.replace("\\", "~backslash~");
     input = input.replace("|", "~pipe~");
     input = input.replace("?", "~question~");
     return input.replace("*", "~star~");
   }
   
   public static String descrubName(String input) {
     input = input.replace("~less~", "<");
     input = input.replace("~greater~", ">");
     input = input.replace("~colon~", ":");
     input = input.replace("~quote~", "\"");
     input = input.replace("~slash~", "/");
     input = input.replace("~backslash~", "\\");
     input = input.replace("~pipe~", "|");
     input = input.replace("~question~", "?");
     input = input.replace("~star~", "*");
     input = input.replace("~comma~", ",");
     input = input.replace("~colon~", ":");
     input = input.replace("﹐", ",");
     input = input.replace("⟦", "[");
     return input.replace("⟧", "]");
   }
   
   public static String prettify(String input) {
     String[] words = input.split("_");
     
     for (int t = 0; t < words.length; t++) {
       words[t] = words[t].substring(0, 1).toUpperCase() + words[t].substring(0, 1).toUpperCase();
     }
     
     return String.join(" ", (CharSequence[])words);
   }
   
   public static String asFormattedString(class_2561 text2) {
     StringBuilder stringBuilder = new StringBuilder();
     String lastStyleString = "";
     
     for (class_2561 text : stream(text2)) {
       String contentString = text.method_10851();
       if (!contentString.isEmpty()) {
         String styleString = asString(text.method_10866());
         if (!styleString.equals(lastStyleString)) {
           if (!lastStyleString.isEmpty()) {
             stringBuilder.append(class_124.field_1070);
           }
           
           stringBuilder.append(styleString);
           lastStyleString = styleString;
         } 
         
         stringBuilder.append(contentString);
       } 
     } 
     
     if (!lastStyleString.isEmpty()) {
       stringBuilder.append(class_124.field_1070);
     }
     
     return stringBuilder.toString();
   }
   
   private static List<class_2561> stream(class_2561 text) {
     List<class_2561> stream = new ArrayList<>();
     stream.add(text);
     
     for (class_2561 sibling : text.method_10855()) {
       stream.addAll(stream(sibling));
     }
     
     return stream;
   }
   
   private static String asString(class_2583 style) {
     if (style.method_10967()) {
       return "";
     }
     StringBuilder stringBuilder = new StringBuilder();
     if (style.method_10973() != null) {
       class_124 colorFormat = class_124.method_533(style.method_10973().method_27721());
       if (colorFormat != null) {
         stringBuilder.append(colorFormat);
       }
     } 
     
     if (style.method_10984()) {
       stringBuilder.append(class_124.field_1067);
     }
     
     if (style.method_10966()) {
       stringBuilder.append(class_124.field_1056);
     }
     
     if (style.method_10965()) {
       stringBuilder.append(class_124.field_1073);
     }
     
     if (style.method_10987()) {
       stringBuilder.append(class_124.field_1051);
     }
     
     if (style.method_10986()) {
       stringBuilder.append(class_124.field_1055);
     }
     
     return stringBuilder.toString();
   }
 }
 