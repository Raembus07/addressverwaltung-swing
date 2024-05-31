/*
 * JPAStatments.java
 *
 * Creator:
 * 17.05.2024 14:14 josia.schweizer
 *
 * Maintainer:
 * 17.05.2024 14:14 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.common;

public class JPAStatments {

  private JPAStatments() {
  }

  public static final String SELECT_PERSON = "SELECT p FROM Person p";
  public static final String SELECT_ID_BY_PERSON = "select p.id from Person p where p.firstname = :firstname and p.lastname = :lastname and p.gender = :gender and p.birthdate = :birthdate";
  public static final String DELETE_ALL_FROM_PERSON = "DELETE FROM Person";
  public static final String SELECT_ADDRESS = "SELECT a FROM Address a";
  public static final String SELECT_ID_BY_ADDRESS = "SELECT a.id FROM Address a WHERE a.street = :street AND a.streetnumber = :streetnumber AND a.city = :city AND a.zipcode = :postalcode AND a.country = :country";
  public static final String DELETE_ALL_FROM_ADDRESS = "DELETE FROM Address";
}