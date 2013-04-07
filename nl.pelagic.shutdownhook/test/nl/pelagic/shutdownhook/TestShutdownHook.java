package nl.pelagic.shutdownhook;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class TestShutdownHook {

  private ShutdownHook shutdownHook = null;
  private DummyShutdownHookParticipant dummyShutdownHookParticipant1 = null;
  private DummyShutdownHookParticipant dummyShutdownHookParticipant2 = null;

  @Before
  public void setUp() {
    shutdownHook = new ShutdownHook();
    dummyShutdownHookParticipant1 = new DummyShutdownHookParticipant();
    dummyShutdownHookParticipant2 = new DummyShutdownHookParticipant();
  }

  @After
  public void tearDown() {
    dummyShutdownHookParticipant1 = null;
    dummyShutdownHookParticipant2 = null;
    shutdownHook = null;
  }

  @Test
  public void test_NoParticipants() {
    shutdownHook.run();

    assertThat(Integer.valueOf(dummyShutdownHookParticipant1.callCount), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(dummyShutdownHookParticipant2.callCount), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void test_OneParticipant() {
    shutdownHook.addShutdownHookParticipant(dummyShutdownHookParticipant1);

    shutdownHook.run();

    assertThat(Integer.valueOf(dummyShutdownHookParticipant1.callCount), equalTo(Integer.valueOf(1)));
    assertThat(Integer.valueOf(dummyShutdownHookParticipant2.callCount), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void test_OneParticipantThrows() {
    shutdownHook.addShutdownHookParticipant(dummyShutdownHookParticipant1);
    dummyShutdownHookParticipant1.throwException = true;

    shutdownHook.run();

    assertThat(Integer.valueOf(dummyShutdownHookParticipant1.callCount), equalTo(Integer.valueOf(1)));
    assertThat(Integer.valueOf(dummyShutdownHookParticipant2.callCount), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void test_OneParticipantRemoved() {
    shutdownHook.addShutdownHookParticipant(dummyShutdownHookParticipant1);
    shutdownHook.removeShutdownHookParticipant(dummyShutdownHookParticipant1);

    shutdownHook.run();

    assertThat(Integer.valueOf(dummyShutdownHookParticipant1.callCount), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(dummyShutdownHookParticipant2.callCount), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void test_TwoParticipants() {
    shutdownHook.addShutdownHookParticipant(dummyShutdownHookParticipant1);
    shutdownHook.addShutdownHookParticipant(dummyShutdownHookParticipant2);

    shutdownHook.run();

    assertThat(Integer.valueOf(dummyShutdownHookParticipant1.callCount), equalTo(Integer.valueOf(1)));
    assertThat(Integer.valueOf(dummyShutdownHookParticipant2.callCount), equalTo(Integer.valueOf(1)));
  }

  @Test
  public void test_TwoParticipantsOneRemoved() {
    shutdownHook.addShutdownHookParticipant(dummyShutdownHookParticipant1);
    shutdownHook.addShutdownHookParticipant(dummyShutdownHookParticipant2);
    shutdownHook.removeShutdownHookParticipant(dummyShutdownHookParticipant1);

    shutdownHook.run();

    assertThat(Integer.valueOf(dummyShutdownHookParticipant1.callCount), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(dummyShutdownHookParticipant2.callCount), equalTo(Integer.valueOf(1)));
  }
}
