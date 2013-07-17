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
import java.io.IOException;
import java.io.Reader;
import kuhnlab.coordinates.KPoint2D;
import kuhnlab.gui.GenericOptionsPanel;

/**
 *
 * @author jrkuhn
 */
public class SoftmaxFileHandler extends AbstractSeriesFileHandler {

    public static SeriesFileFilter SOFTMAX_FILETYPE = new SeriesFileFilter("SoftMax pro file (*.txt)", "txt");
    protected static final String SIGNATURE = "##BLOCKS=";
    protected static final String START_NOTE = "Note:";
    protected static final String START_GROUP = "Group:";
    protected static final String START_PLATE = "Plate:";
    protected static final String END_ALL = "~End";
    protected static final String TIMEFORMAT = "TimeFormat";
    protected static final String TIME_HEADING = "Time(hh:mm:ss)";
    protected static final String WAVELENGTH_HEADING = "Wavelength(nm)";
    protected static final String SAMPLE_HEADING = "Sample";
    protected static final String WELLS_HEADING = "Wells";

    public SoftmaxFileHandler() {
        super(SOFTMAX_FILETYPE, "\t");
    }

    @Override
    public SeriesList readFile(File file) {
        try {
            FileReader fr = new FileReader(file);
            return seriesListFromSoftMaxReader(fr);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public SeriesList seriesListFromSoftMaxReader(Reader reader) {

        try {
            BufferedReader br = new BufferedReader(reader);
            String line = br.readLine();
            String[] split;
            if (!line.startsWith(SIGNATURE)) {
                return null;
            }
            String strTemp = line.substring(SIGNATURE.length()).trim();
            int numBlocks = Integer.parseInt(strTemp);
            SeriesList allData = new SeriesList();
            SeriesList curData = null;
            String curPlateName = "";
            line = br.readLine();
            while (line != null) {
                if (line.startsWith(START_NOTE)) {
                    skipToEndOfSection(br, END_ALL);
                    line = END_ALL;
                    continue;
                } else if (line.startsWith(START_PLATE)) {
                    split = line.split(separator);
                    if (split.length < 4 || !split[3].startsWith(TIMEFORMAT)) {
                        skipToEndOfSection(br, END_ALL);
                        line = END_ALL;
                        continue;
                    }
                    curPlateName = split[1].trim();
                    // read column headings
                    line = br.readLine();
                    split = line.split(separator);

                    if (split.length < 2 || !(split[0].startsWith(TIME_HEADING) || split[0].startsWith(WAVELENGTH_HEADING))) {
                        skipToEndOfSection(br, END_ALL);
                        line = END_ALL;
                        continue;
                    }
                    if (curData != null) {
                        allData.addAllSeries(curData.getSeries());
                    }
                    curData = new SeriesList();
                    int nCurves = split.length - 1;
                    for (int i = 1; i < nCurves; i++) {
                        String name = split[i];
                        Series newser = new Series(name);
                        curData.addSeries(newser);
                    }
                    // Read the data
                    line = br.readLine();
                    boolean bSkipData = false;
                    do {
                        if (line == null) {
                            break;
                        }
                        split = line.split(separator);
                        if (split[0].startsWith(TIME_HEADING)) {
                            // A second row of headings designates summary 
                            // values, which we will ignore. 
                            // We can skip to the end of the plate data.
                            bSkipData = true;
                        }
                        if (!bSkipData) {
                            if (split.length > 1) {
                                int[] hms = {0, 0, 0};
                                String strTime = split[0];
                                // account for times of "01" or "1:02" or "1:01:03"
                                String[] strHMS = strTime.split(":");
                                for (int i = strHMS.length - 1, j = 2; i >= 0; i--, j--) {
                                    strTemp = strHMS[i].trim();
                                    // remove any leading zeros
                                    while (strTemp.length() > 0) {
                                        if (strTemp.charAt(0) == '0') {
                                            strTemp = strTemp.substring(1);
                                        } else {
                                            break;
                                        }
                                    }
                                    if (!strTemp.equals("")) {
                                        hms[j] = Integer.parseInt(strTemp);
                                    }
                                }
                                double time = 3600 * hms[0] + 60 * hms[1] + hms[2];
                                for (int curve = 0; curve < nCurves; curve++) {
                                    if (curve + 1 < split.length) {
                                        String sy = split[curve + 1].trim();
                                        if (!sy.equals("")) {
                                            try {
                                                curData.getSeries(curve).add(new KPoint2D(time, Double.parseDouble(sy)));
                                            } catch (NumberFormatException e) {
                                                // ignore the problem
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        line = br.readLine();
                    } while (!line.startsWith(END_ALL));
                } else if (line.startsWith(START_GROUP)) {
                    if (curData == null) {
                        skipToEndOfSection(br, END_ALL);
                        line = END_ALL;
                        continue;
                    }
                    line = br.readLine();
                    if (line != null && line.startsWith(END_ALL)) {
                        continue;
                    }
                    // determine which group columns contains well labels
                    // and which contains sample names
                    split = line.split(separator);
                    int colSample = -1;
                    int colWells = -1;
                    for (int i = 0; i < split.length; i++) {
                        if (split[i].equals(SAMPLE_HEADING)) {
                            colSample = i;
                        } else if (split[i].equals(WELLS_HEADING)) {
                            colWells = i;
                        }
                    }
                    if (colSample < 0 || colWells < 0) {
                        skipToEndOfSection(br, END_ALL);
                        line = END_ALL;
                        continue;
                    }
                    // convert Well names (such as B3 or H12) to Sample names.
                    int colMax = Math.max(colSample, colWells);
                    String lastSampleName = "";
                    String sampleName, wellName;
                    do {
                        line = br.readLine();
                        if (line == null) {
                            break;
                        }
                        split = line.split(separator);
                        if (colMax >= split.length) {
                            continue;
                        }
                        wellName = split[colWells];
                        sampleName = split[colSample];
                        if (sampleName.isEmpty()) {
                            sampleName = lastSampleName;
                        }
                        lastSampleName = sampleName;
                        for (Series s : curData.getSeries()) {
                            if (s.getName().equals(wellName)) {
                                s.setName(sampleName + "@" + curPlateName);
                                continue;
                            }
                        }
                    } while (!line.startsWith(END_ALL));
                }
                line = br.readLine();
            }
            if (curData != null) {
                allData.addAllSeries(curData.getSeries());
            }
            // Get rid of empty series
            SeriesList goodData = new SeriesList();
            for (Series ser : allData.getSeries()) {
                ser.removeNaN();
                if (!ser.isEmpty()) {
                    goodData.addSeries(ser);
                }
            }
            return goodData;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void skipToEndOfSection(BufferedReader br, String lastLineTag) throws IOException {
        String line = null;
        do {
            line = br.readLine();
            if (line == null) {
                return;
            }
        } while (!line.startsWith(lastLineTag));
    }

    @Override
    public boolean writeFile(SeriesList series, File file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isFileSignature(File file) {
        try {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            String line;

            boolean goodSignature = true;

            // first line should start with "##BLOCKS="
            line = br.readLine();
            if (!line.startsWith(SIGNATURE)) {
                goodSignature = false;
            }
            br.close();
            reader.close();
            return goodSignature;
        } catch (Exception ex) {
        }

        return false;
    }

    @Override
    public GenericOptionsPanel getOptionsPanel(boolean writeMode) {
        return null;
    }
}
