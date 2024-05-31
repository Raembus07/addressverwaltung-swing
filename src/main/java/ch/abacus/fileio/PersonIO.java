/*
 * PersonIO.java
 *
 * Creator:
 * 16.04.2024 09:45 josia.schweizer
 *
 * Maintainer:
 * 16.04.2024 09:45 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.fileio;

import ch.abacus.fileio.components.XMLWRITE;
import ch.abacus.db.entity.Person;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface PersonIO {

  void write(List<Person> persons, File file, XMLWRITE xmlwriteMethode) throws IOException;

  List<Person> read(File file, XMLWRITE xmlwriteMethode) throws IOException;

  void setNext(PersonIO next);
}
