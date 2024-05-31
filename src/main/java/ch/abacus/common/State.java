/*
 * State.java
 *
 * Creator:
 * 05.04.2024 08:24 josia.schweizer
 *
 * Maintainer:
 * 05.04.2024 08:24 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.common;

public enum State {
  READ(),
  ADD(),
  DELETE(),
  MODIFY(),
  SAVE(),
  UNAVAILABLETOSAVE(),
  LOAD(),
  ERROR()
}
