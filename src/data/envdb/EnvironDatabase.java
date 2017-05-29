package data.envdb;

import java.sql.*;

import neo.table.JDBCConnectionFactory;

/**
 * Controls and environmental database in MS Access
 * 
 * @author robpayn
 *
 */
public class EnvironDatabase {
    
    /**
     * Database connection
     */
    private Connection conn;

    /**
     * Create an database connection to the provided file name
     * 
     * @param dbFileName name of database file
     * @throws SQLException if error in creating database connection
     */
    public EnvironDatabase(String dbFileName) throws SQLException
    {
        conn = JDBCConnectionFactory.getAccessDBConnection(dbFileName);
    }

    /**
     * Add a datum record with the provided field information
     * 
     * Time is in MS Office "days since epoch" format. Epoch is 1900-01-00 00:00:00.
     *
     * @param sourceID identification of the related source record
     * @param code unique code for this datum from the source
     * @param propertyID identification of the related property record
     * @param dimID identification of the related dimension record
     * @param siteID identification of the related site record
     * @param time time of datum observation
     * @return identification of the added datum record
     * @throws SQLException if error in database access
     */
    public Long addDatum(long sourceID, long code, long propertyID, long dimID, long siteID, Double time) 
    throws SQLException
    {
        String fieldList = "[SourceID], [Code], [PropertyID], [DimID], [SiteID]";
        String valueList = 
            Long.toString(sourceID) + ", " +
            Long.toString(code) + ", " +
            Long.toString(propertyID) + ", " +
            Long.toString(dimID) + ", " +
            Long.toString(siteID);
        if (time != null)
        {
            fieldList += ", [Time]";
            valueList += ", " + Double.toString(time);
        }
        sqlUpdate(String.format(
                "INSERT INTO [core_data] (%s) " +
                    "VALUES (%s)", 
                    fieldList,
                    valueList));
        return getDatumID(sourceID, code);
    }

    /**
     * Add a datum record with the provided field information
     * 
     * Time is in DB string format "#yyyy-mm-dd hh:mm:ss#"
     * 
     * @param sourceID identification of the related source record
     * @param code unique code for this datum from the source
     * @param propertyID identification of the related property record
     * @param dimID identification of the related dimension record
     * @param siteID identification of the related site record
     * @param time time of datum observation
     * @return identification of the added datum record
     * @throws SQLException if error in database access
     */
    public Long addDatum(long sourceID, long code, long propertyID, long dimID, long siteID, String time) 
    throws SQLException
    {
        String fieldList = "[SourceID], [Code], [PropertyID], [DimID], [SiteID]";
        String valueList = 
            Long.toString(sourceID) + ", " +
            Long.toString(code) + ", " +
            Long.toString(propertyID) + ", " +
            Long.toString(dimID) + ", " +
            Long.toString(siteID);
        if (time != null)
        {
            fieldList += ", [Time]";
            valueList += ", " + time;
        }
        sqlUpdate(String.format(
                "INSERT INTO [core_data] (%s) " +
                    "VALUES (%s)", 
                    fieldList,
                    valueList));
        return getDatumID(sourceID, code);
    }

    /**
     * Add a deployment record with the provided field information
     * 
     * @param sourceID identification of the related source
     * @param instrumentID identification of the related instrument
     * @param startTime start time of the deployment
     * @param stopTime stop time of the deployment
     * @param fileName name of the file with deployment data
     * @throws SQLException if error in database access
     */
    public void addDeployment(long sourceID, long instrumentID, double startTime, 
            double stopTime, String fileName) 
    throws SQLException
    {
        sqlUpdate(String.format(
                "INSERT INTO [src_type_deployments] ([SourceID], [InstrumentID], [TimeBegin], [TimeEnd], [RawData]) " +
                    "VALUES (%s, %s, %s, %s, '%s')", 
                Long.toString(sourceID),
                Long.toString(instrumentID),
                Double.toString(startTime),
                Double.toString(stopTime),
                fileName));
    }

    /**
     * Add a deployment record with names generated from provided information.  
     * Creates a related source record and instrument record as needed.
     * 
     * @param deploymentNameBase name base of the data source used for the deployment
     * @param instrumentName name of the instrument
     * @param startTime time the deployment started
     * @param stopTime time the deployment stopped
     * @param fileName name of the file with deployment data
     * @param overwrite flag indicating if deployment with the same name should be overwritten
     * @return identification of the source created
     * @throws SQLException if error in database access
     */
    public long addDeployment(String deploymentNameBase, String instrumentName, double startTime, 
            double stopTime, String fileName, boolean overwrite) 
    throws SQLException
    {
        Long instID = getInstrumentID(instrumentName);
        if (instID == null)
        {
            instID = addInstrument(instrumentName);
        }
        String sourceName = deploymentNameBase + "_" + instrumentName;
        if (overwrite)
        {
            deleteSource(sourceName);
        }
        Long sourceID = addSource(sourceName, getSourceTypeID("src_type_deployments"));
        addDeployment(sourceID, instID, startTime, stopTime, fileName);
        return sourceID;
    }
    
