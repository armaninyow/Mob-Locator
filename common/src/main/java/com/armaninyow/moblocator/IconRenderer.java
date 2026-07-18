package com.armaninyow.moblocator;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public class IconRenderer {
	private static final int GREY_LIGHT = 0xFFE0E0E0;
	private static final int GREY_MID   = 0xFFBCBCBD;

	public static void drawIcon(GuiGraphicsExtractor context, int centerX, int centerY,
	                            int iconType, int baseColor, int outlineColor) {
		int color   = baseColor   | 0xFF000000;
		int outline = outlineColor | 0xFF000000;

		switch (iconType) {
			case 0 -> drawSmallCircle(context, centerX, centerY, color, outline);
			case 1 -> drawSmallSquare(context, centerX, centerY, color, outline);
			case 2 -> drawLargeCircle(context, centerX, centerY, color, outline);
			case 3 -> drawLargeSquare(context, centerX, centerY, color, outline);
		}
	}

	public static void drawArrow(GuiGraphicsExtractor context, int centerX, int centerY,
	                             boolean pointingUp, int baseColor, int outlineColor) {
		int color   = baseColor   | 0xFF000000;
		int outline = outlineColor | 0xFF000000;

		if (pointingUp) {
			drawArrowUp(context, centerX, centerY, color, outline);
		} else {
			drawArrowDown(context, centerX, centerY, color, outline);
		}
	}

	private static void drawSmallCircle(GuiGraphicsExtractor context, int cx, int cy,
	                                    int color, int outline) {
		int startX = cx - 1;
		int startY = cy - 1;

		int grey = blendColor(color, GREY_MID);

		pixel(context, startX + 0, startY + 0, outline);
		pixel(context, startX + 1, startY + 0, grey);
		pixel(context, startX + 2, startY + 0, outline);

		pixel(context, startX + 0, startY + 1, grey);
		pixel(context, startX + 1, startY + 1, color);
		pixel(context, startX + 2, startY + 1, grey);

		pixel(context, startX + 0, startY + 2, outline);
		pixel(context, startX + 1, startY + 2, grey);
		pixel(context, startX + 2, startY + 2, outline);
	}

	private static void drawSmallSquare(GuiGraphicsExtractor context, int cx, int cy,
	                                    int color, int outline) {
		int startX = cx - 2;
		int startY = cy - 2;

		pixel(context, startX + 1, startY + 0, outline);
		pixel(context, startX + 2, startY + 0, outline);
		pixel(context, startX + 3, startY + 0, outline);

		for (int row = 1; row <= 3; row++) {
			pixel(context, startX + 0, startY + row, outline);
			pixel(context, startX + 1, startY + row, color);
			pixel(context, startX + 2, startY + row, color);
			pixel(context, startX + 3, startY + row, color);
			pixel(context, startX + 4, startY + row, outline);
		}

		pixel(context, startX + 1, startY + 4, outline);
		pixel(context, startX + 2, startY + 4, outline);
		pixel(context, startX + 3, startY + 4, outline);
	}

	private static void drawLargeCircle(GuiGraphicsExtractor context, int cx, int cy,
	                                    int color, int outline) {
		int startX = cx - 3;
		int startY = cy - 3;

		int grey = blendColor(color, GREY_MID);

		pixel(context, startX + 2, startY + 0, outline);
		pixel(context, startX + 3, startY + 0, outline);
		pixel(context, startX + 4, startY + 0, outline);

		pixel(context, startX + 1, startY + 1, outline);
		pixel(context, startX + 2, startY + 1, grey);
		pixel(context, startX + 3, startY + 1, grey);
		pixel(context, startX + 4, startY + 1, grey);
		pixel(context, startX + 5, startY + 1, outline);

		for (int row = 2; row <= 4; row++) {
			pixel(context, startX + 0, startY + row, outline);
			pixel(context, startX + 1, startY + row, grey);
			pixel(context, startX + 2, startY + row, color);
			pixel(context, startX + 3, startY + row, color);
			pixel(context, startX + 4, startY + row, color);
			pixel(context, startX + 5, startY + row, grey);
			pixel(context, startX + 6, startY + row, outline);
		}

		pixel(context, startX + 1, startY + 5, outline);
		pixel(context, startX + 2, startY + 5, grey);
		pixel(context, startX + 3, startY + 5, grey);
		pixel(context, startX + 4, startY + 5, grey);
		pixel(context, startX + 5, startY + 5, outline);

		pixel(context, startX + 2, startY + 6, outline);
		pixel(context, startX + 3, startY + 6, outline);
		pixel(context, startX + 4, startY + 6, outline);
	}

	private static void drawLargeSquare(GuiGraphicsExtractor context, int cx, int cy,
	                                    int color, int outline) {
		int startX = cx - 3;
		int startY = cy - 3;

		int greyLight = blendColor(color, GREY_LIGHT);
		int greyMid   = blendColor(color, GREY_MID);

		pixel(context, startX + 1, startY + 0, outline);
		pixel(context, startX + 2, startY + 0, outline);
		pixel(context, startX + 3, startY + 0, outline);
		pixel(context, startX + 4, startY + 0, outline);
		pixel(context, startX + 5, startY + 0, outline);

		pixel(context, startX + 0, startY + 1, outline);
		pixel(context, startX + 1, startY + 1, greyMid);
		pixel(context, startX + 2, startY + 1, greyLight);
		pixel(context, startX + 3, startY + 1, greyLight);
		pixel(context, startX + 4, startY + 1, greyLight);
		pixel(context, startX + 5, startY + 1, greyMid);
		pixel(context, startX + 6, startY + 1, outline);

		for (int row = 2; row <= 4; row++) {
			pixel(context, startX + 0, startY + row, outline);
			pixel(context, startX + 1, startY + row, greyLight);
			pixel(context, startX + 2, startY + row, color);
			pixel(context, startX + 3, startY + row, color);
			pixel(context, startX + 4, startY + row, color);
			pixel(context, startX + 5, startY + row, greyLight);
			pixel(context, startX + 6, startY + row, outline);
		}

		pixel(context, startX + 0, startY + 5, outline);
		pixel(context, startX + 1, startY + 5, greyMid);
		pixel(context, startX + 2, startY + 5, greyLight);
		pixel(context, startX + 3, startY + 5, greyLight);
		pixel(context, startX + 4, startY + 5, greyLight);
		pixel(context, startX + 5, startY + 5, greyMid);
		pixel(context, startX + 6, startY + 5, outline);

		pixel(context, startX + 1, startY + 6, outline);
		pixel(context, startX + 2, startY + 6, outline);
		pixel(context, startX + 3, startY + 6, outline);
		pixel(context, startX + 4, startY + 6, outline);
		pixel(context, startX + 5, startY + 6, outline);
	}

	private static void drawArrowUp(GuiGraphicsExtractor context, int cx, int cy,
	                                int color, int outline) {
		int startX = cx - 3;
		int startY = cy - 3;

		pixel(context, startX + 2, startY + 0, outline);
		pixel(context, startX + 3, startY + 0, outline);
		pixel(context, startX + 4, startY + 0, outline);

		pixel(context, startX + 1, startY + 1, outline);
		pixel(context, startX + 2, startY + 1, color);
		pixel(context, startX + 3, startY + 1, color);
		pixel(context, startX + 4, startY + 1, color);
		pixel(context, startX + 5, startY + 1, outline);

		pixel(context, startX + 0, startY + 2, outline);
		pixel(context, startX + 1, startY + 2, color);
		pixel(context, startX + 2, startY + 2, color);
		pixel(context, startX + 3, startY + 2, color);
		pixel(context, startX + 4, startY + 2, color);
		pixel(context, startX + 5, startY + 2, color);
		pixel(context, startX + 6, startY + 2, outline);

		for (int x = 0; x <= 6; x++) {
			pixel(context, startX + x, startY + 3, outline);
		}
	}

	private static void drawArrowDown(GuiGraphicsExtractor context, int cx, int cy,
	                                  int color, int outline) {
		int startX = cx - 3;
		int startY = cy - 1;

		for (int x = 0; x <= 6; x++) {
			pixel(context, startX + x, startY + 0, outline);
		}

		pixel(context, startX + 0, startY + 1, outline);
		pixel(context, startX + 1, startY + 1, color);
		pixel(context, startX + 2, startY + 1, color);
		pixel(context, startX + 3, startY + 1, color);
		pixel(context, startX + 4, startY + 1, color);
		pixel(context, startX + 5, startY + 1, color);
		pixel(context, startX + 6, startY + 1, outline);

		pixel(context, startX + 1, startY + 2, outline);
		pixel(context, startX + 2, startY + 2, color);
		pixel(context, startX + 3, startY + 2, color);
		pixel(context, startX + 4, startY + 2, color);
		pixel(context, startX + 5, startY + 2, outline);

		pixel(context, startX + 2, startY + 3, outline);
		pixel(context, startX + 3, startY + 3, outline);
		pixel(context, startX + 4, startY + 3, outline);
	}

	private static void pixel(GuiGraphicsExtractor context, int x, int y, int color) {
		context.fill(x, y, x + 1, y + 1, color);
	}

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