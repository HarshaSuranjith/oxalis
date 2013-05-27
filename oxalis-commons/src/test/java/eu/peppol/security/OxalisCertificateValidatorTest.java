package eu.peppol.security;

import eu.peppol.start.identifier.KeystoreManager;
import org.testng.annotations.Test;

/**
 * @author steinar
 *         Date: 27.05.13
 *         Time: 12:55
 */
public class OxalisCertificateValidatorTest {

    @Test
    public void validateOurCertificate() throws Exception {

        long start = System.currentTimeMillis();
        KeystoreManager keystoreManager = KeystoreManager.getInstance();
        long end = System.currentTimeMillis();

        OxalisCertificateValidator.INSTANCE.validate(keystoreManager.getOurCertificate());

        long complete = System.currentTimeMillis();

        long initElapsed = end - start;
        long validationElapsed = complete - end;
        System.out.printf("Init: %d, validation: %d, diff: %d\n", initElapsed, validationElapsed, validationElapsed-initElapsed);
    }
}
