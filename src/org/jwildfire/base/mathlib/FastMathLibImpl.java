/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2013 Andreas Maschke

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
package org.jwildfire.base.mathlib;

import odk.lang.DoubleWrapper;
import odk.lang.FastMath;

public final class FastMathLibImpl implements BaseMathLib {
  private static final boolean USE_POWTABS_FOR_ASIN = false;
  public static final double PI_SUP = Math.nextUp(Math.PI);
  private static final double ONE_DIV_F2 = 0.5;
  private static final double ONE_DIV_F3 = 0.16666666666666666;
  private static final double ONE_DIV_F4 = 0.041666666666666664;
  private static final double TWO_POW_24 = Double.longBitsToDouble(4715268809856909312L);
  private static final double TWO_POW_N24 = Double.longBitsToDouble(4499096027743125504L);
  private static final double TWO_POW_26 = Double.longBitsToDouble(4724276009111650304L);
  private static final double TWO_POW_N26 = Double.longBitsToDouble(4490088828488384512L);
  private static final double TWO_POW_27 = Double.longBitsToDouble(4728779608739020800L);
  private static final double TWO_POW_N27 = Double.longBitsToDouble(4485585228861014016L);
  private static final double TWO_POW_N28 = Double.longBitsToDouble(4481081629233643520L);
  private static final double TWO_POW_52 = Double.longBitsToDouble(4841369599423283200L);
  private static final double TWO_POW_N54 = Double.longBitsToDouble(4363988038922010624L);
  private static final double TWO_POW_N55 = Double.longBitsToDouble(4359484439294640128L);
  private static final double TWO_POW_66 = Double.longBitsToDouble(4904419994206470144L);
  private static final double TWO_POW_450 = Double.longBitsToDouble(6633802251116740608L);
  private static final double TWO_POW_N450 = Double.longBitsToDouble(2580562586483294208L);
  private static final double TWO_POW_750 = Double.longBitsToDouble(7984882139327889408L);
  private static final double TWO_POW_N750 = Double.longBitsToDouble(1229482698272145408L);
  private static final double MIN_DOUBLE_NORMAL = Double.longBitsToDouble(4503599627370496L);
  private static final int MIN_DOUBLE_EXPONENT = -1074;
  private static final int MAX_DOUBLE_EXPONENT = 1023;
  private static final int MAX_FLOAT_EXPONENT = 127;
  private static final double LOG_2 = StrictMath.log(2.0);
  private static final double LOG_TWO_POW_27;
  private static final double LOG_DOUBLE_MAX_VALUE;
  private static final double DOUBLE_BEFORE_60;
  private static final double[] ONE_OVER_TWOPI_TAB;
  private static final double TWOPI_TAB0;
  private static final double TWOPI_TAB1;
  private static final double TWOPI_TAB2;
  private static final double TWOPI_TAB3;
  private static final double TWOPI_TAB4;
  private static final double INVPIO2;
  private static final double PIO2_HI;
  private static final double PIO2_LO;
  private static final double INVTWOPI;
  private static final double TWOPI_HI;
  private static final double TWOPI_LO;
  private static final double NORMALIZE_ANGLE_MAX_MEDIUM_DOUBLE;
  private static final double TWO_MATH_PI_IN_MINUS_PI_PI = -2.449293598153844E-16;
  private static final int SIN_COS_TABS_SIZE;
  private static final double SIN_COS_DELTA_HI;
  private static final double SIN_COS_DELTA_LO;
  private static final double SIN_COS_INDEXER;
  private static final double[] sinTab;
  private static final double[] cosTab;
  private static final double SIN_COS_MAX_VALUE_FOR_INT_MODULO;
  private static final int TAN_VIRTUAL_TABS_SIZE;
  private static final double TAN_MAX_VALUE_FOR_TABS;
  private static final int TAN_TABS_SIZE;
  private static final double TAN_DELTA_HI;
  private static final double TAN_DELTA_LO;
  private static final double TAN_INDEXER;
  private static final double[] tanTab;
  private static final double[] tanDer1DivF1Tab;
  private static final double[] tanDer2DivF2Tab;
  private static final double[] tanDer3DivF3Tab;
  private static final double[] tanDer4DivF4Tab;
  private static final double TAN_MAX_VALUE_FOR_INT_MODULO;
  private static final double ASIN_MAX_VALUE_FOR_TABS;
  private static final int ASIN_TABS_SIZE;
  private static final double ASIN_DELTA;
  private static final double ASIN_INDEXER;
  private static final double[] asinTab;
  private static final double[] asinDer1DivF1Tab;
  private static final double[] asinDer2DivF2Tab;
  private static final double[] asinDer3DivF3Tab;
  private static final double[] asinDer4DivF4Tab;
  private static final double ASIN_MAX_VALUE_FOR_POWTABS;
  private static final int ASIN_POWTABS_POWER = 84;
  private static final double ASIN_POWTABS_ONE_DIV_MAX_VALUE;
  private static final int ASIN_POWTABS_SIZE;
  private static final int ASIN_POWTABS_SIZE_MINUS_ONE;
  private static final double[] asinParamPowTab;
  private static final double[] asinPowTab;
  private static final double[] asinDer1DivF1PowTab;
  private static final double[] asinDer2DivF2PowTab;
  private static final double[] asinDer3DivF3PowTab;
  private static final double[] asinDer4DivF4PowTab;
  private static final double ASIN_PIO2_HI;
  private static final double ASIN_PIO2_LO;
  private static final double ASIN_PS0;
  private static final double ASIN_PS1;
  private static final double ASIN_PS2;
  private static final double ASIN_PS3;
  private static final double ASIN_PS4;
  private static final double ASIN_PS5;
  private static final double ASIN_QS1;
  private static final double ASIN_QS2;
  private static final double ASIN_QS3;
  private static final double ASIN_QS4;
  private static final double ATAN_MAX_VALUE_FOR_TABS;
  private static final int ATAN_TABS_SIZE;
  private static final double ATAN_DELTA;
  private static final double ATAN_INDEXER;
  private static final double[] atanTab;
  private static final double[] atanDer1DivF1Tab;
  private static final double[] atanDer2DivF2Tab;
  private static final double[] atanDer3DivF3Tab;
  private static final double[] atanDer4DivF4Tab;
  private static final double ATAN_HI3;
  private static final double ATAN_LO3;
  private static final double ATAN_AT0;
  private static final double ATAN_AT1;
  private static final double ATAN_AT2;
  private static final double ATAN_AT3;
  private static final double ATAN_AT4;
  private static final double ATAN_AT5;
  private static final double ATAN_AT6;
  private static final double ATAN_AT7;
  private static final double ATAN_AT8;
  private static final double ATAN_AT9;
  private static final double ATAN_AT10;
  private static final double EXP_OVERFLOW_LIMIT;
  private static final double EXP_UNDERFLOW_LIMIT;
  private static final double EXP_MIN_INT_LIMIT = -705.0;
  private static final int EXP_LO_DISTANCE_TO_ZERO_POT = 0;
  private static final int EXP_LO_DISTANCE_TO_ZERO = 1;
  private static final int EXP_LO_TAB_SIZE_POT;
  private static final int EXP_LO_TAB_SIZE;
  private static final int EXP_LO_TAB_MID_INDEX;
  private static final int EXP_LO_INDEXING;
  private static final int EXP_LO_INDEXING_DIV_SHIFT;
  private static final double[] expHiTab;
  private static final double[] expHiInvTab;
  private static final double[] expLoPosTab;
  private static final double[] expLoNegTab;
  private static final double EXP_QUICK_A;
  private static final double EXP_QUICK_B;
  private static final double EXP_QUICK_C;
  private static final int LOG_BITS;
  private static final int LOG_TAB_SIZE;
  private static final double[] logXLogTab;
  private static final double[] logXTab;
  private static final double[] logXInvTab;
  private static final double[] twoPowTab;
  private static final int SQRT_LO_BITS;
  private static final int SQRT_LO_TAB_SIZE;
  private static final double[] sqrtXSqrtHiTab;
  private static final double[] sqrtXSqrtLoTab;
  private static final double[] sqrtSlopeHiTab;
  private static final double[] sqrtSlopeLoTab;
  private static final int CBRT_LO_BITS;
  private static final int CBRT_LO_TAB_SIZE;
  private static final double[] cbrtXCbrtHiTab;
  private static final double[] cbrtXCbrtLoTab;
  private static final double[] cbrtSlopeHiTab;
  private static final double[] cbrtSlopeLoTab;
  public static final double E = Math.E;
  public static final double PI = Math.PI;
  private static int getTabSizePower(int var0) {
    return var0;
  }

