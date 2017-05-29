package data.leesferry;

import java.io.*;
import java.util.*;

public class LeesFerryFlowFile implements Iterable<LeesFerryFlowRecord> {
    
    private long counter;
    
    private boolean isLoggingEnabled;
    
    private String fileName;
    
    private String lastValidLine;
    
    private BufferedWriter logWriter;
    
    private LeesFerryFlowRecord nextRecord;
    
    private BufferedReader reader;
    
    private Iterator<LeesFerryFlowRecord> iterator;
    
    public LeesFerryFlowFile(File file) throws Exception
    {
        this.fileName = file.getName();
        reader = new BufferedReader(new FileReader(file));
        while (reader.ready())
        {
            String line = reader.readLine();
            if (line.startsWith("DATETIME"))
            {
                break;
            }
        }
        nextRecord = getNextRecord();
        counter = 1;
        nextRecord.setNumber(counter);
        lastValidLine = "";
    }

    protected LeesFerryFlowRecord getNextRecord() throws IOException
    {
        if (!reader.ready())
        {
            return null;
        }
        while (reader.ready())
        {
            String line = reader.readLine();
            LeesFerryFlowRecord record = LeesFerryFlowRecord.createIfValid(line);
            if (record != null)
            {
                lastValidLine = line;
                return record;
            }
            else
            {
                if (isLoggingEnabled)
                {
                    logWriter.write("Warning: invalid line skipped -> " + line);
                    logWriter.newLine();
                    logWriter.write("Last valid line -> " + lastValidLine);
                    logWriter.newLine();
                }
            }
        }
        return null;
    }

    @Override
    public Iterator<LeesFerryFlowRecord> iterator()
    {
        return new Iterator<LeesFerryFlowRecord>() 
        {
            @Override
            public boolean hasNext()
            {
                return !(nextRecord == null);
            }

            @Override
            public LeesFerryFlowRecord next()
            {
                LeesFerryFlowRecord record = nextRecord;
                try
                {
                    nextRecord = getNextRecord();
                    if (nextRecord != null)
                    {
                        counter++;
                        nextRecord.setNumber(counter);
                    }
                }
                catch (IOException e)
                {
                    nextRecord = null;
                }
                return record;
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException(
                        "Removal not supported by this iterator.");
            }
        };
    }

    public void setLoggingEnabled(boolean isLoggingEnabled) throws IOException
    {
        if (!this.isLoggingEnabled && isLoggingEnabled)
        {
            logWriter = new BufferedWriter(new FileWriter(new File(fileName + ".log")));
        }
        this.isLoggingEnabled = isLoggingEnabled;
    }

    public void close() throws IOException
    {
        logWriter.close();
    }

}
