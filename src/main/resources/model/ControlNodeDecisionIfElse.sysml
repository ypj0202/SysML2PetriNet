action controlNodeDecisonIfElse{
	attribute guard1;
	first start;
	then action  action1;
	then decide decide1;
	if guard1 then action2;
	else action3;
	action action2;
	then merge1;
	action action3;
	then merge merge1;
	then action5;
	action action5;
	then done;
}