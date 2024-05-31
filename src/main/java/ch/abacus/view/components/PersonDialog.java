  /*
   * PersonDialog.java
   *
   * Creator:
   * 04.04.2024 16:25 josia.schweizer
   *
   * Maintainer:
   * 04.04.2024 16:25 josia.schweizer
   *
   * Last Modification:
   * $Id:$
   *
   * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
   */
  package ch.abacus.view.components;

  import ch.abacus.common.UITextConst;
  import ch.abacus.common.ViewConst;
  import ch.abacus.db.entity.Person;
  import ch.abacus.view.View;

  import javax.swing.JDialog;
  import javax.swing.JPanel;
  import java.awt.BorderLayout;
  import java.awt.GridBagConstraints;
  import java.awt.GridBagLayout;
  import java.awt.Insets;
  import java.awt.event.ActionListener;

  public class PersonDialog extends JDialog {

    private final transient View view;
    private PersonDetailPanel detailPanel;
    private final CreateBtn createBtn = new CreateBtn();

    public PersonDialog(View view) {
      this.view = view;
    }

    public void initUI(ActionListener listener, boolean enabled) {
      this.setLayout(new BorderLayout());
      this.setSize(500, 600);
      this.setLocationRelativeTo(null);
      this.setModal(true);
      this.setResizable(false);

      if (detailPanel == null) {
        detailPanel = new PersonDetailPanel(view);
      }
      add(detailPanel.initUI(enabled), BorderLayout.CENTER);

      add(createBtnPanel(listener), BorderLayout.SOUTH);
    }

    private JPanel createBtnPanel(ActionListener listener) {
      JPanel btnPanel = new JPanel(new GridBagLayout());
      btnPanel.add(createBtn.createBtn(UITextConst.OK, ViewConst.OKDIALOG, listener), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
      btnPanel.add(createBtn.createBtn(UITextConst.EXIT, ViewConst.RETURNDIALOG, listener), new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
      return btnPanel;
    }

    public void setFocusToFirstTextField() {
      detailPanel.setFocusToFirstTextField();
    }

    public void addInputVerifier() {
      detailPanel.addInputVerifiers();
    }

    public void removeInputVerifiers() {
      detailPanel.removeInputVerifiers();
    }

    public void fillModel() {
      detailPanel.fillModel();
    }

    public Person getModel() {
      return detailPanel.getModel();
    }

    public void clearTextfield() {
      detailPanel.clearTextfield();
    }

    public PersonDetailPanel getDetailPanel() {
      return detailPanel;
    }
  }