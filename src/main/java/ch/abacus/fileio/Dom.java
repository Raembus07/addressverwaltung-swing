/*
 * Dom.java
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

import ch.abacus.common.FileIOConst;
import ch.abacus.fileio.components.GetGender;
import ch.abacus.fileio.components.XMLWRITE;
import ch.abacus.db.entity.Address;
import ch.abacus.db.entity.Gender;
import ch.abacus.db.entity.Person;
import org.apache.xerces.dom.CoreDOMImplementationImpl;
import org.apache.xerces.dom.DOMXSImplementationSourceImpl;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementationSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Dom implements PersonIO {

  private PersonIO next;
  private final GetGender getGender = new GetGender();
  GetFileExtension getFileExtension = new GetFileExtension();

  @Override
  public void write(List<Person> list, File file, XMLWRITE xmlwriteMethode) throws IOException {
    Optional<String> fileExtension = getFileExtension.getExtensionByStringHandling(file.toString());

    if (fileExtension.isPresent() && FileIOConst.XML.equalsIgnoreCase(fileExtension.get()) && xmlwriteMethode.equals(XMLWRITE.DOM)) {
      try {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement(FileIOConst.PERSONS);
        doc.appendChild(rootElement);

        for (Person p : list) {
          Element personElement = doc.createElement(FileIOConst.PERSON);

          Element surnameElement = doc.createElement(FileIOConst.FIRSTNAME);
          surnameElement.appendChild(doc.createTextNode(p.getFirstName()));
          personElement.appendChild(surnameElement);

          Element nameElement = doc.createElement(FileIOConst.LASTNAME);
          nameElement.appendChild(doc.createTextNode(p.getLastName()));
          personElement.appendChild(nameElement);

          Element birthdayElement = doc.createElement(FileIOConst.BIRTHDATE);
          birthdayElement.appendChild(doc.createTextNode(p.getBirthdate().toString()));
          personElement.appendChild(birthdayElement);

          Element genderElement = doc.createElement(FileIOConst.GENDER);
          genderElement.appendChild(doc.createTextNode(p.getGender().toString()));
          personElement.appendChild(genderElement);

          Element addressElement = doc.createElement(FileIOConst.ADDRESS);

          Element streetElement = doc.createElement(FileIOConst.STREET);
          streetElement.appendChild(doc.createTextNode(p.getAddress().getStreet()));
          addressElement.appendChild(streetElement);

          Element streetnumberElement = doc.createElement(FileIOConst.STREETNUMBER);
          streetnumberElement.appendChild(doc.createTextNode(p.getAddress().getStreetNumber()));
          addressElement.appendChild(streetnumberElement);

          Element zipcodeElement = doc.createElement(FileIOConst.ZIPCODE);
          zipcodeElement.appendChild(doc.createTextNode(String.valueOf(p.getAddress().getZipCode())));
          addressElement.appendChild(zipcodeElement);

          Element cityElement = doc.createElement(FileIOConst.CITY);
          cityElement.appendChild(doc.createTextNode(p.getAddress().getCity()));
          addressElement.appendChild(cityElement);

          Element countryElement = doc.createElement(FileIOConst.COUNTRY);
          countryElement.appendChild(doc.createTextNode(p.getAddress().getCountry()));
          addressElement.appendChild(countryElement);

          personElement.appendChild(addressElement);
          rootElement.appendChild(personElement);
        }

        String xmlString = writeDocumentWithEncoding(doc);

        xmlString = xmlString.replaceFirst(FileIOConst.XMLSTRINGTOREPLACE,
                                           FileIOConst.XMLDOCTYP);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
          writer.write(xmlString);
        }
      } catch (Exception e) {
        throw new IOException(FileIOConst.ERRORSTORINGXML, e);
      }
    } else if (getNext() != null) {
      getNext().write(list, file, xmlwriteMethode);
    }
  }

  public static String writeDocumentWithEncoding(Node aContent) {
    String encoding = StandardCharsets.UTF_8.name();

    DOMImplementationSource registry = new DOMXSImplementationSourceImpl();

    CoreDOMImplementationImpl impl = (CoreDOMImplementationImpl) registry.getDOMImplementation(FileIOConst.LS);
    LSSerializer lsSerializer = impl.createLSSerializer();
    DOMConfiguration config = lsSerializer.getDomConfig();
    if (config.canSetParameter(/*NlsIgnore*/FileIOConst.FORMATPRETTYPRINT, true)) {
      config.setParameter(/*NlsIgnore*/FileIOConst.FORMATPRETTYPRINT, true);
    }
    if (config.canSetParameter(/*NlsIgnore*/FileIOConst.NAMESPACE, true)) {
      config.setParameter(/*NlsIgnore*/FileIOConst.NAMESPACE, true);
    }
    if (config.canSetParameter(/*NlsIgnore*/FileIOConst.NAMESPACEDECLARATIONS, true)) {
      config.setParameter(/*NlsIgnore*/FileIOConst.NAMESPACEDECLARATIONS, true);
    }
    LSOutput lsOutput = impl.createLSOutput();
    ByteArrayOutputStream btOutputStream = new ByteArrayOutputStream();

    lsOutput.setEncoding(encoding);

    lsOutput.setByteStream(btOutputStream);
    lsSerializer.write(aContent, lsOutput);
    return lsOutput.getByteStream().toString();
  }

  @Override
  public List<Person> read(File file, XMLWRITE xmlwriteMethode) throws IOException {
    List<Person> newPerson = new LinkedList<>();
    Optional<String> fileExtension = getFileExtension.getExtensionByStringHandling(file.toString());

    if (fileExtension.isPresent() && FileIOConst.XML.equalsIgnoreCase(fileExtension.get()) && xmlwriteMethode.equals(XMLWRITE.DOM)) {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      try {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
          DocumentBuilder builder = factory.newDocumentBuilder();
          Document document = builder.parse(fileInputStream);

          NodeList personNodes = document.getElementsByTagName(FileIOConst.PERSON);

          for (int i = 0; i < personNodes.getLength(); i++) {
            Element personElement = (Element) personNodes.item(i);

            String firstname = getTextContent(personElement, FileIOConst.FIRSTNAME);
            String lastname = getTextContent(personElement, FileIOConst.LASTNAME);
            String birthdate = getTextContent(personElement, FileIOConst.BIRTHDATE);
            Gender gender = getGender.getGender(getTextContent(personElement, FileIOConst.GENDER));
            String street = getTextContent(personElement, FileIOConst.STREET);
            String streetnumber = getTextContent(personElement, FileIOConst.STREETNUMBER);
            String zipcode = getTextContent(personElement, FileIOConst.ZIPCODE);
            String city = getTextContent(personElement, FileIOConst.CITY);
            String country = getTextContent(personElement, FileIOConst.COUNTRY);

            Address address = null;
            if (zipcode != null) {
              address = new Address(street, streetnumber, Integer.parseInt(zipcode), city, country);
            }
            Person person = null;
            if (birthdate != null) {
              person = new Person(firstname, lastname, LocalDate.parse(birthdate), gender, address);
            }
            newPerson.add(person);
          }
        }
      } catch (ParserConfigurationException | SAXException e) {
        throw new IOException(e);
      }
    } else if (getNext() != null) {
      newPerson = getNext().read(file, xmlwriteMethode);
    }
    return newPerson;
  }

  private String getTextContent(Element parentElement, String childElementName) {
    NodeList nodeList = parentElement.getElementsByTagName(childElementName);
    if (nodeList.getLength() > 0) {
      return nodeList.item(0).getTextContent().trim();
    }
    return null;
  }

  @Override
  public void setNext(PersonIO next) {
    this.next = next;
  }

  private PersonIO getNext() {
    return next;
  }
}