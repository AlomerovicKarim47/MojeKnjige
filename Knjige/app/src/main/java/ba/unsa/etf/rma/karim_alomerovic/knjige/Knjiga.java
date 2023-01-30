package ba.unsa.etf.rma.karim_alomerovic.knjige;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by User on 24.3.2018..
 */

public class Knjiga implements Serializable{
    //STARE STVARI ZADRZANE ZBOG STARIH FUNKCIONALNOSTI
    private String kategorija = "", autor = "", offlineSlika;


    public void setOfflineSlika(String id){offlineSlika = id;}
    public String getOfflineSlika(){return offlineSlika;}
    public String getAutor(){return autor;}
    public void setAutor(String a){autor = a;}
    public String getKategorija(){return kategorija;}
    public void setKategorija(String k){kategorija = k;}

    public Knjiga(String naziv, String autor, String kategorija)
    {
        this.naziv = naziv;
        this.autor = autor;
        this.kategorija = kategorija;
    }

    int selektovana = 0;


    //NOVE STVARI
    private String id = "", naziv = "", opis = "", datumObjavljivanja = "";
    private ArrayList<Autor> autori;
    private URL slika;
    private int brojStranica;

    public String getId(){return id;}
    public String getNaziv(){return naziv;}
    public ArrayList<Autor> getAutori(){return autori;}
    public String getOpis(){return opis;}
    public String getDatumObjavljivanja(){return datumObjavljivanja;}
    public URL getSlika(){return slika;}
    public int getBrojStranica(){return brojStranica;}


    public void setId(String id){this.id = id;}
    public void setNaziv(String naziv){this.naziv = naziv;}
    public void setAutori(ArrayList<Autor> autori){this.autori = autori; if (autori.size() > 0) autor = autori.get(0).getImeIPrezime();}
    public void setOpis(String opis){this.opis = opis;}
    public void setDatumObjavljivanja(String datumObjavljivanja){this.datumObjavljivanja = datumObjavljivanja;}
    public void setSlika(URL slika){this.slika = slika;}
    public void setBrojStranica(int brojStranica){this.brojStranica = brojStranica;}

    public Knjiga(String id, String naziv, ArrayList<Autor> autori, String opis, String datumObjavljivanja, URL slika, int brojStranica)
    {
        this.id = id;
        this.naziv = naziv;
        this.autori = autori;
        if (autori.size() > 0) this.autor = autori.get(0).getImeIPrezime();
        this.opis = opis;
        this.datumObjavljivanja = datumObjavljivanja;
        this.slika = slika;
        this.brojStranica = brojStranica;
    }

    public Knjiga(){}
}
