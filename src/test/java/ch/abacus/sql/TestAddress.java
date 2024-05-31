/*
 * TestAddress.java
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

import ch.abacus.controller.Controller;
import ch.abacus.controller.ControllerImpl;
import ch.abacus.db.Model;
import ch.abacus.db.SQLModelImpl;
import ch.abacus.db.entity.Address;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestAddress {

  private Connection connection;
  private final Controller controller = new ControllerImpl();

  @BeforeEach
  public void before() throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    Model model = new SQLModelImpl(controller) {
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
  void testSaveAddress() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    String street = "Schwarzenbach";
    String streetNumber = "2178";
    int zipCode = 9200;
    String city = "Gossau";
    String country = "Schweiz";

    Address address = new Address(null, street, streetNumber, zipCode, city, country);

    ReflectionSql.saveAddressUsingReflection(address, controller);

    try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM address")) {
      ResultSet resultSet = preparedStatement.executeQuery();
      int rowCount = 0;
      while (resultSet.next()) {
        assertEquals(street, resultSet.getString(2));
        assertEquals(streetNumber, resultSet.getString(3));
        assertEquals(city, resultSet.getString(5));
        assertEquals(country, resultSet.getString(6));

        rowCount++;
      }

      assertEquals(1, rowCount);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void testFindAllAddresses() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    String street = "Schwarzenbach";
    String streetNumber = "2178";
    int zipCode = 9200;
    String city = "Gossau";
    String country = "Schweiz";

    Address address = new Address(null, street, streetNumber, zipCode, city, country);

    ReflectionSql.saveAddressUsingReflection(address, controller);

    List<Address> addresses = ReflectionSql.findAllAddressesUsingReflection(controller);

    int listSize = addresses.size();
    assertEquals(1, listSize);

    assertEquals(addresses.get(0).getStreet(), street);
    assertEquals(addresses.get(0).getStreetNumber(), streetNumber);
    assertEquals(addresses.get(0).getZipCode(), zipCode);
    assertEquals(addresses.get(0).getCity(), city);
    assertEquals(addresses.get(0).getCountry(), country);
  }

  @Test
  void testFindById() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Address address = ReflectionSql.saveAddressUsingReflection(new Address(null, "TestStreet", "123", 12345, "TestCity", "TestCountry"), controller);
    Long id = address.getId_address();

    Optional<Address> foundAddress = ReflectionSql.getAddressByIdUsingReflection(id, controller);

    assertTrue(foundAddress.isPresent());
    assertEquals("TestStreet", foundAddress.get().getStreet());
  }
}