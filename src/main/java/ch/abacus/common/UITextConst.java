/*
 * UITextConst.java
 *
 * Creator:
 * 23.05.2024 08:28 josia.schweizer
 *
 * Maintainer:
 * 23.05.2024 08:28 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.common;

public class UITextConst {

  private UITextConst() {
  }

  public static final String FONT = "Arial";
  public static final String NOTHING = "";
  public static final String ERROR = "Error";

  //ViewImpl
  public static final String TITLE = "Adressverwaltung";
  public static final String PERSONDETAILS = "Personendetails";
  public static final String ADDBUTTON = "Hinzufügen";
  public static final String MODIFYBUTTON = "Ändern";
  public static final String DELETEBUTTON = "Löschen";

  public static final String LOADFROMFILE = "Von Datei laden";
  public static final String SAVETOFILE = "In Datei speichern";
  public static final String FILEMENUTITLE = "Datei";

  public static final String DBMENUTITLE = "Datenbank";
  public static final String SQL = "SQL (default)";
  public static final String JPA = "JPA";

  //DefaultPersonTableModel
  public static final String FIRSTNAME = "Vorname";
  public static final String LASTNAME = "Nachname";

  //PersonInputVerifier & PersonDetailPanel
  public static final String ONLYLETTERS = "[a-zA-ZäöüÄÖÜßéàèÉÀÈ. ]+";
  public static final String ONLYNUMBERS = "\\d+";
  public static final String DAYVERIFIER = "([1-9]|[12][0-9]|3[01])";
  public static final String MONTHVERIFIER = "([1-9]|1[0-2])";
  public static final String YEARVERIFIER = "\\d{4}";
  public static final String DATEFORMAT = "%04d-%02d-%02d";
  public static final String GENDERVERIFIERMANN = "Mann";
  public static final String GENDERVERIFIERMALE = "Male";
  public static final String GENDERVERIFIERFRAU = "Frau";
  public static final String GENDERVERIFIERFEMALE = "Female";

  //PersonDialog & ErrorDialog
  public static final String OK = "  Okay  ";
  public static final String EXIT = "Abbrechen";

  //FileDialog
  public static final String XMLCSVJSON = "XML, CSV, JSON";
  public static final String XML = "xml";
  public static final String CSV = "csv";
  public static final String JSON = "json";
  public static final String NEWFILEDESTINATION = "C:\\dev\\java\\addressverwaltungUI\\src";

}