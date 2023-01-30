package ba.unsa.etf.rma.karim_alomerovic.knjige;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by User on 24.3.2018..
 */

public class KnjigaAdapter extends BaseAdapter {

    Activity activity;
    ArrayList data;
    Resources res;
    LayoutInflater inflater;
    String kategorija, autor;

    public KnjigaAdapter(Activity a, ArrayList d, Resources resLocal, String kat, String aut)
    {
        activity = a;
        data = d;
        res = resLocal;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        kategorija = kat;
        autor = aut;
    }

    public static class ViewHolder
    {
        TextView eNaziv, eAutor, eOpis, eDatumObjavljivanja, eBrojStranica;
        ImageView eNaslovna;
        Button dPreporuci;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final Knjiga knjiga = (Knjiga)data.get(i);

        View vi = null;

        ViewHolder holder = new ViewHolder();

        Bitmap bmp;

        vi = inflater.inflate(R.layout.element_lise_knjiga, null);

        holder.eNaziv = (TextView)vi.findViewById(R.id.eNaziv);
        holder.eAutor = (TextView)vi.findViewById(R.id.eAutor);
        holder.eNaslovna = (ImageView)vi.findViewById(R.id.eNaslovna);
        holder.eBrojStranica = (TextView)vi.findViewById(R.id.eBrojStranica);
        holder.eDatumObjavljivanja = (TextView)vi.findViewById(R.id.eDatumObjavljivanja);
        holder.eOpis = (TextView)vi.findViewById(R.id.eOpis);
        holder.dPreporuci = (Button)vi.findViewById(R.id.dPreporuci);
        vi.setTag(holder);

        holder.eNaziv.setText(knjiga.getNaziv());
        holder.eAutor.setText(knjiga.getAutor());
        holder.eBrojStranica.setText("Broj stranica: " + knjiga.getBrojStranica());
        holder.eDatumObjavljivanja.setText("Datum objavljivanja: " + knjiga.getDatumObjavljivanja());
        holder.eOpis.setText("Opis:\n\n" + knjiga.getOpis());

        holder.dPreporuci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentPreporuci fragmentPreporuci = new FragmentPreporuci();
                Bundle argumenti = new Bundle();
                argumenti.putSerializable("knjiga", knjiga);
                fragmentPreporuci.setArguments(argumenti);
                activity.getFragmentManager().beginTransaction().replace(R.id.mjestoF1, fragmentPreporuci).addToBackStack(null).commit();
            }
        });

        try {
            if (knjiga.getOfflineSlika() != null)
            {
                bmp = BitmapFactory.decodeStream(activity.openFileInput(knjiga.getOfflineSlika()));
                holder.eNaslovna.setImageBitmap(bmp);
            }
            else if (knjiga.getSlika() != null)
            {
                Picasso.get().load(knjiga.getSlika().toString()).into(holder.eNaslovna);
            }
            else
            {
                holder.eNaslovna.setImageResource(R.drawable.internet);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setBackgroundColor(res.getColor(R.color.bojaSelect));
                knjiga.selektovana = 1;
                BazaOpenHelper boh = new BazaOpenHelper(activity);
                SQLiteDatabase db = boh.getWritableDatabase();
                db.execSQL("UPDATE Knjiga SET Pregledana = 1 where naziv = '" +  knjiga.getNaziv() + "'" );
            }
        });

        if (knjiga.selektovana ==1)
            vi.setBackgroundColor(res.getColor(R.color.bojaSelect));

        /*if ((kategorija != null && !knjiga.getKategorija().equals(kategorija)) || (autor != null && !knjiga.getAutor().equals(autor))) {
            vi.setVisibility(View.GONE);
            holder.eNaziv.setVisibility(View.GONE);
            holder.eAutor.setVisibility(View.GONE);
            holder.eNaslovna.setVisibility(View.GONE);
            holder.eOpis.setVisibility(View.GONE);
            holder.eDatumObjavljivanja.setVisibility(View.GONE);
            holder.eBrojStranica.setVisibility(View.GONE);

        }*/

        return vi;
    }
}
