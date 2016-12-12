package com.kute.app.Views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kute.app.Activities.MapActivity;
import com.kute.app.Bussiness.Train;
import com.kute.app.Helpers.BottomSheetVehicleAdapter;
import com.kute.app.R;

import java.util.ArrayList;
import java.util.List;

import static com.kute.app.R.string.train;

/**
 * Created by Jeff on 12/12/2016.
 */

public class BottomSheetVehicleFragment extends Fragment {
	private RecyclerView vehicleRecyclerView;
	private BottomSheetVehicleAdapter adapter;
	private List</*Vehicle*/Train> vehicles = new ArrayList<>();
	private int vehicleType;
	public static int activeVehicleType;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_bottom_sheet_vehicle, container, false);
		vehicleRecyclerView = (RecyclerView) view.findViewById(R.id.bottom_sheet_vehicle_list);
		adapter = new BottomSheetVehicleAdapter(vehicleType, vehicles, getContext());
		vehicleRecyclerView.setAdapter(adapter);
		vehicleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		vehicleRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				if (activeVehicleType == vehicleType)
					if (isRecyclerViewAtTop()) {
						MapActivity.reference.setBottomSheetScrollingEnabled(true);
					} else {
						MapActivity.reference.setBottomSheetScrollingEnabled(false);
					}
			}
		});
		if (vehicleType == 2) {
			MapActivity.reference.getVehicles(2);
		}
		//TODO remove this
		else {
			for (int i = 0; i < 5; i++) {
				Train train = new Train();
				train.setTrainname("Non-existent vehicle");
				vehicles.add(train);
				System.out.println(vehicles);
			}
			adapter.notifyDataSetChanged();
		}
		return view;
	}

	public boolean isRecyclerViewAtTop() {
		try {
			return vehicleRecyclerView.getChildAt(0).getTop() == 0;
		} catch (NullPointerException e) {
			return true;
		}
	}

	public List</*Vehicle*/Train> getVehicleList() {
		return vehicles;
	}

	public BottomSheetVehicleAdapter getAdapter() {
		return adapter;
	}

	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
		vehicleType = args.getInt("vehicleType");
	}
}