    /**
     * Add an extraction source record
     * 
     * @param sourceName name of the extraction data source
     * @param fileName name of the file with extracted data
     * @param overwrite flag indicating if an existing extraction with the same source name should be overwritten
     * @return identification of the added record
     * @throws SQLException if error in database access
     */
    public long addExtraction(String sourceName, String fileName, boolean overwrite) throws SQLException
    {
        if (overwrite)
        {
            deleteSource(sourceName);
        }
        long sourceID = addSource(sourceName, getSourceTypeID("src_type_extractions"));
        sqlUpdate(String.format(
                "INSERT INTO [src_type_extractions] ([SourceID], [File]) " +
                    "VALUES (%s, '%s')", 
                Long.toString(sourceID),
                fileName));
        return sourceID;
    }

    /**
     * Add an instrument record with the provided instrument name
     * 
     * @param instrumentName name of instrument to add
     * @return identification of instrument
     * @throws SQLException if error in database access
     */
    public Long addInstrument(String instrumentName) throws SQLException
    {
        sqlUpdate(String.format(
            "INSERT INTO [src_instruments] ([Name]) " +
                "VALUES ('%s')", 
            instrumentName));
        return getInstrumentID(instrumentName);
    }

    /**
     * Add a record to the simulation data source table
     * 
     * @param simName
     *      name of the simulation (unique in source table)
     * @param modelName
     *      name of the model used to run the simulation
     * @param outputLocation
     *      location of the output from the model
     * @param description
     *      description of the simulation
     * @param overwrite
     *      flag indicating if duplicate simulation names should be overwritten
     * @return
     *      integer ID of the source record added
     * @throws SQLException
     *      if error in accessing database
     */
    public long addSimulation(String simName, String modelName, String outputLocation, 
            String description, Boolean overwrite) throws SQLException
    {
        if (overwrite)
        {
            deleteSource(simName);
        }
        long sourceID = addSource(simName, getSourceTypeID("src_type_simulations"));
        sqlUpdate(String.format(
                "INSERT INTO [src_type_simulations] ([SourceID], [Model], [OutputLocation], [Description]) " +
                    "VALUES (%s, '%s', '%s', '%s')", 
                Long.toString(sourceID),
                modelName,
                outputLocation,
                description));
        return sourceID;
    }

    /**
     * Add a site record with the given name
     * 
     * @param siteName name of site to add
     * @return identification of the added site record
     * @throws SQLException if error in database access
     */
    public long addSite(String siteName) throws SQLException
    {
        sqlUpdate(String.format(
                "INSERT INTO [core_sites] ([Name]) " +
                    "VALUES ('%s')", 
                siteName));
        return getSiteID(siteName);
    }

    /**
     * Add the a source record with the information provided.
     * 
     * @param sourceName name of the source to add
     * @param sourceTypeID type of the source to add
     * @return identification of the source created
     * @throws SQLException if error in database access
     */
    public Long addSource(String sourceName, long sourceTypeID) 
    throws SQLException
    {
        sqlUpdate(String.format(
                "INSERT INTO [core_sources] ([Name], [TypeID]) " +
                    "VALUES ('%s', %s)", 
                sourceName,
                Long.toString(sourceTypeID)));
        return getSourceID(sourceName);
    }

    /**
     * Adds a new value record
     * 
     * @param datumID identification of related datum record
     * @param value value to be added
     * @param unitID identification of related unit record
     * @throws SQLException if error in database access
     */
    private void addValue(long datumID, double value, long unitID) 
    throws SQLException
    {
        sqlUpdate(String.format(
                "INSERT INTO [dim_type_values] ([DatumID], [Value], [UnitID]) " +
                    "VALUES (%s, %s, %s)", 
                Long.toString(datumID),
                Double.toString(value),
                Long.toString(unitID)));
    }