  private static double subRemainderTwoPi(double var0, double var2, double var4, int var6, int var7) {
    double var25 = 0.0;
    int var46 = var7 - 1;
    int var47 = (var6 - 3) / 24;
    int var48 = var6 - ((var47 << 4) + (var47 << 3)) - 24;
    int var49 = var47 + 4;
    double var13;
    double var15;
    double var17;
    double var19;
    double var21;
    double var23;
    double var29;
    double var31;
    double var33;
    double var35;
    double var37;
    if (var46 == 1) {
      var23 = var49 >= 0 ? ONE_OVER_TWOPI_TAB[var49] : 0.0;
      var21 = var49 >= 1 ? ONE_OVER_TWOPI_TAB[var49 - 1] : 0.0;
      var19 = var49 >= 2 ? ONE_OVER_TWOPI_TAB[var49 - 2] : 0.0;
      var17 = var49 >= 3 ? ONE_OVER_TWOPI_TAB[var49 - 3] : 0.0;
      var15 = var49 >= 4 ? ONE_OVER_TWOPI_TAB[var49 - 4] : 0.0;
      var13 = var49 >= 5 ? ONE_OVER_TWOPI_TAB[var49 - 5] : 0.0;
      var29 = var0 * var15 + var2 * var13;
      var31 = var0 * var17 + var2 * var15;
      var33 = var0 * var19 + var2 * var17;
      var35 = var0 * var21 + var2 * var19;
      var37 = var0 * var23 + var2 * var21;
    } else {
      var25 = var49 >= 0 ? ONE_OVER_TWOPI_TAB[var49] : 0.0;
      var23 = var49 >= 1 ? ONE_OVER_TWOPI_TAB[var49 - 1] : 0.0;
      var21 = var49 >= 2 ? ONE_OVER_TWOPI_TAB[var49 - 2] : 0.0;
      var19 = var49 >= 3 ? ONE_OVER_TWOPI_TAB[var49 - 3] : 0.0;
      var17 = var49 >= 4 ? ONE_OVER_TWOPI_TAB[var49 - 4] : 0.0;
      var15 = var49 >= 5 ? ONE_OVER_TWOPI_TAB[var49 - 5] : 0.0;
      var13 = var49 >= 6 ? ONE_OVER_TWOPI_TAB[var49 - 6] : 0.0;
      var29 = var0 * var17 + var2 * var15 + var4 * var13;
      var31 = var0 * var19 + var2 * var17 + var4 * var15;
      var33 = var0 * var21 + var2 * var19 + var4 * var17;
      var35 = var0 * var23 + var2 * var21 + var4 * var19;
      var37 = var0 * var25 + var2 * var23 + var4 * var21;
    }

    double var11 = (double)((int)(TWO_POW_N24 * var37));
    int var41 = (int)(var37 - TWO_POW_24 * var11);
    double var9 = var35 + var11;
    var11 = (double)((int)(TWO_POW_N24 * var9));
    int var42 = (int)(var9 - TWO_POW_24 * var11);
    var9 = var33 + var11;
    var11 = (double)((int)(TWO_POW_N24 * var9));
    int var43 = (int)(var9 - TWO_POW_24 * var11);
    var9 = var31 + var11;
    var11 = (double)((int)(TWO_POW_N24 * var9));
    int var44 = (int)(var9 - TWO_POW_24 * var11);
    var9 = var29 + var11;
    double var50 = twoPowTab[var48 - -1074];
    var9 = var9 * var50 % 8.0;
    var9 -= (double)((int)var9);
    int var8;
    if (var48 > 0) {
      var44 &= 16777215 >> var48;
      var8 = var44 >> 23 - var48;
    } else if (var48 == 0) {
      var8 = var44 >> 23;
    } else if (var9 >= 0.5) {
      var8 = 2;
    } else {
      var8 = 0;
    }

    if (var8 > 0) {
      boolean var52;
      if (var41 != 0) {
        var52 = true;
        var41 = 16777216 - var41;
        var42 = 16777215 - var42;
        var43 = 16777215 - var43;
        var44 = 16777215 - var44;
      } else if (var42 != 0) {
        var52 = true;
        var42 = 16777216 - var42;
        var43 = 16777215 - var43;
        var44 = 16777215 - var44;
      } else if (var43 != 0) {
        var52 = true;
        var43 = 16777216 - var43;
        var44 = 16777215 - var44;
      } else if (var44 != 0) {
        var52 = true;
        var44 = 16777216 - var44;
      } else {
        var52 = false;
      }

      if (var48 > 0) {
        switch (var48) {
          case 1:
            var44 &= 8388607;
            break;
          case 2:
            var44 &= 4194303;
        }
      }

      if (var8 == 2) {
        var9 = 1.0 - var9;
        if (var52) {
          var9 -= var50;
        }
      }
    }

    int var45;
    if (var9 == 0.0) {
      double var39;
      if (var46 == 1) {
        var25 = ONE_OVER_TWOPI_TAB[var47 + 5];
        var39 = var0 * var25 + var2 * var23;
      } else {
        double var27 = ONE_OVER_TWOPI_TAB[var47 + 5];
        var39 = var0 * var27 + var2 * var25 + var4 * var23;
      }

      var11 = (double)((int)(TWO_POW_N24 * var39));
      var41 = (int)(var39 - TWO_POW_24 * var11);
      var9 = var37 + var11;
      var11 = (double)((int)(TWO_POW_N24 * var9));
      var42 = (int)(var9 - TWO_POW_24 * var11);
      var9 = var35 + var11;
      var11 = (double)((int)(TWO_POW_N24 * var9));
      var43 = (int)(var9 - TWO_POW_24 * var11);
      var9 = var33 + var11;
      var11 = (double)((int)(TWO_POW_N24 * var9));
      var44 = (int)(var9 - TWO_POW_24 * var11);
      var9 = var31 + var11;
      var11 = (double)((int)(TWO_POW_N24 * var9));
      var45 = (int)(var9 - TWO_POW_24 * var11);
      var9 = var29 + var11;
      var9 = var9 * var50 % 8.0;
      var9 -= (double)((int)var9);
      if (var48 > 0) {
        var45 &= 16777215 >> var48;
        var8 = var45 >> 23 - var48;
      } else if (var48 == 0) {
        var8 = var45 >> 23;
      } else if (var9 >= 0.5) {
        var8 = 2;
      } else {
        var8 = 0;
      }

      if (var8 > 0) {
        if (var41 != 0) {
          var41 = 16777216 - var41;
          var42 = 16777215 - var42;
          var43 = 16777215 - var43;
          var44 = 16777215 - var44;
          var45 = 16777215 - var45;
        } else if (var42 != 0) {
          var42 = 16777216 - var42;
          var43 = 16777215 - var43;
          var44 = 16777215 - var44;
          var45 = 16777215 - var45;
        } else if (var43 != 0) {
          var43 = 16777216 - var43;
          var44 = 16777215 - var44;
          var45 = 16777215 - var45;
        } else if (var44 != 0) {
          var44 = 16777216 - var44;
          var45 = 16777215 - var45;
        } else if (var45 != 0) {
          var45 = 16777216 - var45;
        }

        if (var48 > 0) {
          switch (var48) {
            case 1:
              var45 &= 8388607;
              break;
            case 2:
              var45 &= 4194303;
          }
        }
      }

      var11 = var50 * TWO_POW_N24;
    } else {
      var45 = (int)(var9 / var50);
      var11 = var50;
    }

    var37 = var11 * (double)var45;
    var11 *= TWO_POW_N24;
    var35 = var11 * (double)var44;
    var11 *= TWO_POW_N24;
    var33 = var11 * (double)var43;
    var11 *= TWO_POW_N24;
    var31 = var11 * (double)var42;
    var11 *= TWO_POW_N24;
    var29 = var11 * (double)var41;
    var11 *= TWO_POW_N24;
    var11 = TWOPI_TAB0 * var37;
    var11 += TWOPI_TAB0 * var35 + TWOPI_TAB1 * var37;
    var11 += TWOPI_TAB0 * var33 + TWOPI_TAB1 * var35 + TWOPI_TAB2 * var37;
    var11 += TWOPI_TAB0 * var31 + TWOPI_TAB1 * var33 + TWOPI_TAB2 * var35 + TWOPI_TAB3 * var37;
    var11 += TWOPI_TAB0 * var29 + TWOPI_TAB1 * var31 + TWOPI_TAB2 * var33 + TWOPI_TAB3 * var35 + TWOPI_TAB4 * var37;
    return var8 == 0 ? var11 : -var11;
  }
  private static double remainderTwoPi(double var0) {
    boolean var2;
    if (var0 < 0.0) {
      var2 = true;
      var0 = -var0;
    } else {
      var2 = false;
    }

    if (var0 <= NORMALIZE_ANGLE_MAX_MEDIUM_DOUBLE) {
      double var17 = (double)((int)(var0 * INVTWOPI + 0.5));
      double var18 = var0 - var17 * TWOPI_HI - var17 * TWOPI_LO;
      return var2 ? -var18 : var18;
    } else if (var0 < Double.POSITIVE_INFINITY) {
      long var3 = Double.doubleToRawLongBits(var0);
      long var5 = (var3 >> 52 & 2047L) - 1046L;
      double var7 = Double.longBitsToDouble(var3 - (var5 << 52));
      double var9 = (double)((int)var7);
      var7 = (var7 - var9) * TWO_POW_24;
      double var11 = (double)((int)var7);
      double var13 = (var7 - var11) * TWO_POW_24;
      double var15 = subRemainderTwoPi(var9, var11, var13, (int)var5, var13 == 0.0 ? 2 : 3);
      return var2 ? -var15 : var15;
    } else {
      return Double.NaN;
    }
  }
  @Override
  public double sin(double var0) {
    boolean var2;
    if (var0 < 0.0) {
      var0 = -var0;
      var2 = true;
    } else {
      var2 = false;
    }

    if (var0 > SIN_COS_MAX_VALUE_FOR_INT_MODULO) {
      var0 = remainderTwoPi(var0);
      if (var0 < 0.0) {
        var0 += 6.283185307179586;
      }
    }

    int var3 = (int)(var0 * SIN_COS_INDEXER + 0.5);
    double var4 = var0 - (double)var3 * SIN_COS_DELTA_HI - (double)var3 * SIN_COS_DELTA_LO;
    var3 &= SIN_COS_TABS_SIZE - 2;
    double var6 = sinTab[var3];
    double var8 = cosTab[var3];
    double var10 = var6 + var4 * (var8 + var4 * (-var6 * 0.5 + var4 * (-var8 * 0.16666666666666666 + var4 * var6 * 0.041666666666666664)));
    return var2 ? -var10 : var10;
  }

