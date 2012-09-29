/*
 JWildfireC - an external C-based fractal-flame-renderer for JWildfire
 Copyright (C) 2012 Andreas Maschke

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
#ifndef JWFVAR_BUBBLE_WF_H_
#define JWFVAR_BUBBLE_WF_H_

#include "jwf_Variation.h"

class BubbleWFFunc: public Variation {
public:
	BubbleWFFunc() {
	}

	const char* getName() const {
		return "bubble_wf";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		float r = ((pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y) / 4.0f + 1.0f);
		float t = pAmount / r;
		pVarTP->x += t * pAffineTP->x;
		pVarTP->y += t * pAffineTP->y;
		if (pContext->randGen->random() < 0.5f) {
			pVarTP->z -= pAmount * (2.0f / r - 1.0f);
		}
		else {
			pVarTP->z += pAmount * (2.0f / r - 1.0f);
		}
	}

	BubbleWFFunc* makeCopy() {
		return new BubbleWFFunc(*this);
	}

};

#endif // JWFVAR_BUBBLE_WF_H_
