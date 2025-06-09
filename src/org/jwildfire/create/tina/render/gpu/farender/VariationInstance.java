/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2025 Andreas Maschke

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
package org.jwildfire.create.tina.render.gpu.farender;

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariationInstance {
  private static final Logger logger = LoggerFactory.getLogger(VariationInstance.class);
  private final String originalName;
  private final int instanceId;
  private final boolean singleton; // one instance per variation set which is the default
  private final String transformedName;
  private final VariationFunc func;
  private final boolean hasWFields;

  public VariationInstance(FlameTransformationContext transformCtx, VariationFunc func, boolean singleton, int instanceId, boolean hasWFields) {
    this.hasWFields = hasWFields;
    this.originalName = getVariationName(transformCtx, func);
    this.singleton = singleton;
    this.instanceId = instanceId;
    this.func = func;
    String baseVarName;
    if(func instanceof CustomFullVariationWrapperFunc) {
      // append random id to enforce that this coe is recompiled each time, because the user may change it
      baseVarName = this.originalName+"_"+UUID.randomUUID().toString().replaceAll("-","");
    }
    else {
      baseVarName = this.originalName;
    }
     // renaming required because some variation names may be reserved words or functions in CUDA, e.g. "log" or "sin"
    this.transformedName = (this.singleton ? "jwf_" : String.format("jwf%d_", instanceId)) + baseVarName;
  }

  public static String getVariationName(FlameTransformationContext transformCtx, VariationFunc func) {
    if(func instanceof CustomFullVariationWrapperFunc) {
      // compile the custom func in order to get the actual name of the (inner) variation func
      try {
        return ((CustomFullVariationWrapperFunc)getInitializedVarFunc(transformCtx, func)).getNameOfWrappedVariation();
      }
      catch(Exception ex) {
        logger.error(ex.getMessage(), ex);
      }
    }
    return func.getName();
  }

  public String getOriginalName() {
    return originalName;
  }

  public boolean isSingleton() {
    return singleton;
  }

  public String getTransformedName() {
    return transformedName;
  }

  public VariationFunc getFunc() {
    return func;
  }

  private String injectInstanceId(String code, int instanceId) {
    Pattern p = Pattern.compile("%d");
    Matcher m = p.matcher(code);
    List<Integer> args=new ArrayList<>();
    while (m.find()){
      args.add(instanceId);
    }
    return String.format(code, args.toArray());
  }

  private static SupportsGPU getInitializedVarFunc(FlameTransformationContext transformCtx, VariationFunc variationFunc) {
    VariationFunc copy = variationFunc.makeCopy();
    copy.init(transformCtx, new Layer(), new XForm(), 1.0);
    return (SupportsGPU)copy;
  }

  public String getTransformedCode(FlameTransformationContext transformCtx) {
    String gpuCode, gpuFunctions;
    {
      SupportsGPU supportsGPU = VariationInstance.getInitializedVarFunc(transformCtx, func);
      {
        String rawGpuCode = getWFieldsInitCode(transformedName)
                          +  supportsGPU.getGPUCode(transformCtx);
        gpuCode = singleton ? rawGpuCode : injectInstanceId(rawGpuCode, instanceId);
        gpuCode = gpuCode.replace("varpar->" + originalName, "varpar->" + transformedName);
        gpuCode = gpuCode.replace("__" + originalName, "__" + transformedName);
      }
      {
        String rawGpuFunctions = supportsGPU.getGPUFunctions(transformCtx);
        gpuFunctions = singleton ? rawGpuFunctions : injectInstanceId(rawGpuFunctions, instanceId);
      }
    }
    StringBuilder sb = new StringBuilder();
    sb.append("<variation name=\""+ transformedName +"\" dimension=\"3d\"");
    if (Arrays.asList(func.getVariationTypes()).contains(VariationFuncType.VARTYPE_DC)) {
      sb.append(" directColor=\"yes\"");
    }
    sb.append(">\n");
    for(String param: func.getParameterNames()) {
      sb.append("<parameter name=\""+param+"\" variation=\""+ transformedName +"\"/>\n");
    }
    for(String param: ((SupportsGPU)func).getGPUExtraParameterNames()) {
      sb.append("<parameter name=\""+param+"\" variation=\""+ transformedName +"\"/>\n");
    }
    sb.append("<source>\n");
    sb.append("<![CDATA[\n");
    sb.append(gpuCode);
    sb.append("]]>\n");
    sb.append("</source>\n");
    if(gpuFunctions!=null && !gpuFunctions.trim().isEmpty()) {
      sb.append("<functions>\n");
      sb.append("<![CDATA[\n");
      sb.append(gpuFunctions);
      sb.append("]]>\n");
      sb.append("</functions>\n");
    }
    sb.append("</variation>\n");
    return sb.toString();
  }

  private String getWFieldsInitCode(String funcName) {
    StringBuffer sb = new StringBuffer();
    if(!hasWFields) {
      sb.append(
              "float __"
                      + originalName
                      + " = varpar->"
                      + originalName
                      + ";\n");

      if (func.getParameterNames().length > 0) {
        for (String param : func.getParameterNames()) {
          sb.append(
                  "float __"
                          + originalName
                          + "_"
                          + param
                          + " = varpar->"
                          + originalName
                          + "_"
                          + param
                          + ";\n");
        }
      }
    } else {
      sb.append("float wFieldScale;\n");
      sb.append(
          "if(-1==xform->wfield_param1_param_idx && varCounter==xform->wfield_param1_var_idx)\n");
      sb.append("  wFieldScale = (1.f + __wFieldValue * xform->wfield_param1_amount);\n");
      sb.append(
          "else if(-1==xform->wfield_param2_param_idx && varCounter==xform->wfield_param2_var_idx)\n");
      sb.append("  wFieldScale = (1.f + __wFieldValue * xform->wfield_param2_amount);\n");
      sb.append(
          "else if(-1==xform->wfield_param3_param_idx && varCounter==xform->wfield_param3_var_idx)\n");
      sb.append("  wFieldScale = (1.f + __wFieldValue * xform->wfield_param3_amount);\n");
      sb.append("else\n");
      sb.append("  wFieldScale = 1.f;\n");
      sb.append(
          "float __"
              + originalName
              + " = varpar->"
              + originalName
              + " * __wFieldAmountScale * wFieldScale;\n");
      if (func.getParameterNames().length > 0) {
        int idx = 0;
        for (String param : func.getParameterNames()) {
          sb.append(
              "if("
                  + idx
                  + "==xform->wfield_param1_param_idx && varCounter==xform->wfield_param1_var_idx)\n");
          sb.append("  wFieldScale = (1.f + __wFieldValue * xform->wfield_param1_amount);\n");
          sb.append(
              "else if("
                  + idx
                  + "==xform->wfield_param2_param_idx && varCounter==xform->wfield_param2_var_idx)\n");
          sb.append("  wFieldScale = (1.f + __wFieldValue * xform->wfield_param2_amount);\n");
          sb.append(
              "else if("
                  + idx
                  + "==xform->wfield_param3_param_idx && varCounter==xform->wfield_param3_var_idx)\n");
          sb.append("  wFieldScale = (1.f + __wFieldValue * xform->wfield_param3_amount);\n");
          sb.append("else\n");
          sb.append("  wFieldScale = 1.f;\n");
          sb.append(
              "float __"
                  + originalName
                  + "_"
                  + param
                  + " = varpar->"
                  + originalName
                  + "_"
                  + param
                  + " * wFieldScale;\n");
          idx++;
        }
      }
    }
    return sb.toString();
  }

}
