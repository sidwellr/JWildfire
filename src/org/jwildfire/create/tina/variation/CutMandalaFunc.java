package org.jwildfire.create.tina.variation;


import java.util.Random;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

import js.glsl.G;
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;



public class CutMandalaFunc  extends VariationFunc {

	/*
	 * Variation :cut_kaleido
	 * Date: august 29, 2020
	 * Jesus Sosa
	 * Reference & Credits: https://www.shadertoy.com/view/MttSzS
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_SEED = "seed";	
	private static final String PARAM_MODE = "mode";	
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	


	int seed=1000;
	int mode=1;
	double time=0.0;
	double zoom=2.0;
    int invert=1;


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
 	
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_MODE,PARAM_TIME,PARAM_ZOOM,PARAM_INVERT};


	double circle(vec2 p, double r, double width)
	{
	    double d = 0.;
	    d += G.smoothstep(1., 0., width*Math.abs(p.x - r));
	    return d;
	}

	double arc(vec2 p, double r, double a, double width)
	{
	    double d = 0.;
	    if (Math.abs(p.y) < a) {
		    d += G.smoothstep(1., 0., width*Math.abs(p.x - r));
	    }
	    return d;
	}


	double rose(vec2 p, double t, double width)
	{
	    double a0 = 6.;
	    double d = 0.;
	    p.x *= 7. + 8. * t;
	    d += G.smoothstep(1., 0., width*Math.abs(p.x - Math.sin(a0*p.y)));
	    d += G.smoothstep(1., 0., width*Math.abs(p.x - Math.abs(Math.sin(a0*p.y))));
	    d += G.smoothstep(1., 0., width*Math.abs(Math.abs(p.x) - Math.sin(a0*p.y)));
	    d += G.smoothstep(1., 0., width*Math.abs(Math.abs(p.x) - Math.abs(Math.sin(a0*p.y))));
	    return d;
	}

	double rose2(vec2 p, double t, double width)
	{
	    double a0 = 6.;
	    double d = 0.;
	    p.x *= 7. + 8. * t;
	    d += G.smoothstep(1., 0., width*Math.abs(p.x - Math.cos(a0*p.y)));
	    d += G.smoothstep(1., 0., width*Math.abs(p.x - Math.abs(Math.cos(a0*p.y))));
	    d += G.smoothstep(1., 0., width*Math.abs(Math.abs(p.x) - Math.cos(a0*p.y)));
	    d += G.smoothstep(1., 0., width*Math.abs(Math.abs(p.x) - Math.abs(Math.cos(a0*p.y))));
	    return d;
	}

	double spiral(vec2 p, double width)
	{
	    double d = 0.;
	    d += G.smoothstep(1., 0., width*Math.abs(p.x - 0.5 * p.y / Math.PI));
	    d += G.smoothstep(1., 0., width*Math.abs(p.x - 0.5 * Math.abs(p.y) / Math.PI));
	    d += G.smoothstep(1., 0., width*Math.abs(Math.abs(p.x) - 0.5 * p.y / Math.PI));
	    d += G.smoothstep(1., 0., width*Math.abs(Math.abs(p.x) - 0.5 * Math.abs(p.y) / Math.PI));
	    return d;
	}
	 	
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
		    
		    
		    vec2 p =new vec2(x*zoom,y*zoom);
		    
		    vec2 f = new vec2 ( Math.sqrt(p.x*p.x + p.y*p.y), G.atan2(p.y, p.x) );

		    // double Time=9.5; //iTime;
		    double T0 = Math.cos(0.3*time);
		    double T1 = 0.5 + 0.5 * Math.cos(0.3*time);
		    double T2 = 1.0; //sin(0.15*iTime);
		    
		    double m0 = 0.;
		    double m1 = 0.;
		    double m2 = 0.;
		    double m3 = 0.;
		    double m4 = 0.;
		    if (f.x < 0.7325) {
		        f.y += 0.1 *  time;
			    vec2 c;
			    vec2 f2;
		        c = new vec2(0.225 - 0.1*T0, Math.PI / 4.);
		        if (f.x < 0.25) {
		            for (double i=0.; i < 2.; ++i) {
		                f2 = G.mod(f, c).minus(  c.multiply(0.5));
		                m0 += spiral(new vec2(f2.x, f2.y), 192.);
		            }
		    	}
		        c = new vec2(0.225 + 0.1*T0, Math.PI / 4.);
		        if (f.x > 0.43) {
		            for (double i=0.; i < 2.; ++i) {
		                f.y += Math.PI / 8.;
		            	f2 = G.mod(f, c).minus( c.multiply(0.5));
		                m1 += rose(f2.multiply((0.75-0.5*T0)), 0.4*T1, 24.);
		                m1 += rose2(f2.multiply((0.5+0.5*T1)), 0.2 + 0.2*T0, 36.);
		            }
			    }
		        c = new vec2(0.6 - 0.2*T0, Math.PI / 4.);
		        if (f.x > 0.265) {
		            for (double i=0.; i < 2.; ++i) {
		                f.y += Math.PI / 8.;
		                f2 = G.mod(f, c).minus( c.multiply(0.5));
		                m2 += spiral(new vec2((0.25 + 0.5*T1)*f2.x, f2.y), 392.);
		                m2 += rose2(f2.multiply((1.+0.25*T0)), 0.5, 24.);
		            }
		        }
		        c = new vec2(0.4 + 0.23*T0, Math.PI / 4.);
		        if (f.x < 0.265) {
		            for (double i=0.; i < 2.; ++i) {
		                f.y += Math.PI / 8.;
		                f2 = G.mod(f, c).minus(c.multiply(0.5));
		                m3 += spiral(new vec2(f2.x, f2.y), 256.);
		                m3 += rose(f2, 1.5 * T1, 16.);
		            }
		        }
		        m4 += circle(f, 0.040, 192.);
		        m4 += circle(f, 0.265, 192.);
		        m4 += circle(f, 0.430, 192.);
		    }
		    m4 += circle(f, 0.7325, 192.);

		    // color
		    double z = m0 + m1 + m2 + m3 + m4;
		    z *= z;
		    z = G.clamp(z, 0., 1.);
		    double color=z;
		    //vec3 col = new vec3(z).multiply(new vec3(1.*T2));
		    
		    // Background
	    	double bkg = 0.0 ;
		                  	
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
		    pVarTP.x = pAmount * (x);
		    pVarTP.y = pAmount * (y);
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }
	  
	  
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

	   }

	  
	public String getName() {
		return "cut_mandala";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  seed,mode, time,zoom,invert});
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
		else if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
			time = pValue;
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
	
}

