package com.saftv.app;

import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import java.util.ArrayList;
import java.util.List;

/**
 * SafTV Tekli Seçim Buton Grubu Yöneticisi (Radio Button Simülatörü)
 * Ayarlar ekranlarındaki tekrarlayan OnClickListener ve buton stil döngülerini tek merkezden yönetir (DRY).
 */
public class SelectorGroupHelper<T> {

    public interface OnSelectedListener<T> {
        void onSelected(T value);
    }

    public static class Option<T> {
        final Button button;
        final T value;
        final String baseTitle;
        final int customInactiveColor;

        public Option(Button button, T value, String baseTitle) {
            this(button, value, baseTitle, AppColors.TEXT_WHITE);
        }

        public Option(Button button, T value, String baseTitle, int customInactiveColor) {
            this.button = button;
            this.value = value;
            this.baseTitle = baseTitle;
            this.customInactiveColor = customInactiveColor;
        }
    }

    private final List<Option<T>> options = new ArrayList<>();
    private OnSelectedListener<T> listener;
    private int activeColor = AppColors.ACTIVE_GREEN;
    private boolean addDotPrefix = true;

    public SelectorGroupHelper() {}

    public SelectorGroupHelper<T> setActiveColor(int color) {
        this.activeColor = color;
        return this;
    }

    public SelectorGroupHelper<T> setAddDotPrefix(boolean addDot) {
        this.addDotPrefix = addDot;
        return this;
    }

    public SelectorGroupHelper<T> setOnSelectedListener(OnSelectedListener<T> listener) {
        this.listener = listener;
        return this;
    }

    public SelectorGroupHelper<T> addOption(Button button, final T value, String title) {
        return addOption(button, value, title, AppColors.TEXT_WHITE);
    }

    public SelectorGroupHelper<T> addOption(Button button, final T value, String title, int inactiveColor) {
        if (button == null) return this;
        final Option<T> opt = new Option<>(button, value, title, inactiveColor);
        options.add(opt);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSelected(value);
                }
            }
        });

        return this;
    }

    public void updateSelection(T currentValue) {
        for (Option<T> opt : options) {
            if (opt.button == null) continue;
            boolean isSelected = (currentValue != null && currentValue.equals(opt.value));
            
            if (isSelected) {
                String title = (addDotPrefix && opt.baseTitle != null && !opt.baseTitle.startsWith("● "))
                        ? "● " + opt.baseTitle
                        : opt.baseTitle;
                opt.button.setText(title);
                opt.button.setTextColor(activeColor);
                opt.button.setTypeface(null, Typeface.BOLD);
                opt.button.setSelected(true);
            } else {
                opt.button.setText(opt.baseTitle);
                opt.button.setTextColor(opt.customInactiveColor);
                opt.button.setTypeface(null, Typeface.NORMAL);
                opt.button.setSelected(false);
            }
        }
    }

    public Button getButtonForValue(T value) {
        if (value == null) return null;
        for (Option<T> opt : options) {
            if (value.equals(opt.value)) {
                return opt.button;
            }
        }
        return null;
    }
}
