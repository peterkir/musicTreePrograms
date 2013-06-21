package nl.pelagic.audio.tag.checker.cli;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import nl.pelagic.audio.tag.checker.api.AudioTagChecker;
import nl.pelagic.audio.tag.checker.api.TagChecker;
import nl.pelagic.audio.tag.checker.api.TagConverter;
import nl.pelagic.audio.tag.checker.cli.i18n.Messages;
import nl.pelagic.audio.tag.checker.types.AudioTagCheckerConfiguration;
import nl.pelagic.shutdownhook.api.ShutdownHookParticipant;

import org.jaudiotagger.audio.SupportedFileFormat;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.osgi.framework.BundleContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

/**
 * The main program that checks audio file tags
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
  static final String PROGRAM_NAME = "audiotagchecker"; //$NON-NLS-1$

  /** the application logger */
  private final Logger applicationLogger;

  /** the jaudiotagger library logger */
  private final Logger jaudiotaggerLogger;

  /*
   * Construction
   */

  /**
   * The set of (lowercase) filename extensions (without the dot) supported by
   * the jaudiotagger library
   */
  private static final Set<String> supportedExtensions = new TreeSet<>();

  static {
    for (SupportedFileFormat format : SupportedFileFormat.values()) {
      supportedExtensions.add(format.getFilesuffix().toLowerCase());
    }
  }

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
  }

  /*
   * Consumed Services
   */

  /** The audo file tag checker */
  private AudioTagChecker audioTagChecker = null;

  /**
   * @param audioTagChecker the audioTagChecker to set
   */
  @Reference
  public void setAudioTagChecker(AudioTagChecker audioTagChecker) {
    this.audioTagChecker = audioTagChecker;
  }

  /** A set of all tag converters */
  private Set<TagConverter> tagConverters = new CopyOnWriteArraySet<>();

  /**
   * @param tagConverter the tagConverter to add
   */
  @Reference(type = '+')
  protected void addTagConverter(TagConverter tagConverter) {
    tagConverters.add(tagConverter);
  }

  /**
   * @param tagConverter the tagConverter to remove
   */
  protected void removeTagConverter(TagConverter tagConverter) {
    tagConverters.remove(tagConverter);
  }

  /** A set of all tag checkers */
  private Set<TagChecker> tagCheckers = new CopyOnWriteArraySet<>();

  /**
   * @param tagChecker the tagChecker to add
   */
  @Reference(type = '+')
  protected void addTagChecker(TagChecker tagChecker) {
    tagCheckers.add(tagChecker);
  }

  /**
   * @param tagChecker the tagChecker to remove
   */
  protected void removeTagChecker(TagChecker tagChecker) {
    tagCheckers.remove(tagChecker);
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
   * Checks whether a tag checker name is a valid tag checker name
   * 
   * @param checker the tag checker name
   * @return true when valid
   */
  boolean isInTagCheckers(String checker) {
    for (TagChecker tagChecker : tagCheckers) {
      if (tagChecker.getClass().getSimpleName().toLowerCase().equals(checker.toLowerCase())
          || tagChecker.getClass().getName().toLowerCase().equals(checker.toLowerCase())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Validate the cli options
   * 
   * @param options the cli options
   * @param out the print stream to write the error messages to
   * @return true when valid
   */
  boolean validateConfiguration(CommandLineOptions options, PrintStream out) {
    boolean retval = true;

    List<File> cps = options.getCheckPaths();
    List<File> toremove = new LinkedList<>();

    /*
     * checkPaths
     */

    for (File f : cps) {
      try {
        if (!f.exists()) {
          throw new IOException();
        }
        f = f.getCanonicalFile();
      }
      catch (IOException e) {
        out.printf(Messages.getString("Main.0"), f.getPath()); //$NON-NLS-1$
        toremove.add(f);
      }
    }
    cps.removeAll(toremove);

    if (cps.size() == 0) {
      out.println(Messages.getString("Main.1")); //$NON-NLS-1$
      retval = false;
    }

    /*
     * regex
     */

    String regex = options.getRegex();
    if ((regex != null) && !regex.isEmpty()) {
      try {
        Pattern.compile(regex);
      }
      catch (PatternSyntaxException e) {
        out.printf(Messages.getString("Main.2"), regex); //$NON-NLS-1$
        retval = false;
      }
    }

    /*
     * DisabledTagCheckers / EnabledTagCheckers
     */

    List<String> tcs = options.getDisabledTagCheckers();
    tcs.addAll(options.getEnabledTagCheckers());
    for (String tc : tcs) {
      if (!isInTagCheckers(tc)) {
        out.printf(Messages.getString("Main.3"), tc); //$NON-NLS-1$
        options.setListCheckers(true);
        retval = false;
      }
    }

    return retval;
  }

  /*
   * Helper Methods
   */

  /**
   * Construct a default regular expression when the incoming regex is null or
   * empty. The default expression is based on the jaudiotagger library
   * supported extensions. When such an expression is constructed then the
   * RegexInAllDirs flag in the audio tag checker configuration is also set.
   * 
   * @param regex the regex as set in the command line
   * @param config the audio tag checker configuration
   * @return a default regular expression when regex was null or empty, regex
   *         otherwise
   */
  static String constructExtensionRegex(String regex, AudioTagCheckerConfiguration config) {
    if ((regex == null) || regex.isEmpty()) {
      StringBuilder sb = new StringBuilder();
      for (String se : supportedExtensions) {
        if (sb.length() > 0) {
          sb.append("|"); //$NON-NLS-1$
        }
        sb.append(se);
      }
      config.setRegexInAllDirs(true);
      return "^.*\\.(" + sb.toString() + ")$"; //$NON-NLS-1$ //$NON-NLS-2$
    }
    return regex;
  }

  /**
   * Print the bundle configuration
   * 
   * @param out the print stream to print to
   * @param options the command line options
   * @param config the audio tag checker configuration
   */
  void printSettings(PrintStream out, CommandLineOptions options, AudioTagCheckerConfiguration config) {
    if (!options.isDiagnostics()) {
      return;
    }

    out.printf(Messages.getString("Main.10")); //$NON-NLS-1$
    out.printf("%-30s = %s%n", Messages.getString("Main.11"), Boolean.valueOf(stayAlive)); //$NON-NLS-1$ //$NON-NLS-2$

    out.printf(Messages.getString("Main.12")); //$NON-NLS-1$
    out.printf("%-30s = %s%n", Messages.getString("Main.13"), options.getCheckPaths()); //$NON-NLS-1$ //$NON-NLS-2$
    out.printf("%-30s = %s%n", Messages.getString("Main.14"), Boolean.valueOf(options.isDiagnostics())); //$NON-NLS-1$ //$NON-NLS-2$
    out.printf("%-30s = %s%n", Messages.getString("Main.15"), options.getRegex()); //$NON-NLS-1$ //$NON-NLS-2$
    out.printf("%-30s = %s%n", Messages.getString("Main.16"), Boolean.valueOf(options.isRegexInAllDirs())); //$NON-NLS-1$ //$NON-NLS-2$
    out.printf("%-30s = %s%n", Messages.getString("Main.17"), Boolean.valueOf(options.isRegexCaseSensitive())); //$NON-NLS-1$ //$NON-NLS-2$
    out.printf("%-30s = %s%n", Messages.getString("Main.18"), options.getDisabledTagCheckers()); //$NON-NLS-1$ //$NON-NLS-2$
    out.printf("%-30s = %s%n", Messages.getString("Main.19"), options.getEnabledTagCheckers()); //$NON-NLS-1$ //$NON-NLS-2$
    out.printf("%-30s = %s%n", Messages.getString("Main.20"), Boolean.valueOf(options.isHelp())); //$NON-NLS-1$ //$NON-NLS-2$
    out.printf("%-30s = %s%n", Messages.getString("Main.21"), Boolean.valueOf(options.isListCheckers())); //$NON-NLS-1$ //$NON-NLS-2$
    out.printf("%-30s = %s%n", Messages.getString("Main.22"), Boolean.valueOf(options.isNonRecursive())); //$NON-NLS-1$ //$NON-NLS-2$
    out.printf("%-30s = %s%n", Messages.getString("Main.23"), Boolean.valueOf(options.isVerbose())); //$NON-NLS-1$ //$NON-NLS-2$
    out.printf("%-30s = %s%n", Messages.getString("Main.24"), Boolean.valueOf(options.isExtraVerbose())); //$NON-NLS-1$ //$NON-NLS-2$

    out.printf(Messages.getString("Main.25")); //$NON-NLS-1$
    out.printf("%-30s = %s%n", Messages.getString("Main.26"), Boolean.valueOf(config.isRecursiveScan())); //$NON-NLS-1$ //$NON-NLS-2$
    out.printf("%-30s = %s%n", Messages.getString("Main.27"), Boolean.valueOf(config.isRegexInAllDirs())); //$NON-NLS-1$ //$NON-NLS-2$
    out.printf("%-30s = %s%n", Messages.getString("Main.28"), config.getRegexPattern()); //$NON-NLS-1$ //$NON-NLS-2$
    out.printf("%-30s = %s%n", Messages.getString("Main.29"), config.getDisabledTagCheckers()); //$NON-NLS-1$ //$NON-NLS-2$
    out.printf("%-30s = %s%n", Messages.getString("Main.30"), config.getEnabledTagCheckers()); //$NON-NLS-1$ //$NON-NLS-2$

    out.println();
  }

  /**
   * Print run diagnostics: run-time and unknown tag fields that were
   * encountered (per tag converter)
   * 
   * @param out the print stream to print to
   * @param options the command line options
   * @param startDate the date at the start of the run
   * @param endDate the date at the end of the run
   */
  void printDiagnostics(PrintStream out, CommandLineOptions options, Date startDate, Date endDate) {
    if (!options.isDiagnostics()) {
      return;
    }

    out.printf("%n#%n# %s%n#%n", Messages.getString("Main.31")); //$NON-NLS-1$ //$NON-NLS-2$

    long diff = endDate.getTime() - startDate.getTime();
    long diffMilliSeconds = diff % 1000;
    long diffSeconds = (diff / 1000) % 60;
    long diffMinutes = (diff / (1000 * 60)) % 60;
    long diffHours = (diff / (1000 * 60 * 60));

    out.printf(
        "%s: %02d:%02d:%02d.%03d%n", Messages.getString("Main.33"), Long.valueOf(diffHours), Long.valueOf(diffMinutes), //$NON-NLS-1$ //$NON-NLS-2$
        Long.valueOf(diffSeconds), Long.valueOf(diffMilliSeconds));
    out.println();

    /* print unknown tag field names */
    for (TagConverter tagConverter : tagConverters) {
      String prefix = Messages.getString("Main.34"); //$NON-NLS-1$
      String colon = ":"; //$NON-NLS-1$
      out.printf("%-30s%s %s%n", prefix, colon, tagConverter.getClass().getName()); //$NON-NLS-1$

      int index = 0;
      Set<Class<? extends Object>> supportedTagClasses = tagConverter.getSupportedTagClasses();
      if (supportedTagClasses != null) {
        prefix = Messages.getString("Main.35"); //$NON-NLS-1$
        colon = ":"; //$NON-NLS-1$
        for (Class<? extends Object> clazz : supportedTagClasses) {
          out.printf("  %-28s%s %s%n", prefix, colon, clazz.getName()); //$NON-NLS-1$
          if (index == 0) {
            prefix = prefix.replaceAll(".", " "); //$NON-NLS-1$ //$NON-NLS-2$
            colon = colon.replaceAll(".", " "); //$NON-NLS-1$ //$NON-NLS-2$
          }
          index++;
        }
      }

      index = 0;
      Map<Class<? extends Object>, Set<String>> unknownFieldNamesMap = tagConverter.getUnknownTagFieldNames();
      if (unknownFieldNamesMap != null) {
        prefix = Messages.getString("Main.36"); //$NON-NLS-1$
        colon = ":"; //$NON-NLS-1$
        for (Entry<Class<? extends Object>, Set<String>> entry : unknownFieldNamesMap.entrySet()) {
          Set<String> unknownTagFieldNames = entry.getValue();
          if (unknownTagFieldNames.isEmpty()) {
            continue;
          }

          out.printf("  %-28s%s %s%n", prefix, colon, entry.getKey().getName()); //$NON-NLS-1$
          if (index == 0) {
            prefix = prefix.replaceAll(".", " "); //$NON-NLS-1$ //$NON-NLS-2$
            colon = colon.replaceAll(".", " "); //$NON-NLS-1$ //$NON-NLS-2$
          }
          for (String un : unknownTagFieldNames) {
            out.printf("  %-28s%s   %s%n", prefix, colon, un); //$NON-NLS-1$
          }
          index++;
        }
      }

      out.println();
    }
  }

  /**
   * Print an overview of the known tag checkers
   * 
   * @param out the stream to print to
   */
  void listTagCheckers(PrintStream out) {
    String fmt = "%30s  %s%n"; //$NON-NLS-1$

    out.println();

    String m1 = Messages.getString("Main.4"); //$NON-NLS-1$
    String u1 = m1.replaceAll(".", "="); //$NON-NLS-1$//$NON-NLS-2$
    out.println(m1);
    out.println(u1);

    m1 = Messages.getString("Main.5"); //$NON-NLS-1$
    u1 = m1.replaceAll(".", "="); //$NON-NLS-1$//$NON-NLS-2$
    String m2 = Messages.getString("Main.6"); //$NON-NLS-1$
    String u2 = m2.replaceAll(".", "="); //$NON-NLS-1$ //$NON-NLS-2$
    out.printf(fmt, m1, m2);
    out.printf(fmt, u1, u2);

    Map<String, String> checkers = new TreeMap<>();
    for (TagChecker tagChecker : tagCheckers) {
      checkers.put(tagChecker.getClass().getSimpleName(), tagChecker.getClass().getName());
    }
    for (Entry<String, String> entry : checkers.entrySet()) {
      out.printf(fmt, entry.getKey(), entry.getValue());
    }
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
   * @param out the stream to print messages to
   * @param err the stream to print errors to
   * @return true when successful
   */
  boolean doMain(PrintStream out, PrintStream err) {
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
      err.printf(Messages.getString("Main.32"), e.getLocalizedMessage()); //$NON-NLS-1$
      commandLineOptions.setHelp(true);
    }

    /*
     * Process command-line options
     */

    if (commandLineOptions.isListCheckers()) {
      /*
       * print a list of tag checkers when so requested and exit when no help
       * must be shown
       */
      listTagCheckers(err);
      if (!commandLineOptions.isHelp()) {
        return true;
      }
    }

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
     * Validate the configuration
     */

    if (!validateConfiguration(commandLineOptions, err)) {
      if (commandLineOptions.isListCheckers()) {
        listTagCheckers(err);
      }
      CommandLineOptions.usage(err, PROGRAM_NAME, parser);
      return false;
    }

    /*
     * Setup config
     */

    /* callback */
    Callback callback = new Callback();
    callback.setVerbose(commandLineOptions.isVerbose());
    callback.setExtraVerbose(commandLineOptions.isExtraVerbose());

    /* disabled / enabled checkers */
    Set<String> disabledTagCheckers = new TreeSet<>(commandLineOptions.getDisabledTagCheckers());
    Set<String> enabledTagCheckers = new TreeSet<>(commandLineOptions.getEnabledTagCheckers());

    /* auto tag checker configuration */
    AudioTagCheckerConfiguration config = new AudioTagCheckerConfiguration();
    /* checkPath is set in the loop */
    config.setRecursiveScan(!commandLineOptions.isNonRecursive());
    config.setRegexInAllDirs(commandLineOptions.isRegexInAllDirs());

    String regex = commandLineOptions.getRegex();
    regex = constructExtensionRegex(regex, config);
    assert ((regex != null) && !regex.isEmpty());
    int flags = Pattern.UNICODE_CASE;
    if (!commandLineOptions.isRegexCaseSensitive()) {
      flags |= Pattern.CASE_INSENSITIVE;
    }
    config.setRegexPattern(Pattern.compile(regex, flags));

    config.setDisabledTagCheckers((disabledTagCheckers.isEmpty()) ? null : disabledTagCheckers);
    config.setEnabledTagCheckers((enabledTagCheckers.isEmpty()) ? null : enabledTagCheckers);

    /*
     * Run
     */

    boolean result = true;

    printSettings(out, commandLineOptions, config);
    Date startDate = new Date();

    /* loop over all check paths */
    for (File checkPath : commandLineOptions.getCheckPaths()) {
      if (stop.get()) {
        break;
      }

      config.setCheckPath(checkPath);

      boolean resultInLoop = false;
      try {
        resultInLoop = audioTagChecker.check(config, callback);
        if (!resultInLoop) {
          err.printf(Messages.getString("Main.8"), config.getCheckPath()); //$NON-NLS-1$
        }
      }
      catch (IOException e) {
        err.printf(Messages.getString("Main.9"), config.getCheckPath(), //$NON-NLS-1$
            e.getLocalizedMessage());
      }

      result = result && resultInLoop;
    }

    if (commandLineOptions.isVerbose()) {
      err.println();
    }

    Date endDate = new Date();
    printDiagnostics(err, commandLineOptions, startDate, endDate);

    return result;
  }

  /*
   * Since we're registered as a Runnable with the main.thread property we get
   * called when the system is fully initialised.
   */
  @Override
  public void run() {
    boolean success = doMain(System.out, System.err);

    stayAlive(System.err);

    if (!success) {
      /* can't be covered by a test */
      System.exit(1);
    }
  }
}
