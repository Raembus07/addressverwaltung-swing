/*
 * PersonInputVerifier.java
 *
 * Creator:
 * 05.04.2024 11:53 josia.schweizer
 *
 * Maintainer:
 * 05.04.2024 11:53 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.view.components;

import ch.abacus.common.Borders;
import ch.abacus.common.UITextConst;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.border.Border;
import java.io.Serializable;

public class PersonInputVerifier implements Serializable {

  private transient Border redBorder;
  private transient Border standardBorder;

  public PersonInputVerifier() {
    initBorders();
  }

  private void initBorders() {
    standardBorder = Borders.standardBorder;
    redBorder = Borders.redBorder;
  }

  public final transient InputVerifier onlyLetters = new InputVerifier() {
    @Override
    public boolean verify(JComponent input) {
      JTextField textField = (JTextField) input;
      String text = textField.getText();

      if (text.matches(UITextConst.ONLYLETTERS)) {
        setStandardBorder(textField);
        return true;
      } else {
        setRedBorder(textField);
        return false;
      }
    }
  };

  public final transient InputVerifier max8CharactersEmptyVerifier = new InputVerifier() {
    @Override
    public boolean verify(JComponent input) {
      JTextField textField = (JTextField) input;
      String text = textField.getText();

      if (text.length() <= 8 && !text.isEmpty()) {
        setStandardBorder(textField);
        return true;
      } else {
        setRedBorder(textField);
        return false;
      }
    }
  };

  public final transient InputVerifier onlyNumbers = new InputVerifier() {
    @Override
    public boolean verify(JComponent input) {
      JTextField textField = (JTextField) input;
      String text = textField.getText();

      if (text.matches(UITextConst.ONLYNUMBERS)) {
        setStandardBorder(textField);

        return true;
      } else {
        setRedBorder(textField);
        return false;
      }
    }
  };

  public final transient InputVerifier dayVerfier = new InputVerifier() {
    @Override
    public boolean verify(JComponent input) {
      JTextField textField = (JTextField) input;
      String text = textField.getText();

      if (text.matches(UITextConst.DAYVERIFIER)) {
        setStandardBorder(textField);
        return true;
      } else {
        setRedBorder(textField);
        return false;
      }

    }
  };

  public final transient InputVerifier monthVerifier = new InputVerifier() {
    @Override
    public boolean verify(JComponent input) {
      JTextField textField = (JTextField) input;
      String text = textField.getText();

      if (text.matches(UITextConst.MONTHVERIFIER)) {
        setStandardBorder(textField);
        return true;
      } else {
        setRedBorder(textField);
        return false;
      }

    }
  };
  public final transient InputVerifier yearVerifier = new InputVerifier() {
    @Override
    public boolean verify(JComponent input) {
      JTextField textField = (JTextField) input;
      String text = textField.getText();

      if (text.matches(UITextConst.YEARVERIFIER)) {
        setStandardBorder(textField);
        return true;
      } else {
        setRedBorder(textField);
        return false;
      }
    }
  };

  public final transient InputVerifier genderVerifier = new InputVerifier() {
    @Override
    public boolean verify(JComponent input) {
      JTextField textField = (JTextField) input;
      String text = textField.getText();

      if (UITextConst.GENDERVERIFIERMANN.equalsIgnoreCase(text) || UITextConst.GENDERVERIFIERFRAU.equalsIgnoreCase(text)
          || UITextConst.GENDERVERIFIERMALE.equalsIgnoreCase(text) || UITextConst.GENDERVERIFIERFEMALE.equalsIgnoreCase(text)) {
        setStandardBorder(textField);
        return true;
      } else {
        setRedBorder(textField);
        return false;
      }

    }
  };

  public final transient InputVerifier emptyVerifier = new InputVerifier() {
    @Override
    public boolean verify(JComponent input) {
      JTextField textField = (JTextField) input;
      String text = textField.getText();

      if (!text.isEmpty()) {
        setStandardBorder(textField);
        return true;
      } else {
        setRedBorder(textField);
        return false;
      }

    }
  };

  public void setRedBorder(JTextField textField) {
    textField.setBorder(redBorder);
  }

  public void setStandardBorder(JTextField textField) {
    textField.setBorder(standardBorder);
  }
}