package com.my.shopee.myshopee.Activities;

import android.os.Bundle;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.my.shopee.myshopee.Activities.ui.main.SectionsPagerAdapter;
import com.my.shopee.myshopee.R;
import com.my.shopee.myshopee.fragments.OtpFragment;
import com.my.shopee.myshopee.fragments.Tab1;
import com.my.shopee.myshopee.fragments.Tab2;
import com.my.shopee.myshopee.fragments.Tab3;
import com.my.shopee.myshopee.fragments.listAdsFragment;

import java.util.ArrayList;
import java.util.List;

public class AdminPanelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        Toolbar toolbar = (Toolbar) findViewById(R.id.xtoolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab1(), "List");
        adapter.addFragment(new Tab2(), "Advertisements");
        adapter.addFragment(new Tab3(), "OTP");
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}