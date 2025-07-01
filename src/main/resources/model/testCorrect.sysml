action testCorrect{
	first start;
	then p1;
	action p1{
		attribute pIn := 2;
		attribute initialMarking := 1;
		attribute pOut := 1;
	}
	then fork t1;
	then  p2;
	then  p3;
	action p2{
		attribute pIn := 1;
		attribute pOut := 1;
	}
	then t2;
	action p3{
		attribute pIn := 1;
		attribute pOut := 1;
	}
	then t2; 
	join t2;
	then p1;
	then action p4{
		attribute pIn := 1;
		attribute pOut := 1;
	}
	then done;
}