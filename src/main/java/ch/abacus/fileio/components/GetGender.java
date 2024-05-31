/*
 * GetGender.java
 *
 * Creator:
 * 16.04.2024 10:02 josia.schweizer
 *
 * Maintainer:
 * 16.04.2024 10:02 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.fileio.components;

import ch.abacus.common.FileIOConst;
import ch.abacus.db.entity.Gender;

public class GetGender {

  public Gender getGender(String stringGender) {
    if (FileIOConst.MALE.equalsIgnoreCase(stringGender)) {
      return Gender.MALE;
    } else {
      return Gender.FEMALE;
    }
  }
}