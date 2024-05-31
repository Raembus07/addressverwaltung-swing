/*
 * ModelImpl.java
 *
 * Creator:
 * 03.04.2024 13:42 josia.schweizer
 *
 * Maintainer:
 * 03.04.2024 13:42 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.db;

import ch.abacus.common.DbConst;
import ch.abacus.common.SQLStatments;
import ch.abacus.common.State;
import ch.abacus.controller.Controller;
import ch.abacus.db.entity.Address;
import ch.abacus.db.entity.Gender;
import ch.abacus.db.entity.Person;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static ch.abacus.db.entity.Gender.FEMALE;
import static ch.abacus.db.entity.Gender.MALE;

public class SQLModelImpl implements Model {

  private final Controller controller;
  private Connection connection;

  public SQLModelImpl(Controller controller) {
    this.controller = controller;
    connection = getConnection();
  }

  public void commit() {
    try {
      connection.commit();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Person savePerson(Person person, Consumer<Throwable> consumer) {
    Optional<Long> existingPersonId = getPersonId(person, consumer);
    Optional<Long> existingAddressId = getAddressId(person.getAddress(), consumer);

    if (existingPersonId.isEmpty() && existingAddressId.isPresent()) {
      try (PreparedStatement preparedStatement = connection.prepareStatement(
          SQLStatments.SAVE_PERSON, Statement.RETURN_GENERATED_KEYS)) {
        connection.setAutoCommit(false);
        preparedStatement.setLong(1, existingAddressId.get());
        preparedStatement.setString(2, person.getFirstName());
        preparedStatement.setString(3, person.getLastName());
        preparedStatement.setDate(4, Date.valueOf(person.getBirthdate()));
        preparedStatement.setString(5, String.valueOf(person.getGender()));
        preparedStatement.executeUpdate();

        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        if (generatedKeys.next()) {
          Long id = generatedKeys.getLong(1);

          commit();
          return new Person(id, person.getAddress(), person.getFirstName(), person.getLastName(),
                            person.getBirthdate(), person.getGender());
        }
      } catch (SQLException e) {
        consumer.accept(e);
        return null;
      }
    }

    // Neither person nor address exists || Person exists but address doesn't exist
    if ((existingPersonId.isEmpty() && existingAddressId.isEmpty()) ||
        (existingPersonId.isPresent() && existingAddressId.isEmpty())) {
      Address savedAddress = saveAddress(person.getAddress(), consumer);
      if (savedAddress != null) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
            SQLStatments.SAVE_PERSON, Statement.RETURN_GENERATED_KEYS)) {
          connection.setAutoCommit(false);
          preparedStatement.setLong(1, savedAddress.getId_address());
          preparedStatement.setString(2, person.getFirstName());
          preparedStatement.setString(3, person.getLastName());
          preparedStatement.setDate(4, Date.valueOf(person.getBirthdate()));
          preparedStatement.setString(5, String.valueOf(person.getGender()));
          preparedStatement.executeUpdate();

          ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
          if (generatedKeys.next()) {
            Long id = generatedKeys.getLong(1);

            commit();
            return new Person(id, savedAddress, person.getFirstName(), person.getLastName(),
                              person.getBirthdate(), person.getGender());
          }
        } catch (SQLException e) {
          consumer.accept(e);
        }
      }
    }

    // Person and address exists
    if (existingPersonId.isPresent() && existingAddressId.isPresent()) {
      controller.addState(State.UNAVAILABLETOSAVE);
      return getPersonById(existingPersonId.get(), consumer).orElse(null);
    }

    return person;
  }

  private Address saveAddress(Address address, Consumer<Throwable> consumer) {
    try {
      Optional<Long> existingAddressId = getAddressId(address, consumer);
      if (existingAddressId.isPresent()) {
        return getAddressById(existingAddressId.get(), consumer).orElse(null);
      } else {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
            SQLStatments.SAVE_ADDRESS, Statement.RETURN_GENERATED_KEYS)) {
          connection.setAutoCommit(false);
          preparedStatement.setString(1, address.getStreet());
          preparedStatement.setString(2, address.getStreetNumber());
          preparedStatement.setInt(3, address.getZipCode());
          preparedStatement.setString(4, address.getCity());
          preparedStatement.setString(5, address.getCountry());
          preparedStatement.executeUpdate();

          ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
          if (generatedKeys.next()) {
            Long id = generatedKeys.getLong(1);
            commit();
            return new Address(id, address.getStreet(), address.getStreetNumber(),
                               address.getZipCode(), address.getCity(), address.getCountry());
          } else {
            connection.rollback();
            return null;
          }
        } catch (SQLException e) {
          connection.rollback();
          consumer.accept(e);
          return null;
        }
      }
    } catch (SQLException e) {
      try {
        connection.rollback();
      } catch (SQLException ex) {
        consumer.accept(ex);
      }
      consumer.accept(e);
    }
    return null;
  }

  @Override
  public Person updatePerson(Long id, Person person, Consumer<Throwable> consumer) {
    Optional<Person> p = samePerson(id, person, consumer);
    if (p.isPresent()) { //Überprüfung, ob eine genau gleiche Adresse noch einmal hinzugefügt werden möchte
      return p.get();
    }

    Address oldAddress = getPersonById(id, consumer).map(Person::getAddress).orElseThrow(() -> new IllegalArgumentException(DbConst.NOPERSONFOUND + id));

    Optional<Address> newAddress;
    boolean addressAlreadyExists;
    boolean sameAddress = false; //sameAddress means that it's the same address as before at the person which gets updated -> the address don't has to be touched

    newAddress = addressAlreadyRegistered(person.getAddress(), consumer);
    addressAlreadyExists = newAddress.isPresent();
    if (!addressAlreadyExists) {
      newAddress = Optional.ofNullable(person.getAddress());
    } else if (sameAddress(oldAddress, newAddress.get())) {
      sameAddress = true;
    }

    int personCount = getPersonCountByAddressId(oldAddress.getId_address(), consumer);
    if (personCount > 1 && newAddress.isPresent() && !sameAddress) {
      newAddress = Optional.ofNullable(saveAddress(newAddress.get(), consumer)); //wenn die address noch benutzt wird, wird eine neue erstellt
    } else if (newAddress.isPresent() && !addressAlreadyExists) {
      newAddress = editAddress(oldAddress.getId_address(), newAddress.get(), consumer); //wenn die address sonst nicht mehr benutzt wird, wird sie einfach bearbeitet
    }
    if (newAddress.isPresent()) {
      person = new Person(id, newAddress.get(), person.getFirstName(), person.getLastName(), person.getBirthdate(), person.getGender());
      editPerson(id, person, consumer);
    }

    deleteUnsedAddress(consumer);

    return person;
  }

  @Override
  public Optional<Person> getPersonById(Long id, Consumer<Throwable> consumer) {
    if (id != null) {
      return findPersonByID(id, consumer);
    } else {
      return Optional.empty();
    }
  }

  @Override
  public void delete(Long personId, Consumer<Throwable> consumer) {
    Person personToDelete = getPersonById(personId, consumer).get();
    Long addressId = personToDelete.getAddress().getId_address();
    deleteById(personId, true, consumer);
    if (canDelete(addressId, consumer)) {
      deleteById(addressId, false, consumer);
    }
  }

  @Override
  public List<Person> findAllPerson(Consumer<Throwable> consumer) {
    return findAllPerson(SQLStatments.SELECT_ALL_PERSONS, consumer);
  }

  private Optional<Long> getPersonId(Person person, Consumer<Throwable> consumer) {
    try {
      try (PreparedStatement preparedStatement = connection.prepareStatement(SQLStatments.SELECT_ID_BY_PERSON)) {
        preparedStatement.setString(1, person.getFirstName());
        preparedStatement.setString(2, person.getLastName());
        preparedStatement.setDate(3, Date.valueOf(person.getBirthdate()));
        preparedStatement.setString(4, String.valueOf(person.getGender()));
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
          return Optional.of(resultSet.getLong(DbConst.IDPERSON));
        }
      }
    } catch (SQLException e) {
      consumer.accept(e);
    }
    return Optional.empty();
  }

  private Optional<Long> getAddressId(Address address, Consumer<Throwable> consumer) {
    try {
      try (PreparedStatement preparedStatement = connection.prepareStatement(SQLStatments.SELECT_ID_FROM_ADDRESS)) {
        preparedStatement.setString(1, address.getStreet());
        preparedStatement.setString(2, address.getStreetNumber());
        preparedStatement.setInt(3, address.getZipCode());
        preparedStatement.setString(4, address.getCity());
        preparedStatement.setString(5, address.getCountry());

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
          return Optional.of(resultSet.getLong(DbConst.IDADDRESS));
        }
      }
    } catch (SQLException e) {
      consumer.accept(e);
    }
    return Optional.empty();
  }

  private Gender stringToGender(String stringGender) {
    if (DbConst.MALE.equals(stringGender)) {
      return MALE;
    } else {
      return FEMALE;
    }
  }

  private List<Address> findAllAddresses(Consumer<Throwable> consumer) {
    return findAllAddress(consumer);
  }

  private Optional<Address> getAddressById(Long id, Consumer<Throwable> consumer) {
    return findAddressById(id, consumer);
  }

  private List<Person> findAllPerson(String query, Consumer<Throwable> consumer) {
    List<Person> persons = new LinkedList<>();
    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        long id = resultSet.getLong(DbConst.IDPERSON);
        Optional<Person> person = createPersonFromResultSet(resultSet, id, consumer);
        person.ifPresent(persons::add);
      }
    } catch (SQLException e) {
      consumer.accept(e);
    }
    return persons;
  }

  private Optional<Person> findPersonByID(Long id, Consumer<Throwable> consumer) {
    try (PreparedStatement preparedStatement = connection.prepareStatement(SQLStatments.SELECT_PERSON_BY_ID)) {
      preparedStatement.setLong(1, id);
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        return createPersonFromResultSet(resultSet, id, consumer);
      }
    } catch (SQLException e) {
      consumer.accept(e);
    }
    return Optional.empty();
  }

  private Optional<Address> findAddressById(Long id, Consumer<Throwable> consumer) {
    try (PreparedStatement preparedStatement = connection.prepareStatement(SQLStatments.SELECT_ADDRESS_BY_ID)) {
      preparedStatement.setLong(1, id);
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        return createAddressFromResultSet(resultSet, id);
      }
    } catch (SQLException e) {
      consumer.accept(e);
    }
    return Optional.empty();
  }

  private Optional<Person> createPersonFromResultSet(ResultSet resultSet, Long id, Consumer<Throwable> consumer) throws SQLException {
    long addressId = resultSet.getLong(DbConst.FKADDRESS);
    String firstName = resultSet.getString(DbConst.FIRSTNAME);
    String lastName = resultSet.getString(DbConst.LASTNAME);
    Date birthDate = resultSet.getDate(DbConst.BIRTHDATE);
    Gender gender = stringToGender(resultSet.getString(DbConst.GENDER));

    Optional<Address> address = getAddressById(addressId, consumer);

    return address.map(value -> new Person(id, value, firstName, lastName, birthDate.toLocalDate(), gender));
  }

  private Optional<Address> createAddressFromResultSet(ResultSet resultSet, Long id) throws SQLException {
    String street = resultSet.getString(DbConst.STREET);
    String streetnumber = resultSet.getString(DbConst.STREETNUMBER);
    int zipcode = resultSet.getInt(DbConst.POSTALCODE);
    String city = resultSet.getString(DbConst.CITY);
    String country = resultSet.getString(DbConst.COUNTRY);

    return Optional.of(new Address(id, street, streetnumber, zipcode, city, country));
  }

  private List<Address> findAllAddress(Consumer<Throwable> consumer) {
    List<Address> addresses = new LinkedList<>();

    try (PreparedStatement preparedStatement = connection.prepareStatement(SQLStatments.SELECT_ALL_ADDRESS)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        long id = resultSet.getLong(DbConst.IDADDRESS);
        String street = resultSet.getString(DbConst.STREET);
        String streetNumber = resultSet.getString(DbConst.STREETNUMBER);
        int zipCode = Integer.parseInt(resultSet.getString(DbConst.POSTALCODE));
        String city = resultSet.getString(DbConst.CITY);
        String country = resultSet.getString(DbConst.COUNTRY);

        addresses.add(new Address(id, street, streetNumber, zipCode, city, country));
      }
    } catch (SQLException e) {
      consumer.accept(e);
    }
    return addresses;
  }

  private Optional<Person> samePerson(Long id, Person person, Consumer<Throwable> consumer) {
    findAllPerson(consumer);
    Optional<Person> oldPerson = getPersonById(id, consumer);
    if (oldPerson.isPresent() && (oldPerson.get().getFirstName().equals(person.getFirstName())
                                  && oldPerson.get().getLastName().equals(person.getLastName())
                                  && oldPerson.get().getBirthdate().equals(person.getBirthdate())
                                  && oldPerson.get().getGender().equals(person.getGender()))) {
      Address oldAddress = oldPerson.get().getAddress();
      if (oldAddress.getStreet().equals(person.getAddress().getStreet())
          && oldAddress.getStreetNumber().equals(person.getAddress().getStreetNumber())
          && oldAddress.getZipCode() == person.getAddress().getZipCode()
          && oldAddress.getCity().equals(person.getAddress().getCity())
          && oldAddress.getCountry().equals(person.getAddress().getCountry())) {
        return oldPerson;
      }
    }
    return Optional.empty();
  }

  private boolean sameAddress(Address oldAddress, Address newAddress) {
    return oldAddress.getStreet().equals(newAddress.getStreet()) && oldAddress.getStreetNumber().equals(
        newAddress.getStreetNumber()) && oldAddress.getZipCode() == newAddress.getZipCode() &&
           oldAddress.getCity().equals(newAddress.getCity()) && oldAddress.getCountry().equals(newAddress.getCountry());
  }

  private void editPerson(Long id, Person person, Consumer<Throwable> consumer) {
    try (PreparedStatement preparedStatement = connection.prepareStatement(SQLStatments.UPDATE_PERSON)) {
      preparedStatement.setLong(1, person.getAddress().getId_address());
      preparedStatement.setString(2, person.getFirstName());
      preparedStatement.setString(3, person.getLastName());
      preparedStatement.setDate(4, Date.valueOf(person.getBirthdate()));
      preparedStatement.setString(5, person.getGender().toString());
      preparedStatement.setLong(6, id);

      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      consumer.accept(e);
    }
  }

  private Optional<Address> editAddress(Long id, Address address, Consumer<Throwable> consumer) {
    try {
      Optional<Address> existingAddress = getAddressById(id, consumer);
      if (existingAddress.isEmpty()) {
        return Optional.empty();
      }

      try (PreparedStatement preparedStatement = connection.prepareStatement(SQLStatments.UPDATE_ADDRESS)) {
        preparedStatement.setString(1, address.getStreet());
        preparedStatement.setString(2, address.getStreetNumber());
        preparedStatement.setInt(3, address.getZipCode());
        preparedStatement.setString(4, address.getCity());
        preparedStatement.setString(5, address.getCountry());
        preparedStatement.setLong(6, id);

        preparedStatement.executeUpdate();
      }
    } catch (SQLException e) {
      consumer.accept(e);
    }
    Optional<Long> tempAddress = getAddressId(address, consumer);
    if (tempAddress.isPresent()) {
      return getAddressById(tempAddress.get(), consumer);
    } else {
      return Optional.empty();
    }
  }

  private Optional<Address> addressAlreadyRegistered(Address address, Consumer<Throwable> consumer) {
    return findAllAddresses(consumer).stream()
        .filter(ad -> ad.getStreet().equals(address.getStreet())
                      && ad.getStreetNumber().equals(address.getStreetNumber())
                      && ad.getZipCode() == address.getZipCode()
                      && ad.getCity().equals(address.getCity())
                      && ad.getCountry().equals(address.getCountry()))
        .findFirst();
  }

  private void deleteUnsedAddress(Consumer<Throwable> consumer) {
    findAllAddresses(consumer).stream()
        .filter(address -> getPersonCountByAddressId(address.getId_address(), consumer) == 0)
        .forEach(address -> deleteById(address.getId_address(), false, consumer));
  }

  private int getPersonCountByAddressId(Long addressId, Consumer<Throwable> consumer) {
    try (PreparedStatement preparedStatement = connection.prepareStatement(SQLStatments.USAGECOUNT_FROM_PERSON)) {
      preparedStatement.setLong(1, addressId);
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        return resultSet.getInt(DbConst.COUNT);
      }
    } catch (SQLException e) {
      consumer.accept(e);
    }
    return 0;
  }

  private void deleteById(Long id, boolean isPerson, Consumer<Throwable> consumer) {
    if (isValidId(id, consumer)) {
      if (isPerson) {
        deleteSqlCommand(id, SQLStatments.DELETE_PERSON_BY_ID, consumer);
      } else {
        deleteSqlCommand(id, SQLStatments.DELETE_ADDRESS_BY_ID, consumer);
      }
    }
  }

  public void deleteAll(Consumer<Throwable> consumer) {
    try (PreparedStatement preparedStatement1 = connection.prepareStatement(SQLStatments.DELETE_ALL_PERSONS); PreparedStatement preparedStatement2 = connection.prepareStatement(SQLStatments.DELETE_ALL_ADDRESSES)) {
      preparedStatement1.executeUpdate();
      preparedStatement2.executeUpdate();
    } catch (SQLException e) {
      consumer.accept(e);
    }
  }

  private boolean isValidId(Long id, Consumer<Throwable> consumer) {
    List<Person> persons = findAllPerson(consumer);
    for (Person person : persons) {
      if (person.getId_person().equals(id)) {
        return true;
      }
    }
    List<Address> addresses = findAllAddresses(consumer);
    for (Address address : addresses) {
      if (address.getId_address().equals(id)) {
        return true;
      }
    }
    return false;
  }

  private boolean canDelete(Long addressId, Consumer<Throwable> consumer) {
    int personCount = getPersonCountByAddressId(addressId, consumer);
    return personCount == 0;
  }

  private void deleteSqlCommand(Long id, String sqlCommand, Consumer<Throwable> consumer) {
    try {
      try (PreparedStatement preparedStatement = connection.prepareStatement(sqlCommand)) {
        preparedStatement.setLong(1, id);
        preparedStatement.executeUpdate();
      }
    } catch (SQLException e) {
      consumer.accept(e);
    }
  }

  private Connection getConnection() {
    if (connection == null) {
      try {
        Class.forName(DbConst.DRIVER);
        connection = DriverManager.getConnection(DbConst.URL, DbConst.USER, DbConst.PASS);
      } catch (SQLException ex) {
        System.out.println(DbConst.SQLEXCEPTION + ex.getMessage());
        System.out.println(DbConst.SQLSTATE + ex.getSQLState());
        System.out.println(DbConst.VENDORERRORCODE + ex.getErrorCode());
      } catch (ClassNotFoundException ex2) {
        throw new RuntimeException(ex2);
      }
    }
    return connection;
  }
}