package br.com.secompufscar.secomp_ufscar.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.secompufscar.secomp_ufscar.R;
import br.com.secompufscar.secomp_ufscar.data.Pessoa;


public class PessoasAdapter extends RecyclerView.Adapter<PessoasAdapter.MyViewHolder> {

    private List<Pessoa> pessoaList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nome, empresa;
        public ImageView foto;

        public MyViewHolder(View view) {
            super(view);
            foto = (ImageView) view.findViewById(R.id.pessoa_foto);
            nome = (TextView) view.findViewById(R.id.pessoa_nome);
            empresa = (TextView) view.findViewById(R.id.pessoa_empresa);
        }
    }


    public PessoasAdapter(Context context, List<Pessoa> pessoaList) {
        this.pessoaList = pessoaList;
        this.context = context;
    }

    @Override
    public PessoasAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pessoa_list_row, parent, false);

        return new PessoasAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PessoasAdapter.MyViewHolder holder, int position) {
        Pessoa pessoa = pessoaList.get(position);


        holder.foto.setImageBitmap(pessoa.getFotoBitmap(context));

        holder.nome.setText(pessoa.getNomeCompleto());
        holder.empresa.setText(pessoa.getEmpresa());

    }

    @Override
    public int getItemCount() {
        return pessoaList.size();
    }
}

