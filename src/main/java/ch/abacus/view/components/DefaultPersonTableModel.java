/*
 * DefaultPersonTableModel.java
 *
 * Creator:
 * 24.04.2024 09:16 josia.schweizer
 *
 * Maintainer:
 * 24.04.2024 09:16 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.view.components;

import ch.abacus.common.UITextConst;
import ch.abacus.db.entity.Person;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class DefaultPersonTableModel extends AbstractTableModel {

  private transient List<Person> data;

  public DefaultPersonTableModel(List<Person> people) {
    this.data = new ArrayList<>();
    this.addPerson(people);
  }

  public void addPerson(List<Person> p) {
    data.addAll(p);
    fireTableDataChanged();
  }

  public void addPerson(Person p) {
    data.add(p);
    fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
  }

  @Override
  public String getColumnName(int column) {
    if (column == 0) {
      return UITextConst.FIRSTNAME;
    }
    return UITextConst.LASTNAME;
  }

  @Override
  public int getRowCount() {
    return data.size();
  }

  @Override
  public int getColumnCount() {
    return 2;
  }

  @Override
  public Class<String> getColumnClass(int columnIndex) {
    return String.class;
  }

  public void removePerson(Person p) {
    int i = getIndexFromPerson(p);
    if (i == -1) {
      return;
    }
    data.remove(data.get(i));
    fireTableRowsDeleted(i, i);
  }

  private int getIndexFromPerson(Person p) {
    for (Person person : data) {
      if (person.getFirstName().equals(p.getFirstName()) && person.getLastName().equals(p.getLastName())
          && person.getGender().equals(p.getGender()) && person.getBirthdate().equals(p.getBirthdate())
          && person.getAddress().getStreet().equals(p.getAddress().getStreet())
          && (person.getAddress().getZipCode() == p.getAddress().getZipCode())
          && person.getAddress().getCity().equals(p.getAddress().getCity())
          && person.getAddress().getCountry().equals(p.getAddress().getCountry())) {
        return data.indexOf(person);
      }
    }
    return -1;
  }

  public void repaintTable(List<Person> people) {
    data = people;
    fireTableDataChanged();
  }

  @Override
  public Object getValueAt(int row, int column) {
    Person person = data.get(row);
    switch (column) {
      case 0:
        return person.getFirstName();
      case 1:
        return person.getLastName();
      default:
        return null;
    }
  }

  @Override
  public void setValueAt(Object aValue, int row, int column) {
    switch (column) {
      case 0:
        this.data.get(row).setFirstName(aValue.toString());
        break;
      case 1:
        this.data.get(row).setLastName(aValue.toString());
        break;
      default:
        throw new UnsupportedOperationException();
    }
    fireTableCellUpdated(row, column);
  }
}