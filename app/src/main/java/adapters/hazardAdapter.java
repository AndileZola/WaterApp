package adapters;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.waterapp.R;

import java.util.Collections;
import java.util.List;

import backend.hazardItems;
import interfaces.forumOnclick;
import interfaces.onClickingItem;

/**
 * Created by Developer4 on 5/3/2016.
 */
public class hazardAdapter extends RecyclerView.Adapter<hazardAdapter.MyViewHolder>
{

    private LayoutInflater inflater;
    List<hazardItems> items = Collections.emptyList();
    private forumOnclick clickingItem;

    public  hazardAdapter(Context context,List<hazardItems> items)
    {
        inflater= LayoutInflater.from(context);
        this.items=items;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType)
    {
        //Log.i("LOG","onCreateViewHolder");
        View view = inflater.inflate(R.layout.hazard_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder,int position)
    {
         Log.i("TST", "TEST 123");
        hazardItems current=items.get(position);
        holder.content.setText(current.getContent());
        holder.title.setText(current.getTitle());
        holder.comments.setText(current.getComments());
        holder.date.setText(current.getDate());
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }
    public void setRecyclerViewClickListen(forumOnclick click)
    {

        clickingItem = click;
    }
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView content;
        TextView comments;
        TextView title;
        TextView date;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.hazard_title);
            content = (TextView) itemView.findViewById(R.id.hazard_content);
            comments = (TextView) itemView.findViewById(R.id.hazard_comments);
            date = (TextView) itemView.findViewById(R.id.hazard_date);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v)
        {
            if(clickingItem != null)
            {
                hazardItems current=items.get(getLayoutPosition());
                String title=current.getTitle();
                String content=current.getContent();
                String comments=current.getContent();
                String date =current.getDate();
                clickingItem.recyclerViewListClicked(v,title,content,date,comments,getLayoutPosition());
            }
        }

    }
}
