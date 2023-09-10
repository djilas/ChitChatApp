package com.example.chitchatapplication.messages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chitchatapplication.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


//This is Message Adapter... Not sure if this is right approach
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    private ArrayList<Message> messages;
    private String senderImg, reciverImg;
    private Context context;

    public MessageAdapter(ArrayList<Message> messages, String senderImg, String reciverImg, Context context){
        this.messages = messages;
        this.senderImg = senderImg;
        this.reciverImg = reciverImg;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageAdapter.MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_holder, parent, false);

        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageHolder holder, int position) {

        // !!!!! For later try to rotate card
        //ConstraintSet constraintSet = new ConstraintSet();
        //constraintSet.clone(constraintLayout);
       //constraintSet.clear(R.id.img_pro, constraintSet.LEFT);
        //constraintSet.clear(R.id.lastMessage, constraintSet.LEFT);
        //constraintSet.connect(R.id.img_pro, ConstraintSet.RIGHT, R.id.ccll, ConstraintSet.RIGHT, 0); //basically constraintRight = parent
        //constraintSet.connect(R.id.lastMessage, ConstraintSet.RIGHT, R.id.img_pro, ConstraintSet.LEFT, 0); //basically constraintRight = parent
        // constraintSet.applyTo(constraintLayout);

        holder.textContext.setText(messages.get(position).getContent());

        if(messages.get(position).getReciever().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
            Glide.with(context).load(senderImg).error(R.drawable.account_image).placeholder(R.drawable.account_image).into(holder.imageView);
            holder.user.setText(messages.get(position).getSender());
        }else{
            Glide.with(context).load(reciverImg).error(R.drawable.account_image).placeholder(R.drawable.account_image).into(holder.imageView);
            holder.user.setText(messages.get(position).getSender());

        }
        //holder.user.setText(messages.get(position).getReciever());
        //Glide.with(context).load(reciverImg).error(R.drawable.account_image).placeholder(R.drawable.account_image).into(holder.imageView);

//        if(messages.get(position).getReciever().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
//            holder.textContext.setText(messages.get(position).getContent());
//            holder.user.setText(messages.get(position).getReciever());
//            Glide.with(context).load(reciverImg).error(R.drawable.account_image).placeholder(R.drawable.account_image).into(holder.imageView);
//        }else{
//            holder.textContext.setText(messages.get(position).getContent());
//            holder.user.setText(messages.get(position).getSender());
//            Glide.with(context).load(senderImg).error(R.drawable.account_image).placeholder(R.drawable.account_image).into(holder.imageView);
//        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MessageHolder extends RecyclerView.ViewHolder{
        TextView textContext;
        TextView user;
        ImageView imageView;
        CardView cardV;

        public MessageHolder(@NonNull View itemView){
            super(itemView);

            cardV = itemView.findViewById(R.id.cardView);
            textContext = itemView.findViewById(R.id.lastMessage);
            user = itemView.findViewById(R.id.txtUsername);
            imageView = itemView.findViewById(R.id.img_pro);
        }
    }
}
