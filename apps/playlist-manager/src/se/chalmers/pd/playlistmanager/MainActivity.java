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

/**
 * This is the main activity of the application. It is a fragment activity so that it can host a view pager with
 * fragments. Since this class holds the view pager and its adapter, it contains callback implementations
 * from the fragments. It also has callbacks from the application controller, the search bar and NFC reader.
 */
public class MainActivity extends FragmentActivity implements FragmentCallback, ApplicationController.Callback, QueryTextListener.Callback, NfcReader.NfcCallback {

    private static final int NUMBER_OF_PAGES = 3;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private ApplicationController controller;
    private LoadingDialogFragment searchingDialog;
    private NfcReader nfcReader;

    /**
     * System callback implementations
     */

    /**
     * Sets up the view pager, pager adapter, controller and nfc reader.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(NUMBER_OF_PAGES);
        viewPager.setAdapter(sectionsPagerAdapter);
        controller = new ApplicationController(this, this);
        nfcReader = new NfcReader(this);
    }


    /**
     * Creates the options menu for the application.
     *
     * @param menu the menu being instantiated
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
        searchView.setOnQueryTextListener(new QueryTextListener(this));
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * When an item has been selected in the options menu this method is called by the
     * system. This method creates a connect dialog and shows it to the user.
     *
     * @param item the menu item that was selected
     * @return true if handled by this implementation
     */
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

    /**
     * Pauses the nfc reader when the application pauses.
     */
    @Override
    protected void onPause() {
        super.onPause();
        nfcReader.onPause();
    }

    /**
     * Resumes the nfc reader when the application resumes.
     */
    @Override
    protected void onResume() {
        super.onResume();
        nfcReader.onResume();
    }

    /**
     * Called from the system when a new intent has been received. This method
     * forwards it to the nfc reader.
     *
     * @param intent the intent that was received.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        nfcReader.onNewIntent(intent);
    }

    /**
     * NfcReader callback
     */

    /**
     * Called when the nfc reader has a result from its reading.
     *
     * @param url the url that was read.
     */
    @Override
    public void onNfcResult(String url) {
        controller.connect(url);
    }

    /**
     * Search callbacks
     */

    /**
     * Called from the query text listener on the search field when the search
     * begins. It shows a simpe "searching" dialog message to the user.
     */
    @Override
    public void onSearchBegin() {
        searchingDialog = DialogFactory.buildLoadingDialog(this);
        searchingDialog.show(getFragmentManager(), "searchingDialog");
    }

    /**
     * Called from the query text listener when a result has been received.
     *
     * @param tracks the tracks that matched the search
     */
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

    /**
     * {inheritDoc}
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

    /**
     * {inheritDoc}
     */
    @Override
    public <T extends Object> void onMessageAction(final Action action, final T t) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sectionsPagerAdapter.performAction(action, t);
            }
        });
    }

    /**
     * FragmentCallback implementations
     */

    /**
     * {inheritDoc}
     */
    @Override
    public void onPlayerAction(Action action) {
        controller.performAction(action);
    }

    /**
     * {inheritDoc}
     */
    @Override
    public void onPlayerAction(float position) {
        controller.seek(position);
    }

    /**
     * {inheritDoc}
     */
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
