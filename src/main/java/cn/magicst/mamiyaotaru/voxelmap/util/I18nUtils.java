 package cn.magicst.mamiyaotaru.voxelmap.util;
 
 import java.text.Collator;
 import java.util.Locale;
 import net.minecraft.class_1074;
 import net.minecraft.class_310;
 
 public class I18nUtils
 {
   public static String getString(String translateMe, Object... args) {
     return class_1074.method_4662(translateMe, args);
   }
   
   public static Collator getLocaleAwareCollator() {
     String mcLocale = "en_US";
     
     try {
       mcLocale = class_310.method_1551().method_1526().method_4669().getCode();
     } catch (NullPointerException nullPointerException) {}
 
     
     String[] bits = mcLocale.split("_");
     Locale locale = new Locale(bits[0], (bits.length > 1) ? bits[1] : "");
     return Collator.getInstance(locale);
   }
 }
