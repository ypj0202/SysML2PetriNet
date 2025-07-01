action sample{
	first start;
	then action  actionD{
		attribute pIn:=1;
		attribute initialMarking:=3;
		attribute pOut:=4;
	}
then fork fork1;
then action4;
then action3;
action action3{
		attribute pIn:=1;
		attribute initialMarking:=2;
		attribute pOut:=2;
}
then merge1;
action action4{
		attribute pIn:=1;
		attribute initialMarking:=2;
		attribute pOut:=2;
}
then merge1;
merge merge1;
then action5;
action action5{
		attribute pIn:=1;
		attribute initialMarking:=0;
		attribute pOut:=2;
}
then done;
}