/*
 * CreateBtn.java
 *
 * Creator:
 * 16.04.2024 07:24 josia.schweizer
 *
 * Maintainer:
 * 16.04.2024 07:24 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.view.components;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.Serializable;

public class CreateBtn implements Serializable {

  public JButton createBtn(String title, String btnConst, ActionListener listener) {
    JButton button = new JButton(title);
    button.setActionCommand(btnConst);
    button.setSize(80, 30);
    button.setFocusable(false);
    addActionListeners(listener, button);
    return button;
  }

  private void addActionListeners(ActionListener listener, JButton button) {
    button.addActionListener(listener);
  }
}