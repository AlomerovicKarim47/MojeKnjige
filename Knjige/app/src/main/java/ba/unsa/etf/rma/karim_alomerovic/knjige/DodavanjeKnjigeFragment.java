package ba.unsa.etf.rma.karim_alomerovic.knjige;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

//SLIKE!!!!!
public class DodavanjeKnjigeFragment extends Fragment {
    View vi;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle onInstanceSaveState)
    {
        vi = inflater.inflate(R.layout.dodavanje_knjige_fragment, container, false);
        KategorijeAkt.idSlikeGenerator++;
        final EditText nazivKnjige = (EditText)vi.findViewById(R.id.nazivKnjige);
        final EditText imeAutora = (EditText)vi.findViewById(R.id.imeAutora);
        final Spinner sKategorijaKnjige = (Spinner)vi.findViewById(R.id.sKategorijaKnjige);
        Button dPonisti = (Button)vi.findViewById(R.id.dPonisti);
        Button dUpisiKnjigu = (Button)vi.findViewById(R.id.dUpisiKnjigu);
        Button dNadjiSliku = (Button)vi.findViewById(R.id.dNadjiSliku);
        final ImageView naslovnaStr = (ImageView)vi.findViewById(R.id.naslovnaStr);

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, (ArrayList<String>)getArguments().getStringArrayList("kategorije"));

        sKategorijaKnjige.setAdapter(adapterSpinner);

        //KLIK NA PONISTI
        dPonisti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LinearLayout.LayoutParams)getActivity().findViewById(R.id.mjestoF1).getLayoutParams()).weight = 1;
                getFragmentManager().popBackStack("listaF", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        //KLIK NA UPISI KNJIGU
        dUpisiKnjigu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(nazivKnjige.getText()) || TextUtils.isEmpty(imeAutora.getText()))
                    Toast.makeText(view.getContext(), getString(R.string.upozorenjepodaci), Toast.LENGTH_SHORT).show();
                else {
                    Knjiga knjiga = new Knjiga(nazivKnjige.getText().toString(), imeAutora.getText().toString(), sKategorijaKnjige.getSelectedItem().toString());
                    knjiga.setOfflineSlika("slika"+KategorijeAkt.idSlikeGenerator);
                    Bundle argumenti = new Bundle();
                    ListeFragment listeFragment = (ListeFragment) getFragmentManager().findFragmentByTag("listaF");
                    listeFragment.getArguments().putSerializable("knjiga", knjiga);
                    BazaOpenHelper boh = new BazaOpenHelper(getActivity());
                    boh.dodajKnjigu(knjiga);
                    ((LinearLayout.LayoutParams)getActivity().findViewById(R.id.mjestoF1).getLayoutParams()).weight = 1;
                    getFragmentManager().popBackStack("listaF", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }
        });

        //KLIK NA ODABERI SLIKU
        dNadjiSliku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                i.addCategory(Intent.CATEGORY_OPENABLE);
                if (i.resolveActivity(getActivity().getPackageManager()) != null)
                    startActivityForResult(i, 1);
                else
                    Toast.makeText(view.getContext(), "Nemoguće izvršiti aktivnost", Toast.LENGTH_SHORT).show();

            }
        });

        return  vi;
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getActivity().getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1)
        {
            if (resultCode == getActivity().RESULT_OK && data != null && data.getData() != null)
            {
                ImageView naslovnaStr = (ImageView)vi.findViewById(R.id.naslovnaStr);
                Uri uri = data.getData();

                EditText nazivKnjige = (EditText)vi.findViewById(R.id.nazivKnjige);

                Bitmap slika = null;
                FileOutputStream outputStream;
                try {
                    slika = getBitmapFromUri(data.getData());
                    outputStream = getActivity().openFileOutput("slika" + KategorijeAkt.idSlikeGenerator, Context.MODE_PRIVATE);
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
