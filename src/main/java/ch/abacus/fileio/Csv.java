/*
 * Csv.java
 *
 * Creator:
 * 16.04.2024 09:44 josia.schweizer
 *
 * Maintainer:
 * 16.04.2024 09:44 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.fileio;

import ch.abacus.common.FileIOConst;
import ch.abacus.fileio.components.GetGender;
import ch.abacus.fileio.components.XMLWRITE;
import ch.abacus.db.entity.Address;

import ch.abacus.db.entity.Gender;
import ch.abacus.db.entity.Person;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Csv implements PersonIO {

  private PersonIO next;
  private final GetGender getGender = new GetGender();
  GetFileExtension getFileExtension = new GetFileExtension();

  @Override
  public void write(List<Person> list, File file, XMLWRITE xmlwriteMethode) throws IOException {
    Optional<String> fileExtension = getFileExtension.getExtensionByStringHandling(file.toString());

    if (fileExtension.isPresent() && FileIOConst.CSV.equalsIgnoreCase(fileExtension.get())) {
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
        for (Person person : list) {
          writer.write(person.toCSV());
        }
      }
    } else if (getNext() != null) {
      getNext().write(list, file, xmlwriteMethode);
    }
  }

  @Override
  public List<Person> read(File file, XMLWRITE xmlwriteMethode) throws IOException {
    List<Person> newPersons = new LinkedList<>();
    Optional<String> fileExtension = getFileExtension.getExtensionByStringHandling(file.toString());

    if (fileExtension.isPresent() && FileIOConst.CSV.equalsIgnoreCase(fileExtension.get())) {
      try (Scanner scanner = new Scanner(file, StandardCharsets.ISO_8859_1)) {
        while (scanner.hasNextLine()) {
          newPersons.add(stringToPerson(scanner.nextLine()));
        }
      } catch (IOException e) {
        throw new IOException(e);
      }
    } else if (getNext() != null) {
      newPersons = getNext().read(file, xmlwriteMethode);
    }

    return newPersons;
  }

  public Person stringToPerson(String input) {
    String[] split = input.replace(FileIOConst.SPACE, FileIOConst.NOTHING).split(FileIOConst.COMMASEPARATOR);
    String surname = split[0].trim();
    String name = split[1].trim();
    LocalDate birthday = LocalDate.parse(split[2].trim());
    Gender gender = getGender.getGender(split[3].trim());

    Address address = new Address(split[4].trim(), split[5].trim(), Integer.parseInt(split[6].trim()), split[7].trim(), split[8].trim());

    return new Person(surname, name, birthday, gender, address);
  }

  @Override
  public void setNext(PersonIO next) {
    this.next = next;
  }

  private PersonIO getNext() {
    return next;
  }
}