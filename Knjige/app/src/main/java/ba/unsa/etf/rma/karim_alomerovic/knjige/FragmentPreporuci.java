package ba.unsa.etf.rma.karim_alomerovic.knjige;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.provider.CalendarContract.CalendarCache.URI;

/**
 * Created by User on 23.5.2018..
 */

public class FragmentPreporuci extends Fragment {

    private ArrayList<String> names;
    private ArrayList<String> emails;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle onInstanceSaveState)
    {
        names = new ArrayList<String>();
        emails = new ArrayList<String>();
        Knjiga knjiga = new Knjiga();

        View vi = inflater.inflate(R.layout.preporuci_fragment, container, false);

        Button dPosalji = (Button)vi.findViewById(R.id.dPosalji);
        Spinner sKontakti = (Spinner)vi.findViewById(R.id.sKontakti);
        TextView ePNaziv = (TextView)vi.findViewById(R.id.ePNaziv);
        TextView ePAutor = (TextView)vi.findViewById(R.id.ePAutor);
        TextView ePBrojStranica = (TextView)vi.findViewById(R.id.ePBrojStranica);
        TextView ePDatumObjave = (TextView)vi.findViewById(R.id.ePDatumObjave);
        TextView ePOpis = (TextView)vi.findViewById(R.id.ePOpis);

        if (getArguments() != null)
        {
            if (getArguments().containsKey("knjiga"))
            {
                knjiga = (Knjiga) getArguments().getSerializable("knjiga");
            }
        }

        ePNaziv.setText(knjiga.getNaziv());
        ePAutor.setText(knjiga.getAutor());
        ePBrojStranica.setText("Broj stranica: " + knjiga.getBrojStranica());
        ePDatumObjave.setText("Datum objave: " + knjiga.getDatumObjavljivanja());
        ePOpis.setText("Opis:\n" + knjiga.getOpis());

        getContactInfo();
        sKontakti.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, names));
        String poruka = "Zdravo, " + names.get(sKontakti.getSelectedItemPosition()) + "\nPročitaj knjigu " + knjiga.getNaziv() + " od " + knjiga.getAutor();

        dPosalji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(URI.parse("mailto:"));
                emailIntent.setType("text/plain");
                String[] TO = new String[1];
                TO[0] = emails.get(sKontakti.getSelectedItemPosition());
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_TEXT, poruka);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Preporuka");
                startActivity(emailIntent);
            }
        });

        return vi;
    }

    private void getContactInfo(){
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor cur1 = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                while (cur1.moveToNext()) {
                    //to get the contact names
                    String name = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    names.add(name);
                    String email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    if(email!=null){
                        emails.add(email);
                    }
                }
                cur1.close();
            }
        }
    }

}
