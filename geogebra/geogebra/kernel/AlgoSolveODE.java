package geogebra.kernel;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.ode.sampling.StepInterpolator;

public class AlgoSolveODE extends AlgoElement {

		private static final long serialVersionUID = 1L;
		private GeoFunctionNVar f0, f1; // input
		private GeoNumeric x, y, start, end, step; // input
	    private GeoList g; // output        
	    
	    public AlgoSolveODE(Construction cons, String label, GeoFunctionNVar f0, GeoFunctionNVar f1, GeoNumeric x, GeoNumeric y, GeoNumeric end, GeoNumeric step) {
	    	super(cons);
	        this.f0 = f0;            	
	        this.f1 = f1;            	
	        this.x = x;            	
	        this.y = y;            	   	
	        this.end = end;            	
	        this.step = step;            	
	    	
	        g = new GeoList(cons);                
	        setInputOutput(); // for AlgoElement        
	        compute();
	        g.setLabel(label);
	    }
	    
	    public String getClassName() {
	        return "AlgoSolveODE";
	    }
	    
	    // for AlgoElement
	    protected void setInputOutput() {
	        input = new GeoElement[f1 == null ? 5 : 6];
	    	int i = 0;
	    	
	        input[i++] = f0;
	        if (f1 != null) input[i++] = f1;
	        input[i++] = x;
	        input[i++] = y;
	        input[i++] = end;
	        input[i++] = step;

	        output = new GeoElement[1];
	        output[0] = g;
	        setDependencies(); // done by AlgoElement
	    }

	    public GeoList getResult() {
	        return g;
	    }

	    protected final void compute() {       
	        if (!f0.isDefined() || kernel.isZero(step.getDouble())) {
	        	g.setUndefined();
	        	return;
	        }    
	        
	        g.clear();
	        
	        //FirstOrderIntegrator integrator = new DormandPrince853Integrator(1.0e-8, 100.0, 1.0e-10, 1.0e-10);
	        FirstOrderIntegrator integrator = new ClassicalRungeKuttaIntegrator(step.getDouble());
	        FirstOrderDifferentialEquations ode;
	        
	        if (f1 == null) ode = new ODE(f0); else ode = new ODE2(f0,f1);
	        integrator.addStepHandler(stepHandler);
	        
            boolean oldState = cons.isSuppressLabelsActive();
            cons.setSuppressLabelCreation(true);
            g.add(new GeoPoint(cons, null, x.getDouble(), y.getDouble(), 1.0));
            cons.setSuppressLabelCreation(oldState);

	        double[] yy = new double[] { y.getDouble() }; // initial state
	        double[] yy2 = new double[] { x.getDouble(), y.getDouble() }; // initial state
	        try {
	        	if (f1 == null)
					integrator.integrate(ode, x.getDouble(), yy, end.getDouble(), yy);
	        	else
	        		integrator.integrate(ode, 0.0, yy2, end.getDouble(), yy2);
			} catch (DerivativeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IntegratorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // now y contains final state at time t=16.0
			
			g.setDefined(true);	
			
	    }
	    
	    final public String toString() {
	    	return getCommandDescription();
	    }

	    StepHandler stepHandler = new StepHandler() {
	        public void reset() {}
	        
	        Construction cons = kernel.getConstruction();
	                
	        public boolean requiresDenseOutput() { return false; }
	                
	        public void handleStep(StepInterpolator interpolator, boolean isLast) throws DerivativeException {
	            double   t = interpolator.getCurrentTime();
	            double[] y = interpolator.getInterpolatedState();
	            //System.out.println(t + " " + y[0]);
	            
	            boolean oldState = cons.isSuppressLabelsActive();
	            cons.setSuppressLabelCreation(true);
	            
	            if (f1 == null)
	            	g.add(new GeoPoint(cons, null, t, y[0], 1.0));
	            else
		            g.add(new GeoPoint(cons, null, y[0], y[1], 1.0));
	            	
	            cons.setSuppressLabelCreation(oldState);
	        }
	    };
	    //integrator.addStepHandler(stepHandler);
	    
	    private static class ODE implements FirstOrderDifferentialEquations {

	        GeoFunctionNVar f;

	        public ODE(GeoFunctionNVar f) {
	            this.f = f;
	        }

	        public int getDimension() {
	            return 1;
	        }

	        public void computeDerivatives(double t, double[] y, double[] yDot) {
	            
	        	double input[] = {t, y[0]};
	        	
	        	yDot[0] = f.evaluate(input);

	        }

	    }
	    
	    private static class ODE2 implements FirstOrderDifferentialEquations {

	        GeoFunctionNVar y0, y1;

	        public ODE2(GeoFunctionNVar y, GeoFunctionNVar x) {
	            this.y0 = y;
	            this.y1 = x;
	        }

	        public int getDimension() {
	            return 2;
	        }

	        public void computeDerivatives(double t, double[] y, double[] yDot) {
	            
	        	double input[] = {y[0], y[1]};
	        	
	        	yDot[0] = y1.evaluate(input);
	        	yDot[1] = y0.evaluate(input);

	        }

	    }
	}

