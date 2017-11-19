package com.example.android.movies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{
    Context context = MainActivity.this;
    RecyclerView recyclerView;
    String apiKey = com.example.android.movies.BuildConfig.MOVIES_KEY;
    String jsonURLPopular="https://api.themoviedb.org/3/movie/popular?api_key="+apiKey+"&language=pt-BR";
    String jsonURLTopRated="https://api.themoviedb.org/3/movie/top_rated?api_key="+apiKey+"&language=pt-BR";

    String imageURL = "http://image.tmdb.org/t/p/w342";
    Boolean parse;
    ArrayList<Movie> movies = new ArrayList<>();

    private static final int LOADER=1;

    private static final String QUERY_URL="";

    private String jsonUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film);
        recyclerView = (RecyclerView)findViewById(R.id.recycler);
//        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setHasFixedSize(true);

        Log.v("URL: ",jsonURLPopular);

        Bundle queryBundle = new Bundle();
        queryBundle.putString(QUERY_URL,jsonURLPopular);

//        jsonUrl = queryBundle.toString();

        jsonUrl = queryBundle.getString(QUERY_URL);

        getSupportLoaderManager().initLoader(LOADER,null,this);
//        getSupportLoaderManager().initLoader(LOADER,queryBundle,this);

        Log.v("INITIALIZE : ","HERE "+jsonUrl);

        //new JSONDownloader(context,jsonURLPopular,recyclerView).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }
//    Bundle queryBundle = new Bundle();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemClick = item.getItemId();
        if (itemClick==android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }else if (itemClick == R.id.action_votes){

            Bundle queryBundle = new Bundle();
            queryBundle.putString(QUERY_URL,jsonURLTopRated);

            jsonUrl = queryBundle.getString(QUERY_URL);

            getSupportLoaderManager().restartLoader(LOADER,queryBundle,this);

            Log.v("BUNDLE: ", String.valueOf(queryBundle));

            Log.v("JSON URL: ",jsonUrl);

//            new JSONDownloader(context,jsonURLTopRated,recyclerView).execute();
            return true;
        }
        Bundle queryBundle = new Bundle();
        queryBundle.putString(QUERY_URL,jsonURLPopular);

        jsonUrl = queryBundle.getString(QUERY_URL);

        getSupportLoaderManager().restartLoader(LOADER,queryBundle,this);

        Log.v("BUNDLE: ", String.valueOf(queryBundle));

//        Log.v("JSON URL: ",jsonUrl);

//        new JSONDownloader(context,jsonURLPopular,recyclerView).execute();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if(args == null) {
                    Log.v("ARGS: ", String.valueOf(args));
                    Log.v("JSON URL: ",jsonUrl);
//                    forceLoad();
                    return;
                }


//                if (isNetworkAvailable(context)) {
//               if (netWorkdisponibilidade(context)){
//                  if (isNetworkAvailable()){
                if(isConnected(context)){
//                        Log.v("Error with ", "internet");
                    Toast.makeText(getApplicationContext(), "Conected", Toast.LENGTH_SHORT).show();
//                    Log.v("Error with ", "internet");
//                    return;
                }else{Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();}

                forceLoad();
            }

            @Override
            public String loadInBackground() {
                Log.v("URL MAIN: ",jsonUrl);
//                return download(jsonUrl);
                return download(jsonUrl);
//                return download();
            }
        };
//        String url = jsonURLPopular;
//
//        Log.v("URL DO MAIN: ",url);
//
//        return new JSONDownloader(context,url,recyclerView);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        Log.v("DATA: ",data);
        if (data.startsWith("Error")) {
            String error = data;
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
        } else {

            Log.v("URL DOWNLOAD: ",data);

//            new JSONParser(context, jsonData, recyclerView);

            Log.v("URL IMAGE: ",imageURL);

            parse = new JSON(data,imageURL,movies).parse();

            Log.v("PARSE: ", String.valueOf(parse));

            if(parse){
                recyclerView.setAdapter(new FilmeAdapter(context, movies));

                Log.v("MOVIES : ", String.valueOf(movies));

            //   recyclerView.setAdapter(new FilmeAdapter(context, movies));
            }else {
                Toast.makeText(context, "Unable To Parse,Check Your Log output", Toast.LENGTH_LONG).show();
            }
            //PARSER
//            new JSONParser(context, jsonData, recyclerView).execute();
        }
    }


    @Override
    public void onLoaderReset(Loader loader) {

    }

    private String download(String url) {
//        private String download(){
//        Object connection = Connector.connect(jsonURLPopular);
//        url = jsonUrl;
//        Object connection = Connector.connect(jsonUrl);
        Object connection = Connector.connect(url);
        if (connection.toString().startsWith("Error")) {
            return connection.toString();
        }
        try {
            HttpURLConnection con = (HttpURLConnection) connection;
            if (con.getResponseCode() == con.HTTP_OK) {
                //GET INPUT FROM STREAM
                InputStream is = new BufferedInputStream(con.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer jsonData = new StringBuffer();
                //READ
                while ((line = br.readLine()) != null) {
                    jsonData.append(line + "\n");
                }
                //CLOSE RESOURCES
                br.close();
                is.close();
                //RETURN JSON
                return jsonData.toString();
            } else {
                return "Error " + con.getResponseMessage();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error " + e.getMessage();
        }
    }

    boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean netWorkdisponibilidade(Context cont){
        boolean conectado = false;
        ConnectivityManager conmag;
        conmag = (ConnectivityManager)cont.getSystemService(Context.CONNECTIVITY_SERVICE);
        conmag.getActiveNetworkInfo();
        //Verifica o WIFI
        if(conmag.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()){
            conectado = true;
        }
        //Verifica o 3G
        else if(conmag.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()){
            conectado = true;
        }
        else{
            conectado = false;
        }
        return conectado;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if ((netInfo != null) && (netInfo.isConnectedOrConnecting()) && (netInfo.isAvailable())) {
            return true;
        }
        return false;
    }

}
