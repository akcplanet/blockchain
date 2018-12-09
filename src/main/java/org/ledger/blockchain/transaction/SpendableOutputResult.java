package org.ledger.blockchain.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
*
* @author Amit Chaudhary
* @date 2018/03/08
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpendableOutputResult {

    /**
     * 
     */
    private int accumulated;
    /**
     * 
     */
    private Map<String, int[]> unspentOuts;

}
