package nl.pelagic.musicTree.flac2mp3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.pelagic.audio.conversion.flac2mp3.api.Flac2Mp3Configuration;
import nl.pelagic.audio.conversion.flac2mp3.api.FlacToMp3;
import nl.pelagic.audio.musicTree.configuration.api.MusicTreeConfiguration;
import nl.pelagic.audio.musicTree.configuration.api.MusicTreeConstants;
import nl.pelagic.audio.musicTree.syncer.api.Syncer;
import nl.pelagic.musicTree.flac2mp3.i18n.Messages;
import nl.pelagic.shell.script.listener.api.ShellScriptListener;
import nl.pelagic.util.file.ExtensionUtils;
import nl.pelagic.util.file.FileUtils;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.osgi.framework.BundleContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

/**
 * The main program that synchronises a flac tree into a mp3 tree
 */
@Component(properties = {
  "main.thread=true" /* Signal the launcher that this is the main thread */
})
public class Main implements Runnable {
  /** the application logger name */
  private final String LOGGER_APPLICATION_NAME = "nl.pelagic"; //$NON-NLS-1$

  /** the application logger level to allow */
  private final Level LOGGER_APPLICATION_LEVEL = Level.SEVERE;

  /** the jaudiotagger library logger name */
  private final String LOGGER_JAUDIOTAGGER_NAME = "org.jaudiotagger"; //$NON-NLS-1$

  /** the jaudiotagger library logger level to allow */
  private final Level LOGGER_JAUDIOTAGGER_LEVEL = Level.SEVERE;

  /** the program name */
  private final String PROGRAM_NAME = "flac2mp3"; //$NON-NLS-1$

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
   * Validation
   */

  /**
   * Validate the configuration.
   * 
   * <ul>
   * <li>The flac base directory must exist</li>
   * <li>The mp3 base directory must exist</li>
   * <li>The mp3 directory must NOT be a sub directory of flac base directory</li>
   * </ul>
   * 
   * @param musicTreeConfiguration the music tree configuration
   * @param scanPath the flac sub-directory or file
   * @return true when validation is successful
   */
  static boolean validateConfiguration(MusicTreeConfiguration musicTreeConfiguration, String scanPath) {
    assert (musicTreeConfiguration != null);
    assert (scanPath != null);

    /* get an absolute scanPath */
    File scanPathFile = new File(scanPath);
    if (!scanPathFile.isAbsolute()) {
      scanPathFile = scanPathFile.getAbsoluteFile();
    }

    /* check that the scanPath exists */
    if (!scanPathFile.exists()) {
      System.err.printf(Messages.getString("Main.5"), scanPathFile.getPath()); //$NON-NLS-1$
      return false;
    }

    /*
     * check that scanPath is below the flac base directory so that it doesn't
     * escape the base directory by doing a ../../..
     */
    if (!FileUtils.isFileBelowDirectory(musicTreeConfiguration.getFlacBaseDir(), scanPathFile)) {
      System.err.printf(Messages.getString("Main.6"), //$NON-NLS-1$
          scanPathFile.getPath(), musicTreeConfiguration.getFlacBaseDir().getPath());
      return false;
    }

    return true;
  }

  /*
   * Helpers
   */

