/*
 * Copyright 2011 Google Inc.
 * Copyright 2015 Andreas Schildbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ramble.ramblewallet.blockchain.dogecoin.dogesdk;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import java.util.Arrays;

import javax.annotation.Nullable;

/**
 * Parses and generates private keys in the form used by the Bitcoin "dumpprivkey" command. This is the private key
 * bytes with a header byte and 4 checksum bytes at the end. If there are 33 private key bytes instead of 32, then
 * the last byte is a discriminator value for the compressed pubkey.
 */
public class DumpedPrivateKey extends VersionedChecksummedBytes {

    private boolean compressed;

    DumpedPrivateKey(NetworkParameters params, byte[] keyBytes, boolean compressed) {
        super(params.getDumpedPrivateKeyHeader(), encode(keyBytes, compressed));
        this.compressed = compressed;
    }

    private static byte[] encode(byte[] keyBytes, boolean compressed) {
        Preconditions.checkArgument(keyBytes.length == 32, "Private keys must be 32 bytes");
        if (!compressed) {
            return keyBytes;
        } else {
            byte[] bytes = new byte[33];
            System.arraycopy(keyBytes, 0, bytes, 0, 32);
            bytes[32] = 1;
            return bytes;
        }
    }

    @Deprecated
    public DumpedPrivateKey(@Nullable NetworkParameters params, String encoded) throws org.bitcoinj.core.AddressFormatException {
        super(encoded);
        if (params != null && version != params.getDumpedPrivateKeyHeader())
            throw new WrongNetworkException(version, new int[]{ params.getDumpedPrivateKeyHeader() });
        if (bytes.length == 33 && bytes[32] == 1) {
            compressed = true;
            bytes = Arrays.copyOf(bytes, 32);  // Chop off the additional marker byte.
        } else if (bytes.length == 32) {
            compressed = false;
        } else {
            throw new AddressFormatException("Wrong number of bytes for a private key, not 32 or 33");
        }
    }

    /**
     * Returns an ECKey created from this encoded private key.
     */
    public org.bitcoinj.core.ECKey getKey() {
        final org.bitcoinj.core.ECKey key = ECKey.fromPrivate(bytes);
        return compressed ? key : key.decompress();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DumpedPrivateKey other = (DumpedPrivateKey) o;
        return version == other.version && compressed == other.compressed && Arrays.equals(bytes, other.bytes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(version, compressed, Arrays.hashCode(bytes));
    }
}
