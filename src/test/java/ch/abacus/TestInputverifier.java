/*
 * TestInputverifier.java
 *
 * Creator:
 * 24.05.2024 15:38 josia.schweizer
 *
 * Maintainer:
 * 24.05.2024 15:38 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus;

import ch.abacus.view.components.PersonInputVerifier;
import org.junit.jupiter.api.Test;

import javax.swing.JTextField;

import static junit.framework.Assert.assertTrue;
import static junitx.framework.Assert.assertFalse;

class TestInputverifier {

  PersonInputVerifier personIV = new PersonInputVerifier();

  @Test
  void testInputVerifierOnlyLetters() {
    var textField = new JTextField();
    textField.setText("Test");
    assertTrue(personIV.onlyLetters.verify(textField));
    textField.setText("Test123");
    assertFalse(personIV.onlyLetters.verify(textField));
  }

  @Test
  void testInputVerifierMax8CharactersEmptyVerifier() {
    var textField = new JTextField();
    textField.setText("");
    assertFalse(personIV.max8CharactersEmptyVerifier.verify(textField));
    textField.setText("Test");
    assertTrue(personIV.max8CharactersEmptyVerifier.verify(textField));
    textField.setText("Test123456");
    assertFalse(personIV.max8CharactersEmptyVerifier.verify(textField));
  }

  @Test
  void testInputVerifierOnlyNumbers() {
    var textField = new JTextField();
    textField.setText("123");
    assertTrue(personIV.onlyNumbers.verify(textField));
    textField.setText("Test123");
    assertFalse(personIV.onlyNumbers.verify(textField));
  }

  @Test
  void testDayInputVerifier() {
    var textField = new JTextField();
    textField.setText("21");
    assertTrue(personIV.dayVerfier.verify(textField));
    textField.setText("32!");
    assertFalse(personIV.dayVerfier.verify(textField));
  }

  @Test
  void testMonthInputVerifier() {
    var textField = new JTextField();
    textField.setText("12");
    assertTrue(personIV.monthVerifier.verify(textField));
    textField.setText("13");
    assertFalse(personIV.monthVerifier.verify(textField));
  }

  @Test
  void testYearInputVerifier() {
    var textField = new JTextField();
    textField.setText("2024");
    assertTrue(personIV.yearVerifier.verify(textField));
    textField.setText("20244");
    assertFalse(personIV.yearVerifier.verify(textField));
  }

  @Test
  void testGenderInputVerifier() {
    var textField = new JTextField();
    textField.setText("Male");
    assertTrue(personIV.genderVerifier.verify(textField));
    textField.setText("Female");
    assertTrue(personIV.genderVerifier.verify(textField));
    textField.setText("X");
    assertFalse(personIV.genderVerifier.verify(textField));
  }

  @Test
  void testEmptyInputVerifier() {
    var textField = new JTextField();
    textField.setText("");
    assertFalse(personIV.emptyVerifier.verify(textField));
    textField.setText("Test");
    assertTrue(personIV.emptyVerifier.verify(textField));
  }
}