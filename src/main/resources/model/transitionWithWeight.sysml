action transistionWithWeight{
	action p1{
		attribute initialMarking := 2;
	}
	then t1;
	action t1{
		attribute pIn := 4;
		attribute pOut :=5;
	}
	then t2;
	fork t2;
	then p2;
	then p3;
	action p2{
		attribute initialMarking := 3;
	}
	 action p3{
	 	attribute initialMarking := 4;
	 }
}
