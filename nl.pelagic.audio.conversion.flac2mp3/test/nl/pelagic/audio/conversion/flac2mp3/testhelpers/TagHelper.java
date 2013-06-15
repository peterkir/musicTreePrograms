package nl.pelagic.audio.conversion.flac2mp3.testhelpers;

import java.util.Random;

import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.id3.ID3v11Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.reference.GenreTypes;
import org.junit.Ignore;

@Ignore
@SuppressWarnings("javadoc")
public class TagHelper {
  public static String album;
  public static String albumArtist;
  public static String artist;
  public static String date;
  public static String discNumber;
  public static String discTotal;
  public static int genreNumber;
  public static String genre;
  public static String title;
  public static String tracknumber;
  public static String trackTotal;

  public static FlacTag getRandomFlacTag() throws KeyNotFoundException, FieldDataInvalidException {
    FlacTag flacTag = new FlacTag();

    album = Long.valueOf(new Random().nextLong()).toString();
    albumArtist = Long.valueOf(new Random().nextLong()).toString();
    artist = Long.valueOf(new Random().nextLong()).toString();
    date = Integer.valueOf(new Random().nextInt(2012) + 1).toString();
    discNumber = Integer.valueOf(new Random().nextInt(99) + 1).toString();
    discTotal = Integer.valueOf(new Random().nextInt(99) + 1).toString();
    genre = Long.valueOf(new Random().nextLong()).toString();
    title = Long.valueOf(new Random().nextLong()).toString();
    tracknumber = Integer.valueOf(new Random().nextInt(99) + 1).toString();
    trackTotal = Integer.valueOf(new Random().nextInt(99) + 1).toString();

    flacTag.addField(FieldKey.ALBUM, album);
    flacTag.addField(FieldKey.ALBUM_ARTIST, albumArtist);
    flacTag.addField(FieldKey.ARTIST, artist);
    flacTag.addField(FieldKey.YEAR, date);
    flacTag.addField(FieldKey.DISC_NO, discNumber + "/" + discTotal); //$NON-NLS-1$
    flacTag.addField(FieldKey.GENRE, genre);
    flacTag.addField(FieldKey.TITLE, title);
    flacTag.addField(FieldKey.TRACK, tracknumber);
    flacTag.addField(FieldKey.TRACK_TOTAL, trackTotal);

    return flacTag;
  }

  public static ID3v1Tag getRandomID3v1Tag(boolean v10) throws KeyNotFoundException {
    ID3v1Tag id3v1Tag = null;

    if (v10) {
      id3v1Tag = new ID3v1Tag();
    } else {
      id3v1Tag = new ID3v11Tag();
    }

    album = Long.valueOf(new Random().nextLong()).toString();
    /* no FieldKey.ALBUM_ARTIST, */
    artist = Long.valueOf(new Random().nextLong()).toString();
    date = Integer.valueOf(new Random().nextInt(2014)).toString();
    /* no FieldKey.DISC_NO, */
    genreNumber = new Random().nextInt(147);
    genre = GenreTypes.getInstanceOf().getValueForId(genreNumber);
    title = Long.valueOf(new Random().nextLong()).toString();
    tracknumber = v10 ? null : Integer.valueOf(new Random().nextInt(100)).toString();
    /* no FieldKey.TRACK_TOTAL */

    id3v1Tag.setAlbum(album);
    /* no FieldKey.ALBUM_ARTIST, */
    id3v1Tag.setArtist(artist);
    id3v1Tag.setYear(date);
    /* no FieldKey.DISC_NO, */

    id3v1Tag.setGenre(genre);
    id3v1Tag.setTitle(title);
    if (!v10) {
      ID3v11Tag id3v11Tag = (ID3v11Tag) id3v1Tag;
      id3v11Tag.setTrack(tracknumber);
    }
    /* no FieldKey.TRACK_TOTAL */

    return id3v1Tag;
  }
}
