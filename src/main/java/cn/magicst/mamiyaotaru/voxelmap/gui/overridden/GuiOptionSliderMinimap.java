package cn.magicst.mamiyaotaru.voxelmap.gui.overridden;

import cn.magicst.mamiyaotaru.voxelmap.interfaces.ISettingsManager;
import net.minecraft.class_2561;
import net.minecraft.class_357;

public class GuiOptionSliderMinimap extends class_357 {
  private final ISettingsManager options;

  private final EnumOptionsMinimap option;

  public GuiOptionSliderMinimap(int x, int y, EnumOptionsMinimap optionIn, float sliderValue, ISettingsManager options) {
    super(x, y, 150, 20, (class_2561)class_2561.method_43470(options.getKeyText(optionIn)), sliderValue);
    this.options = options;
    this.option = optionIn;
  }

  protected void method_25346() {
    method_25355((class_2561)class_2561.method_43470(this.options.getKeyText(this.option)));
  }

  protected void method_25344() {
    this.options.setOptionFloatValue(this.option, (float)this.field_22753);
  }

  public EnumOptionsMinimap returnEnumOptions() {
    return this.option;
  }

  public void setValue(float value) {
    if (!method_25367()) {
      this.field_22753 = value;
      method_25346();
    }
  }
}
