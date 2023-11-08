package com.example.delftaiobjectdetector.core.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import java.nio.ByteBuffer;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

public final class YuvToRgbConverter {
    private final RenderScript rs;
    private final ScriptIntrinsicYuvToRGB scriptYuvToRgb;
    private ByteBuffer yuvBits;
    private byte[] bytes;
    private Allocation inputAllocation;
    private Allocation outputAllocation;

    public final synchronized void yuvToRgb(@NotNull Image image, @NotNull Bitmap output) {
        Intrinsics.checkNotNullParameter(image, "image");
        Intrinsics.checkNotNullParameter(output, "output");
        YuvByteBuffer yuvBuffer = new YuvByteBuffer(image, this.yuvBits);
        this.yuvBits = yuvBuffer.getBuffer();
        if (this.needCreateAllocations(image, yuvBuffer)) {
            Type.Builder yuvType = (new Type.Builder(this.rs, Element.U8(this.rs))).setX(image.getWidth()).setY(image.getHeight()).setYuvFormat(yuvBuffer.getType());
            this.inputAllocation = Allocation.createTyped(this.rs, yuvType.create(), 1);
            this.bytes = new byte[yuvBuffer.getBuffer().capacity()];
            Type.Builder rgbaType = (new Type.Builder(this.rs, Element.RGBA_8888(this.rs))).setX(image.getWidth()).setY(image.getHeight());
            this.outputAllocation = Allocation.createTyped(this.rs, rgbaType.create(), 1);
        }

        yuvBuffer.getBuffer().get(this.bytes);
        Allocation var10000 = this.inputAllocation;
        Intrinsics.checkNotNull(var10000);
        var10000.copyFrom(this.bytes);
        var10000 = this.inputAllocation;
        Intrinsics.checkNotNull(var10000);
        var10000.copyFrom(this.bytes);
        this.scriptYuvToRgb.setInput(this.inputAllocation);
        this.scriptYuvToRgb.forEach(this.outputAllocation);
        var10000 = this.outputAllocation;
        Intrinsics.checkNotNull(var10000);
        var10000.copyTo(output);
    }

    private final boolean needCreateAllocations(Image image, YuvByteBuffer yuvBuffer) {
        boolean var4;
        if (this.inputAllocation != null) {
            Allocation var10000 = this.inputAllocation;
            Intrinsics.checkNotNull(var10000);
            Type var3 = var10000.getType();
            Intrinsics.checkNotNullExpressionValue(var3, "inputAllocation!!.type");
            if (var3.getX() == image.getWidth()) {
                var10000 = this.inputAllocation;
                Intrinsics.checkNotNull(var10000);
                var3 = var10000.getType();
                Intrinsics.checkNotNullExpressionValue(var3, "inputAllocation!!.type");
                if (var3.getY() == image.getHeight()) {
                    var10000 = this.inputAllocation;
                    Intrinsics.checkNotNull(var10000);
                    var3 = var10000.getType();
                    Intrinsics.checkNotNullExpressionValue(var3, "inputAllocation!!.type");
                    if (var3.getYuv() == yuvBuffer.getType() && this.bytes.length != yuvBuffer.getBuffer().capacity()) {
                        var4 = false;
                        return var4;
                    }
                }
            }
        }

        var4 = true;
        return var4;
    }

    public YuvToRgbConverter(@NotNull Context context) {
        super();
        this.rs = RenderScript.create(context);
        this.scriptYuvToRgb = ScriptIntrinsicYuvToRGB.create(this.rs, Element.U8_4(this.rs));
        this.bytes = new byte[0];
    }
}
