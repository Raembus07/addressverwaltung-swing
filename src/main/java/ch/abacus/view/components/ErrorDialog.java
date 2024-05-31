/*
 * Errormessage.java
 *
 * Creator:
 * 16.04.2024 12:09 josia.schweizer
 *
 * Maintainer:
 * 16.04.2024 12:09 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.view.components;

import ch.abacus.common.UITextConst;
import ch.abacus.common.ViewConst;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;

public class ErrorDialog extends JDialog {

  private JLabel titleLbl;
  private JLabel messageLbl;
  private String errortitle = UITextConst.NOTHING;
  private String errormessage = UITextConst.NOTHING;
  private final transient CreateBtn createBtn = new CreateBtn();

  public ErrorDialog(String errortitle, String errormessage) {
    this.errortitle = errortitle;
    this.errormessage = errormessage;
  }

  public ErrorDialog() {
  }

  public void initUI(ActionListener listener) {
    this.setSize(400, 150);
    this.setLayout(new BorderLayout());
    this.setLocationRelativeTo(null);
    this.setBackground(Color.RED);
    this.setResizable(false);
    this.setTitle(UITextConst.ERROR);
    this.setModal(true);
    this.setResizable(false);

    this.add(createContentPanel(), BorderLayout.CENTER);
    this.add(createBtnPanel(listener), BorderLayout.SOUTH);
  }

  private JPanel createContentPanel() {
    JPanel contentPanel = new JPanel(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPanel.setBackground(Color.RED);

    JPanel titlePnl = new JPanel();
    titlePnl.setBackground(Color.RED);
    titleLbl = new JLabel(errortitle);
    titleLbl.setFont(new Font(UITextConst.FONT, Font.BOLD, 18));
    titleLbl.setBackground(Color.RED);
    titleLbl.setForeground(Color.BLACK);
    titlePnl.add(titleLbl);

    JPanel messagePnl = new JPanel();
    messagePnl.setBackground(Color.RED);
    messageLbl = new JLabel(errormessage);
    messageLbl.setFont(new Font(UITextConst.FONT, Font.PLAIN, 14));
    messageLbl.setBackground(Color.RED);
    messageLbl.setForeground(Color.BLACK);
    messagePnl.add(messageLbl);

    contentPanel.add(titlePnl, BorderLayout.NORTH);
    contentPanel.add(messagePnl, BorderLayout.CENTER);

    return contentPanel;
  }

  private JPanel createBtnPanel(ActionListener listener) {
    JPanel btnPanel = new JPanel();
    btnPanel.setBackground(Color.RED);
    JButton okBtn = createBtn.createBtn(UITextConst.OK, ViewConst.RETURNDIALOG, listener);
    okBtn.setBackground(Color.WHITE);
    okBtn.setForeground(Color.BLACK);
    btnPanel.add(okBtn);
    return btnPanel;
  }

  public void setErrormessage(String errormessage) {
    this.errormessage = errormessage;
    messageLbl.setText(errormessage);
  }

  public void setErrortitle(String errortitle) {
    this.errortitle = errortitle;
    titleLbl.setText(errortitle);
  }
}
