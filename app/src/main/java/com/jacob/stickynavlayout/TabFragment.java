package com.jacob.stickynavlayout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TabFragment extends Fragment
{
	public static final String TITLE = "title";
	private String mTitle = "Defaut Value";
	private TextView mTextView;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (getArguments() != null)
		{
			mTitle = getArguments().getString(TITLE);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_pager, container, false);
		return view;

	}

	public static TabFragment newInstance()
	{
		TabFragment tabFragment = new TabFragment();
		Bundle bundle = new Bundle();
		tabFragment.setArguments(bundle);
		return tabFragment;
	}

}
