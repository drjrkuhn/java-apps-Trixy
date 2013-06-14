/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kuhnlab.trixy.data.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import kuhnlab.gui.GenericOptionsPanel;
import kuhnlab.trixy.data.SeriesList;

/**
 *
 * @author jrkuhn
 */
public class TabbedFileHandler extends AbstractSeriesFileHandler {
    
    protected static SeriesFileFilter TAB_FILETYPE = new SeriesFileFilter("Tab separated values (*.txt)", "txt");
    
    protected Boolean[] prefsSingleDomain = {false};
    protected Boolean[] prefsAddQuotes = {true};
    
    public TabbedFileHandler() {
        super(TAB_FILETYPE, "\t");
    }
    
    @Override
    public GenericOptionsPanel getOptionsPanel(boolean writeMode) {
        if (writeMode) {
            GenericOptionsPanel ops = new GenericOptionsPanel("Tabbed File", "Save As Tabbed File");
            ops.addBoolean("Single X column:", prefsSingleDomain, 0, true);
            ops.addSeparator("Column format");
            ops.addBoolean("Add quotes (\"\"):", prefsAddQuotes, 0, true);
            return ops;
        } else {
            GenericOptionsPanel ops = new GenericOptionsPanel("Tabbed File", "Read Tabbed File");
            ops.addBoolean("Single X column:", prefsSingleDomain, 0, true);
            return ops;
        }
    }

    @Override
    public boolean writeFile(SeriesList series, File file) {
        try {
            FileWriter fw = new FileWriter(file);
            return super.seriesListToWriter(series, fw, separator, false, prefsAddQuotes[0], prefsSingleDomain[0]);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }


    @Override
    public SeriesList readFile(File file) {
        try {
            FileReader fr = new FileReader(file);
            return seriesListFromReader(fr, separator, false, prefsSingleDomain[0]);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    
    /** Used by clipboard */
    public boolean seriesListToWriter(SeriesList data, Writer writer) {
        return super.seriesListToWriter(data, writer, separator, false, false, false);
    }
    
    /** Used by clipboard */
    public SeriesList seriesListFromReader(Reader reader) {
        return super.seriesListFromReader(reader, separator, false, false);
    }
    
}
