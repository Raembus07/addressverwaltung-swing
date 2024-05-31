/*
 * FileXmlChoice.java
 *
 * Creator:
 * 16.04.2024 10:25 josia.schweizer
 *
 * Maintainer:
 * 16.04.2024 10:25 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.view.components;

import ch.abacus.fileio.components.XMLWRITE;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class FileXmlChoiceDialog extends JDialog {

  private JPanel contentPanel;

  private transient ActionListener listener;
  private final List<JRadioButton> radioButtons = new ArrayList<>();

  public FileXmlChoiceDialog() {
  }

  public void initUI(ActionListener listener) {
    this.listener = listener;
    this.setLayout(new BorderLayout());
    this.setSize(200, 100);
    this.setLocationRelativeTo(null);
    this.setModal(true);
    this.setResizable(false);

    if (contentPanel == null) {
      contentPanel = new JPanel();

      createRadioBtn(contentPanel);

      add(contentPanel, BorderLayout.CENTER);
    }
  }

  private void createRadioBtn(JPanel contentPanel) {
    JRadioButton jaxb;
    JRadioButton stax;
    JRadioButton dom;
    jaxb = new JRadioButton(String.valueOf(XMLWRITE.JAXB));
    radioButtons.add(jaxb);
    stax = new JRadioButton(String.valueOf(XMLWRITE.STAX));
    radioButtons.add(stax);
    dom = new JRadioButton(String.valueOf(XMLWRITE.DOM));
    radioButtons.add(dom);

    ButtonGroup group = new ButtonGroup();
    for (JRadioButton radioButton : radioButtons) {
      group.add(radioButton);
      contentPanel.add(radioButton);
      addActionListener(radioButton);
      radioButton.setFocusable(false);
    }
  }

  private void addActionListener(JRadioButton radioButton) {
    radioButton.addActionListener(listener);
  }

  public void clearRadioBtn() {
    for (JRadioButton radioButton : radioButtons) {
      radioButton.setSelected(false);
    }
  }
}