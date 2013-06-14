/*
 * SeriesFileFilter.java
 *
 * Created on March 8, 2006, 12:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kuhnlab.trixy.data.io;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author drjrkuhn
 */
public class SeriesFileFilter extends FileFilter {
    public String description;
    public String extension;
    
    public SeriesFileFilter(String description, String extension) {
        this.description = description;
        this.extension = extension.toLowerCase();
    }
    
    //Accept all directories and all txt or csv files.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String ext = getExtension(f);
        if (ext != null) {
            if (extension.equals(ext.toLowerCase())) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    //The description of this filter
    public String getDescription() {
        return description;
    }
    
    public String getExtension() {
        return extension;
    }
    
    public boolean isFiletype(File f) {
        return extension.equals(getExtension(f));
    }
    
    //=====================================================
    //  UTILITY FUNCTIONS
    //=====================================================
    public File forceExtension(File file) {
        if (file == null) return null;
        String path = file.getPath();
        int iSep = path.lastIndexOf(File.separatorChar);
        int i = path.lastIndexOf('.');
        if (i > iSep && i > 0) {
            path = path.substring(0,i);
        }
        path += '.';
        path += extension;
        return new File(path);
    }
    
    public static File removeExtension(File file) {
        if (file == null) return null;
        String path = file.getPath();
        int iSep = path.lastIndexOf(File.separatorChar);
        int i = path.lastIndexOf('.');
        if (i > iSep && i > 0) {
            path = path.substring(0,i);
        }
        return new File(path);
    }
    
    public static String getExtension(File f) {
        String ext = "";
        String path = f.getName();
        int iSep = path.lastIndexOf(File.separatorChar);
        int i = path.lastIndexOf('.');
        if (i > iSep && i > 0 &&  i < path.length() - 1) {
            ext = path.substring(i+1).toLowerCase();
        }
        return ext;
    }    
}
