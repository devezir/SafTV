package com.saftv.app;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

/**
 * SafTV Sol Çekmece Canlı Kanal Listesi Adaptörü
 */
public class ChannelAdapter extends BaseChannelAdapter {
    private int currentPlayingId = 1;

    public ChannelAdapter(Context context, List<Channel> channels) {
        super(context, channels);
    }

    public void setCurrentPlayingId(int channelId) {
        this.currentPlayingId = channelId;
        notifyDataSetChanged();
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
            holder.tvEpg = convertView.findViewById(R.id.tv_item_epg);
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
        int badgeSz = (int) (28 * scaleFactor * getDensity());
        holder.tvNum.setMinimumWidth(badgeSz);
        holder.tvNum.setMinimumHeight(badgeSz);

        holder.tvName.setText(channel.getName());
        holder.tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, getScaledSp(14));

        holder.tvCat.setText(channel.getCategory());
        holder.tvCat.setTextSize(TypedValue.COMPLEX_UNIT_SP, getScaledSp(11));

        boolean isFav = ChannelManager.getInstance().isFavorite(channel.getId(), context);
        if (holder.tvFav != null) {
            holder.tvFav.setVisibility(isFav ? View.VISIBLE : View.GONE);
            holder.tvFav.setTextSize(TypedValue.COMPLEX_UNIT_SP, getScaledSp(13));
        }

        if (channel.getId() == currentPlayingId) {
            holder.tvLive.setText("● OYNUYOR");
            holder.tvLive.setTextColor(AppColors.ACCENT_BLUE);
            convertView.setSelected(true);
        } else {
            holder.tvLive.setText("CANLI");
            holder.tvLive.setTextColor(AppColors.EMERALD_GREEN);
            convertView.setSelected(false);
        }
        holder.tvLive.setTextSize(TypedValue.COMPLEX_UNIT_SP, getScaledSp(10));

        if (holder.tvEpg != null) {
            EpgManager epgMgr = EpgManager.getInstance(context);
            EpgProgram p = epgMgr.getCurrentProgram(channel);
            if (p != null) {
                holder.tvEpg.setVisibility(View.VISIBLE);
                holder.tvEpg.setText("🔴 " + p.getTitle());
                holder.tvEpg.setTextSize(TypedValue.COMPLEX_UNIT_SP, getScaledSp(11));
            } else {
                holder.tvEpg.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tvNum;
        TextView tvName;
        TextView tvCat;
        TextView tvLive;
        TextView tvFav;
        TextView tvEpg;
    }
}
