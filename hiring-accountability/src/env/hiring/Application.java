package hiring;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Arrays;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import twocomm.core.IPlayer;
import twocomm.core.ProtocolArtifact;
import twocomm.core.Role;
import twocomm.core.BusinessArtifact;
import twocomm.core.RoleId;
import twocomm.core.RoleMessage;
import twocomm.core.Commitment;
import twocomm.core.LifeCycleState;
import twocomm.core.logic.CompositeExpression;
import twocomm.core.logic.Fact;
import twocomm.core.automated.AutomatedSocialState;
import twocomm.core.logic.LogicalOperatorType;
import twocomm.exception.MissingOperandException;
import twocomm.exception.WrongOperandsNumberException;
import cartago.*;
import hiring.exceptions.IllegalArtifactStatusException;

public class Application extends BusinessArtifact {

	protected Logger logger = LogManager.getLogger(Application.class);
	
	public static String ARTIFACT_TYPE = "Application";
	
	private ArtifactId normativeArtifact;
	private ArtifactId workspaceManager;
	
	private RoleId evaluator;
	private RoleId candidate;
	
	private ApplicationStatus status = ApplicationStatus.NO_APPLICATION;
	
	@OPERATION
	public void init() {
		defineObsProperty("applicationStatus", status.toString());
	}
	
	@LINK
	void initNormativeArtifact() {
		if (normativeArtifact == null) {
			
			try {
				normativeArtifact = lookupArtifact("hiringNormativeState");	
			} catch (CartagoException e) {
				e.printStackTrace();	
			}

		}
	}
	
	@LINK
	void initWorkspaceManager() {
		if (workspaceManager == null) {
			
			try {
				workspaceManager = lookupArtifact("workspaceManager");	
			} catch (CartagoException e) {
				e.printStackTrace();	
			}

		}
	}
	
	@Override
	public String getArtifactType() {
		return ARTIFACT_TYPE;
	}
	
	@OPERATION
	public void setEvaluator(RoleId evaluatorId) {
		
		if(evaluator == null) {
			evaluator = evaluatorId;
		}
		else {
			failed("Evaluator already set!");
		}
	}
	
	@OPERATION
	public void setCandidate(RoleId candidateId) {
		
		if(candidate == null) {
			candidate = candidateId;
		}
		else {
			failed("Candidate already set!");
		}
	}

	// ROLES OPERATIONS
	
	@OPERATION
	public void apply() throws IllegalArtifactStatusException, MissingOperandException, OperationException, InterruptedException {
		
		if(status != ApplicationStatus.NO_APPLICATION) {
			throw new IllegalArtifactStatusException(status);
		}
		
		Random r = new Random();
		Thread.sleep(r.nextInt(4000) + 1000);
		
		RoleId candidateId = getPlayerRoleId(getCurrentOpAgentId().getAgentName());
		assertFact(new Fact("apply", candidateId.toString()));
		logger.trace("APPLICATION: fact asserted apply(" + candidateId.toString() + ")");
		
		status = ApplicationStatus.APPLICATION_SUBMITTED;
		removeObsProperty("applicationStatus");
		defineObsProperty("applicationStatus", status.toString());
		
		execLinkedOp(normativeArtifact, "apply", candidateId);
		
	}
	
	@OPERATION
	public void screenInterview() throws IllegalArtifactStatusException, MissingOperandException, OperationException, InterruptedException {
		
		if(status != ApplicationStatus.APPLICATION_SUBMITTED) {
			throw new IllegalArtifactStatusException(status);
		}
		
		Random r = new Random();
		Thread.sleep(r.nextInt(4000) + 1000);
		
		RoleId evaluatorId = getPlayerRoleId(getCurrentOpAgentId().getAgentName());
		assertFact(new Fact("screenInterview", evaluatorId.toString()));
		logger.trace("APPLICATION: fact asserted screenInterview(" + evaluatorId.toString() + ")");
		
		status = ApplicationStatus.APPLICATION_ASSESSED;
		removeObsProperty("applicationStatus");
		defineObsProperty("applicationStatus", status.toString());
		
		execLinkedOp(normativeArtifact, "screenInterview", evaluatorId);
		
	}
	
	@OPERATION
	public void positionClosed() throws OperationException, MissingOperandException, IllegalArtifactStatusException, InterruptedException {
		
	//		if(status != ApplicationStatus.APPLICATION_SUBMITTED) {
	//		throw new IllegalArtifactStatusException(status);
	//	}
		
		Random r = new Random();
		r.nextInt(5000);
		Thread.sleep(r.nextInt(4000) + 1000);
		
		RoleId evaluatorId = getPlayerRoleId(getCurrentOpAgentId().getAgentName());
		assertFact(new Fact("msgPositionClosed", evaluatorId.toString()));
		logger.trace("APPLICATION: fact asserted msgPositionClosed(" + evaluatorId.toString() + ")");
		
		if(status != ApplicationStatus.OFFER_ACCEPTED) {
			status = ApplicationStatus.APPLICATION_REJECTED;
			removeObsProperty("applicationStatus");
			defineObsProperty("applicationStatus", status.toString());
		}
		
		execLinkedOp(normativeArtifact, "positionClosed", evaluatorId);
		execLinkedOp(normativeArtifact, "terminateCommitments", evaluatorId);
		
	}
	
