/*
 * ErrorConst.java
 *
 * Creator:
 * 23.05.2024 10:03 josia.schweizer
 *
 * Maintainer:
 * 23.05.2024 10:03 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.common;

public class ErrorConst {

  private ErrorConst() {
  }

  public static final String NOPERSONSELECTEDTITLE = "Keine Person ausgewählt";
  public static final String NOPERSONSELECTEDTEXT = "Bitte wählen Sie zuerst eine Person aus!";
  public static final String PERSONALREADYEXISTSTITLE = "Person bereits vorhanden";
  public static final String PERSONALREADYEXISTSTEXT = "Die Person wurde bereits hinzugefügt!";
  public static final String INVALIDFILEEXTENSIONTITLE = "Ungültiges Dateiformat";
  public static final String INVALIDFILEEXTENSIONTEXT = "Bitte nutzen Sie nur die Dateiformate .csv / .xml / .json!";
  public static final String INVALIDFILETITLE = "Ungültige Datei";
  public static final String INVALIDFILETEXT = "Bitte gegben Sie eine existierende Datei ein!";
  public static final String INVALIDINPUTTITLE = "Unvollständige Eingabe";
  public static final String INVALIDINPUTTEXT = "Ihre Eingabe ist inkorrekt!";
  public static final String UNEXPECTEDVALUE = "Unexpected value: ";
}