 package cn.magicst.mamiyaotaru.voxelmap;
 
 import cn.magicst.mamiyaotaru.voxelmap.util.Contact;
 import java.util.Comparator;

 class null
   implements Comparator<Contact>
 {
   public int compare(Contact contact1, Contact contact2) {
     return contact1.y - contact2.y;
   }
 }
