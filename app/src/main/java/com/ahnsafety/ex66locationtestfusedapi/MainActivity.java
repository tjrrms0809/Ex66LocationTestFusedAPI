package com.ahnsafety.ex66locationtestfusedapi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    TextView tv;

    //Google Fused API의 클래스 참조변수
    GoogleApiClient googleApiClient;//위치정보관리 객체[LocationManager 역할]
    FusedLocationProviderClient providerClient;//위치정보제공자 객체[알아서 적절한 제공자 선택]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv= findViewById(R.id.tv);

        //api23이상이면 동적퍼미션 필요
    }

    public void clickBtn(View view) {

        //위치정보객체를 만들어주는 건축가 객체 생성
        GoogleApiClient.Builder builder= new GoogleApiClient.Builder(this);
        //건축가에게 FusedAPI 사용 인증키 지정
        builder.addApi(LocationServices.API);
        //건축가에게 위치정보 탐색 연결 성공 리스너
        builder.addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                Toast.makeText(MainActivity.this, "위치정보 탐색을 시작합니다.", Toast.LENGTH_SHORT).show();

                //연결이 성공되면 위치정보 얻어오기..

                //위치정보 요청 객체 생성 및 설정
                LocationRequest locationRequest= LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); //높은 정확도 : gps
                locationRequest.setInterval(5000); //5초에 한번씩 위치정보 갱신

                providerClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            }

            @Override
            public void onConnectionSuspended(int i) {
                Toast.makeText(MainActivity.this, "위치정보 탐색연결을 잠시 대기합니다.", Toast.LENGTH_SHORT).show();

            }
        });

        //연결실패 콜백리스너 추가
        builder.addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(MainActivity.this, "위치정보 탐색연결 실패", Toast.LENGTH_SHORT).show();
            }
        });

        //건축가에게 위치정보 관리객체 생성 요청
        googleApiClient= builder.build();

        //위치정보관리객체에게 위치정보연결 시도
        googleApiClient.connect();

        //위치정보의 실시간 위치정보 취득을 위해 위치정보제공자 소환
        providerClient= LocationServices.getFusedLocationProviderClient(this);

    }


    //위치정보 결과 콜백 리스너 객체
    LocationCallback locationCallback= new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            Location location= locationResult.getLastLocation();
            double latitude= location.getLatitude();
            double longitude= location.getLongitude();

            tv.setText(latitude +" , "+ longitude);
        }
    };

    //화면에서 보이지 않으면 더이상 위치정보를
    //업데이트 하지 않도록.
    @Override
    protected void onPause() {
        super.onPause();

        if(providerClient!=null){
            providerClient.removeLocationUpdates(locationCallback);
        }
    }
}





