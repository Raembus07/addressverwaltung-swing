/*
 * FileIOConst.java
 *
 * Creator:
 * 23.05.2024 08:51 josia.schweizer
 *
 * Maintainer:
 * 23.05.2024 08:51 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.common;

public class FileIOConst {

  private FileIOConst() {
  }

  public static final String SPACE = " ";
  public static final String NOTHING = "";

  //GetGender
  public static final String MALE = "male";

  //Csv
  public static final String CSV = "csv";
  public static final String COMMASEPARATOR = ",";

  //Dom & Jaxb & Stax
  public static final String XML = "xml";
  public static final String PERSONS = "Persons";
  public static final String PERSON = "Person";
  public static final String ADDRESS = "Address";
  public static final String FIRSTNAME = "firstname";
  public static final String LASTNAME = "lastname";
  public static final String BIRTHDATE = "birthdate";
  public static final String GENDER = "gender";
  public static final String STREET = "street";
  public static final String STREETNUMBER = "streetnumber";
  public static final String ZIPCODE = "zipcode";
  public static final String CITY = "city";
  public static final String COUNTRY = "country";
  public static final String XMLSTRINGTOREPLACE = "<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?>";
  public static final String XMLDOCTYP = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
  public static final String ERRORSTORINGXML = "Error storing XML";
  public static final String LS = "LS";
  public static final String FORMATPRETTYPRINT = "format-pretty-print";
  public static final String NAMESPACE = "namespaces";
  public static final String NAMESPACEDECLARATIONS = "namespace-declarations";
  public static final String NEWLINE = "\n";
  public static final String FOURSPACE = "    ";
  public static final String EIGTHSPACE = "        ";
  public static final String UNEXPECTEDVALUE = "Unexpected value: ";

  //Json
  public static final String JSON = "json";
  public static final String DOCUMENTNOTSUPPORTED1 = "\nDocument with extension ";
  public static final String DOCUMENTNOTSUPPORTED2 = " aren't supported!";
  public static final String DATETIMEORMATTER = "yyyy-MM-dd";

  //GetFileExtension
  public static final String DOT = ".";
}