  @Override
  public double cos(double var0) {
    return FastMath.cos(var0);
/*
    var0 = (var0 <= 0.0D) ? 0.0D - var0 : var0;
    if (var0 > SIN_COS_MAX_VALUE_FOR_INT_MODULO) {
      var0 = remainderTwoPi(var0);
      if (var0 < 0.0) {
        var0 += 6.283185307179586;
      }
    }

    int var2 = (int)(var0 * SIN_COS_INDEXER + 0.5);
    double var3 = var0 - (double)var2 * SIN_COS_DELTA_HI - (double)var2 * SIN_COS_DELTA_LO;
    var2 &= SIN_COS_TABS_SIZE - 2;
    double var5 = cosTab[var2];
    double var7 = sinTab[var2];
    return var5 + var3 * (-var7 + var3 * (-var5 * 0.5 + var3 * (var7 * 0.16666666666666666 + var3 * var5 * 0.041666666666666664)));*/
  }

  @Override
  public void sinAndCos(double a, DoubleWrapper sine, DoubleWrapper cosine) {
    FastMath.sinAndCos(a, sine, cosine);
  }

  @Override
  public double tan(double var0) {
    if (Math.abs(var0) > TAN_MAX_VALUE_FOR_INT_MODULO) {
      var0 = remainderTwoPi(var0);
      if (var0 < -1.5707963267948966) {
        var0 += Math.PI;
      } else if (var0 > 1.5707963267948966) {
        var0 -= Math.PI;
      }
    }

    boolean var2;
    if (var0 < 0.0) {
      var0 = -var0;
      var2 = true;
    } else {
      var2 = false;
    }

    int var3 = (int)(var0 * TAN_INDEXER + 0.5);
    double var4 = var0 - (double)var3 * TAN_DELTA_HI - (double)var3 * TAN_DELTA_LO;
    var3 &= 2 * (TAN_VIRTUAL_TABS_SIZE - 1) - 1;
    if (var3 > TAN_VIRTUAL_TABS_SIZE - 1) {
      var3 = 2 * (TAN_VIRTUAL_TABS_SIZE - 1) - var3;
      var4 = -var4;
      var2 = !var2;
    }

    double var6;
    if (var3 < TAN_TABS_SIZE) {
      var6 = tanTab[var3] + var4 * (tanDer1DivF1Tab[var3] + var4 * (tanDer2DivF2Tab[var3] + var4 * (tanDer3DivF3Tab[var3] + var4 * tanDer4DivF4Tab[var3])));
    } else {
      var3 = TAN_VIRTUAL_TABS_SIZE - 1 - var3;
      var6 = 1.0 / (tanTab[var3] - var4 * (tanDer1DivF1Tab[var3] - var4 * (tanDer2DivF2Tab[var3] - var4 * (tanDer3DivF3Tab[var3] - var4 * tanDer4DivF4Tab[var3]))));
    }

    return var2 ? -var6 : var6;
  }