    /**
     * Add a one-dimensional datum, including a datum record and a value record.
     * 
     * Time is in MS Office "days since epoch" format. Epoch is 1900-01-00 00:00:00.
     * 
     * @param sourceID identification of related source record
     * @param code unique code for the datum from the specified source
     * @param propertyID identification of related property record
     * @param dimID identification of related dimension record
     * @param siteID identification of related site
     * @param time time of observation in floating point <days since epoch>.<fraction of day> format
     * @param value value for the datum
     * @param unitID identification of related unit record
     * @return identification of added datum record
     * @throws SQLException if error in database access
     */
    public long addValueDatum(long sourceID, long code, long propertyID, long dimID, long siteID, Double time, 
            double value, long unitID) 
    throws SQLException
    {
        long datumID = addDatum(sourceID, code, propertyID, dimID, siteID, time);
        addValue(datumID, value, unitID);
        return datumID;
    }

    /**
     * Add a one-dimensional datum, including a datum record and a value record.
     * 
     * Time is in DB string format "#yyyy-mm-dd hh:mm:ss#"
     * 
     * @param sourceID identification of related source record
     * @param code unique code for the datum from the specified source
     * @param propertyID identification of related property record
     * @param dimID identification of related dimension record
     * @param siteID identification of related site
     * @param time time of observation in DB string format
     * @param value value for the datum
     * @param unitID identification of related unit record
     * @return identification of added datum record
     * @throws SQLException if error in database access
     */
    public long addValueDatum(long sourceID, long code, long propertyID, long dimID, long siteID, String time, 
            double value, long unitID) 
    throws SQLException
    {
        long datumID = addDatum(sourceID, code, propertyID, dimID, siteID, time);
        addValue(datumID, value, unitID);
        return datumID;
    }

    /**
     * Close the database connection
     * 
     * @throws SQLException if connection could not be closed
     */
    public void close() throws SQLException
    {
        conn.close();
    }

    /**
     * Delete the source record with the name provided
     * 
     * @param sourceName name of source
     * @throws SQLException if database access error
     */
    public void deleteSource(String sourceName) throws SQLException
    {
        Statement statement = conn.createStatement();
        statement.executeUpdate(String.format(
                "DELETE FROM [core_sources] " +
                    "WHERE [Name] = '%s'", 
                sourceName));
        statement.close();
    }

    /**
     * Get the identification of a datum record based on the provided primary 
     * key information 
     * 
     * @param sourceID identification of the related source
     * @param code code for the record
     * @return identification of the datum record
     * @throws SQLException if error in database access
     */
    public Long getDatumID(long sourceID, long code) throws SQLException
    {
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery(String.format(
                "SELECT [ID] " +
                    "FROM [core_data] " +
                    "WHERE [SourceID] = %s " +
                        "AND [Code] = %s",
                Long.toString(sourceID),
                Long.toString(code)));
        if (rs.next())
        {
            long id = rs.getInt("ID");
            statement.close();
            return id;
        }
        else
        {
            statement.close();
            return null;
        }
    }

    public ResultSet getDeploymentData(String startTimeString, String stopTimeString, String site, String property) 
    throws SQLException
    {
        Statement statement = conn.createStatement();
        return statement.executeQuery(String.format(
            "SELECT [core_data].[Time], " +
                "[src_instruments].[Name] AS [Probe], " +
                "[dim_type_values].[Value] AS [Value] " +
            "FROM ((([core_data] " +
                "INNER JOIN [core_properties] " +
                "ON [core_data].[PropertyID] = [core_properties].[ID]) " +
                "INNER JOIN [dim_type_values] " +
                "ON [dim_type_values].[DatumID] = [core_data].[ID]) " +
                "INNER JOIN [core_sites] " +
                "ON [core_data].[SiteID] = [core_sites].[ID]) " +
                "INNER JOIN ([core_sources] " +
                   "INNER JOIN ([src_type_deployments] " +
                      "INNER JOIN [src_instruments] " +
                      "ON [src_type_deployments].[InstrumentID] = " +
                         "[src_instruments].[ID]) " +
                   "ON [src_type_deployments].[SourceID] = [core_sources].[ID]) " +
                "ON [core_data].[SourceID] = [core_sources].[ID] " +
             "WHERE [core_sites].[Name] = '%s' " +
                 "AND [core_properties].[Name] = '%s' " +
                 "AND [Time] >= #%s# " +
                 "AND [Time] <= #%s# ",
             site,
             property,
             startTimeString,
             stopTimeString));
    }

    /**
     * Get the identification of the dimension record with the provided name
     * 
     * @param dimName name of dimension to retrieve
     * @return identification of retrieved record, null if dimension is not in table
     * @throws SQLException if error in database access
     */
    public Long getDimensionID(String dimName) throws SQLException
    {
        Statement statement = conn.createStatement(); 
        ResultSet rs = statement.executeQuery(
                "SELECT [ID] " +
                    "FROM [core_dimensions] " +
                    "WHERE [Name] = '" + dimName + "'");
        if (rs.next())
        {
            long id = rs.getInt("ID");
            statement.close();
            return id;
        }
        else
        {
            statement.close();
            return null;
        }
    }

