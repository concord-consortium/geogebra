package geogebra3D.euclidian3D.opengl;

import java.awt.Color;

import geogebra.Matrix.GgbVector;
import geogebra.main.Application;
import geogebra3D.euclidian3D.EuclidianView3D;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;


/**
 * 
 * Manager using GL lists
 * 
 * @author ggb3D
 *
 */

public class ManagerGLList extends Manager {


	
	// GL 
	private GLUtessellator tesselator;
	

	/** common constructor
	 * @param gl
	 * @param glu 
	 * @param view3D 3D view
	 */
	public ManagerGLList(GL gl, GLU glu, EuclidianView3D view3D) {
		super(gl,glu,view3D);
	}

	
	/////////////////////////////////////////////
	// LISTS METHODS
	/////////////////////////////////////////////


	private int genLists(int nb){
		return gl.glGenLists(nb);
	}
	
	public void preInit(Geometry geometry){
		
		int index = genLists(geometry.getNb());
		//Application.debug("index = "+index);
		geometry.setIndex(index);

	}
	
	
	
	
	
	/////////////////////////////////////////////
	// GEOMETRY METHODS
	/////////////////////////////////////////////

	
	public int startNewList(){
		// generates a new list
		int ret = genLists(1);
		
		gl.glNewList(ret, GL.GL_COMPILE);
		
		return ret;
	}
	
	private void newList(int index){
		gl.glNewList(index, GL.GL_COMPILE);
	}
	
	
	public void startList(Geometry geometry, int index){
		
		newList(geometry.getIndex()+index);
		
	}
	
	public void endList(){
		
		gl.glEndList();
	}
	
	public void startGeometry(int type){
		gl.glBegin(type);
	}
	
	public void startGeometry(Geometry geometry){
		startGeometry(geometry.getType());
	}
	
	public void endGeometry(){
		gl.glEnd();
	}

	

	
	
	/////////////////////////////////////////////
	// POLYGONS METHODS
	/////////////////////////////////////////////

	
	/** start a new polygon 
	 * @param nx normal x coordinate
	 * @param ny normal y coordinate
	 * @param nz normal z coordinate
	 * @return gl index
	 */
	public int startPolygon(float nx, float ny, float nz){
		
		// generates a new list
		int ret = genLists(1);
		
		//Application.debug("ret = "+ret);
		
		// if ret == 0, there's no list
		if (ret == 0)
			return 0;
		
	    RendererTesselCallBack tessCallback = new RendererTesselCallBack(gl, glu);
	    
	    tesselator = glu.gluNewTess();

	    glu.gluTessCallback(tesselator, GLU.GLU_TESS_VERTEX, tessCallback);// vertexCallback);
	    glu.gluTessCallback(tesselator, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
	    glu.gluTessCallback(tesselator, GLU.GLU_TESS_END, tessCallback);// endCallback);
	    glu.gluTessCallback(tesselator, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback);
	    glu.gluTessCallback(tesselator, GLU.GLU_TESS_COMBINE, tessCallback);// combineCallback);

	    
	    newList(ret);
	    
    	//normal(nx, ny, nz);
    	
	    glu.gluTessBeginPolygon(tesselator, null);
	    glu.gluTessBeginContour(tesselator);
	    
	    glu.gluTessNormal(tesselator,nx,ny,nz);
		normal(nx, ny, nz);
		/*
		newList(ret);
		
		gl.glBegin(GL.GL_TRIANGLES);
		normal(nx, ny, nz);
		*/
	    
	    return ret;

		
	}
	
	
	
    /**
     * ends the current polygon
     */
    public void endPolygon(){
    	
    	
	    glu.gluTessEndContour(tesselator);
	    glu.gluTessEndPolygon(tesselator);
	    gl.glEndList();
	    
	    glu.gluDeleteTess(tesselator);
	    
    	
    	//endGeometry(null);
        
	
    }
    
    
    /** remove the polygon from gl memory
     * @param index
     */
    public void remove(int index){
    	
    	gl.glDeleteLists(index, 1);
    	
    }

	
	/////////////////////////////////////////////
	// PLANE METHODS
	/////////////////////////////////////////////

    public int newPlane(Color color, float alpha, float size){
    	return plane.create(color,alpha,size);
    }
    
    
	/////////////////////////////////////////////
	// SPHERE METHODS
	/////////////////////////////////////////////

    public int newSphere(float x, float y, float z,
			float radius, Color color, float alpha){
    	return sphere.create(x,y,z,radius,color,alpha);
    }
 	
	
	
	/////////////////////////////////////////////
	// DRAWING METHODS
	/////////////////////////////////////////////

	public void draw(int index){
		gl.glCallList(index);
	}
	
	public void draw(Geometry geometry, int index){

		gl.glCallList(geometry.getIndex()+index);
		
	}
	

	
	protected void texture(float x, float y){
		
		gl.glTexCoord2f(x,y);
		
	}

	
	protected void normal(float x, float y, float z){
		
		gl.glNormal3f(x,y,z); 
		
	}
	
	
	protected void vertex(float x, float y, float z){
		
		gl.glVertex3f(x,y,z); 
		
	}
	
	
	
	
	protected void color(float r, float g, float b){
		gl.glColor3f(r,g,b);
	}
	
	protected void color(float r, float g, float b, float a){
		gl.glColor4f(r,g,b,a);
	}
	
	/////////////////////////////////////////////
	// POLYGONS DRAWING METHODS
	/////////////////////////////////////////////

	
	public void addVertexToPolygon(double x, double y, double z){
		
		
		double[] point = {x,y,z};
		glu.gluTessVertex(tesselator, point, 0, point);
		
		
		//vertex((float) x, (float) y,(float)  z);
	}
	
	

	


}
