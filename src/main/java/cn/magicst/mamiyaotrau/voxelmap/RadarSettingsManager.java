package cn.magicst.mamiyaotaru.voxelmap;

import cn.magicst.mamiyaotaru.voxelmap.gui.overridden.EnumOptionsMinimap;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.ISubSettingsManager;
import cn.magicst.mamiyaotaru.voxelmap.util.CustomMob;
import cn.magicst.mamiyaotaru.voxelmap.util.CustomMobsManager;
import cn.magicst.mamiyaotaru.voxelmap.util.EnumMobs;
import cn.magicst.mamiyaotaru.voxelmap.util.I18nUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
//import net.minecraft.class_310;

public class RadarSettingsManager
  implements ISubSettingsManager {
  public final int SIMPLE = 1;
  public final int FULL = 2;
  //public class_310 game;
  private boolean somethingChanged;
  public int radarMode = 2;
  public boolean showRadar = true;
  public boolean showHostiles = true;
  public boolean showPlayers = true;
  public boolean showNeutrals = false;
  public boolean showPlayerNames = true;
  public boolean showMobNames = true;
  public boolean outlines = true;
  public boolean filtering = true;
  public boolean showHelmetsPlayers = true;
  public boolean showHelmetsMobs = true;
  public boolean showFacing = true;
  public Boolean radarAllowed = Boolean.valueOf(true);
  public Boolean radarPlayersAllowed = Boolean.valueOf(true);
  public Boolean radarMobsAllowed = Boolean.valueOf(true);
  float fontScale = 1.0F;
  
  public RadarSettingsManager() {
    this.game = class_310.method_1551();
  }

  
  public void loadSettings(File settingsFile) {
    try {
      BufferedReader in = new BufferedReader(new FileReader(settingsFile));
      
      String sCurrentLine;
      while ((sCurrentLine = in.readLine()) != null) {
        String[] curLine = sCurrentLine.split(":");
        if (curLine[0].equals("Radar Mode")) {
          this.radarMode = Math.max(1, Math.min(2, Integer.parseInt(curLine[1]))); continue;
        }  if (curLine[0].equals("Show Radar")) {
          this.showRadar = Boolean.parseBoolean(curLine[1]); continue;
        }  if (curLine[0].equals("Show Hostiles")) {
          this.showHostiles = Boolean.parseBoolean(curLine[1]); continue;
        }  if (curLine[0].equals("Show Players")) {
          this.showPlayers = Boolean.parseBoolean(curLine[1]); continue;
        }  if (curLine[0].equals("Show Neutrals")) {
          this.showNeutrals = Boolean.parseBoolean(curLine[1]); continue;
        }  if (curLine[0].equals("Filter Mob Icons")) {
          this.filtering = Boolean.parseBoolean(curLine[1]); continue;
        }  if (curLine[0].equals("Outline Mob Icons")) {
          this.outlines = Boolean.parseBoolean(curLine[1]); continue;
        }  if (curLine[0].equals("Show Player Helmets")) {
          this.showHelmetsPlayers = Boolean.parseBoolean(curLine[1]); continue;
        }  if (curLine[0].equals("Show Mob Helmets")) {
          this.showHelmetsMobs = Boolean.parseBoolean(curLine[1]); continue;
        }  if (curLine[0].equals("Show Player Names")) {
          this.showPlayerNames = Boolean.parseBoolean(curLine[1]); continue;
        }  if (curLine[0].equals("Font Scale")) {
          this.fontScale = Float.parseFloat(curLine[1]); continue;
        }  if (curLine[0].equals("Show Facing")) {
          this.showFacing = Boolean.parseBoolean(curLine[1]); continue;
        }  if (curLine[0].equals("Hidden Mobs")) {
          applyHiddenMobSettings(curLine[1]);
        }
      } 
      
      in.close();
    } catch (Exception exception) {}
  }


  
  private void applyHiddenMobSettings(String hiddenMobs) {
    String[] mobsToHide = hiddenMobs.split(",");
    
    for (int t = 0; t < mobsToHide.length; t++) {
      boolean builtIn = false;
      
      for (EnumMobs mob : EnumMobs.values()) {
        if (mob.id.equals(mobsToHide[t])) {
          mob.enabled = false;
          builtIn = true;
        } 
      } 
      
      if (!builtIn) {
        CustomMobsManager.add(mobsToHide[t], false);
      }
    } 
  }


  
  public void saveAll(PrintWriter out) {
    out.println("Radar Mode:" + this.radarMode);
    out.println("Show Radar:" + Boolean.toString(this.showRadar));
    out.println("Show Hostiles:" + Boolean.toString(this.showHostiles));
    out.println("Show Players:" + Boolean.toString(this.showPlayers));
    out.println("Show Neutrals:" + Boolean.toString(this.showNeutrals));
    out.println("Filter Mob Icons:" + Boolean.toString(this.filtering));
    out.println("Outline Mob Icons:" + Boolean.toString(this.outlines));
    out.println("Show Player Helmets:" + Boolean.toString(this.showHelmetsPlayers));
    out.println("Show Mob Helmets:" + Boolean.toString(this.showHelmetsMobs));
    out.println("Show Player Names:" + Boolean.toString(this.showPlayerNames));
    out.println("Font Scale:" + Float.toString(this.fontScale));
    out.println("Show Facing:" + Boolean.toString(this.showFacing));
    out.print("Hidden Mobs:");
    
    for (EnumMobs mob : EnumMobs.values()) {
      if (mob.isTopLevelUnit && !mob.enabled) {
        out.print(mob.id + ",");
      }
    } 
    
    for (CustomMob mob : CustomMobsManager.mobs) {
      if (!mob.enabled) {
        out.print(mob.id + ",");
      }
    } 
    
    out.println();
  }

  
  public String getKeyText(EnumOptionsMinimap par1EnumOptions) {
    String s = I18nUtils.getString(par1EnumOptions.getName(), new Object[0]) + ": ";
    if (par1EnumOptions.isBoolean())
      return getOptionBooleanValue(par1EnumOptions) ? (s + s) : (s + s); 
    if (par1EnumOptions.isList()) {
      String state = getOptionListValue(par1EnumOptions);
      return s + s;
    } 
    return s;
  }

  
  public boolean getOptionBooleanValue(EnumOptionsMinimap par1EnumOptions) {
    switch (par1EnumOptions) {
      case SHOWRADAR:
        return this.showRadar;
      case SHOWHOSTILES:
        return this.showHostiles;
      case SHOWPLAYERS:
        return this.showPlayers;
      case SHOWNEUTRALS:
        return this.showNeutrals;
      case SHOWPLAYERHELMETS:
        return this.showHelmetsPlayers;
      case SHOWMOBHELMETS:
        return this.showHelmetsMobs;
      case SHOWPLAYERNAMES:
        return this.showPlayerNames;
      case RADAROUTLINES:
        return this.outlines;
      case RADARFILTERING:
        return this.filtering;
      case SHOWFACING:
        return this.showFacing;
    } 
    throw new IllegalArgumentException("Add code to handle EnumOptionMinimap: " + par1EnumOptions.getName() + ". (possibly not a boolean)");
  }

  
  public String getOptionListValue(EnumOptionsMinimap par1EnumOptions) {
    switch (par1EnumOptions) {
      case RADARMODE:
        if (this.radarMode == 2) {
          return I18nUtils.getString("options.minimap.radar.radarmode.full", new Object[0]);
        }
        
        return I18nUtils.getString("options.minimap.radar.radarmode.simple", new Object[0]);
    } 
    throw new IllegalArgumentException("Add code to handle EnumOptionMinimap: " + par1EnumOptions.getName() + ". (possibly not a list value applicable to minimap)");
  }


  
  public void setOptionFloatValue(EnumOptionsMinimap idFloat, float sliderValue) {}

  
  public void setOptionValue(EnumOptionsMinimap par1EnumOptions) {
    switch (par1EnumOptions) {
      case SHOWRADAR:
        this.showRadar = !this.showRadar;
        break;
      case SHOWHOSTILES:
        this.showHostiles = !this.showHostiles;
        break;
      case SHOWPLAYERS:
        this.showPlayers = !this.showPlayers;
        break;
      case SHOWNEUTRALS:
        this.showNeutrals = !this.showNeutrals;
        break;
      case SHOWPLAYERHELMETS:
        this.showHelmetsPlayers = !this.showHelmetsPlayers;
        break;
      case SHOWMOBHELMETS:
        this.showHelmetsMobs = !this.showHelmetsMobs;
        break;
      case SHOWPLAYERNAMES:
        this.showPlayerNames = !this.showPlayerNames;
        break;
      case RADAROUTLINES:
        this.outlines = !this.outlines;
        break;
      case RADARFILTERING:
        this.filtering = !this.filtering;
        break;
      case SHOWFACING:
        this.showFacing = !this.showFacing;
        break;
      case RADARMODE:
        if (this.radarMode == 2) {
          this.radarMode = 1; break;
        } 
        this.radarMode = 2;
        break;
      
      default:
        throw new IllegalArgumentException("Add code to handle EnumOptionMinimap: " + par1EnumOptions.getName());
    } 
    
    this.somethingChanged = true;
  }
  
  public boolean isChanged() {
    if (this.somethingChanged) {
      this.somethingChanged = false;
      return true;
    } 
    return false;
  }


  
  public float getOptionFloatValue(EnumOptionsMinimap option) {
    return 0.0F;
  }
}


