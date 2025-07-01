action testWithPlace2Place{
	first start;
	then actionA;
	action actionA{
		attribute pIn:=1;
		attribute initialMarking:=2;
		attribute pOut:=2;
	}
	then
	action actionB{
		attribute pIn:=1;
		attribute initialMarking:=0;
		attribute pOut:=2;
	}
	then
	action actionC{
		attribute pIn:=1;
		attribute initialMarking:=0;
		attribute pOut:=2;
	}
	then done;
}