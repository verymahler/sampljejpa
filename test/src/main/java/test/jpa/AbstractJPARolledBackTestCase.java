package test.jpa;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractJPARolledBackTestCase {
  private static EntityManagerFactory entityManagerFactory;
  protected EntityManager entityManager;

  /**
   * <p>
   * Should return the unique name of the persistence unit that is used for unit tests.
   * </p>
   * <p>
   * Note that this name should be unique for all projects in the workspace since some way or
   * another the {@link Persistence} class sees all <code>persistence.xml</code> files in all
   * projects, even if these files are located in the directory <code>src/test/resources</code>.
   * This should be considered a bug and has last been noticed in <code>Eclipse 4.6.3</code> while
   * running <code>JDK1.8.0.121</code> and using the <code>Java EE 7.0 API</code>. If this bug has
   * been fixed, the {@link EntityManagerFactory} can be initialized in a static method annotated
   * with {@link org.junit.BeforeClass}. The persistence unit could then for example be named
   * <code>testPU</code> again for all projects.
   * </p>
   * <p>
   * Also note that this bug may manifest itself by loading the wrong configuration some of the
   * time, more or less randomly.
   * </p>
   */
  protected abstract String getPersistenceUnitName();

  @AfterClass
  public static void closeEntityManagerFactory() {
    if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
      entityManagerFactory.close();
    }
  }

  @Before
  public void beginTransaction() {
    entityManager = getEntityManagerFactory().createEntityManager();
    entityManager.getTransaction().begin();
  }

  @After
  public void rollbackTransaction() {
    if (entityManager.getTransaction().isActive()) {
      entityManager.getTransaction().rollback();
    }

    if (entityManager.isOpen()) {
      entityManager.close();
    }
  }

  protected void flushAndClear() {
    entityManager.flush();
    entityManager.clear();
  }

  private synchronized EntityManagerFactory getEntityManagerFactory() {
    if (entityManagerFactory == null || entityManagerFactory.isOpen() == false) {
      entityManagerFactory =
          Persistence.createEntityManagerFactory(checkNotNull(getPersistenceUnitName()));
    }
    return entityManagerFactory;
  }
}
