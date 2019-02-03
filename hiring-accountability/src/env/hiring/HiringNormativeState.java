package hiring;

import java.awt.image.ComponentColorModel;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import twocomm.core.Commitment;
import twocomm.core.IPlayer;
import twocomm.core.LifeCycleState;
import twocomm.core.ProtocolArtifact;
import twocomm.core.RoleId;
import twocomm.core.logic.*;
import twocomm.core.automated.AutomatedSocialState;
import twocomm.core.automated.AutomatedSocialStateSingleThreaded;
import twocomm.exception.MissingOperandException;
import twocomm.exception.WrongOperandsNumberException;
import cartago.AgentId;
import cartago.OPERATION;
import cartago.OperationException;
import moise.os.ss.Compatibility;
import cartago.LINK;


public class HiringNormativeState extends ProtocolArtifact {

	protected Logger logger = LogManager.getLogger(HiringNormativeState.class);
	public static String ARTIFACT_TYPE = "HiringNormativeState";
	public static String GENERIC_ROLE = "generic";
	
	private JFrame socialStateGui;
	private JTextArea socialStateTextArea;

	public HiringNormativeState() {
		super();
		socialState = new AutomatedSocialStateSingleThreaded(this);
		socialStateGui = new JFrame("Normative state");
		socialStateGui.setSize(600, 1000);
		socialStateTextArea = new JTextArea();
		socialStateTextArea.setLineWrap(true);
		JScrollPane scroll = new JScrollPane(socialStateTextArea);
		socialStateGui.add(scroll);
		socialStateTextArea.setText(socialState.toString());
		socialStateGui.setVisible(true);
	}

	@Override
	public String getArtifactType() {
		return ARTIFACT_TYPE;
	}
	
	@LINK
	public void initCommitmentsBoHi(RoleId bossId, RoleId hirerId) throws MissingOperandException, WrongOperandsNumberException {
		
		LogicalExpression ant;
		LogicalExpression cons;
		
		Fact postJob = new Fact("postJob", hirerId.toString());
		Fact openPosition = new Fact("openPosition", bossId.toString());
		Fact offerAccepted = new Fact("offerAccepted", "evaluator");
		Fact positionFilled = new Fact("positionFilled", hirerId.toString());
		Fact timeout = new Fact("timeout3months", hirerId.toString());
		Fact positionAbandoned = new Fact("positionAbandoned", hirerId.toString());
		
		ant = offerAccepted;
		cons = new CompositeExpression(LogicalOperatorType.THEN, offerAccepted, positionFilled);
		RoleId evaluator = new RoleId("evaluator",2);
		Commitment c3 = new Commitment(hirerId, evaluator, ant, cons);
		createCommitment(c3);
		
		ant = openPosition;
		cons = new CompositeExpression(LogicalOperatorType.THEN, openPosition, postJob);
		
		Commitment c5 = new Commitment(hirerId, bossId, ant, cons);
		createCommitment(c5);
		
		ant = new CompositeExpression(LogicalOperatorType.THEN, postJob,
				new CompositeExpression(LogicalOperatorType.OR, offerAccepted, timeout));
		LogicalExpression hiring = new CompositeExpression(LogicalOperatorType.THEN, postJob, 
				new CompositeExpression(LogicalOperatorType.OR,
						new CompositeExpression(LogicalOperatorType.THEN, offerAccepted, positionFilled),
						new CompositeExpression(LogicalOperatorType.THEN, timeout, positionAbandoned)));
		cons = hiring;
		Commitment c6 = new Commitment(hirerId, bossId, ant, cons);
		createCommitment(c6);
		
		socialStateTextArea.setText(socialState.toString());
		
	}
	
