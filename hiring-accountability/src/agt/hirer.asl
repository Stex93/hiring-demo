{ include("$jacamoJar/templates/common-cartago.asl") }

!start.

/* Plans */

@p1[atomic]
+!start : true
	<- .wait(500);
	   println("Enacting role hirer...");
	   enact("hirer").
	
+enacted(Id,"hirer",My_Role_Id)
	<- println("Enactment done! My role id is ", My_Role_Id);
	   +enactment_id(My_Role_Id).
	
+positionId(PosId) : normativeStateId(NSId)
	<- focus(PosId);
	   focus(NSId).

//c5
//@p2[atomic]	
+cc(My_Role_Id,Boss_Role_Id,Ant,Cons,"DETACHED")
	 : enactment_id(My_Role_Id) &
	   positionStatus("POSITION_OPEN") &
	   positionId(PosId) &
	   .concat("openPosition(",Boss_Role_Id,")",Ant) &
	   .concat("(openPosition(",Boss_Role_Id,") THEN postJob(",My_Role_Id,"))",Cons)
	<- println("Posting job...");
	   postJob[artifact_id(PosId)];
	   .wait(40000);
	   !afterThreeMonths.

//c3
@p3[atomic]
+cc(My_Role_Id,"evaluator",Ant,Cons,"DETACHED")[artifact_id(NSId)]
	 : enactment_id(My_Role_Id) &
	   positionStatus("POSITION_OPEN") &
	   normativeStateId(NSId) &
	   positionId(PosId) &
	   .concat("offerAccepted(evaluator)",Ant)
	<- .wait(1000);
	   println("The position has been filled. Well done!");
	   updateFilled[artifact_id(PosId)].

+!afterThreeMonths
	 : positionStatus("POSITION_OPEN") &
	   positionId(PosId)
	<- println("Three months have passed. Timeout!");
	   timeout[artifact_id(PosId)].
	   
+!afterThreeMonths
	 : not positionStatus("POSITION_OPEN")
	<- println("Everything done.").
	
//c6
@p4[atomic]
+cc(My_Role_Id,Boss_Role_Id,Ant,Cons,"DETACHED")[artifact_id(NSId)]
	 : enactment_id(My_Role_Id) &
	   positionStatus("POSITION_OPEN") &
	   timeout3months(My_Role_Id) &
	   normativeStateId(NSId) &
	   positionId(PosId) &
	   .concat("(postJob(",My_Role_Id,") THEN (offerAccepted(evaluator) OR timeout3months(",My_Role_Id,")))",Ant)
	<- println("Position abandoned!");
	   updateAbandoned[artifact_id(PosId)].