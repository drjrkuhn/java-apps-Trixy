/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kuhnlab.trixy.data.io;

import kuhnlab.trixy.data.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import kuhnlab.gui.GenericOptionsPanel;

/**
 *
 * @author jrkuhn
 */
public class SerializedFileHandler extends AbstractSeriesFileHandler {
    
    public static SeriesFileFilter SERIAL_FILETYPE = new SeriesFileFilter("Java serialization (*.jsr)", "jsr");
    
    public SerializedFileHandler() {
        super(SERIAL_FILETYPE, "");
    }

    @Override
    public SeriesList readFile(File file) {
        SeriesList data;
        try {
            FileInputStream filestream = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(filestream);
            data = (SeriesList) ois.readObject();
            ois.close();
            filestream.close();
            return data;
        } catch (Exception ex) {
            return null;
        }
    }
    
    @Override
    public boolean writeFile(SeriesList data, File file) {
        try {
            FileOutputStream filestream = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(filestream);
            oos.writeObject(data);
            oos.close();
            filestream.close();
            return true;
        } catch (Exception ex) {
        }
        return false;
    }

    @Override
    public boolean isFileSignature(File file) {
        try {
            FileInputStream filestream = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(filestream);
            ois.close();
            filestream.close();
            return true;
        } catch (Exception ex) {
        }
        return false;
    }
    
    @Override
    public GenericOptionsPanel getOptionsPanel(boolean writeMode) {
        return null;
    }
}
