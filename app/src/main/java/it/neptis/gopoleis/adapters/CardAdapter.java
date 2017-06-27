package it.neptis.gopoleis.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import it.neptis.gopoleis.R;

import it.neptis.gopoleis.defines.Card;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {

    private List<Card> cardsList;

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView code, cost, name, description;

        // TODO add card image

        MyViewHolder(View view) {
            super (view);
            code = (TextView) view.findViewById(R.id.card_code);
            cost = (TextView) view.findViewById(R.id.valore_costo);
            name = (TextView) view.findViewById(R.id.nome);
            description = (TextView) view.findViewById(R.id.d);
        }
    }

    public CardAdapter(List<Card> cardsList) {
        this.cardsList = cardsList;
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
        holder.cost.setText(card.getCost());
        holder.name.setText(card.getName());
        holder.description.setText(card.getDescription());
    }

    @Override
    public int getItemCount(){
        return cardsList.size();
    }
}