  @Override
  public double atan2(double var0, double var2) {
    if (var2 > 0.0) {
      if (var0 == 0.0) {
        return var0 < 0.0 ? -0.0 : 0.0;
      } else if (var2 == Double.POSITIVE_INFINITY) {
        if (var0 == Double.POSITIVE_INFINITY) {
          return 0.7853981633974483;
        } else if (var0 == Double.NEGATIVE_INFINITY) {
          return -0.7853981633974483;
        } else if (var0 > 0.0) {
          return 0.0;
        } else {
          return var0 < 0.0 ? -0.0 : Double.NaN;
        }
      } else {
        return atan(var0 / var2);
      }
    } else if (var2 < 0.0) {
      if (var0 == 0.0) {
        return var0 < 0.0 ? -3.141592653589793 : Math.PI;
      } else if (var2 == Double.NEGATIVE_INFINITY) {
        if (var0 == Double.POSITIVE_INFINITY) {
          return 2.356194490192345;
        } else if (var0 == Double.NEGATIVE_INFINITY) {
          return -2.356194490192345;
        } else if (var0 > 0.0) {
          return Math.PI;
        } else {
          return var0 < 0.0 ? -3.141592653589793 : Double.NaN;
        }
      } else if (var0 > 0.0) {
        return 1.5707963267948966 + atan(-var2 / var0);
      } else {
        return var0 < 0.0 ? -1.5707963267948966 - atan(var2 / var0) : Double.NaN;
      }
    } else if (var2 == 0.0) {
      if (var0 == 0.0) {
        if (var2 < 0.0) {
          return var0 < 0.0 ? -3.141592653589793 : Math.PI;
        } else {
          return var0 < 0.0 ? -0.0 : 0.0;
        }
      } else if (var0 > 0.0) {
        return 1.5707963267948966;
      } else {
        return var0 < 0.0 ? -1.5707963267948966 : Double.NaN;
      }
    } else {
      return Double.NaN;
    }
  }

