package personal.andrewthompson.task.task;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TabHost;

import static personal.andrewthompson.task.task.Constants.LIST_COLOR;
import static personal.andrewthompson.task.task.Constants.TASK_NAMES;
import static personal.andrewthompson.task.task.Constants.TASK_NOTES;

/**
 * Created by Andrew Thompson on 6/10/15.
 */
public class AddTaskTabFragment extends DialogFragment {
    private FragmentTabHost tabHost;
    private ViewPager viewPager;
    private AddTaskAdapter adapter;

    public static final AddTaskTabFragment newInstance(String[] names, String[] notes, int color) {
        AddTaskTabFragment f = new AddTaskTabFragment();
        Bundle bdl = new Bundle(3);
        bdl.putStringArray(TASK_NAMES, names);
        bdl.putStringArray(TASK_NOTES, notes);
        bdl.putInt(LIST_COLOR, color);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_task_dialog, container);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        tabHost = (FragmentTabHost) view.findViewById(R.id.tabs);

        tabHost.setup(getActivity(), getChildFragmentManager());
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("New"), Fragment.class, null);
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("From Past"), Fragment.class, null);

        adapter = new AddTaskAdapter(getChildFragmentManager(), getArguments());

        viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setAdapter(adapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                tabHost.setCurrentTab(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                int i = tabHost.getCurrentTab();
                viewPager.setCurrentItem(i);
            }
        });

        return view;
    }

    private class AddTaskAdapter extends FragmentPagerAdapter {
        private Bundle bundle;
        private final static int NEW_TASK_FRAGMENT_ID = 0, PAST_TASK_FRAGMENT_ID = 1;

        public AddTaskAdapter(FragmentManager fm, Bundle b) {
            super(fm);
            bundle = b;
        }

        @Override
        public Fragment getItem(int num) {
            Fragment fragment = null;
            if (num == NEW_TASK_FRAGMENT_ID) {
                // new task fragment
                fragment = new AddNewTaskFragment();
            } else if (num == PAST_TASK_FRAGMENT_ID) {
                // past task list fragment
                fragment = new AddPastTaskFragment();
                fragment.setArguments(bundle);
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}