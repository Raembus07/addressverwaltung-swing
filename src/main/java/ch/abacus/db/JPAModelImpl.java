/*
 * JPAModel.java
 *
 * Creator:
 * 18.04.2024 17:30 josia.schweizer
 *
 * Maintainer:
 * 18.04.2024 17:30 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.db;

import ch.abacus.common.DbConst;
import ch.abacus.common.JPAStatments;
import ch.abacus.db.components.EntitymanagerUtil;
import ch.abacus.db.entity.Address;
import ch.abacus.db.entity.Person;

import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class JPAModelImpl implements Model {

  private EntityTransaction transaction;

  @Override
  public Person savePerson(Person person, Consumer<Throwable> consumer) {
    Optional<Long> existingPersonId = getPersonId(person, consumer);
    existingPersonId.ifPresent(person::setId_person);

    person.setAddress(saveAddress(person.getAddress(), consumer));

    if (existingPersonId.isEmpty()) {
      EntitymanagerUtil emu = EntitymanagerUtil.getInstance();
      transaction = emu.begin();
      try {
        emu.getEm().persist(person);
        emu.commitTransaction();
        return person;
      } catch (Exception e) {
        if (transaction.isActive()) {
          transaction.rollback();
          deleteById(person.getAddress().getId_address(), false, consumer);
        }
        consumer.accept(e);
        return null;
      }
    }
    return null;
  }

  private Address saveAddress(Address address, Consumer<Throwable> consumer) {
    Optional<Long> existingAddressId = getAddressId(address, consumer);
    if (existingAddressId.isPresent()) {
      return getAddressById(existingAddressId.get(), consumer).orElse(null);
    } else {
      EntitymanagerUtil emu = EntitymanagerUtil.getInstance();
      transaction = emu.begin();
      try {
        emu.getEm().persist(address);
        emu.commitTransaction();
        address.setId_address(getAddressId(address, consumer).get()); //sets the id of the address
        return address;
      } catch (Exception e) {
        if (transaction.isActive()) {
          transaction.rollback();
        }
        consumer.accept(e);
        return null;
      }
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
  public Optional<Person> getPersonById(Long id, Consumer<Throwable> consumer) {
    EntitymanagerUtil emu = EntitymanagerUtil.getInstance();
    try {
      Person person = emu.getEm().find(Person.class, id);
      return Optional.ofNullable(person);
    } catch (Exception e) {
      consumer.accept(e);
      return Optional.empty();
    }
  }

  @Override
  public Person updatePerson(Long id, Person person, Consumer<Throwable> consumer) {
    Optional<Person> p = samePerson(id, person, consumer);
    if (p.isPresent()) {
      return p.get();
    }

    Address oldAddress = getPersonById(id, consumer)
        .map(Person::getAddress)
        .orElseThrow(() -> new IllegalArgumentException(DbConst.NOPERSONFOUND + id));

    Optional<Address> newAddress;
    boolean addressAlreadyExists;
    boolean sameAddress = false;

    addressAlreadyExists = addressAlreadyRegistered(person.getAddress(), consumer).isPresent();
    newAddress = Optional.ofNullable(person.getAddress());
    if (newAddress.get().equals(oldAddress)) {
      sameAddress = true;
    }

    int usagetimesOldAddress = getPersonCountByAddressId(oldAddress.getId_address(), consumer);
    if (usagetimesOldAddress > 1 && !sameAddress) {
      newAddress = Optional.ofNullable(saveAddress(newAddress.get(), consumer));
    } else if (!addressAlreadyExists) {
      newAddress = editAddressWithinTransaction(oldAddress.getId_address(), newAddress.get(), consumer);
    }
    if (newAddress.isPresent()) {
      person = new Person(id, newAddress.get(), person.getFirstName(), person.getLastName(), person.getBirthdate(), person.getGender());
      editPersonWithinTransaction(id, person, consumer);
    }

    deleteNotUsedAddresses(consumer);

    return person;
  }

  @Override
  public List<Person> findAllPerson(Consumer<Throwable> consumer) {
    EntitymanagerUtil emu = EntitymanagerUtil.getInstance();
    try {
      TypedQuery<Person> query = emu.getEm().createQuery(JPAStatments.SELECT_PERSON, Person.class);
      return query.getResultList();
    } catch (Exception e) {
      consumer.accept(e);
      return Collections.emptyList();
    }
  }

  private Optional<Address> getAddressById(Long id, Consumer<Throwable> consumer) {
    try {
      EntitymanagerUtil emu = EntitymanagerUtil.getInstance();
      Address address = emu.getEm().find(Address.class, id);
      return Optional.ofNullable(address);
    } catch (Exception e) {
      consumer.accept(e);
      return Optional.empty();
    }
  }

  private Optional<Long> getPersonId(Person person, Consumer<Throwable> consumer) {
    EntitymanagerUtil emu = EntitymanagerUtil.getInstance();
    try {
      TypedQuery<Long> query = emu.getEm().createQuery(JPAStatments.SELECT_ID_BY_PERSON, Long.class);
      query.setParameter(DbConst.FIRSTNAME, person.getFirstName());
      query.setParameter(DbConst.LASTNAME, person.getLastName());
      query.setParameter(DbConst.GENDER, person.getGender());
      query.setParameter(DbConst.BIRTHDATE, person.getBirthdate());
      List<Long> id = query.getResultList();
      if (!id.isEmpty()) {
        return Optional.of(id.get(0));
      } else {
        return Optional.empty();
      }
    } catch (Exception e) {
      consumer.accept(e);
      return Optional.empty();
    }
  }

  private Optional<Long> getAddressId(Address address, Consumer<Throwable> consumer) {
    EntitymanagerUtil emu = EntitymanagerUtil.getInstance();
    try {
      TypedQuery<Long> query = emu.getEm().createQuery(JPAStatments.SELECT_ID_BY_ADDRESS, Long.class);
      query.setParameter(DbConst.STREET, address.getStreet());
      query.setParameter(DbConst.STREETNUMBER, address.getStreetNumber());
      query.setParameter(DbConst.CITY, address.getCity());
      query.setParameter(DbConst.POSTALCODE, address.getZipCode());
      query.setParameter(DbConst.COUNTRY, address.getCountry());
      List<Long> resultList = query.getResultList();
      if (!resultList.isEmpty()) {
        return Optional.of(resultList.get(0));
      } else {
        return Optional.empty();
      }
    } catch (Exception e) {
      consumer.accept(e);
      return Optional.empty();
    }
  }

  private List<Address> findAllAddresses(Consumer<Throwable> consumer) {
    EntitymanagerUtil emu = EntitymanagerUtil.getInstance();
    try {
      TypedQuery<Address> query = emu.getEm().createQuery(JPAStatments.SELECT_ADDRESS, Address.class);
      return query.getResultList();
    } catch (Exception e) {
      consumer.accept(e);
      return Collections.emptyList();
    }
  }

  private void deleteById(Long id, boolean isPerson, Consumer<Throwable> consumer) {
    EntitymanagerUtil emu = EntitymanagerUtil.getInstance();
    transaction = emu.begin();
    try {
      if (isPerson) {
        Person person = emu.getEm().find(Person.class, id);
        if (person != null) {
          emu.getEm().remove(person);
        }
      } else {
        Address address = emu.getEm().find(Address.class, id);
        if (address != null) {
          emu.getEm().remove(address);
        }
      }
      emu.commitTransaction();
    } catch (Exception e) {
      if (transaction.isActive()) {
        transaction.rollback();
      }
      consumer.accept(e);
    }
  }

  private void deleteAll(Consumer<Throwable> consumer) {
    EntitymanagerUtil emu = EntitymanagerUtil.getInstance();
    transaction = emu.begin();
    try {
      Query query = emu.getEm().createQuery(JPAStatments.DELETE_ALL_FROM_PERSON);
      query.executeUpdate();
      query = emu.getEm().createQuery(JPAStatments.DELETE_ALL_FROM_ADDRESS);
      query.executeUpdate();
      emu.commitTransaction();
    } catch (Exception e) {
      if (transaction.isActive()) {
        transaction.rollback();
      }
      consumer.accept(e);
    }
  }

  private boolean canDelete(Long addressId, Consumer<Throwable> consumer) {
    int personCount = getPersonCountByAddressId(addressId, consumer);
    return personCount == 0;
  }

  private void deleteNotUsedAddresses(Consumer<Throwable> consumer) {
    List<Address> addresses = findAllAddresses(consumer);
    for (Address address : addresses) {
      if (getPersonCountByAddressId(address.getId_address(), consumer) == 0) {
        deleteById(address.getId_address(), false, consumer);
      }
    }
  }

  private void editPersonWithinTransaction(Long personId, Person person, Consumer<Throwable> consumer) {
    EntitymanagerUtil emu = EntitymanagerUtil.getInstance();
    transaction = emu.begin();
    try {
      editPerson(personId, person, consumer);
      emu.commitTransaction();
    } catch (Exception e) {
      if (transaction.isActive()) {
        transaction.rollback();
      }
      consumer.accept(e);
    }
  }

  private Optional<Address> editAddressWithinTransaction(Long id, Address address, Consumer<Throwable> consumer) {
    EntitymanagerUtil emu = EntitymanagerUtil.getInstance();
    transaction = emu.begin();
    try {
      Optional<Address> updatedAddress = editAddress(id, address, consumer);
      emu.commitTransaction();
      return updatedAddress;
    } catch (Exception e) {
      if (transaction.isActive()) {
        transaction.rollback();
      }
      consumer.accept(e);
      return Optional.empty();
    }
  }

  private void editPerson(Long id, Person person, Consumer<Throwable> consumer) {
    EntitymanagerUtil emu = EntitymanagerUtil.getInstance();
    try {
      Person existingPerson = emu.getEm().find(Person.class, id);
      if (existingPerson != null) {
        existingPerson.setFirstName(person.getFirstName());
        existingPerson.setLastName(person.getLastName());
        existingPerson.setGender(person.getGender());
        existingPerson.setBirthdate(person.getBirthdate());
        existingPerson.setAddress(person.getAddress());
      }
    } catch (Exception e) {
      consumer.accept(e);
    }
  }

  private Optional<Address> editAddress(Long id, Address address, Consumer<Throwable> consumer) {
    EntitymanagerUtil emu = EntitymanagerUtil.getInstance();
    try {
      Address existingAddress = emu.getEm().find(Address.class, id);
      if (existingAddress != null) {
        // Aktualisieren der Felder der vorhandenen Adresse mit den Werten der übergebenen Adresse
        existingAddress.setStreet(address.getStreet());
        existingAddress.setStreetNumber(address.getStreetNumber());
        existingAddress.setZipCode(address.getZipCode());
        existingAddress.setCity(address.getCity());
        existingAddress.setCountry(address.getCountry());
      }
      return Optional.ofNullable(existingAddress);
    } catch (Exception e) {
      consumer.accept(e);
      return Optional.empty();
    }
  }

  private Optional<Person> samePerson(Long id, Person person, Consumer<Throwable> consumer) {
    Optional<Person> oldPerson = getPersonById(id, consumer);
    if (oldPerson.isPresent() && (oldPerson.get().getFirstName().equals(person.getFirstName())
                                  && oldPerson.get().getLastName().equals(person.getLastName())
                                  && oldPerson.get().getGender().equals(person.getGender())
                                  && oldPerson.get().getBirthdate().equals(person.getBirthdate()))) {
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

  private Optional<Address> addressAlreadyRegistered(Address address, Consumer<Throwable> consumer) {
    List<Address> list = findAllAddresses(consumer);
    for (Address ad : list) {
      if (ad.getStreet().equals(address.getStreet()) && ad.getStreetNumber().equals(address.getStreetNumber()) && ad.getZipCode() == address.getZipCode() && ad.getCity().equals(address.getCity()) && ad.getCountry().equals(address.getCountry())) {
        return Optional.of(ad);
      }
    }
    return Optional.empty();
  }

  private int getPersonCountByAddressId(Long addressId, Consumer<Throwable> consumer) {
    int count = 0;
    List<Person> people = findAllPerson(consumer);
    for (Person person : people) {
      if (person.getAddress().getId_address().equals(addressId)) {
        count++;
      }
    }
    return count;
  }
}