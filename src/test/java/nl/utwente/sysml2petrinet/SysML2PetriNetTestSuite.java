package nl.utwente.sysml2petrinet;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test suite for SysML2PetriNet transformation
 * Runs all test classes for the transformation system
 */
@RunWith(Suite.class)
@SuiteClasses({
    SysML2PetriNetTest.class
})
public class SysML2PetriNetTestSuite {
    // This class serves as a test suite container
    // All test methods are in the individual test classes
} 