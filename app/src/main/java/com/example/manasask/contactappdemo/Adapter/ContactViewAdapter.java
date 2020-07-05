package com.example.manasask.contactappdemo.Adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.manasask.contactappdemo.ContactDetailActivity;
import com.example.manasask.contactappdemo.ContactHomeActivity;
import com.example.manasask.contactappdemo.CreateContactActivity;
import com.example.manasask.contactappdemo.R;
import com.example.manasask.contactappdemo.model.UserContact;

import java.util.ArrayList;
import java.util.List;



public class ContactViewAdapter extends RecyclerView.Adapter<ContactViewAdapter.ViewHolder> implements Filterable {
    ArrayList selected;
    ContactHomeActivity contactHomeActivity;
    CreateContactActivity createContactActivity;
    List<UserContact> userContactList,mArrayList;
    Context context;
    private static final String TAG = "ContactViewAdapter";
    private boolean selectAll=false;

    public ContactViewAdapter(List<UserContact> userContactList, Context context) {
        this.userContactList = userContactList;
        this.context = context;
        this.mArrayList=userContactList;
        contactHomeActivity=(ContactHomeActivity)context;
        //createContactActivity=(CreateContactActivity)context;
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_display_view, parent, false);
        ViewHolder holder = new ViewHolder(view,contactHomeActivity,createContactActivity);
        return holder;
    }
   public void selectAll()
   {
        //ContactHomeActivity.iscontextualmodeenabled=true;
        selectAll=true;
        notifyDataSetChanged();
    }
    public void unselectAll(){
        //ContactHomeActivity.iscontextualmodeenabled=false;
        selectAll=false;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder( final ViewHolder holder, final int position)
    {
        /*holder.contact_image.setImageResource(R.drawable.ic_group);
        holder.contact_name.setText("My Groups");
        holder.contact_name.setText(null);*/
        Log.d(TAG, "onBindViewHolder: ");
        byte[] Image = userContactList.get(position).getContactUserImage();
        if(Image!=null)
        {
            Bitmap bitmap = BitmapFactory.decodeByteArray(Image, 0, Image.length);
            holder.contact_image.setImageBitmap(bitmap);
        }
        else{
            //Bitmap bitmap = BitmapFactory.decodeByteArray(Image, 0, Image.length);
            holder.contact_image.setImageResource(R.drawable.cp);
        }
        if(userContactList.get(position).getContactUserLastName()==null)
            holder.contact_name.setText(userContactList.get(position).getContactUserFirstName());
        else
        holder.contact_name.setText(userContactList.get(position).getContactUserFirstName() + " " + userContactList.get(position).getContactUserLastName());
        holder.contact_phn_number.setText(userContactList.get(position).getContactUserPhone());

        selected=new ArrayList();
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), ContactDetailActivity.class);
                intent.putExtra("contactUserID", userContactList.get(position).getContactUserId());
                ContactHomeActivity.iscontextualmodeenabled=false;
                context.startActivity(intent);
            }
        });
        /*if (!selectAll) {
            holder.checkBox.setVisibility(View.GONE);
            holder.checkBox.setChecked(false);
        }
        else {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(true);
        }*/
       if(!ContactHomeActivity.iscontextualmodeenabled)
        {
            holder.checkBox.setVisibility(View.GONE);
            holder.checkBox.setChecked(false);
        }
        else{
            if(selectAll) {
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setChecked(true);
            }
            else
            {
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setChecked(false);
            }
        }
    }

    @Override
    public Filter getFilter()
    {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    userContactList = mArrayList;
                } else {
                    ArrayList<UserContact> filteredList = new ArrayList<>();
                    for (UserContact contacts : mArrayList) {
                        if (contacts.getContactUserFirstName().toLowerCase().contains(charString) || contacts.getContactUserPhone().contains(charSequence)) {

                            filteredList.add(contacts);
                        }
                    }
                    userContactList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = userContactList;
                Log.d("manasa", String.valueOf(filterResults));
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                userContactList = (ArrayList<UserContact>) filterResults.values;
                    notifyDataSetChanged();}

        };
    }









    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: ");
        return userContactList.size();
    }

    /*public void updateAdapter(List<UserContact> list) {
        for (UserContact heyvon : list) {
            userContactList.remove(heyvon);
        }
        notifyDataSetChanged();
    }*/


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        ImageView contact_image;
        TextView contact_name;
        TextView contact_phn_number;
        CheckBox checkBox;
        RecyclerView recyclerView;
        RelativeLayout relativeLayout;
        FloatingActionButton add_contact;
        ContactHomeActivity contactHomeActivity;
        CreateContactActivity createContactActivity;
        public ViewHolder(View itemView,ContactHomeActivity contactHomeActivity,CreateContactActivity createContactActivity)
        {
            super(itemView);
            contact_image=itemView.findViewById(R.id.contact_image);
            contact_name=itemView.findViewById(R.id.contact_name);
            contact_phn_number=itemView.findViewById(R.id.tv_contact_phone_number);
            checkBox=itemView.findViewById(R.id.checkbox);
            recyclerView=itemView.findViewById(R.id.contact_home_recycler_view);
            add_contact=itemView.findViewById(R.id.fab);
            relativeLayout=itemView.findViewById(R.id.relative_layout);
            this.contactHomeActivity=contactHomeActivity;
            this.createContactActivity=createContactActivity;
            relativeLayout.setOnLongClickListener((View.OnLongClickListener) contactHomeActivity);
            checkBox.setOnClickListener(this);
        }



        @Override
        public void onClick(View view)
        {
            contactHomeActivity.MakeSelection(view,getAdapterPosition());
        }
    }

}
