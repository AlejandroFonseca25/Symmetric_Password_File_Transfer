package model;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.IOException;

public class FileManager{
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