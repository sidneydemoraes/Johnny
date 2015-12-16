package com.smc.johnny.main.robot.infrastructure;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * <p>This class manipulates the clipboard content in order to allow copy and
 * paste procedures </p>
 * @author smcoelho
 */
public final class TextTransfer implements ClipboardOwner {
	
	private static Logger log = Logger.getRootLogger();

   public void lostOwnership( Clipboard aClipboard, Transferable aContents) {
     //do nothing
   }

   /**
    * Place a String on the clipboard, and make this class the
    * owner of the Clipboard's contents.
    */
   public void setClipboardContents( String aString ){
	
	   StringSelection stringSelection = new StringSelection( aString );
	   
	   Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	   
	   clipboard.setContents( stringSelection, this );
   }

   /**
    * Get the String residing on the clipboard.
    *
    * @return any text found on the Clipboard; if none found, return an
    * empty String.
    */
   public String getClipboardContents() {

	   String result = "";

	   Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

	   Transferable contents = clipboard.getContents(null);

	   boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);

	   if(hasTransferableText){
		   try {
			   result = (String)contents.getTransferData(DataFlavor.stringFlavor);
		   }
		   catch (UnsupportedFlavorException ex){
				log.error("Error copying data from clipboard", ex);
				System.exit(0);
		   }
		   catch (IOException ex) {
				log.error("Error copying data from clipboard", ex);
				System.exit(0);
		   }
	   }
	   return result;
   }
}