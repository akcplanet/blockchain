package org.ledger.blockchain.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

import org.ledger.blockchain.util.Base58Check;

/**
*
* @author Amit Chaudhary
* @date 2018/03/08
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TXOutput {

    /**
     */
    private int value;
    /**
     * Hash
     */
    private byte[] pubKeyHash;

    /**
     *
     * @param value
     * @param address
     * @return
     */
    public static TXOutput newTXOutput(int value, String address) {
        // 
        byte[] versionedPayload = Base58Check.base58ToBytes(address);
        byte[] pubKeyHash = Arrays.copyOfRange(versionedPayload, 1, versionedPayload.length);
        return new TXOutput(value, pubKeyHash);
    }

    /**
     *
     * @param pubKeyHash
     * @return
     */
    public boolean isLockedWithKey(byte[] pubKeyHash) {
        return Arrays.equals(this.getPubKeyHash(), pubKeyHash);
    }

}
