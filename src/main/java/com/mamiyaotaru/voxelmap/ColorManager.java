package com.mamiyaotaru.voxelmap;

import com.google.common.collect.UnmodifiableIterator;
import com.mamiyaotaru.voxelmap.interfaces.AbstractMapData;
import com.mamiyaotaru.voxelmap.interfaces.IColorManager;
import com.mamiyaotaru.voxelmap.interfaces.IVoxelMap;
import com.mamiyaotaru.voxelmap.util.BiomeRepository;
import com.mamiyaotaru.voxelmap.util.BlockModel;
import com.mamiyaotaru.voxelmap.util.BlockRepository;
import com.mamiyaotaru.voxelmap.util.ColorUtils;
import com.mamiyaotaru.voxelmap.util.GLShim;
import com.mamiyaotaru.voxelmap.util.GLUtils;
import com.mamiyaotaru.voxelmap.util.ImageUtils;
import com.mamiyaotaru.voxelmap.util.MessageUtils;
import com.mamiyaotaru.voxelmap.util.MutableBlockPos;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import net.minecraft.class_2323;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2378;
import net.minecraft.class_2404;
import net.minecraft.class_2457;
import net.minecraft.class_2464;
import net.minecraft.class_2478;
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
import net.minecraft.class_809.class_811;

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
    private HashMap blockTintTables = new HashMap();
    private HashSet biomeTextureAvailable = new HashSet();
    private HashMap blockBiomeSpecificColors = new HashMap();
    private float failedToLoadX = 0.0F;
    private float failedToLoadY = 0.0F;
    private String renderPassThreeBlendMode;
    private class_5819 random = class_5819.method_43047();
    private final Object tpLoadLock = new Object();
    private boolean loaded = false;
    private final MutableBlockPos dummyBlockPos;
    private final class_1160 fullbright;
    private final ColorResolver spruceColorResolver;
    private final ColorResolver birchColorResolver;
    private final ColorResolver grassColorResolver;
    private final ColorResolver foliageColorResolver;
    private final ColorResolver waterColorResolver;
    private final ColorResolver redstoneColorResolver;

    public ColorManager(IVoxelMap master) {
        this.dummyBlockPos = new MutableBlockPos(class_2338.field_10980.method_10263(), class_2338.field_10980.method_10264(), class_2338.field_10980.method_10260());
        this.fullbright = new class_1160(1.0F, 1.0F, 1.0F);
        this.spruceColorResolver = (blockState, biomex, blockPos) -> {
            return class_1926.method_8342();
        };
        this.birchColorResolver = (blockState, biomex, blockPos) -> {
            return class_1926.method_8343();
        };
        this.grassColorResolver = (blockState, biomex, blockPos) -> {
            return biomex.method_8711((double)blockPos.method_10263(), (double)blockPos.method_10260());
        };
        this.foliageColorResolver = (blockState, biomex, blockPos) -> {
            return biomex.method_8698();
        };
        this.waterColorResolver = (blockState, biomex, blockPos) -> {
            return biomex.method_8687();
        };
        this.redstoneColorResolver = (blockState, biomex, blockPos) -> {
            return class_2457.method_10487((Integer)blockState.method_11654(class_2457.field_11432));
        };
        this.master = master;
        this.game = class_310.method_1551();
        this.optifineInstalled = false;
        Field ofProfiler = null;

        try {
            ofProfiler = class_315.class.getDeclaredField("ofProfiler");
        } catch (SecurityException var9) {
        } catch (NoSuchFieldException var10) {
        } finally {
            if (ofProfiler != null) {
                this.optifineInstalled = true;
            }

        }

        Iterator var3 = class_5458.field_25933.iterator();

        while(var3.hasNext()) {
            class_1959 biome = (class_1959)var3.next();
            int biomeID = class_5458.field_25933.method_10206(biome);
            if (biomeID > this.sizeOfBiomeArray) {
                this.sizeOfBiomeArray = biomeID;
            }
        }

        ++this.sizeOfBiomeArray;
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
            Iterator var3 = this.world.method_30349().method_30530(class_2378.field_25114).iterator();

            while(var3.hasNext()) {
                class_1959 biome = (class_1959)var3.next();
                int biomeID = this.world.method_30349().method_30530(class_2378.field_25114).method_10206(biome);
                if (biomeID > largestBiomeID) {
                    largestBiomeID = biomeID;
                }
            }

            if (this.sizeOfBiomeArray != largestBiomeID + 1) {
                this.sizeOfBiomeArray = largestBiomeID + 1;
                biomesChanged = true;
            }
        }

        boolean changed = this.resourcePacksChanged || biomesChanged;
        this.resourcePacksChanged = false;
        if (changed) {
            this.loadColors();
        }

        return changed;
    }

    private void loadColors() {
        this.game.field_1724.method_3117();
        BlockRepository.getBlocks();
        BiomeRepository.getBiomes();
        this.loadColorPicker();
        this.loadTexturePackTerrainImage();
        class_1058 missing = (class_1058)this.game.method_1549(class_1059.field_5275).apply(new class_2960("missingno"));
        this.failedToLoadX = missing.method_4594();
        this.failedToLoadY = missing.method_4593();
        this.loaded = false;

        try {
            Arrays.fill(this.blockColors, -16842497);
            Arrays.fill(this.blockColorsWithDefaultTint, -16842497);
            this.loadSpecialColors();
            this.biomeTintsAvailable.clear();
            this.biomeTextureAvailable.clear();
            this.blockBiomeSpecificColors.clear();
            this.blockTintTables.clear();
            if (this.optifineInstalled) {
                try {
                    this.processCTM();
                } catch (Exception var4) {
                    System.err.println("error loading CTM " + var4.getLocalizedMessage());
                    var4.printStackTrace();
                }

                try {
                    this.processColorProperties();
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
            this.drawModel(class_2350.field_11034, blockState, model, stack, iconScale, captureDepth);
            BufferedImage blockImage = ImageUtils.createBufferedImageFromGLID(GLUtils.fboTextureID);
            File var10004 = class_310.method_1551().field_1697;
            String var10005 = blockState.method_26204().method_9518().getString();
            ImageIO.write(blockImage, "png", new File(var10004, var10005 + "-" + class_2248.method_9507(blockState) + ".png"));
            return blockImage;
        } catch (Exception var8) {
            PrintStream var10000 = System.out;
            String var10001 = blockState.toString();
            var10000.println("error getting block armor image for " + var10001 + ": " + var8.getLocalizedMessage());
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
        class_1159 matrix4f = class_1159.method_34239(0.0F, (float)width, 0.0F, (float)height, 1000.0F, 3000.0F);
        RenderSystem.setProjectionMatrix(matrix4f);
        class_4587 matrixStack = RenderSystem.getModelViewStack();
        matrixStack.method_22903();
        matrixStack.method_34426();
        matrixStack.method_22904(0.0, 0.0, -3000.0 + (double)(captureDepth * scale));
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
        GLShim.glClearDepth(1.0);
        GLShim.glClear(16640);
        GLShim.glBlendFunc(770, 771);
        matrixStack.method_22903();
        matrixStack.method_22904((double)((float)(width / 2) - size / 2.0F + transX), (double)((float)(height / 2) - size / 2.0F + transY), (double)(0.0F + transZ));
        matrixStack.method_22905(size, size, size);
        class_310.method_1551().method_1531().method_4619(class_1059.field_5275).method_4527(false, false);
        GLUtils.img2(class_1059.field_5275);
        matrixStack.method_22907(class_1160.field_20705.method_23214(180.0F));
        matrixStack.method_22907(class_1160.field_20705.method_23214(rotY));
        matrixStack.method_22907(class_1160.field_20703.method_23214(rotX));
        matrixStack.method_22907(class_1160.field_20707.method_23214(rotZ));
        if (facing == class_2350.field_11036) {
            matrixStack.method_22907(class_1160.field_20703.method_23214(90.0F));
        }

        RenderSystem.applyModelViewMatrix();
        class_1162 fullbright2 = new class_1162(this.fullbright);
        fullbright2.method_22674(matrixStack.method_23760().method_23761());
        class_1160 fullbright3 = new class_1160(fullbright2);
        RenderSystem.setShaderLights(fullbright3, fullbright3);
        class_4587 newMatrixStack = new class_4587();
        class_4597.class_4598 immediate = this.game.method_22940().method_23000();
        this.game.method_1480().method_23179(stack, class_811.field_4315, false, newMatrixStack, immediate, 15728880, class_4608.field_21444, model);
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
            gfx.drawImage(terrainStitched, 0, 0, (ImageObserver)null);
            gfx.dispose();
        } catch (Exception var4) {
            System.err.println("Error processing new resource pack: " + var4.getLocalizedMessage());
            var4.printStackTrace();
        }

    }

    private void loadSpecialColors() {
        int blockStateID;
        UnmodifiableIterator var6;
        class_2680 blockState;
        for(var6 = BlockRepository.pistonTechBlock.method_9595().method_11662().iterator(); var6.hasNext(); this.blockColors[blockStateID] = 0) {
            blockState = (class_2680)var6.next();
            blockStateID = BlockRepository.getStateId(blockState);
        }

        for(var6 = BlockRepository.barrier.method_9595().method_11662().iterator(); var6.hasNext(); this.blockColors[blockStateID] = 0) {
            blockState = (class_2680)var6.next();
            blockStateID = BlockRepository.getStateId(blockState);
        }

    }

    public final int getBlockColorWithDefaultTint(MutableBlockPos blockPos, int blockStateID) {
        if (this.loaded) {
            int col = 452984832;

            try {
                col = this.blockColorsWithDefaultTint[blockStateID];
            } catch (ArrayIndexOutOfBoundsException var5) {
            }

            return col != -16842497 ? col : this.getBlockColor(blockPos, blockStateID);
        } else {
            return 0;
        }
    }

    public final int getBlockColor(MutableBlockPos blockPos, int blockStateID, int biomeID) {
        if (this.loaded) {
            if (this.optifineInstalled && this.biomeTextureAvailable.contains(blockStateID)) {
                Integer col = (Integer)this.blockBiomeSpecificColors.get("" + blockStateID + " " + biomeID);
                if (col != null) {
                    return col;
                }
            }

            return this.getBlockColor(blockPos, blockStateID);
        } else {
            return 0;
        }
    }

    private int getBlockColor(int blockStateID) {
        return this.getBlockColor(this.dummyBlockPos, blockStateID);
    }

    private final int getBlockColor(MutableBlockPos blockPos, int blockStateID) {
        int col = 452984832;

        try {
            col = this.blockColors[blockStateID];
        } catch (ArrayIndexOutOfBoundsException var5) {
            this.resizeColorArrays(blockStateID);
        }

        if (col == -16842497 || col == 452984832) {
            class_2680 blockState = BlockRepository.getStateById(blockStateID);
            col = this.blockColors[blockStateID] = this.getColor(blockPos, blockState);
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
            int color = this.getColorForBlockPosBlockStateAndFacing(blockPos, blockState, class_2350.field_11036);
            if (color == 452984832) {
                class_776 blockRendererDispatcher = this.game.method_1541();
                color = this.getColorForTerrainSprite(blockState, blockRendererDispatcher);
            }

            class_2248 block = blockState.method_26204();
            if (block == BlockRepository.cobweb) {
                color |= -16777216;
            }

            if (block == BlockRepository.redstone) {
                color = ColorUtils.colorMultiplier(color, this.game.method_1505().method_1697(blockState, (class_1920)null, (class_2338)null, 0) | -16777216);
            }

            if (BlockRepository.biomeBlocks.contains(block)) {
                this.applyDefaultBuiltInShading(blockState, color);
            } else {
                this.checkForBiomeTinting(blockPos, blockState, color);
            }

            if (BlockRepository.shapedBlocks.contains(block)) {
                color = this.applyShape(block, color);
            }

            if ((color >> 24 & 255) < 27) {
                color |= 452984832;
            }

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
                        color = this.getColorForCoordinatesAndImage(new float[]{0.0F, 1.0F, 0.0F, 1.0F}, modelImage);
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
            if (block instanceof class_2404) {
                if (material == class_3614.field_15920) {
                    icon = (class_1058)this.game.method_1549(class_1059.field_5275).apply(new class_2960("minecraft:blocks/water_flow"));
                } else if (material == class_3614.field_15922) {
                    icon = (class_1058)this.game.method_1549(class_1059.field_5275).apply(new class_2960("minecraft:blocks/lava_flow"));
                }
            } else if (material == class_3614.field_15920) {
                icon = (class_1058)this.game.method_1549(class_1059.field_5275).apply(new class_2960("minecraft:blocks/water_still"));
            } else if (material == class_3614.field_15922) {
                icon = (class_1058)this.game.method_1549(class_1059.field_5275).apply(new class_2960("minecraft:blocks/lava_still"));
            }
        }

        return this.getColorForIcon(icon);
    }

    private int getColorForIcon(class_1058 icon) {
        int color = 452984832;
        if (icon != null) {
            float left = icon.method_4594();
            float right = icon.method_4577();
            float top = icon.method_4593();
            float bottom = icon.method_4575();
            color = this.getColorForCoordinatesAndImage(new float[]{left, right, top, bottom}, this.terrainBuff);
        }

        return color;
    }

    private int getColorForCoordinatesAndImage(float[] uv, BufferedImage imageBuff) {
        int color = 452984832;
        if (uv[0] != this.failedToLoadX || uv[2] != this.failedToLoadY) {
            int left = (int)(uv[0] * (float)imageBuff.getWidth());
            int right = (int)Math.ceil((double)(uv[1] * (float)imageBuff.getWidth()));
            int top = (int)(uv[2] * (float)imageBuff.getHeight());
            int bottom = (int)Math.ceil((double)(uv[3] * (float)imageBuff.getHeight()));

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
                System.out.println("" + left + " " + right + " " + top + " " + bottom);
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
                this.blockColorsWithDefaultTint[blockStateID] = ColorUtils.colorMultiplier(color, BiomeRepository.FOREST.method_8687() | -16777216);
            } else {
                this.blockColorsWithDefaultTint[blockStateID] = ColorUtils.colorMultiplier(color, this.game.method_1505().method_1697(blockState, (class_1920)null, (class_2338)null, 0) | -16777216);
            }
        } else {
            this.blockColorsWithDefaultTint[blockStateID] = ColorUtils.colorMultiplier(color, class_1933.method_8377(0.7, 0.8) | -16777216);
        }

    }

    private void checkForBiomeTinting(MutableBlockPos blockPos, class_2680 blockState, int color) {
        try {
            class_2248 block = blockState.method_26204();
            String blockName = "" + class_2378.field_11146.method_10221(block);
            if (BlockRepository.biomeBlocks.contains(block) || !blockName.startsWith("minecraft:")) {
                int tint = true;
                MutableBlockPos tempBlockPos = new MutableBlockPos(0, 0, 0);
                int tint;
                if (blockPos == this.dummyBlockPos) {
                    tint = this.tintFromFakePlacedBlock(blockState, tempBlockPos, (byte)4);
                } else {
                    class_2791 chunk = this.game.field_1687.method_22350(blockPos);
                    if (chunk != null && !((class_2818)chunk).method_12223() && this.game.field_1687.method_8393(blockPos.method_10263() >> 4, blockPos.method_10260() >> 4)) {
                        tint = this.game.method_1505().method_1697(blockState, this.game.field_1687, blockPos, 1) | -16777216;
                    } else {
                        tint = this.tintFromFakePlacedBlock(blockState, tempBlockPos, (byte)4);
                    }
                }

                if (tint != 16777215 && tint != -1) {
                    int blockStateID = BlockRepository.getStateId(blockState);
                    this.biomeTintsAvailable.add(blockStateID);
                    this.blockColorsWithDefaultTint[blockStateID] = ColorUtils.colorMultiplier(color, tint);
                } else {
                    this.blockColorsWithDefaultTint[BlockRepository.getStateId(blockState)] = 452984832;
                }
            }
        } catch (Exception var9) {
        }

    }

    private int tintFromFakePlacedBlock(class_2680 blockState, MutableBlockPos loopBlockPos, byte biomeID) {
        class_638 world = this.game.field_1687;
        if (world == null) {
            return -1;
        } else if (blockState.method_26204() == null) {
            return -1;
        } else {
            int tint = -1;
            return tint;
        }
    }

    public int getBiomeTint(AbstractMapData mapData, class_1937 world, class_2680 blockState, int blockStateID, MutableBlockPos blockPos, MutableBlockPos loopBlockPos, int startX, int startZ) {
        class_2791 chunk = world.method_22350(blockPos);
        boolean live = chunk != null && !((class_2818)chunk).method_12223() && this.game.field_1687.method_8393(blockPos.method_10263() >> 4, blockPos.method_10260() >> 4);
        live = live && this.game.field_1687.method_22340(blockPos);
        int tint = -2;
        if (this.optifineInstalled || !live && this.biomeTintsAvailable.contains(blockStateID)) {
            try {
                int[][] tints = (int[][])this.blockTintTables.get(blockStateID);
                if (tints != null) {
                    int r = 0;
                    int g = 0;
                    int b = 0;

                    for(int t = blockPos.method_10263() - 1; t <= blockPos.method_10263() + 1; ++t) {
                        for(int s = blockPos.method_10260() - 1; s <= blockPos.method_10260() + 1; ++s) {
                            int biomeID = false;
                            int biomeTint;
                            int biomeID;
                            if (live) {
                                biomeID = world.method_30349().method_30530(class_2378.field_25114).method_10206((class_1959)world.method_23753(loopBlockPos.withXYZ(t, blockPos.method_10264(), s)).comp_349());
                            } else {
                                biomeTint = t - startX;
                                int dataZ = s - startZ;
                                biomeTint = Math.max(biomeTint, 0);
                                biomeTint = Math.min(biomeTint, mapData.getWidth() - 1);
                                dataZ = Math.max(dataZ, 0);
                                dataZ = Math.min(dataZ, mapData.getHeight() - 1);
                                biomeID = mapData.getBiomeID(biomeTint, dataZ);
                            }

                            if (biomeID < 0) {
                                biomeID = 1;
                            }

                            biomeTint = tints[biomeID][loopBlockPos.y / 8];
                            r += (biomeTint & 16711680) >> 16;
                            g += (biomeTint & '\uff00') >> 8;
                            b += biomeTint & 255;
                        }
                    }

                    tint = -16777216 | (r / 9 & 255) << 16 | (g / 9 & 255) << 8 | b / 9 & 255;
                }
            } catch (Exception var21) {
                tint = -2;
            }
        }

        if (tint == -2) {
            tint = this.getBuiltInBiomeTint(mapData, world, blockState, blockStateID, blockPos, loopBlockPos, startX, startZ, live);
        }

        return tint;
    }

    private int getBuiltInBiomeTint(AbstractMapData mapData, class_1937 world, class_2680 blockState, int blockStateID, MutableBlockPos blockPos, MutableBlockPos loopBlockPos, int startX, int startZ, boolean live) {
        int tint = -1;
        class_2248 block = blockState.method_26204();
        if (BlockRepository.biomeBlocks.contains(block) || this.biomeTintsAvailable.contains(blockStateID)) {
            if (live) {
                try {
                    tint = this.game.method_1505().method_1697(blockState, world, blockPos, 0) | -16777216;
                } catch (Exception var13) {
                }
            }

            if (tint == -1) {
                tint = this.getBuiltInBiomeTintFromUnloadedChunk(mapData, world, blockState, blockStateID, blockPos, loopBlockPos, startX, startZ) | -16777216;
            }
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

            for(int t = blockPos.method_10263() - 1; t <= blockPos.method_10263() + 1; ++t) {
                for(int s = blockPos.method_10260() - 1; s <= blockPos.method_10260() + 1; ++s) {
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
                        class_2680 var10000 = mapData.getBlockstate(dataX, dataZ);
                        MessageUtils.printDebug("block: " + var10000 + ", height: " + mapData.getHeight(dataX, dataZ));
                        MessageUtils.printDebug("Mapdata: " + mapData.toString());
                        biome = BiomeRepository.FOREST;
                    }

                    int biomeTint = colorResolver.getColorAtPos(blockState, biome, loopBlockPos.withXYZ(t, blockPos.method_10264(), s));
                    r += (biomeTint & 16711680) >> 16;
                    g += (biomeTint & '\uff00') >> 8;
                    b += biomeTint & 255;
                }
            }

            tint = (r / 9 & 255) << 16 | (g / 9 & 255) << 8 | b / 9 & 255;
        } else if (this.biomeTintsAvailable.contains(blockStateID)) {
            tint = this.getCustomBlockBiomeTintFromUnloadedChunk(mapData, world, blockState, blockPos, loopBlockPos, startX, startZ);
        }

        return tint;
    }

    private int getCustomBlockBiomeTintFromUnloadedChunk(AbstractMapData mapData, class_1937 world, class_2680 blockState, MutableBlockPos blockPos, MutableBlockPos loopBlockPos, int startX, int startZ) {
        int tint = true;

        int tint;
        try {
            int dataX = blockPos.method_10263() - startX;
            int dataZ = blockPos.method_10260() - startZ;
            dataX = Math.max(dataX, 0);
            dataX = Math.min(dataX, mapData.getWidth() - 1);
            dataZ = Math.max(dataZ, 0);
            dataZ = Math.min(dataZ, mapData.getHeight() - 1);
            byte biomeID = (byte)mapData.getBiomeID(dataX, dataZ);
            tint = this.tintFromFakePlacedBlock(blockState, loopBlockPos, biomeID);
        } catch (Exception var12) {
            tint = -1;
        }

        return tint;
    }

    private int applyShape(class_2248 block, int color) {
        int alpha = color >> 24 & 255;
        int red = color >> 16 & 255;
        int green = color >> 8 & 255;
        int blue = color >> 0 & 255;
        if (block instanceof class_2478) {
            alpha = 31;
        } else if (block instanceof class_2323) {
            alpha = 47;
        } else if (block == BlockRepository.ladder || block == BlockRepository.vine) {
            alpha = 15;
        }

        return (alpha & 255) << 24 | (red & 255) << 16 | (green & 255) << 8 | blue & 255;
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
        Iterator var4 = this.findResources(namespace, "/optifine/ctm", ".properties", true, false, true).iterator();

        while(var4.hasNext()) {
            class_2960 s = (class_2960)var4.next();

            try {
                this.loadCTM(s);
            } catch (NumberFormatException var7) {
            } catch (IllegalArgumentException var8) {
            }
        }

        for(int t = 0; t < this.blockColors.length; ++t) {
            if (this.blockColors[t] != 452984832 && this.blockColors[t] != -16842497) {
                if ((this.blockColors[t] >> 24 & 255) < 27) {
                    int[] var10000 = this.blockColors;
                    var10000[t] |= 452984832;
                }

                this.checkForBiomeTinting(this.dummyBlockPos, BlockRepository.getStateById(t), this.blockColors[t]);
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
            Set<class_2680> blockStates = new HashSet();
            blockStates.addAll(this.parseBlocksList(matchBlocks, metadata));
            String directory = filePath.substring(0, filePath.lastIndexOf("/") + 1);
            String[] tilesParsed = this.parseStringList(tiles);
            String tilePath = directory + "0";
            if (tilesParsed.length > 0) {
                tilePath = tilesParsed[0].trim();
            }

            if (tilePath.startsWith("~")) {
                tilePath = tilePath.replace("~", "optifine");
            } else if (!tilePath.contains("/")) {
                tilePath = directory + tilePath;
            }

            if (!tilePath.toLowerCase().endsWith(".png")) {
                tilePath = tilePath + ".png";
            }

            String[] biomesArray = biomes.split(" ");
            Iterator var25;
            if (blockStates.size() == 0) {
                class_2248 block = null;
                Pattern pattern = Pattern.compile(".*/block_(.+).properties");
                Matcher matcher = pattern.matcher(filePath);
                if (matcher.find()) {
                    block = this.getBlockFromName(matcher.group(1));
                    if (block != null) {
                        Set<class_2680> matching = this.parseBlockMetadata(block, metadata);
                        if (matching.size() == 0) {
                            matching.addAll(block.method_9595().method_11662());
                        }

                        blockStates.addAll(matching);
                    }
                } else {
                    if (matchTiles.equals("")) {
                        matchTiles = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf(".properties"));
                    }

                    if (!matchTiles.contains(":")) {
                        matchTiles = "minecraft:blocks/" + matchTiles;
                    }

                    class_2960 matchID = new class_2960(matchTiles);
                    class_1058 compareIcon = (class_1058)this.game.method_1549(class_1059.field_5275).apply(matchID);
                    if (compareIcon.method_4598() != class_1047.method_4539()) {
                        ArrayList tmpList = new ArrayList();
                        var25 = class_2378.field_11146.iterator();

                        while(true) {
                            if (!var25.hasNext()) {
                                blockStates.addAll(tmpList);
                                break;
                            }

                            class_2248 testBlock = (class_2248)var25.next();
                            UnmodifiableIterator blockStateID = testBlock.method_9595().method_11662().iterator();

                            while(blockStateID.hasNext()) {
                                class_2680 blockState = (class_2680)blockStateID.next();

                                try {
                                    class_1087 bakedModel = blockModelShapes.method_3335(blockState);
                                    List quads = new ArrayList();
                                    quads.addAll(bakedModel.method_4707(blockState, class_2350.field_11036, this.random));
                                    quads.addAll(bakedModel.method_4707(blockState, (class_2350)null, this.random));
                                    BlockModel model = new BlockModel(quads, this.failedToLoadX, this.failedToLoadY);
                                    if (model.numberOfFaces() > 0) {
                                        ArrayList blockFaces = model.getFaces();

                                        for(int i = 0; i < blockFaces.size(); ++i) {
                                            BlockModel.BlockFace face = (BlockModel.BlockFace)model.getFaces().get(i);
                                            float minU = face.getMinU();
                                            float maxU = face.getMaxU();
                                            float minV = face.getMinV();
                                            float maxV = face.getMaxV();
                                            if (this.similarEnough(minU, maxU, minV, maxV, compareIcon.method_4594(), compareIcon.method_4577(), compareIcon.method_4593(), compareIcon.method_4575())) {
                                                tmpList.add(blockState);
                                            }
                                        }
                                    }
                                } catch (Exception var41) {
                                }
                            }
                        }
                    }
                }
            }

            if (blockStates.size() != 0 && !method.equals("horizontal") && !method.startsWith("overlay") && (method.equals("sandstone") || method.equals("top") || faces.contains("top") || faces.contains("all") || faces.length() == 0)) {
                try {
                    class_2960 pngResource = new class_2960(propertiesFile.method_12836(), tilePath);
                    InputStream is = ((class_3298)this.game.method_1478().method_14486(pngResource).get()).method_14482();
                    Image top = ImageIO.read(is);
                    is.close();
                    Image top = top.getScaledInstance(1, 1, 4);
                    BufferedImage topBuff = new BufferedImage(top.getWidth((ImageObserver)null), top.getHeight((ImageObserver)null), 6);
                    Graphics gfx = topBuff.createGraphics();
                    gfx.drawImage(top, 0, 0, (ImageObserver)null);
                    gfx.dispose();
                    int topRGB = topBuff.getRGB(0, 0);
                    if ((topRGB >> 24 & 255) == 0) {
                        return;
                    }

                    var25 = blockStates.iterator();

                    while(true) {
                        while(var25.hasNext()) {
                            class_2680 blockState = (class_2680)var25.next();
                            topRGB = topBuff.getRGB(0, 0);
                            if (blockState.method_26204() == BlockRepository.cobweb) {
                                topRGB |= -16777216;
                            }

                            int blockStateID;
                            int r;
                            if (renderPass.equals("3")) {
                                topRGB = this.processRenderPassThree(topRGB);
                                blockStateID = BlockRepository.getStateId(blockState);
                                r = this.blockColors[blockStateID];
                                if (r != 452984832 && r != -16842497) {
                                    topRGB = ColorUtils.colorMultiplier(r, topRGB);
                                }
                            }

                            if (BlockRepository.shapedBlocks.contains(blockState.method_26204())) {
                                topRGB = this.applyShape(blockState.method_26204(), topRGB);
                            }

                            blockStateID = BlockRepository.getStateId(blockState);
                            if (!biomes.equals("")) {
                                this.biomeTextureAvailable.add(blockStateID);

                                for(r = 0; r < biomesArray.length; ++r) {
                                    int biomeInt = this.parseBiomeName(biomesArray[r]);
                                    if (biomeInt != -1) {
                                        this.blockBiomeSpecificColors.put("" + blockStateID + " " + biomeInt, topRGB);
                                    }
                                }
                            } else {
                                this.blockColors[blockStateID] = topRGB;
                            }
                        }

                        return;
                    }
                } catch (IOException var40) {
                    PrintStream var10000 = System.err;
                    String var10001 = propertiesFile.method_12832();
                    var10000.println("error getting CTM block from " + var10001 + ": " + filePath + " " + class_2378.field_11146.method_10221(((class_2680)blockStates.iterator().next()).method_26204()).toString() + " " + tilePath);
                    var40.printStackTrace();
                }
            }
        }

    }

    private boolean similarEnough(float a, float b, float c, float d, float one, float two, float three, float four) {
        boolean similar = (double)Math.abs(a - one) < 1.0E-4;
        similar = similar && (double)Math.abs(b - two) < 1.0E-4;
        similar = similar && (double)Math.abs(c - three) < 1.0E-4;
        return similar && (double)Math.abs(d - four) < 1.0E-4;
    }

    private int processRenderPassThree(int rgb) {
        if (this.renderPassThreeBlendMode.equals("color") || this.renderPassThreeBlendMode.equals("overlay")) {
            int red = rgb >> 16 & 255;
            int green = rgb >> 8 & 255;
            int blue = rgb >> 0 & 255;
            float colorAverage = (float)(red + blue + green) / 3.0F;
            float lighteningFactor = (colorAverage - 127.5F) * 2.0F;
            red += (int)((float)red * (lighteningFactor / 255.0F));
            blue += (int)((float)red * (lighteningFactor / 255.0F));
            green += (int)((float)red * (lighteningFactor / 255.0F));
            int newAlpha = (int)Math.abs(lighteningFactor);
            rgb = newAlpha << 24 | (red & 255) << 16 | (green & 255) << 8 | blue & 255;
        }

        return rgb;
    }

    private String[] parseStringList(String list) {
        ArrayList tmpList = new ArrayList();
        String[] a = list.split("\\s+");
        int i = a.length;

        for(int var5 = 0; var5 < i; ++var5) {
            String token = a[var5];
            token = token.trim();

            try {
                if (token.matches("^\\d+$")) {
                    tmpList.add("" + Integer.parseInt(token));
                } else if (token.matches("^\\d+-\\d+$")) {
                    String[] t = token.split("-");
                    int min = Integer.parseInt(t[0]);
                    int max = Integer.parseInt(t[1]);

                    for(int i = min; i <= max; ++i) {
                        tmpList.add("" + i);
                    }
                } else if (token != null && token != "") {
                    tmpList.add(token);
                }
            } catch (NumberFormatException var11) {
            }
        }

        a = new String[tmpList.size()];

        for(i = 0; i < a.length; ++i) {
            a[i] = (String)tmpList.get(i);
        }

        return a;
    }

    private Set parseBlocksList(String blocks, String metadataLine) {
        Set blockStates = new HashSet();
        String[] var4 = blocks.split("\\s+");
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            String blockString = var4[var6];
            String metadata = metadataLine;
            blockString = blockString.trim();
            String[] blockComponents = blockString.split(":");
            int tokensUsed = 0;
            class_2248 block = null;
            block = this.getBlockFromName(blockComponents[0]);
            if (block != null) {
                tokensUsed = 1;
            } else if (blockComponents.length > 1) {
                block = this.getBlockFromName(blockComponents[0] + ":" + blockComponents[1]);
                if (block != null) {
                    tokensUsed = 2;
                }
            }

            if (block != null) {
                if (blockComponents.length > tokensUsed) {
                    metadata = blockComponents[tokensUsed];

                    for(int t = tokensUsed + 1; t < blockComponents.length; ++t) {
                        metadata = metadata + ":" + blockComponents[t];
                    }
                }

                blockStates.addAll(this.parseBlockMetadata(block, metadata));
            }
        }

        return blockStates;
    }

    private Set parseBlockMetadata(class_2248 block, String metadataList) {
        Set blockStates = new HashSet();
        if (metadataList.equals("")) {
            blockStates.addAll(block.method_9595().method_11662());
        } else {
            Set<String> valuePairs = new HashSet();
            String[] var5 = metadataList.split(":");
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                String metadata = var5[var7];
                metadata.trim();
                if (metadata.contains("=")) {
                    valuePairs.add(metadata);
                }
            }

            if (valuePairs.size() > 0) {
                UnmodifiableIterator var22 = block.method_9595().method_11662().iterator();

                label78:
                while(var22.hasNext()) {
                    class_2680 blockState = (class_2680)var22.next();
                    boolean matches = true;
                    Iterator var25 = valuePairs.iterator();

                    while(true) {
                        String[] propertyAndValues;
                        class_2769 property;
                        do {
                            do {
                                if (!var25.hasNext()) {
                                    if (matches) {
                                        blockStates.add(blockState);
                                    }
                                    continue label78;
                                }

                                String pair = (String)var25.next();
                                propertyAndValues = pair.split("\\s*=\\s*", 5);
                            } while(propertyAndValues.length != 2);

                            property = block.method_9595().method_11663(propertyAndValues[0]);
                        } while(property == null);

                        boolean valueIncluded = false;
                        String[] values = propertyAndValues[1].split(",");
                        String[] var14 = values;
                        int var15 = values.length;

                        for(int var16 = 0; var16 < var15; ++var16) {
                            String value = var14[var16];
                            if (property.method_11902() == Integer.class && value.matches("^\\d+-\\d+$")) {
                                String[] range = value.split("-");
                                int min = Integer.parseInt(range[0]);
                                int max = Integer.parseInt(range[1]);
                                int intValue = (Integer)Integer.class.cast(blockState.method_11654(property));
                                if (intValue >= min && intValue <= max) {
                                    valueIncluded = true;
                                }
                            } else if (!blockState.method_11654(property).equals(property.method_11900(value))) {
                                valueIncluded = true;
                            }
                        }

                        matches = matches && valueIncluded;
                    }
                }
            }
        }

        return blockStates;
    }

    private int parseBiomeName(String name) {
        class_1959 biome = (class_1959)this.world.method_30349().method_30530(class_2378.field_25114).method_10223(new class_2960(name));
        return biome != null ? this.world.method_30349().method_30530(class_2378.field_25114).method_10206(biome) : -1;
    }

    private List<class_2960> findResources(String namespace, String directory, String suffixMaybeNull, boolean recursive, boolean directories, boolean sortByFilename) {
        if (directory == null) {
            directory = "";
        }

        if (directory.startsWith("/")) {
            directory = directory.substring(1);
        }

        String suffix = suffixMaybeNull == null ? "" : suffixMaybeNull;
        ArrayList<class_2960> resources = new ArrayList();
        Map<class_2960, class_3298> resourceMap = this.game.method_1478().method_14488(directory, (asset) -> {
            return asset.method_12832().endsWith(suffix);
        });
        Iterator var10 = resourceMap.keySet().iterator();

        while(var10.hasNext()) {
            class_2960 candidate = (class_2960)var10.next();
            if (candidate.method_12836().equals(namespace)) {
                resources.add(candidate);
            }
        }

        if (sortByFilename) {
            resources.sort((o1, o2) -> {
                String f1 = o1.method_12832().replaceAll(".*/", "").replaceFirst("\\.properties", "");
                String f2 = o2.method_12832().replaceAll(".*/", "").replaceFirst("\\.properties", "");
                int result = f1.compareTo(f2);
                return result != 0 ? result : o1.method_12832().compareTo(o2.method_12832());
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
        } catch (IOException var20) {
        }

        class_2680 blockState = BlockRepository.lilypad.method_9564();
        int blockStateID = BlockRepository.getStateId(blockState);
        int lilyRGB = this.getBlockColor(blockStateID);
        int lilypadMultiplier = 2129968;
        String lilypadMultiplierString = properties.getProperty("lilypad");
        if (lilypadMultiplierString != null) {
            lilypadMultiplier = Integer.parseInt(lilypadMultiplierString, 16);
        }

        for(UnmodifiableIterator<class_2680> defaultFormat = BlockRepository.lilypad.method_9595().method_11662().iterator(); defaultFormat.hasNext(); this.blockColorsWithDefaultTint[blockStateID] = this.blockColors[blockStateID]) {
            class_2680 padBlockState = (class_2680)defaultFormat.next();
            blockStateID = BlockRepository.getStateId(padBlockState);
            this.blockColors[blockStateID] = ColorUtils.colorMultiplier(lilyRGB, lilypadMultiplier | -16777216);
        }

        String defaultFormat = properties.getProperty("palette.format");
        boolean globalGrid = defaultFormat != null && defaultFormat.equalsIgnoreCase("grid");
        Enumeration<?> e = properties.propertyNames();

        while(e.hasMoreElements()) {
            String key = (String)e.nextElement();
            if (key.startsWith("palette.block")) {
                String filename = key.substring("palette.block.".length());
                filename = filename.replace("~", "optifine");
                this.processColorPropertyHelper(new class_2960(filename), properties.getProperty(key), globalGrid);
            }
        }

        class_2960 resourcePNG;
        boolean grid;
        int yOffset;
        String names;
        for(Iterator var25 = this.findResources("minecraft", "/optifine/colormap/blocks", ".properties", true, false, true).iterator(); var25.hasNext(); this.processColorProperty(resourcePNG, names, grid, yOffset)) {
            class_2960 resource = (class_2960)var25.next();
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

            names = colorProperties.getProperty("blocks");
            String source;
            if (names == null) {
                source = resource.method_12832();
                source = source.substring(source.lastIndexOf("/") + 1, source.lastIndexOf(".properties"));
                names = source;
            }

            source = colorProperties.getProperty("source");
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
            yOffset = 0;
            if (yOffsetString != null) {
                yOffset = Integer.parseInt(yOffsetString);
            }
        }

        this.processColorPropertyHelper(new class_2960("optifine/colormap/water.png"), "water", globalGrid);
        this.processColorPropertyHelper(new class_2960("optifine/colormap/watercolorx.png"), "water", globalGrid);
        this.processColorPropertyHelper(new class_2960("optifine/colormap/swampgrass.png"), "grass_block grass fern tall_grass large_fern", globalGrid);
        this.processColorPropertyHelper(new class_2960("optifine/colormap/swampgrasscolor.png"), "grass_block grass fern tall_grass large_fern", globalGrid);
        this.processColorPropertyHelper(new class_2960("optifine/colormap/swampfoliage.png"), "oak_leaves vine", globalGrid);
        this.processColorPropertyHelper(new class_2960("optifine/colormap/swampfoliagecolor.png"), "oak_leaves vine", globalGrid);
        this.processColorPropertyHelper(new class_2960("optifine/colormap/pine.png"), "spruce_leaves", globalGrid);
        this.processColorPropertyHelper(new class_2960("optifine/colormap/pinecolor.png"), "spruce_leaves", globalGrid);
        this.processColorPropertyHelper(new class_2960("optifine/colormap/birch.png"), "birch_leaves", globalGrid);
        this.processColorPropertyHelper(new class_2960("optifine/colormap/birchcolor.png"), "birch_leaves", globalGrid);
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
            if (format != null) {
                grid = format.equalsIgnoreCase("grid");
            }

            String yOffsetString = colorProperties.getProperty("yOffset");
            if (yOffsetString != null) {
                yOffset = Integer.valueOf(yOffsetString);
            }
        } catch (IOException var10) {
        }

        this.processColorProperty(resource, list, grid, yOffset);
    }

    private void processColorProperty(class_2960 resource, String list, boolean grid, int yOffset) {
        int[][] tints = new int[this.sizeOfBiomeArray][32];
        int[][] var6 = tints;
        int var7 = tints.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            int[] row = var6[var8];
            Arrays.fill(row, -1);
        }

        boolean swamp = resource.method_12832().contains("/swamp");

        BufferedImage tintColors;
        try {
            InputStream is = ((class_3298)this.game.method_1478().method_14486(resource).get()).method_14482();
            tintColors = ImageIO.read(is);
            is.close();
        } catch (IOException var20) {
            return;
        }

        BufferedImage tintColorsBuff = new BufferedImage(tintColors.getWidth((ImageObserver)null), tintColors.getHeight((ImageObserver)null), 1);
        Graphics gfx = tintColorsBuff.createGraphics();
        gfx.drawImage(tintColors, 0, 0, (ImageObserver)null);
        gfx.dispose();
        int numBiomesToCheck = grid ? Math.min(tintColorsBuff.getWidth(), this.sizeOfBiomeArray) : this.sizeOfBiomeArray;

        int blockStateID;
        for(int t = 0; t < numBiomesToCheck; ++t) {
            class_1959 biome = (class_1959)this.world.method_30349().method_30530(class_2378.field_25114).method_10200(t);
            if (biome != null) {
                int tintMult = false;
                blockStateID = tintColorsBuff.getHeight() / 32;

                for(int s = 0; s < 32; ++s) {
                    int tintMult;
                    if (grid) {
                        tintMult = tintColorsBuff.getRGB(t, Math.max(0, s * blockStateID - yOffset)) & 16777215;
                    } else {
                        double var1 = (double)class_3532.method_15363(biome.method_8712(), 0.0F, 1.0F);
                        double var2 = (double)class_3532.method_15363(biome.method_8715(), 0.0F, 1.0F);
                        var2 *= var1;
                        var1 = 1.0 - var1;
                        var2 = 1.0 - var2;
                        tintMult = tintColorsBuff.getRGB((int)((double)(tintColorsBuff.getWidth() - 1) * var1), (int)((double)(tintColorsBuff.getHeight() - 1) * var2)) & 16777215;
                    }

                    if (tintMult != 0 && (!swamp || biome == BiomeRepository.SWAMP || biome == BiomeRepository.SWAMP_HILLS)) {
                        tints[t][s] = tintMult;
                    }
                }
            }
        }

        Set<class_2680> blockStates = new HashSet();
        blockStates.addAll(this.parseBlocksList(list, ""));
        Iterator var27 = blockStates.iterator();

        while(var27.hasNext()) {
            class_2680 blockState = (class_2680)var27.next();
            blockStateID = BlockRepository.getStateId(blockState);
            int[][] previousTints = (int[][])this.blockTintTables.get(blockStateID);
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
                this.processColorProperty(defaultResource, stateString, false, 0);
                previousTints = (int[][])this.blockTintTables.get(blockStateID);
            }

            if (previousTints != null) {
                for(int t = 0; t < this.sizeOfBiomeArray; ++t) {
                    for(int s = 0; s < 32; ++s) {
                        if (tints[t][s] == -1) {
                            tints[t][s] = previousTints[t][s];
                        }
                    }
                }
            }

            this.blockColorsWithDefaultTint[blockStateID] = ColorUtils.colorMultiplier(this.getBlockColor(blockStateID), tints[4][8] | -16777216);
            this.blockTintTables.put(blockStateID, tints);
            this.biomeTintsAvailable.add(blockStateID);
        }

    }

    private class_2248 getBlockFromName(String name) {
        try {
            class_2960 resourceLocation = new class_2960(name);
            return class_2378.field_11146.method_10250(resourceLocation) ? (class_2248)class_2378.field_11146.method_10223(resourceLocation) : null;
        } catch (NumberFormatException | class_151 var3) {
            return null;
        }
    }

    private interface ColorResolver {
        int getColorAtPos(class_2680 var1, class_1959 var2, class_2338 var3);
    }
}
