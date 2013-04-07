package nl.pelagic.audio.conversion.flac2mp3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.pelagic.audio.conversion.flac2mp3.api.Flac2Mp3Configuration;
import nl.pelagic.audio.conversion.flac2mp3.api.FlacToMp3;
import nl.pelagic.audio.conversion.flac2mp3.i18n.Messages;
import nl.pelagic.jaudiotagger.util.TagUtils;
import nl.pelagic.shell.script.listener.api.ShellScriptListener;
import nl.pelagic.shutdownhook.api.ShutdownHookParticipant;
import nl.pelagic.util.file.DirUtils;
import nl.pelagic.util.string.StringUtils;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.id3.ID3v11Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

/**
 * Convert a flac file into an mp3 file
 */
@Component
public class FlacToMp3Impl implements FlacToMp3, ShutdownHookParticipant {
  /** the logger */
  private Logger logger = Logger.getLogger(this.getClass().getName());

  /** the shell script listener (optional) */
  private AtomicReference<ShellScriptListener> shellScriptListener = new AtomicReference<>();

  /**
   * @param shellScriptListener the shellScriptListener to set
   */
  @Reference(type = '?')
  void setShellScriptListener(ShellScriptListener shellScriptListener) {
    this.shellScriptListener.set(shellScriptListener);
  }

  /**
   * @param shellScriptListener the shellScriptListener to unset
   */
  void unsetShellScriptListener(ShellScriptListener shellScriptListener) {
    this.shellScriptListener.compareAndSet(shellScriptListener, null);
  }

  /** the pipeContainer between flac and lame */
  private AtomicReference<Pipe> pipeContainer = new AtomicReference<>();

  /**
   * Do the flac to mp3 conversion by executing flac and lame with a
   * pipeContainer in between the 2 processes.
   * 
   * @param flac the flac (source) file
   * @param mp3 the mp3 (destination) file
   * @param flacCommandList the command list used to execute flac
   * @param lameCommandList the command list used to execute lame
   * @return true when successful, false otherwise
   */
  boolean runConversionProcesses(File flac, File mp3, List<String> flacCommandList, List<String> lameCommandList) {
    Process flacProcess = null;
    Process lameProcess = null;
    int pipeRetval = -1;
    try {
      ProcessBuilder flacProcessBuilder =
          new ProcessBuilder(flacCommandList.toArray(new String[flacCommandList.size()]));
      ProcessBuilder lameProcessBuilder =
          new ProcessBuilder(lameCommandList.toArray(new String[lameCommandList.size()]));

      flacProcess = flacProcessBuilder.start();
      lameProcess = lameProcessBuilder.start();

      BufferedInputStream flacOutputStream = new BufferedInputStream(flacProcess.getInputStream());
      BufferedOutputStream lameInputStream = new BufferedOutputStream(lameProcess.getOutputStream());

      Pipe pipe = new Pipe(flacOutputStream, lameInputStream);
      pipeContainer.set(pipe);
      pipe.run();
      pipeRetval = pipe.getExitValue();
    }
    catch (Exception e) {
      logger.log(Level.WARNING, String.format(Messages.getString("FlacToMp3Impl.0"), flac.getPath(), mp3.getPath()), e); //$NON-NLS-1$
      pipeRetval = -1;
    }
    finally {
      pipeContainer.set(null);
    }

    boolean complete = false;
    while (!complete && (flacProcess != null)) {
      try {
        flacProcess.waitFor();
        complete = true;
      }
      catch (InterruptedException e) {
        /* swallow & can't be covered by a test */
      }
    }

    complete = false;
    while (!complete && (lameProcess != null)) {
      try {
        lameProcess.waitFor();
        complete = true;
      }
      catch (InterruptedException e) {
        /* swallow & can't be covered by a test */
      }
    }

    int flacRetval = (flacProcess != null) ? flacProcess.exitValue() : -1;
    int lameRetval = (lameProcess != null) ? lameProcess.exitValue() : -1;

    return ((flacRetval == 0) && (lameRetval == 0) && (pipeRetval == 0));
  }

