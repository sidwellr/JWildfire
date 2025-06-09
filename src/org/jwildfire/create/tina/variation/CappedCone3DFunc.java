/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

  This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser 
  General Public License as published by the Free Software Foundation; either version 2.1 of the 
  License, or (at your option) any later version.
 
  This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License along with this software; 
  if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jwildfire.create.tina.variation;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import js.glsl.G;
import js.glsl.vec2;
import js.glsl.vec3;

public class CappedCone3DFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  
	private static final String PARAM_P1 = "h";
	private static final String PARAM_P2 = "r1";
	private static final String PARAM_P3 = "r2";


// cappedcone3D with signed distance functions
// Author: Jesus Sosa
// Date:   17/july/ 2020
// Reference: 	https://www.iquilezles.org/www/articles/distfunctions/distfunctions.htm
//              https://www.shadertoy.com/view/Xds3zN
	
  double p1=0.5,p2=0.10,p3=0.30;

  private static final String[] additionalParamNames = { PARAM_P1,PARAM_P2,PARAM_P3};
  
  
  double dot2(  vec2 v )
  {
	  return G.dot(v,v); 
  }
  
  double dot2(  vec3 v )
  { 
	  return G.dot(v,v);
  }
  
  double sdCappedCone( vec3 p, double h,  double r1,  double r2 )
  {
      vec2 q = new vec2( G.length(new vec2(p.x,p.z)), p.y );
      
      vec2 k1 = new vec2(r2,h);
      vec2 k2 = new vec2(r2-r1,2.0*h);
      vec2 ca = new vec2(q.x-G.min(q.x,(q.y < 0.0)?r1:r2), G.abs(q.y)-h);
      vec2 cb = q.minus(k1).plus( k2.multiply(G.clamp( G.dot(k1.minus(q),k2)/dot2(k2), 0.0, 1.0 )  ));
      double s = (cb.x < 0.0 && ca.y < 0.0) ? -1.0 : 1.0;
      return s*Math.sqrt( G.min(dot2(ca),dot2(cb)) );
  }
  
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		for (int i=1; i<=50; i++) {
			double x = (pContext.random() - 0.5);
	    double y = (pContext.random() - 0.5);
	    double z = (pContext.random() - 0.5);
	    
	    vec3 p=new vec3(x,y,z);
	    double distance=sdCappedCone(p,p1,p2,p3);
	    
	    if(distance <0.0)
	    {
	    	pVarTP.doHide=false;
	    	pVarTP.x+=pAmount*x;
	    	pVarTP.y+=pAmount*y;
	    	pVarTP.z+=pAmount*z;
	    	return;
	    }
		}
    pVarTP.doHide=true;
  }
  
  @Override
  public String getName() {
    return "cappedcone3D";
  }
  
	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() {
		return new Object[] {p1,p2,p3};
	}
//
	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_P1)) {
			p1 =Tools.limitValue(pValue, -1.0, 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_P2)) {
			p2 =Tools.limitValue(pValue, -1.0, 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_P3)) {
			p3 =Tools.limitValue(pValue, -1.0, 1.0);
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
      p1 = Math.random() * 0.5;
      p2 = Math.random() * 0.5;
      p3 = Math.random() * 0.5;
    }

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	  @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "    float x = (RANDFLOAT() - 0.5);"
	    		+"    float y = (RANDFLOAT() - 0.5);"
	    		+"    float z = (RANDFLOAT() - 0.5);"
	    		+"    "
	    		+"    float3 p=make_float3(x,y,z);"
	    		+"    float distance=cappedcone3D_sdCappedCone(p,__cappedcone3D_h,__cappedcone3D_r1,__cappedcone3D_r2); "
	    		+"    __doHide=true;"
	    		+"    if(distance <0.0)"
	    		+"    {"
	    		+" 	    __doHide=false;"
	    		+"    	__px=__cappedcone3D*x;"
	    		+"    	__py=__cappedcone3D*y;"
	    		+"    	__pz=__cappedcone3D*z;"
	    		+"    }";
	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
		    return   "__device__   float  cappedcone3D_dot2 (  float2 v )"
		    		+"  {"
		    		+"	  return dot(v,v); "
		    		+"  }"

		    		+"__device__  float  cappedcone3D_sdCappedCone ( float3 p, float h,  float r1,  float r2 )"
		    		+"  {"
		    		+"      float2 q = make_float2( length(make_float2(p.x,p.z)), p.y );"
		    		+"      "
		    		+"      float2 k1 = make_float2(r2,h);"
		    		+"      float2 k2 = make_float2(r2-r1,2.0*h);"
		    		+"      float2 ca = make_float2(q.x-fminf(q.x,(q.y < 0.0)?r1:r2), fabsf(q.y)-h);"
		    		+"      float2 cb = q-(k1)+( k2*(clamp( dot(k1-(q),k2)/cappedcone3D_dot2(k2), 0.0, 1.0 )  ));"
		    		+"      float s = (cb.x < 0.0 && ca.y < 0.0) ? -1.0 : 1.0;"
		    		+"      return s*sqrtf( fminf( cappedcone3D_dot2 (ca),cappedcone3D_dot2(cb)) );"
		    		+"  }";
	  }
}
