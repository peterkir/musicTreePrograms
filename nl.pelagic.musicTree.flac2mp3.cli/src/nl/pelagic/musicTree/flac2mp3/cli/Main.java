package nl.pelagic.musicTree.flac2mp3.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.pelagic.audio.conversion.flac2mp3.api.Flac2Mp3Configuration;
import nl.pelagic.audio.conversion.flac2mp3.api.FlacToMp3;
import nl.pelagic.audio.musicTree.configuration.api.MusicTreeConfiguration;
import nl.pelagic.audio.musicTree.configuration.api.MusicTreeConstants;
import nl.pelagic.audio.musicTree.syncer.api.Syncer;
import nl.pelagic.audio.musicTree.util.MusicTreeHelpers;
import nl.pelagic.musicTree.flac2mp3.cli.i18n.Messages;
import nl.pelagic.shell.script.listener.api.ShellScriptListener;
import nl.pelagic.shutdownhook.api.ShutdownHookParticipant;
import nl.pelagic.util.file.FileUtils;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.osgi.framework.BundleContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

/**
 * The main program that synchronises a flac tree into a mp3 tree or just
 * converts one or more flac files in mp3 files, based on a music tree
 * configuration
 */
@Component(properties = {
  "main.thread=true" /* Signal the launcher that this is the main thread */
})
public class Main implements Runnable, ShutdownHookParticipant {
  /** the application logger name */
  static private final String LOGGER_APPLICATION_NAME = "nl.pelagic"; //$NON-NLS-1$

  /** the application logger level to allow */
  static private final Level LOGGER_APPLICATION_LEVEL = Level.SEVERE;

  /** the jaudiotagger library logger name */
  static private final String LOGGER_JAUDIOTAGGER_NAME = "org.jaudiotagger"; //$NON-NLS-1$

  /** the jaudiotagger library logger level to allow */
  static private final Level LOGGER_JAUDIOTAGGER_LEVEL = Level.SEVERE;

  /** the program name */
  static final String PROGRAM_NAME = "flac2mp3"; //$NON-NLS-1$

  /** the application logger */
  private final Logger applicationLogger;

  /** the jaudiotagger library logger */
  private final Logger jaudiotaggerLogger;

  /** the list of extension to use in the flac tree */
  private final HashSet<String> extensionsList = new HashSet<>();

  /** the filenames for covers to use in the flac tree */
  private final HashSet<String> coversList = new HashSet<>();

  /*
   * Construction
   */

  /**
   * Default constructor
   */
  public Main() {
    super();

    /**
     * <pre>
     * 1=timestamp
     * 2=level
     * 3=logger
     * 4=class method
     * 5=message
     * 6=stack trace, preceded by a newline (if exception is present)
     * </pre>
     */
    System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] %4$10s %2$s : %5$s%6$s%n"); //$NON-NLS-1$ //$NON-NLS-2$

    applicationLogger = Logger.getLogger(LOGGER_APPLICATION_NAME);
    applicationLogger.setLevel(LOGGER_APPLICATION_LEVEL);

    jaudiotaggerLogger = Logger.getLogger(LOGGER_JAUDIOTAGGER_NAME);
    jaudiotaggerLogger.setLevel(LOGGER_JAUDIOTAGGER_LEVEL);

