package se.chalmers.pd.playlistmanager;

import java.util.ArrayList;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements FragmentCallback, ApplicationController.Callback, QueryTextListener.Callback, NfcReader.NfcCallback {

    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private ApplicationController controller;
    private LoadingDialogFragment searchingDialog;
    private NfcReader nfcReader;

    /**
     * System callback implementations
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        controller = new ApplicationController(this, this);
        nfcReader = new NfcReader(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
        searchView.setOnQueryTextListener(new QueryTextListener(this));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.connect:
                // Build connect dialog with user input when user selects connect from the menu and pass in
                // controller as the receiver of the positive or negative action.
                DialogFactory.buildConnectToUrlDialog(this, controller, controller.getBrokerUrl(), R.string.connect_message).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        nfcReader.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcReader.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        nfcReader.onNewIntent(intent);
    }

    /**
     * NfcReader callback
     */

    @Override
    public void onNfcResult(String url) {
        controller.connect(url);
    }

    /**
     * Search callbacks
     */

    @Override
    public void onSearchBegin() {
        searchingDialog = DialogFactory.buildLoadingDialog(this);
        searchingDialog.show(getFragmentManager(), "searchingDialog");
    }

    @Override
    public void onSearchResult(ArrayList<Track> tracks) {
        searchingDialog.dismiss();
        if (!tracks.isEmpty()) {
            viewPager.setCurrentItem(SectionsPagerAdapter.FIRST_PAGE, true);
            sectionsPagerAdapter.updateSearchResults(tracks);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, getString(R.string.no_tracks_found), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * ApplicationController callbacks
     */

    @Override
    public void onMessageAction(final Action action) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sectionsPagerAdapter.performAction(action);
            }
        });
    }

    @Override
    public void onMessageAction(final float position) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sectionsPagerAdapter.seek(position);
            }
        });
    }

    @Override
    public void onMessageAction(final Action action, final Track track) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sectionsPagerAdapter.performAction(action, track);
            }
        });
    }

    @Override
    public void onMessageAction(final Action action, final ArrayList<Track> playlist) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sectionsPagerAdapter.resetPlaylist();
                for (Track track : playlist) {
                    sectionsPagerAdapter.performAction(Action.add, track);
                }
            }
        });
    }

    /**
     * FragmentCallback implementations
     */

    @Override
    public void onPlayerAction(Action action) {
        controller.performAction(action);
    }

    @Override
    public void onPlayerAction(float position) {
        controller.seek(position);
    }

    @Override
    public void onTrackSelected(Track track) {
        switch (viewPager.getCurrentItem()) {
            case SectionsPagerAdapter.FIRST_PAGE:
                controller.addTrack(track);
                break;
            case SectionsPagerAdapter.SECOND_PAGE:
                // TODO need to send uri data to be able to play selected track
                //onPlayerAction(Action.play);
                break;
        }
    }
}
