action transistionWithWeight{
	first start;
	then p1;
	action p1{
		attribute initialMarking := 2;
	}
	then p2;
	action p2{
		attribute pIn := 4;
		attribute pOut :=5;
	}
	then t2;
	fork t2;
	then p3;
	then p4;
	action p3{
		attribute initialMarking := 3;
	}
	then done;
	 action p4{
	 	attribute initialMarking := 4;
	 }
	 then done;
}
