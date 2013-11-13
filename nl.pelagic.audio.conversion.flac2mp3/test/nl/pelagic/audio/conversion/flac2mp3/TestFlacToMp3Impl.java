package nl.pelagic.audio.conversion.flac2mp3;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.pelagic.audio.conversion.flac2mp3.api.Flac2Mp3Configuration;
import nl.pelagic.audio.conversion.flac2mp3.i18n.Messages;
import nl.pelagic.audio.conversion.flac2mp3.testhelpers.Syncer;
import nl.pelagic.audio.conversion.flac2mp3.testhelpers.TagHelper;
import nl.pelagic.jaudiotagger.util.TagUtils;
import nl.pelagic.shell.script.listener.testhelpers.MyShellScriptListener;
import nl.pelagic.shell.script.listener.testhelpers.MyShellScriptListener.Pair;
import nl.pelagic.util.file.DirUtils;
import nl.pelagic.util.file.FileUtils;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.id3.ID3v11Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings({
    "javadoc", "nls", "static-method"
})
public class TestFlacToMp3Impl {
  private static Logger logger = Logger.getLogger(FlacToMp3Impl.class.getName());
  private static Logger jlogger = Logger.getLogger("org.jaudiotagger");

  private FlacToMp3Impl flacToMp3Impl = null;
  private MyShellScriptListener myShellScriptListener = null;

  private static List<String> flacCommandList = new LinkedList<>();
  private static List<String> lameCommandList = new LinkedList<>();

  private static File tmpTestDir = new File("testresources/tmpTestDir");
  private static File flacFake = new File(tmpTestDir, "TestFlacToMp3Impl.tmp.flac");
  private static File mp3Fake = new File(tmpTestDir, "TestFlacToMp3Impl.tmp.mp3");

  private static File testdataDir = new File("testresources/testdata");
  private static File flac = new File(testdataDir, "laser.flac");
  private static File mp3 = new File(testdataDir, "laser.mp3");

  private static Flac2Mp3Configuration flac2mp3Config = null;

  private static ID3v24Tag readMp3Tag(File flacFile) {
    AudioFile af;
    try {
      af = AudioFileIO.read(flacFile);
    }
    catch (Throwable e) {
      System.err.println(String.format("Failed to read mp3 file", flacFile.getPath()));
      return null;
    }

    Tag tag = af.getTag();
    if (!(tag instanceof ID3v24Tag)) {
      return null;
    }

    return (ID3v24Tag) tag;
  }

  @BeforeClass
  public static void setUpBeforeClass() {
    flacFake.deleteOnExit();
    mp3Fake.deleteOnExit();
    logger.setLevel(Level.OFF);
    jlogger.setLevel(Level.OFF);
    flacCommandList.add("echo");
    flacCommandList.add("bla bla bla");
    lameCommandList.add("cat");
    flac2mp3Config = new Flac2Mp3Configuration();
    List<String> r = flac2mp3Config.validate();
    if (r != null) {
      throw new ExceptionInInitializerError("flac2mp3Config did not validate: " + r);
    }
  }

  @AfterClass
  public static void tearDownAfterClass() {
    flac2mp3Config = null;
    lameCommandList.clear();
    lameCommandList = null;
    flacCommandList.clear();
    flacCommandList = null;
  }

  @Before
  public void setUp() {
    flacToMp3Impl = new FlacToMp3Impl();
    myShellScriptListener = new MyShellScriptListener();
    flacToMp3Impl.setShellScriptListener(myShellScriptListener);
  }

  @After
  public void tearDown() {
    flacToMp3Impl.setShellScriptListener(null);
    myShellScriptListener = null;
    flacToMp3Impl = null;
  }

