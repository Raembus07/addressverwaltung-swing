/*
 * FileDialog.java
 *
 * Creator:
 * 15.04.2024 15:42 josia.schweizer
 *
 * Maintainer:
 * 15.04.2024 15:42 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.view.components;

import ch.abacus.common.UITextConst;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.io.File;

public class FileDialog extends JDialog {

  private JPanel contentPanel;
  private JFileChooser fileChooser;
  private String model;

  public void initUI(ActionListener listener) {
    this.setLayout(new BorderLayout());
    this.setSize(500, 600);
    this.setLocationRelativeTo(null);
    this.setModal(true);
    this.setResizable(false);

    if (contentPanel == null) {
      contentPanel = new JPanel(new BorderLayout());
      contentPanel.add(createFileChooser(listener), BorderLayout.CENTER);
      add(contentPanel);
    }
  }

  private JPanel createFileChooser(ActionListener listener) {
    JPanel fileChooserPanel = new JPanel(new BorderLayout());

    FileNameExtensionFilter filter = new FileNameExtensionFilter(UITextConst.XMLCSVJSON, UITextConst.XML, UITextConst.CSV, UITextConst.JSON);
    fileChooser = new JFileChooser();
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    addActionListener(fileChooser, listener);

    File newfile = new File(UITextConst.NEWFILEDESTINATION);
    fileChooser.setCurrentDirectory(newfile);
    fileChooser.setFileFilter(filter);

    fileChooserPanel.add(fileChooser, BorderLayout.CENTER);

    return fileChooserPanel;
  }

  public void clearModel() {
    model = UITextConst.NOTHING;
  }

  public String getModel() {
    return model;
  }

  public void fillModel() {
    model = fileChooser.getSelectedFile().getAbsolutePath();
  }

  private void addActionListener(JFileChooser fileChooser, ActionListener listener) {
    fileChooser.addActionListener(listener);
  }
}