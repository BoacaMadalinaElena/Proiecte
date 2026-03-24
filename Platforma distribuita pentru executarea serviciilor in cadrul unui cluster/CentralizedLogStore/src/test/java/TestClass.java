import org.example.service.ValidateParameter;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/*
public class TestClass {
    private final ValidateParameter validateParameter = new ValidateParameter();
    @Test
    public void testTrueUserId(){
        String[] validStrings = {"123", "456-789", "0-98765", "1-2-3-4-5", "987-654-321","6336b66b-e2fa-48a1-87ff-81b9ecf5ec4d"};

        for (String str : validStrings) {
            assertTrue(validateParameter.isValidUserId(str));
        }
    }

    @Test
    public void testFalseUserId() {
        String[] invalidStrings = { "-123", "123-"};

        for (String str : invalidStrings) {
            assertFalse(validateParameter.isValidUserId(str));
        }
    }

    @Test
    public void testTrueType(){
        String[] validStrings = {"INFO", "info", "inFO"};

        for (String str : validStrings) {
            assertTrue(validateParameter.isValidType(str));
        }
    }

    @Test
    public void testFalseType() {
        String[] invalidStrings = {"123", "' or 1==1", "v.sfd"};

        for (String str : invalidStrings) {
            assertFalse(validateParameter.isValidType(str));
        }
    }

    @Test
    public void testTrueTime(){
        String[] validStrings = {"31/03/2024 14:45:53", "31/03/2024 14:45", "31/03/2024 14"};

        for (String str : validStrings) {
            assertTrue(validateParameter.isValidDateTimeFormat(str));
        }
    }

    @Test
    public void testFalseTime() {
        String[] invalidStrings = {"34/130/2024 14:45:53", "03/39/2023 14:45:53", "2024/03 14:45:53"};

        for (String str : invalidStrings) {
            System.out.println(str + " " + validateParameter.isValidDateTimeFormat(str));
            assertTrue(validateParameter.isValidDateTimeFormat(str));
        }
    }

    @Test
    public void testTrueIP(){
        String[] validStrings = {"192.168.1.100:8080", "192.168.1.100:45561", "145.0.0.0"};

        for (String str : validStrings) {
            assertTrue(validateParameter.isValidIPAddress(str));
        }
    }

    @Test
    public void testFalseIp() {
        String[] invalidStrings = {"300.168.1.255:", "192.168.1", "192.500.1.100"};

        for (String str : invalidStrings) {
            assertFalse(validateParameter.isValidIPAddress(str));
        }
    }
}
*/