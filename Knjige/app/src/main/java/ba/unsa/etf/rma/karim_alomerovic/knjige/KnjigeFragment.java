package ba.unsa.etf.rma.karim_alomerovic.knjige;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by User on 5.4.2018..
 */

public class KnjigeFragment extends Fragment {
    private ArrayList<Knjiga> knjige = new ArrayList<Knjiga>();
    String autor = null, kategorija = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle onInstanceSaveState)
    {
        View vi = inflater.inflate(R.layout.knjige_fragment, container, false);

        ListView listaKnjiga = vi.findViewById(R.id.listaKnjiga);
        Button dPovratak = vi.findViewById(R.id.dPovratak);

        if (getArguments() != null)
        {
            if (getArguments().containsKey("knjige"))
                knjige = (ArrayList<Knjiga>) getArguments().getSerializable("knjige");
            if (getArguments().containsKey("kategorija"))
                kategorija = getArguments().getString("kategorija");
            else if (getArguments().containsKey("autor"))
                autor = getArguments().getString("autor");
        }

        KnjigaAdapter knjigaAdapter = new KnjigaAdapter(getActivity(), knjige, getActivity().getResources(), kategorija, autor);

        listaKnjiga.setAdapter(knjigaAdapter);
        listaKnjiga.setDivider(null);

        dPovratak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack("listaF", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        return vi;
    }

}
