package org.jwildfire.create.tina.variation;



import js.glsl.G;
import js.glsl.vec2;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class  CutVasarelyFunc  extends VariationFunc implements SupportsGPU {

	/*
	 * Variation : cut_vasarely
	 * Autor: Jesus Sosa
	 * Date: August 20, 2019
	 * Reference & Credits:  https://www.shadertoy.com/view/ltdXR8
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_MODE = "mode";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	private static final String PARAM_SIZE   = "size";
	


    int mode=1;
	double zoom=1.0;
	private int invert = 0;
	double size=0.5;




	private static final String[] additionalParamNames = { PARAM_MODE,PARAM_ZOOM,PARAM_INVERT,PARAM_SIZE};


	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		  double x,y;  
		  if(mode==0)
		    {
		      x= pAffineTP.x;
		      y =pAffineTP.y;
		    }else
		    {
		     x=2.0*pContext.random()-1.0;
		     y=2.0*pContext.random()-1.00;		     
		    }

			vec2 uv = new vec2 (x,y);
			uv=uv.multiply(zoom);
			

		    
		    double l = G.length(uv);
		    double b = Math.max(0.,2.-l/size);
		    
		    double color=0.;
		    color=Math.abs(uv.x)<1.?color-.5-Math.sin( l * Math.sin( G.atan2(uv.y,uv.x) + 1.57 + 4.*b*b)*150.) :color;
		    color -= Math.abs(uv.x)<1.?color-.5-Math.sin( l * Math.sin( G.atan2(uv.y,uv.x) + 1.57 + 4.*b*b)*150.) :color;
			
		    		     
		    pVarTP.doHide=false;
		    if(invert==0)
		    {
		      if (color<0.1)
		      { x=0.;
		        y=0.;
		        pVarTP.doHide = true;
		      }
		    } else
		    {
			      if (color>=0.1 )
			      { x=0.;
			        y=0.;
			        pVarTP.doHide = true;
			      }
		    }
		    pVarTP.x = pAmount * x;
		    pVarTP.y = pAmount * y;
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }

	public String getName() {
		return "cut_vasarely";
	}

	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] { mode,zoom,invert,size};
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_INVERT)) {
			   invert =   (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_SIZE)) {
			   size =   pValue;
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
		zoom = Math.random() * 2.0 + 0.01;
		invert = (int) (Math.random() * 2);
		size = Math.random() * 10.0;
	}

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "		  float x,y;  "
	    		+"		  if( __cut_vasarely_mode ==0)"
	    		+"		    {"
	    		+"		      x= __x;"
	    		+"		      y =__y;"
	    		+"		    }else"
	    		+"		    {"
	    		+"		     x=2.0*RANDFLOAT()-1.0;"
	    		+"		     y=2.0*RANDFLOAT()-1.00;		     "
	    		+"		    }"
	    		+"			float2 uv = make_float2 (x,y);"
	    		+"			uv=uv*( __cut_vasarely_zoom );"
	    		+"			"
	    		+"		    "
	    		+"		    float l = length(uv);"
	    		+"		    float b = fmaxf(0.,2.-l/__cut_vasarely_size);"
	    		+"		    "
	    		+"		    float color=0.0f;"
	    		+"		    color  = abs(uv.x)<1.?color-.5-sinf( l * sinf( atan2(uv.y,uv.x) + 1.57 + 4.*b*b)*150.) :color;"
	    		+"		    color -= abs(uv.x)<1.?color-.5-sinf( l * sinf( atan2(uv.y,uv.x) + 1.57 + 4.*b*b)*150.) :color;"
	    		+"		    		     "
	    		+"		    __doHide=false;"
	    		+"		    if( __cut_vasarely_invert ==0)"
	    		+"		    {"
	    		+"		      if (color<0.1)"
	    		+"		      { x=0.;"
	    		+"		        y=0.;"
	    		+"		        __doHide = true;"
	    		+"		      }"
	    		+"		    } else"
	    		+"		    {"
	    		+"			      if (color>=0.1 )"
	    		+"			      { x=0.;"
	    		+"			        y=0.;"
	    		+"			        __doHide = true;"
	    		+"			      }"
	    		+"		    }"
	    		+"		    __px = __cut_vasarely * x;"
	    		+"		    __py = __cut_vasarely * y;"
	            + (context.isPreserveZCoordinate() ? "__pz += __cut_vasarely * __z;\n" : "");
	  }
}


