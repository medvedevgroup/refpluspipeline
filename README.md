INSTRUCTION MANUAL

1. Create the new reference (ref+), which includes the structural variations of 
interest. 
	1.1 reference_generation/run.sh executes the program for reference generation.
	1.2 The program creates a new reference from an existing one by incorporating
	structural variants from a file provided by the user.
	The variation types are INSERTION, DELETION, INVERSION, TANDEM, TRANSLOCATION,  
	and SNP.
	The TRANSLOCATION and DELETION types actually remove sequence content from the
	original refernce. This is usually not an intended effect, and better results
	are probably achieved by replacing TRANSLOCATIONs with sequence duplications
	(INSERTION) and ommitting DELETIONs altogether. These types are included for
	completeness only.
	1.3 The variation type should have the following format:  
		TYPE  LOCUS [SEQUENCE/LOCUS2]
	The sequence is optional for those the types requiring additional information
	of the inserted sequence.
	1.4 Examples:
	  INSERTION chr1:1024878-1024878  gaggttaaaatctcc
		DELETION  chr2:2017674-2018635
		SNP chr3:1288100-1288100  T
		TRANSLOCATION chr4:2388190-2388190  chr5:4377179-4377817

2. Index the new reference, and align reads to it with a mapper of choice (bwa, 
bowtie2, ...) -- the mapper is best chosen depending on the SV detection tools 
used in the next step.

3. Run an SV detection tool on the aligned read set (Delly, BreakDancer, Socrates). 
The choice here is dependent on the properties of the sequencing library: soft-clip 
based tools such as Socrates require long reads and deep coverage; pure paired-end 
methods such as BreakDancer rely on high average insert size and a tight distribution 
for maximum effct. Multiple tools can be run at this step if desired.

4. Evaluate the results. To assess the presence or absence of the variants added to 
the novel reference, the output from the SV detection has to be compared to the list 
of variants. 
A simple overlapping tool in results_evaluation takes two bed files and establishes 
the recall, precision, and f-score of the best overlap of intervals in the first bed 
file and any interval in the second bed file. 
	4.1 The interval_overlap_deletions.py script takes two files as an input. In the 
	context of the ref+ pipeline, these files should be the list of variants and 
	the (parsed) output of the SV caller. Both files are expected to be in standard 
	tab- or whitespace-delimited format with each row containing the reference 
	chromosome, the starting position, and the end position. Neither of the files 
	have to be sorted, but can be arranged in any order. This makes the script 
	slow and inefficient, but relieves some of the strains of pre-processing. 
	The output of the script contains the variation coordinates, followed by three
	evaluation metrics, and the line of output from the SV caller. The metrics are
	the recall, the precision, and the f-score for the variant from file1 and the
	best hit from file2. If there are no overlapping intervals for a variant in
	file1, the metrics are set to 0. Otherwise, recall describes the proportion of
	the variant being overlapped by a call in file2, precision the proportion of
	the overlapping SV call, and F-score the combination of the two
	(2*recall*precision)/(recall+precision).


