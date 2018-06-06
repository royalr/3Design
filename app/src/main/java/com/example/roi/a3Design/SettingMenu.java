package com.example.roi.a3Design;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.roi.a3Design.ObjectManager.context;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingMenu.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingMenu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingMenu extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private final int NUM_OF_SAVES = 5;
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    public SettingMenu() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingMenu.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingMenu newInstance(String param1, String param2) {
        SettingMenu fragment = new SettingMenu();
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
        View rootView = inflater.inflate(R.layout.fragment_setting_menu, container, false);

        return rootView;
//        return inflater.inflate(R.layout.fragment_setting_menu, container, false);
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
        expListView = view.findViewById(R.id.save);
        // preparing list data
        listAdapter = new ExpandableListAdapter(view.getContext(), listDataHeader, listDataChild, null);
        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                String title = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
                Log.d("list", "" + groupPosition);
                final int child = childPosition;
                switch (groupPosition) {
                    case 0: // save
                        if (title.equals("Empty Slot " + (childPosition + 1))) {
                            // this slot is empty
                            listDataChild.get(listDataHeader.get(0)).set(child, "Saved Data " + (child + 1));
                            listDataChild.get(listDataHeader.get(1)).set(child, "Saved Data " + (child + 1));
                            MyRenderer.saveProject(childPosition);

                        } else {
                            // confirm action
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Caution!")
                                    .setMessage("Slot already contains saved data. Do you want to override it?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            MyRenderer.saveProject(child);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null).show();
                        }
                        parent.collapseGroup(groupPosition);
                        parent.collapseGroup(groupPosition+1);
                        break;
                    case 1: // load
                        if (!title.equals("Empty Slot " + (childPosition + 1))) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Caution!")
                                    .setMessage("Are you sure you want to load? All unsaved changes will be lost.")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            MyRenderer.loadProject(child);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null).show();
                        }

                        break;
                }

                return false;
            }
        });
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        listDataHeader.add("Save");
        listDataHeader.add("Load");

        List<String> save = new ArrayList<>();
        List<String> load = new ArrayList<>();

        for (int i = 0; i < NUM_OF_SAVES; i++) {

            File file = getActivity().getFileStreamPath("save" + i);
            if (file == null || !file.exists()) {
                save.add("Empty Slot " + (i+1));
                load.add("Empty Slot " + (i+1));
            } else {
                save.add("Saved Data " + (i+1));
                load.add("Saved Data " + (i+1));
            }

            // edit here titles if slot is taken!
        }
        listDataChild.put(listDataHeader.get(0), save); // Header, Child data
        listDataChild.put(listDataHeader.get(1), load); // Header, Child data

    }
}
