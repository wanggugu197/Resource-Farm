package com.maple.resource_farm.utils.renderer;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;

import com.google.common.base.Preconditions;
import lombok.Setter;
import org.joml.Vector4f;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.function.Consumer;

/**
 * Builds {@link BakedQuad}s for a textured cube. 1.21.1 rewrite: the 26.1.2-era
 * {@code MutableQuad} / {@code Material.Baked} types no longer exist, so quads are
 * baked through NeoForge's {@link QuadBakingVertexConsumer} instead.
 */
public class CubeBuilder {

    private final Consumer<BakedQuad> output;

    private final EnumMap<Direction, TextureAtlasSprite> textures = new EnumMap<>(Direction.class);

    @Setter
    private EnumSet<Direction> drawFaces = EnumSet.allOf(Direction.class);

    private final EnumMap<Direction, Vector4f> customUv = new EnumMap<>(Direction.class);

    private final byte[] uvRotations = new byte[Direction.values().length];

    private final boolean[] flipU = new boolean[Direction.values().length];

    private final boolean[] flipV = new boolean[Direction.values().length];

    @Setter
    private int color = 0xFFFFFFFF;

    @Setter
    private boolean emissiveMaterial;

    public CubeBuilder(Consumer<BakedQuad> output) {
        this.output = output;
    }

    public void addCube(float x1, float y1, float z1, float x2, float y2, float z2) {
        x1 /= 16.0f;
        y1 /= 16.0f;
        z1 /= 16.0f;
        x2 /= 16.0f;
        y2 /= 16.0f;
        z2 /= 16.0f;

        for (var face : this.drawFaces) {
            this.putFace(face, x1, y1, z1, x2, y2, z2);
        }
    }

    public void addQuad(Direction face, float x1, float y1, float z1, float x2, float y2, float z2) {
        this.putFace(face, x1, y1, z1, x2, y2, z2);
    }

    public void setFlipU(Direction side, boolean enable) {
        flipU[side.ordinal()] = enable;
    }

    public void setFlipV(Direction side, boolean enable) {
        flipV[side.ordinal()] = enable;
    }

    private static final class UvVector {

        float u1;
        float u2;
        float v1;
        float v2;
    }

    private void putFace(Direction face, float x1, float y1, float z1, float x2, float y2, float z2) {
        var texture = this.textures.get(face);
        if (texture == null) {
            return;
        }

        var quad = new QuadBakingVertexConsumer();
        quad.setSprite(texture);
        quad.setDirection(face);
        quad.setShade(false);
        quad.setTintIndex(-1);

        var uv = new UvVector();

        // The user might have set specific UV coordinates for this face
        var customUv = this.customUv.get(face);
        if (customUv != null) {
            uv.u1 = texture.getU(customUv.x());
            uv.v1 = texture.getV(customUv.y());
            uv.u2 = texture.getU(customUv.z());
            uv.v2 = texture.getV(customUv.w());
        } else {
            uv = this.getStandardUv(face, texture, x1, y1, z1, x2, y2, z2);
        }

        // Compute the four corner UVs in slot order (0..3), honoring rotation + flips
        var corners = this.getFaceCorners(face, uv);

        // Compute the four vertex positions in the same slot order
        var vertices = this.getFaceVertices(face, x1, y1, z1, x2, y2, z2);

        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        for (int i = 0; i < 4; i++) {
            var v = vertices[i];
            quad.addVertex(v[0], v[1], v[2]);
            quad.setUv(corners[i][0], corners[i][1]);
            quad.setColor(r, g, b, a);
            quad.setNormal(face.getStepX(), face.getStepY(), face.getStepZ());
            if (emissiveMaterial) {
                // Full-bright lightmap (sky << 16 | block), matching the old light emission of 15
                quad.setUv2(0xF0, 0xF0);
            }
        }

        output.accept(quad.bakeQuad());
    }

    /**
     * Applies UV rotation + flips and returns the four corner UVs in slot order 0..3,
     * replicating the old {@code MutableQuad.setUv(idx, ...)} slot layout.
     */
    private float[][] getFaceCorners(Direction face, UvVector uv) {
        var rotation = uvRotations[face.ordinal()];

        var u1 = uv.u1;
        var v1 = uv.v1;
        var u2 = uv.u2;
        var v2 = uv.v2;

        if (flipU[face.ordinal()]) {
            var tmp = u1;
            u1 = u2;
            u2 = tmp;
        }
        if (flipV[face.ordinal()]) {
            var tmp = v1;
            v1 = v2;
            v2 = tmp;
        }

        var corners = new float[4][2];
        switch (face.getAxis()) {
            case Y -> {
                corners[(4 - rotation) % 4] = new float[] { u1, v1 };
                corners[(1 + 4 - rotation) % 4] = new float[] { u1, v2 };
                corners[(2 + 4 - rotation) % 4] = new float[] { u2, v2 };
                corners[(3 + 4 - rotation) % 4] = new float[] { u2, v1 };
            }
            default -> {
                corners[(4 - rotation) % 4] = new float[] { u1, v2 };
                corners[(1 + 4 - rotation) % 4] = new float[] { u1, v1 };
                corners[(2 + 4 - rotation) % 4] = new float[] { u2, v1 };
                corners[(3 + 4 - rotation) % 4] = new float[] { u2, v2 };
            }
        }
        return corners;
    }

