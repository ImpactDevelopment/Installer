package io.github.ImpactDevelopment.installer;

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

public class GPG {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static boolean verify(byte[] signedData, byte[] signatureData) {
        try {
            JcaPGPObjectFactory pgpFact = new JcaPGPObjectFactory(PGPUtil.getDecoderStream(new ByteArrayInputStream(signatureData)));
            PGPSignatureList sigList = ((PGPSignatureList) pgpFact.nextObject()); // neither this (calling nextObject again)
            PGPSignature sig = sigList.get(0); // nor this (get(1)) allow you to get the second of two concatenated signatures. how annoying.
            ByteArrayInputStream in = new ByteArrayInputStream(MyKey.getBytes());
            PGPPublicKey key = new JcaPGPPublicKeyRingCollection(PGPUtil.getDecoderStream(in)).getPublicKey(sig.getKeyID());
            sig.init(new JcaPGPContentVerifierBuilderProvider().setProvider("BC"), key);
            sig.update(signedData);
            if (!sig.verify()) {
                return false;
            }
            System.out.println("Signed by " + sig.getKeyID());
            System.out.println(key.getUserIDs().next());
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static String MyKey = "-----BEGIN PGP PUBLIC KEY BLOCK-----\n" +
            "\n" +
            "mQINBFlvXTsBEADXjnOk8iwB63z9aYJhUyfHY5ZOurv8sbmFoXd9ZnMBa0VSB9tX\n" +
            "MTpss2Y+E+H5gMEV6A3AcrVfL6DDEjhLw8HzbxVjNhOQjvTi3bv9qUp/Ud2BsXZA\n" +
            "7SJZys/u5kc5FOZ0Zpj6Sxg3TqlSzNytUteftrEgkw5gPhiqCnVmw4ajZwvyE3NH\n" +
            "mLrUMNmof96KUxXWXB4eENOhOCStFC2F33z+d65ocZo8Py2u3uf/fmUHVSZvaKIU\n" +
            "RN9an9Wkza2beJQrZf61TPGQjJ1Y0N27FFHLBESpGKug0NrxfmQoJAsnKe2O6uOF\n" +
            "rSUv1TUUPR7EGzEgHN3fAwuaWCc6O662ol2IMiM3iJ/FLyGRHknR8ZtWG8ZJzCly\n" +
            "NkQsFHS020PRX6wYmfLYQkkWB0QYiIeRlYcbmE7/A1dmCZFt0Fca4UOaYvQdUP92\n" +
            "pkrqoEk24/6KODMmQSVa93SaugEakjLKCj5oXoG5Y6dXLxwMeAx8oOpLOvxNZnr3\n" +
            "cJnNlHwaTeX9NqfxpCuPTLmhdkscurHAGrwN3vEI8NQKCrL3CLwoBcJQuAsa71GR\n" +
            "bM21RjnsIN9dGIGKj6zD2/Yfp/5iE8Bt6UPHHTOwAeQM+1ppgoCmyXbZHevYHn5U\n" +
            "Q87ZTscle8l2XtjqjmMhVphh3/8lqBZRqv1bK7+In5DQrefecTuB0UJUwwARAQAB\n" +
            "tB1MdXJmIEp1cnYgPGxlaWp1cnZAZ21haWwuY29tPokCTgQTAQoAOAIbAwULCQgH\n" +
            "AwUVCgkICwUWAgMBAAIeAQIXgBYhBA/e3eDbUb2Z09IJxUSj6mRuraxqBQJcvkSp\n" +
            "AAoJEESj6mRuraxqxP4QALooLsN+cyVf2CrQB1x7/pQdxAXPeJBOY9yoxxrIREHi\n" +
            "4N7Pmt3+ctK4dd1fnu7StD31K91DXnZ4bVgAFGkX8qzzt7w1Mm14SVf4TwBa8vEh\n" +
            "TuwYFXlM63kHV5wvps6HxVPWFdSp6ikIkDaAJqGSzMyb3fc7sXGm/rxE/NXa7fdQ\n" +
            "d07uH/KbdmCRjc2+FyZI/OYyYap6qczmVAWtIXbpbxApt7FoOikteDYX13E3/l2c\n" +
            "H0Ej7WYHAlvxbDNpzfpO2g6SHvJQh5siOB2Ln5ohID/WCt+W43N2V94UEqfCoDic\n" +
            "kdigG6ILiaRX5gnZmK/vGmqXluvdLgcSAu+EuiYQ6qGVSm4U1rCFhHJmXIA1Rkvj\n" +
            "D7hFOHjwqoN5X7xiZAR7al5KE+P88g22cS+8uFD9suGjU9BmTmMbtQlztRos6Ubi\n" +
            "DSTBHu8FilqyxHeezVS5s0sItduGISFfpvy2ZlDqiQmQ/LBQLgBdRS5z6XI2SPsj\n" +
            "/8efHdp+j7PFlzqB0ieQlRBg0ezKGIGBgPXbwEHvd/qqh39N98rFvg4przA75Ma6\n" +
            "7/j2bvKr7/6XdvtaEsSpj86PEzTag4DkIa8lxW4gmEJzpufsEkandG++8HCgAnw9\n" +
            "Zg1JCFdujPOtFPxGWNc+ZLJnpdK2b7LjWQ2n5VQUHw7/NMLLa9cVWs8jksv0JgMW\n" +
            "uQINBFlvXTsBEADBOrO0asVbGCO8whrrJy1JrBI47VDe2G6gyOf5ATz3UVAAB4TH\n" +
            "kjkJ4YSTCqsRSzYHiL9399yf3so+dK2vIn4yjZVuqGB7UVW/p3Ay32PqIKbASRkS\n" +
            "gwLJqzWwrQIRdoRVgzXv569Pvb9FVqrfp41A7Pd3ROEKKO1Yl/J1WjFvLPY6me2n\n" +
            "3Cr0tNUXEWofFdBQa5SypkbJTDa4d+OaS9V4yqEXrRhsSSYKjso/Cd600NzMhEQv\n" +
            "w5EXjEJwqCB269R1fRHzcUgkI+jjA+5UD24IUVQsjzhqn3UOpvPLAMg9Csow17Rr\n" +
            "F3wNtqkHCOJAx8vht52ibk2F1mpRHaKYYFeO/+R7X5NfaIxQVCCM+n+fLgXCxpoj\n" +
            "Lf+OoJCXsqECrlwW97YkzhweEOabQHy/SM1mrO3gKpldIcBuO74fuRnc0Kyo6lg7\n" +
            "XlOn5vDYPkyG7cVGyVCC2iVogVnWdCqNouCoPWQmFMvTX+Fu/AdQCo0on5UOIMBl\n" +
            "p6QbGhsTkbKPZt/kDowsWPPCDTVQVegZorYPamzO/eCXyuGVh3XEU+RcK4RrTess\n" +
            "rOI4Gyc7azoBJyV1czGdAbJPcDplE7U0FNfzKYRAjIMTP1iqGFKEPCcDBh5b2ZjK\n" +
            "D4gJhYmhms3LwdAROJ4ssG1lOaSfuYe2zI/j5Z2ekUBF6jFb2XhY9Wvo5wARAQAB\n" +
            "iQIlBBgBCgAPBQJZb107AhsMBQkIYkoAAAoJEESj6mRuraxqC58QAJ4W8961FNKu\n" +
            "iuQKqy2yg/qK0nVntGh25/uGFbWXvelqHVyIFc4j2DzDQpbpGFcDrC9mX1pF3Kir\n" +
            "jiKGPpJ044R918Xe0Xf/BL30ffVXDkIUxxvfHmTWf3jSNA9kcgE0SuZbHXXU6TTX\n" +
            "5raMaXHBglXWOxuSCTZCpEGk+2r5fTEtXrTvET7An6HDNG48nriubb+UHUWVYWld\n" +
            "1TNB5HjmB54NjBVXJPjBDrFrGJCZ+7wDCEKwihEIHbfmUn9niETIdf3ZIcKTx3lG\n" +
            "IxejeW2gnik/RAcbLP12T9Aj1ebLCyO4MSjE6ns1lZAoWbuKkJRm5/81kO5fWHjg\n" +
            "fJ9iulkg6OBo77tZc0u2vdCGCePwtQg0weFr1xMXw+nkPxJZMS47FSkrd5xWoJRf\n" +
            "8gqahjuIsXXj4PpgU9NGOzsPb4cDzmwMk7tmnRjrb2jigRUPmpi06FKmxIFxcgfv\n" +
            "TlT6MhD4xvxHcqaIoHS5+dh5mpqRvSP3pfStb5mWUaUiLIhHf8enjUEeYKyHQJqS\n" +
            "ML4Hzv2nmA7MZA+16M/rUeiYsrGeVIeEAFOlDKBIgKZgCXlgO2SRUz+asnVyHlMe\n" +
            "M18Js/na5x8PvFwplCCiDtUgg2ek21N28acE0xY4XzlThV04O8WGQDyVtCEjJBLX\n" +
            "j7wuuBV0qbnfE0nhjlgXUNhfNkTxpNgCmQINBFrGR+ABEACkqduXzioM8siQY2B/\n" +
            "rbllzTjkm4J4ySH99THVNTO4bq8t47t5ymK4kvPA4QJGJeDOJasnsjBBGnW4a105\n" +
            "vOdgUlYxLtTA4lBO76fuownlrIp3DTNmlQdmlnDKUjfcbCACvzGpwrCDYUHP4tMU\n" +
            "UxXU5r6bSSjUSdfeSR3ckC9Ezkq4pL5FZOzXqggJ2JBXtKNmmmYFpwaYh0mk3iw8\n" +
            "wUpxkH7wirWnO4yULEB8uRsbZVgbwLs9SPdBhesSkHmOLF/t/xxne8dwNnoUDz0U\n" +
            "js8CXYWZkoEu03wBdsrHtvfQrwTTd11hRRwgP56MJX7NjaVKIq1aEOjwsCXvxxDH\n" +
            "OkZ2ybslUaM+xf1fgDr3duF+Mr/BMJE2SNjx+YNVWJ7FfMokPYmdwAsTYzN0jcZq\n" +
            "sbNidnZprVEKRw8kxTI+NfQeOsC3jAkXornHVAY4CjegQdbOzIqvWNfmwrOG36sV\n" +
            "bDwpkbWnGSTpHHJyq2jja8Z2PwTrLNuIIVxEFGHbmLrm6QFNW7bsMxFbMVF2vDP/\n" +
            "5xpCxDiKmjh6nC0b6RPB/BZ+59zwJ1HEwt8K+ySonI5wcx6nA3oKcrPBwdW5H+lA\n" +
            "5iUe1te1NHSWRk32gYboLkafFGeaCOHn2Xxs3mwqRQlb1nUQgnmkp+nTfAeGlfeI\n" +
            "Ot+Uz0aNHKx2eAApa+NngB+vUwARAQABtCJCcmFkeSBIYWhuIDxicmFkeWVoYWhu\n" +
            "QGljbG91ZC5jb20+iQJOBBMBCAA4FiEEGoU9OvVEjvH5aaPac6eIN5oZdWcFAlrG\n" +
            "SC4CGwMFCwkIBwIGFQoJCAsCBBYCAwECHgECF4AACgkQc6eIN5oZdWe51A//Yyjh\n" +
            "z5ZVcOvGTirDOLjvT1kzIagnfwk6dBNh0iwcfe9C7q1Rjv7NpY/gUaZ5z3XB3pn0\n" +
            "UwtHe+BrQjeDz5gUIMkdGY3u/12PC83B08ubMqMZiNi2TxRco3X37gEE8lMbpkw8\n" +
            "CPXXHsypTJ/CqGzQ7Vghwt4mam1IjSVVrgPkrT3f21uOt+oMIZtU934az6YowmOV\n" +
            "MjCB5klkJ2G97DH7zCmGSZKoMvYuzuKdeAnd3RMjKZNgD73JJA0P2Q/3VZrJidTk\n" +
            "uOiTo9hKY3IEF4mAGHU2bjxcrmSZNeYzL1hlihJRsAPXuYu0GBK9WeDx/dxBAcos\n" +
            "dXECdgLdMau2NSi7KFPCH0Zb8SY9YsYUYaB5xRdyrW1MmHwsDkB7uqB86oU3bOis\n" +
            "WtJOh6jRYzeSGK6gbMwAjVTI4uYjaCx6o3FdAmTa0kPn6k/96TjVcFHhqUQhgH4E\n" +
            "yS8naReR2Mkbb2igpzB2bYZBo7NM2r/K1nUZrMCLavr7mQuV94wNkLktGTAeH6av\n" +
            "S1FUx8kk8if4Y1GlZ0K0hb5mlmQl5bcwPBXKeM83xOZsp2Dm1ON/25BI8kT6naY+\n" +
            "KGIh4jVRnpmofrGmyV1j+ZkkzhOUDyawj8WioiCdpQfCOjWINJqiBm05STEIeVC/\n" +
            "nQmh1Vh8m7nzAOOdL4n6aM6ifwBjV0586j4galyJAjMEEAEKAB0WIQQP3t3g21G9\n" +
            "mdPSCcVEo+pkbq2sagUCW6q+xAAKCRBEo+pkbq2sanPIEAC44ATgZdTIKFuVCTvQ\n" +
            "heKL8DS8Dfw1gzyip9qgeB1T8s6QRJp6QFEUu+ugoOFaUitw5u+foJkQUhTu5j9O\n" +
            "cIaR1OgShVOjhR+48fxiOCr6a0CmzVrqtQ5FHIEGN3TeNyIhlcx7dgPdQzrcT4HG\n" +
            "FRCH8MMma7qzHFmmaIY55x4FRv/ZSr1lUw2HzKdPnSaihglr3iUClik+n6Y2Q5tt\n" +
            "qDhHaLszvljG3CssWchq3EmqWjtAFlJGr3a30tG9VjSUs5qwhsejg/87A+nb2xLb\n" +
            "lh64BOq39Zujhu2sOqdv3mxTp9+HP5FBjBOWNxPfKmAldQcq8Dp/42yEot/kviHR\n" +
            "NTlivoXondCj/9OTwX/XBGF1yq6lB0q3sBnezzIJy1q83ueIsLHS8twEeF6fg0uz\n" +
            "jDasT4Dv5Q44AU4jjSOyL+w0tOisBDQf5tBwSgBdgqRvVSchcWbW6hxuRmxlfEWy\n" +
            "2THvgIqzWBc+8Jzd5W2lgU3D60drwT/7YOPuRM/YCpxpeyz/4K1aQfgJ2y/AGv9u\n" +
            "23t6Q3Csj81QCcKU9ThwRAgDMCCsC9RW607rNBbRwRBCmGzZGh5HhE4QTNEmo+VM\n" +
            "0k6AuN/V2quFnZlPkA7u0eZqjy1+LQnWokXmBPeUQcUnbbEjfoFh7P3wwj/q+fgn\n" +
            "sqqoj1xI0p72PRJcLG1fRYIsQ7QjQnJhZHkgSGFobiA8emVyb21lbWVzZGV2QGdt\n" +
            "YWlsLmNvbT6JAk4EEwEIADgWIQQahT069USO8flpo9pzp4g3mhl1ZwUCWsZH4AIb\n" +
            "AwULCQgHAgYVCgkICwIEFgIDAQIeAQIXgAAKCRBzp4g3mhl1ZxYmD/9bZs/DSFnB\n" +
            "blN5enHRDR9uV345D+P8wCDIeyddZwDCvw1LsmXkMO9cJ89hr+HTO5PYW5IYsiGI\n" +
            "Ce2jeCaUallfdnWujcmeXVzqDxL6Sf5DW1NL37BqCgxQsVK8X5/MQ6USr4f7i/P5\n" +
            "I0pv3W/mpTTHpVICcqP3MdRc861EYmK2cUH/X/v0nknGdqK9jkbHHAENlNK7cMCl\n" +
            "rY+tyfR6UOgO2Cy3VZ26a/IK/VxuJjp6cRuYNvUnHYqpPlPjgmZ6lLxUYJJYzvuI\n" +
            "Cj4b4jdx6C0lJ/RaemS3AZUQtGdlP2/b1/Xz5jQSeKUrJZaruUmavqnEOuwhacXC\n" +
            "CeebPpHPuMPdCd3mgTARZ24lQcXrgFw5IAx0rIB/96aKHGBy/dwo0bSepgXUfzcJ\n" +
            "FPUNaBEXN5JGXXOCWO2rr/rzBU4RW/gy6QR7yUFpbJ+8mr/zcZ/3Et5L7uegdVWg\n" +
            "0mCvCElyMGM1ufChZWHxwjT2M/Odu8QvqVEiDqtgVZ4vYtoZnCjJMFiqdxs+ze2c\n" +
            "1UW+oM4G8GccX6TJmIswW1qO1V1gSzf3Y747Yqj1gnJju2zF0x11X5I0BqjKGJ1C\n" +
            "cBH4Jy3mfJvSYIlSthB/mnQLoPY8mu346J6TSGBxI+CrTcaGgSBomOfuN7XR2NMn\n" +
            "C3BaDdAX3A8LwfCbDAijrIS2LyGnnATZJokCMwQQAQoAHRYhBA/e3eDbUb2Z09IJ\n" +
            "xUSj6mRuraxqBQJbqr7FAAoJEESj6mRuraxqEk8P/2Xdnts5x4yEN0Ezg+85aYBQ\n" +
            "o3d2Infzz1V3SYvfiLNc6/dALGAXnDPfKMpNAvGY/HJ8+K6zHHBIcGAoLZyt5r5o\n" +
            "cNmQS7DGhmAXbsNy0lMLYPHDPprAlr+YrAza0x6q2Gqiy1Tsszf1QkJVPcHRNCLq\n" +
            "wbwEEoAzDbZwhNSc6UhTsFygrXl1b6Kwj+qKcT3WtgdJwZ3r1uDINa4ieEfDQGXV\n" +
            "x/d0QQpf5pukCvBJYCHkBpY+M94zEYa3E5eelnQ71G6CBBVW3bOavsv8EQyfUATn\n" +
            "Q6EOfs6dnIQ3V0Ie7HgeyCa13fMHQ+w5Ddq0a8eh8xpVxwrO7+MowRJ//zkJNXsO\n" +
            "w8G+3CEORQC0SYII9j1vrC0O5/2ChNV4P2EjYgB1mba3fuej2M+/uIWvFgetCh6a\n" +
            "LPF2ODYY7TWwX2tO03oT6CFLXN+DpnzvjOvm67RS9tw2GdQCSoganPwwErp2sujm\n" +
            "YLIE+kzCrdqfi+M6jMlz0IrRQvEu85ozoYT2XZZBlgFj8/q1AW1LzZ3YwXzU6kI4\n" +
            "ZQfEMNHhfYVNHqO2o/6+uLjFgF+Q+XEy3O0P+rWE0L61QHhZERCpcgt1USnNl1cQ\n" +
            "C5uLVMGI88WqYuj/31wNnqtamADuBuiX79neihR1oA5e7Sr3VjTBR9eSwTRnw+uG\n" +
            "I7dx7RTL8Tr+KFc4dYS2uQINBFrGR+ABEADERAtPwnStV5VJhyqY+ef+ClwL3Rk1\n" +
            "tqIcpuedG6dNH8N0fT/pJlk6fg0Bb4t6ruJgZoOjtLy6jFB/MT0hB8x+7DElfiKR\n" +
            "CbqIFZQI3EgGM7NlcBA1olGhS1/gHTQ+OFsvHXFxyUKcp1U26wIbXeD+cva0MhPS\n" +
            "hsc+6PG6qglwCQqyYHhSnYAZDG7cMdiQoCHoor3moAcE6HRkriQbJjpGAXph3qBV\n" +
            "Q4yIaJA3tNnvUZCFZh1LpaSz6OKqztfZBA1kIm9kSDGeUzZV7bjqHysRvakzQbiQ\n" +
            "3pfuOxea1wFODYHt90OhSqPOKDwqMdVMDLUNIpV5uRWb8rvXZC2+A1yYCno32J98\n" +
            "c0BUDzKrQTY5RLGsOXycJ1u7Lubf7oTQjBRFQq6a9ZGKGClGTCCDlg9kTY1xgua3\n" +
            "28CwM+YSg+HKFNr7HG+JKEKlw+DN8L/9i4GFXOgKFZT2a/bNzfIQztZWiDBGR3o/\n" +
            "yl4yfTJlCsAWgaAaBGx/kFBqR6J7m8eyMqUok9Gz5BywFbVTFzFhyV/uFJTeEOay\n" +
            "BOSwOsXAD/Cgadb6WfT1/3Z+eGek8nhFA6IrEf9bpGCVZODZ264PwcGUgSN8cYxe\n" +
            "xmfWTCbEXP7Q7m+ijNteIVTjBJpVf3AGsNLFkW5qAHA5ZnxpbjBBkwcm9TNeX13N\n" +
            "q9Xfp9kCVy+SDQARAQABiQI2BBgBCAAgFiEEGoU9OvVEjvH5aaPac6eIN5oZdWcF\n" +
            "AlrGR+ACGwwACgkQc6eIN5oZdWewrA//Q0M9Is4BsidTdop+3ns3suPbJXd1lQh8\n" +
            "FgQJ3vS2wRsLBEIn5FtgCbK3lWHa6PXVJrWA0mBZIVNC2LE2/oIAm4HEzI9FkUUD\n" +
            "eaOTFPa32kKShRaom7xA6HzzWezWp5trC10l3Klk+meeiWMWD/6XnMINBP4IDeel\n" +
            "6M1QiAurjZhlvELHmEPakqFGAx4a4XKWY6p9QnUie49JVxupIlWa6gLh32F6MpoV\n" +
            "k1emyDnO+BdDFd1Hogjm1YPIfpoY7porD0iDN1JH+l9JYPxzjUQwmnXE6PIvvv/V\n" +
            "580ri6nlMbwSG/sHG1bCkUIi9Z34deJwbv4f2Nu+mWZmAhF21qCGA+FxbNX2a9ve\n" +
            "w6wYIqP7tfHMQTbMbrRrJr7NC7kKOQY7t7i1NkmwDYAiDoQSDCexp+7z+BLCROiB\n" +
            "wX0fn79GJsmFU29dgu0Eo2sIX0wTMnzejQdDIBjPxX8lQI7NkD613iQWCpxL3IQo\n" +
            "FOUIhCPBNmoEgy+SBNVbB5G2koFqia+5QZVnCLL/5kHOUWo6diolvsijagZT+NtK\n" +
            "MeV3PB6ml63xfP6OSRxfTweGPaTb3NDD8IpmA0YQPWagYjfffimHe3VE0JG2CNjh\n" +
            "dwzw73+BlIcLgU/SPX9t4y4gMHmLuijvDRBjPGElnFcoORm3hiA+0CE/Bq9uWdYz\n" +
            "/Ov2n9t3ruc=\n" +
            "=llAa\n" +
            "-----END PGP PUBLIC KEY BLOCK-----\n";

    public static void verify() {
        String checksums = "6def40625a91a259ae7ea7d5956c65a60b847c03  baritone-api-1.2.4.jar\n" +
                "49802552ad926686bd6b04226fdc1cca4d4bdf32  baritone-api-forge-1.2.4.jar\n" +
                "2dd1466966765627008c203b08880e453b7b3d54  baritone-standalone-1.2.4.jar\n" +
                "fa6d90d458f9d56d3716a7877bd6d32c4f6893ab  baritone-standalone-forge-1.2.4.jar\n" +
                "d7e43bbdf5a950d24407bc39fefaa7fd8d3ffd7e  baritone-unoptimized-1.2.4.jar\n";
        String firstSig = "-----BEGIN PGP SIGNATURE-----\n" +
                "\n" +
                "iQIzBAABCgAdFiEED97d4NtRvZnT0gnFRKPqZG6trGoFAlyJrlsACgkQRKPqZG6t\n" +
                "rGqF8g/+L4t+DpUUttW3fXoNIlDVYWBiU7UaUkiq7+nLy/34dHiuC869HjKI5FEZ\n" +
                "UGqgxgreHAwwuhAZWrPvuenVeSnVBtX4V8XA08Ahcvqp1cxrkxoiq/goyEbbSqeD\n" +
                "UxwQyWCKhPGOVM6xFL5w94bEzKWY9SaHwVcbVLUqVYYtMPmzOe8Ax6WGCGrlAdKJ\n" +
                "LFD5KLUj70fsFjzc7JPmr2j0gBBYH98JGV5INEVZS3/kkOSd/Z51ANZjXJJ5LViA\n" +
                "BCtUhtk141S0BM8PqIwZXedon0QD1senw0TIOqA08hwOOJveqEIBz4wlxWvPPlQT\n" +
                "RQRTOPdzUFh1J91HULW8uE7sY322gNAc233yjrKb3eguQmBK4atcJ2GfMbl5ZkOW\n" +
                "JWRFgUISZJMbcfwcwb+7KcX4QJ3RcGkLpdPxAwp5qRvx1of0HKfk4EkKf27ty3FF\n" +
                "6gOwbYamORzO9W/sw6LtJIW9RfXSYddfVEJz90Uh9ENkpulj6318cMSJT5KNgjKu\n" +
                "pZYLAImplriPDp5R/e/uxu4bJ2nCZmqAnNmu0WE44qtlX1/fzW2Toap4BQ0g3gVl\n" +
                "ed/9TVvgRbiPc0xt6MWjKlbweCQSjxIUboFy/9GGUbA8O0O3ZUw5plFuZMOO4yyd\n" +
                "cTF8seCi9ZZ5s2RJn/VJnyK/t0wcBwzVuSXoXhquOXDq1urp9q4=\n" +
                "=607H\n" +
                "-----END PGP SIGNATURE-----";
        String secondSig = "-----BEGIN PGP SIGNATURE-----\n" +
                "\n" +
                "iQIzBAABCAAdFiEEGoU9OvVEjvH5aaPac6eIN5oZdWcFAlyJuWQACgkQc6eIN5oZ\n" +
                "dWeLYw/9EVoOPgTVOqDgE5CBg2j8K/TTh/qq3QXQYYwn8Z96Imd5fjMWOKGCjtSN\n" +
                "k3T3M88D46IW3WXLh/ge3rZjr0+I/6il/Bjh0J9irQGaIQBno6AVljcNj+096j3Z\n" +
                "X9i2BL0oJlNHAm93d7QyFQ1wvWz3dchbGCuv+G/urKcM1ZykTHBznxCNWrrZAP+c\n" +
                "uA1KPxPBq4c7/IRx/N9cZG6cGtG0eADsC4tkaasmFct1hXN3pUd8UGTw2EQV03Aa\n" +
                "dVu4Xqs4qYJGlrWWvNV6BmL3qr1wKAt7Oxw//kK+Nm6TlH3RWR7xqlktar76S9IM\n" +
                "8wylXSClW+QQQRd8nejO7wxqSKCpg+xu44SUePpeFWgHc4/GHHLAd6Rd2OD1lG00\n" +
                "trIcjvUS3Suiy/yzzry50VGGTX/FJwG4VkWOD6CQXolMJlBNV1cWlnBrHyIB7MpT\n" +
                "x+IBw8/r3g7uqYDBQnCnNTw8MGLP8EzHgQvjLWsf6SvnDTwWFyUd4WRrOiHwrtBl\n" +
                "LSzlLJzoNjyNUbNmQEir3GaH7X9/k1anRtwkFp0881PTYGK7zUqsBd+ph8HmB4Az\n" +
                "9vx1e1VG9tHPMZ4sNGkb9g3LWH5ov0uEkoL+W3p6dO2QNkNa1BQo1CwZFi3hNXoG\n" +
                "Cn5tuBYyjR7TB0bRcF9MaVotl96nH0lLhNDx3BPSpBPYFu1Ep+Y=\n" +
                "=katu\n" +
                "-----END PGP SIGNATURE-----";
        System.out.println(verify(checksums.getBytes(), firstSig.getBytes()));
        System.out.println(verify(checksums.getBytes(), secondSig.getBytes()));
    }

}
