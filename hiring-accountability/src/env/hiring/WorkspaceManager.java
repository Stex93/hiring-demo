package hiring;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cartago.ArtifactConfig;
import cartago.ArtifactId;
import cartago.LINK;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import cartago.OperationException;
import twocomm.core.BusinessArtifact;
import twocomm.core.IPlayer;
import twocomm.core.Role;
import twocomm.core.RoleId;

public class WorkspaceManager extends BusinessArtifact {
	
	protected Logger logger = LogManager.getLogger(WorkspaceManager.class);
	
	public static String ARTIFACT_TYPE = "WorkspaceManager";
	
	public static String BOSS_ROLE = "boss";
	public static String HIRER_ROLE = "hirer";
	public static String EVALUATOR_ROLE = "evaluator";
	public static String CANDIDATE_ROLE = "candidate";
	
	private RoleId boss;
	
	private RoleId hirer;
	
	private ArrayList<RoleId> pendingEvaluators = new ArrayList<>();
	private ArrayList<RoleId> pendingCandidates = new ArrayList<>();
	private ArrayList<RoleId> evaluators = new ArrayList<>();
	private ArrayList<RoleId> candidates = new ArrayList<>();
	
	private static AtomicLong counter = new AtomicLong();
	
	private ArtifactId normativeArtifact;
	private ArtifactId position;
	private ArrayList<ArtifactId> applications = new ArrayList<>();
	
	static {
		addEnabledRole(BOSS_ROLE, Boss.class);
		addEnabledRole(HIRER_ROLE, Hirer.class);
		addEnabledRole(EVALUATOR_ROLE, Evaluator.class);
		addEnabledRole(CANDIDATE_ROLE, Candidate.class);
	}
	
	@OPERATION
	@Override
	protected void enact(String roleName) {
		
		if(roleName.equals("boss")) {
			
			if(boss != null) {
				failed("There cannot be multiple bosses!");
			}
			
			else {
				
				super.enact(roleName);
				Role r = enactedRoles.get(enactedRoles.size()-1);
				boss = r.getRoleId();
				
				try {
					normativeArtifact = makeArtifact("hiringNormativeState", HiringNormativeState.class.getName(), ArtifactConfig.DEFAULT_CONFIG);
					defineObsProperty("normativeStateId", normativeArtifact);
				} catch (OperationException e) {
					failed("Cannot initialize normative artifact");
				}
				
				try {
					position = makeArtifact("position", Position.class.getName(), ArtifactConfig.DEFAULT_CONFIG);
					execLinkedOp(position, "initNormativeArtifact");
					execLinkedOp(position, "initWorkspaceManager");
					execLinkedOp(position, "setBoss", boss);
					defineObsProperty("positionId", position);
				} catch (OperationException e) {
					failed("Cannot initialize position artifact");
				}
				
			}
			
		}
		
		else if(roleName.equals("hirer")) {
			
			if(boss == null) {
				failed("There is not a boss yet");
			}
			
			else if(hirer != null) {
				failed("There cannot be multiple hirers!");
			}
			
			else {
				
				super.enact(roleName);
				Role r = enactedRoles.get(enactedRoles.size()-1);
				hirer = r.getRoleId();
				
				try {
					execLinkedOp(position, "setHirer", hirer);
					execLinkedOp(normativeArtifact, "initCommitmentsBoHi", boss, hirer);
				} catch (OperationException e) {
					failed("Cannot enact role hirer");
				}
				
			}
			
		}
		
		else if(hirer != null) {
			
			if(roleName.equals("evaluator")) {
			
				super.enact(roleName);
			
				Role r = enactedRoles.get(enactedRoles.size()-1);
				RoleId evaluator = r.getRoleId();
				
				try {
					execLinkedOp(position, "addEvaluator", evaluator);
				} catch (OperationException e) {
					failed("Cannot add evaluator!");
				}
			
				pendingEvaluators.add(evaluator);
			
			}
			
			else if(roleName.equals("candidate")) {
				
				super.enact(roleName);
				
				Role r = enactedRoles.get(enactedRoles.size()-1);
				RoleId candidate = r.getRoleId();
				
				pendingCandidates.add(candidate);
				
			}
			
			if (pendingCandidates.size() == pendingEvaluators.size() && !pendingCandidates.isEmpty()) {
				
				while(!(pendingCandidates.isEmpty() || pendingEvaluators.isEmpty())) {
				
					try {
						
						RoleId evaluator = pendingEvaluators.remove(0);
						RoleId candidate = pendingCandidates.remove(0);
		
						evaluators.add(evaluator);
						candidates.add(candidate);
						
						ArtifactId application = makeArtifact("application" + counter.incrementAndGet(), Application.class.getName(), ArtifactConfig.DEFAULT_CONFIG);
						applications.add(application);
						execLinkedOp(application, "initNormativeArtifact");
						execLinkedOp(application, "initWorkspaceManager");
						execLinkedOp(application, "setEvaluator", evaluator);
						execLinkedOp(application, "setCandidate", candidate);
						
						defineObsProperty("applicationId", application, evaluator.toString(), candidate.toString());
						
						execLinkedOp(normativeArtifact, "initCommitmentsBoHiEvCa", boss, hirer, evaluator, candidate);
					
					} catch (OperationException e) {
						failed("Cannot initialize application artifact");
					}
					
				}
			}
			
		}
		else {
			failed("Enactment failed!");
		}
		
	}

	@Override
	public String getArtifactType() {
		return ARTIFACT_TYPE;
	}
	
	@LINK
	public void getPlayerRoleId(String playerName, OpFeedbackParam<RoleId> r) {
		r.set(getRoleIdByPlayerName(playerName));
	}
	
	@LINK
	public void getRoleIdByName(String roleIdName, OpFeedbackParam<RoleId> r) {
		r.set(getRoleIdByRoleName(roleIdName));
	}

	
	// INNER CLASSES for ROLES
	// hirer role
	public class Boss extends PARole {

		public Boss(String playerName, IPlayer player) {
			super(BOSS_ROLE, player);
		}
	}
	
	// hirer role
	public class Hirer extends PARole {

		public Hirer(String playerName, IPlayer player) {
			super(HIRER_ROLE, player);
		}
	}

	// evaluator role
	public class Evaluator extends PARole {
		public Evaluator(String playerName, IPlayer player) {
			super(EVALUATOR_ROLE, player);
		}
	}
	
	// candidate role
	public class Candidate extends PARole {
		public Candidate(String playerName, IPlayer player) {
			super(CANDIDATE_ROLE, player);
		}
	}
	
	public interface BossObserver extends ProtocolObserver {	}
	
	public interface HirerObserver extends ProtocolObserver {	}

	public interface EvaluatorObserver extends ProtocolObserver {	}
	
	public interface CandidateObserver extends ProtocolObserver {	}
	
}
