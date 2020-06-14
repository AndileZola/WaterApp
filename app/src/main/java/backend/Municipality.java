package backend;

/**
 * Created by Andile on 12/06/2016.
 */
public class Municipality
{
    String Id;
    String Name;
    public void setMunicipality(String Id,String Name)
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
