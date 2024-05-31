/*
 * Const.java
 *
 * Creator:
 * 03.04.2024 09:47 josia.schweizer
 *
 * Maintainer:
 * 03.04.2024 09:47 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.common;

public class DbConst {

  private DbConst() {
  }

  public static final String URL = "jdbc:mysql://localhost:3306/persons?useLegacyDatetimeCode=false&serverTimezone=UTC";
  public static final String USER = "test";
  public static final String PASS = "Passwort1234";
  public static final String DRIVER = "com.mysql.cj.jdbc.Driver";

  //SQLModelImpl & JPAModelImpl & Persons & Person
  public static final String NOPERSONFOUND = "No person found with id: ";
  public static final String PERSONS = "Persons";
  public static final String PERSON = "Person";
  public static final String PERSONSMALL = "person";
  public static final String ADDRESSSMALL = "address";
  public static final String ADDRESS = "Address";
  public static final String IDPERSON = "id_person";
  public static final String IDADDRESS = "id_address";
  public static final String FKADDRESS = "fk_address";
  public static final String FIRSTNAME = "firstname";
  public static final String LASTNAME = "lastname";
  public static final String BIRTHDATE = "birthdate";
  public static final String GENDER = "gender";
  public static final String STREET = "street";
  public static final String STREETNUMBER = "streetnumber";
  public static final String POSTALCODE = "postalcode";
  public static final String CITY = "city";
  public static final String COUNTRY = "country";
  public static final String MALE = "male";
  public static final String COUNT = "count";
  public static final String SQLEXCEPTION = "SQLException: ";
  public static final String SQLSTATE = "SQLState: ";
  public static final String VENDORERRORCODE = "VendorError: ";
  public static final String TITLE = "addressverwaltungUI";
  public static final String SPACE = " ";
  public static final String COMMASPACE = ", ";

}