    /**
     * Returns the four vertex positions in slot order 0..3, matching the UV corner layout:
     * top/bottom faces use slot (u1,v1), (u1,v2), (u2,v2), (u2,v1); side faces use
     * (u1,v2), (u1,v1), (u2,v1), (u2,v2) — where v runs bottom-to-top.
     */
    private float[][] getFaceVertices(Direction face, float x1, float y1, float z1, float x2, float y2, float z2) {
        return switch (face) {
            case DOWN -> new float[][] {
                    { x1, y1, z1 }, { x1, y1, z2 }, { x2, y1, z2 }, { x2, y1, z1 }
            };
            case UP -> new float[][] {
                    { x1, y2, z1 }, { x1, y2, z2 }, { x2, y2, z2 }, { x2, y2, z1 }
            };
            case NORTH -> new float[][] {
                    { x1, y2, z1 }, { x1, y1, z1 }, { x2, y1, z1 }, { x2, y2, z1 }
            };
            case SOUTH -> new float[][] {
                    { x1, y2, z2 }, { x1, y1, z2 }, { x2, y1, z2 }, { x2, y2, z2 }
            };
            case WEST -> new float[][] {
                    { x1, y2, z1 }, { x1, y1, z1 }, { x1, y1, z2 }, { x1, y2, z2 }
            };
            case EAST -> new float[][] {
                    { x2, y2, z2 }, { x2, y1, z2 }, { x2, y1, z1 }, { x2, y2, z1 }
            };
        };
    }

    private UvVector getStandardUv(Direction face, TextureAtlasSprite texture, float x1, float y1, float z1, float x2,
                                   float y2, float z2) {
        UvVector uv = new UvVector();

        if (face.getAxis() != Direction.Axis.Y) {
            uv.v1 = texture.getV(1 - y1);
            uv.v2 = texture.getV(1 - y2);
        } else {
            uv.v1 = texture.getV(z1);
            uv.v2 = texture.getV(z2);
        }

        switch (face) {
            case DOWN, UP, SOUTH -> {
                uv.u1 = texture.getU(x1);
                uv.u2 = texture.getU(x2);
            }
            case NORTH -> {
                uv.u1 = texture.getU(1 - x2);
                uv.u2 = texture.getU(1 - x1);
            }
            case WEST -> {
                uv.u1 = texture.getU(z1);
                uv.u2 = texture.getU(z2);
            }
            case EAST -> {
                uv.u1 = texture.getU(1 - z2);
                uv.u2 = texture.getU(1 - z1);
            }
        }

        return uv;
    }

    public void setTexture(TextureAtlasSprite texture) {
        for (Direction face : Direction.values()) {
            this.textures.put(face, texture);
        }
    }

    public void setTextures(TextureAtlasSprite up, TextureAtlasSprite down, TextureAtlasSprite north,
                            TextureAtlasSprite south, TextureAtlasSprite east, TextureAtlasSprite west) {
        this.textures.put(Direction.UP, up);
        this.textures.put(Direction.DOWN, down);
        this.textures.put(Direction.NORTH, north);
        this.textures.put(Direction.SOUTH, south);
        this.textures.put(Direction.EAST, east);
        this.textures.put(Direction.WEST, west);
    }

    public void setTexture(Direction facing, TextureAtlasSprite sprite) {
        this.textures.put(facing, sprite);
    }

    /**
     * Sets the vertex color for future vertices to the given RGB value, and forces the alpha component to 255.
     */
    public void setColorRGB(int color) {
        this.setColor(color | 0xFF000000);
    }

    public void setColorRGB(float r, float g, float b) {
        this.setColorRGB((int) (r * 255) << 16 | (int) (g * 255) << 8 | (int) (b * 255));
    }

    public void setCustomUv(Direction facing, float u1, float v1, float u2, float v2) {
        this.customUv.put(facing, new Vector4f(u1, v1, u2, v2));
    }

    public void setUvRotation(Direction facing, int rotation) {
        Preconditions.checkArgument(rotation >= 0 && rotation <= 3, "rotation");
        this.uvRotations[facing.ordinal()] = (byte) rotation;
    }
}
