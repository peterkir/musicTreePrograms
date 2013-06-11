package nl.pelagic.musicTree.flac2mp3;

import java.io.File;
import java.io.IOException;

import nl.pelagic.musicTree.flac2mp3.i18n.Messages;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ExampleMode;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.BooleanOptionHandler;

/**
 * The command line options for the program, uses args4j
 */
public class CommandLineOptions {
  /** the default base directory of the flac files tree */
  public static final String DEFAULT_FLAC_BASE_DIR = "./Music"; //$NON-NLS-1$

  /**
   * the default directory in which the tree with flac files must be converted
   * as mp3 files
   */
  public static final String DEFAULT_MP3_BASE_DIR = "./from.flac"; //$NON-NLS-1$

  /** The default flac sub-directory */
  public static final String flacSubDirDefault = null;

  /** The default quiet mode */
  public static final boolean quietDefault = false;

  /** The default verbose mode */
  public static final boolean verboseDefault = false;

  /** The default simulation mode */
  public static final boolean simulateDefault = false;

  /*
   * Configuration variables
   */

  /** the flac base directory */
  private File flacBaseDir;

  /** the mp3 base directory */
  private File mp3BaseDir;

  /**
   * Default constructor
   */
  public CommandLineOptions() {
    super();

    try {
      flacBaseDir = new File(DEFAULT_FLAC_BASE_DIR).getCanonicalFile();
      mp3BaseDir = new File(DEFAULT_MP3_BASE_DIR).getCanonicalFile();
    }
    catch (IOException e) {
      /* can't be covered in a test */
      throw new ExceptionInInitializerError(e);
    }
  }

  /** the file list */
  private File fileList = null;

  /** the flac sub-directory */
  @Argument(metaVar = "flacSubDirectory", required = false, index = 0,
      usage = "The subdirectory of the flac tree to process (by default the same as the flac tree base directory)")
  private String flacSubDir = flacSubDirDefault;

  /** the quiet mode */
  @Option(name = "-q", handler = BooleanOptionHandler.class, usage = "Quiet")
  private boolean quiet = quietDefault;

  /** the verbose mode */
  @Option(name = "-v", handler = BooleanOptionHandler.class, usage = "Verbose")
  private boolean verbose = verboseDefault;

  /** the simulation mode */
  @Option(name = "-n", handler = BooleanOptionHandler.class, usage = "Simulation; do not write to the filesystem")
  private boolean simulate = simulateDefault;

  /** the help mode */
  @Option(name = "-h", handler = BooleanOptionHandler.class, usage = "Help")
  private boolean help = false;

  /*
   * Configuration setters
   */

  /**
   * @param flacBaseDir the flacBaseDir to set
   * @throws IOException when the file could not be resolved
   */
  @Option(name = "-flac", metaVar = "/flac/base/directory", usage = "The flac tree base directory (default = "
      + DEFAULT_FLAC_BASE_DIR + ")")
  public void setFlacBaseDir(String flacBaseDir) throws IOException {
    this.flacBaseDir = new File(flacBaseDir).getCanonicalFile();
  }

  /**
   * @param mp3BaseDir the mp3BaseDir to set
   * @throws IOException when the file could not be resolved
   */
  @Option(name = "-mp3", metaVar = "/mp3/base/directory", usage = "The mp3 tree base directory (default = "
      + DEFAULT_MP3_BASE_DIR + ")")
  public void setMp3BaseDir(String mp3BaseDir) throws IOException {
    this.mp3BaseDir = new File(mp3BaseDir).getCanonicalFile();
  }

  /**
   * @param fileList the fileList to set
   */
  @Option(name = "-f", metaVar = "/some/file/list",
      usage = "Convert the files listed in the specified file (no default, but takes priority when set)")
  public void setFileList(String fileList) {
    this.fileList = new File(fileList);
  }

  /**
   * @param help the help to set
   */
  public void setHelp(boolean help) {
    this.help = help;
  }

  /*
   * Configuration getters
   */

  /**
   * @return the canonical flacBaseDir or null when the file could not be
   *         resolved
   */
  public File getFlacBaseDir() {
    return flacBaseDir;
  }

  /**
   * @return the canonical mp3BaseDir or null when the file could not be
   *         resolved
   */
  public File getMp3BaseDir() {
    return mp3BaseDir;
  }

  /**
   * @return the fileList
   */
  public File getFileList() {
    return fileList;
  }

  /**
   * @return the flacSubDir
   */
  public String getFlacSubDir() {
    return flacSubDir;
  }

  /**
   * @return the quiet
   */
  public boolean isQuiet() {
    return quiet;
  }

  /**
   * @return the verbose
   */
  public boolean isVerbose() {
    return verbose;
  }

  /**
   * @return the simulate
   */
  public boolean isSimulate() {
    return simulate;
  }

  /**
   * @return the help
   */
  public boolean isHelp() {
    return help;
  }

  /*
   * Utilities
   */

  /**
   * Prints usage information
   * 
   * @param programName the name of the program
   * @param parser the args4j parser
   */
  static void usage(String programName, CmdLineParser parser) {
    System.err.println();
    System.err.printf(Messages.getString("CommandLineOptions.0"), programName); //$NON-NLS-1$
    parser.printUsage(System.err);
    System.err.println();
    System.err.printf(Messages.getString("CommandLineOptions.1"), programName, parser.printExample(ExampleMode.ALL)); //$NON-NLS-1$
  }
}
