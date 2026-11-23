package com.saftv.app;

import android.content.Context;
import android.widget.BaseAdapter;
import java.util.List;

/**
 * SafTV Kanal Listesi Taban Adaptörü
 * ChannelAdapter ve SettingsChannelAdapter arasındaki ortak liste, boyut ve ölçekleme mantığını birleştirir (DRY).
 */
public abstract class BaseChannelAdapter extends BaseAdapter {
    protected final Context context;
    protected List<Channel> channels;
    protected float scaleFactor = 1.0f;

    public BaseChannelAdapter(Context context, List<Channel> channels) {
        this.context = context;
        this.channels = channels;
    }

    public void setTvMode(boolean isTvMode) {
        setUiScale(isTvMode ? 1.30f : 1.0f);
    }

    public void setUiScale(float scaleFactor) {
        this.scaleFactor = scaleFactor;
        notifyDataSetChanged();
    }

    public void updateList(List<Channel> newChannels) {
        this.channels = newChannels;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return channels != null ? channels.size() : 0;
    }

    @Override
    public Channel getItem(int position) {
        return (channels != null && position >= 0 && position < channels.size()) ? channels.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        Channel c = getItem(position);
        return c != null ? c.getId() : position;
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
