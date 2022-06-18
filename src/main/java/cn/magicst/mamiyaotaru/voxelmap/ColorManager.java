package cn.magicst.mamiyaotaru.voxelmap;

import com.google.common.collect.UnmodifiableIterator;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.AbstractMapData;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.IColorManager;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.IVoxelMap;
import cn.magicst.mamiyaotaru.voxelmap.util.BiomeRepository;
import cn.magicst.mamiyaotaru.voxelmap.util.BlockModel;
import cn.magicst.mamiyaotaru.voxelmap.util.BlockRepository;
import cn.magicst.mamiyaotaru.voxelmap.util.ColorUtils;
import cn.magicst.mamiyaotaru.voxelmap.util.GLShim;
import cn.magicst.mamiyaotaru.voxelmap.util.GLUtils;
import cn.magicst.mamiyaotaru.voxelmap.util.ImageUtils;
import cn.magicst.mamiyaotaru.voxelmap.util.MessageUtils;
import cn.magicst.mamiyaotaru.voxelmap.util.MutableBlockPos;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import net.minecraft.class_1047;
import net.minecraft.class_1058;
import net.minecraft.class_1059;
import net.minecraft.class_1060;
import net.minecraft.class_1087;
import net.minecraft.class_1159;
import net.minecraft.class_1160;
import net.minecraft.class_1162;
import net.minecraft.class_1309;
import net.minecraft.class_151;
import net.minecraft.class_1799;
import net.minecraft.class_1920;
import net.minecraft.class_1926;
import net.minecraft.class_1933;
import net.minecraft.class_1937;
import net.minecraft.class_1959;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2378;
import net.minecraft.class_2457;
import net.minecraft.class_2464;
import net.minecraft.class_2680;
import net.minecraft.class_2769;
import net.minecraft.class_2791;
import net.minecraft.class_2818;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_315;
import net.minecraft.class_3298;
import net.minecraft.class_3300;
import net.minecraft.class_3532;
import net.minecraft.class_3614;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_5458;
import net.minecraft.class_5819;
import net.minecraft.class_638;
import net.minecraft.class_773;
import net.minecraft.class_776;
import net.minecraft.class_804;
import net.minecraft.class_809;

public class ColorManager implements IColorManager {
  private IVoxelMap master;

  class_310 game = null;

  private boolean resourcePacksChanged = false;

  private class_638 world = null;

  private BufferedImage terrainBuff = null;

  private BufferedImage colorPicker;

  private int sizeOfBiomeArray = 0;

  private final int BIOME_ARRAY_HEIGHT = 32;

  private final int BIOME_ARRAY_HEIGHT_MULTIPLIER = 8;

  private int[] blockColors = new int[16384];

  private int[] blockColorsWithDefaultTint = new int[16384];

  private final int COLOR_NOT_LOADED = -16842497;

  private final int COLOR_FAILED_LOAD = 452984832;

  private HashSet biomeTintsAvailable = new HashSet();

  private boolean optifineInstalled = false;

  private HashMap blockTintTables = new HashMap<>();

  private HashSet biomeTextureAvailable = new HashSet();

  private HashMap blockBiomeSpecificColors = new HashMap<>();

  private float failedToLoadX = 0.0F;

  private float failedToLoadY = 0.0F;

  private String renderPassThreeBlendMode;

  private class_5819 random = class_5819.method_43047();

  private final Object tpLoadLock = new Object();

  private boolean loaded = false;

  private final MutableBlockPos dummyBlockPos = new MutableBlockPos(class_2338.field_10980.method_10263(), class_2338.field_10980.method_10264(), class_2338.field_10980.method_10260());

  private final class_1160 fullbright = new class_1160(1.0F, 1.0F, 1.0F);

  private final ColorResolver spruceColorResolver = (blockState, biomex, blockPos) -> class_1926.method_8342();

  private final ColorResolver birchColorResolver = (blockState, biomex, blockPos) -> class_1926.method_8343();

  private final ColorResolver grassColorResolver;

  private final ColorResolver foliageColorResolver;

  private final ColorResolver waterColorResolver;

  private final ColorResolver redstoneColorResolver;

  public ColorManager(IVoxelMap master) {
    this.grassColorResolver = ((blockState, biomex, blockPos) -> biomex.method_8711(blockPos.method_10263(), blockPos.method_10260()));
    this.foliageColorResolver = ((blockState, biomex, blockPos) -> biomex.method_8698());
    this.waterColorResolver = ((blockState, biomex, blockPos) -> biomex.method_8687());
    this.redstoneColorResolver = ((blockState, biomex, blockPos) -> class_2457.method_10487(((Integer)blockState.method_11654((class_2769)class_2457.field_11432)).intValue()));
    this.master = master;
    this.game = class_310.method_1551();
    this.optifineInstalled = false;
    Field ofProfiler = null;
    try {
      ofProfiler = class_315.class.getDeclaredField("ofProfiler");
    } catch (SecurityException securityException) {

    } catch (NoSuchFieldException noSuchFieldException) {

    } finally {
      if (ofProfiler != null)
        this.optifineInstalled = true;
    }
    for (class_1959 biome : class_5458.field_25933) {
      int biomeID = class_5458.field_25933.method_10206(biome);
      if (biomeID > this.sizeOfBiomeArray)
        this.sizeOfBiomeArray = biomeID;
    }
    this.sizeOfBiomeArray++;
  }

  public int getAirColor() {
    return this.blockColors[BlockRepository.airID];
  }

  public BufferedImage getColorPicker() {
    return this.colorPicker;
  }

  public void onResourceManagerReload(class_3300 resourceManager) {
    this.resourcePacksChanged = true;
  }

  public boolean checkForChanges() {
    boolean biomesChanged = false;
    if (this.game.field_1687 != null && this.game.field_1687 != this.world) {
      this.world = this.game.field_1687;
      int largestBiomeID = 0;
      for (class_1959 biome : this.world.method_30349().method_30530(class_2378.field_25114)) {
        int biomeID = this.world.method_30349().method_30530(class_2378.field_25114).method_10206(biome);
        if (biomeID > largestBiomeID)
          largestBiomeID = biomeID;
      }
      if (this.sizeOfBiomeArray != largestBiomeID + 1) {
        this.sizeOfBiomeArray = largestBiomeID + 1;
        biomesChanged = true;
      }
    }
    boolean changed = (this.resourcePacksChanged || biomesChanged);
    this.resourcePacksChanged = false;
    if (changed)
      loadColors();
    return changed;
  }

  private void loadColors() {
    this.game.field_1724.method_3117();
    BlockRepository.getBlocks();
    BiomeRepository.getBiomes();
    loadColorPicker();
    loadTexturePackTerrainImage();
    class_1058 missing = this.game.method_1549(class_1059.field_5275).apply(new class_2960("missingno"));
    this.failedToLoadX = missing.method_4594();
    this.failedToLoadY = missing.method_4593();
    this.loaded = false;
    try {
      Arrays.fill(this.blockColors, -16842497);
      Arrays.fill(this.blockColorsWithDefaultTint, -16842497);
      loadSpecialColors();
      this.biomeTintsAvailable.clear();
      this.biomeTextureAvailable.clear();
      this.blockBiomeSpecificColors.clear();
      this.blockTintTables.clear();
      if (this.optifineInstalled) {
        try {
          processCTM();
        } catch (Exception var4) {
          System.err.println("error loading CTM " + var4.getLocalizedMessage());
          var4.printStackTrace();
        }
        try {
          processColorProperties();
        } catch (Exception var3) {
          System.err.println("error loading custom color properties " + var3.getLocalizedMessage());
          var3.printStackTrace();
        }
      }
      this.master.getMap().forceFullRender(true);
    } catch (Exception var5) {
      System.err.println("error loading pack");
      var5.printStackTrace();
    }
    this.loaded = true;
  }

