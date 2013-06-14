/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kuhnlab.trixy.data.io;

import kuhnlab.gui.GenericOptionsPanel;

/**
 *
 * @author jrkuhn
 */
public class CommaFileHandler extends AbstractSeriesFileHandler {
    
    public static SeriesFileFilter CSV_FILETYPE = new SeriesFileFilter("Comma-separated values File (*.csv)", "csv");
    
    public CommaFileHandler() {
        super(CSV_FILETYPE, ",");
    }
    
    @Override
    public GenericOptionsPanel getOptionsPanel(boolean writeMode) {
        return null;
    }
}
