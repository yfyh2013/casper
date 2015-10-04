package au.com.clearboxsystems.casper.isopointal;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by pauls on 16/09/15.
 */
public class SimulatedAnnealTest {

	@Test
	public void testName() throws Exception {
		SimulatedAnneal sim = new SimulatedAnneal();

//		double minEnergy = sim.findMinimumEnergy(2, 100000, 225, new String[]{"a"},  0.5, 6);
//		double minEnergy = sim.findMinimumEnergy(2, 100000, 229, new String[]{"a"},  1, 2);
		double minEnergy = sim.findMinimumEnergy(5, 100000, 89, new String[]{"a", "b", "c", "d", "e", "f", "h"},  0.5, 4);

		System.out.println(minEnergy);

	}
}