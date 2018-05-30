package com.example.roi.a3Design;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainMenu.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainMenu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainMenu extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private OnFragmentInteractionListener mListener;
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    public MainMenu() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainMenu.
     */
    // TODO: Rename and change types and number of parameters
    public static MainMenu newInstance(String param1, String param2) {
        MainMenu fragment = new MainMenu();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareListData();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);
        return rootView;
//        return inflater.inflate(R.layout.fragment_main_menu, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        expListView = view.findViewById(R.id.lvExp);

        // preparing list data

        listAdapter = new ExpandableListAdapter(view.getContext(), listDataHeader, listDataChild);


        // setting list adapter
        expListView.setAdapter(listAdapter);
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Living Room");
        listDataHeader.add("Bedroom");
        listDataHeader.add("Bathroom");
        listDataHeader.add("Kitchen");

        // Adding child data
        List<String> LivingRoom = new ArrayList<String>();
        LivingRoom.add("The Shawshank Redemption");
        LivingRoom.add("The Godfather");
        LivingRoom.add("The Godfather: Part II");
        LivingRoom.add("Pulp Fiction");
        LivingRoom.add("The Good, the Bad and the Ugly");
        LivingRoom.add("The Dark Knight");
        LivingRoom.add("12 Angry Men");

        List<String> bedroom = new ArrayList<String>();
        bedroom.add("The Conjuring");
        bedroom.add("Despicable Me 2");
        bedroom.add("Turbo");
        bedroom.add("Grown Ups 2");
        bedroom.add("Red 2");
        bedroom.add("The Wolverine");

        List<String> bathroom = new ArrayList<String>();
        bathroom.add("2 Guns");
        bathroom.add("The Smurfs 2");
        bathroom.add("The Spectacular Now");
        bathroom.add("The Canyons");
        bathroom.add("Europa Report");

        listDataChild.put(listDataHeader.get(0), LivingRoom); // Header, Child data
        listDataChild.put(listDataHeader.get(1), bedroom);
        listDataChild.put(listDataHeader.get(2), bathroom);
//        listDataChild.put(listDataHeader.get(3), kitchen);
    }
}
