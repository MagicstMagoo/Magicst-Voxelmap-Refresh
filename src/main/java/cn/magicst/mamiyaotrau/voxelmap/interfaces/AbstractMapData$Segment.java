 package cn.magicst.mamiyaotaru.voxelmap.interfaces;
 
 import cn.magicst.mamiyaotaru.voxelmap.util.BiomeRepository;
 import java.util.ArrayList;
 import java.util.List;
 import net.minecraft.class_310;
 
 public class Segment
 {
   public ArrayList<AbstractMapData.Point> memberPoints;
   ArrayList<AbstractMapData.Point> currentShell;
   public int biomeID;
   public String name = null;
   public int centerX = 0;
   public int centerZ = 0;
   
   public Segment(AbstractMapData.Point point) {
     this.biomeID = point.biomeID;
     if (this.biomeID != -1) {
       this.name = BiomeRepository.getName(this.biomeID);
     }
     
     this.memberPoints = new ArrayList<>();
     this.memberPoints.add(point);
     this.currentShell = new ArrayList<>();
   }
   
   public void flood() {
     ArrayList<AbstractMapData.Point> candidatePoints = new ArrayList<>();
     candidatePoints.add(this.memberPoints.remove(0));
     
     while (candidatePoints.size() > 0) {
       AbstractMapData.Point point = candidatePoints.remove(0);
       point.isCandidate = false;
       if (point.biomeID == this.biomeID) {
         this.memberPoints.add(point);
         point.inSegment = true;
         boolean edge = false;
         if (point.x < AbstractMapData.this.width - 1) {
           AbstractMapData.Point neighbor = AbstractMapData.this.points[point.x + 1][point.z];
           if (!neighbor.inSegment && !neighbor.isCandidate) {
             candidatePoints.add(neighbor);
             neighbor.isCandidate = true;
           } 
           
           if (neighbor.biomeID != point.biomeID) {
             edge = true;
           }
         } else {
           edge = true;
         } 
         
         if (point.x > 0) {
           AbstractMapData.Point neighbor = AbstractMapData.this.points[point.x - 1][point.z];
           if (!neighbor.inSegment && !neighbor.isCandidate) {
             candidatePoints.add(neighbor);
             neighbor.isCandidate = true;
           } 
           
           if (neighbor.biomeID != point.biomeID) {
             edge = true;
           }
         } else {
           edge = true;
         } 
         
         if (point.z < AbstractMapData.this.height - 1) {
           AbstractMapData.Point neighbor = AbstractMapData.this.points[point.x][point.z + 1];
           if (!neighbor.inSegment && !neighbor.isCandidate) {
             candidatePoints.add(neighbor);
             neighbor.isCandidate = true;
           } 
           
           if (neighbor.biomeID != point.biomeID) {
             edge = true;
           }
         } else {
           edge = true;
         } 
         
         if (point.z > 0) {
           AbstractMapData.Point neighbor = AbstractMapData.this.points[point.x][point.z - 1];
           if (!neighbor.inSegment && !neighbor.isCandidate) {
             candidatePoints.add(neighbor);
             neighbor.isCandidate = true;
           } 
           
           if (neighbor.biomeID != point.biomeID) {
             edge = true;
           }
         } else {
           edge = true;
         } 
         
         if (edge) {
           point.layer = 0;
           this.currentShell.add(point);
         } 
       } 
     } 
   }
 
   
   public void calculateCenter(boolean horizontalBias) {
     calculateCenterOfMass();
     morphologicallyErode(horizontalBias);
   }
   
   public void calculateCenterOfMass() {
     calculateCenterOfMass(this.memberPoints);
   }
   
   public void calculateCenterOfMass(List<AbstractMapData.Point> points) {
     this.centerX = 0;
     this.centerZ = 0;
     
     for (AbstractMapData.Point point : points) {
       this.centerX += point.x;
       this.centerZ += point.z;
     } 
     
     this.centerX /= points.size();
     this.centerZ /= points.size();
   }
   
   public void calculateClosestPointToCenter(List<AbstractMapData.Point> points) {
     int distanceSquared = AbstractMapData.this.width * AbstractMapData.this.width + AbstractMapData.this.height * AbstractMapData.this.height;
     AbstractMapData.Point centerPoint = null;
     
     for (AbstractMapData.Point point : points) {
       int pointDistanceSquared = (point.x - this.centerX) * (point.x - this.centerX) + (point.z - this.centerZ) * (point.z - this.centerZ);
       if (pointDistanceSquared < distanceSquared) {
         distanceSquared = pointDistanceSquared;
         centerPoint = point;
       } 
     } 
     
     this.centerX = centerPoint.x;
     this.centerZ = centerPoint.z;
   }
   
   public void morphologicallyErode(boolean horizontalBias) {
     float labelWidth = ((class_310.method_1551()).field_1772.method_1727(this.name) + 8);
     float multi = (AbstractMapData.this.width / 32);
     float shellWidth = 2.0F;
     float labelPadding = labelWidth / 16.0F * multi / shellWidth;
     
     int layer;
     for (layer = 0; this.currentShell.size() > 0 && layer < labelPadding; this.currentShell = getNextShell(this.currentShell, layer, horizontalBias)) {
       layer++;
     }
     
     if (this.currentShell.size() > 0) {
       ArrayList<AbstractMapData.Point> remainingPoints = new ArrayList<>();
       
       for (AbstractMapData.Point point : this.memberPoints) {
         if (point.layer < 0 || point.layer == layer) {
           remainingPoints.add(point);
         }
       } 
       
       calculateClosestPointToCenter(remainingPoints);
     } 
   }
 
   
   public ArrayList<AbstractMapData.Point> getNextShell(List<AbstractMapData.Point> pointsToCheck, int layer, boolean horizontalBias) {
     int layerWidth = horizontalBias ? 2 : 1;
     int layerHeight = horizontalBias ? 1 : 2;
     ArrayList<AbstractMapData.Point> nextShell = new ArrayList<>();
     
     for (AbstractMapData.Point point : pointsToCheck) {
       if (point.x < AbstractMapData.this.width - layerWidth) {
         boolean foundEdge = false;
         
         for (int t = layerWidth; t > 0; t--) {
           AbstractMapData.Point neighbor = AbstractMapData.this.points[point.x + t][point.z];
           if (neighbor.biomeID == point.biomeID && neighbor.layer < 0) {
             neighbor.layer = layer;
             if (!foundEdge) {
               foundEdge = true;
               nextShell.add(neighbor);
             } 
           } 
         } 
       } 
       
       if (point.x >= layerWidth) {
         boolean foundEdge = false;
         
         for (int t = layerWidth; t > 0; t--) {
           AbstractMapData.Point neighbor = AbstractMapData.this.points[point.x - t][point.z];
           if (neighbor.biomeID == point.biomeID && neighbor.layer < 0) {
             neighbor.layer = layer;
             if (!foundEdge) {
               foundEdge = true;
               nextShell.add(neighbor);
             } 
           } 
         } 
       } 
       
       if (point.z < AbstractMapData.this.height - layerHeight) {
         boolean foundEdge = false;
         
         for (int t = layerHeight; t > 0; t--) {
           AbstractMapData.Point neighbor = AbstractMapData.this.points[point.x][point.z + t];
           if (neighbor.biomeID == point.biomeID && neighbor.layer < 0) {
             neighbor.layer = layer;
             if (!foundEdge) {
               foundEdge = true;
               nextShell.add(neighbor);
             } 
           } 
         } 
       } 
       
       if (point.z >= layerHeight) {
         boolean foundEdge = false;
         
         for (int t = layerHeight; t > 0; t--) {
           AbstractMapData.Point neighbor = AbstractMapData.this.points[point.x][point.z - t];
           if (neighbor.biomeID == point.biomeID && neighbor.layer < 0) {
             neighbor.layer = layer;
             if (!foundEdge) {
               foundEdge = true;
               nextShell.add(neighbor);
             } 
           } 
         } 
       } 
     } 
     
     if (nextShell.size() > 0) {
       return nextShell;
     }
     calculateCenterOfMass(pointsToCheck);
     calculateClosestPointToCenter(pointsToCheck);
     return nextShell;
   }
 }


