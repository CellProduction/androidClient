package io.cell.androidclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.cell.androidclient.model.Area;
import io.cell.androidclient.utils.tasks.AreaLoader;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Активити для первоначальной загрузки текущей для пользователя области.
 */
public class SplashScreenActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Area> {

    private static final int AREA_LOADER_ID = 1;
    private Loader<Area> areaLoader;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .create();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        findViewById(R.id.loadCaptionFrame).setVisibility(VISIBLE);
        findViewById(R.id.connectButton).setVisibility(INVISIBLE);
        areaLoader = LoaderManager.getInstance(this).initLoader(AREA_LOADER_ID, new Bundle(), this);
    }

    public void connectOnClick(View view) {
        findViewById(R.id.loadCaptionFrame).setVisibility(VISIBLE);
        findViewById(R.id.connectButton).setVisibility(INVISIBLE);
        areaLoader.onContentChanged();
    }

    @NonNull
    @Override
    public Loader<Area> onCreateLoader(int id, @Nullable Bundle bundle) {
        if (id == AREA_LOADER_ID) {
            return new AreaLoader(this, null);
        }
        return this.areaLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Area> loader, Area area) {
        if (loader.getId() == AREA_LOADER_ID) {
            AreaLoader areaLoader = (AreaLoader) loader;
            if (areaLoader.isErrors()) {
                findViewById(R.id.loadCaptionFrame).setVisibility(INVISIBLE);
                findViewById(R.id.connectButton).setVisibility(VISIBLE);
                Toast.makeText(this,
                        areaLoader.getErrorMessage(),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            Intent mainIntent = new Intent(this, AreaActivity.class);
            mainIntent.putExtra(Area.class.getCanonicalName(), gson.toJson(area));
            this.startActivity(mainIntent);
            this.finish();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Area> loader) {
    }
}