  public final BufferedImage getBlockImage(class_2680 blockState, class_1799 stack, class_1937 world, float iconScale, float captureDepth) {
    try {
      class_1087 model = this.game.method_1480().method_4019(stack, world, (class_1309)null, 0);
      drawModel(class_2350.field_11034, blockState, model, stack, iconScale, captureDepth);
      BufferedImage blockImage = ImageUtils.createBufferedImageFromGLID(GLUtils.fboTextureID);
      ImageIO.write(blockImage, "png", new File((class_310.method_1551()).field_1697, blockState.method_26204().method_9518().getString() + "-" + blockState.method_26204().method_9518().getString() + ".png"));
      return blockImage;
    } catch (Exception var8) {
      System.out.println("error getting block armor image for " + blockState.toString() + ": " + var8.getLocalizedMessage());
      var8.printStackTrace();
      return null;
    }
  }

  private void drawModel(class_2350 facing, class_2680 blockState, class_1087 model, class_1799 stack, float scale, float captureDepth) {
    float size = 8.0F * scale;
    class_809 transforms = model.method_4709();
    class_804 headTransforms = transforms.field_4311;
    class_1160 translations = headTransforms.field_4286;
    float transX = -translations.method_4943() * size + 0.5F * size;
    float transY = translations.method_4945() * size + 0.5F * size;
    float transZ = -translations.method_4947() * size + 0.5F * size;
    class_1160 rotations = headTransforms.field_4287;
    float rotX = rotations.method_4943();
    float rotY = rotations.method_4945();
    float rotZ = rotations.method_4947();
    GLShim.glBindTexture(3553, GLUtils.fboTextureID);
    int width = GLShim.glGetTexLevelParameteri(3553, 0, 4096);
    int height = GLShim.glGetTexLevelParameteri(3553, 0, 4097);
    GLShim.glBindTexture(3553, 0);
    GLShim.glViewport(0, 0, width, height);
    class_1159 minimapProjectionMatrix = RenderSystem.getProjectionMatrix();
    class_1159 matrix4f = class_1159.method_34239(0.0F, width, 0.0F, height, 1000.0F, 3000.0F);
    RenderSystem.setProjectionMatrix(matrix4f);
    class_4587 matrixStack = RenderSystem.getModelViewStack();
    matrixStack.method_22903();
    matrixStack.method_34426();
    matrixStack.method_22904(0.0D, 0.0D, -3000.0D + (captureDepth * scale));
    RenderSystem.applyModelViewMatrix();
    GLUtils.bindFrameBuffer();
    GLShim.glDepthMask(true);
    GLShim.glEnable(2929);
    GLShim.glEnable(3553);
    GLShim.glEnable(3042);
    GLShim.glDisable(2884);
    GLShim.glBlendFunc(770, 771);
    GLShim.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GLShim.glClearColor(1.0F, 1.0F, 1.0F, 0.0F);
    GLShim.glClearDepth(1.0D);
    GLShim.glClear(16640);
    GLShim.glBlendFunc(770, 771);
    matrixStack.method_22903();
    matrixStack.method_22904(((width / 2) - size / 2.0F + transX), ((height / 2) - size / 2.0F + transY), (0.0F + transZ));
    matrixStack.method_22905(size, size, size);
    class_310.method_1551().method_1531().method_4619(class_1059.field_5275).method_4527(false, false);
    GLUtils.img2(class_1059.field_5275);
    matrixStack.method_22907(class_1160.field_20705.method_23214(180.0F));
    matrixStack.method_22907(class_1160.field_20705.method_23214(rotY));
    matrixStack.method_22907(class_1160.field_20703.method_23214(rotX));
    matrixStack.method_22907(class_1160.field_20707.method_23214(rotZ));
    if (facing == class_2350.field_11036)
      matrixStack.method_22907(class_1160.field_20703.method_23214(90.0F));
    RenderSystem.applyModelViewMatrix();
    class_1162 fullbright2 = new class_1162(this.fullbright);
    fullbright2.method_22674(matrixStack.method_23760().method_23761());
    class_1160 fullbright3 = new class_1160(fullbright2);
    RenderSystem.setShaderLights(fullbright3, fullbright3);
    class_4587 newMatrixStack = new class_4587();
    class_4597.class_4598 immediate = this.game.method_22940().method_23000();
    this.game.method_1480().method_23179(stack, class_809.class_811.field_4315, false, newMatrixStack, (class_4597)immediate, 15728880, class_4608.field_21444, model);
    immediate.method_22993();
    matrixStack.method_22909();
    matrixStack.method_22909();
    RenderSystem.applyModelViewMatrix();
    GLShim.glEnable(2884);
    GLShim.glDisable(2929);
    GLShim.glDepthMask(false);
    GLUtils.unbindFrameBuffer();
    RenderSystem.setProjectionMatrix(minimapProjectionMatrix);
    GLShim.glViewport(0, 0, this.game.method_22683().method_4489(), this.game.method_22683().method_4506());
  }

  private void loadColorPicker() {
    try {
      InputStream is = ((class_3298)this.game.method_1478().method_14486(new class_2960("voxelmap", "images/colorpicker.png")).get()).method_14482();
      Image picker = ImageIO.read(is);
      is.close();
      this.colorPicker = new BufferedImage(picker.getWidth((ImageObserver)null), picker.getHeight((ImageObserver)null), 2);
      Graphics gfx = this.colorPicker.createGraphics();
      gfx.drawImage(picker, 0, 0, (ImageObserver)null);
      gfx.dispose();
    } catch (Exception var4) {
      System.err.println("Error loading color picker: " + var4.getLocalizedMessage());
    }
  }

  public void setSkyColor(int skyColor) {
    this.blockColors[BlockRepository.airID] = skyColor;
    this.blockColors[BlockRepository.voidAirID] = skyColor;
    this.blockColors[BlockRepository.caveAirID] = skyColor;
  }

  private void loadTexturePackTerrainImage() {
    try {
      class_1060 textureManager = this.game.method_1531();
      textureManager.method_22813(class_1059.field_5275);
      BufferedImage terrainStitched = ImageUtils.createBufferedImageFromCurrentGLImage();
      this.terrainBuff = new BufferedImage(terrainStitched.getWidth((ImageObserver)null), terrainStitched.getHeight((ImageObserver)null), 6);
      Graphics gfx = this.terrainBuff.createGraphics();
      gfx.drawImage(terrainStitched, 0, 0, null);
      gfx.dispose();
    } catch (Exception var4) {
      System.err.println("Error processing new resource pack: " + var4.getLocalizedMessage());
      var4.printStackTrace();
    }
  }

