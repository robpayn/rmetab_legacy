package data.envdb;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import stream_metab.utils.ConfigElement;

public class DeploymentData {
    
    public static void main(String[] args)
    {
        String oldTimeZone = System.getProperty("user.timezone");
        TimeZone oldDefaultTimeZone = TimeZone.getDefault();
        System.setProperty("user.timezone", "GMT");
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        
        File workingDir = new File(System.getProperty("user.dir"));
        DeploymentData data = null;
        ConfigElement element = null;
        try
        {
             Document xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                    workingDir + File.separator + "envdb.xml");
             ConfigElement docElement = new ConfigElement(xml.getDocumentElement());
             element = docElement.getFirstElementByTag("DeploymentData");
             data = new DeploymentData(
                     docElement.getElement().getAttribute("file"),
                     element);
        }
        catch (Exception t)
        {
            System.out.println("Problem loading manager XML file.  Model not executed.");
            t.printStackTrace();
            System.exit(1);
        }
        try
        {
            data.acquire();
            data.writeTextFile(element.getTextContentForFirstTag("TextFile"));
            data.close();
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1);
        }
        
        System.setProperty("user.timezone", oldTimeZone);
        TimeZone.setDefault(oldDefaultTimeZone);
    }

    private EnvironDatabase envDB;
    private String dbFileName;
    private HashMap<String, ArrayList<String>> siteMap;
    private String startTimeString;
    private String stopTimeString;
    
    public DeploymentData(String dbFileName, ConfigElement element)
    {
        this.dbFileName = dbFileName;
        this.startTimeString = element.getElement().getAttribute("startTime");
        this.stopTimeString = element.getElement().getAttribute("stopTime");
        NodeList siteList = element.getElement().getElementsByTagName("Site");
        siteMap = new LinkedHashMap<String, ArrayList<String>>();
        for (int i = 0; i < siteList.getLength(); i++)
        {
            Element siteElement = (Element)siteList.item(i);
            ArrayList<String> propertyList = new ArrayList<String>();
            NodeList propList = siteElement.getElementsByTagName("Property");
            for (int j=0; j < siteList.getLength(); j++)
            {
                Element propElement = (Element)propList.item(i);
                propertyList.add(propElement.getAttribute("name"));
            }
            siteMap.put(siteElement.getAttribute("name"), propertyList);
        }
    }

    private void acquire() throws SQLException
    {
        envDB = new EnvironDatabase(dbFileName);
        for (Entry<String, ArrayList<String>> siteEntry: siteMap.entrySet())
        {
            for (String property: siteEntry.getValue())
            {
                ResultSet results = envDB.getDeploymentData(
                        startTimeString, 
                        stopTimeString, 
                        siteEntry.getKey(), 
                        property);
            } 
        }
    }

    private void close() throws SQLException
    {
        envDB.close();
    }

    private void writeTextFile(String textContentForFirstTag)
    {
        //FIXME: Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
