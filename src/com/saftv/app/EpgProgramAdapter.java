package com.saftv.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Saf TV - 24 Saatlik EPG Program Listesi Adaptörü
 * Günlük yayın akışını canlı rozet ve saat formatıyla listeler.
 */
public class EpgProgramAdapter extends BaseAdapter {
    private final Context context;
    private final LayoutInflater inflater;
    private List<EpgProgram> programs = new ArrayList<>();

    public EpgProgramAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setPrograms(List<EpgProgram> list) {
        this.programs = (list != null) ? list : new ArrayList<EpgProgram>();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return programs.size();
    }

    @Override
    public EpgProgram getItem(int position) {
        if (position >= 0 && position < programs.size()) {
            return programs.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder h;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_epg_program, parent, false);
            h = new ViewHolder();
            h.tvTime = (TextView) convertView.findViewById(R.id.tv_epg_item_time);
            h.tvLive = (TextView) convertView.findViewById(R.id.tv_epg_item_live);
            h.tvTitle = (TextView) convertView.findViewById(R.id.tv_epg_item_title);
            h.tvDesc = (TextView) convertView.findViewById(R.id.tv_epg_item_desc);
            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }

        EpgProgram p = getItem(position);
        if (p != null) {
            h.tvTime.setText(p.getFormattedTimeRange());
            h.tvTitle.setText(p.getTitle());

            long now = System.currentTimeMillis();
            boolean isLive = p.isCurrentlyAir(now);

            if (isLive) {
                h.tvLive.setVisibility(View.VISIBLE);
                h.tvTime.setTextColor(AppColors.ACTIVE_GREEN);
                h.tvTitle.setTextColor(AppColors.ACTIVE_GREEN);
            } else {
                h.tvLive.setVisibility(View.GONE);
                h.tvTime.setTextColor(AppColors.AMBER_GOLD);
                h.tvTitle.setTextColor(0xFFFFFFFF);
            }

            if (p.getDescription() != null && !p.getDescription().isEmpty()) {
                h.tvDesc.setVisibility(View.VISIBLE);
                h.tvDesc.setText(p.getDescription());
            } else {
                h.tvDesc.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView tvTime;
        TextView tvLive;
        TextView tvTitle;
        TextView tvDesc;
    }
}
