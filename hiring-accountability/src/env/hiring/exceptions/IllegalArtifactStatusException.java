package hiring.exceptions;

import cartago.CartagoException;
import hiring.ApplicationStatus;
import hiring.PositionStatus;

public class IllegalArtifactStatusException extends CartagoException {

	public IllegalArtifactStatusException(PositionStatus status) {
		System.out.println("Position status is " + status.toString());
		printStackTrace();
	}
	
	public IllegalArtifactStatusException(ApplicationStatus status) {
		System.out.println("Application status is " + status.toString());
		printStackTrace();
	}
	
}
