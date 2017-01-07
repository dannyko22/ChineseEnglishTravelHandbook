package com.chineseenglishtravelhandbook;

import android.content.Context;
import android.nfc.Tag;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Danny on 29/08/2016.
 */

public class notepadRecyclerAdapter extends RecyclerView.Adapter<notepadRecyclerAdapter.ViewHolder> {
    private List<NotepadData> mNotes;
    private Context mContext;
    public MyAdapterListener onClickListener;


    public notepadRecyclerAdapter(List<NotepadData> notes, Context context, MyAdapterListener listener){
        mNotes = notes;
        mContext = context;
        onClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notepadrecycler_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(rowView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.noteTitle.setText(mNotes.get(position).getTitle());
        holder.noteBody.setText(mNotes.get(position).getBody());
        holder.noteDate.setText(mNotes.get(position).getModifiedDate());
    }



    @Override
    public int getItemCount() {
        return mNotes.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder  {
        public final TextView noteTitle, noteBody, noteDate;
        Button deleteButton;



        public ViewHolder(View itemView) {
            super(itemView);
            noteTitle = (TextView)itemView.findViewById(R.id.note_titleTextView);
            noteBody = (TextView)itemView.findViewById(R.id.note_bodyTextView);
            noteDate = (TextView) itemView.findViewById(R.id.note_dateTextView);
            deleteButton = (Button) itemView.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onClickListener.buttonViewOnClick(v, getAdapterPosition());
                }
            });
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onClickListener.otherViewOnClick(v, getAdapterPosition());
                }
            });
        }

    }

    public interface MyAdapterListener {

        void buttonViewOnClick(View v, int position);
        void otherViewOnClick(View v, int position);
    }

}
