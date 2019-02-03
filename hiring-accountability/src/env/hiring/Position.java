package hiring;

import java.util.ArrayList;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import twocomm.core.IPlayer;
import twocomm.core.BusinessArtifact;
import twocomm.core.RoleId;
import twocomm.core.logic.Fact;
import twocomm.core.automated.AutomatedSocialState;
import twocomm.exception.MissingOperandException;
import cartago.*;
import hiring.exceptions.IllegalArtifactStatusException;

public class Position extends BusinessArtifact {

	protected Logger logger = LogManager.getLogger(Position.class);
	
	public static String ARTIFACT_TYPE = "Position";
	
	private ArtifactId normativeArtifact;
	private ArtifactId workspaceManager;
	
	private RoleId boss;
	private RoleId hirer;
	private ArrayList<RoleId> evaluators = new ArrayList<>();
	
	private PositionStatus status = PositionStatus.POSITION_NULL;
	
	@OPERATION
	public void init() {
		defineObsProperty("positionStatus", status.toString());
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
	
	@LINK
	public void setBoss(RoleId bossId) throws OperationException {
		
		if(boss == null) {
			boss = bossId;
		}
		else {
			failed("Boss already set!");
		}

	}
	
	@LINK
	public void setHirer(RoleId hirerId) throws OperationException {
		
		if(hirer == null) {
			hirer = hirerId;
		}
		else {
			failed("Hirer already set!");
		}

	}
	
	@OPERATION
	public void addEvaluator(RoleId evaluatorId) {

		if(!evaluators.contains(evaluatorId)) {
			evaluators.add(evaluatorId);
		}
		else {
			failed("Evaluator already added!");
		}
	}

	// ROLES OPERATIONS

	@OPERATION
	public void openPosition() throws IllegalArtifactStatusException, OperationException, MissingOperandException, InterruptedException {

		if(status != PositionStatus.POSITION_NULL) {
			throw new IllegalArtifactStatusException(status);
		}
		
		Random r = new Random();
		Thread.sleep(r.nextInt(4000) + 1000);
		
		RoleId bossId = getPlayerRoleId(getCurrentOpAgentId().getAgentName());
		assertFact(new Fact("openPosition", bossId.toString()));
		logger.trace("POSITION: fact asserted openPosition(" + bossId.toString() + ")");
		
		status = PositionStatus.POSITION_OPEN;
		removeObsProperty("positionStatus");
		defineObsProperty("positionStatus", status.toString());
		
		execLinkedOp(normativeArtifact, "openPosition", bossId);
	
	}
	
	@OPERATION
	public void checkPosition() throws IllegalArtifactStatusException, OperationException, MissingOperandException, InterruptedException {

//		if(status != PositionStatus.POSITION_OPEN) {
//			throw new IllegalArtifactStatusException(status);
//		}
		
		Random r = new Random();
		Thread.sleep(r.nextInt(4000) + 1000);
		
		RoleId evaluatorId = getPlayerRoleId(getCurrentOpAgentId().getAgentName());
		assertFact(new Fact("checkPosition", evaluatorId.toString()));
		logger.trace("POSITION: fact asserted checkPosition(" + evaluatorId.toString() + ")");
		
		execLinkedOp(normativeArtifact, "checkPosition", evaluatorId);
	
	}
	
	@OPERATION
	public void postJob() throws IllegalArtifactStatusException, OperationException, MissingOperandException, InterruptedException {

		if(status != PositionStatus.POSITION_OPEN) {
			throw new IllegalArtifactStatusException(status);
		}
		
		Random r = new Random();
		Thread.sleep(r.nextInt(4000) + 1000);
		
		RoleId hirerId = getPlayerRoleId(getCurrentOpAgentId().getAgentName());
		assertFact(new Fact("postJob", hirerId.toString()));
		logger.trace("POSITION: fact asserted postJob(" + hirerId.toString() + ")");
		
		execLinkedOp(normativeArtifact, "postJob", hirerId);
	
	}
	
	@OPERATION
	public void updateFilled() throws IllegalArtifactStatusException, MissingOperandException, OperationException, InterruptedException {

		if(status != PositionStatus.POSITION_OPEN) {
			throw new IllegalArtifactStatusException(status);
		}
		
		Random r = new Random();
		Thread.sleep(r.nextInt(4000) + 1000);
		
		RoleId hirerId = getPlayerRoleId(getCurrentOpAgentId().getAgentName());
		assertFact(new Fact("positionFilled", hirerId.toString()));
		logger.trace("POSITION: fact asserted positionFilled(" + hirerId.toString() + ")");
		
		status = PositionStatus.POSITION_FILLED;
		removeObsProperty("positionStatus");
		defineObsProperty("positionStatus", status.toString());
		
		execLinkedOp(normativeArtifact, "updateFilled", hirerId);
		
	}
	
	@OPERATION
	public void timeout() throws IllegalArtifactStatusException, MissingOperandException, OperationException, InterruptedException {
		
		if(status != PositionStatus.POSITION_OPEN) {
			throw new IllegalArtifactStatusException(status);
		}
		
		Random r = new Random();
		Thread.sleep(r.nextInt(4000) + 1000);
		
		RoleId hirerId = getPlayerRoleId(getCurrentOpAgentId().getAgentName());
		assertFact(new Fact("timeout3months", hirerId.toString()));
		logger.trace("POSITION: fact asserted timeout3months(" + hirerId.toString() + ")");
		
		execLinkedOp(normativeArtifact, "timeout", hirerId);
		
	}
	
	@OPERATION
	public void updateAbandoned() throws IllegalArtifactStatusException, MissingOperandException, OperationException, InterruptedException {
		
		if(status != PositionStatus.POSITION_OPEN) {
			throw new IllegalArtifactStatusException(status);
		}
		
		Random r = new Random();
		Thread.sleep(r.nextInt(4000) + 1000);
		
		RoleId hirerId = getPlayerRoleId(getCurrentOpAgentId().getAgentName());
		assertFact(new Fact("positionAbandoned", hirerId.toString()));
		logger.trace("POSITION: fact asserted positionAbandoned(" + hirerId.toString() + ")");
		
		status = PositionStatus.POSITION_ABANDONED;
		removeObsProperty("positionStatus");
		defineObsProperty("positionStatus", status.toString());
		
		execLinkedOp(normativeArtifact, "updateAbandoned", hirerId);
		
		execLinkedOp(normativeArtifact, "terminateCommitments", hirerId);
		
	}
	
	private RoleId getPlayerRoleId(String playerName) throws OperationException {
		OpFeedbackParam<RoleId> r = new OpFeedbackParam<>();
		execLinkedOp(workspaceManager, "getPlayerRoleId", getCurrentOpAgentId().getAgentName(), r);
		return r.get();
	}

}