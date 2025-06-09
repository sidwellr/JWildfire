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

import java.io.Serializable;

import static org.jwildfire.base.mathlib.MathLib.*;

public class GlynnSim2Func extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_THICKNESS = "thickness";
  private static final String PARAM_CONTRAST = "contrast";
  private static final String PARAM_POW = "pow";
  private static final String PARAM_PHI1 = "phi1";
  private static final String PARAM_PHI2 = "phi2";

  private static final String[] paramNames = {PARAM_RADIUS, PARAM_THICKNESS, PARAM_CONTRAST, PARAM_POW, PARAM_PHI1, PARAM_PHI2};

  private double radius = 1.0;
  private double thickness = 0.1;
  private double contrast = 0.5;
  private double pow = 1.5;
  private double phi1 = 110.0;
  private double phi2 = 150.0;

  private static class Point implements Serializable {
    private static final long serialVersionUID = 1L;

    private double x, y;
  }

  private void circle(FlameTransformationContext pContext, Point p) {
    double r = this.radius + this.thickness - this._gamma * pContext.random();
    double Phi = this._phi10 + this._delta * pContext.random();
    double sinPhi = sin(Phi);
    double cosPhi = cos(Phi);
    p.x = r * cosPhi;
    p.y = r * sinPhi;
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* GlynnSim2 by eralex61, http://eralex61.deviantart.com/art/GlynnSim-plugin-112621621 */
    double r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double Alpha = this.radius / r;
    if (r < this.radius) {
      circle(pContext, toolPoint);
      pVarTP.x += pAmount * toolPoint.x;
      pVarTP.y += pAmount * toolPoint.y;
    } else {
      if (pContext.random() > this.contrast * pow(Alpha, this._absPow)) {
        pVarTP.x += pAmount * pAffineTP.x;
        pVarTP.y += pAmount * pAffineTP.y;
      } else {
        pVarTP.x += pAmount * Alpha * Alpha * pAffineTP.x;
        pVarTP.y += pAmount * Alpha * Alpha * pAffineTP.y;
      }
    }
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
    return new Object[]{radius, thickness, contrast, pow, phi1, phi2};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_RADIUS.equalsIgnoreCase(pName))
      radius = pValue;
    else if (PARAM_THICKNESS.equalsIgnoreCase(pName))
      thickness = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_CONTRAST.equalsIgnoreCase(pName))
      contrast = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_POW.equalsIgnoreCase(pName))
      pow = pValue;
    else if (PARAM_PHI1.equalsIgnoreCase(pName))
      phi1 = pValue;
    else if (PARAM_PHI2.equalsIgnoreCase(pName))
      phi2 = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "glynnSim2";
  }

  @Override
  public String[] getParameterAlternativeNames() {
    return new String[]{"GlynnSim2_radius", "GlynnSim2_thickness", "GlynnSim2_contrast", "GlynnSim2_pow", "GlynnSim2_Phi1", "GlynnSim2_Phi2"};
  }

  private Point toolPoint = new Point();
  private double _phi10, _phi20, _gamma, _delta, _absPow;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    this._phi10 = M_PI * this.phi1 / 180.0;
    this._phi20 = M_PI * this.phi2 / 180.0;
    this._gamma = this.thickness * (2.0 * this.radius + this.thickness) / (this.radius + this.thickness);
    this._delta = this._phi20 - this._phi10;
    this._absPow = fabs(this.pow);
  }

  @Override
  public void randomize() {
  	radius = Math.random() * 1.5 + 0.25;
  	thickness = Math.random();
  	contrast = Math.random();
  	pow = Math.random() * 2.0 + 0.5;
  	phi1 = Math.random() * 360.0 - 180.0;
  	phi2 = Math.random() * 360.0 - 180.0;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float _phi10, _phi20, _gamma, _delta, _absPow, tx, ty;\n"
        + "_phi10 = PI * __glynnSim2_phi1 / 180.0;\n"
        + "    _phi20 = PI * __glynnSim2_phi2 / 180.0;\n"
        + "    _gamma = __glynnSim2_thickness * (2.0 * __glynnSim2_radius + __glynnSim2_thickness) / (__glynnSim2_radius + __glynnSim2_thickness);\n"
        + "    _delta = _phi20 - _phi10;\n"
        + "    _absPow = fabsf(__glynnSim2_pow);\n"
        + " float r = sqrtf(__x * __x + __y * __y);\n"
        + "    float Alpha = __glynnSim2_radius / r;\n"
        + "    if (r < __glynnSim2_radius) {\n"
        + "      glynnSim2_circle(&tx, &ty, RANDFLOAT(), RANDFLOAT(), __glynnSim2_radius, __glynnSim2_thickness, _gamma, _phi10, _delta);\n"
        + "      __px += __glynnSim2 * tx;\n"
        + "      __py += __glynnSim2 * ty;\n"
        + "    } else {\n"
        + "      if (RANDFLOAT() > __glynnSim2_contrast * powf(Alpha, _absPow)) {\n"
        + "        __px += __glynnSim2 * __x;\n"
        + "        __py += __glynnSim2 * __y;\n"
        + "      } else {\n"
        + "        __px += __glynnSim2 * Alpha * Alpha * __x;\n"
        + "        __py += __glynnSim2 * Alpha * Alpha * __y;\n"
        + "      }\n"
        + "    }\n"
        + (context.isPreserveZCoordinate() ? "      __pz += __glynnSim2 * __z;\n" : "");
  }

  @Override
  public String getGPUFunctions(FlameTransformationContext context) {
    return "__device__ void glynnSim2_circle(float *x, float *y, float rnd1, float rnd2, float radius, float thickness, float gamma, float phi10, float delta) {\n"
        + "  float r = radius + thickness - gamma * rnd1;\n"
        + "  float Phi = phi10 + delta * rnd2;\n"
        + "  float sinPhi = sinf(Phi);\n"
        + "  float cosPhi = cosf(Phi);\n"
        + "  *x = r * cosPhi;\n"
        + "  *y = r * sinPhi;\n"
        + "}";
  }

}
