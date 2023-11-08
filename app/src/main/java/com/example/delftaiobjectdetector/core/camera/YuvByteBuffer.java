package com.example.delftaiobjectdetector.core.camera;

// YuvByteBuffer.java


import android.media.Image;
import java.nio.ByteBuffer;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public  class YuvByteBuffer {
    private int type;
    @NotNull
    private  ByteBuffer buffer;

    /** @deprecated */
    // $FF: synthetic method
    public static void getType$annotations() {
    }

    public final int getType() {
        return this.type;
    }

    @NotNull
    public final ByteBuffer getBuffer() {
        return this.buffer;
    }

    private final void removePadding(ImageWrapper image) {
        int sizeLuma = image.getY().getWidth() * image.getY().getHeight();
        int sizeChroma = image.getU().getWidth() * image.getU().getHeight();
        if (image.getY().getRowStride() > image.getY().getWidth()) {
            this.removePaddingCompact(image.getY(), this.buffer, 0);
        } else {
            this.buffer.position(0);
            this.buffer.put(image.getY().getBuffer());
        }

        if (this.type == 35) {
            if (image.getU().getRowStride() > image.getU().getWidth()) {
                this.removePaddingCompact(image.getU(), this.buffer, sizeLuma);
                this.removePaddingCompact(image.getV(), this.buffer, sizeLuma + sizeChroma);
            } else {
                this.buffer.position(sizeLuma);
                this.buffer.put(image.getU().getBuffer());
                this.buffer.position(sizeLuma + sizeChroma);
                this.buffer.put(image.getV().getBuffer());
            }
        } else if (image.getU().getRowStride() > image.getU().getWidth() * 2) {
            this.removePaddingNotCompact(image, this.buffer, sizeLuma);
        } else {
            this.buffer.position(sizeLuma);
            ByteBuffer uv = image.getV().getBuffer();
            int properUVSize = image.getV().getHeight() * image.getV().getRowStride() - 1;
            if (uv.capacity() > properUVSize) {
                uv = this.clipBuffer(image.getV().getBuffer(), 0, properUVSize);
            }

            this.buffer.put(uv);
            byte lastOne = image.getU().getBuffer().get(image.getU().getBuffer().capacity() - 1);
            this.buffer.put(this.buffer.capacity() - 1, lastOne);
        }

        this.buffer.rewind();
    }

    private final void removePaddingCompact(PlaneWrapper plane, ByteBuffer dst, int offset) {
        boolean var4 = plane.getPixelStride() == 1;
        if (!var4) {
            boolean var10 = false;
            String var11 = "use removePaddingCompact with pixelStride == 1";
            throw new IllegalArgumentException(var11.toString());
        } else {
            ByteBuffer src = plane.getBuffer();
            int rowStride = plane.getRowStride();
            ByteBuffer row = null;
            dst.position(offset);
            int i = 0;

            for(int var8 = plane.getHeight(); i < var8; ++i) {
                row = this.clipBuffer(src, i * rowStride, plane.getWidth());
                dst.put(row);
            }

        }
    }

    private final void removePaddingNotCompact(ImageWrapper image, ByteBuffer dst, int offset) {
        boolean var4 = image.getU().getPixelStride() == 2;
        if (!var4) {
            boolean var11 = false;
            String var12 = "use removePaddingNotCompact pixelStride == 2";
            throw new IllegalArgumentException(var12.toString());
        } else {
            int width = image.getU().getWidth();
            int height = image.getU().getHeight();
            int rowStride = image.getU().getRowStride();
            ByteBuffer row = null;
            dst.position(offset);
            int i = 0;

            for(int var9 = height - 1; i < var9; ++i) {
                row = this.clipBuffer(image.getV().getBuffer(), i * rowStride, width * 2);
                dst.put(row);
            }

            row = this.clipBuffer(image.getU().getBuffer(), (height - 1) * rowStride - 1, width * 2);
            dst.put(row);
        }
    }

    private final ByteBuffer clipBuffer(ByteBuffer buffer, int start, int size) {
        ByteBuffer duplicate = buffer.duplicate();
        duplicate.position(start);
        duplicate.limit(start + size);
        ByteBuffer var10000 = duplicate.slice();
        Intrinsics.checkNotNullExpressionValue(var10000, "duplicate.slice()");
        return var10000;
    }

    public YuvByteBuffer(@NotNull Image image, @Nullable ByteBuffer dstBuffer) {
        super();
        ImageWrapper wrappedImage = new ImageWrapper(image);
        this.type = wrappedImage.getU().getPixelStride() == 1 ? 35 : 17;
        int size = image.getWidth() * image.getHeight() * 3 / 2;
        ByteBuffer var10001;
        if (dstBuffer != null && dstBuffer.capacity() >= size && !dstBuffer.isReadOnly() && dstBuffer.isDirect()) {
            var10001 = dstBuffer;
        } else {
            var10001 = ByteBuffer.allocateDirect(size);
            Intrinsics.checkNotNullExpressionValue(var10001, "ByteBuffer.allocateDirect(size)");
        }

        this.buffer = var10001;
        this.buffer.rewind();
        this.removePadding(wrappedImage);
    }

    // $FF: synthetic method
    public YuvByteBuffer(Image var1, ByteBuffer var2, int var3, DefaultConstructorMarker var4) {
        this(var1, var2);
        if ((var3 & 2) != 0) {
            var2 = null;
        }


    }

    @Metadata(
            mv = {1, 9, 0},
            k = 1,
            d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\t\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\r\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\fR\u0011\u0010\u000f\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\bR\u0011\u0010\u0011\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\f¨\u0006\u0013"},
            d2 = {"Lcom/example/delftaiobjectdetector/core/camera/YuvByteBuffer$ImageWrapper;", "", "image", "Landroid/media/Image;", "(Landroid/media/Image;)V", "height", "", "getHeight", "()I", "u", "Lcom/example/delftaiobjectdetector/core/camera/YuvByteBuffer$PlaneWrapper;", "getU", "()Lcom/example/delftaiobjectdetector/core/camera/YuvByteBuffer$PlaneWrapper;", "v", "getV", "width", "getWidth", "y", "getY", "DelftAI_Object_Detector.app.main"}
    )
    private static final class ImageWrapper {
        private final int width;
        private final int height;
        @NotNull
        private final PlaneWrapper y;
        @NotNull
        private final PlaneWrapper u;
        @NotNull
        private final PlaneWrapper v;

        public final int getWidth() {
            return this.width;
        }

        public final int getHeight() {
            return this.height;
        }

        @NotNull
        public final PlaneWrapper getY() {
            return this.y;
        }

        @NotNull
        public final PlaneWrapper getU() {
            return this.u;
        }

        @NotNull
        public final PlaneWrapper getV() {
            return this.v;
        }

        public ImageWrapper(@NotNull Image image) {
            super();
            this.width = image.getWidth();
            this.height = image.getHeight();
            int var10003 = this.width;
            int var10004 = this.height;
            Image.Plane var10005 = image.getPlanes()[0];
            Intrinsics.checkNotNullExpressionValue(var10005, "image.planes[0]");
            this.y = new PlaneWrapper(var10003, var10004, var10005);
            var10003 = this.width / 2;
            var10004 = this.height / 2;
            var10005 = image.getPlanes()[1];
            Intrinsics.checkNotNullExpressionValue(var10005, "image.planes[1]");
            this.u = new PlaneWrapper(var10003, var10004, var10005);
            var10003 = this.width / 2;
            var10004 = this.height / 2;
            var10005 = image.getPlanes()[2];
            Intrinsics.checkNotNullExpressionValue(var10005, "image.planes[2]");
            this.v = new PlaneWrapper(var10003, var10004, var10005);
            boolean var2 = this.y.getPixelStride() == 1;
            boolean var3;
            String var4;
            if (!var2) {
                var3 = false;
                var4 = "Pixel stride for Y plane must be 1 but got " + this.y.getPixelStride() + " instead.";
                throw new IllegalArgumentException(var4.toString());
            } else {
                var2 = this.u.getPixelStride() == this.v.getPixelStride() && this.u.getRowStride() == this.v.getRowStride();
                if (!var2) {
                    var3 = false;
                    var4 = "U and V planes must have the same pixel and row strides " + "but got pixel=" + this.u.getPixelStride() + " row=" + this.u.getRowStride() + " for U " + "and pixel=" + this.v.getPixelStride() + " and row=" + this.v.getRowStride() + " for V";
                    throw new IllegalArgumentException(var4.toString());
                } else {
                    var2 = this.u.getPixelStride() == 1 || this.u.getPixelStride() == 2;
                    if (!var2) {
                        var3 = false;
                        var4 = "Supported pixel strides for U and V planes are 1 and 2";
                        throw new IllegalArgumentException(var4.toString());
                    }
                }
            }
        }
    }

    @Metadata(
            mv = {1, 9, 0},
            k = 1,
            d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\n\b\u0002\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u000e\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\rR\u0011\u0010\u0010\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\rR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\r¨\u0006\u0013"},
            d2 = {"Lcom/example/delftaiobjectdetector/core/camera/YuvByteBuffer$PlaneWrapper;", "", "width", "", "height", "plane", "Landroid/media/Image$Plane;", "(IILandroid/media/Image$Plane;)V", "buffer", "Ljava/nio/ByteBuffer;", "getBuffer", "()Ljava/nio/ByteBuffer;", "getHeight", "()I", "pixelStride", "getPixelStride", "rowStride", "getRowStride", "getWidth", "DelftAI_Object_Detector.app.main"}
    )
    private static final class PlaneWrapper {
        private final int width;
        private final int height;
        @NotNull
        private final ByteBuffer buffer;
        private final int rowStride;
        private final int pixelStride;

        public final int getWidth() {
            return this.width;
        }

        public final int getHeight() {
            return this.height;
        }

        @NotNull
        public final ByteBuffer getBuffer() {
            return this.buffer;
        }

        public final int getRowStride() {
            return this.rowStride;
        }

        public final int getPixelStride() {
            return this.pixelStride;
        }

        public PlaneWrapper(int width, int height, @NotNull Image.Plane plane) {
            super();
            this.width = width;
            this.height = height;
            ByteBuffer var10001 = plane.getBuffer();
            Intrinsics.checkNotNullExpressionValue(var10001, "plane.buffer");
            this.buffer = var10001;
            this.rowStride = plane.getRowStride();
            this.pixelStride = plane.getPixelStride();
        }
    }
}
