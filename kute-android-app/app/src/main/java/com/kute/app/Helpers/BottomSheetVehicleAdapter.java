package com.kute.app.Helpers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kute.app.Bussiness.Train;
import com.kute.app.R;
import com.kute.app.Activities.MapActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeff on 12/12/2016.
 */

public class BottomSheetVehicleAdapter extends RecyclerView.Adapter<BottomSheetVehicleAdapter.VehicleViewHolder> {
	private int vehicleType;
	private List<Train> vehicles;
	private Context context;
	//0 is car
	//1 is bus
	//2 is train
	private VehicleViewHolder selectedViewHolder = null;
	private static List<BottomSheetVehicleAdapter> instances = new ArrayList<>();

	public BottomSheetVehicleAdapter(int vehicleType, List<Train> vehicles, Context context) {
		this.vehicleType = vehicleType;
		this.vehicles = vehicles;
		this.context = context;
		instances.add(this);
	}

	@Override
	public VehicleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vehicle_list, parent, false);
		return new VehicleViewHolder(root);
	}

	@Override
	public void onBindViewHolder(final VehicleViewHolder holder, final int position) {
		Train vehicle = vehicles.get(position);
		holder.vehicle = vehicle;
		holder.mIconView.setImageDrawable(MapActivity.getVehicleDrawable(vehicleType, context));
		holder.mTextView.setText(vehicle.getTrainname()+"");

		holder.mRootView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				for (BottomSheetVehicleAdapter adapter: instances)
					adapter.onVehicleClicked(holder, position);
			}
		});
	}

	public void onVehicleClicked(VehicleViewHolder holder, int index) {
		if (selectedViewHolder != null)
			selectedViewHolder.mTickView.setVisibility(View.INVISIBLE);
		selectedViewHolder = holder;
		selectedViewHolder.mTickView.setVisibility(View.VISIBLE);
		MapActivity.reference.setSelectedVehicle(selectedViewHolder);
		MapActivity.reference.collapseBottomSheet();
	}

	@Override
	public int getItemCount() {
		return vehicles.size();
	}

	public class VehicleViewHolder extends RecyclerView.ViewHolder{
		public View mRootView;
		public View mTickView;
		public TextView mTextView;
		public ImageView mIconView;
		public int vehicleType;
		public Train vehicle;

		public VehicleViewHolder(View v) {
			super(v);
			mRootView = v.findViewById(R.id.root_view);
			mTickView = v.findViewById(R.id.tick);
			mTextView = (TextView) v.findViewById(R.id.vehicle_name);
			mIconView = (ImageView) v.findViewById(R.id.icon);
			this.vehicleType = BottomSheetVehicleAdapter.this.vehicleType;
		}
	}
}