  @Override
  public double exp(double a) {
    return FastMath.exp(a);
  }

  @Override
  public double sqrt(double var0) {
    if (!(var0 > 0.0)) {
      return var0 == 0.0 ? var0 : Double.NaN;
    } else if (var0 == Double.POSITIVE_INFINITY) {
      return Double.POSITIVE_INFINITY;
    } else {
      double var2;
      if (var0 < MIN_DOUBLE_NORMAL) {
        var0 *= TWO_POW_52;
        var2 = 2.0 * TWO_POW_N26;
      } else {
        var2 = 2.0;
      }

      int var4 = (int)(Double.doubleToRawLongBits(var0) >> 32);
      int var5 = (var4 >> 20) + 51;
      int var6 = var4 << 12 >>> 32 - SQRT_LO_BITS;
      double var7 = sqrtXSqrtHiTab[var5] * sqrtXSqrtLoTab[var6];
      double var9 = sqrtSlopeHiTab[var5] * sqrtSlopeLoTab[var6];
      var0 *= 0.25;
      var7 += (var0 - var7 * var7) * var9;
      var7 += (var0 - var7 * var7) * var9;
      return var2 * (var7 + (var0 - var7 * var7) * var9);
    }
  }

  @Override
  public double pow(double value, double power) {
    return FastMath.pow(value, power);
  }

  // fast approximation from Martin Ankerl's blog: http://martin.ankerl.com/2007/10/04/optimized-pow-approximation-for-java-and-c-c/
  // maybe for fast preview?
  public double fastpow(double value, double power) {
    final long tmp = Double.doubleToLongBits(value);
    final long tmp2 = (long) (power * (tmp - 4606921280493453312L)) + 4606921280493453312L;
    return Double.longBitsToDouble(tmp2);
  }

  @Override
  public double floor(double value) {
    return FastMath.floor(value);
  }

  @Override
  public double round(double value) {
    return FastMath.round(value);
  }

  @Override
  public double log10(double value) {
    return Math.log10(value);
  }

  @Override
  public double log(double value) {
    return Math.log(value);
  }

  @Override
  public double sinh(double value) {
    return FastMath.sinh(value);
  }

  @Override
  public double cosh(double value) {
    return FastMath.cosh(value);
  }

  @Override
  public double tanh(double value) {
    return FastMath.tanh(value);
  }

  @Override
  public double atan(double value) {
    return FastMath.atan(value);
  }

  @Override
  public double acos(double value) {
    return FastMath.acos(value);
  }

  @Override
  public double asin(double value) {
    return FastMath.asin(value);
  }