  private void loadSpecialColors() {
    for (UnmodifiableIterator<class_2680> unmodifiableIterator2 = BlockRepository.pistonTechBlock.method_9595().method_11662().iterator(); unmodifiableIterator2.hasNext(); this.blockColors[blockStateID] = 0) {
      class_2680 blockState = unmodifiableIterator2.next();
      int blockStateID = BlockRepository.getStateId(blockState);
    }
    for (UnmodifiableIterator<class_2680> unmodifiableIterator1 = BlockRepository.barrier.method_9595().method_11662().iterator(); unmodifiableIterator1.hasNext(); this.blockColors[blockStateID] = 0) {
      class_2680 blockState = unmodifiableIterator1.next();
      int blockStateID = BlockRepository.getStateId(blockState);
    }
  }

  public final int getBlockColorWithDefaultTint(MutableBlockPos blockPos, int blockStateID) {
    if (this.loaded) {
      int col = 452984832;
      try {
        col = this.blockColorsWithDefaultTint[blockStateID];
      } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {}
      return (col != -16842497) ? col : getBlockColor(blockPos, blockStateID);
    }
    return 0;
  }

  public final int getBlockColor(MutableBlockPos blockPos, int blockStateID, int biomeID) {
    if (this.loaded) {
      if (this.optifineInstalled && this.biomeTextureAvailable.contains(Integer.valueOf(blockStateID))) {
        Integer col = (Integer)this.blockBiomeSpecificColors.get("" + blockStateID + " " + blockStateID);
        if (col != null)
          return col.intValue();
      }
      return getBlockColor(blockPos, blockStateID);
    }
    return 0;
  }

  private int getBlockColor(int blockStateID) {
    return getBlockColor(this.dummyBlockPos, blockStateID);
  }

  private final int getBlockColor(MutableBlockPos blockPos, int blockStateID) {
    int col = 452984832;
    try {
      col = this.blockColors[blockStateID];
    } catch (ArrayIndexOutOfBoundsException var5) {
      resizeColorArrays(blockStateID);
    }
    if (col == -16842497 || col == 452984832) {
      class_2680 blockState = BlockRepository.getStateById(blockStateID);
      col = this.blockColors[blockStateID] = getColor(blockPos, blockState);
    }
    return col;
  }

  private synchronized void resizeColorArrays(int queriedID) {
    if (queriedID >= this.blockColors.length) {
      int[] newBlockColors = new int[this.blockColors.length * 2];
      int[] newBlockColorsWithDefaultTint = new int[this.blockColors.length * 2];
      System.arraycopy(this.blockColors, 0, newBlockColors, 0, this.blockColors.length);
      System.arraycopy(this.blockColorsWithDefaultTint, 0, newBlockColorsWithDefaultTint, 0, this.blockColorsWithDefaultTint.length);
      Arrays.fill(newBlockColors, this.blockColors.length, newBlockColors.length, -16842497);
      Arrays.fill(newBlockColorsWithDefaultTint, this.blockColorsWithDefaultTint.length, newBlockColorsWithDefaultTint.length, -16842497);
      this.blockColors = newBlockColors;
      this.blockColorsWithDefaultTint = newBlockColorsWithDefaultTint;
    }
  }

  private int getColor(MutableBlockPos blockPos, class_2680 blockState) {
    try {
      int color = getColorForBlockPosBlockStateAndFacing((class_2338)blockPos, blockState, class_2350.field_11036);
      if (color == 452984832) {
        class_776 blockRendererDispatcher = this.game.method_1541();
        color = getColorForTerrainSprite(blockState, blockRendererDispatcher);
      }
      class_2248 block = blockState.method_26204();
      if (block == BlockRepository.cobweb)
        color |= 0xFF000000;
      if (block == BlockRepository.redstone)
        color = ColorUtils.colorMultiplier(color, this.game.method_1505().method_1697(blockState, (class_1920)null, (class_2338)null, 0) | 0xFF000000);
      if (BlockRepository.biomeBlocks.contains(block)) {
        applyDefaultBuiltInShading(blockState, color);
      } else {
        checkForBiomeTinting(blockPos, blockState, color);
      }
      if (BlockRepository.shapedBlocks.contains(block))
        color = applyShape(block, color);
      if ((color >> 24 & 0xFF) < 27)
        color |= 0x1B000000;
      return color;
    } catch (Exception var5) {
      System.err.println("failed getting color: " + blockState.method_26204().method_9518().getString());
      var5.printStackTrace();
      return 452984832;
    }
  }

  private int getColorForBlockPosBlockStateAndFacing(class_2338 blockPos, class_2680 blockState, class_2350 facing) {
    int color = 452984832;
    try {
      class_2464 blockRenderType = blockState.method_26217();
      class_776 blockRendererDispatcher = this.game.method_1541();
      if (blockRenderType == class_2464.field_11458) {
        class_1087 iBakedModel = blockRendererDispatcher.method_3349(blockState);
        List quads = new ArrayList();
        quads.addAll(iBakedModel.method_4707(blockState, facing, this.random));
        quads.addAll(iBakedModel.method_4707(blockState, (class_2350)null, this.random));
        BlockModel model = new BlockModel(quads, this.failedToLoadX, this.failedToLoadY);
        if (model.numberOfFaces() > 0) {
          BufferedImage modelImage = model.getImage(this.terrainBuff);
          if (modelImage != null) {
            color = getColorForCoordinatesAndImage(new float[] { 0.0F, 1.0F, 0.0F, 1.0F }, modelImage);
          } else {
            System.out.println("image was null");
          }
        }
      }
    } catch (Exception var11) {
      System.out.println(var11.getMessage());
      var11.printStackTrace();
      color = 452984832;
    }
    return color;
  }

  private int getColorForTerrainSprite(class_2680 blockState, class_776 blockRendererDispatcher) {
    int color = 452984832;
    class_773 blockModelShapes = blockRendererDispatcher.method_3351();
    class_1058 icon = blockModelShapes.method_3339(blockState);
    if (icon == blockModelShapes.method_3333().method_4744().method_4711()) {
      class_2248 block = blockState.method_26204();
      class_3614 material = blockState.method_26207();
      if (block instanceof net.minecraft.class_2404) {
        if (material == class_3614.field_15920) {
          icon = this.game.method_1549(class_1059.field_5275).apply(new class_2960("minecraft:blocks/water_flow"));
        } else if (material == class_3614.field_15922) {
          icon = this.game.method_1549(class_1059.field_5275).apply(new class_2960("minecraft:blocks/lava_flow"));
        }
      } else if (material == class_3614.field_15920) {
        icon = this.game.method_1549(class_1059.field_5275).apply(new class_2960("minecraft:blocks/water_still"));
      } else if (material == class_3614.field_15922) {
        icon = this.game.method_1549(class_1059.field_5275).apply(new class_2960("minecraft:blocks/lava_still"));
      }
    }
    return getColorForIcon(icon);
  }

  private int getColorForIcon(class_1058 icon) {
    int color = 452984832;
    if (icon != null) {
      float left = icon.method_4594();
      float right = icon.method_4577();
      float top = icon.method_4593();
      float bottom = icon.method_4575();
      color = getColorForCoordinatesAndImage(new float[] { left, right, top, bottom }, this.terrainBuff);
    }
    return color;
  }

