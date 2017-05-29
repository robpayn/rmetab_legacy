package stream_metab.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

import org.neosimulation.apps.modelmanager.neov1.strategy.metaparam.TimeSeriesExtractor;
import org.neosimulation.apps.modelmanager.neov1.tools.TimeSeries;
import org.w3c.dom.Element;

public class InstrumentExtractor extends SiteExtractor {
    
    public static final String KEY_INSTRUMENT = "instrument";

    public InstrumentExtractor(String paramID, Element element,
            String timeDatum, String startTime, String stopTime, String filePath)
    {
        super(paramID, element, timeDatum, startTime, stopTime, filePath);
    }

    @Override
    public TimeSeries getTimeSeries(String name) throws Exception
    {
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
                    "INNER JOIN ([core_sources] " +
                        "INNER JOIN ([src_type_deployments] " +
                            "INNER JOIN [src_instruments] " +
                            "ON [src_type_deployments].[InstrumentID] = [src_instruments].[ID]) " +
                        "ON [src_type_deployments].[SourceID] = [core_sources].[ID]) " +
                    "ON [core_data].[SourceID] = [core_sources].[ID]) " +
                "WHERE [Time] >= #%s# " +
                    "AND [Time] <= #%s# " +
                    "AND [core_sites].[Name] = '%s' " +
                    "AND [core_properties].[Name] = '%s' " +
                    "AND [src_instruments].[Name] = '%s' " +
                "ORDER BY [Time]",
                startTime,
                stopTime,
                element.getElementsByTagName(KEY_SITE).item(0).getTextContent(),
                element.getElementsByTagName(KEY_PROPERTY).item(0).getTextContent(),
                element.getElementsByTagName(KEY_INSTRUMENT).item(0).getTextContent());
        
        String[] states = {"State"};
        TimeSeries timeSeries = new TimeSeries(
                name, 
                statement.executeQuery(sql), 
                "Time", 
                states,
                timeDatum);
        sourceConn.close();
        
        return timeSeries;
    }

}
