package br.com.secompufscar.secomp_ufscar;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.secompufscar.secomp_ufscar.data.DatabaseHandler;
import br.com.secompufscar.secomp_ufscar.data.Pessoa;
import br.com.secompufscar.secomp_ufscar.utilities.NetworkUtils;

public class AtividadeDetalhes extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    public static final String EXTRA_POSITION = "position";
    ImageView imageTeste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_atividade_detalhes);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set Collapsing Toolbar layout to the screen
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        // Set title of Detail page
         collapsingToolbar.setTitle(getString(R.string.app_name));

        int postion = getIntent().getIntExtra(EXTRA_POSITION, 0);


        Resources resources = getResources();
        String[] places = resources.getStringArray(R.array.places);
        collapsingToolbar.setTitle(places[postion % places.length]);

        String[] placeDetails = resources.getStringArray(R.array.place_details);
        TextView placeDetail = (TextView) findViewById(R.id.place_detail);
        placeDetail.setText(placeDetails[postion % placeDetails.length]);

        String[] placeLocations = resources.getStringArray(R.array.place_locations);
        TextView placeLocation =  (TextView) findViewById(R.id.place_location);
        placeLocation.setText(placeLocations[postion % placeLocations.length]);

        TypedArray placePictures = resources.obtainTypedArray(R.array.places_picture);
        ImageView placePicutre = (ImageView) findViewById(R.id.image);
        placePicutre.setImageDrawable(placePictures.getDrawable(postion % placePictures.length()));

        placePictures.recycle();
//        new setFotoTask().execute("https://secompufscar.com.br/media/images/secompufscar2017/equipe/felipe_sampaio_de_souza.jpg");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    // TODO: Retirar esse trecho de código, ele está aqui apenas para teste
    private class setFotoTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            Pessoa pessoa1 = new Pessoa();
            Pessoa pessoa2 = new Pessoa();
            pessoa1.setId(1);
            pessoa1.setNome("Felipe");
            try {

                pessoa1.setFoto(NetworkUtils.getImageFromHttpUrl(params[0]));
                DatabaseHandler.getDB().addPessoa(pessoa1);
                pessoa2 = DatabaseHandler.getDB().getPessoa(1);

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return pessoa2.getFotoBitmap();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageTeste.setImageBitmap(result);
        }
    }
}
