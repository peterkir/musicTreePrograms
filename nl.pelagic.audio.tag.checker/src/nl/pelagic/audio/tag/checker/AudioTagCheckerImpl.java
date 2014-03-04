package nl.pelagic.audio.tag.checker;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

import nl.pelagic.audio.tag.checker.api.AudioTagChecker;
import nl.pelagic.audio.tag.checker.api.AudioTagCheckerCallback;
import nl.pelagic.audio.tag.checker.api.TagChecker;
import nl.pelagic.audio.tag.checker.api.TagConverter;
import nl.pelagic.audio.tag.checker.types.AudioTagCheckerConfiguration;
import nl.pelagic.audio.tag.checker.types.GenericTag;
import nl.pelagic.shutdownhook.api.ShutdownHookParticipant;
import nl.pelagic.util.file.ExtensionUtils;
import nl.pelagic.util.file.FilenameFilterWithRegex;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.SupportedFileFormat;
import org.jaudiotagger.tag.Tag;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

/**
 * This bundle performs checks on the tags in music files.
 * 
 * The actual music files that are visited depend on the tag conversion bundles
 * that are deployed along this bundle.
 * 
 * The actual checks that are performed depend on the tag checker bundles that
 * are deployed along this bundle.
 */
@Component
public class AudioTagCheckerImpl implements AudioTagChecker, ShutdownHookParticipant {
  /**
   * The set of (lowercase) filename extensions (without the dot) supported by
   * the jaudiotagger library
   */
  private static final Set<String> supportedExtensions = new TreeSet<>();

  static {
    Locale locale = Locale.getDefault();
    for (SupportedFileFormat format : SupportedFileFormat.values()) {
      supportedExtensions.add(format.getFilesuffix().toLowerCase(locale));
    }
  }

  /*
   * Consumed Services
   */

  /** A set of all tag converters */
  private Set<TagConverter> tagConverters = new CopyOnWriteArraySet<>();

  /**
   * @param tagConverter the tagConverter to add
   */
  @Reference(type = '+')
  void addTagConverter(TagConverter tagConverter) {
    tagConverters.add(tagConverter);
  }

  /**
   * @param tagConverter the tagConverter to remove
   */
  void removeTagConverter(TagConverter tagConverter) {
    tagConverters.remove(tagConverter);
  }

  /** A set of all tag checkers */
  private final Set<TagChecker> tagCheckers = new CopyOnWriteArraySet<>();

  /**
   * @param tagChecker the tagChecker to add
   */
  @Reference(type = '+')
  void addTagChecker(TagChecker tagChecker) {
    tagCheckers.add(tagChecker);
  }

  /**
   * @param tagChecker the tagChecker to remove
   */
  void removeTagChecker(TagChecker tagChecker) {
    tagCheckers.remove(tagChecker);
  }

  /*
   * Internal Methods
   */

  /**
   * <p>
   * Process a certain directory/file, and below (if so indicated).
   * </p>
   * <p>
   * This is a recursive method.
   * </p>
   * 
   * @param configuration the configuration
   * @param file the directory/file to process
   * @param filenameFilter the filename filter to use
   * @param scanDeeper if true then also process files and directories below the
   *          current directory (unless inhibited by the global recursiveScan
   *          setting in the configuration)
   * @param callback the callback to use
   */
  void process(AudioTagCheckerConfiguration configuration, File file, FilenameFilter filenameFilter,
      boolean scanDeeper, AudioTagCheckerCallback callback) {
    assert (configuration != null);
    assert (filenameFilter != null);
    assert (callback != null);
    assert (file != null);

    if (!run.get() || !file.exists()) {
      return;
    }

    if (file.isDirectory()) {
      if (!scanDeeper) {
        /* we're not allowed to dive into the directory */
        return;
      }

      /* list all (filtered) files in the directory */
      File[] directoryFiles = file.listFiles(filenameFilter);
      if (directoryFiles.length == 0) {
        /* no files: return */
        return;
      }

      /* sort the files */
      Arrays.sort(directoryFiles);

      /* process all the files and directories in this directory */
      for (File directoryFile : directoryFiles) {
        if (!run.get()) {
          return;
        }
        process(configuration, directoryFile, filenameFilter, configuration.isRecursiveScan(), callback);
      }

      return;
    }

    /*
     * We're not dealing with a directory; it must be a file
     */

    String extension = ExtensionUtils.split(file.getName(), false)[1].toLowerCase(Locale.getDefault());
    if (extension.isEmpty() || !supportedExtensions.contains(extension)) {
      callback.unsupportedExtension(file);
      return;
    }

    AudioFile af;
    try {
      af = AudioFileIO.read(file);
    }
    catch (Exception e) {
      callback.notReadable(file, e);
      return;
    }

    Tag tag = af.getTag();
    if (tag == null) {
      /* can't be covered by a test */
      callback.noTag(file);
      return;
    }

    /* always set the backing file of the tag */
    GenericTag genericTag = new GenericTag();
    genericTag.setBackingFile(file);
    genericTag.setArtwork(tag.getArtworkList());

    Class<? extends Tag> tagClass = tag.getClass();
    genericTag.addSourceTagClass(tagClass);

    boolean converted = false;
    for (TagConverter tagConverter : tagConverters) {
      converted = tagConverter.getSupportedTagClasses().contains(tagClass) && tagConverter.convert(genericTag, tag);
      if (converted) {
        /* exit the loop early when conversion succeeded */
        break;
      }
    }

    if (!converted) {
      callback.tagNotConverted(file, tag);
      return;
    }

    /*
     * Run all tag checkers
     */

    for (TagChecker tagChecker : tagCheckers) {
      if (configuration.isTagCheckerDisabled(tagChecker) || !configuration.isTagCheckerEnabled(tagChecker)) {
        /*
         * do not run this tag checker when it's disabled or when it's not
         * enabled
         */
        continue;
      }

      tagChecker.check(genericTag);
    }

    /*
     * Report results
     */

    if (genericTag.getReports().size() != 0) {
      callback.checksFailed(genericTag);
      return;
    }

    callback.checksPassed(genericTag);
  }

  /*
   * Interface Methods
   */

  @Override
  public boolean check(AudioTagCheckerConfiguration config, AudioTagCheckerCallback callback) throws IOException {
    if ((config == null) || (callback == null)) {
      return false;
    }

    File scanPath = config.getCheckPath();
    if ((scanPath == null) || !scanPath.exists()) {
      return false;
    }

    FilenameFilter filenameFilter =
        new FilenameFilterWithRegex(config.isRegexInAllDirs() ? null : scanPath, config.getRegexPattern());

    process(config, scanPath, filenameFilter, true, callback);

    return true;
  }

  /*
   * ShutdownHookParticipant
   */

  /** true while we must keep running */
  private AtomicBoolean run = new AtomicBoolean(true);

  @Override
  public void shutdownHook() {
    run.set(false);
  }
}
