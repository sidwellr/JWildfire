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

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class Elliptic2Func extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;


  private static final String PARAM_A1 = "a1";
  private static final String PARAM_A2 = "a2";
  private static final String PARAM_A3 = "a3";
  private static final String PARAM_B1 = "b1";
  private static final String PARAM_B2 = "b2";
  private static final String PARAM_C = "c";
  private static final String PARAM_D = "d";
  private static final String PARAM_E = "e";
  private static final String PARAM_F = "f";
  private static final String PARAM_G = "g";
  private static final String PARAM_H = "h";
  private static final String[] paramNames = {PARAM_A1, PARAM_A2, PARAM_A3, PARAM_B1, PARAM_B2, PARAM_C, PARAM_D, PARAM_E, PARAM_F, PARAM_G, PARAM_H};


  private double a1 = 1.0;
  private double a2 = 1.0;
  private double a3 = 0.0;
  private double b1 = 2.0;
  private double b2 = 1.0;
  private double c = 0.5;
  private double d = 1.0;
  private double e = 0.5;
  private double f = 1.0;
  private double g = 1.0;
  private double h = 2.0;

  private double sqrt_safe(double x) {
    return (x < SMALL_EPSILON) ? 0.0 : sqrt(x);
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Elliptic with variables added by Brad Stefanov */

    double tmp = pAffineTP.y * pAffineTP.y + pAffineTP.x * pAffineTP.x + a1;
    double x2 = b1 * pAffineTP.x;
    double xmax = c * (sqrt(tmp + x2) + sqrt(tmp - x2));

    double a = pAffineTP.x / xmax * a2;
    double b = sqrt_safe(d - a * a) * b2;
    double ps = -M_PI_2 * a3;
    pVarTP.x += _v * atan2(a, b) + ps;

    //    if (pAffineTP.y > 0)
    if (pContext.random() < e)
      pVarTP.y += _v * log(xmax + sqrt_safe(xmax - f));
    else
      pVarTP.y -= _v * log(xmax + sqrt_safe(xmax - g));
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
    return new Object[]{a1, a2, a3, b1, b2, c, d, e, f, g, h};
  }

  @Override
  public void setParameter(String pName, double pValue) {

    if (PARAM_A1.equalsIgnoreCase(pName)) {
      a1 = pValue;
    } else if (PARAM_A2.equalsIgnoreCase(pName)) {
      a2 = pValue;
    } else if (PARAM_A3.equalsIgnoreCase(pName)) {
      a3 = pValue;
    } else if (PARAM_B1.equalsIgnoreCase(pName)) {
      b1 = pValue;
    } else if (PARAM_B2.equalsIgnoreCase(pName)) {
      b2 = pValue;
    } else if (PARAM_C.equalsIgnoreCase(pName)) {
      c = pValue;
    } else if (PARAM_D.equalsIgnoreCase(pName)) {
      d = pValue;
    } else if (PARAM_E.equalsIgnoreCase(pName)) {
      e = pValue;
    } else if (PARAM_F.equalsIgnoreCase(pName)) {
      f = pValue;
    } else if (PARAM_G.equalsIgnoreCase(pName)) {
      g = pValue;
    } else if (PARAM_H.equalsIgnoreCase(pName)) {
      h = pValue;
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "elliptic2";
  }

  private double _v;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _v = pAmount / (M_PI / h);
  }
  
  @Override
  public void randomize() {
    a1 = Math.random();
    a2 = Math.random() * 0.5 + 0.75;
    a3 = Math.random() * 2.0 - 1.0;
    b1 = Math.random() * 1.0 + 1.0;
    b2 = Math.random() * 1.5 + 0.5;
    c = Math.random() * 0.5 + 0.25;
    d = Math.random() * 1.5 + 0.25;
    e = Math.random();
    f = Math.random() * 1.5 + 0.5;
    g = Math.random() * 1.5 + 0.5;
    h = Math.random() * 2.0 + 1.0;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float _v = __elliptic2 / (PI / __elliptic2_h);\n"
        + "float tmp = __y * __y + __x * __x + __elliptic2_a1;\n"
        + "float x2 = __elliptic2_b1 * __x;\n"
        + "float xmax = __elliptic2_c * (sqrtf(tmp + x2) + sqrtf(tmp - x2));\n"
        + "float a = __x / xmax * __elliptic2_a2;\n"
        + "float b = sqrtf_safe(__elliptic2_d - a * a) * __elliptic2_b2;\n"
        + "float ps = -PI*0.5f * __elliptic2_a3;\n"
        + "__px += _v * atan2f(a, b) + ps;\n"
        + "if (RANDFLOAT() < __elliptic2_e)\n"
        + "  __py += _v * logf(xmax + sqrtf_safe(xmax - __elliptic2_f));\n"
        + "else\n"
        + "  __py -= _v * logf(xmax + sqrtf_safe(xmax - __elliptic2_g));\n"
        + (context.isPreserveZCoordinate() ? "__pz += __elliptic2 * __z;\n" : "");
  }
}