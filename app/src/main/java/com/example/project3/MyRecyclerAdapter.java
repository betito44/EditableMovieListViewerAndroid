package com.example.project3;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder>
implements Filterable{

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference movies = database.getReference("movieData");
    ChildEventListener moviesListener;
    private List<movie> movieList;
    private List<movie> movieList_filtered;
    private List<String> keyList;
    private List<String> keyList_filtered;

    private OnListItemClickListener onListItemClickListener = null;   //Call back to the Activity
    public MyRecyclerAdapter(RecyclerView recyclerView)     //Constructor
    {
        movieList = new ArrayList<>();
        keyList = new ArrayList<>();
        moviesListener = movies.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                movie movieItem = new movie(
                        snapshot.child("title").getValue().toString(),
                        snapshot.child("picture").getValue().toString(),
                        snapshot.child("description").getValue().toString(),
                        snapshot.child("length").getValue().toString(),
                        snapshot.child("year").getValue().toString(),
                        snapshot.child("rating").getValue().toString(),
                        snapshot.child("director").getValue().toString(),
                        snapshot.child("stars").getValue().toString());

                movieList.add(movieItem);
                keyList.add(snapshot.getKey());

                MyRecyclerAdapter.this.notifyItemInserted(movieList.size() - 1);
                recyclerView.scrollToPosition(movieList.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                int index = keyList_filtered.indexOf(snapshot.getKey());
                movieList_filtered.remove(index);
                keyList_filtered.remove(index);
                MyRecyclerAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        movieList_filtered = movieList;
        keyList_filtered = keyList;

    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if(charString.isEmpty()) {
                    movieList_filtered = movieList;
                    keyList_filtered = keyList;
                } else {
                    List<movie> filteredList = new ArrayList<>();
                    List<String> filteredKeyList = new ArrayList<>();
                    for(int i = 0; i < movieList.size(); i++){
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if(Float.parseFloat((movieList.get(i).getRating())) >= Float.parseFloat(charString)){
                            filteredList.add(movieList.get(i));
                            filteredKeyList.add(keyList.get(i));
                        }
                    }
                    movieList_filtered = filteredList;
                    keyList_filtered = filteredKeyList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = movieList_filtered;
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                movieList_filtered = (ArrayList<movie>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView year;
        public TextView director;
        public TextView length;
        public ImageView large_image;
        public TextView rating;
        public RatingBar ratingbar;
        public ImageView modifyData;


        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            year = (TextView) view.findViewById(R.id.year);
            director = (TextView) view.findViewById(R.id.director);
            length = (TextView) view.findViewById(R.id.runtime);
            large_image = (ImageView) view.findViewById(R.id.large_image);
            rating = (TextView) view.findViewById(R.id.numericalrating);
            ratingbar = (RatingBar) view.findViewById(R.id.movie_rating);
            modifyData = (ImageView) view.findViewById(R.id.edit);

        }
    }

    public movie getItem(int i){
        return movieList_filtered.get(i);
    }

    public void setOnListItemClickListener(OnListItemClickListener listener){
        onListItemClickListener= listener;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        final ViewHolder view_holder = new ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(onListItemClickListener!=null){
                    onListItemClickListener.onItemClick(v, view_holder.getAdapterPosition());
                }
            }
        });
        return view_holder;

    }



    @Override
    public void onBindViewHolder (ViewHolder holder, @SuppressLint("RecyclerView") final int position){

        final movie u = movieList.get(position);

        holder.title.setText(movieList_filtered.get(position).getName());
        holder.year.setText(movieList_filtered.get(position).getYear());
        Picasso.get().load(movieList_filtered.get(position).getImage()).into(holder.large_image);
        holder.rating.setText(movieList_filtered.get(position).getRating());
        holder.director.setText(movieList_filtered.get(position).getDirector());
        holder.length.setText(movieList_filtered.get(position).getLength());
        holder.ratingbar.setRating(Float.parseFloat(movieList_filtered.get(position).getRating()));


        holder.modifyData.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.extras, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.Delete:
                                movies.child(keyList_filtered.get(position)).removeValue();

                                return true;

                            case R.id.Duplicate:

                                String key = movies.push().getKey();
                                key = key.concat("_duplication");
                                movie movie = movieList_filtered.get(position);

                                Map<String, Object> movieValues = movie.toMap();
                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put(key, movie);
                                movies.updateChildren(childUpdates);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
            }

        });
    }

    @Override
    public int getItemCount() {
        return movieList_filtered.size();
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public void removeListener(){
        if(movies !=null && moviesListener !=null)
            movies.removeEventListener(moviesListener);
    }



}