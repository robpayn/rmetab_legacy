package data.doprobe;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.Map.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import data.*;

/**
 * Imports metabolism data from a DO probe excel file
 * 
 * @author robpayn
 *
 */
public class DOProbeDBImporter extends DBImporter {
    
    /**
     * Map of data property names to heading names in DO files
     */
    public static final HashMap<String, String> DO_PROBE_HEADING_MAP = 
        new HashMap<String, String>();
    static
    {
        DO_PROBE_HEADING_MAP.put("Temperature", "Temp");    
        DO_PROBE_HEADING_MAP.put("ConductivityElecTemp", "SpCond");
        DO_PROBE_HEADING_MAP.put("ConcDissolvedOxygen", "ODO");
    }

    /**
     * Epoch used for date/time on sheet
     */
    public static final Date EPOCH = new GregorianCalendar(1899, 11, 31).getTime();
    /**
     * Column number for the probe ID in the meta sheet
     */
    public static final int META_COL_PROBE_ID = 0;
    
    /**
     * Column number for the length in the meta sheet
     */
    private static final int META_COL_LENGTH = 2;

    /**
     * Column number for the location in the meta sheet
     */
    public static final int META_COL_LOCATION = 3;

    /**
     * Default basekey for files
     */
    public static final String DEFAULT_FILE_BASEKEY = "File.Default";

    /**
     * Index for deployment in the property array list
     */
    public static final int INDEX_DEPLOYMENT = 0;

    /**
     * Index for dimension in the property array list
     */
    public static final int INDEX_UNIT = 1;
    
    /**
     * Counter for processed sheets
     */
    private int sheetCounter;
    
    /**
     * File writer for log
     */
    private BufferedWriter logWriter;

    /**
     * Import DO probe data to the database
     */
    @Override
    public void importData() throws Exception
    {
        String baseDir = "";
        String propBaseDir = prop("BaseDir");
        if (propBaseDir != null)
        {
            baseDir = File.separator + propBaseDir.replace("/", File.separator);
        }
        
        String fileSuffix = ".xlsx";
        String propFileSuffix = prop("Suffix");
        if (propFileSuffix != null)
        {
            fileSuffix = "_" + propFileSuffix + fileSuffix;
        }
        
        int fileCount = 1;
        while (processFile(fileCount, baseDir, fileSuffix))
        {
            fileCount++;
        }
    }


    /**
     * Process a DO probe spreadsheet file
     * 
     * @param fileNumber the index of the file to process
     * @param baseDir base directory where files are located
     * @param fileSuffix suffix for the file name
     * @return [true if processed | false otherwise]
     * @throws Exception if error in processing file
     */
    private boolean processFile(int fileNumber, String baseDir, String fileSuffix) 
    throws Exception
    {
        // open the data workbook and metatdata worksheet
        String fileName = prop("File." + Integer.toString(fileNumber));
        String fileBaseKey = "File." + fileName;
        
        if (fileName == null)
        {
            return false;
        }
        String pathName = workingDir.getAbsolutePath() + 
            baseDir + File.separator + 
            fileName + fileSuffix;
        XSSFWorkbook wb = new XSSFWorkbook(pathName);
        Sheet metaSheet = wb.getSheet("meta");
        logWriter = new BufferedWriter(new FileWriter(new File(pathName + ".log")));
        
        // process all sheets in the workbook
        sheetCounter = 1;
        while (processSheet(fileBaseKey, fileName, fileName + fileSuffix, 
                metaSheet.getRow(sheetCounter + 1)))
        {
            sheetCounter++;
        }
        System.out.println("Finished " + fileName + fileSuffix);
        logWriter.close();
        return true;
    }

