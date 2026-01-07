package com.resource_farm.api.block;

import java.util.Arrays;

public record ColoringSettings(
                               boolean[] tintLayers,
                               int[] colors) {

    public static ColoringSettings of(boolean[] tintLayers, int[] colors) {
        boolean[] processedTintLayers = tintLayers == null ? new boolean[0] : tintLayers;
        int[] processedColors = colors == null ? new int[0] : colors;
        for (int i = 0; i < processedColors.length; i++) {
            processedColors[i] = processColorAlphaChannel(processedColors[i]);
        }
        int minLength = Math.min(processedTintLayers.length, processedColors.length);
        boolean[] finalTintLayers = Arrays.copyOf(processedTintLayers, minLength);
        int[] finalColors = Arrays.copyOf(processedColors, minLength);

        return new ColoringSettings(finalTintLayers, finalColors);
    }

    private static int processColorAlphaChannel(int originalColor) {
        int rgbOnly = originalColor & 0x00FFFFFF;
        return 0xFF000000 | rgbOnly;
    }

    public static ColoringSettings getLeave(ColoringSettings original) {
        boolean[] originalTintLayers = original == null ? new boolean[0] : original.tintLayers();
        boolean[] newTintLayers = Arrays.copyOf(originalTintLayers, originalTintLayers.length);
        if (newTintLayers.length >= 1) newTintLayers[0] = true;
        else newTintLayers = new boolean[] { true };

        int[] originalColors = original == null ? new int[0] : original.colors();
        int[] newColors = Arrays.copyOf(originalColors, originalColors.length);
        if (newColors.length >= 1) newColors[0] = 0x61964F;
        else newColors = new int[] { 0x61964F };

        return ColoringSettings.of(newTintLayers, newColors);
    }
}
