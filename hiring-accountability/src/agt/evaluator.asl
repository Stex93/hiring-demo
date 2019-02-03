{ include("$jacamoJar/templates/common-cartago.asl") }

!start.

/* Plans */

@p1[atomic]
+!start : true
	<- 	.wait(1000);
	    println("Enacting role evaluator...");
		enact("evaluator").
		
+enacted(Id,"evaluator",My_Role_Id)
	<- println("Enactment done! My role id is ", My_Role_Id);
	   +enactment_id(My_Role_Id).
	
+enactment_id(My_Role_Id) : applicationId(AppId,My_Role_Id,_) & normativeStateId(NSId) & positionId(PosId)
	<- focus(PosId);
	   focus(AppId);
	   focus(NSId).

+applicationId(AppId,My_Role_Id,_) : enactment_id(My_Role_Id) & normativeStateId(NSId) & positionId(PosId)
	<- focus(PosId);
	   focus(AppId);
	   focus(NSId).

//c1
@p2[atomic]
+cc(My_Role_Id,Hirer_Role_Id,Ant,Cons,"DETACHED")
	 : enactment_id(My_Role_Id) &
	   applicationId(AppId,My_Role_Id,Candidate_Role_Id) &
	   positionStatus("POSITION_FILLED") &
	   hirer(Hirer_Role_Id) &
	   .concat("(postJob(",Hirer_Role_Id,") THEN apply(",Candidate_Role_Id,"))",Ant)
	<- println("Application received! The position has already been filled. Sending position closed message...");
	   positionClosed[artifact_id(AppId)].

//c1
@p3[atomic]
+cc(My_Role_Id,Hirer_Role_Id,Ant,Cons,"DETACHED")
	 : enactment_id(My_Role_Id) &
	   applicationId(AppId,My_Role_Id,Candidate_Role_Id) &
	   positionId(PosId) &
	   hirer(Hirer_Role_Id) &
	   .concat("(postJob(",Hirer_Role_Id,") THEN apply(",Candidate_Role_Id,"))",Ant)
	<- println("Application received! Checking the position...");
	   checkPosition[artifact_id(PosId)];
	   !checkPosition.

+!checkPosition
     : enactment_id(My_Role_Id) &
	   applicationId(AppId,My_Role_Id,Candidate_Role_Id) &
	   positionId(PosId) &
	   hirer(Hirer_Role_Id) &
       not positionStatus("POSITION_OPEN") &
       cc(My_Role_Id,Hirer_Role_Id,Ant,Cons,"DETACHED") &
	   .concat("(postJob(",Hirer_Role_Id,") THEN apply(",Candidate_Role_Id,"))",Ant)
	<- println("The position is not open. Sending position closed message...");
	   positionClosed[artifact_id(AppId)].

+!checkPosition
     : enactment_id(My_Role_Id) &
	   applicationId(AppId,My_Role_Id,Candidate_Role_Id) &
	   positionId(PosId) &
	   hirer(Hirer_Role_Id) &
       positionStatus("POSITION_OPEN") &
       cc(My_Role_Id,Hirer_Role_Id,Ant,Cons,"DETACHED") &
	   .concat("(postJob(",Hirer_Role_Id,") THEN apply(",Candidate_Role_Id,"))",Ant)
	<- println("Position open! Screening the application...");
	   screenInterview[artifact_id(AppId)];
	   .random(N);
	   !offerOrReject(N).
	   
+!offerOrReject(N)
     : N < 0.5 &
       enactment_id(My_Role_Id) &
	   applicationId(AppId,My_Role_Id,Candidate_Role_Id) &
	   positionStatus("POSITION_OPEN")
	<- println("Application ok! Making an offer...");
	   makeOffer[artifact_id(AppId)].
	
+!offerOrReject(N)
	 : N >= 0.5 &
	   enactment_id(My_Role_Id) &
	   applicationId(AppId,My_Role_Id,Candidate_Role_Id) &
	   positionStatus("POSITION_OPEN")
	<- println("Application not ok! Sending rejection notice...");
	   rejectionNotice[artifact_id(AppId)].

+responseYes(Candidate_Role_Id)[artifact_id(NSId)]
	 : enactment_id(My_Role_Id) &
	   applicationId(AppId,My_Role_Id,Candidate_Role_Id) &
	   normativeStateId(NSId) &
	   hirer(Hirer_Role_Id) &
	   cc(My_Role_Id,Hirer_Role_Id,Ant,Cons,"DETACHED") &
	   .concat("(postJob(",Hirer_Role_Id,") THEN apply(",Candidate_Role_Id,"))",Ant)
	<- .wait(1000);
	   println("Offer accepted! Notifying the hirer...");
	   offerAccepted[artifact_id(AppId)].
	   	
+responseNo(Candidate_Role_Id)[artifact_id(NSId)]
	 : enactment_id(My_Role_Id) &
	   applicationId(AppId,My_Role_Id,Candidate_Role_Id) &
	   normativeStateId(NSId) &
	   hirer(Hirer_Role_Id) &
	   cc(My_Role_Id,Hirer_Role_Id,Ant,Cons,"DETACHED") &
	   .concat("(postJob(",Hirer_Role_Id,") THEN apply(",Candidate_Role_Id,"))",Ant)
	<- println("Offer rejected! My job is done.");
	   offerRejected[artifact_id(AppId)].
