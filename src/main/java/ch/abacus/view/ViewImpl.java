/*
 * ViewImpl.java
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

import ch.abacus.common.UITextConst;
import ch.abacus.common.ViewConst;
import ch.abacus.controller.Controller;
import ch.abacus.db.entity.Person;
import ch.abacus.view.components.DefaultPersonTableModel;
import ch.abacus.view.components.PersonDetailPanel;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.List;

public class ViewImpl extends JFrame implements View {

  private DefaultPersonTableModel model;
  private final transient Controller controller;
  private PersonDetailPanel detailPanel;
  private JTable table;
  private JButton modifyBtn;
  private JButton deleteBtn;

  public ViewImpl(Controller controller) {
    this.controller = controller;
  }

  @Override
  public void initUI(ActionListener actionListener, ListSelectionListener selectionListener) {
    initFrame();

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.setBorder(new EmptyBorder(7, 7, 7, 7));

    JMenuBar menuBar = new JMenuBar();
    initFileMenu(menuBar, actionListener);
    initModelMenu(menuBar, actionListener);
    setJMenuBar(menuBar);

    JSplitPane splitPane = createSplitPane(actionListener, selectionListener);
    panel.add(splitPane, BorderLayout.CENTER);

    this.add(panel);
    this.setVisible(true);
    setVisible(true);
  }

  private void initFrame() {
    setTitle(UITextConst.TITLE);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(1100, 600);
    setLocationRelativeTo(null);
    setMinimumSize(new Dimension(800, 450));
  }

  private void initFileMenu(JMenuBar menuBar, ActionListener actionListener) {
    JMenu menu = new JMenu(UITextConst.FILEMENUTITLE);
    menu.add(this.createJMenuItem(UITextConst.LOADFROMFILE, ViewConst.LOADFROMFILE, actionListener));
    menu.add(this.createJMenuItem(UITextConst.SAVETOFILE, ViewConst.SAVETOFILE, actionListener));
    menuBar.add(menu);
  }

  private void initModelMenu(JMenuBar menuBar, ActionListener actionListener) {
    JMenu menu = new JMenu(UITextConst.DBMENUTITLE);
    menu.add(createJMenuItem(UITextConst.SQL, ViewConst.SQL, actionListener));
    menu.add(createJMenuItem(UITextConst.JPA, ViewConst.JPA, actionListener));
    menuBar.add(menu);
  }

  private JMenuItem createJMenuItem(String text, String actionCommand, ActionListener listener) {
    JMenuItem item = new JMenuItem(text);
    addActionListener(item, actionCommand, listener);
    return item;
  }

  private void addActionListener(JMenuItem item, String actionCommand, ActionListener listener) {
    item.addActionListener(listener);
    item.setActionCommand(actionCommand);
  }

  private JSplitPane createSplitPane(ActionListener actionListener, ListSelectionListener selectionListener) {
    JScrollPane leftSide = createLeftSide(selectionListener);
    JScrollPane rightSide = createRightSide(actionListener);

    return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSide, rightSide);
  }

  private JScrollPane createRightSide(ActionListener actionListener) {
    JScrollPane pane = new JScrollPane();

    JPanel panel = new JPanel(new BorderLayout());

    panel.add(getDetailPanel().initUI(false), BorderLayout.CENTER);
    panel.add(createButtonPanel(actionListener), BorderLayout.SOUTH);

    getDetailPanel().setTitle(UITextConst.PERSONDETAILS);
    pane.setViewportView(panel);

    return pane;
  }

  private JScrollPane createLeftSide(ListSelectionListener selectionListener) {
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setViewportView(createTable(selectionListener));
    return scrollPane;
  }

  private JTable createTable(ListSelectionListener selectionListener) {
    List<Person> personList = controller.getAllPeople();
    model = new DefaultPersonTableModel(personList);
    table = new JTable(model);
    addSelectionListeners(selectionListener, table);
    return table;
  }

  @Override
  public void updateTableAdd(Person p) {
    model.addPerson(p);
  }

  @Override
  public void updateTableModify(int index) {
    Person p = controller.getAllPeople().get(index);
    model.setValueAt(p.getFirstName(), index, 0);
    model.setValueAt(p.getLastName(), index, 1);
  }

  @Override
  public void updateTableRemove(Person p) {
    model.removePerson(p);
  }

  @Override
  public void repaintTable(List<Person> people) {
    model.repaintTable(people);
  }

  @Override
  public void selectRow(int index) {
    table.setRowSelectionInterval(index, index);
  }

  private JPanel createButtonPanel(ActionListener actionListener) {
    Insets leftInset = new Insets(0, 0, 0, 10);
    Insets centerInset = new Insets(0, 0, 0, 0);
    Insets rightInset = new Insets(0, 10, 0, 0);

    JPanel btnPanel = new JPanel(new GridBagLayout());
    btnPanel.add(initBtn(UITextConst.ADDBUTTON, ViewConst.ADDPERSON, actionListener), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, leftInset, 0, 0));
    modifyBtn = initBtn(UITextConst.MODIFYBUTTON, ViewConst.MODIFYPERSON, actionListener);
    modifyBtn.setEnabled(false);
    btnPanel.add(modifyBtn, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, centerInset, 0, 0));
    deleteBtn = initBtn(UITextConst.DELETEBUTTON, ViewConst.DELETEPERSON, actionListener);
    deleteBtn.setEnabled(false);
    btnPanel.add(deleteBtn, new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, rightInset, 0, 0));
    return btnPanel;
  }

  @Override
  public void enableModifyDelete() {
    modifyBtn.setEnabled(true);
    deleteBtn.setEnabled(true);
  }

  @Override
  public void disableModifyDelete() {
    modifyBtn.setEnabled(false);
    deleteBtn.setEnabled(false);
  }

  private JButton initBtn(String title, String btnConst, ActionListener actionListener) {
    JButton button = new JButton(title);
    button.setActionCommand(btnConst);
    button.setSize(80, 30);
    button.setFocusable(false);
    addActionListeners(actionListener, button);
    return button;
  }

  private void addActionListeners(ActionListener actionListener, JButton button) {
    button.addActionListener(actionListener);
  }

  private void addSelectionListeners(ListSelectionListener listener, JTable table) {
    table.getSelectionModel().addListSelectionListener(listener);
  }

  @Override
  public Color getBground() {
    return this.getBackground();
  }

  @Override
  public PersonDetailPanel getDetailPanel() {
    if (detailPanel == null) {
      detailPanel = new PersonDetailPanel(this);
    }
    return detailPanel;
  }

  @Override
  public JTable getTable() {
    return table;
  }
}