  private int getColorForCoordinatesAndImage(float[] uv, BufferedImage imageBuff) {
    int color = 452984832;
    if (uv[0] != this.failedToLoadX || uv[2] != this.failedToLoadY) {
      int left = (int)(uv[0] * imageBuff.getWidth());
      int right = (int)Math.ceil((uv[1] * imageBuff.getWidth()));
      int top = (int)(uv[2] * imageBuff.getHeight());
      int bottom = (int)Math.ceil((uv[3] * imageBuff.getHeight()));
      try {
        BufferedImage blockTexture = imageBuff.getSubimage(left, top, right - left, bottom - top);
        Image singlePixel = blockTexture.getScaledInstance(1, 1, 4);
        BufferedImage singlePixelBuff = new BufferedImage(1, 1, imageBuff.getType());
        Graphics gfx = singlePixelBuff.createGraphics();
        gfx.drawImage(singlePixel, 0, 0, (ImageObserver)null);
        gfx.dispose();
        color = singlePixelBuff.getRGB(0, 0);
      } catch (RasterFormatException var12) {
        System.out.println("error getting color");
        System.out.println("" + left + " " + left + " " + right + " " + top);
        color = 452984832;
      }
    }
    return color;
  }

  private void applyDefaultBuiltInShading(class_2680 blockState, int color) {
    class_2248 block = blockState.method_26204();
    int blockStateID = BlockRepository.getStateId(blockState);
    if (block != BlockRepository.largeFern && block != BlockRepository.tallGrass && block != BlockRepository.reeds) {
      if (block == BlockRepository.water) {
        this.blockColorsWithDefaultTint[blockStateID] = ColorUtils.colorMultiplier(color, BiomeRepository.FOREST.method_8687() | 0xFF000000);
      } else {
        this.blockColorsWithDefaultTint[blockStateID] = ColorUtils.colorMultiplier(color, this.game.method_1505().method_1697(blockState, (class_1920)null, (class_2338)null, 0) | 0xFF000000);
      }
    } else {
      this.blockColorsWithDefaultTint[blockStateID] = ColorUtils.colorMultiplier(color, class_1933.method_8377(0.7D, 0.8D) | 0xFF000000);
    }
  }

  private void checkForBiomeTinting(MutableBlockPos blockPos, class_2680 blockState, int color) {
    try {
      class_2248 block = blockState.method_26204();
      String blockName = "" + class_2378.field_11146.method_10221(block);
      if (BlockRepository.biomeBlocks.contains(block) || !blockName.startsWith("minecraft:")) {
        int tint = -1;
        MutableBlockPos tempBlockPos = new MutableBlockPos(0, 0, 0);
        if (blockPos == this.dummyBlockPos) {
          tint = tintFromFakePlacedBlock(blockState, tempBlockPos, (byte)4);
        } else {
          class_2791 chunk = this.game.field_1687.method_22350((class_2338)blockPos);
          if (chunk != null && !((class_2818)chunk).method_12223() && this.game.field_1687.method_8393(blockPos.method_10263() >> 4, blockPos.method_10260() >> 4)) {
            tint = this.game.method_1505().method_1697(blockState, (class_1920)this.game.field_1687, (class_2338)blockPos, 1) | 0xFF000000;
          } else {
            tint = tintFromFakePlacedBlock(blockState, tempBlockPos, (byte)4);
          }
        }
        if (tint != 16777215 && tint != -1) {
          int blockStateID = BlockRepository.getStateId(blockState);
          this.biomeTintsAvailable.add(Integer.valueOf(blockStateID));
          this.blockColorsWithDefaultTint[blockStateID] = ColorUtils.colorMultiplier(color, tint);
        } else {
          this.blockColorsWithDefaultTint[BlockRepository.getStateId(blockState)] = 452984832;
        }
      }
    } catch (Exception exception) {}
  }

  private int tintFromFakePlacedBlock(class_2680 blockState, MutableBlockPos loopBlockPos, byte biomeID) {
    class_638 world = this.game.field_1687;
    if (world == null)
      return -1;
    if (blockState.method_26204() == null)
      return -1;
    int tint = -1;
    return tint;
  }

  public int getBiomeTint(AbstractMapData mapData, class_1937 world, class_2680 blockState, int blockStateID, MutableBlockPos blockPos, MutableBlockPos loopBlockPos, int startX, int startZ) {
    class_2791 chunk = world.method_22350((class_2338)blockPos);
    boolean live = (chunk != null && !((class_2818)chunk).method_12223() && this.game.field_1687.method_8393(blockPos.method_10263() >> 4, blockPos.method_10260() >> 4));
    live = (live && this.game.field_1687.method_22340((class_2338)blockPos));
    int tint = -2;
    if (this.optifineInstalled || (!live && this.biomeTintsAvailable.contains(Integer.valueOf(blockStateID))))
      try {
        int[][] tints = (int[][])this.blockTintTables.get(Integer.valueOf(blockStateID));
        if (tints != null) {
          int r = 0;
          int g = 0;
          int b = 0;
          for (int t = blockPos.method_10263() - 1; t <= blockPos.method_10263() + 1; t++) {
            for (int s = blockPos.method_10260() - 1; s <= blockPos.method_10260() + 1; s++) {
              int biomeID = 0;
              if (live) {
                biomeID = world.method_30349().method_30530(class_2378.field_25114).method_10206(world.method_23753((class_2338)loopBlockPos.withXYZ(t, blockPos.method_10264(), s)).comp_349());
              } else {
                int dataX = t - startX;
                int dataZ = s - startZ;
                dataX = Math.max(dataX, 0);
                dataX = Math.min(dataX, mapData.getWidth() - 1);
                dataZ = Math.max(dataZ, 0);
                dataZ = Math.min(dataZ, mapData.getHeight() - 1);
                biomeID = mapData.getBiomeID(dataX, dataZ);
              }
              if (biomeID < 0)
                biomeID = 1;
              int biomeTint = tints[biomeID][loopBlockPos.y / 8];
              r += (biomeTint & 0xFF0000) >> 16;
              g += (biomeTint & 0xFF00) >> 8;
              b += biomeTint & 0xFF;
            }
          }
          tint = 0xFF000000 | (r / 9 & 0xFF) << 16 | (g / 9 & 0xFF) << 8 | b / 9 & 0xFF;
        }
      } catch (Exception var22) {
        tint = -2;
      }
    if (tint == -2)
      tint = getBuiltInBiomeTint(mapData, world, blockState, blockStateID, blockPos, loopBlockPos, startX, startZ, live);
    return tint;
  }

  private int getBuiltInBiomeTint(AbstractMapData mapData, class_1937 world, class_2680 blockState, int blockStateID, MutableBlockPos blockPos, MutableBlockPos loopBlockPos, int startX, int startZ, boolean live) {
    int tint = -1;
    class_2248 block = blockState.method_26204();
    if (BlockRepository.biomeBlocks.contains(block) || this.biomeTintsAvailable.contains(Integer.valueOf(blockStateID))) {
      if (live)
        try {
          tint = this.game.method_1505().method_1697(blockState, (class_1920)world, (class_2338)blockPos, 0) | 0xFF000000;
        } catch (Exception exception) {}
      if (tint == -1)
        tint = getBuiltInBiomeTintFromUnloadedChunk(mapData, world, blockState, blockStateID, blockPos, loopBlockPos, startX, startZ) | 0xFF000000;
    }
    return tint;
  }

