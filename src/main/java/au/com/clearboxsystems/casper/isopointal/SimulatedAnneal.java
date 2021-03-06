package au.com.clearboxsystems.casper.isopointal;

/**
 * Created by pauls on 16/09/15.
 */
public class SimulatedAnneal {

	public IsopointalSetResult findMinimumEnergy(int numRuns, int trialsPerRun, int spaceGroup, String[] wyckoffSites, double potential_param1, double potential_param2, double potential_param3) {
		double startkT = 2;
		double endT = 0.1;

		IsopointalSetFactory isopointalSetFactory = new IsopointalSetFactory();
		double minEnergy = Double.MAX_VALUE;
		IsopointalSetResult minResult = null;
		for (int i = 0; i < numRuns; i++) {
			IsopointalSet isopointalSet = isopointalSetFactory.getIsopointalSet(spaceGroup, wyckoffSites);
			IsopointalSetResult result = runSimulatedAnneal(trialsPerRun, startkT, endT, isopointalSet, potential_param1, potential_param2, potential_param3);
			if (result.energyPerAtom < minEnergy) {
				minEnergy = result.energyPerAtom;
				minResult = result;
			}
		}

		return minResult;
	}

	public IsopointalSetResult runSimulatedAnneal(int numTrials, double startkT, double endkT, IsopointalSet isopointalSet, double potential_param1, double potential_param2, double potential_param3) {
		//LJEmbeddedAtomPotential pot = new LJEmbeddedAtomPotential(potential_param1, potential_param2, potential_param3); /* p1=A, p2=beta */ /*AJB*/
//		LJGPotential pot = new LJGPotential(potential_param1, potential_param2, potential_param3); /* p1=r0, p2=epsilon, sigmasq=0.02*/
		//LJEAM_Peter_Potential pot = new LJEAM_Peter_Potential(potential_param1, potential_param2, potential_param3); /* p1=A, p2=rhobar0, p3=beta*/
		OPPotential pot = new OPPotential(potential_param1,potential_param2,potential_param3);  /*p1 = k, p2 = phi, p3=blank  */

		double numAtoms = isopointalSet.getNumPositions();

		isopointalSet.updateRandomVariable(1); //must do initial update to make sure crystal variables are set
		double energy = pot.computeEnergy(isopointalSet);
		double lastEnergy = energy;
		double minEnergy = energy;
		IsopointalSetResult minResult = isopointalSet.saveResult();
		double startTime = System.currentTimeMillis();
		double kT = startkT;
		double geometricStep = Math.pow(endkT/startkT, 1.0/numTrials);
		for (int i = 0; i < numTrials; i++) {
			kT *= geometricStep;
//			if ((i % 10000 == 0)||(lastEnergy==0))
//				System.out.println("kT = " + kT + ", Energy = " + lastEnergy / numAtoms + ", ax= " + isopointalSet.vecA.x + " by= " + isopointalSet.vecB.y + " cz= " + isopointalSet.vecC.z + ", ay= " + isopointalSet.vecA.y + " az= " + isopointalSet.vecA.z + " bz= " + isopointalSet.vecB.z);

			isopointalSet.updateRandomVariable(1);
			energy = pot.computeEnergy(isopointalSet);

			double deltaEnergy = energy - lastEnergy;
//			if (i % 10000 == 0)
//				System.out.println("delta = " + deltaEnergy + ", e = " + Math.exp(-deltaEnergy / kT));
			if ((deltaEnergy < 0 || isopointalSet.random.nextDouble() < Math.exp(-deltaEnergy / kT))) {
				// Accept
				lastEnergy = energy;
				if (lastEnergy < minEnergy) {
					minEnergy = lastEnergy;
					minResult = isopointalSet.saveResult();
				}
			} else {
				// Reject
				isopointalSet.revertLastUpdate();
//				rejectCount++;
			}
		}
		double dt = System.currentTimeMillis() - startTime;

		minResult.energyPerAtom = minEnergy/numAtoms;
		minResult.pot_param1 = potential_param1;
		minResult.pot_param2 = potential_param2;
		minResult.pot_param3 = potential_param3;
		minResult.startkT = startkT;
		minResult.endkT = endkT;
		minResult.numTrials = numTrials;
		minResult.simTime = dt / 1000;
		return minResult;
	}
}
