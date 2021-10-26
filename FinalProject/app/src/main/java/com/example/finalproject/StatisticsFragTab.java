package com.example.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class StatisticsFragTab extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewpager;
    ArrayList fragmentList = new ArrayList<Fragment>();
    String[] temp = new String[2];
    //String[] temp = {"支出","收入"};


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        //setHasOptionsMenu(true);

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        tabLayout = view.findViewById(R.id.tab_statistics);
        viewpager = view.findViewById(R.id.vp_statistics);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // fragment中嵌套fragment, Manager需要用(getChildFragmentManager())
        MPagerAdapter mPagerAdapter = new MPagerAdapter(getChildFragmentManager());
        initFragment();
        temp[0] = getResources().getString(R.string.str_outcome);
        temp[1] = getResources().getString(R.string.str_income);
        tabLayout.setupWithViewPager(viewpager);
        viewpager.setAdapter(mPagerAdapter);
    }

    private void initFragment() {
        fragmentList.add(new StatisticsFragment_outcome());
        fragmentList.add(new StatisticsFragment_income());
    }


    class MPagerAdapter extends FragmentPagerAdapter {


        public MPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return (Fragment) fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        //返回tablayout的標題文字;
        @Override
        public CharSequence getPageTitle(int position) {
            return temp[position];
        }
    }

}