    /**
     * Get the identification of the instrument record with the provided name.
     * 
     * @param instrumentName name of the instrument record to retrieve
     * @return identification of the record, null if instrument does not exist
     *      in the table
     * @throws SQLException if error in database access
     */
    public Long getInstrumentID(String instrumentName) throws SQLException
    {
        Statement statement = conn.createStatement(); 
        ResultSet rs = statement.executeQuery(
                "SELECT [ID] " +
                    "FROM [src_instruments] " +
                    "WHERE [Name] = '" + instrumentName + "'");
        if (rs.next())
        {
            long id = rs.getInt("ID");
            statement.close();
            return id;
        }
        else
        {
            statement.close();
            return null;
        }
    }

    /**
     * Get the identification of the property record associated with the provided name
     * 
     * @param propertyName name of property
     * @return identification of retrieved record, null if property is not in table
     * @throws SQLException if error in database access
     */
    public Long getPropertyID(String propertyName) throws SQLException
    {
        Statement statement = conn.createStatement(); 
        ResultSet rs = statement.executeQuery(
                "SELECT [ID] " +
                    "FROM [core_properties] " +
                    "WHERE [Name] = '" + propertyName + "'");
        if (rs.next())
        {
            long id = rs.getInt("ID");
            statement.close();
            return id;
        }
        else
        {
            statement.close();
            return null;
        }
    }

    /**
     * Get the identification of the site record with the provided name
     * 
     * @param siteName name of site to retrieve
     * @return identification of the site record
     * @throws SQLException if error in database access
     */
    public Long getSiteID(String siteName) throws SQLException
    {
        Statement statement = conn.createStatement(); 
        ResultSet rs = statement.executeQuery(
                "SELECT [ID] " +
                    "FROM [core_sites] " +
                    "WHERE [Name] = '" + siteName + "'");
        if (rs.next())
        {
            long id = rs.getInt("ID");
            statement.close();
            return id;
        }
        else
        {
            statement.close();
            return null;
        }
    }

    /**
     * Get the identification of the source record with the given name
     * 
     * @param sourceName name of the source to retrieve
     * @return identification of the source record
     * @throws SQLException if error in database access
     */
    public Long getSourceID(String sourceName) throws SQLException
    {
        Statement statement = conn.createStatement(); 
        ResultSet rs = statement.executeQuery(
                "SELECT [ID] " +
                    "FROM [core_sources] " +
                    "WHERE [Name] = '" + sourceName + "'");
        if (rs.next())
        {
            long id = rs.getInt("ID");
            statement.close();
            return id;
        }
        else
        {
            statement.close();
            return null;
        }
    }

    /**
     * Get the identification of the source type record with the provided name
     * 
     * @param sourceTypeName name of the source type
     * @return identification of the source type record
     * @throws SQLException if error in database access
     */
    public Long getSourceTypeID(String sourceTypeName) throws SQLException
    {
        Statement statement = conn.createStatement(); 
        ResultSet rs = statement.executeQuery(
                "SELECT [ID] " +
                    "FROM [src_sourcetypes] " +
                    "WHERE [Table] = '" + sourceTypeName + "'");
        if (rs.next())
        {
            long id = rs.getInt("ID");
            statement.close();
            return id;
        }
        else
        {
            statement.close();
            return null;
        }
    }

    /**
     * Get the identification of the unit record with the provided name
     * 
     * @param unitsName name of unit to retrieve
     * @return identification of retrieved record, null if unit is not in table
     * @throws SQLException if error in database access
     */
    public Long getUnitID(String unitsName) throws SQLException
    {
        Statement statement = conn.createStatement(); 
        ResultSet rs = statement.executeQuery(
                "SELECT [ID] " +
                    "FROM [dim_units] " +
                    "WHERE [Name] = '" + unitsName + "'");
        if (rs.next())
        {
            long id = rs.getInt("ID");
            statement.close();
            return id;
        }
        else
        {
            statement.close();
            return null;
        }
    }

    /**
     * Execute an SQL update statement and free the JDBC resources
     * 
     * @param sql SQL statement
     * @return results of the JDBC Statement.executeUpdate() method
     * @throws SQLException if error in database access
     */
    private int sqlUpdate(String sql) throws SQLException
    {
        Statement statement = conn.createStatement();
        int result = statement.executeUpdate(sql);
        statement.close();
        return result;
    }

}
