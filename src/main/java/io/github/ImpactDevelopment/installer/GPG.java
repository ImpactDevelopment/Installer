package io.github.ImpactDevelopment.installer;

import io.github.ImpactDevelopment.installer.github.GithubRelease;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.jcajce.JcaPGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;

import java.io.ByteArrayInputStream;
import java.security.Security;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class GPG {
    private static final JcaPGPPublicKeyRingCollection KEYRING;

    static {
        Security.addProvider(new BouncyCastleProvider());
        try {
            KEYRING = new JcaPGPPublicKeyRingCollection(PGPUtil.getDecoderStream(GPG.class.getResourceAsStream("/keys.asc")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static final PGPPublicKey leijurv = getMust(0x44A3EA646EADAC6AL);
    public static final PGPPublicKey brady = getMust(0x73A788379A197567L);
    public static final PGPPublicKey leafhacker = getMust(0x66DAAF98172FBF6EL);


    /**
     * Verify signed data
     *
     * @param signedData
     * @param signatureData
     * @return the PGPPublicKey that created this signature, if it's valid.  null if not valid, or unknown public key.
     */
    private static PGPPublicKey verify(byte[] signedData, byte[] signatureData) {
        try {
            JcaPGPObjectFactory pgpFact = new JcaPGPObjectFactory(PGPUtil.getDecoderStream(new ByteArrayInputStream(signatureData)));
            PGPSignature sig = ((PGPSignatureList) pgpFact.nextObject()).get(0);
            PGPPublicKey key = KEYRING.getPublicKey(sig.getKeyID());
            if (key == null) {
                System.out.println("WARNING: signature from unknown public key " + sig.getKeyID());
                return null;
            }
            sig.init(new JcaPGPContentVerifierBuilderProvider().setProvider("BC"), key);
            sig.update(signedData);
            if (!sig.verify()) {
                return null;
            }
            return key;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static PGPPublicKey getMust(long keyID) {
        try {
            PGPPublicKey key = KEYRING.getPublicKey(keyID);
            if (key == null) {
                throw new IllegalStateException("Unable to find " + keyID);
            }
            return key;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verifyRelease(GithubRelease release, String fileToSign, String fileWithSignatures, Predicate<Set<PGPPublicKey>> condition) {
        try {
            String checksums = release.byName(fileToSign).get().fetch();
            String checksumsSigned = release.byName(fileWithSignatures).get().fetch();
            Set<PGPPublicKey> sigs = findValidSignatures(checksums, checksumsSigned);
            return condition.test(sigs);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private static Set<PGPPublicKey> findValidSignatures(String checksums, String signatures) {
        Set<PGPPublicKey> sigs = new HashSet<>();
        for (String sig : signatures.split("-----END PGP SIGNATURE-----")) {
            if (sig.trim().isEmpty()) {
                continue;
            }
            sig += "-----END PGP SIGNATURE-----";
            PGPPublicKey verify = verify(checksums.getBytes(), sig.getBytes());
            if (verify != null) {
                sigs.add(verify);
            }
        }
        return sigs;
    }
}
