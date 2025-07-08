action controlNodeAll{
	attribute conditionA;
	attribute conditionB;
	first start;
	then action  action1;
	then decide decide1;
	if conditionA then action2;
	if conditionB then action3;
	action action2;
	then merge1;
	action action3;
	then merge merge1;
	then action  action6;
	then fork1;
	action action7;
	then join1;
 	fork fork1;
	then action7;
	then action8;
	action action8;
	then join1;
	join join1;
	then action9;
	action action9;
	then done;
}