  private int getBuiltInBiomeTintFromUnloadedChunk(AbstractMapData mapData, class_1937 world, class_2680 blockState, int blockStateID, MutableBlockPos blockPos, MutableBlockPos loopBlockPos, int startX, int startZ) {
    int tint = -1;
    class_2248 block = blockState.method_26204();
    ColorResolver colorResolver = null;
    if (block == BlockRepository.water) {
      colorResolver = this.waterColorResolver;
    } else if (block == BlockRepository.spruceLeaves) {
      colorResolver = this.spruceColorResolver;
    } else if (block == BlockRepository.birchLeaves) {
      colorResolver = this.birchColorResolver;
    } else if (block != BlockRepository.oakLeaves && block != BlockRepository.jungleLeaves && block != BlockRepository.acaciaLeaves && block != BlockRepository.darkOakLeaves && block != BlockRepository.mangroveLeaves && block != BlockRepository.vine) {
      if (block == BlockRepository.redstone) {
        colorResolver = this.redstoneColorResolver;
      } else if (BlockRepository.biomeBlocks.contains(block)) {
        colorResolver = this.grassColorResolver;
      }
    } else {
      colorResolver = this.foliageColorResolver;
    }
    if (colorResolver != null) {
      int r = 0;
      int g = 0;
      int b = 0;
      for (int t = blockPos.method_10263() - 1; t <= blockPos.method_10263() + 1; t++) {
        for (int s = blockPos.method_10260() - 1; s <= blockPos.method_10260() + 1; s++) {
          int dataX = t - startX;
          int dataZ = s - startZ;
          dataX = Math.max(dataX, 0);
          dataX = Math.min(dataX, 255);
          dataZ = Math.max(dataZ, 0);
          dataZ = Math.min(dataZ, 255);
          int biomeID = mapData.getBiomeID(dataX, dataZ);
          class_1959 biome = (class_1959)world.method_30349().method_30530(class_2378.field_25114).method_10200(biomeID);
          if (biome == null) {
            MessageUtils.printDebug("Null biome ID! " + biomeID + " at " + t + "," + s);
            MessageUtils.printDebug("block: " + mapData.getBlockstate(dataX, dataZ) + ", height: " + mapData.getHeight(dataX, dataZ));
            MessageUtils.printDebug("Mapdata: " + mapData.toString());
            biome = BiomeRepository.FOREST;
          }
          int biomeTint = colorResolver.getColorAtPos(blockState, biome, (class_2338)loopBlockPos.withXYZ(t, blockPos.method_10264(), s));
          r += (biomeTint & 0xFF0000) >> 16;
          g += (biomeTint & 0xFF00) >> 8;
          b += biomeTint & 0xFF;
        }
      }
      tint = (r / 9 & 0xFF) << 16 | (g / 9 & 0xFF) << 8 | b / 9 & 0xFF;
    } else if (this.biomeTintsAvailable.contains(Integer.valueOf(blockStateID))) {
      tint = getCustomBlockBiomeTintFromUnloadedChunk(mapData, world, blockState, blockPos, loopBlockPos, startX, startZ);
    }
    return tint;
  }

  private int getCustomBlockBiomeTintFromUnloadedChunk(AbstractMapData mapData, class_1937 world, class_2680 blockState, MutableBlockPos blockPos, MutableBlockPos loopBlockPos, int startX, int startZ) {
    int tint = -1;
    try {
      int dataX = blockPos.method_10263() - startX;
      int dataZ = blockPos.method_10260() - startZ;
      dataX = Math.max(dataX, 0);
      dataX = Math.min(dataX, mapData.getWidth() - 1);
      dataZ = Math.max(dataZ, 0);
      dataZ = Math.min(dataZ, mapData.getHeight() - 1);
      byte biomeID = (byte)mapData.getBiomeID(dataX, dataZ);
      tint = tintFromFakePlacedBlock(blockState, loopBlockPos, biomeID);
    } catch (Exception var12) {
      tint = -1;
    }
    return tint;
  }

  private int applyShape(class_2248 block, int color) {
    int alpha = color >> 24 & 0xFF;
    int red = color >> 16 & 0xFF;
    int green = color >> 8 & 0xFF;
    int blue = color >> 0 & 0xFF;
    if (block instanceof net.minecraft.class_2478) {
      alpha = 31;
    } else if (block instanceof net.minecraft.class_2323) {
      alpha = 47;
    } else if (block == BlockRepository.ladder || block == BlockRepository.vine) {
      alpha = 15;
    }
    return (alpha & 0xFF) << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
  }

  private void processCTM() {
    this.renderPassThreeBlendMode = "alpha";
    Properties properties = new Properties();
    class_2960 propertiesFile = new class_2960("minecraft", "optifine/renderpass.properties");
    try {
      InputStream input = ((class_3298)this.game.method_1478().method_14486(propertiesFile).get()).method_14482();
      if (input != null) {
        properties.load(input);
        input.close();
        this.renderPassThreeBlendMode = properties.getProperty("blend.3", "alpha");
      }
    } catch (IOException var9) {
      this.renderPassThreeBlendMode = "alpha";
    }
    String namespace = "minecraft";
    for (class_2960 s : findResources(namespace, "/optifine/ctm", ".properties", true, false, true)) {
      try {
        loadCTM(s);
      } catch (NumberFormatException numberFormatException) {

      } catch (IllegalArgumentException illegalArgumentException) {}
    }
    for (int t = 0; t < this.blockColors.length; t++) {
      if (this.blockColors[t] != 452984832 && this.blockColors[t] != -16842497) {
        if ((this.blockColors[t] >> 24 & 0xFF) < 27)
          this.blockColors[t] = this.blockColors[t] | 0x1B000000;
        checkForBiomeTinting(this.dummyBlockPos, BlockRepository.getStateById(t), this.blockColors[t]);
      }
    }
  }

