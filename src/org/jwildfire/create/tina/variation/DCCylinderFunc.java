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

public class DCCylinderFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_OFFSET = "offset";
  private static final String PARAM_ANGLE = "angle";
  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_X = "x";
  private static final String PARAM_Y = "y";
  private static final String PARAM_BLUR = "blur";

  private static final String[] paramNames = {PARAM_OFFSET, PARAM_ANGLE, PARAM_SCALE, PARAM_X, PARAM_Y, PARAM_BLUR};

  private static final String RESSOURCE_DESCRIPTION = "description";
  private static final String[] ressourceNames = {RESSOURCE_DESCRIPTION};
  
  private double offset = 0.0;
  private double angle = 0.0;
  private double scale = 0.5;
  private double x = 0.125;
  private double y = 0.125;
  private double blur = 1.0;
  private int n = 0;
  private double[] r = {0.0, 0.0, 0.0, 0.0};
  private double ldcs, ldca;

  private String description = "org.jwildfire.create.tina.variation.reference.ReferenceFile dc_cylinder-help.txt";

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    n = 0;
    r[0] = pContext.random();
    r[1] = pContext.random();
    r[2] = pContext.random();
    r[3] = pContext.random();
    ldcs = 1.0 / (scale == 0 ? 10e-6 : scale);
    ldca = offset * M_PI;
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* dc_cylinder by FracFx, http://fracfx.deviantart.com/art/DC-Cylinder-Plugin-for-Apo-7x-260748952 */

    double a = pContext.random() * M_2PI;
    double sr = sin(a);
    double cr = cos(a);
    double rr = blur * (r[0] + r[1] + r[2] + r[3] - 2.0);
    r[n] = pContext.random();
    n = n + 1 & 3;


    pVarTP.x += pAmount * sin(pAffineTP.x + rr * sr) * x;
    pVarTP.y += rr + pAffineTP.y * y;
    pVarTP.z += pAmount * cos(pAffineTP.x + rr * cr);

    pVarTP.color = fmod(fabs(0.5 * (ldcs * ((cos(angle) * pVarTP.x + sin(angle) * pVarTP.y + offset)) + 1.0)), 1.0);

  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{offset, angle, scale, x, y, blur};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_OFFSET.equalsIgnoreCase(pName))
      offset = pValue;
    else if (PARAM_ANGLE.equalsIgnoreCase(pName))
      angle = pValue;
    else if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = pValue;
    else if (PARAM_X.equalsIgnoreCase(pName))
      x = pValue;
    else if (PARAM_Y.equalsIgnoreCase(pName))
      y = pValue;
    else if (PARAM_BLUR.equalsIgnoreCase(pName))
      blur = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String[] getParameterAlternativeNames() {
    return new String[]{"dc_cyl_offset", "dc_cyl_angle", "dc_cyl_scale", "cyl_x", "cyl_y", "cyl_blur"};
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
    return "dc_cylinder";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float ldcs, ldca;\n"
        + "ldcs = 1.0f / (__dc_cylinder_scale == 0 ? 1.0e-5f : __dc_cylinder_scale);\n"
        + "ldca = __dc_cylinder_offset * PI;\n"
        + "float a = RANDFLOAT() * 2.0f*PI;\n"
        + "float sr = sinf(a);\n"
        + "float cr = cosf(a);\n"
        + "float rr = __dc_cylinder_blur * (RANDFLOAT() + RANDFLOAT() + RANDFLOAT() + RANDFLOAT() - 2.0f);\n"
        + "__px += __dc_cylinder * sinf(__x + rr * sr) * __dc_cylinder_x;\n"
        + "__py += rr + __y * __dc_cylinder_y;\n"
        + "__pz += __dc_cylinder * cosf(__x + rr * cr);\n"
        + "__pal = fmodf(fabsf(0.5f * (ldcs * ((cosf(__dc_cylinder_angle) * __px + sinf(__dc_cylinder_angle) * __py + __dc_cylinder_offset)) + 1.0f)), 1.0);\n";
  }
}

