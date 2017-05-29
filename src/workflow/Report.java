package workflow;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import stream_metab.utils.ConfigElement;

public class Report {

    private File workingDir;
    
    public Report(File workingDir) throws Exception
    {
        if (!workingDir.isDirectory())
        {
            throw new Exception("Directory is not selected.");
        }
        this.workingDir = workingDir;
    }

    public void run()
    {
    }

}
