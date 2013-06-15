# Music Tree Programs

## Introduction

I have a large tree with music.
Most of it is in the flac format, while I also have many mp3
files and some files in other formats.

My car audio system and some of my other music players don't
understand flac while they all understand mp3.

My solution is to transcode my flac files into mp3 files while
copying the tag information from the flac file to the mp3 file.

To this end I've built two programs:

* **flac2mp3**: performs flac-to-mp3 transcoding and tree syncing.
* **audiotagchecker**: checks the tags of music files (currently
both flac and mp3) against certain criteria that I personally want
to enforce.

Both programs are OSGi programs, built with
bnd*tools* (http://bndtools.org) in Eclipse.


## Music Tree Layout

The layout of my music tree is as shown below.

```
Base
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

## Building

In the root of the checkout, run

```
ant build package
```

## flac2mp3

### Functionality

The program 'mirrors' a tree with flac files into a tree with mp3 files:

* Every flac file is transcoded to a mp3 file
* The tag information from the flac file is copied into the tag of the mp3 file
* The mp3 file is placed in the same tree hierarchy as the flac file
* The timestamp of the mp3 file is set to be the same as that of the flac file
* The cover image is copied from the flac album directory into the mp3 album directory
* The timestamp of the mp3 cover is set to be the same as that of the flac cover

All these actions are only performed when the relevant (flac tree) file has changed (has
a newer timestamp than the corresponding file in the mp3 tree).

Superfluous files in the mp3 tree are removed.

**Summary**:
The whole operation can be understood as an rsync of flac files and cover.jpg files exclusively,
where the flac files are transcoded into mp3 files.

## Requirements

You'll need ```flac``` (http://flac.sourceforge.net/) and ```lame``` (http://lame.sourceforge.net) in your path.

## Example
Running the program on the tree that is shown above will result in the following tree:

```
Base
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

### Running

After building, the program can be found in the directory

```
nl.pelagic.musicTree.flac2mp3.cli/generated/packaged
```

Getting usage information for the program is performed by running:

```
java -jar nl.pelagic.musicTree.flac2mp3.cli/generated/packaged/flac2mp3.jar -h
```

### Installation

It is recommended to copy the ```flac2mp3.jar``` file into the ```Base``` directory of your music tree.

If your music tree has the same layout as mine then you can just run the program from there
without arguments to mirror your flac tree in the ```Music``` tree into an mp3 tree in the ```from.flac``` directory.


## audiotagchecker

### Functionality

The program checks the tags of music files (currently both flac and mp3) against certain criteria that I personally want to enforce.

For the checks I make a distinction between primary, pseudo-primary, and non-primary fields:

* Primary fields
    * Album title
    * Album disc number
    * Total number of album tracks
    * Album genre
    * Album year
    * Track artist
    * Track number
    * Track title
* Pseudo-primary fields
    * Album artist
* Non-primary fields
    * All other fields

The checks that are performed:

* Basic checks
    * On primary fields:
        * A tag is present
        * The tag has no artwork
        * All primary fields are present
    * On primary and pseudo-primary fields fields:
        * Fields are not empty
        * Fields have no leading and/or trailing whitespace
        * Fields don't have 2 or more consecutive whitespace characters
        * Disc number fields are formatted as number/number
        * The current disc number not greater than the total number of discs
        * Disc number fields are formatted as number/number
        * Track number is a number
        * Total number of track on the album is a number
        * Album year is a 4-digit year between 1500 and this year

* Filename checks
    * In case there is only 1 'disc' in the 'album', then the filename must be formatted as:
        * ```trackNumber - trackTitle.extension```
        * (trackNumber has 2 digits, with leading zero)
        * For example: 05 - Forty-Six & 2.flac
    * In case there are multiple 'discs' in the 'album', then the filename must be formatted as:
        * ```discNumberTrackNumber - trackTitle.extension```
        * (discNumber has 2 or 3 digits, with leading zero)
        * (trackNumber has 2 digits, with leading zero)
        * For example: 0103 - One Look.flac
    * The directory in which the file is located must be equal to the album title.
    * In case the album artist tag field is set, then the parent directory of the directory in which the file is located must be equal to the album artist, otherwise it must be equal to the track artist
    * Summary:
        * A music file is expected on the path
            * ```.../artistName/albumTitle/numbers - trackTitle.extension```
        * or
            * ```.../albumArtist/albumTitle/numbers - trackTitle.extension```

* Unwanted character checks
    * On primary and pseudo-primary fields fields:
        * Only regular ASCII characters are allowed; character codes ```[32, 126]``` (hex ```[20, 7e]```)

* ID3v1 truncation checks
    * Checks the album title, track artist, track title and album artist fields to see if they might be truncated (are exactly 30 characters long) because they're part of an ID3v1 tag.


All tag checkers can be individually enable and/or disabled.

I personally almost always disable the ID3v1 truncation checks.


## Requirements

No special requirements.

### Running

After building, the program can be found in the directory

```
/nl.pelagic.audio.tag.checker.cli/generated/packaged
```

Getting usage information for the program is performed by running:

```
java -jar /nl.pelagic.audio.tag.checker.cli/generated/packaged/audiotagchecker.jar -h
```

### Installation

It is recommended to copy the ```audiotagchecker.jar``` file into the ```Base``` directory of your music tree.

If your music tree has the same layout as mine then you can just run the program from there
with the single ```Music``` argument to checks all tags in the ```Music``` tree.