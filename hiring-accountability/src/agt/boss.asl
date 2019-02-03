{ include("$jacamoJar/templates/common-cartago.asl") }

!start.

/* Plans */

@p1[atomic]
+!start : true
	<- println("Enacting role boss...");
	   enact("boss").
	
+enacted(Id,"boss",My_Role_Id)
	<- println("Enactment done! My role id is ", My_Role_Id);
	   +enactment_id(My_Role_Id).

+positionId(PosId) : normativeStateId(NSId)
	<- focus(PosId);
	   focus(NSId).

@p2[atomic]	   
//c5
+cc(Hirer_Role_Id,My_Role_Id,Ant,Cons,"CONDITIONAL")
	 : enactment_id(My_Role_Id) &
	   positionStatus("POSITION_NULL") &
	   positionId(PosId) &
	   .concat("openPosition(",My_Role_Id,")",Ant)
	<- .wait(2000);
	   println("Opening position...");
	   openPosition[artifact_id(PosId)].