package ba.unsa.etf.rma.karim_alomerovic.knjige;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by User on 11.5.2018..
 */

public class FragmentOnline extends Fragment implements DohvatiKnjige.IDohvatiKnjigeDone, DohvatiNajnovije.IDohvatiNajnovijeDone, MojResultReceiver.Receiver{

    private ArrayList<Knjiga> knjigeRez = new ArrayList<Knjiga>();
    private ArrayList<String> kategorije = new ArrayList<String>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle onInstanceSaveState)
    {
        View vi = inflater.inflate(R.layout.online_fragment, container, false);

        Spinner sKategorije = (Spinner)vi.findViewById(R.id.sKategorije);
        Spinner sRezultat = (Spinner)vi.findViewById(R.id.sRezultat);
        EditText tekstUpit = (EditText)vi.findViewById(R.id.tekstUpit);
        Button dRun = (Button)vi.findViewById(R.id.dRun);
        Button dAdd = (Button)vi.findViewById(R.id.dAdd);
        Button dPovratak = (Button)vi.findViewById(R.id.dPovratak);

        if (getArguments() != null && getArguments().containsKey("kategorije"))
            kategorije = getArguments().getStringArrayList("kategorije");
        sKategorije.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, kategorije));
        //KLIK NA PRETRAGU
        dRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String upit = tekstUpit.getText().toString();
                String[] params = new String[1];
                if (upit.length() >= 9 && upit.substring(0,9).equals("korisnik:"))
                {
                    Intent intent = new Intent(Intent.ACTION_SYNC, null, getActivity(), KnjigePoznanika.class);
                    MojResultReceiver mReceiver = new MojResultReceiver(new Handler());
                    mReceiver.setReceiver(FragmentOnline.this);
                    intent.putExtra("receiver", mReceiver);
                    intent.putExtra("idKorisnika", upit.substring(9));
                    getActivity().startService(intent);
                }
                else if (upit.length() >= 6 && upit.substring(0,6).equals("autor:"))
                {
                    params[0] = upit.substring(6);
                    new DohvatiNajnovije(FragmentOnline.this).execute(params);
                }
                else {
                    params = upit.split(";");
                    new DohvatiKnjige(FragmentOnline.this).execute(params);
                }
            }
        });

        //KLIK NA NAZAD
        dPovratak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack("listaF", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        //KLIK NA ADD
        dAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sRezultat.getCount() == 0)
                    Toast.makeText(getActivity(), "Nije dohvaćena ni jedna knjiga.", Toast.LENGTH_SHORT).show();
                else {
                    Knjiga knjiga = knjigeRez.get(sRezultat.getSelectedItemPosition());
                    knjiga.setKategorija(((Spinner) vi.findViewById(R.id.sKategorije)).getSelectedItem().toString());
                    BazaOpenHelper boh = new BazaOpenHelper(getActivity());
                    boh.dodajKnjigu(knjiga);
                    ListeFragment listeFragment = (ListeFragment) getFragmentManager().findFragmentByTag("listaF");
                    listeFragment.getArguments().putSerializable("knjiga", knjiga);
                    getFragmentManager().popBackStack("listaF", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }
        });

        return vi;
    }

    @Override
    public void onDohvatiDone(ArrayList<Knjiga> rez) {
        preuzmiRezultat(rez);
    }

    @Override
    public void onNajnovijeDone(ArrayList<Knjiga> rez) {
        preuzmiRezultat(rez);
    }

    private void preuzmiRezultat(ArrayList<Knjiga> rez)
    {
        knjigeRez = rez;
        ArrayList<String> nazivi = new ArrayList<String>();
        for(Knjiga k:rez)
            nazivi.add(k.getNaziv());
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, nazivi);
        Spinner sRezultat = (Spinner)getActivity().findViewById(R.id.sRezultat);
        sRezultat.setAdapter(adapterSpinner);
        Toast.makeText(getActivity(), "Pretraga gotova.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch(resultCode)
        {
            case KnjigePoznanika.STATUS_START:

                break;
            case KnjigePoznanika.STATUS_FINISH:
                preuzmiRezultat((ArrayList<Knjiga>) resultData.getSerializable("listaKnjiga"));
                break;
            case KnjigePoznanika.STATUS_ERROR:
                Toast.makeText(getActivity(), "Greška. Pretraga prekinuta.", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
