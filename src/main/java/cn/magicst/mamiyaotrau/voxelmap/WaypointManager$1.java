package cn.magicst.mamiyaotaru.voxelmap;

import cn.magicst.mamiyaotaru.voxelmap.textures.IIconCreator;
import cn.magicst.mamiyaotaru.voxelmap.textures.Sprite;
import cn.magicst.mamiyaotaru.voxelmap.textures.TextureAtlas;
import java.util.List;
import net.minecraft.class_2960;
import net.minecraft.class_310;

class null
  implements IIconCreator
{
  public void addIcons(TextureAtlas textureAtlas) {
    class_310 mc = class_310.method_1551();
    
    for (class_2960 candidate : mc.method_1478().method_14488("images", asset -> asset.endsWith(".png"))) {
      if (candidate.method_12836().equals("voxelmap") && candidate.method_12832().contains("images/waypoints")) {
        images.add(candidate);
      }
    } 
    
    Sprite markerIcon = textureAtlas.registerIconForResource(new class_2960("voxelmap", "images/waypoints/marker.png"), class_310.method_1551().method_1478());
    Sprite markerIconSmall = textureAtlas.registerIconForResource(new class_2960("voxelmap", "images/waypoints/markersmall.png"), class_310.method_1551().method_1478());
    
    for (class_2960 resourceLocation : images) {
      Sprite icon = textureAtlas.registerIconForResource(resourceLocation, class_310.method_1551().method_1478());
      String name = resourceLocation.toString();
      if (name.toLowerCase().contains("waypoints/waypoint") && !name.toLowerCase().contains("small")) {
        textureAtlas.registerMaskedIcon(name.replace(".png", "Small.png"), icon);
        textureAtlas.registerMaskedIcon(name.replace("waypoints/waypoint", "waypoints/marker"), markerIcon);
        textureAtlas.registerMaskedIcon(name.replace("waypoints/waypoint", "waypoints/marker").replace(".png", "Small.png"), markerIconSmall); continue;
      }  if (name.toLowerCase().contains("waypoints/marker") && !name.toLowerCase().contains("small"))
        textureAtlas.registerMaskedIcon(name.replace(".png", "Small.png"), icon); 
    } 
  }
}
