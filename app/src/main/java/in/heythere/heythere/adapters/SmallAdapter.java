package in.heythere.heythere.adapters;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import in.heythere.heythere.Detailed;
import in.heythere.heythere.R;

public class SmallAdapter extends RecyclerView.Adapter<SmallAdapter.MySmallViewHolder> {

    private Activity context;
    private Cursor array = null;

    class MySmallViewHolder extends RecyclerView.ViewHolder {

        ImageView poster;
        TextView name,venue,type;
        RelativeLayout thumb;

        MySmallViewHolder(View itemView) {
            super(itemView);
            thumb = (RelativeLayout)itemView.findViewById(R.id.thumb);
            poster = (ImageView)itemView.findViewById(R.id.poster);
            name = (TextView)itemView.findViewById(R.id.name);
            venue = (TextView)itemView.findViewById(R.id.venue);
            type = (TextView)itemView.findViewById(R.id.type);
        }
    }

    public SmallAdapter(Activity context,Cursor events){
        this.context = context;
        events.moveToFirst();
        array = events;
        array.moveToFirst();
    }

    @Override
    public MySmallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.thumbview, parent, false);
        return new MySmallViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MySmallViewHolder holder, final int position) {

        holder.name.setText(array.getString(1));
        holder.venue.setText(array.getString(2));
        holder.type.setText(array.getString(3));
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.crowdsquare);
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(),bitmap);
        drawable.setCornerRadius(50.0f);
        drawable.setAntiAlias(true);
        holder.poster.setImageDrawable(drawable);

        holder.thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context,Detailed.class).putExtra("e_id",array.getString(0)));
            }
        });

        array.moveToNext();
    }

    @Override
    public int getItemCount() {
        return array.getCount();
    }

}
