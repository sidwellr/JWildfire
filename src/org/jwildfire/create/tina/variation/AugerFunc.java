/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2021 Andreas Maschke

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

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class AugerFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_WEIGHT = "weight";
  private static final String PARAM_SYM = "sym";
  private static final String PARAM_SCALE = "scale";  
  private static final String RESSOURCE_DESCRIPTION = "description";

  private static final String[] paramNames = {PARAM_FREQ, PARAM_WEIGHT, PARAM_SYM, PARAM_SCALE};
  private static final String[] ressourceNames = {RESSOURCE_DESCRIPTION};
  
  private double freq = 1.00;
  private double weight = 0.5;
  private double sym = 0.1;
  private double scale = 0.9;

  private String description = "org.jwildfire.create.tina.variation.reference.ReferenceFile auger.txt";

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Auger, by Xyrus02
    double s = sin(freq * pAffineTP.x);
    double t = sin(freq * pAffineTP.y);
    double dy = pAffineTP.y + weight * (scale * s * 0.5 + fabs(pAffineTP.y) * s);
    double dx = pAffineTP.x + weight * (scale * t * 0.5 + fabs(pAffineTP.x) * t);

    pVarTP.x += pAmount * (pAffineTP.x + sym * (dx - pAffineTP.x));
    pVarTP.y += pAmount * dy;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{freq, weight, sym, scale};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQ.equalsIgnoreCase(pName))
      freq = pValue;
    else if (PARAM_WEIGHT.equalsIgnoreCase(pName))
      weight = pValue;
    else if (PARAM_SYM.equalsIgnoreCase(pName))
      sym = pValue;
    else if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][] {description.getBytes()};
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_DESCRIPTION.equalsIgnoreCase(pName)) {
      return RessourceType.REFERENCE;
    }
    else throw new IllegalArgumentException(pName);
  }
  
  @Override
  public String getName() {
    return "auger";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float s = sinf(__auger_freq*__x);\n"
        + "float t = sinf(__auger_freq*__y);\n"
        + "float dx = __x + __auger_weight*(__auger_scale*t/2.f+fabsf(__x)*t);\n"
        + "float dy = __y + __auger_weight*(__auger_scale*s/2.f+fabsf(__y)*s);\n"
        + "__px += __auger*(__x+__auger_sym*(dx-__x));\n"
        + "__py += __auger*dy;\n"
        + (context.isPreserveZCoordinate() ? "__pz += __auger*__z;\n" : "");
  }
}