    /**
     * Process a work sheet in the DO probe spreadsheet file
     * 
     * @param fileBaseKey base key for properties associated with this file
     * @param deploymentNameBase base of the deployment name (probe id will be added)
     * @param fileName name of file for the deployment record
     * @param metaRow row with the meta information about the work sheet
     * @return [true if sheet is processed | false otherwise]
     * @throws IOException if error in writing to log
     */
    private boolean processSheet(String fileBaseKey, String deploymentNameBase, String fileName, Row metaRow) 
    throws IOException
    {
        // check for row validity
        if (metaRow == null)
        {
            return false;
        }
        String probeSuffix = metaRow.getCell(META_COL_PROBE_ID).getStringCellValue();
        if (probeSuffix == null || probeSuffix.matches(""))
        {
            return false;
        }
        
        DOProbeSheet probeSheet = new DOProbeSheet(
                metaRow.getSheet().getWorkbook().getSheet(probeSuffix));
        try
        {
            String[] importProperties = 
                prop(fileBaseKey, DEFAULT_FILE_BASEKEY, "ImportProperties").split(",");
            String[] importPropertyUnits = 
                prop(fileBaseKey, DEFAULT_FILE_BASEKEY, "ImportPropertyUnits").split(",");
            Iterator<String> propIterator = Arrays.asList(importProperties).iterator();
            Iterator<String> propUnitIterator = Arrays.asList(importPropertyUnits).iterator();
            String deploymentName = propIterator.next();
            HashMap<String, ArrayList<Long>> dataIDMap = new HashMap<String, ArrayList<Long>>();
            ArrayList<Long> ids = new ArrayList<Long>();
            ids.add(INDEX_DEPLOYMENT, environDB.getPropertyID(deploymentName));
            ids.add(INDEX_UNIT, environDB.getUnitID(propUnitIterator.next()));
            dataIDMap.put(
                    DO_PROBE_HEADING_MAP.get(deploymentName), 
                    ids);
            while (propIterator.hasNext())
            {
                String propertyName = propIterator.next();
                deploymentName += "_" + propertyName;
                ids = new ArrayList<Long>();
                ids.add(INDEX_DEPLOYMENT, environDB.getPropertyID(propertyName));
                ids.add(INDEX_UNIT, environDB.getUnitID(propUnitIterator.next()));
                dataIDMap.put(
                        DO_PROBE_HEADING_MAP.get(propertyName), 
                        ids);
            }
            
            deploymentName = deploymentNameBase + "_" + deploymentName;
            String siteName = metaRow.getCell(META_COL_LOCATION).getStringCellValue();
            
            Long siteID = environDB.getSiteID(siteName);
            if (siteID == null)
            {
                String locationSourceName = deploymentNameBase + "_DOprobe_locations";
                Long extractionID = environDB.getSourceID(locationSourceName);
                if (extractionID == null)
                {
                    extractionID = environDB.addExtraction(locationSourceName, fileName, false);
                }
                
                siteID = environDB.addSite(siteName);
                environDB.addValueDatum(
                        extractionID, 
                        sheetCounter, 
                        environDB.getPropertyID("Location"), 
                        environDB.getDimensionID("RiverLengthLF"),
                        siteID, 
                        (Double)null, 
                        metaRow.getCell(META_COL_LENGTH).getNumericCellValue(), 
                        environDB.getUnitID("km"));
            }
            
            long sourceID = environDB.addDeployment(
                    deploymentName,
                    "DOProbe_" + probeSuffix,
                    probeSheet.getStartTime(),
                    probeSheet.getStopTime(),
                    fileName,
                    Boolean.valueOf(prop("Overwrite")));

            long dimID = environDB.getDimensionID("Point");
            int counter = 1;
            for (Row row: probeSheet)
            {
                HashSet<String> tags = new HashSet<String>();
                Integer tagCol = probeSheet.getColNumber("Tags");
                if (tagCol != null)
                {
                    Cell tagCell = row.getCell(tagCol);
                    if (tagCell != null)
                    {
                        String tagList = tagCell.getStringCellValue();
                        if (tagList != null & !tagList.matches(""))
                        {
                            tags.addAll(Arrays.asList(
                                    tagList.split(",")));
                        }
                    }
                }

                double time = 
                    row.getCell(probeSheet.getColNumber("Date")).getNumericCellValue() +
                    row.getCell(probeSheet.getColNumber("Time")).getNumericCellValue();
                for (Entry<String, ArrayList<Long>> property: dataIDMap.entrySet())
                {
                    if (property.getKey().matches("ODO") && tags.contains("DOFail"))
                    {
                        Date dateCell = row.getCell(probeSheet.getColNumber("Date")).getDateCellValue();
                        Date timeCell = row.getCell(probeSheet.getColNumber("Time")).getDateCellValue();
                        dateCell.setTime(dateCell.getTime() + timeCell.getTime() - EPOCH.getTime());
                        logWriter.write("DO datum ignored due to instrument failure:" +
                                probeSheet.getSheetName() + ", " + 
                                dateCell.toString());
                        logWriter.newLine();
                    }
                    else
                    {
                        environDB.addValueDatum(
                                sourceID, 
                                counter, 
                                property.getValue().get(INDEX_DEPLOYMENT), 
                                dimID, 
                                siteID, 
                                time, 
                                row.getCell(probeSheet.getColNumber(property.getKey())).getNumericCellValue(), 
                                property.getValue().get(INDEX_UNIT));
                        counter++;
                    }
                }
            }
            //DEBUG
            System.out.println(siteID + ", " + sourceID);
            
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return true;
    }


}
