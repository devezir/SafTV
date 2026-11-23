package com.saftv.app;

import android.content.Context;
import android.util.TypedValue;
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
public class EpgProgramAdapter extends BaseScaleAdapter<EpgProgram> {
    private final LayoutInflater inflater;

    public EpgProgramAdapter(Context context) {
        super(context, new ArrayList<EpgProgram>());
        this.inflater = LayoutInflater.from(context);
    }

    public void setPrograms(List<EpgProgram> list) {
        updateList(list);
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
            int padH = getScaledHorizontalPadding();
            int padV = Math.max(2, (int) (6 * scaleFactor * getDensity()));
            convertView.setPadding(padH, padV, padH, padV);

            h.tvTime.setText(p.getFormattedTimeRange());
            h.tvTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, getScaledSp(12));

            h.tvLive.setTextSize(TypedValue.COMPLEX_UNIT_SP, getScaledSp(10));

            h.tvTitle.setText(p.getTitle());
            h.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, getScaledSp(13));

            long now = System.currentTimeMillis();
            boolean isLive = p.isCurrentlyAir(now);

            if (isLive) {
                h.tvLive.setVisibility(View.VISIBLE);
                h.tvTime.setTextColor(AppColors.ACTIVE_GREEN);
                h.tvTitle.setTextColor(AppColors.ACTIVE_GREEN);
            } else {
                h.tvLive.setVisibility(View.GONE);
                h.tvTime.setTextColor(AppColors.AMBER_GOLD);
                h.tvTitle.setTextColor(AppColors.TEXT_WHITE);
            }

            if (p.getDescription() != null && !p.getDescription().isEmpty()) {
                h.tvDesc.setVisibility(View.VISIBLE);
                h.tvDesc.setText(p.getDescription());
                h.tvDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, getScaledSp(11));
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
