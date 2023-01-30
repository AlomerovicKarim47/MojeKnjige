package ba.unsa.etf.rma.karim_alomerovic.knjige;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DodavanjeKnjigeAkt extends AppCompatActivity {
    public static int idSlikeGenerator = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        idSlikeGenerator++;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodavanje_knjige_akt);

        final EditText nazivKnjige = (EditText)findViewById(R.id.nazivKnjige);
        final EditText imeAutora = (EditText)findViewById(R.id.imeAutora);
        final Spinner sKategorijaKnjige = (Spinner)findViewById(R.id.sKategorijaKnjige);
        Button dPonisti = (Button)findViewById(R.id.dPonisti);
        Button dUpisiKnjigu = (Button)findViewById(R.id.dUpisiKnjigu);
        Button dNadjiSliku = (Button)findViewById(R.id.dNadjiSliku);
        final ImageView naslovnaStr = (ImageView)findViewById(R.id.naslovnaStr);

        Intent i = getIntent();

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, i.getStringArrayListExtra("kategorije"));

        sKategorijaKnjige.setAdapter(adapterSpinner);

        //KLIK NA UPISI KNJIGU
        dUpisiKnjigu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(nazivKnjige.getText()) || TextUtils.isEmpty(imeAutora.getText()))
                    Toast.makeText(view.getContext(), "Molimo unesite tražene podatke.", Toast.LENGTH_SHORT).show();
                else {
                    Knjiga knjiga = new Knjiga(nazivKnjige.getText().toString(), imeAutora.getText().toString(), sKategorijaKnjige.getSelectedItem().toString());
                    knjiga.setOfflineSlika("slika" + idSlikeGenerator);
                    Intent i = new Intent();
                    i.putExtra("knjiga", knjiga);
                    setResult(RESULT_OK, i);
                    finish();
                }
            }
        });

        //KLIK NA PONISTI
        dPonisti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        //KLIK NA ODABERI SLIKU
        dNadjiSliku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("image/*");
            i.addCategory(Intent.CATEGORY_OPENABLE);
            if (i.resolveActivity(getPackageManager()) != null)
                startActivityForResult(i, 1);
            else
                Toast.makeText(view.getContext(), "Nemoguće izvršiti aktivnost", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1)
        {
            if (resultCode == RESULT_OK && data != null && data.getData() != null)
            {
                    ImageView naslovnaStr = (ImageView) findViewById(R.id.naslovnaStr);
                    Uri uri = data.getData();

                    EditText nazivKnjige = (EditText)findViewById(R.id.nazivKnjige);

                    Bitmap slika = null;
                    FileOutputStream outputStream;
                    try {
                        slika = getBitmapFromUri(data.getData());
                        outputStream = openFileOutput("slika" + idSlikeGenerator, Context.MODE_PRIVATE);
                        slika.compress(Bitmap.CompressFormat.JPEG,90,outputStream);
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (slika != null)
                        naslovnaStr.setImageBitmap(slika);

            }
        }
    }

}
