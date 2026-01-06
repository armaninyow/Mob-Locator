package com.moblocator;

import net.minecraft.client.gui.DrawContext;

public class IconRenderer {
    private static final int BLACK = 0xFF000000;
    
    // Grey shades for anti-aliasing (will be blended with config color)
    private static final int GREY_LIGHT = 0xFFE0E0E0; // Light grey
    private static final int GREY_MID = 0xFFBCBCBD;   // Mid grey
    
    public static void drawIcon(DrawContext context, int centerX, int centerY, int iconType, int baseColor) {
        // Add full alpha to the base color
        int color = baseColor | 0xFF000000;
        
        switch (iconType) {
            case 0 -> drawSmallCircle(context, centerX, centerY, color);
            case 1 -> drawSmallSquare(context, centerX, centerY, color);
            case 2 -> drawLargeCircle(context, centerX, centerY, color);
            case 3 -> drawLargeSquare(context, centerX, centerY, color);
        }
    }
    
    public static void drawArrow(DrawContext context, int centerX, int centerY, boolean pointingUp, int baseColor) {
        int color = baseColor | 0xFF000000;
        
        if (pointingUp) {
            drawArrowUp(context, centerX, centerY, color);
        } else {
            drawArrowDown(context, centerX, centerY, color);
        }
    }
    
    // Small Circle - 3x3
    private static void drawSmallCircle(DrawContext context, int cx, int cy, int color) {
        int startX = cx - 1;
        int startY = cy - 1;
        
        int grey = blendColor(color, GREY_MID);
        
        // Row 0: black, grey, black
        pixel(context, startX + 0, startY + 0, BLACK);
        pixel(context, startX + 1, startY + 0, grey);
        pixel(context, startX + 2, startY + 0, BLACK);
        
        // Row 1: grey, white, grey
        pixel(context, startX + 0, startY + 1, grey);
        pixel(context, startX + 1, startY + 1, color);
        pixel(context, startX + 2, startY + 1, grey);
        
        // Row 2: black, grey, black
        pixel(context, startX + 0, startY + 2, BLACK);
        pixel(context, startX + 1, startY + 2, grey);
        pixel(context, startX + 2, startY + 2, BLACK);
    }
    
    // Small Square - 5x5
    private static void drawSmallSquare(DrawContext context, int cx, int cy, int color) {
        int startX = cx - 2;
        int startY = cy - 2;
        
        // Row 0: transparent, black, black, black, transparent
        pixel(context, startX + 1, startY + 0, BLACK);
        pixel(context, startX + 2, startY + 0, BLACK);
        pixel(context, startX + 3, startY + 0, BLACK);
        
        // Row 1-3: black, white, white, white, black
        for (int row = 1; row <= 3; row++) {
            pixel(context, startX + 0, startY + row, BLACK);
            pixel(context, startX + 1, startY + row, color);
            pixel(context, startX + 2, startY + row, color);
            pixel(context, startX + 3, startY + row, color);
            pixel(context, startX + 4, startY + row, BLACK);
        }
        
        // Row 4: transparent, black, black, black, transparent
        pixel(context, startX + 1, startY + 4, BLACK);
        pixel(context, startX + 2, startY + 4, BLACK);
        pixel(context, startX + 3, startY + 4, BLACK);
    }
    
    // Large Circle - 7x7
    private static void drawLargeCircle(DrawContext context, int cx, int cy, int color) {
        int startX = cx - 3;
        int startY = cy - 3;
        
        int grey = blendColor(color, GREY_MID);
        
        // Row 0: transparent, transparent, black, black, black, transparent, transparent
        pixel(context, startX + 2, startY + 0, BLACK);
        pixel(context, startX + 3, startY + 0, BLACK);
        pixel(context, startX + 4, startY + 0, BLACK);
        
        // Row 1: transparent, black, grey, grey, grey, black, transparent
        pixel(context, startX + 1, startY + 1, BLACK);
        pixel(context, startX + 2, startY + 1, grey);
        pixel(context, startX + 3, startY + 1, grey);
        pixel(context, startX + 4, startY + 1, grey);
        pixel(context, startX + 5, startY + 1, BLACK);
        
        // Row 2-4: black, grey, white, white, white, grey, black
        for (int row = 2; row <= 4; row++) {
            pixel(context, startX + 0, startY + row, BLACK);
            pixel(context, startX + 1, startY + row, grey);
            pixel(context, startX + 2, startY + row, color);
            pixel(context, startX + 3, startY + row, color);
            pixel(context, startX + 4, startY + row, color);
            pixel(context, startX + 5, startY + row, grey);
            pixel(context, startX + 6, startY + row, BLACK);
        }
        
        // Row 5: transparent, black, grey, grey, grey, black, transparent
        pixel(context, startX + 1, startY + 5, BLACK);
        pixel(context, startX + 2, startY + 5, grey);
        pixel(context, startX + 3, startY + 5, grey);
        pixel(context, startX + 4, startY + 5, grey);
        pixel(context, startX + 5, startY + 5, BLACK);
        
        // Row 6: transparent, transparent, black, black, black, transparent, transparent
        pixel(context, startX + 2, startY + 6, BLACK);
        pixel(context, startX + 3, startY + 6, BLACK);
        pixel(context, startX + 4, startY + 6, BLACK);
    }
    
