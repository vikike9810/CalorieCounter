package hu.aut.bme.kaloriaszamlalo.Today.Adapter;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hu.aut.bme.kaloriaszamlalo.R;
import hu.aut.bme.kaloriaszamlalo.Model.Item;


public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    public final List<Item> items;
    public  int osszreggeli=0;
    public  int osszebed=0;
    public  int osszvacsora=0;
    public Context con;

    public Adapter() {
        items = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        String currentDateandTime = sdf.format(new Date());
        //currentDateandTime="2017.12.05";
        final Item item = items.get(position);
        holder.nameTextView.setText(item.name);
        holder.categoryTextView.setText(item.category.name());
        int cal=(int)(((float)(item.kcal)/100)*(float)item.mennyiseg);
        item.date=currentDateandTime;

        if(item.category.equals(Item.Category.Reggeli)){
            osszreggeli+=(int)(((float)(item.kcal)/100)*(float)item.mennyiseg);
        }
        if(item.category.equals(Item.Category.Ebéd)){
            osszebed+=(int)(((float)(item.kcal)/100)*(float)item.mennyiseg);
        }
        if(item.category.equals(Item.Category.Vacsora)){
            osszvacsora+=(int)(((float)(item.kcal)/100)*(float)item.mennyiseg);
        }
        holder.priceTextView.setText(Integer.toString(cal)+con.getString(R.string.kcal));
        holder.iconImageView.setImageResource(getImageResource(item.category));

        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Item torlendo = item;

                if(torlendo.category.equals(Item.Category.Reggeli)){
                    osszreggeli-=(int)(((float)(torlendo.kcal)/100)*(float)torlendo.mennyiseg);
                }
                if(torlendo.category.equals(Item.Category.Ebéd)){
                    osszebed-=(int)(((float)(torlendo.kcal)/100)*(float)torlendo.mennyiseg);
                }
                if(torlendo.category.equals(Item.Category.Vacsora)){
                    osszvacsora-=(int)(((float)(torlendo.kcal)/100)*(float)torlendo.mennyiseg);
                }
                items.remove(holder.getItemViewType());
                item.delete();
                notifyItemRemoved(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    private @DrawableRes
    int getImageResource(Item.Category category) {
        @DrawableRes int ret;
        switch (category) {
            case Reggeli:
                ret = R.drawable.breakfast;
                break;
            case Ebéd:
                ret = R.drawable.dinner;
                break;
            case Vacsora:
                ret = R.drawable.luncs;
                break;
            default:
                ret = 0;
        }
        return ret;
    }

    public void addItem(Item item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void update(List<Item> Items) {
        items.clear();
        items.addAll(Items);
        notifyDataSetChanged();
    }

    public void delete(){
        items.clear();
        osszreggeli=0;
        osszebed=0;
        osszvacsora=0;
        Item.deleteAll(Item.class);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iconImageView;
        TextView nameTextView;
        TextView categoryTextView;
        TextView priceTextView;
        ImageButton removeButton;

        public ViewHolder(View itemView) {
            super(itemView);
            iconImageView = (ImageView) itemView.findViewById(R.id.ItemIconImageView);
            nameTextView = (TextView) itemView.findViewById(R.id.ItemNameTextView);
            categoryTextView = (TextView) itemView.findViewById(R.id.ItemCategoryTextView);
            priceTextView = (TextView) itemView.findViewById(R.id.ItemKcalTextView);
            removeButton = (ImageButton) itemView.findViewById(R.id.ItemRemoveButton);
        }


    }
}