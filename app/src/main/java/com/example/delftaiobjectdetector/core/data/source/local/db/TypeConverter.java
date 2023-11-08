package com.example.delftaiobjectdetector.core.data.source.local.db;

import android.graphics.RectF;

public class TypeConverter {

    @androidx.room.TypeConverter
    public static RectF toRectF(String value) {
        String[] split = value.split(",");
        return new RectF(
                Float.parseFloat(split[0]),
                Float.parseFloat(split[1]),
                Float.parseFloat(split[2]),
                Float.parseFloat(split[3])
        );
    }

    @androidx.room.TypeConverter
    public static String fromRectF(RectF rectF) {
        return rectF.left + "," + rectF.top + "," + rectF.right + "," + rectF.bottom;
    }
}
