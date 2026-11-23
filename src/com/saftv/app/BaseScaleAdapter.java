package com.saftv.app;

import android.content.Context;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * SafTV Genel Ölçeklenebilir Liste Adaptörü
 * Tüm liste adaptörleri (kanallar, ayarlar, EPG akışı) arasındaki UI ölçeği ve boyutlandırma mantığını tek merkezden yönetir (DRY).
 */
public abstract class BaseScaleAdapter<T> extends BaseAdapter {
    protected final Context context;
    protected List<T> items;
    protected float scaleFactor = 1.0f;

    public BaseScaleAdapter(Context context, List<T> items) {
        this.context = context;
        this.items = (items != null) ? items : new ArrayList<T>();
    }

    public void setUiScale(float scaleFactor) {
        this.scaleFactor = scaleFactor;
        notifyDataSetChanged();
    }

    public void updateList(List<T> newItems) {
        this.items = (newItems != null) ? newItems : new ArrayList<T>();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return (items != null && position >= 0 && position < items.size()) ? items.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    protected float getDensity() {
        return context.getResources().getDisplayMetrics().density;
    }

    protected int getScaledRowHeight() {
        return (int) (38 * scaleFactor * getDensity());
    }

    protected int getScaledHorizontalPadding() {
        return (int) (10 * scaleFactor * getDensity());
    }

    protected int getScaledVerticalPadding() {
        return Math.max(2, (int) (5 * scaleFactor * getDensity()));
    }

    protected int getScaledSp(float baseSp) {
        return Math.round(baseSp * scaleFactor);
    }
}
