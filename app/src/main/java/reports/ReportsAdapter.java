package reports;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.neireport.ireport.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import de.hdodenhof.circleimageview.CircleImageView;


public class ReportsAdapter extends FirestoreRecyclerAdapter<Reports, ReportsAdapter.ReportsHolder> {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public ReportsAdapter(@NonNull FirestoreRecyclerOptions<Reports> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ReportsAdapter.ReportsHolder holder, int position, @NonNull Reports model) {
        holder.text_date.setText(getFormatPostDate(model));
        String description = model.getDescription();
        if (!description.isEmpty()) {
            holder.text_description.setVisibility(View.VISIBLE);
            holder.text_description.setText(model.getDescription());
        }
        String imageURL = model.getImageURL();
        Picasso.get().load(imageURL).into(holder.image_post);
        final String userID = model.getUserID();

        firestore.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    DocumentSnapshot document = task.getResult();
                    String url_userProfileImage = document.getString("Profile Image Link");
                    String field_userName = document.getString("Name");
                    holder.text_username.setText(field_userName);
                    Picasso.get().load(url_userProfileImage).into(holder.image_user);
                } else {
                    firestore.collection("Admin Accounts").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                String url_userProfileImage = document.getString("Profile Image Link");
                                String field_userName = document.getString("Name");
                                holder.text_username.setText(field_userName);
                                Picasso.get().load(url_userProfileImage).into(holder.image_user);
                            }
                        }
                    });
                }
            }
        });
        holder.text_location.setText(model.getLocation());
    }

    @NonNull
    @Override
    public ReportsAdapter.ReportsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_reports,
                parent, false);
        return new ReportsAdapter.ReportsHolder(v);
    }

    private String getFormatPostDate(Reports reports) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy hh:mm a");
        String date = simpleDateFormat.format(reports.getTimestamp().toDate());
        return date;
    }

    class ReportsHolder extends RecyclerView.ViewHolder {

        CardView cardView_post;
        TextView text_description, text_date, text_location, text_username;
        ImageView image_post;
        CircleImageView image_user;

        public ReportsHolder(View itemView) {
            super(itemView);
            cardView_post = itemView.findViewById(R.id.main_post);
            text_description = itemView.findViewById(R.id.text_description);
            image_post = itemView.findViewById(R.id.image_post);
            text_date = itemView.findViewById(R.id.text_date);
            image_user = itemView.findViewById(R.id.image_user);
            text_location = itemView.findViewById(R.id.text_location);
            text_username = itemView.findViewById(R.id.text_username);
        }
    }
}

