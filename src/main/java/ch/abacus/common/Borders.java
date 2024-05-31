/*
 * BordersConst.java
 *
 * Creator:
 * 29.05.2024 12:34 josia.schweizer
 *
 * Maintainer:
 * 29.05.2024 12:34 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.common;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.awt.Color;

public class Borders {

  private Borders() {

  }

  public static final Border standardBorder = BorderFactory.createLineBorder(Color.GRAY, 1);
  public static final Border redBorder = BorderFactory.createLineBorder(Color.RED, 3);
}