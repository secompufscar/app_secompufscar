package br.com.secompufscar.secomp_ufscar;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import br.com.secompufscar.secomp_ufscar.data.Pessoa;

public class ContatoAdapter extends RecyclerView.Adapter<ContatoAdapter.MyViewHolder> {

    private List<Pessoa.Contato> contatoList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView tipo_contato_image;

        public MyViewHolder(View view) {
            super(view);
            tipo_contato_image = (ImageView) view.findViewById(R.id.contato_tipo_image);
        }
    }


    public ContatoAdapter(Context context, List<Pessoa.Contato> contatoList) {
        this.contatoList = contatoList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contato_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            Pessoa.Contato contato = contatoList.get(position);

            switch (contato.getTipo().toLowerCase()) {
                case "facebook":
                    holder.tipo_contato_image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_facebook));
                    break;
                case "twitter":
                    holder.tipo_contato_image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_twitter));
                    break;
                case "linkedin":
                    holder.tipo_contato_image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_linkedin));
                    break;
                case "github":
                    holder.tipo_contato_image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_github));
                    break;
                default:
                    holder.tipo_contato_image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_outro));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return contatoList.size();
    }
}