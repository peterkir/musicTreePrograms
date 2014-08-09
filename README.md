# <a name="MusicTreeProgramsIntroduction"/>Music Tree Programs - Introduction

I have a large tree with music.
Most of it is in the flac format, while I also have many mp3
files and some files in other formats.

My car audio system and some of my other music players don't
understand flac while they all understand mp3.

My solution is to transcode my flac files into mp3 files while
copying the tag information from the flac file to the mp3 file.

To this end I've built two programs:

* &nbsp;**flac2mp3** performs flac-to-mp3 transcoding and tree syncing.

* &nbsp;**audiotagchecker** checks the tags of music files (currently
both flac and mp3) against certain criteria that I personally want
to enforce.

Both programs are OSGi programs, built with
bnd*tools* (http://bndtools.org) in Eclipse.


# <a name="TableOfContents"/>Table Of Contents

* [Music Tree Programs - Introduction](#MusicTreeProgramsIntroduction)
* [Table Of Contents](#TableOfContents)
* [Music Tree Layout](#MusicTreeLayout)
* [Building](#Building)
* [flac2mp3](#flac2mp3)
  * [Functionality](#flac2mp3Functionality)
  * [Requirements](#flac2mp3Requirements)
  * [Example](#flac2mp3Example)
  * [Running](#flac2mp3Running)
  * [Installation](#flac2mp3Installation)
* [audiotagchecker](#audiotagchecker)
  * [Functionality](#audiotagcheckerFunctionality)
  * [Checks](#audiotagcheckerChecks)
    * [Basic Checks](#audiotagcheckerChecksBasic)
    * [Filename Checks](#audiotagcheckerChecksFilename)
    * [Unwanted Character Checks](#audiotagcheckerChecksChars)
    * [ID3v1 Truncation Checks](#audiotagcheckerChecksID3v1)
    * [Remarks](#audiotagcheckerChecksRemarks)
  * [Requirements](#audiotagcheckerRequirements)
  * [Running](#audiotagcheckerRunning)
  * [Installation](#audiotagcheckerInstallation)

# <a name="MusicTreeLayout"/>Music Tree Layout

The layout of my music tree is as shown below.

```
Base Directory
  +-- from.flac
  +-- Music
        +-- flac
              +-- Artist
                    +-- Album
                          +-- 01 - song 1.flac
                          +-- 02 - song 2.flac
                          ...
                          +-- cover.jpg
        +-- mp3
              +-- Artist
                    +-- Album
                          +-- 01 - song 1.mp3
                          +-- 02 - song 2.mp3
                          ...
                          +-- cover.jpg
```

# <a name="Building"/>Building

You'll need at least Gradle 2.0. For instructions on how to install Gradle, see
the [build setup documentation](BUILDING-GRADLE.md).


In the root of the checkout, run

```
gradle export
```

# <a name="flac2mp3"/>flac2mp3

## <a name="flac2mp3Functionality"/>Functionality

The program 'mirrors' a tree with flac files into a tree with mp3 files:

* Every flac file is transcoded to a mp3 file.

* The tag information from the flac file is copied into the tag of the mp3 file.

* The mp3 file is placed in the same tree hierarchy as the flac file.

* The timestamp of the mp3 file is set to be the same as that of the flac file.

* The cover image is copied from the flac album directory into the mp3 album
  directory.

* The timestamp of the mp3 cover is set to be the same as that of the flac
  cover.

All these actions are only performed when the relevant (flac tree) file has
changed (has a newer timestamp than the corresponding file in the mp3 tree).

Superfluous files in the mp3 tree are removed.

**Summary**

The whole operation can be understood as an rsync of flac files and cover.jpg
files exclusively, where the flac files are transcoded into mp3 files.

## <a name="flac2mp3Requirements"/>Requirements

You'll need ```flac``` (http://flac.sourceforge.net/)
and ```lame``` (http://lame.sourceforge.net) in your path.

## <a name="flac2mp3Example"/>Example

Running the program on the tree that is shown above will result in the
following tree:

```
Base Directory
  +-- from.flac
        +-- Music
              +-- flac
                    +-- Artist
                          +-- Album
                                +-- 01 - song 1.mp3
                                +-- 02 - song 2.mp3
                                ...
                                +-- cover.jpg
  +-- Music
        +-- flac
              +-- Artist
                    +-- Album
                          +-- 01 - song 1.flac
                          +-- 02 - song 2.flac
                          ...
                          +-- cover.jpg
        +-- mp3
              +-- Artist
                    +-- Album
                          +-- 01 - song 1.mp3
                          +-- 02 - song 2.mp3
                          ...
                          +-- cover.jpg
```

## <a name="flac2mp3Running"/>Running

After building, the program can be found in the directory

```
flac2mp3/generated/distributions
```

Getting usage information for the program is performed by running:

```
java -jar flac2mp3/generated/distributions/flac2mp3.jar -h
```

## <a name="flac2mp3Installation"/>Installation

It is recommended to copy the ```flac2mp3.jar``` file into
the ```Base Directory``` of your music tree.

If your music tree has the same layout as mine then you can just run the
program from there without arguments to mirror your flac tree in the ```Music```
tree into an mp3 tree in the ```from.flac``` directory.


# <a name="audiotagchecker"/>audiotagchecker

## <a name="audiotagcheckerFunctionality"/>Functionality

The program checks the tags of music files (currently both flac and mp3) against
certain criteria that I personally want to enforce.

For these checks I make a distinction between primary, pseudo-primary, and
non-primary fields:

* Primary fields:

  * Album title.

  * Album disc number.

  * Total number of album tracks.

  * Album genre.

  * Album year.

  * Track artist.

  * Track number.

  * Track title.

* Pseudo-primary fields:

  * Album artist.

* Non-primary fields:

   * All other fields

## <a name="audiotagcheckerChecks"/>Checks

<a name="audiotagcheckerChecksBasic"/>**Basic Checks**

* On primary fields:

  * A tag is present.

  * The tag has no artwork.

  * All primary fields are present.

* On primary and pseudo-primary fields fields:

  * Fields are not empty.

  * Fields have no leading and/or trailing whitespace.

  * Fields don't have 2 or more consecutive whitespace characters.

  * Disc number fields are formatted as number/number.

  * The current disc number not greater than the total number of discs.

  * Disc number fields are formatted as number/number.

  * Track number is a number.

  * Total number of track on the album is a number.

  * Album year is a 4-digit year between 1500 and this year.

<a name="audiotagcheckerChecksFilename"/>**Filename Checks**

* In case there is only 1 'disc' in the 'album', then the filename must be
formatted as:

  * &nbsp;```trackNumber - trackTitle.extension```.

  * &nbsp;```trackNumber``` has 2 digits, with leading zero.

  * For example: ```05 - Forty-Six & 2.flac```.

* In case there are multiple 'discs' in the 'album', then the filename must be
formatted as:

  * &nbsp;```discNumberTrackNumber - trackTitle.extension```.

  * &nbsp;```discNumber``` has 2 or 3 digits, with leading zero.

  * &nbsp;```trackNumber``` has 2 digits, with leading zero.

  * For example: ```0103 - One Look.flac```

* The directory in which the file is located must be equal to the album title.

* In case the album artist tag field is set, then the parent directory of the
  directory in which the file is located must be equal to the album artist,
  otherwise it must be equal to the track artist

* Summary:

  * A music file is expected on the path
    &nbsp;```.../artistName/albumTitle/numbers - trackTitle.extension```, or

  * on the path ```.../albumArtist/albumTitle/numbers - trackTitle.extension```.

<a name="audiotagcheckerChecksChars"/>**Unwanted Character Checks**

* On primary and pseudo-primary fields fields:

  * Only regular ASCII characters are allowed; character codes ```[32, 126]```
    (hex ```[20, 7e]```).

<a name="audiotagcheckerChecksID3v1"/>**ID3v1 Truncation Checks**

  * Checks the album title, track artist, track title and album artist fields
    to see if they might be truncated (are exactly 30 characters long)
    because they're (or were) part of an ID3v1 tag.


<a name="audiotagcheckerChecksGeneric"/>**Remarks**

All tag checkers can be individually enable or disabled.

I personally almost always disable the ID3v1 truncation checks.


## <a name="audiotagcheckerRequirements"/>Requirements

No special requirements.

## <a name="audiotagcheckerRunning"/>Running

After building, the program can be found in the directory

```
audiotagchecker/generated/distributions
```

Getting usage information for the program is performed by running:

```
java -jar audiotagchecker/generated/distributions/audiotagchecker.jar -h
```

## <a name="audiotagcheckerInstallation"/>Installation

It is recommended to copy the ```audiotagchecker.jar``` file into
the ```Base Directory``` of your music tree.

If your music tree has the same layout as mine then you can just run the
program from there with the single ```Music``` argument to checks all tags in
the ```Music``` tree.
