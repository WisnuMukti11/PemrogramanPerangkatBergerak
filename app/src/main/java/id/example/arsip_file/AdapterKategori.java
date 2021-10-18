package id.example.arsip_file;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.example.arsip_file.API.response.ResponseKategori;


public class AdapterKategori extends RecyclerView.Adapter<AdapterKategori.vieHolder>{
    Context context;
    List<ResponseKategori> pilihTeknik = new ArrayList<>();
    ItemClickListener itemClickListener;

    public AdapterKategori(Context context, List<ResponseKategori> pilihTeknik, ItemClickListener itemClickListener) {
        this.context = context;
        this.pilihTeknik = pilihTeknik;
        this.itemClickListener = itemClickListener;

    }
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    @NonNull
    @Override
    public vieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_kat, parent, false);

        return new vieHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final vieHolder holder, final int position) {
        holder.teknik.setText(pilihTeknik.get(position).getKategori());
    }

    @Override
    public int getItemCount() {
        return pilihTeknik.size();
    }

    public class vieHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        TextView teknik;
        ItemClickListener itemClickListener;

        public vieHolder(@NonNull View itemView, ItemClickListener clickListener) {
            super(itemView);

            teknik = (TextView) itemView.findViewById(R.id.text);
            this.itemClickListener = clickListener;
            teknik.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}
