/*
 * Persons.java
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = DbConst.PERSONS)
@XmlAccessorType(XmlAccessType.FIELD)
public class Persons {

  @XmlElement(name = DbConst.PERSON)
  private List<Person> persons;

  public Persons() {
  }

  public List<Person> getPersons() {
    return persons;
  }

  public void setPersons(List<Person> persons) {
    this.persons = persons;
  }
}