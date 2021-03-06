package show.com.tron;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FragmentShow extends Fragment {
    public AdapterShow showAdapter;
    private List<Show> showList = new ArrayList<>();
    private TronApplication tron;
    private static final String TAG = "FRAGMENT SHOW";


    public FragmentShow() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_show, container, false);
        tron = (TronApplication) getActivity().getApplicationContext();
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.show_recycler);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        showAdapter = new AdapterShow(showList, tron, recyclerView, TAG);
        recyclerView.setAdapter(showAdapter);
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setColorNormal(getResources().getColor(R.color.button));
        fab.setColorPressed(getResources().getColor(R.color.buttonPressed));
        fab.setColorRipple(getResources().getColor(R.color.ripple));
        fab.attachToRecyclerView(recyclerView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            List<Show> list = tron.getShowList();
            if (list != null ) {
                addShow(list);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error thrown in OnResume");
            e.printStackTrace();
        }
    }

    public void addShow(final List<Show> newShows) {
        showList.clear();
        showList.addAll(newShows);
        publish();
    }

    public void removeShow(int id) {
        Show remove = null;
        for ( Show show : showList ) {
            if (show.getId() == id) {
                remove = show;
                break;
            }
        }
        if (remove != null) {
            boolean result = showList.remove(remove);
            Log.d(TAG, result + "");
        }
        publish();
    }

    private void publish(){
        Handler mainHandler = new Handler(tron.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                showAdapter.notifyDataSetChanged();
            }
        };
        mainHandler.post(myRunnable);
    }
}
