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

public class Hypertile3DbFunc extends VariationFunc implements SupportsGPU {
	private static final long serialVersionUID = 1L;

	private static final String PARAM_P = "p";
	private static final String PARAM_Q = "q";
	private static final String PARAM_N = "n";

	private static final String PARAM_B = "b";
	private static final String PARAM_C = "c";
	private static final String PARAM_D = "d";
	private static final String PARAM_E = "e";
	private static final String PARAM_F = "f";
	private static final String PARAM_G = "g";
	private static final String PARAM_H = "h";
	private static final String PARAM_I = "i";
	private static final String PARAM_J = "j";
	private static final String PARAM_K = "k";

	private static final String[] paramNames = { PARAM_P, PARAM_Q, PARAM_N, PARAM_B, PARAM_C, PARAM_D, PARAM_E, PARAM_F,
			PARAM_G, PARAM_H, PARAM_I, PARAM_J, PARAM_K };

	private int p = 3;
	private int q = 7;
	private int n = 0;

	private double b = 2.0;
	private double c = 2.0;
	private double d = 1.0;
	private double e = 1.0;
	private double f = 1.0;
	private double g = 2.0;
	private double h = 2.0;
	private double i = 1.0;
	private double j = 1.0;
	private double k = 1.0;

	@Override
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
			double pAmount) {
		/*
		 * hypertile3D by Zueuk,
		 * http://zueuk.deviantart.com/art/3D-Hyperbolic-tiling-plugins-169047926
		 * Variables added by Brad Stefanov
		 */
		double r2 = sqr(pAffineTP.x) + sqr(pAffineTP.y) + sqr(pAffineTP.z);

		double x2cx = c2x * pAffineTP.x, y2cy = c2y * pAffineTP.y;

		double d = pAmount / (c2 * r2 + x2cx - y2cy + 1);

		pVarTP.x += d * (pAffineTP.x * s2x - cx * (y2cy - r2 - 1));
		pVarTP.y += d * (pAffineTP.y * s2y + cy * (-x2cx - r2 - 1));
		pVarTP.z += d * (pAffineTP.z * s2z);
	}

	@Override
	public String[] getParameterNames() {
		return paramNames;
	}

	@Override
	public Object[] getParameterValues() {
		return new Object[] { p, q, n, b, c, d, e, f, g, h, i, j, k };
	}

	@Override
	public void setParameter(String pName, double pValue) {
		if (PARAM_P.equalsIgnoreCase(pName))
			p = limitIntVal(Tools.FTOI(pValue), 3, Integer.MAX_VALUE);
		else if (PARAM_Q.equalsIgnoreCase(pName))
			q = limitIntVal(Tools.FTOI(pValue), 3, Integer.MAX_VALUE);
		else if (PARAM_N.equalsIgnoreCase(pName))
			n = limitIntVal(Tools.FTOI(pValue), 0, Integer.MAX_VALUE);
		else if (PARAM_B.equalsIgnoreCase(pName)) {
			b = pValue;
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
		} else if (PARAM_I.equalsIgnoreCase(pName)) {
			i = pValue;
		} else if (PARAM_J.equalsIgnoreCase(pName)) {
			j = pValue;
		} else if (PARAM_K.equalsIgnoreCase(pName)) {
			k = pValue;
		} else
			throw new IllegalArgumentException(pName);
	}

	@Override
	public String getName() {
		return "hypertile3Db";
	}

	private double c2x, c2y, c2, s2x, s2y, s2z, cx, cy;

	@Override
	public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		double pa = b * M_PI / p, qa = c * M_PI / q;

		double r = -(cos(pa) - d) / (cos(pa) + cos(qa));
		if (r > 0)
			r = e / sqrt(f + r);
		else
			r = 1;

		double na = n * pa;

		cx = r * cos(na);
		cy = r * sin(na);

		c2 = sqr(cx) + sqr(cy);

		c2x = g * cx;
		c2y = h * cy;

		s2x = i + sqr(cx) - sqr(cy);
		s2y = j + sqr(cy) - sqr(cx);
		s2z = k - sqr(cy) - sqr(cx);
	}

    @Override
    public void randomize() {
      p = (int) (Math.random() * 8 + 3);
      q = (int) (Math.random() * 8 + 3);
      n = (int) (Math.random() * p);
      b = Math.random() * 2.5;
      c = Math.random() * 2.5;
      d = Math.random() * 2.5;
      e = Math.random() * 2.5;
      f = Math.random() * 2.5;
      g = Math.random() * 2.5;
      h = Math.random() * 2.5;
      i = Math.random() * 2.5;
      j = Math.random() * 2.5;
      k = Math.random() * 2.5;
    }

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}

	@Override
	public String getGPUCode(FlameTransformationContext context) {
    return "float c2x, c2y, c2, s2x, s2y, s2z, cx, cy;\n"
        + "float pa = __hypertile3Db_b * PI / lroundf(__hypertile3Db_p), qa = __hypertile3Db_c * PI / lroundf(__hypertile3Db_q);\n"
        + "\n"
        + "  float r = -(cosf(pa) - __hypertile3Db_d) / (cosf(pa) + cosf(qa));\n"
        + "  if (r > 0)\n"
        + "   r = __hypertile3Db_e / sqrtf(__hypertile3Db_f + r);\n"
        + "  else\n"
        + "   r = 1.f;\n"
        + "\n"
        + "  float na = lroundf(__hypertile3Db_n) * pa;\n"
        + "\n"
        + "  cx = r * cosf(na);\n"
        + "  cy = r * sinf(na);\n"
        + "\n"
        + "  c2 = cx*cx + cy*cy;\n"
        + "\n"
        + "  c2x = __hypertile3Db_g * cx;\n"
        + "  c2y = __hypertile3Db_h * cy;\n"
        + "\n"
        + "  s2x = __hypertile3Db_i + cx*cx - cy*cy;\n"
        + "  s2y = __hypertile3Db_j + cy*cy - cx*cx;\n"
        + "  s2z = __hypertile3Db_k - cy*cy - cx*cx;\n"
        + "  float r2 = __x*__x + __y*__y + __z*__z;\n"
        + "\n"
        + "  float x2cx = c2x * __x, y2cy = c2y * __y;\n"
        + "\n"
        + "  float d = __hypertile3Db / (c2 * r2 + x2cx - y2cy + 1.f);\n"
        + "\n"
        + "  __px += d * (__x * s2x - cx * (y2cy - r2 - 1.f));\n"
        + "  __py += d * (__y * s2y + cy * (-x2cx - r2 - 1.f));\n"
        + "  __pz += d * (__z * s2z);\n";
	}
}
