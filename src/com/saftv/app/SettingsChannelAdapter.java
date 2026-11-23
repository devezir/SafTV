package com.saftv.app;

import android.content.Context;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

/**
 * SafTV Ayarlar Sekmesi 1 (Kanal Yönetimi) Adaptörü
 */
public class SettingsChannelAdapter extends BaseChannelAdapter {
    private int movingPosition = -1;

    public SettingsChannelAdapter(Context context, List<Channel> channels) {
        super(context, channels);
    }

    public void setMovingPosition(int position) {
        this.movingPosition = position;
        notifyDataSetChanged();
    }

    public int getMovingPosition() {
        return movingPosition;
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
        if (channel == null) return convertView;

        int rowHeight = getScaledRowHeight();
        convertView.setMinimumHeight(rowHeight);
        int hPad = getScaledHorizontalPadding();
        int vPad = getScaledVerticalPadding();
        convertView.setPadding(hPad, vPad, hPad, vPad);

        holder.tvNum.setText(String.valueOf(channel.getId()));
        holder.tvNum.setTextSize(TypedValue.COMPLEX_UNIT_SP, getScaledSp(12));

        holder.tvName.setText(channel.getName());
        holder.tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, getScaledSp(14));

        holder.tvCat.setText(channel.getCategory());
        holder.tvCat.setTextSize(TypedValue.COMPLEX_UNIT_SP, getScaledSp(11));

        holder.tvStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, getScaledSp(11));

        if (position == movingPosition) {
            convertView.setBackgroundColor(AppColors.MOVING_BG);
            holder.tvStatus.setText("↕ TAŞINIYOR");
            holder.tvStatus.setTextColor(AppColors.AMBER_GOLD);
            holder.tvName.setTextColor(AppColors.AMBER_GOLD);
            holder.tvName.setPaintFlags(holder.tvName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        } else {
            convertView.setBackgroundResource(R.drawable.selector_channel_item);
            if (channel.isActive()) {
                holder.tvStatus.setText("AÇIK ✓");
                holder.tvStatus.setTextColor(AppColors.EMERALD_GREEN);
                holder.tvName.setTextColor(AppColors.TEXT_WHITE);
                holder.tvName.setPaintFlags(holder.tvName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            } else {
                holder.tvStatus.setText("KAPALI ✕");
                holder.tvStatus.setTextColor(AppColors.ERROR_RED);
                holder.tvName.setTextColor(AppColors.TEXT_GRAY);
                holder.tvName.setPaintFlags(holder.tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
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
