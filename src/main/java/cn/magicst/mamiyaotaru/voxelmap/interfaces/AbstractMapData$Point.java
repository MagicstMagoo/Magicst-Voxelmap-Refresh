 package cn.magicst.mamiyaotaru.voxelmap.interfaces;
 
 class Point
 {
   public int x;
   public int z;
   public boolean inSegment = false;
   public boolean isCandidate = false;
   public int layer = -1;
   public int biomeID = -1;
   
   public Point(int x, int z, int biomeID) {
     this.x = x;
     this.z = z;
     if (biomeID == 255 || biomeID == -1) {
       biomeID = -1;
       this.inSegment = true;
     } 
     
     this.biomeID = biomeID;
   }
 }


