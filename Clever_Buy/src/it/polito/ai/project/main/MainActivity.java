package it.polito.ai.project.main;

import it.polito.ai.project.R;
import it.polito.ai.project.adapter.NavDrawerListAdapter;
import it.polito.ai.project.fragment.AboutFragment;
import it.polito.ai.project.fragment.HomeFragment;
import it.polito.ai.project.fragment.IMiglioriAffariFragment;
import it.polito.ai.project.fragment.InScadenzaFragment;
import it.polito.ai.project.fragment.InserisciUnProdottoFragment;
import it.polito.ai.project.fragment.LeMieInserzioniFragment;
import it.polito.ai.project.fragment.ListFragment;
import it.polito.ai.project.fragment.ValutaInserzioneFragment;
import it.polito.ai.project.model.NavDrawerItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.http.Header;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	// User Session Manager Class
	UserSessionManager session;


	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	private static ArrayList<ItemHintListFragment> miglioriAffari_itemArrayList = new ArrayList<ItemHintListFragment>();
	//TODO
	private NavDrawerItem i_migliori_affari_Drawew;

	private SparseArray<Fragment> fragmentArray;

	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private static Location currentBestLocation = null;

	public static Location getLocation() {
		return currentBestLocation;
	}

	public static ArrayList<ItemHintListFragment> getMiglioriAffari_itemArrayList(){
		return miglioriAffari_itemArrayList;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Session class instance
		session = new UserSessionManager(getApplicationContext());

		fragmentArray = new SparseArray<Fragment>();


		/* START GPS LOCATION  */

		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location provider.
				if(isBetterLocation(location, currentBestLocation)) { 
					System.out.println("Trovata una posizione migliore. LAT: " + location.getLatitude() + ", LNG: " + location.getLongitude() + ", ACC: " + location.getAccuracy() + ", PROV: " + location.getProvider());
					currentBestLocation = location;
				}
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}
		};

		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 25, locationListener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 25, locationListener);

		//String locationProvider = LocationManager.NETWORK_PROVIDER;
		String locationProvider = LocationManager.NETWORK_PROVIDER;
		currentBestLocation = locationManager.getLastKnownLocation(locationProvider);

		// ad esempio, se per 5 volte non trovo una posizione migliore allora interrompo.
		// Remove the listener you previously added
		//locationManager.removeUpdates(locationListener);

		/* END GPS LOCATION  */

		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// Home
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		// inserisci_un_prodotto
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		// valuta_un_prodotto
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		// cerca_un_prodotto
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
		// le_tue_liste
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		// i_migliori_affari"
		//TODO
		i_migliori_affari_Drawew = new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1));
		navDrawerItems.add(i_migliori_affari_Drawew);
		// in_scadenza
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));
		// about
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(7, -1)));

		
		
		
		// statistiche
		//navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], 0));
		// premium
		//navDrawerItems.add(new NavDrawerItem(navMenuTitles[8], 0));


		// List
		//navDrawerItems.add(new NavDrawerItem(navMenuTitles[10], 0));
		// AquireBarCode
		//navDrawerItems.add(new NavDrawerItem(navMenuTitles[11], 0));
		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
		mDrawerList.setAdapter(adapter);

		aggiungiNotificaDrawer(0);
		
		//TODO chiedi suggerimenti al server
		inviaRichiestaListaDescrizioni();

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
				) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}


	}


	private void inviaRichiestaListaDescrizioni() {
		// TODO voglio gli utimi 40 record della tabella lista desideri + gli ultimi 40 della lista_spesa_prodotti
		RequestParams param = new RequestParams();
		param.put("cmd","lista_descrizioni");
		//param.put("id_lista_desideri",String.valueOf(idLista));
		MyHttpClient.post("/abitudini", param, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				ArrayList<String> arrayDescrizioni = new ArrayList<String>();
				ArrayList<Integer> arrayContatori = new ArrayList<Integer>();
				ArrayList<String> risultatoDescrizioni;
				for (int i = 0; i < response.length(); i++) 
					try {
						String descrizione = response.getJSONObject(i).getString("descrizione");
						Integer contatore = Integer.valueOf( response.getJSONObject(i).getString("contatore") );
						arrayDescrizioni.add(descrizione);
						arrayContatori.add(contatore);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				risultatoDescrizioni = calcolaSuggerimenti(arrayDescrizioni,arrayContatori);
				riceviSuggerimentiDaVisualizzare(risultatoDescrizioni);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				Log.v("ERROR" , "onFailure error : " + throwable.getMessage() + " \n content : " + responseString);
			}
		});
	}



	protected void riceviSuggerimentiDaVisualizzare(ArrayList<String> risultatoDescrizioni) {
		// TODO
		if(risultatoDescrizioni.size()==0)
			return;
		String stringa_lista_descrizioni = "";
		for(String tmp : risultatoDescrizioni){
			stringa_lista_descrizioni += "\"%" + tmp + "%\" ";
		}
		RequestParams param = new RequestParams();
		param.put("cmd","ottieni_suggerimenti_acquisti");
		param.put("latitudine",Double.toString(MainActivity.getLocation().getLatitude()));
		param.put("longitudine",Double.toString(MainActivity.getLocation().getLongitude()));
		param.put("array_descrizioni",stringa_lista_descrizioni);
		MyHttpClient.post("/abitudini", param, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				for (int i = 0; i < response.length(); i++) 
					try {
						if(response.getJSONObject(i).has("id_elemento"))
							continue;
						ItemHintListFragment hint = null;
						DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
						int id_inserzione = response.getJSONObject(i).getInt("id_inserzione");
						boolean selezionato = false;
						String descrizione = response.getJSONObject(i).getString("descrizione");
						DateTime data_fine = formatter.parseDateTime(response.getJSONObject(i).getString("data_fine"));
						String supermercato = response.getJSONObject(i).getString("supermercato");
						String prezzo = response.getJSONObject(i).getString("prezzo");
						String foto = response.getJSONObject(i).getString("foto");
						hint = new ItemHintListFragment(id_inserzione, selezionato, descrizione, data_fine, supermercato, prezzo, foto);
						miglioriAffari_itemArrayList.add(hint);
						//System.out.println("fromServer - " + hint.getItem_id() + " " + hint.getDescrizione() + " " +  hint.getPrezzo() + " " + hint.getSupermercato() + " " + hint.getData_fine());
					} catch (Exception e) {
						e.printStackTrace();
					}
				aggiungiNotificaDrawer(miglioriAffari_itemArrayList.size());
				
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				Log.v("ERROR" , "onFailure error : " + throwable.getMessage() + " \n content : " + responseString);
			}
		});		
	}

	protected void aggiungiNotificaDrawer(int size) {
		// TODO Auto-generated method stub

		i_migliori_affari_Drawew.setCounterVisibility(true);
		i_migliori_affari_Drawew.setCount(String.valueOf(size));
		i_migliori_affari_Drawew.setCounterVisibility(true);
		//navDrawerItems.get(5).setCount(String.valueOf(size));
		adapter.notifyDataSetChanged();
	}

	protected ArrayList<String> calcolaSuggerimenti(ArrayList<String> arrayDescrizione, ArrayList<Integer> arrayContatori) {
		ArrayList<String> risultato = new ArrayList<String>();
		HashMap<String, Integer> cache = new HashMap<String, Integer>();
		String currentWord = null;
		int posizione = 0;
		//Collection<Integer> collection = null;
		for(String i : arrayDescrizione)
		{
			StringTokenizer st = new StringTokenizer(i);
			//---- Split by space ------
			while (st.hasMoreElements()) {
				currentWord = (String) st.nextElement();
				if(cache.containsKey(currentWord)) 				
					cache.put(currentWord, cache.get(currentWord) + arrayContatori.get(posizione));
				else				
					cache.put(currentWord, arrayContatori.get(posizione));
			}
			posizione++;
		}

		Map<String, Integer> map = MapSort.sortByValue(cache);
		Set<String> set = map.keySet();
		if(set.size()>10)
		{
			//TODO inserisci solo i 5 piu occorrenti
			Iterator<String> tmp = set.iterator();
			int esci = 0;
			while(tmp.hasNext() && esci<10)
			{
				risultato.add(tmp.next());
				esci++;
			}
		}
		else
			risultato.addAll(set);
		return  risultato;
	}






	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
	ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_esci:
			finish();
			return true;
		case R.id.action_logout:
			// effettuare il logout, eliminando i dati dalla shared preferences and avviare la splashscreen
			session.setCheckLoginAble( false );
			Intent intent = new Intent(this, SplashScreen.class);
			finish();
			startActivity(intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Called when invalidateOptionsMenu() is triggered
	 * */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		for(int i = 0; i < menu.size(); i++)
			menu.getItem(i).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	public void displayView(int position) {

		// update the main content by replacing fragments
		//		System.out.println("posizione frammento: " + position + "Nome frammento:" +  navMenuTitles[position] );

		String stringFragment = navMenuTitles[position];

		Fragment fragment = null;

		switch(position){
		case 0: 			// home
			if(fragmentArray.get(position) != null)
				fragment = fragmentArray.get(position);
			else {
				fragment = new HomeFragment();
				fragmentArray.append(position, fragment);
			}
			break;
		case 1: 			// inserisci_un_prodotto
			if(fragmentArray.get(position) != null)
				fragment = fragmentArray.get(position);
			else
				fragment = new InserisciUnProdottoFragment();
			break;
		case 2: 			// valuta_un_prodotto
			fragment = new ValutaInserzioneFragment();
			break;
		case 3: 			// le_mie_inserzioni
			fragment = new LeMieInserzioniFragment();
			break;
		case 4: 			// le_mie_liste
			fragment = new ListFragment();
			break;
		case 5: 			// i_migliori_affari
			fragment = new IMiglioriAffariFragment();
			break;
		case 6: 			// in_scadenza
			fragment = new InScadenzaFragment();
			break;
		case 7: 			// about
			if(fragmentArray.get(position) != null)
				fragment = fragmentArray.get(position);
			else {
				fragment = new AboutFragment();
				fragmentArray.append(position, fragment);
			}
			break;
		default:
			System.out.println("Richiesto frammento in posizione: " + position);
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction ft = fragmentManager.beginTransaction();
			ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, 0, 0);
			ft.replace(R.id.frame_container, fragment, stringFragment);
			ft.addToBackStack(null);
			ft.commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void modificaInserzioneById(Integer idInserzione) {
		// do some stuff
		Fragment fragment = new InserisciUnProdottoFragment(); 
		Bundle bundle = new Bundle();
		Integer id = idInserzione;
		bundle.putInt("idInserzione", id);
		fragment.setArguments(bundle);

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction ft = fragmentManager.beginTransaction();
		ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, 0, 0);
		ft.replace(R.id.frame_container, fragment, "modificaInserzione");
		ft.addToBackStack(null);
		ft.commit();

		mDrawerList.setItemChecked(1, true);
		mDrawerList.setSelection(1);
		setTitle(navMenuTitles[1]);

		return;
	}



	/** Determines whether one Location reading is better than the current Location fix
	 * @param location  The new Location that you want to evaluate
	 * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	 */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

}