	@LINK
	public void initCommitmentsBoHiEvCa(RoleId bossId, RoleId hirerId, RoleId evaluatorId, RoleId candidateId) throws MissingOperandException, WrongOperandsNumberException {
		
		LogicalExpression ant;
		LogicalExpression cons;
		
		Fact postJob = new Fact("postJob", hirerId.toString());
		Fact positionFilled = new Fact("positionFilled", hirerId.toString());
		Fact apply = new Fact("apply", candidateId.toString());
		Fact checkPosition = new Fact("checkPosition", evaluatorId.toString());
		Fact screenInterview = new Fact("screenInterview", evaluatorId.toString());
		Fact positionClosed = new Fact("msgPositionClosed", evaluatorId.toString());
		Fact rejectionNotice = new Fact("msgRejectionNotice", evaluatorId.toString());
		Fact makeOffer = new Fact("makeOffer", evaluatorId.toString());
		Fact responseYes = new Fact("responseYes", candidateId.toString());
		Fact responseNo = new Fact("responseNo", candidateId.toString());
		Fact offerAccepted = new Fact("offerAccepted", evaluatorId.toString());
		Fact offerRejected = new Fact("offerRejected", evaluatorId.toString());
		
		ant = new CompositeExpression(LogicalOperatorType.THEN, postJob, apply);
		LogicalExpression evaluateCandidate = new CompositeExpression(LogicalOperatorType.OR, 
				new CompositeExpression(LogicalOperatorType.THEN, positionFilled, positionClosed),
				new CompositeExpression(LogicalOperatorType.OR,
						new CompositeExpression(LogicalOperatorType.THEN, checkPosition, positionClosed),
						new CompositeExpression(LogicalOperatorType.THEN, checkPosition,
							new CompositeExpression(LogicalOperatorType.THEN, screenInterview,
									new CompositeExpression(LogicalOperatorType.OR, rejectionNotice,
											new CompositeExpression(LogicalOperatorType.THEN, makeOffer,
													new CompositeExpression(LogicalOperatorType.OR, 
															new CompositeExpression(LogicalOperatorType.THEN, responseYes, offerAccepted), 
															new CompositeExpression(LogicalOperatorType.THEN, responseNo, offerRejected)
													)
											)
									)
							)
						)
				)
		);
		cons = new CompositeExpression(LogicalOperatorType.THEN, postJob,
				new CompositeExpression(LogicalOperatorType.THEN, apply, evaluateCandidate));
		Commitment c1 = new Commitment(evaluatorId, hirerId, ant, cons);
		createCommitment(c1);
		
		ant = new CompositeExpression(LogicalOperatorType.THEN, postJob, apply);
		LogicalExpression informOutcome = new CompositeExpression(LogicalOperatorType.OR, positionClosed, 
				new CompositeExpression(LogicalOperatorType.OR, rejectionNotice, makeOffer));
		cons = new CompositeExpression(LogicalOperatorType.THEN, postJob,
				new CompositeExpression(LogicalOperatorType.THEN, apply, informOutcome));
		Commitment c2 = new Commitment(evaluatorId, candidateId, ant, cons);
		createCommitment(c2);
		
		ant = makeOffer;
		cons = new CompositeExpression(LogicalOperatorType.THEN, makeOffer, new CompositeExpression(LogicalOperatorType.OR, responseYes, responseNo));
		Commitment c4 = new Commitment(candidateId, evaluatorId, ant, cons);
		createCommitment(c4);
		
		socialStateTextArea.setText(socialState.toString());

	}

	@LINK
	public void terminateCommitments(RoleId roleId) {
		ArrayList<Commitment> comms = new ArrayList<>();
		comms.addAll(socialState.retrieveCommitmentsByDebtorRoleId(roleId));
		comms.addAll(socialState.retrieveCommitmentsByCreditorRoleId(roleId));
		for(Commitment c : comms) {
			if(c.getLifeCycleStatus() == LifeCycleState.CONDITIONAL) {
				releaseCommitment(c);
			}
		}
		
		socialStateTextArea.setText(socialState.toString());
		
	}
	
	@LINK
	public void openPosition(RoleId bossId) throws MissingOperandException {
		assertFact(new Fact("openPosition", bossId.toString()));
		logger.trace("NORMATIVE STATE: fact asserted openPosition(" + bossId.toString() + ")");
		
		socialStateTextArea.setText(socialState.toString());
		
	}
	
	@LINK
	public void checkPosition(RoleId evaluatorId) throws MissingOperandException {
		assertFact(new Fact("checkPosition", evaluatorId.toString()));
		logger.trace("NORMATIVE STATE: fact asserted checkPosition(" + evaluatorId.toString() + ")");
		
		socialStateTextArea.setText(socialState.toString());
		
	}
	
	@LINK
	public void postJob(RoleId hirerId) throws MissingOperandException {
		assertFact(new Fact("postJob", hirerId.toString()));
		logger.trace("NORMATIVE STATE: fact asserted postJob(" + hirerId.toString() + ")");
		
		socialStateTextArea.setText(socialState.toString());
		
	}
	
