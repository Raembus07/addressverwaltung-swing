/*
 * Main.java
 *
 * Creator:
 * 03.04.2024 14:14 josia.schweizer
 *
 * Maintainer:
 * 03.04.2024 14:14 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.main;

import ch.abacus.controller.Controller;
import ch.abacus.controller.ControllerImpl;

public class Main {
  public static void main(String[] args){
    Controller controller = new ControllerImpl();
    controller.init();
  }
}