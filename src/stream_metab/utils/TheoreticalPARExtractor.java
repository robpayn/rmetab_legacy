package stream_metab.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

import org.neosimulation.apps.modelmanager.neov1.tools.TimeSeries;
import org.w3c.dom.Element;

public class TheoreticalPARExtractor extends SiteExtractor {

    public TheoreticalPARExtractor(String paramID, Element element, String timeDatum,
            String startTime, String stopTime, String filePath)
    {
        super(paramID, element, timeDatum, startTime, stopTime, filePath);
    }
    
    @Override
    public TimeSeries getTimeSeries(String name) throws Exception
    {
        String[] dateTimeSplit = startTime.split(" ");
        String[] dateSplit = dateTimeSplit[0].split("-"); 
        String offsetStartTime = null;
        if (dateSplit[1].equals("12") && dateSplit[2].equals("31"))
        {
            offsetStartTime = startTime.replace(dateSplit[0], "2008");
        }
        else
        {
            offsetStartTime = startTime.replace(dateSplit[0], "2009");
        }
        
        dateTimeSplit = timeDatum.split(" ");
        dateSplit = dateTimeSplit[0].split("-"); 
        String offsetTimeDatum = null;
        if (dateSplit[1].equals("12") && dateSplit[2].equals("31"))
        {
            offsetTimeDatum = timeDatum.replace(dateSplit[0], "2008");
        }
        else
        {
            offsetTimeDatum = timeDatum.replace(dateSplit[0], "2009");
        }
        
        dateTimeSplit = stopTime.split(" ");
        dateSplit = dateTimeSplit[0].split("-"); 
        String offsetStopTime = null;
        if (dateSplit[1].equals("01") && dateSplit[2].equals("01"))
        {
            offsetStopTime = stopTime.replace(dateSplit[0], "2010");
        }
        else
        {
            offsetStopTime = stopTime.replace(dateSplit[0], "2009");
        }
        
        Connection sourceConn = DriverManager.getConnection(
                MSACCESS_ODBC_PREFIX + filePath + MSACCESS_ODBC_SUFFIX);
        Statement statement = sourceConn.createStatement();
        String sql = String.format(
                "SELECT [core_data].[Time], " +
                    "[dim_type_values].[Value] AS [State] " +
                "FROM (((([core_data] " +
                    "INNER JOIN [core_properties] " +
                    "ON [core_data].[PropertyID] = [core_properties].[ID]) " +
                    "INNER JOIN [dim_type_values] " +
                    "ON [dim_type_values].[DatumID] = [core_data].[ID]) " +
                    "INNER JOIN [core_sites] " +
                    "ON [core_data].[SiteID] = [core_sites].[ID]) " +
                    "INNER JOIN [core_sources] " +
                    "ON [core_data].[SourceID] = [core_sources].[ID]) " +
                "WHERE [Time] >= #%s# " +
                    "AND [Time] <= #%s# " +
                    "AND [core_sources].[Name] = '%s'" +
                    "AND [core_sites].[Name] = '%s' " +
                    "AND [core_properties].[Name] = '%s' " +
                "ORDER BY [Time]",
                offsetStartTime,
                offsetStopTime,
                "Glen_Canyon_theoretical_PAR",
                element.getElementsByTagName(KEY_SITE).item(0).getTextContent(),
                "PhotoActiveRad");
        String[] states = {"State"};
        TimeSeries timeSeries = new TimeSeries(
                name, 
                statement.executeQuery(sql), 
                "Time", 
                states,
                offsetTimeDatum);
        sourceConn.close();
        
        return timeSeries;
    }
    
}