	@OPERATION
	public void rejectionNotice() throws IllegalArtifactStatusException, MissingOperandException, OperationException, InterruptedException {
		
		if(status != ApplicationStatus.APPLICATION_ASSESSED) {
			throw new IllegalArtifactStatusException(status);
		}
		
		Random r = new Random();
		Thread.sleep(r.nextInt(4000) + 1000);
		
		RoleId evaluatorId = getPlayerRoleId(getCurrentOpAgentId().getAgentName());
		assertFact(new Fact("msgRejectionNotice", evaluatorId.toString()));
		logger.trace("APPLICATION: fact asserted msgRejectionNotice(" + evaluatorId.toString() + ")");
		
		status = ApplicationStatus.APPLICATION_REJECTED;
		removeObsProperty("applicationStatus");
		defineObsProperty("applicationStatus", status.toString());
		
		execLinkedOp(normativeArtifact, "rejectionNotice", evaluatorId);
		execLinkedOp(normativeArtifact, "terminateCommitments", evaluatorId);
		
	}
		
	@OPERATION
	public void makeOffer() throws IllegalArtifactStatusException, MissingOperandException, OperationException, InterruptedException {
		
		if(status != ApplicationStatus.APPLICATION_ASSESSED) {
			throw new IllegalArtifactStatusException(status);
		}
		
		Random r = new Random();
		Thread.sleep(r.nextInt(4000) + 1000);
		
		RoleId evaluatorId = getPlayerRoleId(getCurrentOpAgentId().getAgentName());
		assertFact(new Fact("makeOffer", evaluatorId.toString()));
		logger.trace("APPLICATION: fact asserted makeOffer(" + evaluatorId.toString() + ")");
		
		status = ApplicationStatus.OFFER_MADE;
		removeObsProperty("applicationStatus");
		defineObsProperty("applicationStatus", status.toString());
		
		execLinkedOp(normativeArtifact, "makeOffer", evaluatorId);
		
	}
	
	@OPERATION
	public void responseYes() throws IllegalArtifactStatusException, MissingOperandException, OperationException, InterruptedException {
		
		if(status != ApplicationStatus.OFFER_MADE) {
			throw new IllegalArtifactStatusException(status);
		}
		
		Random r = new Random();
		Thread.sleep(r.nextInt(4000) + 1000);
		
		RoleId candidateId = getPlayerRoleId(getCurrentOpAgentId().getAgentName());
		assertFact(new Fact("responseYes", candidateId.toString()));
		logger.trace("APPLICATION: fact asserted responseYes(" + candidateId.toString() + ")");
		
		status = ApplicationStatus.OFFER_ACCEPTED;
		removeObsProperty("applicationStatus");
		defineObsProperty("applicationStatus", status.toString());
		
		execLinkedOp(normativeArtifact, "responseYes", candidateId);
		
	}
	
	@OPERATION
	public void responseNo() throws IllegalArtifactStatusException, MissingOperandException, OperationException, InterruptedException {
		
		if(status != ApplicationStatus.OFFER_MADE) {
			throw new IllegalArtifactStatusException(status);
		}
		
		Random r = new Random();
		Thread.sleep(r.nextInt(4000) + 1000);
		
		RoleId candidateId = getPlayerRoleId(getCurrentOpAgentId().getAgentName());
		assertFact(new Fact("responseNo", candidateId.toString()));
		logger.trace("APPLICATION: fact asserted responseNo(" + candidateId.toString() + ")");
		
		status = ApplicationStatus.OFFER_REJECTED;
		removeObsProperty("applicationStatus");
		defineObsProperty("applicationStatus", status.toString());
		
		execLinkedOp(normativeArtifact, "responseNo", candidateId);		
		
	}
	
	@OPERATION
	public void offerAccepted() throws IllegalArtifactStatusException, MissingOperandException, OperationException, InterruptedException {
		
		if(status != ApplicationStatus.OFFER_ACCEPTED) {
			throw new IllegalArtifactStatusException(status);
		}
		
		Random r = new Random();
		Thread.sleep(r.nextInt(4000) + 1000);
		
		RoleId evaluatorId = getPlayerRoleId(getCurrentOpAgentId().getAgentName());
		assertFact(new Fact("offerAccepted", evaluatorId.toString()));
		logger.trace("APPLICATION: fact asserted offerAccepted(" + evaluatorId.toString() + ")");
		
		execLinkedOp(normativeArtifact, "offerAccepted", evaluatorId);
		//execLinkedOp(normativeArtifact, "terminateCommitments", evaluatorId);
		
	}
	
	@OPERATION
	public void offerRejected() throws IllegalArtifactStatusException, MissingOperandException, OperationException, InterruptedException {
		
		if(status != ApplicationStatus.OFFER_REJECTED) {
			throw new IllegalArtifactStatusException(status);
		}
		
		Random r = new Random();
		Thread.sleep(r.nextInt(4000) + 1000);
		
		RoleId evaluatorId = getPlayerRoleId(getCurrentOpAgentId().getAgentName());
		assertFact(new Fact("offerRejected", evaluatorId.toString()));
		logger.trace("APPLICATION: fact asserted offerRejected(" + evaluatorId.toString() + ")");
		
		execLinkedOp(normativeArtifact, "offerRejected", evaluatorId);
		execLinkedOp(normativeArtifact, "terminateCommitments", evaluatorId);
		
	}
	
	private RoleId getPlayerRoleId(String playerName) throws OperationException {
		OpFeedbackParam<RoleId> r = new OpFeedbackParam<>();
		execLinkedOp(workspaceManager, "getPlayerRoleId", getCurrentOpAgentId().getAgentName(), r);
		return r.get();
	}

}