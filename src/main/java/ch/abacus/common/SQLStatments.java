/*
 * SQLStatments.java
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

public class SQLStatments {

  private SQLStatments() {
  }

  public static final String SAVE_PERSON = "INSERT INTO person(fk_address, firstname, lastname, birthdate, gender) VALUE (?, ?, ?, ?, ?)";
  public static final String SAVE_ADDRESS = "INSERT INTO address (street, streetnumber, postalcode, city, country) VALUES (?, ?, ?, ?, ?)";
  public static final String SELECT_ALL_PERSONS = "SELECT * FROM person";
  public static final String SELECT_PERSON_BY_ID = "SELECT * FROM person WHERE id_person = ?";
  public static final String SELECT_ID_BY_PERSON = "SELECT * FROM person  WHERE firstname = ? AND lastname = ? AND birthdate = ? AND gender = ?";
  public static final String DELETE_PERSON_BY_ID = "DELETE FROM person WHERE id_person = ?";
  public static final String DELETE_ALL_PERSONS = "DELETE FROM person";
  public static final String UPDATE_PERSON = "UPDATE person SET fk_address = ?, firstname = ?, lastname = ?, birthdate = ?, gender = ? WHERE id_person = ?";
  public static final String SELECT_ALL_ADDRESS = "SELECT * FROM address";
  public static final String SELECT_ADDRESS_BY_ID = "SELECT * FROM address WHERE id_address = ?";
  public static final String SELECT_ID_FROM_ADDRESS = "SELECT * FROM address WHERE street = ? AND streetnumber = ? AND postalcode = ? AND city = ? AND country = ?";
  public static final String DELETE_ALL_ADDRESSES = "DELETE FROM address";
  public static final String DELETE_ADDRESS_BY_ID = "DELETE FROM address WHERE id_address = ?";
  public static final String UPDATE_ADDRESS = "UPDATE address SET street = ?, streetnumber = ?, postalcode = ?, city = ?, country = ? WHERE id_address = ?";
  public static final String USAGECOUNT_FROM_PERSON = "SELECT COUNT(*) AS count FROM person WHERE fk_address = ?";
}