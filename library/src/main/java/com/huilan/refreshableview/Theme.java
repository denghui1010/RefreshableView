package com.huilan.refreshableview;

import android.content.Context;

/**
 * 主题
 * Created by liudenghui on 14-11-19.
 */
public class Theme {
    public static int def_header_text_color = 0xff585858;//header的字体颜色
    public static int def_footer_text_color = 0xff585858;//footer的字体颜色

    public static int getValue(Context context, int id) {
        if (id == R.color.def_header_text_color) {
            return def_header_text_color == -1 ? context.getResources().getColor(R.color.def_header_text_color) : def_header_text_color;
        } else if (id == R.color.def_footer_text_color) {
            return def_footer_text_color == -1 ? context.getResources().getColor(R.color.def_footer_text_color) : def_footer_text_color;
        }
        return 0;
    }
}
