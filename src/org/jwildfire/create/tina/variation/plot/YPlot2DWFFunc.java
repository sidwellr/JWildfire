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

package org.jwildfire.create.tina.variation.plot;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.GfxMathLib;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.base.mathlib.VecMathLib.VectorD;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.render.gpu.farender.FARenderTools;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.variation.*;

import java.util.HashMap;
import java.util.Map;

import static org.jwildfire.base.mathlib.MathLib.*;

public class YPlot2DWFFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_PRESET_ID = "preset_id";
  private static final String PARAM_XMIN = "xmin";
  private static final String PARAM_XMAX = "xmax";
  private static final String PARAM_YMIN = "ymin";
  private static final String PARAM_YMAX = "ymax";
  private static final String PARAM_ZMIN = "zmin";
  private static final String PARAM_ZMAX = "zmax";
  private static final String PARAM_DIRECT_COLOR = "direct_color";
  private static final String PARAM_COLOR_MODE = "color_mode";
  private static final String PARAM_BLEND_COLORMAP = "blend_colormap";
  private static final String PARAM_DISPL_AMOUNT = "displ_amount";
  private static final String PARAM_BLEND_DISPLMAP = "blend_displ_map";

  private static final String PARAM_PARAM_A = "param_a";
  private static final String PARAM_PARAM_B = "param_b";
  private static final String PARAM_PARAM_C = "param_c";
  private static final String PARAM_PARAM_D = "param_d";
  private static final String PARAM_PARAM_E = "param_e";
  private static final String PARAM_PARAM_F = "param_f";

  private static final String RESSOURCE_FORMULA = "formula";
  private static final String RESSOURCE_COLORMAP_FILENAME = "colormap_filename";
  private static final String RESSOURCE_DISPL_MAP_FILENAME = "displ_map_filename";
  private static final String RESSOURCE_ID_REFERENCE = "preset_id_reference";

  private static final String[] paramNames = {
    PARAM_PRESET_ID,
    PARAM_XMIN,
    PARAM_XMAX,
    PARAM_YMIN,
    PARAM_YMAX,
    PARAM_ZMIN,
    PARAM_ZMAX,
    PARAM_DIRECT_COLOR,
    PARAM_COLOR_MODE,
    PARAM_BLEND_COLORMAP,
    PARAM_DISPL_AMOUNT,
    PARAM_BLEND_DISPLMAP,
    PARAM_PARAM_A,
    PARAM_PARAM_B,
    PARAM_PARAM_C,
    PARAM_PARAM_D,
    PARAM_PARAM_E,
    PARAM_PARAM_F
  };

  private static final String[] ressourceNames = {
    RESSOURCE_FORMULA, RESSOURCE_COLORMAP_FILENAME, RESSOURCE_DISPL_MAP_FILENAME, RESSOURCE_ID_REFERENCE
  };

  private int preset_id = -1;

  private double xmin = -3.0;
  private double xmax = 2.0;
  private double ymin = -4.0;
  private double ymax = 4.0;
  private double zmin = -2.0;
  private double zmax = 2.0;
  private int direct_color = 1;
  private int color_mode = CM_X;

  private double param_a = 0.0;
  private double param_b = 0.0;
  private double param_c = 0.0;
  private double param_d = 0.0;
  private double param_e = 0.0;
  private double param_f = 0.0;

  private static final int CM_COLORMAP = 0;
  private static final int CM_X = 1;
  private static final int CM_Y = 2;

  private String formula;

  private final ColorMapHolder colorMapHolder = new ColorMapHolder();
  private final DisplacementMapHolder displacementMapHolder = new DisplacementMapHolder();
  
  private String id_reference = "org.jwildfire.create.tina.variation.reference.ReferenceFile yplot2d-presets.pdf";

  private YPlot2DFormulaEvaluator evaluator;

  @Override
  public void transform(
      FlameTransformationContext pContext,
      XForm pXForm,
      XYZPoint pAffineTP,
      XYZPoint pVarTP,
      double pAmount) {
    if (evaluator == null) {
      return;
    }
    double randU = pContext.random();
    double randV = pContext.random();
    double x = _xmin + randU * _dx;
    double z = _zmin + randV * _dz;
    double y = evaluator.evaluate(x);

    if (displacementMapHolder.isActive()) {
      double eps = _dx / 100.0;
      double x1 = x + eps;
      double y1 = evaluator.evaluate(x1);

      VectorD av = new VectorD(eps, y1 - y, 0);
      VectorD bv = new VectorD(0.0, 0.0, 1.0);
      VectorD n = VectorD.cross(av, bv);
      n.normalize();
      double iu =
          GfxMathLib.clamp(
              randU * (displacementMapHolder.getDisplacementMapWidth() - 1.0),
              0.0,
              displacementMapHolder.getDisplacementMapWidth() - 1.0);
      double iv =
          GfxMathLib.clamp(
              displacementMapHolder.getDisplacementMapHeight()
                  - 1.0
                  - randV * (displacementMapHolder.getDisplacementMapHeight() - 1.0),
              0,
              displacementMapHolder.getDisplacementMapHeight() - 1.0);
      int ix = (int) MathLib.trunc(iu);
      int iy = (int) MathLib.trunc(iv);
      double d = displacementMapHolder.calculateImageDisplacement(ix, iy, iu, iv) * _displ_amount;
      pVarTP.x += pAmount * n.x * d;
      pVarTP.y += pAmount * n.y * d;
      pVarTP.z += pAmount * n.z * d;
    }

    if (direct_color > 0) {
      switch (color_mode) {
        case CM_COLORMAP:
          if (colorMapHolder.isActive()) {
            double iu =
                GfxMathLib.clamp(
                    randU * (colorMapHolder.getColorMapWidth() - 1.0),
                    0.0,
                    colorMapHolder.getColorMapWidth() - 1.0);
            double iv =
                GfxMathLib.clamp(
                    colorMapHolder.getColorMapHeight()
                        - 1.0
                        - randV * (colorMapHolder.getColorMapHeight() - 1.0),
                    0,
                    colorMapHolder.getColorMapHeight() - 1.0);
            int ix = (int) MathLib.trunc(iu);
            int iy = (int) MathLib.trunc(iv);
            colorMapHolder.applyImageColor(pVarTP, ix, iy, iu, iv);
            pVarTP.color =
                getUVColorIdx(
                    Tools.FTOI(pVarTP.redColor),
                    Tools.FTOI(pVarTP.greenColor),
                    Tools.FTOI(pVarTP.blueColor));
          }
          break;
        case CM_Y:
          pVarTP.color = (y - _ymin) / _dy;
          break;
        default:
        case CM_X:
          pVarTP.color = (x - _xmin) / _dx;
          break;
      }
      if (pVarTP.color < 0.0) pVarTP.color = 0.0;
      else if (pVarTP.color > 1.0) pVarTP.color = 1.0;
    }
    pVarTP.x += pAmount * x;
    pVarTP.y += pAmount * y;
    pVarTP.z += pAmount * z;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] {
      preset_id,
      xmin,
      xmax,
      ymin,
      ymax,
      zmin,
      zmax,
      direct_color,
      color_mode,
      colorMapHolder.getBlend_colormap(),
      displacementMapHolder.getDispl_amount(),
      displacementMapHolder.getBlend_displ_map(),
      param_a,
      param_b,
      param_c,
      param_d,
      param_e,
      param_f
    };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_PRESET_ID.equalsIgnoreCase(pName)) {
      preset_id = Tools.FTOI(pValue);
      if (preset_id >= 0) {
        refreshFormulaFromPreset(preset_id);
      }
    } else if (PARAM_XMIN.equalsIgnoreCase(pName)) {
      xmin = pValue;
      validatePresetId();
    } else if (PARAM_XMAX.equalsIgnoreCase(pName)) {
      xmax = pValue;
      validatePresetId();
    } else if (PARAM_YMIN.equalsIgnoreCase(pName)) {
      ymin = pValue;
    } else if (PARAM_YMAX.equalsIgnoreCase(pName)) {
      ymax = pValue;
    } else if (PARAM_ZMIN.equalsIgnoreCase(pName)) {
      zmin = pValue;
    } else if (PARAM_ZMAX.equalsIgnoreCase(pName)) {
      zmax = pValue;
    } else if (PARAM_DIRECT_COLOR.equalsIgnoreCase(pName)) {
      direct_color = Tools.FTOI(pValue);
    } else if (PARAM_COLOR_MODE.equalsIgnoreCase(pName)) {
      color_mode = Tools.FTOI(pValue);
    } else if (PARAM_BLEND_COLORMAP.equalsIgnoreCase(pName)) {
      colorMapHolder.setBlend_colormap(limitIntVal(Tools.FTOI(pValue), 0, 1));
    } else if (PARAM_DISPL_AMOUNT.equalsIgnoreCase(pName)) {
      displacementMapHolder.setDispl_amount(pValue);
    } else if (PARAM_BLEND_DISPLMAP.equalsIgnoreCase(pName)) {
      displacementMapHolder.setBlend_displ_map(limitIntVal(Tools.FTOI(pValue), 0, 1));
    } else if (PARAM_PARAM_A.equalsIgnoreCase(pName)) {
      param_a = pValue;
    } else if (PARAM_PARAM_B.equalsIgnoreCase(pName)) {
      param_b = pValue;
    } else if (PARAM_PARAM_C.equalsIgnoreCase(pName)) {
      param_c = pValue;
    } else if (PARAM_PARAM_D.equalsIgnoreCase(pName)) {
      param_d = pValue;
    } else if (PARAM_PARAM_E.equalsIgnoreCase(pName)) {
      param_e = pValue;
    } else if (PARAM_PARAM_F.equalsIgnoreCase(pName)) {
      param_f = pValue;
    } else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "yplot2d_wf";
  }

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][] {
      (formula != null ? formula.getBytes() : null),
      (colorMapHolder.getColormap_filename() != null
          ? colorMapHolder.getColormap_filename().getBytes()
          : null),
      (displacementMapHolder.getDispl_map_filename() != null
          ? displacementMapHolder.getDispl_map_filename().getBytes()
          : null),
      id_reference.getBytes()
    };
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_FORMULA.equalsIgnoreCase(pName)) {
      formula = pValue != null ? new String(pValue) : "";
      validatePresetId();
    } else if (RESSOURCE_COLORMAP_FILENAME.equalsIgnoreCase(pName)) {
      colorMapHolder.setColormap_filename(pValue != null ? new String(pValue) : "");
      colorMapHolder.clear();
      uvIdxMap.clear();
    } else if (RESSOURCE_DISPL_MAP_FILENAME.equalsIgnoreCase(pName)) {
      displacementMapHolder.setDispl_map_filename(pValue != null ? new String(pValue) : "");
      displacementMapHolder.clear();
    } else if (RESSOURCE_ID_REFERENCE.equalsIgnoreCase(pName)) {
    	// ignore read-only parameter
    } else throw new IllegalArgumentException(pName);
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_FORMULA.equalsIgnoreCase(pName)) {
      return RessourceType.BYTEARRAY;
    } else if (RESSOURCE_COLORMAP_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    } else if (RESSOURCE_DISPL_MAP_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    } else if (RESSOURCE_ID_REFERENCE.equalsIgnoreCase(pName)) { 
    	return RessourceType.REFERENCE;
    } else throw new IllegalArgumentException(pName);
  }

  private double _xmin, _xmax, _dx;
  private double _ymin, _ymax, _dy;
  private double _zmin, _zmax, _dz;
  private double _displ_amount;

  @Override
  public void init(
      FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    colorMapHolder.init();
    uvColors =
        pLayer
            .getPalette()
            .createRenderPalette(pContext.getFlameRenderer().getFlame().getWhiteLevel());
    displacementMapHolder.init();
    _displ_amount = displacementMapHolder.getDispl_amount();

    _xmin = xmin;
    _xmax = xmax;
    if (_xmin > _xmax) {
      double t = _xmin;
      _xmin = _xmax;
      _xmax = t;
    }
    _dx = _xmax - _xmin;

    _ymin = ymin;
    _ymax = ymax;
    if (_ymin > _ymax) {
      double t = _ymin;
      _ymin = _ymax;
      _ymax = t;
    }
    _dy = _ymax - _ymin;

    _zmin = zmin;
    _zmax = zmax;
    if (_zmin > _zmax) {
      double t = _zmin;
      _zmin = _zmax;
      _zmax = t;
    }
    _dz = _zmax - _zmin;

    evaluator = null;
    if (!formula.isEmpty()) {
      String code =
          "import static org.jwildfire.base.mathlib.MathLib.*;\r\n"
              + "\r\n"
              + "  public double evaluate(double x) {\r\n"
              + "    double pi = M_PI;\r\n"
              + "    double param_a = "
              + param_a
              + ";\r\n"
              + "    double param_b = "
              + param_b
              + ";\r\n"
              + "    double param_c = "
              + param_c
              + ";\r\n"
              + "    double param_d = "
              + param_d
              + ";\r\n"
              + "    double param_e = "
              + param_e
              + ";\r\n"
              + "    double param_f = "
              + param_f
              + ";\r\n"
              + "    return "
              + formula
              + ";\r\n"
              + "  }\r\n";

      try {
        evaluator = YPlot2DFormulaEvaluator.compile(code);
      } catch (Exception e) {
        evaluator = null;
        e.printStackTrace();
        System.out.println(code);
        throw new IllegalArgumentException(e);
      }
    }
  }

  public YPlot2DWFFunc() {
    super();
    preset_id = WFFuncPresetsStore.getYPlot2DWFFuncPresets().getRandomPresetId();
    refreshFormulaFromPreset(preset_id);
  }

  private void validatePresetId() {
    if (preset_id >= 0) {
      YPlot2DWFFuncPreset preset =
          WFFuncPresetsStore.getYPlot2DWFFuncPresets().getPreset(preset_id);
      if (!preset.getFormula().equals(formula)
          || (fabs(xmin - preset.getXmin()) > EPSILON)
          || (fabs(xmax - preset.getXmax()) > EPSILON)) {
        preset_id = -1;
      }
    }
  }

  private void refreshFormulaFromPreset(int presetId) {
    YPlot2DWFFuncPreset preset = WFFuncPresetsStore.getYPlot2DWFFuncPresets().getPreset(presetId);
    formula = preset.getFormula();
    xmin = preset.getXmin();
    xmax = preset.getXmax();

    param_a = preset.getParam_a();
    param_b = preset.getParam_b();
    param_c = preset.getParam_c();
    param_d = preset.getParam_d();
    param_e = preset.getParam_e();
    param_f = preset.getParam_f();
  }

  private RenderColor[] uvColors;
  protected Map<RenderColor, Double> uvIdxMap = new HashMap<RenderColor, Double>();

  private double getUVColorIdx(int pR, int pG, int pB) {
    RenderColor pColor = new RenderColor(pR, pG, pB);
    Double res = uvIdxMap.get(pColor);
    if (res == null) {

      int nearestIdx = 0;
      RenderColor color = uvColors[0];
      double dr, dg, db;
      dr = (color.red - pR);
      dg = (color.green - pG);
      db = (color.blue - pB);
      double nearestDist = sqrt(dr * dr + dg * dg + db * db);
      for (int i = 1; i < uvColors.length; i++) {
        color = uvColors[i];
        dr = (color.red - pR);
        dg = (color.green - pG);
        db = (color.blue - pB);
        double dist = sqrt(dr * dr + dg * dg + db * db);
        if (dist < nearestDist) {
          nearestDist = dist;
          nearestIdx = i;
        }
      }
      res = (double) nearestIdx / (double) (uvColors.length - 1);
      uvIdxMap.put(pColor, res);
    }
    return res;
  }

  @Override
  public void randomize() {
    if (Math.random() < 0.5) {
      preset_id = WFFuncPresetsStore.getYPlot2DWFFuncPresets().getRandomPresetId();
      refreshFormulaFromPreset(preset_id);
    } else {
      preset_id = -1;
      xmin = Math.random() * 3.0 - 4.0;
      xmax = Math.random() * 3.0 + 1.0;
    }
    ymin = Math.random() * 3.0 - 4.0;
    ymax = Math.random() * 3.0 + 1.0;
    zmax = Math.random() * 2.5;
    zmin = -zmax;
    param_a = Math.random() * 5.0 - 2.5;
    param_b = Math.random() * 5.0 - 2.5;
    param_c = Math.random() * 5.0 - 2.5;
    param_d = Math.random() * 5.0 - 2.5;
    param_e = Math.random() * 5.0 - 2.5;
    param_f = Math.random() * 5.0 - 2.5;
    direct_color = (int) (Math.random() * 2);
    if (colorMapHolder.getColormap_filename() == null) 
      color_mode = (int) (Math.random() * 2 + 1);
    else {
      color_mode = (int) (Math.random() * 3);
      colorMapHolder.setBlend_colormap((int) (Math.random() * 2));
    }
    if (displacementMapHolder.getDispl_map_filename() != null) {
      displacementMapHolder.setDispl_amount(Math.random());
      displacementMapHolder.setBlend_displ_map((int) (Math.random() * 2));
    }
  }
  
  @Override
  public boolean dynamicParameterExpansion() {
    return true;
  }

  @Override
  public boolean dynamicParameterExpansion(String pName) {
    // preset_id doesn't really expand parameters, but it changes them; this will make them refresh
    return PARAM_PRESET_ID.equalsIgnoreCase(pName);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[] {
      VariationFuncType.VARTYPE_BASE_SHAPE,
      VariationFuncType.VARTYPE_DC,
      VariationFuncType.VARTYPE_EDIT_FORMULA,
      VariationFuncType.VARTYPE_SUPPORTS_GPU,
      VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN
    };
  }

  @Override
  public boolean isStateful() {
    return true;
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float _xmin, _xmax, _dx;\n"
        + "float _ymin, _ymax, _dy;\n"
        + "float _zmin, _zmax, _dz;\n"
        + "_xmin = __yplot2d_wf_xmin;\n"
        + "    _xmax = __yplot2d_wf_xmax;\n"
        + "    if (_xmin > _xmax) {\n"
        + "      float t = _xmin;\n"
        + "      _xmin = _xmax;\n"
        + "      _xmax = t;\n"
        + "    }\n"
        + "    _dx = _xmax - _xmin;\n"
        + "\n"
        + "    _ymin = __yplot2d_wf_ymin;\n"
        + "    _ymax = __yplot2d_wf_ymax;\n"
        + "    if (_ymin > _ymax) {\n"
        + "      float t = _ymin;\n"
        + "      _ymin = _ymax;\n"
        + "      _ymax = t;\n"
        + "    }\n"
        + "    _dy = _ymax - _ymin;\n"
        + "\n"
        + "    _zmin = __yplot2d_wf_zmin;\n"
        + "    _zmax = __yplot2d_wf_zmax;\n"
        + "    if (_zmin > _zmax) {\n"
        + "      float t = _zmin;\n"
        + "      _zmin = _zmax;\n"
        + "      _zmax = t;\n"
        + "    }\n"
        + "    _dz = _zmax - _zmin;\n"
        + "\n"
        + "float randU = RANDFLOAT();\n"
        + "float randV = RANDFLOAT();\n"
        + "float x = _xmin + randU * _dx;\n"
        + "float z = _zmin + randV * _dz;\n"
        + "float y = eval%d_yplot2d_wf(x, __yplot2d_wf_param_a, __yplot2d_wf_param_b, __yplot2d_wf_param_c, __yplot2d_wf_param_d, __yplot2d_wf_param_e, __yplot2d_wf_param_f);\n"
        + "if(lroundf(__yplot2d_wf_direct_color)>0) {\n"
        + "  switch (lroundf(__yplot2d_wf_color_mode)) {\n"
        + "        case 0:\n"
        + "          break;\n"
        + "        case 2:\n"
        + "          __pal = (y - _ymin) / _dy;\n"
        + "          break;\n"
        + "        default:\n"
        + "        case 1:\n"
        + "          __pal = (x - _xmin) / _dx;\n"
        + "          break;\n"
        + "      }\n"
        + "      if (__pal < 0.0) __pal = 0.0;\n"
        + "      else if (__pal > 1.0) __pal = 1.0;\n"
        + "}\n"
        + "__px += __yplot2d_wf * x;\n"
        + "__py += __yplot2d_wf * y;\n"
        + "__pz += __yplot2d_wf * z;\n";
  }

  @Override
  public String getGPUFunctions(FlameTransformationContext context) {
    return "__device__ float eval%d_yplot2d_wf(float x, float param_a,float param_b, float param_c, float param_d, float param_e, float param_f) {\n"
           +"  float pi = PI;\n"
           +"  return "+ FARenderTools.rewriteJavaFormulaForCUDA(formula) +";\n"
           +"}\n";
  }
}
