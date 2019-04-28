package io.github.ImpactDevelopment.installer;

import io.github.ImpactDevelopment.installer.GithubReleases.GithubRelease;
import io.github.ImpactDevelopment.installer.GithubReleases.ReleaseAsset;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.jcajce.JcaPGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;

import java.io.ByteArrayInputStream;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GPG {
    public static final JcaPGPPublicKeyRingCollection KEYRING;

    static {
        Security.addProvider(new BouncyCastleProvider());
        try {
            KEYRING = new JcaPGPPublicKeyRingCollection(PGPUtil.getDecoderStream(GPG.class.getResourceAsStream("/keys.asc")));
            for (PGPPublicKeyRing ring : KEYRING) {
                System.out.println("Loaded ring " + ring.getPublicKey().getUserIDs().next());
                for (PGPPublicKey pubkey : ring) {
                    System.out.println("Loaded key " + pubkey.getKeyID());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static final PGPPublicKey leijurv = getMust(4946054532857441386L);
    public static final PGPPublicKey brady = getMust(8333779407862330727L);
    public static final PGPPublicKey leafhacker = getMust(7411429204550467438L);


    /**
     * Verify signed data
     *
     * @param signedData
     * @param signatureData
     * @return the PGPPublicKey that created this signature, if it's valid.  null if not valid, or unknown public key.
     */
    public static PGPPublicKey verify(byte[] signedData, byte[] signatureData) {
        try {
            JcaPGPObjectFactory pgpFact = new JcaPGPObjectFactory(PGPUtil.getDecoderStream(new ByteArrayInputStream(signatureData)));
            PGPSignatureList sigList = ((PGPSignatureList) pgpFact.nextObject()); // neither this (calling nextObject again)
            PGPSignature sig = sigList.get(0); // nor this (get(1)) allow you to get the second of two concatenated signatures. how annoying.
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

    public static PGPPublicKey getMust(long keyID) {
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

    public static boolean verifyRelease(GithubRelease release) {
        try {
            Map<String, List<ReleaseAsset>> byName = Stream.of(release.assets).collect(Collectors.groupingBy(r -> r.name));
            String checksums = byName.get("checksums.txt").get(0).fetch();
            String checksumsSigned = byName.get("checksums_signed.asc").get(0).fetch();
            List<PGPPublicKey> sigs = findValidSignatures(checksums, checksumsSigned);
            if (sigs.contains(brady)) {
                System.out.println("Signed by brady");
            }
            if (sigs.contains(leafhacker)) {
                System.out.println("Signed by leafhacker");
            }
            if (sigs.contains(leijurv)) {
                System.out.println("Signed by leijurv");
                return true; // 😉
            }
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static List<PGPPublicKey> findValidSignatures(String checksums, String signatures) {
        List<PGPPublicKey> sigs = new ArrayList<>();
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
