/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2023 Andreas Maschke

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
package org.jwildfire.create.tina.mutagen;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.VariationFunc;

public class RandomParamMutation extends AbstractMutation {

  @Override
  public void execute(Layer pLayer, double mutationStrength) {
    setRandomFlameProperty(pLayer, 6.0 * (0.25 + 0.75 * Math.random()) * mutationStrength);
    setRandomFlameProperty(pLayer, 5.0 * (0.25 + 0.75 * Math.random()) * mutationStrength);
    setRandomFlameProperty(pLayer, 2.0 * (0.25 + 0.75 * Math.random()) * mutationStrength);
  }

  private static List<String> BLACKLIST;

  static {
    BLACKLIST = new ArrayList<String>();
    BLACKLIST.add("truchet");
    BLACKLIST.add("mandelbrot");
    BLACKLIST.add("fract_formula_julia_wf");
    BLACKLIST.add("fract_formula_mand_wf");
    BLACKLIST.add("dc_perlin");
    BLACKLIST.add("snowflake_wf");
    BLACKLIST.add("crob");
    BLACKLIST.add("tree_js");
    BLACKLIST.add("brownian_js");
    BLACKLIST.add("dragon_js");
    BLACKLIST.add("maurer_lines");
    BLACKLIST.add("htree_js");
    BLACKLIST.add("gosperisland_js");
    BLACKLIST.add("rsquares_js");
    BLACKLIST.add("hilbert_js");
    BLACKLIST.add("koch_js");
    BLACKLIST.add("bubbleT3D");
  }

  public void setRandomFlameProperty(Layer pLayer, double pAmount) {
    List<VariationFunc> variations = new ArrayList<VariationFunc>();

    for (XForm xForm : pLayer.getXForms()) {
      addVariations(variations, xForm);
    }
    for (XForm xForm : pLayer.getFinalXForms()) {
      addVariations(variations, xForm);
    }
    filterVariations(variations);
    if (variations.size() > 0) {
      int idx = (int) (Math.random() * variations.size());
      VariationFunc var = variations.get(idx);

      var.mutate(pAmount);
    }
  }

  private void filterVariations(List<VariationFunc> variations) {
    int idx = 0;
    while (idx < variations.size()) {
      if (BLACKLIST.indexOf(variations.get(idx).getName().toLowerCase()) >= 0) {
        variations.remove(idx);
      }
      else {
        idx++;
      }
    }
  }

  private void addVariations(List<VariationFunc> pVariations, XForm pXForm) {
    if (pXForm.getVariationCount() > 0) {
      for (int i = 0; i < pXForm.getVariationCount(); i++) {
        VariationFunc var = pXForm.getVariation(i).getFunc();
        if (var.getParameterNames() != null && var.getParameterNames().length > 0) {
          pVariations.add(var);
        }
      }
    }
  }

}
