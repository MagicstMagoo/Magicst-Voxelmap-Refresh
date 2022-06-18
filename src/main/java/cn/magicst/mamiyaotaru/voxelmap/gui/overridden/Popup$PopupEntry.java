package cn.magicst.mamiyaotaru.voxelmap.gui.overridden;

public class PopupEntry {
    public String name;

    public int action;

    boolean causesClose;

    boolean enabled;

    public PopupEntry(String name, int action, boolean causesClose, boolean enabled) {
        this.name = name;
        this.action = action;
        this.causesClose = causesClose;
        this.enabled = enabled;
    }
}
