/* Folio Batch Renamer
 * 
 * This tool is designed to help users who need to batch rename files, typically pictures, according to 
 * the fashion used for manuscript material, i.e. where the same folio number appears twice, typically followed 
 * by an indication of "recto" or "verso" side. Although rather trivial, this operation is not easily performed 
 * with traditional batch rename tools, especially for people who are not specially familiar with regular 
 * expressions or such. 
 * The tool works on files as well as folders, but always processes them one type at a time. It goes through 
 * the list of files (or folders), following the alphabetical order, and renames each according to the rules
 * defined by the user. 
 * The rules define: 
 * 	- whether the files or the folders should be renamed
 * 	- if a "prefix" should be added to the new file/folder name (for instance, "Paris, BnF, lat. 16480, fol. "
 * 	- the folio number from which the automatic numbering should start, 
 * 	- on how many digits this folio number should be represented (for instance, if your first folio number is 99, 
 * starting on a recto, and you choose in the combo menu to represent the folio number on 4 digits, the first file 
 * of the list will be renamed as "0099r"
 * 	- whether the first file to be renamed corresponds to a recto or a verso
 * 	- you can choose the style or suffix you want to apply for recto or verso (by default, "r" and "v" respectively)
 * If you wish to add a character or string of character between the folio number and its recto or verso suffix, use
 * the text box preceding each combo menu (for instance, you may choose to insert a blank space between the fol. number
 * and the recto / verso suffix). 
 * 	- Finally, you can add a general suffix, that will follow the recto / verso suffix. 
 * The application can run in test mode, showing you how the rules would be applied to your files / folders. 
 * 
 * DISCLAIMER
 * This application is in beta-test mode, do not use it on important material without making a copy of your files. 
 * Generally, use at your own risk. And drink responsibly. 
 * 
 * 
* Project manager: Marjorie Burghart, EHESS - CIHAM UMR 5648 <marjorie.burghart@ehess.fr>
* February 2015
* 
* This source code makes use of and modifies the code of the "batchfilerenametool" application 
* (https://code.google.com/p/batchfilerenametool/) by CharCheng Mun and Cheong Wee Lau (August 2009) 
* 
* licence GNU GPL (v3)
*
*/

package folioFileRename;


import java.io.*;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.event.*;
import java.awt.*;

import javax.swing.text.*;
import javax.swing.JTabbedPane;

public class Rename extends JFrame implements ActionListener{   

       private static final String TITLE = "Folio Batch Renamer";
       private static final int WIDTH = 600;
       private static final int HEIGHT = 500;
       private static final String DESCRIPTION = "Select a folder, and define the rules "
       		+ "to rename the files or folder it contains.";
       private boolean OUTPUT_ON = false;
                
       private File directory;   //change all the files in this directory;
       
       private Container contentPane;
       private JTabbedPane tabPnl;
       private JPanel tab1, tab2, tabHelp, pnlDescription, pnlChoice, pnlDirectory, pnlPrefix, pnlRename, pnlFolioOpt, pnlSuffix, pnlCtrl, pnlOption, pnlDescription2, pnlChoice2, pnlDirectory2, pnlPrefix2, pnlRename2, pnlFolioOpt2, pnlSuffix2, pnlCtrl2, pnlOption2;
       private JLabel lblTab1, lblTab2, lblDesc, lblChoice, lblSequence, lblLeadingZero, lblStartWith, lblDirectory, lblFolioOpt, lblDesc2, lblChoice2, lblSequence2, lblLeadingZero2, lblStartWith2, lblDirectory2, lblFolioOpt2, lblSeparator;
       private JTextField txtDirectory, txtPrefix, txtSuffix, txtRename, txtSequence, txtPreRecto, txtPreVerso, txtDirectory2, txtPrefix2, txtSuffix2, txtRename2, txtSequence2, txtPreRecto2, txtPreVerso2, txtSeparator;
       private JButton btnOk, btnCancel, btnAbout, btnOk2, btnCancel2, btnAbout2;
       private JCheckBox cbxPrefix, cbxSuffix, cbxRename, cbxIgnoreExtension, cbxExperiment, cbxOutput, cbxPrefix2, cbxSuffix2, cbxRename2, cbxIgnoreExtension2, cbxExperiment2, cbxOutput2;
       private JComboBox<?> cboSequence, cboStartWith, cboFolioOptRecto, cboFolioOptVerso, cboSequence2, cboFolioOptRecto2, cboFolioOptVerso2;
       private Dimension stdDim;
       private ButtonGroup bg1, bg2;
       private JRadioButton choiceFiles, choiceFolders, choiceFiles2, choiceFolders2;
       private static JFrame outputFrame; //the output display of the system console
       
       //------- Output Window ----- variables
       private JScrollPane scrOutput;
       private JTextArea txaOutput;
       
       
       //Constructor - never should have a return type, a lil mistake make thing doesn't work
       public Rename(){
               
               setSize(WIDTH, HEIGHT);
               setTitle(TITLE);
               setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
               setResizable(false);
               setLocation(400, 300);
               
               //Add the closingWindow listener
       this.addWindowListener(
                       new WindowAdapter(){
                               public void windowClosing(WindowEvent e){
                                       int response = JOptionPane.showConfirmDialog(null, "Are you sure want to exit?", 
                                               "Close Program", JOptionPane.YES_NO_OPTION);
                                               
                                       if(response == JOptionPane.YES_OPTION)
                                               System.exit(0);
                               }
                       }//end - WindowAdapter
               ); //close - addWindowListener
               
               buildGUI();
       
       }// end constructor
       
       
       
