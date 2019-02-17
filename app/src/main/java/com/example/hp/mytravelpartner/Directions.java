package com.example.hp.mytravelpartner;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */


public class Directions extends Fragment {
    View v;
    private TextView textView3;


    public Directions() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_route, container, false);
        TextView textView3 = (TextView) v.findViewById(R.id.textView3);

        return v;
    }

    public void obj(String ob)
    {
        //textView3.setText(Html.fromHtml(ob));
    }

    public void DirStep(String u)
    {
      mapActivity.TaskParser parser;
      String jsonURL =u;

      }

}
