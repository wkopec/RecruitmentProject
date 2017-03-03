package com.recruitmentproject;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentMap extends Fragment implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private MapView mMapView;
    private View mView;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final String url = "https://interview-ready4s.herokuapp.com";
    private List<LocationList> locationList = new ArrayList<>();
    private ProgressDialog prograssDialog;
    private DatabaseHelper locationsDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prograssDialog = new ProgressDialog(getActivity());
        prograssDialog.setMessage(getString(R.string.determiningLocation));

        locationsDb = new DatabaseHelper(getActivity());

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                prograssDialog.dismiss();
                loadLocations(location.getLatitude(), location.getLongitude());
                mGoogleMap.setMyLocationEnabled(true);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(getString(R.string.localization_disable))
                            .setMessage(getString(R.string.do_set_localization_enable))
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                            .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            }).create().show();
                }
            }
        };

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 1);

            }
            else {
                setRequestLocationUpdates();
            }

        }
        else {
            setRequestLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setRequestLocationUpdates();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.localization_options_will_not_be_avaible), Toast.LENGTH_LONG).show();
                }
        }
    }

    private void setRequestLocationUpdates(){
        prograssDialog.show();
        locationManager.requestLocationUpdates("gps", 10000, 10, locationListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_map, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mMapView = (MapView) mView.findViewById(R.id.mapView);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.41177549551888, 19.17423415929079), (float) 5.3173866));

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LocationList location = getLocation(Integer.parseInt(marker.getSnippet()));
                if(locationsDb.insertData(location.getId(), location.getName(), location.getAvatar(), location.getLat(), location.getLng())) {
                    onSubmitListener.onSubmit();
                }
                else {
                    onSubmitListener.onSubmit();
                }
                if (isDataConnectionAvailable(getActivity())){
                    Intent intent = new Intent(getActivity(), LocationDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", Integer.parseInt(marker.getSnippet()));
                    intent.putExtras(bundle);
                    mView.getContext().startActivity(intent);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(getString(R.string.internet_disable))
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();
                }
                return false;
            }
        });
    }

    private void loadLocations(double latitude, double longitude){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RestApi service = retrofit.create(RestApi.class);

        Call call = service.getLocations(latitude, longitude);

        call.enqueue(new Callback<List<LocationList>>() {

            @Override
            public void onResponse(Call<List<LocationList>> call, Response<List<LocationList>> response) {
                try {
                    List<LocationList> locationsToShow = new ArrayList<>();
                    for(int i = 0; i < response.body().size(); i++) {
                        if (getLocation(response.body().get(i).getId()) == null) {
                            locationsToShow.add(response.body().get(i));
                            locationList.add(response.body().get(i));
                        }
                    }
                    showLocationsOnMap(locationsToShow);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<LocationList>> call, Throwable t) {

            }
        });
    }

    private LocationList getLocation(int id) {
        for(LocationList location: locationList) {
            if (location.getId() == id) return location;
        }
        return null;
    }

    CameraUpdate cameraUpdate;
    List<Marker> markersList = new ArrayList<>();
    private void showLocationsOnMap(List<LocationList> locations) {

        for (int i = 0; i < locations.size(); i++) {
            Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(locations.get(i).getLat(), locations.get(i).getLng()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_pink_900_36dp))
                    .snippet(String.valueOf(locations.get(i).getId()))
            );
            markersList.add(marker);
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < markersList.size(); i++) {
            builder.include(markersList.get(i).getPosition());
        }
        int padding = 200;
        LatLngBounds bounds = builder.build();
        cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mGoogleMap.animateCamera(cameraUpdate);

            }
        });
    }

    public static boolean isDataConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public interface SubmitListener {
        void onSubmit();
    }

    private SubmitListener onSubmitListener;

    public void setSubmitListener(SubmitListener onSubmitListener){
        this.onSubmitListener = onSubmitListener;
    }
}