       //Main
   public static void main(String[] args) {    
       
       Rename renameTask = new Rename();
          
       renameTask.setVisible(true);            
   }//end Main
   
   
   private void renameFile(){
       
       boolean operationResult = false;
       boolean overallResult = true;
       int failCount = 0;
       
       /* the operation of this part is ensured by the chooseDirectory()
        * We get the list of files in the directory
        * get the conditions set by users
        * and perform the file rename operation.
        */
        
       //Let's get all the information from user
       String[] fileList = directory.list();  //the list of files in the directory
       String Prefix = txtPrefix.getText();
       String Rename = txtRename.getText();
       String Suffix = txtSuffix.getText();
       String PreRecto = txtPreRecto.getText();
       String PreVerso = txtPreVerso.getText();
       String digits = (String) cboSequence.getSelectedItem();
       String Folio;
       String startsWith = (String) cboStartWith.getSelectedItem();
       int StartingNum;
       int currentFileIndex = 0;
       int currentFileIndexBis = 0;
       String generatedSequence;
       File oldFileTestType;
       File oldFile = null;
       Arrays.sort(fileList, String.CASE_INSENSITIVE_ORDER);
       if (digits.equals("")) {
    	   digits = "1";
       }
       if(startsWith.equals("verso") == true){
    	   currentFileIndexBis = 1;
   }
       
       //let's call the output frame
       if(cbxOutput.isSelected() && OUTPUT_ON == false){
               buildOutput();
               OUTPUT_ON = true;
       }
               
                      
       for(int i = 0; i < fileList.length; i++){
               /* get the file extension that we need, and form a new name, 
                * we would check if the Ignore File Extension is selected
                */
               oldFile = new File(directory.getPath()+"/"+ fileList[i]);
               
          if ((oldFile.isFile() && choiceFiles.isSelected()) || (oldFile.isDirectory() && choiceFolders.isSelected())) {      
           

        	  
        	  String fileExtension;
        	  String fileName;
        	  
              if (oldFile.isDirectory() && choiceFolders.isSelected()) { 
            	  // folder => extension is irrelevant
            	  fileExtension = "";
            	  fileName = fileList[i]; 
              } else {
            	  // file => extension is relevant                  
                  if(cbxIgnoreExtension.isSelected() == true ){
                      fileExtension = "";
                      //this part get the original filename           
                      fileName = fileList[i];
                  }
                  else {
                      fileExtension = getFileExtension(fileList[i]);
                      //this part get the original filename           
                      fileName = getFileName(fileList[i]);
                  }                      
              }              

                             
               
              String inputInfo = fileList[i];   
               System.out.println(inputInfo);
                        
               if(OUTPUT_ON)
                       txaOutput.append("\n"+inputInfo);
               

               
               if (currentFileIndexBis % 2 == 0) {
            	   // even => recto (currentFileIndex starts at 0, which is even)
            	   Folio = (String) cboFolioOptRecto.getSelectedItem();
            	   Folio = PreRecto + Folio;
            	 } else {
            	   // odd => verso
            	   Folio = (String) cboFolioOptVerso.getSelectedItem();
            	   Folio = PreVerso + Folio;
            	 }

               
               /* generate sequence for the Name
                */
              
                       StartingNum = Integer.parseInt(txtSequence.getText());
                       generatedSequence = nameSequence(StartingNum + currentFileIndex, digits);
              
               
               
               if(StartingNum > 9 && Integer.parseInt(digits) < 2)  {
            	   digits = "2";
               }
               if(StartingNum > 99 && Integer.parseInt(digits) < 3)  {
            	   digits = "3";
               }
               if(StartingNum > 999 && Integer.parseInt(digits) < 4)  {
            	   digits = "4";
               }
               if(StartingNum > 9999 && Integer.parseInt(digits) < 5)  {
            	   digits = "5";
               }
               
               
                       fileName = generatedSequence;
           
               
               
               
               //the New File Name
               String newFileName = Prefix + fileName + Folio + Suffix + fileExtension;
               String tentativeName = " -> "+newFileName+"\n";
               System.out.println(tentativeName);
               
               if(OUTPUT_ON)
                       txaOutput.append("\n"+tentativeName);
               
               
               
               
                   // ! Perform the file rename, if the Experimental Mode is not selected
                   if(cbxExperiment.isSelected() == false){
                       
                       operationResult = oldFile.renameTo(new File(directory.getPath()+"/"+newFileName));
                       String renameResult = "\t*Renamed successfully?: " + operationResult+"\n\n";
                       System.out.println(renameResult);
                               if(operationResult == false)
                                       failCount++;
                                       
                                       if(OUTPUT_ON)
                                       txaOutput.append("\n"+renameResult);
                               
                       //make up the overall result
                       overallResult = (operationResult && overallResult);
                   }
                   
                   if (currentFileIndexBis % 2 == 0) {
                	   // even => recto (currentFileIndex starts at 0, which is odd)
                	   
                	   } else {
                	// odd
                		   currentFileIndex++;
                	 }      
                   
                   
                   
                	   currentFileIndexBis++;
                  
          }
                   
           
        	   
        	                          
        
        	   
           
       }
       
       if(cbxExperiment.isSelected() == false){
               System.out.println("Overall Result: "+overallResult);
               if(overallResult)
                       JOptionPane.showMessageDialog(null, "All files renamed successfully!");
               else
                       JOptionPane.showMessageDialog(null, "File renamed with "+ failCount+ " failure(s)");
       }//end if
                       
   }//end renameFile
   

   
   
