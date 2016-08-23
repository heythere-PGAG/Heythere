package in.heythere.heythere.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.heythere.heythere.R;
import in.heythere.heythere.Utilities.Tools;


public class Whatsup extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    public Whatsup() {
    }

    public static Whatsup newInstance(int sectionNumber) {
        Whatsup fragment = new Whatsup();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_whatsup, container, false);
        return rootView;
    }
}