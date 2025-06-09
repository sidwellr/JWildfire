package org.jwildfire.create.tina.variation;


import java.util.Random;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

import js.glsl.G;
import js.glsl.mat2;
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;



public class KaplanFunc  extends VariationFunc implements SupportsGPU {

	/*
	 * Variation :kaplan
	 * Date: january 10, 2021
	 * Jesus Sosa
	 * Reference & Credits: https://www.shadertoy.com/view/4sfyzX
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_SEED = "seed";	
	private static final String PARAM_N = "N";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_INVERT = "invert";
	


	int seed=1000;
	int mode=1;
	int N=800;
	double time=10.0;
    int invert=0;

	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
 	
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_N,PARAM_TIME,PARAM_INVERT};
	
	 	
	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		    int x,y;

	        x =  (int)((N) * pContext.random());
	        y =  (int)((N) * pContext.random());  

		     double xv ;
		     double yv ;
		     
		    double zoom=G.floor(time);

		      xv = zoom* ( x  - (int) N/2.0);
		      yv = zoom* ( y  - (int) N/2.0);
    
//		    double r0 = G.mod(time / 100.0 + Math.acos(-1.0) / 4.0, Math.acos(-1.0)*2.0);
		    double r0 = G.atan(xv, yv);
		    mat2 rot = new mat2(Math.cos(r0), -Math.sin(r0), Math.sin(r0), Math.cos(r0));
			vec2 uv = new vec2(xv,yv).times(rot);
		    xv=uv.x;
		    yv=uv.y;

		    
		    double value = (xv*xv) + (yv*yv);
		    
		    double exponent =  G.floor(Math.log(value)/Math.log(2.));
		    double mantissa =  value*Math.pow(2.0, -exponent)-1.;
		    
		    double r = mantissa - G.floor(mantissa*Math.pow(2.,16.)+0.5)/Math.pow(2.,16.);
		    double color = G.sign((r));
		    		                  	
		    pVarTP.doHide=false;
		    if(invert==0)
		    {
		      if (color>0.0)
		      { x=0;
		        y=0;
		        pVarTP.doHide = true;	        
		      }
		    } else
		    {
			      if (color<=0.0)
			      { x=0;
			        y=0;
			        pVarTP.doHide = true;
			      }
		    }
		    pVarTP.x = pAmount * ((double)x/N- 0.5);
		    pVarTP.y = pAmount * ((double)y/N- 0.5);
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }
	  
	  
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

	   }

	  
	public String getName() {
		return "kaplan";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		 return (new Object[] {  seed, N, time, invert});
	}

	public void setParameter(String pName, double pValue) {
		if(pName.equalsIgnoreCase(PARAM_SEED))
		{
			   seed =   (int)pValue;
		       randomize=new Random(seed);
		          long current_time = System.currentTimeMillis();
		          elapsed_time += (current_time - last_time);
		          last_time = current_time;
		          time = (double) (elapsed_time / 1000.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_N)) {
			N =(int)Tools.limitValue(pValue, 50 , 1500);
		}
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
			time = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_INVERT)) {
			invert =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else
		      throw new IllegalArgumentException(pName);
	}
	
	@Override
	public boolean dynamicParameterExpansion() {
		return true;
	}

	@Override
	public boolean dynamicParameterExpansion(String pName) {
		// preset_id doesn't really expand parameters, but it changes them; this will make them refresh
		return true;
	}

	@Override
	public void randomize() {
		setParameter(PARAM_SEED, (int) (Math.random() * 1000000));
		N = (int) (Math.random() * 1450 + 50);
		invert = (int) (Math.random() * 2);
	}
	
	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	  @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "	        int x =  (int)(__kaplan_N * RANDFLOAT());"
	    		+"	        int y =  (int)(__kaplan_N * RANDFLOAT());"
	    		+"		    float zoom=floorf( __kaplan_time );"
	    		+"		    float xv = zoom* ( x  - (int) __kaplan_N/2.0);"
	    		+"		    float yv = zoom* ( y  - (int) __kaplan_N/2.0);"
	    		+"		    float r0 = atan2f(xv, yv);"
	    		+"          Mat2 rot;"
	    		+"		    Mat2_Init(&rot,cosf(r0), -sinf(r0), sinf(r0), cosf(r0));"
	    		+"			float2 uv = times(&rot,make_float2(xv,yv));"
	    		+"		    float value = uv.x*uv.x + uv.y*uv.y;"
	    		+"		    float exponent =  floorf(logf(value)/logf(2.0f));"
	    		+"		    float mantissa =  value*powf(2.0f, -exponent)-1.0f;"
	    		+"		    float r = mantissa - floorf(mantissa*powf(2.0f,16.0f)+0.5)/powf(2.0f,16.0f);"
	    		+"		    float color = sign(r);"
	    		+"		    __doHide=false;"
	    		+"		    if( __kaplan_invert ==0.0f)"
	    		+"		    {"
	    		+"		      if (color>0.0f)"
	    		+"		      { x=0;"
	    		+"		        y=0;"
	    		+"		        __doHide = true;"
	    		+"		      }"
	    		+"		    } else"
	    		+"		    {"
	    		+"			      if (color<=0.0f)"
	    		+"			      { x=0;"
	    		+"			        y=0;"
	    		+"			        __doHide = true;"
	    		+"			      }"
	    		+"		    }"
	    		+"		    __px = __kaplan * ( (float)x /__kaplan_N - 0.5f);"
	    		+"		    __py = __kaplan * ( (float)y /__kaplan_N - 0.5f);"
	            + (context.isPreserveZCoordinate() ? "__pz += __kaplan * __z;" : "");
	  }
}

