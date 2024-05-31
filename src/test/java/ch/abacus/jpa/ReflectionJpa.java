/*
 * Relection.java
 *
 * Creator:
 * 22.05.2024 09:06 josia.schweizer
 *
 * Maintainer:
 * 22.05.2024 09:06 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.jpa;

import ch.abacus.controller.Controller;
import ch.abacus.db.JPAModelImpl;
import ch.abacus.db.entity.Address;
import ch.abacus.db.entity.Person;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ReflectionJpa {

  public static Person savePersonUsingReflection(Person person, Controller controller) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    JPAModelImpl impl = new JPAModelImpl();
    Method method = impl.getClass().getDeclaredMethod("savePerson", Person.class, Consumer.class);
    method.setAccessible(true);
    return (Person) method.invoke(impl, person, controller);
  }

  public static Address saveAddressUsingReflection(Address address, Controller controller) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    JPAModelImpl impl = new JPAModelImpl();
    Method method = impl.getClass().getDeclaredMethod("saveAddress", Address.class, Consumer.class);
    method.setAccessible(true);
    return (Address) method.invoke(impl, address, controller);
  }

  public static void deleteAllUsingReflection(Controller controller) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    JPAModelImpl impl = new JPAModelImpl();
    Method method = impl.getClass().getDeclaredMethod("deleteAll", Consumer.class);
    method.setAccessible(true);
    method.invoke(impl, controller);
  }

  public static boolean canDeleteUsingReflection(Long id, Controller controller) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    JPAModelImpl impl = new JPAModelImpl();
    Method method = impl.getClass().getDeclaredMethod("canDelete", Long.class, Consumer.class);
    method.setAccessible(true);
    return (boolean) method.invoke(impl, id, controller);
  }

  @SuppressWarnings("unchecked")
  public static List<Address> findAllAddressesUsingReflection(Controller controller) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    JPAModelImpl impl = new JPAModelImpl();
    Method method = impl.getClass().getDeclaredMethod("findAllAddresses", Consumer.class);
    method.setAccessible(true);
    return (List<Address>) method.invoke(impl, controller);
  }

  public static void deleteByIdUsingRefelection(Long id, boolean isPerson, Controller controller) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    JPAModelImpl impl = new JPAModelImpl();
    Method method = impl.getClass().getDeclaredMethod("deleteById", Long.class, boolean.class, Consumer.class);
    method.setAccessible(true);
    method.invoke(impl, id, isPerson, controller);
  }

  @SuppressWarnings("unchecked")
  public static Optional<Person> samePersonUsingReflection(Long id, Person person, Controller controller) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    JPAModelImpl impl = new JPAModelImpl();
    Method method = impl.getClass().getDeclaredMethod("samePerson", Long.class, Person.class, Consumer.class);
    method.setAccessible(true);
    return (Optional<Person>) method.invoke(impl, id, person, controller);
  }

  @SuppressWarnings("unchecked")
  public static Optional<Address> getAddressByIdUsingReflection(Long id, Controller controller) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    JPAModelImpl impl = new JPAModelImpl();
    Method method = impl.getClass().getDeclaredMethod("getAddressById", Long.class, Consumer.class);
    method.setAccessible(true);
    return (Optional<Address>) method.invoke(impl, id, controller);
  }

  @SuppressWarnings("unchecked")
  public static Optional<Long> getAddressIdUsingReflection(Address address, Controller controller) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    JPAModelImpl impl = new JPAModelImpl();
    Method method = impl.getClass().getDeclaredMethod("getAddressId", Address.class, Consumer.class);
    method.setAccessible(true);
    return (Optional<Long>) method.invoke(impl, address, controller);
  }

  public static void editAddressUsingRefelection(Long id, Address address, Controller controller) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    JPAModelImpl impl = new JPAModelImpl();
    Method method = impl.getClass().getDeclaredMethod("editAddress", Long.class, Address.class, Consumer.class);
    method.setAccessible(true);
    method.invoke(impl, id, address, controller);
  }

  @SuppressWarnings("unchecked")
  public static Optional<Address> addressAlreadyRegisteredUsingReflection(Address address, Controller controller) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    JPAModelImpl impl = new JPAModelImpl();
    Method method = impl.getClass().getDeclaredMethod("addressAlreadyRegistered", Address.class, Consumer.class);
    method.setAccessible(true);
    return (Optional<Address>) method.invoke(impl, address, controller);
  }

  public static int getPersonCountByAddressIdUsingReflection(Long id, Controller controller) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    JPAModelImpl impl = new JPAModelImpl();
    Method method = impl.getClass().getDeclaredMethod("getPersonCountByAddressId", Long.class, Consumer.class);
    method.setAccessible(true);
    return (int) method.invoke(impl, id, controller);
  }
}