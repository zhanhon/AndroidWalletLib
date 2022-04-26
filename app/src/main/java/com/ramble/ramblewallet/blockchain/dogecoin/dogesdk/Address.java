package com.ramble.ramblewallet.blockchain.dogecoin.dogesdk;

import static com.google.common.base.Preconditions.checkArgument;
import org.bitcoinj.core.NetworkParameters;


public class Address extends VersionedChecksummedBytes {

    public Address(NetworkParameters params, byte[] hash160) {
        super(params.getAddressHeader(), hash160);
        checkArgument(hash160.length == 20, "Addresses are 160-bit hashes, so you must provide 20 bytes");
    }

}
