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
import org.jwildfire.create.tina.farender.FARenderTools;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.variation.*;

import java.util.HashMap;
import java.util.Map;

import static org.jwildfire.base.mathlib.MathLib.*;

public class PolarPlot3DWFFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_PRESET_ID = "preset_id";
  private static final String PARAM_TMIN = "tmin";
  private static final String PARAM_TMAX = "tmax";
  private static final String PARAM_UMIN = "umin";
  private static final String PARAM_UMAX = "umax";
  private static final String PARAM_RMIN = "rmin";
  private static final String PARAM_RMAX = "rmax";
  private static final String PARAM_CYLINDRICAL = "cylindrical";
  private static final String PARAM_DIRECT_COLOR = "direct_color";
  private static final String PARAM_COLOR_MODE = "color_mode";
  private static final String PARAM_BLEND_COLORMAP = "blend_colormap";
  private static final String PARAM_DISPL_AMOUNT = "displ_amount";
  private static final String PARAM_BLEND_DISPLMAP = "blend_displ_map";

  private static final String RESSOURCE_FORMULA = "formula";
  private static final String RESSOURCE_COLORMAP_FILENAME = "colormap_filename";
  private static final String RESSOURCE_DISPL_MAP_FILENAME = "displ_map_filename";

  private static final String PARAM_PARAM_A = "param_a";
  private static final String PARAM_PARAM_B = "param_b";
  private static final String PARAM_PARAM_C = "param_c";
  private static final String PARAM_PARAM_D = "param_d";
  private static final String PARAM_PARAM_E = "param_e";
  private static final String PARAM_PARAM_F = "param_f";

  private static final String[] paramNames = {PARAM_PRESET_ID, PARAM_TMIN, PARAM_TMAX, PARAM_UMIN, PARAM_UMAX, PARAM_RMIN, PARAM_RMAX, PARAM_CYLINDRICAL, PARAM_DIRECT_COLOR, PARAM_COLOR_MODE, PARAM_BLEND_COLORMAP, PARAM_DISPL_AMOUNT, PARAM_BLEND_DISPLMAP, PARAM_PARAM_A, PARAM_PARAM_B, PARAM_PARAM_C, PARAM_PARAM_D, PARAM_PARAM_E, PARAM_PARAM_F};

  private static final String[] ressourceNames = {RESSOURCE_FORMULA, RESSOURCE_COLORMAP_FILENAME, RESSOURCE_DISPL_MAP_FILENAME};

  private int preset_id = -1;

  private double tmin = -M_PI;
  private double tmax = M_PI;
  private double umin = 0.0;
  private double umax = M_PI;
  private double rmin = -2.0;
  private double rmax = 2.0;
  private int cylindrical = 0;
  private int direct_color = 1;
  private int color_mode = CM_U;

  private double param_a = 0.0;
  private double param_b = 0.0;
  private double param_c = 0.0;
  private double param_d = 0.0;
  private double param_e = 0.0;
  private double param_f = 0.0;

  private static final int CM_COLORMAP = 0;
  private static final int CM_T = 1;
  private static final int CM_U = 2;
  private static final int CM_R = 3;
  private static final int CM_TU = 4;

  private String formula;

  private ColorMapHolder colorMapHolder = new ColorMapHolder();
  private DisplacementMapHolder displacementMapHolder = new DisplacementMapHolder();

  private PolarPlot3DFormulaEvaluator evaluator;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    if (evaluator == null) {
      return;
    }
    double randT = pContext.random();
    double randU = pContext.random();
    double t = _tmin + randT * _dt;
    double u = _umin + randU * _du;
    double r = evaluator.evaluate(t, u);
    double x, y, z;
    
    if (cylindrical == 0) {
      x = r * sin(u) * cos(t);
      y = r * sin(u) * sin(t);
      z = r * cos(u);
    }
    else {
      x = r * cos(t);
      y = r * sin(t);
      z = u;
    }

    if (displacementMapHolder.isActive()) {
      double epst = _dt / 100.0;
      double t1 = t + epst;
      double r1 = evaluator.evaluate(t1, u);
      double y1 = (cylindrical == 0) ? r1 * sin(u) * sin(t1) : r1 * sin(t1);

      double epsu = _du / 100.0;
      double u2 = u + epsu;
      double r2 = evaluator.evaluate(t, u2);
      double y2 = (cylindrical == 0) ? r2 * sin(u2) * sin(t) : r2 * sin(t);
     
      
      VectorD av = new VectorD(epst, y1 - y, 0);
      VectorD bv = new VectorD(0.0, y2 - y, epsu);
      VectorD n = VectorD.cross(av, bv);
      n.normalize();
      double iu = GfxMathLib.clamp(randT * (displacementMapHolder.getDisplacementMapWidth() - 1.0), 0.0, displacementMapHolder.getDisplacementMapWidth() - 1.0);
      double iv = GfxMathLib.clamp(displacementMapHolder.getDisplacementMapHeight() - 1.0 - randU * (displacementMapHolder.getDisplacementMapHeight() - 1.0), 0, displacementMapHolder.getDisplacementMapHeight() - 1.0);
      int ix = (int) MathLib.trunc(iu);
      int iy = (int) MathLib.trunc(iv);
      double d = displacementMapHolder.calculateImageDisplacement(ix, iy, iu, iv) * _displ_amount;
      pVarTP.x += pAmount * n.x * d;
      pVarTP.y += pAmount * n.y * d;
      pVarTP.z += pAmount * n.z * d;
    }

    if (direct_color > 0) {
      switch (color_mode) {
        case CM_T:
          pVarTP.color = (t - _tmin) / _dt;
          break;
        case CM_U:
          pVarTP.color = (u - _umin) / _du;
          break;
        case CM_R:
          pVarTP.color = (r - _rmin) / _dr;
          break;
        case CM_COLORMAP:
          if (colorMapHolder.isActive()) {
            double iu = GfxMathLib.clamp(randT * (colorMapHolder.getColorMapWidth() - 1.0), 0.0, colorMapHolder.getColorMapWidth() - 1.0);
            double iv = GfxMathLib.clamp(colorMapHolder.getColorMapHeight() - 1.0 - randU * (colorMapHolder.getColorMapHeight() - 1.0), 0, colorMapHolder.getColorMapHeight() - 1.0);
            int ix = (int) MathLib.trunc(iu);
            int iy = (int) MathLib.trunc(iv);
            colorMapHolder.applyImageColor(pVarTP, ix, iy, iu, iv);
            pVarTP.color = getUVColorIdx(Tools.FTOI(pVarTP.redColor), Tools.FTOI(pVarTP.greenColor), Tools.FTOI(pVarTP.blueColor));
          }
          break;
        case CM_TU:
          pVarTP.color = (t - _tmin) / _dt * (u - _umin) / _du;
          break;
        default:
      }
      if (pVarTP.color < 0.0)
        pVarTP.color = 0.0;
      else if (pVarTP.color > 1.0)
        pVarTP.color = 1.0;
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
    return new Object[]{preset_id, tmin, tmax, umin, umax, rmin, rmax, cylindrical, direct_color, color_mode, colorMapHolder.getBlend_colormap(), displacementMapHolder.getDispl_amount(), displacementMapHolder.getBlend_displ_map(), param_a, param_b, param_c, param_d, param_e, param_f};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_PRESET_ID.equalsIgnoreCase(pName)) {
      preset_id = Tools.FTOI(pValue);
      if (preset_id >= 0) {
        refreshFormulaFromPreset(preset_id);
      }
    } else if (PARAM_TMIN.equalsIgnoreCase(pName)) {
      tmin = pValue;
      validatePresetId();
    } else if (PARAM_TMAX.equalsIgnoreCase(pName)) {
      tmax = pValue;
      validatePresetId();
    } else if (PARAM_UMIN.equalsIgnoreCase(pName)) {
      umin = pValue;
      validatePresetId();
    } else if (PARAM_UMAX.equalsIgnoreCase(pName)) {
      umax = pValue;
      validatePresetId();
    } else if (PARAM_RMIN.equalsIgnoreCase(pName)) {
      rmin = pValue;
      validatePresetId();
    } else if (PARAM_RMAX.equalsIgnoreCase(pName)) {
      rmax = pValue;
      validatePresetId();
    } else if (PARAM_CYLINDRICAL.equalsIgnoreCase(pName)) {
      cylindrical = Tools.FTOI(pValue);
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
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "polarplot3d_wf";
  }

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][]{(formula != null ? formula.getBytes() : null), (colorMapHolder.getColormap_filename() != null ? colorMapHolder.getColormap_filename().getBytes() : null), (displacementMapHolder.getDispl_map_filename() != null ? displacementMapHolder.getDispl_map_filename().getBytes() : null)};
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
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_FORMULA.equalsIgnoreCase(pName)) {
      return RessourceType.BYTEARRAY;
    } else if (RESSOURCE_COLORMAP_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    } else if (RESSOURCE_DISPL_MAP_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    } else
      throw new IllegalArgumentException(pName);
  }

  private double _tmin, _tmax, _dt;
  private double _umin, _umax, _du;
  private double _rmin, _rmax, _dr;
  private double _displ_amount;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    colorMapHolder.init();
    uvColors = pLayer.getPalette().createRenderPalette(pContext.getFlameRenderer().getFlame().getWhiteLevel());
    displacementMapHolder.init();
    _displ_amount = displacementMapHolder.getDispl_amount();

    _tmin = tmin;
    _tmax = tmax;
    if (_tmin > _tmax) {
      double t = _tmin;
      _tmin = _tmax;
      _tmax = t;
    }
    _dt = _tmax - _tmin;

    _umin = umin;
    _umax = umax;
    if (_umin > _umax) {
      double t = _umin;
      _umin = _umax;
      _umax = t;
    }
    _du = _umax - _umin;

    _rmin = rmin;
    _rmax = rmax;
    if (_rmin > _rmax) {
      double t = _rmin;
      _rmin = _rmax;
      _rmax = t;
    }
    _dr = _rmax - _rmin;

    evaluator = null;
    if (!formula.isEmpty()) {
      String code = "import static org.jwildfire.base.mathlib.MathLib.*;\r\n" +
              "\r\n" +
              "  public double evaluate(double t, double u) {\r\n" +
              "    double pi = M_PI;\r\n" +
              "    double param_a = " + param_a + ";\r\n" +
              "    double param_b = " + param_b + ";\r\n" +
              "    double param_c = " + param_c + ";\r\n" +
              "    double param_d = " + param_d + ";\r\n" +
              "    double param_e = " + param_e + ";\r\n" +
              "    double param_f = " + param_f + ";\r\n" +
              "    return " + formula + ";\r\n" +
              "  }\r\n";

      try {
        evaluator = PolarPlot3DFormulaEvaluator.compile(code);
      } catch (Exception e) {
        evaluator = null;
        e.printStackTrace();
        System.out.println(code);
        throw new IllegalArgumentException(e);
      }
    }

  }

  public PolarPlot3DWFFunc() {
    super();
    preset_id = WFFuncPresetsStore.getPolarPlot3DWFFuncPresets().getRandomPresetId();
    refreshFormulaFromPreset(preset_id);
  }

  private void validatePresetId() {
    if (preset_id >= 0) {
      PolarPlot3DWFFuncPreset preset = WFFuncPresetsStore.getPolarPlot3DWFFuncPresets().getPreset(preset_id);
      if (!preset.getFormula().equals(formula) ||
              (fabs(tmin - preset.getTmin()) > EPSILON) || (fabs(tmax - preset.getTmax()) > EPSILON) ||
              (fabs(umin - preset.getUmin()) > EPSILON) || (fabs(umax - preset.getUmax()) > EPSILON) ||
              (cylindrical != preset.getCylindrical())) {
        preset_id = -1;
      }
    }
  }

  private void refreshFormulaFromPreset(int presetId) {
    PolarPlot3DWFFuncPreset preset = WFFuncPresetsStore.getPolarPlot3DWFFuncPresets().getPreset(presetId);
    formula = preset.getFormula();
    tmin = preset.getTmin();
    tmax = preset.getTmax();
    umin = preset.getUmin();
    umax = preset.getUmax();
    cylindrical = preset.getCylindrical();

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
      preset_id = WFFuncPresetsStore.getPolarPlot3DWFFuncPresets().getRandomPresetId();
      refreshFormulaFromPreset(preset_id);
    } else {
      preset_id = -1;
      tmin = Math.random() * 3.0 - 4.0;
      tmax = Math.random() * 3.0 + 1.0;
      umin = Math.random() * 3.0 - 4.0;
      umax = Math.random() * 3.0 + 1.0;
    }
    rmin = Math.random() * 6.0 - 6.0;
    rmax = Math.random() * 6.0;
    param_a = Math.random() * 5.0 - 2.5;
    param_b = Math.random() * 5.0 - 2.5;
    param_c = Math.random() * 5.0 - 2.5;
    param_d = Math.random() * 5.0 - 2.5;
    param_e = Math.random() * 5.0 - 2.5;
    param_f = Math.random() * 5.0 - 2.5;
    cylindrical = (int) (Math.random() * 2);
    direct_color = (int) (Math.random() * 2);
    if (colorMapHolder.getColormap_filename() == null) 
      color_mode = (int) (Math.random() * 4 + 1);
    else {
      color_mode = (int) (Math.random() * 5);
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
    if (PARAM_PRESET_ID.equalsIgnoreCase(pName)) return true;
    else return false;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_EDIT_FORMULA, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public boolean isStateful() {
    return true;
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float _tmin, _tmax, _dt;\n"
        + "float _umin, _umax, _du;\n"
        + "float _rmin, _rmax, _dr;\n"
        + "_tmin = __polarplot3d_wf_tmin;\n"
        + "    _tmax = __polarplot3d_wf_tmax;\n"
        + "    if (_tmin > _tmax) {\n"
        + "      float t = _tmin;\n"
        + "      _tmin = _tmax;\n"
        + "      _tmax = t;\n"
        + "    }\n"
        + "    _dt = _tmax - _tmin;\n"
        + "\n"
        + "    _umin = __polarplot3d_wf_umin;\n"
        + "    _umax = __polarplot3d_wf_umax;\n"
        + "    if (_umin > _umax) {\n"
        + "      float t = _umin;\n"
        + "      _umin = _umax;\n"
        + "      _umax = t;\n"
        + "    }\n"
        + "    _du = _umax - _umin;\n"
        + "\n"
        + "    _rmin = __polarplot3d_wf_rmin;\n"
        + "    _rmax = __polarplot3d_wf_rmax;\n"
        + "    if (_rmin > _rmax) {\n"
        + "      float t = _rmin;\n"
        + "      _rmin = _rmax;\n"
        + "      _rmax = t;\n"
        + "    }\n"
        + "    _dr = _rmax - _rmin;\n"
        + "\n"
        + "float randT = RANDFLOAT();\n"
        + "float randU = RANDFLOAT();\n"
        + "float t = _tmin + randT * _dt;\n"
        + "float u = _umin + randU * _du;\n"
        + "float r = eval%d_polarplot3d_wf(t, u, __polarplot3d_wf_param_a, __polarplot3d_wf_param_b, __polarplot3d_wf_param_c, __polarplot3d_wf_param_d, __polarplot3d_wf_param_e, __polarplot3d_wf_param_f);\n"
        + "float x, y, z;\n"
        + "if (lroundf(__polarplot3d_wf_cylindrical) == 0) {\n"
        + "      x = r * sinf(u) * cosf(t);\n"
        + "      y = r * sinf(u) * sinf(t);\n"
        + "      z = r * cosf(u);\n"
        + "    }\n"
        + "    else {\n"
        + "      x = r * cosf(t);\n"
        + "      y = r * sinf(t);\n"
        + "      z = u;\n"
        + "}\n"
        + "if(lroundf(__polarplot3d_wf_direct_color)>0) {\n"
        + "  switch (lroundf(__polarplot3d_wf_color_mode)) {\n"
        + "        case 1:\n"
        + "          __pal = (t - _tmin) / _dt;\n"
        + "          break;\n"
        + "        case 2:\n"
        + "          __pal = (u - _umin) / _du;\n"
        + "          break;\n"
        + "        case 3:\n"
        + "          __pal = (r - _rmin) / _dr;\n"
        + "          break;\n"
        + "        case 0:\n"
        + "          break;\n"
        + "        default:\n"
        + "        case 4:\n"
        + "          __pal = (t - _tmin) / _dt * (u - _umin) / _du;\n"
        + "          break;\n"
        + "  };\n"
        + "  if (__pal < 0.0) __pal = 0.0;\n"
        + "  else if (__pal > 1.0) __pal = 1.0;\n"
        + "}\n"
        + "__px += __polarplot3d_wf * x;\n"
        + "__py += __polarplot3d_wf * y;\n"
        + "__pz += __polarplot3d_wf * z;\n";
  }

  @Override
  public String getGPUFunctions(FlameTransformationContext context) {
    return "__device__ float eval%d_polarplot3d_wf(float t, float u, float param_a,float param_b, float param_c, float param_d, float param_e, float param_f) {\n"
            +"  float pi = PI;\n"
            +"  return "+ FARenderTools.rewriteJavaFormulaForCUDA(formula) +";\n"
            +"}\n";
  }

}