  /**
   * Set a tag field in an mp3 file. If fieldValue is null or empty then the tag
   * field fieldName will be removed.
   * 
   * @param mp3 the mp3 file
   * @param mp3tag the tag of the mp3 file
   * @param fieldName the field name
   * @param fieldValue the field value
   * @param overRide true to override an existing value
   * @return true upon success, false otherwise
   */
  boolean setMp3TagField(File mp3, ID3v24Tag mp3tag, FieldKey fieldName, String fieldValue, boolean overRide) {
    assert (mp3 != null);
    assert (mp3tag != null);
    assert (fieldName != null);

    if (!overRide) {
      try {
        if (TagUtils.concatenateTagFields(mp3tag.getFields(fieldName), null) != null) {
          /* the tag field already has a value */
          return true;
        }
      }
      catch (Exception e) {
        logger.log(Level.WARNING, String.format(Messages.getString("FlacToMp3Impl.1"), fieldName, mp3.getPath()), e); //$NON-NLS-1$
        return false;
      }
    }

    try {
      if ((fieldValue == null) || fieldValue.isEmpty()) {
        mp3tag.deleteField(fieldName);
      } else {
        mp3tag.setField(fieldName, fieldValue);
      }
    }
    catch (Exception e) {
      logger.log(Level.WARNING, String.format(Messages.getString("FlacToMp3Impl.2"), mp3.getPath()), e); //$NON-NLS-1$
      return false;
    }

    return true;
  }

  /**
   * Convert an ID3v1 tag into an ID3v24 tag
   * 
   * @param id3v1tag the ID3v1 tag
   * @return the ID3v24 tag
   */
  static ID3v24Tag convertFromId3V1(ID3v1Tag id3v1tag) {
    assert (id3v1tag != null);

    boolean getTrack = false;
    if (id3v1tag instanceof ID3v11Tag) {
      getTrack = true;
    }

    ID3v24Tag mp3tag = new ID3v24Tag();

    try {
      FieldKey[] keys = {
          FieldKey.ALBUM, /* no FieldKey.ALBUM_ARTIST, */
          FieldKey.ARTIST, FieldKey.YEAR, /* no FieldKey.DISC_NO, */
          FieldKey.GENRE, FieldKey.TITLE, FieldKey.TRACK
      /* no FieldKey.TRACK_TOTAL */
      };

      String track = getTrack ? TagUtils.concatenateTagFields(id3v1tag.getTrack(), null) : null;
      if ("0".equals(track)) { //$NON-NLS-1$
        track = null;
      }

      String[] values =
          {
              TagUtils.concatenateTagFields(id3v1tag.getAlbum(), null),
              /* no FieldKey.ALBUM_ARTIST, */
              TagUtils.concatenateTagFields(id3v1tag.getArtist(), null),
              TagUtils.concatenateTagFields(id3v1tag.getYear(), null),
              /* no FieldKey.DISC_NO, */
              TagUtils.concatenateTagFields(id3v1tag.getGenre(), null),
              TagUtils.concatenateTagFields(id3v1tag.getTitle(), null),
              track
          /* no FieldKey.TRACK_TOTAL */
          };

      for (int i = 0; i < keys.length; i++) {
        FieldKey key = keys[i];
        String value = values[i];
        if (value == null) {
          mp3tag.deleteField(key);
        } else {
          mp3tag.setField(key, value);
        }
      }
    }
    catch (KeyNotFoundException | FieldDataInvalidException e) {
      /* swallow */
    }

    return mp3tag;
  }

