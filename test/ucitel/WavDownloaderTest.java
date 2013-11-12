/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ucitel;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author peta
 */
public class WavDownloaderTest {
    
    public WavDownloaderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of run method, of class WavDownloader.
     */
    @Test
    public void testRun() {
        System.out.println("run");
        WavDownloader instance = new WavDownloader();
        instance.run();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of downLoad method, of class WavDownloader.
     */
    @Test
    public void testDownLoad() {
        System.out.println("downLoad");
        String word = "bestow";
        WavDownloader.downLoad(word);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of main method, of class WavDownloader.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        WavDownloader.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of exists method, of class WavDownloader.
     */
    @Test
    public void testExists() {
        System.out.println("exists");
        String word = "";
        boolean expResult = false;
        boolean result = WavDownloader.exists(word);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
