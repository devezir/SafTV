package com.saftv.app;

import android.content.Context;
import java.util.List;

/**
 * SafTV Kanal Listesi Taban Adaptörü
 * ChannelAdapter ve SettingsChannelAdapter arasındaki ortak kanal ID ve TV modu mantığını yönetir (DRY).
 */
public abstract class BaseChannelAdapter extends BaseScaleAdapter<Channel> {

    public BaseChannelAdapter(Context context, List<Channel> channels) {
        super(context, channels);
    }

    public void setTvMode(boolean isTvMode) {
        setUiScale(isTvMode ? 1.30f : 1.0f);
    }

    @Override
    public long getItemId(int position) {
        Channel c = getItem(position);
        return c != null ? c.getId() : position;
    }
}
