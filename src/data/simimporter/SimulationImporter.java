package data.simimporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import data.DBImporter;

public class SimulationImporter extends DBImporter {

    @Override
    public void importData() throws Exception
    {
        NodeList fileList = importerElem.getElementsByTagName("file");
        for (int fileCount = 0; fileCount < fileList.getLength(); fileCount++)
        {
            Element fileElem = (Element)fileList.item(fileCount);
            long sourceID = environDB.addSimulation(
                    importerElem.getAttribute("name"),
                    importerElem.getAttribute("model"), 
                    fileElem.getAttribute("path"), 
                    fileElem.getAttribute("description"),
                    Boolean.valueOf(fileElem.getAttribute("overwrite")));         
            GregorianCalendar timeDatum = getCalendar(fileElem.getAttribute("timedatum"));
            long totalRecordCount = 1;
            NodeList colList = fileElem.getElementsByTagName("column");
            for (int colCount = 0; colCount < colList.getLength(); colCount++)
            {
                Element colElem = (Element)colList.item(colCount);
                
                GregorianCalendar timeStart = null;
                if (colElem.hasAttribute("timeStart"))
                {
                    timeStart = getCalendar(colElem.getAttribute("timeStart"));
                }
                
                GregorianCalendar timeStop = null;
                if (colElem.hasAttribute("timeStop"))
                {
                     timeStop = getCalendar(colElem.getAttribute("timeStop"));
                }
                
                GregorianCalendar time = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
                
                int colNum = Integer.valueOf(colElem.getAttribute("number"));
                long propertyID = environDB.getPropertyID(colElem.getAttribute("property"));
                long dimID = environDB.getDimensionID("Point");
                long siteID = environDB.getSiteID(colElem.getAttribute("site"));
                long unitID = environDB.getUnitID(colElem.getAttribute("units"));
                
                BufferedReader reader = new BufferedReader(new FileReader(new File(
                        fileElem.getAttribute("path"))));
                reader.readLine();
                String[] line = null;
                if (timeStart != null)
                {
                    while (reader.ready())
                    {
                        line = reader.readLine().split("\t");
                        time.setTimeInMillis(timeDatum.getTimeInMillis() + (int)(Double.valueOf(line[1]) * 1000));
                        if (time.equals(timeStart) || time.after(timeStart))
                        {
                            break;
                        }
                    }
                }
                else
                {
                    line = reader.readLine().split("\t");
                }
                boolean initial = true;
                while (reader.ready())
                {
                    if (!initial)
                    {
                        line = reader.readLine().split("\t");
                    }
                    else
                    {
                        initial = false;
                    }
                    
                    time.setTimeInMillis(timeDatum.getTimeInMillis() + (long)(Double.valueOf(line[1]) * 1000));
                    if (timeStop != null)
                    {
                        if (time.equals(timeStop) || time.after(timeStop))
                        {
                            break;
                        }
                    }
                    
                    environDB.addValueDatum(
                            sourceID, 
                            totalRecordCount, 
                            propertyID, 
                            dimID, 
                            siteID, 
                            getDBTime(time), 
                            Double.valueOf(line[colNum]), 
                            unitID);
                    totalRecordCount++;
                }
            }
        }
    }

    private String getDBTime(GregorianCalendar time)
    {
        return String.format(
                "#%04d-%02d-%02d %02d:%02d:%02d#", 
                time.get(GregorianCalendar.YEAR),
                time.get(GregorianCalendar.MONTH) + 1,
                time.get(GregorianCalendar.DAY_OF_MONTH),
                time.get(GregorianCalendar.HOUR_OF_DAY),
                time.get(GregorianCalendar.MINUTE),
                time.get(GregorianCalendar.SECOND));
    }

    private GregorianCalendar getCalendar(String dateTimeString)
    {
        String[] datumString = dateTimeString.split(" ");
        String[] dateString = datumString[0].split("-");
        String[] timeString = datumString[1].split(":");
        GregorianCalendar calendar = new GregorianCalendar(
                Integer.valueOf(dateString[0]),
                Integer.valueOf(dateString[1]) - 1,
                Integer.valueOf(dateString[2]),
                Integer.valueOf(timeString[0]),
                Integer.valueOf(timeString[1]),
                Integer.valueOf(timeString[2]));
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        return calendar;
    }

}
