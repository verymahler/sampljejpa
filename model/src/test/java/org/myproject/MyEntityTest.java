package org.myproject;

import static test.matchers.MoreMatchers.valid;
import static test.matchers.MoreMatchers.validEntityType;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import test.reflect.ReflectionTestUtils;

public class MyEntityTest {

  // System under test (SUT)
  private MyEntity myEntity;

  @Before
  public void setUp() throws Exception {
    myEntity = new MyEntity();
  }

  @Test
  public void testEntityType() {
    assertThat(MyEntity.class, is(validEntityType()));
  }

  @Test
  public void testValidates() {
    assertThat(myEntity, is(valid()));
  }

  @Test
  public void testValidatesNotIfBytesAreEmpty() {
    ReflectionTestUtils.setField(myEntity, byte[].class, null);

    assertThat(myEntity, is(not(valid())));
  }
}
