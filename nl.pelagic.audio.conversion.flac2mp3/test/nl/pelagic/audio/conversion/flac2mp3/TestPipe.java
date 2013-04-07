package nl.pelagic.audio.conversion.flac2mp3;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import nl.pelagic.audio.conversion.flac2mp3.testhelpers.PipeTestInputStream;
import nl.pelagic.audio.conversion.flac2mp3.testhelpers.PipeTestOutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings({
    "nls", "javadoc"
})
public class TestPipe {

  Pipe pipe = null;
  private PipeTestInputStream srcOutputStream = null;
  private PipeTestOutputStream dstInputStream = null;

  @Before
  public void setUp() {
    srcOutputStream = new PipeTestInputStream();
    dstInputStream = new PipeTestOutputStream();
    pipe = new Pipe(srcOutputStream, dstInputStream);
  }

  @After
  public void tearDown() {
    pipe = null;
    try {
      dstInputStream.close();
    }
    catch (IOException e) {
      /* swallow */
    }
    dstInputStream = null;
    try {
      srcOutputStream.close();
    }
    catch (IOException e) {
      /* swallow */
    }
    srcOutputStream = null;
  }

  @SuppressWarnings("unused")
  @Test(expected = IllegalArgumentException.class)
  public void testPipe_NullParameter1() {
    Pipe p = new Pipe(null, dstInputStream);
  }

  @SuppressWarnings("unused")
  @Test(expected = IllegalArgumentException.class)
  public void testPipe_NullParameter2() {
    Pipe p = new Pipe(srcOutputStream, null);
  }

  @SuppressWarnings({
      "static-method", "unused"
  })
  @Test(expected = IllegalArgumentException.class)
  public void testPipe_NullParameterBoth() {
    Pipe p = new Pipe(null, null);
  }

  @Test
  public void testGetExitValue_Default() {
    assertThat(Integer.valueOf(pipe.getExitValue()), equalTo(Integer.valueOf(Pipe.EXIT_OK)));
  }

  @Test
  public void testRun_AfterStop() {
    pipe.signalStop();
    pipe.run();
    assertThat(Integer.valueOf(pipe.getExitValue()), equalTo(Integer.valueOf(Pipe.EXIT_ERROR_INTERRUPTED)));
  }

  @Test
  public void testRun_ReadError() {
    String outstr1 = "testRun_ReadError: string 1";
    srcOutputStream.entries.add(new PipeTestInputStream.Entry(PipeTestInputStream.CMD_STRING, outstr1));
    srcOutputStream.entries.add(new PipeTestInputStream.Entry(PipeTestInputStream.CMD_EXCEPTION,
        "testRun_ReadError: throwExceptionDuringWrite"));

    pipe.run();
    assertThat(Integer.valueOf(pipe.getExitValue()), equalTo(Integer.valueOf(Pipe.EXIT_ERROR_READ)));
    assertThat(Integer.valueOf(dstInputStream.writtenStrings.size()), equalTo(Integer.valueOf(1)));
    assertThat(dstInputStream.writtenStrings.get(0), equalTo(outstr1));
  }

  @Test
  public void testRun_WriteError() {
    String outstr1 = "testRun_WriteError: string 1";
    srcOutputStream.entries.add(new PipeTestInputStream.Entry(PipeTestInputStream.CMD_STRING, outstr1));
    dstInputStream.throwExceptionDuringWrite = true;

    pipe.run();
    assertThat(Integer.valueOf(pipe.getExitValue()), equalTo(Integer.valueOf(Pipe.EXIT_ERROR_WRITE)));
  }

  @Test
  public void testRun_Normal() {
    String outstr1 = "testRun_Normal: string 1";
    srcOutputStream.entries.add(new PipeTestInputStream.Entry(PipeTestInputStream.CMD_STRING, outstr1));
    String outstr2 = "testRun_Normal: string 2";
    srcOutputStream.entries.add(new PipeTestInputStream.Entry(PipeTestInputStream.CMD_STRING, outstr2));
    srcOutputStream.entries.add(new PipeTestInputStream.Entry(PipeTestInputStream.CMD_EOF, null));

    pipe.run();
    assertThat(Integer.valueOf(pipe.getExitValue()), equalTo(Integer.valueOf(Pipe.EXIT_OK)));
    assertThat(Integer.valueOf(dstInputStream.writtenStrings.size()), equalTo(Integer.valueOf(2)));
    assertThat(dstInputStream.writtenStrings.get(0), equalTo(outstr1));
    assertThat(dstInputStream.writtenStrings.get(1), equalTo(outstr2));
  }

