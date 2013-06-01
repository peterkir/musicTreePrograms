package nl.pelagic.audio.tag.checker.types;

import aQute.bnd.annotation.ProviderType;

/**
 * A generalised tag field name
 */
@ProviderType
public enum GenericTagFieldName {

  /* Primary fields. These I really want in my tags */

  /** Disc Number, like 1/2 */
  ALBUMDISCNUMBER,

  /** Album genre */
  ALBUMGENRE,

  /** Album title */
  ALBUMTITLE,

  /** Total number of tracks of the album */
  ALBUMTOTALTRACKS,

  /** Year of the album */
  ALBUMYEAR,

  /** Track artist */
  TRACKARTIST,

  /** Track number */
  TRACKNUMBER,

  /** Track title */
  TRACKTITLE,

  /*
   * Pseudo-primary (primary-but-optional) fields: in isPrimary() but not in
   * getPrimaries(). These are in the tag if the album artist is not the same as
   * the track artist.
   */

  /** Album artist */
  ALBUMARTIST,

  /*
   * Other non-primary fields. All other encountered fields are aggregated under
   * this field name.
   */

  /** Another (non-primary) field */
  OTHER,

  /* Reporting-only fields */

  /** The file path */
  FILE,

  /** Artwork */
  ARTWORK;

  /**
   * Determines if the field name is a primary field or (optionally) a pseudo
   * primary field.
   * 
   * @param includePseudoPrimaries when true, the pseudo primary fields will be
   *          included in the evaluation
   * @return true when the instance expresses a primary field (includes
   *         pseudo-primaries)
   */
  public boolean isPrimary(boolean includePseudoPrimaries) {
    return !(isReportingOnly() || isNonPrimary() || (!includePseudoPrimaries && isPseudoPrimary()));
  }

  /**
   * @return true when the instance expresses a pseudo primary field
   */
  public boolean isPseudoPrimary() {
    return this.equals(ALBUMARTIST);
  }

  /**
   * @return true when the instance expresses a non-primary field
   */
  public boolean isNonPrimary() {
    return this.equals(OTHER);
  }

  /**
   * @return true when the instance expresses a reporting-only field
   */
  public boolean isReportingOnly() {
    return this.equals(FILE) || this.equals(ARTWORK);
  }

  /**
   * Get the primary fields. Optionally including pseudo primary fields.
   * 
   * @param includePseudoPrimaries when true, the pseudo primary fields will be
   *          included
   * @return an array with all primary fields (excludes pseudo-primaries)
   */
  public static GenericTagFieldName[] getPrimaries(boolean includePseudoPrimaries) {
    if (!includePseudoPrimaries) {
      GenericTagFieldName[] resultList = {
          ALBUMDISCNUMBER, ALBUMGENRE, ALBUMTITLE, ALBUMTOTALTRACKS, ALBUMYEAR, TRACKARTIST, TRACKNUMBER, TRACKTITLE
      };
      return resultList;
    }

    GenericTagFieldName[] resultList =
        {
            ALBUMARTIST,
            ALBUMDISCNUMBER,
            ALBUMGENRE,
            ALBUMTITLE,
            ALBUMTOTALTRACKS,
            ALBUMYEAR,
            TRACKARTIST,
            TRACKNUMBER,
            TRACKTITLE
        };
    return resultList;
  }
}
