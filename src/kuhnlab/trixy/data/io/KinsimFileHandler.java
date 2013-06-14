/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kuhnlab.trixy.data.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import kuhnlab.coordinates.KPoint2D;
import kuhnlab.gui.GenericOptionsPanel;
import kuhnlab.trixy.data.Series;
import kuhnlab.trixy.data.SeriesList;

/**
 *
 * @author jrkuhn
 */
public class KinsimFileHandler extends AbstractSeriesFileHandler {
    
    protected static SeriesFileFilter KINSIM_FILETYPE = new SeriesFileFilter("KINSIM files (*.xy)", "xy");
    
    public KinsimFileHandler() {
        super(KINSIM_FILETYPE, " ");
    }
    
    protected Double[] ystart = {0.0};
    
    @Override
    public GenericOptionsPanel getOptionsPanel(boolean writeMode) {
        if (writeMode) {
            GenericOptionsPanel ops = new GenericOptionsPanel("Kinsim File", "Save As Kinsim File");
            ops.addDouble("Y value if no x=0", ystart, 0, 10);
            return ops;
        }
        return null;
    }

    @Override
    public SeriesList readFile(File file) {
        // reading not supported
        return null;
    }
    
    @Override
    public boolean writeFile(SeriesList series, File file) {
        // save individual traces in a directory with the name of "file"

        try {
            File path = SeriesFileFilter.removeExtension(file);
            path.mkdir();
            String pathname = path.getAbsolutePath() + File.separator;
            final String format = "%10g %10g";
            
            for (Series ser : series.getSeries()) {
                File serFile = KINSIM_FILETYPE.forceExtension(new File(pathname + ser.getName()));
                FileWriter writer = new FileWriter(serFile);
                BufferedWriter bw = new BufferedWriter(writer);
                boolean bFirst = true;
                double deltax = ser.getAverageDeltaX();
                double maxx = ser.getXRange()[1];
                int nPoints = 0;
                for (KPoint2D pt : ser.getPoints()) {
                    if (!pt.isNaN()) {
                        if (bFirst && pt.x > 0) {
                            bw.write(String.format(format, 0.0, ystart[0]));
                            bw.newLine();
                        }
                        bw.write(String.format(format, pt.x, pt.y));
                        bw.newLine();
                        bFirst = false;
                        nPoints++;
                    }
                }
                // KINSIM seems to have a bug where it only keeps 1/2 of the points.
                // To get around this, we write an equal number (+1) of empty points at
                // the end of the file.
                double x = maxx + deltax;
                for (int i=0; i<=nPoints; i++) {
                    bw.write(String.format(format, x, 0.0));
                    bw.newLine();
                    x += deltax;
                }
                bw.close();
                writer.close();
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }
}
