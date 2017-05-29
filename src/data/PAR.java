package data;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import data.envdb.EnvironDatabase;

public class PAR {
    
    /**
     * Prefix for access odbc driver
     */
    public static final String MSACCESS_ODBC_PREFIX = 
            "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ=";

    /**
     * Suffix for access odbc driver
     */
    public static final String MSACCESS_ODBC_SUFFIX = ";DriverID=22;READONLY=true}";

    /**
     * Name of JDBC/ODBC bridge driver class
     */
    public static final String JDBC_ODBC_CLASS = "sun.jdbc.odbc.JdbcOdbcDriver";
    
    public static void main(String[] args)
    {
        try
        {
            Class.forName(JDBC_ODBC_CLASS);
            Connection sourceConn = DriverManager.getConnection(
                    MSACCESS_ODBC_PREFIX + 
                        "D:\\payn\\model_ws\\metabolism\\leesferry\\mcmcanalysis\\unsteady\\data\\data.accdb" + 
                        MSACCESS_ODBC_SUFFIX);
            EnvironDatabase edb = new EnvironDatabase(
                    "R:\\research\\archive\\data\\az_colorado_river\\data.accdb");
                        
            Statement sourceStatement = sourceConn.createStatement();
            ResultSet results = sourceStatement.executeQuery(
                    "SELECT * " + 
                    "FROM [light_avg] " +
                    "WHERE [time] > #2009-01-01 00:01:00# AND [time] < #2009-01-02 00:01:00#");
            GregorianCalendar date1970 = new GregorianCalendar(1899,11,30,0,0,0);
            
            long counter = 106000;
            while (results.next())
            {
                GregorianCalendar gregTime = new GregorianCalendar();
                gregTime.setTimeInMillis(results.getTimestamp("time").getTime());
                gregTime.add(GregorianCalendar.YEAR, 1);
                long millis = gregTime.getTimeInMillis() - date1970.getTimeInMillis();
                double time = ((double)millis / 1000) / 86400;
                edb.addValueDatum(273, counter, 7, 3, 76, time, results.getDouble("light"), 6);
                counter++;
            }
            edb.close();
            sourceConn.close();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
