action test3{
	action A;
	then B;
    action B;
    then C;
    action C;
	then action  actionD{
		attribute pIn:=1;
		attribute initialMarking:=3;
		attribute pOut:=4;
	}
	then action actionE{
		attribute pIn:=1;
		attribute initialMarking:=2;
		attribute pOut:=2;
	}
	then fork fork1;
then action1;
then action2;
action action1;
then join1;
action action2;
then join1;
join join1;
then action3;
then action4;
action action3;
then merge1;
action action4;
then merge1;
merge merge1;
then action5;
action action5{
	attribute initalMarking:=1;
}	
}