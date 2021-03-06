package geogebra.euclidian;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.arithmetic.FunctionalNVar;
import geogebra.kernel.arithmetic.Inequality;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * Graphical representation of linear inequality
 * 
 * @author Michael Borcherds
 * 
 */
public class DrawInequality extends Drawable {

	

	private boolean isVisible;
	private boolean labelVisible;
	
	private FunctionalNVar function;
	private int ineqCount;
	private ArrayList<Drawable> drawables;

	/**
	 * Creates new drawable linear inequality
	 * 
	 * @param view
	 * @param function boolean 2-var function
	 */
	public DrawInequality(EuclidianView view, FunctionalNVar function) {
		this.view = view;
		this.function = function;
		geo = (GeoElement)function;
		update();

	}

	final public void update() {
		// take line g here, not geo this object may be used for conics too
		isVisible = geo.isEuclidianVisible();
		if (!isVisible)
			return;
		labelVisible = geo.isLabelVisible();
		// updateStrokes(n);

		// init gp

		ineqCount = function.getIneqs().size();
		// plot like definite integral
		if (drawables == null) 
			drawables = new ArrayList<Drawable>(ineqCount);		
			
		for (int i = 0; i < ineqCount; i++) {
			Inequality ineq = function.getIneqs().get(i);			
			if(drawables.size() <= i || !matchBorder(ineq.getBorder(),i)){
				Drawable draw;
				switch (ineq.getType()){
					case Inequality.INEQUALITY_PARAMETRIC_Y: 
						draw = new DrawParametricInequality(ineq, view, geo);
						break;
					case Inequality.INEQUALITY_PARAMETRIC_X: 
						draw = new DrawParametricInequality(ineq, view, geo);
						break;
					case Inequality.INEQUALITY_1VAR_X: 
						draw = new DrawInequality1Var(ineq, view, geo, false);
						break;
					case Inequality.INEQUALITY_1VAR_Y: 
						draw = new DrawInequality1Var(ineq, view, geo, true);
						break;	
					case Inequality.INEQUALITY_CONIC: 
						draw = new DrawConic(view, ineq.getConicBorder());					
						ineq.getConicBorder().setInverseFill(geo.isInverseFill() ^ ineq.isAboveBorder());	
						break;	
					case Inequality.INEQUALITY_IMPLICIT: 
						draw = new DrawImplicitPoly(view, ineq.getImpBorder());
						break;
					default: draw = null;
				}
			
				draw.setGeoElement((GeoElement)function);
				draw.update();
				if(drawables.size() <= i)
					drawables.add(draw);
				else
					drawables.set(i,draw);
			}
			else {
				if(ineq.getType() == Inequality.INEQUALITY_CONIC) {					
					ineq.getConicBorder().setInverseFill(geo.isInverseFill() ^ ineq.isAboveBorder());
				}
				drawables.get(i).update();
			}
			// gp on screen?
			/*if (!gp[i].intersects(0, 0, view.width, view.height)) {
				isVisible = false;
				// don't return here to make sure that getBounds() works for
				// offscreen points too
			}*/

			
		}
	}

	

	private boolean matchBorder(GeoElement border, int i) {
		Drawable d = drawables.get(i);
		if(d instanceof DrawConic && ((DrawConic)d).getConic().equals(border))
			return true;
		if(d instanceof DrawImplicitPoly && ((DrawImplicitPoly)d).getPoly().equals(border))
			return true;
		if(drawables.get(i) instanceof DrawParametricInequality && ((DrawParametricInequality)d).getBorder().equals(border))
			return ((DrawParametricInequality)drawables.get(i)).isXparametric();
		
		return false;
	}

