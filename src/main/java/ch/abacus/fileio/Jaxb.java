/*
 * Jaxb.java
 *
 * Creator:
 * 16.04.2024 09:48 josia.schweizer
 *
 * Maintainer:
 * 16.04.2024 09:48 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.fileio;

import ch.abacus.common.FileIOConst;
import ch.abacus.fileio.components.XMLWRITE;
import ch.abacus.db.entity.Person;
import ch.abacus.db.entity.Persons;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Jaxb implements PersonIO {

  private PersonIO next;
  GetFileExtension getFileExtension = new GetFileExtension();

  @Override
  public void write(List<Person> list, File file, XMLWRITE xmlwriteMethode) throws IOException {
    Optional<String> fileExtension = getFileExtension.getExtensionByStringHandling(file.toString());

    if (fileExtension.isPresent() && FileIOConst.XML.equalsIgnoreCase(fileExtension.get()) && xmlwriteMethode.equals(XMLWRITE.JAXB)) {
      try {
        Persons persons = new Persons();
        persons.setPersons(list);

        JAXBContext context = JAXBContext.newInstance(Persons.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
          marshaller.marshal(persons, bufferedWriter);
        }
      } catch (JAXBException e) {
        throw new IOException(e);
      }
    } else if (getNext() != null) {
      getNext().write(list, file, xmlwriteMethode);
    }
  }

  @Override
  public List<Person> read(File file, XMLWRITE xmlwriteMethode) throws IOException {
    List<Person> newPersons = new LinkedList<>();
    Optional<String> fileExtension = getFileExtension.getExtensionByStringHandling(file.toString());

    if (fileExtension.isPresent() && FileIOConst.XML.equalsIgnoreCase(fileExtension.get()) && xmlwriteMethode.equals(XMLWRITE.JAXB)) {
      try {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
          JAXBContext context = JAXBContext.newInstance(Persons.class);
          Unmarshaller unmarshaller = context.createUnmarshaller();

          Persons persons = (Persons) unmarshaller.unmarshal(fileInputStream);
          newPersons = new LinkedList<>(persons.getPersons());
        }
      } catch (JAXBException e) {
        throw new IOException(e);
      }
    } else if (getNext() != null) {
      newPersons = getNext().read(file, xmlwriteMethode);
    }
    return newPersons;
  }

  @Override
  public void setNext(PersonIO next) {
    this.next = next;
  }

  private PersonIO getNext() {
    return next;
  }
}