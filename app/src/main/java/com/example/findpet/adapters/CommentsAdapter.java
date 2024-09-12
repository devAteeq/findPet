package com.example.findpet.adapters;

import static com.example.findpet.utils.Constants.ERRORS;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findpet.R;
import com.example.findpet.interfaces.CommentDelete;
import com.example.findpet.models.CommentsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.Holder> {

    List<CommentsModel> list;
    Context context;
    CommentDelete commentDelete;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = auth.getCurrentUser();

    public CommentsAdapter(List<CommentsModel> list, Context context, CommentDelete commentDelete) {
        this.list = list;
        this.context = context;
        this.commentDelete = commentDelete;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.comments_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        CommentsModel commentsModel = list.get(position);
        try {
            Picasso.get().load(commentsModel.getImageUrl()).placeholder(R.color.light_gray).into(holder.imgYourComment);
        } catch (Exception e) {
            Log.d(ERRORS, "onBindViewHolder: " + e.getMessage());
        }
        holder.commentYourComment.setText(commentsModel.getComment());
        holder.commentYourName.setText(commentsModel.getName());

        if (commentsModel.getuId().equals(firebaseUser.getUid())) {
            holder.imgMenu.setVisibility(View.VISIBLE);
        } else {
            holder.imgMenu.setVisibility(View.GONE);
        }

        holder.imgMenu.setOnClickListener(v -> {
            //  delete comment
            PopupMenu popupMenu = new PopupMenu(context, holder.imgMenu);
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete");

            if (commentsModel.getuId().equals(firebaseUser.getUid())) {
                popupMenu.setOnMenuItemClickListener(item -> {

                    commentDelete.deleteComment(commentsModel.getKey());
                    return true;
                });
                popupMenu.show();

            }

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        CircleImageView imgYourComment;
        ImageView imgMenu;
        TextView commentYourName, commentYourComment;

        public Holder(@NonNull View itemView) {
            super(itemView);
            imgYourComment = itemView.findViewById(R.id.imgYourComment);
            commentYourName = itemView.findViewById(R.id.commentYourName);
            commentYourComment = itemView.findViewById(R.id.commentYourComment);
            imgMenu = itemView.findViewById(R.id.imgMenu);
        }
    }
}
