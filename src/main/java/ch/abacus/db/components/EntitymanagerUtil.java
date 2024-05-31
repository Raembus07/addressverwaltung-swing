/*
 * EntityManagerUtil.java
 *
 * Creator:
 * 16.05.2024 11:54 josia.schweizer
 *
 * Maintainer:
 * 16.05.2024 11:54 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.db.components;

import ch.abacus.common.DbConst;
import ch.abacus.common.JPAStatments;
import ch.abacus.db.entity.Address;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class EntitymanagerUtil {

  private static EntitymanagerUtil entitymanagerUtil = null;
  private final EntityManager entityManager;

  public static EntitymanagerUtil getInstance() {
    if (entitymanagerUtil == null) {
      entitymanagerUtil = new EntitymanagerUtil();
    }
    return entitymanagerUtil;
  }

  public static void setInstance(EntitymanagerUtil entitymanagerUtil) {
    EntitymanagerUtil.entitymanagerUtil = entitymanagerUtil;
  }

  public EntitymanagerUtil() {
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(DbConst.TITLE);
      entityManager = entityManagerFactory.createEntityManager();
  }

  public EntityManager getEm() {
    return entityManager;
  }

  public EntityTransaction begin() {
    EntityTransaction transaction = getEm().getTransaction();
    if (transaction.isActive()) {
      transaction.rollback();
    }
    transaction.begin();
    return transaction;
  }

  public void commitTransaction() {
    EntityTransaction transaction = getEm().getTransaction();
    if (!transaction.getRollbackOnly()) {
      transaction.commit();
    }
  }

  public void clearDatabase() {

    EntityManager em = getEm();
    Query query;

    begin();

    query = em.createQuery(JPAStatments.DELETE_ALL_FROM_PERSON);
    query.executeUpdate();

    query = em.createQuery(JPAStatments.DELETE_ALL_FROM_ADDRESS);
    query.executeUpdate();

    em.createQuery(JPAStatments.SELECT_ADDRESS, Address.class).getResultList();

    em.flush();
    commitTransaction();
  }
}