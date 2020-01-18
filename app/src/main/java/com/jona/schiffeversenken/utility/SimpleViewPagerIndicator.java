package com.jona.schiffeversenken.utility;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jona.schiffeversenken.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jarrod Robins, Papercloud
 * @see https://github.com/jarrodrobins/SimpleViewPagerIndicator
 */
public class SimpleViewPagerIndicator extends LinearLayout implements OnPageChangeListener {
    @SuppressWarnings("unused")
    private static final String TAG = SimpleViewPagerIndicator.class.getSimpleName();

    private final Context context;
    private ViewPager pager;
    private final OnClickListener itemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();

            pager.setCurrentItem(position);
        }
    };
    private OnPageChangeListener onPageChangeListener;
    private LinearLayout itemContainer;
    private List<ImageView> items;

    public SimpleViewPagerIndicator(Context context) {
        super(context);
        this.context = context;
        setup();
    }

    public SimpleViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setup();
    }

    public SimpleViewPagerIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        setup();
    }

    public OnPageChangeListener getOnPageChangeListener() {
        return onPageChangeListener;
    }

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }

    public ViewPager getViewPager() {
        return pager;
    }

    @SuppressWarnings("deprecation")
    public void setViewPager(ViewPager pager) {
        this.pager = pager;
        this.pager.setOnPageChangeListener(this);
        notifyDataSetChanged();
    }

    /**
     * Notifies the pager indicator that the data set has changed. Be sure to
     * notify the pager as well (though you may wish to place that call in here
     * yourself).
     */
    public void notifyDataSetChanged() {
        if (pager != null && pager.getAdapter() != null) {

            // remove the old items (if any exist)
            itemContainer.removeAllViews();

            // I'm sure this could be optimised a lot more, eg,
            // by reusing existing ImageViews, but it
            // does the job well enough for now.
            items.removeAll(items);
            // now create the new items.
            for (int i = 0; i < pager.getAdapter().getCount(); i++) {

                LayoutInflater inflater = LayoutInflater.from(context);
                ImageView item = (ImageView) inflater.inflate(R.layout.view_pager_item, null);

                if (i == pager.getCurrentItem()) {
                    MyColorFilter.filter(context, item, R.color.accentColor, R.drawable.pageindicator_selected);
//                    item.setImageResource(R.drawable.pageindicator_selected);
                } else {
                    MyColorFilter.filter(context, item, R.color.accentColor, R.drawable.pageindicator_not_selected);
//                    item.setImageResource(R.drawable.pageindicator_not_selected);
                }

                item.setTag(i);
                item.setOnClickListener(itemClickListener);
                items.add(item);

                itemContainer.addView(item);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (this.onPageChangeListener != null) {
            this.onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (this.onPageChangeListener != null) {
            this.onPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void onPageSelected(int position) {
        setCurrentItem(position);
        if (this.onPageChangeListener != null) {
            this.onPageChangeListener.onPageSelected(position);
        }
    }

    private void setCurrentItem(int position) {
        if (pager != null && pager.getAdapter() != null) {
            int numberOfItems = pager.getAdapter().getCount();

            for (int i = 0; i < numberOfItems; i++) {
                ImageView item = items.get(i);
                if (item != null) {
                    if (i == position) {
                        MyColorFilter.filter(context, item, R.color.accentColor, R.drawable.pageindicator_selected);
//                        item.setImageResource(R.drawable.pageindicator_selected);
                    } else {
                        MyColorFilter.filter(context, item, R.color.accentColor, R.drawable.pageindicator_not_selected);
//                        item.setImageResource(R.drawable.pageindicator_not_selected);
                    }
                }
            }
        }
    }

    private void setup() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            inflater.inflate(R.layout.view_pager_indicator, this);

            itemContainer = (LinearLayout) findViewById(R.id.pager_indicator_container);

            items = new ArrayList<ImageView>();
        }
    }
}