	@LINK
	public void updateFilled(RoleId hirerId) throws MissingOperandException {
		assertFact(new Fact("positionFilled", hirerId.toString()));
		logger.trace("NORMATIVE STATE: fact asserted positionFilled(" + hirerId.toString() + ")");
		
		socialStateTextArea.setText(socialState.toString());
		
	}
	
	@LINK
	public void timeout(RoleId hirerId) throws MissingOperandException {
		assertFact(new Fact("timeout3months", hirerId.toString()));
		logger.trace("NORMATIVE STATE: fact asserted timeout3months(" + hirerId.toString() + ")");
		
		socialStateTextArea.setText(socialState.toString());
		
	}
	
	@LINK
	public void updateAbandoned(RoleId hirerId) throws MissingOperandException {
		assertFact(new Fact("positionAbandoned", hirerId.toString()));
		logger.trace("NORMATIVE STATE: fact asserted positionAbandoned(" + hirerId.toString() + ")");
		
		socialStateTextArea.setText(socialState.toString());
		
	}
	
	@LINK
	public void apply(RoleId candidateId) throws MissingOperandException {
		assertFact(new Fact("apply", candidateId.toString()));
		logger.trace("NORMATIVE STATE: fact asserted apply(" + candidateId.toString() + ")");
		
		socialStateTextArea.setText(socialState.toString());
		
	}
	
	@LINK
	public void screenInterview(RoleId evaluatorId) throws MissingOperandException {
		assertFact(new Fact("screenInterview", evaluatorId.toString()));
		logger.trace("NORMATIVE STATE: fact asserted screenInterview(" + evaluatorId.toString() + ")");
		
		socialStateTextArea.setText(socialState.toString());
		
	}
	
	@LINK
	public void positionClosed(RoleId evaluatorId) throws MissingOperandException {
		assertFact(new Fact("msgPositionClosed", evaluatorId.toString()));
		logger.trace("NORMATIVE STATE: fact asserted msgPositionClosed(" + evaluatorId.toString() + ")");
		
		socialStateTextArea.setText(socialState.toString());
		
	}
	
	@LINK
	public void rejectionNotice(RoleId evaluatorId) throws MissingOperandException {
		assertFact(new Fact("msgRejectionNotice", evaluatorId.toString()));
		logger.trace("NORMATIVE STATE: fact asserted msgRejectionNotice(" + evaluatorId.toString() + ")");
		
		socialStateTextArea.setText(socialState.toString());
		
	}
		
	@LINK
	public void makeOffer(RoleId evaluatorId) throws MissingOperandException {
		assertFact(new Fact("makeOffer", evaluatorId.toString()));
		logger.trace("NORMATIVE STATE: fact asserted makeOffer(" + evaluatorId.toString() + ")");
		
		socialStateTextArea.setText(socialState.toString());
		
	}
	
	@LINK
	public void responseYes(RoleId candidateId) throws MissingOperandException {
		assertFact(new Fact("responseYes", candidateId.toString()));
		logger.trace("NORMATIVE STATE: fact asserted responseYes(" + candidateId.toString() + ")");
		
		socialStateTextArea.setText(socialState.toString());
		
	}
	
	@LINK
	public void responseNo(RoleId candidateId) throws MissingOperandException {
		assertFact(new Fact("responseNo", candidateId.toString()));
		logger.trace("NORMATIVE STATE: fact asserted responseNo(" + candidateId.toString() + ")");
		
		socialStateTextArea.setText(socialState.toString());
		
	}
	
	@LINK
	public void offerAccepted(RoleId evaluatorId) throws MissingOperandException {
		assertFact(new Fact("offerAccepted", evaluatorId.toString()));
		logger.trace("NORMATIVE STATE: fact asserted offerAccepted(" + evaluatorId.toString() + ")");
		Fact f = new Fact("offerAccepted", "evaluator");
		if(!socialState.existsFact(f)) {
			assertFact(f);
			logger.trace("NORMATIVE STATE: fact asserted offerAccepted(evaluator)");
		}
		
		socialStateTextArea.setText(socialState.toString());
		
	}
	
	@LINK
	public void offerRejected(RoleId evaluatorId) throws MissingOperandException {
		assertFact(new Fact("offerRejected", evaluatorId.toString()));
		logger.trace("NORMATIVE STATE: fact asserted offerRejected(" + evaluatorId.toString() + ")");
		
		socialStateTextArea.setText(socialState.toString());
		
	}

}