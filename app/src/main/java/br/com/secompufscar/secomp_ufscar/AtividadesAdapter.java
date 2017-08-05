package br.com.secompufscar.secomp_ufscar;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.secompufscar.secomp_ufscar.data.Atividade;

public class AtividadesAdapter extends RecyclerView.Adapter<AtividadesAdapter.MyViewHolder> {

    private List<Atividade> atividadeList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nome, local, tipo;

        public MyViewHolder(View view) {
            super(view);
            nome = (TextView) view.findViewById(R.id.atividade_nome);
            local = (TextView) view.findViewById(R.id.atividade_local);
            tipo = (TextView) view.findViewById(R.id.atividade_tipo);
        }
    }


    public AtividadesAdapter(List<Atividade> atividadeList) {
        this.atividadeList = atividadeList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.atividade_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Atividade atividade = atividadeList.get(position);
        // TODO: Arrumar o layout dos itens e definir o que irá ter
        holder.nome.setText(atividade.getTitulo());
        holder.local.setText(atividade.getLocal());
        holder.tipo.setText(atividade.getTipo());
    }

    @Override
    public int getItemCount() {
        return atividadeList.size();
    }
}