package ba.unsa.etf.rma.karim_alomerovic.knjige;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;

import static ba.unsa.etf.rma.karim_alomerovic.knjige.BazaOpenHelper.AUTOR_IME;
import static ba.unsa.etf.rma.karim_alomerovic.knjige.BazaOpenHelper.AUTOR_TABLE;
import static ba.unsa.etf.rma.karim_alomerovic.knjige.BazaOpenHelper.KATEGORIJA_ID;
import static ba.unsa.etf.rma.karim_alomerovic.knjige.BazaOpenHelper.KATEGORIJA_NAZIV;
import static ba.unsa.etf.rma.karim_alomerovic.knjige.BazaOpenHelper.KATEGORIJA_TABLE;
import static ba.unsa.etf.rma.karim_alomerovic.knjige.BazaOpenHelper.KNJIGA_BROJ_STRANICA;
import static ba.unsa.etf.rma.karim_alomerovic.knjige.BazaOpenHelper.KNJIGA_DATUM_OBJAVE;
import static ba.unsa.etf.rma.karim_alomerovic.knjige.BazaOpenHelper.KNJIGA_ID;
import static ba.unsa.etf.rma.karim_alomerovic.knjige.BazaOpenHelper.KNJIGA_ID_KATEGORIJE;
import static ba.unsa.etf.rma.karim_alomerovic.knjige.BazaOpenHelper.KNJIGA_NAZIV;
import static ba.unsa.etf.rma.karim_alomerovic.knjige.BazaOpenHelper.KNJIGA_OPIS;
import static ba.unsa.etf.rma.karim_alomerovic.knjige.BazaOpenHelper.KNJIGA_PREGLEDANA;
import static ba.unsa.etf.rma.karim_alomerovic.knjige.BazaOpenHelper.KNJIGA_SLIKA;
import static ba.unsa.etf.rma.karim_alomerovic.knjige.BazaOpenHelper.KNJIGA_TABLE;
import static ba.unsa.etf.rma.karim_alomerovic.knjige.BazaOpenHelper.KNJIGA_WEB_SERVIS;

/**
 * Created by User on 4.4.2018..
 */

public class ListeFragment extends Fragment{

