action duplicateNodeNames{
	attribute guard1;
	first start;
	then action action1;
	if guard1 then action2;
	action action2;
	then merge1;
	action action2; 
	then merge1;
	then action action3;
	then done;
} 