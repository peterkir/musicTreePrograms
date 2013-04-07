# Music Tree Programs

## Introduction

I have a large tree with music.
Most of it is in the flac format, while I also have many mp3
files and some files in other formats.

My car audio system and some of my other music players don't understand
flac while they all understand mp3.

My solution is to transcode my flac files into mp3 files while copying the
tag information from the flac file to the mp3 file.

To this end I've built the flac2mp3 program.


The program is an OSGi program, built with bndtools (http://bndtools.org) in Eclipse.


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

## flac2mp3

### Functionality

The program 'mirrors' a tree with flac files into a tree with mp3 files:

* Every flac file is transcoded to a mp3 file
* The tag information from the flac file is copied into the tag of the mp3 file
* The mp3 file  is placed in the same tree hierarchy as the flac file
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

### Building

In the root of the checkout, run

```
ant build package
```

### Running

After building, the program can be found in the directory

```
nl.pelagic.musicTree.flac2mp3/generated/packaged
```

Getting usage information for the program is performed by running:

```
java -jar nl.pelagic.musicTree.flac2mp3/generated/packaged/flac2mp3.jar -h
```

### Installation

It is advised to copy the ```flac2mp3.jar``` file into the ```Base``` directory of your music tree.

If your music tree has the same layout as mine then you can just run the program from there
without arguments to mirror your flac tree in the ```Music``` tree into an mp3 tree in the ```from.flac``` directory.