package backend;

/**
 * Created by Andile on 09/08/2016.
 */
public class chatModel
{
    String name;
    String number;
    String message;

    public chatModel(String name,String number,String message)
    {
        this.name = name;
        this.number = number;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getMessage() {
        return message;
    }
}
