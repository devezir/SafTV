package com.saftv.app;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class SettingsChannelAdapter extends BaseAdapter {
    private final Context context;
    private final List<Channel> channels;
    private float scaleFactor = 1.0f;

    public SettingsChannelAdapter(Context context, List<Channel> channels) {
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

    @Override
    public int getCount() {
        return channels.size();
    }

    @Override
    public Channel getItem(int position) {
        return channels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return channels.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_settings_channel, parent, false);
            holder = new ViewHolder();
            holder.tvNum = convertView.findViewById(R.id.tv_settings_num);
            holder.tvName = convertView.findViewById(R.id.tv_settings_name);
            holder.tvCat = convertView.findViewById(R.id.tv_settings_cat);
            holder.tvStatus = convertView.findViewById(R.id.tv_settings_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Channel channel = getItem(position);
        float density = context.getResources().getDisplayMetrics().density;
        int rowHeight = (int) (38 * scaleFactor * density);
        convertView.setMinimumHeight(rowHeight);
        int vPad = Math.max(2, (int) (4 * scaleFactor * density));
        int hPad = (int) (10 * scaleFactor * density);
        convertView.setPadding(hPad, vPad, hPad, vPad);

        holder.tvNum.setText(String.valueOf(channel.getId()));
        holder.tvNum.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, Math.round(12 * scaleFactor));

        holder.tvName.setText(channel.getName());
        holder.tvName.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, Math.round(14 * scaleFactor));

        holder.tvCat.setText(channel.getCategory());
        holder.tvCat.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, Math.round(11 * scaleFactor));

        holder.tvStatus.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, Math.round(11 * scaleFactor));

        if (channel.isActive()) {
            holder.tvStatus.setText("AÇIK ✓");
            holder.tvStatus.setTextColor(0xFF10B981); // Emerald Green
            holder.tvName.setTextColor(0xFFFFFFFF); // White
            holder.tvName.setPaintFlags(holder.tvName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        } else {
            holder.tvStatus.setText("KAPALI ✕");
            holder.tvStatus.setTextColor(0xFFEF4444); // Red
            holder.tvName.setTextColor(0xFF64748B); // Muted gray
            holder.tvName.setPaintFlags(holder.tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tvNum;
        TextView tvName;
        TextView tvCat;
        TextView tvStatus;
    }
}