  static {
    LOG_TWO_POW_27 = StrictMath.log(TWO_POW_27);
    LOG_DOUBLE_MAX_VALUE = StrictMath.log(Double.MAX_VALUE);
    DOUBLE_BEFORE_60 = Math.nextAfter(60.0, 0.0);
    ONE_OVER_TWOPI_TAB = new double[]{2670176.0, 1.4390161E7, 346751.0, 644596.0, 8211767.0, 7354072.0, 1.0839631E7, 1106960.0, 8361048.0, 1.539883E7, 1.5816813E7, 1.317979E7, 9474932.0, 1.2059026E7, 4962946.0, 7627911.0, 4163450.0, 1.3053002E7, 6934458.0, 2133373.0, 4959953.0, 2177639.0, 1837485.0, 1564560.0, 5137525.0, 9330900.0, 1.3532455E7, 2168802.0, 1.5695434E7, 968702.0, 2490359.0, 8480259.0, 1.65017E7, 6477442.0, 1.0176475E7, 5087155.0, 1.3234882E7, 7197649.0, 9427367.0, 9960075.0, 6113774.0, 1.1664121E7, 8150735.0, 4312701.0, 1.4849188E7, 1.2229374E7, 1.4150727E7};
    TWOPI_TAB0 = Double.longBitsToDouble(4618760255839404032L);
    TWOPI_TAB1 = Double.longBitsToDouble(4509304086968926208L);
    TWOPI_TAB2 = Double.longBitsToDouble(4402346256551116800L);
    TWOPI_TAB3 = Double.longBitsToDouble(4294406894572797952L);
    TWOPI_TAB4 = Double.longBitsToDouble(4183874305429340160L);
    INVPIO2 = Double.longBitsToDouble(4603909380684499075L);
    PIO2_HI = Double.longBitsToDouble(4609753056924401664L);
    PIO2_LO = Double.longBitsToDouble(4454258360616903473L);
    INVTWOPI = INVPIO2 / 4.0;
    TWOPI_HI = 4.0 * PIO2_HI;
    TWOPI_LO = 4.0 * PIO2_LO;
    NORMALIZE_ANGLE_MAX_MEDIUM_DOUBLE = StrictMath.pow(2.0, 20.0) * 6.283185307179586;
    SIN_COS_TABS_SIZE = (1 << getTabSizePower(11)) + 1;
    SIN_COS_DELTA_HI = TWOPI_HI / (double)(SIN_COS_TABS_SIZE - 1);
    SIN_COS_DELTA_LO = TWOPI_LO / (double)(SIN_COS_TABS_SIZE - 1);
    SIN_COS_INDEXER = 1.0 / (SIN_COS_DELTA_HI + SIN_COS_DELTA_LO);
    sinTab = new double[SIN_COS_TABS_SIZE];
    cosTab = new double[SIN_COS_TABS_SIZE];
    SIN_COS_MAX_VALUE_FOR_INT_MODULO = 4194303.0 / SIN_COS_INDEXER * 0.99;
    TAN_VIRTUAL_TABS_SIZE = (1 << getTabSizePower(12)) + 1;
    TAN_MAX_VALUE_FOR_TABS = Math.toRadians(77.0);
    TAN_TABS_SIZE = (int)(TAN_MAX_VALUE_FOR_TABS / 1.5707963267948966 * (double)(TAN_VIRTUAL_TABS_SIZE - 1)) + 1;
    TAN_DELTA_HI = PIO2_HI / (double)(TAN_VIRTUAL_TABS_SIZE - 1);
    TAN_DELTA_LO = PIO2_LO / (double)(TAN_VIRTUAL_TABS_SIZE - 1);
    TAN_INDEXER = 1.0 / (TAN_DELTA_HI + TAN_DELTA_LO);
    tanTab = new double[TAN_TABS_SIZE];
    tanDer1DivF1Tab = new double[TAN_TABS_SIZE];
    tanDer2DivF2Tab = new double[TAN_TABS_SIZE];
    tanDer3DivF3Tab = new double[TAN_TABS_SIZE];
    tanDer4DivF4Tab = new double[TAN_TABS_SIZE];
    TAN_MAX_VALUE_FOR_INT_MODULO = 4194303.0 / TAN_INDEXER * 0.99;
    ASIN_MAX_VALUE_FOR_TABS = StrictMath.sin(Math.toRadians(73.0));
    ASIN_TABS_SIZE = (1 << getTabSizePower(13)) + 1;
    ASIN_DELTA = ASIN_MAX_VALUE_FOR_TABS / (double)(ASIN_TABS_SIZE - 1);
    ASIN_INDEXER = 1.0 / ASIN_DELTA;
    asinTab = new double[ASIN_TABS_SIZE];
    asinDer1DivF1Tab = new double[ASIN_TABS_SIZE];
    asinDer2DivF2Tab = new double[ASIN_TABS_SIZE];
    asinDer3DivF3Tab = new double[ASIN_TABS_SIZE];
    asinDer4DivF4Tab = new double[ASIN_TABS_SIZE];
    ASIN_MAX_VALUE_FOR_POWTABS = StrictMath.sin(Math.toRadians(88.6));
    ASIN_POWTABS_ONE_DIV_MAX_VALUE = 1.0 / ASIN_MAX_VALUE_FOR_POWTABS;
    ASIN_POWTABS_SIZE = 0;
    ASIN_POWTABS_SIZE_MINUS_ONE = ASIN_POWTABS_SIZE - 1;
    asinParamPowTab = new double[ASIN_POWTABS_SIZE];
    asinPowTab = new double[ASIN_POWTABS_SIZE];
    asinDer1DivF1PowTab = new double[ASIN_POWTABS_SIZE];
    asinDer2DivF2PowTab = new double[ASIN_POWTABS_SIZE];
    asinDer3DivF3PowTab = new double[ASIN_POWTABS_SIZE];
    asinDer4DivF4PowTab = new double[ASIN_POWTABS_SIZE];
    ASIN_PIO2_HI = Double.longBitsToDouble(4609753056924675352L);
    ASIN_PIO2_LO = Double.longBitsToDouble(4364452196894661639L);
    ASIN_PS0 = Double.longBitsToDouble(4595172819793696085L);
    ASIN_PS1 = Double.longBitsToDouble(-4623835544539140227L);
    ASIN_PS2 = Double.longBitsToDouble(4596417465768494165L);
    ASIN_PS3 = Double.longBitsToDouble(-4637438604930937029L);
    ASIN_PS4 = Double.longBitsToDouble(4560439845004096136L);
    ASIN_PS5 = Double.longBitsToDouble(4540259411154564873L);
    ASIN_QS1 = Double.longBitsToDouble(-4610777653840302773L);
    ASIN_QS2 = Double.longBitsToDouble(4611733184086379208L);
    ASIN_QS3 = Double.longBitsToDouble(-4618997306433404583L);
    ASIN_QS4 = Double.longBitsToDouble(4590215604441354882L);
    ATAN_MAX_VALUE_FOR_TABS = StrictMath.tan(Math.toRadians(74.0));
    ATAN_TABS_SIZE = (1 << getTabSizePower(12)) + 1;
    ATAN_DELTA = ATAN_MAX_VALUE_FOR_TABS / (double)(ATAN_TABS_SIZE - 1);
    ATAN_INDEXER = 1.0 / ATAN_DELTA;
    atanTab = new double[ATAN_TABS_SIZE];
    atanDer1DivF1Tab = new double[ATAN_TABS_SIZE];
    atanDer2DivF2Tab = new double[ATAN_TABS_SIZE];
    atanDer3DivF3Tab = new double[ATAN_TABS_SIZE];
    atanDer4DivF4Tab = new double[ATAN_TABS_SIZE];
    ATAN_HI3 = Double.longBitsToDouble(4609753056924675352L);
    ATAN_LO3 = Double.longBitsToDouble(4364452196894661639L);
    ATAN_AT0 = Double.longBitsToDouble(4599676419421066509L);
    ATAN_AT1 = Double.longBitsToDouble(-4626998257160492092L);
    ATAN_AT2 = Double.longBitsToDouble(4594314991288484863L);
    ATAN_AT3 = Double.longBitsToDouble(-4630701217362536847L);
    ATAN_AT4 = Double.longBitsToDouble(4591215095208222830L);
    ATAN_AT5 = Double.longBitsToDouble(-4633165035261879699L);
    ATAN_AT6 = Double.longBitsToDouble(4589464229703073105L);
    ATAN_AT7 = Double.longBitsToDouble(-4634804155249132134L);
    ATAN_AT8 = Double.longBitsToDouble(4587333258118041067L);
    ATAN_AT9 = Double.longBitsToDouble(-4637946461342241745L);
    ATAN_AT10 = Double.longBitsToDouble(4580351289466214929L);
    EXP_OVERFLOW_LIMIT = Double.longBitsToDouble(4649454530587146735L);
    EXP_UNDERFLOW_LIMIT = Double.longBitsToDouble(-4573606559926636463L);
    EXP_LO_TAB_SIZE_POT = getTabSizePower(11);
    EXP_LO_TAB_SIZE = (1 << EXP_LO_TAB_SIZE_POT) + 1;
    EXP_LO_TAB_MID_INDEX = (EXP_LO_TAB_SIZE - 1) / 2;
    EXP_LO_INDEXING = EXP_LO_TAB_MID_INDEX / 1;
    EXP_LO_INDEXING_DIV_SHIFT = EXP_LO_TAB_SIZE_POT - 1 - 0;
    expHiTab = new double[1 + (int)EXP_OVERFLOW_LIMIT];
    expHiInvTab = new double[1 - (int)EXP_UNDERFLOW_LIMIT];
    expLoPosTab = new double[EXP_LO_TAB_SIZE];
    expLoNegTab = new double[EXP_LO_TAB_SIZE];
    EXP_QUICK_A = TWO_POW_52 / LOG_2;
    EXP_QUICK_B = 1023.0 * TWO_POW_52;
    EXP_QUICK_C = Math.ceil((StrictMath.log(LOG_2 + 0.7357588823428847) - LOG_2 - StrictMath.log(LOG_2)) * EXP_QUICK_A);
    LOG_BITS = getTabSizePower(12);
    LOG_TAB_SIZE = 1 << LOG_BITS;
    logXLogTab = new double[LOG_TAB_SIZE];
    logXTab = new double[LOG_TAB_SIZE];
    logXInvTab = new double[LOG_TAB_SIZE];
    twoPowTab = new double[2098];
    SQRT_LO_BITS = getTabSizePower(12);
    SQRT_LO_TAB_SIZE = 1 << SQRT_LO_BITS;
    sqrtXSqrtHiTab = new double[2098];
    sqrtXSqrtLoTab = new double[SQRT_LO_TAB_SIZE];
    sqrtSlopeHiTab = new double[2098];
    sqrtSlopeLoTab = new double[SQRT_LO_TAB_SIZE];
    CBRT_LO_BITS = getTabSizePower(12);
    CBRT_LO_TAB_SIZE = 1 << CBRT_LO_BITS;
    cbrtXCbrtHiTab = new double[2098];
    cbrtXCbrtLoTab = new double[CBRT_LO_TAB_SIZE];
    cbrtSlopeHiTab = new double[2098];
    cbrtSlopeLoTab = new double[CBRT_LO_TAB_SIZE];
    int var0 = (SIN_COS_TABS_SIZE - 1) / 2;
    int var1 = 2 * var0;
    int var2 = var0 / 2;
    int var3 = 3 * var0 / 2;

    int var4;
    double var5;
    double var7;
    double var9;
    for(var4 = 0; var4 < SIN_COS_TABS_SIZE; ++var4) {
      var5 = (double)var4 * SIN_COS_DELTA_HI + (double)var4 * SIN_COS_DELTA_LO;
      var7 = StrictMath.sin(var5);
      var9 = StrictMath.cos(var5);
      if (var4 == var0) {
        var7 = 0.0;
      } else if (var4 == var1) {
        var7 = 0.0;
      } else if (var4 == var2) {
        var9 = 0.0;
      } else if (var4 == var3) {
        var9 = 0.0;
      }

      sinTab[var4] = var7;
      cosTab[var4] = var9;
    }

    double var11;
    double var13;
    double var15;
    for(var4 = 0; var4 < TAN_TABS_SIZE; ++var4) {
      var5 = (double)var4 * TAN_DELTA_HI + (double)var4 * TAN_DELTA_LO;
      tanTab[var4] = StrictMath.tan(var5);
      var7 = StrictMath.cos(var5);
      var9 = StrictMath.sin(var5);
      var11 = 1.0 / var7;
      var13 = var11 * var11;
      var15 = var13 * var11;
      double var17 = var13 * var13;
      double var19 = var15 * var13;
      tanDer1DivF1Tab[var4] = var13;
      tanDer2DivF2Tab[var4] = 2.0 * var9 * var15 * 0.5;
      tanDer3DivF3Tab[var4] = 2.0 * (1.0 + 2.0 * var9 * var9) * var17 * 0.16666666666666666;
      tanDer4DivF4Tab[var4] = 8.0 * var9 * (2.0 + var9 * var9) * var19 * 0.041666666666666664;
    }

    for(var4 = 0; var4 < ASIN_TABS_SIZE; ++var4) {
      var5 = (double)var4 * ASIN_DELTA;
      asinTab[var4] = StrictMath.asin(var5);
      var7 = 1.0 / (1.0 - var5 * var5);
      var9 = StrictMath.sqrt(var7);
      var11 = var9 * var7;
      var13 = var11 * var7;
      var15 = var13 * var7;
      asinDer1DivF1Tab[var4] = var9;
      asinDer2DivF2Tab[var4] = var5 * var11 * 0.5;
      asinDer3DivF3Tab[var4] = (1.0 + 2.0 * var5 * var5) * var13 * 0.16666666666666666;
      asinDer4DivF4Tab[var4] = (5.0 + 2.0 * var5 * (2.0 + var5 * (5.0 - 2.0 * var5))) * var15 * 0.041666666666666664;
    }

    for(var4 = 0; var4 < ATAN_TABS_SIZE; ++var4) {
      var5 = (double)var4 * ATAN_DELTA;
      var7 = 1.0 / (1.0 + var5 * var5);
      var9 = var7 * var7;
      var11 = var9 * var7;
      var13 = var9 * var9;
      atanTab[var4] = StrictMath.atan(var5);
      atanDer1DivF1Tab[var4] = var7;
      atanDer2DivF2Tab[var4] = -2.0 * var5 * var9 * 0.5;
      atanDer3DivF3Tab[var4] = (-2.0 + 6.0 * var5 * var5) * var11 * 0.16666666666666666;
      atanDer4DivF4Tab[var4] = 24.0 * var5 * (1.0 - var5 * var5) * var13 * 0.041666666666666664;
    }

    for(var4 = 0; var4 < EXP_LO_TAB_SIZE; ++var4) {
      var5 = -1.0 + (double)var4 / (double)EXP_LO_INDEXING;
      expLoPosTab[var4] = StrictMath.exp(var5);
      expLoNegTab[var4] = -StrictMath.expm1(-var5);
    }

    for(var4 = 0; var4 <= (int)EXP_OVERFLOW_LIMIT; ++var4) {
      expHiTab[var4] = StrictMath.exp((double)var4);
    }

    for(var4 = 0; var4 <= -((int)EXP_UNDERFLOW_LIMIT); ++var4) {
      if ((double)(-var4) >= -705.0) {
        expHiInvTab[var4] = StrictMath.exp((double)(-var4));
      } else {
        expHiInvTab[var4] = StrictMath.exp(54.0 * LOG_2 - (double)var4);
      }
    }

    for(var4 = 0; var4 < LOG_TAB_SIZE; ++var4) {
      var5 = 1.0 + (double)var4 * (1.0 / (double)LOG_TAB_SIZE);
      logXLogTab[var4] = StrictMath.log(var5);
      logXTab[var4] = var5;
      logXInvTab[var4] = 1.0 / var5;
    }

    for(var4 = -1074; var4 <= 1023; ++var4) {
      twoPowTab[var4 - -1074] = StrictMath.pow(2.0, (double)var4);
    }

    for(var4 = -1074; var4 <= 1023; ++var4) {
      var5 = StrictMath.pow(2.0, (double)var4 * 0.5);
      sqrtXSqrtHiTab[var4 - -1074] = var5 * 0.5;
      sqrtSlopeHiTab[var4 - -1074] = 1.0 / var5;
    }

    sqrtXSqrtLoTab[0] = 1.0;
    sqrtSlopeLoTab[0] = 1.0;
    long var23 = 4607182418800017408L | 4503599627370495L >> SQRT_LO_BITS;

    int var6;
    for(var6 = 1; var6 < SQRT_LO_TAB_SIZE; ++var6) {
      long var22 = var23 | (long)(var6 - 1) << 52 - SQRT_LO_BITS;
      var9 = StrictMath.sqrt(Double.longBitsToDouble(var22));
      sqrtXSqrtLoTab[var6] = var9;
      sqrtSlopeLoTab[var6] = 1.0 / var9;
    }

    for(var6 = -1074; var6 <= 1023; ++var6) {
      var7 = StrictMath.pow(2.0, (double)var6 / 3.0);
      cbrtXCbrtHiTab[var6 - -1074] = var7 * 0.5;
      var9 = 1.0 / var7;
      cbrtSlopeHiTab[var6 - -1074] = 1.3333333333333333 * var9 * var9;
    }

    cbrtXCbrtLoTab[0] = 1.0;
    cbrtSlopeLoTab[0] = 1.0;
    long var21 = 4607182418800017408L | 4503599627370495L >> CBRT_LO_BITS;

    for(int var8 = 1; var8 < CBRT_LO_TAB_SIZE; ++var8) {
      long var24 = var21 | (long)(var8 - 1) << 52 - CBRT_LO_BITS;
      var11 = StrictMath.cbrt(Double.longBitsToDouble(var24));
      cbrtXCbrtLoTab[var8] = var11;
      cbrtSlopeLoTab[var8] = 1.0 / (var11 * var11);
    }

  }
}

