/*
 * ControllerImpl.java
 *
 * Creator:
 * 03.04.2024 13:39 josia.schweizer
 *
 * Maintainer:
 * 03.04.2024 13:39 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.controller;

import ch.abacus.common.Borders;
import ch.abacus.common.ControllerConst;
import ch.abacus.common.ErrorConst;
import ch.abacus.common.State;
import ch.abacus.common.ViewConst;
import ch.abacus.db.JPAModelImpl;
import ch.abacus.db.Model;
import ch.abacus.db.SQLModelImpl;
import ch.abacus.db.entity.Address;
import ch.abacus.db.entity.Person;
import ch.abacus.fileio.PersonIOFactory;
import ch.abacus.fileio.components.XMLWRITE;
import ch.abacus.view.View;
import ch.abacus.view.ViewImpl;
import ch.abacus.view.components.ErrorDialog;
import ch.abacus.view.components.FileDialog;
import ch.abacus.view.components.FileXmlChoiceDialog;
import ch.abacus.view.components.PersonDetailPanel;
import ch.abacus.view.components.PersonDialog;
import ch.abacus.view.components.Rows;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static javax.swing.JFileChooser.APPROVE_SELECTION;
import static javax.swing.JFileChooser.CANCEL_SELECTION;

public class ControllerImpl implements Controller, ActionListener, ListSelectionListener {

  private Model model;
  private final Model sqlModel;
  private final Model jpaModel;
  private final View view;
  private final List<State> states = new ArrayList<>();
  private FileXmlChoiceDialog fileXmlChoiceDialog;
  private PersonDetailPanel detailPanel;
  private ErrorDialog errorDialog;
  private PersonIOFactory factory;
  private FileDialog fileDialog;
  private PersonDialog personDialog;
  private Person selectedPerson;

  private XMLWRITE xmlEditMethode = XMLWRITE.JAXB;
  private int selectedRow = -1;

  public ControllerImpl() {
    this.view = new ViewImpl(this);
    this.sqlModel = new SQLModelImpl(this);
    this.jpaModel = new JPAModelImpl();

    model = sqlModel;

    states.add(State.READ);
  }

  @Override
  public void init() {
    view.initUI(this, this);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    switch (e.getActionCommand()) {
      case ViewConst.ADDPERSON:
        states.add(State.ADD);
        addPerson();
        break;
      case ViewConst.MODIFYPERSON:
        states.add(State.MODIFY);
        modifyPerson();
        break;
      case ViewConst.DELETEPERSON:
        states.add(State.DELETE);
        deletePerson();
        states.add(State.READ);
        break;
      case ViewConst.SAVETOFILE:
        states.add(State.SAVE);
        loadFileDialog();
        break;
      case ViewConst.LOADFROMFILE:
        states.add(State.LOAD);
        loadFileDialog();
        break;
      case ViewConst.OKDIALOG:
        if (getPersonDialog().getDetailPanel().everyInputCorrect()) {
          savePerson();
          states.add(State.READ);
        } else {
          getErrorDialog(ErrorConst.INVALIDINPUTTITLE, ErrorConst.INVALIDINPUTTEXT).setVisible(true);
        }
        break;
      case ViewConst.RETURNDIALOG:
      case CANCEL_SELECTION:
        closeFrames();
        break;
      case APPROVE_SELECTION:
        approveSelection();
        break;
      case ControllerConst.STAX:
        xmlEditMethode = XMLWRITE.STAX;
        fileXmlChoiceDialog.clearRadioBtn();
        closeFileXmlChoiceDialog();
        closeFileDialog();
        break;
      case ControllerConst.JAXB:
        xmlEditMethode = XMLWRITE.JAXB;
        fileXmlChoiceDialog.clearRadioBtn();
        closeFileXmlChoiceDialog();
        closeFileDialog();
        break;
      case ControllerConst.DOM:
        xmlEditMethode = XMLWRITE.DOM;
        fileXmlChoiceDialog.clearRadioBtn();
        closeFileXmlChoiceDialog();
        closeFileDialog();
        break;
      case ControllerConst.JPA:
        model = getJpaModel();
        break;
      case ControllerConst.SQL:
        model = getSqlModel();
        break;
      default:
        throw new IllegalStateException(ErrorConst.UNEXPECTEDVALUE + e.getActionCommand());
    }
  }

  private void addPerson() {
    getPersonDialog().removeInputVerifiers();
    getPersonDialog().setFocusToFirstTextField();
    getPersonDialog().setTitle(ControllerConst.ADDPERSON);
    getPersonDialog().getDetailPanel().setTitle(ControllerConst.ADDPERSON);
    getPersonDialog().addInputVerifier();
    getPersonDialog().setVisible(true);
  }

  private void deletePerson() {
    if (detailPanel == null) {
      getErrorDialog(ErrorConst.NOPERSONSELECTEDTITLE, ErrorConst.NOPERSONSELECTEDTEXT).setVisible(true);
    } else if (selectedRow != -1) {
      selectedPerson = getAllPeople().get(selectedRow);
      delete(selectedPerson);

      if (getAllPeople().size() - 1 >= 0) {
        view.selectRow(0);
      } else {
        view.getDetailPanel().clearTextfield();
      }

      getPersonDialog().getDetailPanel().setModel(new Person());
      getPersonDialog().clearTextfield();
    }
  }

  private void modifyPerson() {
    if (detailPanel != null) {
      Person person = detailPanel.getModel();
      getPersonDialog().getDetailPanel().setTitle(ControllerConst.EDITPERSON);
      getPersonDialog().getDetailPanel().setModel(person);
      getPersonDialog().getDetailPanel().fillTextfield();
      getPersonDialog().addInputVerifier();
      getPersonDialog().setVisible(true);
    } else {
      getErrorDialog(ErrorConst.NOPERSONSELECTEDTITLE, ErrorConst.NOPERSONSELECTEDTEXT).setVisible(true);
    }
  }

  private void savePerson() {
    boolean validAddedPerson = true;
    Person newP = getPersonDialog().getModel();
    getPersonDialog().fillModel();
    if (getLastState() == State.MODIFY) {
      getPersonDialog().fillModel();
      Person oldPerson = getAllPeople().get(selectedRow);
      try {
        model.updatePerson(oldPerson.getId_person(), newP, this);
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
      view.updateTableModify(selectedRow);
      detailPanel.clearTextfield();
    } else {
      if (!personAlreadyAdded(newP)) {
        try {
          model.savePerson(newP, this);
        } catch (Throwable e) {
          throw new RuntimeException(e);
        }

        if (getLastState().equals(State.ADD)) {
          view.updateTableAdd(newP);
          selectLastPerson();
        } else {
          states.add(State.ADD);
        }

        view.getDetailPanel().setModel(newP);
      } else {
        validAddedPerson = false;
        getErrorDialog(ErrorConst.PERSONALREADYEXISTSTITLE, ErrorConst.PERSONALREADYEXISTSTEXT).setVisible(true);
      }
    }

    if (validAddedPerson) {
      view.getDetailPanel().fillTextfield();
      closeFrames();
    }
  }

  private void selectLastPerson() {
    int newRowIndex = view.getTable().getRowCount() - 1;
    view.selectRow(newRowIndex);
  }

  private boolean personAlreadyAdded(Person newP) {
    return getAllPeople().stream()
        .anyMatch(p -> p.getFirstName().equals(newP.getFirstName())
                       && p.getLastName().equals(newP.getLastName())
                       && p.getBirthdate().equals(newP.getBirthdate())
                       && p.getGender().equals(newP.getGender())
                       && p.getAddress().getStreet().equals(newP.getAddress().getStreet())
                       && p.getAddress().getStreetNumber().equals(newP.getAddress().getStreetNumber())
                       && p.getAddress().getZipCode() == newP.getAddress().getZipCode()
                       && p.getAddress().getCity().equals(newP.getAddress().getCity())
                       && p.getAddress().getCountry().equals(newP.getAddress().getCountry()));
  }

  private void approveSelection() {
    getFileDialog().fillModel();
    String path = getFileDialog().getModel();
    String[] splitedPath = path.split("/");
    String[] splitedFilename = splitedPath[splitedPath.length - 1].split("\\.");
    if (splitedFilename[1].equals(ControllerConst.XML) || splitedFilename[1].equals(ControllerConst.CSV)
        || splitedFilename[1].equals(ControllerConst.JSON)) {
      if (getLastState() == State.LOAD) {
        loadFromFile(path);
      } else {
        saveToFile(path);
      }

      selectLastPerson();

    } else {
      getErrorDialog("Invalid File", "Please enter a XML, CSV or JSON file!").setVisible(true);
    }
  }

  private void saveToFile(String path) {
    File file = new File(path);
    if (!file.exists() || !isValidFileExtension(path)) {
      if (isValidFileExtension(path)) {
        try {
          file.createNewFile();
        } catch (IOException e) {
          accept(e);
        }
      } else {
        getErrorDialog(ErrorConst.INVALIDFILEEXTENSIONTITLE, ErrorConst.INVALIDFILEEXTENSIONTEXT).setVisible(true);
        return;
      }
    }
    try {
      getFactory().write(personToPersonFileIO(getAllPeople()), file, xmlEditMethode);
      closeFrames();
    } catch (IOException e) {
      accept(e);
    }
  }

  private void loadFromFile(String path) {
    File file = new File(path);
    if (!file.exists()) {
      getErrorDialog(ErrorConst.INVALIDFILETITLE, ErrorConst.INVALIDFILETEXT).setVisible(true);
    } else {
      try {
        List<Person> newPeople = personFileIOToPerson(getFactory().read(file, xmlEditMethode));
        if (!newPeople.isEmpty()) {
          for (Person person : newPeople) {
            model.savePerson(person, this);
          }
          if (getLastState() != State.LOAD) {
            states.add(State.LOAD);
          }
          view.repaintTable(getAllPeople());
          closeFrames();
        }
      } catch (Throwable e) {
        accept(e);
      }
    }
  }

  private List<Person> personToPersonFileIO(List<Person> people) {
    return people.stream()
        .map(person -> new Person(person.getFirstName(), person.getLastName(),
                                  person.getBirthdate(), person.getGender(),
                                  new Address(person.getAddress().getStreet(),
                                              person.getAddress().getStreetNumber(),
                                              person.getAddress().getZipCode(),
                                              person.getAddress().getCity(),
                                              person.getAddress().getCountry())))
        .collect(Collectors.toList());
  }

  private List<Person> personFileIOToPerson(List<Person> persons) {
    return persons.stream()
        .map(p -> new Person(null, new Address(null, p.getAddress().getStreet(),
                                               p.getAddress().getStreetNumber(),
                                               p.getAddress().getZipCode(),
                                               p.getAddress().getCity(),
                                               p.getAddress().getCountry()),
                             p.getFirstName(),
                             p.getLastName(),
                             p.getBirthdate(),
                             p.getGender()))
        .collect(Collectors.toList());
  }

  private void delete(Person person) {
    try {
      model.delete(person.getId_person(), this);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
    view.updateTableRemove(person);
  }

  private boolean isValidFileExtension(String path) {
    String[] splitedPath = path.split("\\.");
    return ControllerConst.XML.equals(splitedPath[splitedPath.length - 1]) || ControllerConst.CSV.equals(splitedPath[splitedPath.length - 1])
           || ControllerConst.JSON.equals(splitedPath[splitedPath.length - 1]);
  }

  private Model getSqlModel() {
    return sqlModel;
  }

  private Model getJpaModel() {
    return jpaModel;
  }

  private PersonDialog getPersonDialog() {
    if (personDialog == null) {
      personDialog = new PersonDialog(view);
      personDialog.requestFocus();
      personDialog.initUI(this, true);
      personDialog.clearTextfield();
    }
    return personDialog;
  }

  private void loadFileDialog() {
    if (getLastState() == State.SAVE) {
      if (!getAllPeople().isEmpty()) {
        getFileDialog().setVisible(true);
      }
    } else {
      getFileDialog().setVisible(true);
    }
  }

  private FileDialog getFileDialog() {
    if (fileDialog == null) {
      fileDialog = new FileDialog();
      fileDialog.requestFocus();
      fileDialog.initUI(this);
      fileDialog.clearModel();
    }
    return fileDialog;
  }

  private FileXmlChoiceDialog getFileXmlChoiceDialog() {
    if (fileXmlChoiceDialog == null) {
      fileXmlChoiceDialog = new FileXmlChoiceDialog();
      fileXmlChoiceDialog.initUI(this);
    }
    return fileXmlChoiceDialog;
  }

  private PersonIOFactory getFactory() {
    if (factory == null) {
      factory = new PersonIOFactory();
    }
    return factory;
  }

  private ErrorDialog getErrorDialog() {
    if (errorDialog == null) {
      errorDialog = new ErrorDialog();
      errorDialog.initUI(this);
    }
    if (getLastState() != State.ERROR) {
      states.add(State.ERROR);
    }
    return errorDialog;
  }

  private ErrorDialog getErrorDialog(String title, String text) {
    if (errorDialog == null) {
      errorDialog = new ErrorDialog(title, text);
      errorDialog.initUI(this);
    } else {
      errorDialog.setErrortitle(title);
      errorDialog.setErrormessage(text);
    }
    if (getLastState() != State.ERROR) {
      states.add(State.ERROR);
    }
    return errorDialog;
  }

  private void closeFrames() {
    if (getLastState() == State.ERROR) {
      closeErrorDialog();
      states.add(states.get(states.size() - 2));
    } else if (getLastState() == State.SAVE || getLastState() == State.LOAD) {
      if (getFileDialog() != null) {
        closeFileDialog();
      }
      closeFileDialog();
    } else {
      closePersonDialog();
    }
  }

  private void closePersonDialog() {
    getPersonDialog().setVisible(false);
    getPersonDialog().clearTextfield();
    Map<String, Rows> rowsMap = getPersonDialog().getDetailPanel().getRowsMap();
    rowsMap.values().
        forEach(row -> row.getTextField().setBorder(Borders.standardBorder));
  }

  private void closeFileDialog() {
    fileDialog.setVisible(false);
  }

  private void closeErrorDialog() {
    if (errorDialog != null) {
      getErrorDialog().setVisible(false);
    }
  }

  private void closeFileXmlChoiceDialog() {
    if (fileXmlChoiceDialog != null) {
      getFileXmlChoiceDialog().setVisible(false);
    }
  }

  private State getLastState() {
    return states.get(states.size() - 1);
  }

  @Override
  public void addState(State state) {
    states.add(state);
  }

  @Override
  public List<Person> getAllPeople() {
    try {
      return model.findAllPerson(this);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void valueChanged(ListSelectionEvent e) {
    selectedRow = view.getTable().getSelectedRow();
    if (selectedRow != -1) {
      view.enableModifyDelete();
      selectedPerson = getAllPeople().get(selectedRow);
      if (selectedPerson != null) {
        if (detailPanel == null) {
          detailPanel = view.getDetailPanel();
        }
        detailPanel.setModel(selectedPerson);
        detailPanel.clearTextfield();
        detailPanel.fillTextfield();
      }
    } else {
      view.disableModifyDelete();
    }
  }

  @Override
  public void accept(Throwable throwable) {
    System.out.println(throwable.getMessage());
    throw new RuntimeException(throwable);
  }
}