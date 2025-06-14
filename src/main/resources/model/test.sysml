action  action00 {
first start;
then fork fork1;
then action action2 {
	in weightA := 2;
	attribute initialMarking := 1;
}
then action3;
action action3;
then done;
action action4;
then done;
}

