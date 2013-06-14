/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kuhnlab.trixy.data.io;

import kuhnlab.trixy.data.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;
import kuhnlab.gui.GenericOptionsPanel;

/**
 *
 * @author jrkuhn
 */
public class PtiFileHandler extends AbstractSeriesFileHandler {
    
    public static SeriesFileFilter PTI_FILETYPE = new SeriesFileFilter("PTI Text File (*.txt)", "txt");
    
    public PtiFileHandler() {
        super(PTI_FILETYPE, "\t");
    }

    @Override
    public SeriesList readFile(File file) {
        try {
            FileReader fr = new FileReader(file);
            return seriesListFromReader(fr, "\t", true, false);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean writeFile(SeriesList series, File file) {
        try {
            FileWriter fw = new FileWriter(file);
            return seriesListToWriter(series, fw, "\t", true, true, false);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    
    @Override
    public boolean isFileSignature(File file) {
        try {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            String line;
            String[] split = {""};
            StringTokenizer tok;

            boolean goodSignature = true;
            
            // first line should be the number of traces
            line = br.readLine();
            int nTraces = Integer.parseInt(line);
            if (nTraces < 2) {
                goodSignature = false;
            }
            
            if (goodSignature) {
                // second line should be:
                //  #POINTS\t\t#Points\t\t#POINTS...
                // repeated for each trace
                line = br.readLine();
                split = line.split(separator);
                if (split.length < 2)
                    goodSignature = false;
            }
            if (goodSignature) {
                for (int i=0; i<nTraces; i++) {
                    int nPoints = Integer.parseInt(split[2*i]);
                    if (nPoints < 1) {
                        goodSignature = false;
                        break;
                    }
                    if ((i < nTraces-1) && !("".equals(split[2*i+1]))) {
                        goodSignature = false;
                        break;
                    }
                }
            }
            br.close();
            reader.close();
            return goodSignature;
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } catch (NumberFormatException ex) {
        } catch (IndexOutOfBoundsException ex) {
        }
        
        return false;
    }
    
    @Override
    public GenericOptionsPanel getOptionsPanel(boolean writeMode) {
        return null;
    }
}
