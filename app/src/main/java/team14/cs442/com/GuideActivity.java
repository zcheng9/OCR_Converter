package team14.cs442.com;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends Activity {
    private List<View> views;
    private ViewPager mPager;
    private ImageButton mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        mPager = (ViewPager) findViewById(R.id.guide_viewpager);
        mBtn = (ImageButton) findViewById(R.id.guide_btn);

        initPager();
        mPager.setAdapter(new ViewPagerAdapter(views));
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == views.size() - 1) {
                    mBtn.setVisibility(View.VISIBLE);
                } else {
                    mBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHome();
            }
        });
    }

    private void initPager() {
        views = new ArrayList<View>();
        int[] images = new int[] {R.drawable.new01, R.drawable.new02, R.drawable.new03, R.drawable.new04 };
        int[] texts = new int[] { R.drawable.new_text1, R.drawable.new_text2, R.drawable.new_text3, R.drawable.new_text4};
        for (int i=0; i < images.length; i++) {
            views.add(initView(images[i], texts[i]));
        }
    }

    private View initView(int res,int text){
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_guide, null);
        ImageView imageView = (ImageView)view.findViewById(R.id.iguide_img);
        ImageView textview = (ImageView)view.findViewById(R.id.iguide_text);
        imageView.setImageResource(res);
        textview.setImageResource(text);
        return view;
    }

    private void openHome(){
        Intent intent = new Intent(getApplicationContext(),ImageActivity.class);
        startActivity(intent);
        finish();
    }


    class ViewPagerAdapter extends PagerAdapter {
        private List<View> data;

        public ViewPagerAdapter(List<View> data) {
            super();
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(data.get(position));
            return data.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(data.get(position));
        }
    }
}