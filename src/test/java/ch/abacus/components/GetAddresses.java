/*
 * GetAddresses.java
 *
 * Creator:
 * 15.05.2024 09:49 josia.schweizer
 *
 * Maintainer:
 * 15.05.2024 09:49 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.components;

import ch.abacus.db.entity.Address;

public class GetAddresses {

  public Address getAddress() {
    return new Address(null, "Schwarzenbach", "2178",
                       9200, "Gossau", "Schweiz");
  }

  public Address getAddress2() {
    return new Address(null, "hund", "12a", 9209,
                       "hunddorf", "hundland");
  }

  public Address getAddress3() {
    return new Address(null, "katze", "12a", 9209,
                       "katzenstadt", "katzenland");
  }
}