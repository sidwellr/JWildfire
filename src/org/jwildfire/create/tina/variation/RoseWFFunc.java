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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class RoseWFFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_AMP = "amp";
  private static final String PARAM_WAVES = "waves";
  private static final String PARAM_FILLED = "filled";
  private static final String[] paramNames = {PARAM_AMP, PARAM_WAVES, PARAM_FILLED};

  private double amp = 0.5;
  private int waves = 4;
  private double filled = 0.85;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double a = pAffineTP.getPrecalcAtan();
    double r = amp * cos(waves * a);

    if (filled > 0 && filled > pContext.random()) {
      r *= pContext.random();
    }

    double nx = sin(a) * r;
    double ny = cos(a) * r;

    pVarTP.x += pAmount * nx;
    pVarTP.y += pAmount * ny;
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
    return new Object[]{amp, waves, filled};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_AMP.equalsIgnoreCase(pName))
      amp = pValue;
    else if (PARAM_WAVES.equalsIgnoreCase(pName))
      waves = Tools.FTOI(pValue);
    else if (PARAM_FILLED.equalsIgnoreCase(pName))
      filled = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "rose_wf";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }
  
  @Override
  public void randomize() {
    amp = Math.random() + 0.1;
    waves = (int) (Math.random() * 25 + 1);
    if (Math.random() < 0.5) filled = 0.0;
    else filled = Math.random();
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "   float a = __phi;\n"
        + "    float r = __rose_wf_amp * cosf(lroundf(__rose_wf_waves) * a);\n"
        + "\n"
        + "    if (__rose_wf_filled > 0 && __rose_wf_filled>RANDFLOAT()) {\n"
        + "      r *= RANDFLOAT();\n"
        + "    }\n"
        + "\n"
        + "    float nx = sinf(a) * r;\n"
        + "    float ny = cosf(a) * r;\n"
        + "\n"
        + "    __px += __rose_wf * nx;\n"
        + "    __py += __rose_wf * ny;\n"
        + (context.isPreserveZCoordinate() ? "__py += __rose_wf * __z;\n" : "");
  }
}
