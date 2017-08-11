package br.com.secompufscar.secomp_ufscar.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import br.com.secompufscar.secomp_ufscar.R;
import br.com.secompufscar.secomp_ufscar.data.Pessoa;

public class MinistrantesAdapter extends RecyclerView.Adapter<MinistrantesAdapter.MyViewHolder> {

    private List<Pessoa> pessoaList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView foto;

        public MyViewHolder(View view) {
            super(view);
            foto = (ImageView) view.findViewById(R.id.foto_ministrante);
        }
    }


    public MinistrantesAdapter(Context context, List<Pessoa> pessoaList) {
        this.pessoaList = pessoaList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ministrante_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            Pessoa ministrante = pessoaList.get(position);
            holder.foto.setImageBitmap(ministrante.getFotoBitmap(context));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return pessoaList.size();
    }
}