    extensionsList.add(MusicTreeConstants.FLACEXTENSION);
    coversList.add(MusicTreeConstants.COVER);
  }

  /*
   * Consumed Services
   */

  /** the shell script listener (optional) */
  private ShellScriptListener shellScriptListener = null;

  /**
   * @param shellScriptListener the shellScriptListener to set
   */
  @Reference
  void setShellScriptListener(ShellScriptListener shellScriptListener) {
    this.shellScriptListener = shellScriptListener;
  }

  /** the flac2Mp3 service */
  private FlacToMp3 flacToMp3 = null;

  /**
   * @param flacToMp3 the flacToMp3 to set
   */
  @Reference
  void setFlacToMp3(FlacToMp3 flacToMp3) {
    this.flacToMp3 = flacToMp3;
  }

  /** the syncer service */
  private Syncer syncer = null;

  /**
   * @param syncer the syncer to set
   */
  @Reference
  void setSyncer(Syncer syncer) {
    this.syncer = syncer;
  }

  /*
   * Command line arguments
   */

  /** the launcher arguments property name */
  static final String LAUNCHER_ARGUMENTS = "launcher.arguments"; //$NON-NLS-1$

  /** the command line arguments */
  private String[] args = null;

  /**
   * The bnd launcher provides access to the command line arguments via the
   * Launcher object. This object is also registered under Object.
   * 
   * @param done unused
   * @param parameters the launcher parameters, which includes the command line
   *          arguments
   */
  @Reference
  void setDone(@SuppressWarnings("unused") Object done, Map<String, Object> parameters) {
    args = (String[]) parameters.get(LAUNCHER_ARGUMENTS);
  }

  /*
   * Bundle
   */

  /** The setting name for the stayAlive property */
  public static final String SETTING_STAYALIVE = "stayAlive"; //$NON-NLS-1$

  /** true when the application should NOT automatically exit when done */
  private boolean stayAlive = false;

  /**
   * Bundle activator
   * 
   * @param bundleContext the bundle context
   */
  @Activate
  void activate(BundleContext bundleContext) {
    String ex = bundleContext.getProperty(PROGRAM_NAME + "." + SETTING_STAYALIVE); //$NON-NLS-1$
    if (ex != null) {
      stayAlive = Boolean.parseBoolean(ex);
    }
  }

  /**
   * Bundle deactivator
   */
  @Deactivate
  void deactivate() {
    /* nothing to do */
  }

  /*
   * Helpers
   */

  /**
   * Read a filelist file into a list of entries to convert
   * 
   * @param out the stream to print the error messages to
   * @param fileList the filelist file
   * @param entriesToConvert a list of files to convert, to which the files read
   *          from the filelist file must be added
   * @return true when successful
   */
  static boolean readFileList(PrintStream out, File fileList, List<String> entriesToConvert) {
    assert (out != null);
    assert (fileList != null);
    assert (entriesToConvert != null);

    if (!fileList.isFile()) {
      out.printf(Messages.getString("Main.8"), fileList.getPath()); //$NON-NLS-1$
      return false;
    }

    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileList), "UTF-8")); //$NON-NLS-1$
      String line = null;

      while ((line = reader.readLine()) != null) {
        /* skip empty lines */
        if (line.trim().isEmpty()) {
          continue;
        }
        entriesToConvert.add(line);
      }
    }
    catch (IOException e) {
      /* can't be covered in a test */
      out.printf(Messages.getString("Main.11"), fileList, e.getLocalizedMessage()); //$NON-NLS-1$
      return false;
    }
    finally {
      if (reader != null) {
        try {
          reader.close();
        }
        catch (IOException e) {
          /* swallow, can't be covered in a test */
        }
        reader = null;
      }
    }

    return true;
  }

  /**
   * Validate the entry to convert to filter out non-existing directories and
   * files. An entry to convert must exist and be below the flac base directory.
   * 
   * @param out the stream to print the error messages to
   * @param musicTreeConfiguration the music tree configuration
   * @param entryToConvert the entry to convert
   * @return true when validation is successful
   */
  static boolean validateEntryToConvert(PrintStream out, MusicTreeConfiguration musicTreeConfiguration,
      File entryToConvert) {
    assert (out != null);
    assert (musicTreeConfiguration != null);
    assert (entryToConvert != null);

    /* check that the entry exists */
    if (!entryToConvert.exists()) {
      out.printf(Messages.getString("Main.5"), entryToConvert.getPath()); //$NON-NLS-1$
      return false;
    }

    /*
     * check that entry is below the flac base directory so that it doesn't
     * escape the base directory by doing a ../../..
     */
    if (!FileUtils.isFileBelowDirectory(musicTreeConfiguration.getFlacBaseDir(), entryToConvert, true)) {
      out.printf(Messages.getString("Main.6"), //$NON-NLS-1$
          entryToConvert.getPath(), musicTreeConfiguration.getFlacBaseDir().getPath());
      return false;
    }

    return true;
  }

  /**
   * Convert a flac file into an mp3 file.
   * 
   * @param err the stream to print to
   * @param flac2Mp3Configuration the conversion configuration. When null then
   *          the default configuration is used.
   * @param musicTreeConfiguration the music tree configuration
   * @param simulate true to simulate conversion
   * @param fileToConvert the flac file to convert
   * 
   * @return true when successful
   */
  boolean convertFile(PrintStream err, Flac2Mp3Configuration flac2Mp3Configuration,
      MusicTreeConfiguration musicTreeConfiguration, boolean simulate, File fileToConvert) {
    assert (err != null);
    assert (flac2Mp3Configuration != null);
    assert (musicTreeConfiguration != null);
    assert (fileToConvert != null);
    assert (fileToConvert.isFile());

    File mp3File = MusicTreeHelpers.flacFileToMp3File(musicTreeConfiguration, fileToConvert);
    if (mp3File == null) {
      err.printf(Messages.getString("Main.12"), fileToConvert.getPath(), //$NON-NLS-1$
          musicTreeConfiguration.getMp3BaseDir().getPath());
      return false;
    }

    boolean doConversion = !mp3File.exists() || (fileToConvert.lastModified() > mp3File.lastModified());
    if (!doConversion) {
      return true;
    }

    boolean converted = false;
    try {
      converted = flacToMp3.convert(flac2Mp3Configuration, fileToConvert, mp3File, simulate);
      if (!converted) {
        err.printf(Messages.getString("Main.1"), fileToConvert.getPath()); //$NON-NLS-1$
      }
    }
    catch (IOException e) {
      converted = false;
      err.printf(Messages.getString("Main.2"), fileToConvert.getPath(), e.getLocalizedMessage()); //$NON-NLS-1$
    }

    return converted;
  }

  /**
   * Stay alive, if needed (which is when the component has a SETTING_STAYALIVE
   * property set to true).
   * 
   * @param err the stream to print a 'staying alive' message to
   */
  void stayAlive(PrintStream err) {
    if (!stayAlive) {
      return;
    }

    err.printf(Messages.getString("Main.7")); //$NON-NLS-1$

    try {
      Thread.sleep(Long.MAX_VALUE);
    }
    catch (InterruptedException e) {
      /* swallow */
    }
  }

  /*
   * ShutdownHookParticipant
   */

  /** true when we have to stop */
  private AtomicBoolean stop = new AtomicBoolean(false);

  @Override
  public void shutdownHook() {
    stop.set(true);
  }

  /*
   * Main
   */

  /**
   * Run the main program
   * 
   * @param err the stream to print errors to
   * @return true when successful
   */
  boolean doMain(PrintStream err) {
    if (args == null) {
      /*
       * the launcher didn't set our command line options so set empty arguments
       * (use defaults)
       */
      args = new String[0];
    }

    /*
     * Parse the command line
     */

    CommandLineOptions commandLineOptions = new CommandLineOptions();
    CmdLineParser parser = new CmdLineParser(commandLineOptions);
    try {
      parser.parseArgument(args);
    }
    catch (CmdLineException e) {
      err.printf(Messages.getString("Main.4"), e.getLocalizedMessage()); //$NON-NLS-1$
      commandLineOptions.setHelp(true);
    }

    /*
     * Process command-line options
     */

    /* print usage when so requested and exit */
    if (commandLineOptions.isHelp()) {
      try {
        /* can't be covered by a test */
        int cols = Integer.parseInt(System.getenv("COLUMNS")); //$NON-NLS-1$
        if (cols > 80) {
          parser.setUsageWidth(cols);
        }
      }
      catch (NumberFormatException e) {
        /* swallow, can't be covered by a test */
      }

      CommandLineOptions.usage(err, PROGRAM_NAME, parser);
      return false;
    }

    /*
     * Setup verbose modes in the shell script listener
     */
    shellScriptListener.setVerbose(commandLineOptions.isVerbose(), commandLineOptions.isExtraVerbose(),
        commandLineOptions.isQuiet());

    /*
     * Setup & validate the music tree configuration
     */

    MusicTreeConfiguration musicTreeConfiguration =
        new MusicTreeConfiguration(commandLineOptions.getFlacBaseDir(), commandLineOptions.getMp3BaseDir());

    List<String> errors = musicTreeConfiguration.validate(true);
    if (errors != null) {
      for (String error : errors) {
        err.println(error);
      }
      return false;
    }

    /*
     * Setup & validate the flac2mp3 configuration
     */

    Flac2Mp3Configuration flac2Mp3Configuration = new Flac2Mp3Configuration();
    flac2Mp3Configuration.setFlacExecutable(commandLineOptions.getFlacExecutable().getPath());
    flac2Mp3Configuration.setLameExecutable(commandLineOptions.getLameExecutable().getPath());
    flac2Mp3Configuration.setFlacOptions(commandLineOptions.getFlacOptions());
    flac2Mp3Configuration.setLameOptions(commandLineOptions.getLameOptions());

    errors = flac2Mp3Configuration.validate();
    if (errors != null) {
      /* can't be covered by a test */
      for (String error : errors) {
        err.println(error);
      }
      return false;
    }

    /*
     * Setup the entries to convert: first get them from the command-line (if
     * specified) and then add those in the file list (if set)
     */

    List<String> entriesToConvert = commandLineOptions.getEntriesToConvert();

    File fileList = commandLineOptions.getFileList();
    if (fileList != null) {
      readFileList(err, fileList, entriesToConvert);
    }

    if (entriesToConvert.isEmpty()) {
      /*
       * no entries to convert, so default to the flac base directory: sync the
       * whole tree
       */
      entriesToConvert.add(musicTreeConfiguration.getFlacBaseDir().getAbsolutePath());
    }

    /*
     * Run
     */

    boolean result = true;
    for (String entryToConvert : entriesToConvert) {
      if (stop.get()) {
        break;
      }

      File entryToConvertFile = new File(entryToConvert);
      boolean validationResult = validateEntryToConvert(err, musicTreeConfiguration, entryToConvertFile);
      if (validationResult) {
        if (entryToConvertFile.isDirectory()) {
          result =
              result
                  && syncer.syncFlac2Mp3(flac2Mp3Configuration, musicTreeConfiguration, entryToConvertFile,
                      extensionsList, coversList, commandLineOptions.isSimulate());
        } else if (entryToConvertFile.isFile()) {
          result =
              result
                  && convertFile(err, flac2Mp3Configuration, musicTreeConfiguration, commandLineOptions.isSimulate(),
                      entryToConvertFile);
        } else {
          /* can't be covered by a test */
          err.printf(Messages.getString("Main.3"), entryToConvert); //$NON-NLS-1$
        }
      } else {
        result = false;
      }
    }

    return result;
  }

  /*
   * Since we're registered as a Runnable with the main.thread property we get
   * called when the system is fully initialised.
   */
  @Override
  public void run() {
    boolean success = doMain(System.err);

    stayAlive(System.err);

    if (!success) {
      /* can't be covered by a test */
      System.exit(1);
    }
  }
}
