package ba.unsa.etf.rma.karim_alomerovic.knjige;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by User on 12.5.2018..
 */

public class KnjigePoznanika extends IntentService {

    public static final int STATUS_START = 0;
    public static final int STATUS_FINISH = 1;
    public static final int STATUS_ERROR = 2;

    public KnjigePoznanika() {
        super("ReminderService");
    }

    public KnjigePoznanika(String name) {
        super(name);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String idKorisnika = intent.getStringExtra("idKorisnika");
        ResultReceiver receiver = intent.getParcelableExtra("receiver");
        receiver.send(STATUS_START, Bundle.EMPTY);

        ArrayList<Knjiga> rez = new ArrayList<Knjiga>();

        String url1 = "https://www.googleapis.com/books/v1/users/" + idKorisnika + "/bookshelves";

        try {
            URL URL1 = new URL(url1);
            URLConnection urlConnection1 = (URLConnection)URL1.openConnection();
            InputStream in1 = new BufferedInputStream(urlConnection1.getInputStream());
            String rezultat1 = DohvatiKnjige.convertStreamToString(in1);

            JSONObject jo1 = new JSONObject(rezultat1);
            JSONArray bookshelves = new JSONArray(), knjige = new JSONArray();
            try {
                if (jo1.has("items")) {
                    bookshelves = jo1.getJSONArray("items");

                    for (int i = 0; i < bookshelves.length(); i++) {
                        String url2 = url1 + "/" + bookshelves.getJSONObject(i).getString("id") + "/volumes";
                        URL URL2 = new URL(url2);
                        URLConnection urlConnection2 = (URLConnection) URL2.openConnection();
                        InputStream in2 = new BufferedInputStream(urlConnection2.getInputStream());
                        String rezultat2 = DohvatiKnjige.convertStreamToString(in2);
                        JSONObject jo2 = new JSONObject(rezultat2);
                        if (jo2.has("items")) knjige = jo2.getJSONArray("items");
                        else throw new Exception();
                        rez.addAll(DohvatiKnjige.jsonToArrayList(knjige));
                    }
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("listaKnjiga", rez); //putSerializable?
                    receiver.send(STATUS_FINISH, bundle);
                } else {
                    throw new Exception();
                }
            } catch (Exception e) {
                receiver.send(STATUS_ERROR, Bundle.EMPTY);;
            }

        }catch(MalformedURLException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
