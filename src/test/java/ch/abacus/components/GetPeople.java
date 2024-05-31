/*
 * GetPeople.java
 *
 * Creator:
 * 15.05.2024 09:49 josia.schweizer
 *
 * Maintainer:
 * 15.05.2024 09:49 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.components;

import ch.abacus.db.entity.Address;
import ch.abacus.db.entity.Gender;
import ch.abacus.db.entity.Person;

import java.time.LocalDate;

public class GetPeople {

  public Person getPerson(Address address) {
    return new Person(null, address
        , "Josia", "Schweizer", LocalDate.of(2007, 9, 18), Gender.MALE);
  }

  public Person getPerson2(Address address) {
    return new Person(null, address, "hund", "hundename", LocalDate.of(2000, 9, 19), Gender.FEMALE);
  }

  public Person getPerson3(Address address) {
    return new Person(null, address, "katze", "katzename", LocalDate.of(2000, 1, 1), Gender.MALE);
  }
}