  private void loadCTM(class_2960 propertiesFile) {
    if (propertiesFile != null) {
      class_776 blockRendererDispatcher = this.game.method_1541();
      class_773 blockModelShapes = blockRendererDispatcher.method_3351();
      Properties properties = new Properties();
      try {
        InputStream input = ((class_3298)this.game.method_1478().method_14486(propertiesFile).get()).method_14482();
        if (input != null) {
          properties.load(input);
          input.close();
        }
      } catch (IOException var39) {
        return;
      }
      String filePath = propertiesFile.method_12832();
      String method = properties.getProperty("method", "").trim().toLowerCase();
      String faces = properties.getProperty("faces", "").trim().toLowerCase();
      String matchBlocks = properties.getProperty("matchBlocks", "").trim().toLowerCase();
      String matchTiles = properties.getProperty("matchTiles", "").trim().toLowerCase();
      String metadata = properties.getProperty("metadata", "").trim().toLowerCase();
      String tiles = properties.getProperty("tiles", "").trim();
      String biomes = properties.getProperty("biomes", "").trim().toLowerCase();
      String renderPass = properties.getProperty("renderPass", "").trim().toLowerCase();
      metadata = metadata.replaceAll("\\s+", ",");
      Set<class_2680> blockStates = new HashSet<>();
      blockStates.addAll(parseBlocksList(matchBlocks, metadata));
      String directory = filePath.substring(0, filePath.lastIndexOf("/") + 1);
      String[] tilesParsed = parseStringList(tiles);
      String tilePath = directory + "0";
      if (tilesParsed.length > 0)
        tilePath = tilesParsed[0].trim();
      if (tilePath.startsWith("~")) {
        tilePath = tilePath.replace("~", "optifine");
      } else if (!tilePath.contains("/")) {
        tilePath = directory + directory;
      }
      if (!tilePath.toLowerCase().endsWith(".png"))
        tilePath = tilePath + ".png";
      String[] biomesArray = biomes.split(" ");
      if (blockStates.size() == 0) {
        class_2248 block = null;
        Pattern pattern = Pattern.compile(".*/block_(.+).properties");
        Matcher matcher = pattern.matcher(filePath);
        if (matcher.find()) {
          block = getBlockFromName(matcher.group(1));
          if (block != null) {
            Set<class_2680> matching = parseBlockMetadata(block, metadata);
            if (matching.size() == 0)
              matching.addAll((Collection<? extends class_2680>)block.method_9595().method_11662());
            blockStates.addAll(matching);
          }
        } else {
          if (matchTiles.equals(""))
            matchTiles = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf(".properties"));
          if (!matchTiles.contains(":"))
            matchTiles = "minecraft:blocks/" + matchTiles;
          class_2960 matchID = new class_2960(matchTiles);
          class_1058 compareIcon = this.game.method_1549(class_1059.field_5275).apply(matchID);
          if (compareIcon.method_4598() != class_1047.method_4539()) {
            ArrayList<class_2680> tmpList = new ArrayList();
            for (class_2248 testBlock : class_2378.field_11146) {
              UnmodifiableIterator blockStateID = testBlock.method_9595().method_11662().iterator();
              while (blockStateID.hasNext()) {
                class_2680 blockState = (class_2680)blockStateID.next();
                try {
                  class_1087 bakedModel = blockModelShapes.method_3335(blockState);
                  List quads = new ArrayList();
                  quads.addAll(bakedModel.method_4707(blockState, class_2350.field_11036, this.random));
                  quads.addAll(bakedModel.method_4707(blockState, (class_2350)null, this.random));
                  BlockModel model = new BlockModel(quads, this.failedToLoadX, this.failedToLoadY);
                  if (model.numberOfFaces() > 0) {
                    ArrayList blockFaces = model.getFaces();
                    for (int i = 0; i < blockFaces.size(); i++) {
                      BlockModel.BlockFace face = model.getFaces().get(i);
                      float minU = face.getMinU();
                      float maxU = face.getMaxU();
                      float minV = face.getMinV();
                      float maxV = face.getMaxV();
                      if (similarEnough(minU, maxU, minV, maxV, compareIcon.method_4594(), compareIcon.method_4577(), compareIcon.method_4593(), compareIcon.method_4575()))
                        tmpList.add(blockState);
                    }
                  }
                } catch (Exception exception) {}
              }
            }
            blockStates.addAll(tmpList);
          }
        }
      }
      if (blockStates.size() != 0 &&
              !method.equals("horizontal") && !method.startsWith("overlay") && (method.equals("sandstone") || method.equals("top") || faces.contains("top") || faces.contains("all") || faces.length() == 0))
        try {
          class_2960 pngResource = new class_2960(propertiesFile.method_12836(), tilePath);
          InputStream is = ((class_3298)this.game.method_1478().method_14486(pngResource).get()).method_14482();
          Image top = ImageIO.read(is);
          is.close();
          top = top.getScaledInstance(1, 1, 4);
          BufferedImage topBuff = new BufferedImage(top.getWidth((ImageObserver)null), top.getHeight((ImageObserver)null), 6);
          Graphics gfx = topBuff.createGraphics();
          gfx.drawImage(top, 0, 0, (ImageObserver)null);
          gfx.dispose();
          int topRGB = topBuff.getRGB(0, 0);
          if ((topRGB >> 24 & 0xFF) == 0)
            return;
          for (class_2680 blockState : blockStates) {
            topRGB = topBuff.getRGB(0, 0);
            if (blockState.method_26204() == BlockRepository.cobweb)
              topRGB |= 0xFF000000;
            if (renderPass.equals("3")) {
              topRGB = processRenderPassThree(topRGB);
              int i = BlockRepository.getStateId(blockState);
              int baseRGB = this.blockColors[i];
              if (baseRGB != 452984832 && baseRGB != -16842497)
                topRGB = ColorUtils.colorMultiplier(baseRGB, topRGB);
            }
            if (BlockRepository.shapedBlocks.contains(blockState.method_26204()))
              topRGB = applyShape(blockState.method_26204(), topRGB);
            int blockStateID = BlockRepository.getStateId(blockState);
            if (!biomes.equals("")) {
              this.biomeTextureAvailable.add(Integer.valueOf(blockStateID));
              for (int r = 0; r < biomesArray.length; r++) {
                int biomeInt = parseBiomeName(biomesArray[r]);
                if (biomeInt != -1)
                  this.blockBiomeSpecificColors.put("" + blockStateID + " " + blockStateID, Integer.valueOf(topRGB));
              }
              continue;
            }
            this.blockColors[blockStateID] = topRGB;
          }
        } catch (IOException var40) {
          System.err.println("error getting CTM block from " + propertiesFile.method_12832() + ": " + filePath + " " + class_2378.field_11146.method_10221(((class_2680)blockStates.iterator().next()).method_26204()).toString() + " " + tilePath);
          var40.printStackTrace();
        }
    }
  }

  private boolean similarEnough(float a, float b, float c, float d, float one, float two, float three, float four) {
    boolean similar = (Math.abs(a - one) < 1.0E-4D);
    similar = (similar && Math.abs(b - two) < 1.0E-4D);
    similar = (similar && Math.abs(c - three) < 1.0E-4D);
    return (similar && Math.abs(d - four) < 1.0E-4D);
  }

  private int processRenderPassThree(int rgb) {
    if (this.renderPassThreeBlendMode.equals("color") || this.renderPassThreeBlendMode.equals("overlay")) {
      int red = rgb >> 16 & 0xFF;
      int green = rgb >> 8 & 0xFF;
      int blue = rgb >> 0 & 0xFF;
      float colorAverage = (red + blue + green) / 3.0F;
      float lighteningFactor = (colorAverage - 127.5F) * 2.0F;
      red += (int)(red * lighteningFactor / 255.0F);
      blue += (int)(red * lighteningFactor / 255.0F);
      green += (int)(red * lighteningFactor / 255.0F);
      int newAlpha = (int)Math.abs(lighteningFactor);
      rgb = newAlpha << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
    }
    return rgb;
  }

  private String[] parseStringList(String list) {
    ArrayList<String> tmpList = new ArrayList();
    for (String token : list.split("\\s+")) {
      token = token.trim();
      try {
        if (token.matches("^\\d+$")) {
          tmpList.add("" + Integer.parseInt(token));
        } else if (token.matches("^\\d+-\\d+$")) {
          String[] t = token.split("-");
          int min = Integer.parseInt(t[0]);
          int max = Integer.parseInt(t[1]);
          for (int j = min; j <= max; j++)
            tmpList.add("" + j);
        } else if (token != null && token != "") {
          tmpList.add(token);
        }
      } catch (NumberFormatException numberFormatException) {}
    }
    String[] a = new String[tmpList.size()];
    for (int i = 0; i < a.length; i++)
      a[i] = tmpList.get(i);
    return a;
  }

  private Set parseBlocksList(String blocks, String metadataLine) {
    Set blockStates = new HashSet();
    for (String blockString : blocks.split("\\s+")) {
      String metadata = metadataLine;
      blockString = blockString.trim();
      String[] blockComponents = blockString.split(":");
      int tokensUsed = 0;
      class_2248 block = null;
      block = getBlockFromName(blockComponents[0]);
      if (block != null) {
        tokensUsed = 1;
      } else if (blockComponents.length > 1) {
        block = getBlockFromName(blockComponents[0] + ":" + blockComponents[0]);
        if (block != null)
          tokensUsed = 2;
      }
      if (block != null) {
        if (blockComponents.length > tokensUsed) {
          metadata = blockComponents[tokensUsed];
          for (int t = tokensUsed + 1; t < blockComponents.length; t++)
            metadata = metadata + ":" + metadata;
        }
        blockStates.addAll(parseBlockMetadata(block, metadata));
      }
    }
    return blockStates;
  }

  private Set parseBlockMetadata(class_2248 block, String metadataList) {
    Set<class_2680> blockStates = new HashSet();
    if (metadataList.equals("")) {
      blockStates.addAll((Collection)block.method_9595().method_11662());
    } else {
      Set<String> valuePairs = new HashSet<>();
      for (String metadata : metadataList.split(":")) {
        metadata.trim();
        if (metadata.contains("="))
          valuePairs.add(metadata);
      }
      if (valuePairs.size() > 0) {
        UnmodifiableIterator var22 = block.method_9595().method_11662().iterator();
        while (var22.hasNext()) {
          class_2680 blockState = (class_2680)var22.next();
          boolean matches = true;
          for (String pair : valuePairs) {
            String[] propertyAndValues = pair.split("\\s*=\\s*", 5);
            if (propertyAndValues.length == 2) {
              class_2769 property = block.method_9595().method_11663(propertyAndValues[0]);
              if (property != null) {
                boolean valueIncluded = false;
                String[] values = propertyAndValues[1].split(",");
                for (String value : values) {
                  if (property.method_11902() == Integer.class && value.matches("^\\d+-\\d+$")) {
                    String[] range = value.split("-");
                    int min = Integer.parseInt(range[0]);
                    int max = Integer.parseInt(range[1]);
                    int intValue = ((Integer)Integer.class.cast(blockState.method_11654(property))).intValue();
                    if (intValue >= min && intValue <= max)
                      valueIncluded = true;
                  } else if (!blockState.method_11654(property).equals(property.method_11900(value))) {
                    valueIncluded = true;
                  }
                }
                matches = (matches && valueIncluded);
              }
            }
          }
          if (matches)
            blockStates.add(blockState);
        }
      }
    }
    return blockStates;
  }

  private int parseBiomeName(String name) {
    class_1959 biome = (class_1959)this.world.method_30349().method_30530(class_2378.field_25114).method_10223(new class_2960(name));
    return (biome != null) ? this.world.method_30349().method_30530(class_2378.field_25114).method_10206(biome) : -1;
  }

  private List<class_2960> findResources(String namespace, String directory, String suffixMaybeNull, boolean recursive, boolean directories, boolean sortByFilename) {
    if (directory == null)
      directory = "";
    if (directory.startsWith("/"))
      directory = directory.substring(1);
    String suffix = (suffixMaybeNull == null) ? "" : suffixMaybeNull;
    ArrayList<class_2960> resources = new ArrayList<>();
    Map<class_2960, class_3298> resourceMap = this.game.method_1478().method_14488(directory, asset -> asset.method_12832().endsWith(suffix));
    for (class_2960 candidate : resourceMap.keySet()) {
      if (candidate.method_12836().equals(namespace))
        resources.add(candidate);
    }
    if (sortByFilename) {
      resources.sort((o1, o2) -> {
        String f1 = o1.method_12832().replaceAll(".*/", "").replaceFirst("\\.properties", "");
        String f2 = o2.method_12832().replaceAll(".*/", "").replaceFirst("\\.properties", "");
        int result = f1.compareTo(f2);
        return (result != 0) ? result : o1.method_12832().compareTo(o2.method_12832());
      });
    } else {
      resources.sort(Comparator.comparing(class_2960::method_12832));
    }
    return resources;
  }

  private void processColorProperties() {
    Properties properties = new Properties();
    try {
      InputStream input = ((class_3298)this.game.method_1478().method_14486(new class_2960("optifine/color.properties")).get()).method_14482();
      if (input != null) {
        properties.load(input);
        input.close();
      }
    } catch (IOException iOException) {}
    class_2680 blockState = BlockRepository.lilypad.method_9564();
    int blockStateID = BlockRepository.getStateId(blockState);
    int lilyRGB = getBlockColor(blockStateID);
    int lilypadMultiplier = 2129968;
    String lilypadMultiplierString = properties.getProperty("lilypad");
    if (lilypadMultiplierString != null)
      lilypadMultiplier = Integer.parseInt(lilypadMultiplierString, 16);
    for (UnmodifiableIterator<class_2680> unmodifiableIterator = BlockRepository.lilypad.method_9595().method_11662().iterator(); unmodifiableIterator.hasNext(); this.blockColorsWithDefaultTint[blockStateID] = this.blockColors[blockStateID]) {
      class_2680 padBlockState = (class_2680)unmodifiableIterator.next();
      blockStateID = BlockRepository.getStateId(padBlockState);
      this.blockColors[blockStateID] = ColorUtils.colorMultiplier(lilyRGB, lilypadMultiplier | 0xFF000000);
    }
    String defaultFormat = properties.getProperty("palette.format");
    boolean globalGrid = (defaultFormat != null && defaultFormat.equalsIgnoreCase("grid"));
    Enumeration<?> e = properties.propertyNames();
    while (e.hasMoreElements()) {
      String key = (String)e.nextElement();
      if (key.startsWith("palette.block")) {
        String filename = key.substring("palette.block.".length());
        filename = filename.replace("~", "optifine");
        processColorPropertyHelper(new class_2960(filename), properties.getProperty(key), globalGrid);
      }
    }
    for (class_2960 resource : findResources("minecraft", "/optifine/colormap/blocks", ".properties", true, false, true)) {
      class_2960 resourcePNG;
      boolean grid;
      Properties colorProperties = new Properties();
      try {
        InputStream input = ((class_3298)this.game.method_1478().method_14486(resource).get()).method_14482();
        if (input != null) {
          colorProperties.load(input);
          input.close();
        }
      } catch (IOException var21) {
        break;
      }
      String names = colorProperties.getProperty("blocks");
      if (names == null) {
        String name = resource.method_12832();
        name = name.substring(name.lastIndexOf("/") + 1, name.lastIndexOf(".properties"));
        names = name;
      }
      String source = colorProperties.getProperty("source");
      if (source != null) {
        resourcePNG = new class_2960(resource.method_12836(), source);
        this.game.method_1478().method_14486(resourcePNG);
      } else {
        resourcePNG = new class_2960(resource.method_12836(), resource.method_12832().replace(".properties", ".png"));
      }
      String format = colorProperties.getProperty("format");
      if (format != null) {
        grid = format.equalsIgnoreCase("grid");
      } else {
        grid = globalGrid;
      }
      String yOffsetString = colorProperties.getProperty("yOffset");
      int yOffset = 0;
      if (yOffsetString != null)
        yOffset = Integer.parseInt(yOffsetString);
      processColorProperty(resourcePNG, names, grid, yOffset);
    }
    processColorPropertyHelper(new class_2960("optifine/colormap/water.png"), "water", globalGrid);
    processColorPropertyHelper(new class_2960("optifine/colormap/watercolorx.png"), "water", globalGrid);
    processColorPropertyHelper(new class_2960("optifine/colormap/swampgrass.png"), "grass_block grass fern tall_grass large_fern", globalGrid);
    processColorPropertyHelper(new class_2960("optifine/colormap/swampgrasscolor.png"), "grass_block grass fern tall_grass large_fern", globalGrid);
    processColorPropertyHelper(new class_2960("optifine/colormap/swampfoliage.png"), "oak_leaves vine", globalGrid);
    processColorPropertyHelper(new class_2960("optifine/colormap/swampfoliagecolor.png"), "oak_leaves vine", globalGrid);
    processColorPropertyHelper(new class_2960("optifine/colormap/pine.png"), "spruce_leaves", globalGrid);
    processColorPropertyHelper(new class_2960("optifine/colormap/pinecolor.png"), "spruce_leaves", globalGrid);
    processColorPropertyHelper(new class_2960("optifine/colormap/birch.png"), "birch_leaves", globalGrid);
    processColorPropertyHelper(new class_2960("optifine/colormap/birchcolor.png"), "birch_leaves", globalGrid);
  }

  private void processColorPropertyHelper(class_2960 resource, String list, boolean grid) {
    class_2960 resourceProperties = new class_2960(resource.method_12836(), resource.method_12832().replace(".png", ".properties"));
    Properties colorProperties = new Properties();
    int yOffset = 0;
    try {
      InputStream input = ((class_3298)this.game.method_1478().method_14486(resourceProperties).get()).method_14482();
      if (input != null) {
        colorProperties.load(input);
        input.close();
      }
      String format = colorProperties.getProperty("format");
      if (format != null)
        grid = format.equalsIgnoreCase("grid");
      String yOffsetString = colorProperties.getProperty("yOffset");
      if (yOffsetString != null)
        yOffset = Integer.valueOf(yOffsetString).intValue();
    } catch (IOException iOException) {}
    processColorProperty(resource, list, grid, yOffset);
  }

  private void processColorProperty(class_2960 resource, String list, boolean grid, int yOffset) {
    Image tintColors;
    int[][] tints = new int[this.sizeOfBiomeArray][32];
    for (int[] row : tints)
      Arrays.fill(row, -1);
    boolean swamp = resource.method_12832().contains("/swamp");
    try {
      InputStream is = ((class_3298)this.game.method_1478().method_14486(resource).get()).method_14482();
      tintColors = ImageIO.read(is);
      is.close();
    } catch (IOException var21) {
      return;
    }
    BufferedImage tintColorsBuff = new BufferedImage(tintColors.getWidth((ImageObserver)null), tintColors.getHeight((ImageObserver)null), 1);
    Graphics gfx = tintColorsBuff.createGraphics();
    gfx.drawImage(tintColors, 0, 0, null);
    gfx.dispose();
    int numBiomesToCheck = grid ? Math.min(tintColorsBuff.getWidth(), this.sizeOfBiomeArray) : this.sizeOfBiomeArray;
    for (int t = 0; t < numBiomesToCheck; t++) {
      class_1959 biome = (class_1959)this.world.method_30349().method_30530(class_2378.field_25114).method_10200(t);
      if (biome != null) {
        int tintMult = 0;
        int heightMultiplier = tintColorsBuff.getHeight() / 32;
        for (int s = 0; s < 32; s++) {
          if (grid) {
            tintMult = tintColorsBuff.getRGB(t, Math.max(0, s * heightMultiplier - yOffset)) & 0xFFFFFF;
          } else {
            double var1 = class_3532.method_15363(biome.method_8712(), 0.0F, 1.0F);
            double var2 = class_3532.method_15363(biome.method_8715(), 0.0F, 1.0F);
            var2 *= var1;
            var1 = 1.0D - var1;
            var2 = 1.0D - var2;
            tintMult = tintColorsBuff.getRGB((int)((tintColorsBuff.getWidth() - 1) * var1), (int)((tintColorsBuff.getHeight() - 1) * var2)) & 0xFFFFFF;
          }
          if (tintMult != 0 && (!swamp || biome == BiomeRepository.SWAMP || biome == BiomeRepository.SWAMP_HILLS))
            tints[t][s] = tintMult;
        }
      }
    }
    Set<class_2680> blockStates = new HashSet<>();
    blockStates.addAll(parseBlocksList(list, ""));
    for (class_2680 blockState : blockStates) {
      int blockStateID = BlockRepository.getStateId(blockState);
      int[][] previousTints = (int[][])this.blockTintTables.get(Integer.valueOf(blockStateID));
      if (swamp && previousTints == null) {
        class_2960 defaultResource;
        if (resource.method_12832().contains("grass")) {
          defaultResource = new class_2960("textures/colormap/grass.png");
        } else {
          defaultResource = new class_2960("textures/colormap/foliage.png");
        }
        String stateString = blockState.toString().toLowerCase();
        stateString = stateString.replaceAll("^block", "");
        stateString = stateString.replace("{", "");
        stateString = stateString.replace("}", "");
        stateString = stateString.replace("[", ":");
        stateString = stateString.replace("]", "");
        stateString = stateString.replace(",", ":");
        processColorProperty(defaultResource, stateString, false, 0);
        previousTints = (int[][])this.blockTintTables.get(Integer.valueOf(blockStateID));
      }
      if (previousTints != null)
        for (int i = 0; i < this.sizeOfBiomeArray; i++) {
          for (int s = 0; s < 32; s++) {
            if (tints[i][s] == -1)
              tints[i][s] = previousTints[i][s];
          }
        }
      this.blockColorsWithDefaultTint[blockStateID] = ColorUtils.colorMultiplier(getBlockColor(blockStateID), tints[4][8] | 0xFF000000);
      this.blockTintTables.put(Integer.valueOf(blockStateID), tints);
      this.biomeTintsAvailable.add(Integer.valueOf(blockStateID));
    }
  }

  private class_2248 getBlockFromName(String name) {
    try {
      class_2960 resourceLocation = new class_2960(name);
      return class_2378.field_11146.method_10250(resourceLocation) ? (class_2248)class_2378.field_11146.method_10223(resourceLocation) : null;
    } catch (class_151|NumberFormatException var3) {
      return null;
    }
  }

  private static interface ColorResolver {
    int getColorAtPos(class_2680 param1class_2680, class_1959 param1class_1959, class_2338 param1class_2338);
  }
}
