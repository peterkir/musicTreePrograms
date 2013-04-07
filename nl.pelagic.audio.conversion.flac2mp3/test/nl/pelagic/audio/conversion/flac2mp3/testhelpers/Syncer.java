package nl.pelagic.audio.conversion.flac2mp3.testhelpers;

import java.util.concurrent.atomic.AtomicBoolean;

import nl.pelagic.audio.conversion.flac2mp3.FlacToMp3Impl;
import nl.pelagic.shell.script.listener.testhelpers.MyShellScriptListener;

import org.junit.Ignore;

@SuppressWarnings({
    "nls", "javadoc"
})
@Ignore
public class Syncer extends Thread {
  private MyShellScriptListener myShellScriptListener = null;
  FlacToMp3Impl flacToMp3Impl = null;

  public AtomicBoolean busy = new AtomicBoolean(false);
  AtomicBoolean stopperbusy = new AtomicBoolean(false);

  public Syncer(MyShellScriptListener myShellScriptListener, FlacToMp3Impl flacToMp3Impl) {
    super();
    this.myShellScriptListener = myShellScriptListener;
    this.flacToMp3Impl = flacToMp3Impl;
    this.setName(this.getClass().getSimpleName());
  }

  @Override
  public void run() {
    busy.set(true);

    while (!myShellScriptListener.stalled.get()) {
      try {
        Thread.sleep(1);
      }
      catch (InterruptedException e) {
        /* swallow */
      }
    }

    Thread stopper = new Thread() {
      @Override
      public void run() {
        this.setName("Stopper");
        stopperbusy.set(true);
        flacToMp3Impl.shutdownHook();
      }
    };
    stopper.start();

    while (!stopperbusy.get()) {
      try {
        Thread.sleep(1);
      }
      catch (InterruptedException e) {
        /* swallow */
      }
    }

    myShellScriptListener.stalled.set(false);
  }
}
