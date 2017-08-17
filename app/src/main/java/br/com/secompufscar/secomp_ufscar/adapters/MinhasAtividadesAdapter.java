package br.com.secompufscar.secomp_ufscar.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import br.com.secompufscar.secomp_ufscar.R;
import br.com.secompufscar.secomp_ufscar.data.Atividade;

public class MinhasAtividadesAdapter extends RecyclerView.Adapter<MinhasAtividadesAdapter.MyViewHolder> {

    private List<Atividade> atividadeList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nome, local, tipo, horario;
        private LinearLayout layout_tipo;

        public MyViewHolder(View view) {
            super(view);
            nome = (TextView) view.findViewById(R.id.atividade_nome);
            local = (TextView) view.findViewById(R.id.atividade_local);
            tipo = (TextView) view.findViewById(R.id.atividade_tipo);
            horario = (TextView) view.findViewById(R.id.atividade_horario_inicial);
            layout_tipo = (LinearLayout) view.findViewById(R.id.layout_tipo);
        }
    }


    public MinhasAtividadesAdapter(Context context, List<Atividade> atividadeList) {
        this.atividadeList = atividadeList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.atividade_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            Atividade atividade = atividadeList.get(position);
            holder.nome.setText(atividade.getTitulo());

            String local_atividade = atividade.getPredio() != null ? atividade.getLocal() : context.getResources().getString(R.string.atividade_indisponivel_local);

            holder.local.setText(local_atividade);

            holder.tipo.setText(atividade.getTipo());
            holder.horario.setText(atividade.getHorarioInicialDiaDaSemana());

            holder.layout_tipo.setBackgroundColor(atividade.getColor(context));
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return atividadeList.size();
    }
}