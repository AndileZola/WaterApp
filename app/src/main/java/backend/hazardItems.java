package backend;

/**
 * Created by Developer4 on 5/3/2016.
 */
public class hazardItems
{
     String hazardId;
     String title;
     String content;
     String date;
     String comments;

    public hazardItems(String hazardId,String title,String content,String date,String comments)
    {
        this.hazardId=hazardId;
        this.title=title;
        this.content=content;
        this.date=date;
        this.comments=comments;
    }
    public String geId()
    {
        return hazardId;
    }
    public String getTitle()
    {
        return title;
    }
    public String getContent()
    {
        return content;
    }
    public String getDate()
    {
        return date;
    }
    public String getComments()
    {
        return comments;
    }
}