    // Large Square - 7x7
    private static void drawLargeSquare(DrawContext context, int cx, int cy, int color) {
        int startX = cx - 3;
        int startY = cy - 3;
        
        int greyLight = blendColor(color, GREY_LIGHT);
        int greyMid = blendColor(color, GREY_MID);
        
        // Row 0: transparent, black, black, black, black, black, transparent
        pixel(context, startX + 1, startY + 0, BLACK);
        pixel(context, startX + 2, startY + 0, BLACK);
        pixel(context, startX + 3, startY + 0, BLACK);
        pixel(context, startX + 4, startY + 0, BLACK);
        pixel(context, startX + 5, startY + 0, BLACK);
        
        // Row 1: black, grey, light, light, light, grey, black
        pixel(context, startX + 0, startY + 1, BLACK);
        pixel(context, startX + 1, startY + 1, greyMid);
        pixel(context, startX + 2, startY + 1, greyLight);
        pixel(context, startX + 3, startY + 1, greyLight);
        pixel(context, startX + 4, startY + 1, greyLight);
        pixel(context, startX + 5, startY + 1, greyMid);
        pixel(context, startX + 6, startY + 1, BLACK);
        
        // Row 2-4: black, light, white, white, white, light, black
        for (int row = 2; row <= 4; row++) {
            pixel(context, startX + 0, startY + row, BLACK);
            pixel(context, startX + 1, startY + row, greyLight);
            pixel(context, startX + 2, startY + row, color);
            pixel(context, startX + 3, startY + row, color);
            pixel(context, startX + 4, startY + row, color);
            pixel(context, startX + 5, startY + row, greyLight);
            pixel(context, startX + 6, startY + row, BLACK);
        }
        
        // Row 5: black, grey, light, light, light, grey, black
        pixel(context, startX + 0, startY + 5, BLACK);
        pixel(context, startX + 1, startY + 5, greyMid);
        pixel(context, startX + 2, startY + 5, greyLight);
        pixel(context, startX + 3, startY + 5, greyLight);
        pixel(context, startX + 4, startY + 5, greyLight);
        pixel(context, startX + 5, startY + 5, greyMid);
        pixel(context, startX + 6, startY + 5, BLACK);
        
        // Row 6: transparent, black, black, black, black, black, transparent
        pixel(context, startX + 1, startY + 6, BLACK);
        pixel(context, startX + 2, startY + 6, BLACK);
        pixel(context, startX + 3, startY + 6, BLACK);
        pixel(context, startX + 4, startY + 6, BLACK);
        pixel(context, startX + 5, startY + 6, BLACK);
    }
    
    // Arrow Up - 7x4
    private static void drawArrowUp(DrawContext context, int cx, int cy, int color) {
        int startX = cx - 3;
        int startY = cy - 2;
        
        // Row 0: transparent, transparent, black, black, black, transparent, transparent
        pixel(context, startX + 2, startY + 0, BLACK);
        pixel(context, startX + 3, startY + 0, BLACK);
        pixel(context, startX + 4, startY + 0, BLACK);
        
        // Row 1: transparent, black, white, white, white, black, transparent
        pixel(context, startX + 1, startY + 1, BLACK);
        pixel(context, startX + 2, startY + 1, color);
        pixel(context, startX + 3, startY + 1, color);
        pixel(context, startX + 4, startY + 1, color);
        pixel(context, startX + 5, startY + 1, BLACK);
        
        // Row 2: black, white, white, white, white, white, black
        pixel(context, startX + 0, startY + 2, BLACK);
        pixel(context, startX + 1, startY + 2, color);
        pixel(context, startX + 2, startY + 2, color);
        pixel(context, startX + 3, startY + 2, color);
        pixel(context, startX + 4, startY + 2, color);
        pixel(context, startX + 5, startY + 2, color);
        pixel(context, startX + 6, startY + 2, BLACK);
        
        // Row 3: black, black, black, black, black, black, black
        for (int x = 0; x <= 6; x++) {
            pixel(context, startX + x, startY + 3, BLACK);
        }
    }
    
    // Arrow Down - 7x4
    private static void drawArrowDown(DrawContext context, int cx, int cy, int color) {
        int startX = cx - 3;
        int startY = cy - 2;
        
        // Row 0: black, black, black, black, black, black, black
        for (int x = 0; x <= 6; x++) {
            pixel(context, startX + x, startY + 0, BLACK);
        }
        
        // Row 1: black, white, white, white, white, white, black
        pixel(context, startX + 0, startY + 1, BLACK);
        pixel(context, startX + 1, startY + 1, color);
        pixel(context, startX + 2, startY + 1, color);
        pixel(context, startX + 3, startY + 1, color);
        pixel(context, startX + 4, startY + 1, color);
        pixel(context, startX + 5, startY + 1, color);
        pixel(context, startX + 6, startY + 1, BLACK);
        
        // Row 2: transparent, black, white, white, white, black, transparent
        pixel(context, startX + 1, startY + 2, BLACK);
        pixel(context, startX + 2, startY + 2, color);
        pixel(context, startX + 3, startY + 2, color);
        pixel(context, startX + 4, startY + 2, color);
        pixel(context, startX + 5, startY + 2, BLACK);
        
        // Row 3: transparent, transparent, black, black, black, transparent, transparent
        pixel(context, startX + 2, startY + 3, BLACK);
        pixel(context, startX + 3, startY + 3, BLACK);
        pixel(context, startX + 4, startY + 3, BLACK);
    }
    
    // Helper: Draw a single pixel
    private static void pixel(DrawContext context, int x, int y, int color) {
        context.fill(x, y, x + 1, y + 1, color);
    }
    
    // Helper: Blend config color with grey for anti-aliasing
    private static int blendColor(int color, int grey) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        
        int greyValue = grey & 0xFF;
        float factor = greyValue / 255.0f;
        
        int newR = (int)(r * factor);
        int newG = (int)(g * factor);
        int newB = (int)(b * factor);
        
        return 0xFF000000 | (newR << 16) | (newG << 8) | newB;
    }
}