  /**
   * Convert a flac file (from below the base directory of the flac files tree)
   * to a mp3 file (below the directory in which the tree with flac files must
   * be converted as mp3 files). Replaces a .flac extension by a .mp3 extension
   * but leaves other extensions alone.
   * 
   * @param flacBaseDir the flac base directory
   * @param mp3BaseDir the mp3 base directory
   * @param flacFile the flac file. If null, then the base directory of the flac
   *          files tree is converted.
   * @return null if the flac file is not below the base directory of the flac
   *         files tree or when a path can't be resolved, the converted file
   *         otherwise
   */
  static File flacFileToMp3File(File flacBaseDir, File mp3BaseDir, File flacFile) {
    assert (flacBaseDir != null);
    assert (mp3BaseDir != null);
    assert (flacFile != null);

    if (!FileUtils.isFileBelowDirectory(flacBaseDir, flacFile)) {
      return null;
    }

    String flacBaseDirPath;
    String flacFilePath;
    try {
      flacBaseDirPath = flacBaseDir.getParentFile().getCanonicalPath();
      flacFilePath = flacFile.getCanonicalPath();
    }
    catch (IOException e) {
      /* can't be covered by a test */
      return null;
    }

    String relativeFlacFile = flacFilePath.substring(flacBaseDirPath.length() + File.separator.length());

    String[] relativeFlacFileSplit = ExtensionUtils.split(relativeFlacFile, true);
    if (relativeFlacFileSplit[1].equals(MusicTreeConstants.FLACEXTENSION)) {
      relativeFlacFile = relativeFlacFileSplit[0] + MusicTreeConstants.MP3EXTENSION;
    }

    return new File(mp3BaseDir, relativeFlacFile);
  }

  /*
   * Main
   */

  /**
   * Since we're registered as a Runnable with the main.thread property we get
   * called when the system is fully initialised.
   */
  @Override
  public void run() {
    boolean success = doMain();

    if (stayAlive) {
      System.out.printf(Messages.getString("Main.7")); //$NON-NLS-1$
      try {
        Thread.sleep(Long.MAX_VALUE);
      }
      catch (InterruptedException e) {
        /* swallow */
      }
    }

    if (!success) {
      /* can't be covered by a test */
      System.exit(1);
    }
  }

  /**
   * Run the main program
   * 
   * @return true when successful
   */
  boolean doMain() {
    /* setup the commandline options parser */
    CommandLineOptions commandLineOptions = new CommandLineOptions();
    CmdLineParser parser = new CmdLineParser(commandLineOptions);

    /*
     * if the launcher didn't set our command line options then set empty
     * arguments (use defaults)
     */
    if (args == null) {
      args = new String[0];
    }

    /* parse the command line arguments */
    try {
      parser.parseArgument(args);
    }
    catch (CmdLineException e) {
      System.err.println(e.getMessage());
      commandLineOptions.setHelp(true);
    }

    /* print usage when so requested and exit */
    if (commandLineOptions.isHelp()) {
      CommandLineOptions.usage(System.err, PROGRAM_NAME, parser);
      return false;
    }

    /* Setup verbosity in the shell script listener */
    shellScriptListener.setVerbose(commandLineOptions.isVerbose(), commandLineOptions.isQuiet());

    /*
     * Setup & validate the flac2mp3 configuration
     */

    Flac2Mp3Configuration flac2Mp3Configuration = new Flac2Mp3Configuration();

    List<String> errors = flac2Mp3Configuration.validate();
    if (errors != null) {
      for (String error : errors) {
        System.err.println(error);
      }
      return false;
    }

    /*
     * Setup & validate the music tree configuration
     */

    MusicTreeConfiguration musicTreeConfiguration =
        new MusicTreeConfiguration(commandLineOptions.getFlacBaseDir(), commandLineOptions.getMp3BaseDir());

    errors = musicTreeConfiguration.validate(true);
    if (errors != null) {
      for (String error : errors) {
        System.err.println(error);
      }
      return false;
    }

    musicTreeConfiguration.setMp3BaseDir(new File(musicTreeConfiguration.getMp3BaseDir(), musicTreeConfiguration
        .getFlacBaseDir().getName()));

    /*
     * Validate the configuration
     */

    String flacSubDir = commandLineOptions.getFlacSubDir();
    if ((flacSubDir == null) || flacSubDir.isEmpty()) {
      flacSubDir = musicTreeConfiguration.getFlacBaseDir().getAbsolutePath();
    }

    if (!validateConfiguration(musicTreeConfiguration, flacSubDir)) {
      return false;
    }

    /*
     * Run
     */

    File fileList = commandLineOptions.getFileList();
    boolean result = false;

    /* file list mode has priority, if set */
    if (fileList == null) {
      /*
       * normal mode with default conversion configuration
       */
      result =
          syncer.syncFlac2Mp3(flac2Mp3Configuration, musicTreeConfiguration, extensionsList, coversList,
              commandLineOptions.isSimulate());
    } else {
      result = syncFileList(flac2Mp3Configuration, musicTreeConfiguration, fileList, commandLineOptions.isSimulate());
    }

    return result;
  }

