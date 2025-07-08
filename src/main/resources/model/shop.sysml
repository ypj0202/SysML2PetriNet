action shop{
	attribute CustomerWantsCoffee;
	attribute CustomerWantsSnack;
	first start;
	then enterStore;
	action enterStore;
	then decide decide1;
	if CustomerWantsSnack then prepareSnack;
	if CustomerWantsCoffee then prepareCoffee;
	action prepareCoffee;
	then merge1;
	action prepareSnack;
	then merge1;
	merge merge1;
	then pay;
	action pay;
	then takeOrder;
	action takeOrder;
	then done;
}