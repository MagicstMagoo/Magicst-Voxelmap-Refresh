package cn.magicst.mamiyaotaru.voxelmap.gui;

import cn.magicst.mamiyaotaru.voxelmap.util.I18nUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.class_1657;
import net.minecraft.class_1664;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_350;
import net.minecraft.class_364;
import net.minecraft.class_410;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import net.minecraft.class_5250;
import net.minecraft.class_634;
import net.minecraft.class_6382;
import net.minecraft.class_640;
import net.minecraft.class_757;
import org.jetbrains.annotations.Nullable;

public class GuiButtonRowListPlayers extends class_350 {
  private final class_310 client = class_310.method_1551();

  private final ArrayList<class_640> players;

  private ArrayList<?> playersFiltered;

  final GuiSelectPlayer parentGui;

  Row everyoneRow;

  final class_2561 ALL = (class_2561)class_2561.method_43471("minimap.waypointshare.all");

  final class_2561 TITLE = (class_2561)class_2561.method_43471("minimap.waypointshare.sharewitheveryone");

  final class_2561 EXPLANATION = (class_2561)class_2561.method_43471("minimap.waypointshare.sharewitheveryone2");

  final class_2561 AFFIRM = (class_2561)class_2561.method_43471("gui.yes");

  final class_2561 DENY = (class_2561)class_2561.method_43471("gui.cancel");

  public GuiButtonRowListPlayers(GuiSelectPlayer par1GuiSelectPlayer) {
    super(class_310.method_1551(), par1GuiSelectPlayer.getWidth(), par1GuiSelectPlayer.getHeight(), 89, par1GuiSelectPlayer.getHeight() - 65 + 4, 25);
    this.parentGui = par1GuiSelectPlayer;
    class_634 netHandlerPlayClient = (class_310.method_1551()).field_1724.field_3944;
    this.players = new ArrayList<>(netHandlerPlayClient.method_2880());
    sort();
    class_4185 everyoneButton = new class_4185(this.parentGui.getWidth() / 2 - 75, 0, 150, 20, this.ALL, null) {
      public void method_25306() {}
    };
    this.everyoneRow = new Row(everyoneButton, -1);
    updateFilter("");
  }

  private class_2561 getPlayerName(class_640 ScoreboardEntryIn) {
    return (ScoreboardEntryIn.method_2971() != null) ? ScoreboardEntryIn.method_2971() : (class_2561)class_2561.method_43470(ScoreboardEntryIn.method_2966().getName());
  }

  private class_4185 createButtonFor(int x, int y, class_640 ScoreboardEntry) {
    if (ScoreboardEntry == null)
      return null;
    class_2561 name = getPlayerName(ScoreboardEntry);
    return new class_4185(x, y, 150, 20, name, button -> {

    });
  }

  public int method_25322() {
    return 400;
  }

  protected int method_25329() {
    return super.method_25329() + 32;
  }

  protected void sort() {
    Collator collator = I18nUtils.getLocaleAwareCollator();
    this.players.sort((player1, player2) -> {
      String name1 = getPlayerName(player1).getString();
      String name2 = getPlayerName(player2).getString();
      return collator.compare(name1, name2);
    });
  }

  protected void updateFilter(String filterString) {
    this.playersFiltered = new ArrayList(this.players);
    Iterator<?> iterator = this.playersFiltered.iterator();
    while (iterator.hasNext()) {
      class_640 ScoreboardEntry = (class_640)iterator.next();
      String name = getPlayerName(ScoreboardEntry).getString();
      if (!name.toLowerCase().contains(filterString))
        iterator.remove();
    }
    method_25339();
    method_25321(this.everyoneRow);
    for (int i = 0; i < this.playersFiltered.size(); i += 2) {
      class_640 ScoreboardEntry1 = (class_640)this.playersFiltered.get(i);
      class_640 ScoreboardEntry2 = (i < this.playersFiltered.size() - 1) ? (class_640)this.playersFiltered.get(i + 1) : null;
      class_4185 guibutton1 = createButtonFor(this.parentGui.getWidth() / 2 - 155, 0, ScoreboardEntry1);
      class_4185 guibutton2 = createButtonFor(this.parentGui.getWidth() / 2 - 155 + 160, 0, ScoreboardEntry2);
      method_25321(new Row(guibutton1, i, guibutton2, i + 1));
    }
  }