  /**
   * Update the tag of the mp3 file with tag information fetched from the
   * corresponding flac file (tagInformation)
   * 
   * @param mp3 the mp3 file
   * @param tagInformation the tag information (fetched from the flac file)
   * @param overRide true to override the tag information in the mp3 file
   * @return true upon success, false otherwise
   */
  boolean setMp3Tag(File mp3, TagInformation tagInformation, boolean overRide) {
    assert (mp3 != null);
    assert (tagInformation != null);

    boolean result = true;

    try {
      MP3File mp3file = new MP3File(mp3);

      ID3v24Tag mp3tag;
      if (mp3file.hasID3v2Tag()) {
        mp3tag = mp3file.getID3v2TagAsv24();
      } else {
        if (mp3file.hasID3v1Tag()) {
          mp3tag = convertFromId3V1(mp3file.getID3v1Tag());
        } else {
          /*
           * Can't be covered by a test since mp3file.hasID3v2Tag() also covers
           * the situation that the file has no tag. This line is here for
           * safety.
           */
          mp3tag = new ID3v24Tag();
        }
        mp3file.setTag(mp3tag);
      }

      result = /* result && */setMp3TagField(mp3, mp3tag, FieldKey.ALBUM, tagInformation.getAlbum(), overRide);
      result = result && setMp3TagField(mp3, mp3tag, FieldKey.ALBUM_ARTIST, tagInformation.getAlbumArtist(), overRide);
      result = result && setMp3TagField(mp3, mp3tag, FieldKey.ARTIST, tagInformation.getArtist(), overRide);
      result = result && setMp3TagField(mp3, mp3tag, FieldKey.YEAR, tagInformation.getDate(), overRide);

      String[] discNumberSplit = {
          null, null
      };
      String discNumber = tagInformation.getDiscNumber();
      if (discNumber != null) {
        discNumber = discNumber.trim();
        if (discNumber.matches("^\\d+\\s*/\\s*\\d+$")) { //$NON-NLS-1$
          discNumberSplit = discNumber.split("\\s*/\\s*", 2); //$NON-NLS-1$
        }
      }
      result =
          result
              && setMp3TagField(mp3, mp3tag, FieldKey.DISC_NO, (discNumberSplit.length >= 1)
                  ? discNumberSplit[0]
                  : null, overRide);
      result =
          result
              && setMp3TagField(mp3, mp3tag, FieldKey.DISC_TOTAL, (discNumberSplit.length >= 2)
                  ? discNumberSplit[1]
                  : null, overRide);

      result = result && setMp3TagField(mp3, mp3tag, FieldKey.GENRE, tagInformation.getGenre(), overRide);
      result = result && setMp3TagField(mp3, mp3tag, FieldKey.TITLE, tagInformation.getTitle(), overRide);
      result = result && setMp3TagField(mp3, mp3tag, FieldKey.TRACK, tagInformation.getTrackNumber(), overRide);
      result = result && setMp3TagField(mp3, mp3tag, FieldKey.TRACK_TOTAL, tagInformation.getTrackTotal(), overRide);

      if (result) {
        mp3file.save();
      }
    }
    catch (Throwable e) {
      logger.log(Level.WARNING, String.format(Messages.getString("FlacToMp3Impl.3"), mp3.getPath()), e); //$NON-NLS-1$
      result = false;
    }

    return result;
  }

  /**
   * Update the timestamp on an mp3 file: copy the timestamp from the flac file.
   * If this operation fails then that's ok: the mp3 file will have a newer
   * timestamp than the flac file anyway.
   * 
   * @param flac the flac file
   * @param mp3 the mp3 file
   * @return true
   */
  boolean updateTimestamp(File flac, File mp3) {
    if (!mp3.setLastModified(flac.lastModified())) {
      /* can't be covered by a test */
      logger.log(Level.INFO, String.format(Messages.getString("FlacToMp3Impl.4"), mp3.getPath())); //$NON-NLS-1$
    }

    return true;
  }

  /**
   * Read the tag information from a flac file
   * 
   * @param flacFile the flac file
   * @return the tag information, or null when reading the tag information
   *         failed or the tag is not a flac tag.
   */
  TagInformation readTag(File flacFile) {
    AudioFile af;
    try {
      af = AudioFileIO.read(flacFile);
    }
    catch (Throwable e) {
      logger.log(Level.WARNING, String.format(Messages.getString("FlacToMp3Impl.5"), flacFile.getPath()), e); //$NON-NLS-1$
      return null;
    }

    Tag tag = af.getTag();
    if (!(tag instanceof FlacTag)) {
      /* can't be covered by a test */
      return null;
    }

    return new TagInformation((FlacTag) tag);
  }

  /**
   * Remove an incomplete mp3 file.
   * 
   * @param mp3 the incomplete mp3 file
   */
  void removeIncompleteMp3File(File mp3) {
    if (mp3.exists()) {
      boolean removed = mp3.delete();
      ShellScriptListener listener = shellScriptListener.get();
      if (listener != null) {
        if (!removed) {
          /* can't be covered by a test */
          listener.addMessage(String.format(Messages.getString("FlacToMp3Impl.6"), //$NON-NLS-1$
              mp3.getPath()));
        } else {
          listener.addMessage(String.format(Messages.getString("FlacToMp3Impl.7"), mp3.getPath())); //$NON-NLS-1$
        }
      }
    }
  }

  /*
   * State
   */

  /** idle state */
  private static final int STATE_IDLE = 0;

  /** running state */
  private static final int STATE_RUNNING = 1;

