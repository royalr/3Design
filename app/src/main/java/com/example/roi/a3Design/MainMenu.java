package com.example.roi.a3Design;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainMenu.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainMenu newInstance} factory method to
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
    private HashMap<String, List<Integer>> listDataChildImages;
    private static View chosenChild = null;

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
        listAdapter = new ExpandableListAdapter(view.getContext(), listDataHeader, listDataChild, listDataChildImages);
        expListView.setAdapter(listAdapter);

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {


                if (chosenChild == v) {
                    unchooseChild();
                    return false;
                }
                unchooseChild();

                // choose the child
                chosenChild = v;
                v.setBackgroundColor(Color.rgb(214, 214, 214));

                int imageId = listDataChildImages.get(listDataHeader.get(groupPosition)).get(childPosition);
                String imageName = getResources().getResourceEntryName(imageId);

                ObjectManager.setObjToBeCreatedName(imageName);
                ObjectManager.setObjToBeCreated(true);

                return false;
            }
        });
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int i) {
                unchooseChild();
            }
        });
    }

    public static void unchooseChild() {
        if (chosenChild != null) {
            ObjectManager.setObjToBeCreated(false);
            chosenChild.setBackgroundColor(Color.TRANSPARENT);
            chosenChild = null;
        }
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        listDataChildImages = new HashMap<>();

        String category = "livingroom_";
        String categoryName = "Living Room";
        addChildDataForCategory(category, categoryName, listDataHeader, listDataChildImages, listDataChild);

        category = "bathroom_";
        categoryName = "Bathroom";
        addChildDataForCategory(category, categoryName, listDataHeader, listDataChildImages, listDataChild);

        category = "bedroom_";
        categoryName = "Bedroom";
        addChildDataForCategory(category, categoryName, listDataHeader, listDataChildImages, listDataChild);

        category = "kitchen_";
        categoryName = "Kitchen";
        addChildDataForCategory(category, categoryName, listDataHeader, listDataChildImages, listDataChild);

        category = "general_";
        categoryName = "General";
        addChildDataForCategory(category, categoryName, listDataHeader, listDataChildImages, listDataChild);
    }

    private void addChildDataForCategory(String category, String categoryName, List<String> listDataHeader, HashMap<String, List<Integer>> listDataChildImages, HashMap<String, List<String>> listDataChild) {
        listDataHeader.add(categoryName);
        List<String> categoryImagesNames = getListImagesByCategory(category);
        List<String> categoryList = new ArrayList<>();
        List<Integer> categoryListImages = new ArrayList<>();

        for (String imageName : categoryImagesNames) {
            categoryList.add(imageName.replace(category, "").replace("_", " "));
            try {
                categoryListImages.add(R.drawable.class.getField(imageName).getInt(null));
            } catch (Exception e) {
                Log.e("Drawables", "Failed to retrieve id of drawable - " + imageName, e);
            }
        }

        listDataChildImages.put(categoryName, categoryListImages);
        listDataChild.put(categoryName, categoryList);
    }

    private List<String> getListImagesByCategory(String category) {
        List<String> listImages = new ArrayList<>();

        Field[] ID_Fields = R.drawable.class.getFields();
        for (Field f : ID_Fields) {
            try {
                String imageName = f.getName();
                if (imageName.startsWith(category)) {
                    listImages.add(imageName);
                }
            } catch (IllegalArgumentException e) {
                Log.e("Drawables", "Failed to retrieve an image name", e);
            }
        }

        return listImages;
    }
}
