/*
 * TestAddress.java
 *
 * Creator:
 * 02.05.2024 16:28 josia.schweizer
 *
 * Maintainer:
 * 02.05.2024 16:28 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.jpa;

import ch.abacus.controller.Controller;
import ch.abacus.controller.ControllerImpl;
import ch.abacus.components.GetAddresses;
import ch.abacus.components.GetPeople;
import ch.abacus.db.entity.Address;
import ch.abacus.db.components.EntitymanagerUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityTransaction;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestAddress {

  private final Controller controller = new ControllerImpl();
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
  void testSaveAddress() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
    Address address = getAddresses.getAddress();

    ReflectionJpa.saveAddressUsingReflection(address, controller);
    List<Address> addresses = ReflectionJpa.findAllAddressesUsingReflection(controller);

    assertEquals(1, addresses.size());
    assertEquals(address, addresses.get(0));
  }

  @Test
  void testFindAllAddresses() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
    Address address = getAddresses.getAddress();
    ReflectionJpa.saveAddressUsingReflection(address, controller);
    List<Address> addresses = ReflectionJpa.findAllAddressesUsingReflection(controller);

    int listSize = addresses.size();
    assertEquals(1, listSize);

    assertEquals(addresses.get(0).getStreet(), address.getStreet());
    assertEquals(addresses.get(0).getStreetNumber(), address.getStreetNumber());
    assertEquals(addresses.get(0).getZipCode(), address.getZipCode());
    assertEquals(addresses.get(0).getCity(), address.getCity());
    assertEquals(addresses.get(0).getCountry(), address.getCountry());
  }

  @Test
  void testFindById() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
    Address address = getAddresses.getAddress();
    Address savedAddress = ReflectionJpa.saveAddressUsingReflection(address, controller);
    Long id = savedAddress.getId_address();

    Optional<Address> foundAddress = ReflectionJpa.getAddressByIdUsingReflection(id, controller);

    assertTrue(foundAddress.isPresent());
    assertEquals(address.getStreet(), foundAddress.get().getStreet());
    assertEquals(address.getStreetNumber(), foundAddress.get().getStreetNumber());
    assertEquals(address.getZipCode(), foundAddress.get().getZipCode());
    assertEquals(address.getCity(), foundAddress.get().getCity());
    assertEquals(address.getCountry(), foundAddress.get().getCountry());
  }

  @Test
  void testGetAddressId() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
    Address address = getAddresses.getAddress();
    ReflectionJpa.saveAddressUsingReflection(address, controller);
    List<Address> addresses = ReflectionJpa.findAllAddressesUsingReflection(controller);
    assertEquals(addresses.get(0).getId_address(), ReflectionJpa.getAddressIdUsingReflection(address, controller).get());
  }

  @Test
  void testGetAllAddresses() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
    Address address = getAddresses.getAddress();
    ReflectionJpa.saveAddressUsingReflection(address, controller);
    Address anotherAddress = getAddresses.getAddress2();
    ReflectionJpa.saveAddressUsingReflection(anotherAddress, controller);
    List<Address> addresses = ReflectionJpa.findAllAddressesUsingReflection(controller);
    assertEquals(2, addresses.size());
    assertEquals(address, addresses.get(0));
    assertEquals(anotherAddress, addresses.get(1));
  }

  @Test
  void testDeleteAddressById() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
    Address address = getAddresses.getAddress();

    ReflectionJpa.saveAddressUsingReflection(address, controller);
    Long id = ReflectionJpa.getAddressIdUsingReflection(address, controller).get();

    ReflectionJpa.deleteByIdUsingRefelection(id, false, controller);

    Optional<Address> foundAddress = ReflectionJpa.getAddressByIdUsingReflection(id, controller);
    assertTrue(foundAddress.isEmpty());
  }

  @Test
  void testEditAddress() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
    Address address = ReflectionJpa.saveAddressUsingReflection(getAddresses.getAddress(), controller);
    Address newAddress = getAddresses.getAddress();

    ReflectionJpa.editAddressUsingRefelection(address.getId_address(), newAddress, controller);

    Optional<Address> editedAddress = ReflectionJpa.getAddressByIdUsingReflection(address.getId_address(), controller);
    assertTrue(editedAddress.isPresent());
    assertEquals(newAddress.getStreet(), editedAddress.get().getStreet());
    assertEquals(newAddress.getStreetNumber(), editedAddress.get().getStreetNumber());
    assertEquals(newAddress.getZipCode(), editedAddress.get().getZipCode());
    assertEquals(newAddress.getCity(), editedAddress.get().getCity());
    assertEquals(newAddress.getCountry(), editedAddress.get().getCountry());
  }

  @Test
  void testAddressAlreadyRegistered() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
    Address address = getAddresses.getAddress();
    Optional<Address> addressAlreadyRegistered = ReflectionJpa.addressAlreadyRegisteredUsingReflection(address, controller);
    assertTrue(addressAlreadyRegistered.isEmpty());

    ReflectionJpa.saveAddressUsingReflection(address, controller);
    addressAlreadyRegistered = ReflectionJpa.addressAlreadyRegisteredUsingReflection(address, controller);
    assertTrue(addressAlreadyRegistered.isPresent());
  }

  @Test
  void testPersonCountByAddressId() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
    Address insertedAddress = ReflectionJpa.saveAddressUsingReflection(getAddresses.getAddress(), controller);
    ReflectionJpa.savePersonUsingReflection(getPeople.getPerson(insertedAddress), controller);

    Optional<Long> addressId = ReflectionJpa.getAddressIdUsingReflection(insertedAddress, controller);
    int count = ReflectionJpa.getPersonCountByAddressIdUsingReflection(addressId.get(), controller);

    assertEquals(1, count);

    ReflectionJpa.savePersonUsingReflection(getPeople.getPerson2(insertedAddress), controller);
    count = ReflectionJpa.getPersonCountByAddressIdUsingReflection(addressId.get(), controller);

    assertEquals(2, count);
  }

}