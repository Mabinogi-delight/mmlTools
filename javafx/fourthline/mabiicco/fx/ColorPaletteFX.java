/*
 * Copyright (C) 2014 たんらる
 */

package fourthline.mabiicco.fx;

import javafx.scene.paint.Color;


public enum ColorPaletteFX {
	ACTIVE(250, 200, 0) {
		@Override
		public Color getRectColor(int index) {
			return Color.BLACK;
		}
	},
	MELODY(0, 200, 0),
	CHORD1(0, 200, 40),
	CHORD2(0, 200, 80),
	SONGEX(0, 200, 120);

	protected Color filter(Color color) {
		return new Color(
				limit(color.getRed()+beta), 
				limit(color.getGreen()+beta), 
				limit(color.getBlue()+beta), 
				color.getOpacity());
	}

	private final Color trackBaseColor[] = {
			new Color(0.9, 0, 0, 0.8),
			new Color(0, 0.9, 0, 0.8),
			new Color(0, 0, 0.9, 0.8),
			Color.ORANGE.darker(),
			Color.CYAN.darker(),
			Color.MAGENTA.darker(),
			Color.YELLOW.darker(),
			Color.valueOf("#FF5564"),
	};

	private final Color rectColorTable[];
	private final Color fillColorTable[];
	private final double beta;

	private static double limit(double a) {
		if (a > 1.0) return 1.0;
		if (a < 0)   return 0;
		return a;
	}

	private ColorPaletteFX(int rectAlpha, int fillAlpha, int beta) {
		this.beta = beta/255;
		rectColorTable = new Color[trackBaseColor.length];
		fillColorTable = new Color[trackBaseColor.length];

		for (int i = 0; i < trackBaseColor.length; i++) {
			Color baseColor = trackBaseColor[i];
			rectColorTable[i] = filter(new Color(
					baseColor.getRed(),
					baseColor.getGreen(),
					baseColor.getBlue(),
					rectAlpha/255.0));
			fillColorTable[i] = filter(new Color(
					baseColor.getRed(),
					baseColor.getGreen(),
					baseColor.getBlue(),
					fillAlpha/255.0));
		}
	}

	public int size() {
		return rectColorTable.length;
	}

	public Color getRectColor(int index) {
		return rectColorTable[index%rectColorTable.length];
	}

	public Color getFillColor(int index) {
		return fillColorTable[index%fillColorTable.length];
	}

	public static ColorPaletteFX getColorType(int part) {
		switch (part) {
		case 0:
			return MELODY;
		case 1:
			return CHORD1;
		case 2:
			return CHORD2;
		case 3:
			return SONGEX;
		}

		return null;
	}
}
