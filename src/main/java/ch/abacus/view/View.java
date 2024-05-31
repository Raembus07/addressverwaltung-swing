/*
 * View.java
 *
 * Creator:
 * 04.04.2024 16:24 josia.schweizer
 *
 * Maintainer:
 * 04.04.2024 16:24 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.view;

import ch.abacus.db.entity.Person;
import ch.abacus.view.components.PersonDetailPanel;

import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.List;

public interface View extends Serializable {

  void initUI(ActionListener actionListener, ListSelectionListener selectionListener);

  void updateTableAdd(Person p);

  void updateTableModify(int index);

  void updateTableRemove(Person p);

  void repaintTable(List<Person> people);

  void selectRow(int index);

  void enableModifyDelete();

  void disableModifyDelete();

  Color getBground();

  JTable getTable();

  PersonDetailPanel getDetailPanel();
}
