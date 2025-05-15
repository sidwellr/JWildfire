package org.jwildfire.create.tina.variation;


import java.util.Random;
import js.glsl.G;
import js.glsl.vec2;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class CutFractalFunc  extends VariationFunc implements SupportsGPU {

	/*
	 * Variation :cut_fractal
	 * Date: august 29, 2019
	 * Author: Jesus Sosa
	 * Reference:  https://www.shadertoy.com/view/ltlfRn
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_SEED = "seed";	
	private static final String PARAM_MODE = "mode";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ITERS = "iters";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	
	int seed=0;
	int mode=1;
	double time=0.0;
	int iters=30;
    double zoom=1.0;
    int invert=0;


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
 	

    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_MODE,PARAM_TIME,PARAM_ITERS,PARAM_ZOOM,PARAM_INVERT};
	 	
	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		    double x,y;
		    
		    if(mode==0)
		    {
		      x= pAffineTP.x;
		      y =pAffineTP.y;
		    }else
		    {
		     x=pContext.random()-0.5;
		     y=pContext.random()-0.5; 		     
		    }
		    
		    vec2 c = new vec2(Math.cos(time+1.5), Math.sin(time+1.8) ).multiply(0.15).minus(0.25);
		    vec2 t =new vec2(x*zoom,y*zoom);
            
            vec2 U=new vec2(t.y,t.x);

            for (int i=0; i<iters; i++)
              U = G.abs(U).multiply( 0.5/ G.dot(U, U)).plus(c);
            
             double color=Math.abs(G.dot(U, U) - .01) *2.;
		    pVarTP.doHide=false;
		    if(invert==0)
		    {
		      if (color>0.5)
		      { x=0;
		        y=0;
		        pVarTP.doHide = true;	        
		      }
		    } else
		    {
			      if (color<=0.5)
			      { x=0;
			        y=0;
			        pVarTP.doHide = true;
			      }
		    }
		    pVarTP.x = pAmount * (x);
		    pVarTP.y = pAmount * (y);
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }
	  
	  
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		  randomize=new Random(seed);
	   }

	  
	public String getName() {
		return "cut_fractal";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  seed, mode,time,iters,zoom,invert});
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_TIME)) {
			time = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else if(pName.equalsIgnoreCase(PARAM_SEED))
		{
			   seed =   (int)pValue;
		       randomize=new Random(seed);
		          long current_time = System.currentTimeMillis();
		          elapsed_time += (current_time - last_time);
		          last_time = current_time;
		          time = (double) (elapsed_time / 1000.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_ITERS)) {
			iters =(int)Tools.limitValue(pValue, 1 , 100);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom =pValue;
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
		// Don't change mode
		setParameter(PARAM_SEED, (int) (Math.random() * 1000000));
		iters = ((int) (Math.random() * 20 + 5)) * 2;
		zoom = Math.random() + 1.0;
		invert = (int) (Math.random() * 2);
	}

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return  "		    float x,y;"
	    		+"		    "
	    		+"		    if( __cut_fractal_mode ==0)"
	    		+"		    {"
	    		+"		      x= __x;"
	    		+"		      y =__y;"
	    		+"		    }else"
	    		+"		    {"
	    		+"		     x=RANDFLOAT()-0.5;"
	    		+"		     y=RANDFLOAT()-0.5; 		     "
	    		+"		    }"
	    		+"		    "
	    		+"		    float2 c = make_float2(cos( __cut_fractal_time +1.5), sin( __cut_fractal_time +1.8) )*(0.15)-(0.25);"
	    		+"		    float2 t =make_float2(x* __cut_fractal_zoom ,y* __cut_fractal_zoom );"
	    		+"            "
	    		+"          float2 U=make_float2(t.y,t.x);"
	    		+"          for (int i=0; i<  __cut_fractal_iters ; i++)"
	    		+"              U = abs(U)*( 0.5/ dot(U, U))+(c);"
	    		+"            "
	    		+"          float color=fabsf(dot(U, U) - .01) *2.;"
	    		+"		    __doHide=false;"
	    		+"		    if( __cut_fractal_invert ==0)"
	    		+"		    {"
	    		+"		      if (color>0.5)"
	    		+"		      { x=0;"
	    		+"		        y=0;"
	    		+"		        __doHide = true;	        "
	    		+"		      }"
	    		+"		    } else"
	    		+"		    {"
	    		+"			      if (color<=0.5)"
	    		+"			      { x=0;"
	    		+"			        y=0;"
	    		+"			        __doHide = true;"
	    		+"			      }"
	    		+"		    }"
	    		+"		    __px = __cut_fractal * (x);"
	    		+"		    __py = __cut_fractal * (y);"
	            + (context.isPreserveZCoordinate() ? "__pz += __cut_fractal * __z;\n" : "");
	  }
}