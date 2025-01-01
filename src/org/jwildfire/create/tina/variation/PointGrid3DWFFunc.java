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
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.random.MarsagliaRandomGenerator;

public class PointGrid3DWFFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_XMIN = "xmin";
  private static final String PARAM_XMAX = "xmax";
  private static final String PARAM_XCOUNT = "xcount";
  private static final String PARAM_YMIN = "ymin";
  private static final String PARAM_YMAX = "ymax";
  private static final String PARAM_YCOUNT = "ycount";
  private static final String PARAM_ZMIN = "zmin";
  private static final String PARAM_ZMAX = "zmax";
  private static final String PARAM_ZCOUNT = "zcount";
  private static final String PARAM_DISTORTION = "distortion";
  private static final String PARAM_SEED = "seed";

  private static final String[] paramNames = {PARAM_XMIN, PARAM_XMAX, PARAM_XCOUNT, PARAM_YMIN, PARAM_YMAX, PARAM_YCOUNT, PARAM_ZMIN, PARAM_ZMAX, PARAM_ZCOUNT, PARAM_DISTORTION, PARAM_SEED};

  private double xmin = -3.0;
  private double xmax = 3.0;
  private int xcount = 10;
  private double ymin = -3.0;
  private double ymax = 3.0;
  private int ycount = 10;
  private double zmin = -1.0;
  private double zmax = 1.0;
  private int zcount = 10;
  private double distortion = 2.3;
  private int seed = 1234;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // pointgrid3d_wf by Andreas Maschke
    int xIdx = pContext.random(xcount);
    int yIdx = pContext.random(ycount);
    int zIdx = pContext.random(zcount);
    double x = xmin + _dx * xIdx;
    double y = ymin + _dy * yIdx;
    double z = zmin + _dz * zIdx;
    if (distortion > 0) {
      long xseed = (seed + 1563) * xIdx + zIdx;
      _shapeRandGen.randomize(xseed);
      double distx = (0.5 - _shapeRandGen.random()) * distortion;
      long yseed = (seed + 6715) * yIdx + xIdx;
      _shapeRandGen.randomize(yseed);
      double disty = (0.5 - _shapeRandGen.random()) * distortion;
      long zseed = (seed + 4761) * zIdx + yIdx;
      _shapeRandGen.randomize(zseed);
      double distz = (0.5 - _shapeRandGen.random()) * distortion;
      x += distx;
      y += disty;
      z += distz;
    }
    pVarTP.x += x * pAmount;
    pVarTP.y += y * pAmount;
    pVarTP.z += z * pAmount;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{xmin, xmax, xcount, ymin, ymax, ycount, zmin, zmax, zcount, distortion, seed};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_XMIN.equalsIgnoreCase(pName))
      xmin = pValue;
    else if (PARAM_XMAX.equalsIgnoreCase(pName))
      xmax = pValue;
    else if (PARAM_XCOUNT.equalsIgnoreCase(pName))
      xcount = Tools.FTOI(pValue);
    else if (PARAM_YMIN.equalsIgnoreCase(pName))
      ymin = pValue;
    else if (PARAM_YMAX.equalsIgnoreCase(pName))
      ymax = pValue;
    else if (PARAM_YCOUNT.equalsIgnoreCase(pName))
      ycount = Tools.FTOI(pValue);
    else if (PARAM_ZMIN.equalsIgnoreCase(pName))
      zmin = pValue;
    else if (PARAM_ZMAX.equalsIgnoreCase(pName))
      zmax = pValue;
    else if (PARAM_ZCOUNT.equalsIgnoreCase(pName))
      zcount = Tools.FTOI(pValue);
    else if (PARAM_DISTORTION.equalsIgnoreCase(pName))
      distortion = pValue;
    else if (PARAM_SEED.equalsIgnoreCase(pName))
      seed = Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "pointgrid3d_wf";
  }

  private AbstractRandomGenerator _shapeRandGen;
  private double _dx, _dy, _dz;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _shapeRandGen = new MarsagliaRandomGenerator();
    _dx = (xmax - xmin) / (double) xcount;
    _dy = (ymax - ymin) / (double) ycount;
    _dz = (zmax - zmin) / (double) zcount;
  }

  @Override
  public void randomize() {
    xmin = Math.random() * 4.0 - 4.1;
    xmax = Math.random() * 4.0 + 0.1;
    xcount = (int) (Math.random() * 20 + 5);
    ymin = Math.random() * 4.0 - 4.1;
    ymax = Math.random() * 4.0 + 0.1;
    ycount = (int) (Math.random() * 20 + 5);
    zmin = Math.random() * 4.0 - 4.1;
    zmax = Math.random() * 4.0 + 0.1;
    zcount = (int) (Math.random() * 20 + 5);
    distortion = Math.random() * 5.0;
    seed = (int) (Math.random() * 1000000);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float _dx = (__pointgrid3d_wf_xmax - __pointgrid3d_wf_xmin) / (float) __pointgrid3d_wf_xcount;\n"
        + "float _dy = (__pointgrid3d_wf_ymax - __pointgrid3d_wf_ymin) / (float) __pointgrid3d_wf_ycount;\n"
        + "float _dz = (__pointgrid3d_wf_zmax - __pointgrid3d_wf_zmin) / (float) __pointgrid3d_wf_zcount;\n"
        + "int xIdx = (int)(RANDFLOAT()*__pointgrid3d_wf_xcount);\n"
        + "int yIdx = (int)(RANDFLOAT()*__pointgrid3d_wf_ycount);\n"
        + "int zIdx = (int)(RANDFLOAT()*__pointgrid3d_wf_zcount);\n"
        + "float x = __pointgrid3d_wf_xmin + _dx * xIdx;\n"
        + "float y = __pointgrid3d_wf_ymin + _dy * yIdx;\n"
        + "float z = __pointgrid3d_wf_zmin + _dz * zIdx;\n"
        + "if (__pointgrid3d_wf_distortion > 0) {\n"
        + "   long xseed = (__pointgrid3d_wf_seed + 1563) * xIdx + zIdx;\n"
        + "   pointgrid3d_randomize(xseed);\n"
        + "   float distx = (0.5 - pointgrid3d_random()) * __pointgrid3d_wf_distortion;\n"
        + "   long yseed = (__pointgrid3d_wf_seed + 6715) * yIdx + xIdx;\n"
        + "   pointgrid3d_randomize(yseed);\n"
        + "   float disty = (0.5 - pointgrid3d_random()) * __pointgrid3d_wf_distortion;\n"
        + "   long zseed = (__pointgrid3d_wf_seed + 4761) * zIdx + yIdx;\n"
        + "   pointgrid3d_randomize(zseed);\n"
        + "   float distz = (0.5 - pointgrid3d_random()) * __pointgrid3d_wf_distortion;\n"
        + "   x += distx;\n"
        + "   y += disty;\n"
        + "   z += distz;\n"
        + "}\n"
        + "__px += x * __pointgrid3d_wf;\n"
        + "__py += y * __pointgrid3d_wf;\n"
        + "__pz += z * __pointgrid3d_wf;\n";
  }

  @Override
  public String getGPUFunctions(FlameTransformationContext context) {
    return
            "__device__ int pointgrid3d_u = 12244355;\n"
                    +"__device__ int pointgrid3d_v = 34384;\n\n"
                    + "__device__ void pointgrid3d_randomize(long seed) {\n"
                    + "  pointgrid3d_u = (int) (seed << 16);\n"
                    + "  pointgrid3d_v = (int) (seed << 16) >> 16;"
                    + "}\n\n"
                    + "__device__ float pointgrid3d_random() {\n"
                    + "  pointgrid3d_v = 36969 * (pointgrid3d_v & 65535) + (pointgrid3d_v >> 16);\n"
                    + "  pointgrid3d_u = 18000 * (pointgrid3d_u & 65535) + (pointgrid3d_u >> 16);\n"
                    + "  int rnd = (pointgrid3d_v << 16) + pointgrid3d_u;\n"
                    + "  double res = (float) rnd / (float)0x7fffffff;\n"
                    + "  return res < 0 ? -res : res;"
                    + "}\n";
  }
}
