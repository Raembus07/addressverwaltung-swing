/*
 * Rows.java
 *
 * Creator:
 * 05.04.2024 10:09 josia.schweizer
 *
 * Maintainer:
 * 05.04.2024 10:09 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.view.components;

import ch.abacus.common.ViewConst;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.GridLayout;

public class Rows extends JPanel {


  private JLabel label;
  private JTextField textField;
  private transient PersonInputVerifier personInputVerifier;
  private String title;

  public Rows createRow(String title, boolean enabled) {
    this.title = title;

    this.setLayout(new GridLayout(1, 2));
    this.setBorder(new EmptyBorder(5, 20, 5, 20));


    label = new JLabel(title);
    textField = new JTextField();
    textField.setEditable(enabled);
    textField.setBackground(Color.WHITE);
    if (!enabled) {
      textField.setBorder(null);
    }

    add(label);
    add(textField);

    return this;
  }

  public void setPersonInputverfier() {
    switch (title) {
      case ViewConst.FIRSTNAME:
      case ViewConst.STREET:
      case ViewConst.LASTNAME:
        textField.setInputVerifier(getPersonInputVerifier().emptyVerifier);
        break;
      case ViewConst.GENDER:
        textField.setInputVerifier(getPersonInputVerifier().genderVerifier);
        break;
      case ViewConst.BIRTHDATE_DAY:
        textField.setInputVerifier(getPersonInputVerifier().dayVerfier);
        break;
      case ViewConst.BIRTHDATE_MONTH:
        textField.setInputVerifier(getPersonInputVerifier().monthVerifier);
        break;
      case ViewConst.BIRTHDATE_YEAR:
        textField.setInputVerifier(getPersonInputVerifier().yearVerifier);
        break;
      case ViewConst.STREETNUMBER:
        textField.setInputVerifier(getPersonInputVerifier().max8CharactersEmptyVerifier);
        break;
      case ViewConst.POSTALCODE:
        textField.setInputVerifier(getPersonInputVerifier().onlyNumbers);
        break;
      case ViewConst.CITY:
      case ViewConst.COUNTRY:
        textField.setInputVerifier(getPersonInputVerifier().onlyLetters);
        break;
      default:
        throw new IllegalStateException("Unexpected value: " + title);
    }
  }

  private PersonInputVerifier getPersonInputVerifier() {
    if (personInputVerifier == null) {
      personInputVerifier = new PersonInputVerifier();
    }
    return personInputVerifier;
  }

  public void removeInputVerifier() {
    textField.setInputVerifier(null);
  }


  public JTextField getTextField() {
    return textField;
  }

  public void setTextFieldText(String text) {
    textField.setText(text);
  }

  public void setBground(Color color) {
    label.setBackground(color);
    textField.setBackground(color);
  }
}