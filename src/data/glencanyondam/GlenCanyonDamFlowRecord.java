package data.glencanyondam;

public class GlenCanyonDamFlowRecord {
    
    private String dateTime;
    
    private long number;
    
    double qCFS;
    
    double qCMS;

    public GlenCanyonDamFlowRecord(String dateTime, double qCFS, double qCMS)
    {
        this.dateTime = dateTime;
        this.qCFS = qCFS;
        this.qCMS = qCMS;
    }

    public static GlenCanyonDamFlowRecord createIfValid(String line)
    {
        String[] lineSplit = line.split("\t");
        if (lineSplit.length != 3)
        {
            return null;
        }
        try
        {
            return new GlenCanyonDamFlowRecord(
                    lineSplit[0], 
                    Double.valueOf(lineSplit[1]),
                    Double.valueOf(lineSplit[2]));
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
