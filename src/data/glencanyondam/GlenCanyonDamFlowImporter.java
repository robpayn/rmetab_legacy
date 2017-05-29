package data.glencanyondam;

import java.io.*;

import data.DBImporter;

public class GlenCanyonDamFlowImporter extends DBImporter {

    @Override
    public void importData() throws Exception
    {
        String baseDir = "";
        String propBaseDir = prop("BaseDir");
        if (propBaseDir != null)
        {
            baseDir = File.separator + propBaseDir.replace("/", File.separator);
        }
        
        String fileSuffix = ".txt";
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

    private boolean processFile(int fileNumber, String baseDir, String fileSuffix)
    throws Exception
    {
        String fileName = prop("File." + Integer.toString(fileNumber));
        String fileBaseKey = "File." + fileName;
        
        if (fileName == null)
        {
            return false;
        }
        String pathName = workingDir.getAbsolutePath() + 
            baseDir + File.separator + 
            fileName + fileSuffix;
        GlenCanyonDamFlowFile file = new GlenCanyonDamFlowFile(new File(pathName));
        file.setLoggingEnabled(true);
        
        long sourceID = environDB.addExtraction(
                fileName + fileSuffix + "_dam_data_extraction", 
                fileName + fileSuffix, 
                Boolean.valueOf(prop("Overwrite")));
        
        long propertyID = environDB.getPropertyID("DamRelease");
        long dimID = environDB.getDimensionID("Point");
        long siteID = environDB.getSiteID("RKM_-25.4_GCD");
        long unitID = environDB.getUnitID("m3/sec");
        
        String lastGoodTime = "";
        for (GlenCanyonDamFlowRecord record: file)
        {
            try
            {
                String time = record.timeAsDBString();
                environDB.addValueDatum(
                        sourceID, 
                        record.getNumber(), 
                        propertyID, 
                        dimID, 
                        siteID, 
                        time, 
                        record.getCMS(), 
                        unitID);
                lastGoodTime = time;
            }
            catch (Exception e)
            {
                System.out.println("Last good time = " + lastGoodTime);
            }
        }
        file.close();
        
        return true;
    }

}
