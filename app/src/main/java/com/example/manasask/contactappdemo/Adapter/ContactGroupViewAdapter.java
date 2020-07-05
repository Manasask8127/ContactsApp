package com.example.manasask.contactappdemo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manasask.contactappdemo.ContactGroupActivity;
import com.example.manasask.contactappdemo.ContactHomeActivity;
import com.example.manasask.contactappdemo.R;
import com.example.manasask.contactappdemo.data.MySQLiteHelper;

import java.util.List;

public class ContactGroupViewAdapter extends RecyclerView.Adapter<ContactGroupViewAdapter.ViewHolder>  {

    String groupList[];
    int array[];
    ContactGroupActivity contactGroupActivity;
    Context context;
    MySQLiteHelper mySQLiteHelper;
    public ContactGroupViewAdapter(String groupList[],Context context)
    {
        this.groupList=groupList;
        //this.array=arr;
        this.context=context;
        contactGroupActivity=(ContactGroupActivity) context;
    }
    @NonNull
    @Override
    public ContactGroupViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_view, parent, false);
        ContactGroupViewAdapter.ViewHolder holder = new ContactGroupViewAdapter.ViewHolder(view,contactGroupActivity);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position) {

        holder.group_name.setText(groupList[position]);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                //Toast.makeText(view.getContext(),"Groupr",Toast.LENGTH_LONG).show();
                //ContactHomeActivity.fab.setVisibility(View.INVISIBLE);
               Intent intent = new Intent(context.getApplicationContext(), ContactHomeActivity.class);
               intent.putExtra("group_name", groupList[position]);
               context.startActivity(intent);
            }
        });
        if(!ContactGroupActivity.iscontextmode)
        {
            holder.checkBox.setVisibility(View.GONE);
            holder.checkBox.setChecked(false);
        }
        else{
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return groupList.length;
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView group_image;
        TextView group_name;
        RecyclerView recyclerView;
        RelativeLayout relativeLayout;
        ContactGroupActivity contactGroupActivity;
        CheckBox checkBox;
        public ViewHolder(View itemView,ContactGroupActivity contactGroupActivity) {
            super(itemView);
            group_image= itemView.findViewById(R.id.group_image);
            group_name=itemView.findViewById(R.id.group_name);
            relativeLayout=itemView.findViewById(R.id.relative_layout_group);
            recyclerView=itemView.findViewById(R.id.contact_home_recycler_view_group);
            checkBox=itemView.findViewById(R.id.checkbox_group);
            this.contactGroupActivity=contactGroupActivity;
            relativeLayout.setOnLongClickListener((View.OnLongClickListener) contactGroupActivity);
            checkBox.setOnClickListener((View.OnClickListener) this);
        }

        @Override
        public void onClick(View view) {
            contactGroupActivity.MakeSelection(view,getAdapterPosition());
        }
    }


}
