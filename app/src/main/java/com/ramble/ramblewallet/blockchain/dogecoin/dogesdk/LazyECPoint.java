package com.ramble.ramblewallet.blockchain.dogecoin.dogesdk;

import static com.google.common.base.Preconditions.checkNotNull;
import org.spongycastle.math.ec.ECCurve;
import org.spongycastle.math.ec.ECFieldElement;
import org.spongycastle.math.ec.ECPoint;
import java.math.BigInteger;
import java.util.Arrays;
import javax.annotation.Nullable;

/**
 * A wrapper around ECPoint that delays decoding of the point for as long as possible. This is useful because point
 * encode/decode in Bouncy Castle is quite slow especially on Dalvik, as it often involves decompression/recompression.
 */
public class LazyECPoint {
    // If curve is set, bits is also set. If curve is unset, point is set and bits is unset. Point can be set along
    // with curve and bits when the cached form has been accessed and thus must have been converted.

    private final ECCurve curve;
    private final byte[] bits;

    // This field is effectively final - once set it won't change again. However it can be set after
    // construction.
    @Nullable
    private ECPoint point;

    public LazyECPoint(ECCurve curve, byte[] bits) {
        this.curve = curve;
        this.bits = bits;
    }

    public LazyECPoint(ECPoint point) {
        this.point = checkNotNull(point);
        this.curve = null;
        this.bits = null;
    }

    public ECPoint get() {
        if (point == null)
            point = curve.decodePoint(bits);
        return point;
    }

    public ECPoint getDetachedPoint() {
        return get().getDetachedPoint();
    }

    public byte[] getEncoded() {
        if (bits != null)
            return Arrays.copyOf(bits, bits.length);
        else
            return get().getEncoded();
    }

    public boolean isInfinity() {
        return get().isInfinity();
    }

    public boolean isNormalized() {
        return get().isNormalized();
    }

    public boolean isCompressed() {
        if (bits != null)
            return bits[0] == 2 || bits[0] == 3;
        else
            return get().isCompressed();
    }

    public ECPoint multiply(BigInteger k) {
        return get().multiply(k);
    }

    public boolean isValid() {
        return get().isValid();
    }

    public boolean equals(ECPoint other) {
        return get().equals(other);
    }

    public ECPoint negate() {
        return get().negate();
    }

    public byte[] getEncoded(boolean compressed) {
        if (compressed == isCompressed() && bits != null)
            return Arrays.copyOf(bits, bits.length);
        else
            return get().getEncoded(compressed);
    }

    public ECPoint add(ECPoint b) {
        return get().add(b);
    }

    public ECCurve getCurve() {
        return get().getCurve();
    }

    public ECPoint normalize() {
        return get().normalize();
    }

    public ECFieldElement getY() {
        return this.normalize().getYCoord();
    }

    public ECFieldElement getX() {
        return this.normalize().getXCoord();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Arrays.equals(getCanonicalEncoding(), ((LazyECPoint)o).getCanonicalEncoding());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getCanonicalEncoding());
    }

    private byte[] getCanonicalEncoding() {
        return getEncoded(true);
    }
}
