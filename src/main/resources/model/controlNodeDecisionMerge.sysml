action controlNodeDecisionMerge{
	attribute guard1;
	attribute guard2;
	first start;
	then action  action1;
	then decide decide1;
	if guard1 then action2;
	if guard2 then action3;
	action action2;
	then merge1;
	action action3;
	then merge merge1;
	then action5;
	action action5;
	then done;
}