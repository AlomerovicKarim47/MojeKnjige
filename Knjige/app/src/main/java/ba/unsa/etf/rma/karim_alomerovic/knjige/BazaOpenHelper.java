package ba.unsa.etf.rma.karim_alomerovic.knjige;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class BazaOpenHelper extends SQLiteOpenHelper {

    private Context context;
    public static final String DATABASE_NAME = "mojaBaza.db";
    public static final int DATABASE_VERSION = 10;

    public static final String KATEGORIJA_TABLE = "Kategorija";
    public static final String KATEGORIJA_ID = "_id";
    public static final String KATEGORIJA_NAZIV = "naziv";
    public static final String KATEGORIJA_CREATE = "create table if not exists " + KATEGORIJA_TABLE + " (" +
            KATEGORIJA_ID + " integer primary key autoincrement, " +
            KATEGORIJA_NAZIV + " text unique);";

    public static final String KNJIGA_TABLE = "Knjiga";
    public static final String KNJIGA_ID = "_id";
    public static final String KNJIGA_NAZIV = "naziv";
    public static final String KNJIGA_OPIS = "opis";
    public static final String KNJIGA_DATUM_OBJAVE = "datumObjavljivanja";
    public static final String KNJIGA_BROJ_STRANICA = "brojStranica";
    public static final String KNJIGA_WEB_SERVIS = "idWebServis";
    public static final String KNJIGA_ID_KATEGORIJE = "idkategorije";
    public static final String KNJIGA_SLIKA = "slika";
    public static final String KNJIGA_PREGLEDANA = "pregledana";
    public static final String KNJIGA_CREATE = "create table if not exists " + KNJIGA_TABLE + " (" +
            KNJIGA_ID + " integer primary key autoincrement, " +
            KNJIGA_NAZIV + " text unique, " +
            KNJIGA_OPIS + " text, " +
            KNJIGA_DATUM_OBJAVE + " text, " +
            KNJIGA_BROJ_STRANICA + " integer, " +
            KNJIGA_WEB_SERVIS + " text, " +
            KNJIGA_ID_KATEGORIJE + " integer, " +
            KNJIGA_SLIKA + " text, " +
            KNJIGA_PREGLEDANA + " integer);";

    public static final String AUTOR_TABLE = "Autor";
    public static final String AUTOR_ID = "_id";
    public static final String AUTOR_IME = "ime";
    public static final String AUTOR_CREATE = "create table if not exists " + AUTOR_TABLE + " (" +
            AUTOR_ID + " integer primary key autoincrement, " +
            AUTOR_IME + " text unique);";

    public static final String AUTORSTVO_TABLE = "Autorstvo";
    public static final String AUTORSTVO_ID = "_id";
    public static final String AUTORSTVO_IDAUTORA = "idautora";
    public static final String AUTORSTVO_IDKNJIGE = "idknjige";
    public static final String AUTORSTVO_CREATE = "create table if not exists " + AUTORSTVO_TABLE + " (" +
            AUTORSTVO_ID + " integer primary key autoincrement, " +
            AUTORSTVO_IDAUTORA + " integer, " +
            AUTORSTVO_IDKNJIGE + " integer);";

    public BazaOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(KATEGORIJA_CREATE);
        sqLiteDatabase.execSQL(KNJIGA_CREATE);
        sqLiteDatabase.execSQL(AUTOR_CREATE);
        sqLiteDatabase.execSQL(AUTORSTVO_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Brisanje stare verzije
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + KATEGORIJA_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + KNJIGA_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AUTOR_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AUTORSTVO_TABLE);
        // Kreiranje nove
        onCreate(sqLiteDatabase);
    }

    public long dodajKategoriju(String naziv)
    {
        ContentValues nova = new ContentValues();
        nova.put(KATEGORIJA_NAZIV, naziv);
        SQLiteDatabase db = getWritableDatabase();
        try {
            return db.insert(KATEGORIJA_TABLE, null, nova);
        }catch (SQLiteConstraintException e) {
            return -1;
        }
    }
    public long dodajKnjigu(Knjiga knjiga)
    {
        ContentValues nova = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();

        //STAVI KNJIGU U TABELU
        nova.put(KNJIGA_NAZIV, knjiga.getNaziv());
        nova.put(KNJIGA_OPIS, knjiga.getOpis());
        nova.put(KNJIGA_DATUM_OBJAVE, knjiga.getDatumObjavljivanja());
        nova.put(KNJIGA_BROJ_STRANICA, knjiga.getBrojStranica());
        nova.put(KNJIGA_PREGLEDANA, knjiga.selektovana);
        if (knjiga.getSlika() != null)
            nova.put(KNJIGA_SLIKA, knjiga.getSlika().toString());
        nova.put(KNJIGA_ID_KATEGORIJE, nadjiIdKategorije(knjiga.getKategorija(), context));
        nova.put(KNJIGA_WEB_SERVIS, knjiga.getId());
        int idKnjige = 0;
        try {

            idKnjige = (int) db.insert(KNJIGA_TABLE, null, nova);
            if (idKnjige != -1) {
                //STAVI AUTORSTVO I AUTORE U TABELE
                if (knjiga.getAutori() == null) {
                    ContentValues novi = new ContentValues();
                    novi.put(AUTOR_IME, knjiga.getAutor());
                    db.insert(AUTOR_TABLE, null, novi);
                    ContentValues novo = new ContentValues();
                    novo.put(AUTORSTVO_IDAUTORA, nadjiIdAutora(knjiga.getAutor(), context));
                    novo.put(AUTORSTVO_IDKNJIGE, idKnjige);
                    db.insert(AUTORSTVO_TABLE, null, novo);
                } else {
                    for (Autor a : knjiga.getAutori()) {
                        ContentValues novi = new ContentValues();
                        novi.put(AUTOR_IME, a.getImeIPrezime());
                        db.insert(AUTOR_TABLE, null, novi);
                        ContentValues novo = new ContentValues();
                        novo.put(AUTORSTVO_IDAUTORA, nadjiIdAutora(a.getImeIPrezime(), context));
                        novo.put(AUTORSTVO_IDKNJIGE, idKnjige);
                        db.insert(AUTORSTVO_TABLE, null, novo);
                    }
                }
            }
            return idKnjige;
        }
        catch(SQLiteConstraintException e)
        {
            return  -1;
        }
    }

    public static int nadjiIdKategorije(String nazivKat, Context context)
    {
        BazaOpenHelper boh = new BazaOpenHelper(context);
        SQLiteDatabase db = boh.getWritableDatabase();
        Cursor cursorKat = db.query(KATEGORIJA_TABLE, new String[]{KATEGORIJA_ID}, "naziv = '" + nazivKat + "'", null, null, null, null);
        int idKat = 0;
        int ind = cursorKat.getColumnIndexOrThrow(KATEGORIJA_ID);
        while (cursorKat.moveToNext())
            idKat = cursorKat.getInt(ind);
        return idKat;
    }
    public static int nadjiIdAutora(String nazivAut, Context context)
    {
        BazaOpenHelper boh = new BazaOpenHelper(context);
        SQLiteDatabase db = boh.getWritableDatabase();
        Cursor cursorKat = db.query(AUTOR_TABLE, new String[]{KATEGORIJA_ID}, "ime = '" + nazivAut + "'", null, null, null, null);
        int idAut = 0;
        int ind = cursorKat.getColumnIndexOrThrow(AUTOR_ID);
        while (cursorKat.moveToNext())
            idAut = cursorKat.getInt(ind);
        return idAut;
    }

    public ArrayList<Knjiga> knjigeKategorije(long idKategorije){
        ArrayList<Knjiga> rezultat = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        //UZMI KNJIGE IZ KATEGORIJE
        Cursor c = db.query(KNJIGA_TABLE, new String[]{KNJIGA_ID, KNJIGA_NAZIV, KNJIGA_OPIS, KNJIGA_DATUM_OBJAVE, KNJIGA_BROJ_STRANICA, KNJIGA_WEB_SERVIS,
                        KNJIGA_ID_KATEGORIJE, KNJIGA_SLIKA, KNJIGA_PREGLEDANA}, "idkategorije = " + idKategorije,
                null, null, null, null);
        //PROLAZI KROZ KNJIGE
        while (c.moveToNext())
        {
            Knjiga k = new Knjiga();
            k.setNaziv(c.getString(c.getColumnIndexOrThrow(KNJIGA_NAZIV)));
            k.setOpis(c.getString(c.getColumnIndexOrThrow(KNJIGA_OPIS)));
            k.setDatumObjavljivanja(c.getString(c.getColumnIndexOrThrow(KNJIGA_DATUM_OBJAVE)));
            k.setBrojStranica(c.getInt(c.getColumnIndexOrThrow(KNJIGA_BROJ_STRANICA)));
            k.setId(c.getString(c.getColumnIndexOrThrow(KNJIGA_WEB_SERVIS)));
            try {
                k.setSlika(new URL(c.getString(c.getColumnIndexOrThrow(KNJIGA_SLIKA))));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            k.selektovana = c.getInt(c.getColumnIndexOrThrow(KNJIGA_PREGLEDANA));

            //UZMI ID-EVE AUTORA IZ AUTORSTVA
            Cursor c1 = db.query(AUTORSTVO_TABLE, new String[]{AUTORSTVO_IDAUTORA}, AUTORSTVO_IDKNJIGE + " = " + c.getInt(c.getColumnIndexOrThrow(KNJIGA_ID)), null,
                    null, null, null);

            ArrayList<Autor> autoriKnjige = new ArrayList<>();

            //NADJI AUTORE NA OSNOVU IDEVA
            while (c1.moveToNext())
            {
                //IME
                Cursor c2 = db.query(AUTOR_TABLE, new String[]{AUTOR_ID, AUTOR_IME}, AUTOR_ID + " = " + c1.getInt(c1.getColumnIndexOrThrow(AUTORSTVO_IDAUTORA)), null, null,
                null, null, null);
                while (c2.moveToNext())
                {
                    Autor a = new Autor(c2.getString(c2.getColumnIndexOrThrow(AUTOR_IME)), c.getString(c.getColumnIndexOrThrow(KNJIGA_WEB_SERVIS)));
                    autoriKnjige.add(a);
                }

            }
            k.setAutori(autoriKnjige);
            rezultat.add(k);

        }
        return rezultat;
    }

    public ArrayList<Knjiga> knjigeAutora(long idAutora)
    {
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<Knjiga> rezultat = new ArrayList<>();

        String upit = "select k.idWebServis, k.naziv, k.datumObjavljivanja, k.opis, k.brojStranica, k.idkategorije, k.slika, k.pregledana, a.ime " +
                "from Knjiga k, Autorstvo s, Autor a" +
                " where a._id = s.idautora and s.idknjige = k._id and a._id = "+ idAutora;
        Cursor c = db.rawQuery(upit, null);

        while (c.moveToNext())
        {
            Knjiga k = new Knjiga();

            k.setNaziv(c.getString(c.getColumnIndexOrThrow(KNJIGA_NAZIV)));
            k.setOpis(c.getString(c.getColumnIndexOrThrow(KNJIGA_OPIS)));
            k.setDatumObjavljivanja(c.getString(c.getColumnIndexOrThrow(KNJIGA_DATUM_OBJAVE)));
            k.setBrojStranica(c.getInt(c.getColumnIndexOrThrow(KNJIGA_BROJ_STRANICA)));
            k.setId(c.getString(c.getColumnIndexOrThrow(KNJIGA_WEB_SERVIS)));
            try {
                k.setSlika(new URL(c.getString(c.getColumnIndexOrThrow(KNJIGA_SLIKA))));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            k.selektovana = c.getInt(c.getColumnIndexOrThrow(KNJIGA_PREGLEDANA));
            String autorUpit = "select ime from Autor where _id = " + idAutora;
            Cursor c1 = db.rawQuery(autorUpit, null);
            while (c1.moveToNext())
            k.setAutor(c1.getString(c1.getColumnIndexOrThrow(AUTOR_IME)));
            rezultat.add(k);
        }

        return rezultat;
    }
}
