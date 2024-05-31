/*
 * GetFileExtension.java
 *
 * Creator:
 * 16.04.2024 09:46 josia.schweizer
 *
 * Maintainer:
 * 16.04.2024 09:46 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.fileio;

import ch.abacus.common.FileIOConst;

import java.util.Optional;

public class GetFileExtension {

  public Optional<String> getExtensionByStringHandling(String filename) {
    return Optional.ofNullable(filename)
        .filter(f -> f.contains(FileIOConst.DOT))
        .map(f -> f.substring(filename.lastIndexOf(FileIOConst.DOT) + 1));
  }
}