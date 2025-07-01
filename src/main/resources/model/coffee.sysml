action coffee{
	first start;
	then init;
	action init{
		attribute initialMarking := 1;
	}
	then pay;
	then fork fork1;
	then prepareSnack;
	then prepareCoffee;
	action prepareCoffee{
		attribute initialMarking := 0;
	}
	then merge1;
	action prepareSnack{
		attribute initialMarking := 0;
	}
	then merge1;
	merge merge1;
	then takeOrder;
	action pay{
		attribute initialMarking := 0;
	}
	then takeOrder;
	action takeOrder{
		attribute pIn := 2;
	}
	then done;
	
}