   private void renameFileSingle(){
       
       boolean operationResult = false;
       boolean overallResult = true;
       int failCount = 0;
       
       /* the operation of this part is ensured by the chooseDirectory()
        * We get the list of files in the directory
        * get the conditions set by users
        * and perform the file rename operation.
        */
        
       //Let's get all the information from user
       String[] fileList = directory.list();  //the list of files in the directory
       String Prefix = txtPrefix2.getText();
       String Rename = txtRename2.getText();
       String Suffix = txtSuffix2.getText();
       String PreRecto = txtPreRecto2.getText();
       String PreVerso = txtPreVerso2.getText();
       String digits = (String) cboSequence2.getSelectedItem();
       String FolioR = "r";
       String FolioV = "v";
       String Separator = txtSeparator.getText();
       int StartingNum;
       int currentFileIndex = 0;
       int currentFileIndexBis = 0;
       String generatedSequenceVerso;
       String generatedSequenceRecto;
       File oldFileTestType;
       File oldFile = null;
       Arrays.sort(fileList, String.CASE_INSENSITIVE_ORDER);
       if (digits.equals("")) {
    	   digits = "1";
       }
       
       
       //let's call the output frame
       if(cbxOutput2.isSelected() && OUTPUT_ON == false){
               buildOutput();
               OUTPUT_ON = true;
       }
               
                      
       for(int i = 0; i < fileList.length; i++){
               /* get the file extension that we need, and form a new name, 
                * we would check if the Ignore File Extension is selected
                */
               oldFile = new File(directory.getPath()+"/"+ fileList[i]);
               
          if ((oldFile.isFile() && choiceFiles2.isSelected()) || (oldFile.isDirectory() && choiceFolders2.isSelected())) {      
           
        	  
        	  String fileExtension;
        	  
             
              
        	
        	  String fileName;
        	  
              if (oldFile.isDirectory() && choiceFolders2.isSelected()) { 
            	  // folder => extension is irrelevant
            	  fileExtension = "";
            	  fileName = fileList[i]; 
              } else {
            	  // file => extension is relevant                  
                  if(cbxIgnoreExtension2.isSelected() == true ){
                      fileExtension = "";
                      //this part get the original filename           
                      fileName = fileList[i];
                  }
                  else {
                      fileExtension = getFileExtension(fileList[i]);
                      //this part get the original filename           
                      fileName = getFileName(fileList[i]);
                  }                      
              }              

               
              String inputInfo = fileList[i];   
               System.out.println(inputInfo);
                        
               if(OUTPUT_ON)
                       txaOutput.append("\n"+inputInfo);
               

               
            	   FolioR = (String) cboFolioOptRecto2.getSelectedItem();
            	   FolioR = PreRecto + FolioR;
            	   FolioV = (String) cboFolioOptVerso2.getSelectedItem();
            	   FolioV = PreVerso + FolioV;

               
               /* generate sequence for the Name
                */
              
                       StartingNum = Integer.parseInt(txtSequence2.getText());
                       // verso part of the image
                       generatedSequenceVerso = nameSequence(StartingNum + currentFileIndex - 1, digits);

                       // recto part of the image
                       generatedSequenceRecto = nameSequence(StartingNum + currentFileIndex, digits);
              
               
               
               if(StartingNum > 9 && Integer.parseInt(digits) < 2)  {
            	   digits = "2";
               }
               if(StartingNum > 99 && Integer.parseInt(digits) < 3)  {
            	   digits = "3";
               }
               if(StartingNum > 999 && Integer.parseInt(digits) < 4)  {
            	   digits = "4";
               }
               if(StartingNum > 9999 && Integer.parseInt(digits) < 5)  {
            	   digits = "5";
               }
               
               
               
               
               
               //the New File Name
               String newFileName = Prefix + generatedSequenceVerso + FolioV + Separator + generatedSequenceRecto + FolioR + Suffix + fileExtension;
               String tentativeName = " -> "+newFileName+"\n";
               System.out.println(tentativeName);
               
               if(OUTPUT_ON)
                       txaOutput.append("\n"+tentativeName);
               
               
               
               
                   // ! Perform the file rename, if the Experimental Mode is not selected
                   if(cbxExperiment2.isSelected() == false){
                       
                       operationResult = oldFile.renameTo(new File(directory.getPath()+"/"+newFileName));
                       String renameResult = "\t*Renamed successfully?: " + operationResult+"\n\n";
                       System.out.println(renameResult);
                               if(operationResult == false)
                                       failCount++;
                                       
                                       if(OUTPUT_ON)
                                       txaOutput.append("\n"+renameResult);
                               
                       //make up the overall result
                       overallResult = (operationResult && overallResult);
                   }
                   
                   
                		   currentFileIndex++;
                	                   
                   
                	   currentFileIndexBis++;
                  
          }
                   
           
        	   
        	                          
        
        	   
           
       }
       
       if(cbxExperiment2.isSelected() == false){
               System.out.println("Overall Result: "+overallResult);
               if(overallResult)
                       JOptionPane.showMessageDialog(null, "All files renamed successfully!");
               else
                       JOptionPane.showMessageDialog(null, "File renamed with "+ failCount+ " failure(s)");
       }//end if
                       
   }//end renameFileSingle
   
   
   
   
   private boolean chooseDirectory(){
       
       /* Choose the file Directory
        * this will ensure that the class variable directory get the value
        * only when a directory is chosen, then the button Ok will be enabled
        */
        
       JFileChooser fc = new JFileChooser();
       fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
       fc.setAcceptAllFileFilterUsed(false);
       
       int returnval = fc.showOpenDialog(this);

       if(returnval == JFileChooser.APPROVE_OPTION){
               directory = fc.getSelectedFile();
               btnOk.setEnabled(true);
               return true;            
       }
       
       return false;
   }// end chooseDirectory
   private boolean chooseDirectory2(){
       
       /* Choose the file Directory
        * this will ensure that the class variable directory get the value
        * only when a directory is chosen, then the button Ok will be enabled
        */
        
       JFileChooser fc = new JFileChooser();
       fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
       fc.setAcceptAllFileFilterUsed(false);
       
       int returnval = fc.showOpenDialog(this);

       if(returnval == JFileChooser.APPROVE_OPTION){
               directory = fc.getSelectedFile();
               btnOk2.setEnabled(true);
               return true;            
       }
       
       return false;
   }// end chooseDirectory2
   
   
   
   private boolean welcomeScreen(){
       
       //Display the instruction
       JOptionPane.showMessageDialog(null, "Select a folder (all the files will be renamed)", 
               "File Rename Tool", JOptionPane.OK_OPTION);
               
       //Decide the file prefix
       String prefix = JOptionPane.showInputDialog(null, "Please specify the name prefix", "File Rename Tool", JOptionPane.YES_NO_OPTION );
       
       //if it's a null entry, we just make it "" better, than getting a word null.
       if(prefix == null){
               prefix = "";
       }
       
       System.out.println(prefix);
       int agree = JOptionPane.showConfirmDialog(null, "Are you sure?", "Confirmation", JOptionPane.YES_NO_OPTION);
       
       if(agree == JOptionPane.YES_OPTION)
               return true;
               else
                       return false;
   }//end welcomeScreen
   
   
   //buildOutputFrame
   private void buildOutput(){
       outputFrame = new JFrame("Output");
       outputFrame.setSize(WIDTH+100, HEIGHT);
       
       Container outputPane = outputFrame.getContentPane();
       outputPane.setBackground(Color.BLACK);
       outputPane.setForeground(Color.WHITE);
       
       txaOutput = new JTextArea();
       txaOutput.setEditable(false);
       txaOutput.setBackground(Color.BLACK);
       txaOutput.setForeground(Color.WHITE);
       txaOutput.setFont(new Font("Courier New", 1, 15));
       
       scrOutput = new JScrollPane(txaOutput);
       
       outputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
       outputFrame.setVisible(true);
       
       outputFrame.addWindowListener(
               new WindowAdapter(){
                       public void windowClosing(WindowEvent e){
                               outputFrame.dispose();
                               OUTPUT_ON = false;
                       }
               }
               
       );// close addWindowListener
       
       outputPane.add(scrOutput);
   }
   
   
   
