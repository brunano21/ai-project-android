package it.polito.ai.project.main;

import it.polito.ai.project.adapter.NavDrawerListAdapter;
import it.polito.ai.project.andoidside.R;
import it.polito.ai.project.database.DBHelper;
import it.polito.ai.project.database.Libri;
import it.polito.ai.project.fragment.AboutFragment;
import it.polito.ai.project.fragment.AquireBarCodeFragment;
import it.polito.ai.project.fragment.HomeFragment;
import it.polito.ai.project.fragment.HomeRegistrationLoginFragment;
import it.polito.ai.project.fragment.InserisciUnProdottoFragment;
import it.polito.ai.project.fragment.ListFragment;
import it.polito.ai.project.model.NavDrawerItem;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends Activity {
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Intent intent = new Intent(this, MainService.class);
		startService(intent);
		
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
		// le_tue_liste
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1), true, "22"));
		// in_scadenza
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		// valuta_un_prodotto
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "50+"));
		// inserisci_un_prodotto
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		// cerca_un_prodotto
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
		// statistiche
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));
		// i_migliori_affari
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(7, -1)));
		// premium
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[8], navMenuIcons.getResourceId(8, -1)));
		// about
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[9], navMenuIcons.getResourceId(9, -1)));
		

		// List
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[10], navMenuIcons.getResourceId(10, -1)));
		// AquireBarCode
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[11], navMenuIcons.getResourceId(11, -1)));
		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

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


		//creo l'helper per aprire il DB
        DBHelper databaseHelper = new DBHelper(this);
        //apro il DB sia in lettura che in scrittura
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
 
        //Popolo il DB con un pò di dati
        Libri.insertLibro(db, "Divina Commedia", 15, "Dante nell'inferno, purgatorio, paradiso");
        Libri.insertLibro(db, "Promessi Sposi", 12, "Storia di un amore");
        Libri.insertLibro(db, "De bello gallico", 5, "La guerra in gallia di Cesare");
 
        System.err.println("Inseriti 3 libri");
 
        System.err.println("Inseriti 3 clienti");
 
        //Assegno a un cursore tutti i lirbi trovati nel DB
        Cursor c = Libri.getAllLibri(db);
        try
        {
            System.err.println("Elenco libri:");
            stampaAll(c);
 
           
 
            System.err.println("Cancello libro con id 1");
            Libri.deleteLibro(db, 1);
 
            c = Libri.getAllLibri(db);
            System.err.println("Elenco libri, deve mancare id 1:");
            stampaAll(c);
 
            System.err.println("Modifico libro con id 2, metto prezzo a 23");
            Libri.updateLibro(db, 2, "De bello gallico", 23, "La guerra in gallia di Cesare");
 
            c = Libri.getAllLibri(db);
            System.err.println("Elenco libri, il prezzo di id 2 deve essere 23:");
            stampaAll(c);
        }
        finally
        {
            //Chiudo il cursore e il db
            c.close();
            db.close();
            System.err.println("Ho chiuso il cursore e il db");
        }
    }
 
    /**
     * Prende il cursore passato, lo scorre fino alla fine e a mano a mano stampa i record trovati.
     * @param c
     */
    public void stampaAll(Cursor c){
        while (c.moveToNext())
        {
            System.err.println("informaticoonline.it " + c.getLong(0) + " " + 
                c.getString(1) + " " + c.getString(2) + " " + c.getString(3));
        }
    }	
		

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
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
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		boolean tmpFlagLog_registrato = true;
		// update the main content by replacing fragments
		String stringFragment = navMenuTitles[position];
		
		Fragment fragment = null;
		
		if(tmpFlagLog_registrato){
			if( "Home".equals(stringFragment) )
				fragment = new HomeFragment();
			else
			if( "List".equals(stringFragment) )
				fragment = new ListFragment();
			else
			if( "AquireBarCode".equals(stringFragment) )
				fragment = new AquireBarCodeFragment();
			else
			if( "About".equals(stringFragment) )
				fragment = new AboutFragment();
			else
			if( "Inserisci un prodotto".equals(stringFragment) )
				fragment = new InserisciUnProdottoFragment();
		}
		else
		{
			fragment = new HomeRegistrationLoginFragment();
		}
		/*switch (position) {
		case 0:
			fragment = new HomeFragment();
			break;
		case 1:
			fragment = new FindPeopleFragment();
			break;
		case 2:
			fragment = new PhotosFragment();
			break;
		case 3:
			fragment = new CommunityFragment();
			break;
		case 4:
			fragment = new PagesFragment();
			break;
		case 5:
			fragment = new WhatsHotFragment();
			break;

		case 6:
			fragment = new AboutFragment();
			break;

		default:
			break;
		}*/

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

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

}
