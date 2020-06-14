package backend;

public class usageModel
{
    String usageid;
    String usagetypeid;
    String usagename;
    String volume;
    String date;
    String bill;

    public usageModel(String usageid,String usagetypeid, String usagename,String volume,String date, String bill)
    {
        this.usageid     = usageid;
        this.usagetypeid = usagetypeid;
        this.usagename   = usagename;
        this.volume      = volume;
        this.date        = date;
        this.bill        = bill;
    }
    public String getUsageid()
    {
        return usageid;
    }
    public String getUsagetypeid() {return usagetypeid;}
    public String getUsagename()
    {
        return usagename;
    }
    public String getVolume() {return volume;}
    public String getDate()
    {
        return date;
    }
    public String getBill() {return bill;}
}
