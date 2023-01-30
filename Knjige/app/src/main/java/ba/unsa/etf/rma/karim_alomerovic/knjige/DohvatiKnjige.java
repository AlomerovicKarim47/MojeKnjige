package ba.unsa.etf.rma.karim_alomerovic.knjige;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import static java.io.FileDescriptor.in;

/**
 * Created by User on 11.5.2018..
 */

public class DohvatiKnjige extends AsyncTask<String, Integer, Void> {

    public interface IDohvatiKnjigeDone
    {
        public void onDohvatiDone(ArrayList<Knjiga> rez);
    }

    ArrayList<Knjiga> rez;
    IDohvatiKnjigeDone pozivatelj;

    public DohvatiKnjige(IDohvatiKnjigeDone p)
    {
        rez = new ArrayList<Knjiga>();
        pozivatelj = p;
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);
        pozivatelj.onDohvatiDone(rez);
    }

    @Override
    protected Void doInBackground(String... params) {
        for (int i = 0; i < params.length; i++) {

            String query = null;
            try
            {
                query = URLEncoder.encode(params[i], "utf-8");
            } catch(UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }

            int maxResults = 5;
            String url1 = "https://www.googleapis.com/books/v1/volumes?q=intitle:" + query + "&maxResults=" + maxResults;

            try
            {
                URL url = new URL(url1);
                URLConnection urlConnection = (HttpURLConnection)url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                String rezultat = convertStreamToString(in);

                JSONObject jo = new JSONObject(rezultat);
                JSONArray knjige = jo.getJSONArray("items");

                rez.addAll(jsonToArrayList(knjige));

            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e){
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
        return sb.toString();
    }

    public static ArrayList<Knjiga> jsonToArrayList(JSONArray knjige)
    {
        ArrayList<Knjiga> rezultat = new ArrayList<Knjiga>();
        for (int j = 0; j < knjige.length(); j++)
        {
            try {
                JSONObject knjiga = knjige.getJSONObject(j);

                String id = "", naziv  = "", opis = "", datumObjavljivanja = "";
                int brojStranica = 0;
                URL slika = null;
                JSONObject slike = new JSONObject(), volumeInfo = new JSONObject();
                JSONArray autoriJSON = new JSONArray();
                ArrayList<Autor> autori = new ArrayList<Autor>();

                if (knjiga.has("id")) id = knjiga.getString("id");
                if (knjiga.has("volumeInfo")) {
                    volumeInfo = knjiga.getJSONObject("volumeInfo");
                    if(volumeInfo.has("title"))naziv = volumeInfo.getString("title");
                    if(volumeInfo.has("authors"))autoriJSON = volumeInfo.getJSONArray("authors");
                    for (int k = 0; k < autoriJSON.length(); k++)
                        autori.add(new Autor(autoriJSON.getString(k), id));
                    if(volumeInfo.has("description"))opis = volumeInfo.getString("description");
                    if(volumeInfo.has("publishedDate"))datumObjavljivanja = volumeInfo.getString("publishedDate");
                    if(volumeInfo.has("imageLinks"))slike = volumeInfo.getJSONObject("imageLinks");
                    if(slike.has("smallThumbnail"))slika = new URL(slike.getString("smallThumbnail"));
                    if(volumeInfo.has("pageCount"))brojStranica = volumeInfo.getInt("pageCount");
                }
                Knjiga k = new Knjiga(id, naziv, autori, opis, datumObjavljivanja, slika, brojStranica);
                rezultat.add(k);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return rezultat;
    }
}
