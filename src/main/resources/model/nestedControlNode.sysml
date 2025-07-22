action nestedControlNode{
	attribute guard1;
	first start;
	then action  action1;
	then decide decide1;
	if guard1 then action2;
	else action3;
	fork fork1;	
	then action6; 
	then action7;
	action action2;
	then merge1;
	action action3;
	then fork1;
	action action6;
	then join1;
	join join1;
	then merge1;
	action action7;
	then join1;
	merge merge1;
	then action5;
	action action5;
	then done;
}