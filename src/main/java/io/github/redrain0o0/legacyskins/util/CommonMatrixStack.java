package io.github.redrain0o0.legacyskins.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix3x2fStack;

public interface CommonMatrixStack {
    static CommonMatrixStack of(final PoseStack stack) {
        return new CommonMatrixStack() {
            public void push() {
                stack.pushPose();
            }

            public void pop() {
                stack.popPose();
            }

            public void translate(double x, double y, double z) {
                stack.translate(x, y, z);
            }

            public void translate(double x, double y) {
                this.translate(x, y, 0.0);
            }

            public void scale(double x, double y, double z) {
                stack.scale((float)x, (float)y, (float)z);
            }

            public void scale(double x, double y) {
                this.scale(x, y, 0.0);
            }

            public <T> T getNative() {
                return (T) stack;
            }
        };
    }

    static CommonMatrixStack of(final GuiGraphics graphics) {
        return new CommonMatrixStack() {
            private CommonMatrixStack delegate = CommonMatrixStack.of(graphics.pose());

            public void push() {
                this.delegate.push();
            }

            public void pop() {
                this.delegate.pop();
            }

            public void translate(double x, double y, double z) {
                this.delegate.translate(x, y, z);
            }

            public void translate(double x, double y) {
                this.delegate.translate(x, y);
            }

            public void scale(double x, double y, double z) {
                this.delegate.scale(x, y, z);
            }

            public void scale(double x, double y) {
                this.delegate.scale(x, y);
            }

            public <T> T getNative() {
                return (T) graphics;
            }
        };
    }

    static CommonMatrixStack of(final Matrix3x2fStack stack) {
        return new CommonMatrixStack() {
            public void push() {
                stack.pushMatrix();
            }

            public void pop() {
                stack.popMatrix();
            }

            public void translate(double x, double y, double z) {
                this.translate(x, y);
            }

            public void translate(double x, double y) {
                stack.translate((float)x, (float)y);
            }

            public void scale(double x, double y, double z) {
                this.scale(x, y);
            }

            public void scale(double x, double y) {
                stack.scale((float)x, (float)y);
            }

            public <T> T getNative() {
                return (T) stack;
            }
        };
    }

    @Deprecated
    default void pushPose() {
        this.push();
    }

    @Deprecated
    default void popPose() {
        this.pop();
    }

    void push();

    void pop();

    void translate(double var1, double var3, double var5);

    void translate(double var1, double var3);

    void scale(double var1, double var3, double var5);

    void scale(double var1, double var3);

    <T> T getNative();
}
