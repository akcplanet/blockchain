package org.ledger.blockchain.pow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
*
* @author Amit Chaudhary
* @date 2018/03/08
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PowResult {

    /**
     * 
     */
    private long nonce;
    /**
     * hash
     */
    private String hash;

}
