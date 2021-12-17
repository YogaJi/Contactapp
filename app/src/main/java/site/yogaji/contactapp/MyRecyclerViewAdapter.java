package site.yogaji.contactapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import site.yogaji.contactapp.model.Contact;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<Contact> contactArrayList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //set id
        ImageView avatarIv ;
        TextView nameTv;
        TextView telephoneTv;
        TextView addressTv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.avatar_iv);
            nameTv = itemView.findViewById(R.id.name_tv);
            telephoneTv = itemView.findViewById(R.id.telephone_tv);
            addressTv = itemView.findViewById(R.id.address_tv);
        }
    }
    //set recycler view adapter array list
    public MyRecyclerViewAdapter(ArrayList<Contact> contacts) {
        contactArrayList = contacts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_view_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Contact contact = contactArrayList.get(position);
        holder.avatarIv.setImageResource(contact.getAvatar());
        holder.nameTv.setText(contact.getName());
        holder.telephoneTv.setText(contact.getTelephone());
        holder.addressTv.setText(contact.getAddress());
    }

    @Override
    public int getItemCount() {
        return contactArrayList.size();
    }
}
