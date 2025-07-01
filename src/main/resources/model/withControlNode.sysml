action withControlNode{
	first start;
	then action  actionD{
		attribute pIn:=1;
		attribute initialMarking:=3;
		attribute pOut:=4;
	}
	then fork1;
	action actionE{
		attribute pIn:=1;
		attribute initialMarking:=2;
		attribute pOut:=2;
	}
	then join1;
 fork fork1;
then actionE;
then action2;
action action2{
		attribute pIn:=1;
		attribute initialMarking:=2;
		attribute pOut:=2;
}
then join1;
join join1;
then action3;
then action4;
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