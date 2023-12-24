# folio-rename
Folio Batch Renamer - a simple application to rename files, typically pictures, according to the fashion used for manuscript material distributed across folios, with a recto and verso
Folio Batch Renamer

Folio Batch Renamer is a tool designed to help users who need to batch rename files, typically pictures, according to the fashion used for manuscript material, i.e. where the same folio number appears twice, typically followed by an indication of "recto" or "verso" side. Although rather trivial, this operation is not easily performed  with traditional batch rename tools, especially for people who are not specially familiar with regular expressions or such.
The tool works on files as well as folders, but always processes them one type at a time. It goes through the list of files (or folders), following the alphabetical order, and renames each according to the rules defined by the user.

DISCLAIMER
This application is in beta-test mode, do not use it on important material without making a copy of your files. Generally, use at your own risk.

The tool exists in two versions: 

## the most recent, in Python (Dec. 2023):

In the GUI, the user can define the renaming rules: 
- choose the folder where the files to be renamed are;
- if a "prefix" should be added to the new file/folder name, typically the manuscript shelfmark (e.g.: "Paris, BnF, lat. 16480, fol. ")
- the folio number from which the automatic numbering should start, and...
- ... whether it is a recto or a verso;
- on how many digits this folio number should be represented (for instance, if your first image is of folio number is 99 verso, and you choose to represent the folio number with 4 digits, the first file of the list will be renamed as "0099v"
- you can choose the suffix you want to apply for recto or verso (by default, "r" and "v" respectively);
- finally, you can add a general suffix, that will follow the recto / verso suffix. 
Note that the application can run in "Test mode", showing you how the rules that will be applied  to your files / folders.

## the first version, in Java (Feb. 2015)

The first version has the same features, but it also features an extra tab, "Single file for r/v", designed to batch rename files or folders where each file or folder represents the full view of an open manuscript, with the verso of a folio and the recto of the following.
The source code of the Java version makes use of and modifies the code of the "batchfilerenametool" application (https://code.google.com/p/batchfilerenametool/) by CharCheng Mun and Cheong Wee Lau (August 2009)

If you wish to add a character or string of characters between the folio number and its recto or verso suffix, use the text box preceding each combo menu (for instance, you may choose to insert a blank space between the fol. number and the recto / verso suffix).


Developer: Marjorie Burghart, CNRS - CIHAM UMR 5648 <marjorie.burghart@cnrs.fr>
Licence GNU GPL (v3)