  public void buttonClicked(int id) {
    if (id == -1) {
      this.parentGui.allClicked = true;
      class_410 confirmScreen = new class_410(this.parentGui, this.TITLE, this.EXPLANATION, this.AFFIRM, this.DENY);
      this.client.method_1507((class_437)confirmScreen);
    } else {
      class_640 ScoreboardEntry = (class_640)this.playersFiltered.get(id);
      String name = getPlayerName(ScoreboardEntry).getString();
      this.parentGui.sendMessageToPlayer(name);
    }
  }

  public void method_37020(class_6382 builder) {}

  public class Row extends class_350.class_351<Row> {
    private final class_310 client = class_310.method_1551();

    private class_4185 button = null;

    private class_4185 button1 = null;

    private class_4185 button2 = null;

    private int id = 0;

    private int id1 = 0;

    private int id2 = 0;

    public Row(class_4185 button, int id) {
      this.button = button;
      this.id = id;
    }

    public Row(class_4185 button1, int id1, class_4185 button2, int id2) {
      this.button1 = button1;
      this.id1 = id1;
      this.button2 = button2;
      this.id2 = id2;
    }

    public void method_25343(class_4587 matrixStack, int slotIndex, int y, int x, int listWidth, int itemHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
      drawButton(matrixStack, this.button, this.id, slotIndex, x, y, listWidth, itemHeight, mouseX, mouseY, isSelected, partialTicks);
      drawButton(matrixStack, this.button1, this.id1, slotIndex, x, y, listWidth, itemHeight, mouseX, mouseY, isSelected, partialTicks);
      drawButton(matrixStack, this.button2, this.id2, slotIndex, x, y, listWidth, itemHeight, mouseX, mouseY, isSelected, partialTicks);
    }

    private void drawButton(class_4587 matrixStack, class_4185 button, int id, int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
      if (button != null) {
        button.field_22761 = y;
        button.method_25394(matrixStack, mouseX, mouseY, partialTicks);
        if (id != -1)
          drawIconForButton(matrixStack, button, id);
        if (button.method_25367() && mouseY >= GuiButtonRowListPlayers.this.field_19085 && mouseY <= GuiButtonRowListPlayers.this.field_19086) {
          class_5250 class_5250 = class_2561.method_43469("minimap.waypointshare.sharewithname", new Object[] { button.method_25369() });
          GuiSelectPlayer.setTooltip(GuiButtonRowListPlayers.this.parentGui, (class_2561)class_5250);
        }
      }
    }

    private void drawIconForButton(class_4587 matrixStack, class_4185 button, int id) {
      class_640 networkPlayerInfo = (class_640)GuiButtonRowListPlayers.this.playersFiltered.get(id);
      GameProfile gameProfile = networkPlayerInfo.method_2966();
      class_1657 entityPlayer = this.client.field_1687.method_18470(gameProfile.getId());
      RenderSystem.setShader(class_757::method_34542);
      RenderSystem.setShaderTexture(0, networkPlayerInfo.method_2968());
      class_437.method_25293(matrixStack, button.field_22760 + 6, button.field_22761 + 6, 8, 8, 8.0F, 8.0F, 8, 8, 64, 64);
      if (entityPlayer != null && entityPlayer.method_7348(class_1664.field_7563))
        class_437.method_25293(matrixStack, button.field_22760 + 6, button.field_22761 + 6, 8, 8, 40.0F, 8.0F, 8, 8, 64, 64);
    }

    public boolean method_25402(double mouseX, double mouseY, int mouseEvent) {
      if (this.button != null && this.button.method_25402(mouseX, mouseY, mouseEvent)) {
        GuiButtonRowListPlayers.this.buttonClicked(this.id);
        return true;
      }
      if (this.button1 != null && this.button1.method_25402(mouseX, mouseY, mouseEvent)) {
        GuiButtonRowListPlayers.this.buttonClicked(this.id1);
        return true;
      }
      if (this.button2 != null && this.button2.method_25402(mouseX, mouseY, mouseEvent)) {
        GuiButtonRowListPlayers.this.buttonClicked(this.id2);
        return true;
      }
      return false;
    }

    public boolean method_25406(double mouseX, double mouseY, int mouseEvent) {
      if (this.button != null) {
        this.button.method_25406(mouseX, mouseY, mouseEvent);
        return true;
      }
      if (this.button1 != null) {
        this.button1.method_25406(mouseX, mouseY, mouseEvent);
        return true;
      }
      if (this.button2 != null) {
        this.button2.method_25406(mouseX, mouseY, mouseEvent);
        return true;
      }
      return false;
    }
  }
}
