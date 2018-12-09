package org.ledger.blockchain.wallet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.ledger.blockchain.util.Base58Check;
import org.ledger.blockchain.util.BtcAddressUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;

/**
*
* @author Amit Chaudhary
* @date 2018/03/08
*/
@Data
@AllArgsConstructor
@Slf4j
public class Wallet implements Serializable {

    private static final long serialVersionUID = 166249065006236265L;

    /**
     * 
     */
    private static final int ADDRESS_CHECKSUM_LEN = 4;
    /**
     * 
     */
    private BCECPrivateKey privateKey;
    /**
     * 
     */
    private byte[] publicKey;


    public Wallet() {
        initWallet();
    }

    /**
     * 
     */
    private void initWallet() {
        try {
            KeyPair keyPair = newECKeyPair();
            BCECPrivateKey privateKey = (BCECPrivateKey) keyPair.getPrivate();
            BCECPublicKey publicKey = (BCECPublicKey) keyPair.getPublic();

            byte[] publicKeyBytes = publicKey.getQ().getEncoded(false);

            this.setPrivateKey(privateKey);
            this.setPublicKey(publicKeyBytes);
        } catch (Exception e) {
            log.error("Fail to init wallet ! ", e);
            throw new RuntimeException("Fail to init wallet ! ", e);
        }
    }


    /**
     *
     * @return
     * @throws Exception
     */
    private KeyPair newECKeyPair() throws Exception {
        //  BC Provider
        Security.addProvider(new BouncyCastleProvider());
        //  ECDSA
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", BouncyCastleProvider.PROVIDER_NAME);
        // 
        // bitcoin secp256k1， https://bitcointalk.org/index.php?topic=151120.0
        ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
        keyPairGenerator.initialize(ecSpec, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }


    /**
     *
     * @return
     */
    public String getAddress() {
        try {
            // 1.  ripemdHashedKey
            byte[] ripemdHashedKey = BtcAddressUtils.ripeMD160Hash(this.getPublicKey());

            // 2.  0x00
            ByteArrayOutputStream addrStream = new ByteArrayOutputStream();
            addrStream.write((byte) 0);
            addrStream.write(ripemdHashedKey);
            byte[] versionedPayload = addrStream.toByteArray();

            // 3. 
            byte[] checksum = BtcAddressUtils.checksum(versionedPayload);

            // 4. version + paylod + checksum 
            addrStream.write(checksum);
            byte[] binaryAddress = addrStream.toByteArray();

            // 5. Base58
            return Base58Check.rawBytesToBase58(binaryAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Fail to get wallet address ! ");
    }


}
