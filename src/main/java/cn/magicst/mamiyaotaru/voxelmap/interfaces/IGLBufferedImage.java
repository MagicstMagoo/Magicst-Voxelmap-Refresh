package cn.magicst.mamiyaotaru.voxelmap.interfaces;

public interface IGLBufferedImage {
  int getIndex();
  
  int getWidth();
  
  int getHeight();
  
  void baleet();
  
  void write();
  
  void blank();
  
  void setRGB(int paramInt1, int paramInt2, int paramInt3);
  
  void moveX(int paramInt);
  
  void moveY(int paramInt);
}


