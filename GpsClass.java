package com.android.intellicycleV2;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GpsClass extends Fragment {
	View rootView;
	private GoogleMap mMap;
	
	public static final String ARG_SECTION_NUMBER = "section_number";
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		if (rootView != null){
				((ViewGroup) rootView.getParent()).removeView(rootView);
			}
			try{
				rootView = inflater.inflate(R.layout.gps, container,false);
				mMap = ((SupportMapFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.mymap)).getMap();
				mMap.setMyLocationEnabled(true);
				
			} catch(InflateException e){
			 
			}
			return rootView;
		}
}
