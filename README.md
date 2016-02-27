# folio-rename
Folio Batch Renamer - a simple Java application to rename files, typically pictures, according to the fashion used for manuscript material distributed across folios, with a recto and verso
Folio Batch Renamer


This tool is designed to help users who need to batch rename files, typically pictures, according 
to the fashion used for manuscript material, i.e. where the same folio number appears twice, typically
followed by an indication of "recto" or "verso" side. Although rather trivial, this operation is 
not easily performed  with traditional batch rename tools, especially for people who are not specially 
familiar with regular expressions or such.

The tool works on files as well as folders, but always processes them one type at a time. 
It goes through the list of files (or folders), following the alphabetical order, and renames each 
according to the rules defined by the user.
 The first tab, "Separate files for r/v", is designed to batch rename files or folder where 
each file or folder represents a single face of a folio, recto or verso. 
The second tab, "Single file for r/v", is designed to batch rename files or folders where each 
file or folder represents the full view of an open manuscript, with the verso of a folio and 
the recto of the following.
The rules define: 
 	- whether the files or the folders should be renamed
 	- if a "prefix" should be added to the new file/folder name 
(e.g.: "Paris, BnF, lat. 16480, fol. ")
 	- the folio number from which the automatic numbering should start, 
 	- on how many digits this folio number should be represented (for instance, if your first 
folio number is 99, starting on a recto, and you choose in the combo menu to represent the folio
number on 4 digits, the 
first file of the list will be renamed as "0099r"
 	- whether the first file to be renamed corresponds to a recto or a verso
 	- you can choose the style or suffix you want to apply for recto or verso (by default, 
"r" and "v" respectively). If you wish to add a character or string of characters between 
the folio number and its recto or verso suffix, use the text box preceding each combo menu
 (for instance, you may choose to insert a blank space between the fol. number and the 
recto / verso suffix).
 	- Finally, you can add a general suffix, that will follow the recto / verso suffix. 
The application can run in test mode, showing you how the rules would be applied 
to your files / folders.


DISCLAIMER
This application is in beta-test mode, do not use it on important material without making 
a copy of your files. 
Generally, use at your own risk.

Project manager: Marjorie Burghart, CNRS - CIHAM UMR 5648 <marjorie.burghart@ehess.fr>
February 2015

This source code makes use of and modifies the code of the "batchfilerenametool" application 
(https://code.google.com/p/batchfilerenametool/)
 by CharCheng Mun and Cheong Wee Lau (August 2009)

 licence GNU GPL (v3)
