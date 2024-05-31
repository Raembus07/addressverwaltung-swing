/*
 * Address.java
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = DbConst.ADDRESSSMALL)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Address implements Serializable {

  @Id
  @XmlTransient
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = DbConst.IDADDRESS)
  private Long id_address;

  @Column(name = DbConst.STREET)
  private String street;

  @Column(name = DbConst.STREETNUMBER)
  private String streetnumber;

  @Column(name = DbConst.POSTALCODE)
  private int zipcode;

  @Column(name = DbConst.CITY)
  private String city;

  @Column(name = DbConst.COUNTRY)
  private String country;

  public Address() {
  }

  public Address(Long id, String street, String streetnumber, int zipcode, String city, String country) {
    this.id_address = id;
    this.street = street;
    this.streetnumber = streetnumber;
    this.zipcode = zipcode;
    this.city = city;
    this.country = country;
  }

  public Address(String street, String streetnumber, int zipcode, String city, String country) {
    this.street = street;
    this.streetnumber = streetnumber;
    this.zipcode = zipcode;
    this.city = city;
    this.country = country;
  }

  @Override
  public String toString() {
    return DbConst.ADDRESS + "{" +
           DbConst.IDADDRESS + "=" + id_address +
           DbConst.COMMASPACE + DbConst.STREET + "='" + street + '\'' +
           DbConst.COMMASPACE + DbConst.STREETNUMBER + "='" + streetnumber + '\'' +
           DbConst.COMMASPACE + DbConst.POSTALCODE + "=" + zipcode +
           DbConst.COMMASPACE + DbConst.CITY + "='" + city + '\'' +
           DbConst.COMMASPACE + DbConst.COUNTRY + "='" + country + '\'' +
           '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Address address = (Address) o;
    return zipcode == address.zipcode &&
           Objects.equals(id_address, address.id_address) &&
           Objects.equals(street, address.street) &&
           Objects.equals(streetnumber, address.streetnumber) &&
           Objects.equals(city, address.city) &&
           Objects.equals(country, address.country);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id_address, street, streetnumber, zipcode, city, country);
  }

  public String toCSV() {
    return street + DbConst.COMMASPACE + streetnumber + DbConst.COMMASPACE + zipcode + DbConst.COMMASPACE + city + DbConst.COMMASPACE + country;
  }

  public Long getId_address() {
    return id_address;
  }

  public void setId_address(Long id_address) {
    this.id_address = id_address;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getStreetNumber() {
    return streetnumber;
  }

  public void setStreetNumber(String streetnumber) {
    this.streetnumber = streetnumber;
  }

  public int getZipCode() {
    return zipcode;
  }

  public void setZipCode(int zipcode) {
    this.zipcode = zipcode;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }
}
