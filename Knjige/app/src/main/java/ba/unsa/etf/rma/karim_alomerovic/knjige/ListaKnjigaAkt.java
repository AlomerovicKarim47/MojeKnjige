package ba.unsa.etf.rma.karim_alomerovic.knjige;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;

public class ListaKnjigaAkt extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_knjiga_akt);

        ListView listaKnjiga = (ListView)findViewById(R.id.listaKnjiga);
        listaKnjiga.setDivider(null);
        Button dPovratak = (Button)findViewById(R.id.dPovratak);

        Intent i = getIntent();
        String kategorija = i.getStringExtra("kategorija");
        final ArrayList<Knjiga> sveKnjige = (ArrayList<Knjiga>) i.getSerializableExtra("knjige");

        /*KnjigaAdapter adapterKnjige = new KnjigaAdapter(this, sveKnjige, getResources(), kategorija);
        listaKnjiga.setAdapter(adapterKnjige);

        //KLIK NA NAZAD
        dPovratak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("novaListaKnjiga", sveKnjige);
                setResult(RESULT_OK, i);
                finish();
            }
        });*/
    }
}
