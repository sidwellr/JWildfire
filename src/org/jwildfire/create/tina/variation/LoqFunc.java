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

import odk.lang.FastMath;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class LoqFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_BASE = "base";

  private static final String[] paramNames = {PARAM_BASE};

  private double base = M_E;
  private double denom;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    denom = 0.5 * pAmount / log(base);
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Loq by zephyrtronium http://zephyrtronium.deviantart.com/art/Quaternion-Apo-Plugin-Pack-165451482 */

    double abs_v = FastMath.hypot(pAffineTP.y, pAffineTP.z);
    double C = pAmount * atan2(abs_v, pAffineTP.x) / abs_v;
    pVarTP.x += log(sqr(pAffineTP.x) + sqr(abs_v)) * denom;
    pVarTP.y += C * pAffineTP.y;
    pVarTP.z += C * pAffineTP.z;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{base};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_BASE.equalsIgnoreCase(pName))
      base = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "loq";
  }
  
  @Override
  public void randomize() {
    base = Math.random() * 9.0 + 1.25;
    if (Math.random() < 0.5) base = 1 / base;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float denom = 0.5 * __loq / logf(__loq_base);\n"
        + "float abs_v = hypotf(__y, __z);\n"
        + "    float C = __loq * atan2f(abs_v, __x) / abs_v;\n"
        + "    __px += logf(__x*__x + abs_v*abs_v) * denom;\n"
        + "    __py += C * __y;\n"
        + "    __pz += C * __z;\n";
  }
}
