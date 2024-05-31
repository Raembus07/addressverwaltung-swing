/*
 * ReflectionSql.java
 *
 * Creator:
 * 22.05.2024 12:04 josia.schweizer
 *
 * Maintainer:
 * 22.05.2024 12:04 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.sql;

import ch.abacus.controller.Controller;
import ch.abacus.controller.ControllerImpl;
import ch.abacus.db.SQLModelImpl;
import ch.abacus.db.entity.Address;
import ch.abacus.db.entity.Person;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ReflectionSql {

  public static Person savePersonUsingReflection(Person person, Controller controller) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    SQLModelImpl impl = new SQLModelImpl(controller);
    Method method = impl.getClass().getDeclaredMethod("savePerson", Person.class, Consumer.class);
    method.setAccessible(true);
    return (Person) method.invoke(impl, person, controller);
  }

  public static Address saveAddressUsingReflection(Address address, Controller controller) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    SQLModelImpl impl = new SQLModelImpl(controller);
    Method method = impl.getClass().getDeclaredMethod("saveAddress", Address.class, Consumer.class);
    method.setAccessible(true);
    return (Address) method.invoke(impl, address, controller);
  }

  public static void deleteAllUsingReflection(Controller controller) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    SQLModelImpl impl = new SQLModelImpl(controller);
    Method method = impl.getClass().getDeclaredMethod("deleteAll", Consumer.class);
    method.setAccessible(true);
    method.invoke(impl, controller);
  }

  public static boolean canDeleteUsingRefletion(Long id, Controller controller) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    SQLModelImpl impl = new SQLModelImpl(controller);
    Method method = impl.getClass().getDeclaredMethod("canDelete", Long.class, Consumer.class);
    method.setAccessible(true);
    return (boolean) method.invoke(impl, id, controller);
  }

  @SuppressWarnings("unchecked")
  public static List<Person> findAllPersonsUsingReflection(Controller controller) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    SQLModelImpl impl = new SQLModelImpl(controller);
    Method method = impl.getClass().getDeclaredMethod("findAllPerson", Consumer.class);
    method.setAccessible(true);
    return (List<Person>) method.invoke(impl, controller);
  }

  @SuppressWarnings("unchecked")
  public static List<Address> findAllAddressesUsingReflection(Controller controller) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    SQLModelImpl impl = new SQLModelImpl(controller);
    Method method = impl.getClass().getDeclaredMethod("findAllAddresses", Consumer.class);
    method.setAccessible(true);
    return (List<Address>) method.invoke(impl, controller);
  }

  @SuppressWarnings("unchecked")
  public static Optional<Long> getPersonIdUsingReflection(Person person, Controller controller) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    SQLModelImpl impl = new SQLModelImpl(controller);
    Method method = impl.getClass().getDeclaredMethod("getPersonId", Person.class, Consumer.class);
    method.setAccessible(true);
    return (Optional<Long>) method.invoke(impl, person, controller);
  }

  @SuppressWarnings("unchecked")
  public static Optional<Long> getAddressIdUsingReflection(Address address, Controller controller) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    SQLModelImpl impl = new SQLModelImpl(controller);
    Method method = impl.getClass().getDeclaredMethod("getAddressId", Address.class, Consumer.class);
    method.setAccessible(true);
    return (Optional<Long>) method.invoke(impl, address, controller);
  }

  @SuppressWarnings("unchecked")
  public static Optional<Person> getPersonByIdUsingReflection(Long id, Controller controller) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    SQLModelImpl impl = new SQLModelImpl(controller);
    Method method = impl.getClass().getDeclaredMethod("getPersonById", Long.class, Consumer.class);
    method.setAccessible(true);
    return (Optional<Person>) method.invoke(impl, id, controller);
  }

  @SuppressWarnings("unchecked")
  public static Optional<Address> getAddressByIdUsingReflection(Long id, Controller controller) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    SQLModelImpl impl = new SQLModelImpl(controller);
    Method method = impl.getClass().getDeclaredMethod("getAddressById", Long.class, Consumer.class);
    method.setAccessible(true);
    return (Optional<Address>) method.invoke(impl, id, controller);
  }

  public static void deleteByIdUsingReflection(Long id, boolean isPerson, Controller controller) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    SQLModelImpl impl = new SQLModelImpl(controller);
    Method method = impl.getClass().getDeclaredMethod("deleteById", Long.class, boolean.class, Consumer.class);
    method.setAccessible(true);
    method.invoke(impl, id, isPerson, controller);
  }

  public static void deleteUsingReflection(Long id, Controller controller) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    SQLModelImpl impl = new SQLModelImpl(controller);
    Method method = impl.getClass().getDeclaredMethod("delete", Long.class, Consumer.class);
    method.setAccessible(true);
    method.invoke(impl, id, controller);
  }

  public static Connection getConnectionUsingReflection() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    SQLModelImpl impl = new SQLModelImpl(new ControllerImpl());
    Method method = impl.getClass().getDeclaredMethod("getConnection");
    method.setAccessible(true);
    return (Connection) method.invoke(impl);
  }
}