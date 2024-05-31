/*
 * Model.java
 *
 * Creator:
 * 03.04.2024 13:42 josia.schweizer
 *
 * Maintainer:
 * 03.04.2024 13:42 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.db;

import ch.abacus.db.entity.Person;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface Model {

  Person savePerson(Person person, Consumer<Throwable> consumer) throws Throwable;

  Optional<Person> getPersonById(Long id, Consumer<Throwable> consumer) throws Throwable;

  Person updatePerson(Long id, Person person, Consumer<Throwable> consumer) throws Throwable;

  void delete(Long personId, Consumer<Throwable> consumer) throws Throwable;

  List<Person> findAllPerson(Consumer<Throwable> consumer) throws Throwable;
}
