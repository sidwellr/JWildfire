/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2020 Andreas Maschke

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
package org.jwildfire.create.tina.render.denoiser;

import org.jwildfire.base.Tools;

import java.awt.*;

public class OptixCmdLineAIPostDenoiser extends CmdLineAIPostDenoiser {

  @Override
  protected String[] getDenoiserCmd(String denoiserPath, String inputFilename, String outputFilename, double blend) {
    return new String[]{String.format("\"%s\\OptiXDenoiser.exe\"", denoiserPath), "-i", String.format("\"%s\"", inputFilename.replace('\\', '/')), "-o", String.format("\"%s\"", outputFilename.replace('\\', '/')), "-b", String.format("%s", Tools.doubleToString(blend))};
  }

  @Override
  protected Color getIndicatorColor() {
    return new Color(118, 185, 0);
  }
}
