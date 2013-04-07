package nl.pelagic.audio.conversion.flac2mp3;

import java.util.Date;

import nl.pelagic.audio.conversion.flac2mp3.i18n.Messages;
import nl.pelagic.jaudiotagger.util.TagUtils;

import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.flac.FlacTag;

/**
 * The tag information to transfer from a flac file into it's converted mp3 file
 */
public class TagInformation {
  /** default value for the album */
  public static final String DEFAULT_ALBUM = new Date().toString();

  /** default value for the album artist */
  public static final String DEFAULT_ALBUMARTIST = Messages.getString("TagInformation.0"); //$NON-NLS-1$

  /** default value for the artist */
  public static final String DEFAULT_ARTIST = Messages.getString("TagInformation.1"); //$NON-NLS-1$

  /** default value for the date (year) of the album */
  public static final String DEFAULT_DATE = ""; //$NON-NLS-1$

  /** default value for the disc number (for example: 1/2) */
  public static final String DEFAULT_DISCNUMBER = ""; //$NON-NLS-1$

  /** default value for the genre */
  public static final String DEFAULT_GENRE = ""; //$NON-NLS-1$

  /** default value for the track title */
  public static final String DEFAULT_TITLE = Messages.getString("TagInformation.2"); //$NON-NLS-1$

  /** default value for the track number */
  public static final String DEFAULT_TRACKNUMBER = ""; //$NON-NLS-1$

  /** default value for the total number of tracks on the disc */
  public static final String DEFAULT_TRACKTOTAL = ""; //$NON-NLS-1$

  /** the album */
  private String album = DEFAULT_ALBUM;

  /** the album artist */
  private String albumArtist = DEFAULT_ALBUMARTIST;

  /** the artist */
  private String artist = DEFAULT_ARTIST;

  /** the album year/date */
  private String date = DEFAULT_DATE;

  /** the album disc number */
  private String discNumber = DEFAULT_DISCNUMBER;

  /** the track genre */
  private String genre = DEFAULT_GENRE;

  /** the track title */
  private String title = DEFAULT_TITLE;

  /** the track number */
  private String trackNumber = DEFAULT_TRACKNUMBER;

  /** the total number of tracks on the album */
  private String trackTotal = DEFAULT_TRACKTOTAL;

  /**
   * Default constructor.
   */
  TagInformation() {
    super();
    /* default constructor for tests */
  }

  /**
   * Construct tag information from a flac tag
   * 
   * @param flacTag the flac tag
   */
  public TagInformation(FlacTag flacTag) {
    super();

    if (flacTag == null) {
      return;
    }

    album = TagUtils.concatenateTagFields(flacTag.getFields(FieldKey.ALBUM), album);
    albumArtist = TagUtils.concatenateTagFields(flacTag.getFields(FieldKey.ALBUM_ARTIST), albumArtist);
    artist = TagUtils.concatenateTagFields(flacTag.getFields(FieldKey.ARTIST), artist);
    date = TagUtils.concatenateTagFields(flacTag.getFields(FieldKey.YEAR), date);
    discNumber = TagUtils.concatenateTagFields(flacTag.getFields(FieldKey.DISC_NO), discNumber);
    genre = TagUtils.concatenateTagFields(flacTag.getFields(FieldKey.GENRE), genre);
    title = TagUtils.concatenateTagFields(flacTag.getFields(FieldKey.TITLE), title);
    trackNumber = TagUtils.concatenateTagFields(flacTag.getFields(FieldKey.TRACK), trackNumber);
    trackTotal = TagUtils.concatenateTagFields(flacTag.getFields(FieldKey.TRACK_TOTAL), trackTotal);
  }

  /**
   * @return the album
   */
  public String getAlbum() {
    return album;
  }

  /**
   * @return the albumArtist
   */
  public String getAlbumArtist() {
    return albumArtist;
  }

  /**
   * @return the artist
   */
  public String getArtist() {
    return artist;
  }

  /**
   * @return the date
   */
  public String getDate() {
    return date;
  }

  /**
   * @return the discNumber
   */
  public String getDiscNumber() {
    return discNumber;
  }

  /**
   * @return the genre
   */
  public String getGenre() {
    return genre;
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * @return the trackNumber
   */
  public String getTrackNumber() {
    return trackNumber;
  }

  /**
   * @return the trackTotal
   */
  public String getTrackTotal() {
    return trackTotal;
  }
}
