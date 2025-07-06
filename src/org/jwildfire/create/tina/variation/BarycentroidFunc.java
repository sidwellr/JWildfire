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

import static org.jwildfire.base.mathlib.MathLib.sqr;
import static org.jwildfire.base.mathlib.MathLib.sqrt;

public class BarycentroidFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";
  private static final String PARAM_D = "d";
  private static final String[] paramNames = {PARAM_A, PARAM_B, PARAM_C, PARAM_D};

  private static final String RESSOURCE_DESCRIPTION = "description";
  private static final String[] ressourceNames = {RESSOURCE_DESCRIPTION};
  
  private double a = 1.0;
  private double b = 0.0;
  private double c = 0.0;
  private double d = 1.0;

  private String description = "org.jwildfire.create.tina.variation.reference.ReferenceFile barycentroid.txt";

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* barycentroid from Xyrus02, http://xyrusworx.deviantart.com/art/Barycentroid-Plugin-144832371?q=sort%3Atime+favby%3Amistywisp&qo=0&offset=10 */
    // helpers

    /*  The code is supposed to be fast and you all can read it so I dont 
        create those aliases for readability in actual code:
               
        v0x = this.a
        v0y = this.b 
        v1x = this.c
        v1y = this.d
        v2x = pAffineTP.x
        v2y = pAffineTP.y
    */

    // compute dot products
    double dot00 = this.a * this.a + this.b * this.b; // v0 * v0
    double dot01 = this.a * this.c + this.b * this.d; // v0 * v1
    double dot02 = this.a * pAffineTP.x + this.b * pAffineTP.y; // v0 * v2
    double dot11 = this.c * this.c + this.d * this.d; // v1 * v1
    double dot12 = this.c * pAffineTP.x + this.d * pAffineTP.y; // v1 * v2

    // compute inverse denomiator
    double invDenom = 1 / (dot00 * dot11 - dot01 * dot01);

    /* now we can pull [u,v] as the barycentric coordinates of the point 
       P in the triangle [A, B, C]
    */
    double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
    double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

    // now combine with input
    double um = sqrt(sqr(u) + sqr(pAffineTP.x)) * sgn(u);
    double vm = sqrt(sqr(v) + sqr(pAffineTP.y)) * sgn(v);

    pVarTP.x += pAmount * um;
    pVarTP.y += pAmount * vm;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  private double sgn(double v) {
    return v < 0.0 ? -1.0 : v > 0 ? 1.0 : 0.0;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{a, b, c, d};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_A.equalsIgnoreCase(pName))
      a = pValue;
    else if (PARAM_B.equalsIgnoreCase(pName))
      b = pValue;
    else if (PARAM_C.equalsIgnoreCase(pName))
      c = pValue;
    else if (PARAM_D.equalsIgnoreCase(pName))
      d = pValue;
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
    return "barycentroid";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return " float dot00 = __barycentroid_a * __barycentroid_a + __barycentroid_b * __barycentroid_b;\n"
        + "    float dot01 = __barycentroid_a * __barycentroid_c + __barycentroid_b * __barycentroid_d;\n"
        + "    float dot02 = __barycentroid_a * __x + __barycentroid_b * __y;\n"
        + "    float dot11 = __barycentroid_c * __barycentroid_c + __barycentroid_d * __barycentroid_d;\n"
        + "    float dot12 = __barycentroid_c * __x + __barycentroid_d * __y;\n"
        + "\n"
        + "    float invDenom = 1.0f / (dot00 * dot11 - dot01 * dot01);\n"
        + "\n"
        + "    float u = (dot11 * dot02 - dot01 * dot12) * invDenom;\n"
        + "    float v = (dot00 * dot12 - dot01 * dot02) * invDenom;\n"
        + "\n"
        + "    float um = sqrtf(u*u + __x*__x) * (u<0.f ? -1 : u>0.f ? 1 : 0);\n"
        + "    float vm = sqrtf(v*v + __y*__y) * (v<0.f ? -1 : v>0.f ? 1 : 0);\n"
        + "\n"
        + "    __px += __barycentroid * um;\n"
        + "    __py += __barycentroid * vm;\n"
        + "\n"
        + (context.isPreserveZCoordinate() ? "__pz += __barycentroid*__z;\n" : "");
  }
}
