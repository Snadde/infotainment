package se.chalmers.pd.playlistmanager;

import java.util.ArrayList;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.SearchView;

public class MainActivity extends FragmentActivity implements ApplicationController.Callback, QueryTextListener.Callback, NfcReader.NFCCallback {

	private SectionsPagerAdapter sectionsPagerAdapter;
	private ViewPager viewPager;
	private ApplicationController controller;
	private LoadingDialogFragment loadingDialog;
	private NfcReader nfcReader;

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
	public void onSearchBegin() {
		loadingDialog = DialogFactory.buildLoadingDialog();
		loadingDialog.show(getFragmentManager(), "loadingDialog");
	}

	@Override
	public void onSearchResult(ArrayList<Track> tracks) {
		loadingDialog.dismiss();
		viewPager.setCurrentItem(SectionsPagerAdapter.FIRST_PAGE, true);
		sectionsPagerAdapter.updateResults(tracks);
	}
	
	public void onTrackSelected(Track track) {
		switch (viewPager.getCurrentItem()) {
		case SectionsPagerAdapter.FIRST_PAGE:
			controller.addTrack(track);
			break;
		case SectionsPagerAdapter.SECOND_PAGE:
			//onPlayerAction(Action.play); need to send uri data to be able to play selected track
			break;
		}
	}
	
	@Override
	public void resetPlaylist() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				sectionsPagerAdapter.resetPlaylist();
			}
		});
	}
	
	@Override
	public void onUpdatePlaylist(final Track track) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				sectionsPagerAdapter.addToPlaylist(track);
			}
		});
	}

	public void onPlayerAction(Action action) {
		controller.performAction(action);
	}
	
	
	@Override
	public void onMessageAction(final Action action) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				sectionsPagerAdapter.updateAction(action);
			}
		});
	}

	public void updatePlayer(Track track) {
		sectionsPagerAdapter.updatePlayer(track);
	}
	
	public void onNFCResult(String url) {
		controller.connect(url);		
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
}
