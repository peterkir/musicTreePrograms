package nl.pelagic.audio.tag.checker.cli;

import java.io.File;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import nl.pelagic.audio.tag.checker.cli.i18n.Messages;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.BooleanOptionHandler;

/**
 * The command line options for the program, uses args4j
 */
public class CommandLineOptions {
  /*
   * Configuration variables
   */

  /** The list of directories/files to check */
  @Argument(metaVar = "checkPath", required = false, multiValued = true,
      usage = "The directory/file to check. Can be specified multiple times.")
  private List<File> checkPaths = new LinkedList<>();

  /**
   * @return the checkPaths
   */
  public List<File> getCheckPaths() {
    return checkPaths;
  }

  /** Diagnostics mode */
  @Option(name = "-D", aliases = {
    "--diagnostics"
  }, handler = BooleanOptionHandler.class, usage = "Diagnostics will be shown when checking has finished.")
  boolean diagnostics = false;

  /**
   * @return the diagnostics
   */
  public boolean isDiagnostics() {
    return diagnostics;
  }

  /** the regular expression */
  private String regex = null;

  /**
   * @return the regex
   */
  public String getRegex() {
    return regex;
  }

  /**
   * @param regex the regex to set
   */
  @Option(name = "-r", aliases = {
    "--regex"
  }, metaVar = "regex", usage = "The regular expression to use for filename filtering."
      + "If no regular expression is specified then a default one will be"
      + " generated and used, which will accept all files that have a supported file extension"
      + " (implies --regex-in-all-dirs). For example: '^.*?\\.(flac|mp3)$'")
  public void setRegex(String regex) {
    this.regex = regex;
  }

  /** the regexInAllDirs mode */
  @Option(name = "-a", aliases = {
    "--regex-in-all-dirs"
  }, handler = BooleanOptionHandler.class, usage = "Apply the regular expression in all directories")
  private boolean regexInAllDirs = false;

  /**
   * @return the regexInAllDirs
   */
  public boolean isRegexInAllDirs() {
    return regexInAllDirs;
  }

  /** the case-sensitive mode for the regular expression */
  @Option(name = "-c", aliases = {
    "--case-sensitive"
  }, handler = BooleanOptionHandler.class, usage = "Case-sensitive regular expression.")
  private boolean regexCaseSensitive = false;

  /**
   * @return the regexCaseSensitive
   */
  public boolean isRegexCaseSensitive() {
    return regexCaseSensitive;
  }

  /** the set of disabled tag checkers */
  @Option(name = "-d", aliases = {
    "--disable"
  }, metaVar = "tagChecker", usage = "Disable the specified tag checker"
      + " (see also --enable). If this option is specified then the disabled"
      + " checker(s) will not be used. Can be specified multiple times.")
  private final List<String> disabledTagCheckers = new LinkedList<>();

  /**
   * @return the disabledTagCheckers
   */
  public List<String> getDisabledTagCheckers() {
    return disabledTagCheckers;
  }

  /** the set of enabled tag checkers */
  @Option(name = "-e", aliases = {
    "--enable"
  }, metaVar = "tagChecker", usage = "Enable the specified tag checker."
      + " A tag checker can be specified by its simple name or by its fully-qualified class name"
      + " (use --listCheckers for an overview). If this option is specified then ONLY the enabled"
      + " checker(s) will be used. Can be specified multiple times.")
  private final List<String> enabledTagCheckers = new LinkedList<>();

  /**
   * @return the enabledTagCheckers
   */
  public List<String> getEnabledTagCheckers() {
    return enabledTagCheckers;
  }

  /** the help mode */
  @Option(name = "-h", aliases = {
    "--help"
  }, handler = BooleanOptionHandler.class, usage = "Help.")
  private boolean help = false;

  /**
   * @return the help
   */
  public boolean isHelp() {
    return help;
  }

  /**
   * @param help the help to set
   */
  public void setHelp(boolean help) {
    this.help = help;
  }

  /** listCheckers all tag converters */
  @Option(name = "-l", aliases = {
    "--listCheckers"
  }, handler = BooleanOptionHandler.class, usage = "List all tag checkers.")
  private boolean listCheckers = false;

  /**
   * @return the listCheckers
   */
  public boolean isListCheckers() {
    return listCheckers;
  }

  /**
   * @param listCheckers the listCheckers to set
   */
  public void setListCheckers(boolean listCheckers) {
    this.listCheckers = listCheckers;
  }

  /** the non-recursive mode */
  @Option(name = "-n", aliases = {
    "--no-recurse"
  }, handler = BooleanOptionHandler.class, usage = "No recursion into directories.")
  private boolean nonRecursive = false;

  /**
   * @return the nonRecursive
   */
  public boolean isNonRecursive() {
    return nonRecursive;
  }

  /** the verbose mode */
  @Option(name = "-v", aliases = {
    "--verbose"
  }, handler = BooleanOptionHandler.class, usage = "Verbose.")
  private boolean verbose = false;

  /**
   * @return the verbose
   */
  public boolean isVerbose() {
    return verbose;
  }

  /** the verbose mode */
  @Option(name = "-vv", aliases = {
    "--extra-verbose"
  }, handler = BooleanOptionHandler.class, usage = "Extra Verbose: print the filename instead of a dot.")
  private boolean extraVerbose = false;

  /**
   * @return the extraVerbose
   */
  public boolean isExtraVerbose() {
    return extraVerbose;
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
        "%s [%s...] %s+%n", programName, Messages.getString("CommandLineOptions.0"), Messages.getString("CommandLineOptions.1")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    parser.printUsage(out);
    out.println();
    out.printf("%s: %s %s%n", "Example", programName, Messages.getString("CommandLineOptions.2")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
  }
}
