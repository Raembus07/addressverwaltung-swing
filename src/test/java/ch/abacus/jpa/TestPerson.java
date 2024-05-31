/*
 * TestJpa.java
 *
 * Creator:
 * 02.05.2024 16:02 josia.schweizer
 *
 * Maintainer:
 * 02.05.2024 16:02 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.jpa;

import ch.abacus.controller.Controller;
import ch.abacus.controller.ControllerImpl;
import ch.abacus.db.components.EntitymanagerUtil;
import ch.abacus.components.GetAddresses;
import ch.abacus.components.GetPeople;
import ch.abacus.db.JPAModelImpl;
import ch.abacus.db.Model;
import ch.abacus.db.entity.Address;
import ch.abacus.db.entity.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityTransaction;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestPerson {

  private final Controller controller = new ControllerImpl();
  private final Model jpaModel = new JPAModelImpl();
  private final GetPeople getPeople = new GetPeople();
  private final GetAddresses getAddresses = new GetAddresses();
  private EntityTransaction transaction = null;

  @BeforeEach
  void before() throws Throwable {
    EntitymanagerUtil entitymanagerUtil = new EntitymanagerUtil() {
      @Override
      public EntityTransaction begin() {
        if (transaction == null) {
          transaction = super.begin();
          transaction.setRollbackOnly();
        }
        return transaction;
      }
    };
    EntitymanagerUtil.setInstance(entitymanagerUtil);
    EntitymanagerUtil.getInstance().clearDatabase();
  }

  @AfterEach
  public void after() {
    if (transaction != null) {
      transaction.rollback();
      transaction = null;
    }
  }

  @Test
  void testSavePerson() throws Throwable {
    Person insertedPerson = jpaModel.savePerson(getPeople.getPerson(getAddresses.getAddress()), controller);

    List<Person> people = jpaModel.findAllPerson(controller);

    assertEquals(1, people.size());
    assertEquals(insertedPerson, people.get(0));
  }

  @Test
  void testSave2Person() throws Throwable {
    Person insertedPerson = jpaModel.savePerson(getPeople.getPerson(getAddresses.getAddress()), controller);
    Person insertedPerson2 = jpaModel.savePerson(getPeople.getPerson2(getAddresses.getAddress2()), controller);
    List<Person> people = jpaModel.findAllPerson(controller);

    assertEquals(2, people.size());
    assertEquals(insertedPerson, people.get(0));
    assertEquals(insertedPerson2, people.get(1));
  }

  @Test
  void testAssignAddressToTwoPersonsAndCanDelete() throws Throwable {
    Address address = getAddresses.getAddress();

    Person person1 = getPeople.getPerson(address);
    Person person2 = getPeople.getPerson(address);

    jpaModel.savePerson(person1, controller);
    jpaModel.savePerson(person2, controller);

    assertEquals(person1.getAddress().getId_address(), person2.getAddress().getId_address());

    assertFalse(ReflectionJpa.canDeleteUsingReflection(person1.getAddress().getId_address(), controller));
  }

  @Test
  void testUpdatePerson() throws Throwable {
    Address address = ReflectionJpa.saveAddressUsingReflection(getAddresses.getAddress(), controller);
    Person person = jpaModel.savePerson(getPeople.getPerson(address), controller);

    Address newAddress = getAddresses.getAddress2();
    Person newPerson = getPeople.getPerson2(newAddress);

    jpaModel.updatePerson(person.getId_person(), newPerson, controller);

    Optional<Person> editedPerson = jpaModel.getPersonById(person.getId_person(), controller);
    assertTrue(editedPerson.isPresent());
    assertEquals(newPerson.getFirstName(), editedPerson.get().getFirstName());
    assertEquals(newPerson.getLastName(), editedPerson.get().getLastName());
    assertEquals(newPerson.getBirthdate(), editedPerson.get().getBirthdate());
    assertEquals(newPerson.getGender(), editedPerson.get().getGender());
    assertEquals(newAddress.getStreet(), editedPerson.get().getAddress().getStreet());
  }

  @Test
  void testUpdatePerson2() throws Throwable {
    Address address = ReflectionJpa.saveAddressUsingReflection(getAddresses.getAddress(), controller);
    Address secondAddress = ReflectionJpa.saveAddressUsingReflection(getAddresses.getAddress2(), controller);
    Person person = jpaModel.savePerson(getPeople.getPerson(address), controller);
    jpaModel.savePerson(getPeople.getPerson(secondAddress), controller);

    Address newAddress = getAddresses.getAddress3();
    Person newPerson = getPeople.getPerson3(newAddress);

    jpaModel.updatePerson(person.getId_person(), newPerson, controller);

    Optional<Person> editedPerson = jpaModel.getPersonById(person.getId_person(), controller);
    assertTrue(editedPerson.isPresent());
    assertEquals(newPerson.getFirstName(), editedPerson.get().getFirstName());
    assertEquals(newPerson.getLastName(), editedPerson.get().getLastName());
    assertEquals(newPerson.getBirthdate(), editedPerson.get().getBirthdate());
    assertEquals(newPerson.getGender(), editedPerson.get().getGender());
    assertEquals(newAddress.getStreet(), editedPerson.get().getAddress().getStreet());
    assertEquals(newAddress.getStreetNumber(), editedPerson.get().getAddress().getStreetNumber());
    assertEquals(newAddress.getZipCode(), editedPerson.get().getAddress().getZipCode());
    assertEquals(newAddress.getCity(), editedPerson.get().getAddress().getCity());
    assertEquals(newAddress.getCountry(), editedPerson.get().getAddress().getCountry());
  }

  @Test
  void testUpdatePersonWithDoubleUsedAddress() throws Throwable {
    Address address = ReflectionJpa.saveAddressUsingReflection(getAddresses.getAddress(), controller);
    Person person = jpaModel.savePerson(getPeople.getPerson(address), controller);
    Person secondPerson = jpaModel.savePerson(getPeople.getPerson2(address), controller);

    Person newPerson = getPeople.getPerson3(address);

    Person editedPerson = jpaModel.updatePerson(person.getId_person(), newPerson, controller);

    assertEquals(newPerson.getFirstName(), editedPerson.getFirstName());
    assertEquals(newPerson.getLastName(), editedPerson.getLastName());
    assertEquals(newPerson.getBirthdate(), editedPerson.getBirthdate());
    assertEquals(newPerson.getGender(), editedPerson.getGender());
    assertEquals(address.getStreet(), editedPerson.getAddress().getStreet());
    assertEquals(address.getStreetNumber(), editedPerson.getAddress().getStreetNumber());
    assertEquals(address.getZipCode(), editedPerson.getAddress().getZipCode());
    assertEquals(address.getCity(), editedPerson.getAddress().getCity());
    assertEquals(address.getCountry(), editedPerson.getAddress().getCountry());
    assertEquals(2, jpaModel.findAllPerson(controller).size());
    assertEquals(1, ReflectionJpa.findAllAddressesUsingReflection(controller).size());

    Address address3 = getAddresses.getAddress3();
    newPerson = getPeople.getPerson3(address3);

    editedPerson = jpaModel.updatePerson(secondPerson.getId_person(), newPerson, controller);

    assertEquals(newPerson.getFirstName(), editedPerson.getFirstName());
    assertEquals(newPerson.getLastName(), editedPerson.getLastName());
    assertEquals(newPerson.getBirthdate(), editedPerson.getBirthdate());
    assertEquals(newPerson.getGender(), editedPerson.getGender());
    assertEquals(address3.getStreet(), editedPerson.getAddress().getStreet());
    assertEquals(address3.getStreetNumber(), editedPerson.getAddress().getStreetNumber());
    assertEquals(address3.getZipCode(), editedPerson.getAddress().getZipCode());
    assertEquals(address3.getCity(), editedPerson.getAddress().getCity());
    assertEquals(address3.getCountry(), editedPerson.getAddress().getCountry());
    assertEquals(2, jpaModel.findAllPerson(controller).size());
    assertEquals(2, ReflectionJpa.findAllAddressesUsingReflection(controller).size());
  }

  @Test
  void testUpdateSamePersonWithDifferentAddresses() throws Throwable {
    Address address = ReflectionJpa.saveAddressUsingReflection(getAddresses.getAddress(), controller);
    Address secondAddress = ReflectionJpa.saveAddressUsingReflection(getAddresses.getAddress2(), controller);
    Address thirdAddress = ReflectionJpa.saveAddressUsingReflection(getAddresses.getAddress3(), controller);
    Person person = jpaModel.savePerson(getPeople.getPerson(address), controller);
    jpaModel.savePerson(getPeople.getPerson(secondAddress), controller);
    jpaModel.savePerson(getPeople.getPerson(thirdAddress), controller);

    Person newPerson = getPeople.getPerson2(address);

    jpaModel.updatePerson(person.getId_person(), newPerson, controller);

    Optional<Person> editedPerson = jpaModel.getPersonById(person.getId_person(), controller);
    assertTrue(editedPerson.isPresent());
    assertEquals(newPerson.getFirstName(), editedPerson.get().getFirstName());
    assertEquals(newPerson.getLastName(), editedPerson.get().getLastName());
    assertEquals(newPerson.getBirthdate(), editedPerson.get().getBirthdate());
    assertEquals(newPerson.getGender(), editedPerson.get().getGender());
    assertEquals(address.getStreet(), editedPerson.get().getAddress().getStreet());
    assertEquals(address.getStreetNumber(), editedPerson.get().getAddress().getStreetNumber());
    assertEquals(address.getZipCode(), editedPerson.get().getAddress().getZipCode());
    assertEquals(address.getCity(), editedPerson.get().getAddress().getCity());
    assertEquals(address.getCountry(), editedPerson.get().getAddress().getCountry());
    assertEquals(1, jpaModel.findAllPerson(controller).size());
  }

  @Test
  void testDeletePerson() throws Throwable {
    Address address = ReflectionJpa.saveAddressUsingReflection(getAddresses.getAddress(), controller);
    Person person = jpaModel.savePerson(getPeople.getPerson(address), controller);

    jpaModel.delete(person.getId_person(), controller);

    Optional<Person> foundPerson = jpaModel.getPersonById(person.getId_person(), controller);
    assertTrue(foundPerson.isEmpty());
  }

  @Test
  void testDeleteById() throws Throwable {
    Address address = ReflectionJpa.saveAddressUsingReflection(getAddresses.getAddress(), controller);
    Person person = jpaModel.savePerson(getPeople.getPerson(address), controller);

    ReflectionJpa.deleteByIdUsingRefelection(person.getId_person(), true, controller);

    Optional<Person> foundPerson = jpaModel.getPersonById(person.getId_person(), controller);
    assertTrue(foundPerson.isEmpty());
  }

  @Test
  void testGetPersonById() throws Throwable {
    Address address = ReflectionJpa.saveAddressUsingReflection(getAddresses.getAddress(), controller);
    Person person = jpaModel.savePerson(getPeople.getPerson(address), controller);

    Optional<Person> foundPerson = jpaModel.getPersonById(person.getId_person(), controller);
    assertTrue(foundPerson.isPresent());
    assertEquals(person, foundPerson.get());
  }

  @Test
  void testSamePerson() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
    Address address = ReflectionJpa.saveAddressUsingReflection(getAddresses.getAddress(), controller);
    Person person = ReflectionJpa.savePersonUsingReflection(getPeople.getPerson(address), controller);

    Optional<Person> samePerson = ReflectionJpa.samePersonUsingReflection(person.getId_person(), person, controller);
    assertTrue(samePerson.isPresent());
    assertEquals(person, samePerson.get());
  }

  @Test
  void testDeleteAll() throws Throwable {
    Address address = getAddresses.getAddress();
    Address address2 = getAddresses.getAddress2();
    ReflectionJpa.savePersonUsingReflection(getPeople.getPerson(address), controller);
    ReflectionJpa.savePersonUsingReflection(getPeople.getPerson2(address2), controller);

    ReflectionJpa.deleteAllUsingReflection(controller);
    assertTrue(jpaModel.findAllPerson(controller).isEmpty());
  }
}