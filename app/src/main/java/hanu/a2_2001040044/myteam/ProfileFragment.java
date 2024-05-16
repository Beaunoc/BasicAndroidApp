package hanu.a2_2001040044.myteam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.core.os.HandlerCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class ProfileFragment extends Fragment {

    ImageView imvAvatar;
    TextView tvName, tvEmail;
    private int id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imvAvatar = view.findViewById(R.id.imvAvatar);
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);

        if (getArguments() != null) {
            id = getArguments().getInt("id");
        }

        String url = "https://jsonplaceholder.typicode.com/users/" + id;
        Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());
        Constants.executerSer.execute(() -> {
            String json = fetchJSON(url);
            handler.post(() -> {
                if (json == null) {
                    Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    JSONObject root = new JSONObject(json);
                    String name = root.getString("name");
                    String email = root.getString("email");

                    tvName.setText(name);
                    tvEmail.setText(email);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        });

        String link = "https://robohash.org/" + id + "?set=set2";
        Constants.executerSer.execute(() -> {
            Bitmap bitmap = loadImage(link);
            if (bitmap != null) {
                handler.post(() -> imvAvatar.setImageBitmap(bitmap));
            }
        });

        return view;
    }

    public String fetchJSON(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            InputStream is = urlConnection.getInputStream();
            Scanner sc = new Scanner(is);
            StringBuilder result = new StringBuilder();
            while (sc.hasNextLine()) {
                result.append(sc.nextLine());
            }
            return result.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap loadImage(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream is = connection.getInputStream();
            return BitmapFactory.decodeStream(is);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}