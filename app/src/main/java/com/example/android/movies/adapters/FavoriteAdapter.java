package com.example.android.movies.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.movies.ItemClickListener;
import com.example.android.movies.R;
import com.example.android.movies.UI.DetailActivity;
import com.example.android.movies.data.Movie;
import com.example.android.movies.data.MoviesContract;
import com.squareup.picasso.Picasso;

/**
 * Created by CASA on 02/12/2017.
 */

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    // Class variables for the Cursor that holds task data and the Context
    private Cursor mCursor;
    private Context mContext;



    public FavoriteAdapter(Context mContext, Cursor mCursor) {
        this.mContext = mContext;
        this.mCursor = mCursor;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    @Override
    public FavoriteAdapter.FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext).inflate(R.layout.model_film, parent, false);

        return new FavoriteViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(final FavoriteViewHolder holder, int position) {

        final Movie movie = populateMovie(mCursor, position, holder);
        holder.setMovie(movie);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent i = new Intent(mContext, DetailActivity.class);
                i.putExtra("movie", movie);
                mContext.startActivity(i);
            }
        });


    }

    Movie populateMovie(Cursor cursor, int position, FavoriteViewHolder holder) {
        try {
            cursor.moveToPosition(position);
            int idMovie = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_ID_MOVIE);
            int titleIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_NAME_MOVIE);
            int voteIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_VOTE_MOVIE);
            int imageIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_IMAGE_MOVIE);
            int imageBackgroundIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_IMAGE_BACKGROUND_MOVIE);
            int synopsisIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_SYNOPSIS_MOVIE);
            int releaseIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RELEASE_MOVIE);


            // Determine the values of the wanted data
            int id = cursor.getInt(idMovie);
            String mTitle = cursor.getString(titleIndex);
            double mVote = cursor.getDouble(voteIndex);
            String mImage = cursor.getString(imageIndex);
            String mImageBack = cursor.getString(imageBackgroundIndex);
            String mSynopsis = cursor.getString(synopsisIndex);
            String mRelease = cursor.getString(releaseIndex);

            Picasso.with(mContext).load(mImage).into(holder.thumbnailFilm);

            return new Movie(id, mVote, mTitle, mImage, mImageBack, mSynopsis, mRelease);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    // Inner class for creating ViewHolders
    public class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iconRemove;
        ImageView thumbnailFilm;
        Movie movie;

        ItemClickListener itemClickListener;

        public FavoriteViewHolder(View itemView) {
            super(itemView);
            thumbnailFilm = (ImageView) itemView.findViewById(R.id.thumbnail_film);
            iconRemove = (ImageView) itemView.findViewById(R.id.icon_remove);
            iconRemove.setVisibility(View.VISIBLE);

            iconRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Confirma a exclusão?");
                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int idMovie = movie.getMovieId();
                            String stringId = Integer.toString(idMovie);
                            Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;
                            uri = uri.buildUpon().appendPath(stringId).build();

                            DatabaseUtils.dumpCursor(mCursor);

                            mContext.getContentResolver().delete(uri, MoviesContract.MoviesEntry.COLUMN_ID_MOVIE, new String[]{String.valueOf(idMovie)});

                            Toast.makeText(mContext, "Filme " + movie.getOriginalTitle().toString() + " deletado com sucesso!", Toast.LENGTH_LONG).show();

                        }
                    });
                    builder.setNegativeButton("Não", null);
                    AlertDialog dialog = builder.create();
                    dialog.setTitle("Confirmar operação");
                    dialog.show();
                }
            });


            itemView.setOnClickListener(this);
        }

        public void setMovie(final Movie movie) {
            this.movie = movie;
        }

        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClick(this.getLayoutPosition());
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

    }
}