   //buildGUI
   private void buildGUI(){
       
       stdDim = new Dimension(WIDTH, 25);
       
       contentPane = getContentPane();
       contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
       tabPnl = new JTabbedPane();
       
       tab1 = new JPanel();
       tab2 = new JPanel();
       tabHelp = new JPanel();
       
       
       
       
       
       
       String initString ="Folio Batch Renamer\n\n\n"
+ "This tool is designed to help users who need to batch rename files, typically pictures, according \n"
+ "to the fashion used for manuscript material, i.e. where the same folio number appears twice, typically\n"
+ "followed by an indication of \"recto\" or \"verso\" side. Although rather trivial, this operation is \n"
+ "not easily performed  with traditional batch rename tools, especially for people who are not specially \n"
+ "familiar with regular expressions or such.\n\n"
+ "The tool works on files as well as folders, but always processes them one type at a time. \n"
+ "It goes through the list of files (or folders), following the alphabetical order, and renames each \n"
+ "according to the rules defined by the user.\n "
+ "The first tab, \"Separate files for r/v\", is designed to batch rename files or folder where \n"
+ "each file or folder represents a single face of a folio, recto or verso. \n"
+ "The second tab, \"Single file for r/v\", is designed to batch rename files or folders where each \n"
+ "file or folder represents the full view of an open manuscript, with the verso of a folio and \n"
+ "the recto of the following.\n"
+ "The rules define: \n"
+ " 	- whether the files or the folders should be renamed\n"
+ " 	- if a \"prefix\" should be added to the new file/folder name \n"
+ "(e.g.: \"Paris, BnF, lat. 16480, fol. \")\n"
+ " 	- the folio number from which the automatic numbering should start, \n"
+ " 	- on how many digits this folio number should be represented (for instance, if your first \n"
+ "folio number is 99, starting on a recto, and you choose in the combo menu to represent the folio\n"
+ "number on 4 digits, the \n"
+ "first file of the list will be renamed as \"0099r\"\n"
+ " 	- whether the first file to be renamed corresponds to a recto or a verso\n"
+ " 	- you can choose the style or suffix you want to apply for recto or verso (by default, \n"
+ "\"r\" and \"v\" respectively). If you wish to add a character or string of characters between \n"
+ "the folio number and its recto or verso suffix, use the text box preceding each combo menu\n "
+ "(for instance, you may choose to insert a blank space between the fol. number and the \n"
+ "recto / verso suffix).\n"
+ " 	- Finally, you can add a general suffix, that will follow the recto / verso suffix. \n"
+ "The application can run in test mode, showing you how the rules would be applied \n"
+ "to your files / folders.\n\n\n"
+ "DISCLAIMER\n"
+ "This application is in beta-test mode, do not use it on important material without making \n"
+ "a copy of your files. \n"
+ "Generally, use at your own risk.\n\n"
+ "Project manager: Marjorie Burghart, EHESS - CIHAM UMR 5648 <marjorie.burghart@ehess.fr>\n"
+ "February 2015\n\n"
+ "This source code makes use of and modifies the code of the \"batchfilerenametool\" application \n"
+ "(https://code.google.com/p/batchfilerenametool/)\n"
+ " by CharCheng Mun and Cheong Wee Lau (August 2009)\n\n "
+ "licence GNU GPL (v3)";

   JTextPane textPane = new JTextPane();
   textPane.setText(initString);
   
       
       JScrollPane scrollFrame = new JScrollPane(tabHelp);
       tabHelp.setAutoscrolls(true);       
       
       tabHelp.add(textPane);
      
       
       lblTab1 = new JLabel("Recto / verso in different files");
       lblTab2 = new JLabel("Recto / verso in same files");
   //    tab1.add(lblTab1);
   //    tab2.add(lblTab2);
       tabPnl.addTab("Separate files for r/v", tab1);
       tabPnl.addTab("Single file for v/r", tab2);
       tabPnl.addTab("Help",  scrollFrame);

       tab1.setLayout( new GridLayout(9, 1));       
       tab2.setLayout( new GridLayout(9, 1));       
       
       contentPane.add(tabPnl);
       buildDescPanel();
       buildChoicePanel();
       buildDirPanel();
       buildPrefixPanel();
       buildRenamePanel();
       buildFolioOptPanel();
       buildSuffixPanel();
       buildOptPanel();
       buildCtrlPanel();
       
       buildDescPanel2();
       buildChoicePanel2();
       buildDirPanel2();
       buildPrefixPanel2();
       buildRenamePanel2();
       buildFolioOptPanel2();
       buildSuffixPanel2();
       buildOptPanel2();
       buildCtrlPanel2();
       
       /* the look and feel part of the GUI, 
        * experimental
        * from http://java.sun.com/docs/books/tutorial/uiswing/lookandfeel/plaf.html
        *
        * 
        */      
               
      
        
               try {
               // Set System L&F
                       UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
               } 
               catch (UnsupportedLookAndFeelException e) {
                       System.out.println(e.getMessage());
               }
               catch (ClassNotFoundException e) {
                       System.out.println(e.getMessage());
               }
               catch (InstantiationException e) {
                       System.out.println(e.getMessage());
               }
               catch (IllegalAccessException e) {
                       System.out.println(e.getMessage());
               }
       

       
   }//end buildGUI
   
   private void buildDescPanel(){
       
       pnlDescription = new JPanel();
       pnlDescription.setLayout(new FlowLayout(FlowLayout.LEADING));
       Border blackline = BorderFactory.createLineBorder(Color.BLACK);
       pnlDescription.setBorder(BorderFactory.createTitledBorder(blackline, "Description"));
       
       //the Upper Description field --------------------
       lblDesc = new JLabel(DESCRIPTION);
       lblDesc.setMaximumSize(new Dimension(WIDTH-10, HEIGHT/10));       
       pnlDescription.add(lblDesc);
          
       
       tab1.add(pnlDescription);
       
       
   }//end buildDescPanel
   
