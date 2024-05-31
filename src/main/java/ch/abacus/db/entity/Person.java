/*
 * Person.java
 *
 * Creator:
 * 03.04.2024 08:03 josia.schweizer
 *
 * Maintainer:
 * 03.04.2024 08:03 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.db.entity;

import ch.abacus.common.DbConst;
import ch.abacus.fileio.LocalDateAdapter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = DbConst.PERSONSMALL)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Person implements Serializable {

  @Id
  @XmlTransient
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = DbConst.IDPERSON)
  private Long id_person;
  @Column(name = DbConst.FIRSTNAME)
  private String firstname;
  @Column(name = DbConst.LASTNAME)
  private String lastname;
  @Column(name = DbConst.BIRTHDATE)
  @XmlJavaTypeAdapter(LocalDateAdapter.class)
  private LocalDate birthdate;
  @Column(name = DbConst.GENDER)
  @Enumerated(EnumType.STRING)
  private Gender gender;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = DbConst.FKADDRESS)
  @XmlElement(name = DbConst.ADDRESS)
  private Address address;

  public Person(Long id_person, Address address, String surname, String name, LocalDate birthday, Gender gender) {
    this.id_person = id_person;
    this.address = address;
    this.firstname = surname;
    this.lastname = name;
    this.birthdate = birthday;
    this.gender = gender;
  }

  public Person(String surname, String lastnname, LocalDate birthday, Gender gender, Address address) {
    this.firstname = surname;
    this.lastname = lastnname;
    this.birthdate = birthday;
    this.gender = gender;
    this.address = address;
  }

  public Person() {

  }

  @Override
  public String toString() {
    return firstname + DbConst.SPACE + lastname + DbConst.SPACE + birthdate + DbConst.SPACE + getAddress();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Person person = (Person) o;
    return Objects.equals(firstname, person.firstname) && Objects.equals(lastname, person.lastname) && Objects.equals(birthdate, person.birthdate) && Objects.equals(address, person.address);
  }

  @Override
  public int hashCode() {
    return Objects.hash(firstname, lastname, birthdate, address);
  }

  public String toCSV() {
    return firstname + DbConst.COMMASPACE + lastname + DbConst.COMMASPACE + birthdate + DbConst.COMMASPACE + gender + DbConst.COMMASPACE + address.toCSV() + "\n";
  }

  public Long getId_person() {
    return id_person;
  }

  public void setId_person(Long id_person) {
    this.id_person = id_person;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public String getFirstName() {
    return firstname;
  }

  public void setFirstName(String firstname) {
    this.firstname = firstname;
  }

  public String getLastName() {
    return lastname;
  }

  public void setLastName(String lastname) {
    this.lastname = lastname;
  }

  public LocalDate getBirthdate() {
    return birthdate;
  }

  public void setBirthdate(LocalDate birthdate) {
    this.birthdate = birthdate;
  }

  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }
}