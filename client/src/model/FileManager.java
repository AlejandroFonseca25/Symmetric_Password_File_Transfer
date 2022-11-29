package model;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.IOException;
/**

 * This entity is mainly used for displaying a file chooser dialog and select the desired file to transfer.

 */
public class FileManager{

    /**
	* Opens Java file explorer for selection of the to-be-encrypted file
	* <p>
	* After file selection, the method retrieves its' path and file name,
    * and returns it in a String array for later use
	* @return   A String array containing the path and name of the selected file
	*/
	public String[] chooseFile() throws IOException{
        String [] array = new String[2];

        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & GIF Images", "jpg", "gif","txt");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            array[0] = chooser.getSelectedFile().getPath();
            array[1] = chooser.getSelectedFile().getName();
            return array;
        }
        return null;
	}
    
}