   private void buildDescPanel2(){
       
       pnlDescription2 = new JPanel();
       pnlDescription2.setLayout(new FlowLayout(FlowLayout.LEADING));
       Border blackline = BorderFactory.createLineBorder(Color.BLACK);
       pnlDescription2.setBorder(BorderFactory.createTitledBorder(blackline, "Description"));
       
       //the Upper Description field --------------------
       lblDesc2 = new JLabel(DESCRIPTION);
       lblDesc2.setMaximumSize(new Dimension(WIDTH-10, HEIGHT/10));       
       pnlDescription2.add(lblDesc2);
          
       
       tab2.add(pnlDescription2);
       
       
   }//end buildDescPanel
   
   
   private void buildChoicePanel(){
       
       pnlChoice = new JPanel();
       pnlChoice.setLayout(new FlowLayout());       
       lblChoice = new JLabel("Apply rules only to: ");  
       lblChoice.setMaximumSize(new Dimension(stdDim));       
       pnlChoice.add(lblChoice);
          
       ButtonGroup bg1 = new ButtonGroup();
       choiceFiles = new JRadioButton("files", true);
       choiceFolders = new JRadioButton("folders", false);

       bg1.add(choiceFiles);
       bg1.add(choiceFolders);
       cbxIgnoreExtension = new JCheckBox("Check this box if the files have no extension");
       
       
       pnlChoice.add(choiceFiles);
       pnlChoice.add(choiceFolders);
       pnlChoice.add(cbxIgnoreExtension);
       
       tab1.add(pnlChoice);
       
   }//end buildChoicePanel
   
private void buildChoicePanel2(){
       
       pnlChoice2 = new JPanel();
       pnlChoice2.setLayout(new FlowLayout());       
       lblChoice2 = new JLabel("Apply rules only to: ");  
       lblChoice2.setMaximumSize(new Dimension(stdDim));       
       pnlChoice2.add(lblChoice2);
       cbxIgnoreExtension2 = new JCheckBox("Check this box if the files have no extension");
          
       ButtonGroup bg2 = new ButtonGroup();
       choiceFiles2 = new JRadioButton("files", true);
       choiceFolders2 = new JRadioButton("folders", false);

       bg2.add(choiceFiles2);
       bg2.add(choiceFolders2);

       
   
   
       pnlChoice2.add(choiceFiles2);
       pnlChoice2.add(choiceFolders2);
       pnlChoice2.add(cbxIgnoreExtension2);
       
       tab2.add(pnlChoice2);
       
   }//end buildChoicePanel
   
   
   
   private void buildDirPanel(){
       
       pnlDirectory = new JPanel();
       pnlDirectory.setLayout(new BoxLayout(pnlDirectory, BoxLayout.X_AXIS));
       pnlDirectory.setPreferredSize(stdDim);
       pnlDirectory.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
       
       //choose the Directory ----------------------------
       lblDirectory = new JLabel("Folder: ");
       txtDirectory = new JTextField();
       txtDirectory.setEditable(false);
//       txtDirectory.setPreferredSize(stdDim);
       txtDirectory.setMaximumSize(stdDim);
       JButton btnSelectDirectory = new JButton("select");
       
       ActionListener selectAction = new ActionListener(){
               public void actionPerformed(ActionEvent e){
                       if(chooseDirectory())
                               txtDirectory.setText(directory.getPath()); //set the Path to the directory display
               }
       };
   
       btnSelectDirectory.addActionListener(selectAction); //close addActionListener
       pnlDirectory.add(lblDirectory);
       pnlDirectory.add(txtDirectory);
       pnlDirectory.add(btnSelectDirectory);
       
       tab1.add(pnlDirectory);
       
   }
   
   
private void buildDirPanel2(){
       
       pnlDirectory2 = new JPanel();
       pnlDirectory2.setLayout(new BoxLayout(pnlDirectory2, BoxLayout.X_AXIS));
       pnlDirectory2.setPreferredSize(stdDim);
       pnlDirectory2.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
       
       //choose the Directory ----------------------------
       lblDirectory2 = new JLabel("Folder: ");
       txtDirectory2 = new JTextField();
       txtDirectory2.setEditable(false);
//       txtDirectory.setPreferredSize(stdDim);
       txtDirectory2.setMaximumSize(stdDim);
       
       JButton btnSelectDirectory = new JButton("select");
       
       ActionListener selectAction = new ActionListener(){
               public void actionPerformed(ActionEvent e){
                       if(chooseDirectory2())
                               txtDirectory2.setText(directory.getPath()); //set the Path to the directory display
               }
       };
   
       btnSelectDirectory.addActionListener(selectAction); //close addActionListener
       pnlDirectory2.add(lblDirectory2);
       pnlDirectory2.add(txtDirectory2);
       pnlDirectory2.add(btnSelectDirectory);
       
       tab2.add(pnlDirectory2);
       
   }
   
   
   private void buildPrefixPanel(){
       
       pnlPrefix = new JPanel();
       pnlPrefix.setLayout(new BoxLayout(pnlPrefix, BoxLayout.X_AXIS));
       pnlPrefix.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
       
       // the Prefix -----------------------
       
       //lblPrefix = new JLabel("Prefix");
       txtPrefix = new JTextField(40);
       txtPrefix.setMaximumSize(stdDim);
       txtPrefix.setEditable(false);
       cbxPrefix = new JCheckBox("Prefix");
       cbxPrefix.addActionListener(
               new ActionListener(){
                       public void actionPerformed(ActionEvent e){
                               if(cbxPrefix.isSelected())
                                       txtPrefix.setEditable(true);
                               else{
                                       txtPrefix.setEditable(false);
                                       txtPrefix.setText("");
                               }
                                       
                       }
               }
       ); //close addActionListener
       
       pnlPrefix.add(cbxPrefix);
       pnlPrefix.add(txtPrefix);
       
       tab1.add(pnlPrefix);
       
   }//end buildPrefixPanel
   
