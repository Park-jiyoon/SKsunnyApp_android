package com.example.wooisso.sunnyapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class AppPackageSelectListVIew extends Activity implements  SearchView.OnQueryTextListener{
	private final String TAG = AppPackageSelectListVIew.class.getSimpleName();
	private PackageAdapter listViewAdapter;
	public HashSet<String> selectApps = new HashSet<>();
	public boolean flag_AlloweMode = false;
	private SearchView searchView;
	ListView applistView;

	public final static String USE_APP_LIST = "app_list";
	public final static String USE_APP_MODE = "app_allowed_mode";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		selectApps.clear();

		listViewAdapter = new PackageAdapter(this);
		showAppPackageInfoDialog(this);

	}


	private void safeCloseEvent(Boolean result, Intent res) {
		if (res == null) {
			res = new Intent();
		}

		if (result == true)
			setResult(Activity.RESULT_OK, res);
		else
			setResult(Activity.RESULT_CANCELED, res);
		finish();
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}


	// change filter text
	@Override
	public boolean onQueryTextChange(String newText) {
	/*   // use nomal mode searchview
		if (TextUtils.isEmpty(newText))
			applistView.clearTextFilter();
		else {
			applistView.setFilterText(newText);
			applistView.getTextFilter();

		}
	*/

		if (TextUtils.isEmpty(newText))
			listViewAdapter.getFilter().filter(null);
		else {
			listViewAdapter.getFilter().filter(newText);
		}

		return true;

	}

	private void setupSearchView() {
		searchView.setIconifiedByDefault(false);
		searchView.setOnQueryTextListener((SearchView.OnQueryTextListener) this);
		searchView.setSubmitButtonEnabled(false);
		searchView.setQueryHint(getResources().getString(R.string.appname_searchhint));
	}

	/**
	 * 다이얼로그 형태로 리스트를 보여주는 함수
	 */
	private void showAppPackageInfoDialog(final Activity ctx) {

		View layout = getLayoutInflater().inflate(R.layout.app_packagelist_layout, null);

		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setView(layout);
		alertBuilder.setTitle(R.string.client_app_title);

		final Activity tempAcitivy = this;

		Switch vpnOnDefaultSwitch = (Switch) layout.findViewById(R.id.default_allow);

		vpnOnDefaultSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// set action listener
				flag_AlloweMode = isChecked;
				Log.d(TAG, "on check Switch Allowe flag " + isChecked);
			}
		});

		applistView = (ListView) layout.findViewById(R.id.auth_info_listview);
		searchView = (SearchView) layout.findViewById(R.id.searchView);
		applistView.setAdapter(listViewAdapter);
		applistView.setOnItemClickListener(listViewOnItemClickListener);
		applistView.setFastScrollEnabled(true);

		// applistView.setTextFilterEnabled(false); // using popup textview
		alertBuilder.setView(layout);


		// ok
		alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				// 이미 확정된 리스트가 있다.

				ArrayList<String> applist = new ArrayList<String>();

				if (selectApps.size() != 0)
					for (String appPackage : selectApps) {
						applist.add(appPackage);
					}

				Intent res = new Intent();
				res.putStringArrayListExtra(USE_APP_LIST, applist);
				res.putExtra(USE_APP_MODE, flag_AlloweMode);
				setResult(Activity.RESULT_OK, res);

				ctx.finish();
			}
		});

		// cancel
		alertBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				safeCloseEvent(false, null);
			}
		});
		// back key 에 대한 처리
		alertBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					ctx.finish();
				}
				return false;
			}
		});
		alertBuilder.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				listViewAdapter.populateList(tempAcitivy);
			}
		}).start();

		setupSearchView();
	}


	private void addAllowedAppNameAndCheckListBox(CompoundButton buttonView, boolean isChecked) {
		String packageName = (String) buttonView.getTag();
		if (!isChecked) {
			Log.d("vpn", "adding to allowed apps" + packageName);
			selectApps.add(packageName);
			buttonView.setChecked(true);
		} else {
			Log.d("vpn", "removing from allowed apps" + packageName);
			selectApps.remove(packageName);
			buttonView.setChecked(false);
		}
	}



	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}


	static class AppViewHolder {
		public ApplicationInfo mInfo;
		View rootView;
		TextView appName;
		ImageView appIcon;
		CompoundButton checkBox;

		static public AppViewHolder createOrRecycle(LayoutInflater inflater, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.app_info_item, parent, false);

				AppViewHolder holder = new AppViewHolder();
				holder.rootView = convertView;
				holder.appName = (TextView) convertView.findViewById(R.id.app_name);
				holder.appIcon = (ImageView) convertView.findViewById(R.id.app_icon);
				holder.checkBox = (CompoundButton) convertView.findViewById(R.id.app_selected);
				convertView.setTag(holder);


				return holder;
			} else {
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				return (AppViewHolder) convertView.getTag();
			}
		}

	}
	private OnItemClickListener listViewOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.d(TAG, "Use pos" + position);
			AppViewHolder holder = (AppViewHolder)view.getTag();

			addAllowedAppNameAndCheckListBox(holder.checkBox, holder.checkBox.isChecked());

		}
	};

	/**
	 * 앱 패키지 리스트 출력할 어댑터
	 */
	class PackageAdapter extends BaseAdapter implements Filterable {
		final Context mCtx;
		Vector<ApplicationInfo> mPackages;
		final LayoutInflater mInflater;
		final PackageManager mPm;
		ItemFilter mFilter = new ItemFilter();
		Vector<ApplicationInfo> mFilteredData;
		HashMap<String, Integer> alphaIndexer;
		String[] sections;

		PackageAdapter(Context c) {
			super();
			mCtx = c;
			mPm = c.getPackageManager();
			mInflater = LayoutInflater.from(c);

			mPackages = new Vector<>();
			mFilteredData = mPackages;

		}

		// alphaindexer 사전로드  not use
		/*
		public void setPreloadAlphaIndexer() {
			alphaIndexer = new HashMap<String, Integer>();
			int size = mFilteredData.size();

			for (int x = 0; x < size; x++) {

				final ApplicationInfo mInfo = mFilteredData.get(x);
				CharSequence appNameChar = mInfo.loadLabel(mPm);

				if (TextUtils.isEmpty(appNameChar))
					appNameChar = mInfo.packageName;

				String s = appNameChar.toString();
				// get the first letter of the store
				String ch = s.substring(0, 1);
				// convert to uppercase otherwise lowercase a -z will be sorted
				// after upper A-Z
				ch = ch.toUpperCase();
				// put only if the key does not exist
				if (!alphaIndexer.containsKey(ch))
					alphaIndexer.put(ch, x);
			}

			Set<String> sectionLetters = alphaIndexer.keySet();
			// create a list from the set to sort
			ArrayList<String> sectionList = new ArrayList<String>(
					sectionLetters);
            RuleBasedCollator en_korCollator = (RuleBasedCollator)Collator.getInstance(Locale.KOREAN);
			Collections.sort(sectionList, en_korCollator);
			sections = new String[sectionList.size()];
			sections = sectionList.toArray(sections);
		}
		*/

		@Override
		public Filter getFilter() {
			return mFilter;
		}

		private class ItemFilter extends Filter {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
				if (constraint == null ) {
					results.values = mPackages;
					results.count = mPackages.size();
					return results;
				}

				String filterString = constraint.toString().toLowerCase(Locale.getDefault());

				int count = mPackages.size();
				final Vector<ApplicationInfo> nlist = new Vector<>(count);

				for (int i = 0; i < count; i++) {
					ApplicationInfo pInfo = mPackages.get(i);
					CharSequence appName = pInfo.loadLabel(mPm);

					if (TextUtils.isEmpty(appName))
						appName = pInfo.packageName;

					if (appName instanceof String) {
						if (((String) appName).toLowerCase(Locale.getDefault()).contains(filterString))
							nlist.add(pInfo);
					} else {
						if (appName.toString().toLowerCase(Locale.getDefault()).contains(filterString))
							nlist.add(pInfo);
					}
				}
				results.values = nlist;
				results.count = nlist.size();

				return results;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				mFilteredData = (Vector<ApplicationInfo>) results.values;
				notifyDataSetChanged();
			}

		}

		private void populateList(Activity c) {
			List<ApplicationInfo> installedPackages = mPm.getInstalledApplications(PackageManager.GET_META_DATA);

			// Remove apps not using Internet

			int androidSystemUid = 0;
			ApplicationInfo system = null;
			Vector<ApplicationInfo> apps = new Vector<ApplicationInfo>();

			try {
				system = mPm.getApplicationInfo("android", PackageManager.GET_META_DATA);
				androidSystemUid = system.uid;
				apps.add(system);
			} catch (PackageManager.NameNotFoundException e) {
			}


			for (ApplicationInfo app : installedPackages) {
				if (mPm.checkPermission(Manifest.permission.INTERNET, app.packageName) == PackageManager.PERMISSION_GRANTED &&
						app.uid != androidSystemUid) {

					apps.add(app);
				}
			}

			Collections.sort(apps, new AppNameKorSortComparator(mPm));

			mPackages = apps;
			mFilteredData = apps;
			c.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					notifyDataSetChanged();
				}
			});
		}

        public class AppNameKorSortComparator implements Comparator<ApplicationInfo> {
            private final Collator collatorKor = Collator.getInstance(Locale.KOREAN);
            private PackageManager pm;
            private GetHangulChosung getCho;
            String lh_c, rh_c;


            public AppNameKorSortComparator(PackageManager pm) {
                this.pm = pm;
            }

            @Override
            public int compare(ApplicationInfo lhs, ApplicationInfo rhs) {
                CharSequence sa = pm.getApplicationLabel(lhs);
                if (sa == null) {
                    sa = lhs.packageName;
                }
                char getchar = sa.charAt(0);
                if (getCho.isHangul(getchar)) {
                    lh_c = getCho.getChosung(getchar) +"";

                } else {
                    lh_c = sa.charAt(0) + " ";
                }
                // lh_c = sa.charAt(0) + " ";

                CharSequence sb = pm.getApplicationLabel(rhs);
                if (sb == null) {
                    sb = rhs.packageName;
                }

                getchar = sb.charAt(0);
                if (getCho.isHangul(getchar)) {
                    rh_c = getCho.getChosung(getchar) +"";

                } else {
                    rh_c = sb.charAt(0) + " ";
                }
                // rh_c = sb.charAt(0) + "";

                Log.d(TAG, "Compare char " + lh_c + " : " + rh_c);
                return collatorKor.compare(lh_c, rh_c);
            }
        }

		@Override
		public int getCount() { return mFilteredData.size(); }

		@Override
		public Object getItem(int position) {
			return mFilteredData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return mFilteredData.get(position).packageName.hashCode();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppViewHolder viewHolder = AppViewHolder.createOrRecycle(mInflater, convertView, parent);
			convertView = viewHolder.rootView;
			viewHolder.mInfo = mFilteredData.get(position);
			final ApplicationInfo mInfo = mFilteredData.get(position);
			CharSequence appNameChar = mInfo.loadLabel(mPm);

			if (TextUtils.isEmpty(appNameChar))
				appNameChar = mInfo.packageName;
			viewHolder.appName.setText(appNameChar);
			viewHolder.appIcon.setImageDrawable(mInfo.loadIcon(mPm));
			viewHolder.appIcon.setClickable(false);
			viewHolder.appIcon.setFocusable(false);
			viewHolder.checkBox.setTag(mInfo.packageName);
			viewHolder.checkBox.setClickable(false);
			viewHolder.checkBox.setFocusable(false);
			viewHolder.checkBox.setChecked(selectApps.contains(mInfo.packageName));

			return convertView;
		}
	}

}
