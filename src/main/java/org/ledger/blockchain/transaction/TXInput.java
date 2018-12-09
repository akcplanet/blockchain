package org.ledger.blockchain.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

import org.ledger.blockchain.util.BtcAddressUtils;

/**
*
* @author Amit Chaudhary
* @date 2018/03/08
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TXInput {

    /**
     * hash
     */
    private byte[] txId;
    /**
     */
    private int txOutputIndex;
    /**
     * 
     */
    private byte[] signature;
    /**
     * 
     */
    private byte[] pubKey;

    /**
     *
     * @param pubKeyHash
     * @return
     */
    public boolean usesKey(byte[] pubKeyHash) {
        byte[] lockingHash = BtcAddressUtils.ripeMD160Hash(this.getPubKey());
        return Arrays.equals(lockingHash, pubKeyHash);
    }

}
