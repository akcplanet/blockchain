# blockchain
Block Chain Impl

The previous sections:

1.Basic prototype
2. Proof of workload
3. Persistence & Command Line
4. Transaction (UTXO)
5. Address (wallet)


UTXO pool
In persistent & command line in this article, we examined the Bitcoin core memory block the way. We mentioned that the block-related data is stored in the data bucket of blocks , and the transaction data is stored in the data bucket of chainstate . Let us recall the data structure of the chainstate data bucket:

'c' + 32-byte transaction hash -> unspent transaction output record for that transaction

UTXO record for a transaction

'B' -> 32-byte block hash: the block hash up to which the database represents the unspent transaction outputs

Block Hash of UTXO represented by the database

Since that article, we have implemented the Bitcoin trading mechanism, but we have not used the chainstate bucket to store our transaction output. So, this will be what we are going to do now.

Chainstate does not store transaction data. Instead, it stores a UTXO set, which is a collection of transaction outputs that have not been spent. In addition, it also stores the "block Hash of UTXO represented by the database", we will ignore this point for the time being, because we have not used the block height .


The method is used to find the transaction information corresponding to the wallet address containing the unspent transaction output. Since the transaction information is stored in the block, our current practice is to traverse each block in the blockchain, then traverse the transaction information in each block, and then traverse the transaction output in each transaction. And check if the transaction output is locked by the corresponding wallet address, the efficiency is very low. As of March 29, 2018, there were 515,698 blocks in Bitcoin , and these data occupied 140+Gb of disk space. This means that a person must run a full node (download all block data) to verify transaction information. In addition, verifying transaction information requires traversing all blocks.

The solution to this problem is to have an index that stores all UTXOs (unexpended transaction output). This is what the UTXOs pool does: the UTXOs pool is actually a cache space, and the data it caches needs to be built from the block. It is obtained from all the transaction data in the chain (by traversing all the blockchains, but this build operation only needs to be performed once), and it will be used later for the calculation of the wallet balance and the verification of the new transaction data. As of September 2017, the UTXOs pool is approximately 2.7Gb.

Ok, let's think about what we need to do to implement the UTXOs pool. Currently, the following methods are used to find transaction information:

Blockchain.getAllSpentTXOs -- Query all transaction output that has been spent. It needs to traverse transaction information in all blocks in the blockchain.

Blockchain.findUnspentTransactions -- Query transaction information containing unsuccessful transaction output. It also needs to traverse transaction information in all blocks in the blockchain.

Blockchain.findSpendableOutputs -- This method is used when new transactions are created. It needs to find enough transaction output to meet the amount of money that needs to be paid. Need to call the Blockchain.findUnspentTransactions method.

Blockchain.findUTXO -- Query all unspent transaction output for the wallet address and use it to calculate the wallet balance. Need to call

Blockchain.findUnspentTransactions method.

Blockchain.findTransaction - Query transaction information by transaction ID. It needs to iterate through all the blocks until it finds the transaction information.

As you can see, all of the above methods need to traverse all the blocks in the database. Since the UTXOs pool only stores unprocessed transaction output and does not store all transaction information, we will not optimize Blockchain.findTransaction .

So, we need the following methods:

Blockchain.findUTXO - Find all unspent transaction output by traversing all blocks.
UTXOSet.reindex - Call the above findUTXO method and store the query results in the database. That is, where you need to cache.
UTXOSet.findSpendableOutputs -- Similar to Blockchain.findSpendableOutputs , except that UTXO pools are used.
UTXOSet.findUTXO -- Similar to Blockchain.findUTXO , except that UTXO pools are used.
Blockchain.findTransaction -- The logic remains the same.


Merkle Tree
Merkle Tree is a mechanism we need to focus on in this article.

As I mentioned earlier, the entire Bitcoin database accounts for about 140G of disk space. Due to the distributed nature of Bitcoin, each node in the network must be independent and self-sufficient. Each bitcoin node is a collection of functions for routing, blockchain databases, mining, and wallet services. Each node participates in the routing function of the entire network and may also include other functions. Each node participates in verifying and propagating transaction and block information, discovering and maintaining connections to peer nodes. A full node includes the following four functions:


As more and more people start using Bitcoin, this rule is starting to become more and more difficult to follow: it is not realistic to have everyone run a complete node. In Nakamoto published Bitcoin White Paper , with respect to this problem proposed a solution: Simplified Payment Verification (SPV) (Simple payment verification). SPV is a lightweight Bitcoin nodes, it does not need to download all the data block chain, also does not require authentication and transaction data blocks . Conversely, when the SPV wants to verify the validity of a transaction, it retrieves some of the required data from the full node it is connected to. This mechanism ensures that multiple SPV light wallet nodes can be run in the case of only one full node.

For more information about SPV, please see: Chapter 8 of "Mastering Bitcoin (Second Edition)"

In order to make SPV possible, there is a need to check whether a block contains a transaction without downloading the block data in full. This is where the Merkle Tree comes into play.

The Merkle Tree used in Bitcoin is to obtain the hash value of the transaction, and then the hash value that has been approved by the Pow (Workload Proof) system is saved to the block header. So far, we have simply calculated the hash value of each transaction in a block, and then performed SHA-256 calculations on these transactions when preparing the Pow data . Although this is a good way to get the only representation of a block transaction, this approach does not have the advantages of Merkle Tree.
