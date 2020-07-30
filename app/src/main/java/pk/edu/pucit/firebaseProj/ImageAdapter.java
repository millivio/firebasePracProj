package pk.edu.pucit.firebaseProj;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<Upload> mUploads;
    private OnItemClickListener mListener;
    public ImageAdapter(Context context, List<Upload>  uploads)
    {
        mContext=context;
        mUploads=uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.image_view, parent,false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            Upload uploadCurrent = mUploads.get(position);
            holder.textViewName.setText(uploadCurrent.getName());
            holder.textViewEmail.setText(uploadCurrent.getEmail());
        Picasso.with(mContext)
                .load(uploadCurrent.getImageUri())
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        public TextView textViewName,textViewEmail;
        public ImageView imageView;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName= itemView.findViewById(R.id.name);
            textViewEmail= itemView.findViewById(R.id.email);
            imageView= itemView.findViewById(R.id.image);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mListener!=null)
            {
                int position= getAdapterPosition();
                if(position != RecyclerView.NO_POSITION)
                {
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

                MenuItem deleteItem= menu.add(Menu.NONE,1,1, "Delete");
                deleteItem.setOnMenuItemClickListener(this);

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(mListener!=null)
            {
                int position= getAdapterPosition();
                if(position != RecyclerView.NO_POSITION)
                {
                    switch (item.getItemId()) {
                        case 1:
                            mListener.OnDeleteClick(position);
                                return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void OnDeleteClick(int position);

        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener= listener;
    }
}
