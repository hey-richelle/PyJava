package com.sys.system.Class;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sys.system.Activity.private_chat;
import com.sys.system.R;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<Message3> {
    private List<Message3> memberList;
    private LayoutInflater inflater;
    private Context context; // Need to store the context for starting the private chat intent
    public CustomAdapter(Activity activity, List<Message3> data) {
        super(activity, R.layout.list_item_layout, data);
        this.memberList = data;
        this.inflater = LayoutInflater.from(activity);
        this.context = activity;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_layout, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Message3 message = getItem(position);
        holder.fullNameTextView.setText(message.getName());
        // Load and display the image using Picasso (if you have the imageUrl)
        if (message.getImageUrl() != null && !message.getImageUrl().isEmpty()) {
            Picasso.get()
                    .load(message.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.ic_baseline_person_24)
                    .fit()
                    .centerCrop()
                    .into(holder.profileImageView);
        } else {
            holder.profileImageView.setImageResource(R.drawable.placeholder_image);
        }

        // Set click listener for the button to open the private chat with the user
        convertView.setOnClickListener(v -> {
            // Get the corresponding Message3 object
            Message3 selectedItem = memberList.get(position);

            // Check if the user is an admin or DBMember
            if (selectedItem.getEmail() != null) {
                // If an admin, open the private chat using email
                openPrivateChat(selectedItem.getEmail());
            } else if (selectedItem.getProfession() != null) {
                // If a DBMember, open the private chat using profession
                openPrivateChat(selectedItem.getProfession());
            }
        });

        return convertView;
    }

    public void updateList(List<Message3> newList) {
        clear();
        addAll(newList);
        notifyDataSetChanged();
    }

    private void openPrivateChat(String identifier) {
        // Replace "YourPrivateChatActivity" with the activity you want to open for private chat
        // Here, we are assuming you have an activity that handles private chats
        Intent intent = new Intent(context, private_chat.class);
        intent.putExtra("chat_identifier", identifier);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(0, 0); // Disable animation
        ((Activity) context).finish();
    }


    private static class ViewHolder {
        TextView fullNameTextView;
        ImageView profileImageView;

        ViewHolder(View view) {
            fullNameTextView = view.findViewById(R.id.fullNameTextView);
            profileImageView = view.findViewById(R.id.profileImageView);
        }
    }
}
