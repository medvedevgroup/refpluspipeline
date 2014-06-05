This program creates a new reference from an existing one by incorporating 
structural variants from a file provided by the user.
The variation types are INSERTION, DELETION, INVERSION, TANDEM, TRANSLOCATION, 
and SNP.
The TRANSLOCATION and DELETION types actually remove sequence content from the
original refernce. This is usually not an intended effect, and better results
are probably achieved by replacing TRANSLOCATIONs with sequence duplications
(INSERTION) and ommitting DELETIONs altogether. These types are included for
completeness only.
The variation type should have the following format:
	TYPE	LOCUS	[SEQUENCE/LOCUS2]
The sequence is optional for those the types requiring additional information
of the inserted sequence.
Examples:
	INSERTION	chr1:1024878-1024878	gaggttaaaatctcc
	DELETION	chr2:2017674-2018635
	SNP	chr3:1288100-1288100	T
	TRANSLOCATION	chr4:2388190-2388190	chr5:4377179-4377817

