/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2022 Andreas Maschke

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
package org.jwildfire.create.tina.swing;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


enum FieldScope {
  FLAME, LAYER, GRADIENT, XFORM
}

public class MainEditorEvents {
  private final TinaController tinaController;
  private final TinaControllerParameter fields;

  public MainEditorEvents(TinaController tinaController, TinaControllerParameter fields) {
    this.tinaController = tinaController;
    this.fields = fields;
  }

  private void setupSlider(
      JLabel label,
      Runnable onReset,
      JWFNumberField editField,
      BiConsumer<ChangeEvent, Boolean> onEditFieldChange,
      JSlider slider,
      BiConsumer<ChangeEvent, Boolean> onSliderChange,
      String motionPropertyName, FieldScope scope) {
    final String labelName = motionPropertyName + "Lbl";
    final String sliderName = motionPropertyName + "Slider";

    label.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
              tinaController.saveUndoPoint();
              onReset.run();
            }
          }
        });
    label.setName(labelName);

    editField.setLinkedLabelControlName(labelName);
    editField.setLinkedMotionControlName(sliderName);
    editField.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            switch (scope) {
              case FLAME: tinaController.getFlameControls().editMotionCurve(e); break;
              case LAYER: tinaController.getLayerControls().editMotionCurve(e); break;
              case GRADIENT: tinaController.getGradientControls().editMotionCurve(e); break;
              case XFORM: tinaController.getXFormControls().editMotionCurve(e); break;
            }
          }
        });
    editField.setMotionPropertyName(motionPropertyName);
    editField.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            if (!editField.isMouseAdjusting() || editField.getMouseChangeCount() == 0) {
              if (!slider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            onEditFieldChange.accept(e, editField.isMouseAdjusting());
          }
        });

    slider.setName(sliderName);
    slider.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mousePressed(MouseEvent e) {
            tinaController.saveUndoPoint();
          }
        });
    slider.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            onSliderChange.accept(e, true);
          }
        });

    slider.addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {

      }

      @Override
      public void mousePressed(MouseEvent e) {

      }

      @Override
      public void mouseReleased(MouseEvent e) {
        onSliderChange.accept(null, false);
      }

      @Override
      public void mouseEntered(MouseEvent e) {

      }

      @Override
      public void mouseExited(MouseEvent e) {

      }
    });

  }

  private void setupField(
          JLabel label,
          Runnable onReset,
          JWFNumberField editField,
          BiConsumer<ChangeEvent, Boolean> onEditFieldChange,
          String motionPropertyName, FieldScope scope) {
    final String labelName = motionPropertyName + "Lbl";

    label.addMouseListener(
            new MouseAdapter() {
              @Override
              public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                  tinaController.saveUndoPoint();
                  onReset.run();
                }
              }
            });
    label.setName(labelName);

    editField.setLinkedLabelControlName(labelName);
    editField.addActionListener(
            new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                switch (scope) {
                  case FLAME: tinaController.getFlameControls().editMotionCurve(e); break;
                  case LAYER: tinaController.getLayerControls().editMotionCurve(e); break;
                  case GRADIENT: tinaController.getGradientControls().editMotionCurve(e); break;
                  case XFORM: tinaController.getXFormControls().editMotionCurve(e); break;
                }
              }
            });
    editField.setMotionPropertyName(motionPropertyName);
    editField.addChangeListener(
            new ChangeListener() {
              public void stateChanged(ChangeEvent e) {
                if (!editField.isMouseAdjusting() || editField.getMouseChangeCount() == 0) {
                  tinaController.saveUndoPoint();
                }
                onEditFieldChange.accept(e, editField.isMouseAdjusting());
              }
            });
  }

  public void setupEvents() {
    setupCameraTabEvents();
    setupColoringTabEvents();
    setupLayersTabEvents();
    setupGradientTabEvents();
    setupAffineTabEvents();
  }

  private void setupCameraTabEvents() {
    setupSlider(
            fields.cameraRollLbl,
            () -> tinaController.getFlameControls().cameraRollREd_reset(),
            fields.pCameraRollREd,
            (ChangeEvent e, Boolean mouseDown) -> tinaController.getFlameControls().cameraRollREd_changed(mouseDown),
            fields.pCameraRollSlider,
            (ChangeEvent e, Boolean mouseDown) -> tinaController.getFlameControls().cameraRollSlider_stateChanged(e, mouseDown),
            "camRoll", FieldScope.FLAME);
    setupSlider(
            fields.cameraPitchLbl,
            () -> tinaController.getFlameControls().cameraPitchREd_reset(),
            fields.pCameraPitchREd,
            (ChangeEvent e, Boolean mouseDown) -> tinaController.getFlameControls().cameraPitchREd_changed(mouseDown),
            fields.pCameraPitchSlider,
            (ChangeEvent e, Boolean mouseDown) -> tinaController.getFlameControls().cameraPitchSlider_stateChanged(e, mouseDown),
            "camPitch", FieldScope.FLAME);
    setupSlider(
            fields.cameraYawLbl,
            () -> tinaController.getFlameControls().cameraYawREd_reset(),
            fields.pCameraYawREd,
            (ChangeEvent e, Boolean mouseDown) -> tinaController.getFlameControls().cameraYawREd_changed(mouseDown),
            fields.pCameraYawSlider,
            (ChangeEvent e, Boolean mouseDown) -> tinaController.getFlameControls().cameraYawSlider_stateChanged(e, mouseDown),
            "camYaw", FieldScope.FLAME);
    setupSlider(
            fields.cameraBankLbl,
            () -> tinaController.getFlameControls().cameraBankREd_reset(),
            fields.pCameraBankREd,
            (ChangeEvent e, Boolean mouseDown) -> tinaController.getFlameControls().cameraBankREd_changed(mouseDown),
            fields.pCameraBankSlider,
            (ChangeEvent e, Boolean mouseDown) -> tinaController.getFlameControls().cameraBankSlider_stateChanged(e, mouseDown),
            "camBank", FieldScope.FLAME);
    setupSlider(
            fields.cameraPerspectiveLbl,
            () -> tinaController.getFlameControls().cameraPerspectiveREd_reset(),
            fields.pCameraPerspectiveREd,
            (ChangeEvent e, Boolean mouseDown) -> tinaController.getFlameControls().cameraPerspectiveREd_changed(mouseDown),
            fields.pCameraPerspectiveSlider,
            (ChangeEvent e, Boolean mouseDown) -> tinaController.getFlameControls().cameraPerspectiveSlider_stateChanged(e, mouseDown),
            "camPerspective", FieldScope.FLAME);

    setupSlider(
        fields.pCameraCentreXLbl,
        () -> tinaController.getFlameControls().cameraCentreXREd_reset(),
        fields.pCameraCentreXREd,
        (ChangeEvent e, Boolean mouseDown) -> tinaController.getFlameControls().cameraCentreXREd_changed(mouseDown),
        fields.pCameraCentreXSlider,
        (ChangeEvent e, Boolean mouseDown) -> tinaController.getFlameControls().cameraCentreXSlider_stateChanged(e, mouseDown),
        "centreX", FieldScope.FLAME);
    setupSlider(
            fields.pCameraCentreYLbl,
            () -> tinaController.getFlameControls().cameraCentreYREd_reset(),
            fields.pCameraCentreYREd,
            (ChangeEvent e, Boolean mouseDown) -> tinaController.getFlameControls().cameraCentreYREd_changed(mouseDown),
            fields.pCameraCentreYSlider,
            (ChangeEvent e, Boolean mouseDown) -> tinaController.getFlameControls().cameraCentreYSlider_stateChanged(e, mouseDown),
            "centreY", FieldScope.FLAME);
    setupSlider(
            fields.pCameraZoomLbl,
            () -> tinaController.getFlameControls().cameraCentreXREd_reset(),
            fields.pCameraZoomREd,
            (ChangeEvent e, Boolean mouseDown) -> tinaController.getFlameControls().cameraZoomREd_changed(mouseDown),
            fields.pCameraZoomSlider,
            (ChangeEvent e, Boolean mouseDown) -> tinaController.getFlameControls().cameraZoomSlider_stateChanged(e, mouseDown),
            "camZoom", FieldScope.FLAME);
    setupSlider(
            fields.pixelsPerUnitLbl,
            () -> tinaController.getFlameControls().pixelsPerUnitREd_reset(),
            fields.pPixelsPerUnitREd,
            (ChangeEvent e, Boolean mouseDown) -> tinaController.getFlameControls().pixelsPerUnitREd_changed(mouseDown),
            fields.pPixelsPerUnitSlider,
            (ChangeEvent e, Boolean mouseDown) -> tinaController.getFlameControls().pixelsPerUnitSlider_stateChanged(e, mouseDown),
            "pixelsPerUnit", FieldScope.FLAME);
    setupSlider(
            fields.camPosXLbl,
            () -> tinaController.getFlameControls().camPosXREd_reset(),
            fields.camPosXREd,
            (ChangeEvent e, Boolean mouseDown) -> tinaController.getFlameControls().camPosXREd_changed(mouseDown),
            fields.camPosXSlider,
            (ChangeEvent e, Boolean mouseDown) -> tinaController.getFlameControls().camPosXSlider_stateChanged(e, mouseDown),
            "camPosX", FieldScope.FLAME);
    setupSlider(
            fields.camPosYLbl,
            () -> tinaController.getFlameControls().camPosYREd_reset(),
            fields.camPosYREd,
            (ChangeEvent e, Boolean mouseDown) -> tinaController.getFlameControls().camPosYREd_changed(mouseDown),
            fields.camPosYSlider,
            (ChangeEvent e, Boolean mouseDown) -> tinaController.getFlameControls().camPosYSlider_stateChanged(e, mouseDown),
            "camPosY", FieldScope.FLAME);
    setupSlider(
            fields.camPosZLbl,
            () -> tinaController.getFlameControls().camPosZREd_reset(),
            fields.camPosZREd,
            (ChangeEvent e, Boolean mouseDown) -> tinaController.getFlameControls().camPosZREd_changed(mouseDown),
            fields.camPosZSlider,
            (ChangeEvent e, Boolean mouseDown) -> tinaController.getFlameControls().camPosZSlider_stateChanged(e, mouseDown),
            "camPosZ", FieldScope.FLAME);


  }

  private void setupColoringTabEvents() {
    setupSlider(
        fields.brightnessLbl,
        () -> tinaController.getFlameControls().brightnessREd_reset(),
        fields.pBrightnessREd,
        (ChangeEvent e, Boolean mouseDown) ->
            tinaController.getFlameControls().brightnessREd_changed(mouseDown),
        fields.pBrightnessSlider,
        (ChangeEvent e, Boolean mouseDown) ->
            tinaController.getFlameControls().brightnessSlider_stateChanged(e, mouseDown),
            "brightness", FieldScope.FLAME);
    setupSlider(
            fields.lowDensityBrightnessLbl,
            () -> tinaController.getFlameControls().lowDensityBrightnessREd_reset(),
            fields.lowDensityBrightnessREd,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.getFlameControls().lowDensityBrightnessREd_changed(mouseDown),
            fields.lowDensityBrightnessSlider,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.getFlameControls().lowDensityBrightnessSlider_stateChanged(e, mouseDown),
            "lowDensityBrightness", FieldScope.FLAME);
    setupSlider(
            fields.gammaLbl,
            () -> tinaController.getFlameControls().gammaREd_reset(),
            fields.pGammaREd,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.getFlameControls().gammaREd_changed(mouseDown),
            fields.pGammaSlider,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.getFlameControls().gammaSlider_stateChanged(e, mouseDown),
            "gamma", FieldScope.FLAME);
    setupSlider(
            fields.gammaThresholdLbl,
            () -> tinaController.getFlameControls().gammaThresholdREd_reset(),
            fields.pGammaThresholdREd,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.getFlameControls().gammaThresholdREd_changed(mouseDown),
            fields.pGammaThresholdSlider,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.getFlameControls().gammaThresholdSlider_stateChanged(e, mouseDown),
            "gammaThreshold", FieldScope.FLAME);
    setupSlider(
            fields.contrastLbl,
            () -> tinaController.getFlameControls().contrastREd_reset(),
            fields.pContrastREd,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.getFlameControls().contrastREd_changed(mouseDown),
            fields.pContrastSlider,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.getFlameControls().contrastSlider_stateChanged(e, mouseDown),
            "contrast", FieldScope.FLAME);
    setupSlider(
            fields.balanceRedLbl,
            () -> tinaController.getFlameControls().balanceRedREd_reset(),
            fields.balanceRedREd,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.getFlameControls().balanceRedREd_changed(mouseDown),
            fields.balanceRedSlider,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.getFlameControls().balanceRedSlider_stateChanged(e, mouseDown),
            "balanceRed", FieldScope.FLAME);
    setupSlider(
            fields.balanceGreenLbl,
            () -> tinaController.getFlameControls().balanceGreenREd_reset(),
            fields.balanceGreenREd,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.getFlameControls().balanceGreenREd_changed(mouseDown),
            fields.balanceGreenSlider,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.getFlameControls().balanceGreenSlider_stateChanged(e, mouseDown),
            "balanceGreen", FieldScope.FLAME);
    setupSlider(
            fields.balanceBlueLbl,
            () -> tinaController.getFlameControls().balanceBlueREd_reset(),
            fields.balanceBlueREd,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.getFlameControls().balanceBlueREd_changed(mouseDown),
            fields.balanceBlueSlider,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.getFlameControls().balanceBlueSlider_stateChanged(e, mouseDown),
            "balanceBlue", FieldScope.FLAME);
    setupSlider(
            fields.whiteLevelLbl,
            () -> tinaController.getFlameControls().whiteLevelREd_reset(),
            fields.whiteLevelREd,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.getFlameControls().whiteLevelREd_changed(mouseDown),
            fields.whiteLevelSlider,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.getFlameControls().whiteLevelSlider_stateChanged(e, mouseDown),
            "whiteLevel", FieldScope.FLAME);
    setupSlider(
            fields.vibrancyLbl,
            () -> tinaController.getFlameControls().vibrancyREd_reset(),
            fields.pVibrancyREd,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.getFlameControls().vibrancyREd_changed(mouseDown),
            fields.pVibrancySlider,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.getFlameControls().vibrancySlider_stateChanged(e, mouseDown),
            "vibrancy", FieldScope.FLAME);
    setupSlider(
            fields.saturationLbl,
            () -> tinaController.getFlameControls().saturationREd_reset(),
            fields.saturationREd,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.getFlameControls().saturationREd_changed(mouseDown),
            fields.saturationSlider,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.getFlameControls().saturationSlider_stateChanged(e, mouseDown),
            "saturation", FieldScope.FLAME);
    setupSlider(
            fields.foregroundOpacityLbl,
            () -> tinaController.getFlameControls().foregroundOpacityREd_reset(),
            fields.foregroundOpacityField,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.getFlameControls().foregroundOpacityREd_changed(mouseDown),
            fields.foregroundOpacitySlider,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.getFlameControls().foregroundOpacitySlider_stateChanged(e, mouseDown),
            "foregroundOpacity", FieldScope.FLAME);
  }

  private void setupLayersTabEvents() {
    setupField(
        fields.layerDensityLbl,
        () -> tinaController.layerDensityREd_reset(),
        fields.layerDensityREd,
        (ChangeEvent e, Boolean mouseDown) ->
            tinaController.layerDensityREd_changed(mouseDown),
            "density",
        FieldScope.LAYER);
    setupField(
            fields.layerWeightLbl,
            () -> tinaController.layerWeightREd_reset(),
            fields.layerWeightEd,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.layerWeightREd_changed(mouseDown),
            "weight",
            FieldScope.LAYER);
  }

  private void setupGradientTabEvents() {
    setupSlider(
            fields.paletteShiftLbl,
            () -> tinaController.paletteShiftREd_reset(),
            fields.pPaletteShiftREd,
            (ChangeEvent e, Boolean mouseDown) -> tinaController.paletteShiftREd_changed(mouseDown),
            fields.pPaletteShiftSlider,
            (ChangeEvent e, Boolean mouseDown) -> tinaController.paletteShiftSlider_stateChanged(e, mouseDown),
            "modShift", FieldScope.GRADIENT);
  }

  private void setupAffineTabEvents() {
    setupField(
            fields.pAffineC00Lbl,
            () -> tinaController.affineC00REd_reset(),
            fields.pAffineC00REd,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.affineC00REd_changed(mouseDown),
            "xyCoeff00",
            FieldScope.XFORM);
    setupField(
            fields.pAffineC10Lbl,
            () -> tinaController.affineC10REd_reset(),
            fields.pAffineC10REd,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.affineC10REd_changed(mouseDown),
            "xyCoeff10",
            FieldScope.XFORM);
    setupField(
            fields.pAffineC20Lbl,
            () -> tinaController.affineC20REd_reset(),
            fields.pAffineC20REd,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.affineC20REd_changed(mouseDown),
            "xyCoeff20",
            FieldScope.XFORM);
    setupField(
            fields.pAffineC01Lbl,
            () -> tinaController.affineC01REd_reset(),
            fields.pAffineC01REd,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.affineC01REd_changed(mouseDown),
            "xyCoeff01",
            FieldScope.XFORM);
    setupField(
            fields.pAffineC11Lbl,
            () -> tinaController.affineC11REd_reset(),
            fields.pAffineC11REd,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.affineC11REd_changed(mouseDown),
            "xyCoeff11",
            FieldScope.XFORM);
    setupField(
            fields.pAffineC21Lbl,
            () -> tinaController.affineC21REd_reset(),
            fields.pAffineC21REd,
            (ChangeEvent e, Boolean mouseDown) ->
                    tinaController.affineC21REd_changed(mouseDown),
            "xyCoeff21",
            FieldScope.XFORM);
  }

}
