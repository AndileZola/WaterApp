package adapters;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.waterapp.MainActivity;
import com.waterapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import backend.usageModel;

public class usageAdapter extends RecyclerView.Adapter<usageAdapter.MyViewHolder>
{
    private LayoutInflater inflater;
    List<usageModel> items = Collections.emptyList();
    Context context;
    public usageAdapter(Context context, List<usageModel> items)
    {
        inflater= LayoutInflater.from(context);
        this.items=items;
        this.context = context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType)
    {
        View view = inflater.inflate(R.layout.contacts_row,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder,final int position)
    {
        final usageModel current=items.get(position);
        holder.type.setText(current.getUsagename());
        holder.volume.setText(current.getVolume() +" ml");
        holder.bill.setText("R"+String.valueOf(Integer.parseInt(current.getVolume())*2.00)+"0");
        MainActivity.totalBill.setText(": R"+current.getBill()+"0");
        switch(current.getUsagetypeid())
        {
            case "1":holder.icon.setImageResource(R.mipmap.drink);break;
            case "2":holder.icon.setImageResource(R.mipmap.pot);break;
            case "3":holder.icon.setImageResource(R.mipmap.bath);break;
            case "4":holder.icon.setImageResource(R.mipmap.cleaning);break;
            case "5":holder.icon.setImageResource(R.mipmap.car);break;
            case "6":holder.icon.setImageResource(R.mipmap.watering);break;
            case "7":holder.icon.setImageResource(R.mipmap.laundry);break;
        }
    }
    @Override
    public int getItemCount()
    {
        return items.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView type,volume,bill,date;
        ImageView icon;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            type    = (TextView) itemView.findViewById(R.id.type);
            volume  = (TextView) itemView.findViewById(R.id.volume);
            bill    = (TextView) itemView.findViewById(R.id.bill);
            date    = (TextView) itemView.findViewById(R.id.date);
            icon    = (ImageView) itemView.findViewById(R.id.icn);
        }
    }
    public void setFilter(List<usageModel> contactsModels)
    {
        Log.d("filter",String.valueOf(contactsModels.size()));
        items= new ArrayList<>();
        items.addAll(contactsModels);
        notifyDataSetChanged();
    }
}
