/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kuhnlab.trixy.data.io;
import kuhnlab.trixy.data.*;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import kuhnlab.gui.GenericOptionsPanel;

/**
 *
 * @author jrkuhn
 */
public interface SeriesFileHandler {
    public GenericOptionsPanel getOptionsPanel(boolean writeMode);
    
    public SeriesList readFile(File file);
    
    public boolean writeFile(SeriesList series, File file);
    
    public boolean isFileExtension(File file);
    
    public File forceFileExtension(File file);
    
    public boolean isFileSignature(File file);
    
    public FileFilter getFilter();
}
