package ba.unsa.etf.rma.karim_alomerovic.knjige;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by User on 11.5.2018..
 */

public class Autor implements Serializable{
    private String imeIPrezime;
    ArrayList<String> knjige;

    public String getImeIPrezime(){return imeIPrezime;}
    public ArrayList<String> getKnjige(){return knjige;}

    public void setImeIPrezime(String imeIPrezime){this.imeIPrezime = imeIPrezime;}
    public void setKnjige(ArrayList<String> knjige){this.knjige = knjige;}

    public Autor(String imeIPrezime, String id)
    {
        knjige = new ArrayList<String>();
        this.imeIPrezime = imeIPrezime;
        dodajKnjigu(id);
    }

    public void dodajKnjigu(String id)
    {
        if (!knjige.contains(id))
            knjige.add(id);
    }
}
