{ include("$jacamoJar/templates/common-cartago.asl") }

!start.

/* Plans */

@p1[atomic]
+!start : true
	<- 	.wait(1000);
	    println("Enacting role candidate...");
		enact("candidate").
		
+enacted(Id,"candidate",My_Role_Id)
	<- println("Enactment done! My role id is ", My_Role_Id);
	   +enactment_id(My_Role_Id).
	
+enactment_id(My_Role_Id) : applicationId(AppId,_,My_Role_Id) & normativeStateId(NSId)
	<- focus(AppId);
	   focus(NSId).
	   
+applicationId(AppId,_,My_Role_Id) : enactment_id(My_Role_Id) & normativeStateId(NSId)
	<- focus(AppId);
	   focus(NSId).
	   
+postJob(Hirer)[artifact_id(NSId)]
	<- .random(N);
	   !sendApplication(N).
	
+!sendApplication(N)
     : N < 0.5 &
       enactment_id(My_Role_Id) &
       normativeStateId(NSId) &
       applicationId(AppId,_,My_Role_Id)
	<- .wait(300);
	   println("A job has been posted! Sending my application...");
	   apply[artifact_id(AppId)].
	   
+!sendApplication(N)
     : N >= 0.5 & 
       enactment_id(My_Role_Id) &
       normativeStateId(NSId) &
       applicationId(AppId,_,My_Role_Id)
	<- println("A job has been posted! Sending my application...");
	   apply[artifact_id(AppId)].

//c4
+cc(My_Role_Id,Evaluator_Role_Id,Ant,Cons,"DETACHED")
	 : enactment_id(My_Role_Id) &
	   applicationId(_,Evaluator_Role_Id,My_Role_Id) &
	   .concat("makeOffer(",Evaluator_Role_Id,")",Ant) &
	   .concat("(makeOffer(",Evaluator_Role_Id,") THEN (responseYes(",My_Role_Id,") OR responseNo(",My_Role_Id,")))",Cons)
	<- println("An offer was made to me! Evaluating it...");
	   .random(N);
	   !response(N).
	   
+!response(N)
     : N < 0.5 & 
       enactment_id(My_Role_Id) &
	   applicationId(AppId,Evaluator_Role_Id,My_Role_Id)
	<- println("The offer seems good! Sending a response yes...");
	   responseYes[artifact_id(AppId)].
	
+!response(N)
	 : N >= 0.5 &
	   enactment_id(My_Role_Id) &
	   applicationId(AppId,Evaluator_Role_Id,My_Role_Id)
	<- println("The offer is unacceptable. Refusing it...");
	   responseNo[artifact_id(AppId)].