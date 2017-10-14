package it.neptis.go.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import it.neptis.go.R;

import it.neptis.go.model.Card;
import it.neptis.go.model.GlideApp;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {

    private List<Card> cardsList;
    private Context context;

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView code, rarity, name, description;
        public ImageView image;

        MyViewHolder(View view) {
            super (view);
            code = (TextView) view.findViewById(R.id.card_code);
            rarity = (TextView) view.findViewById(R.id.rarity_value);
            name = (TextView) view.findViewById(R.id.nome);
            description = (TextView) view.findViewById(R.id.d);
            image = (ImageView) view.findViewById(R.id.card_icon);
        }
    }

    public CardAdapter(Context context, List<Card> cardsList) {
        this.cardsList = cardsList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder (MyViewHolder holder, int position) {
        Card card = cardsList.get(position);
        holder.code.setText(card.getCode());
        holder.rarity.setText(card.getRarity());
        holder.name.setText(card.getName());
        holder.description.setText(card.getDescription());
        GlideApp.with(context).load(cardsList.get(position).getFilepath()).placeholder(R.drawable.progress_animation).error(R.drawable.noimage).into(holder.image);
    }

    @Override
    public int getItemCount(){
        return cardsList.size();
    }
}