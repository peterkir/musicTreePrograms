package nl.pelagic.audio.conversion.flac2mp3.api;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import nl.pelagic.audio.conversion.flac2mp3.api.i18n.Messages;

/**
 * Configuration for the flac to mp3 conversion. Holds options for the flac and
 * lame processes that actually perform the conversion.
 */
public class Flac2Mp3Configuration {
  /** default flac executable */
  public static final String DEFAULT_FLAC_EXECUTABLE = "flac"; //$NON-NLS-1$

  /** default flac options */
  public static final String DEFAULT_FLAC_OPTIONS = "-s -d -c"; //$NON-NLS-1$

  /** default lame executable */
  public static final String DEFAULT_LAME_EXECUTABLE = "lame"; //$NON-NLS-1$

  /** default lame options */
  public static final String DEFAULT_LAME_OPTIONS = "-S -h -b 320"; //$NON-NLS-1$

  /** flac executable */
  private String flacExecutable;

  /** flac options */
  private List<String> flacOptions = new LinkedList<>();

  /** flac executable */
  private String lameExecutable;

  /** lame options */
  private List<String> lameOptions = new LinkedList<>();

  /**
   * Constructor. Sets default options.
   */
  public Flac2Mp3Configuration() {
    setFlacExecutable(DEFAULT_FLAC_EXECUTABLE);
    setFlacOptions(DEFAULT_FLAC_OPTIONS);
    setLameExecutable(DEFAULT_LAME_EXECUTABLE);
    setLameOptions(DEFAULT_LAME_OPTIONS);
  }

  /**
   * Try to run the program
   * 
   * @param program the program to run
   * @return true when running the program was successful
   */
  static boolean tryProgramRun(String[] program) {
    if ((program == null) || (program[0] == null) || (program[0].isEmpty())) {
      return false;
    }

    Process programProcess = null;
    try {
      ProcessBuilder programProcessBuilder = new ProcessBuilder(program);
      programProcess = programProcessBuilder.start();
    }
    catch (Exception e) {
      /* swallow */
      return false;
    }
    finally {
      boolean complete = false;
      while (!complete && (programProcess != null)) {
        try {
          programProcess.waitFor();
          complete = true;
        }
        catch (InterruptedException e) {
          /* swallow & can't be covered by a test */
        }
      }
    }

    return true;
  }

  /**
   * Validate the configuration. Currently only checks that the flac and lame
   * executables are actually executable by trying to execute them with a 'help'
   * argument ("-h" and "--help" for flac and lame respectively)
   * 
   * @return A list with errors, or null when validated
   */
  public List<String> validate() {
    List<String> result = new LinkedList<>();

    String[] flacProgram = {
        flacExecutable, "-h" //$NON-NLS-1$
    };
    if (!tryProgramRun(flacProgram)) {
      result.add(String.format(Messages.getString("Flac2Mp3Configuration.0"), flacExecutable)); //$NON-NLS-1$
    }

    String[] lameProgram = {
        lameExecutable, "--help" //$NON-NLS-1$
    };
    if (!tryProgramRun(lameProgram)) {
      result.add(String.format(Messages.getString("Flac2Mp3Configuration.1"), lameExecutable)); //$NON-NLS-1$
    }

    if (result.size() == 0) {
      return null;
    }

    return result;
  }

  /**
   * @return the flacExecutable
   */
  public String getFlacExecutable() {
    return flacExecutable;
  }

  /**
   * @param flacExecutable the flacExecutable to set
   */
  public void setFlacExecutable(String flacExecutable) {
    this.flacExecutable = flacExecutable;
  }

  /**
   * @return the flacOptions
   */
  public List<String> getFlacOptions() {
    return flacOptions;
  }

  /**
   * @param flacOptions the flacOptions to set. Options are whitespace
   *          separated.
   */
  public void setFlacOptions(String flacOptions) {
    this.flacOptions = Arrays.asList(flacOptions.trim().split("\\s+")); //$NON-NLS-1$
  }

  /**
   * @return the lameExecutable
   */
  public String getLameExecutable() {
    return lameExecutable;
  }

  /**
   * @param lameExecutable the lameExecutable to set
   */
  public void setLameExecutable(String lameExecutable) {
    this.lameExecutable = lameExecutable;
  }

  /**
   * @return the lameOptions
   */
  public List<String> getLameOptions() {
    return lameOptions;
  }

  /**
   * @param lameOptions the lameOptions to set. Options are whitespace
   *          separated.
   */
  public void setLameOptions(String lameOptions) {
    this.lameOptions = Arrays.asList(lameOptions.trim().split("\\s+")); //$NON-NLS-1$
  }
}
