package backend;

/**
 * Created by Andile on 12/06/2016.
 */
public class Province
{
    String Id;
    String Name;
    public void setProvince(String Id,String Name)
    {
        this.Id = Id;
        this.Name = Name;
    }
    public String getId()
    {
        return Id;
    }
    public String getName()
    {
        return Name;
    }
}
