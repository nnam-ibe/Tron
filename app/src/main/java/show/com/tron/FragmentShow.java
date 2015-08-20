package show.com.tron;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FragmentShow extends Fragment {
    public AdapterShow showAdapter;
    private RecyclerView recyclerView;
    private List<Show> showList = new ArrayList<>();
    private FloatingActionButton fab;
    private TronApplication tron;


    public FragmentShow() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_show, container, false);
        tron = (TronApplication) getActivity().getApplicationContext();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.show_recycler);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        showAdapter = new AdapterShow(showList, tron);
        recyclerView.setAdapter(showAdapter);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setColorNormal(getResources().getColor(R.color.accent));
        fab.setColorPressed(getResources().getColor(R.color.accent_pressed));
        fab.setColorRipple(getResources().getColor(R.color.ripple));
        fab.attachToRecyclerView(recyclerView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            List<Show> list = tron.getShowList();
            if (list != null && !list.isEmpty()) {
                addShow(list);
            }
        } catch (Exception e) {
            Log.e("Fragment Show", "ERORRR!!! NOT SURE YET!!!");
        }
    }

    public void addShow(final List<Show> newShows) {
        showList.clear();
        showList.addAll(newShows);
        showAdapter.notifyDataSetChanged();
    }
}
