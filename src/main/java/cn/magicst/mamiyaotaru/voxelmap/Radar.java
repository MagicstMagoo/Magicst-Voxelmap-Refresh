package cn.magicst.mamiyaotaru.voxelmap;

import com.google.common.collect.Maps;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.IRadar;
import cn.magicst.mamiyaotaru.voxelmap.interfaces.IVoxelMap;
import cn.magicst.mamiyaotaru.voxelmap.textures.FontRendererWithAtlas;
import cn.magicst.mamiyaotaru.voxelmap.textures.Sprite;
import cn.magicst.mamiyaotaru.voxelmap.textures.StitcherException;
import cn.magicst.mamiyaotaru.voxelmap.textures.TextureAtlas;
import cn.magicst.mamiyaotaru.voxelmap.util.Contact;
import cn.magicst.mamiyaotaru.voxelmap.util.CustomMob;
import cn.magicst.mamiyaotaru.voxelmap.util.CustomMobsManager;
import cn.magicst.mamiyaotaru.voxelmap.util.EnumMobs;
import cn.magicst.mamiyaotaru.voxelmap.util.GLShim;
import cn.magicst.mamiyaotaru.voxelmap.util.GLUtils;
import cn.magicst.mamiyaotaru.voxelmap.util.GameVariableAccessShim;
import cn.magicst.mamiyaotaru.voxelmap.util.ImageUtils;
import cn.magicst.mamiyaotaru.voxelmap.util.LayoutVariables;
import cn.magicst.mamiyaotaru.voxelmap.util.ReflectionUtils;
import cn.magicst.mamiyaotaru.voxelmap.util.TextUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.StreamSupport;
import javax.imageio.ImageIO;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1044;
import net.minecraft.class_1046;
import net.minecraft.class_1068;
import net.minecraft.class_1159;
import net.minecraft.class_1160;
import net.minecraft.class_1162;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1321;
import net.minecraft.class_1454;
import net.minecraft.class_1456;
import net.minecraft.class_1463;
import net.minecraft.class_1472;
import net.minecraft.class_1474;
import net.minecraft.class_1493;
import net.minecraft.class_156;
import net.minecraft.class_1590;
import net.minecraft.class_1657;
import net.minecraft.class_1664;
import net.minecraft.class_1738;
import net.minecraft.class_1747;
import net.minecraft.class_1767;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2248;
import net.minecraft.class_2350;
import net.minecraft.class_2487;
import net.minecraft.class_2512;
import net.minecraft.class_2520;
import net.minecraft.class_2631;
import net.minecraft.class_2680;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3298;
import net.minecraft.class_3300;
import net.minecraft.class_3879;
import net.minecraft.class_3888;
import net.minecraft.class_4057;
import net.minecraft.class_4466;
import net.minecraft.class_4495;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_4791;
import net.minecraft.class_4997;
import net.minecraft.class_5148;
import net.minecraft.class_549;
import net.minecraft.class_553;
import net.minecraft.class_555;
import net.minecraft.class_558;
import net.minecraft.class_5597;
import net.minecraft.class_5605;
import net.minecraft.class_5607;
import net.minecraft.class_562;
import net.minecraft.class_564;
import net.minecraft.class_565;
import net.minecraft.class_567;
import net.minecraft.class_570;
import net.minecraft.class_571;
import net.minecraft.class_572;
import net.minecraft.class_574;
import net.minecraft.class_575;
import net.minecraft.class_576;
import net.minecraft.class_5772;
import net.minecraft.class_582;
import net.minecraft.class_583;
import net.minecraft.class_588;
import net.minecraft.class_591;
import net.minecraft.class_596;
import net.minecraft.class_597;
import net.minecraft.class_602;
import net.minecraft.class_604;
import net.minecraft.class_606;
import net.minecraft.class_607;
import net.minecraft.class_608;
import net.minecraft.class_609;
import net.minecraft.class_610;
import net.minecraft.class_611;
import net.minecraft.class_620;
import net.minecraft.class_624;
import net.minecraft.class_630;
import net.minecraft.class_742;
import net.minecraft.class_757;
import net.minecraft.class_889;
import net.minecraft.class_897;
import net.minecraft.class_922;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Radar implements IRadar {
    private class_310 game;

    private IVoxelMap master = null;

    private LayoutVariables layoutVariables = null;

    public MapSettingsManager minimapOptions = null;

    public RadarSettingsManager options = null;

    private FontRendererWithAtlas fontRenderer;

    private TextureAtlas textureAtlas;

    private boolean newMobs = false;

    private boolean enabled = true;

    private boolean completedLoading = false;

    private int timer = 500;

    private float direction = 0.0F;

    private ArrayList<Contact> contacts = new ArrayList<>(40);

    public HashMap mpContactsSkinGetTries = new HashMap<>();

    public HashMap contactsSkinGetTries = new HashMap<>();

    private Sprite clothIcon = null;

    private static final int CLOTH = 0;

    private static final int UNKNOWN = EnumMobs.UNKNOWN.ordinal();

    private String[] armorNames = new String[] { "cloth", "clothOverlay", "clothOuter", "clothOverlayOuter", "chain", "iron", "gold", "diamond", "netherite", "turtle" };

    private final float iconScale = 4.0F;

    private boolean randomobsOptifine = false;

    private Class randomEntitiesClass = null;

    private Field mapPropertiesField = null;

    private Map mapProperties = null;

    private Field randomEntityField = null;

    private Object randomEntity = null;

    private Class iRandomEntityClass = null;

    private Class randomEntityClass = null;

    private Method setEntityMethod = null;

    private Class randomEntitiesPropertiesClass = null;

    private Method getEntityTextureMethod = null;

    private boolean hasCustomNPCs = false;

    private Class entityCustomNpcClass = null;

    private Class modelDataClass = null;

    private Class entityNPCInterfaceClass = null;

    private Field modelDataField = null;

    private Method getEntityMethod = null;

    private boolean lastOutlines = true;

    UUID devUUID = UUID.fromString("9b37abb9-2487-4712-bb96-21a1e0b2023c");

    private class_607 playerSkullModel;

    private class_572 bipedArmorModel;

    private class_606 strayOverlayModel;

    private class_564 drownedOverlayModel;

    private class_572 piglinArmorModel;

    private class_1043 nativeBackedTexture = new class_1043(2, 2, false);

    private final class_2960 nativeBackedTextureLocation = new class_2960("voxelmap", "tempimage");

    private final class_1160 fullbright = new class_1160(1.0F, 1.0F, 1.0F);

    private final Logger logger = LogManager.getLogger();

    private static final Int2ObjectMap LEVEL_TO_ID;

    private static final Map TEXTURES;

    static {
        LEVEL_TO_ID = (Int2ObjectMap)class_156.method_654(new Int2ObjectOpenHashMap(), int2ObjectOpenHashMap -> {
            int2ObjectOpenHashMap.put(1, new class_2960("stone"));
            int2ObjectOpenHashMap.put(2, new class_2960("iron"));
            int2ObjectOpenHashMap.put(3, new class_2960("gold"));
            int2ObjectOpenHashMap.put(4, new class_2960("emerald"));
            int2ObjectOpenHashMap.put(5, new class_2960("diamond"));
        });
        TEXTURES = (Map)class_156.method_654(Maps.newEnumMap(class_5148.class), enumMap -> {
            enumMap.put(class_5148.field_23808, null);
            enumMap.put(class_5148.field_23809, new class_2960("textures/entity/horse/horse_markings_white.png"));
            enumMap.put(class_5148.field_23810, new class_2960("textures/entity/horse/horse_markings_whitefield.png"));
            enumMap.put(class_5148.field_23811, new class_2960("textures/entity/horse/horse_markings_whitedots.png"));
            enumMap.put(class_5148.field_23812, new class_2960("textures/entity/horse/horse_markings_blackdots.png"));
        });
    }

    public Radar(IVoxelMap master) {
        this.master = master;
        this.minimapOptions = master.getMapOptions();
        this.options = master.getRadarOptions();
        this.game = class_310.method_1551();
        this.fontRenderer = new FontRendererWithAtlas(this.game.method_1531(), new class_2960("textures/font/ascii.png"));
        this.textureAtlas = new TextureAtlas("mobs");
        this.textureAtlas.method_4527(false, false);
        try {
            this.randomEntitiesClass = Class.forName("net.optifine.RandomEntities");
            this.mapPropertiesField = this.randomEntitiesClass.getDeclaredField("mapProperties");
            this.mapPropertiesField.setAccessible(true);
            this.mapProperties = (Map)this.mapPropertiesField.get(null);
            this.randomEntityField = this.randomEntitiesClass.getDeclaredField("randomEntity");
            this.randomEntityField.setAccessible(true);
            this.randomEntity = this.randomEntityField.get(null);
            this.iRandomEntityClass = Class.forName("net.optifine.IRandomEntity");
            this.randomEntityClass = Class.forName("net.optifine.RandomEntity");
            Class[] argClasses1 = { class_1297.class };
            this.setEntityMethod = this.randomEntityClass.getDeclaredMethod("setEntity", argClasses1);
            this.randomEntitiesPropertiesClass = Class.forName("net.optifine.RandomEntityProperties");
            Class[] argClasses2 = { class_2960.class, this.iRandomEntityClass };
            this.getEntityTextureMethod = this.randomEntitiesPropertiesClass.getDeclaredMethod("getTextureLocation", argClasses2);
            this.randomobsOptifine = true;
        } catch (ClassNotFoundException var7) {
            this.randomobsOptifine = false;
        } catch (NoSuchMethodException var8) {
            this.randomobsOptifine = false;
        } catch (NoSuchFieldException var9) {
            this.randomobsOptifine = false;
        } catch (SecurityException var10) {
            this.randomobsOptifine = false;
        } catch (IllegalArgumentException var11) {
            this.randomobsOptifine = false;
        } catch (IllegalAccessException var12) {
            this.randomobsOptifine = false;
        }
        try {
            this.entityCustomNpcClass = Class.forName("noppes.npcs.entity.EntityCustomNpc");
            this.modelDataClass = Class.forName("noppes.npcs.ModelData");
            this.modelDataField = this.entityCustomNpcClass.getField("modelData");
            this.entityNPCInterfaceClass = Class.forName("noppes.npcs.entity.EntityNPCInterface");
            this.getEntityMethod = this.modelDataClass.getMethod("getEntity", new Class[] { this.entityNPCInterfaceClass });
            this.hasCustomNPCs = true;
        } catch (ClassNotFoundException var4) {
            this.hasCustomNPCs = false;
        } catch (NoSuchFieldException var5) {
            this.hasCustomNPCs = false;
        } catch (NoSuchMethodException var6) {
            this.hasCustomNPCs = false;
        }
    }

    public void onResourceManagerReload(class_3300 resourceManager) {
        loadTexturePackIcons();
        this.fontRenderer.onResourceManagerReload(resourceManager);
    }

    private void loadTexturePackIcons() {
        this.completedLoading = false;
        try {
            this.mpContactsSkinGetTries.clear();
            this.contactsSkinGetTries.clear();
            this.textureAtlas.reset();
            class_5607 texturedModelData12 = class_607.method_32049();
            class_630 skullModelPart = texturedModelData12.method_32109();
            this.playerSkullModel = new class_607(skullModelPart);
            class_5605 ARMOR_DILATION = new class_5605(1.0F);
            class_5607 texturedModelData2 = class_5607.method_32110(class_572.method_32011(ARMOR_DILATION, 0.0F), 64, 32);
            class_630 bipedArmorModelPart = texturedModelData2.method_32109();
            this.bipedArmorModel = new class_572(bipedArmorModelPart);
            class_5607 strayModelData = class_5607.method_32110(class_572.method_32011(new class_5605(0.25F), 0.0F), 64, 32);
            class_630 strayOverlayModelPart = strayModelData.method_32109();
            this.strayOverlayModel = new class_606(strayOverlayModelPart);
            class_5607 drownedModelData = class_564.method_31993(new class_5605(0.25F));
            class_630 drownedOverlayModelPart = drownedModelData.method_32109();
            this.drownedOverlayModel = new class_564(drownedOverlayModelPart);
            class_5607 texturedModelData3 = class_5607.method_32110(class_572.method_32011(new class_5605(1.02F), 0.0F), 64, 32);
            class_630 piglinArmorModelPart = texturedModelData3.method_32109();
            this.piglinArmorModel = new class_572(piglinArmorModelPart);
            if (ReflectionUtils.classExists("com.prupe.mcpatcher.mob.MobOverlay") && ImageUtils.loadImage(new class_2960("mcpatcher/mob/cow/mooshroom_overlay.png"), 0, 0, 1, 1) != null) {
                EnumMobs.MOOSHROOM.secondaryResourceLocation = new class_2960("mcpatcher/mob/cow/mooshroom_overlay.png");
            } else {
                EnumMobs.MOOSHROOM.secondaryResourceLocation = new class_2960("textures/block/red_mushroom.png");
            }
            for (int t = 0; t < (EnumMobs.values()).length - 1; t++) {
                String identifier = "minecraft." + (EnumMobs.values()[t]).id;
                String identifierSimple = (EnumMobs.values()[t]).id;
                String spriteName = identifier + identifier;
                spriteName = spriteName + spriteName;
                BufferedImage mobImage = getCustomMobImage(identifier, identifierSimple);
                if (mobImage != null) {
                    Sprite sprite = this.textureAtlas.registerIconForBufferedImage(identifier + "custom", mobImage);
                    this.textureAtlas.registerMaskedIcon(spriteName, sprite);
                } else {
                    this.textureAtlas.registerFailedIcon(identifier + "custom");
                    if ((EnumMobs.values()[t]).expectedWidth > 0.5D) {
                        mobImage = createImageFromTypeAndResourceLocations(EnumMobs.values()[t], (EnumMobs.values()[t]).resourceLocation, (EnumMobs.values()[t]).secondaryResourceLocation, (class_1297)null);
                        if (mobImage != null) {
                            float f = mobImage.getWidth() / (EnumMobs.values()[t]).expectedWidth;
                            mobImage = ImageUtils.fillOutline(ImageUtils.pad(ImageUtils.scaleImage(mobImage, 4.0F / f)), this.options.outlines, 2);
                            this.textureAtlas.registerIconForBufferedImage(spriteName, mobImage);
                        }
                    }
                }
            }
            BufferedImage[] armorImages = { ImageUtils.loadImage(new class_2960("textures/models/armor/leather_layer_1.png"), 8, 8, 8, 8), ImageUtils.loadImage(new class_2960("textures/models/armor/leather_layer_1.png"), 40, 8, 8, 8), ImageUtils.loadImage(new class_2960("textures/models/armor/leather_layer_1_overlay.png"), 8, 8, 8, 8), ImageUtils.loadImage(new class_2960("textures/models/armor/leather_layer_1_overlay.png"), 40, 8, 8, 8) };
            for (int i = 0; i < armorImages.length; i++) {
                float f = armorImages[i].getWidth() / 8.0F;
                armorImages[i] = ImageUtils.fillOutline(ImageUtils.pad(ImageUtils.scaleImage(armorImages[i], 4.0F / f * 47.0F / 38.0F)), (this.options.outlines && i != 2 && i != 3), true, 37.6F, 37.6F, 2);
                Sprite icon = this.textureAtlas.registerIconForBufferedImage("armor " + this.armorNames[i], armorImages[i]);
                if (i == 0)
                    this.clothIcon = icon;
            }
            BufferedImage zombie = ImageUtils.loadImage(EnumMobs.ZOMBIE.resourceLocation, 8, 8, 8, 8, 64, 64);
            float scale = zombie.getWidth() / 8.0F;
            zombie = ImageUtils.scaleImage(zombie, 4.0F / scale * 47.0F / 38.0F);
            BufferedImage zombieHat = ImageUtils.loadImage(EnumMobs.ZOMBIE.resourceLocation, 40, 8, 8, 8, 64, 64);
            zombieHat = ImageUtils.scaleImage(zombieHat, 4.0F / scale * 47.0F / 35.0F);
            zombie = ImageUtils.addImages(ImageUtils.addImages(new BufferedImage(zombieHat.getWidth(), zombieHat.getHeight() + 8, 6), zombie, (zombieHat.getWidth() - zombie.getWidth()) / 2.0F, (zombieHat.getHeight() - zombie.getHeight()) / 2.0F, zombieHat.getWidth(), zombieHat.getHeight() + 8), zombieHat, 0.0F, 0.0F, zombieHat.getWidth(), zombieHat.getHeight() + 8);
            zombieHat.flush();
            zombie = ImageUtils.fillOutline(ImageUtils.pad(zombie), this.options.outlines, true, 37.6F, 37.6F, 2);
            this.textureAtlas.registerIconForBufferedImage("minecraft." + EnumMobs.ZOMBIE.id + EnumMobs.ZOMBIE.resourceLocation.toString() + "head", zombie);
            BufferedImage skeleton = ImageUtils.loadImage(EnumMobs.SKELETON.resourceLocation, 8, 8, 8, 8, 64, 32);
            scale = skeleton.getWidth() / 8.0F;
            skeleton = ImageUtils.scaleImage(skeleton, 4.0F / scale * 47.0F / 38.0F);
            skeleton = ImageUtils.addImages(new BufferedImage(skeleton.getWidth(), skeleton.getHeight() + 8, 6), skeleton, 0.0F, 0.0F, skeleton.getWidth(), skeleton.getHeight() + 8);
            skeleton = ImageUtils.fillOutline(ImageUtils.pad(skeleton), this.options.outlines, true, 37.6F, 37.6F, 2);
            this.textureAtlas.registerIconForBufferedImage("minecraft." + EnumMobs.SKELETON.id + EnumMobs.SKELETON.resourceLocation.toString() + "head", skeleton);
            BufferedImage witherSkeleton = ImageUtils.loadImage(EnumMobs.SKELETONWITHER.resourceLocation, 8, 8, 8, 8, 64, 32);
            scale = witherSkeleton.getWidth() / 8.0F;
            witherSkeleton = ImageUtils.scaleImage(witherSkeleton, 4.0F / scale * 47.0F / 38.0F);
            witherSkeleton = ImageUtils.addImages(new BufferedImage(witherSkeleton.getWidth(), witherSkeleton.getHeight() + 8, 6), witherSkeleton, 0.0F, 0.0F, witherSkeleton.getWidth(), witherSkeleton.getHeight() + 8);
            witherSkeleton = ImageUtils.fillOutline(ImageUtils.pad(witherSkeleton), this.options.outlines, true, 37.6F, 37.6F, 2);
            this.textureAtlas.registerIconForBufferedImage("minecraft." + EnumMobs.SKELETONWITHER.id + EnumMobs.SKELETONWITHER.resourceLocation.toString() + "head", witherSkeleton);
            BufferedImage creeper = ImageUtils.addImages(ImageUtils.blankImage(EnumMobs.CREEPER.resourceLocation, 8, 10), ImageUtils.loadImage(EnumMobs.CREEPER.resourceLocation, 8, 8, 8, 8), 0.0F, 0.0F, 8, 10);
            scale = creeper.getWidth() / EnumMobs.CREEPER.expectedWidth;
            creeper = ImageUtils.fillOutline(ImageUtils.pad(ImageUtils.scaleImage(creeper, 4.0F / scale * 47.0F / 38.0F)), this.options.outlines, true, 37.6F, 37.6F, 2);
            this.textureAtlas.registerIconForBufferedImage("minecraft." + EnumMobs.CREEPER.id + EnumMobs.CREEPER.resourceLocation.toString() + "head", creeper);
            BufferedImage dragon = createImageFromTypeAndResourceLocations(EnumMobs.ENDERDRAGON, EnumMobs.ENDERDRAGON.resourceLocation, (class_2960)null, (class_1297)null);
            scale = dragon.getWidth() / EnumMobs.ENDERDRAGON.expectedWidth;
            dragon = ImageUtils.fillOutline(ImageUtils.pad(ImageUtils.scaleImage(dragon, 4.0F / scale)), this.options.outlines, true, 32.0F, 32.0F, 2);
            this.textureAtlas.registerIconForBufferedImage("minecraft." + EnumMobs.ENDERDRAGON.id + EnumMobs.ENDERDRAGON.resourceLocation.toString() + "head", dragon);
            BufferedImage sheepFur = ImageUtils.loadImage(new class_2960("textures/entity/sheep/sheep_fur.png"), 6, 6, 6, 6);
            scale = sheepFur.getWidth() / 6.0F;
            sheepFur = ImageUtils.scaleImage(sheepFur, 4.0F / scale * 1.0625F);
            int chop = (int)Math.max(1.0F, 2.0F);
            sheepFur = ImageUtils.eraseArea(sheepFur, chop, chop, sheepFur.getWidth() - chop * 2, sheepFur.getHeight() - chop * 2, sheepFur.getWidth(), sheepFur.getHeight());
            sheepFur = ImageUtils.fillOutline(ImageUtils.pad(sheepFur), this.options.outlines, true, 27.5F, 27.5F, (int)Math.max(1.0F, 2.0F));
            this.textureAtlas.registerIconForBufferedImage("sheepfur", sheepFur);
            BufferedImage crown = ImageUtils.loadImage(new class_2960("voxelmap", "images/radar/crown.png"), 0, 0, 16, 16, 16, 16);
            crown = ImageUtils.fillOutline(ImageUtils.scaleImage(crown, 2.0F), this.options.outlines, true, 32.0F, 32.0F, 2);
            this.textureAtlas.registerIconForBufferedImage("crown", crown);
            BufferedImage glow = ImageUtils.loadImage(new class_2960("voxelmap", "images/radar/glow.png"), 0, 0, 16, 16, 16, 16);
            glow = ImageUtils.fillOutline(glow, this.options.outlines, true, 32.0F, 32.0F, 2);
            this.textureAtlas.registerIconForBufferedImage("glow", glow);
            class_2960 fontResourceLocation = new class_2960("textures/font/ascii.png");
            BufferedImage fontImage = ImageUtils.loadImage(fontResourceLocation, 0, 0, 128, 128, 128, 128);
            if (fontImage.getWidth() > 512 || fontImage.getHeight() > 512) {
                int maxDim = Math.max(fontImage.getWidth(), fontImage.getHeight());
                float scaleBy = 512.0F / maxDim;
                fontImage = ImageUtils.scaleImage(fontImage, scaleBy);
            }
            fontImage = ImageUtils.addImages(new BufferedImage(fontImage.getWidth() + 2, fontImage.getHeight() + 2, fontImage.getType()), fontImage, 1.0F, 1.0F, fontImage.getWidth() + 2, fontImage.getHeight() + 2);
            Sprite fontSprite = this.textureAtlas.registerIconForBufferedImage(fontResourceLocation.toString(), fontImage);
            class_2960 blankResourceLocation = new class_2960("voxelmap", "images/radar/solid.png");
            BufferedImage blankImage = ImageUtils.loadImage(blankResourceLocation, 0, 0, 8, 8, 8, 8);
            Sprite blankSprite = this.textureAtlas.registerIconForBufferedImage(blankResourceLocation.toString(), blankImage);
            this.fontRenderer.setSprites(fontSprite, blankSprite);
            this.fontRenderer.setFontRef(this.textureAtlas.method_4624());
            this.textureAtlas.stitch();
            this.completedLoading = true;
        } catch (Exception var30) {
            System.err.println("Failed getting mobs " + var30.getLocalizedMessage());
            var30.printStackTrace();
        }
    }

    private BufferedImage createImageFromTypeAndResourceLocations(EnumMobs type, class_2960 resourceLocation, class_2960 resourceLocationSecondary, class_1297 entity) {
        BufferedImage mobImage = ImageUtils.createBufferedImageFromResourceLocation(resourceLocation);
        BufferedImage mobImageSecondary = null;
        if (resourceLocationSecondary != null)
            mobImageSecondary = ImageUtils.createBufferedImageFromResourceLocation(resourceLocationSecondary);
        try {
            return createImageFromTypeAndImages(type, mobImage, mobImageSecondary, entity);
        } catch (Exception var8) {
            return null;
        }
    }

    private BufferedImage createImageFromTypeAndImages(EnumMobs type, BufferedImage mobImage, BufferedImage mobImageSecondary, class_1297 entity) {
        float[] primaryColorsA, secondaryColorsA;
        BufferedImage baseA, patternA;
        float[] primaryColorsB, secondaryColorsB;
        BufferedImage baseB, patternB, image = null;
        switch (type) {
            case GENERICHOSTILE:
                image = ImageUtils.loadImage(new class_2960("voxelmap", "images/radar/hostile.png"), 0, 0, 16, 16, 16, 16);
                break;
            case GENERICNEUTRAL:
                image = ImageUtils.loadImage(new class_2960("voxelmap", "images/radar/neutral.png"), 0, 0, 16, 16, 16, 16);
                break;
            case GENERICTAME:
                image = ImageUtils.loadImage(new class_2960("voxelmap", "images/radar/tame.png"), 0, 0, 16, 16, 16, 16);
                break;
            case BAT:
                image = ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.blankImage(mobImage, 8, 12, 64, 64), ImageUtils.loadImage(mobImage, 25, 1, 3, 4), 0.0F, 0.0F, 8, 12), ImageUtils.flipHorizontal(ImageUtils.loadImage(mobImage, 25, 1, 3, 4)), 5.0F, 0.0F, 8, 12), ImageUtils.loadImage(mobImage, 6, 6, 6, 6), 1.0F, 3.0F, 8, 12);
                break;
            case CHICKEN:
                image = ImageUtils.addImages(ImageUtils.addImages(ImageUtils.loadImage(mobImage, 2, 3, 6, 6), ImageUtils.loadImage(mobImage, 16, 2, 4, 2), 1.0F, 2.0F, 6, 6), ImageUtils.loadImage(mobImage, 16, 6, 2, 2), 2.0F, 4.0F, 6, 6);
                break;
            case COD:
                image = ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.blankImage(mobImage, 16, 5, 32, 32), ImageUtils.loadImage(mobImage, 15, 3, 1, 3, 32, 32), 1.0F, 1.0F, 16, 5), ImageUtils.loadImage(mobImage, 16, 3, 3, 4, 32, 32), 2.0F, 1.0F, 16, 5), ImageUtils.loadImage(mobImage, 9, 7, 7, 4, 32, 32), 5.0F, 1.0F, 16, 5), ImageUtils.loadImage(mobImage, 26, 7, 4, 4, 32, 32), 12.0F, 1.0F, 16, 5), ImageUtils.loadImage(mobImage, 26, 0, 6, 1, 32, 32), 4.0F, 0.0F, 16, 5);
                break;
            case ENDERDRAGON:
                image = ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.blankImage(mobImage, 16, 20, 256, 256), ImageUtils.loadImage(mobImage, 128, 46, 16, 16, 256, 256), 0.0F, 4.0F, 16, 16), ImageUtils.loadImage(mobImage, 192, 60, 12, 5, 256, 256), 2.0F, 11.0F, 16, 16), ImageUtils.loadImage(mobImage, 192, 81, 12, 4, 256, 256), 2.0F, 16.0F, 16, 16), ImageUtils.loadImage(mobImage, 6, 6, 2, 4, 256, 256), 3.0F, 0.0F, 16, 16), ImageUtils.flipHorizontal(ImageUtils.loadImage(mobImage, 6, 6, 2, 4, 256, 256)), 11.0F, 0.0F, 16, 16);
                break;
            case GHAST:
                image = ImageUtils.loadImage(mobImage, 16, 16, 16, 16);
                break;
            case GHASTATTACKING:
                image = ImageUtils.loadImage(mobImage, 16, 16, 16, 16);
                break;
            case GUARDIAN:
                image = ImageUtils.scaleImage(ImageUtils.addImages(ImageUtils.loadImage(mobImage, 16, 16, 12, 12), ImageUtils.loadImage(mobImage, 9, 1, 2, 2), 5.0F, 5.5F, 12, 12), 0.5F);
                break;
            case GUARDIANELDER:
                image = ImageUtils.addImages(ImageUtils.loadImage(mobImage, 16, 16, 12, 12), ImageUtils.loadImage(mobImage, 9, 1, 2, 2), 5.0F, 5.5F, 12, 12);
                break;
            case HORSE:
                image = ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.blankImage(mobImage, 16, 24, 64, 64), ImageUtils.loadImage(mobImage, 56, 38, 2, 16, 64, 64), 1.0F, 7.0F, 16, 24), ImageUtils.loadImage(mobImage, 0, 42, 7, 12, 64, 64), 3.0F, 12.0F, 16, 24), ImageUtils.loadImage(mobImage, 0, 20, 7, 5, 64, 64), 3.0F, 7.0F, 16, 24), ImageUtils.loadImage(mobImage, 0, 30, 5, 5, 64, 64), 10.0F, 7.0F, 16, 24), ImageUtils.loadImage(mobImage, 19, 17, 1, 3, 64, 64), 3.0F, 4.0F, 16, 24), ImageUtils.loadImage(mobImage, 0, 13, 1, 7, 64, 64), 3.0F, 0.0F, 16, 24);
                break;
            case IRONGOLEM:
                image = ImageUtils.addImages(ImageUtils.addImages(ImageUtils.blankImage(mobImage, 8, 12, 128, 128), ImageUtils.loadImage(mobImage, 8, 8, 8, 10, 128, 128), 0.0F, 1.0F, 8, 12), ImageUtils.loadImage(mobImage, 26, 2, 2, 4, 128, 128), 3.0F, 8.0F, 8, 12);
                break;
            case LLAMA:
                image = ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.blankImage(mobImage, 8, 14, 128, 64), ImageUtils.loadImage(mobImage, 6, 20, 8, 8, 128, 64), 0.0F, 3.0F, 8, 14), ImageUtils.loadImage(mobImage, 9, 9, 4, 4, 128, 64), 2.0F, 5.0F, 8, 14), ImageUtils.loadImage(mobImage, 19, 2, 3, 3, 128, 64), 0.0F, 0.0F, 8, 14), ImageUtils.loadImage(mobImage, 19, 2, 3, 3, 128, 64), 5.0F, 0.0F, 8, 14);
                break;
            case LLAMATRADER:
                image = ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.blankImage(mobImage, 8, 14, 128, 64), ImageUtils.loadImage(mobImage, 6, 20, 8, 8, 128, 64), 0.0F, 3.0F, 8, 14), ImageUtils.loadImage(mobImage, 9, 9, 4, 4, 128, 64), 2.0F, 5.0F, 8, 14), ImageUtils.loadImage(mobImage, 19, 2, 3, 3, 128, 64), 0.0F, 0.0F, 8, 14), ImageUtils.loadImage(mobImage, 19, 2, 3, 3, 128, 64), 5.0F, 0.0F, 8, 14);
                break;
            case MAGMA:
                image = ImageUtils.addImages(ImageUtils.addImages(ImageUtils.loadImage(mobImage, 8, 8, 8, 8), ImageUtils.loadImage(mobImage, 32, 18, 8, 1), 0.0F, 3.0F, 8, 8), ImageUtils.loadImage(mobImage, 32, 27, 8, 1), 0.0F, 4.0F, 8, 8);
                break;
            case MOOSHROOM:
                image = ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.blankImage(mobImage, 40, 40), ImageUtils.loadImage(mobImage, 6, 6, 8, 8), 16.0F, 16.0F, 40, 40), ImageUtils.loadImage(mobImage, 23, 1, 1, 3), 15.0F, 15.0F, 40, 40), ImageUtils.loadImage(mobImage, 23, 1, 1, 3), 24.0F, 15.0F, 40, 40);
                if (mobImageSecondary != null) {
                    BufferedImage mushroomImage;
                    if (mobImageSecondary.getWidth() != mobImageSecondary.getHeight()) {
                        mushroomImage = ImageUtils.loadImage(mobImageSecondary, 32, 0, 16, 16, 48, 16);
                    } else {
                        mushroomImage = ImageUtils.loadImage(mobImageSecondary, 0, 0, 16, 16, 16, 16);
                    }
                    float ratio = image.getWidth() / mushroomImage.getWidth();
                    if (ratio < 2.5D) {
                        image = ImageUtils.scaleImage(image, 2.5F / ratio);
                    } else if (ratio > 2.5D) {
                        mushroomImage = ImageUtils.scaleImage(mushroomImage, ratio / 2.5F);
                    }
                    image = ImageUtils.addImages(image, mushroomImage, 12.0F, 0.0F, 40, 40);
                }
                break;
            case PARROT:
                image = ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.blankImage(mobImage, 8, 8, 32, 32), ImageUtils.loadImage(mobImage, 2, 22, 3, 5, 32, 32), 1.0F, 0.0F, 8, 8), ImageUtils.loadImage(mobImage, 10, 4, 4, 1, 32, 32), 2.0F, 4.0F, 8, 8), ImageUtils.loadImage(mobImage, 2, 4, 2, 3, 32, 32), 2.0F, 5.0F, 8, 8), ImageUtils.loadImage(mobImage, 11, 8, 1, 2, 32, 32), 4.0F, 5.0F, 8, 8), ImageUtils.loadImage(mobImage, 16, 8, 1, 2, 32, 32), 5.0F, 5.0F, 8, 8);
                break;
            case PHANTOM:
                image = ImageUtils.addImages(ImageUtils.loadImage(mobImage, 5, 5, 7, 3, 64, 64), ImageUtils.loadImage(mobImageSecondary, 5, 5, 7, 3, 64, 64), 0.0F, 0.0F, 7, 3);
                break;
            case PUFFERFISH:
                image = ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.blankImage(mobImage, 3, 3, 32, 32), ImageUtils.loadImage(mobImage, 3, 30, 3, 2, 32, 32), 0.0F, 1.0F, 3, 3), ImageUtils.loadImage(mobImage, 3, 29, 1, 1, 32, 32), 0.0F, 0.0F, 3, 3), ImageUtils.loadImage(mobImage, 5, 29, 1, 1, 32, 32), 2.0F, 0.0F, 3, 3);
                break;
            case PUFFERFISHHALF:
                image = ImageUtils.loadImage(mobImage, 17, 27, 5, 5, 32, 32);
                break;
            case PUFFERFISHFULL:
                image = ImageUtils.loadImage(mobImage, 8, 8, 8, 8, 32, 32);
                break;
            case SALMON:
                image = ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.blankImage(mobImage, 26, 7, 32, 32), ImageUtils.loadImage(mobImage, 27, 3, 3, 4, 32, 32), 1.0F, 2.5F, 26, 7), ImageUtils.loadImage(mobImage, 11, 8, 8, 5, 32, 32), 4.0F, 2.0F, 26, 7), ImageUtils.loadImage(mobImage, 11, 21, 8, 5, 32, 32), 12.0F, 2.0F, 26, 7), ImageUtils.loadImage(mobImage, 26, 16, 6, 5, 32, 32), 20.0F, 2.0F, 26, 7), ImageUtils.loadImage(mobImage, 0, 0, 2, 2, 32, 32), 10.0F, 0.0F, 26, 7), ImageUtils.loadImage(mobImage, 5, 6, 3, 2, 32, 32), 12.0F, 0.0F, 26, 7);
                break;
            case SLIME:
                image = ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.blankImage(mobImage, 8, 8), ImageUtils.loadImage(mobImage, 6, 22, 6, 6), 1.0F, 1.0F, 8, 8), ImageUtils.loadImage(mobImage, 34, 6, 2, 2), 5.0F, 2.0F, 8, 8), ImageUtils.loadImage(mobImage, 34, 2, 2, 2), 1.0F, 2.0F, 8, 8), ImageUtils.loadImage(mobImage, 33, 9, 1, 1), 4.0F, 5.0F, 8, 8), ImageUtils.loadImage(mobImage, 8, 8, 8, 8), 0.0F, 0.0F, 8, 8);
                break;
            case TROPICALFISHA:
                primaryColorsA = new float[] { 0.9765F, 0.502F, 0.1137F };
                secondaryColorsA = new float[] { 0.9765F, 1.0F, 0.9961F };
                if (entity != null && entity instanceof class_1474) {
                    class_1474 fish = (class_1474)entity;
                    primaryColorsA = fish.method_6658();
                    secondaryColorsA = fish.method_6655();
                }
                baseA = ImageUtils.colorify(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.blankImage(mobImage, 10, 6, 32, 32), ImageUtils.loadImage(mobImage, 8, 6, 6, 3, 32, 32), 0.0F, 3.0F, 10, 6), ImageUtils.loadImage(mobImage, 17, 1, 5, 3, 32, 32), 1.0F, 0.0F, 10, 6), ImageUtils.loadImage(mobImage, 28, 0, 4, 3, 32, 32), 6.0F, 3.0F, 10, 6), primaryColorsA[0], primaryColorsA[1], primaryColorsA[2]);
                patternA = ImageUtils.colorify(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.blankImage(mobImageSecondary, 10, 6, 32, 32), ImageUtils.loadImage(mobImageSecondary, 8, 6, 6, 3, 32, 32), 0.0F, 3.0F, 10, 6), ImageUtils.loadImage(mobImageSecondary, 17, 1, 5, 3, 32, 32), 1.0F, 0.0F, 10, 6), ImageUtils.loadImage(mobImageSecondary, 28, 0, 4, 3, 32, 32), 6.0F, 3.0F, 10, 6), secondaryColorsA[0], secondaryColorsA[1], secondaryColorsA[2]);
                image = ImageUtils.addImages(baseA, patternA, 0.0F, 0.0F, 10, 6);
                baseA.flush();
                patternA.flush();
                break;
            case TROPICALFISHB:
                primaryColorsB = new float[] { 0.5373F, 0.1961F, 0.7216F };
                secondaryColorsB = new float[] { 0.9961F, 0.8471F, 0.2392F };
                if (entity != null && entity instanceof class_1474) {
                    class_1474 fish = (class_1474)entity;
                    primaryColorsB = fish.method_6658();
                    secondaryColorsB = fish.method_6655();
                }
                baseB = ImageUtils.colorify(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.blankImage(mobImage, 12, 12, 32, 32), ImageUtils.loadImage(mobImage, 0, 26, 6, 6, 32, 32), 6.0F, 3.0F, 12, 12), ImageUtils.loadImage(mobImage, 20, 21, 6, 6, 32, 32), 0.0F, 3.0F, 12, 12), ImageUtils.loadImage(mobImage, 20, 18, 5, 3, 32, 32), 6.0F, 0.0F, 12, 12), ImageUtils.loadImage(mobImage, 20, 27, 5, 3, 32, 32), 6.0F, 9.0F, 12, 12), primaryColorsB[0], primaryColorsB[1], primaryColorsB[2]);
                patternB = ImageUtils.colorify(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.blankImage(mobImageSecondary, 12, 12, 32, 32), ImageUtils.loadImage(mobImageSecondary, 0, 26, 6, 6, 32, 32), 6.0F, 3.0F, 12, 12), ImageUtils.loadImage(mobImageSecondary, 20, 21, 6, 6, 32, 32), 0.0F, 3.0F, 12, 12), ImageUtils.loadImage(mobImageSecondary, 20, 18, 5, 3, 32, 32), 6.0F, 0.0F, 12, 12), ImageUtils.loadImage(mobImageSecondary, 20, 27, 5, 3, 32, 32), 6.0F, 9.0F, 12, 12), secondaryColorsB[0], secondaryColorsB[1], secondaryColorsB[2]);
                image = ImageUtils.addImages(baseB, patternB, 0.0F, 0.0F, 12, 12);
                baseB.flush();
                patternB.flush();
                break;
            case WITHER:
                image = ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.blankImage(mobImage, 24, 10, 64, 64), ImageUtils.loadImage(mobImage, 8, 8, 8, 8, 64, 64), 8.0F, 0.0F, 24, 10), ImageUtils.loadImage(mobImage, 38, 6, 6, 6, 64, 64), 0.0F, 2.0F, 24, 10), ImageUtils.loadImage(mobImage, 38, 6, 6, 6, 64, 64), 18.0F, 2.0F, 24, 10);
                break;
            case WITHERINVULNERABLE:
                image = ImageUtils.addImages(ImageUtils.addImages(ImageUtils.addImages(ImageUtils.blankImage(mobImage, 24, 10, 64, 64), ImageUtils.loadImage(mobImage, 8, 8, 8, 8, 64, 64), 8.0F, 0.0F, 24, 10), ImageUtils.loadImage(mobImage, 38, 6, 6, 6, 64, 64), 0.0F, 2.0F, 24, 10), ImageUtils.loadImage(mobImage, 38, 6, 6, 6, 64, 64), 18.0F, 2.0F, 24, 10);
                break;
            default:
                image = null;
                break;
        }
        mobImage.flush();
        if (mobImageSecondary != null)
            mobImageSecondary.flush();
        return image;
    }

    public void onTickInGame(class_4587 matrixStack, class_310 mc, LayoutVariables layoutVariables) {
        if (this.options.radarAllowed.booleanValue() || this.options.radarMobsAllowed.booleanValue() || this.options.radarPlayersAllowed.booleanValue()) {
            if (this.game == null)
                this.game = mc;
            this.layoutVariables = layoutVariables;
            if (this.options.isChanged()) {
                this.timer = 500;
                if (this.options.outlines != this.lastOutlines) {
                    this.lastOutlines = this.options.outlines;
                    loadTexturePackIcons();
                }
            }
            this.direction = GameVariableAccessShim.rotationYaw() + 180.0F;
            while (this.direction >= 360.0F)
                this.direction -= 360.0F;
            while (this.direction < 0.0F)
                this.direction += 360.0F;
            if (this.enabled) {
                if (this.completedLoading && this.timer > 95) {
                    calculateMobs();
                    this.timer = 0;
                }
                this.timer++;
                if (this.completedLoading)
                    renderMapMobs(matrixStack, this.layoutVariables.mapX, this.layoutVariables.mapY);
                GLShim.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    private int chkLen(String paramStr) {
        return this.fontRenderer.getStringWidth(paramStr);
    }

    private void write(String paramStr, float x, float y, int color) {
        GLShim.glTexParameteri(3553, 10241, 9728);
        GLShim.glTexParameteri(3553, 10240, 9728);
        this.fontRenderer.drawStringWithShadow(paramStr, x, y, color);
    }

    private boolean isEntityShown(class_1297 entity) {
        return (entity != null && !entity.method_5756((class_1657)this.game.field_1724) && ((this.options.showHostiles && (this.options.radarAllowed.booleanValue() || this.options.radarMobsAllowed.booleanValue()) && isHostile(entity)) || (this.options.showPlayers && (this.options.radarAllowed.booleanValue() || this.options.radarPlayersAllowed.booleanValue()) && isPlayer(entity)) || (this.options.showNeutrals && this.options.radarMobsAllowed.booleanValue() && isNeutral(entity))));
    }

    public void calculateMobs() {
        this.contacts.clear();
        for (class_1297 entity : this.game.field_1687.method_18112()) {
            try {
                if (isEntityShown(entity)) {
                    int wayX = GameVariableAccessShim.xCoord() - (int)entity.method_19538().method_10216();
                    int wayZ = GameVariableAccessShim.zCoord() - (int)entity.method_19538().method_10215();
                    int wayY = GameVariableAccessShim.yCoord() - (int)entity.method_19538().method_10214();
                    double hypot = (wayX * wayX + wayZ * wayZ + wayY * wayY);
                    hypot /= this.layoutVariables.zoomScaleAdjusted * this.layoutVariables.zoomScaleAdjusted;
                    if (hypot < 961.0D) {
                        class_1309 class_1309;
                        if (this.hasCustomNPCs)
                            try {
                                if (this.entityCustomNpcClass.isInstance(entity)) {
                                    Object modelData = this.modelDataField.get(entity);
                                    class_1309 wrappedEntity = (class_1309)this.getEntityMethod.invoke(modelData, new Object[] { entity });
                                    if (wrappedEntity != null)
                                        class_1309 = wrappedEntity;
                                }
                            } catch (Exception exception) {}
                        Contact contact = new Contact((class_1297)class_1309, EnumMobs.getMobTypeByEntity((class_1297)class_1309));
                        String unscrubbedName = TextUtils.asFormattedString(contact.entity.method_5476());
                        contact.setName(unscrubbedName);
                        if (contact.entity.method_5854() != null && isEntityShown(contact.entity.method_5854()))
                            contact.yFudge = 1;
                        contact.updateLocation();
                        boolean enabled = false;
                        if (!contact.vanillaType) {
                            String type = class_1309.method_5864().method_5882();
                            CustomMob customMob = CustomMobsManager.getCustomMobByType(type);
                            if (customMob == null || customMob.enabled)
                                enabled = true;
                        } else if (contact.type.enabled) {
                            enabled = true;
                        }
                        if (enabled) {
                            if (contact.type == EnumMobs.PLAYER)
                                handleMPplayer(contact);
                            if (contact.icon == null)
                                tryCustomIcon(contact);
                            if (contact.icon == null)
                                tryAutoIcon(contact);
                            if (contact.icon == null)
                                getGenericIcon(contact);
                            if (contact.type == EnumMobs.HORSE)
                                contact.setRotationFactor(45);
                            String scrubbedName = TextUtils.scrubCodes(contact.entity.method_5477().getString());
                            if (scrubbedName != null && (scrubbedName.equals("Dinnerbone") || scrubbedName.equals("Grumm")) && (!(contact.entity instanceof class_1657) || ((class_1657)contact.entity).method_7348(class_1664.field_7559)))
                                contact.setRotationFactor(contact.rotationFactor + 180);
                            if ((this.options.showHelmetsPlayers && contact.type == EnumMobs.PLAYER) || (this.options.showHelmetsMobs && contact.type != EnumMobs.PLAYER) || contact.type == EnumMobs.SHEEP)
                                getArmor(contact, (class_1297)class_1309);
                            this.contacts.add(contact);
                        }
                    }
                }
            } catch (Exception var16) {
                System.err.println(var16.getLocalizedMessage());
                var16.printStackTrace();
            }
        }
        if (this.newMobs)
            try {
                this.textureAtlas.stitchNew();
            } catch (StitcherException var14) {
                System.err.println("Stitcher exception!  Resetting mobs texture atlas.");
                loadTexturePackIcons();
            }
        this.newMobs = false;
        Collections.sort(this.contacts, new Comparator<Contact>() {
            public int compare(Contact contact1, Contact contact2) {
                return contact1.y - contact2.y;
            }
        });
    }

    private void tryCustomIcon(Contact contact) {
        String identifier = contact.vanillaType ? ("minecraft." + contact.type.id) : contact.entity.getClass().getName();
        String identifierSimple = contact.vanillaType ? contact.type.id : contact.entity.getClass().getSimpleName();
        Sprite icon = this.textureAtlas.getAtlasSprite(identifier + "custom");
        if (icon == this.textureAtlas.getMissingImage()) {
            boolean isHostile = isHostile(contact.entity);
            CustomMobsManager.add(contact.entity.method_5864().method_5882(), isHostile, !isHostile);
            BufferedImage mobSkin = getCustomMobImage(identifier, identifierSimple);
            if (mobSkin != null) {
                icon = this.textureAtlas.registerIconForBufferedImage(identifier + "custom", mobSkin);
                this.newMobs = true;
                contact.icon = icon;
                contact.custom = true;
            } else {
                this.textureAtlas.registerFailedIcon(identifier + "custom");
            }
        } else if (icon != this.textureAtlas.getFailedImage()) {
            contact.custom = true;
            contact.icon = icon;
        }
    }

    private BufferedImage getCustomMobImage(String identifier, String identifierSimple) {
        BufferedImage mobSkin = null;
        try {
            int intendedSize = 8;
            String fullPath = ("textures/icons/" + identifier + ".png").toLowerCase();
            InputStream is = null;
            try {
                is = ((class_3298)this.game.method_1478().method_14486(new class_2960(fullPath)).get()).method_14482();
            } catch (IOException var15) {
                is = null;
            }
            if (is == null) {
                fullPath = ("textures/icons/" + identifierSimple + ".png").toLowerCase();
                try {
                    is = ((class_3298)this.game.method_1478().method_14486(new class_2960(fullPath)).get()).method_14482();
                } catch (IOException var14) {
                    is = null;
                }
            }
            if (is == null) {
                fullPath = ("textures/icons/" + identifier + "8.png").toLowerCase();
                try {
                    is = ((class_3298)this.game.method_1478().method_14486(new class_2960(fullPath)).get()).method_14482();
                } catch (IOException var13) {
                    is = null;
                }
            }
            if (is == null) {
                fullPath = ("textures/icons/" + identifierSimple + "8.png").toLowerCase();
                try {
                    is = ((class_3298)this.game.method_1478().method_14486(new class_2960(fullPath)).get()).method_14482();
                } catch (IOException var12) {
                    is = null;
                }
            }
            if (is == null) {
                intendedSize = 16;
                fullPath = ("textures/icons/" + identifier + "16.png").toLowerCase();
                try {
                    is = ((class_3298)this.game.method_1478().method_14486(new class_2960(fullPath)).get()).method_14482();
                } catch (IOException var11) {
                    is = null;
                }
            }
            if (is == null) {
                fullPath = ("textures/icons/" + identifierSimple + "16.png").toLowerCase();
                try {
                    is = ((class_3298)this.game.method_1478().method_14486(new class_2960(fullPath)).get()).method_14482();
                } catch (IOException var10) {
                    is = null;
                }
            }
            if (is == null) {
                intendedSize = 32;
                fullPath = ("textures/icons/" + identifier + "32.png").toLowerCase();
                try {
                    is = ((class_3298)this.game.method_1478().method_14486(new class_2960(fullPath)).get()).method_14482();
                } catch (IOException var9) {
                    is = null;
                }
            }
            if (is == null) {
                fullPath = ("textures/icons/" + identifierSimple + "32.png").toLowerCase();
                try {
                    is = ((class_3298)this.game.method_1478().method_14486(new class_2960(fullPath)).get()).method_14482();
                } catch (IOException var8) {
                    is = null;
                }
            }
            if (is != null) {
                mobSkin = ImageIO.read(is);
                is.close();
                mobSkin = ImageUtils.validateImage(mobSkin);
                float scale = mobSkin.getWidth() / intendedSize;
                mobSkin = ImageUtils.fillOutline(ImageUtils.pad(ImageUtils.scaleImage(mobSkin, 4.0F / scale)), this.options.outlines, 2);
            }
        } catch (Exception var16) {
            mobSkin = null;
        }
        return mobSkin;
    }

    private void tryAutoIcon(Contact contact) {
        // Byte code:
        //   0: aload_0
        //   1: getfield game : Lnet/minecraft/class_310;
        //   4: invokevirtual method_1561 : ()Lnet/minecraft/class_898;
        //   7: aload_1
        //   8: getfield entity : Lnet/minecraft/class_1297;
        //   11: invokevirtual method_3953 : (Lnet/minecraft/class_1297;)Lnet/minecraft/class_897;
        //   14: astore_2
        //   15: aload_2
        //   16: aload_1
        //   17: getfield entity : Lnet/minecraft/class_1297;
        //   20: invokevirtual method_3931 : (Lnet/minecraft/class_1297;)Lnet/minecraft/class_2960;
        //   23: astore_3
        //   24: aload_0
        //   25: aload_3
        //   26: aload_1
        //   27: getfield entity : Lnet/minecraft/class_1297;
        //   30: invokevirtual getRandomizedResourceLocationForEntity : (Lnet/minecraft/class_2960;Lnet/minecraft/class_1297;)Lnet/minecraft/class_2960;
        //   33: astore_3
        //   34: aconst_null
        //   35: astore #4
        //   37: aconst_null
        //   38: astore #5
        //   40: aconst_null
        //   41: astore #6
        //   43: ldc_w ''
        //   46: astore #7
        //   48: aload_1
        //   49: getfield type : Lcom/mamiyaotaru/voxelmap/util/EnumMobs;
        //   52: getfield secondaryResourceLocation : Lnet/minecraft/class_2960;
        //   55: ifnull -> 631
        //   58: aload_1
        //   59: getfield type : Lcom/mamiyaotaru/voxelmap/util/EnumMobs;
        //   62: getstatic com/mamiyaotaru/voxelmap/util/EnumMobs.MOOSHROOM : Lcom/mamiyaotaru/voxelmap/util/EnumMobs;
        //   65: if_acmpne -> 98
        //   68: aload_1
        //   69: getfield entity : Lnet/minecraft/class_1297;
        //   72: checkcast net/minecraft/class_1438
        //   75: invokevirtual method_6109 : ()Z
        //   78: ifne -> 92
        //   81: getstatic com/mamiyaotaru/voxelmap/util/EnumMobs.MOOSHROOM : Lcom/mamiyaotaru/voxelmap/util/EnumMobs;
        //   84: getfield secondaryResourceLocation : Lnet/minecraft/class_2960;
        //   87: astore #4
        //   89: goto -> 571
        //   92: aconst_null
        //   93: astore #4
        //   95: goto -> 571
        //   98: aload_1
        //   99: getfield type : Lcom/mamiyaotaru/voxelmap/util/EnumMobs;
        //   102: getstatic com/mamiyaotaru/voxelmap/util/EnumMobs.TROPICALFISHA : Lcom/mamiyaotaru/voxelmap/util/EnumMobs;
        //   105: if_acmpeq -> 538
        //   108: aload_1
        //   109: getfield type : Lcom/mamiyaotaru/voxelmap/util/EnumMobs;
        //   112: getstatic com/mamiyaotaru/voxelmap/util/EnumMobs.TROPICALFISHB : Lcom/mamiyaotaru/voxelmap/util/EnumMobs;
        //   115: if_acmpeq -> 538
        //   118: aload_1
        //   119: getfield type : Lcom/mamiyaotaru/voxelmap/util/EnumMobs;
        //   122: getstatic com/mamiyaotaru/voxelmap/util/EnumMobs.HORSE : Lcom/mamiyaotaru/voxelmap/util/EnumMobs;
        //   125: if_acmpne -> 242
        //   128: aload_1
        //   129: getfield entity : Lnet/minecraft/class_1297;
        //   132: astore #8
        //   134: aload #8
        //   136: instanceof net/minecraft/class_1498
        //   139: ifeq -> 242
        //   142: aload #8
        //   144: checkcast net/minecraft/class_1498
        //   147: astore #9
        //   149: getstatic com/mamiyaotaru/voxelmap/Radar.TEXTURES : Ljava/util/Map;
        //   152: aload #9
        //   154: invokevirtual method_27078 : ()Lnet/minecraft/class_5148;
        //   157: invokeinterface get : (Ljava/lang/Object;)Ljava/lang/Object;
        //   162: checkcast net/minecraft/class_2960
        //   165: astore #4
        //   167: aload #9
        //   169: invokevirtual method_6786 : ()Lnet/minecraft/class_1799;
        //   172: astore #10
        //   174: aload_0
        //   175: getfield options : Lcom/mamiyaotaru/voxelmap/RadarSettingsManager;
        //   178: getfield showHelmetsMobs : Z
        //   181: ifeq -> 571
        //   184: aload #10
        //   186: invokevirtual method_7909 : ()Lnet/minecraft/class_1792;
        //   189: astore #11
        //   191: aload #11
        //   193: instanceof net/minecraft/class_4059
        //   196: ifeq -> 239
        //   199: aload #11
        //   201: checkcast net/minecraft/class_4059
        //   204: astore #12
        //   206: aload #12
        //   208: invokevirtual method_18454 : ()Lnet/minecraft/class_2960;
        //   211: astore #5
        //   213: aload #12
        //   215: instanceof net/minecraft/class_4058
        //   218: ifeq -> 239
        //   221: aload #12
        //   223: checkcast net/minecraft/class_4058
        //   226: astore #13
        //   228: aload_1
        //   229: aload #13
        //   231: aload #10
        //   233: invokevirtual method_7800 : (Lnet/minecraft/class_1799;)I
        //   236: putfield armorColor : I
        //   239: goto -> 571
        //   242: aload_1
        //   243: getfield type : Lcom/mamiyaotaru/voxelmap/util/EnumMobs;
        //   246: getstatic com/mamiyaotaru/voxelmap/util/EnumMobs.VILLAGER : Lcom/mamiyaotaru/voxelmap/util/EnumMobs;
        //   249: if_acmpeq -> 274
        //   252: aload_1
        //   253: getfield type : Lcom/mamiyaotaru/voxelmap/util/EnumMobs;
        //   256: getstatic com/mamiyaotaru/voxelmap/util/EnumMobs.ZOMBIEVILLAGER : Lcom/mamiyaotaru/voxelmap/util/EnumMobs;
        //   259: if_acmpeq -> 274
        //   262: aload_1
        //   263: getfield type : Lcom/mamiyaotaru/voxelmap/util/EnumMobs;
        //   266: getfield secondaryResourceLocation : Lnet/minecraft/class_2960;
        //   269: astore #4
        //   271: goto -> 571
        //   274: aload_1
        //   275: getfield type : Lcom/mamiyaotaru/voxelmap/util/EnumMobs;
        //   278: getstatic com/mamiyaotaru/voxelmap/util/EnumMobs.ZOMBIEVILLAGER : Lcom/mamiyaotaru/voxelmap/util/EnumMobs;
        //   281: if_acmpne -> 290
        //   284: ldc_w 'zombie_'
        //   287: goto -> 293
        //   290: ldc_w ''
        //   293: astore #8
        //   295: aload_1
        //   296: getfield entity : Lnet/minecraft/class_1297;
        //   299: checkcast net/minecraft/class_3851
        //   302: invokeinterface method_7231 : ()Lnet/minecraft/class_3850;
        //   307: astore #9
        //   309: aload #9
        //   311: invokevirtual method_16919 : ()Lnet/minecraft/class_3854;
        //   314: astore #10
        //   316: aload #9
        //   318: invokevirtual method_16924 : ()Lnet/minecraft/class_3852;
        //   321: astore #11
        //   323: getstatic net/minecraft/class_2378.field_17166 : Lnet/minecraft/class_2348;
        //   326: aload #10
        //   328: invokevirtual method_10221 : (Ljava/lang/Object;)Lnet/minecraft/class_2960;
        //   331: astore #4
        //   333: new net/minecraft/class_2960
        //   336: dup
        //   337: aload #4
        //   339: invokevirtual method_12836 : ()Ljava/lang/String;
        //   342: aload #8
        //   344: aload #4
        //   346: invokevirtual method_12832 : ()Ljava/lang/String;
        //   349: <illegal opcode> makeConcatWithConstants : (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //   354: invokespecial <init> : (Ljava/lang/String;Ljava/lang/String;)V
        //   357: astore #4
        //   359: aload #11
        //   361: getstatic net/minecraft/class_3852.field_17051 : Lnet/minecraft/class_3852;
        //   364: if_acmpeq -> 480
        //   367: aload_1
        //   368: getfield entity : Lnet/minecraft/class_1297;
        //   371: checkcast net/minecraft/class_1309
        //   374: invokevirtual method_6109 : ()Z
        //   377: ifne -> 480
        //   380: getstatic net/minecraft/class_2378.field_17167 : Lnet/minecraft/class_2348;
        //   383: aload #11
        //   385: invokevirtual method_10221 : (Ljava/lang/Object;)Lnet/minecraft/class_2960;
        //   388: astore #5
        //   390: new net/minecraft/class_2960
        //   393: dup
        //   394: aload #5
        //   396: invokevirtual method_12836 : ()Ljava/lang/String;
        //   399: aload #8
        //   401: aload #5
        //   403: invokevirtual method_12832 : ()Ljava/lang/String;
        //   406: <illegal opcode> makeConcatWithConstants : (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //   411: invokespecial <init> : (Ljava/lang/String;Ljava/lang/String;)V
        //   414: astore #5
        //   416: aload #11
        //   418: getstatic net/minecraft/class_3852.field_17062 : Lnet/minecraft/class_3852;
        //   421: if_acmpeq -> 480
        //   424: getstatic com/mamiyaotaru/voxelmap/Radar.LEVEL_TO_ID : Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;
        //   427: aload #9
        //   429: invokevirtual method_16925 : ()I
        //   432: iconst_1
        //   433: getstatic com/mamiyaotaru/voxelmap/Radar.LEVEL_TO_ID : Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;
        //   436: invokeinterface size : ()I
        //   441: invokestatic method_15340 : (III)I
        //   444: invokeinterface get : (I)Ljava/lang/Object;
        //   449: checkcast net/minecraft/class_2960
        //   452: astore #6
        //   454: new net/minecraft/class_2960
        //   457: dup
        //   458: aload #6
        //   460: invokevirtual method_12836 : ()Ljava/lang/String;
        //   463: aload #8
        //   465: aload #6
        //   467: invokevirtual method_12832 : ()Ljava/lang/String;
        //   470: <illegal opcode> makeConcatWithConstants : (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //   475: invokespecial <init> : (Ljava/lang/String;Ljava/lang/String;)V
        //   478: astore #6
        //   480: aload_0
        //   481: aload #4
        //   483: invokevirtual getHatType : (Lnet/minecraft/class_2960;)Lnet/minecraft/class_3888$class_3889;
        //   486: astore #12
        //   488: aload_0
        //   489: aload #5
        //   491: invokevirtual getHatType : (Lnet/minecraft/class_2960;)Lnet/minecraft/class_3888$class_3889;
        //   494: astore #13
        //   496: aload #13
        //   498: getstatic net/minecraft/class_3888$class_3889.field_17160 : Lnet/minecraft/class_3888$class_3889;
        //   501: if_acmpeq -> 520
        //   504: aload #13
        //   506: getstatic net/minecraft/class_3888$class_3889.field_17161 : Lnet/minecraft/class_3888$class_3889;
        //   509: if_acmpne -> 524
        //   512: aload #12
        //   514: getstatic net/minecraft/class_3888$class_3889.field_17162 : Lnet/minecraft/class_3888$class_3889;
        //   517: if_acmpeq -> 524
        //   520: iconst_1
        //   521: goto -> 525
        //   524: iconst_0
        //   525: istore #14
        //   527: iload #14
        //   529: ifne -> 535
        //   532: aconst_null
        //   533: astore #4
        //   535: goto -> 571
        //   538: aload_1
        //   539: getfield entity : Lnet/minecraft/class_1297;
        //   542: checkcast net/minecraft/class_1474
        //   545: astore #8
        //   547: aload #8
        //   549: invokevirtual method_6646 : ()Lnet/minecraft/class_2960;
        //   552: astore #4
        //   554: aload #8
        //   556: invokevirtual method_6658 : ()[F
        //   559: aload #8
        //   561: invokevirtual method_6655 : ()[F
        //   564: <illegal opcode> makeConcatWithConstants : ([F[F)Ljava/lang/String;
        //   569: astore #7
        //   571: aload #4
        //   573: ifnull -> 591
        //   576: aload_0
        //   577: aload #4
        //   579: aload_1
        //   580: getfield entity : Lnet/minecraft/class_1297;
        //   583: checkcast net/minecraft/class_1309
        //   586: invokevirtual getRandomizedResourceLocationForEntity : (Lnet/minecraft/class_2960;Lnet/minecraft/class_1297;)Lnet/minecraft/class_2960;
        //   589: astore #4
        //   591: aload #5
        //   593: ifnull -> 611
        //   596: aload_0
        //   597: aload #5
        //   599: aload_1
        //   600: getfield entity : Lnet/minecraft/class_1297;
        //   603: checkcast net/minecraft/class_1309
        //   606: invokevirtual getRandomizedResourceLocationForEntity : (Lnet/minecraft/class_2960;Lnet/minecraft/class_1297;)Lnet/minecraft/class_2960;
        //   609: astore #5
        //   611: aload #6
        //   613: ifnull -> 631
        //   616: aload_0
        //   617: aload #6
        //   619: aload_1
        //   620: getfield entity : Lnet/minecraft/class_1297;
        //   623: checkcast net/minecraft/class_1309
        //   626: invokevirtual getRandomizedResourceLocationForEntity : (Lnet/minecraft/class_2960;Lnet/minecraft/class_1297;)Lnet/minecraft/class_2960;
        //   629: astore #6
        //   631: aload_1
        //   632: getfield vanillaType : Z
        //   635: ifeq -> 653
        //   638: aload_1
        //   639: getfield type : Lcom/mamiyaotaru/voxelmap/util/EnumMobs;
        //   642: getfield id : Ljava/lang/String;
        //   645: <illegal opcode> makeConcatWithConstants : (Ljava/lang/String;)Ljava/lang/String;
        //   650: goto -> 663
        //   653: aload_1
        //   654: getfield entity : Lnet/minecraft/class_1297;
        //   657: invokevirtual getClass : ()Ljava/lang/Class;
        //   660: invokevirtual getName : ()Ljava/lang/String;
        //   663: astore #8
        //   665: aload_3
        //   666: ifnull -> 676
        //   669: aload_3
        //   670: invokevirtual toString : ()Ljava/lang/String;
        //   673: goto -> 679
        //   676: ldc_w ''
        //   679: aload #4
        //   681: ifnull -> 692
        //   684: aload #4
        //   686: invokevirtual toString : ()Ljava/lang/String;
        //   689: goto -> 695
        //   692: ldc_w ''
        //   695: <illegal opcode> makeConcatWithConstants : (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //   700: astore #9
        //   702: aload #9
        //   704: aload #5
        //   706: ifnull -> 717
        //   709: aload #5
        //   711: invokevirtual toString : ()Ljava/lang/String;
        //   714: goto -> 720
        //   717: ldc_w ''
        //   720: aload #6
        //   722: ifnull -> 733
        //   725: aload #6
        //   727: invokevirtual toString : ()Ljava/lang/String;
        //   730: goto -> 736
        //   733: ldc_w ''
        //   736: <illegal opcode> makeConcatWithConstants : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //   741: astore #9
        //   743: aload #9
        //   745: aload_1
        //   746: getfield armorColor : I
        //   749: iconst_m1
        //   750: if_icmpeq -> 763
        //   753: aload_1
        //   754: getfield armorColor : I
        //   757: invokestatic valueOf : (I)Ljava/lang/Integer;
        //   760: goto -> 766
        //   763: ldc_w ''
        //   766: <illegal opcode> makeConcatWithConstants : (Ljava/lang/String;Ljava/io/Serializable;)Ljava/lang/String;
        //   771: astore #9
        //   773: aload #8
        //   775: aload #7
        //   777: aload #9
        //   779: <illegal opcode> makeConcatWithConstants : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //   784: astore #10
        //   786: aload_0
        //   787: getfield textureAtlas : Lcom/mamiyaotaru/voxelmap/textures/TextureAtlas;
        //   790: aload #10
        //   792: invokevirtual getAtlasSprite : (Ljava/lang/String;)Lcom/mamiyaotaru/voxelmap/textures/Sprite;
        //   795: astore #11
        //   797: aload #11
        //   799: aload_0
        //   800: getfield textureAtlas : Lcom/mamiyaotaru/voxelmap/textures/TextureAtlas;
        //   803: invokevirtual getMissingImage : ()Lcom/mamiyaotaru/voxelmap/textures/Sprite;
        //   806: if_acmpne -> 1276
        //   809: aload_0
        //   810: getfield contactsSkinGetTries : Ljava/util/HashMap;
        //   813: aload #10
        //   815: invokevirtual get : (Ljava/lang/Object;)Ljava/lang/Object;
        //   818: checkcast java/lang/Integer
        //   821: astore #12
        //   823: aload #12
        //   825: ifnonnull -> 834
        //   828: iconst_0
        //   829: invokestatic valueOf : (I)Ljava/lang/Integer;
        //   832: astore #12
        //   834: aconst_null
        //   835: astore #13
        //   837: aload_1
        //   838: getfield type : Lcom/mamiyaotaru/voxelmap/util/EnumMobs;
        //   841: getstatic com/mamiyaotaru/voxelmap/util/EnumMobs.HORSE : Lcom/mamiyaotaru/voxelmap/util/EnumMobs;
        //   844: if_acmpne -> 1034
        //   847: aload_3
        //   848: invokestatic createBufferedImageFromResourceLocation : (Lnet/minecraft/class_2960;)Ljava/awt/image/BufferedImage;
        //   851: astore #14
        //   853: aload #4
        //   855: ifnull -> 924
        //   858: aload #4
        //   860: invokestatic createBufferedImageFromResourceLocation : (Lnet/minecraft/class_2960;)Ljava/awt/image/BufferedImage;
        //   863: astore #15
        //   865: aload #15
        //   867: aload #14
        //   869: invokevirtual getWidth : ()I
        //   872: i2f
        //   873: aload #15
        //   875: invokevirtual getWidth : ()I
        //   878: i2f
        //   879: fdiv
        //   880: aload #14
        //   882: invokevirtual getHeight : ()I
        //   885: i2f
        //   886: aload #15
        //   888: invokevirtual getHeight : ()I
        //   891: i2f
        //   892: fdiv
        //   893: invokestatic scaleImage : (Ljava/awt/image/BufferedImage;FF)Ljava/awt/image/BufferedImage;
        //   896: astore #15
        //   898: aload #14
        //   900: aload #15
        //   902: fconst_0
        //   903: fconst_0
        //   904: aload #14
        //   906: invokevirtual getWidth : ()I
        //   909: aload #14
        //   911: invokevirtual getHeight : ()I
        //   914: invokestatic addImages : (Ljava/awt/image/BufferedImage;Ljava/awt/image/BufferedImage;FFII)Ljava/awt/image/BufferedImage;
        //   917: astore #14
        //   919: aload #15
        //   921: invokevirtual flush : ()V
        //   924: aload #5
        //   926: ifnull -> 1006
        //   929: aload #5
        //   931: invokestatic createBufferedImageFromResourceLocation : (Lnet/minecraft/class_2960;)Ljava/awt/image/BufferedImage;
        //   934: astore #15
        //   936: aload #15
        //   938: aload #14
        //   940: invokevirtual getWidth : ()I
        //   943: i2f
        //   944: aload #15
        //   946: invokevirtual getWidth : ()I
        //   949: i2f
        //   950: fdiv
        //   951: aload #14
        //   953: invokevirtual getHeight : ()I
        //   956: i2f
        //   957: aload #15
        //   959: invokevirtual getHeight : ()I
        //   962: i2f
        //   963: fdiv
        //   964: invokestatic scaleImage : (Ljava/awt/image/BufferedImage;FF)Ljava/awt/image/BufferedImage;
        //   967: astore #15
        //   969: aload #15
        //   971: aload_1
        //   972: getfield armorColor : I
        //   975: invokestatic colorify : (Ljava/awt/image/BufferedImage;I)Ljava/awt/image/BufferedImage;
        //   978: astore #15
        //   980: aload #14
        //   982: aload #15
        //   984: fconst_0
        //   985: fconst_0
        //   986: aload #14
        //   988: invokevirtual getWidth : ()I
        //   991: aload #14
        //   993: invokevirtual getHeight : ()I
        //   996: invokestatic addImages : (Ljava/awt/image/BufferedImage;Ljava/awt/image/BufferedImage;FFII)Ljava/awt/image/BufferedImage;
        //   999: astore #14
        //   1001: aload #15
        //   1003: invokevirtual flush : ()V
        //   1006: aload_0
        //   1007: aload_1
        //   1008: getfield type : Lcom/mamiyaotaru/voxelmap/util/EnumMobs;
        //   1011: aload #14
        //   1013: aconst_null
        //   1014: checkcast java/awt/image/BufferedImage
        //   1017: aload_1
        //   1018: getfield entity : Lnet/minecraft/class_1297;
        //   1021: invokevirtual createImageFromTypeAndImages : (Lcom/mamiyaotaru/voxelmap/util/EnumMobs;Ljava/awt/image/BufferedImage;Ljava/awt/image/BufferedImage;Lnet/minecraft/class_1297;)Ljava/awt/image/BufferedImage;
        //   1024: astore #13
        //   1026: aload #14
        //   1028: invokevirtual flush : ()V
        //   1031: goto -> 1066
        //   1034: aload_1
        //   1035: getfield type : Lcom/mamiyaotaru/voxelmap/util/EnumMobs;
        //   1038: getfield expectedWidth : F
        //   1041: f2d
        //   1042: ldc2_w 0.5
        //   1045: dcmpl
        //   1046: ifle -> 1066
        //   1049: aload_0
        //   1050: aload_1
        //   1051: getfield type : Lcom/mamiyaotaru/voxelmap/util/EnumMobs;
        //   1054: aload_3
        //   1055: aload #4
        //   1057: aload_1
        //   1058: getfield entity : Lnet/minecraft/class_1297;
        //   1061: invokevirtual createImageFromTypeAndResourceLocations : (Lcom/mamiyaotaru/voxelmap/util/EnumMobs;Lnet/minecraft/class_2960;Lnet/minecraft/class_2960;Lnet/minecraft/class_1297;)Ljava/awt/image/BufferedImage;
        //   1064: astore #13
        //   1066: aload #13
        //   1068: ifnull -> 1085
        //   1071: aload_0
        //   1072: aload_1
        //   1073: aload #13
        //   1075: iconst_0
        //   1076: iconst_1
        //   1077: invokevirtual trimAndOutlineImage : (Lcom/mamiyaotaru/voxelmap/util/Contact;Ljava/awt/image/BufferedImage;ZZ)Ljava/awt/image/BufferedImage;
        //   1080: astore #13
        //   1082: goto -> 1116
        //   1085: aload_0
        //   1086: aload_1
        //   1087: aload_2
        //   1088: iconst_4
        //   1089: anewarray net/minecraft/class_2960
        //   1092: dup
        //   1093: iconst_0
        //   1094: aload_3
        //   1095: aastore
        //   1096: dup
        //   1097: iconst_1
        //   1098: aload #4
        //   1100: aastore
        //   1101: dup
        //   1102: iconst_2
        //   1103: aload #5
        //   1105: aastore
        //   1106: dup
        //   1107: iconst_3
        //   1108: aload #6
        //   1110: aastore
        //   1111: invokevirtual createAutoIconImageFromResourceLocations : (Lcom/mamiyaotaru/voxelmap/util/Contact;Lnet/minecraft/class_897;[Lnet/minecraft/class_2960;)Ljava/awt/image/BufferedImage;
        //   1114: astore #13
        //   1116: aload #13
        //   1118: ifnull -> 1218
        //   1121: aload_0
        //   1122: getfield textureAtlas : Lcom/mamiyaotaru/voxelmap/textures/TextureAtlas;
        //   1125: aload #10
        //   1127: aload #13
        //   1129: invokevirtual registerIconForBufferedImage : (Ljava/lang/String;Ljava/awt/image/BufferedImage;)Lcom/mamiyaotaru/voxelmap/textures/Sprite;
        //   1132: astore #11
        //   1134: aload_1
        //   1135: aload #11
        //   1137: putfield icon : Lcom/mamiyaotaru/voxelmap/textures/Sprite;
        //   1140: aload_0
        //   1141: iconst_1
        //   1142: putfield newMobs : Z
        //   1145: aload_0
        //   1146: getfield contactsSkinGetTries : Ljava/util/HashMap;
        //   1149: aload #10
        //   1151: invokevirtual remove : (Ljava/lang/Object;)Ljava/lang/Object;
        //   1154: pop
        //   1155: goto -> 1273
        //   1158: astore #14
        //   1160: aload #12
        //   1162: invokevirtual intValue : ()I
        //   1165: iconst_1
        //   1166: iadd
        //   1167: invokestatic valueOf : (I)Ljava/lang/Integer;
        //   1170: astore #12
        //   1172: aload #12
        //   1174: invokevirtual intValue : ()I
        //   1177: iconst_4
        //   1178: if_icmple -> 1203
        //   1181: aload_0
        //   1182: getfield textureAtlas : Lcom/mamiyaotaru/voxelmap/textures/TextureAtlas;
        //   1185: aload #10
        //   1187: invokevirtual registerFailedIcon : (Ljava/lang/String;)V
        //   1190: aload_0
        //   1191: getfield contactsSkinGetTries : Ljava/util/HashMap;
        //   1194: aload #10
        //   1196: invokevirtual remove : (Ljava/lang/Object;)Ljava/lang/Object;
        //   1199: pop
        //   1200: goto -> 1215
        //   1203: aload_0
        //   1204: getfield contactsSkinGetTries : Ljava/util/HashMap;
        //   1207: aload #10
        //   1209: aload #12
        //   1211: invokevirtual put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   1214: pop
        //   1215: goto -> 1273
        //   1218: aload #12
        //   1220: invokevirtual intValue : ()I
        //   1223: iconst_1
        //   1224: iadd
        //   1225: invokestatic valueOf : (I)Ljava/lang/Integer;
        //   1228: astore #12
        //   1230: aload #12
        //   1232: invokevirtual intValue : ()I
        //   1235: iconst_4
        //   1236: if_icmple -> 1261
        //   1239: aload_0
        //   1240: getfield textureAtlas : Lcom/mamiyaotaru/voxelmap/textures/TextureAtlas;
        //   1243: aload #10
        //   1245: invokevirtual registerFailedIcon : (Ljava/lang/String;)V
        //   1248: aload_0
        //   1249: getfield contactsSkinGetTries : Ljava/util/HashMap;
        //   1252: aload #10
        //   1254: invokevirtual remove : (Ljava/lang/Object;)Ljava/lang/Object;
        //   1257: pop
        //   1258: goto -> 1273
        //   1261: aload_0
        //   1262: getfield contactsSkinGetTries : Ljava/util/HashMap;
        //   1265: aload #10
        //   1267: aload #12
        //   1269: invokevirtual put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   1272: pop
        //   1273: goto -> 1294
        //   1276: aload #11
        //   1278: aload_0
        //   1279: getfield textureAtlas : Lcom/mamiyaotaru/voxelmap/textures/TextureAtlas;
        //   1282: invokevirtual getFailedImage : ()Lcom/mamiyaotaru/voxelmap/textures/Sprite;
        //   1285: if_acmpeq -> 1294
        //   1288: aload_1
        //   1289: aload #11
        //   1291: putfield icon : Lcom/mamiyaotaru/voxelmap/textures/Sprite;
        //   1294: return
        // Line number table:
        //   Java source line number -> byte code offset
        //   #825	-> 0
        //   #826	-> 15
        //   #827	-> 24
        //   #828	-> 34
        //   #829	-> 37
        //   #830	-> 40
        //   #831	-> 43
        //   #832	-> 48
        //   #833	-> 58
        //   #834	-> 68
        //   #835	-> 81
        //   #837	-> 92
        //   #839	-> 98
        //   #842	-> 118
        //   #843	-> 128
        //   #844	-> 134
        //   #845	-> 142
        //   #846	-> 149
        //   #847	-> 167
        //   #848	-> 174
        //   #849	-> 184
        //   #850	-> 191
        //   #851	-> 199
        //   #852	-> 206
        //   #853	-> 213
        //   #854	-> 221
        //   #855	-> 228
        //   #858	-> 239
        //   #863	-> 242
        //   #864	-> 262
        //   #866	-> 274
        //   #867	-> 295
        //   #868	-> 309
        //   #869	-> 316
        //   #870	-> 323
        //   #871	-> 333
        //   #872	-> 359
        //   #873	-> 380
        //   #874	-> 390
        //   #875	-> 416
        //   #876	-> 424
        //   #877	-> 454
        //   #881	-> 480
        //   #882	-> 488
        //   #883	-> 496
        //   #884	-> 527
        //   #885	-> 532
        //   #887	-> 535
        //   #890	-> 538
        //   #891	-> 547
        //   #892	-> 554
        //   #895	-> 571
        //   #896	-> 576
        //   #899	-> 591
        //   #900	-> 596
        //   #903	-> 611
        //   #904	-> 616
        //   #908	-> 631
        //   #909	-> 665
        //   #910	-> 702
        //   #911	-> 743
        //   #912	-> 773
        //   #913	-> 786
        //   #914	-> 797
        //   #915	-> 809
        //   #916	-> 823
        //   #917	-> 828
        //   #920	-> 834
        //   #921	-> 837
        //   #922	-> 847
        //   #923	-> 853
        //   #924	-> 858
        //   #925	-> 865
        //   #926	-> 898
        //   #927	-> 919
        //   #930	-> 924
        //   #931	-> 929
        //   #932	-> 936
        //   #933	-> 969
        //   #934	-> 980
        //   #935	-> 1001
        //   #938	-> 1006
        //   #939	-> 1026
        //   #940	-> 1031
        //   #941	-> 1049
        //   #944	-> 1066
        //   #945	-> 1071
        //   #947	-> 1085
        //   #950	-> 1116
        //   #952	-> 1121
        //   #953	-> 1134
        //   #954	-> 1140
        //   #955	-> 1145
        //   #964	-> 1155
        //   #956	-> 1158
        //   #957	-> 1160
        //   #958	-> 1172
        //   #959	-> 1181
        //   #960	-> 1190
        //   #962	-> 1203
        //   #964	-> 1215
        //   #966	-> 1218
        //   #967	-> 1230
        //   #968	-> 1239
        //   #969	-> 1248
        //   #971	-> 1261
        //   #974	-> 1273
        //   #975	-> 1288
        //   #978	-> 1294
        // Local variable table:
        //   start	length	slot	name	descriptor
        //   228	11	13	dyableHorseArmorItem	Lnet/minecraft/class_4058;
        //   206	33	12	horseArmorItem	Lnet/minecraft/class_4059;
        //   191	48	11	var30	Lnet/minecraft/class_1792;
        //   149	93	9	horse	Lnet/minecraft/class_1498;
        //   174	68	10	itemStack	Lnet/minecraft/class_1799;
        //   134	108	8	var22	Lnet/minecraft/class_1297;
        //   295	240	8	zombie	Ljava/lang/String;
        //   309	226	9	villagerData	Lnet/minecraft/class_3850;
        //   316	219	10	villagerType	Lnet/minecraft/class_3854;
        //   323	212	11	villagerProfession	Lnet/minecraft/class_3852;
        //   488	47	12	biomeHatType	Lnet/minecraft/class_3888$class_3889;
        //   496	39	13	professionHatType	Lnet/minecraft/class_3888$class_3889;
        //   527	8	14	showBiomeHat	Z
        //   547	24	8	fish	Lnet/minecraft/class_1474;
        //   865	59	15	pattern	Ljava/awt/image/BufferedImage;
        //   936	70	15	armor	Ljava/awt/image/BufferedImage;
        //   853	178	14	base	Ljava/awt/image/BufferedImage;
        //   1160	55	14	var16	Ljava/lang/Exception;
        //   823	450	12	checkCount	Ljava/lang/Integer;
        //   837	436	13	mobImage	Ljava/awt/image/BufferedImage;
        //   0	1295	0	this	Lcom/mamiyaotaru/voxelmap/Radar;
        //   0	1295	1	contact	Lcom/mamiyaotaru/voxelmap/util/Contact;
        //   15	1280	2	render	Lnet/minecraft/class_897;
        //   24	1271	3	resourceLocation	Lnet/minecraft/class_2960;
        //   37	1258	4	resourceLocationSecondary	Lnet/minecraft/class_2960;
        //   40	1255	5	resourceLocationTertiary	Lnet/minecraft/class_2960;
        //   43	1252	6	resourceLocationQuaternary	Lnet/minecraft/class_2960;
        //   48	1247	7	color	Ljava/lang/String;
        //   665	630	8	entityName	Ljava/lang/String;
        //   702	593	9	resourceLocationString	Ljava/lang/String;
        //   786	509	10	name	Ljava/lang/String;
        //   797	498	11	icon	Lcom/mamiyaotaru/voxelmap/textures/Sprite;
        // Exception table:
        //   from	to	target	type
        //   1121	1155	1158	java/lang/Exception
    }

    public class_3888.class_3889 getHatType(class_2960 resourceLocation) {
        class_3888.class_3889 hatType = class_3888.class_3889.field_17160;
        if (resourceLocation != null)
            try {
                Optional<class_3298> resource = this.game.method_1478().method_14486(resourceLocation);
                if (resource.isPresent()) {
                    class_3888 villagerResourceMetadata = (class_3888)((class_3298)resource.get()).method_14481();
                    if (villagerResourceMetadata != null)
                        hatType = villagerResourceMetadata.method_17167();
                    ((class_3298)resource.get()).method_43039().close();
                }
            } catch (IOException|ClassCastException ignored) {
                hatType = class_3888.class_3889.field_17160;
            }
        return hatType;
    }

    private BufferedImage createAutoIconImageFromResourceLocations(Contact contact, class_897 render, class_2960... resourceLocations) {
        BufferedImage headImage = null;
        class_583 model = null;
        if (render instanceof class_922)
            try {
                model = ((class_922)render).method_4038();
                ArrayList<Field> submodels = ReflectionUtils.getFieldsByType(model, class_3879.class, class_630.class);
                ArrayList<Field> submodelArrays = ReflectionUtils.getFieldsByType(model, class_3879.class, class_630[].class);
                class_630[] headBits = null;
                ArrayList<ModelPartWithResourceLocation> headPartsWithResourceLocationList = new ArrayList();
                Properties properties = new Properties();
                String fullName = contact.vanillaType ? ("minecraft." + contact.type.id) : contact.entity.getClass().getName();
                String simpleName = contact.vanillaType ? contact.type.id : contact.entity.getClass().getSimpleName();
                String fullPath = ("textures/icons/" + fullName + ".properties").toLowerCase();
                class_3300 resourceManager = this.game.method_1478();
                Optional<class_3298> resource = resourceManager.method_14486(new class_2960(fullPath));
                if (resource.isEmpty()) {
                    fullPath = ("textures/icons/" + simpleName + ".properties").toLowerCase();
                    resource = resourceManager.method_14486(new class_2960(fullPath));
                }
                if (resource.isPresent()) {
                    InputStream is = ((class_3298)resource.get()).method_14482();
                    try {
                        properties.load(is);
                        is.close();
                        String subModelNames = properties.getProperty("models", "").toLowerCase();
                        String[] submodelNamesArray = subModelNames.split(",");
                        List<String> subModelNamesList = Arrays.asList(submodelNamesArray);
                        HashSet<String> subModelNamesSet = new HashSet();
                        subModelNamesSet.addAll(subModelNamesList);
                        ArrayList<class_630> headPartsArrayList = new ArrayList();
                        for (Field submodelArray : submodelArrays) {
                            String name = submodelArray.getName().toLowerCase();
                            if (subModelNamesSet.contains(name) || subModelNames.equals("all")) {
                                class_630[] submodelArrayValue = (class_630[])submodelArray.get(model);
                                if (submodelArrayValue != null)
                                    for (int t = 0; t < submodelArrayValue.length; t++)
                                        headPartsArrayList.add(submodelArrayValue[t]);
                            }
                        }
                        for (Field submodel : submodels) {
                            String name = submodel.getName().toLowerCase();
                            if ((subModelNamesSet.contains(name) || subModelNames.equals("all")) && submodel.get(model) != null)
                                headPartsArrayList.add((class_630)submodel.get(model));
                        }
                        if (headPartsArrayList.size() > 0)
                            headBits = headPartsArrayList.<class_630>toArray(new class_630[headPartsArrayList.size()]);
                        if (is != null)
                            is.close();
                    } catch (Throwable throwable) {
                        if (is != null)
                            try {
                                is.close();
                            } catch (Throwable throwable1) {
                                throwable.addSuppressed(throwable1);
                            }
                        throw throwable;
                    }
                }
                if (headBits == null)
                    if (model instanceof class_591) {
                        boolean showHat = true;
                        class_1297 var39 = contact.entity;
                        if (var39 instanceof class_1657) {
                            class_1657 player = (class_1657)var39;
                            showHat = player.method_7348(class_1664.field_7563);
                        }
                        if (showHat) {
                            headBits = new class_630[] { ((class_591)model).field_3398, ((class_591)model).field_3394 };
                        } else {
                            headBits = new class_630[] { ((class_591)model).field_3398 };
                        }
                    } else if (contact.type == EnumMobs.STRAY) {
                        headPartsWithResourceLocationList.add(new ModelPartWithResourceLocation(((class_606)model).field_3398, resourceLocations[0]));
                        headPartsWithResourceLocationList.add(new ModelPartWithResourceLocation(((class_606)model).field_3394, resourceLocations[0]));
                        headPartsWithResourceLocationList.add(new ModelPartWithResourceLocation(this.strayOverlayModel.field_3398, resourceLocations[1]));
                        headPartsWithResourceLocationList.add(new ModelPartWithResourceLocation(this.strayOverlayModel.field_3394, resourceLocations[1]));
                    } else if (contact.type == EnumMobs.DROWNED) {
                        headPartsWithResourceLocationList.add(new ModelPartWithResourceLocation(((class_564)model).field_3398, resourceLocations[0]));
                        headPartsWithResourceLocationList.add(new ModelPartWithResourceLocation(((class_564)model).field_3394, resourceLocations[0]));
                        headPartsWithResourceLocationList.add(new ModelPartWithResourceLocation(this.drownedOverlayModel.field_3398, resourceLocations[1]));
                        headPartsWithResourceLocationList.add(new ModelPartWithResourceLocation(this.drownedOverlayModel.field_3394, resourceLocations[1]));
                    } else if (model instanceof class_5772) {
                        headBits = new class_630[] { (class_630)ReflectionUtils.getPrivateFieldValueByType(model, class_5772.class, class_630.class, 6) };
                    } else if (model instanceof class_553) {
                        class_553 batEntityModel = (class_553)model;
                        headBits = new class_630[] { batEntityModel.method_32008().method_32086("head") };
                    } else if (model instanceof class_4495) {
                        headBits = new class_630[] { ((class_630)ReflectionUtils.getPrivateFieldValueByType(model, class_4495.class, class_630.class, 0)).method_32086("body") };
                    } else if (model instanceof class_572) {
                        class_572 bipedEntityModel = (class_572)model;
                        headBits = new class_630[] { bipedEntityModel.field_3398, bipedEntityModel.field_3394 };
                    } else if (model instanceof class_555) {
                        class_555 blazeEntityModel = (class_555)model;
                        headBits = new class_630[] { blazeEntityModel.method_32008().method_32086("head") };
                    } else if (model instanceof class_558) {
                        headBits = new class_630[] { (class_630)ReflectionUtils.getPrivateFieldValueByType(model, class_558.class, class_630.class) };
                    } else if (model instanceof class_562) {
                        class_562 creeperEntityModel = (class_562)model;
                        headBits = new class_630[] { creeperEntityModel.method_32008().method_32086("head") };
                    } else if (model instanceof class_889) {
                        class_889 dolphinEntityModel = (class_889)model;
                        headBits = new class_630[] { dolphinEntityModel.method_32008().method_32086("body").method_32086("head") };
                    } else if (model instanceof class_565) {
                        class_565 endermiteEntityModel = (class_565)model;
                        headBits = new class_630[] { endermiteEntityModel.method_32008().method_32086("segment0"), endermiteEntityModel.method_32008().method_32086("segment1") };
                    } else if (model instanceof class_567) {
                        class_567 ghastEntityModel = (class_567)model;
                        headBits = new class_630[] { ghastEntityModel.method_32008() };
                    } else if (model instanceof class_570) {
                        class_570 guardianEntityModel = (class_570)model;
                        headBits = new class_630[] { guardianEntityModel.method_32008().method_32086("head") };
                    } else if (model instanceof class_4791) {
                        headBits = new class_630[] { (class_630)ReflectionUtils.getPrivateFieldValueByType(model, class_4791.class, class_630.class) };
                    } else if (model instanceof class_549) {
                        class_549 horseEntityModel = (class_549)model;
                        headBits = (class_630[])StreamSupport.stream(horseEntityModel.method_22946().spliterator(), false).toArray(x$0 -> new class_630[x$0]);
                    } else if (model instanceof class_575) {
                        class_575 illagerEntityModel = (class_575)model;
                        headBits = new class_630[] { illagerEntityModel.method_32008().method_32086("head") };
                    } else if (model instanceof class_574) {
                        class_574 ironGolemEntityModel = (class_574)model;
                        headBits = new class_630[] { ironGolemEntityModel.method_32008().method_32086("head") };
                    } else if (model instanceof class_576) {
                        headBits = (class_630[])ReflectionUtils.getPrivateFieldValueByType(model, class_576.class, class_630[].class);
                    } else if (model instanceof class_582) {
                        headBits = new class_630[] { (class_630)ReflectionUtils.getPrivateFieldValueByType(model, class_582.class, class_630.class, 6) };
                    } else if (model instanceof class_588) {
                        class_588 phantomEntityModel = (class_588)model;
                        headBits = new class_630[] { phantomEntityModel.method_32008().method_32086("body") };
                    } else if (model instanceof class_596) {
                        headBits = new class_630[] { (class_630)ReflectionUtils.getPrivateFieldValueByType(model, class_596.class, class_630.class, 7), (class_630)ReflectionUtils.getPrivateFieldValueByType(model, class_596.class, class_630.class, 8), (class_630)ReflectionUtils.getPrivateFieldValueByType(model, class_596.class, class_630.class, 9), (class_630)ReflectionUtils.getPrivateFieldValueByType(model, class_596.class, class_630.class, 11) };
                    } else if (model instanceof class_571) {
                        class_571 ravagerEntityModel = (class_571)model;
                        headBits = new class_630[] { ravagerEntityModel.method_32008().method_32086("neck").method_32086("head") };
                    } else if (model instanceof class_602) {
                        class_602 shulkerEntityModel = (class_602)model;
                        headBits = new class_630[] { shulkerEntityModel.method_2830() };
                    } else if (model instanceof class_604) {
                        class_604 silverFishEntityModel = (class_604)model;
                        headBits = new class_630[] { silverFishEntityModel.method_32008().method_32086("segment0"), silverFishEntityModel.method_32008().method_32086("segment1") };
                    } else if (model instanceof class_609) {
                        class_609 slimeEntityModel = (class_609)model;
                        headBits = new class_630[] { slimeEntityModel.method_32008() };
                    } else if (model instanceof class_608) {
                        class_608 snowGolemEntityModel = (class_608)model;
                        headBits = new class_630[] { snowGolemEntityModel.method_32008().method_32086("head") };
                    } else if (model instanceof class_611) {
                        class_611 spiderEntityModel = (class_611)model;
                        headBits = new class_630[] { spiderEntityModel.method_32008().method_32086("head"), spiderEntityModel.method_32008().method_32086("body0") };
                    } else if (model instanceof class_610) {
                        class_610 squidEntityModel = (class_610)model;
                        headBits = new class_630[] { squidEntityModel.method_32008().method_32086("body") };
                    } else if (model instanceof class_4997) {
                        class_4997 striderEntityModel = (class_4997)model;
                        headBits = new class_630[] { striderEntityModel.method_32008().method_32086("body") };
                    } else if (model instanceof class_620) {
                        class_620 villagerResemblingModel = (class_620)model;
                        headBits = new class_630[] { villagerResemblingModel.method_2838() };
                    } else if (model instanceof class_624) {
                        headBits = new class_630[] { (class_630)ReflectionUtils.getPrivateFieldValueByType(model, class_624.class, class_630.class) };
                    } else if (model instanceof class_597) {
                        headBits = new class_630[] { (class_630)ReflectionUtils.getPrivateFieldValueByType(model, class_597.class, class_630.class) };
                    } else if (model instanceof class_5597) {
                        class_5597 singlePartEntityModel = (class_5597)model;
                        try {
                            headBits = new class_630[] { singlePartEntityModel.method_32008().method_32086("head") };
                        } catch (Exception exception) {}
                    }
                if (headBits == null) {
                    ArrayList<class_630> headPartsArrayList = new ArrayList<>();
                    ArrayList<?> purge = new ArrayList();
                    for (Field submodelArray : submodelArrays) {
                        String name = submodelArray.getName().toLowerCase();
                        if ((name.contains("head") | name.contains("eye") | name.contains("mouth") | name.contains("teeth") | name.contains("tooth") | name.contains("tusk") | name.contains("jaw") | name.contains("mand") | name.contains("nose") | name.contains("beak") | name.contains("snout") | name.contains("muzzle") | ((!name.contains("rear") && name.contains("ear")) ? 1 : 0) | name.contains("trunk") | name.contains("mane") | name.contains("horn") | name.contains("antler")) != 0) {
                            class_630[] submodelArrayValue = (class_630[])submodelArray.get(model);
                            if (submodelArrayValue != null && submodelArrayValue.length >= 0)
                                headPartsArrayList.add(submodelArrayValue[0]);
                        }
                    }
                    for (Field submodel : submodels) {
                        String name = submodel.getName().toLowerCase();
                        String nameS = submodel.getName();
                        if ((name.contains("head") | name.contains("eye") | name.contains("mouth") | name.contains("teeth") | name.contains("tooth") | name.contains("tusk") | name.contains("jaw") | name.contains("mand") | name.contains("nose") | name.contains("beak") | name.contains("snout") | name.contains("muzzle") | ((!name.contains("rear") && name.contains("ear")) ? 1 : 0) | name.contains("trunk") | name.contains("mane") | name.contains("horn") | name.contains("antler") | nameS.equals("REar") | nameS.equals("Trout")) != 0)
                            if (((!nameS.equals("LeftSmallEar") ? 1 : 0) & (!nameS.equals("RightSmallEar") ? 1 : 0) & (!nameS.equals("BHead") ? 1 : 0) & (!nameS.equals("BSnout") ? 1 : 0) & (!nameS.equals("BMouth") ? 1 : 0) & (!nameS.equals("BMouthOpen") ? 1 : 0) & (!nameS.equals("BLEar") ? 1 : 0) & (!nameS.equals("BREar") ? 1 : 0) & (!nameS.equals("CHead") ? 1 : 0) & (!nameS.equals("CSnout") ? 1 : 0) & (!nameS.equals("CMouth") ? 1 : 0) & (!nameS.equals("CMouthOpen") ? 1 : 0) & (!nameS.equals("CLEar") ? 1 : 0) & (!nameS.equals("CREar") ? 1 : 0)) != 0 && submodel.get(model) != null)
                                headPartsArrayList.add((class_630)submodel.get(model));
                    }
                    if (headPartsArrayList.size() == 0) {
                        int pos = (model instanceof class_5597) ? 1 : 0;
                        if (submodels.size() > pos) {
                            if (((Field)submodels.get(pos)).get(model) != null)
                                headPartsArrayList.add((class_630)((Field)submodels.get(pos)).get(model));
                        } else if (submodelArrays.size() > 0 && ((Field)submodelArrays.get(0)).get(model) != null) {
                            class_630[] submodelArrayValue = (class_630[])((Field)submodelArrays.get(0)).get(model);
                            if (submodelArrayValue.length > 0)
                                headPartsArrayList.add(submodelArrayValue[0]);
                        }
                    }
                    for (class_630 bit : headPartsArrayList) {
                        try {
                            Object childrenObj = ReflectionUtils.getPrivateFieldValueByType(bit, class_630.class, ObjectList.class, 1);
                            if (childrenObj != null) {
                                List children = (List)childrenObj;
                                purge.addAll(children);
                            }
                        } catch (Exception exception) {}
                    }
                    headPartsArrayList.removeAll(purge);
                    headBits = headPartsArrayList.<class_630>toArray(new class_630[headPartsArrayList.size()]);
                }
                if (contact.entity != null && model != null && ((headBits != null && headBits.length > 0) || headPartsWithResourceLocationList.size() > 0) && resourceLocations[0] != null) {
                    String scaleString = properties.getProperty("scale", "1");
                    float scale = Float.parseFloat(scaleString);
                    class_2350 facing = class_2350.field_11043;
                    String facingString = properties.getProperty("facing", "front");
                    if (facingString.equals("top")) {
                        facing = class_2350.field_11036;
                    } else if (facingString.equals("side")) {
                        facing = class_2350.field_11034;
                    }
                    class_2960 resourceLocation = combineResourceLocations(resourceLocations);
                    if (headBits != null)
                        for (int t = 0; t < headBits.length; t++)
                            headPartsWithResourceLocationList.add(new ModelPartWithResourceLocation(headBits[t], resourceLocation));
                    ModelPartWithResourceLocation[] headBitsWithLocations = headPartsWithResourceLocationList.<ModelPartWithResourceLocation>toArray(new ModelPartWithResourceLocation[headPartsWithResourceLocationList.size()]);
                    boolean success = drawModel(scale, 1000, (class_1309)contact.entity, facing, (class_3879)model, headBitsWithLocations);
                    ImageUtils.saveImage(contact.type.id, GLUtils.fboTextureID, 0, 512, 512);
                    if (success)
                        headImage = ImageUtils.createBufferedImageFromGLID(GLUtils.fboTextureID);
                }
            } catch (Exception var44) {
                headImage = null;
                var44.printStackTrace();
            }
        if (headImage != null)
            headImage = trimAndOutlineImage(contact, headImage, true, (model != null && model instanceof class_572));
        return headImage;
    }

    private class_2960 combineResourceLocations(class_2960... resourceLocations) {
        class_2960 resourceLocation = resourceLocations[0];
        if (resourceLocations.length > 1) {
            boolean hasAdditional = false;
            try {
                BufferedImage base = null;
                for (int t = 1; t < resourceLocations.length; t++) {
                    if (resourceLocations[t] != null) {
                        if (!hasAdditional)
                            base = ImageUtils.createBufferedImageFromResourceLocation(resourceLocation);
                        hasAdditional = true;
                        BufferedImage overlay = ImageUtils.createBufferedImageFromResourceLocation(resourceLocations[t]);
                        float xScale = (base.getWidth() / overlay.getWidth());
                        float yScale = (base.getHeight() / overlay.getHeight());
                        if (xScale != 1.0F || yScale != 1.0F)
                            overlay = ImageUtils.scaleImage(overlay, xScale, yScale);
                        base = ImageUtils.addImages(base, overlay, 0.0F, 0.0F, base.getWidth(), base.getHeight());
                        overlay.flush();
                    }
                }
                if (hasAdditional) {
                    class_1011 nativeImage = GLUtils.nativeImageFromBufferedImage(base);
                    base.flush();
                    this.nativeBackedTexture.close();
                    this.nativeBackedTexture = new class_1043(nativeImage);
                    GLUtils.register(this.nativeBackedTextureLocation, (class_1044)this.nativeBackedTexture);
                    resourceLocation = this.nativeBackedTextureLocation;
                }
            } catch (Exception var9) {
                var9.printStackTrace();
            }
        }
        return resourceLocation;
    }

    private boolean drawModel(float scale, int captureDepth, class_1309 livingEntity, class_2350 facing, class_3879 model, ModelPartWithResourceLocation[] headBits) {
        boolean failed = false;
        float size = 64.0F * scale;
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
        matrixStack.method_22904(0.0D, 0.0D, -3000.0D + captureDepth);
        RenderSystem.applyModelViewMatrix();
        GLUtils.bindFrameBuffer();
        GLShim.glDepthMask(true);
        GLShim.glEnable(2929);
        GLShim.glEnable(3553);
        GLShim.glEnable(3042);
        GLShim.glDisable(2884);
        GLShim.glClearColor(1.0F, 1.0F, 1.0F, 0.0F);
        GLShim.glClearDepth(1.0D);
        GLShim.glClear(16640);
        GLShim.glBlendFunc(770, 771);
        matrixStack.method_22903();
        matrixStack.method_22904((width / 2), (height / 2), 0.0D);
        matrixStack.method_22905(size, size, size);
        matrixStack.method_22907(class_1160.field_20707.method_23214(180.0F));
        matrixStack.method_22907(class_1160.field_20705.method_23214(180.0F));
        if (facing == class_2350.field_11034) {
            matrixStack.method_22907(class_1160.field_20705.method_23214(-90.0F));
        } else if (facing == class_2350.field_11036) {
            matrixStack.method_22907(class_1160.field_20703.method_23214(90.0F));
        }
        RenderSystem.applyModelViewMatrix();
        class_1162 fullbright2 = new class_1162(this.fullbright);
        fullbright2.method_22674(matrixStack.method_23760().method_23761());
        class_1160 fullbright3 = new class_1160(fullbright2);
        RenderSystem.setShaderLights(fullbright3, fullbright3);
        try {
            class_4587 newMatrixStack = new class_4587();
            class_4597.class_4598 immediate = this.game.method_22940().method_23000();
            float offsetByY = (model instanceof net.minecraft.class_566) ? 8.0F : ((!(model instanceof class_572) && !(model instanceof class_607)) ? 0.0F : 4.0F);
            float maxY = 0.0F;
            float minY = 0.0F;
            int t;
            for (t = 0; t < headBits.length; t++) {
                if ((headBits[t]).modelPart.field_3656 < minY)
                    minY = (headBits[t]).modelPart.field_3656;
                if ((headBits[t]).modelPart.field_3656 > maxY)
                    maxY = (headBits[t]).modelPart.field_3656;
            }
            if (minY < -25.0F) {
                offsetByY = -25.0F - minY;
            } else if (maxY > 25.0F) {
                offsetByY = 25.0F - maxY;
            }
            for (t = 0; t < headBits.length; t++) {
                class_4588 vertexConsumer = immediate.getBuffer(model.method_23500((headBits[t]).resourceLocation));
                if (model instanceof class_583) {
                    class_583 entityModel = (class_583)model;
                    entityModel.method_2819((class_1297)livingEntity, 0.0F, 0.0F, 163.0F, 360.0F, 0.0F);
                }
                float y = (headBits[t]).modelPart.field_3656;
                (headBits[t]).modelPart.field_3656 += offsetByY;
                (headBits[t]).modelPart.method_22698(newMatrixStack, vertexConsumer, 15728880, class_4608.field_21444);
                (headBits[t]).modelPart.field_3656 = y;
                immediate.method_22993();
            }
        } catch (Exception var25) {
            System.out.println("Error attempting to render head bits for " + livingEntity.getClass().getSimpleName());
            var25.printStackTrace();
            failed = true;
        }
        matrixStack.method_22909();
        matrixStack.method_22909();
        RenderSystem.applyModelViewMatrix();
        GLShim.glEnable(2884);
        GLShim.glDisable(2929);
        GLShim.glDepthMask(false);
        GLUtils.unbindFrameBuffer();
        RenderSystem.setProjectionMatrix(minimapProjectionMatrix);
        GLShim.glViewport(0, 0, this.game.method_22683().method_4489(), this.game.method_22683().method_4506());
        return !failed;
    }

    private void getGenericIcon(Contact contact) {
        contact.type = getUnknownMobNeutrality(contact.entity);
        String name = "minecraft." + contact.type.id + contact.type.resourceLocation.toString();
        contact.icon = this.textureAtlas.getAtlasSprite(name);
    }

    private class_2960 getRandomizedResourceLocationForEntity(class_2960 resourceLocation, class_1297 entity) {
        try {
            if (this.randomobsOptifine) {
                Object randomEntitiesProperties = this.mapProperties.get(resourceLocation.method_12832());
                if (randomEntitiesProperties != null) {
                    this.setEntityMethod.invoke(this.randomEntityClass.cast(this.randomEntity), new Object[] { entity });
                    resourceLocation = (class_2960)this.getEntityTextureMethod.invoke(this.randomEntitiesPropertiesClass.cast(randomEntitiesProperties), new Object[] { resourceLocation, this.randomEntityClass.cast(this.randomEntity) });
                }
            }
        } catch (Exception exception) {}
        return resourceLocation;
    }

    private BufferedImage trimAndOutlineImage(Contact contact, BufferedImage image, boolean auto, boolean centered) {
        if (auto) {
            image = centered ? ImageUtils.trimCentered(image) : ImageUtils.trim(image);
            double acceptableMax = 64.0D;
            if (ImageUtils.percentageOfEdgePixelsThatAreSolid(image) < 30.0F)
                acceptableMax = 128.0D;
            int maxDimension = Math.max(image.getWidth(), image.getHeight());
            float f = (float)Math.ceil(maxDimension / acceptableMax);
            return ImageUtils.fillOutline(ImageUtils.pad(ImageUtils.scaleImage(image, 1.0F / f)), this.options.outlines, 2);
        }
        float scale = image.getWidth() / contact.type.expectedWidth;
        return ImageUtils.fillOutline(ImageUtils.pad(ImageUtils.scaleImage(image, 4.0F / scale)), this.options.outlines, 2);
    }

    private void handleMPplayer(Contact contact) {
        class_742 player = (class_742)contact.entity;
        GameProfile gameProfile = player.method_7334();
        UUID uuid = gameProfile.getId();
        contact.setUUID(uuid);
        String playerName = scrubCodes(gameProfile.getName());
        Sprite icon = this.textureAtlas.getAtlasSprite(playerName);
        Integer checkCount = Integer.valueOf(0);
        if (icon == this.textureAtlas.getMissingImage()) {
            checkCount = (Integer)this.mpContactsSkinGetTries.get(playerName);
            if (checkCount == null)
                checkCount = Integer.valueOf(0);
            if (checkCount.intValue() < 5) {
                class_1046 imageData = null;
                try {
                    if (player.method_3117() == class_1068.method_4648(player.method_5667()))
                        throw new Exception("failed to get skin: skin is default");
                    class_742.method_3120(player.method_3117(), player.method_5477().getString());
                    imageData = (class_1046)class_310.method_1551().method_1531().method_4619(player.method_3117());
                    if (imageData == null)
                        throw new Exception("failed to get skin: image data was null");
                    class_897 render = this.game.method_1561().method_3953(contact.entity);
                    BufferedImage skinImage = createAutoIconImageFromResourceLocations(contact, render, new class_2960[] { player.method_3117(), null });
                    icon = this.textureAtlas.registerIconForBufferedImage(playerName, skinImage);
                    this.newMobs = true;
                    this.mpContactsSkinGetTries.remove(playerName);
                } catch (Exception var11) {
                    icon = this.textureAtlas.getAtlasSprite("minecraft." + EnumMobs.PLAYER.id + EnumMobs.PLAYER.resourceLocation.toString());
                    checkCount = Integer.valueOf(checkCount.intValue() + 1);
                    this.mpContactsSkinGetTries.put(playerName, checkCount);
                }
                contact.icon = icon;
            }
        } else {
            contact.icon = icon;
        }
    }

    private void getArmor(Contact contact, class_1297 entity) {
        Sprite icon = null;
        class_1799 stack = ((class_1309)entity).method_6118(class_1304.field_6169);
        class_1792 helmet = null;
        if (stack != null && stack.method_7947() > 0)
            helmet = stack.method_7909();
        if (contact.type == EnumMobs.SHEEP) {
            class_1472 sheepEntity = (class_1472)contact.entity;
            if (!sheepEntity.method_6629()) {
                icon = this.textureAtlas.getAtlasSprite("sheepfur");
                float[] sheepColors = class_1472.method_6634(sheepEntity.method_6633());
                contact.setArmorColor((int)(sheepColors[0] * 255.0F) << 16 | (int)(sheepColors[1] * 255.0F) << 8 | (int)(sheepColors[2] * 255.0F));
            }
        } else if (helmet != null) {
            if (helmet == class_1802.field_8398) {
                icon = this.textureAtlas.getAtlasSprite("minecraft." + EnumMobs.SKELETON.id + EnumMobs.SKELETON.resourceLocation.toString() + "head");
            } else if (helmet == class_1802.field_8791) {
                icon = this.textureAtlas.getAtlasSprite("minecraft." + EnumMobs.SKELETONWITHER.id + EnumMobs.SKELETONWITHER.resourceLocation.toString() + "head");
            } else if (helmet == class_1802.field_8470) {
                icon = this.textureAtlas.getAtlasSprite("minecraft." + EnumMobs.ZOMBIE.id + EnumMobs.ZOMBIE.resourceLocation.toString() + "head");
            } else if (helmet == class_1802.field_8681) {
                icon = this.textureAtlas.getAtlasSprite("minecraft." + EnumMobs.CREEPER.id + EnumMobs.CREEPER.resourceLocation.toString() + "head");
            } else if (helmet == class_1802.field_8712) {
                icon = this.textureAtlas.getAtlasSprite("minecraft." + EnumMobs.ENDERDRAGON.id + EnumMobs.ENDERDRAGON.resourceLocation.toString() + "head");
            } else if (helmet == class_1802.field_8575) {
                GameProfile gameProfile = null;
                if (stack.method_7985()) {
                    class_2487 nbttagcompound = stack.method_7969();
                    if (nbttagcompound.method_10573("SkullOwner", 10)) {
                        gameProfile = class_2512.method_10683(nbttagcompound.method_10562("SkullOwner"));
                    } else if (nbttagcompound.method_10573("SkullOwner", 8)) {
                        String name = nbttagcompound.method_10558("SkullOwner");
                        if (name != null && !name.equals("")) {
                            gameProfile = new GameProfile((UUID)null, name);
                            nbttagcompound.method_10551("SkullOwner");
                            class_2631.method_11335(gameProfile, gameProfilex -> nbttagcompound.method_10566("SkullOwner", (class_2520)class_2512.method_10684(new class_2487(), gameProfilex)));
                        }
                    }
                }
                class_2960 resourceLocation = class_1068.method_4649();
                if (gameProfile != null) {
                    Map map = this.game.method_1582().method_4654(gameProfile);
                    if (map.containsKey(MinecraftProfileTexture.Type.SKIN))
                        resourceLocation = this.game.method_1582().method_4656((MinecraftProfileTexture)map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
                }
                icon = this.textureAtlas.getAtlasSpriteIncludingYetToBeStitched("minecraft." + EnumMobs.PLAYER.id + resourceLocation.toString() + "head");
                if (icon == this.textureAtlas.getMissingImage()) {
                    class_630 inner = (class_630)ReflectionUtils.getPrivateFieldValueByType(this.playerSkullModel, class_607.class, class_630.class, 0);
                    class_630 outer = (class_630)ReflectionUtils.getPrivateFieldValueByType(this.playerSkullModel, class_607.class, class_630.class, 1);
                    ModelPartWithResourceLocation[] headBits = { new ModelPartWithResourceLocation(inner, resourceLocation), new ModelPartWithResourceLocation(outer, resourceLocation) };
                    boolean success = drawModel(1.1875F, 1000, (class_1309)contact.entity, class_2350.field_11043, (class_3879)this.playerSkullModel, headBits);
                    if (success) {
                        BufferedImage headImage = ImageUtils.createBufferedImageFromGLID(GLUtils.fboTextureID);
                        headImage = trimAndOutlineImage(new Contact((class_1297)this.game.field_1724, EnumMobs.PLAYER), headImage, true, true);
                        icon = this.textureAtlas.registerIconForBufferedImage("minecraft." + EnumMobs.PLAYER.id + resourceLocation.toString() + "head", headImage);
                        this.newMobs = true;
                    }
                }
            } else if (helmet instanceof class_1738) {
                class_1738 helmetArmor = (class_1738)helmet;
                int armorType = getArmorType(helmetArmor);
                if (armorType != UNKNOWN) {
                    icon = this.textureAtlas.getAtlasSprite("armor " + this.armorNames[armorType]);
                } else {
                    boolean isPiglin = (contact.type == EnumMobs.PIGLIN || contact.type == EnumMobs.PIGLINZOMBIE);
                    icon = this.textureAtlas.getAtlasSprite("armor " + helmet.method_7876() + (isPiglin ? "_piglin" : ""));
                    if (icon == this.textureAtlas.getMissingImage()) {
                        icon = createUnknownArmorIcons(contact, stack, helmet);
                    } else if (icon == this.textureAtlas.getFailedImage()) {
                        icon = null;
                    }
                }
                if (helmetArmor instanceof class_4057) {
                    class_4057 dyeableHelmetArmor = (class_4057)helmetArmor;
                    contact.setArmorColor(dyeableHelmetArmor.method_7800(stack));
                }
            } else if (helmet instanceof class_1747) {
                class_1747 blockItem = (class_1747)helmet;
                class_2248 block = blockItem.method_7711();
                class_2680 blockState = block.method_9564();
                int stateID = class_2248.method_9507(blockState);
                icon = this.textureAtlas.getAtlasSprite("blockArmor " + stateID);
                if (icon == this.textureAtlas.getMissingImage()) {
                    BufferedImage blockImage = this.master.getColorManager().getBlockImage(blockState, stack, entity.field_6002, 4.9473686F, -8.0F);
                    if (blockImage != null) {
                        int width = blockImage.getWidth();
                        int height = blockImage.getHeight();
                        blockImage = ImageUtils.eraseArea(blockImage, width / 2 - 15, height / 2 - 15, 30, 30, width, height);
                        BufferedImage blockImageFront = this.master.getColorManager().getBlockImage(blockState, stack, entity.field_6002, 4.9473686F, 7.25F);
                        blockImageFront = blockImageFront.getSubimage(width / 2 - 15, height / 2 - 15, 30, 30);
                        blockImage = ImageUtils.addImages(blockImage, blockImageFront, (width / 2 - 15), (height / 2 - 15), width, height);
                        blockImageFront.flush();
                        blockImage = ImageUtils.fillOutline(ImageUtils.pad(ImageUtils.trimCentered(blockImage)), this.options.outlines, true, 37.6F, 37.6F, 2);
                        icon = this.textureAtlas.registerIconForBufferedImage("blockArmor " + stateID, blockImage);
                        this.newMobs = true;
                    }
                }
            }
        }
        contact.armorIcon = icon;
    }

    private Sprite createUnknownArmorIcons(Contact contact, class_1799 stack, class_1792 helmet) {
        Sprite icon = null;
        boolean isPiglin = (contact.type == EnumMobs.PIGLIN || contact.type == EnumMobs.PIGLINZOMBIE);
        Method m = null;
        try {
            Class<?> c = Class.forName("net.minecraftforge.client.ForgeHooksClient");
            m = c.getMethod("getArmorTexture", new Class[] { class_1297.class, class_1799.class, String.class, class_1304.class, String.class });
        } catch (Exception exception) {}
        Method getResourceLocation = m;
        class_2960 resourceLocation = null;
        try {
            String materialName = ((class_1738)helmet).method_7686().method_7694();
            String domain = "minecraft";
            int sep = materialName.indexOf(':');
            if (sep != -1) {
                domain = materialName.substring(0, sep);
                materialName = materialName.substring(sep + 1);
            }
            String suffix = null;
            suffix = (suffix == null) ? "" : ("_" + suffix);
            String resourcePath = String.format("%s:textures/models/armor/%s_layer_%d%s.png", new Object[] { domain, materialName, Integer.valueOf(1), suffix });
            if (getResourceLocation != null)
                resourcePath = (String)getResourceLocation.invoke(null, new Object[] { contact.entity, stack, resourcePath, class_1304.field_6169, null });
            resourceLocation = new class_2960(resourcePath);
        } catch (Exception exception) {}
        m = null;
        try {
            Class<?> c = Class.forName("net.minecraftforge.client.ForgeHooksClient");
            m = c.getMethod("getArmorModel", new Class[] { class_1309.class, class_1799.class, class_1304.class, class_572.class });
        } catch (Exception exception) {}
        Method getModel = m;
        class_572 modelBiped = null;
        try {
            if (getModel != null)
                modelBiped = (class_572)getModel.invoke(null, new Object[] { contact.entity, stack, class_1304.field_6169, null });
        } catch (Exception exception) {}
        float intendedWidth = 9.0F;
        float intendedHeight = 9.0F;
        if (modelBiped == null)
            if (!isPiglin) {
                modelBiped = this.bipedArmorModel;
            } else {
                modelBiped = this.piglinArmorModel;
                intendedWidth = 11.5F;
            }
        if (modelBiped != null && resourceLocation != null) {
            ModelPartWithResourceLocation[] headBitsWithResourceLocation = { new ModelPartWithResourceLocation(modelBiped.field_3398, resourceLocation), new ModelPartWithResourceLocation(modelBiped.field_3394, resourceLocation) };
            drawModel(1.0F, 2, (class_1309)contact.entity, class_2350.field_11043, (class_3879)modelBiped, headBitsWithResourceLocation);
            BufferedImage armorImage = ImageUtils.createBufferedImageFromGLID(GLUtils.fboTextureID);
            armorImage = armorImage.getSubimage(200, 200, 112, 112);
            armorImage = ImageUtils.fillOutline(ImageUtils.pad(ImageUtils.trimCentered(armorImage)), this.options.outlines, true, intendedWidth * 4.0F, intendedHeight * 4.0F, 2);
            icon = this.textureAtlas.registerIconForBufferedImage("armor " + helmet.method_7876() + (isPiglin ? "_piglin" : ""), armorImage);
            this.newMobs = true;
        }
        if (icon == null && resourceLocation != null) {
            BufferedImage armorTexture = ImageUtils.createBufferedImageFromResourceLocation(resourceLocation);
            if (armorTexture != null) {
                if (!isPiglin) {
                    armorTexture = ImageUtils.addImages(ImageUtils.loadImage(armorTexture, 8, 8, 8, 8), ImageUtils.loadImage(armorTexture, 40, 8, 8, 8), 0.0F, 0.0F, 8, 8);
                    float scale = armorTexture.getWidth() / 8.0F;
                    BufferedImage armorImage = ImageUtils.fillOutline(ImageUtils.pad(ImageUtils.scaleImage(armorTexture, 4.0F / scale * 47.0F / 38.0F)), this.options.outlines, true, 37.6F, 37.6F, 2);
                    icon = this.textureAtlas.registerIconForBufferedImage("armor " + resourceLocation.toString(), armorImage);
                } else {
                    armorTexture = ImageUtils.addImages(ImageUtils.loadImage(armorTexture, 8, 8, 8, 8), ImageUtils.loadImage(armorTexture, 40, 8, 8, 8), 0.0F, 0.0F, 8, 8);
                    float scale = armorTexture.getWidth() / 8.0F;
                    BufferedImage armorImage = ImageUtils.fillOutline(ImageUtils.pad(ImageUtils.scaleImage(armorTexture, 4.0F / scale * 47.0F / 38.0F)), this.options.outlines, true, 47.0F, 37.6F, 2);
                    icon = this.textureAtlas.registerIconForBufferedImage("armor " + resourceLocation.toString() + "_piglin", armorImage);
                }
                this.newMobs = true;
            }
        }
        if (icon == null) {
            System.out.println("can't get texture for custom armor type: " + helmet.getClass());
            this.textureAtlas.registerFailedIcon("armor " + helmet.method_7876() + helmet.getClass().getName());
        }
        return icon;
    }

    private String scrubCodes(String string) {
        return string.replaceAll("(\\xA7.)", "");
    }

    private EnumMobs getUnknownMobNeutrality(class_1297 entity) {
        if (isHostile(entity))
            return EnumMobs.GENERICHOSTILE;
        if (entity instanceof class_1321) {
            class_1321 tameableEntity = (class_1321)entity;
            if (tameableEntity.method_6181() && (this.game.method_1496() || tameableEntity.method_6177().equals(this.game.field_1724)))
                return EnumMobs.GENERICTAME;
        }
        return EnumMobs.GENERICNEUTRAL;
    }

    private int getArmorType(class_1738 helmet) {
        return helmet.method_7876().equals("item.minecraft.leather_helmet") ? 0 : UNKNOWN;
    }

    public void renderMapMobs(class_4587 matrixStack, int x, int y) {
        double max = this.layoutVariables.zoomScaleAdjusted * 32.0D;
        double lastX = GameVariableAccessShim.xCoordDouble();
        double lastZ = GameVariableAccessShim.zCoordDouble();
        int lastY = GameVariableAccessShim.yCoord();
        RenderSystem.setShader(class_757::method_34542);
        GLUtils.disp2(this.textureAtlas.method_4624());
        GLShim.glEnable(3042);
        GLShim.glBlendFunc(770, 771);
        for (Contact contact : this.contacts) {
            boolean inRange;
            RenderSystem.setShader(class_757::method_34542);
            contact.updateLocation();
            double contactX = contact.x;
            double contactZ = contact.z;
            int contactY = contact.y;
            double wayX = lastX - contactX;
            double wayZ = lastZ - contactZ;
            int wayY = lastY - contactY;
            double adjustedDiff = max - Math.max(Math.abs(wayY), 0);
            contact.brightness = (float)Math.max(adjustedDiff / max, 0.0D);
            contact.brightness *= contact.brightness;
            contact.angle = (float)Math.toDegrees(Math.atan2(wayX, wayZ));
            contact.distance = Math.sqrt(wayX * wayX + wayZ * wayZ) / this.layoutVariables.zoomScaleAdjusted;
            if (wayY < 0) {
                GLShim.glColor4f(1.0F, 1.0F, 1.0F, contact.brightness);
            } else {
                GLShim.glColor3f(contact.brightness, contact.brightness, contact.brightness);
            }
            if (this.minimapOptions.rotates) {
                contact.angle += this.direction;
            } else if (this.minimapOptions.oldNorth) {
                contact.angle -= 90.0F;
            }
            if (!this.minimapOptions.squareMap) {
                inRange = (contact.distance < 31.0D);
            } else {
                double radLocate = Math.toRadians(contact.angle);
                double dispX = contact.distance * Math.cos(radLocate);
                double dispY = contact.distance * Math.sin(radLocate);
                inRange = (Math.abs(dispX) <= 28.5D && Math.abs(dispY) <= 28.5D);
            }
            if (inRange)
                try {
                    matrixStack.method_22903();
                    if (this.options.filtering) {
                        matrixStack.method_22904(x, y, 0.0D);
                        matrixStack.method_22907(class_1160.field_20707.method_23214(-contact.angle));
                        matrixStack.method_22904(0.0D, -contact.distance, 0.0D);
                        matrixStack.method_22907(class_1160.field_20707.method_23214(contact.angle + contact.rotationFactor));
                        matrixStack.method_22904(-x, -y, 0.0D);
                    } else {
                        wayX = Math.sin(Math.toRadians(contact.angle)) * contact.distance;
                        wayZ = Math.cos(Math.toRadians(contact.angle)) * contact.distance;
                        matrixStack.method_22904(Math.round(-wayX * this.layoutVariables.scScale) / this.layoutVariables.scScale, Math.round(-wayZ * this.layoutVariables.scScale) / this.layoutVariables.scScale, 0.0D);
                    }
                    RenderSystem.applyModelViewMatrix();
                    float yOffset = 0.0F;
                    if (contact.entity.method_5854() != null && isEntityShown(contact.entity.method_5854()))
                        yOffset = -4.0F;
                    if (contact.type == EnumMobs.GHAST || contact.type == EnumMobs.GHASTATTACKING || contact.type == EnumMobs.WITHER || contact.type == EnumMobs.WITHERINVULNERABLE || contact.type == EnumMobs.VEX || contact.type == EnumMobs.VEXCHARGING || contact.type == EnumMobs.PUFFERFISH || contact.type == EnumMobs.PUFFERFISHHALF || contact.type == EnumMobs.PUFFERFISHFULL) {
                        if (contact.type != EnumMobs.GHAST && contact.type != EnumMobs.GHASTATTACKING) {
                            if (contact.type != EnumMobs.WITHER && contact.type != EnumMobs.WITHERINVULNERABLE) {
                                if (contact.type != EnumMobs.VEX && contact.type != EnumMobs.VEXCHARGING) {
                                    if (contact.type == EnumMobs.PUFFERFISH || contact.type == EnumMobs.PUFFERFISHHALF || contact.type == EnumMobs.PUFFERFISHFULL) {
                                        int size = ((class_1454)contact.entity).method_6594();
                                        switch (size) {
                                            case 0:
                                                contact.type = EnumMobs.PUFFERFISH;
                                                break;
                                            case 1:
                                                contact.type = EnumMobs.PUFFERFISHHALF;
                                                break;
                                            case 2:
                                                contact.type = EnumMobs.PUFFERFISHFULL;
                                                break;
                                        }
                                    }
                                } else {
                                    class_897 render = this.game.method_1561().method_3953(contact.entity);
                                    String path = render.method_3931(contact.entity).method_12832();
                                    contact.type = path.endsWith("vex_charging.png") ? EnumMobs.VEXCHARGING : EnumMobs.VEX;
                                }
                            } else {
                                class_897 render = this.game.method_1561().method_3953(contact.entity);
                                String path = render.method_3931(contact.entity).method_12832();
                                contact.type = path.endsWith("wither_invulnerable.png") ? EnumMobs.WITHERINVULNERABLE : EnumMobs.WITHER;
                            }
                        } else {
                            class_897 render = this.game.method_1561().method_3953(contact.entity);
                            String path = render.method_3931(contact.entity).method_12832();
                            contact.type = path.endsWith("ghast_fire.png") ? EnumMobs.GHASTATTACKING : EnumMobs.GHAST;
                        }
                        tryAutoIcon(contact);
                        tryCustomIcon(contact);
                        if (this.newMobs) {
                            try {
                                this.textureAtlas.stitchNew();
                            } catch (StitcherException var45) {
                                System.err.println("Stitcher exception in render method!  Resetting mobs texture atlas.");
                                loadTexturePackIcons();
                            }
                            GLUtils.disp2(this.textureAtlas.method_4624());
                        }
                        this.newMobs = false;
                    }
                    if (contact.uuid != null && contact.uuid.equals(this.devUUID)) {
                        Sprite icon = this.textureAtlas.getAtlasSprite("glow");
                        applyFilteringParameters();
                        GLUtils.drawPre();
                        GLUtils.setMap(icon, x, y + yOffset, (int)(icon.getIconWidth() / 2.0F));
                        GLUtils.drawPost();
                    }
                    applyFilteringParameters();
                    GLUtils.drawPre();
                    GLUtils.setMap(contact.icon, x, y + yOffset, (int)(contact.icon.getIconWidth() / 4.0F));
                    GLUtils.drawPost();
                    if (((this.options.showHelmetsPlayers && contact.type == EnumMobs.PLAYER) || (this.options.showHelmetsMobs && contact.type != EnumMobs.PLAYER) || contact.type == EnumMobs.SHEEP) && contact.armorIcon != null) {
                        Sprite icon = contact.armorIcon;
                        float armorOffset = 0.0F;
                        if (contact.type == EnumMobs.ZOMBIEVILLAGER)
                            armorOffset = -0.5F;
                        float armorScale = 1.0F;
                        float red = 1.0F;
                        float green = 1.0F;
                        float blue = 1.0F;
                        if (contact.armorColor != -1) {
                            red = (contact.armorColor >> 16 & 0xFF) / 255.0F;
                            green = (contact.armorColor >> 8 & 0xFF) / 255.0F;
                            blue = (contact.armorColor >> 0 & 0xFF) / 255.0F;
                            if (contact.type == EnumMobs.SHEEP) {
                                class_1472 sheepEntity = (class_1472)contact.entity;
                                if (sheepEntity.method_16914() && "jeb_".equals(sheepEntity.method_5477().getString())) {
                                    int semiRandom = sheepEntity.field_6012 / 25 + sheepEntity.method_5628();
                                    int numDyeColors = (class_1767.values()).length;
                                    int colorID1 = semiRandom % numDyeColors;
                                    int colorID2 = (semiRandom + 1) % numDyeColors;
                                    float lerpVal = ((sheepEntity.field_6012 % 25) + this.game.method_1488()) / 25.0F;
                                    float[] sheepColors1 = class_1472.method_6634(class_1767.method_7791(colorID1));
                                    float[] sheepColors2 = class_1472.method_6634(class_1767.method_7791(colorID2));
                                    red = sheepColors1[0] * (1.0F - lerpVal) + sheepColors2[0] * lerpVal;
                                    green = sheepColors1[1] * (1.0F - lerpVal) + sheepColors2[1] * lerpVal;
                                    blue = sheepColors1[2] * (1.0F - lerpVal) + sheepColors2[2] * lerpVal;
                                }
                                armorScale = 1.04F;
                            }
                            if (wayY < 0) {
                                GLShim.glColor4f(red, green, blue, contact.brightness);
                            } else {
                                GLShim.glColor3f(red * contact.brightness, green * contact.brightness, blue * contact.brightness);
                            }
                        }
                        applyFilteringParameters();
                        GLUtils.drawPre();
                        GLUtils.setMap(icon, x, y + yOffset + armorOffset, (int)(icon.getIconWidth() / 4.0F * armorScale));
                        GLUtils.drawPost();
                        if (icon == this.clothIcon) {
                            if (wayY < 0) {
                                GLShim.glColor4f(1.0F, 1.0F, 1.0F, contact.brightness);
                            } else {
                                GLShim.glColor3f(contact.brightness, contact.brightness, contact.brightness);
                            }
                            icon = this.textureAtlas.getAtlasSprite("armor " + this.armorNames[2]);
                            applyFilteringParameters();
                            GLUtils.drawPre();
                            GLUtils.setMap(icon, x, y + yOffset + armorOffset, icon.getIconWidth() / 4.0F * armorScale);
                            GLUtils.drawPost();
                            if (wayY < 0) {
                                GLShim.glColor4f(red, green, blue, contact.brightness);
                            } else {
                                GLShim.glColor3f(red * contact.brightness, green * contact.brightness, blue * contact.brightness);
                            }
                            icon = this.textureAtlas.getAtlasSprite("armor " + this.armorNames[1]);
                            applyFilteringParameters();
                            GLUtils.drawPre();
                            GLUtils.setMap(icon, x, y + yOffset + armorOffset, icon.getIconWidth() / 4.0F * armorScale * 40.0F / 37.0F);
                            GLUtils.drawPost();
                            GLShim.glColor3f(1.0F, 1.0F, 1.0F);
                            icon = this.textureAtlas.getAtlasSprite("armor " + this.armorNames[3]);
                            applyFilteringParameters();
                            GLUtils.drawPre();
                            GLUtils.setMap(icon, x, y + yOffset + armorOffset, icon.getIconWidth() / 4.0F * armorScale * 40.0F / 37.0F);
                            GLUtils.drawPost();
                        }
                    } else if (contact.uuid != null && contact.uuid.equals(this.devUUID)) {
                        Sprite icon = this.textureAtlas.getAtlasSprite("crown");
                        applyFilteringParameters();
                        GLUtils.drawPre();
                        GLUtils.setMap(icon, x, y + yOffset, icon.getIconWidth() / 4.0F);
                        GLUtils.drawPost();
                    }
                    if (contact.name != null && ((this.options.showPlayerNames && contact.type == EnumMobs.PLAYER) || (this.options.showMobNames && contact.type != EnumMobs.PLAYER)) && (
                            contact.entity.method_16914() || contact.type == EnumMobs.PLAYER)) {
                        float scaleFactor = this.layoutVariables.scScale / this.options.fontScale;
                        matrixStack.method_22905(1.0F / scaleFactor, 1.0F / scaleFactor, 1.0F);
                        RenderSystem.applyModelViewMatrix();
                        String name = contact.entity.method_5476().getString();
                        int m = this.fontRenderer.method_1727(name) / 2;
                        write(name, x * scaleFactor - m, (y + 3) * scaleFactor, 16777215);
                    }
                } catch (Exception e) {
                    System.err.println("Error rendering mob icon! " + e.getLocalizedMessage() + " contact type " + contact.type);
                    this.logger.log(Level.ERROR, e);
                } finally {
                    matrixStack.method_22909();
                    RenderSystem.applyModelViewMatrix();
                }
        }
    }

    private void applyFilteringParameters() {
        if (this.options.filtering) {
            GLShim.glTexParameteri(3553, 10241, 9729);
            GLShim.glTexParameteri(3553, 10240, 9729);
            GLShim.glTexParameteri(3553, 10242, 10496);
            GLShim.glTexParameteri(3553, 10243, 10496);
        } else {
            GLShim.glTexParameteri(3553, 10241, 9728);
            GLShim.glTexParameteri(3553, 10240, 9728);
        }
    }

    private boolean isHostile(class_1297 entity) {
        if (entity instanceof class_1590) {
            class_1590 zombifiedPiglinEntity = (class_1590)entity;
            return zombifiedPiglinEntity.method_7076((class_1657)this.game.field_1724);
        }
        if (entity instanceof net.minecraft.class_1569)
            return true;
        if (entity instanceof class_4466) {
            class_4466 beeEntity = (class_4466)entity;
            return beeEntity.method_29511();
        }
        if (entity instanceof class_1456) {
            class_1456 polarBearEntity = (class_1456)entity;
            for (Object object : polarBearEntity.field_6002.method_18467(class_1456.class, polarBearEntity.method_5829().method_1009(8.0D, 4.0D, 8.0D))) {
                if (((class_1456)object).method_6109())
                    return true;
            }
        }
        if (entity instanceof class_1463) {
            class_1463 rabbitEntity = (class_1463)entity;
            return (rabbitEntity.method_6610() == 99);
        }
        if (entity instanceof class_1493) {
            class_1493 wolfEntity = (class_1493)entity;
            return wolfEntity.method_29511();
        }
        return false;
    }

    private boolean isPlayer(class_1297 entity) {
        return entity instanceof net.minecraft.class_745;
    }

    private boolean isNeutral(class_1297 entity) {
        if (!(entity instanceof class_1309))
            return false;
        return (!(entity instanceof class_1657) && !isHostile(entity));
    }

    private class ModelPartWithResourceLocation {
        class_630 modelPart;

        class_2960 resourceLocation;

        public ModelPartWithResourceLocation(class_630 modelPart, class_2960 resourceLocation) {
            this.modelPart = modelPart;
            this.resourceLocation = resourceLocation;
        }
    }
}
