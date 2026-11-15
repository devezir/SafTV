package com.saftv.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class ChannelAdapter extends BaseAdapter {
    private final Context context;
    private List<Channel> channels;
    private int currentPlayingId = 1;
    private float scaleFactor = 1.0f;

    public ChannelAdapter(Context context, List<Channel> channels) {
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

    public void setCurrentPlayingId(int channelId) {
        this.currentPlayingId = channelId;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_channel, parent, false);
            holder = new ViewHolder();
            holder.tvNum = convertView.findViewById(R.id.tv_item_num);
            holder.tvName = convertView.findViewById(R.id.tv_item_name);
            holder.tvCat = convertView.findViewById(R.id.tv_item_cat);
            holder.tvLive = convertView.findViewById(R.id.tv_item_live);
            holder.tvFav = convertView.findViewById(R.id.tv_item_fav);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Channel channel = getItem(position);
        float density = context.getResources().getDisplayMetrics().density;
        int rowHeight = (int) (38 * scaleFactor * density);
        convertView.setMinimumHeight(rowHeight);
        int vPad = Math.max(2, (int) (5 * scaleFactor * density));
        int hPad = (int) (10 * scaleFactor * density);
        convertView.setPadding(hPad, vPad, hPad, vPad);

        holder.tvNum.setText(String.valueOf(channel.getId()));
        holder.tvNum.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, Math.round(12 * scaleFactor));
        int badgeSz = (int) (28 * scaleFactor * density);
        holder.tvNum.setMinimumWidth(badgeSz);
        holder.tvNum.setMinimumHeight(badgeSz);

        holder.tvName.setText(channel.getName());
        holder.tvName.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, Math.round(14 * scaleFactor));

        holder.tvCat.setText(channel.getCategory());
        holder.tvCat.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, Math.round(11 * scaleFactor));

        boolean isFav = ChannelManager.getInstance().isFavorite(channel.getId(), context);
        if (holder.tvFav != null) {
            holder.tvFav.setVisibility(isFav ? View.VISIBLE : View.GONE);
            holder.tvFav.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, Math.round(13 * scaleFactor));
        }

        if (channel.getId() == currentPlayingId) {
            holder.tvLive.setText("● OYNUYOR");
            holder.tvLive.setTextColor(0xFF3B82F6); // Blue
            convertView.setSelected(true);
        } else {
            holder.tvLive.setText("CANLI");
            holder.tvLive.setTextColor(0xFF10B981); // Emerald green
            convertView.setSelected(false);
        }
        holder.tvLive.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, Math.round(10 * scaleFactor));

        return convertView;
    }

    static class ViewHolder {
        TextView tvNum;
        TextView tvName;
        TextView tvCat;
        TextView tvLive;
        TextView tvFav;
    }
}
