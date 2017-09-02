package br.com.secompufscar.app.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.secompufscar.app.R;
import br.com.secompufscar.app.data.Patrocinador;

public class PatrocinadorAdapter extends RecyclerView.Adapter<PatrocinadorAdapter.SimpleViewHolder> {
    private static final int COUNT = 100;

    private final Context context;
    private final List<Patrocinador> patrocinadores;
    private int currentItemId = 0;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {

        public final ImageView logo;
        public final TextView title;

        public SimpleViewHolder(View view) {
            super(view);
            logo = (ImageView) view.findViewById(R.id.logo_patrocinador);
            title = (TextView) view.findViewById(R.id.patrocinador_nome);
        }
    }

    public PatrocinadorAdapter(Context context, List<Patrocinador> patrocinadores) {
        this.context = context;
        this.patrocinadores = patrocinadores;
    }

    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.patrocinador_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, final int position) {
        holder.logo.setImageBitmap(patrocinadores.get(position).getLogoBitmap(context));
        holder.title.setText(patrocinadores.get(position).getNome());
        if(patrocinadores.get(position).getLogoBitmap(context) == null){
            holder.title.setVisibility(View.VISIBLE);
            holder.logo.setVisibility(View.GONE);
        } else {
            holder.title.setVisibility(View.GONE);
            holder.logo.setVisibility(View.VISIBLE);

        }
    }

    public void addItem(Patrocinador patrocinador) {
        final int id = currentItemId++;
        patrocinadores.add(patrocinador);
        notifyItemInserted(patrocinadores.size());
    }

    public void removeItem(int position) {
        patrocinadores.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return patrocinadores.size();
    }
}
