package ba.unsa.etf.rma.karim_alomerovic.knjige;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class KategorijeAkt extends AppCompatActivity{

    public static int idSlikeGenerator = -1;
    public static boolean siriL = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_kategorije_akt);

        FrameLayout fl = findViewById(R.id.mjestoF2);

        if (fl != null)
        {
            //siriL = true;
            KnjigeFragment knjigeFragment = (KnjigeFragment) getFragmentManager().findFragmentById(R.id.mjestoF2);

            if (knjigeFragment == null) {
                knjigeFragment = new KnjigeFragment();
                getFragmentManager().beginTransaction().replace(R.id.mjestoF2, knjigeFragment, "knjigeF").commit();
            }

        }

        ListeFragment listeFragment = (ListeFragment) getFragmentManager().findFragmentByTag("listaF");
        if (listeFragment == null)
        {
            listeFragment = new ListeFragment();
            listeFragment.setArguments(new Bundle());
            getFragmentManager().beginTransaction().replace(R.id.mjestoF1, listeFragment, "listaF").commit();
        }
        else
        {
            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }


    }
}