  @Test
  public void testRunConversionProcesses_Null_FlacCommandList() {
    boolean r = flacToMp3Impl.runConversionProcesses(flacFake, mp3Fake, null, lameCommandList);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test
  public void testRunConversionProcesses_Null_LameCommandList() {
    boolean r = flacToMp3Impl.runConversionProcesses(flacFake, mp3Fake, flacCommandList, null);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test
  public void testRunConversionProcesses_Null_FlacCommandList_DoesNotExist() {
    List<String> flacCommandList = new LinkedList<>();
    flacCommandList.add(0, "some_dummy_command_that_does_not_exist");
    boolean r = flacToMp3Impl.runConversionProcesses(flacFake, mp3Fake, flacCommandList, lameCommandList);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test
  public void testRunConversionProcesses_Null_LameCommandList_DoesNotExist() {
    List<String> lameCommandList = new LinkedList<>();
    lameCommandList.add(0, "some_dummy_command_that_does_not_exist");
    boolean r = flacToMp3Impl.runConversionProcesses(flacFake, mp3Fake, flacCommandList, lameCommandList);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test
  public void testRunConversionProcesses_Null_LameCommandList_Normal() {
    boolean r = flacToMp3Impl.runConversionProcesses(flacFake, mp3Fake, flacCommandList, lameCommandList);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));
  }

  @Test
  public void testRunConversionProcesses_Null_LameCommandList_Normal_FlacError() {
    List<String> flacCommandList = new LinkedList<>();
    flacCommandList.add(0, "cat");
    flacCommandList.add(1, "--some_dummy_option_that_doesnt_exist");
    boolean r = flacToMp3Impl.runConversionProcesses(flacFake, mp3Fake, flacCommandList, lameCommandList);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test
  public void testRunConversionProcesses_Null_LameCommandList_Normal_LameError() {
    List<String> lameCommandList = new LinkedList<>();
    lameCommandList.add(0, "cat");
    lameCommandList.add(1, "--some_dummy_option_that_doesnt_exist");
    boolean r = flacToMp3Impl.runConversionProcesses(flacFake, mp3Fake, flacCommandList, lameCommandList);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test
  public void testSetMp3TagField_Normal() throws KeyNotFoundException, FieldDataInvalidException {
    String originalValue = "bla bla";

    boolean[] mp3tagHasValues = {
        false, true
    };
    String[] newValues = {
        null, "", "yeah yeah"
    };
    boolean[] overrides = {
        false, true
    };

    for (boolean mp3tagHasValue : mp3tagHasValues) {
      for (String newValue : newValues) {
        for (boolean override : overrides) {
          String assertMessage =
              String.format("mp3tagHasValue=%b, newValue=%s, override=%b", Boolean.valueOf(mp3tagHasValue), newValue,
                  Boolean.valueOf(override));

          ID3v24Tag mp3tag = new ID3v24Tag();
          if (mp3tagHasValue) {
            mp3tag.setField(FieldKey.ALBUM, originalValue);
          }

          boolean r = flacToMp3Impl.setMp3TagField(mp3Fake, mp3tag, FieldKey.ALBUM, newValue, override);

          boolean fieldKeep = mp3tagHasValue && !override;
          boolean fieldRemoved = (newValue == null) || newValue.isEmpty();

          assertThat(assertMessage, Boolean.valueOf(r), equalTo(Boolean.TRUE));
          List<TagField> fields = mp3tag.getFields(FieldKey.ALBUM);
          if (fieldKeep) {
            int expectedCount = (mp3tagHasValue || !fieldRemoved) ? 1 : 0;
            assertThat(assertMessage, Integer.valueOf(fields.size()), equalTo(Integer.valueOf(expectedCount)));

            if (expectedCount == 1) {
              assertThat(assertMessage, fields.get(0).toString(), equalTo("Text=\"" + originalValue + "\"; "));
            }
          } else {
            int expectedCount = fieldRemoved ? 0 : 1;
            assertThat(assertMessage, Integer.valueOf(fields.size()), equalTo(Integer.valueOf(expectedCount)));

            if (expectedCount == 1) {
              /* changed */
              assertThat(assertMessage, fields.get(0).toString(), equalTo("Text=\"" + newValue + "\"; "));
            }
          }
        }
      }
    }
  }

  @Test
  public void testSetMp3TagField_Keep_Null_Illegal_Field() throws KeyNotFoundException {
    boolean r = flacToMp3Impl.setMp3TagField(mp3Fake, new ID3v24Tag(), null, null, false);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test
  public void testSetMp3TagField_Override_Null_Illegal_Field() throws KeyNotFoundException {
    boolean r = flacToMp3Impl.setMp3TagField(mp3Fake, new ID3v24Tag(), null, null, true);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test
  public void testConvertFromId3V1_Empty_v10() {
    ID3v1Tag id3v1Tag = new ID3v1Tag();
    ID3v24Tag id3v24Tag = FlacToMp3Impl.convertFromId3V1(id3v1Tag);

    assertThat(id3v24Tag, notNullValue());

    assertThat(TagUtils.concatenateTagFields(id3v24Tag.getFields(FieldKey.ALBUM), null), equalTo(null));
    /* no FieldKey.ALBUM_ARTIST */
    assertThat(TagUtils.concatenateTagFields(id3v24Tag.getFields(FieldKey.ARTIST), null), equalTo(null));
    assertThat(TagUtils.concatenateTagFields(id3v24Tag.getFields(FieldKey.YEAR), null), equalTo(null));
    /* no FieldKey.DISC_NO */
    assertThat(TagUtils.concatenateTagFields(id3v24Tag.getFields(FieldKey.GENRE), null), equalTo(null));
    assertThat(TagUtils.concatenateTagFields(id3v24Tag.getFields(FieldKey.TITLE), null), equalTo(null));
    assertThat(TagUtils.concatenateTagFields(id3v24Tag.getFields(FieldKey.TRACK), null), equalTo(null));
    /* no FieldKey.TRACK_TOTAL */
  }

  @Test
  public void testConvertFromId3V1_Empty_v11() {
    ID3v11Tag id3v1Tag = new ID3v11Tag();
    ID3v24Tag id3v24Tag = FlacToMp3Impl.convertFromId3V1(id3v1Tag);

    assertThat(id3v24Tag, notNullValue());

    assertThat(TagUtils.concatenateTagFields(id3v24Tag.getFields(FieldKey.ALBUM), null), equalTo(null));
    /* no FieldKey.ALBUM_ARTIST */
    assertThat(TagUtils.concatenateTagFields(id3v24Tag.getFields(FieldKey.ARTIST), null), equalTo(null));
    assertThat(TagUtils.concatenateTagFields(id3v24Tag.getFields(FieldKey.YEAR), null), equalTo(null));
    /* no FieldKey.DISC_NO */
    assertThat(TagUtils.concatenateTagFields(id3v24Tag.getFields(FieldKey.GENRE), null), equalTo(null));
    assertThat(TagUtils.concatenateTagFields(id3v24Tag.getFields(FieldKey.TITLE), null), equalTo(null));
    assertThat(TagUtils.concatenateTagFields(id3v24Tag.getFields(FieldKey.TRACK), null), equalTo(null));
    /* no FieldKey.TRACK_TOTAL */
  }

  @Test
  public void testConvertFromId3V1_Random_v10() throws KeyNotFoundException {
    ID3v1Tag id3v1Tag = TagHelper.getRandomID3v1Tag(true);

    ID3v24Tag id3v24Tag = FlacToMp3Impl.convertFromId3V1(id3v1Tag);

    assertThat(id3v24Tag, notNullValue());

    assertThat(TagUtils.concatenateTagFields(id3v24Tag.getFields(FieldKey.ALBUM), null), equalTo("Text=\""
        + TagHelper.album + "\"; "));
    /* no FieldKey.ALBUM_ARTIST */
    assertThat(TagUtils.concatenateTagFields(id3v24Tag.getFields(FieldKey.ARTIST), null), equalTo("Text=\""
        + TagHelper.artist + "\"; "));
    assertThat(TagUtils.concatenateTagFields(id3v24Tag.getFields(FieldKey.YEAR), null), equalTo("Text=\""
        + TagHelper.date + "\"; "));
    /* no FieldKey.DISC_NO */
    assertThat(TagUtils.concatenateTagFields(id3v24Tag.getFields(FieldKey.GENRE), null), equalTo("Text=\""
        + TagHelper.genreNumber + "\"; "));
    assertThat(TagUtils.concatenateTagFields(id3v24Tag.getFields(FieldKey.TITLE), null), equalTo("Text=\""
        + TagHelper.title + "\"; "));
    assertThat(TagUtils.concatenateTagFields(id3v24Tag.getFields(FieldKey.TRACK), null), equalTo(null));
    /* no FieldKey.TRACK_TOTAL */
  }

  @Test
  public void testConvertFromId3V1_Random_v11() throws KeyNotFoundException {
    ID3v1Tag id3v1Tag = TagHelper.getRandomID3v1Tag(false);

    ID3v24Tag id3v24Tag = FlacToMp3Impl.convertFromId3V1(id3v1Tag);

    assertThat(id3v24Tag, notNullValue());

    assertThat(TagUtils.concatenateTagFields(id3v24Tag.getFields(FieldKey.ALBUM), null), equalTo("Text=\""
        + TagHelper.album + "\"; "));
    /* no FieldKey.ALBUM_ARTIST */
    assertThat(TagUtils.concatenateTagFields(id3v24Tag.getFields(FieldKey.ARTIST), null), equalTo("Text=\""
        + TagHelper.artist + "\"; "));
    assertThat(TagUtils.concatenateTagFields(id3v24Tag.getFields(FieldKey.YEAR), null), equalTo("Text=\""
        + TagHelper.date + "\"; "));
    /* no FieldKey.DISC_NO */
    assertThat(TagUtils.concatenateTagFields(id3v24Tag.getFields(FieldKey.GENRE), null), equalTo("Text=\""
        + TagHelper.genreNumber + "\"; "));
    assertThat(TagUtils.concatenateTagFields(id3v24Tag.getFields(FieldKey.TITLE), null), equalTo("Text=\""
        + TagHelper.title + "\"; "));
    assertThat(TagUtils.concatenateTagFields(id3v24Tag.getFields(FieldKey.TRACK), null), equalTo("Text=\""
        + TagHelper.tracknumber + "\"; "));
    /* no FieldKey.TRACK_TOTAL */
  }

  @Test
  public void testSetMp3Tag_Illegal_MP3_File() {
    TagInformation tagInformation = new TagInformation();
    boolean r = flacToMp3Impl.setMp3Tag(mp3Fake, tagInformation, true);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test
  public void testSetMp3Tag_Normal() throws FileAlreadyExistsException, FileNotFoundException, IOException,
      KeyNotFoundException, FieldDataInvalidException {
    FlacTag flacTag = TagHelper.getRandomFlacTag();
    TagInformation tagInformation = new TagInformation(flacTag);

    File[] mp3s =
        {
            new File(testdataDir, "laser_no_tag.mp3"),
            new File(testdataDir, "laser_id3v1.mp3"),
            new File(testdataDir, "laser.mp3")
        };
    boolean[] overrides = {
        false, true
    };

    int mp3Index = 0;
    for (File mp3 : mp3s) {
      for (boolean override : overrides) {

        FileUtils.copy(mp3, mp3Fake);

        boolean r = flacToMp3Impl.setMp3Tag(mp3Fake, tagInformation, override);

        assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));

        /* read back the tag */
        ID3v24Tag mp3tagNew = readMp3Tag(mp3Fake);
        assertThat(mp3tagNew, notNullValue());
        mp3Fake.delete();

        String newAlbum = null;
        String newAlbumArtist = null;
        String newArtist = null;
        String newYear = null;
        String newDiscNo = null;
        String newGenre = null;
        String newTitle = null;
        String newTrack = null;
        String newTrackTotal = null;
        if (!override && (mp3Index > 0)) {
          newAlbum = "Text=\"Testdata\"; ";
          if (mp3Index == 1) {
            newAlbumArtist = "Text=\"" + tagInformation.getAlbumArtist() + "\"; ";
          } else {
            newAlbumArtist = "Text=\"Pelagic\"; ";
          }
          newArtist = "Text=\"Flac2Mp3\"; ";
          newYear = "Text=\"2013\"; ";
          if (mp3Index == 1) {
            newDiscNo = "Text=\"" + tagInformation.getDiscNumber() + "\"; ";
          } else {
            newDiscNo = "Text=\"1/2\"; ";
          }
          newGenre = "Text=\"37\"; ";
          newTitle = "Text=\"Laser mp3\"; ";
          if (mp3Index == 1) {
            newTrack = "Text=\"2/" + tagInformation.getTrackTotal() + "\"; ";
          } else {
            newTrack = "Text=\"02/12\"; ";
          }
          newTrackTotal = newTrack;
        } else {
          newAlbum = "Text=\"" + tagInformation.getAlbum() + "\"; ";
          newAlbumArtist = "Text=\"" + tagInformation.getAlbumArtist() + "\"; ";
          newArtist = "Text=\"" + tagInformation.getArtist() + "\"; ";
          newYear = "Text=\"" + tagInformation.getDate() + "\"; ";
          newDiscNo = "Text=\"" + tagInformation.getDiscNumber() + "\"; ";
          newGenre = "Text=\"" + tagInformation.getGenre() + "\"; ";
          newTitle = "Text=\"" + tagInformation.getTitle() + "\"; ";
          newTrack = "Text=\"" + tagInformation.getTrackNumber() + "/" + tagInformation.getTrackTotal() + "\"; ";
          newTrackTotal = "Text=\"" + tagInformation.getTrackNumber() + "/" + tagInformation.getTrackTotal() + "\"; ";
        }
        String assertStr = String.format("mp3=%s, override=%b", mp3.getPath(), Boolean.valueOf(override));
        assertThat(assertStr, mp3tagNew.getFields(FieldKey.ALBUM).get(0).toString(), equalTo(newAlbum));
        assertThat(assertStr, mp3tagNew.getFields(FieldKey.ALBUM_ARTIST).get(0).toString(), equalTo(newAlbumArtist));
        assertThat(assertStr, mp3tagNew.getFields(FieldKey.ARTIST).get(0).toString(), equalTo(newArtist));
        assertThat(assertStr, mp3tagNew.getFields(FieldKey.YEAR).get(0).toString(), equalTo(newYear));
        assertThat(assertStr, mp3tagNew.getFields(FieldKey.DISC_NO).get(0).toString(), equalTo(newDiscNo));
        assertThat(assertStr, mp3tagNew.getFields(FieldKey.GENRE).get(0).toString(), equalTo(newGenre));
        assertThat(assertStr, mp3tagNew.getFields(FieldKey.TITLE).get(0).toString(), equalTo(newTitle));
        assertThat(assertStr, mp3tagNew.getFields(FieldKey.TRACK).get(0).toString(), equalTo(newTrack));
        assertThat(assertStr, mp3tagNew.getFields(FieldKey.TRACK_TOTAL).get(0).toString(), equalTo(newTrackTotal));
      }
      mp3Index++;
    }
  }

  @Test
  public void testUpdateTimestamp_DummyFiles() {
    boolean r =
        flacToMp3Impl.updateTimestamp(new File("some dummy file that doesn't exist.flac"), new File(
            "some dummy file that doesn't exist.mp3"));
    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));
  }

  @Test
  public void testUpdateTimestamp_RealFiles() throws FileAlreadyExistsException, FileNotFoundException, IOException {

    FileUtils.copy(flac, flacFake);
    FileUtils.copy(mp3, mp3Fake);

    boolean r = flacToMp3Impl.updateTimestamp(flacFake, mp3Fake);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));
  }

  @Test
  public void testReadTag_InvalidFile() {
    File flacFake = new File(tmpTestDir, "testReadTag_InvalidFile.tmp.flac");
    flacFake.deleteOnExit();

    TagInformation tagInformation = flacToMp3Impl.readTag(flacFake);

    assertThat(tagInformation, nullValue());
  }

  @Test
  public void testReadTag_FileWithoutTag() {
    File[] files = {
        new File(testdataDir, "laser_no_tag.flac"), new File(testdataDir, "laser_mp3_tag.flac")
    };

    for (File file : files) {
      TagInformation defaultTagInformation = new TagInformation();
      TagInformation tagInformation = flacToMp3Impl.readTag(file);

      assertThat(tagInformation.getAlbum(), equalTo(defaultTagInformation.getAlbum()));
      assertThat(tagInformation.getAlbumArtist(), equalTo(defaultTagInformation.getAlbumArtist()));
      assertThat(tagInformation.getArtist(), equalTo(defaultTagInformation.getArtist()));
      assertThat(tagInformation.getDate(), equalTo(defaultTagInformation.getDate()));
      assertThat(tagInformation.getDiscNumber(), equalTo(defaultTagInformation.getDiscNumber()));
      assertThat(tagInformation.getGenre(), equalTo(defaultTagInformation.getGenre()));
      assertThat(tagInformation.getTitle(), equalTo(defaultTagInformation.getTitle()));
      assertThat(tagInformation.getTrackNumber(), equalTo(defaultTagInformation.getTrackNumber()));
      assertThat(tagInformation.getTrackTotal(), equalTo(defaultTagInformation.getTrackTotal()));
    }
  }

  @Test
  public void testReadTag_FileWithTag() {
    File flacFake = new File(testdataDir, "laser.flac");

    TagInformation tagInformation = flacToMp3Impl.readTag(flacFake);

    assertThat(tagInformation.getAlbum(), equalTo("Testdata"));
    assertThat(tagInformation.getAlbumArtist(), equalTo("Pelagic"));
    assertThat(tagInformation.getArtist(), equalTo("Flac2Mp3"));
    assertThat(tagInformation.getDate(), equalTo("2013"));
    assertThat(tagInformation.getDiscNumber(), equalTo("1/2"));
    assertThat(tagInformation.getGenre(), equalTo("Sound Clip"));
    assertThat(tagInformation.getTitle(), equalTo("Laser flac"));
    assertThat(tagInformation.getTrackNumber(), equalTo("01"));
    assertThat(tagInformation.getTrackTotal(), equalTo("12"));
  }

  @Test
  public void testRemoveIncompleteMp3File_FileNotExists() {
    flacToMp3Impl.removeIncompleteMp3File(new File("some dummy file that doesn't exist.mp3"));
  }

  @Test
  public void testRemoveIncompleteMp3File_FileExists() throws FileAlreadyExistsException, FileNotFoundException,
      IOException {
    FileUtils.copy(mp3, mp3Fake);

    flacToMp3Impl.removeIncompleteMp3File(mp3Fake);

    assertThat(Boolean.valueOf(mp3Fake.exists()), equalTo(Boolean.FALSE));

    List<Pair> msgs = myShellScriptListener.received;
    assertThat(Integer.valueOf(msgs.size()), equalTo(Integer.valueOf(1)));
    assertThat(Integer.valueOf(msgs.get(0).type), equalTo(Integer.valueOf(MyShellScriptListener.TYPE_MESSAGE)));
    assertThat(msgs.get(0).string, equalTo(String.format(Messages.getString("FlacToMp3Impl.7"), mp3Fake.getPath())));
  }

  @Test
  public void testRemoveIncompleteMp3File_FileExists_NoLogger() throws FileAlreadyExistsException,
      FileNotFoundException, IOException {
    FileUtils.copy(mp3, mp3Fake);

    flacToMp3Impl.setShellScriptListener(null);
    flacToMp3Impl.removeIncompleteMp3File(mp3Fake);

    assertThat(Boolean.valueOf(mp3Fake.exists()), equalTo(Boolean.FALSE));

    List<Pair> msgs = myShellScriptListener.received;
    assertThat(Integer.valueOf(msgs.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testConvert_AlreadyStopped() throws FileNotFoundException {
    flacToMp3Impl.shutdownHook();
    boolean r = flacToMp3Impl.convert(null, flac, mp3Fake, true);

    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @SuppressWarnings("unused")
  @Test(expected = FileNotFoundException.class)
  public void testConvert_FlacNull() throws FileNotFoundException {
    boolean r = flacToMp3Impl.convert(null, null, mp3Fake, true);
  }

  @SuppressWarnings("unused")
  @Test(expected = FileNotFoundException.class)
  public void testConvert_FlacDirectory() throws FileNotFoundException, FileAlreadyExistsException {
    File tmpDir = new File(tmpTestDir, "testConvert_FlacDirectory");
    DirUtils.mkdir(tmpDir);
    try {
      boolean r = flacToMp3Impl.convert(null, tmpDir, mp3Fake, true);
    }
    finally {
      tmpDir.delete();
    }
  }

  @SuppressWarnings("unused")
  @Test(expected = FileNotFoundException.class)
  public void testConvert_Mp3Null() throws FileNotFoundException {
    boolean r = flacToMp3Impl.convert(null, flac, null, true);
  }

  @SuppressWarnings("unused")
  @Test(expected = FileNotFoundException.class)
  public void testConvert_Mp3Directory() throws FileNotFoundException, FileAlreadyExistsException {
    File tmpDir = new File(tmpTestDir, "testConvert_Mp3Directory");
    DirUtils.mkdir(tmpDir);
    try {
      boolean r = flacToMp3Impl.convert(null, flac, tmpDir, true);
    }
    finally {
      tmpDir.delete();
    }
  }

  @Test(timeout = 1000)
  public void testConvert_StopBeforeDirectoryCreation_Mp3_NotExists() throws FileNotFoundException {
    myShellScriptListener.stallPoints.add("^mkdir -p .*$");

    Syncer syncer = new Syncer(myShellScriptListener, flacToMp3Impl);
    syncer.start();
    while (!syncer.busy.get()) {
      try {
        Thread.sleep(1);
      }
      catch (InterruptedException e) {
        /* swallow */
      }
    }

    File mp3DstDir = new File(tmpTestDir, "from.flac");
    File mp3Dst = new File(mp3DstDir, "mp3Dst.mp3");
    boolean r = flacToMp3Impl.convert(flac2mp3Config, flac, mp3Dst, true);

    try {
      syncer.join();
    }
    catch (InterruptedException e) {
      /* swallow */
      FileUtils.delete(mp3DstDir);
    }

    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test(timeout = 1000)
  public void testConvert_StopBeforeDirectoryCreation_Mp3_Exists() throws FileAlreadyExistsException, IOException {
    FileUtils.copy(mp3, mp3Fake);

    myShellScriptListener.stallPoints.add("^mkdir -p .*$");

    File mp3DstDir = null;
    try {
      Syncer syncer = new Syncer(myShellScriptListener, flacToMp3Impl);
      syncer.start();
      while (!syncer.busy.get()) {
        try {
          Thread.sleep(1);
        }
        catch (InterruptedException e) {
          /* swallow */
        }
      }

      mp3DstDir = new File(tmpTestDir, "from.flac");
      File mp3Dst = new File(mp3DstDir, "mp3Dst.mp3");
      boolean r = flacToMp3Impl.convert(flac2mp3Config, flac, mp3Dst, true);

      try {
        syncer.join();
      }
      catch (InterruptedException e) {
        /* swallow */
      }

      assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
      assertThat(Boolean.valueOf(mp3Fake.exists()), equalTo(Boolean.TRUE));
    }
    finally {
      mp3Fake.delete();
      FileUtils.delete(mp3DstDir);
    }
  }

  @Test
  public void testConvert_StopBeforeConversion_Mp3_Exists() throws FileAlreadyExistsException, IOException {
    FileUtils.copy(mp3, mp3Fake);

    myShellScriptListener.stallPoints.add("^flac .*");
    File mp3DstDir = null;
    try {
      Syncer syncer = new Syncer(myShellScriptListener, flacToMp3Impl);
      syncer.start();
      while (!syncer.busy.get()) {
        try {
          Thread.sleep(1);
        }
        catch (InterruptedException e) {
          /* swallow */
        }
      }

      mp3DstDir = new File(tmpTestDir, "from.flac");
      File mp3Dst = new File(mp3DstDir, "mp3Dst.mp3");
      boolean r = flacToMp3Impl.convert(flac2mp3Config, flac, mp3Dst, true);

      try {
        syncer.join();
      }
      catch (InterruptedException e) {
        /* swallow */
      }

      assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
      assertThat(Boolean.valueOf(mp3Fake.exists()), equalTo(Boolean.TRUE));
    }
    finally {
      mp3Fake.delete();
      FileUtils.delete(mp3DstDir);
    }
  }

  @Test
  public void testConvert_StopBeforeTagCopy_Mp3_Exists() throws FileAlreadyExistsException, IOException {
    FileUtils.copy(mp3, mp3Fake);

    myShellScriptListener.stallPoints.add("^id3v2 .*");
    File mp3DstDir = null;
    try {
      Syncer syncer = new Syncer(myShellScriptListener, flacToMp3Impl);
      syncer.start();
      while (!syncer.busy.get()) {
        try {
          Thread.sleep(1);
        }
        catch (InterruptedException e) {
          /* swallow */
        }
      }

      mp3DstDir = new File(tmpTestDir, "from.flac");
      File mp3Dst = new File(mp3DstDir, "mp3Dst.mp3");
      boolean r = flacToMp3Impl.convert(flac2mp3Config, flac, mp3Dst, true);

      try {
        syncer.join();
      }
      catch (InterruptedException e) {
        /* swallow */
      }

      assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
      assertThat(Boolean.valueOf(mp3Fake.exists()), equalTo(Boolean.TRUE));
    }
    finally {
      mp3Fake.delete();
      FileUtils.delete(mp3DstDir);
    }
  }

  @Test
  public void testConvert_StopBeforeTimestampCopy_Mp3_Exists() throws FileAlreadyExistsException, IOException {
    FileUtils.copy(mp3, mp3Fake);

    myShellScriptListener.stallPoints.add("^touch .*");
    File mp3DstDir = null;
    try {
      Syncer syncer = new Syncer(myShellScriptListener, flacToMp3Impl);
      syncer.start();
      while (!syncer.busy.get()) {
        try {
          Thread.sleep(1);
        }
        catch (InterruptedException e) {
          /* swallow */
        }
      }

      mp3DstDir = new File(tmpTestDir, "from.flac");
      File mp3Dst = new File(mp3DstDir, "mp3Dst.mp3");
      boolean r = flacToMp3Impl.convert(flac2mp3Config, flac, mp3Dst, true);

      try {
        syncer.join();
      }
      catch (InterruptedException e) {
        /* swallow */
      }

      assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
      assertThat(Boolean.valueOf(mp3Fake.exists()), equalTo(Boolean.TRUE));
    }
    finally {
      mp3Fake.delete();
      FileUtils.delete(mp3DstDir);
    }
  }

  @Test
  public void testConvert_Normal_Simulate() throws IOException {
    File mp3DstDir = new File(tmpTestDir, "from.flac");
    File mp3Dst = new File(mp3DstDir, "mp3Dst.mp3");
    mp3Dst.delete();

    try {
      boolean r = flacToMp3Impl.convert(flac2mp3Config, flac, mp3Dst, true);

      assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));
      assertThat(Boolean.valueOf(mp3DstDir.exists()), equalTo(Boolean.FALSE));
      assertThat(Boolean.valueOf(mp3Dst.exists()), equalTo(Boolean.FALSE));
    }
    finally {
      FileUtils.delete(mp3DstDir);
    }
  }

  @Test
  public void testConvert_Normal_Simulate_NoListener() throws IOException {
    File mp3DstDir = new File(tmpTestDir, "from.flac");
    File mp3Dst = new File(mp3DstDir, "mp3Dst.mp3");
    mp3Dst.delete();
    flacToMp3Impl.setShellScriptListener(null);

    try {
      boolean r = flacToMp3Impl.convert(flac2mp3Config, flac, mp3Dst, true);

      assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));
      assertThat(Boolean.valueOf(mp3DstDir.exists()), equalTo(Boolean.FALSE));
      assertThat(Boolean.valueOf(mp3Dst.exists()), equalTo(Boolean.FALSE));
    }
    finally {
      FileUtils.delete(mp3DstDir);
    }
  }

  @Test
  public void testConvert_Normal() throws IOException {
    File mp3DstDir = new File(tmpTestDir, "from.flac");
    File mp3Dst = new File(mp3DstDir, "mp3Dst.mp3");
    mp3Dst.delete();

    try {
      boolean r = flacToMp3Impl.convert(flac2mp3Config, flac, mp3Dst, false);

      assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));
      assertThat(Boolean.valueOf(mp3DstDir.exists()), equalTo(Boolean.TRUE));
      assertThat(Boolean.valueOf(mp3Dst.exists()), equalTo(Boolean.TRUE));
    }
    finally {
      mp3Dst.delete();
      FileUtils.delete(mp3DstDir);
    }
  }

  @Test
  public void testConvert_Normal_NoListener() throws IOException {
    File mp3DstDir = new File(tmpTestDir, "from.flac");
    File mp3Dst = new File(mp3DstDir, "mp3Dst.mp3");
    mp3Dst.delete();
    flacToMp3Impl.setShellScriptListener(null);

    try {
      boolean r = flacToMp3Impl.convert(flac2mp3Config, flac, mp3Dst, false);

      assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));
      assertThat(Boolean.valueOf(mp3DstDir.exists()), equalTo(Boolean.TRUE));
      assertThat(Boolean.valueOf(mp3Dst.exists()), equalTo(Boolean.TRUE));
    }
    finally {
      mp3Dst.delete();
      FileUtils.delete(mp3DstDir);
    }
  }
}
