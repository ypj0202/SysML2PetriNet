action testStartAndEnd{
	first start;
	then p1;
	first start;
	then p2;
	action p1{
		attribute initialMarking := 1;
	}
	then action t1;
	then p2;
	action p2{
		attribute initialMarking := 1;
	}
	then done;
}