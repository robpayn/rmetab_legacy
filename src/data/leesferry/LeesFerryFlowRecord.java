package data.leesferry;

public class LeesFerryFlowRecord {
    
    private String dateTime;
    
    private long number;
    
    double qCFS;
    
    double qCMS;

    public LeesFerryFlowRecord(String dateTime, double qCFS, double qCMS)
    {
        this.dateTime = dateTime;
        this.qCFS = qCFS;
        this.qCMS = qCMS;
    }

    public static LeesFerryFlowRecord createIfValid(String line)
    {
        String[] lineSplit = line.split("\t");
        if (lineSplit.length != 4)
        {
            return null;
        }
        try
        {
            return new LeesFerryFlowRecord(
                    lineSplit[1], 
                    Double.valueOf(lineSplit[2]),
                    Double.valueOf(lineSplit[3]));
        }
        catch (Exception e)
        {
            return null;
        }
        
    }
    
    public long getNumber()
    {
        return number;
    }

    public void setNumber(long number)
    {
        this.number = number;
    }

    public String timeAsDBString()
    {
        String[] dateTimeSplit = dateTime.split(" ");
        String[] dateSplit = dateTimeSplit[0].split("/");
        String[] timeSplit = dateTimeSplit[1].split(":");
        return String.format(
                "#%4d-%2d-%2d %2d:%2d:00#",
                Integer.valueOf(dateSplit[2]),
                Integer.valueOf(dateSplit[0]),
                Integer.valueOf(dateSplit[1]),
                Integer.valueOf(timeSplit[0]),
                Integer.valueOf(timeSplit[1]));
    }

    public double getCMS()
    {
        return qCMS;
    }

}