  private void buildPrefixPanel2(){
       
       pnlPrefix2 = new JPanel();
       pnlPrefix2.setLayout(new BoxLayout(pnlPrefix2, BoxLayout.X_AXIS));
       pnlPrefix2.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
       
       // the Prefix -----------------------
       
       //lblPrefix = new JLabel("Prefix");
       txtPrefix2 = new JTextField(40);
       txtPrefix2.setMaximumSize(stdDim);
       txtPrefix2.setEditable(false);
       cbxPrefix2 = new JCheckBox("Prefix");
       cbxPrefix2.addActionListener(
               new ActionListener(){
                       public void actionPerformed(ActionEvent e){
                               if(cbxPrefix2.isSelected())
                                       txtPrefix2.setEditable(true);
                               else{
                                       txtPrefix2.setEditable(false);
                                       txtPrefix2.setText("");
                               }
                                       
                       }
               }
       ); //close addActionListener
       
       pnlPrefix2.add(cbxPrefix2);
       pnlPrefix2.add(txtPrefix2);
       
       tab2.add(pnlPrefix2);
       
   }//end buildPrefixPanel
      
   
   private void buildRenamePanel(){
       
       pnlRename = new JPanel();
       pnlRename.setLayout(new BoxLayout(pnlRename, BoxLayout.X_AXIS));
       pnlRename.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
       
       // the Rename -------------------------
       txtRename = new JTextField();
       txtRename.setMaximumSize(stdDim);
       txtRename.setEditable(false);
       cbxRename = new JCheckBox("Rename");
       lblSequence = new JLabel("From folio: ");
       lblLeadingZero = new JLabel(" number of digits: ");
       lblStartWith = new JLabel("start with a ");
       txtSequence = new JTextField(5);
       txtSequence.setEditable(false);
       
       
       Dimension seqDim = new Dimension(150,25);
       txtSequence.setMaximumSize(seqDim);
       txtSequence.setEditable(true);
               
       
       
       String[] sequenceValue = {"", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
       cboSequence = new JComboBox(sequenceValue);
       cboSequence.setMaximumSize(new Dimension(50, 25));
       cboSequence.setEnabled(true);

       String[] startWithValue = {"recto", "verso"};
       cboStartWith = new JComboBox(startWithValue);
       cboStartWith.setMaximumSize(new Dimension(80, 25));
       cboStartWith.setEnabled(true);
       
       
       pnlRename.add(lblSequence);
       pnlRename.add(txtSequence);
       pnlRename.add(lblLeadingZero);
       pnlRename.add(cboSequence);
       pnlRename.add(lblStartWith);
       pnlRename.add(cboStartWith);
       tab1.add(pnlRename);
       
   }//end buildRenamePanel
   
   private void buildRenamePanel2(){
       
       pnlRename2 = new JPanel();
       pnlRename2.setLayout(new BoxLayout(pnlRename2, BoxLayout.X_AXIS));
       pnlRename2.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
       
       // the Rename -------------------------
       txtRename2 = new JTextField();
       txtRename2.setMaximumSize(stdDim);
       txtRename2.setEditable(false);
       cbxRename2 = new JCheckBox("Rename");
       lblSequence2 = new JLabel("From folio (number of first recto): ");
       lblLeadingZero2 = new JLabel(" number of digits: ");
//       lblStartWith2 = new JLabel("start with a ");
       txtSequence2 = new JTextField(5);
       txtSequence2.setEditable(false);
       
       
       Dimension seqDim = new Dimension(150,25);
       txtSequence2.setMaximumSize(seqDim);
       txtSequence2.setEditable(true);
               
       String[] sequenceValue2 = {"", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
       cboSequence2 = new JComboBox(sequenceValue2);
       cboSequence2.setMaximumSize(new Dimension(50, 25));
       cboSequence2.setEnabled(true);
/*
       String[] startWithValue = {"recto", "verso"};
       cboStartWith = new JComboBox(startWithValue);
       cboStartWith.setMaximumSize(new Dimension(80, 25));
       cboStartWith.setEnabled(true);
*/       
       
       pnlRename2.add(lblSequence2);
       pnlRename2.add(txtSequence2);
       pnlRename2.add(lblLeadingZero2);
       pnlRename2.add(cboSequence2);
//       pnlRename.add(lblStartWith);
//       pnlRename.add(cboStartWith);
       tab2.add(pnlRename2);
       
   }//end buildRenamePanel
   
   

   private void buildFolioOptPanel(){
       
       pnlFolioOpt = new JPanel();
       pnlFolioOpt.setLayout(new BoxLayout(pnlFolioOpt, BoxLayout.X_AXIS));
       pnlFolioOpt.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
       
       
       lblFolioOpt = new JLabel("Choose suffix style for recto and verso: ");
       txtPreRecto = new JTextField(4);
       txtPreRecto.setMaximumSize(stdDim);
       txtPreVerso = new JTextField(4);
       txtPreVerso.setMaximumSize(stdDim);
       
       String[] FolioOptRectoValue = {"r", "recto", "", "A"};
       cboFolioOptRecto = new JComboBox(FolioOptRectoValue);
       cboFolioOptRecto.setMaximumSize(new Dimension(80, 25));
       cboFolioOptRecto.setEnabled(true);

       String[] FolioOptVersoValue = {"v", "verso", "B"};
       cboFolioOptVerso = new JComboBox(FolioOptVersoValue);
       cboFolioOptVerso.setMaximumSize(new Dimension(80, 25));
       cboFolioOptVerso.setEnabled(true);
       
       
       
       
       
       pnlFolioOpt.add(lblFolioOpt);
       pnlFolioOpt.add(txtPreRecto);
       pnlFolioOpt.add(cboFolioOptRecto);
       pnlFolioOpt.add(txtPreVerso);
       pnlFolioOpt.add(cboFolioOptVerso);
       
       tab1.add(pnlFolioOpt);
       
   }//end buildRenamePanel

   private void buildFolioOptPanel2(){
       
       pnlFolioOpt2 = new JPanel();
       pnlFolioOpt2.setLayout(new BoxLayout(pnlFolioOpt2, BoxLayout.X_AXIS));
       pnlFolioOpt2.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
       
       
       lblFolioOpt2 = new JLabel("Choose suffix style for recto and verso: ");
       txtPreRecto2 = new JTextField(4);
       txtPreRecto2.setMaximumSize(stdDim);
       txtPreVerso2 = new JTextField(4);
       txtPreVerso2.setMaximumSize(stdDim);
       
       String[] FolioOptRectoValue2 = {"r", "recto", "", "A"};
       cboFolioOptRecto2 = new JComboBox(FolioOptRectoValue2);
       cboFolioOptRecto2.setMaximumSize(new Dimension(80, 25));
       cboFolioOptRecto2.setEnabled(true);

       String[] FolioOptVersoValue2 = {"v", "verso", "B"};
       cboFolioOptVerso2 = new JComboBox(FolioOptVersoValue2);
       cboFolioOptVerso2.setMaximumSize(new Dimension(80, 25));
       cboFolioOptVerso2.setEnabled(true);
       
       txtSeparator = new JTextField(4);
       lblSeparator = new JLabel("separator: ");
       txtSeparator.setText(" - ");
       txtSeparator.setMaximumSize(new Dimension(80, 25));
       
       pnlFolioOpt2.add(lblFolioOpt2);
       pnlFolioOpt2.add(txtPreRecto2);
       pnlFolioOpt2.add(cboFolioOptRecto2);
       pnlFolioOpt2.add(txtPreVerso2);
       pnlFolioOpt2.add(cboFolioOptVerso2);
       pnlFolioOpt2.add(lblSeparator);
       pnlFolioOpt2.add(txtSeparator);
       
       tab2.add(pnlFolioOpt2);
       
   }//end buildRenamePanel

   
   
  
   private void buildSuffixPanel(){
       
       pnlSuffix = new JPanel();
       pnlSuffix.setLayout(new BoxLayout(pnlSuffix, BoxLayout.X_AXIS));
       pnlSuffix.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
       
       // the Suffix -------------------------
       txtSuffix = new JTextField(40);
       txtSuffix.setMaximumSize(stdDim);
       txtSuffix.setEditable(false);
       cbxSuffix = new JCheckBox("Suffix");
              
       
               
       ActionListener cbxSuffixListener = new ActionListener(){
               public void actionPerformed(ActionEvent e){
                               if(cbxSuffix.isSelected())
                                       txtSuffix.setEditable(true);
                               else
                               {
                                       txtSuffix.setEditable(false);
                                       txtSuffix.setText("");
                               }       
               }
       };
       
       //check listener
       cbxSuffix.addActionListener(cbxSuffixListener);
       
       pnlSuffix.add(cbxSuffix);
       pnlSuffix.add(txtSuffix);
//       pnlSuffix.add(cbxIgnoreExtension);
       
       
       tab1.add(pnlSuffix);
   }//end buildSuffixPanel
   
   private void buildSuffixPanel2(){
       
       pnlSuffix2 = new JPanel();
       pnlSuffix2.setLayout(new BoxLayout(pnlSuffix2, BoxLayout.X_AXIS));
       pnlSuffix2.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
       
       // the Suffix -------------------------
       txtSuffix2 = new JTextField(40);
       txtSuffix2.setMaximumSize(stdDim);
       txtSuffix2.setEditable(false);
       cbxSuffix2 = new JCheckBox("Suffix");
       
               
       ActionListener cbxSuffixListener = new ActionListener(){
               public void actionPerformed(ActionEvent e){
                               if(cbxSuffix2.isSelected())
                                       txtSuffix2.setEditable(true);
                               else
                               {
                                       txtSuffix2.setEditable(false);
                                       txtSuffix2.setText("");
                               }       
               }
       };
       
       //check listener
       cbxSuffix2.addActionListener(cbxSuffixListener);
       
       pnlSuffix2.add(cbxSuffix2);
       pnlSuffix2.add(txtSuffix2);       
//       pnlSuffix2.add(cbxIgnoreExtension2);

       tab2.add(pnlSuffix2);
   }//end buildSuffixPanel
   
  
   //buildOptPanel
   private void buildOptPanel(){
       
       pnlOption = new JPanel();
       pnlOption.setLayout(new FlowLayout());
       pnlOption.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
       
       cbxExperiment = new JCheckBox("Test Mode");
       cbxOutput = new JCheckBox("Output Window");
       
       ActionListener cbxExperimentListener = new ActionListener(){
           public void actionPerformed(ActionEvent e){
                           if(cbxExperiment.isSelected())
                        	   cbxOutput.setSelected(true);
           }
   };
   
   //check listener
   cbxExperiment.addActionListener(cbxExperimentListener);
          
       pnlOption.add(cbxExperiment);
       pnlOption.add(cbxOutput);
       tab1.add(pnlOption);
       
   }//end buildOptPanel
   
   
 //buildOptPanel
   private void buildOptPanel2(){
       
       pnlOption2 = new JPanel();
       pnlOption2.setLayout(new FlowLayout());
       pnlOption2.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
       
       cbxExperiment2 = new JCheckBox("Test Mode");
       cbxOutput2 = new JCheckBox("Output Window");
       
       ActionListener cbxExperimentListener = new ActionListener(){
           public void actionPerformed(ActionEvent e){
                           if(cbxExperiment2.isSelected())
                        	   cbxOutput2.setSelected(true);
           }
   };
   
   //check listener
   cbxExperiment2.addActionListener(cbxExperimentListener);
          
       pnlOption2.add(cbxExperiment2);
       pnlOption2.add(cbxOutput2);
       tab2.add(pnlOption2);
       
   }//end buildOptPanel
      
   
   private void buildCtrlPanel(){
       
       pnlCtrl = new JPanel();
//       pnlCtrl.setLayout(new BoxLayout(pnlCtrl, BoxLayout.X_AXIS));
       pnlCtrl.setLayout(new FlowLayout());
       pnlCtrl.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
       
       //setup the ctrl panel ---------------------
       btnOk = new JButton("Ok");
       btnOk.setEnabled(false);
       btnOk.addActionListener(this);
       
       btnCancel = new JButton("Cancel");
       btnCancel.addActionListener(this);
       
       btnAbout = new JButton("About");
       btnAbout.addActionListener(this);
       
       pnlCtrl.add(btnOk);
       pnlCtrl.add(btnCancel);
       pnlCtrl.add(btnAbout);
       
       tab1.add(pnlCtrl);
       
   }//end buildCtrlPanel
   

   private void buildCtrlPanel2(){
       
       pnlCtrl2 = new JPanel();
       pnlCtrl2.setLayout(new FlowLayout());
       pnlCtrl2.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
       
       //setup the ctrl panel ---------------------
       btnOk2 = new JButton("Ok");
       btnOk2.setEnabled(false);
       btnOk2.addActionListener(this);
       
       btnCancel2 = new JButton("Cancel");
       btnCancel2.addActionListener(this);
       
       btnAbout2 = new JButton("About");
       btnAbout2.addActionListener(this);
       
       pnlCtrl2.add(btnOk2);
       pnlCtrl2.add(btnCancel2);
       pnlCtrl2.add(btnAbout2);
       
       tab2.add(pnlCtrl2);
       
   }//end buildCtrlPanel
   
   
   
   //the getFileExtension seems useful, so we left it open for public use.
   public String getFileExtension(String filename){
       
       int dotIndex = filename.lastIndexOf(".");
       if(dotIndex >= 0){
               String fileXT = filename.substring(dotIndex);
               //fileXT = fileXT.toLowerCase(); //make the extension to lower case, nvm just follow the original
               //System.out.println("The input string: "+ filename + " file extension: " + fileXT);
               return fileXT;
       }
       else
               return "";
   }
   
   
   //this will get the FileName without the extension
   public String getFileName(String filename){

       int dotIndex = filename.lastIndexOf(".");
       
       if(dotIndex >= 0){
               String fileName = filename.substring(0, dotIndex);      
               //System.out.println("The input string: "+ filename + " file name: " + fileName);
               return fileName;
       }
       else
               return "";
       
   }
   
   private String nameSequence(int number, String digits) {
               
       String leadingZeroSpecifier = "%0" + digits + "d";
       
       String generatedSequence = String.format(leadingZeroSpecifier, number);
       
       return generatedSequence;
       
   }     
       
   
   public void aboutMessage() {
	   
	   JOptionPane.showMessageDialog(null, "Folio Batch Renamer \n"
	   		+ "A tool to batch-rename files or folders according to the fashion used for manuscript material (folio, recto, verso) \n"
	   		+ "Licence: GNU GPL v3\n"
	   		+ "Project manager: Marjorie Burghart \n"
	   		+ "CIHAM UMR 5648 - 2015");   
   }
   
   
   public void actionPerformed(ActionEvent e){
       JButton clickedButton = (JButton) e.getSource();
              
       //when we click the ok button... then
       if(clickedButton == btnOk){
               
               String cboString = (String) cboSequence.getSelectedItem();
               
               /* we have to check if user have entered any value, is the chosen digit is NOT NONE.
                * the ComboBox selection is NOT "None"
                */
               if(cboString.equals("None") == false){
                       String temp = txtSequence.getText();
                       
                       // then it has to be have some value for the textfield
                       if(temp.equals("") || temp == null){
                               JOptionPane.showMessageDialog(null, "Please fill in the 'From folio' number field");
                               txtSequence.grabFocus();
                               //something to highlight the field in future?
                               return;
                       }
               }
               
            //   JOptionPane.showMessageDialog(null, "rename invoked");
                       renameFile();   
                               
       }
       else if(clickedButton == btnCancel){
               btnOk.setEnabled(false);
               txtDirectory.setText("");
               directory = null;
               cbxPrefix.setSelected(false);
               cbxRename.setSelected(false);
               cbxSuffix.setSelected(false);
               txtPrefix.setEditable(false);
               txtPrefix.setText("");
               txtSuffix.setEditable(false);
               txtSuffix.setText("");
               txtRename.setEditable(false);
               txtRename.setText("");
               
               cbxIgnoreExtension.setSelected(false);
//               txtSequence.setEditable(false);
               txtSequence.setText("");
               cboSequence.setSelectedIndex(0);
               
       }
       else if(clickedButton == btnAbout){
    	   aboutMessage();
       }
       
       
     //when we click the ok button in tab2... then
       if(clickedButton == btnOk2){
               
               String cboString = (String) cboSequence2.getSelectedItem();
               
               /* we have to check if user have entered any value, is the chosen digit is NOT NONE.
                * the ComboBox selection is NOT "None"
                */
               if(cboString.equals("None") == false){
                       String temp = txtSequence2.getText();
                       
                       // then it has to be have some value for the textfield
                       if(temp.equals("") || temp == null){
                               JOptionPane.showMessageDialog(null, "Please fill in the 'From folio' number field");
                               txtSequence2.grabFocus();
                               //something to highlight the field in future?
                               return;
                       }
               }
               
            //   JOptionPane.showMessageDialog(null, "rename invoked");
                       renameFileSingle();   
                               
       }
       else if(clickedButton == btnCancel2){
               btnOk2.setEnabled(false);
               txtDirectory2.setText("");
               directory = null;
               cbxPrefix2.setSelected(false);
               cbxRename2.setSelected(false);
               cbxSuffix2.setSelected(false);
               txtPrefix2.setEditable(false);
               txtPrefix2.setText("");
               txtSuffix2.setEditable(false);
               txtSuffix2.setText("");
               txtRename2.setEditable(false);
               txtRename2.setText("");
               
               cbxIgnoreExtension2.setSelected(false);
 //              txtSequence2.setEditable(false);
               txtSequence2.setText("");
               txtSeparator.setText(" - ");
               cboSequence2.setSelectedIndex(0);
               
       }
       else if(clickedButton == btnAbout2){
    	   aboutMessage();
       }
       
       
   }
   
   
   /* for nested class */
   //special nested class for TextField Limit
       class JTextFieldLimit extends PlainDocument{
               private int limit;
               private boolean toUpperCase = false;
               
               //constructor
               JTextFieldLimit(int limit){
                       super();
                       this.limit = limit;
               }
               
               JTextFieldLimit(int limit, boolean upperCase){
                       super();
                       this.limit = limit;
                       toUpperCase = upperCase;
               }
               
               public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException{
                       if(str == null)
                               return;
                       if((getLength() + str.length())<= limit){
                               if(toUpperCase) str = str.toUpperCase();
                               super.insertString(offset, str, attr);
                                       
                       }
               }
               
       }//end of nested class
}
