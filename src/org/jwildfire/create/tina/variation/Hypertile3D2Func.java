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
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class Hypertile3D2Func extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_P = "p";
  private static final String PARAM_Q = "q";

  private static final String[] paramNames = {PARAM_P, PARAM_Q};

  private int p = 3;
  private int q = 7;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* hypertile3D2 by Zueuk, http://zueuk.deviantart.com/art/3D-Hyperbolic-tiling-plugins-169047926 */
    double r2 = sqr(pAffineTP.x) + sqr(pAffineTP.y) + sqr(pAffineTP.z);

    double x2cx = c2x * pAffineTP.x;

    double x = pAffineTP.x * s2x - cx * (-r2 - 1);
    double y = pAffineTP.y * s2y;

    double vr = pAmount / (c2 * r2 + x2cx + 1);

    double a = pContext.random(Integer.MAX_VALUE) * pa;
    double sina = sin(a);
    double cosa = cos(a);

    pVarTP.x += vr * (x * cosa + y * sina);
    pVarTP.y += vr * (y * cosa - x * sina);
    pVarTP.z += vr * (pAffineTP.z * s2z);
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{p, q};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_P.equalsIgnoreCase(pName))
      p = limitIntVal(Tools.FTOI(pValue), 3, Integer.MAX_VALUE);
    else if (PARAM_Q.equalsIgnoreCase(pName))
      q = limitIntVal(Tools.FTOI(pValue), 3, Integer.MAX_VALUE);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "hypertile3D2";
  }

  private double pa, qa, cx, c2, c2x, s2x, s2y, s2z;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    pa = 2 * M_PI / p;
    qa = 2 * M_PI / q;

    double r = -(cos(pa) - 1) / (cos(pa) + cos(qa));
    if (r > 0)
      r = 1 / sqrt(1 + r);
    else
      r = 1;

    cx = r;
    c2 = sqr(cx);
    c2x = 2 * cx;

    s2x = 1 + sqr(cx);
    s2y = 1 - sqr(cx);
    s2z = 1 - sqr(cx);
  }

  @Override
  public void randomize() {
    p = (int) (Math.random() * 8 + 3);
    q = (int) (Math.random() * 8 + 3);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float pa, qa, cx, c2, c2x, s2x, s2y, s2z;\n"
        + "pa = 2.f * PI / lroundf(__hypertile3D2_p);\n"
        + "    qa = 2.f * PI / lroundf(__hypertile3D2_q);\n"
        + "\n"
        + "    float r = -(cosf(pa) - 1.f) / (cosf(pa) + cosf(qa));\n"
        + "    if (r > 0)\n"
        + "      r = 1.f / sqrtf(1.f + r);\n"
        + "    else\n"
        + "      r = 1.f;\n"
        + "\n"
        + "    cx = r;\n"
        + "    c2 = cx*cx;\n"
        + "    c2x = 2.f * cx;\n"
        + "\n"
        + "    s2x = 1.f + cx*cx;\n"
        + "    s2y = 1.f - cx*cx;\n"
        + "    s2z = 1.f - cx*cx;\n"
        + "    float r2 = __x*__x + __y*__y + __z*__z;\n"
        + "\n"
        + "    float x2cx = c2x * __x;\n"
        + "\n"
        + "    float x = __x * s2x - cx * (-r2 - 1);\n"
        + "    float y = __y * s2y;\n"
        + "\n"
        + "    float vr = __hypertile3D2 / (c2 * r2 + x2cx + 1.f);\n"
        + "\n"
        + "    float a = lroundf(RANDFLOAT()*0x00007fff) * pa;\n"
        + "    float sina = sinf(a);\n"
        + "    float cosa = cosf(a);\n"
        + "\n"
        + "    __px += vr * (x * cosa + y * sina);\n"
        + "    __py += vr * (y * cosa - x * sina);\n"
        + "    __pz += vr * (__z * s2z);";
  }
}
