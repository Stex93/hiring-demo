{ include("$jacamoJar/templates/common-cartago.asl") }

!startRequest.

/* Plans */

+!startRequest : true
	<- 	.wait(1000); 
		lookupArtifact("items",ArtId);
      	focus(ArtId);
		enact("customer").

+netBillNormativeStateId(id)
	<- focus(id).
	
+enacted(Id,"customer",My_Role_Id)
	<-	+enactment_id(My_Role_Id);
		!buy(10).
	
+!buy(Quantity) <- request(Quantity).

+cc(Merchant_Role_Id, My_Role_Id, AcceptedQuotation, Goods,"CONDITIONAL")
	:  	enactment_id(My_Role_Id) 
		& jia.getAcceptedQuotationComponentsPrice(AcceptedQuotation,Price)
		& jia.getAcceptedQuotationComponentsQuantity(AcceptedQuotation,Quantity)
		& jia.getAcceptedQuotationComponentsCustomer(AcceptedQuotation,My_Role_Id)
	<- // --- Code for evaluating quotation		
		accept(Price,Quantity).
//	
+cc(My_Role_Id, Merchant_Role_Id, _, Paid,"DETACHED")
	:  enactment_id(My_Role_Id)        
		& .term2string(Term1,Paid)
		& Term1 = paid(My_Role)
	<- 	sendEPO(53530331).