	public void draw(Graphics2D g2) {
		if (isVisible) {
			for(int i=0;i<ineqCount;i++)
				drawables.get(i).draw(g2);
		}
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public boolean hit(int x, int y) {
		 if(drawables==null)
			 return false;
		 boolean ret = false; 
		 for (int i = 0; i < ineqCount; i++)
				ret |= drawables.get(i).hit(x,y);
		 return ret;
	}

	@Override
	public boolean isInside(Rectangle rect) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	private class DrawParametricInequality extends Drawable{

		private Inequality ineq;
		private GeneralPathClipped gp;
		
		protected DrawParametricInequality(Inequality ineq,EuclidianView view,GeoElement geo){
			this.view = view;
			this.ineq = ineq;
			this.geo = geo;			
		}
		
		private Object getBorder() {
			return ineq.getBorder();			
		}
		@Override
		public void draw(Graphics2D g2) {
			if (geo.doHighlighting()) {
				g2.setPaint(geo.getSelColor());
				g2.setStroke(selStroke);
				Drawable.drawWithValueStrokePure(gp, g2);
			}
			fill(g2, gp, true); // fill using default/hatching/image as
			// appropriate

			if (geo.lineThickness > 0) {
				g2.setPaint(geo.getObjectColor());
				g2.setStroke(objStroke);
				Drawable.drawWithValueStrokePure(gp, g2);
			}

			if (labelVisible) {
				g2.setFont(view.fontConic);
				g2.setPaint(geo.getLabelColor());
				drawLabel(g2);
			}
			
		}

		@Override
		public GeoElement getGeoElement() {
			return geo;
		}

		@Override
		public boolean hit(int x, int y) {
			return gp.contains(x, y) || gp.intersects(x - 3, y - 3, 6, 6);			
		}

		@Override
		public boolean isInside(Rectangle rect) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setGeoElement(GeoElement geo) {
			this.geo = geo;
			
		}

		@Override
		public void update() {
			if(gp==null)
				gp=new GeneralPathClipped(view);
			else
				gp.reset();
			GeoFunction border = ineq.getFunBorder();
			updateStrokes(border);
			if(ineq.getType() == Inequality.INEQUALITY_PARAMETRIC_X){
				double ax = view.toRealWorldCoordY(0);
				double bx = view.toRealWorldCoordY(view.height);
				if (geo.isInverseFill() ^ ineq.isAboveBorder()) {
					gp.moveTo(view.width+10, -10);
					DrawParametricCurve.plotCurve(border, ax, bx, view, gp,
							false, false);
					gp.lineTo(view.width+10, view.height+10);
					gp.lineTo(view.width+10, -10);
					gp.closePath();
				} else {
					gp.moveTo(-10, -10);
					DrawParametricCurve.plotCurve(border, ax, bx, view, gp,
							false, false);
					gp.lineTo(-10, view.height+10);
					gp.lineTo(-10, -10);
					gp.closePath();
				}
				if (labelVisible) {
					xLabel = (int) Math.round((ax + bx) / 2) - 6;
					yLabel = (int) view.yZero - view.fontSize;
					labelDesc = geo.getLabelDescription();
					addLabelOffset();
				}
			}
			else{
				double ax = view.toRealWorldCoordX(0);
				double bx = view.toRealWorldCoordX(view.width);
				if (geo.isInverseFill() ^ ineq.isAboveBorder()) {
					gp.moveTo(-10, -10);
					DrawParametricCurve.plotCurve(border, ax, bx, view, gp,
							false, false);
					gp.lineTo(view.width+10, -10);
					gp.lineTo(-10, -10);
					gp.closePath();
				} else {
					gp.moveTo(-10, view.height+10);
					DrawParametricCurve.plotCurve(border, ax, bx, view, gp,
							false, false);
					gp.lineTo(view.width+10, view.height+10);
					gp.lineTo(-10, view.height+10);
					gp.closePath();
				}
				border.evaluateCurve(ax);
				if (labelVisible) {
					yLabel = (int) Math.round((ax + bx) / 2) - 6;
					xLabel = (int) view.xZero;
					labelDesc = geo.getLabelDescription();
					addLabelOffset();
				}
			}
			
		}
		
		private boolean  isXparametric(){
			return ineq.getType() == Inequality.INEQUALITY_PARAMETRIC_X;
		}
		
	}
}


