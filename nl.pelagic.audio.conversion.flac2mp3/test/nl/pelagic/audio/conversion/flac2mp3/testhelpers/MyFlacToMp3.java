package nl.pelagic.audio.conversion.flac2mp3.testhelpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import nl.pelagic.audio.conversion.flac2mp3.api.Flac2Mp3Configuration;
import nl.pelagic.audio.conversion.flac2mp3.api.FlacToMp3;

import org.junit.Ignore;

@Ignore
@SuppressWarnings({
    "nls", "javadoc"
})
public class MyFlacToMp3 implements FlacToMp3 {
  public static final String msg = "BOOM";

  public int countAll = 0;
  public int countNormal = 0;
  public boolean throwException = false;
  public boolean retval = true;

  @Override
  public boolean convert(Flac2Mp3Configuration configuration, File flac, File mp3, boolean simulate)
      throws FileNotFoundException {
    countAll++;
    if (throwException) {
      throw new FileNotFoundException(msg);
    }
    if (retval && !simulate) {
      try (FileWriter fw = new FileWriter(mp3)) {
        fw.write(mp3.getPath());
      }
      catch (IOException e) {
        throw new FileNotFoundException(e.getLocalizedMessage());
      }
    }

    countNormal++;
    return retval;
  }
}