    private static ArrayList<Knjiga> knjige = new ArrayList<Knjiga>();
    private static ArrayList<String> kategorije = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle onInstanceSaveState)
    {
        View vi = inflater.inflate(R.layout.liste_fragment, container, false);
        final EditText tekstPretraga = (EditText)vi.findViewById(R.id.tekstPretraga);
        final Button dDodajKategoriju = (Button)vi.findViewById(R.id.dDodajKategoriju);
        final Button dDodajKnjigu = (Button)vi.findViewById(R.id.dDodajKnjigu);
        final Button dPretraga = (Button)vi.findViewById(R.id.dPretraga);
        final ListView listaKategorija = (ListView)vi.findViewById(R.id.listaKategorija);
        Button dKategorije = (Button)vi.findViewById(R.id.dKategorije);
        Button dAutori = (Button)vi.findViewById(R.id.dAutori);
        Button dOnline = (Button)vi.findViewById(R.id.dDodajOnline);

        if (getArguments() != null) {
            if (getArguments().containsKey("knjige"))
                knjige = (ArrayList<Knjiga>) getArguments().getSerializable("knjige");
            else if (getArguments().containsKey("knjiga"))
                knjige.add((Knjiga) getArguments().getSerializable("knjiga"));
            getArguments().clear();
        }

        //UZMI KATEGORIJE IZ BAZE
        String[] koloneRezultat = new String[]{KATEGORIJA_ID, KATEGORIJA_NAZIV};

        BazaOpenHelper boh = new BazaOpenHelper(getActivity());
        SQLiteDatabase db = boh.getWritableDatabase();

        Cursor cursor = db.query(KATEGORIJA_TABLE, koloneRezultat, null, null, null, null, null);

        int ind = cursor.getColumnIndexOrThrow(KATEGORIJA_NAZIV);
        kategorije.clear();
        while (cursor.moveToNext())
            kategorije.add(cursor.getString(ind));

        final ArrayAdapter<String> adapterKategorije = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, kategorije);
        listaKategorija.setAdapter(adapterKategorije);

        final ArrayList<String> autori = new ArrayList<String>();
        ArrayList<Integer> brojeviKnjiga = new ArrayList<>();

        //LISTA AUTORA
        Cursor c1 = db.query(AUTOR_TABLE, new String[]{AUTOR_IME}, null, null, null, null, null);
        while(c1.moveToNext())
        {
            int n = boh.knjigeAutora(BazaOpenHelper.nadjiIdAutora(c1.getString(c1.getColumnIndexOrThrow(AUTOR_IME)), getActivity())).size();
            brojeviKnjiga.add(n);
            String a = c1.getString(c1.getColumnIndexOrThrow(AUTOR_IME)) + " (" + n + ")";
            autori.add(a);
        }
        final ArrayAdapter<String> adapterAutori = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, autori);

        dDodajKategoriju.setEnabled(false);

        //KLIK NA DODAJ KNJIGU
        dDodajKnjigu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (kategorije.size() == 0)
                    Toast.makeText(getActivity(), getString(R.string.upozorenjekat), Toast.LENGTH_SHORT).show();
                else {
                    DodavanjeKnjigeFragment dodavanjeKnjigeFragment = new DodavanjeKnjigeFragment();
                    Bundle argumenti = new Bundle();
                    argumenti.putStringArrayList("kategorije", kategorije);
                    dodavanjeKnjigeFragment.setArguments(argumenti);
                    ((LinearLayout.LayoutParams)getActivity().findViewById(R.id.mjestoF1).getLayoutParams()).weight = 2;
                    getFragmentManager().beginTransaction().replace(R.id.mjestoF1, dodavanjeKnjigeFragment, "dodavanjeKnjigeF").addToBackStack("listaF").commit();
                }
            }
        });

        //KLIK NA DAUTORI
        dAutori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dPretraga.setVisibility(View.GONE);
                dDodajKategoriju.setVisibility(View.GONE);
                tekstPretraga.setVisibility(View.GONE);
                listaKategorija.setAdapter(adapterAutori);
            }
        });

        //KLIK NA DKATEGORIJE
        dKategorije.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dPretraga.setVisibility(View.VISIBLE);
                dDodajKategoriju.setVisibility(View.VISIBLE);
                tekstPretraga.setVisibility(View.VISIBLE);
                listaKategorija.setAdapter(adapterKategorije);
            }
        });

        //KLIK NA PRETRAGU KATEGORIJA
        dPretraga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                adapterKategorije.getFilter().filter(tekstPretraga.getText().toString(), new Filter.FilterListener() {
                    @Override
                    public void onFilterComplete(int i) {
                        if (i == 0) {
                            if (!TextUtils.isEmpty(tekstPretraga.getText()))
                                dDodajKategoriju.setEnabled(true);
                        }
                        else
                            dDodajKategoriju.setEnabled(false);
                    }
                });
            }
        });

        //KLIK NA DODAJ KATEGORIJU
        dDodajKategoriju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterKategorije.add(tekstPretraga.getText().toString());
                kategorije.add(tekstPretraga.getText().toString());
                BazaOpenHelper boh = new BazaOpenHelper(getActivity());
                boh.dodajKategoriju(tekstPretraga.getText().toString());
                tekstPretraga.setText("");
                dDodajKategoriju.setEnabled(false);
            }
        });

        //KLIK NA AUTORA ILI KATEGORIJU
        listaKategorija.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle argumenti = new Bundle();
                ArrayList<Knjiga> knjigeIzUpita = new ArrayList<>();
                BazaOpenHelper boh = new BazaOpenHelper(getActivity());
                SQLiteDatabase db = boh.getWritableDatabase();
                KnjigeFragment knjigeFragment = new KnjigeFragment();
                if (tekstPretraga.getVisibility() == View.VISIBLE) {
                    //argumenti.putString("kategorija", adapterView.getItemAtPosition(i).toString());
                    knjigeIzUpita = boh.knjigeKategorije(BazaOpenHelper.nadjiIdKategorije(adapterView.getItemAtPosition(i).toString(), getActivity()));
                }
                else if (tekstPretraga.getVisibility() == View.GONE) {
                    //argumenti.putString("autor", adapterView.getItemAtPosition(i).toString().substring(0, adapterView.getItemAtPosition(i).toString().length() - 4));
                    String tekst = adapterView.getItemAtPosition(i).toString();
                    int n = (int) (Math.log10(brojeviKnjiga.get(i)) + 1);
                    knjigeIzUpita = boh.knjigeAutora(BazaOpenHelper.nadjiIdAutora(tekst.substring(0, adapterView.getItemAtPosition(i).toString().length() - 3 - n), getActivity()));
                }
                argumenti.putSerializable("knjige", knjigeIzUpita);
                knjigeFragment.setArguments(argumenti);

                if (getActivity().findViewById(R.id.mjestoF2) != null)
                    getFragmentManager().beginTransaction().replace(R.id.mjestoF2, knjigeFragment).commit();
                else
                    getFragmentManager().beginTransaction().replace(R.id.mjestoF1, knjigeFragment, "knjigeF").addToBackStack("listaF").commit();
            }
        });

        //KLIK NA DODAJ ONLINE
        dOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (kategorije.size() == 0)
                    Toast.makeText(getActivity(), getString(R.string.upozorenjekat), Toast.LENGTH_SHORT).show();
                else {
                    Bundle argumenti = new Bundle();
                    argumenti.putSerializable("knjige", knjige);
                    argumenti.putStringArrayList("kategorije", kategorije);
                    FragmentOnline fragmentOnline = new FragmentOnline();
                    fragmentOnline.setArguments(argumenti);
                    getFragmentManager().beginTransaction().replace(R.id.mjestoF1, fragmentOnline, "onlineF").addToBackStack("listaF").commit();
                }
            }
        });

        return  vi;
    }
}
