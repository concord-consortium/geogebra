package org.geogebra.ggjsviewer.client.kernel.gawt;

public class Color {
	
		private int r;
		private int g;
		private int b;
		private int alpha;
	public static final Color black = new Color();
	public static final Color white = new Color(255,255,255);
	public static final Color blue = new Color(0,0,255);
	public static final Color darkGray = new Color(64,64,64);
	
	public Color() {
		setRed(0);
		setGreen(0);
		setBlue(0);
	}

	public Color(int r, int g, int b) {
		setRed(r);
		setGreen(g);
		setBlue(b);
		// TODO Auto-generated constructor stub
	}

	public Color(int r, int g, int b, int alpha) {
		setRed(r);
		setGreen(g);
		setBlue(b);
		setAlpha(alpha);
		// TODO Auto-generated constructor stub
	}

	public Color(float r, float g, float b, float alpha) {
		if (r>1) 
			r=1;
		if (r<0)
			r=0;
		
		if (g>1) 
			g=1;
		if (g<0)
			g=0;
		
		
		if (b>1) 
			b=1;
		if (b<0)
			b=0;
		
		if (alpha>1)
			alpha=1;
		if (alpha<0)
			alpha=0;
		
		setRed((int) (r*255));
		setGreen((int) (g*255));
		setBlue((int) (b*255));
		setAlpha((int)(b*255));
		
		// TODO Auto-generated constructor stub
	}

	public void setRed(int r) {
		this.r = r;
	}

	public int getRed() {
		return r;
	}

	public void setGreen(int g) {
		this.g = g;
	}

	public int getGreen() {
		return g;
	}

	public void setBlue(int b) {
		this.b = b;
	}

	public int getBlue() {
		return b;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	public int getAlpha() {
		return alpha;
	}

	public void getRGBColorComponents(float[] rgb) {
		rgb[0] = getRed();
		rgb[1] = getGreen();
		rgb[2] = getBlue();
		// TODO Auto-generated method stub
		
	}

}
