{ include("$jacamoJar/templates/common-cartago.asl") }

!sell("Asus ZenPhone 3", 1000).

/* Plans */

+!sell(Item, MaxQuantity) : true
	<- 	.print("start"); 
		makeArtifact("items", "item.Items", [Item, MaxQuantity], ArtId); 
      	focus(ArtId);
      	enact("merchant").
      
+netBillNormativeStateId(id)
	<- focus(id).
		
+enacted(Id,"merchant",Role_Id)
	<-	+enactment_id(Role_Id).
		
+requestedQuote(Quantity, Customer_Id)
	<- 	// --- Code for computing price
		quote(1000, Quantity, Customer_Id).

+cc(My_Role_Id, Customer_Role_Id, AcceptedQuotation, Goods,"DETACHED")
	:  enactment_id(My_Role_Id) 
		& jia.getAcceptedQuotationComponentsPrice(AcceptedQuotation,Price)
		& jia.getAcceptedQuotationComponentsQuantity(AcceptedQuotation,Quantity)
		& jia.getAcceptedQuotationComponentsCustomer(AcceptedQuotation,Customer_Role_Id)
	<- 	ship(Customer_Role_Id, Quantity).

+cc(My_Role_Id, Customer_Role_Id, Paid, Receipt,"DETACHED")
	:  enactment_id(My_Role_Id)
		& .term2string(Term1,Paid)
		& Term1 = paid(Customer)
		& .term2string(Customer,S)
		& S == Customer_Role_Id
	<- 	emitReceipt(Customer_Role_Id).
