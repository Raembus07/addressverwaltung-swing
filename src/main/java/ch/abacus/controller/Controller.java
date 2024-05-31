/*
 * Controller.java
 *
 * Creator:
 * 03.04.2024 13:38 josia.schweizer
 *
 * Maintainer:
 * 03.04.2024 13:38 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.controller;

import ch.abacus.common.State;
import ch.abacus.db.entity.Person;

import java.util.List;
import java.util.function.Consumer;

public interface Controller extends Consumer<Throwable> {

  void init();

  void addState(State state);

  List<Person> getAllPeople();

}