  /**
   * Synchronise based on a list of files.
   * 
   * @param flac2Mp3Configuration the conversion configuration. When null then
   *          the default configuration is used.
   * @param musicTreeConfiguration the music tree configuration
   * @param fileList the file with the list of files to synchronise
   * @param simulate true to simulate synchronisation
   * 
   * @return true when successful
   */
  boolean syncFileList(Flac2Mp3Configuration flac2Mp3Configuration, MusicTreeConfiguration musicTreeConfiguration,
      File fileList, boolean simulate) {
    assert (fileList != null);

    if (!fileList.isFile()) {
      System.err.printf(Messages.getString("Main.8"), fileList.getPath()); //$NON-NLS-1$
      return false;
    }

    /* use a list to preserver the order of the list */
    List<File> filesToConvert = new LinkedList<>();

    /* keep a list of files for which errors and warnings were reported */
    Set<String> errorFiles = new TreeSet<>();

    /*
     * first read in the entire list and validate that the files are below the
     * flac base directory; skip files that do not comply to this and skip
     * unreadable files
     */
    BufferedReader reader = null;

    File flacBaseDir = musicTreeConfiguration.getFlacBaseDir();
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileList), "UTF-8")); //$NON-NLS-1$
      String line = null;
      while ((line = reader.readLine()) != null) {
        File flacFile = new File(line);

        if (!FileUtils.isFileBelowDirectory(flacBaseDir, flacFile)) {
          System.err.printf(Messages.getString("Main.9"), flacFile.getPath(), //$NON-NLS-1$
              flacBaseDir.getPath());
          errorFiles.add(flacFile.getPath());
          continue;
        }
        if (!flacFile.canRead()) {
          System.err.printf(Messages.getString("Main.10"), flacFile.getPath()); //$NON-NLS-1$
          errorFiles.add(flacFile.getPath());
          continue;
        }

        filesToConvert.add(flacFile);
      }
    }
    catch (IOException e) {
      /* can't be covered in a test */
      System.err.printf(Messages.getString("Main.11"), fileList, e.getLocalizedMessage()); //$NON-NLS-1$
      e.printStackTrace();
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

    File mp3BaseDir = musicTreeConfiguration.getMp3BaseDir();
    for (File fileToConvert : filesToConvert) {
      File mp3File = flacFileToMp3File(flacBaseDir, mp3BaseDir, fileToConvert);
      if (mp3File == null) {
        /* can't be covered by a test */
        System.err.printf(Messages.getString("Main.12"), fileToConvert, //$NON-NLS-1$
            mp3BaseDir.getPath());
        errorFiles.add(fileToConvert.getPath());
        continue;
      }

      boolean converted = false;
      try {
        converted = flacToMp3.convert(flac2Mp3Configuration, fileToConvert, mp3File, simulate);
      }
      catch (IOException e) {
        /* swallow */
        converted = false;
      }
      if (!converted) {
        errorFiles.add(fileToConvert.getPath());
      }
    }

    if (errorFiles.size() != 0) {
      if (errorFiles.size() == 1) {
        System.err.printf(Messages.getString("Main.13")); //$NON-NLS-1$
      } else {
        System.err.printf(Messages.getString("Main.14"), //$NON-NLS-1$
            Integer.valueOf(errorFiles.size()));
      }
      for (String errorFile : errorFiles) {
        System.err.println("  " + errorFile); //$NON-NLS-1$
      }
    }

    return (errorFiles.size() == 0);
  }
}
