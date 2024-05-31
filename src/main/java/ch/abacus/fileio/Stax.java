/*
 * Stax.java
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
import ch.abacus.fileio.components.GetGender;
import ch.abacus.fileio.components.XMLWRITE;
import ch.abacus.db.entity.Address;
import ch.abacus.db.entity.Person;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Stax implements PersonIO {

  private PersonIO next;
  private final GetGender getGender = new GetGender();

  GetFileExtension getFileExtension = new GetFileExtension();

  @Override
  public void write(List<Person> list, File file, XMLWRITE xmlwriteMethode) throws IOException {
    Optional<String> fileExtension = getFileExtension.getExtensionByStringHandling(file.toString());

    if (fileExtension.isPresent() && FileIOConst.XML.equalsIgnoreCase(fileExtension.get()) && xmlwriteMethode.equals(XMLWRITE.STAX)) {
      try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(bufferedWriter);

        writer.writeDTD(FileIOConst.XMLDOCTYP);
        writer.writeCharacters(FileIOConst.NEWLINE);

        writer.writeStartElement(FileIOConst.PERSONS);
        writer.writeCharacters(FileIOConst.NEWLINE);

        for (Person p : list) {
          writer.writeCharacters(FileIOConst.FOURSPACE);
          writer.writeStartElement(FileIOConst.PERSON);
          writer.writeCharacters(FileIOConst.NEWLINE);

          pWrite(writer, FileIOConst.FIRSTNAME, p.getFirstName());
          pWrite(writer, FileIOConst.LASTNAME, p.getLastName());
          pWrite(writer, FileIOConst.BIRTHDATE, String.valueOf(p.getBirthdate()));
          pWrite(writer, FileIOConst.GENDER, String.valueOf(p.getGender()));

          writer.writeCharacters(FileIOConst.EIGTHSPACE);
          writer.writeStartElement(FileIOConst.ADDRESS);
          writer.writeCharacters(FileIOConst.NEWLINE);
          aWrite(writer, FileIOConst.STREET, p.getAddress().getStreet());
          aWrite(writer, FileIOConst.STREETNUMBER, p.getAddress().getStreetNumber());
          aWrite(writer, FileIOConst.ZIPCODE, String.valueOf(p.getAddress().getZipCode()));
          aWrite(writer, FileIOConst.CITY, p.getAddress().getCity());
          aWrite(writer, FileIOConst.COUNTRY, p.getAddress().getCountry());

          writer.writeCharacters(FileIOConst.EIGTHSPACE);
          writer.writeEndElement();
          writer.writeCharacters(FileIOConst.NEWLINE);

          writer.writeCharacters(FileIOConst.FOURSPACE);
          writer.writeEndElement();
          writer.writeCharacters(FileIOConst.NEWLINE);
          writer.flush();
        }
        writer.writeEndElement();
        writer.writeCharacters(FileIOConst.NEWLINE);

        writer.writeEndDocument();
      } catch (XMLStreamException e) {
        throw new IOException(e);
      }
    } else if (getNext() != null) {
      getNext().write(list, file, xmlwriteMethode);
    }
  }

  private void pWrite(XMLStreamWriter writer, String name, String value) throws XMLStreamException {
    writer.writeCharacters(FileIOConst.EIGTHSPACE);
    writer.writeStartElement(name);
    writer.writeCharacters(value);
    writer.writeEndElement();
    writer.writeCharacters(FileIOConst.NEWLINE);
  }

  private void aWrite(XMLStreamWriter writer, String name, String value) throws XMLStreamException {
    writer.writeCharacters(FileIOConst.EIGTHSPACE);
    writer.writeStartElement(name);
    writer.writeCharacters(value);
    writer.writeEndElement();
    writer.writeCharacters(FileIOConst.NEWLINE);
  }

  @Override
  public List<Person> read(File file, XMLWRITE xmlwriteMethode) throws IOException {
    List<Person> list = new LinkedList<>();
    List<Person> newPersons = new LinkedList<>();

    Optional<String> fileExtension = getFileExtension.getExtensionByStringHandling(file.toString());

    if (fileExtension.isPresent() && FileIOConst.XML.equalsIgnoreCase(fileExtension.get()) && xmlwriteMethode.equals(XMLWRITE.STAX)) {
      try {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
          SAXParserFactory factory = SAXParserFactory.newInstance();
          SAXParser saxParser = factory.newSAXParser();

          DefaultHandler handler = new DefaultHandler() {
            private Person person;
            private Address address;
            private String elementName;

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
              elementName = qName;
              if (FileIOConst.PERSON.equals(elementName)) {
                person = new Person(null, null, null, null, null);
                address = new Address(null, null, 0, null, null);
              }
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
              String text = new String(ch, start, length).trim();
              if (!text.isEmpty()) {
                if (FileIOConst.BIRTHDATE.equals(elementName)) {
                  if (person != null) {
                    person.setBirthdate(LocalDate.parse(text));
                  }
                } else {
                  setPersonProperty(person, address, elementName, text);
                }
              }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
              if (FileIOConst.PERSON.equals(qName)) {
                if (person != null) {
                  person.setAddress(address);
                }
                list.add(person);
                person = null;
                address = null;
              }
            }
          };

          saxParser.parse(fileInputStream, handler);
        }
      } catch (ParserConfigurationException | SAXException | IOException e) {
        throw new IOException(e);
      }
    } else if (getNext() != null) {
      newPersons = getNext().read(file, xmlwriteMethode);
    }

    if (newPersons.isEmpty()) {
      return list;
    } else {
      return newPersons;
    }
  }

  private void setPersonProperty(Person person, Address address, String elementName, String value) {
    switch (elementName) {
      case FileIOConst.FIRSTNAME:
        person.setFirstName(value);
        break;
      case FileIOConst.LASTNAME:
        person.setLastName(value);
        break;
      case FileIOConst.BIRTHDATE:
        person.setBirthdate(LocalDate.parse(value));
        break;
      case FileIOConst.GENDER:
        person.setGender(getGender.getGender(value));
        break;
      case FileIOConst.STREET:
        address.setStreet(value);
        break;
      case FileIOConst.STREETNUMBER:
        address.setStreetNumber(value);
        break;
      case FileIOConst.ZIPCODE:
        address.setZipCode(Integer.parseInt(value));
        break;
      case FileIOConst.CITY:
        address.setCity(value);
        break;
      case FileIOConst.COUNTRY:
        address.setCountry(value);
        break;
      default:
        throw new IllegalStateException(FileIOConst.UNEXPECTEDVALUE + elementName);
    }
  }

  @Override
  public void setNext(PersonIO next) {
    this.next = next;
  }

  private PersonIO getNext() {
    return next;
  }
}