  @Test
  public void testRun_Normal_CloseExceptionRead() {
    srcOutputStream.throwExceptionDuringClose = true;
    String outstr1 = "testRun_Normal_CloseExceptionRead: string 1";
    srcOutputStream.entries.add(new PipeTestInputStream.Entry(PipeTestInputStream.CMD_STRING, outstr1));
    String outstr2 = "testRun_Normal_CloseExceptionRead: string 2";
    srcOutputStream.entries.add(new PipeTestInputStream.Entry(PipeTestInputStream.CMD_STRING, outstr2));
    srcOutputStream.entries.add(new PipeTestInputStream.Entry(PipeTestInputStream.CMD_EOF, null));

    pipe.run();
    assertThat(Integer.valueOf(pipe.getExitValue()), equalTo(Integer.valueOf(Pipe.EXIT_OK)));
    assertThat(Integer.valueOf(dstInputStream.writtenStrings.size()), equalTo(Integer.valueOf(2)));
    assertThat(dstInputStream.writtenStrings.get(0), equalTo(outstr1));
    assertThat(dstInputStream.writtenStrings.get(1), equalTo(outstr2));
  }

  @Test
  public void testRun_Normal_CloseExceptionWrite() {
    dstInputStream.throwExceptionDuringClose = true;
    String outstr1 = "testRun_Normal_CloseExceptionWrite: string 1";
    srcOutputStream.entries.add(new PipeTestInputStream.Entry(PipeTestInputStream.CMD_STRING, outstr1));
    String outstr2 = "testRun_Normal_CloseExceptionWrite: string 2";
    srcOutputStream.entries.add(new PipeTestInputStream.Entry(PipeTestInputStream.CMD_STRING, outstr2));
    srcOutputStream.entries.add(new PipeTestInputStream.Entry(PipeTestInputStream.CMD_EOF, null));

    pipe.run();
    assertThat(Integer.valueOf(pipe.getExitValue()), equalTo(Integer.valueOf(Pipe.EXIT_OK)));
    assertThat(Integer.valueOf(dstInputStream.writtenStrings.size()), equalTo(Integer.valueOf(2)));
    assertThat(dstInputStream.writtenStrings.get(0), equalTo(outstr1));
    assertThat(dstInputStream.writtenStrings.get(1), equalTo(outstr2));
  }

  class Stopper extends Thread {
    public AtomicBoolean busy = new AtomicBoolean(false);

    @Override
    public void run() {
      busy.set(true);
      pipe.signalStop();
    }
  }

  @Test
  public void testRun_StopDuringRead() {
    String outstr1 = "testRun_Normal_CloseExceptionWrite: string 1";
    srcOutputStream.entries.add(new PipeTestInputStream.Entry(PipeTestInputStream.CMD_STRING, outstr1));
    srcOutputStream.entries.add(new PipeTestInputStream.Entry(PipeTestInputStream.CMD_STALL, null));
    String outstr2 = "testRun_Normal_CloseExceptionWrite: string 2";
    srcOutputStream.entries.add(new PipeTestInputStream.Entry(PipeTestInputStream.CMD_STRING, outstr2));
    String outstr3 = "testRun_Normal_CloseExceptionWrite: string 3";
    srcOutputStream.entries.add(new PipeTestInputStream.Entry(PipeTestInputStream.CMD_STRING, outstr3));
    srcOutputStream.entries.add(new PipeTestInputStream.Entry(PipeTestInputStream.CMD_EOF, null));

    Thread t = new Thread(pipe);
    t.start();
    while (!srcOutputStream.stalled.get()) {
      try {
        Thread.sleep(1);
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    Stopper stopper = new Stopper();
    stopper.start();

    while (!stopper.busy.get()) {
      try {
        Thread.sleep(1);
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    /* to cover the interrupt during 'wait for idle' */
    stopper.interrupt();

    /* to cover the Thread.sleep(1) during 'wait for idle' */
    try {
      Thread.sleep(2);
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }

    srcOutputStream.unstall();
    while (srcOutputStream.stalled.get()) {
      try {
        Thread.sleep(1);
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    try {
      stopper.join();
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }

    try {
      t.join();
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }

    assertThat(Integer.valueOf(pipe.getExitValue()), equalTo(Integer.valueOf(Pipe.EXIT_ERROR_INTERRUPTED)));
    assertThat(Integer.valueOf(dstInputStream.writtenStrings.size()), equalTo(Integer.valueOf(1)));
    assertThat(dstInputStream.writtenStrings.get(0), equalTo(outstr1));
  }
}
