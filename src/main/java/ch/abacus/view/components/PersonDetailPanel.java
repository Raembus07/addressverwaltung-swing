/*
 * PersonDetailPanel.java
 *
 * Creator:
 * 04.04.2024 16:25 josia.schweizer
 *
 * Maintainer:
 * 04.04.2024 16:25 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.view.components;

import ch.abacus.common.UITextConst;
import ch.abacus.common.ViewConst;
import ch.abacus.db.entity.Address;
import ch.abacus.db.entity.Gender;
import ch.abacus.db.entity.Person;
import ch.abacus.view.View;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

public class PersonDetailPanel extends JPanel {

  private Person model;
  private JLabel titleLabel;
  private final transient View view;
  private final Map<String, Rows> rowsMap = new HashMap<>();
  private final transient PersonInputVerifier personInputVerifier;

  public PersonDetailPanel(View view) {
    this.view = view;
    this.model = new Person();
    personInputVerifier = new PersonInputVerifier();
  }

  public JPanel initUI(boolean enabled) {
    this.setLayout(new BorderLayout());

    this.add(createTitle(), BorderLayout.NORTH);
    this.add(createRows(enabled), BorderLayout.CENTER);

    return this;
  }

  private JPanel createTitle() {
    JPanel titlePanel = new JPanel();
    titleLabel = new JLabel();
    titleLabel.setBorder(null);
    titleLabel.setFont(new Font(UITextConst.FONT, Font.BOLD, 18));
    titleLabel.setBackground(view.getBground());
    titlePanel.add(titleLabel);

    return titlePanel;
  }

  private JPanel createRows(boolean enabled) {
    JPanel rowsPanel = new JPanel(new GridLayout(0, 1));
    rowsPanel.add(createRow(ViewConst.FIRSTNAME, ViewConst.FIRSTNAME, enabled));
    rowsPanel.add(createRow(ViewConst.LASTNAME, ViewConst.LASTNAME, enabled));
    rowsPanel.add(createRow(ViewConst.GENDER, ViewConst.GENDER, enabled));
    rowsPanel.add(createRow(ViewConst.BIRTHDATE_DAY, ViewConst.BIRTHDATE_DAY, enabled));
    rowsPanel.add(createRow(ViewConst.BIRTHDATE_MONTH, ViewConst.BIRTHDATE_MONTH, enabled));
    rowsPanel.add(createRow(ViewConst.BIRTHDATE_YEAR, ViewConst.BIRTHDATE_YEAR, enabled));
    rowsPanel.add(createRow(ViewConst.STREET, ViewConst.STREET, enabled));
    rowsPanel.add(createRow(ViewConst.STREETNUMBER, ViewConst.STREETNUMBER, enabled));
    rowsPanel.add(createRow(ViewConst.POSTALCODE, ViewConst.POSTALCODE, enabled));
    rowsPanel.add(createRow(ViewConst.CITY, ViewConst.CITY, enabled));
    rowsPanel.add(createRow(ViewConst.COUNTRY, ViewConst.COUNTRY, enabled));
    return rowsPanel;
  }

  private JPanel createRow(String identifier, String title, boolean enabled) {
    Rows rows = new Rows();
    rowsMap.put(identifier, rows.createRow(title, enabled));
    rows.setBground(view.getBground());
    return rows;
  }

  public void fillModel() {
    this.model.setFirstName(rowsMap.get(ViewConst.FIRSTNAME).getTextField().getText());
    this.model.setLastName(rowsMap.get(ViewConst.LASTNAME).getTextField().getText());
    Gender gender = Gender.MALE;
    if (UITextConst.GENDERVERIFIERFEMALE.equalsIgnoreCase(rowsMap.get(ViewConst.GENDER).getTextField().getText())
        || UITextConst.GENDERVERIFIERFRAU.equalsIgnoreCase(rowsMap.get(ViewConst.GENDER).getTextField().getText())) {
      gender = Gender.FEMALE;
    }
    this.model.setGender(gender);
    int day = Integer.parseInt(rowsMap.get(ViewConst.BIRTHDATE_DAY).getTextField().getText());
    int month = Integer.parseInt(rowsMap.get(ViewConst.BIRTHDATE_MONTH).getTextField().getText());
    int year = Integer.parseInt(rowsMap.get(ViewConst.BIRTHDATE_YEAR).getTextField().getText());
    LocalDate birthdate = LocalDate.of(year, month, day);
    this.model.setBirthdate(birthdate);

    if (this.model.getAddress() == null) {
      this.model.setAddress(new Address());
    }

    this.model.getAddress().setStreet(rowsMap.get(ViewConst.STREET).getTextField().getText());
    this.model.getAddress().setStreetNumber(rowsMap.get(ViewConst.STREETNUMBER).getTextField().getText());
    this.model.getAddress().setCity(rowsMap.get(ViewConst.CITY).getTextField().getText());
    this.model.getAddress().setZipCode(Integer.parseInt(rowsMap.get(ViewConst.POSTALCODE).getTextField().getText()));
    this.model.getAddress().setCountry(rowsMap.get(ViewConst.COUNTRY).getTextField().getText());
  }

  public void fillTextfield() {
    rowsMap.get(ViewConst.FIRSTNAME).setTextFieldText(model.getFirstName());
    rowsMap.get(ViewConst.LASTNAME).setTextFieldText(model.getLastName());
    rowsMap.get(ViewConst.GENDER).setTextFieldText(model.getGender().toString().toLowerCase());
    LocalDate birthdate = model.getBirthdate();
    int day = birthdate.getDayOfMonth();
    int month = birthdate.getMonthValue();
    int year = birthdate.getYear();
    rowsMap.get(ViewConst.BIRTHDATE_DAY).setTextFieldText(String.valueOf(day));
    rowsMap.get(ViewConst.BIRTHDATE_MONTH).setTextFieldText(String.valueOf(month));
    rowsMap.get(ViewConst.BIRTHDATE_YEAR).setTextFieldText(String.valueOf(year));
    rowsMap.get(ViewConst.STREET).setTextFieldText(model.getAddress().getStreet());
    rowsMap.get(ViewConst.STREETNUMBER).setTextFieldText(model.getAddress().getStreetNumber());
    rowsMap.get(ViewConst.CITY).setTextFieldText(model.getAddress().getCity());
    rowsMap.get(ViewConst.POSTALCODE).setTextFieldText(String.valueOf(model.getAddress().getZipCode()));
    rowsMap.get(ViewConst.COUNTRY).setTextFieldText(model.getAddress().getCountry());
  }

  public void setFocusToFirstTextField() {
    rowsMap.get(ViewConst.FIRSTNAME).getTextField().requestFocusInWindow();
  }

  public void addInputVerifiers() {
    rowsMap.forEach((identifier, rows) -> rows.setPersonInputverfier());
  }

  public void removeInputVerifiers() {
    rowsMap.forEach((identifier, rows) -> rows.removeInputVerifier());
  }

  public Person getModel() {
    return this.model;
  }

  public void clearTextfield() {
    for (Rows rows : rowsMap.values()) {
      rows.getTextField().setText(UITextConst.NOTHING);
    }
  }

  public void setModel(Person model) {
    this.model = model;
  }

  public Map<String, Rows> getRowsMap() {
    return rowsMap;
  }

  public void setTitle(String text) {
    titleLabel.setText(text);
  }

  public boolean everyInputCorrect() {
    for (Rows row : rowsMap.values()) {
      String text = row.getTextField().getText();
      if (text == null || text.trim().isEmpty()) {
        return false;
      }
      if (!row.getTextField().getInputVerifier().verify(row.getTextField())) {
        return false;
      }
    }

    int day = Integer.parseInt(rowsMap.get(ViewConst.BIRTHDATE_DAY).getTextField().getText());
    int month = Integer.parseInt(rowsMap.get(ViewConst.BIRTHDATE_MONTH).getTextField().getText());
    int year = Integer.parseInt(rowsMap.get(ViewConst.BIRTHDATE_YEAR).getTextField().getText());

    if (month < 1 || month > 12) {
      personInputVerifier.setRedBorder(rowsMap.get(ViewConst.BIRTHDATE_MONTH).getTextField());
      return false;
    }

    if (day < 1 || day > LocalDate.of(year, month, 1).lengthOfMonth()) {
      personInputVerifier.setRedBorder(rowsMap.get(ViewConst.BIRTHDATE_DAY).getTextField());
      return false;
    }

    if (year > getYear()) {
      personInputVerifier.setRedBorder(rowsMap.get(ViewConst.BIRTHDATE_YEAR).getTextField());
      return false;
    }

    String dateString = String.format(UITextConst.DATEFORMAT, year, month, day);
    return validLocaldate(dateString, day, month, year);
  }

  private boolean validLocaldate(String dateString, int day, int month, int year) {
    try {
      LocalDate.parse(dateString);
      return earlierThanToday(day, month, year);
    } catch (DateTimeParseException e) {
      personInputVerifier.setRedBorder(rowsMap.get(ViewConst.BIRTHDATE_DAY).getTextField());
      return false;
    }
  }

  private int getYear() {
    return LocalDate.now().getYear();
  }

  private boolean earlierThanToday(int day, int month, int year) {
    return LocalDate.of(year, month, day).isBefore(LocalDate.now());
  }
}