  /** stopping state */
  private static final int STATE_STOPPING = 2;

  /** the state of the conversion */
  private AtomicInteger state = new AtomicInteger(STATE_IDLE);

  @Override
  public boolean convert(Flac2Mp3Configuration configuration, File flac, File mp3, boolean simulate)
      throws FileNotFoundException {
    if (!state.compareAndSet(STATE_IDLE, STATE_RUNNING)) {
      /* we were not idle, so we can't run */
      return false;
    }

    Flac2Mp3Configuration config = configuration;
    if (config == null) {
      config = new Flac2Mp3Configuration();
    }

    boolean successfulConversion = true;

    try {
      /* Check that the flac file exists */
      if ((flac == null) || !flac.isFile()) {
        throw new FileNotFoundException(String.format(Messages.getString("FlacToMp3Impl.9"), flac != null //$NON-NLS-1$
            ? flac.getPath()
            : Messages.getString("FlacToMp3Impl.10"))); //$NON-NLS-1$
      }

      /* get the shell script listener */
      ShellScriptListener listener = shellScriptListener.get();

      /* Progress message */
      if (listener != null) {
        listener.addMessage(String.format(Messages.getString("FlacToMp3Impl.8"), flac.getPath())); //$NON-NLS-1$
      }

      /* Check that the mp3 file doesn't exist or is a regular file */
      if ((mp3 == null) || (mp3.exists() && !mp3.isFile())) {
        throw new FileNotFoundException(String.format(
            Messages.getString("FlacToMp3Impl.11"), mp3 != null ? mp3.getPath() : "NULL")); //$NON-NLS-1$//$NON-NLS-2$
      }

      /* Read the tag from the flac file */
      TagInformation tagInformation = readTag(flac);
      if (tagInformation == null) {
        /* can't be covered by a test */
        return false;
      }

      /* re-get the shell script listener */
      listener = shellScriptListener.get();

      /* Create directory for mp3 file */
      File mp3Dir = mp3.getParentFile();
      if (!mp3Dir.exists() && (listener != null)) {
        listener.addCommand(String.format("mkdir -p \"%s\"", StringUtils.escQuote(mp3Dir.getPath()))); //$NON-NLS-1$
      }
      boolean mp3DirCreated = false;
      try {
        mp3DirCreated = (state.get() == STATE_RUNNING) && (simulate || DirUtils.mkdir(mp3Dir));
      }
      catch (FileAlreadyExistsException e) {
        /* can't be covered by a test */
        logger.log(Level.WARNING, String.format(Messages.getString("FlacToMp3Impl.12"), e.getMessage())); //$NON-NLS-1$
      }
      if (!mp3DirCreated) {
        return false;
      }

      /*
       * Convert flac to mp3
       */

      /* flac */
      List<String> flacCommandList = new LinkedList<>();

      // FIXME replace this by a cmd line option with a default
      flacCommandList.add(config.getFlacExecutable());

      flacCommandList.addAll(config.getFlacOptions());
      flacCommandList.add(StringUtils.escQuote(flac.getPath()));

      /* lame */
      List<String> lameCommandList = new LinkedList<>();

      // FIXME replace this by a cmd line option with a default
      lameCommandList.add(config.getLameExecutable());

      lameCommandList.addAll(config.getLameOptions());
      lameCommandList.add("-"); //$NON-NLS-1$
      lameCommandList.add("--add-id3v2"); //$NON-NLS-1$
      lameCommandList.add("--pad-id3v2"); //$NON-NLS-1$
      lameCommandList.add("--ta"); //$NON-NLS-1$
      lameCommandList.add(StringUtils.escQuote(tagInformation.getArtist()));
      lameCommandList.add("--tl"); //$NON-NLS-1$
      lameCommandList.add(StringUtils.escQuote(tagInformation.getAlbum()));
      lameCommandList.add("--tt"); //$NON-NLS-1$
      lameCommandList.add(StringUtils.escQuote(tagInformation.getTitle()));
      lameCommandList.add("--tg"); //$NON-NLS-1$
      lameCommandList.add(StringUtils.escQuote(tagInformation.getGenre()));
      lameCommandList.add("--ty"); //$NON-NLS-1$
      lameCommandList.add(StringUtils.escQuote(tagInformation.getDate()));
      lameCommandList.add("--tn"); //$NON-NLS-1$
      lameCommandList.add(StringUtils.escQuote(tagInformation.getTrackNumber() + "/" + //$NON-NLS-1$
          tagInformation.getTrackTotal()));
      lameCommandList.add(StringUtils.escQuote(mp3.getPath()));

      /* re-get the shell script listener */
      listener = shellScriptListener.get();

      if (listener != null) {
        listener.addCommand(String.format("%s | \\%n%s", //$NON-NLS-1$
            listener.commandListToString(flacCommandList, 0), listener.commandListToString(lameCommandList, 4)));
      }

      successfulConversion =
          (state.get() == STATE_RUNNING)
              && (simulate || runConversionProcesses(flac, mp3, flacCommandList, lameCommandList));
      if (!successfulConversion) {
        return false;
      }

      /*
       * Set tag on mp3 (from the tag that we read from the flac file)
       */

      /* re-get the shell script listener */
      listener = shellScriptListener.get();

      if (listener != null) {
        List<String> commandList = new LinkedList<>();
        commandList.add("id3v2"); //$NON-NLS-1$
        commandList.add("-a"); //$NON-NLS-1$
        commandList.add(StringUtils.escQuote(tagInformation.getArtist()));
        commandList.add("-A"); //$NON-NLS-1$
        commandList.add(StringUtils.escQuote(tagInformation.getAlbum()));
        commandList.add("--TPE2"); //$NON-NLS-1$
        commandList.add(StringUtils.escQuote(tagInformation.getAlbumArtist()));
        commandList.add("-t"); //$NON-NLS-1$
        commandList.add(StringUtils.escQuote(tagInformation.getTitle()));
        commandList.add("-g"); //$NON-NLS-1$
        commandList.add(StringUtils.escQuote(tagInformation.getGenre()));
        commandList.add("-y"); //$NON-NLS-1$
        commandList.add(StringUtils.escQuote(tagInformation.getDate()));
        commandList.add("-T"); //$NON-NLS-1$
        commandList.add(StringUtils.escQuote(tagInformation.getTrackNumber() + "/" + tagInformation.getTrackTotal())); //$NON-NLS-1$
        commandList.add("--TPOS"); //$NON-NLS-1$
        commandList.add(StringUtils.escQuote(tagInformation.getDiscNumber()));
        commandList.add(StringUtils.escQuote(mp3.getPath()));
        listener.addCommand(listener.commandListToString(commandList, 0));
      }

      successfulConversion = (state.get() == STATE_RUNNING) && (simulate || setMp3Tag(mp3, tagInformation, true));
      if (!successfulConversion) {
        return false;
      }

      /*
       * Set the timestamp of the flac file on the mp3 file
       */

      /* re-get the shell script listener */
      listener = shellScriptListener.get();

      if ((state.get() == STATE_RUNNING) && (listener != null)) {
        listener.addCommand(String.format("touch --reference=\"%s\" \\%n                  \"%s\"", //$NON-NLS-1$
            StringUtils.escQuote(flac.getPath()), StringUtils.escQuote(mp3.getPath())));
      }

      successfulConversion = (state.get() == STATE_RUNNING) && (simulate || updateTimestamp(flac, mp3));
      if (!successfulConversion) {
        return false;
      }
    }
    finally {
      if (!successfulConversion) {
        assert (flac != null);
        assert (mp3 != null);
        logger.log(Level.WARNING, String.format(Messages.getString("FlacToMp3Impl.13"), flac.getPath(), mp3.getPath())); //$NON-NLS-1$
        removeIncompleteMp3File(mp3);
      }

      /*
       * only clear the state here, to make sure that an incomplete mp3 file is
       * removed
       */
      state.set(STATE_IDLE);
    }

    return true;
  }

  /*
   * ShutdownHookParticipant
   */

  @Override
  public void shutdownHook() {
    int currentState = state.getAndSet(STATE_STOPPING);

    if (currentState != STATE_RUNNING) {
      /* we were not running: no need to wait for idle */
      assert (pipeContainer.get() == null);
      return;
    }

    /* signal the pipeContainer to stop */
    Pipe pipe = pipeContainer.get();
    if (pipe != null) {
      pipe.signalStop();
    }

    /* wait for idle of the conversion */
    while (state.get() != STATE_IDLE) {
      try {
        Thread.sleep(1);
      }
      catch (InterruptedException e) {
        /* swallow */
      }
    }
  }
}
