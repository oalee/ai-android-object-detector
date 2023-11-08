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

@Metadata(
        mv = {1, 9, 0},
        k = 1,
        d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0018\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J\u0016\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0019\u001a\u00020\u001aR\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0016\u0010\n\u001a\n \f*\u0004\u0018\u00010\u000b0\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\r\u001a\n \f*\u0004\u0018\u00010\u000e0\u000eX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u001b"},
        d2 = {"Lcom/example/delftaiobjectdetector/core/camera/YuvToRgbConverter;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "bytes", "", "inputAllocation", "Landroid/renderscript/Allocation;", "outputAllocation", "rs", "Landroid/renderscript/RenderScript;", "kotlin.jvm.PlatformType", "scriptYuvToRgb", "Landroid/renderscript/ScriptIntrinsicYuvToRGB;", "yuvBits", "Ljava/nio/ByteBuffer;", "needCreateAllocations", "", "image", "Landroid/media/Image;", "yuvBuffer", "Lcom/example/delftaiobjectdetector/core/camera/YuvByteBuffer;", "yuvToRgb", "", "output", "Landroid/graphics/Bitmap;", "DelftAI_Object_Detector.app.main"}
)
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
