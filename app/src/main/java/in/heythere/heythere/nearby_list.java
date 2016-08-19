package in.heythere.heythere;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;

import in.heythere.heythere.adapters.EventsListAdapter;

public class nearby_list extends Fragment {

    private static JSONArray response = new JSONArray();

    public static nearby_list newInstance(JSONArray response1) {
        response = response1;
        return new nearby_list();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_nearby_list, container, false);

        RecyclerView map_items = (RecyclerView)root.findViewById(R.id.near_list);

        RecyclerView.LayoutManager recentmanager = new LinearLayoutManager(getActivity());

        map_items.setLayoutManager(recentmanager);

        RecyclerView.Adapter adapter = new EventsListAdapter(getActivity(),response,R.layout.events_list_item);
        map_items.setAdapter(adapter);
        return root;
    }

}
