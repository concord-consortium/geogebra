package org.geogebra.ggjsviewer.client.kernel.gawt;

public class Font {
	public static final int PLAIN = 0;
	public static final int BOLD = 1;
	private String fontStyle = "normal";
	private String fontVariant = "normal";
	private String fontWeight = "normal";
	private String fontSize = "12";
	private String lineHeight = "12";
	private String fontFamily = "sans-serif";
	
	public Font(String fontStyle) {
		this.setFontStyle(fontStyle);			
		// TODO Auto-generated constructor stub
	}

	public void setFontStyle(String fontStyle) {
		this.fontStyle = fontStyle;
	}

	public String getFontStyle() {
		return fontStyle;
	}

	public String getFullFontString() {
		return fontStyle+" "+fontVariant+" "+fontWeight+" "+fontSize+"px/"+lineHeight+"px "+fontFamily;
	}

	public void setFontVariant(String fontVariant) {
		this.fontVariant = fontVariant;
	}

	public String getFontVariant() {
		return fontVariant;
	}

	public void setFontWeight(String fontWeight) {
		this.fontWeight = fontWeight;
	}

	public String getFontWeight() {
		return fontWeight;
	}

	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}

	public String getFontSize() {
		return fontSize;
	}

	public void setLineHeight(String lineHeight) {
		this.lineHeight = lineHeight;
	}

	public String getLineHeight() {
		return lineHeight;
	}

	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}

	public String getFontFamily() {
		return fontFamily;
	}

	public Font deriveFont(String fontStyle2, final int newSize) {
		// TODO Auto-generated method stub
		return new Font(fontStyle) {
			{
				setFontSize(String.valueOf(newSize));
			}
		};
	}

	public int canDisplayUpTo(String textString) {
		// TODO Auto-generated method stub
		return 0;
	}
	

}
