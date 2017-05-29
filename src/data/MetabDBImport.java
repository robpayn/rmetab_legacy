package data;

import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import data.envdb.EnvironDatabase;

/**
 * Imports data from spreadsheets
 * 
 * @author robpayn
 *
 */
public class MetabDBImport {
    
    /**
     * Environmental database with metabolism data
     */
    private EnvironDatabase environDB;
    
    /**
     * Properties for the importer
     */
    private Properties props;
    
    /**
     * XML document with importer specifications
     */
    private Document xml;
    
    /**
     * Working directory for the importer
     */
    private File workingDir;
    
    /**
     * Construct an importer that operates in the designated working directory and
     * with the designated properties
     * 
     * @param workingDir 
     *      working directory for the importer
     * @param args 
     *      command line arguments
     * @throws IOException 
     *      if error in accessing properties file
     * @throws FileNotFoundException  
     *      if properties file is not found
     * @throws Exception 
     *      if error in creating the environmental database
     */
    public MetabDBImport(File workingDir, String[] args) 
    throws Exception
    {
        this.workingDir = workingDir;
        if (args[0].equals("props"))
        {
            xml = null;
            props = new Properties();
            props.load(new FileReader(
                    new File(this.workingDir.getAbsolutePath() + File.separator + args[1])));
            environDB = new EnvironDatabase(
                    this.workingDir.getAbsolutePath() + File.separator + 
                    props.getProperty("DatabaseName"));
        }
        else
        {
            props = null;
            xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                        workingDir.getAbsolutePath() + 
                        File.separator + args[0] + ".xml");
            environDB = new EnvironDatabase(
                    this.workingDir.getAbsolutePath() + File.separator + 
                    xml.getDocumentElement().getAttribute("DatabaseName"));
        }
    }

    /**
     * Close the database
     */
    private void close()
    {
        try
        {
            environDB.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Entry point to run a spreadsheet importer with a properties file
     * defined in the command line.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args)
    {
        MetabDBImport importer = null;
        try
        {
            File workingDir = new File(System.getProperties().getProperty("user.dir"));
            importer = new MetabDBImport(workingDir, args);
            importer.run();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            importer.close();
        }
    }

    /**
     * Run the importers designated in the properties
     */
    public void run()
    {
        if (props != null)
        {
            int i = 1;
            while (i > 0)
            {
                String importerName = props.getProperty(Integer.toString(i));
                if (importerName != null)
                {
                    try
                    {
                        DBImporter.execute(workingDir, environDB, props, importerName);
                    }
                    catch (Exception e)
                    {
                        System.out.println(importerName + " could not be executed.");
                        e.printStackTrace();
                    }
                }
                else
                {
                    break;
                }
                i++;
            }
        }
        else
        {
            NodeList importerList = xml.getElementsByTagName("importer");
            for (int i = 0; i < importerList.getLength(); i++)
            {
                Element importerElem = (Element)importerList.item(i);
                try
                {
                    DBImporter.execute(workingDir, environDB, importerElem);
                }
                catch (Exception e)
                {
                    System.out.println(importerElem.getAttribute("name") + " could not be executed.");
                    e.printStackTrace();
                }
            }
        }
    }

}
