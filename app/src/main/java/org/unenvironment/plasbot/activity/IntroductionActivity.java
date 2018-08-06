package org.unenvironment.plasbot.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.unenvironment.plasbot.R;

import java.util.ArrayList;
import java.util.List;

public class IntroductionActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private Button btnNext;
    List<PageItem> items;
    private IntroductionViewPager myViewPagerAdapter;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    boolean authorised;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        viewPager = findViewById(R.id.view_pager);
        btnNext = findViewById(R.id.btn_next);

        prefs = PreferenceManager.getDefaultSharedPreferences(IntroductionActivity.this);
        authorised = prefs.getBoolean("authorise", false);
        editor = prefs.edit();

        setupPages();

        myViewPagerAdapter = new IntroductionViewPager();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        viewPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.viewpager_margin_overlap));
        viewPager.setOffscreenPageLimit(4);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (viewPager.getCurrentItem() == items.size() - 1) {
                    btnNext.setText("Get Started");
                } else {
                    btnNext.setText("Next");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = viewPager.getCurrentItem() + 1;
                if (current < items.size()) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    editor.putBoolean("show_intro", false);
                    editor.commit();
                    Intent intent;
                    if (authorised) {
                        intent = new Intent(IntroductionActivity.this, MainActivity.class);
                    } else {
                        intent = new Intent(IntroductionActivity.this, AuthorizationActivity.class);
                    }
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void bottomProgressDots(int current_index) {
        LinearLayout dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        ImageView[] dots = new ImageView[items.size()];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            int width_height = 15;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(width_height, width_height));
            params.setMargins(10, 10, 10, 10);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.shape_circle);
            dots[i].setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[current_index].setImageResource(R.drawable.shape_circle);
            dots[current_index].setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        }
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(final int position) {
            bottomProgressDots(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void setupPages(){
        items = new ArrayList<>();
        PageItem item = new PageItem();

        item.setImage(R.drawable.ic_photo_camera);
        item.setTitle("Take a Photo");
        item.setDescription("Take a photo of you holding a plastic bottle");

        items.add(item);

        item = new PageItem();
        item.setImage(R.drawable.ic_image);
        item.setTitle("Upload Image");
        item.setDescription("Upload the photo to the server and let the machine determine what plastic it is");

        items.add(item);

        item = new PageItem();
        item.setImage(R.drawable.ic_poster);
        item.setTitle("Customized Poster");
        item.setDescription("After a short wait you will get a customized poster");

        items.add(item);

        item = new PageItem();
        item.setImage(R.drawable.ic_friendship);
        item.setTitle("Share with Friends");
        item.setDescription("You can then download and shre the photo with your friends across all social media platforms");

        items.add(item);
    }

    public class IntroductionViewPager extends PagerAdapter{
        public LayoutInflater layoutInflater;

        public IntroductionViewPager(){}

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.item_card_wizard, container, false);

            PageItem item = items.get(position);

            ((TextView) view.findViewById(R.id.title)).setText(item.getTitle());
            ((TextView) view.findViewById(R.id.description)).setText(item.getDescription());
            ((ImageView) view.findViewById(R.id.image)).setImageResource(item.getImage());

            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    private class PageItem{
        private int image;
        private String title, description;

        public int getImage() {
            return image;
        }

        public void setImage(int image) {
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
