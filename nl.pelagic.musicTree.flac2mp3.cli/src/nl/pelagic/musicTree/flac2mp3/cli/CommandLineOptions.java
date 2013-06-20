package nl.pelagic.musicTree.flac2mp3.cli;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import nl.pelagic.musicTree.flac2mp3.cli.i18n.Messages;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.BooleanOptionHandler;

/**
 * The command line options for the program, uses args4j
 */
public class CommandLineOptions {
  /** the default base directory of the flac files tree */
  public static final String DEFAULT_FLAC_BASE_DIR = "Music"; //$NON-NLS-1$

  /**
   * the default directory in which the tree with flac files must be converted
   * as mp3 files
   */
  public static final String DEFAULT_MP3_BASE_DIR = "from.flac"; //$NON-NLS-1$

  /** The default quiet mode */
  public static final boolean quietDefault = false;

  /** The default verbose mode */
  public static final boolean verboseDefault = false;

  /** The default extraVerbose mode */
  public static final boolean extraVerboseDefault = false;

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
  @Argument(metaVar = "entryToConvert", required = false, index = 0, usage = "A file or directory in the flac tree."
      + " Can be specified multiple times. Optional, by default the same as the flac tree base directory")
  private List<String> entriesToConvert = new LinkedList<>();

  /** the quiet mode */
  @Option(name = "-q", aliases = {
    "--quiet"
  }, handler = BooleanOptionHandler.class, usage = "Quiet")
  private boolean quiet = quietDefault;

  /** the verbose mode */
  @Option(name = "-v", aliases = {
    "--verbose"
  }, handler = BooleanOptionHandler.class, usage = "Verbose: print which directory is being processed.")
  private boolean verbose = verboseDefault;

  /** the verbose mode */
  @Option(name = "-vv", aliases = {
    "--extra-verbose"
  }, handler = BooleanOptionHandler.class,
      usage = "Extra Verbose: also print the commands to replicate the actions being performed.")
  private boolean extraVerbose = extraVerboseDefault;

  /** the simulation mode */
  @Option(name = "-s", aliases = {
    "--simulate"
  }, handler = BooleanOptionHandler.class, usage = "Simulation; do not write to the filesystem")
  private boolean simulate = simulateDefault;

  /** the help mode */
  @Option(name = "-h", aliases = {
    "--help"
  }, handler = BooleanOptionHandler.class, usage = "Help")
  private boolean help = false;

  /*
   * Configuration setters
   */

  /**
   * @param flacBaseDir the flacBaseDir to set
   * @throws IOException when the file could not be resolved
   */
  @Option(name = "-f", aliases = {
    "--flac"
  }, metaVar = "/flac/base/directory", usage = "The flac tree base directory (default = " + DEFAULT_FLAC_BASE_DIR + ")")
  public
      void setFlacBaseDir(File flacBaseDir) throws IOException {
    this.flacBaseDir = flacBaseDir.getCanonicalFile();
  }

  /**
   * @param mp3BaseDir the mp3BaseDir to set
   * @throws IOException when the file could not be resolved
   */
  @Option(name = "-m", aliases = {
    "--mp3"
  }, metaVar = "/mp3/base/directory", usage = "The mp3 tree base directory (default = " + DEFAULT_MP3_BASE_DIR + ")")
  public void setMp3BaseDir(File mp3BaseDir) throws IOException {
    this.mp3BaseDir = mp3BaseDir.getCanonicalFile();
  }

  /**
   * @param fileList the fileList to set
   * @throws IOException when the file could not be resolved
   */
  @Option(name = "-l", aliases = {
    "--filelist"
  }, metaVar = "/some/file/list", usage = "Convert the directories and files listed"
      + " in the specified file (no default)")
  public void setFileList(File fileList) throws IOException {
    this.fileList = fileList.getCanonicalFile();
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
   * @return the entriesToConvert
   */
  public List<String> getEntriesToConvert() {
    return entriesToConvert;
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
   * @return the extraVerbose
   */
  public boolean isExtraVerbose() {
    return extraVerbose;
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
   * @param out the stream to print to
   * @param programName the name of the program
   * @param parser the args4j parser
   */
  static void usage(PrintStream out, String programName, CmdLineParser parser) {
    out.println();
    out.printf(
        "%s [%s...] [%s]*%n", programName, Messages.getString("CommandLineOptions.0"), Messages.getString("CommandLineOptions.1")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    parser.printUsage(out);
    out.println();
    out.printf("%s: %s %s%n", "Example", programName, Messages.getString("CommandLineOptions.2")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
  }
}
