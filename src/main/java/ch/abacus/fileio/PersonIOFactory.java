/*
 * PersonIOFactory.java
 *
 * Creator:
 * 16.04.2024 09:47 josia.schweizer
 *
 * Maintainer:
 * 16.04.2024 09:47 josia.schweizer
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

public class PersonIOFactory {

  private final PersonIO startElement;

  public PersonIOFactory() {
    PersonIO xmlDomAddressIO = startElement = new Dom();
    PersonIO xmlSaxAddressIO = new Stax();
    PersonIO xmlJaxbAddressIO = new Jaxb();
    PersonIO csvAddressIO = new Csv();
    PersonIO jsonGsonAddressIO = new Json();

    xmlDomAddressIO.setNext(xmlSaxAddressIO);
    xmlSaxAddressIO.setNext(xmlJaxbAddressIO);
    xmlJaxbAddressIO.setNext(csvAddressIO);
    csvAddressIO.setNext(jsonGsonAddressIO);
  }

  public void write(List<Person> persons, File file, XMLWRITE xmlwrite) throws IOException {
    startElement.write(persons, file, xmlwrite);
  }

  public List<Person> read(File file, XMLWRITE xmlwriteMethode) throws IOException {
    return startElement.read(file, xmlwriteMethode);
  }
}