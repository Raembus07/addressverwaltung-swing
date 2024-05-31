/*
 * TestPerson.java
 *
 * Creator:
 * 15.05.2024 14:07 josia.schweizer
 *
 * Maintainer:
 * 15.05.2024 14:07 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.sql;

import ch.abacus.components.GetAddresses;
import ch.abacus.components.GetPeople;
import ch.abacus.controller.Controller;
import ch.abacus.controller.ControllerImpl;
import ch.abacus.db.SQLModelImpl;
import ch.abacus.db.entity.Address;
import ch.abacus.db.entity.Gender;
import ch.abacus.db.entity.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestPerson {

  private Connection connection;
  private final Controller controller = new ControllerImpl();
  private SQLModelImpl model;
  private final GetPeople getPeople = new GetPeople();
  private final GetAddresses getAddresses = new GetAddresses();

  @BeforeEach
  public void before() throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    model = new SQLModelImpl(controller) {
      @Override
      public void commit() {

      }
    };
    connection = ReflectionSql.getConnectionUsingReflection();
    connection.setAutoCommit(false);
    ReflectionSql.deleteAllUsingReflection(controller);
  }

  @AfterEach
  public void after() throws SQLException {
    connection.rollback();
    connection.close();
  }

  @Test
  void testSavePerson() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
    Address address = getAddresses.getAddress();
    Person person = getPeople.getPerson(address);

    Person insertedPerson = ReflectionSql.savePersonUsingReflection(person, controller);

    try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM person")) {
      ResultSet resultSet = preparedStatement.executeQuery();
      int rowCount = 0;
      while (resultSet.next()) {
        assertEquals(insertedPerson.getFirstName(), resultSet.getString(3));
        assertEquals(insertedPerson.getLastName(), resultSet.getString(4));
        assertEquals(insertedPerson.getGender().toString().toUpperCase(), resultSet.getString(6).toUpperCase());
        rowCount++;
      }
      assertEquals(1, rowCount);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void testFindAllPersons() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
    Address address = getAddresses.getAddress();
    Person person = getPeople.getPerson(address);

    Person insertedPerson = ReflectionSql.savePersonUsingReflection(person, controller);

    List<Person> persons = ReflectionSql.findAllPersonsUsingReflection(controller);

    int listSize = persons.size();
    assertEquals(1, listSize);

    assertEquals(persons.get(0).getFirstName(), insertedPerson.getFirstName());
    assertEquals(persons.get(0).getLastName(), insertedPerson.getLastName());
    assertEquals(persons.get(0).getBirthdate(), insertedPerson.getBirthdate());
    assertEquals(persons.get(0).getGender(), insertedPerson.getGender());
    assertEquals(persons.get(0).getAddress().getStreet(), insertedPerson.getAddress().getStreet());
    assertEquals(persons.get(0).getAddress().getStreetNumber(), insertedPerson.getAddress().getStreetNumber());
    assertEquals(persons.get(0).getAddress().getZipCode(), insertedPerson.getAddress().getZipCode());
    assertEquals(persons.get(0).getAddress().getCity(), insertedPerson.getAddress().getCity());
    assertEquals(persons.get(0).getAddress().getCountry(), insertedPerson.getAddress().getCountry());
  }

  @Test
  void testDeleteAddressById() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
    Address address = new Address(null, "Schwarzenbach", "2178", 9200, "Gossau", "Schweiz");
    Person person = new Person(null, address, "Josia", "Schweizer", LocalDate.of(2007, 9, 18), Gender.MALE);

    ReflectionSql.savePersonUsingReflection(person, controller);
    Optional<Long> id = ReflectionSql.getPersonIdUsingReflection(person, controller);

    ReflectionSql.deleteByIdUsingReflection(id.get(), true, controller);

    Optional<Person> foundPerson = ReflectionSql.getPersonByIdUsingReflection(id.get(), controller);
    assertTrue(foundPerson.isEmpty());
  }

  @Test
  void testAssignAddressToTwoPersonsAndCanDelete() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
    Address address = new Address(null, "Schwarzenbach", "2178", 9200, "Gossau", "Schweiz");
    ReflectionSql.savePersonUsingReflection(new Person(null, address, "Josia", "Schweizer", LocalDate.of(2007, 9, 18), Gender.MALE), controller);
    ReflectionSql.savePersonUsingReflection(new Person(null, address, "Hans", "Müller", LocalDate.of(2007, 9, 18), Gender.MALE), controller);

    boolean sameAddress;
    long addressId1;
    long addressId2;
    try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT fk_address FROM person")) {
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        resultSet.next();
        addressId1 = resultSet.getLong("fk_address");
      }

      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        resultSet.next();
        addressId2 = resultSet.getLong("fk_address");
      }

      sameAddress = Objects.equals(addressId1, addressId2);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    assertTrue(sameAddress);
    assertFalse(ReflectionSql.canDeleteUsingRefletion(ReflectionSql.getAddressIdUsingReflection(address, controller).get(), controller));
  }

  @Test
  void testClearSchema() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
    Address address = new Address(null, "Schwarzenbach", "2178", 9200, "Gossau", "Schweiz");
    ReflectionSql.savePersonUsingReflection(new Person(null, address, "Josia", "Schweizer", LocalDate.of(2007, 9, 18), Gender.MALE), controller);
    ReflectionSql.savePersonUsingReflection(new Person(null, address, "Hans", "Müller", LocalDate.of(2007, 9, 18), Gender.MALE), controller);

    ReflectionSql.deleteAllUsingReflection(controller);

    try (PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT * FROM person"); PreparedStatement preparedStatement2 = connection.prepareStatement("SELECT * FROM address")) {
      ResultSet resultSet = preparedStatement1.executeQuery();
      assertFalse(resultSet.next());

      resultSet = preparedStatement2.executeQuery();
      assertFalse(resultSet.next());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void testGetConnection() throws IOException {
    try {
      connection = ReflectionSql.getConnectionUsingReflection();
      connection.setAutoCommit(false);
      assertNotNull(connection);
    } catch (Exception e) {
      throw new IOException();
    }
  }

  @Test
  void testUpdatePerson() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
    Address address = ReflectionSql.saveAddressUsingReflection(new Address(null, "Schwarzenbach", "2178", 9200, "Gossau", "Schweiz"), controller);
    Person person = ReflectionSql.savePersonUsingReflection(new Person(null, address, "Josia", "Schweizer", LocalDate.of(2007, 9, 18), Gender.MALE), controller);

    Address newAddress = new Address(null, "asdf", "asdf", 1234, "asdf", "asdf");
    Person newPerson = new Person(null, newAddress, "asdf", "asdf", LocalDate.of(2007, 3, 3), Gender.MALE);

    model.updatePerson(person.getId_person(), newPerson, controller);

    Optional<Person> editedPerson = ReflectionSql.getPersonByIdUsingReflection(person.getId_person(), controller);
    assertTrue(editedPerson.isPresent());
    assertEquals(newPerson.getFirstName(), editedPerson.get().getFirstName());
    assertEquals(newPerson.getLastName(), editedPerson.get().getLastName());
    assertEquals(newPerson.getBirthdate(), editedPerson.get().getBirthdate());
    assertEquals(newPerson.getGender(), editedPerson.get().getGender());
    assertEquals(newAddress.getStreet(), editedPerson.get().getAddress().getStreet());
  }

  @Test
  void testUpdate() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
    Address address1 = getAddresses.getAddress();
    Address address2 = getAddresses.getAddress2();
    Address address3 = getAddresses.getAddress3();

    Person person1 = getPeople.getPerson(address1);
    Person person2 = getPeople.getPerson2(address2);
    Person person3 = getPeople.getPerson3(address3);

    ReflectionSql.savePersonUsingReflection(person1, controller);
    Person insertedPerson2 = ReflectionSql.savePersonUsingReflection(person2, controller);
    Person insertedPerson3 = ReflectionSql.savePersonUsingReflection(person3, controller);

    List<Address> addresses = ReflectionSql.findAllAddressesUsingReflection(controller);
    List<Person> personList = ReflectionSql.findAllPersonsUsingReflection(controller);

    assertEquals(3, personList.size());
    assertEquals(3, addresses.size());

    Person newPerson2 = new Person(null, address1, "Josia2", "Schweizer", LocalDate.of(2007, 9, 18), Gender.MALE);
    model.updatePerson(insertedPerson2.getId_person(), newPerson2, controller);
    model.updatePerson(insertedPerson2.getId_person(), newPerson2, controller);

    Person newPerson3 = new Person(null, address1, "Josia3", "Schweizer", LocalDate.of(2007, 9, 18), Gender.MALE);
    model.updatePerson(insertedPerson3.getId_person(), newPerson3, controller);

    addresses = ReflectionSql.findAllAddressesUsingReflection(controller);
    personList = ReflectionSql.findAllPersonsUsingReflection(controller);

    assertEquals(3, personList.size());
    assertEquals(1, addresses.size());
  }

  @Test
  void testUpdateSamePerson() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
    Address address = getAddresses.getAddress();
    Person person = ReflectionSql.savePersonUsingReflection(getPeople.getPerson(address), controller);

    model.updatePerson(person.getId_person(), person, controller);
    assertEquals(person, model.findAllPerson(controller).get(0));
  }

  @Test
  void testDeleteAll() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
    Address address = getAddresses.getAddress();
    Address address2 = getAddresses.getAddress2();
    ReflectionSql.savePersonUsingReflection(getPeople.getPerson(address), controller);
    ReflectionSql.savePersonUsingReflection(getPeople.getPerson2(address2), controller);

    model.deleteAll(controller);
    assertTrue(model.findAllPerson(controller).isEmpty());
  }
}