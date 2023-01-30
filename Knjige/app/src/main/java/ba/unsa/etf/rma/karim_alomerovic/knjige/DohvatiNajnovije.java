package ba.unsa.etf.rma.karim_alomerovic.knjige;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import ba.unsa.etf.rma.karim_alomerovic.knjige.DohvatiKnjige;
import ba.unsa.etf.rma.karim_alomerovic.knjige.Knjiga;

/**
 * Created by User on 12.5.2018..
 */

public class DohvatiNajnovije extends AsyncTask<String, Integer, Void> {

    public interface IDohvatiNajnovijeDone
    {
        public void onNajnovijeDone(ArrayList<Knjiga> rez);
    }

    ArrayList<Knjiga> rez;
    IDohvatiNajnovijeDone pozivatelj;

    public DohvatiNajnovije( IDohvatiNajnovijeDone p)
    {
        pozivatelj = p;
        rez = new ArrayList<Knjiga>();
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);
        pozivatelj.onNajnovijeDone(rez);
    }

    @Override
    protected Void doInBackground(String... params) {
        String query = null;
        try
        {
            query = URLEncoder.encode(params[0], "utf-8");
        } catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        String url1 = "https://www.googleapis.com/books/v1/volumes?q=:" + query + "&orderBy=newest";

        try {
            URL url = new URL(url1);
            URLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            String rezultat = DohvatiKnjige.convertStreamToString(in);

            JSONObject jo = new JSONObject(rezultat);
            JSONArray knjigeJSON = jo.getJSONArray("items");
            ArrayList<Knjiga> knjige = DohvatiKnjige.jsonToArrayList(knjigeJSON);

            for (Knjiga k:knjige)
            {
                boolean sadrzi = false;
                for (Autor a:k.getAutori())
                {
                    if (a.getImeIPrezime().equals(params[0]))
                    {
                        sadrzi = true;
                        break;
                    }
                }
                if (rez.size()<5 && sadrzi)
                {
                    rez.add(k);
                }
            }

        }
        catch (MalformedURLException e) {
            e.printStackTrace();}
        catch (IOException e) {
            e.printStackTrace();}
        catch (JSONException e) {
            e.printStackTrace();}

        return null;
    }
}
